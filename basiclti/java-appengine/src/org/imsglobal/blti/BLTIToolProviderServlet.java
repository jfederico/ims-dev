/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/basiclti/java-appengine/src/org/imsglobal/blti/BLTIToolProviderServlet.java $
 * $Id: BLTIToolProviderServlet.java 231 2011-01-12 00:39:57Z drchuck $
 **********************************************************************************
 * Copyright (c) 2008 IMS GLobal Learning Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 *
 **********************************************************************************/

/*  Note:  This code was initially developed by 
    Chuck Wight at utah.edu and practicezone.org 
    This servlet file is adapted from an open-source Java servlet 
    LTIProviderServlet written by Charles Severance at imsglobal.org
@author Chuck Wight
*/

package org.imsglobal.blti;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.OAuthServlet;
import net.oauth.signature.OAuthSignatureMethod;

public class BLTIToolProviderServlet extends HttpServlet {

private static final long serialVersionUID = 48373566L;

@Override
public void init(ServletConfig config) throws ServletException {
	super.init(config);
}

@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
throws ServletException, IOException 
{
	doPost(request, response);
}

@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) 
throws ServletException, IOException 
{
	String ipAddress = request.getRemoteAddr();
	System.out.println("Basic LTI Provider request from IP="+ipAddress);

	// required 
	String oauth_consumer_key = request.getParameter("oauth_consumer_key");
	String resource_link_id = request.getParameter("resource_link_id");
	if ( ! "basic-lti-launch-request".equals(request.getParameter("lti_message_type")) ||
			! "LTI-1p0".equals(request.getParameter("lti_version")) ||
			oauth_consumer_key == null || resource_link_id == null ) {
		doError(request, response, "Missing required parameter.", null);
		return;
	}

	// Lookup the secret that corresponds to the oauth_consumer_key in the AppEngine datastore
	String oauth_secret = BLTIConsumer.getSecret(oauth_consumer_key);

	OAuthMessage oam = OAuthServlet.getMessage(request, null);
	OAuthValidator oav = new SimpleOAuthValidator();
	OAuthConsumer cons = new OAuthConsumer("about:blank#OAuth+CallBack+NotUsed",oauth_consumer_key, oauth_secret, null);
	OAuthAccessor acc = new OAuthAccessor(cons);

	String base_string = null;
	try {
		base_string = OAuthSignatureMethod.getBaseString(oam);
	} catch (Exception e) {
		base_string = null;
	}
	
	try {
		oav.validateMessage(oam,acc);
	} catch(Exception e) {
		System.out.println("Provider failed to validate message");
		System.out.println(e.getMessage());
		if ( base_string != null ) System.out.println(base_string);
		doError(request, response,"Launch data does not validate.", null);
		return;
	}
	// BLTI Launch message was validated successfully. 
	
	// Store the request parameters name/value pairs in the user's web session
	// In a real application these values would be used to authenticate the user and provision an account
	
	HttpSession session = request.getSession();
	Enumeration<?> names = request.getParameterNames();
	while (names.hasMoreElements()) {
		String n = (String) names.nextElement();
		session.setAttribute(n, request.getParameter(n));
	}
	response.sendRedirect("/");	
}

public void doError(HttpServletRequest request, HttpServletResponse response, 
		 String message, Exception e)
throws java.io.IOException
{
	String return_url = request.getParameter("launch_presentation_return_url");
	if ( return_url != null && return_url.length() > 1 ) {
		if ( return_url.indexOf('?') > 1 ) {
			return_url += "&lti_msg=" + message;
		} else {
			return_url += "?lti_msg=" + message;
		}
		response.sendRedirect(return_url);
		return;
	}
	response.getWriter().println(message);
}

@Override
public void destroy() {

}

}

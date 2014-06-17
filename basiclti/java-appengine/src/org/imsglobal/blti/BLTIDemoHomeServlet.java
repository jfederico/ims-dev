/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/basiclti/java-appengine/src/org/imsglobal/blti/BLTIDemoHomeServlet.java $
 * $Id: BLTIDemoHomeServlet.java 231 2011-01-12 00:39:57Z drchuck $
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
    Chuck Wight of the University of Utah and practicezone.org 
@author Chuck Wight
*/

package org.imsglobal.blti;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;


public class BLTIDemoHomeServlet extends HttpServlet {

	private static final long serialVersionUID = 48373566L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ObjectifyService.register(BLTIConsumer.class);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer buf = new StringBuffer();
		
		if (session == null || !session.getAttributeNames().hasMoreElements()) { // user has no active web session
			buf.append("<h2>BLTI Tool Provider Demo on Google App Engine</h2>"
					+ "This is a demo site for launching a third-party learning tool hosted on "
					+ "<a href=http://code.google.com/appengine/>Google App Engine</a> from inside "
					+ "a student learning management system (LMS) such as Moodle, Blackboard, or Sakai "
					+ "using the Basic Learning Tool Interoperability (BLTI) standard developed by the "
					+ "<a href=http://imsglobal.org>IMS Global Learning Consortium</a>.<p>");
			buf.append("The basic idea is that the student or instructor has already authenticated to the "
					+ "LMS when a link is activated to lauch a session with the third-party tool "
					+ "provider.  The LMS provides the user's browser with a set of information (unique "
					+ "user_id, name, email, role, course_id, etc).  The information is signed using an "
					+ "<a href=http://oauth.net>OAuth</a> tool and a secret that is shared between the "
					+ "LMS and the third-party tool provider.  The user's browser is then redirected to the "
					+ "tool provider site, which validates the request using the signature and shared "
					+ "secret to ensure that validity of the information.  The tool provider then trusts "
					+ "the information to authenticate the user and provision the account appropriately.<p>");
			buf.append("<h2>How to Use This Demo Tool Provider</h2>"
					+ "In order to establish a trusted relationship between a tool provider and an LMS, "
					+ "the tool provider admin must provide three pieces of information to the LMS admin:<OL>"
					+ "<LI>a launch URL that can receive, validate and process the BLTI launch request. The "
					+ "launch URL for this demo tool is<br><a href=http://blti-launch-demo.appspot.com/launch/>"
					+ "http://blti-launch-demo.appspot.com/launch/</a>"
					+ "<LI>a Consumer Key, which is a string that uniquely identifies the LMS. Often this "
					+ "is a human-readable string that resembles a domain name (but is not necessarily "
					+ "functional). You can use any unique string as a consumer key."
					+ "<LI>a shared secret (random string or hexadecimal number that is difficult to guess). "
					+ "You may enter your own (made-up) consumer key in the form below and generate a long "
					+ "hex string to act as a shared secret for this demo.</OL>");
			buf.append("Write down the launch URL and one of the consumer key/secret combinations below."
					+ "Then enter them into the <a href=http://www.imsglobal.org/developers/BLTI/lms.php>"
					+ "IMS BasicLTI PHP Consumer</a> form. Click the buttons to 'Recompute Launch Data' and "
					+ "then 'Press to Launch'.  Your browser will be redirected to this site to launch a "
					+ "session with the indicated user information and will appear in a window on the IMS "
					+ "consumer demo page.<br>");
			buf.append("<FORM METHOD=POST>"
					+ "Consumer Key: <INPUT TYPE=TEXT NAME=ConsumerKey>"
					+ "<INPUT TYPE=SUBMIT NAME=UserRequest VALUE='Generate New Shared Secret'>"
					+ "<INPUT TYPE=SUBMIT NAME=UserRequest VALUE='Delete Key/Secret Combination'>"
					+ "</FORM>");
			Query<BLTIConsumer> consumers = ObjectifyService.begin().query(BLTIConsumer.class);
			if (consumers.count() == 0) buf.append("(no BLTI consumers have been authorized yet)<p>");
			else {
				buf.append("<TABLE><TR><TH>Consumer Key</TH><TH>Secret</TH></TR>");
				for (BLTIConsumer c : consumers) {
					buf.append("<TR><TD>" + c.oauth_consumer_key + "</TD>");
					buf.append("<TD><INPUT TYPE=BUTTON VALUE='Reveal secret' "
							+ "onClick=javascript:getElementById('" + c.oauth_consumer_key + "').style.display='';this.style.display='none'>"
							+ "<div id='"+ c.oauth_consumer_key + "' style='display: none'>" + c.secret + "</div></TD></TR>");
				}
				buf.append("</TABLE><hr>");
			}
			buf.append("Please contact <a href=mailto:chuck.wight@gmail.com>Chuck Wight</a> if you encounter "
					+ "any errors or problems with the site.  Source code for this demo site is available as "
					+ "a free download from Google Code.");
		} else { // user has an active BLTI session
			buf.append("<h2>Successful Basic LTI Launch</h2>"
					+ "This text is being generated by a demo third-party tool provider on Google App Engine "
					+ "at <a href=http://blti-launch-demo.appspot.com>http://blti-launch-demo.appspot.com</a><p>"
					+ "The data in the table below were included in the launch request and are now stored in the "
					+ "user's web session.<br>"
					+ "<FORM METHOD=POST>"
					+ "<INPUT TYPE=SUBMIT NAME=UserRequest VALUE='Click Here To Delete This Session'>"
					+ "</FORM>"
					+ "<TABLE><TR><TH>Parameter Name</TH><TH>Value</TH></TR>");
			Enumeration<?> names = session.getAttributeNames();
			while (names.hasMoreElements()) {
				String p = (String) names.nextElement();
				buf.append("<TR><TD>" + p + "</TD><TD>" + session.getAttribute(p) + "</TD></TR>");
			}
			buf.append("</TABLE>");
		}
		out.println(buf.toString());
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	throws ServletException, IOException {
		String userRequest = request.getParameter("UserRequest");
		
		if ("Generate New Shared Secret".equals(userRequest)) {
			BLTIConsumer.create(request.getParameter("ConsumerKey"));
		} else if ("Delete Key/Secret Combination".equals(userRequest)) {
			BLTIConsumer.delete(request.getParameter("ConsumerKey"));
		} else if ("Click Here To Delete This Session".equals(userRequest)) {
			request.getSession().invalidate();
		}
		
		doGet(request,response);
	}
}

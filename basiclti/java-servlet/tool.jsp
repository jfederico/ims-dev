<html>
<head>
  <title>IMS Basic Learning Tools Interoperability</title>
</head>
<body style="font-family:sans-serif">
<img src="http://www.sun.com/images/l2/l2_duke_java.gif" align="right">
<p><b>IMS BasicLTI Java Provider</b></p>
<p>This is a very simple reference implementaton of the tool side (i.e. provider) for IMS BasicLTI.</p>
<p>This tool is configured with an LMS-wide guid of "lmsng.school.edu" protected by a secret of "secret".
For this tool, all resource level secrets are also "secret".</p>
</p>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="net.oauth.OAuth" %>
<%@ page import="net.oauth.OAuthMessage" %>
<%@ page import="net.oauth.OAuthConsumer" %>
<%@ page import="net.oauth.OAuthAccessor" %>
<%@ page import="net.oauth.OAuthValidator" %>
<%@ page import="net.oauth.SimpleOAuthValidator" %>
<%@ page import="net.oauth.signature.OAuthSignatureMethod" %>
<%@ page import="net.oauth.server.HttpRequestMessage" %>
<%@ page import="net.oauth.server.OAuthServlet" %>
<%@ page import="net.oauth.signature.OAuthSignatureMethod" %>
<pre>
<%

  Enumeration en = request.getParameterNames();
  while (en.hasMoreElements()) {
    String paramName = (String) en.nextElement();
    out.println(paramName + " = " + request.getParameter(paramName) );
  }

  OAuthMessage oam = OAuthServlet.getMessage(request, null);
  OAuthValidator oav = new SimpleOAuthValidator();
  String oauth_consumer_key = request.getParameter("oauth_consumer_key");
  if ( oauth_consumer_key == null ) {
    out.println("<b>Missing oauth_consumer_key</b>\n");
    return;
  }
  OAuthConsumer cons = null;
  if ( "lmsng.school.edu".equals(oauth_consumer_key) ) {
    cons = new OAuthConsumer("http://call.back.url.com/", "lmsng.school.edu", "secret", null);
  } else if ( "12345".equals(oauth_consumer_key) ) {
    cons = new OAuthConsumer("http://call.back.url.com/", "12345", "secret", null);
  } else {
    out.println("<b>oauth_consumer_key="+oauth_consumer_key+" not found.</b>\n");
    return;
  }

  OAuthAccessor acc = new OAuthAccessor(cons);

  try {
    out.println("\n<b>Base Message</b>\n</pre><p>\n");
    out.println(OAuthSignatureMethod.getBaseString(oam));
    out.println("<pre>\n");
    oav.validateMessage(oam,acc);
    out.println("Message validated");
  } catch(Exception e) {
    out.println("<b>Error while valdating message:</b>\n");
    out.println(e);
  }

%>
</pre>
<hr>
<p>
Note: Unpublished drafts of IMS Specifications are only available to IMS members and any software based on
an unpublished draft is subject to change.
Sample code is provided to help developers understand the specification more quickly.
Simply interoperating with this sample implementation code does not
allow one to claim compliance with a specification.
<p>
<a href=http://www.imsglobal.org/toolsinteroperability2.cfm>IMS Learning Tools Interoperability Working Group</a> <br/>
<a href="http://www.imsglobal.org/ProductDirectory/directory.cfm">IMS Compliance Detail</a> <br/>
<a href="http://www.imsglobal.org/community/forum/index.cfm?forumid=11">IMS Developer Community</a> <br/>
<a href="http:///www.imsglobal.org/" class="footerlink">&copy; 2009 IMS Global Learning Consortium, Inc.</a> under the Apache 2 License.</p>


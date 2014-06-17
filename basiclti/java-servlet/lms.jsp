<html>
<head>
  <title>IMS Basic Learning Tools Interoperability</title>
</head>
<body style="font-family:sans-serif">
<img src="http://www.sun.com/images/l2/l2_duke_java.gif" align="right">
<p><b>IMS BasicLTI Java Consumer</b></p>
<p>This is a very simple reference implementaton of the LMS side (i.e. consumer) for IMS BasicLTI.</p>
<%@ page import="org.imsglobal.basiclti.BasicLTIUtil" %>
<%@ page import="java.util.Properties" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%@ include file="blti_util.jsp" %>
<%

  String cur_url = request.getRequestURL().toString();

  String secret = request.getParameter("secret");
  String key = request.getParameter("key");
  if ( key == null ) key = "12345";
  String org_id = request.getParameter("org_id");
  if ( org_id == null ) org_id = "lmsng.school.edu";
  String org_desc = request.getParameter("org_desc");
  if ( org_desc == null ) org_desc = "University of School";
  String org_secret = request.getParameter("org_secret");
  if ( secret == null && org_secret == null) secret = "secret";
  if ( org_secret == null ) org_secret = "";
  String endpoint = request.getParameter("endpoint");
  if ( endpoint == null ) endpoint = cur_url.replace("lms.jsp","provider");
  String urlformatstr = request.getParameter("format");
  boolean urlformat = urlformatstr == null || ! urlformatstr.equals("XML");
  String lmspwstr = request.getParameter("lmspw");
  boolean lmspw = lmspwstr == null || ! lmspwstr.equals("Resource");

  String default_desc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<basic_lti_link xmlns=\"http://www.imsglobal.org/services/cc/imsblti_v1p0\" \n" +
    "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
    "  <title>A Simple Descriptor</title>\n" +
    "  <custom>\n" +
    "    <parameter key=\"Cool:Factor\">120</parameter>\n" +
    "  </custom>\n" +
    "  <launch_url>CUR_URL</launch_url>\n" +
    "</basic_lti_link>\n".replace("CUR_URL",cur_url.replace("lms.jsp", "provider"));

  // To keep roundtrips from adding backslashes to double quotes
  String xmldesc = request.getParameter("xmldesc");
  if ( xmldesc == null ) xmldesc = default_desc;
  // xmldesc = str_replace("\\\"","\"",$_REQUEST["xmldesc"]);

  out.println("<form method=\"post\">\n");
  out.println("<p><select name=\"format\" onchange=\"this.form.submit();\">\n");
  out.println("<option value=\"URL\">URL plus Secret</option>\n");
  if ( urlformat ) {
    out.println("<option value=\"XML\">XML Descriptor</option>\n");
  } else {
    out.println("<option value=\"XML\" selected=\"selected\">XML Descriptor</option>\n");
  }
  out.println("</select>");
  out.println("<fieldset><legend>Add New BasicLTI Resource</legend>");
  if ( urlformat ) {
    out.println("Launch URL: <input size=\"80\" type=\"text\" name=\"endpoint\" value=\""+
        (endpoint == null ? "" : BasicLTIUtil.htmlspecialchars(endpoint))+"\">\n");
    out.println("<br/>You can also launch to <b>tool.jsp</b> to see an example of a tool written in JSP.</br>\n");
  } else {
    out.println("XML BasicLTI Resource Descriptor: <br/> <textarea name=\"xmldesc\" rows=\"10\" cols=\"80\">"+
                 BasicLTIUtil.htmlspecialchars(xmldesc)+"</textarea>\n");
  }
  out.print("<br/>Resource Key: <input type\"text\" name=\"key\" value=\""+
     BasicLTIUtil.htmlspecialchars(key)+"\">\n");
  out.print("<br/>Resource Secret: <input type\"text\" name=\"secret\" value=\""+
     BasicLTIUtil.htmlspecialchars(secret)+"\">\n");
  out.print("<br/><input type=\"submit\" value=\"Submit\">\n");
  out.print("</fieldset><p>");
  out.print("<fieldset><legend>LMS Administrator Data</legend>\n");
  out.print("LMS name: <input type\"text\" name=\"org_desc\" value=\""+
        BasicLTIUtil.htmlspecialchars(org_desc)+"\">\n");
  out.print("<br/>LMS key: <input type\"text\" name=\"org_id\" value=\""+
        BasicLTIUtil.htmlspecialchars(org_id)+"\">\n");
  out.print("<br/>LMS secret: <input type\"text\" name=\"org_secret\" value=\""+
     BasicLTIUtil.htmlspecialchars(org_secret)+"\">\n");
  out.print("<br/>If both a resource secret and LMS secret are entered - the LMS secret is used.\n");
  out.print("</fieldset>");
  out.println("<input type=\"submit\" value=\"Submit\">\n");
  out.println("<a href=\"lms.jsp\">Default Values</a>\n");
  out.println("</form>");
  out.print("<p>Note that if you are launching to tool.jsp, it \n");
  out.print("accepts a \"12345/secret\" as a valid resource key/secret.\n");
  out.print("and lmsng.school.edu/secret as a valid LMS key/secret.\n");
  out.print("<hr>");

  // Ignore the organizational info if this is not an LMS password
  if ( ! lmspw ) org_id = null;

  Properties info = new Properties();
  Properties postProp = new Properties();
  if ( urlformat ) {
    getLMSDummyData(postProp);
  } else {
    if ( BasicLTIUtil.parseDescriptor(info, postProp, xmldesc) ) {
      getLMSDummyData(postProp);
      endpoint = info.getProperty("launch_url");
      if ( endpoint == null ) {
        out.println("<p>Error, did not find a launch_url or secure_launch_url in the XML descriptor</p>\n");
        return;
      }
      endpoint = endpoint.replace("CUR_URL",cur_url.replace("lms.jsp", "provider"));
    }
  }

  // Off to the races with BasicLTI...
  if ( org_secret.equals("") ) org_secret = null;
  postProp = BasicLTIUtil.signProperties(postProp, endpoint, "POST", key, secret, org_secret, org_id, org_desc);
  String postData = BasicLTIUtil.postLaunchHTML(postProp, endpoint, true);
  out.println(postData);

%>
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


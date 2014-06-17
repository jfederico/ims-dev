<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>IMS Global Basic LTI Servlet Filter Admin Tool</title>
<style type="text/css">
@import "css/page.css";
</style>
</head>
<body id="blti_page">
	<div id="container">
		<div id="main">
			<a href="index.jsp">Home</a>
			<h1>IMS Global Basic LTI Servlet Filter Admin Tool</h1>
			<h2>
				<a href="test.jsp">Test page</a>
			</h2>
			<p>The test page is page that has the Basic LTI filter enabled
				and can be used to test the filter and your Basic LTI consumer. The
				url of the test page is on this host is:
			<pre><%=request.getScheme()%>://<%=request.getLocalAddr()%>/<%=request.getContextPath()%><%=("".equals(request.getContextPath())?"":"/") %>test.jsp</pre>

			</p>
			<h2>
				<a href="admin.html">The admin console</a>
			</h2>
			<p>
				The admin console requires an authenticated user with the role
				"manager". On tomcat, this can be configured by adding the following
				line to the file <strong>TOMCAT_HOME/conf/tomcat-users.xml</strong>:
			<pre>&lt;user username="manager" password="tomcat" roles="manager"/&gt;</pre>

			It is advised to change the password into something different than
			tomcat.

			</p>
		</div>
	</div>
</body>
</html>
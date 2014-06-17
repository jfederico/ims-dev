<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; UTF-8">
<title>IMS Global Basic LTI Servlet Filter Test Page</title>
<style type="text/css">
@import "css/page.css";
</style>
</head>
<body id="blti_page">
	<div id="container">
		<div id="main">
	<h1>IMS Global Basic LTI Servlet Filter Test Page</h1>
 	 <% 
	Map<String, String[]> myMap = request.getParameterMap();
	Set<Entry<String, String[]>> entrySet = myMap.entrySet();
	for (Entry<String, String[]> entry : entrySet) {
		Object key = entry.getKey();
		String[] value = entry.getValue();
 	%>
 	<%= key%> : <% for(int i=0; i < value.length; i++) { %>
		<%=value[i] %>
 <% 
 	} 
 %>
 <br/>
 <% 
 	} 
 %>
 </div>
 </div>
</body>
</html>
<?xml version="1.0" encoding="UTF-8"?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title><s:message code="admin.title" />
</title>
<style type="text/css">
@import "css/page.css";
@import "css/table.css";
</style>
<script type="text/javascript" language="javascript" src="js/jquery.js"></script>
<script type="text/javascript" language="javascript" src="js/jquery.jeditable.js"></script>
<script type="text/javascript" language="javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" charset="utf-8"> 
		$(document).ready(function() {
			var dataTable = $('#example').dataTable({
				"bProcessing": true,
				"sAjaxSource": "secrets.json",
				"fnInitComplete": function(){
					$('td', dataTable.fnGetNodes()).editable( 'secrets.json', {
						"callback": function( sValue, y ) {
							var aPos = dataTable.fnGetPosition( this );
							var d = $.parseJSON(sValue);
							if (d.aaData.length==0) {
								dataTable.fnDeleteRow(dataTable.fnGetPosition( this )[0]);
							} else {
								dataTable.fnUpdate( d.aaData[0][aPos[1]], aPos[0], aPos[1] );
							}
						},
						"submitdata": function ( xvalue, settings ) {
							var aRow = dataTable.fnGetPosition( this.parentNode );
							var aData = dataTable.fnGetData( this.parentNode );
							var aColumn = dataTable.fnGetPosition( this )[1];
							var key = aData[ 0 ];
							var secret = aData[ 1 ];
							var oldValue = aData[ aColumn ];
							var target = (aColumn == 0 ? "key" : "secret");
							return {
								"key": key,
								"secret": secret,
								"target": target,
							};
						},
						"height": "14px"
					});					
				}
			});

			$('#add-cancel').click(function(){
			});
			$('#add-submit').click(function(){
				$.ajax({
					  url: 'secrets.json',
					  dataType: 'json',
					  data: {
							"key": $('#add-consumerKey').val(),
							"secret": $('#add-consumerSecret').val(),
							"target": "secret",
							"value": $('#add-consumerSecret').val()
						},
					  success: function(data){
						  dataTable.fnAddData([data.aaData[0]]);
					  },
					  type: 'POST'
					});
				return false;
			});
			$('#addFrom').show();
		} 
		);
	</script>
</head>
<body id="blti_page">
	<div id="container">
		<div id="main">
			<a href="index.jsp">Home</a> > <a href="admin.html">Admin</a> 
			<h1>
				<s:message code="admin.title" />
			</h1>
			<table id="example" class="display">
				<thead>
					<tr>
						<th width="30%">Key</th>
						<th width="70%">Secret</th>
					</tr>
				</thead>
				<%--
				<tbody>
					<c:choose>
						<c:when test="${not empty secrets}">
							<c:forEach var="secret" items="${secrets}">
								<tr>
									<td><a href="#" class="updateField" data-consumerKey="${secret.consumerKey}" data-consumerSecret="${secret.consumerSecret}">${secret.consumerKey}</a></td>
									<td><a href="#" class="updateField" data-consumerKey="${secret.consumerKey}" data-consumerSecret="${secret.consumerSecret}">${secret.consumerSecret}</a></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td>&nbsp;</td>
								<td></td>
							</tr>
						</c:otherwise>
					</c:choose>
				</tbody>
			</table>
				 --%>

			<form id="addFrom" style="display: none;">
				<table class="display">
					<tbody>
						<tr class="odd">
							<td width="30%"><input id="add-consumerKey" name="consumerKey" type="text"/></td>
							<td width="70%"><input id="add-consumerSecret" name="consumerSecret" type="text"/>
							<button id="add-submit"><s:message code="action.add" /></button>
							</td>
						</tr>
					</tbody>
				</table>
			</form>
		</div>
	</div>
</body>
</html>
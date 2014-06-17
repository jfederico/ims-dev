<cfinclude template="../../../../includes/tng/tNG.inc.cfm">
<cfscript>
//Start Restrict Access To Page
restrict = Request.tNG_CreateObject("triggers.tNG_RestrictAccess");
restrict.init("registeredusers", "../../../../");
//Grand Levels: Level
restrict.addLevel("1");
restrict.addLevel("2");
restrict.addLevel("3");
restrict.addLevel("4");
restrict.addLevel("5");
restrict.Execute();
//End Restrict Access To Page
</cfscript>
<cfinclude template="header.php">
<h1>Process for LTI Certification</h1>
<p>Quick Link: <a href="lmscert.cfm">Tool Consumer</a> | 
<a href="toolcert.cfm">Tool Provider</a>
</p>
<p>
To pass certification, you must take the following
steps:
<ul>
<li>You must be an IMS Common Cartridge Alliance member
<li>You must pass the tests using certification suite hosted
on the Alliance web site.
<li>You must prepare a report of the results of the test including any 
necessary explanations of any anomalies in the test.
<li>The report must be submitted by a designated representative of the 
alliance member and you must agree that there is no mis-representation
or manipulation of the results in the submitted report. 
<li>You must submit your report via E-Mail to 
<i>conformance@imsglobal.org</i>
<li>After IMS reviews your report and notifies you that the report
is approved and adds you to the LTI product directory, 
you can claim certification to IMS LTI and 
display the certification badge on your web site and in your software.
</ul>
<cfinclude template="footer.php">

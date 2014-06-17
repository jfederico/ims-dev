<html>
<head>
  <title>IMS LTI Tool Test Detail</title>
  <link rel="stylesheet" href="style.css" type="text/css">
</head>
<body>
<a href="http://www.imsglobal.org" target="_new">
<img src="http://www.imsglobal.org/images/imslogo96dpi-ialsm2.jpg" align="right" border="0"/>
</a>
<h1>IMS LTI Tool Test Detail</h1>
<p>
This describes the tests which are used in the IMS
Learning Tools Interoperability Tool Tests.
</p><p>
<?    echo(gmDate("Y-m-d\TH:i:s\Z").' '); ?>
<br clear="all"/>
</p>
<?php

require_once("blti_util.php");
require_once("cert_util.php");

// Print out the list
echo('<table><tr><th>Test</th><th>Decription</th></tr>'."\n");
$idno = 100;
$count = 0;
$good = 0;

foreach($tool_tests as $test => $testinfo ) {
    echo('<tr><td>');
    echo($test);
    echo('</td><td>');
    echo($testinfo["doc"]."\n");
    $extra = "";
    if ( array_key_exists('detail', $testinfo) ) {
        $extra = $extra . "<hr/>Detail:\n".$testinfo["detail"]. "\n";
    }
    if ( array_key_exists('result', $testinfo) ) {
        $extra = $extra . "<hr/>Expected Result:\n".$testinfo["result"]. "\n";
    }
    echo($extra);
    echo("</td></tr>\n");
}
echo("</table>\n");
?>
<p>
<a href="http://www.imsglobal.org/toolsinteroperability2.cfm" target="_new">IMS Learning Tools Interoperability Working Group</a> <br/>
<a href="http://www.imsglobal.org/ProductDirectory/directory.cfm" target="_new">IMS Compliance Detail</a> <br/>
<a href="http://www.imsglobal.org/community/forum/index.cfm?forumid=5" target="_new">IMS Developer Community</a> <br/>
<a href="http://www.imsglobal.org/" class="footerlink" target="_new">&copy; 2010 IMS Global Learning Consortium, Inc.</a> under the Apache 2 License.</p>

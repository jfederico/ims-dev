<?php

function curPageURL() {
$pageURL = (!isset($_SERVER['HTTPS']) || $_SERVER['HTTPS'] != "on")
             ? 'http'
             : 'https';
$pageURL .= "://";
$pageURL .= $_SERVER['HTTP_HOST'];
//$pageURL .= $_SERVER['REQUEST_URI'];
$pageURL .= $_SERVER['PHP_SELF'];
return $pageURL;
}

$default_desc = str_replace("CUR_URL", str_replace("lms.php", "tool.php", curPageURL()), 
'<?xml version="1.0" encoding="UTF-8"?>
<basic_lti_link xmlns="http://www.imsglobal.org/services/cc/imsblti_v1p0" 
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <title>A Simple Descriptor</title>
  <custom>
    <parameter key="Cool:Factor">120</parameter>
  </custom>
  <launch_url>CUR_URL</launch_url>
</basic_lti_link>');

?>

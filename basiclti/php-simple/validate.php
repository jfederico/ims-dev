<?php
$doc = new DOMDocument();
$doc->loadXML('<root><node>YO</node></root>');
echo("Valid=". $doc->schemaValidate("validate.xsd") );
?>
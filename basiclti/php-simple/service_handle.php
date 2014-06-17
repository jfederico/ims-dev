<?php
error_reporting(E_ALL & ~E_NOTICE);
ini_set("display_errors", 1);

header('Content-Type: application/xml; charset=utf-8'); 

require_once("ims-blti/OAuth.php");
require_once("ims-blti/OAuthBody.php");

// For my application, I only allow application/xml
$request_headers = OAuthUtil::get_headers();
if ($request_headers['Content-type'] != 'application/xml' ) {
   die("Must be content type xml");
}

$oauth_consumer_key = "lmsng.school.edu";
$oauth_consumer_secret = "secret";

$response = '<?xml version="1.0" encoding="UTF-8"?>
<imsx_POXEnvelopeResponse xmlns = \"http://www.imsglobal.org/lis/oms1p0/pox\">
    <imsx_POXHeader>
        <imsx_POXResponseHeaderInfo>
            <imsx_version>V1.0</imsx_version>
            <imsx_messageIdentifier>4560</imsx_messageIdentifier>
            <imsx_statusInfo>
                <imsx_codeMajor>MAJOR</imsx_codeMajor>
                <imsx_severity>Status</imsx_severity>
                <imsx_description>DESCRIPTION</imsx_description>
                <imsx_messageRefIdentifier>OMSR_SEED_20110811190414_10</imsx_messageRefIdentifier> 
            </imsx_statusInfo>
        </imsx_POXResponseHeaderInfo>
    </imsx_POXHeader>
    <imsx_POXBody>
        <replaceResultResponse/>
    </imsx_POXBody>
</imsx_POXEnvelopeResponse>';

try {
    $body = handleOAuthBodyPOST($oauth_consumer_key, $oauth_consumer_secret);
    $xml = new SimpleXMLElement($body);
    $imsx_body = $xml->imsx_POXBody->children();
    $operation = $imsx_body->getName();
    $parms = $imsx_body->children();
    $sourcedid = $parms->resultRecord->sourcedGUID->sourcedId;
    $score =     $parms->resultRecord->result->resultScore->textString;
    echo(str_replace(array('MAJOR','DESCRIPTION'),array('success', "Score for $sourcedid is now $score"), $response));
} catch (Exception $e) {
    echo(str_replace(array('MAJOR','DESCRIPTION'),array('failure', $e->getMessage()), $response));
}

?>
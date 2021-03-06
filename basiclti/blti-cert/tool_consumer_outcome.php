<?php
if (version_compare(PHP_VERSION, '5.3.0') >= 0) {
 error_reporting(E_ALL & ~E_NOTICE & ~E_DEPRECATED);
} else { 
 error_reporting(E_ALL & ~E_WARNING & ~E_NOTICE);
}

$old_error_handler = set_error_handler("myErrorHandler");

function myErrorHandler($errno, $errstr, $errfile, $errline)
{
    // echo("YO ". $errorno . $errstr . "\n");
    if ( strpos($errstr, 'deprecated') !== false ) return true;
    return false;
}

ini_set("display_errors", 1);

if ( !isset ( $_REQUEST['b64'] ) ) {
   die("Missing b64 parameter");
}

$b64 = $_REQUEST['b64'];
session_id(md5($b64));
session_start();

require_once("OAuth.php");
require_once("OAuthBody.php");

// For my application, We only allow application/xml
$request_headers = OAuthUtil::get_headers();
$hct = $request_headers['Content-Type'];
if ( ! isset($hct) ) $hct = $request_headers['Content-type'];
if ($hct != 'application/xml' ) {
   header('Content-Type: text/plain');
   print_r($request_headers);
   die("Must be content type xml, found ".$hct);
}

header('Content-Type: application/xml; charset=utf-8'); 

$b64 = base64_decode($b64);
$b64 = explode(":::", $b64);

$oauth_consumer_key = $b64[0];
$oauth_consumer_secret = $b64[1];

$response = '<?xml version="1.0" encoding="UTF-8"?>
<imsx_POXEnvelopeResponse xmlns="http://www.imsglobal.org/lis/oms1p0/pox">
    <imsx_POXHeader>
        <imsx_POXResponseHeaderInfo>
            <imsx_version>V1.0</imsx_version>
            <imsx_messageIdentifier>%s</imsx_messageIdentifier>
            <imsx_statusInfo>
                <imsx_codeMajor>%s</imsx_codeMajor>
                <imsx_severity>status</imsx_severity>
                <imsx_description>%s</imsx_description>
                <imsx_messageRefIdentifier>%s</imsx_messageRefIdentifier> 
            </imsx_statusInfo>
        </imsx_POXResponseHeaderInfo>
    </imsx_POXHeader>
    <imsx_POXBody>%s
    </imsx_POXBody>
</imsx_POXEnvelopeResponse>';

try {
    $body = handleOAuthBodyPOST($oauth_consumer_key, $oauth_consumer_secret);
    $xml = new SimpleXMLElement($body);
    $imsx_header = $xml->imsx_POXHeader->children();
    $parms = $imsx_header->children();
    $message_ref = (string) $parms->imsx_messageIdentifier;

    $imsx_body = $xml->imsx_POXBody->children();
    $operation = $imsx_body->getName();
    $parms = $imsx_body->children();
} catch (Exception $e) {
    echo(sprintf($response,uniqid(),'failure', $e->getMessage(),'zzz',""));
    exit();
}


$header_key = getOAuthKeyFromHeaders();
if ( $header_key != $oauth_consumer_key ) {
   echo(sprintf($response,uniqid(),'failure', "B64=$oauth_consumer_key HDR=$header_key",$message_ref,""));
   exit();
}

$sourcedid = (string) $parms->resultRecord->sourcedGUID->sourcedId;
if ( !isset($sourcedid) && strlen($coursedid) > 0 ) {
   echo(sprintf($response,uniqid(),'failure', "Missing required lis_result_sourcedid",$message_ref,""));
   exit();
}

$gradebook = $_SESSION['cert_gradebook'];
if ( !isset($gradebook) ) $gradebook = Array();

$top_tag = str_replace("Request","Response",$operation);
if ( $operation == "replaceResultRequest" ) {
    $score =  (string) $parms->resultRecord->result->resultScore->textString;
    $fscore = (float) $score;
    if ( ! is_numeric($score) ) {
        echo(sprintf($response,uniqid(),'failure', "Score must be numeric",$message_ref,""));
        exit();
    }
    $fscore = (float) $score;
    if ( $fscore < 0.0 || $fscore > 1.0 ) {
        echo(sprintf($response,uniqid(),'failure', "Score not between 0.0 and 1.0",$message_ref,""));
        exit();
    }
    echo(sprintf($response,uniqid(),'success', "Score for $sourcedid is now $score",$message_ref,"\n<".$top_tag."/>"));
    $gradebook[$sourcedid] = $score;
} else if ( $operation == "readResultRequest" ) {
    $score =  $gradebook[$sourcedid];
    $body = '
    <readResultResponse>
      <result>
        <resultScore>
          <language>en</language>
          <textString>%s</textString>
        </resultScore>
      </result>
    </readResultResponse>';
    $body = sprintf($body,$score);
    echo(sprintf($response,uniqid(),'success', "Score read successfully",$message_ref,$body));
} else if ( $operation == "deleteResultRequest" ) {
    unset( $gradebook[$sourcedid]);
    echo(sprintf($response,uniqid(),'success', "Score deleted",'zzz',"\n<".$top_tag."/>"));
} else {
    echo(sprintf($response,uniqid(),'unsupported', "Operation not supported - $operation",$message_ref,""));
}
$_SESSION['cert_gradebook'] = $gradebook;
print_r($gradebook);
?>

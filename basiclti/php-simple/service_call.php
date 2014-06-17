<pre>
<?php
require_once("ims-blti/OAuthBody.php");

$method="POST";
$oauth_consumer_key = "lmsng.school.edu";
$oauth_consumer_secret = "secret";
$endpoint = 'http://localhost/~csev/php-simple/service_handle.php';
$endpoint = 'http://localhost:8080/imsblis/service';
$endpoint = 'http://localhost:8080/java-servlet/service';
$endpoint = 'http://localhost/~csev/php-simple/service_handle.php';
$endpoint = 'http://www.imsglobal.org/developers/BLTI/service_handle.php';

$content_type = "application/xml";

$body = '<?xml version = "1.0" encoding = "UTF-8"?>  
<imsx_POXEnvelopeRequest xmlns = "http://www.imsglobal.org/lis/oms1p0/pox">      
	<imsx_POXHeader>         
		<imsx_POXRequestHeaderInfo>            
			<imsx_version>V1.0</imsx_version>  
			<imsx_messageIdentifier>999999123</imsx_messageIdentifier>         
		</imsx_POXRequestHeaderInfo>      
	</imsx_POXHeader>      
	<imsx_POXBody>         
		<replaceResultRequest>            
			<resultRecord>
				<sourcedGUID>
					<sourcedId>3124567</sourcedId>
				</sourcedGUID>
				<result>
					<resultScore>
						<language>en-us</language>
						<textString>A</textString>
					</resultScore>
				</result>
			</resultRecord>       
		</replaceResultRequest>      
	</imsx_POXBody>   
</imsx_POXEnvelopeRequest>';

try {
    $response = sendOAuthBodyPOST($method, $endpoint, $oauth_consumer_key, $oauth_consumer_secret, $content_type, $body);
    echo("Base\n");
    echo(getLastOAuthBodyBaseString());

    echo("\r\n\r\n------------ POST RETURNS ------------\r\n");
    $response = str_replace("<","&lt;",$response);
    $response = str_replace(">","&gt;",$response);
    echo($response);
    echo("\n");
} catch (Exception $e) {
    $message = $e->getMessage();
    echo("General POST Failure: " . $message. "\n");
}

echo("\r\n\r\n------------ WE SENT ------------\r\n");
$body = str_replace("<","&lt;",$body);
$body = str_replace(">","&gt;",$body);
echo($body);
echo("\n");

?>
</pre>

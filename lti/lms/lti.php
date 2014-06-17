<?php
require_once "config.php";
header('P3P:CP="IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT"');

try {
    session_start();
} catch (Exception $e) {
    $warning_session = true;
}

require_once "db.php";

// Load up the Basic LTI Support code
require_once 'lib/lti_util.php';

$oauth_consumer_key = $_POST['oauth_consumer_key'];
if ( strlen($oauth_consumer_key) < 1 ) {
   die("Launch requires OAuth information");
}

$secret = false;
$sql = sprintf('SELECT * FROM  LTI_Keys WHERE lkey=%s',$db->quote($oauth_consumer_key));
$q = $db->query($sql);
if ( $q ) {
	$keydata = $q->fetch();
    if ( strlen($keydata['secret']) > 0 ) {
		$secret = $keydata['secret'];
		$consumer_id = $keydata['id'];
	}
    if ( strlen($keydata['secret']) > 0 ) $secret = $keydata['secret'];
}

if ( $secret === false ) {
    $sql = 'SELECT * FROM  LTI_Keys';
    $q = $db->query($sql);
    if ( $q ) {
	    $keydata = $q->fetch();
        if ( $keydata ) die('oauth_consumer_key not found');
    }
	$secret = "secret";
    $consumer_id = $CFG->defaultkeyid;
}

$context = new LTI($secret, true, false);
$sessid = session_id();
$_SESSION['_context_consumer_key'] = $oauth_consumer_key;
$_SESSION['_context_consumer_id'] = $consumer_id;

if ( ! $context->valid ) {
    echo("<p>This tool must be launched using IMS LTI from a Learning Management System.");
    if ( $SALT == 'secret' ) {
        echo("  All secrets are 'secret'.</p>\n");
    } else {
        echo(" Send your desired key to the site owner to get your secret.\n");
    }
    return;
}
//echo("<pre>\n");echo($context->dump()."\n");print_r($_SESSION);echo("</pre>");
//print "Redirecting....\n"; flush();
$context->redirect('mod/response/index.php');
?>

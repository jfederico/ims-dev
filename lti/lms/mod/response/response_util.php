<?php

// Set to "secret" to make all secrets secret
global $SALT;
$SALT = "secret";

function getFolderName($context)
{
    $foldername = $context->getResourceKey();
    // $foldername = md5($foldername);
    $foldername = dirname(__FILE__).'/upload/' . $foldername;
    return $foldername;
}

function getStudentFolder($context)
{
    $foldername = $context->getResourceKey();
    $userkey = $context->getUserKey();
    // $foldername = md5($foldername);
    // $userkey = md5($userkey);
    $foldername = dirname(__FILE__).'/upload/' . $foldername . '-students/' . $userkey . '/';
    return $foldername;
}

function fixFileName($name)
{
    $new = str_replace("..","-",$name);
    $new = str_replace("/", "-", $new);
    $new = str_replace("\\", "-", $new);
    $new = str_replace("\\", "-", $new);
    $new = str_replace(" ", "-", $new);
    return $new;
}

function establishContext() {
    session_start();
    // We want this to come from the session, not from the secret - this is not launchable
    $context = new LTI(rand()+"xyzzy", true, false);
    return $context;
}

function getSecret($oauth_consumer_key=false)
{ 
    if ( $oauth_consumer_key === false ) $oauth_consumer_key = $_REQUEST['oauth_consumer_key'];
    global $SALT;
    if ( $SALT == "secret" ) return "secret";
    return md5($SALT.$oauth_consumer_key);
}

function doCSS($context) {
    global $CFG;
    echo '<link rel="stylesheet" type="text/css" href="'.$CFG->wwwroot.'/static/css/default.css" />'."\n";
    foreach ( $context->getCSS() as $css ) {
        echo '<link rel="stylesheet" type="text/css" href="'.$css.'" />'."\n";
    }
}

function dumpPost() {
        print "<pre>\n";
        print "Raw POST Parameters:\n\n";
        ksort($_POST);
        foreach($_POST as $key => $value ) {
            if (get_magic_quotes_gpc()) $value = stripslashes($value);
            print "$key=$value (".mb_detect_encoding($value).")\n";
        }
        print "</pre>";
}

function dumpSession() {
        print "<pre>\n";
        print_r($SESSION);
        print "</pre>";
}
?>

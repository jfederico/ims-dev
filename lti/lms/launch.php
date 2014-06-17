<?php
define('COOKIE_SESSION', true);
require_once "db.php";
session_start();

requireLogin();

if ( ! isset($_GET['id']) ) {
    $_SESSION['err'] = 'Missing value for id';
    header( 'Location: error.php' ) ;
    return;
}

$sql = sprintf("SELECT name,lkey,id FROM LTI_Courses WHERE id=%s AND key_id=%s", 
    $db->quote($_GET['id']),$CFG->localkeyid) ;
$q = $db->query($sql);
$course = $q->fetch();
if ( ! $course ) {
    $_SESSION['error'] = 'Bad value for id';
    header( 'Location: error.php' ) ;
    return;
}

$sql = sprintf("SELECT name,lkey,image,email FROM LTI_Users WHERE id=%s AND key_id=%s", 
    $db->quote($_SESSION['user_id']),$CFG->localkeyid) ;
$q = $db->query($sql);
$user = $q->fetch();
if ( ! $course ) {
    $_SESSION['error'] = 'Bad value for id';
    header( 'Location: error.php' ) ;
    return;
}

$sql = sprintf("SELECT role_id FROM LTI_Members WHERE user_id=%s AND course_id=%s",
    $db->quote($_SESSION['user_id']), $db->quote($course['id']));
$q = $db->query($sql);
if ( $q ) $member = $q->fetch() ;

$_SESSION['_context_consumer_key'] = $CFG->localkey;
$_SESSION['_context_consumer_id'] = $CFG->localkeyid;

$_SESSION['_lti_context'] = Array(
            'oauth_consumer_key' => 'local',
            'resource_link_id' => 'rlid-1234',
            'resource_link_title' => 'This Week',
            'resource_link_description' => 'Please complete this',
            'user_id' => $user['lkey'],
            'roles' => ( $member['roleid'] == 0 ) ? 'Instructor' : 'Learner',
            'lis_person_name_full' => $user['name'],
            'lis_person_contact_email_primary' => $user['email'],
            'lis_person_sourcedid' => 'localhost.edu::'.$user['id'],
            'context_id' => $course['lkey'],
            'context_title' => $course['name'],
            'context_label' => $course['lkey'],
            'tool_consumer_info_product_family_code' => 'ims',
            'tool_consumer_info_version' => '1.1',
            'tool_consumer_instance_guid' => 'www.imsglobal.org',
            'tool_consumer_instance_description' => 'University of Localhost (LMSng)'
);

/*
echo("<pre>\n");
print_r($course);
print_r($user);
print_r($member);
print_r($_SESSION);
echo("</pre>\n");
*/

// Do this by hand to switch away from cookie based sessions.
$location = addSession('mod/response/index.php');
doRedirect($location);




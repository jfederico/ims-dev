<?php
define('COOKIE_SESSION', true);
require_once "db.php";
session_start();

requireLogin();
userMenu();
flashMessages();

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
?>
<iframe name="basicltiLaunchFrame"  id="basicltiLaunchFrame" src="launch.php?id=<?php echo($_GET['id']); ?>"
width="100%" height="600" scrolling="auto" frameborder="1" transparency>
<p>frames_required</p>
</iframe>



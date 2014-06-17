<?php
define('COOKIE_SESSION', true);
require_once "../db.php";
session_start();

if ( isset($_POST['name']) && isset($_POST['lkey']) ) {
    $n = $db->quote($_POST['name']);
    $k = $db->quote($_POST['lkey']);
    $ki = $CFG->localkeyid;
    $sql = "INSERT INTO LTI_Courses (name, lkey, key_id) 
              VALUES ($n, $k, $ki)";
    $rows = $db->exec($sql); 
    if ( $rows > 0 ) {
       $_SESSION['success'] = 'Record Added';
    } else {
       $info = $db->errorInfo();
       $_SESSION['err'] = 'Unable to add record:'.$db->errorCode().' '.$info[2].' '.$sql;
    }
    header( 'Location: index.php' ) ;
    return;
}

// Start the real page
adminMenu();
flashMessages();
?>
<p>Add A New Course</p>
<form method="post">
<p>Code:
<input type="text" name="lkey"></p>
<p>Title:
<input type="text" name="name"></p>
<p><input type="submit" value="Add New"/>
<a href="index.php">Cancel</a></p>
</form>


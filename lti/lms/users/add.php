<?php
define('COOKIE_SESSION', true);
require_once "../db.php";
session_start();

if ( isset($_POST['name']) && isset($_POST['email']) 
     && isset($_POST['password'])) {
    $n = $db->quote($_POST['name']);
    $e = $db->quote($_POST['email']);
    $p = $db->quote($_POST['password']);
    $l = $db->quote($_POST['lkey']);
    $ki = $CFG->localkeyid;
    $sql = "INSERT INTO LTI_Users (name, email, password, lkey, key_id) 
              VALUES ($n, $e, $p, $l, $ki)";
    $rows = $db->exec($sql); 
    if ( $rows > 0 ) {
       $_SESSION['success'] = 'Record Added';
    } else {
       $info = $db->errorInfo();
       $_SESSION['success'] = 'Unable to add record:'.$db->errorCode().' '.$info[2].' '.$sql;
    }
    header( 'Location: index.php' ) ;
    return;
}

// Start the real page
adminMenu();
flashMessages();

?>
<p>Add A New User</p>
<form method="post">
<p>Name:
<input type="text" name="name"></p>
<p>Account:
<input type="text" name="lkey"></p>
<p>Email:
<input type="text" name="email"></p>
<p>Password:
<input type="password" name="password"></p>
<p><input type="submit" value="Add New"/>
<a href="index.php">Cancel</a></p>
</form>

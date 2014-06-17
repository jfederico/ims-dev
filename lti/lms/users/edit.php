<?php
require_once "../db.php";
session_start();

if ( isset($_POST['name']) && isset($_POST['email']) 
     && isset($_POST['password']) && isset($_POST['id']) ) {
    $n = $db->quote($_POST['name']);
    $e = $db->quote($_POST['email']);
    $p = $db->quote($_POST['password']);
    $l = $db->quote($_POST['lkey']);
    $i = $db->quote($_POST['id']);
    $sql = sprintf("UPDATE LTI_Users SET name=%s, email=%s, password=%s, lkey=%s WHERE id=%s",
             $n, $e, $p, $l, $i);
    $rows = $db->exec($sql); 
    if ( $rows > 0 ) {
       $_SESSION['success'] = 'Record updated';
    } else {
       $info = $db->errorInfo();
       $_SESSION['success'] = 'Unable to update record:'.$db->errorCode().' '.$info[2].' '.$sql;
    }
    header( 'Location: index.php' ) ;
    return;
}

if ( ! isset($_GET['id']) ) {
    $_SESSION['err'] = 'Missing value for id';
    session_commit();
    header( 'Location: index.php' ) ;
    return;
}

$id = $db->quote($_GET['id']);
$sql = "SELECT name,email,password,id, lkey FROM LTI_Users WHERE id=$id";
$q = $db->query($sql);
$row = $q->fetch();
if ( ! $row ) {
    $_SESSION['error'] = 'Bad value for id';
    header( 'Location: index.php' ) ;
    return;
}

adminMenu();
flashMessages();

$n = htmlentities($row[0]);
$e = htmlentities($row[1]);
$p = htmlentities($row[2]);
$id = htmlentities($row[3]);
$l = htmlentities($row[4]);

echo <<< _END
<p>Edit User</p>
<form method="post">
<p>Name:
<input type="text" name="name" value="$n"></p>
<p>Account:
<input type="text" name="lkey" value="$l"></p>
<p>Email:
<input type="text" name="email" value="$e"></p>
<p>Password:
<input type="password" name="password" value="$p"></p>
<input type="hidden" name="id" value="$id">
<p><input type="submit" value="Update"/>
<a href="index.php">Cancel</a></p>
</form>
_END
?>


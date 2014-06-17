<?php
define('COOKIE_SESSION', true);
require_once "../db.php";
session_start();

if ( isset($_POST['name']) && isset($_POST['lkey']) && isset($_POST['id']) ) {
    $n = $db->quote($_POST['name']);
    $k = $db->quote($_POST['lkey']);
    $i = $db->quote($_POST['id']);
    $sql = sprintf("UPDATE LTI_Courses SET name=%s, lkey=%s WHERE id=%s", $n, $k, $i);
    $rows = $db->exec($sql); 
    if ( $rows > 0 ) {
       $_SESSION['success'] = 'Record Updated';
    } else {
       $info = $db->errorInfo();
       $_SESSION['err'] = 'Unable to update record:'.$db->errorCode().' '.$info[2].' '.$sql;
    }
    header( 'Location: index.php' ) ;
    return;
}

if ( ! isset($_GET['id']) ) {
    $_SESSION['err'] = 'Missing value for id';
    header( 'Location: index.php' ) ;
    return;
}

$sql = sprintf("SELECT name,lkey,id FROM LTI_Courses WHERE id=%s", $db->quote($_GET['id']) ) ;
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
$l = htmlentities($row[1]);
$id = htmlentities($_GET['id']);

echo <<< _END
<p>Edit Course</p>
<form method="post">
<p>Title:
<input type="text" name="name" value="$n"></p>
<p>Key:
<input type="text" name="lkey" value="$l"></p>
<input type="hidden" name="id" value="$id">
<p><input type="submit" value="Update"/>
<a href="index.php">Cancel</a></p>
</form>
_END
?>


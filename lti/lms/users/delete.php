<?php
define('COOKIE_SESSION', true);
require_once "../db.php";
session_start();

if ( isset($_POST['delete']) && isset($_POST['id']) ) {
    $id = $db->quote($_POST['id']);
    $sql = "DELETE FROM LTI_Users WHERE id = $id";
    $rows = $db->exec($sql);
    $_SESSION['success'] = 'Record deleted';
    header( 'Location: index.php' ) ;
    return;
}

if ( ! isset($_GET['id']) ) {
    $_SESSION['error'] = 'Missing value for id';
    header( 'Location: index.php' ) ;
    return;
}

$id = $db->quote($_GET['id']);
$sql = "SELECT name,id FROM LTI_Users WHERE id=$id";
$q = $db->query($sql);
$row = $q->fetch();
if ( ! $row ) {
    $_SESSION['error'] = 'Bad value for id';
    header( 'Location: index.php' ) ;
    return;
}

adminMenu();
flashMessages();

echo "<p>Confirm: Deleting $row[0]</p>\n";

echo('<form method="post"><input type="hidden" ');
echo('name="id" value="'.htmlentities($row[1]).'">'."\n");
echo('<input type="submit" value="Delete" name="delete">');
echo('<a href="index.php">Cancel</a>');
echo("\n</form>\n");
?>
<?php
define('COOKIE_SESSION', true);
require_once "../db.php";
session_start();

adminMenu();
flashMessages();
$sql = sprintf("SELECT name, image, email, password, id, lkey FROM LTI_Users where key_id=%s",$CFG->localkeyid);
$q = $db->query($sql);
$first = true;

while ( $q && $row = $q->fetch() ) {
    if ( $first ) {
        echo '<table>'."\n";
        echo("<tr><th>User Name</th><th>Account</th><th>EMail</th><th>Action</th></tr>\n");
        $first = false;
    }
    echo "<tr><td>";
    if ( strlen($row[1]) > 0 ) {
        echo('<img src="'.htmlentities($row[1]).'" width="40" height="40" style="align:left"/>');
    }
    echo(htmlentities($row[0]));
    echo("</td><td>");
    echo(htmlentities($row[5]));
    echo("</td><td>");
    echo(htmlentities($row[2]));
    echo("</td><td>\n");
    echo('<a href="edit.php?id='.htmlentities($row[4]).'">Edit</a> / ');
    echo('<a href="delete.php?id='.htmlentities($row[4]).'">Delete</a>');
    echo("</td></tr>\n");
}

if ( $first ) {
    echo("<p>No users found.</p>\n");
} else {
    echo("</table>\n");
}
?>
</table>
<a href="add.php">Add New</a>

<?php
define('COOKIE_SESSION', true);
require_once "../db.php";
session_start(); 

adminMenu();
flashMessages();

$sql = sprintf("SELECT name, lkey, id FROM LTI_Courses where key_id=%s", $CFG->localkeyid);
$q = $db->query($sql);
$first = true;

while ( $q && $row = $q->fetch() ) {
    if ( $first ) {
        echo '<table>'."\n";
        echo("<tr><th>Course Name</th><th>Course Key</th><th>Action</th></tr>\n");
        $first = false;
    }
    echo "<tr><td>";
    echo(htmlentities($row[0]));
    echo("</td><td>");
    echo(htmlentities($row[1]));
    echo("</td><td>\n");
    echo('<a href="members.php?course_id='.htmlentities($row[2]).'">Members</a> / ');
    echo('<a href="edit.php?id='.htmlentities($row[2]).'">Edit</a> / ');
    echo('<a href="delete.php?id='.htmlentities($row[2]).'">Delete</a>');
    echo("</td></tr>\n");
}

if ( $first ) {
    echo("<p>No courses found.</p>\n");
} else {
    echo("</table>\n");
}
?>
</table>
<a href="add.php">Add New</a>

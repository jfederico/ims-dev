<?php
define('COOKIE_SESSION', true);
require_once "db.php";
session_start();
requireLogin();

userMenu();
flashMessages();

$sql = sprintf("SELECT name, lkey, id FROM LTI_Courses WHERE key_id=%s",$CFG->localkeyid);
$q = $db->query($sql);
$first = true;

?>
<div id="medium-dialog-container">
<div id="medium-dialog">
<?php

while ( $q && $row = $q->fetch() ) {
    if ( $first ) {
        echo '<center><table>'."\n";
        echo("<tr><th>Course Name</th><th>Course Key</th><th>Action</th></tr>\n");
        $first = false;
    }
    echo "<tr><td>";
    echo(htmlentities($row[0]));
    echo("</td><td>");
    echo(htmlentities($row[1]));
    echo("</td><td>\n");
    echo('<a href="course.php?id='.htmlentities($row[2]).'">Launch</a>');
    echo("</td></tr>\n");
}

if ( $first ) {
    echo("<p>No courses found.</p>\n");
} else {
    echo("</table></center>\n");
}
?>
</div>
</div>
</body>
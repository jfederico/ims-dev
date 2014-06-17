<?php
define('COOKIE_SESSION', true);
require_once "../db.php";
session_start(); 

$sql = sprintf("SELECT * FROM LTI_Courses WHERE id=%s", 
    $db->quote($_REQUEST['course_id']) ) ;
$q = $db->query($sql);
if ( $q && $course = $q->fetch() ) {
    // Good
} else {
    $_SESSION['err'] = 'Bad value for course_id';
    header( 'Location: index.php' ) ;
    return;
}

if ( strlen($_POST['role_user_id']) > 0 && strlen($_POST['role_id']) > 0 ) {
    $u = $db->quote($_POST['role_user_id']);
    $r = $db->quote($_POST['role_id']);
    $c = $db->quote($_REQUEST['course_id']);
    $sql = sprintf("INSERT INTO LTI_Members (user_id, course_id, role_id) VALUES (%s, %s, %s)", $u, $c, $r);
    $rows = $db->exec($sql); 
    if ( $rows > 0 ) {
       $_SESSION['success'] = 'Membership Added';
    } else {
       $info = $db->errorInfo();
       $_SESSION['err'] = 'Unable to add membership record:'.$db->errorCode().' '.$info[2].' '.$sql;
    }
    // Need to add the course_id
    // header( 'Location: members.php' ) ;
    // return;
}

if ( strlen($_POST['course_id']) > 0 && strlen($_POST['delete_id']) > 0 ) {
    $c = $db->quote($_POST['course_id']);
    $d = $db->quote($_POST['delete_id']);
    $sql = sprintf("DELETE FROM LTI_Members WHERE course_id=%s AND id=%s", $c, $d);
    $rows = $db->exec($sql); 
    if ( $rows > 0 ) {
       $_SESSION['success'] = 'Membership deleted';
    } else {
       $info = $db->errorInfo();
       $_SESSION['err'] = 'Unable to delete membership record:'.$db->errorCode().' '.$info[2].' '.$sql;
    }
    // Need to add the course_id
    // header( 'Location: members.php' ) ;
    // return;
}

adminMenu();
flashMessages();

$sql = sprintf("SELECT LTI_Members.id, LTI_Members.user_id, course_id, role_id, name FROM LTI_Members JOIN LTI_Users
        ON LTI_Members.user_id = LTI_Users.id WHERE course_id=%s",$db->quote($_REQUEST['course_id']) ) ;
$q = $db->query($sql);
// echo($sql);
echo '<table>'."\n";
echo("<tr><th>Member Name</th><th>Role</th><th>Action</th></tr>\n");
$found = false;
$users = Array();
while ( $q && $row = $q->fetch() ) {
    // echo("<pre>\n");print_r($row);echo("</pre>");
    $users[] = $row['user_id'];
    $found = true;
    echo "<tr><td>";
    if ( strlen($row['image']) > 0 ) {
        echo('<img src="'.htmlentities($row['image']).'" width="40" height="40" style="align:left"/>');
    }
    echo(htmlentities($row['name']));
    echo("</td><td>");
    echo(htmlentities($row['role_id']));
    echo("</td><td>\n");
    echo('<form method="post"><input type="hidden" name="course_id" value="'.htmlentities($_REQUEST['course_id']).'">');
    echo('<input type="hidden" name="delete_id" value="'.htmlentities($row['id']).'"><input type="submit" value="Delete">');
    echo("</form>\n");
    echo("</td></tr>\n");
}

if ( ! $found ) {
    echo('<tr><td colspan="3">No memberships.</td></tr>'."\n");
}
echo('<tr><td>');
echo('<form method="post">'."\n");
echo('<input type="hidden" name="course_id" value="'.htmlentities($course['id']).'">'."\n");

// Addition
$sql = 'SELECT * FROM LTI_Users WHERE key_id IS NULL';
if ( strlen($course['key_id']) > 0 ) {
    $sql = sprintf("SELECT * FROM LTI_Users WHERE key_id=%s",$db->quote($course['key_id']) ) ;
}
// echo($sql);
$q = $db->query($sql);
$first = true;
?>
<select name="role_user_id">
  <option value="-1">-- Please Select a User --</option>
<?php
while ( $q && $row = $q->fetch() ) {
    $found = false;
    foreach ( $users as $user_id ) {
       if ( $user_id == $row['id'] ) $found = true;;
    }
    if ( $found ) continue;
    echo('  <option value="'.htmlentities($row['id']).'">'.htmlentities($row['name']).'</option>'."\n");
}
?>
</select>
</td><td>
<select name="role_id">
  <option value="-1">-- Role --</option>
  <option value="0">Instructor</option>
  <option value="1">Learner</option>
</select>
</td><td>
<input type="submit" value="Add">
</td></tr>
</form>
</table>
<p>
<a href="index.php">Back to courses</a>
</p>
</body>
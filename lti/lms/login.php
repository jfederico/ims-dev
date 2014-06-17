<?php
define('COOKIE_SESSION', true);
require_once "db.php";
session_start();

unset($_SESSION['admin']);
unset($_SESSION['user_id']);
unset($_SESSION['user_name']);

if ( isset($_POST['account']) && isset($_POST['password']) ) {
    if ( $_POST['account'] == 'admin' && $_POST['password'] == 'imslti' ) {
        $_SESSION['admin'] = 'yes';
        doRedirect('courses/index.php');
        return;
    }
    $a = $db->quote($_POST['account']);
    $p = $db->quote($_POST['password']);
    $sql = sprintf("SELECT * FROM LTI_Users WHERE (email=%s OR lkey=%s) AND password=%s AND key_id=%s",
             $a, $a, $p, $CFG->localkeyid);
    $q = $db->query($sql); 
    if ( $q && $user = $q->fetch() ) {
        $_SESSION['user_id'] = $user['id'];
        $_SESSION['user_name'] = $user['name'];
        doRedirect('courses.php');
    } else {
        $_SESSION['err'] = 'Login failed';
    }
}

userMenu();
flashMessages();
?>
<div id="small-dialog-container">
<div id="small-dialog">
<form method="post">
<p>E-Mail or Account:
<input type="text" name="account"></p>
<p>Password:
<input type="text" name="password"></p>
<p><input type="submit" value="Login"/>
<?php
if ( stripos($CFG->pdo, 'sqlite') !== false) {
?>
<a href="<?php echo($CFG->wwwroot); ?>/phpliteadmin.php" target="_new">Database Admin</a>
<?php } ?>
</p>
</form>
</div>
</div>
</body>
<?php
require_once "config.php";
require_once "lib/foorm.php";

global $db;

// TODO: Make this absolute - config.php
try {
    // $db = new SQLiteDatabase('db/response.sqlite');
    $db = new PDO($CFG->pdo, $CFG->pdouser, $CFG->pdopass);
} catch(Exception $e ) {
    if ( strpos($CFG->pdo,'mysql') !== false ) {
        echo("<p>Unable to connect to MySQL database. You need to 
create the database, grant access to the database, and then
edit the <b>config.php</b> file to set the database name, 
user, and password. The following are the commands you 
may want to alter/use to create the database and 
authorize a user.</p>
<pre>
create database lms default character set utf8;
grant all on lms.* to lmsuser@'localhost' identified by 'lmspassword';
grant all on lms.* to lmsuser@'127.0.0.1' identified by 'lmspassword';
</pre>
");
    } else {
        echo('
<p>We cannot create the SQLite database for this application.  The most likely
reason is that the "db" directory either does not exist or is not
writeable by the PHP web server.  Please give the web server permission
to write in this directory and re-attempt.</p>
');
    }
    echo("<p>\n");
    echo($e->getMessage());
    echo("</p>\n");
    die("Unable to start database..");
}

function makeTable($db, $table, $formFields) {
    $vendor = $db->getAttribute(PDO::ATTR_DRIVER_NAME);
    $commands = FOORM::formSqlTable($table, $formFields, $vendor);
    foreach ( $commands as $command ) {
        // echo($command);echo("<br/>");
        $ret = $db->exec($command);
        $code = (string)$db->errorCode();
        $info = $db->errorInfo();
        if ( $code == '42S01' ) $ret = true;  // Mysql already exists
		if ( stripos($info[2],'already exists') > 0 ) $ret = true;
        if ( $ret === false ) {
            echo("<p>Error in SQL:</p><p>\n".$command."</p>\n");
            die($code . ':' . $info[2]);
        }
    }
}

// Generic tables for keys/users/resource
makeTable($db, 'LTI_Keys', Array(
    'id:key',
    'lkey:lkey:maxlength=512',
    'secret:text:maxlength=512',
    'name:text:maxlength=512',
    'email:text:maxlength=512',
    'context_id:text:maxlength=80',
    )
);

makeTable($db, 'LTI_Users', Array(
    'id:key',
    'lkey:text:unique=true:maxlength=256',
    'key_id:integer:unique=true',
    'name:text:maxlength=512',
    'image:text:maxlength=2048',
    'email:text:maxlength=512',
    'password:text:maxlength=128',
    'user_id:integer',
    )
);

makeTable($db, 'LTI_Courses', Array(
    'id:key',
    'lkey:text:unique=true:maxlength=256',
    'key_id:integer:unique=true',
    'name:text:maxlength=512',
    'image:text:maxlength=2048',
    'course_id:integer',
    )
);

makeTable($db, 'LTI_Members', Array(
    'id:key',
    'user_id:integer:unique=true',
    'course_id:integer:unique=true',
    'role_id:integer',
    )
);

makeTable($db, 'LTI_Resources', Array(
    'id:key',
    'lkey:text:unique=true:maxlength=256',
    'course_id:integer:unique=true',
    'name:text:maxlength=512',
    'service:text:maxlength=2048',
    )
);

// Modules
require_once('mod/response/db.php');

?>

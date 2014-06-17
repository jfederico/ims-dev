<?php
require_once("../../config.php");
require_once $CFG->dirroot.'/lib/lti_util.php';
require_once 'response_util.php';

// Get our session setup without a cookie
$context = establishContext();

if ( ! $context->valid ) {
   die("Basic LTI Session failure ".$_SERVER['PHP_SELF']);
}

require_once $CFG->dirroot.'/db.php';
require_once $CFG->dirroot.'/pdo_util.php';

setupPrimaryKeys($db, $context);

if ( $_POST['response'] ) {
    $sql = sprintf("SELECT * FROM Responses WHERE resource_id=%s AND user_id=%s LIMIT 1\n",
            $db->quote($context->getResourceID()), $db->quote($context->getUserID()) );
    
    $q = @$db->query($sql);
    $response = $q->fetch();
    if ( $response ) {

        // Do we need to change anything?
        if ( $response['data'] != $_POST['response'] || 
           ( strlen($context->getOutcomeSourceDID()) > 0 &&
              $response['sourcedid'] != $context->getOutcomeSourceDID() ) ) {

            if ( strlen($context->getOutcomeSourceDID()) > 0 ) {
                $sql = sprintf("UPDATE Responses SET data=%s, sourcedid=%s 
                                WHERE resource_id=%s AND user_id=%s\n",
                    $db->quote($_POST['response']), $db->quote($context->getOutcomeSourceDID()),
                    $db->quote($context->getResourceID()), $db->quote($context->getUserID()) );
            } else {
                $sql = sprintf("UPDATE Responses SET data=%s WHERE resource_id=%s AND user_id=%s\n",
                    $db->quote($_POST['response']), 
                    $db->quote($context->getResourceID()), $db->quote($context->getUserID()) );
            }
            // echo($sql);flush();
            $rows = $db->exec($sql);
            if ( $rows > 0 ) {
                $_SESSION['success'] = 'Data updated';
            } else { 
                $_SESSION['err'] = 'Unable to update data';
            }
        }
    } else {
        $sql = sprintf("INSERT INTO Responses (resource_id, user_id, data, sourcedid) 
                        VALUES (%s, %s, %s, %s)\n",
            $db->quote($context->getResourceID()), $db->quote($context->getUserID()),
            $db->quote($_POST['response']), $db->quote($context->getOutcomeSourceDID()) );
        // echo($sql);flush();
        $rows = $db->exec($sql);
        if ( $rows > 0 ) {
            $_SESSION['success'] = 'Data inserted';
        } else { 
            $_SESSION['err'] = 'Unable to insert data ';
        }
    }
    $context->redirect('index.php');
    return;
}

header('Content-Type: text/html; charset=utf-8');
?>
<html>
<head><title>
<?php echo $context->getCourseName; echo " "; echo $context->getResourceTitle(); ?>
</title> 
<?php doCSS($context); ?>
<script type="text/javascript" src="<?php echo($CFG->wwwroot); ?>/static/js/jquery.min.js">
</script>
</head> 
<body>
<?php
if ( isset($_SESSION['err']) ) {
    echo '<p style="color:red">'.$_SESSION['err']."</p>\n";
    unset($_SESSION['err']);
}
if ( isset($_SESSION['success']) ) {
    echo '<p style="color:green">'.$_SESSION['success']."</p>\n";
    unset($_SESSION['success']);
}

if ( $context->isInstructor() ) {
    $sql = sprintf("SELECT * FROM Responses JOIN LTI_Users
                    ON Responses.user_id = LTI_Users.id WHERE resource_id=%s\n",
            $db->quote($context->getResourceID()));
    $q = @$db->query($sql);
    $first = true;
    while ( $response = $q->fetch() ) {
        if ( $first ) {
            echo('<table width="100%"><tr><th>Student</th><th>Response</th><th>Notes</th><th>Grade</th></tr>'."\n");
            $first = false;
        }
        echo("<tr><td>");
        if ( strlen($response['image']) > 0 ) {
            echo('<img src="'.$response['image'].'" width="30" height="30" style="float:left">');
        }
        echo(htmlentities($response['name']));
        echo('</td><td>');
        echo(htmlentities(substr($response['data'],0,60)));
        echo('</td><td>');
        echo(htmlentities(substr($response['note'],0,60)));
        echo('</td><td>');
        echo(htmlentities($response['grade']));
        echo(' <a href="grade.php?response_id='.$response[0].'">Grade</a>');
        echo('</td></tr>'."\n");
    }
    if ( $first ) {
        echo("<p>No Student Responses Found</p>\n");
    } else {
        echo("</table>\n");
    }
}

$sql = sprintf("SELECT * FROM Responses WHERE resource_id=%s AND user_id=%s LIMIT 1\n",
        $db->quote($context->getResourceID()), $db->quote($context->getUserID()) );

$q = @$db->query($sql);
$response = $q->fetch();
$text = false;
if ( $response ) {
   $text = $response['data'];
}

?>
<form  method="post">
<textarea name="response" rows="10" cols="60">
<?php echo($text); ?>
</textarea><br/>
<input type="submit" value="Submit">
</form>
<?php
/*
print "\n<pre>\n";
print "Context Information:\n\n";
print $context->dump();
print "\n\nSESSION\n";
print_r($_SESSION);
print "\n</pre>\n";
*/

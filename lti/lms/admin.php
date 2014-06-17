<?php
header('P3P:CP="IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT"');
define('COOKIE_SESSION', true);
require_once("config.php");

try {
    session_start();
} catch (Exception $e) {
    $warning_session = true;
}

error_reporting(E_ALL & ~E_NOTICE);
ini_set("display_errors", 1);

require_once("db.php");

$self = $_SERVER['PHP_SELF'];
$action = $_REQUEST['action'];
$message = false;
$oauth_consumer_key = false;
$secret = false;
$context_id = false;
$name = false;
$idvalue = false;

// Delete Action - Make sure you are admin
if ( $action == 'delete' && ! empty($_SESSION['admin']) ) {
    $idvalue = $_REQUEST['id'];
    if ( $idvalue ) {
        $sql = sprintf('DELETE FROM LTI_Keys WHERE id=%s',
            $db->quote($idvalue));
        $retval = $db->exec($sql);
        if ( $retval != 1 ) {
            $message = "Error, unable to delete data.";
            $action = 'view';
        } else {
            $message = "Deleted record id=".$idvalue;
            $action = 'view';
        }
    }
}

// Edit/Add Action
if ( $_SERVER['REQUEST_METHOD'] == 'POST' && ! empty($_SESSION['admin']) && 
     empty($_REQUEST['password']) ) {

    $oauth_consumer_key = $_REQUEST['oauth_consumer_key'];
    $secret = $_REQUEST['secret'];
    $name = $_REQUEST['name'];
    $context_id = $_REQUEST['context_id'];
    if ( strlen($context_id) < 1 ) $context_id = false;
    $idvalue = $_REQUEST['id'];
    if ( $name && $secret && $oauth_consumer_key ) {
       if ( $idvalue ) {
           $sql = sprintf('UPDATE LTI_Keys SET name=%s, lkey=%s, secret=%s, context_id=%s
                           WHERE id=%s',
              $db->quote($name), $db->quote($oauth_consumer_key), $db->quote($secret),
              $db->quote($context_id), $db->quote($idvalue) );
           $retval = $db->exec($sql);
           if ( $retval != 1 ) {
               $message = "Error, unable to update data.";
               $action = 'edit';
           } else {
               $message = "Updated record for '".$name."' id=".$idvalue;
               $action = 'view';
           }
       } else {
           $sql = sprintf('INSERT INTO LTI_Keys (name, lkey, secret, context_id)
                           VALUES (%s, %s, %s, %s)',
              $db->quote($name), $db->quote($oauth_consumer_key), $db->quote($secret),
              $db->quote($context_id));
           $retval = $db->exec($sql);
           if ( $retval != 1 ) {
               $message = "Error, unable to insert data.";
               $action = 'add';
           } else {
               $message = "Inserted record";
               $action = 'view';
           }
       }
    } else {
       $message = "Error, please specify all data.";
       $action = 'add';
       if ( $idvalue ) $action = 'edit';
    }
}

// Bootstrapping admin account - check if we have one
$adminsecret = false;
$sql = "SELECT * FROM LTI_Keys WHERE lkey='admin'";
$q = $db->query($sql);
if ( ! $q ) {
    $message = "You are insecure - bootstrapping the admin account.  Please create a consumer with an oauth_consumer_key of 'admin' and remember the secret to login as admin.";
    $_SESSION['admin'] = 'temp';
} else {
    while ( $result = $q->fetch() ) {
        $adminsecret = $row['secret'];
        break;
    }
}

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
   <link href="<?php echo($CFG->wwwroot); ?>/static/css/default.css" rel="stylesheet" type="text/css" />
  </head>
<body>
<h1>IMS LTI Consumer Administration</h1>
<?php

// Deal with the admin need to log in and the log in action
if ( empty($_SESSION['admin']) || ( $adminsecret && $_SESSION['admin'] == 'temp') ) {
    if ( $_SERVER['REQUEST_METHOD'] == 'POST' ) {
        if ( $adminsecret == $_REQUEST['password'] ) {
            $_SESSION['admin'] = 'on';
            $message = "Administrator logged in.";
        }            
    }
    if ( empty($_SESSION['admin']) || $_SESSION['admin'] == 'temp' ) {
        ?>
        <p>Please Log In</p>
        <form method="post" action="<?=$self?>">
        <p>Admin Password<br>
        <input type="password" name="password" ></p>
        <p><input type="submit"></p>
        </form>
        <?php
        exit();
    }
}

// Retrieve the requested record for the edit view
if ( $action == 'edit' ) {
    $idvalue = $_REQUEST['id'];
    if ( $idvalue ) {
       $sql = sprintf('SELECT * FROM LTI_Keys WHERE id=%s',
          $db->quote($idvalue));
      $q = $db->query($sql);
      $row = $q->fetch();
      if ( ! $row ) {
          $message = "Error, could not locate data.";
          $action = 'view';
      } else {
           $oauth_consumer_key = $row['lkey'];
           $secret = $row['secret'];
           $context_id = $row['context_id'];
           $name = $row['name'];
      }
    }
}

// Put up the form for the add / edit views
if ( $action == 'add' || $action == 'edit' ) {
    if ( $message ) {
       echo('<p style="color:red;">'.$message."</p>\n");
    }
    if ( $action == 'add' ) {
        echo("<p>Add New Consumer</p>\n");
    } else {
        echo("<p>Edit Consumer</p>\n");
    }
    ?>
    <form method="post">
    <?php
    if ( $idvalue ) echo('<input type="hidden" name="id" value="'.$idvalue.'">'."\n");
    ?>
    <p>Display Name
    <input type="text" name="name" value="<?=$name?>"></p>
    <p>oauth_consumer_key
    <input type="text" name="oauth_consumer_key" value="<?=$oauth_consumer_key?>" ></p>
    <p>secret
    <input type="text" name="secret"  value="<?=$secret?>"></p>
    <p>context_id 
    <input type="text" name="context_id"  value="<?=$context_id?>"></p>
    <p>Leave context_id blank unless you want to override the 
    context_id from the Consumer for this key.</p>
    <p><input type='submit'>
    <input type="submit" value="Cancel" 
        onclick="window.location='<?=$self?>'; return false;"/>
    </p>
    </form>
    <?php
    exit();
}

if ( $message ) {
    echo('<p style="color:red;">'.$message."</p>\n");
}

// Normal main/list view that shows all the existing keys
$sql = "SELECT * FROM  LTI_Keys;";
$q = $db->query($sql);
if ( ! $q ) {
    echo "<p>This system has no defined oauth_consumer_key values.</p>\n";
} else {
    ?>
    <table>
    <tr><th>Name</th><th>oauth_consumer_key</th><th>Secret</th><th>context_id</th><th>Action</th></tr>
    <?php
    while( $row = $q->fetch() ) {
         ?>
         <tr>
         <td><?=$row['name']?></td>
         <td><?=$row['lkey']?></td>
         <td><?=$row['secret']?></td>
         <td><?=$row['context_id']?></td>
         <td>
         <a title="Edit Tool" href="<?=$self?>?action=edit&id=<?=$row[id]?>">edit</a>
         <a title="Delete Tool" href="<?=$self?>?action=delete&id=<?=$row[id]?>">delete</a>
         </td>
         </tr>
         <?php
    }
    ?>
    </table>
    <?php
}
echo('<p><a title="Add a Tool" href="'.$self.'?action=add">Add Consumer</a></p>'."\n");

?>
</body>

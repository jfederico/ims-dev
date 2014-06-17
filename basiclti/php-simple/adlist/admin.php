<?php
header('P3P:CP="IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT"');
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
        $sql = 'DELETE FROM blti_keys WHERE id=' .
            "'".mysql_real_escape_string($idvalue)."'";
        $result = mysql_query($sql);
        $retval = mysql_affected_rows();
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
           $sql = 'UPDATE blti_keys SET ' .
              "updated_at=NOW(), " .
              "name='".mysql_real_escape_string($name)."', " .
              "secret='".mysql_real_escape_string($secret)."', " .
              "secret='".mysql_real_escape_string($secret)."', " .
              "context_id=".($context_id ? ("'".mysql_real_escape_string($context_id)."', ") : "NULL, " ) .
              "oauth_consumer_key='".mysql_real_escape_string($oauth_consumer_key)."' ".
              "WHERE id=".
              "'".mysql_real_escape_string($idvalue)."'";
           $result = mysql_query($sql);
           $retval = mysql_affected_rows();
           if ( $retval != 1 ) {
               $message = "Error, unable to update data.";
               $action = 'edit';
           } else {
               $message = "Updated record for '".$name."' id=".$idvalue;
               $action = 'view';
           }
       } else {
           $sql = 'INSERT INTO blti_keys ' .
              '( name, secret, oauth_consumer_key, context_id ) ' .
              ' VALUES ( ' .
              "'".mysql_real_escape_string($name)."', " .
              "'".mysql_real_escape_string($secret)."', " .
              "'".mysql_real_escape_string($oauth_consumer_key)."', " .
              ($context_id ? ("'".mysql_real_escape_string($context_id)."' ") : 'NULL' ) . ')' ;
           $result = mysql_query($sql);
           $retval = mysql_affected_rows();
           if ( $retval != 1 ) {
               $message = "Error, unable to insert data.";
               $action = 'add';
           } else {
               $idvalue = mysql_insert_id();
               $message = "Inserted record for '".$name."' id=".$idvalue;
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
$sql = "SELECT * FROM blti_keys WHERE oauth_consumer_key='admin'";
$result = mysql_query($sql);
$num_rows = mysql_num_rows($result);
if ( $num_rows == 0 ) {
    $message = "You are insecure - bootstrapping the admin account.  Please create a consumer with an oauth_consumer_key of 'admin' and remember the secret to login as admin.";
    $_SESSION['admin'] = 'temp';
} else {
    while ($row = mysql_fetch_assoc($result)) {
        $adminsecret = $row['secret'];
        break;
    }
}

?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
   <link href="glike.css" rel="stylesheet" type="text/css" />
  </head>
<body>
<h1>IMS Basic LTI Consumer Administration</h1>
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
       $sql = 'SELECT * FROM blti_keys WHERE id=' .
          "'".mysql_real_escape_string($idvalue)."'";
      $result = mysql_query($sql);
      $num_rows = mysql_num_rows($result);
      if ( $num_rows != 1 ) {
          $message = "Error, could not locate data.";
          $action = 'view';
      } else {
          while ($row = mysql_fetch_assoc($result)) {
              $oauth_consumer_key = $row['oauth_consumer_key'];
              $secret = $row['secret'];
              $context_id = $row['context_id'];
              $name = $row['name'];
          }
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
    <form method="post" action="<?=$self?>">
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

// Normal main/list view that shows all the existing tools
$sql = "SELECT * FROM  blti_keys;";
$result = mysql_query($sql);
$num_rows = mysql_num_rows($result);

if ( $message ) {
    echo('<p style="color:red;">'.$message."</p>\n");
}

if( mysql_num_rows( $result ) == 0 ) {
    echo "<p>This system has no defined oauth_consumer_key values.</p>\n";
} else {
    ?>
    <table>
    <tr><th>Name</th><th>oauth_consumer_key</th><th>Secret</th><th>context_id</th><th>Action</th></tr>
    <?php
    while( $row = mysql_fetch_array( $result ) ) {
         ?>
         <tr>
         <td><?=$row['name']?></td>
         <td><?=$row['oauth_consumer_key']?></td>
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

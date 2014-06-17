<?php 
header('P3P:CP="IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT"');
ini_set('session.use_cookies', '0');
try {
    session_start(); 
} catch (Exception $e) {
    $warning_session = true;
}

error_reporting(E_ALL & ~E_NOTICE);
ini_set("display_errors", 1);

// Load up the Basic LTI Support code
require_once '../ims-blti/blti.php';

// Establish the database connnection
require_once("db.php");

// Establish the context
$context = new BLTI(array('table' => 'blti_keys'));
if ( $context->complete ) exit();
if ( ! $context->valid ) {
    print "Could not establish context: ".$context->message."<p>\n";
    exit();
}

// Start of the ad code
$self = $context->addSession($_SERVER['PHP_SELF']);
if ( strpos($self,'?') > 0 ) {
  $selfp = $self . '&';
} else {
  $selfp = $self . '?';
}
$action = $_REQUEST['action'];
$message = false;
$title = false;
$description = false;
$idvalue = false;

// print_r($_REQUEST);

// AuthZ WHERE clause terms
$authzsql = "course_key=".
              "'".mysql_real_escape_string($context->getCourseKey())."'";
if ( ! $context->isInstructor() ) {
    $authzsql = $authzsql . "AND user_key=".
              "'".mysql_real_escape_string($context->getUserKey())."'";
}

if ( $action == 'delete' ) {
    $idvalue = $_REQUEST['id'];
    if ( $idvalue ) {
        $sql = 'DELETE FROM ads WHERE id=' .
            "'".mysql_real_escape_string($idvalue)."' AND ".$authzsql;
        $result = mysql_query($sql);
        $retval = mysql_affected_rows();
        if ( $retval != 1 ) {
            $message = "Error, unable to delete ad.";
            $action = 'main';
        } else {
            $message = "Deleted record id=".$idvalue;
            $action = 'main';
        }
    }
}

if ( $_SERVER['REQUEST_METHOD'] == 'POST' ) {
    $title = $_REQUEST['title'];
    $description = $_REQUEST['description'];
    $idvalue = $_REQUEST['id'];
    if ( $title && $description ) {
       if ( $idvalue ) {
           $sql = 'UPDATE ads SET ' .
              "title='".mysql_real_escape_string($title)."', " .
              "description='".mysql_real_escape_string($description)."', " .
              "updated_at= NOW() " .
              "WHERE id=".
              "'".mysql_real_escape_string($idvalue)."' AND ".$authzsql;
           $result = mysql_query($sql);
           $retval = mysql_affected_rows();
           if ( $retval != 1 ) {
               $message = "Error, unable to update ad.";
               $action = 'edit';
           } else {
               $message = "Updated record for '".$title."' id=".$idvalue;
               $action = 'main';
           }
       } else {
           $sql = 'INSERT INTO ads ' .
              '( title, description, course_key, user_key, user_name, user_image, created_at, updated_at ) ' .
              ' VALUES ( ' .
              "'".mysql_real_escape_string($title)."', " .
              "'".mysql_real_escape_string($description)."', " .
              "'".mysql_real_escape_string($context->getCourseKey())."', " .
              "'".mysql_real_escape_string($context->getUserKey())."', " .
              "'".mysql_real_escape_string($context->getUserName())."', " .
              "'".mysql_real_escape_string($context->getUserImage())."', " .
              " NOW(), NOW() ) ";
           $result = mysql_query($sql);
           $retval = mysql_affected_rows();
           if ( $retval != 1 ) {
               $message = "Error, unable to insert ad.";
               $action = 'add';
           } else {
               $idvalue = mysql_insert_id();
               $message = "Inserted ad '".$name."' id=".$idvalue;
               $action = 'main';
               $context->redirect();
           }
       }
    } else {
       $message = "Error, please specify all data.";
       $action = 'add';
       if ( $idvalue ) $action = 'edit';
    }
}
?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
  <head>
   <title>Basic LTI Classified Ad System</title>
   <link href="glike.css" rel="stylesheet" type="text/css" />
  </head>
<body>
<?php

if ( $action == 'edit' || $action == 'view' ) {
    $idvalue = $_REQUEST['id'];
    if ( $idvalue ) {
       $sql = 'SELECT * FROM ads WHERE id=' .
          "'".mysql_real_escape_string($idvalue)."' AND ".$authzsql;
      $result = mysql_query($sql);
      $num_rows = mysql_num_rows($result);
      if ( $num_rows != 1 ) {
          $message = "Error, could not locate ad.";
          $action = 'main';
      } else {
          while ($row = mysql_fetch_assoc($result)) {
              $title = $row['title'];
              $description = $row['description'];
              $user_name = $row['user_name'];
              $user_image = $row['user_image'];
              $updated_at = $row['updated_at'];
          }
      }
    }
}

if ( $action == 'view' ) {
    $image = $context->getUserImage();
    if ( strlen($image) > 0 ) {
        echo('<img src="'.$user_image.'" align="right" width="40" height="40">'."\n");   
    }
    if ( $message ) {
       echo('<p style="color:red;">'.$message."</p>\n");
    }
    echo("<p><b>$title</b><br/>\n");
    echo($description."</p>\n");
    echo("<p>$user_name ($updated_at) </p>\n");
    ?>
        <input type="submit" value="Back To List" 
            onclick="window.location='<?=$self?>'; return false;"/>
    <?php
    exit();
}

if ( $action == 'add' || $action == 'edit') {
    if ( $message ) {
       echo('<p style="color:red;">'.$message."</p>\n");
    }
    $enabled="";
    if ( $action == 'add' ) {
        echo("<p>Add New Classified Ad</p>\n");
    } else {
        echo("<p>Edit Classified Ad</p>\n");
    }
    ?>
    <form method="post">
    <?php
    if ( $idvalue ) echo('<input type="hidden" value="'.$idvalue.'">'."\n");
    ?>
    <p>Title<br/>
    <input type="text" name="title" size="70" value="<?=$title?>"></p>
    <p>Description<br/>
    <textarea id="description" name="description" rows="12" cols="70"><?=$description?></textarea></p>
    <p><input type='submit'>
    <input type="submit" value="Cancel" 
        onclick="window.location='<?=$self?>'; return false;"/>
    </p>
    </form>
    <?php
    exit();
}

$sql = "SELECT * FROM  ads WHERE course_key=".
    "'".mysql_real_escape_string($context->getCourseKey())."' " .
    "ORDER BY created_at DESC";

$result = mysql_query($sql);
$num_rows = mysql_num_rows($result);

if ( $message ) {
    echo('<p style="color:red;">'.$message."</p>\n");
}

if( mysql_num_rows( $result ) == 0 ) {
    echo "<p>No Ads available for '".$context->getCourseName()."'";
    echo(' (<a title="New Ad" href="'.$selfp.'action=add">Create New Ad</a>)'."\n");
} else {
    ?>
    <table style="width: 100%">
    <tr><th style="width: 70%">
<?php 
$title = $context->getResourceTitle();
if ( $title === false ) {
   echo('Title');
} else {
   echo($title);
}
?>

(<a title="New Ad" href="<?=$selfp?>action=add">Create New Ad</a>)
</th><th>Date</th><th>Action</th></tr>
    <?php
    while( $row = mysql_fetch_array( $result ) ) {
         ?>
         <tr>
         <td>
         <a title="View" href="<?=$selfp?>action=view&id=<?=$row[id]?>"><?=$row['title']?></a></td>
         <td><?=$row['updated_at']?></td>
         <td>
         <?php 
         if ( $context->isInstructor() || $row['user_key'] == $context->getUserKey() ) {
         ?>
             <a title="Edit" href="<?=$selfp?>action=edit&id=<?=$row[id]?>">edit</a>
             <a title="Delete" href="<?=$selfp?>action=delete&id=<?=$row[id]?>">delete</a>
         <?php
         }
         ?>
         </td>
         </tr>
         <?php
    }
    ?>
    </table>
    <?php
}

/*
echo("<pre>\n");
echo("Debug Output:\n");
echo($context->dump());
echo("</pre>\n");
*/

?>
</body>

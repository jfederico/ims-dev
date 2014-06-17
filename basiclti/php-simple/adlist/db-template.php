<?php
error_reporting(E_ALL);

$MY_CONNECTION = mysql_connect("localhost","adsuser","adspassword");
mysql_select_db("ads");
if ( ! $MY_CONNECTION ) {
  echo("Bad connection");
  die();
}
if ( $_REQUEST['dbcheck'] ) {
  echo('Good Database connection');
}
?>

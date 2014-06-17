<?php // Configuration file

unset($CFG);
global $CFG;
$CFG = new stdClass();

// No trailing slash
$CFG->wwwroot = 'http://localhost:8888/lms';

$CFG->dirroot = realpath(dirname(__FILE__));

$CFG->pdo    = 'sqlite:'.$CFG->dirroot.'/db/response.sqlite';
$CFG->pdo    = 'mysql:host=localhost;dbname=lms';
$CFG->pdouser    = 'lmsuser';
$CFG->pdopass    = 'lmspassword';

$CFG->localkey    = 'local';   // oauth_consumer_key for local launches
$CFG->localkeyid   = -1;   // For locally created accounts (don't change)
$CFG->defaultkeyid = -2;   // For dev launches when there are no LTI_Keys defined (don't change)

require_once(dirname(__FILE__) . '/lib/setup.php');
require_once(dirname(__FILE__) . '/lib/portal.php');

// No trailing tag to avoid white space

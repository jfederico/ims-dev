<?php

global $db;

// My data for this application
makeTable($db, 'Responses', Array(
    'id:key',
    'resource_id:integer:unique=true',
    'user_id:integer:unique=true',
    'sourcedid:text:maxlength=2048',
    'data:text:maxlength=2048',
    'note:text:maxlength=2048',
    'grade:text:maxlength=32',
	)
);

?>

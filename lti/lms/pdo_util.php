<?php

// http://www.php.net/manual/en/ref.sqlite.php

// Setup the primary keys for the current logged in user, course, and resource
// User the session to cache these lookups
function setupPrimaryKeys($db, $context)
{

    if ( $_SESSION['_context_consumer_key'] == $context->getConsumerKey() && isset($_SESSION['_context_consumer_id']) &&
         $_SESSION['_context_user_key'] == $context->getUserLKey() && isset($_SESSION['_context_user_id']) &&
		 $_SESSION['_context_course_key'] == $context->getCourseLKey() && isset($_SESSION['_context_course_id']) && 
		 $_SESSION['_context_resource_key'] == $context->getResourceLKey() && isset($_SESSION['_context_resource_id']) ) {
  		$context->setUserID($_SESSION['_context_user_id']);
  		$context->setCourseID($_SESSION['_context_course_id']);
  		$context->setResourceID($_SESSION['_context_resource_id']);
        $context->setConsumerID($_SESSION['_context_consumer_id']);
        // echo("Loaded from session"); flush();
        // echo("<pre>\nFROM SESSION\n");print_r($_SESSION);echo("</pre>\n");flush();
        return;
    }

	// Comes in from above
    $context->setConsumerID($_SESSION['_context_consumer_id']);

    unset($_SESSION['_context_user_key']);
    unset($_SESSION['_context_user_id']);
    unset($_SESSION['_context_course_key']);
    unset($_SESSION['_context_course_id']);
    unset($_SESSION['_context_resource_key']);
    unset($_SESSION['_context_resource_id']);

    if ( strlen($context->getUserLKey()) < 1 ) die('user_id is required');
    if ( strlen($context->getCourseLKey()) < 1 ) die('context_id is required');
    if ( strlen($context->getResourceLKey()) < 1 ) die('resource_link_id is required');

    $sql = sprintf("SELECT * FROM LTI_Users WHERE lkey=%s AND key_id=%s LIMIT 1\n",
		$db->quote($context->getUserLKey()), $db->quote($context->getConsumerID()));
    $q = @$db->query($sql);
    $user = $q->fetch();
    if ( isset($user['id']) ) {
        if ( $user['name'] != $context->getUserName() || $user['image'] != $context->getUserImage() || 
             $user['email'] != $context->getUserEmail()) {
			$sql = sprintf(
				"UPDATE LTI_Users SET name=%s, image=%s, email=%s WHERE id=%s AND key_id=%s",
				$db->quote($context->getUserName()), $db->quote($context->getUserImage()), 
				$db->quote($context->getUserEmail()), $db->quote($user['id']), 
                $db->quote($context->getConsumerID()) );
			$rows = @$db->exec($sql);
		}
    } else {
    	$sql = sprintf(
        	"INSERT INTO LTI_Users (lkey, key_id, name, image) VALUES (%s, %s, %s, %s)",
        	$db->quote($context->getUserLKey()), $db->quote($context->getConsumerID()),
            $db->quote($context->getUserName()),$db->quote($context->getUserImage()));
    	$rows = @$db->exec($sql);
		if ( $rows > 0 ) {
			$sql = sprintf("SELECT * FROM LTI_Users WHERE lkey=%s AND key_id=%s LIMIT 1\n",
				$db->quote($context->getUserLKey()), $db->quote($context->getConsumerID()));
			$q = @$db->query($sql);
			$user = $q->fetch();
		}
    }
    if ( $user['id'] ) {
		$context->setUserID($user['id']);
		$_SESSION['_context_user_key'] = $context->getUserLKey();
		$_SESSION['_context_user_id'] = $user['id'];
	} else {
	    die('Unable to set user key '.$sql);
    }

    $sql = sprintf("SELECT * FROM LTI_Courses WHERE lkey=%s AND key_id=%s LIMIT 1\n",
		$db->quote($context->getCourseLKey()), $db->quote($context->getConsumerID()));
    $q = @$db->query($sql);
    $course = $q->fetch();
    if ( isset($course['id']) ) {
        if ( $course['name'] != $context->getCourseName() ) {
			$sql = sprintf(
				"UPDATE LTI_Courses SET name=%s WHERE id=%s",
				$db->quote($context->getCourseName()), $course['id']);
			$rows = @$db->exec($sql);
		}
    } else {
    	$sql = sprintf(
        	"INSERT INTO LTI_Courses (lkey, key_id, name) VALUES (%s, %s, %s)",
        	$db->quote($context->getCourseLKey()), $db->quote($context->getConsumerID()), 
            $db->quote($context->getCourseName()));
    	$rows = @$db->exec($sql);

		if ( $rows > 0 ) {
            $sql = sprintf("SELECT * FROM LTI_Courses WHERE lkey=%s AND key_id=%s LIMIT 1\n",
		        $db->quote($context->getCourseLKey()), $db->quote($context->getConsumerID()));
			$q = @$db->query($sql);
			$course = $q->fetch();
		}
    }
	if ( $course['id'] ) {
		$context->setCourseID($course['id']);
		$_SESSION['_context_course_key'] = $context->getCourseLKey();
		$_SESSION['_context_course_id'] = $course['id'];
	} else {
	    die('Unable to set course key '.$sql);
	}

    $sql = sprintf("SELECT * FROM LTI_Resources WHERE lkey=%s AND course_id=%s LIMIT 1\n",
		$db->quote($context->getResourceLKey()), $context->getCourseID());
    $q = @$db->query($sql);
    $resource = $q->fetch();
    if ( isset($resource['id']) ) {
        if ( $resource['name'] != $context->getResourceTitle() || $resource['service'] != $context->getOutcomeService() ) {
			$sql = sprintf(
				"UPDATE LTI_Resources SET name=%s, service=%s WHERE id=%s AND course_id=%s",
				$db->quote($context->getResourceTitle()), 
                $db->quote($context->getOutcomeService(), $context->getCourseID()),
                $resource['id']);
			$rows = @$db->exec($sql);
		}
    } else {
    	$sql = sprintf(
        	"INSERT INTO LTI_Resources (lkey, course_id, name, service) VALUES (%s, %s, %s, %s)",
        	$db->quote($context->getResourceLKey()), $context->getCourseID(),
            $db->quote($context->getResourceTitle()), $db->quote($context->getOutcomeService()) );
    	$rows = @$db->exec($sql);

		if ( $rows > 0 ) {
            $sql = sprintf("SELECT * FROM LTI_Resources WHERE lkey=%s AND course_id=%s LIMIT 1\n",
                $db->quote($context->getResourceLKey()), $context->getCourseID());
			$q = @$db->query($sql);
			$resource = $q->fetch();
		}
    }
	if ( $resource['id']  ) {
		$context->setResourceID($resource['id']);
		$_SESSION['_context_resource_key'] = $context->getResourceLKey();
		$_SESSION['_context_resource_id'] = $resource['id'];
	} else {
	    die('Unable to set resource key '.$sql);
	}
    //echo("<pre>\nFROM POST\n");print_r($_SESSION);echo("</pre>\n");
}

<body>
<p>Welcome to the simple L.M.S. project.   The idea is to produce
a clean, simple learning management system to showcase IMS
standards.
</p><p>
This can also be used as a framework to easily build tools 
that support the IMS Learning Tools Interoperability standard.
</p>
<h2>Setup</h2>
<p>
This program can run using either MySql or SQLite.  After your download
and extract the program, copy either:
<pre>
config-mysql.php
config.sqlite.php
</pre>
To be 'config.php'.   For SQLite you only need to alter the 'wwwroot'
line to reflect the actual absolute URL of the tool on your server.
<pre>
$CFG->wwwroot = 'http://localhost/~csev/lms';
</pre>
In addition before you start the application for the first time, on Mac
and Linux you need to open the permissions on the "db" folder using 
the following command:
<pre>
chmod 777 db
</pre>
That of course is a great reason to only use SQLite in developer situations.
</p>
<p>
If you select the MySql configuration file, you need to change the user, password,
and database name.   You can create the database with the following commands:
<pre>
create database lms default character set utf8;
grant all on lms.* to lmsuser@'localhost' identified by 'lmspassword';
grant all on lms.* to lmsuser@'127.0.0.1' identified by 'lmspassword';
</pre>
Changing the values for database name, password, and user both in these statement
and in the configuration file.
</p>
<h2>Getting Started</h2>
<p>
Once you have completed the setup, navigate to the login.php file and you should
see a simple login screen.  If you are using SQLite, you will have a link to the
exellent <a href="http://code.google.com/p/phpliteadmin/" target="_new">phpLiteAdmin</a>
software which is included and allows you to look at the database directly.
The phpLiteAdmin software requires a password.  Look at the code for phpliteadmin.php 
to find or change the password.
</p>
<p>
The administator account is 'admin' and you can look at the file login.php to find or
change the password.  As the administrator, you can create users, courses, and assign
users roles in the courses.  For now all users are autoamtically given the role of 
'Learner' in all courses, so you only need to assign 'Instructor' roles.
</p>
<p>
Once you have created a user, course, and role, you leave the admin role and log in as 
the user you just created and you will be able to launch into a course.  There is only one
tool in the system, a simple student text response system where the teacher can grade
the student responses.  You can play with the tool, enter responses and grade 
the responses.
</p>
<h2>Using this with Learning Tools Interoperability</h2>
<p>
You can also use the response tool via IMS Learning Tools Interoperability.  The URL,
key and secret are as follows:
<pre>
http://localhost/~csev/lms/lti.php
Key: 12345
Secret: secret
</pre>
Of course the first part of the URL must point to your server.  You can use the IMS
test harness at 
<pre>
<a href="http://www.imsglobal.org/developers/LTI/test/v1p1/lms.php" target="_new">http://www.imsglobal.org/developers/LTI/test/v1p1/lms.php</a>
</pre>
Enter the URL, Key and secret, and Recompute launch data and launch the tool.   You 
should be able to play with the Response tool including grading.
</p>
</body>

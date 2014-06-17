Thu Jan 21 18:35:24 EST 2010

This is a minimal reference implementation of the IMS Basic Learning Tools 
Interoperability protocol.

This is distributed as a Java Web Application (war file) which 
can be dropped into a servlet container. The source code is included 
in the WEB-INF/classes folder so that it is self contained and 
self-deploying. There is a README.txt in that folder describing 
developer issues.

This includes three JSP files :

o lms.jsp - Simulates a LMS that does launches
o tool.jsp - Simulates a tool

There is also a Java version of a tool under /provider which can be
tested as well.

You can use these files to test your tool or LMS, or you can use
these as sample code that are starting points.

If you are checking this out from SVN you will need to compile the
classes - go into WEB-INF and see README.txt.

To install this, simply drop the war file into a Tomcat instance and 
navigate to the web application in your browser.

-- Chuck


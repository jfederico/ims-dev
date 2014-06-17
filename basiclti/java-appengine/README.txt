Tue Jan 11 19:32:36 EST 2011

Compiling the sample IMS Global Learning Consortium
Java Google App Engine Sample Application graciously 
provided by Chuck Wight of utah.edu.

This tool is available at:

   http://blti-launch-demo.appspot.com/

First, edit the build.xml to point to your App Engine
Java SDK folder and make sure ant is installed and 
in yor path

Then run:

    ant compile
    ant runserver

It should be up at http://localhost:8080/

To clean up the war folder when you are done, do 

    ant clean

Here is a sample run:

67-194-21-206:java-appengine csev$ ant compile
Buildfile: /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/build.xml

copyjars:
     [copy] Copying 10 files to /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/war/WEB-INF/lib

compile:
     [copy] Copying 8 files to /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/war
     [copy] Copied 12 empty directories to 6 empty directories under /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/war
    [javac] /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/build.xml:43: warning: 'includeantruntime' was not set, defaulting to build.sysclasspath=last; set to false for repeatable builds
    [javac] Compiling 32 source files to /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/war/WEB-INF/classes
    [javac] Note: /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/src/net/oauth/server/HttpRequestMessage.java uses unchecked or unsafe operations.
    [javac] Note: Recompile with -Xlint:unchecked for details.

BUILD SUCCESSFUL
Total time: 3 seconds
67-194-21-206:java-appengine csev$ ant runserver
Buildfile: /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/build.xml

runserver:
     [java] 2011-01-11 19:32:11.172 java[14932:903] [Java CocoaComponent compatibility mode]: Enabled
     [java] 2011-01-11 19:32:11.175 java[14932:903] [Java CocoaComponent compatibility mode]: Setting timeout for SWT to 0.100000
     [java] Jan 12, 2011 12:32:13 AM com.google.apphosting.utils.jetty.JettyLogger info
     [java] INFO: Logging to JettyLogger(null) via com.google.apphosting.utils.jetty.JettyLogger
     [java] Jan 12, 2011 12:32:13 AM com.google.apphosting.utils.config.AppEngineWebXmlReader readAppEngineWebXml
     [java] INFO: Successfully processed /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/war/WEB-INF/appengine-web.xml
     [java] Jan 12, 2011 12:32:13 AM com.google.apphosting.utils.config.AbstractConfigXmlReader readConfigXml
     [java] INFO: Successfully processed /Users/csev/dev/ims-dev/trunk/basiclti/java-appengine/war/WEB-INF/web.xml
     [java] Jan 11, 2011 7:32:14 PM com.google.appengine.tools.development.DevAppServerImpl start
     [java] INFO: The server is running at http://localhost:8080/



Thu Jan 21 18:31:38 EST 2010

Developer Guide

The source code is in the classes folder.   To compile, 

cd classes
javac -cp .:../../../../common/lib/servlet-api.jar:../lib/commons-lang-2.6.jar *.java ; touch ../web.xml 

This compiles both the Java version of the provider and all of the 
support classes.  You need to compile the servlet to get all of the 
OAuth and IMS Support code - even to use the JSP files.

Note that the code in the net folder is taken from the sample
Java code from oauth.net and is unchanged in this distribution.

/Chuck

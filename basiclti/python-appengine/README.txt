Basic LTI on App Engine (Python)

This is sample code that you can use to build IMS
Basic LTI Tool Providers on Google App Engine in 
Python.

There are a number of ways you can use this 
sample code.

(a) The index.py shows a simple launch pattern using cookies
and not using cookies (no cookies are recommended)

(b) The wiscrowd/wiscrowd.py is a very simple stand alone 
application.

(c) The mods folder shows how to implement tools and a set
of modular extensions with automatic registration.

The (a) and (b) are simple, classic Google App Engine 
applications and a good pattern if you want a single 
application.   The (c) pattern is nice if we end up with 
many authors making tools and we don't want to use a whole
App Engine instance for each tool.



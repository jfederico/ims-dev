import os
import time
import datetime
import random
import Cookie
import logging
from google.appengine.api import memcache

# Note - please do not use this for production applications
# see: http://code.google.com/p/appengine-utitlies/

COOKIE_NAME = 'appengine-simple-session-sid'
DEFAULT_COOKIE_PATH = '/'
SESSION_EXPIRE_TIME = 7200 # sessions are valid for 7200 seconds (2 hours)
SESSION_USE_COOKIES = True

class Session(object):

    def __init__(self, handler, thesid = None):
        try:
            self.session = handler.__session__.session
            self.sid = handler.__session__.sid
            self.key = handler.__session__.key
            self.fromcookie = handler.__session__.fromcookie
            self.new = handler.__session__.new
            logging.info('Singleton retrieval - '+handler.__session__.sid);
            return
        except:
            pass

        handler.__session__ = self
        self.handler = handler
        self.sid = None
        self.key = None
        self.session = None
        self.fromcookie = False
        self.new = True

        # Get sid from GET or POST data
        if thesid == None : 
            thesid = handler.request.get(COOKIE_NAME, None) 

	# Get sid from cookie if we are using cookies
        cookiesid = None
        if SESSION_USE_COOKIES :
            string_cookie = os.environ.get('HTTP_COOKIE', '')
            self.cookie = Cookie.SimpleCookie()
            self.cookie.load(string_cookie)
            if self.cookie.get(COOKIE_NAME):
                cookiesid = self.cookie[COOKIE_NAME].value

       if cookiesid is not None and cookiesid == thesid:
           self.fromcookie = True

	# If we have been handed a session id we retrieve it or create it
        if thesid is not None:
            self.sid = thesid
            self.key = 'session-' + self.sid
            try:
	        self.session = memcache.get(self.key)
                logging.info('Session restored - '+self.sid);
            except:
                logging.info('Creating session '+self.key);
                self.session = dict()
                self.new = True
	        memcache.add(self.key, self.session, 3600)

        # If we still don't have a session, lets look in the cookie
        if self.session is None:
            string_cookie = os.environ.get('HTTP_COOKIE', '')
            self.cookie = Cookie.SimpleCookie()
            self.cookie.load(string_cookie)

            # check for existing cookie
            if self.cookie.get(COOKIE_NAME):
                self.sid = self.cookie[COOKIE_NAME].value
                self.key = 'session-' + self.sid
                try:
	            self.session = memcache.get(self.key)
                    logging.info('Session restored - '+self.sid);
                    self.fromcookie = True
                except:
	            self.session = None
                if self.session is None:
                   logging.info('Invalidating session '+self.sid)
                   self.sid = None
                   self.key = None

        # If we still do not have a session, make one up
        if self.session is None:
            self.sid = str(random.random())[5:]+str(random.random())[5:]
            self.key = 'session-' + self.sid
            logging.info('Creating session '+self.key);
            self.session = dict()
	    memcache.add(self.key, self.session, 3600)
            self.new = True

        # If 
            self.cookie[COOKIE_NAME] = self.sid
            self.cookie[COOKIE_NAME]['path'] = DEFAULT_COOKIE_PATH
            # Send the Cookie header to the browser
            print self.cookie


    # Private method to update the cache on modification 
    def _update_cache(self):
        memcache.replace(self.key, self.session, 3600)

    # Convenient delete with no error method
    def delete_item(self, keyname):
        if keyname in self.session:
            del self.session[keyname]
            self._update_cache()

    # Support the dictionary get() method
    def get(self, keyname, default=None):
        if keyname in self.session:
            return self.session[keyname]
        return default

    # session[keyname] = value
    def __setitem__(self, keyname, value):
        self.session[keyname] = value
        self._update_cache()

    # x = session[keyname]
    def __getitem__(self, keyname):
        if keyname in self.session:
            return self.session[keyname]
        raise KeyError(str(keyname))

    # del session[keyname]
    def __delitem__(self, keyname):
        if keyname in self.session:
	    del self.session[keyname]
            self._update_cache()
            return
        raise KeyError(str(keyname))

    # if keyname in session :
    def __contains__(self, keyname):
        try:
            r = self.__getitem__(keyname)
        except KeyError:
            return False
        return True

    # x = len(session)
    def __len__(self):
        return len(self.session)


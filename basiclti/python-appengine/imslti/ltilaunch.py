import os
import logging
import cgi
import wsgiref.handlers
import logging
import hashlib
import base64
import uuid
import urllib
import Cookie
from datetime import datetime, timedelta

from google.appengine.api import memcache

import oauth
from ltistore import LTI_OAuthDataStore

LTI_LAUNCH_PARAMETER_NAME = '_lti_launch_key'
LTI_LAUNCH_PREFIX = 'LTILaunchKey-'
LTI_SESSION_EXPIRE_TIME = 7200 # sessions are valid for 7200 seconds (2 hours)

class LTI_Launch():

  USE_COOKIE = False
  dStr = ''  # For Debugging

  web = None
  launch = None
  launchkey = None
  loaded = False
  fromcookie = False
  new = False
  error = False
  errormsg = None
  complete = False

  # We have several scenarios to handle
  def __init__(self, web):
    self.web = web

    # Check to see if we already have a launch in this request
    try:
        self.dStr = web.__BaseLaunch__.dStr
        self.launch = web.__BaseLaunch__.launch
        self.launchkey = web.__BaseLaunch__.launchkey
        self.loaded = web.__BaseLaunch__.loaded
        self.fromcookie = web.__BaseLaunch__.fromcookie
        self.new = web.__BaseLaunch__.new
        self.error = web.__BaseLaunch__.error
        self.errormsg = web.__BaseLaunch__.errormsg
        self.complete = web.__BaseLaunch__.complete
        # logging.info('Singleton Launch-'+self.launchkey)
        return
    except:
        web.__BaseLaunch__ = self

    # Check if this is the launch from the LMS
    self.handlelaunch(web)
    if self.error or self.complete: return

    # Check to see if we have a suitable cookie already
    cookiekey = self.getCookieKey()
    if self.loaded and LTI_Launch.USE_COOKIE and cookiekey == self.launchkey : 
       logging.info('Cookie: '+self.launchkey)
       self.fromcookie = True
       return

    # If we are not an LMS launch, we attempt a load from memcache
    # Request parameters have precedence over the cookie
    if self.loaded is False:
        thesid = web.request.get(LTI_LAUNCH_PARAMETER_NAME, None)
        etc = ' (request)'
        if LTI_Launch.USE_COOKIE and thesid == None and cookiekey is not None :
            thesid = cookiekey
            etc = ' (cookie)'
        if thesid is None:
            self.error = True
            self.errormsg = 'Could not establish session'
            logging.info(self.errormsg)
            return

        memkey = LTI_LAUNCH_PREFIX + thesid
        try:
            self.launch = memcache.get(memkey)
        except:
            self.launch = None

        # Make sure it is a dictionary
        if not isinstance(self.launch,dict) : self.launch = None

        if self.launch is None:
            self.error = True
            self.errormsg = 'Launch not Found:'+memkey+etc
            logging.info(self.errormsg)
            return

        self.launchkey = thesid
        logging.info("Loaded: "+memkey+etc)
        self.loaded = True

    # Again, check to see if we have a suitable cookie already
    if self.loaded and LTI_Launch.USE_COOKIE and cookiekey == self.launchkey : 
       self.fromcookie = True
       return

    # Now we have a session, and want to set a cookie 
    if self.loaded and LTI_Launch.USE_COOKIE and self.fromcookie is False:
       cookiestr = str(LTI_LAUNCH_PARAMETER_NAME + '='+self.launchkey+'; Path=/')
       web.response.headers.add_header('P3P',
           'CP="IDC DSP COR ADM DEVi TAIi PSA PSD IVAi IVDi CONi HIS OUR IND CNT"')
       web.response.headers.add_header('Set-Cookie', cookiestr)
       logging.info('Set-Cookie: '+self.launchkey)
       # Note - we do *not* set fromcookie here - it must be proven
       # on a later request before we trust it

    # self.handlesesson(web, useKey, createOK)

  def getCookieKey(self):
    string_cookie = os.environ.get('HTTP_COOKIE', '')
    self.cookie = Cookie.SimpleCookie()
    self.cookie.load(string_cookie)
    if self.cookie.get(LTI_LAUNCH_PARAMETER_NAME):
      return self.cookie[LTI_LAUNCH_PARAMETER_NAME].value
    return None

  def handlelaunch(self, web):
    # Check for sanity - silently return
    version = web.request.get('lti_version')
    if ( len(version) < 1 ) : return
    message = web.request.get('lti_message_type')
    if message != 'basic-lti-launch-request' : return

    resource_link_id = web.request.get("resource_link_id")
    oauth_consumer_key = web.request.get("oauth_consumer_key")

    if len(oauth_consumer_key) <= 0 or len(resource_link_id) <= 0 : 
      self.launcherror(web, None, "Missing one of resource_link_id or oauth_consumer_key")
      return

    urlpath = web.request.path

    # Do OAuth Here
    options = None
    self.oauth_server = oauth.OAuthServer(LTI_OAuthDataStore(web, options))
    self.oauth_server.add_signature_method(oauth.OAuthSignatureMethod_PLAINTEXT())
    self.oauth_server.add_signature_method(oauth.OAuthSignatureMethod_HMAC_SHA1())

    params = dict(web.request.params)

    # construct the oauth request from the request parameters
    oauth_request = oauth.OAuthRequest.from_request("POST", web.request.url, headers=web.request.headers, parameters=params)

    # verify the request has been oauth authorized
    try:
        logging.debug(self.requestdebug(web))
        consumer, token, params = self.oauth_server.verify_request(oauth_request)
    except oauth.OAuthError, err:
        logging.info(err)
        self.launcherror(web, None, "OAuth Security Validation failed:"+err.mymessage)
    	return

    user_id = web.request.get("user_id")
    context_id = web.request.get("context_id")

    # If we have a enough launch data to make it distinct, reuse the launch session
    if user_id is not None and context_id is not None:
        launchkey = oauth_consumer_key + ':::' + resource_link_id + ':::' + context_id + ':::' + user_id
        launchkey = hashlib.sha1(launchkey).hexdigest()
    else:
        launchkey = str(uuid.uuid4())
    
    self.launchkey = launchkey
    self.launch = dict(web.request.params)
    self.launch['_launch_type'] = 'basiclti'
    self.launch['_session'] = dict()
    memkey = LTI_LAUNCH_PREFIX + self.launchkey
    try:
        memcache.set(memkey, self.launch, LTI_SESSION_EXPIRE_TIME)
        logging.info("Created: "+ memkey)
        self.new = True
        self.loaded = True
    except:
        self.launcherror(web, None, "Unable to create launch:"+memkey)
        return

  def getKey(self):
      return self.launchkey

  def getKeyName(self):
      return LTI_LAUNCH_PARAMETER_NAME

  def getParameter(self) :
     return LTI_LAUNCH_PARAMETER_NAME + '=' + self.launchkey

  def getUrl(self,url = None):
     if url is None:
         url = self.web.request.url
     if '?' in url : 
         url += '&'
     else:
         url += '?'
     url += self.getParameter()
     return url

  def getInputTag(self):
    return '<input type="hidden" name="'+LTI_LAUNCH_PARAMETER_NAME+'" value="'+self.launchkey+'">'

  def cookieRedirect(self):
     if self.new is False : return
     if self.error is True : return
     if self.loaded is False : return
     if self.fromcookie is True : return
     redirect_url = self.getUrl()
     logging.info('Redirect: '+redirect_url)
     self.complete = True
     self.web.redirect(redirect_url)

  # It sure would be nice to have an error url to redirect to 
  def launcherror(self, web, dig, desc) :
      self.complete = True
      self.error = True
      self.errormsg = desc
      logging.info('LTI Launch error - '+desc)
      logging.info(self.getDetail(web))

      return_url = web.request.get("launch_presentation_return_url")
      if len(return_url) > 1 :
          desc = urllib.quote(desc) 
          if return_url.find('?') > 1 : 
              return_url = return_url + '&lti_errormsg=' + desc
          else :
              return_url = return_url + '?lti_errormsg=' + desc
          web.redirect(return_url)
          return

      web.response.out.write("<p>\nIncorrect authentication data presented by the Learning Management System.\n</p>\n")
      web.response.out.write("<p>\nError code:\n</p>\n")
      web.response.out.write("<p>\n"+desc+"\n</p>\n")
      web.response.out.write("<!--\n")
  
      web.response.out.write("<pre>\nHTML Formatted Output(Test):\n\n")
      desc = cgi.escape(desc) 
      web.response.out.write(desc)
      web.response.out.write("\n\nDebug Log:\n")
      web.response.out.write(self.dStr)
      web.response.out.write("\nRequest Data:\n")
      web.response.out.write(self.requestdebug(web))
      web.response.out.write("\n</pre>\n")
      web.response.out.write("\n-->\n")

      if dig:
        dig.debug = self.dStr
        dig.put()

  def getDetail(self, web) :
      detail = 'key=' + web.request.get('oauth_consumer_key')
      if web.request.get('context_label',None) is not None:
          detail += ',cl=' + web.request.get('context_label')
      elif web.request.get('context_id',None) :
          detail += ',cid=' + web.request.get('context_label')
      if web.request.get('resource_link_title',None) is not None:
          detail += ',rlt=' + web.request.get('resource_link_title')
      elif web.request.get('resource_link_id',None) :
          detail += ',rlid=' + web.request.get('resource_link_id')
      if web.request.get('lis_person_contact_email_primary',None) is not None:
          detail += ',em=' + web.request.get('lis_person_contact_email_primary')
      elif web.request.get('lis_person_name_full',None) :
          detail += ',ful=' + web.request.get('lis_person_name_full')
      elif web.request.get('lis_person_name_family',None) :
          detail += ',fam=' + web.request.get('lis_person_name_family')
      elif web.request.get('lis_person_name_given',None) :
          detail += ',giv=' + web.request.get('lis_person_name_given')
      elif web.request.get('context_id',None) :
          detail += ',cid=' + web.request.get('context_id')
      return detail

  def getContextType() :
      return 'basiclti'

  # Debugging methods
  def debug(self, str):
    logging.info(str)
    self.dStr = self.dStr + str + "\n"

  def getDebug(self):
    return dStr

  def dump(self):
    ret = "<pre>\n"
    ret = ret + "Dump of LTI Object\n"
    if ( not self.launch ):
      ret = ret + "No launch data\n"
      ret = ret + "</pre>\n";
      return ret
    ret = ret + "Complete = "+str(self.complete) + "\n"
    ret = ret + "isInstructor = "+str(self.isInstructor()) + "\n";
    ret = ret + "getUserKey = "+str(self.getUserKey()) + "\n"
    ret = ret + "getUserName = "+str(self.getUserName()) + "\n"
    ret = ret + "getUserEmail = "+str(self.getUserEmail()) + "\n"
    ret = ret + "getUserImage = "+str(self.getUserImage()) + "\n"
    ret = ret + "getResourceKey = "+str(self.getResourceKey()) + "\n"
    ret = ret + "getResourceTitle = "+str(self.getResourceTitle()) + "\n"
    ret = ret + "getConsumerKey = "+str(self.getConsumerKey()) + "\n"
    ret = ret + "getCourseKey = "+str(self.getCourseKey()) + "\n"
    ret = ret + "getCourseName = "+str(self.getCourseName()) + "\n"
    ret = ret + "</pre>\n";
    return ret

  def requestdebug(self, web):
    # Drop in the request for debugging
    reqstr = web.request.path + "\n"
    for key in web.request.params.keys():
      value = web.request.get(key)
      if len(value) < 100: 
         reqstr = reqstr + key+':'+value+'\n'
      else: 
         reqstr = reqstr + key+':'+str(len(value))+' (bytes long)\n'
    return reqstr

  # Convienence methods
  def isInstructor(self) :
    roles = self.launch.get('roles')
    if roles == None or len(roles) < 1 : return False
    roles = roles.lower()
    if roles.find("instructor") >= 0 : return True 
    if roles.find("administrator") >=0  : return True 
    return False

  def isAdmin(self):
      if users.is_current_user_admin(): return True
      return False

  def getUserKey(self):
      key = self.launch.get('oauth_consumer_key')
      id = self.launch.get('user_id')
      if ( id and key and len(id) > 0 and len(key) > 0 ) : return key + ':' + id
      return None

  def getUserEmail(self):
      email = self.launch.get('lis_person_contact_email_primary')
      if ( email and len(email) > 0 ) : return email
      # Sakai Hack
      email = self.launch.get('lis_person_contact_emailprimary')
      if ( email and len(email) > 0 ) : return email
      return None

  def getUserShortName(self):
      email = self.getUserEmail()
      givenname = self.launch.get('lis_person_name_given')
      familyname = self.launch.get('lis_person_name_family')
      fullname = self.launch.get('lis_person_name_full')
      if ( email and len(email) > 0 ) : return email
      if ( givenname and len(givenname) > 0 ) : return givenname
      if ( familyname and len(familyname) > 0 ) : return familyname
      return self.getUserName()

  def getUserName(self):
      givenname = self.launch.get('lis_person_name_given')
      familyname = self.launch.get('lis_person_name_family')
      fullname = self.launch.get('lis_person_name_full')
      if ( fullname and len(fullname) > 0 ) : return fullname
      if ( familyname and len(familyname) > 0 and givenname and len(givenname) > 0 ) : return givenname + familyname
      if ( givenname and len(givenname) > 0 ) : return givenname
      if ( familyname and len(familyname) > 0 ) : return familyname
      email = self.getUserEmail()
      if ( email and len(email) > 0 ) : return email
      return None

  def getUserImage(self):
      image = self.launch.get('user_image')
      if ( image and len(image) > 0 ) : return image
      email = self.launch.get('lis_person_contact_email_primary')
      if ( email and len(email) > 0 ) : 
          grav_url = "https://www.gravatar.com/avatar.php?gravatar_id="+hashlib.md5(email.lower()).hexdigest() + "&size=40"
          return grav_url
      return None

  def getResourceKey(self):
      key = self.launch.get('oauth_consumer_key')
      id = self.launch.get('resource_link_id')
      if ( key and id and len(key) > 0 and len(id) > 0 ) : return key + ':' + id;
      return None

  def getResourceTitle(self):
      title = self.launch.get('resource_link_title')
      if ( title and len(title) > 0 ) : return title
      return None

  def getConsumerKey(self):
      key = self.launch.get('oauth_consumer_key')
      if ( key and len(key) > 0 ) : return key
      return None

  def getCourseKey(self):
      key = self.launch.get('oauth_consumer_key')
      id = self.launch.get('context_id')
      if ( id and key and len(id) > 0 and len(key) > 0 ) : return key + ':' + id
      return None

  def getCourseName(self):
      label = self.launch.get('context_label')
      title = self.launch.get('context_title')
      id = self.launch.get('context_id')
      if ( label and len(label) > 0 ) : return label
      if ( title and len(title) > 0 ) : return title
      if ( id and len(id) > 0 ) : return id
      return None

  def getContextType(self):
      return self.launch.get('_launch_type')

  ## Session oriented methods
  # Private method to update the cache on modification 
  def _update_cache(self):
      memkey = LTI_LAUNCH_PREFIX + self.launchkey
      try:
          memcache.replace(memkey, self.launch, LTI_SESSION_EXPIRE_TIME)
      except:
          pass

  # Convenient delete with no error method
  def delete_item(self, keyname):
      self.session = self.launch['_session']
      if keyname in self.session:
          del self.session[keyname]
          self._update_cache()

   # Support the dictionary get() method
  def get(self, keyname, default=None):
      self.session = self.launch['_session']
      if keyname in self.session:
          return self.session[keyname]
      return default

  # session[keyname] = value
  def __setitem__(self, keyname, value):
      self.session = self.launch['_session']
      self.session[keyname] = value
      self._update_cache()

  # x = session[keyname]
  def __getitem__(self, keyname):
      self.session = self.launch['_session']
      if keyname in self.session:
          return self.session[keyname]
      raise KeyError(str(keyname))

  # del session[keyname]
  def __delitem__(self, keyname):
      self.session = self.launch['_session']
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
      self.session = self.launch['_session']
      return len(self.session)


import os
import logging
import wsgiref.handlers
from google.appengine.api import memcache
from google.appengine.ext import webapp
from google.appengine.ext import db
from google.appengine.ext.webapp import template

# Where is the base URL of this appliction
APP_PATH = '/request'

# A Model for an OAuth Comsumer Key
class OAuthConsumerKey(db.Model):
  consumer = db.StringProperty()
  secret = db.StringProperty()
  name = db.StringProperty()
  email = db.StringProperty()

def doRender(handler, tname = "index.htm", values = { }):
  if tname.find("_") == 0: return
  temp = os.path.join(os.path.dirname(__file__),
         'templates/' + tname)
  if not os.path.isfile(temp):
    handler.response.out.write('Couple not find template '+tname)
    return 

  outstr = template.render(temp, values)
  handler.response.out.write(outstr)

class TempKeyHandler(webapp.RequestHandler):

  def get(self, msg=None):
    doRender(self, 'request.htm')

  def post(self):
    action = self.request.get('action')
    consumer = self.request.get('consumer')
    secret = self.request.get('secret')
    memkey = 'OAuthConsumerKey-' + consumer

    # Check if the user already exists
    que = db.Query(OAuthConsumerKey).filter('consumer =',consumer)
    results = que.fetch(limit=1)

    if len(results) > 0 :
      doRender(self, 'request.htm',
          {'error' : 'You cannot have a temporary key that is already in the Key Store'} )
      return

    if action == 'Set' : 
        if secret == '' :
            doRender(self, 'request.htm',
              {'error' : 'Please include a secret'} )
            return
        memcache.set(memkey, secret)

    if action == 'Delete' : 
        memcache.delete(memkey)

    secret = memcache.get(memkey)
    msg = None
    if secret is not None:
        msg = 'The secret for %s is <b>%s</b>' % (consumer, secret)

    doRender(self, 'request.htm', {'msg' : msg } ) 

def main():
  application = webapp.WSGIApplication([
     (APP_PATH+'.*', TempKeyHandler)],
     debug=True)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()


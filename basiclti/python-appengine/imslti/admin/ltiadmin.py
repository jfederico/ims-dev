import os
import logging
import wsgiref.handlers
from google.appengine.api import memcache
from google.appengine.ext import webapp
from google.appengine.ext import db
from google.appengine.ext.webapp import template

# Where is the base URL of this appliction
APP_PATH = '/ltiadmin'

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

class AdminHandler(webapp.RequestHandler):

  def get(self, msg=None):
    msg = None
    consumer = self.request.get('deletekey')
    if consumer != '' :
        que = db.Query(OAuthConsumerKey).filter('consumer =',consumer)
        results = que.fetch(limit=1)

        if len(results) > 0 :
          memcache.delete('OAuthConsumerKey-' + consumer)
          results[0].delete()
          self.redirect(self.request.path)
       
    # Retrieve the records for display
    que = db.Query(OAuthConsumerKey)
    consumer_list = que.fetch(limit=1000)
    doRender(self, 'list.htm',
        {'handler': self,
         'msg': msg,
         'consumer_list': consumer_list})

class NewHandler(webapp.RequestHandler):

  def get(self, msg=None):
    doRender(self, 'new.htm')

  def post(self):
    action = self.request.get('action')
    if action == 'Cancel' : 
        self.redirect(APP_PATH)
        return

    consumer = self.request.get('consumer')
    secret = self.request.get('secret')
    name = self.request.get('name')
    email = self.request.get('email')
    logging.info('Adding consumer='+consumer)

    if consumer == '' or secret == '' or name == '' or email == '':
      doRender(
          self,
          'new.htm',
           {'error' : 'Please fill in all fields'} )
      return

    memcache.delete('OAuthConsumerKey-' + consumer)

    # Check if the user already exists
    que = db.Query(OAuthConsumerKey).filter('consumer =',consumer)
    results = que.fetch(limit=1)

    if len(results) > 0 :
      doRender(
          self,
          'new.htm',
          {'error' : 'Account Already Exists'} )
      return

    # Create the User object and log the user in
    newrec = OAuthConsumerKey(consumer=consumer, secret=secret, name=name, email=email);
    newrec.put();
    self.redirect(APP_PATH)

def main():
  application = webapp.WSGIApplication([
     (APP_PATH+'/new', NewHandler),
     (APP_PATH+'.*', AdminHandler)],
     debug=True)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()


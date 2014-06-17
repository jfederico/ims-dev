import os
import logging
import pickle
import wsgiref.handlers
from google.appengine.api import memcache
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from imslti.ltilaunch import LTI_Launch
from mod import util

BASEPATH = None

# Return our Registration
def register(mypath):
   global BASEPATH
   BASEPATH = mypath
   return util.ToolRegistration(WisHandler, mypath, 'Wisdom of Crowds',
     '''This tool lets classes guess numbers as in the Wisdom of Crowds.''')

def doRender(tname = 'index.htm', values = { }):
  if tname.find('_') == 0: return
  temp = os.path.join(os.path.dirname(__file__),
         'templates/' + tname)
  if not os.path.isfile(temp):
    return False

  outstr = template.render(temp, values)
  return outstr

class WisHandler(webapp.RequestHandler):

  def post(self):
    launch = LTI_Launch(self)
    if launch.complete: return
    if launch.loaded is False:
        self.response.out.write('<p>This tool must be launched using IMS Basic LTI.</p>')
        return
    
    wiskey = 'WisCrowd-'+str(launch.getCourseKey())
    data = self.getmodel(wiskey)
    logging.info('WisHandler Loading Wis Key='+wiskey)

    name = self.request.get('name')
    if len(name) < 1 : name = launch.getUserName()
    if name is None or len(name) < 1:
        self.display(launch, data, 'You must enter a user name')
        return

    guess = self.request.get('guess')
    try: guess = int(guess)
    except: guess = -1

    msg = ''
    if guess < 1 :
       msg = 'Please enter a valid, numeric guess'
    elif  len(name) < 1 : 
       msg = 'No Name Found'
    elif name in data : 
       msg = 'You already have answered this'
    elif len(data) > 1000 : 
       msg = 'Game only supports 1000 players.'
    else:
       data[name] = guess
       memcache.set(wiskey, data, 3600)
       logging.info('Storing Wis Key='+wiskey)
       # Retrieve and check to see if it was stored
       data = self.getmodel(wiskey)
       if data.get(name,None) == guess :
         msg = 'Thank you for your guess'
       else:
         msg = 'Unable to store your guess please re-submit'
         logging.warning('Failed to Store Wis Key='+wiskey)

    self.display(launch, data, msg)

  def get(self):
    launch = LTI_Launch(self)
    if launch.complete: return
    if launch.loaded is False:
        self.response.out.write('<p>This tool must be launched using IMS Basic LTI.</p>')
        return
    
    msg = None
    wiskey = 'WisCrowd-'+str(launch.getCourseKey())
    logging.info('WisHandler.render Loading Wis Key='+wiskey)

    self.action = self.request.get('action')
    if launch.isInstructor() and self.action == 'reset':
       data = dict()
       logging.info('Resetting Wis Key='+wiskey)
       memcache.set(wiskey, data, 3600)
       msg = 'Data reset'
    else:
       data = self.getmodel(wiskey)

    self.display(launch, data, msg)

  def display(self, launch, data, msg) :
    reseturl = launch.getUrl(self.request.path + '?action=reset')
    rendervars = {'launch': launch,
                  'reset' : reseturl,
                  'msg' : msg}
    # Show the instructor the current data and average
    if launch.isInstructor() and len(data) > 0 :
       text = ''
       total = 0
       for (key, val) in data.items(): 
         text = text + key + ',' + str(val) + '\n'
         total = total + val
       count = len(data)
       ave = 0
       if count > 0 : ave = total / count
       rendervars['ave'] = ave
       rendervars['count'] = count
       rendervars['data'] = text

    outstr = doRender('wiscrowd.htm', rendervars)
    self.response.out.write(outstr)

  def getmodel(self, wiskey):
    data =  memcache.get(wiskey)
    # If we changed the program ignore old things in the cache
    if data == None or not isinstance(data, dict):
      data = dict()
    return data

def main():
  application = webapp.WSGIApplication([
     ('/.*', WiscrowdHandler)],
     debug=True)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()


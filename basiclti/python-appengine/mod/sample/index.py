import logging
from google.appengine.ext.webapp import template
from google.appengine.ext import webapp
from imslti.ltilaunch import LTI_Launch

from mod import util

BASEPATH = None

# Return our Registration
def register(mypath):
   global BASEPATH
   BASEPATH = mypath
   return util.ToolRegistration(TestHandler, mypath, 'Test Tool', 
     '''This tool shows how to build a simple learning tool to test Basic LTI.''')

# Note that this tool does not use cookies - so it decorates URLs
class TestHandler(webapp.RequestHandler):

  def signon(self) : 
      launch = LTI_Launch(self)
      if launch.complete: return None
      if not launch.loaded : 
          self.response.out.write('<p>This tool must be launched using IMS Basic LTI.</p>')
          return None
      return launch

  # We serve static files without requiring authorization
  def get(self):
      global BASEPATH
      if util.handleStatic(self,BASEPATH) : return
      launch = self.signon()
      if launch is None : return

      imgurl = BASEPATH+'/static/ims-global-logo.jpg'
      self.response.out.write('<p><img src="'+imgurl+'" align="right"')

      msg = launch.get('msg',None)
      if msg is not None:
          launch.delete_item('msg')
          self.response.out.write(msg+'</p><p>\n')

      self.response.out.write('Hello:'+launch.getUserName())
      self.response.out.write(' <a href="'+launch.getUrl()+'" target="_new">Click Me</a></p>')
      val = launch.get('val',0)
      self.response.out.write('<p>Val '+str(val)+'</p>')
      launch['val'] = val + 1
      self.response.out.write('\n'+launch.dump()+'\n')

  # We use laucn to pass a message across the redirect
  # to a decorated version of the same url
  def post(self):
      launch = self.signon()
      if launch is None : return
      launch['msg'] = 'Welcome from '+launch.getCourseName()
      self.redirect(launch.getUrl())


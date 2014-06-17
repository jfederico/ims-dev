import os
import wsgiref.handlers
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from imslti.ltilaunch import LTI_Launch

# A best practice normal launch assumes no cookies 
class LaunchHandler(webapp.RequestHandler):

  def get(self):
      self.post()

  def post(self):
      launch = LTI_Launch(self)
      if launch.complete: return

      if launch.loaded:
          self.response.out.write('<p>Done:'+launch.getUserName())
          self.response.out.write(' <a href="'+launch.getUrl()+'" target="_new">Open</a></p>')
          val = launch.get('val',0)
          self.response.out.write('<p>Val '+str(val)+'</p>')
          launch['val'] = val + 1
      else:
          self.response.out.write('<p>This tool must be launched using IMS Basic LTI.</p>')
      self.response.out.write('\n'+launch.dump()+'\n');

# This launch shows a cookie pattern, a less preferred approach
class CookieHandler(webapp.RequestHandler):

    def get(self):
        self.post()

    def post(self):
        LTI_Launch.USE_COOKIE = True
        launch = LTI_Launch(self)
        if launch.complete: return
        if launch.new and launch.fromcookie is False:
            launch.cookieRedirect()
            if launch.complete : return

        if launch.loaded:
            self.response.out.write('<p>Done:'+launch.getUserName()+'</p>')
            if launch.fromcookie:
                self.response.out.write('<p>(Cookie is Working) ')
                self.response.out.write('<a href="'+self.request.url+'" target="_new">Open</a></p>')
            else:
                self.response.out.write('<p>(Cookie are off) ')
                self.response.out.write('<a href="'+launch.getUrl()+'" target="_new">Open</a></p>')
            val = launch.get('val',0)
            self.response.out.write('<p>Val '+str(val)+'</p>')
            launch['val'] = val + 1
        else:
            self.response.out.write('<p>This tool must be launched using IMS Basic LTI.</p>')
        self.response.out.write('\n'+launch.dump()+'\n');

class IndexHandler(webapp.RequestHandler):

    def get(self):
        temp = os.path.join(
            os.path.dirname(__file__),
            'templates/index.htm')
        outstr = template.render(temp, {})
        self.response.out.write(outstr)

def main():
    routes = [ ('/cookie', CookieHandler),
               ('/launch', LaunchHandler) ]
    routes.append( ('/.*', IndexHandler) )
 
    application = webapp.WSGIApplication(routes, debug=True)
    wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
    main()

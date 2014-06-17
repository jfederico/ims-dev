import os
import wsgiref.handlers
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from imslti.ltilaunch import LTI_Launch
from mod import module_setup

# Load any Modules
MODULES = module_setup.getModules()

class MainHandler(webapp.RequestHandler):

    def get(self):
        global MODULES
        self.response.out.write('<p>No registered module for path: '+self.request.path+'</p>\n')
        self.response.out.write('<p>Registered Modules:</p><ul>\n')
        for mod in MODULES:
            self.response.out.write('<li>Name: '+mod.title+' at '+mod.path+'</p>\n')
        self.response.out.write('</ul>\n')

def main():
    global MODULES
    routes = list()
    for module in MODULES:
        routes.append( (module.path, module.handler) ) 
        routes.append( (module.path+'/.*', module.handler) ) 

    routes.append( ('/.*', MainHandler) )
 
    application = webapp.WSGIApplication(routes, debug=True)
    wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
    main()

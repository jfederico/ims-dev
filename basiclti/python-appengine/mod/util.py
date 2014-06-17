import os

# TODO: Set up cache values

def getLocalPath(web,basepath):
   path = web.request.path
   if basepath is None : return '/'
   if len(path) <= len(basepath) : return '/'
   return path[len(basepath):]

suffixes = { '.png' :  ('image/png', True) , 
             '.jpg' :  ('image/jpeg', True), 
             '.pdf' :  ('application/pdf', True), 
             '.mov' :  ('application/Quicktime', True), 
             '.js': ('text/javascript', False),
             '.html': ('text/html', False),
             '.css': ('text/css', False),
             '.txt': ('text/plain', False) }

def handleStatic(web,basepath):
    path = getLocalPath(web,basepath)
    if not path.startswith('/static/') : return False
    pwd = os.path.dirname(__file__)
    if pwd.endswith('/mod') : pwd = pwd[:-4]
    requestpath = web.request.path
    if ( '..' in requestpath ) : return False
    if not requestpath.startswith('/mod') : return False
    temp = os.path.join(pwd, requestpath[1:])
    if not os.path.isfile(temp): return False
    detail = None
    for (suf, mime) in suffixes.items():
        if path.endswith(suf) :
            detail = mime
            break

    if detail is None : return False

    web.response.headers['Content-Type'] = detail[0]
    if detail[1] : 
        outstr = open(temp, 'rb').read()
        web.response.headers['Content-Transfer-Encoding'] = 'binary'
    else : 
        outstr = open(temp).read()

    web.response.headers['Content-Length'] = str(len(outstr))
    web.response.out.write(outstr)

    return True

class ToolRegistration():

  def __init__(self,handler,path,title,desc):
    self.handler = handler
    self.path = path
    self.title = title
    self.desc = desc
    

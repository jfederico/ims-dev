import os 

def getModules():
    modules = list()
    dirs = os.listdir(os.path.dirname(__file__))

    for dir in dirs:
        if len(dir) < 1 : continue
        if dir[0] == '.' : continue
        if dir[0] == '_' : continue
        if dir.endswith('.py') : continue;
        if dir.endswith('.pyc') : continue;
        if dir.endswith('.yaml') : continue;
        temp = os.path.join(os.path.dirname(__file__), dir, 'index.py')
        if not os.path.isfile(temp): continue
        X = __import__(dir+'.index', globals(), locals(), [''])
        Y = X.register('/mod/'+dir)
        modules.append( Y )

    return modules

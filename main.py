import os

def findReplace(dir,find, replace):
    for dname, dirs, files in os.walk(dir):
        files = [ file for file in files if file.endswith( ('.java','.gradle') ) ]
        for fname in files:
            fpath = os.path.join(dname, fname)
            try:
                with open(fpath,encoding="cp437") as f:
                    s = f.read()
            except PermissionError:
                pass
            s = s.replace(find, replace)
            try:
                with open(fpath, "w",encoding="cp437") as f:
                    f.write(s)
            except PermissionError:
                pass

def renameFiles(dir, find, replace):
    for dname, dirs, files in os.walk(dir):
        for fname in files:
            newName = str(fname).replace(find, replace)
            fpath = os.path.join(dname, fname)
            newPath = os.path.join(dname, newName)
            os.rename(fpath, newPath)
def renameDir(dir, find, replace):
    for f in os.listdir(dir):
        old = f
        f = f.replace(find, replace)
        if old != f:
            os.rename(old, f)
        if os.path.isdir(f):
            os.chdir(f)
            renameDir(".", find, replace)
            os.chdir("..")

path = os.path.dirname(__file__)
arrFind = ["KamiTools", "kamiTools", "Kami Screen Markers", "kamiscreenmarkers", "kamimarkers"]
arrReplace = ["KamiXXX", "kamiXXX", "Kami XXX", "kamiXXXgroup", "kamiXXXkey"]
for i in range(len(arrFind)):
    findReplace(path, arrFind[i], arrReplace[i])
    renameFiles(path, arrFind[i], arrReplace[i])
    renameDir(path, arrFind[i], arrReplace[i])

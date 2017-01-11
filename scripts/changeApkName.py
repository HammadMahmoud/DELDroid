import os
import subprocess

dir="/Volumes/Android/adaptation_dataset/dating/"


for filename in os.listdir(dir):
    if filename.endswith(".apk"):
    	print filename
    	newfilename=filename.replace(' ','')
        os.rename(dir+filename, dir+newfilename)
        
        p = subprocess.Popen(['extract_package_name_from_apk.sh', newfilename], stdout=subprocess.PIPE,
                                stderr=subprocess.PIPE)
        pkg, err = p.communicate()
        pkg=pkg.strip()
        print pkg
        if len(pkg)>4:
        	os.rename(dir+newfilename, dir+pkg.strip()+'.apk')
        	print dir+pkg.strip()+'.apk'



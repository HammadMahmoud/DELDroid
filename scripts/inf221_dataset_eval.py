import os, subprocess, sys

datasetDir='/Volumes/Android/lpdroid_models/inf221_dataset'
apktool = '/usr/local/bin/apktool' 

def renameDirs():
	for d in next(os.walk(datasetDir))[1]:
		cmd = 'mv '+datasetDir+'/'+d+' '+datasetDir+'/'+(d.split('_')[0]).replace('B','S')
# 		os.system(cmd)		
		
def createApksDir():
	for d in next(os.walk(datasetDir))[1]:
# 		apks = os.path.join(datasetDir,d,'apk')
		if os.path.exists(os.path.join(datasetDir,d,'apks')):
			c=1 
		else:
			os.mkdir(os.path.join(datasetDir,d,'apks'))
			
def copyAppsToApksDir():
	for d in next(os.walk(datasetDir))[1]:
		apks= datasetDir+'/'+d+'/apks/'
# 		print os.listdir(apks)
		if len(os.listdir(apks)) == 0:
			cmd = 'mv '+datasetDir+'/'+d+'/*.apk '+apks
			os.system(cmd)

def renameApks():
	f = open('/Volumes/Android/lpdroid_models/inf221_dataset/INFO/packages.txt','w')
	for d in next(os.walk(datasetDir))[1]:		
		apks= datasetDir+'/'+d+'/apks/'
		if os.path.exists(apks):
			apps = os.listdir(apks)
			for app in apps:
				if app.endswith('.apk'):
					appPath = apks+app
					packageName = subprocess.check_output(['/Users/Mahmoud/bin/extract_package_name_from_apk.sh '+ appPath], shell=True)
					packageName=packageName.replace('\n','')+'.apk'
					f.write(appPath+','+packageName+'\n')
					os.rename(appPath, apks+packageName)
	f.close()			

def duplicates():
	appsCnt=0
	uniqueApps=0
	packageStuMap = {}
	for d in next(os.walk(datasetDir))[1]:		
		apks= datasetDir+'/'+d+'/apks/'		
		if os.path.exists(apks):
			apps = os.listdir(apks)
			appsCnt=appsCnt+len(apps)
			for app in apps:
				key = app
				if app.endswith('.apk'):
					if key not in packageStuMap:			
						packageStuMap[key] = list()
					packageStuMap[key].append(d)			
	uniqueApps=appsCnt
	for k, v in packageStuMap.iteritems():
		if len(v)>1:
			uniqueApps-=1
			print k + ','+str(v).replace('[','').replace(']','').replace('\'','')
	print 'All apps = '+str(appsCnt)
	print 'Unique Apps = '+str(uniqueApps)
	print 'Duplicate Apps = '+str(appsCnt-uniqueApps)		

def changePackageName():
# 	for stu in next(os.walk(datasetDir))[1]:		
		stu='S55'		
		print os.path.join(datasetDir,stu,'apks')
		apks= os.path.join(datasetDir,stu,'apks')			
		if os.path.exists(apks):
			print 'apks:'+apks
			apps = os.listdir(apks)
			for app in apps:
				s = stu.split('_')[0].lower()
				if app.endswith('.apk'):
					try:						
						print '----------------------------------------------------------------------'
						if app.startswith(s):
							print 'The package is already changed for '+os.path.join(apks,app)
							continue									
						oldPackage = app.replace('.apk','')
						d = apktool+' d '+os.path.join(apks,app)+' -o '+os.path.join(apks,oldPackage)
						print d
		 			 	os.system(d)
		 			 	appPath = os.path.join(apks,oldPackage)
		 			 	if not os.path.exists(os.path.join(appPath,'smali')):
		 			 		print 'smali folder not found in:'+appPath
		 			 		os.system('rm -rf '+appPath)
		 			 		continue															
						newPackage = s+'.'+oldPackage
						Lold = oldPackage.replace('.','/')
						Lnew = newPackage.replace('.','/')
						print 'change '+Lold+' to '+Lnew
						res='xmlns:android="http://schemas.android.com/apk/res/android"'
		 				for root, dirs, files  in os.walk(appPath):
		 					for f in files: 
		 						if f.endswith('.smali') or f.endswith('.xml') or f.endswith('apktool.yml'):
		 							f = root+'/'+f
		 							print f
		 							o = open(f,'r')
		 							n = open(f+'_new','w')
		 							for l in o:		 								
		 								if not f.endswith('AndroidManifest.xml') and f.endswith('.xml') and res in l:
		 									n.write(l.replace(res,'xmlns:android="http://schemas.android.com/apk/lib/android"'))
		 									print '** res line replaced in '+f		 									
		 								elif f.endswith('AndroidManifest.xml') or f.endswith('apktool.yml'):		 									
		 									if 'android:fullBackupContent="@xml/backup_scheme"' in l:
		 										print '** backup line removed'
		 										n.write(l.replace('android:fullBackupContent="@xml/backup_scheme"',''))
		 									else:
		 										n.write(l.replace(oldPackage,newPackage))	
		 								else:	
		 									n.write(l.replace(Lold,Lnew))
		 							n.close()
		 							o.close()
		 							os.remove(f)
		 							os.rename(f+'_new' , f)	
						print 'mkdir '+os.path.join(appPath,'smali',s)
		 				os.mkdir(os.path.join(appPath,'smali',s))				
						cmd = 'mv '+os.path.join(appPath,'smali',oldPackage.split('.')[0])+' '+os.path.join(appPath,'smali',s)
						print cmd
		 				os.system(cmd)		
						newApk = apktool+' b '+appPath+' -o '+os.path.join(apks,newPackage)+'.apk'
						print newApk
		 				os.system(newApk)
						signCmd = '/Users/Mahmoud/bin/sign_apk_with_mahmoud.sh '+os.path.join(apks,newPackage)+'.apk'
						
						if os.path.exists(os.path.join(apks,newPackage)+'.apk'):
							print 'signCmd:'+signCmd
		 					os.system(signCmd)
		 					mvOldAppCmd = 'mv '+os.path.join(apks,app)+' '+os.path.join(datasetDir,stu)
							print mvOldAppCmd
		 					os.system(mvOldAppCmd)
		 				removePackageDirCmd = 'rm -rf '+appPath
		 				os.system(removePackageDirCmd)
		 				print '----------------------------------------------------------------------'
					except RuntimeError as err:
						print 'RuntimeError: '+str(err)
						pass
					except OSError as err:
						print 'OSError: '+str(err)
						pass
			
	
# renameDirs()
# createApksDir()
# copyAppsToApksDir()
# renameApks()
# duplicates()
changePackageName()
# duplicates()

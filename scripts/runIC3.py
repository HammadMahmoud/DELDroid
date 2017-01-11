import os, subprocess, datetime, sys
from time import localtime, strftime
from timeout import timeout
from timeout import TimeoutError


#load the paths from ./paths.txt file
paths = open('/Volumes/Android/lpdroid_models/scripts/paths.txt', 'r')
dict={}
for l in paths:
	l=l.strip()
	keyVal = l.split('=')
	if len(keyVal)==2:
		dict[keyVal[0]] = keyVal[1]

apk_repo=dict['APK_REPO']
modelRepo=dict['MODEL_REPO'] + 'ic3/'
dataStorageDirs=dict['DATA_STORAGE'] + 'ic3/'
dareOutputDir=dict['MODEL_REPO'] + 'dare/'
ic3Dir=dict['IC3_DIR']

#ic3Dir='/Volumes/Android/lpdroid_models/scripts/ic3-0.2.0/'


#modelRepo='/Volumes/Android/lpdroid_models/models/iccta/ic3/'
#dataStorageDirs='/Volumes/Android/lpdroid_models/logs/iccta/ic3/'
#apk_repo='/Volumes/Android/adaptation_dataset/iccre_iccta/installed/'
#dareOutputDir='/Volumes/Android/lpdroid_models/models/iccta/dare/'

# modelRepo='/Volumes/Android/lpdroid_models/models/malg_anserverbot/ic3/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/malg_anserverbot/ic3/'
# dareOutputDir='/Volumes/Android/lpdroid_models/models/malg_anserverbot/dare/'
# apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/AnserverBot/'

# modelRepo='/Volumes/Android/lpdroid_models/models/malg_GPSSMSSpy/ic3/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/malg_GPSSMSSpy/ic3/'
# dareOutputDir='/Volumes/Android/lpdroid_models/models/malg_GPSSMSSpy/dare/'
# apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/GPSSMSSpy/'

# modelRepo='/Volumes/Android/lpdroid_models/models/brainTest/ic3/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/brainTest/ic3/'
# dareOutputDir='/Volumes/Android/lpdroid_models/models/brainTest/dare/'
# apk_repo='/Volumes/Android/malwareDetection/BrainTest/'

# modelRepo='/Volumes/Android/lpdroid_models/models/implicitIntent/ic3/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/implicitIntent/ic3/'
# dareOutputDir='/Volumes/Android/lpdroid_models/models/implicitIntent/dare/'
# apk_repo='/Volumes/Android/implicitIntentDataset/'



t = datetime.datetime.now().strftime('%m-%d %H:%M:%S')
log=open(dataStorageDirs+'IC3generatedModels_'+t+'.log','w')
log.write('apps in '+apk_repo+'\n')
log.write('size in KB and time format is m-d H:M:S.000\n')
log.write('---------------------------------------------------\n')

@timeout(30) #set a maximum of 30 seconds to get the package name from an app
def packageName(apkPath):
	out='PACKAGE'
	try:
		# output = subprocess.Popen(["extract_package_name_from_apk.sh ", apkPath], stdout=subprocess.PIPE).communicate()[0]
		cmd = ['/Users/Mahmoud/bin/extract_package_name_from_apk.sh', apkPath]
		p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, stdin=subprocess.PIPE)
		out, err = p.communicate()
	except TimeoutError:	
	 	print ('<packageName> TimeoutError: '+apkPath)
	except: 	
	 	print ('<packageName> Error: '+apkPath)


	return out

@timeout(600)	# 10 minutes maximum time for IC3 to extract the architecture of an app. Note that: IC3 paper claimed to extract the architecture of an app in 2 minutes
def runIC3tool(command):
	try:
		print command
		os.system(command)
	except TimeoutError:	
	 	print ('<runIC3tool> TimeoutError: '+command)
	# except: 	
	#  	print ('<runIC3tool> Error: '+command)


def runIC3():
	try:
		# i=0
		dStart = datetime.datetime.now()
		log.write('IC3 starts at '+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
		data = os.walk(apk_repo)
		command=''
		for x in data:
			files = x[2]		
			for apk in files:						
				if ".apk" in apk:
					# if(i==1):
					# 	sys.exit(0)
					# i+=1				
					appName=apk.replace('.apk','').strip()
					apkPath=x[0]+apk				
					package=packageName(apkPath).strip()
					if ('PACKAGE' != package):
						modelPath=modelRepo+package+'/'
						if (os.path.exists(dataStorageDirs+package+'.log')):							
							print 'log exists: '+dataStorageDirs+package+'.log'
						else:	
							log.write('app:('+str(os.path.getsize(apkPath)/1024)+')'+apk+'\n')
							d1 = datetime.datetime.now()
							log.write('start:'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
							dareOutput=dareOutputDir+package+'/retargeted/'+appName					
							command='/usr/local/bin/gtimeout 10m java -Xms32m -Xmx4096m -jar '+ic3Dir+'ic3-0.2.0-full.jar -input '+dareOutput+' -apkormanifest '+apkPath+' -cp '+ic3Dir+'android.jar -db '+ic3Dir+'db/cc.properties'+' >'+dataStorageDirs+package+'.log'					
							runIC3tool(command)
							# os.system(command)
							d2 = datetime.datetime.now()
							log.write('end:'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
							d = (d2-d1).total_seconds()
							log.write('total time:('+str(d)+') seconds\n')
					else:
						log.write('could not get the package name for '+x[0]+apk)		
		dEnd = datetime.datetime.now()	
		log.write('IC3 ends at '+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
		diff = (dEnd-dStart).total_seconds()
		log.write('IC3 total time:('+str(diff)+') seconds\n')
	except TimeoutError:	
	 	print ('<runIC3> TimeoutError: '+command)
	# except: 	
	#  	print ('<runIC3> Error: '+command)
	


c = datetime.datetime.now().strftime('%m-%d %H:%M:%S.000')

runIC3()

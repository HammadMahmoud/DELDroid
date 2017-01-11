import os, subprocess, datetime, sys
from time import localtime, strftime

#load the paths from ./paths.txt file
paths = open('/Volumes/Android/lpdroid_models/scripts/paths.txt', 'r')
dict={}
for l in paths:
	l=l.strip()
	keyVal = l.split('=')
	if len(keyVal)==2:
		dict[keyVal[0]] = keyVal[1]

apk_repo=dict['APK_REPO']
modelRepo=dict['MODEL_REPO'] + 'dare/'
dataStorageDirs=dict['DATA_STORAGE'] + 'dare/'

#iccta dataset
#modelRepo='/Volumes/Android/lpdroid_models/models/iccta/dare/'
#dataStorageDirs='/Volumes/Android/lpdroid_models/logs/iccta/dare/'
#apk_repo='/Volumes/Android/adaptation_dataset/iccre_iccta/installed/'

# #AnserverBot
# modelRepo='/Volumes/Android/lpdroid_models/models/malg_anserverbot/dare/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/malg_anserverbot/dare/'
# apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/AnserverBot/'

# #AnserverBot
# modelRepo='/Volumes/Android/lpdroid_models/models/malg_GPSSMSSpy/dare/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/malg_GPSSMSSpy/dare/'
# apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/GPSSMSSpy/'

#AnserverBot
# modelRepo='/Volumes/Android/lpdroid_models/models/brainTest/dare/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/brainTest/dare/'
# apk_repo='/Volumes/Android/malwareDetection/BrainTest/'

# #implicitIntent Dataset
# modelRepo='/Volumes/Android/lpdroid_models/models/implicitIntent/dare/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/implicitIntent/dare/'
# apk_repo='/Volumes/Android/implicitIntentDataset/'

t = datetime.datetime.now().strftime('%m-%d %H:%M:%S')
log=open(dataStorageDirs+'generateDareModel_'+t+'.log','w')
log.write('apps in '+apk_repo+'\n')
log.write('size in KB and time format is m-d H:M:S.000\n')
log.write('---------------------------------------------------\n')

def packageName(apkPath):
	# output = subprocess.Popen(["extract_package_name_from_apk.sh ", apkPath], stdout=subprocess.PIPE).communicate()[0]
	cmd = ['/Users/Mahmoud/bin/extract_package_name_from_apk.sh', apkPath]
	p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, stdin=subprocess.PIPE)
	out, err = p.communicate()

	return out
def runDare():
	dStart = datetime.datetime.now()
	log.write('Start DARE '+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
	data = os.walk(apk_repo)
	for x in data:
		files = x[2]		
		for apk in files:						
			if ".apk" in apk:
				apkPath=x[0]+apk				
				package=packageName(apkPath).strip()				
				modelPath=modelRepo+package.strip()+'/'
				if (os.path.exists(modelPath)):
					print 'apk path:'+apkPath
					print 'model exists for package '+package+' at path:'+modelPath
				else:	
					log.write('app:('+str(os.path.getsize(apkPath)/1024)+')'+apk+'\n')
					d1 = datetime.datetime.now()
					log.write('start:'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
					command='/usr/local/bin/gtimeout 5m /Volumes/Android/lpdroid_models/scripts/dare-1.1.0-macos/dare -d '+modelPath+' '+apkPath+'>'+dataStorageDirs+package+'.log'
					print command
					os.system(command)
					d2 = datetime.datetime.now()
					log.write('end:'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
					d = (d2-d1).total_seconds()
					log.write('total time:('+str(d)+') seconds')
	dEnd = datetime.datetime.now()	
	log.write('End DARE at '+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
	diff = (dEnd-dStart).total_seconds()
	log.write('DARE total time:('+str(diff)+') seconds')


c = datetime.datetime.now().strftime('%m-%d %H:%M:%S.000')

runDare()
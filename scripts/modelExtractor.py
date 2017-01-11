import os, subprocess, datetime, sys
from time import localtime, strftime


override="0"
if(len(sys.argv))>1:
	override=sys.argv[1]

#load the paths from ./paths.txt file
paths = open('/Volumes/Android/lpdroid_models/scripts/paths.txt', 'r')
dict={}
for l in paths:
	l=l.strip()
	keyVal = l.split('=')
	if len(keyVal)==2:
		dict[keyVal[0]] = keyVal[1]

# covertPath='/Users/Mahmoud/Documents/eclipseWorkspace/covert/runCovert.sh'
covertPath=dict['COVERT_PATH']
apk_repo=dict['APK_REPO']
modelRepo=dict['MODEL_REPO'] + 'arch/'
dataStorageDirs=dict['DATA_STORAGE'] + 'arch/'



# #IccTA dataset
# modelRepo='/Volumes/Android/lpdroid_models/models/iccta/arch/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/iccta/arch/'
# apk_repo='/Volumes/Android/adaptation_dataset/iccre_iccta/installed/'


# # Malgenome-AnServerBot dataset
# modelRepo='/Volumes/Android/lpdroid_models/models/malg_anserverbot/arch/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/malg_anserverbot/arch/'
# apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/AnserverBot/'

# # Malgenome-AnServerBot dataset
# modelRepo='/Volumes/Android/lpdroid_models/models/malg_GPSSMSSpy/arch/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/malg_GPSSMSSpy/arch/'
# apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/GPSSMSSpy/'

# # Malgenome-AnServerBot dataset
# modelRepo='/Volumes/Android/lpdroid_models/models/brainTest/arch/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/brainTest/arch/'
# apk_repo='/Volumes/Android/malwareDetection/BrainTest/'

# ImplicitIntent dataset
# modelRepo='/Volumes/Android/lpdroid_models/models/implicitIntent/arch/'
# dataStorageDirs='/Volumes/Android/lpdroid_models/logs/implicitIntent/arch/'
# apk_repo='/Volumes/Android/implicitIntentDataset/'

#modelRepo='/Volumes/Android/adaptation_dataset/dating/model/'
#apk_repo='/Volumes/Android/adaptation_dataset/iccre_iccta/installed'
#apk_repo='/Volumes/Android/malwareDetection/BrainTest'
#apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/jSMSHider'
#apk_repo='/Users/Mahmoud/Tools/AndroidSAS/APKRepo'
#apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/GPSSMSSpy'
#apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/AnserverBot'
#apk_repo='/Volumes/Android/malwareDetection/malwareResearch/Malgenome/Plankton'
#apk_repo='/Volumes/Android/adaptation_dataset/iccre_iccta/installed/'

#apk_repo="/Volumes/Android/adaptation_dataset/dating"

t = datetime.datetime.now().strftime('%m-%d %H:%M:%S')
log=open(dataStorageDirs+'generateModel_'+t+'.log','w')
log.write('apps in '+apk_repo+'\n')
log.write('size in KB and time format is m-d H:M:S.000\n')
log.write('---------------------------------------------------\n')

#directory = "/Volumes/Android/AOSP/out/target/product/bullhead/obj/APPS" #APKs in this location don't have classes.dex file
def copyAPKS():
	directory = "/Volumes/Android/AOSP/out/target/product/generic/system/app/" #APKs in this location don't have classes.dex file
	data = os.walk(directory)
	for x in data:
		files = x[2]
		if len(files)>0:
			apk = files[0]
			if ".apk" in apk:			
				apkPath=x[0]+'/'+apk
				print ('extracting model for '+apkPath)
				#command=covertPath+' '+apkPath+' '+dataStorageDirs 
				print command			
				# os.system(command)
				print apkPath
				packageName = subprocess.check_output(['/Users/Mahmoud/bin/extract_package_name_from_apk.sh '+ apkPath], shell=True)
				packageName=packageName.replace('\n','')+'.apk'
				print packageName
				# os.system('cp '+apkPath+' '+apk_repo+'/'+packageName)

def runCover(format):
	dStart = datetime.datetime.now()
	log.write('Start architecture extractor '+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))	
	data = os.walk(apk_repo)			
	for x in data:		
		files = x[2]			
		for apk in files:			
			if ".apk" in apk:
				apkPath=x[0]+'/'+apk				
				f = format				
				if format=='both':
					f='xml'
				packageName = subprocess.check_output(['/Users/Mahmoud/bin/extract_package_name_from_apk.sh '+ apkPath], shell=True)					
				if override=="0" and (os.path.exists(modelRepo+'/'+packageName.strip()+'.'+f)):
					print 'model exists: '+apkPath+" at "+modelRepo+'/'+packageName.strip()+'.'+f
				else:	
					log.write('app:('+str(os.path.getsize(apkPath)/1024)+')'+apk+'\n')
					log.write('start:'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
					d1=datetime.datetime.now()
					command='/usr/local/bin/gtimeout 10m '+covertPath+' '+apkPath+' '+modelRepo+' '+dataStorageDirs+' '+format
					print command
					os.system(command)
					d2=datetime.datetime.now()
					d = (d2-d1).total_seconds()					
					log.write('end:'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
					log.write('total time:('+str(d)+') seconds')
	dEnd = datetime.datetime.now()	
	log.write('End architecture extractor at '+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
	diff = (dEnd-dStart).total_seconds()
	log.write('Archi total time:('+str(diff)+') seconds')


c = datetime.datetime.now().strftime('%m-%d %H:%M:%S.000')


runCover("xml")


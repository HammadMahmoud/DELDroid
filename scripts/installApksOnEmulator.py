import os, subprocess, datetime, subprocess
from time import localtime, strftime, sleep


covertPath='/Users/Mahmoud/Documents/eclipseWorkspace/covert/runCovert.sh'
modelRepo='/Users/Mahmoud/Tools/AndroidSAS/ModelsRepo/'
dataStorageDirs=modelRepo+' /Users/Mahmoud/Tools/AndroidSAS/logs/'
apk_repo='/Volumes/Android/adaptation_dataset/iccre_iccta/all'
adbLogDir='/Volumes/Android/adaptation_dataset/iccre_iccta/logcats/'
installDir=adbLogDir+'/install/'
uninstallDir=adbLogDir+'/uninstall/'
clearLogcatCmnd = 'adb logcat -c'
installationLog = open(adbLogDir+'installationLog'+datetime.datetime.now().strftime('%m-%d_%H:%M:%S')+'.log','w')
hashPkgMapFile = open(adbLogDir+'hashPkgMap.log','w')
failedToInstallDir = '/Volumes/Android/adaptation_dataset/iccre_iccta/failed_to_install/'
numberOfInstalledApps=0
pkgList=[]
grpNo=0;
maxApps=20

#refresh the adb server
os.system('adb kill-server')
os.system('adb start-server')


def removeApk(pkg):
	logPath = 	uninstallDir+apk+'.log'
	log = open(logPath,'w')
	os.system(clearLogcatCmnd)				
	print 'Uinstalling '+pkg
	log.write('uninstall,'+pkg+','+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))				
	pkg = subprocess.check_output(['adb','uninstall', pkg])
	sleep(5) #time frame for adaptDroid to work before we push logcat to the log file
	os.system('adb logcat -d >>'+logPath)
	log.write('finish,'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))


data = os.walk(apk_repo)
for x in data:
		files = x[2]		
		for apk in files:						
			if ".apk" in apk:
				## max number of allowed apps to be installed in the emulator is maxApps
				##if the pkgList has maxApps members then uninstall the first one from the emulator
				if (len(pkgList)==maxApps):
					uninstallPkg = pkgList[0]
					pkgList.remove(uninstallPkg)
					removeApk(uninstallPkg)	

				apkPath=x[0]+'/'+apk			
				logPath = 	installDir+apk+'.log'
				log = open(logPath,'w')
				os.system(clearLogcatCmnd)				
				#p = subprocess.Popen(['extract_package_name_from_apk.sh', apkPath], stdout=subprocess.PIPE)
				pkg = subprocess.check_output(['extract_package_name_from_apk.sh', apkPath])
				pkg = pkg.replace('\n','')
				hashPkgMapFile.write(apk+':'+pkg+'\n')
				print 'installing '+pkg
				log.write('install,'+pkg+','+apk+','+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))				
				installMsg = subprocess.check_output(['adb','install', apkPath])
				if ('Success' in installMsg):
					installationLog.write('Success,'+apk+','+pkg+'\n')
					pkgList.append(pkg)
					numberOfInstalledApps+=1
				else:
					log.write('\nFailed to install '+apk+','+pkg+'\n')
					installationLog.write('Failure,'+apk+','+pkg+'\n'+installMsg)
					subprocess.call(['mv',apkPath,failedToInstallDir])
				#os.system('adb install '+apkPath)
				sleep(7) #time frame for adaptDroid to work before we push logcat to the log file
				os.system('adb logcat -d >>'+logPath)
				log.write('finish,'+datetime.datetime.now().strftime('%m-%d %H:%M:%S.000\n'))
				
				if(numberOfInstalledApps==maxApps):
					installationLog.write('---------------------- GROUP '+str(grpNo)+' ----------------------\n')
					installationLog.write(str(pkgList))
					installationLog.write('\n-------------------------------------------------------------\n')
					grpNo+=1
					numberOfInstalledApps=0


for pkg in pkgList:
	removeApk(pkg)				



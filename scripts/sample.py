import random
from random import shuffle

expPath='/Volumes/Android/lpdroid_models/eval/eval_30apps/'
IccTA_apps=expPath+'iccta_apps.txt'
malware_apps=expPath+'malware_apps.txt'
gplay_apps=expPath+'gplay_apps.txt'
pureGplay_apps=expPath+'pureBenignGPlay_apps.txt'

IccTA = open(IccTA_apps, 'r')
malware = open(malware_apps, 'r')
gplay = open(gplay_apps, 'r')
pureGplay = open(pureGplay_apps, 'r')

IccTAlst = []	#draw 5 random apps from the IccTA dataset
malwareLst = []	#draw need 5 random apps from the Malware dataset
gplayLst = []	#draw need 40 random apps from gplay dataset
pureGplayLst=[]

for l in pureGplay:
	l=l.strip()
	pureGplayLst.append(l)

for l in IccTA:
	l=l.strip()
	IccTAlst.append(l)

for l in malware:
	l=l.strip()
	malwareLst.append(l)

for l in gplay:
	l=l.strip()
	gplayLst.append(l)

print (str(len(IccTAlst))+' IccTA apps')
print (str(len(malwareLst))+' Malware apps')
print (str(len(gplayLst))+' GPlay apps')
print (str(len(pureGplayLst))+' Pure GPlay apps')

shuffle(IccTAlst)
shuffle(malwareLst)
shuffle(gplayLst)
shuffle(pureGplayLst)

#Bundle Structure
pureGplayBundle=10
gplayBundle=14
iccTABundle=3
malwareBundle=3

print '---------------------------------'
for bundleNo in range(1, 11):
	f1 = (bundleNo-1)*iccTABundle
	f2 = (bundleNo-1)*malwareBundle
	f3 = (bundleNo-1)*pureGplayBundle
	f4 = (bundleNo-1)*gplayBundle

	# print str(bundleNo)+":"+ str(IccTAlst[f1: (f1+iccTABundle)]).replace('[','').replace(']','')	
	# print str(bundleNo)+":"+ str(malwareLst[f2: (f2+malwareBundle)]).replace('[','').replace(']','')
	# print str(bundleNo)+":"+ str(pureGplayLst[f3: (f3+pureGplayBundle)]).replace('[','').replace(']','')
	# print str(bundleNo)+":"+ str(gplayLst[f4: (f4+gplayBundle)]).replace('[','').replace(']','')

	deleteQuery = 'delete from apps_bundle where bundle='+str(bundleNo)+';'
 	query = ('insert into apps_bundle select id,app,version,dataset,ds_id,'+str(bundleNo)+' from applications where id in ('+
 		str(IccTAlst[f1 : (f1+iccTABundle)]).replace('[','').replace(']','')+','+
 		str(malwareLst[f2 : (f2+malwareBundle)]).replace('[','').replace(']','') +','+
 		str(pureGplayLst[f3 : (f3+pureGplayBundle)]).replace('[','').replace(']','') +','+
 		str(gplayLst[f4 : (f4+gplayBundle)]).replace('[','').replace(']','') + ');')
	print deleteQuery+'\n'+query





# for i in range(1, 11): # 1 ..10	
# 	IccTArandList = random.sample(IccTAlst, 5)
# 	malwareRandList = random.sample(malwareLst, 5)
# 	gplayRandList = random.sample(gplayLst, 40)

# 	expFile = open(expPath+'exp'+str(i)+'.txtw')
# 	for a in IccTArandList:
# 		expFile.write('\''+a+'\',\n')

# 	for a in malwareRandList:
# 		expFile.write('\''+a+'\',\n')

# 	for a in gplayRandList:
# 		expFile.write('\''+a+'\',\n')



import os
import subprocess
inf221_hw1_submission='/Users/Mahmoud/Documents/UCI/Fall_2016/INF221/HW1_all_submissions/'
# 'B50_Armin_Balalaie',
# 'B51_PRAKUL_AGARWAL',
# 'B52_Andrew_Chang',
lst = [
'B53_Arpan_Sunil_Tolat',
'B54_ASHISH_PEDABALLI',
'B55_Chaitanya_Kshirsagar',
'B56_Chaiyathorn_Vachirakornwattana',
'B57_Farima_FarmahiniFarahani',
'B58_Hao_Ni',
'B59_Haorui_Wang',
'B60_Jun-Wei_Lin',
'B61_Nayanika_Upadhyay',
'B62_Negar_Ghorbani',
'B63_Nikshep_Kotha_Nagendra',
'B64_Nishad_Gurav',
'B65_Prasannah_Balasubramanian',
'B66_Pratik_Shetty',
'B67_Reeta_Ashokkumar_Singh',
'B68_Rui_Zhang',
'B69_Saeed_Mirzamohammadi',
'B70_Sang_Hai',
'B71_Seungmok_Lee',
'B72_Shannon_nicole_Stanton',
'B73_Sijie_Yu',
'B74_Sripad_Kowshik_Subramanyam_late',
'B75_Sumit_Salvi',
'B76_Swarun_Krishnamoorthy',
'B77_Tanmay_Khemka',
'B78_TARIQ_IBRAHIM',
'B79_Uday_Mittal',
'B80_Varun_Bharill',
'B81_Wang_Zhendong',
'B82_Xi_Chen',
'B83_Xi_Zhang',
'B84_Yadhu_Prakash',
'B85_Yen-Feng_Cheng',
'B86_Yixian_Chen',
'B87_Zhisheng_Li',
'B88_Zhu_Haitao',
'B89_Zhuomeng_Li']


line1='COVERT_PATH=/Users/Mahmoud/Documents/eclipseWorkspace/covert/runCovert.sh\n'
line2='IC3_DIR=/Volumes/Android/lpdroid_models/scripts/ic3-0.2.0/\n'
line3='MODEL_REPO=/Volumes/Android/lpdroid_models/models/INF221/\n'
line4='DATA_STORAGE=/Volumes/Android/lpdroid_models/logs/INF221/\n'
line5='APK_REPO=/Users/Mahmoud/Documents/UCI/Fall_2016/INF221/HW1_all_submissions/'


for d in lst:
    d=d.strip()
    print d
    paths = open ('./paths.txt','w')
    paths.write(line1+line2+line3+line4+line5+d+'/\n')
    paths.close()
    print('run model extractor on '+d)

    print 'python ./modelExtractor.py'
    os.system('python ./modelExtractor.py')
    print('run Dare on '+d)
    print('python ./runDare.py')
    os.system('python ./runDare.py')
    print('run IC3 on '+d)
    print('python ./runIC3.py')
    os.system('python ./runIC3.py')
    print('Finished '+d+'\n')    
    






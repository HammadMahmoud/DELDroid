from timeout import timeout
from timeout import TimeoutError
import time, os


@timeout(1)
def f():
# 	time.sleep(60)
	paths = open('./paths.txt', 'r')
	dict={}
	for l in paths:
		l=l.strip()
		keyVal = l.split('=')
		if len(keyVal)==2:
			dict[keyVal[0]] = keyVal[1]

	print dict

print ('start')
try:
	f()
except TimeoutError:	
 	print ('Error')
except: 	
 	raise

print ('end')
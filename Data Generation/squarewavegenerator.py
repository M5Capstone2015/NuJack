import math
import matplotlib.pyplot as plt
import logging
import argparse
from numpy import linspace,sin,pi,int16,hstack
import numpy
from scipy.io.wavfile import write
from scipy import signal
from pylab import plot,show,axis

parser = argparse.ArgumentParser()
logging.basicConfig(level=logging.WARNING)
parser.add_argument("name", help="name of wave file")
parser.add_argument("startfreq", help="Frequency of Start Bit",type=int)
parser.add_argument("zerofreq", help="Frequency of Binary Zero",type=int)
parser.add_argument("onefreq", help = "Frequency of Binary One",type=int)
parser.add_argument("bitlength", help = "Length of each bit",type=float)
parser.add_argument("bitpattern", help = "Pattern of bits")


args = parser.parse_args()
name = args.name+'.wav'
startfreq = args.startfreq
zerofreq = args.zerofreq
onefreq = args.onefreq
bitlength = args.bitlength
bitpattern = args.bitpattern
bitarray = []

#parse input into bit array
for i in range(0,len(bitpattern)):
	print("Bitpattern %s " %bitpattern[i])
	bitarray.append(int(bitpattern[i]))



# tone synthesis
def note(freq, len, amp=1, rate=44100):
	t = linspace(0,len,len*rate)
	data = sin(2*pi*freq*t)*amp
	return data.astype(int16) # two byte integers

def square(freq,len,amp=1,rate=44100):
	t=linspace(0,len,len*rate)
	data = signal.square(2*pi *freq* t)*amp
	return data.astype(int16) # two byte integers


# converting bit array to square waves

together = square(startfreq,bitlength,amp=10000)
for i in enumerate(bitarray):
	print(i[1])
	if i[1] is 0:
		tone = square(zerofreq,bitlength,amp=10000)
	else:
		tone = square(onefreq,bitlength,amp=10000)	
	together = hstack((together,tone))

# debug, can be activated by changing line 12 to
# logging.basicConfig(level=logging.DEBUG)

for i in enumerate(together):
	 logging.debug("Line: %i, Data: %.2f" %(i[0] ,i[1]))

write(name,44100,together) # writing the sound to a file
 

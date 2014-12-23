import spidev as spi
from time import sleep
import time
import struct
import copy

a = spi.SpiDev(0, 0)

print "PY: initialising SPI mode, speed, delay"
a.mode = 2
a.bits_per_word = 8
a.max_speed_hz = 488000

fout = open("out.raw", "wb")

def get_tx():
        ar = []
        for i in range(1801):
                ar.extend([0,1])
        return ar
tx_data = get_tx()
#buffer=[0 for i in range(80032)]
buffer=[]
k=40
i=k
tbegin = time.time()
print tbegin
while i:
        i-=1
        trans = get_tx()
        t1=time.time()
        ar = a.xfer(copy.copy(tx_data))
        print len(ar)/2.0/(time.time()-t1)
        buffer.append(ar[2:])
elapsed = time.time()-tbegin
print "elapsed ", elapsed
for bytes in buffer:
        fout.write(struct.pack('3600B',*bytes))
fout.close()

samples = k*3600
print "samples ", samples
print "rate ", samples/elapsed

"oggenc -r  -B 16 -C 1 -R 13000 out.raw -o out.ogg"
import spidev as spi
from time import sleep
import time
import struct
from threading import Thread
from subprocess import call

_adc = spi.SpiDev(0, 0)

print "PY: initialising SPI mode, speed, delay"
_adc.mode = 2
_adc.bits_per_word = 8
_adc.max_speed_hz = 488000


class AdcController():
    def __init__(self):
        self.thread = None

    @staticmethod
    def __read_data(args):
        file_name = "out.raw"
        compressed_file_name = "out.ogg"
        fout = open(file_name, "wb")
        buff = []
        k = 80
        i = k
        tbegin = time.time()
        tx = []
        for i in range(1801):
            tx.extend([0, 1])
        while i:
            i -= 1
            # t1=time.time()
            ar = _adc.xfer(tx);
            # print len(ar)/2.0/(time.time()-t1)
            buff.append(ar[2:])
        elapsed = time.time() - tbegin
        print "elapsed ", elapsed
        for block in buff:
            fout.write(struct.pack('3600B', *block))
        fout.close()

        samples = k * 1800
        print "samples ", samples
        print "rate ", samples / elapsed
        call(["sh", "-c", "oggenc -r  -B 16 -C 1 -R 13000 '%s' -o '%s'" % (file_name, compressed_file_name)])
        args(compressed_file_name)

    def capture_audio(self, callback):
        if self.thread:
            return
        self.thread = Thread(target=self.__read_data, args=callback)


_adc_controller = AdcController()


def get_adc_controller():
    return _adc_controller



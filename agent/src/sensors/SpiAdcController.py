import spidev as spi
from time import sleep
import time
import struct
from threading import Thread
from subprocess import call

_adc1 = spi.SpiDev(0, 0)    #audio
_adc2 = spi.SpiDev(0, 1)    #ldr

print "PY: initialising SPI mode, speed, delay"
_adc1.mode = 2
_adc1.bits_per_word = 8
_adc1.max_speed_hz = 977000
_adc2.mode = 2
_adc2.bits_per_word = 8
_adc2.max_speed_hz = 721000


class AdcController():
    def __init__(self):
        self.thread = None
        self.callback = None

    def __read_audio_data(self, callback):
        file_name = "out.raw"
        compressed_file_name = "out.ogg"
        fout = open(file_name, "wb")
        buff = []
        k = 160
        tbegin = time.time()
        # print tbegin
        tx = []
        samplesBufferSize = 1800
        for i in range(samplesBufferSize+1):
            tx.extend([0, 1])
        i = k
        while i:
            i -= 1
            #t1 = time.time()
            try:
                ar = _adc1.xfer(tx)
                buff.append(ar[2:])
            except Exception, e:
                print "Error: ", e
            #print len(ar)/2.0/(time.time()-t1)
        elapsed = time.time() - tbegin
        print "elapsed ", elapsed
        for block in buff:
            fout.write(struct.pack(str(samplesBufferSize*2)+'B', *block))
        fout.close()

        samples = k * samplesBufferSize
        print "samples ", samples
        print "rate ", samples / elapsed
        call(["sh", "-c", "oggenc -r  -B 16 -C 1 -R 21040 '%s' -o '%s'" % (file_name, compressed_file_name)])
        callback(compressed_file_name)

    def capture_audio(self, callback):
        if self.thread:
            return
        self.callback = callback
        l = lambda x: self.__read_audio_data(x)
        self.thread = Thread(target=l, args=(lambda arg: self.on_recording_finished(arg),))
        self.thread.start()

    def read_ldr_sensor(self):
        tx = []
        for i in range(9):
            tx.extend([0, 1])
        ar = _adc2.xfer(tx)
        lar = len(ar)
        j = 2
        value = 0
        i = 0
        while j < lar:
            val = (ar[j] << 8) + ar[j + 1]
            value += val
            j += 2
            i += 1
        value /= i
        return value

    def read_temperature_sensor(self):
        tx = []
        for i in range(9):
            tx.extend([0, 1])
        ar = _adc1.xfer(tx)
        lar = len(ar)
        j = 2
        value = 0
        i = 0
        while j < lar:
            val = (ar[j] << 8) + ar[j + 1]
            j += 2
            if val > 4000:
                continue
            value += val
            i += 1
        value /= i
        return (3.3*value/4096)*100

    def on_recording_finished(self, arg):
        self.callback(arg)
        self.thread = None


_adc_controller = AdcController()


def get_adc_controller():
    return _adc_controller



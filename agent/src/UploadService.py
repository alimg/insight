from threading import Thread
import Queue
import socket
import os
import struct
import time
import AgentConfig
import json
from PIL import Image


from ServerConstants import *


class UploadService(Thread):
    def __init__(self, device_id):
        super(UploadService, self).__init__()
        self.file_queue = Queue.Queue()
        self.running = 1
        self._STOP = object()
        self.device_id = device_id

    def run(self):
        while self.running:
            file_name, meta_data = self.file_queue.get(True)
            if file_name == self._STOP:
                break
            print "Sending file: ", file_name, meta_data
            self.__send_file(file_name, meta_data)

    def __send_file(self, file_name, meta_data):
        con = socket.socket()
        con.connect(PHOTO_SERVER_ADDRESS)
        with open(file_name, "rb") as f:
            f.seek(0, os.SEEK_END)
            meta_data_len = len(meta_data)
            con.send(struct.pack('I%ds' % (meta_data_len,), meta_data_len, meta_data))
            size = f.tell()
            con.send(struct.pack('I', size))
            f.seek(0, os.SEEK_SET)
            buff = f.read(1024)
            con.send(buff)
            #print len(buff)
            while buff:
                buff = f.read(1024)
                con.send(buff)
                #print len(buff)
        data = con.recv(64)
        print "response: "+data
        #os.remove(file_name)
        con.close()

    def upload_photo(self, image_data):
        file_name = AgentConfig.DATA_STORAGE_DIR+"%s.jpeg" % time.time()
        image_data.save(file_name, "jpeg")
        self.file_queue.put((file_name, json.dumps(
            {"type": "jpeg", "date": int(time.time()), "device": self.device_id})))

    def stop(self):
        self.running = False
        self.file_queue.put(self._STOP)

    def upload_audio(self, file_name):
        self.file_queue.put((file_name, json.dumps(
            {"type": "ogg", "date": int(time.time()), "device": self.device_id})))
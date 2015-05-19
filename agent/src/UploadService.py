from threading import Thread
import Queue
import socket
import os
import struct
import time
import AgentConfig
import json
from PIL import Image

import EncryptionUtil
from ServerConstants import *
import select


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
            try:
                self.__send_file(file_name, meta_data)
            except Exception, e:
                print "Failed upload: ", e
            print "Done: ", file_name, meta_data



    def __send_file(self, file_name, meta_data):
        def send_timeout(con, f):
            _ready = select.select([], [con], [], 30)
            if _ready[1]:
                f()
            else:
                con.close()
                raise Exception("timed out")
        con = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        con.connect(PHOTO_SERVER_ADDRESS)
        con.setblocking(0)
        with open(file_name, "rb") as f:
            f.seek(0, os.SEEK_END)
            meta_data_len = len(meta_data)
            con.send(struct.pack('I%ds' % (meta_data_len,), meta_data_len, meta_data))
            size = f.tell()
            ready = select.select([], [con], [], 30)
            send_timeout(con, lambda:
                con.send(struct.pack('I', size)))
            f.seek(0, os.SEEK_SET)
            buff = f.read(1024)
            send_timeout(con, lambda:
                con.send(buff))
            #print len(buff)
            while buff:
                buff = f.read(1024)
                send_timeout(con, lambda:
                    con.send(buff))
                #print len(buff)

        ready = select.select([con], [], [], 60)
        if ready[0]:
            data = con.recv(64)
        else:
            con.close()
            raise Exception("timed out")
        print "response: "+data
        if data == "ok":
            os.remove(file_name)
            print "sent file "+file_name+" successfully"
        con.close()

    def upload_photo(self, image_data):
        file_name = AgentConfig.DATA_STORAGE_DIR+"%s.jpeg" % time.time()
        image_data.save(file_name, "jpeg")
        print "Queuing file upload: ", file_name
        #new_file = EncryptionUtil.encrypt_file(file_name)
        self.file_queue.put((file_name, json.dumps(
            {"type": "jpeg", "date": int(time.time()), "device": self.device_id, "encryption": ""})))

    def stop(self):
        self.running = False
        self.file_queue.put(self._STOP)

    def upload_audio(self, file_name):
        self.file_queue.put((file_name, json.dumps(
            {"type": "ogg", "date": int(time.time()), "device": self.device_id, "encryption": ""})))

    def upload_video(self, file_name):
        self.file_queue.put((file_name, json.dumps(
            {"type": "h264", "date": int(time.time()), "device": self.device_id, "encryption": ""})))

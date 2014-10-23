from threading import Thread
import Queue
import socket
import os
import struct
from ServerConstants import *

class UploadService(Thread):
    def __init__(self):
        super(UploadService, self).__init__()
        self.file_queue = Queue.Queue()
        self.running = 1

    def run(self):
        while self.running:
            file_name = self.file_queue.get(True)
            print "Sending file: "+file_name
            self.__send_file(file_name)

    def __send_file(self, file_name):
        con = socket.socket()
        con.connect(PHOTO_SERVER_ADDRESS)
        with open(file_name, "rb") as f:
            f.seek(0, os.SEEK_END)
            size = f.tell()
            con.send(struct.pack('I', size))
            f.seek(0, os.SEEK_SET)
            buff = f.read(1024)
            con.send(buff)
            print len(buff)
            while buff:
                buff = f.read(1024)
                con.send(buff)
                print len(buff)
        data = con.recv(64)
        print "response: "+data
        con.close()

    def upload_photo(self, file_name):
        self.file_queue.put(file_name)
#!/usr/bin/python2.7
import struct
import SocketServer
import json
from webservice.api import ServerConstants
from threading import Thread


class UploadServer(Thread):
    def __init__(self, address, upload_listener):
        super(UploadServer, self).__init__()
        self.daemon = True
        self.server = SocketServer.TCPServer(address, UploadRequestHandler)
        self.server.upload_listener = upload_listener
        print "Server set to {}".format(address)

    def run(self):
        print "Server started"
        self.server.serve_forever()

    def stop(self):
        self.server.shutdown()


class UploadRequestHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        # self.request is the TCP socket connected to the client

        header_size = struct.unpack('I', self.request.recv(4))[0]
        header = struct.unpack('%ds' % (header_size), self.request.recv(header_size))[0]
        print "file header: ", header
        meta_data = json.loads(header)

        file_size = struct.unpack('I', self.request.recv(4))[0]  # up to 1 GB
        print file_size
        current_size = 0

        file_name = "%s-%s.%s" % (meta_data["device"], meta_data["date"], meta_data["type"])
        with open(ServerConstants.STORAGE_DIR+file_name, "w") as f:
            while current_size < file_size:
                d = self.request.recv(1024)
                if not d:
                    break
                current_size += len(d)
                f.write(d)
                # print "\r\tProcess: ", (current_size), "/", self.size,
            f.close()
            print "Recieved", file_size
        # just send back the same data, but upper-cased

        self.server.upload_listener(meta_data, file_name)

        self.request.sendall('ok')


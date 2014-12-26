#!/usr/bin/python2.7
import struct
import SocketServer
from webservice.api import ServerConstants
from threading import Thread


class UploadServer(Thread):
    def __init__(self, address):
        super(UploadServer, self).__init__()
        self.server = SocketServer.TCPServer(address, UploadRequestHandler)
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
        header = struct.unpack('%ds'% (header_size), self.request.recv(header_size))[0]
        print header

        self.size = struct.unpack('I', self.request.recv(4))[0]  # up to 1 GB
        print self.size
        current_size = 0
        with open(ServerConstants.FILE, "w") as f:
            while current_size < self.size:
                d = self.request.recv(1024)
                if not d:
                    break
                current_size += len(d)
                f.write(d)
                print "\r\tProcess: ", (current_size), "/", self.size,
            f.close()
            print "Recieved"
        # just send back the same data, but upper-cased

        self.request.sendall('ok')


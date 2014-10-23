import SocketServer
import struct

class MyTCPHandler(SocketServer.BaseRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(4)
        print "{} wrote:".format(self.client_address[0])
        print self.data
        size = struct.unpack("I", self.data)[0]
        print "size {}".format(size)
        received = 0
        while received < size:
            self.data = self.request.recv(1024)
            data_len = len(self.data)
            received += data_len
            print "{}/{}".format(received, size)
            if data_len == 0:
                break


        # just send back the same data, but upper-cased
        self.request.sendall("ok")


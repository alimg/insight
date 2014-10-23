from TestServer import MyTCPHandler

import SocketServer

HOST, PORT = "localhost", 5014

server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
server.serve_forever()
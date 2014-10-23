#!/usr/bin/python2.7

import SocketServer
import ServerConstants

class UploadServer:

	def __init__(self, port):
		host = 'localhost'
		# Create the server, binding to localhost on port 9999
		self.server = SocketServer.TCPServer((host, port), MyTCPHandler)
		print "Server set to ", host, ":", port
	def start(self):
		print "Server started"
		self.server.serve_forever()

class MyTCPHandler(SocketServer.BaseRequestHandler):
	"""
	The RequestHandler class for our server.

	It is instantiated once per connection to the server, and must
	override the handle() method to implement communication to the
	client.
	"""

	def handle(self):
		# self.request is the TCP socket connected to the client
		size_c = bytearray(self.request.recv(4)) # up to 1 GB
		self.size = (size_c[0]<<24)+(size_c[1]<<16)+(size_c[2]<<8)+size_c[3]
		print self.size
		current_size = 0
		with open("out", "w") as f:
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

		#self.request.sendall(self.data.upper())

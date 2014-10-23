#!/usr/bin/python2.7

import socket
import sys
import constants
import os

if len(sys.argv) == 1:
	print "Usage: ", sys.argv[0], " filename"
	sys.exit(-1)

HOST, PORT = "localhost", constants.PORT
data = " ".join(sys.argv[1:])
filepath = sys.argv[1]

# Create a socket (SOCK_STREAM means a TCP socket)
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
	# Connect to server and send data
	if os.path.exists(filepath):
		length = os.path.getsize(filepath)
		print length
		sock.connect((HOST, PORT))
		length_c = bytearray(4)
		length_c[0] = length&255
		length /= 256
		length_c[1] = length&255
		length /= 256
		length_c[2] = length&255
		length /= 256
		length_c[3] = length&255
		print length_c[3]
		sock.send(length_c) # up to 1 GB
		with open(filepath, "r") as f:
			d = f.read(1024)
			while d:
				sock.send(d)
				d = f.read(1024)
	else:
		print sys.argv[1], " not exists"
		sys.exit(-1)

	# Receive data from the server and shut down
#	received = sock.recv(1024)
finally:
	sock.close()

print "Sent:     {}".format(data)
#print "Received: {}".format(received)

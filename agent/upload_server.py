#!/usr/bin/python2.7

from UploadServer import UploadServer
import ServerConstants

server = UploadServer(ServerConstants.UPLOAD_SERVER_ADDRESS[1])
server.start()

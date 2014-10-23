#!/usr/bin/python2.7

from UploadServer import UploadServer
import constants

server = UploadServer(constants.PORT)
server.start()

from UploadServer import UploadServer


HOST, PORT = "localhost", 5014

server = UploadServer((HOST, PORT))
server.start()

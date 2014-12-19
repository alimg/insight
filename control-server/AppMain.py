from UploadServer import UploadServer
from CommandServer import CommandServer
import ClientConnectionHandler


class AppMain:
    def __init__(self):
        HOST = "localhost"
        UPLOAD_SERVER_PORT = 5014
        COMMAND_SERVER_PORT = 5013
        self.command_server = CommandServer((HOST, COMMAND_SERVER_PORT))
        self.upload_server = UploadServer((HOST, UPLOAD_SERVER_PORT))
        self.running = True

    def start(self):
        self.command_server.set_connection_handler(ClientConnectionHandler.ConnectionHandler())
        self.command_server.start()
        self.upload_server.start()

    def stop(self):
        self.running = False
        self.command_server.stop()
        self.upload_server.stop()

    def wait(self):
        while self.running:
            self.command_server.join(1000)
            self.upload_server.join(1000)

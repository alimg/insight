from UploadServer import UploadServer
from CommandServer import CommandServer
import ClientConnectionHandler
from webservice import WebService

class AppMain:
    def __init__(self):
        HOST = "0.0.0.0"
        UPLOAD_SERVER_PORT = 5014
        COMMAND_SERVER_PORT = 5013
        self.command_server = CommandServer((HOST, COMMAND_SERVER_PORT))
        self.upload_server = UploadServer((HOST, UPLOAD_SERVER_PORT))
        self.web_service = WebService.WebService(lambda x: self.command_server.send_command(x))
        self.running = True

    def start(self):
        self.command_server.set_connection_handler(ClientConnectionHandler.ConnectionHandler())
        self.command_server.start()
        self.upload_server.start()
        self.web_service.start()


    def stop(self):
        self.running = False
        self.command_server.stop()
        self.upload_server.stop()

    def wait(self):
        while self.running:
            self.command_server.join(1000)
            self.upload_server.join(1000)

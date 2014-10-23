import socket
import thread
from ServerConstants import *


class NetClient:
    def __init__(self):
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.commandHandlers = []
        self.running = 1

    def start(self):
        thread.start_new(self.run)

    def run(self):
        self.socket.connect(NET_SERVER_ADDRESS)

        self.socket.send("hello")
        while self.running:
            data = self.socket.recvfrom(1024)
            print data
            for handler in self.commandHandlers:
                handler(data)

    def add_command_handler(self, handler):
        self.commandHandlers.append(handler)

    def stop(self):
        self.running = 0
        self.socket.close()
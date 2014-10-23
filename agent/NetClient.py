import socket
from threading import Thread
from ServerConstants import *


class NetClient(Thread):
    def __init__(self):
        super(NetClient, self).__init__()
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.commandHandlers = []
        self.running = 1

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
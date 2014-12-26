import socket
import time
import DaemonThread
import json

from ServerConstants import *


class CommandClient(DaemonThread.DaemonThread):
    def __init__(self):
        super(CommandClient, self).__init__()
        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.commandHandlers = []
        self.running = 1

    def _run(self):
        self.socket.connect(NET_SERVER_ADDRESS)
        #self.socket.send("hello")

        while self.running:
            data = self.socket.recv(1024)
            if not data:
                print "socket down"
                time.sleep(5)
                self.socket.close()
                raise socket.error()
            for handler in self.commandHandlers:
                handler(data)

    def send_message(self, command):
        self.socket.sendall(json.dumps(command))

    def add_command_handler(self, handler):
        self.commandHandlers.append(handler)

    def stop(self):
        self.running = 0
        try:
            self.socket.shutdown(socket.SHUT_WR)
            self.socket.close()
        except:
            print "err"

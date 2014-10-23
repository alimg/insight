import time

from NetClient import NetClient
from HwController import HwController


class Agent:
    def __init__(self, args):
        self.args = args
        self.hwController = HwController()
        self.netClient = NetClient()

    def run(self):
        self.netClient.add_command_handler(lambda command: self.hwController.process_command(command))

        self.netClient.start()
        self.hwController.start()


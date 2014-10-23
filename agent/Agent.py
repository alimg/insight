import time

from NetClient import NetClient
from HwController import HwController
from UploadService import UploadService


class Agent:
    def __init__(self, args):
        self.args = args
        self.hwController = HwController(lambda event: self.uploadService.upload_photo(event))
        self.netClient = NetClient()
        self.uploadService = UploadService()

    def run(self):
        self.netClient.add_command_handler(lambda command: self.hwController.process_command(command))

        self.netClient.start()
        self.hwController.start()



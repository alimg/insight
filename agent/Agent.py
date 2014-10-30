import time

from CommandClient import CommandClient
from HwController import HwController
from UploadService import UploadService


class Agent:
    def __init__(self, args):
        self.args = args
        self.hwController = HwController(lambda event: self.uploadService.upload_photo(event))
        self.commandClient = CommandClient()
        self.uploadService = UploadService()
        self.running = True

    def run(self):
        self.commandClient.add_command_handler(lambda command: self.hwController.process_command(command))

        self.commandClient.start()
        self.hwController.start()
        self.uploadService.start()

    def stop(self):
        self.running = False
        self.commandClient.stop()
        self.uploadService.stop()
        self.hwController.stop()

    def wait(self):
        while self.running:
            self.uploadService.join(1000)
            self.hwController.join(1000)
            self.commandClient.join(1000)

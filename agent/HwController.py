from threading import Thread
import time

class HwController(Thread):
    def __init__(self, camera_event_handler):
        super(HwController, self).__init__()
        self.camera_event_handler = camera_event_handler

    def run(self):
        time.sleep(1)
        self.camera_event_handler("data/R4s9z.jpg")

    def process_command(self, command):
        print "proccessCommand " + command


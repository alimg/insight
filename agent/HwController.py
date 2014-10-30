from threading import Thread
import json
import Queue


class HwController(Thread):
    def __init__(self, camera_event_handler):
        super(HwController, self).__init__()
        self.camera_event_handler = camera_event_handler
        self.command_queue = Queue.Queue()
        self._STOP = object()
        self.running = True

    def run(self):
        while self.running:
            data = self.command_queue.get()
            if data == self._STOP:
                break
            command = json.loads(data)
            if command['action'] == "cap_photo":
                self.camera_event_handler("data/R4s9z.jpg")

    def process_command(self, command):
        print "proccessCommand ", command
        self.command_queue.put(command)

    def stop(self):
        self.running = False
        self.command_queue.put(self._STOP)


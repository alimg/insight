from threading import Thread
import json
import Queue
from sensors.PIRSensor import PIRSensor
from sensors import Camera


class HwController(Thread):
    def __init__(self, camera_event_handler):
        super(HwController, self).__init__()
        self.camera_event_handler = camera_event_handler
        self.camera = Camera.get_camera()
        self.command_queue = Queue.Queue()
        self._STOP = object()
        self.running = True
        self.pir_sensor = PIRSensor(lambda: self.on_pir_trigger())

    def run(self):
        while self.running:
            data = self.command_queue.get()
            if data == self._STOP:
                break
            command = json.loads(data)
            if command['action'] == "cap_photo":
                image = self.camera.take_picture()
                self.camera_event_handler(image)


    def process_command(self, command):
        print "proccessCommand ", command
        self.command_queue.put(command)

    def stop(self):
        self.running = False
        self.command_queue.put(self._STOP)

    def on_pir_trigger(self):
        print "hello human"
        image = self.camera.take_picture()
        self.camera_event_handler(image)



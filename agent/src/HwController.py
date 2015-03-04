from threading import Thread
import Queue
from sensors.LedController import LedController
from sensors.PIRSensor import PIRSensor
from sensors import Camera
from sensors import SpiAdcController


class HwController(Thread):
    def __init__(self, camera_event_handler, video_event_handler,   audio_event_handler):
        super(HwController, self).__init__()
        self.camera_event_handler = camera_event_handler
        self.video_event_handler = video_event_handler
        self.audio_event_handler = audio_event_handler

        self.camera = Camera.get_camera()
        self.adc_controller = SpiAdcController.get_adc_controller()
        self.command_queue = Queue.Queue()
        self._STOP = object()
        self.running = True
        self.pir_sensor = PIRSensor(lambda: self.on_pir_trigger())
        self.led_controller = LedController()

    def run(self):
        while self.running:
            command = self.command_queue.get()
            if command == self._STOP:
                break
            if command['action'] == "cap_photo":
                image = self.camera.take_picture()
                self.camera_event_handler(image)
            elif command['action'] == "cap_audio":
                self.adc_controller.capture_audio(lambda captured_file: self.on_audio_captured(captured_file))
            elif command['action'] == "cap_video":
                self.camera.capture_video(lambda captured_file: self.on_video_captured(captured_file))
            elif command['action'] == "cap_temperature":
                temp = self.adc_controller.read_temperature_sensor()
                print temp



    def process_command(self, command):
        print "processCommand ", command
        self.command_queue.put(command)

    def stop(self):
        self.running = False
        self.command_queue.put(self._STOP)

    def on_pir_trigger(self):
        print "hello human"
        image = self.camera.take_picture()
        self.camera_event_handler(image)

    def on_audio_captured(self, captured_file):
        self.audio_event_handler(captured_file)

    def set_led_status(self, status):
        self.led_controller.set_status(status)

    def on_video_captured(self, captured_file):
        self.video_event_handler(captured_file)



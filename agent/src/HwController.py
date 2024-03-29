from threading import Thread, Timer
import Queue
from sensors.IRController import IRController
from sensors.LedController import LedController
from sensors.PIRSensor import PIRSensor
from sensors import Camera
from sensors import SpiAdcController
from sensors.ButtonController import ButtonController


class HwController(Thread):
    def __init__(self, camera_event_handler, video_event_handler, audio_event_handler, setup_button_handler):
        super(HwController, self).__init__()
        self.camera_event_handler = camera_event_handler
        self.video_event_handler = video_event_handler
        self.audio_event_handler = audio_event_handler
        self.setup_button_handler = setup_button_handler

        self.camera = Camera.get_camera()
        self.adc_controller = SpiAdcController.get_adc_controller()
        self.command_queue = Queue.Queue()
        self._STOP = object()
        self.running = True
        self.pir_sensor = PIRSensor(lambda: self.on_pir_trigger())
        self.led_controller = LedController()
        self.IR_controller = IRController()
        self.button_controller = ButtonController(lambda: self.setup_button_handler())
        self.start_ldr_timer()
        self.pir_enabled = True

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
        if not self.pir_enabled:
            print "pir disabled by user conf"
            return
        image = self.camera.take_picture()
        self.camera_event_handler(image)

    def on_audio_captured(self, captured_file):
        self.audio_event_handler(captured_file)

    def set_led_status(self, status):
        self.led_controller.set_status(status)

    def on_video_captured(self, captured_file):
        self.video_event_handler(captured_file)

    def start_ldr_timer(self):
        ldr_val = self.adc_controller.read_ldr_sensor()
        print "LDR: ", ldr_val
        if ldr_val > 2500:
            self.IR_controller.enable()
        else:
            self.IR_controller.disable()
        self.ldr_timer = Timer(5.0, lambda: self.start_ldr_timer())
        self.ldr_timer.start()


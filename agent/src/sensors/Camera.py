import io
import time
import picamera
from threading import Thread
from PIL import Image


class Camera():
    def __init__(self):
        self.camera = picamera.PiCamera()
        self.camera.raw_format = 'yuv'
        self.camera.resolution = (1440, 1080)
        self.stream = io.BytesIO()
        self.thread = None

    def set_resolution(self, resolution):
        self.camera.resolution = resolution

    def take_picture(self):
        time_begin = time.time()
        self.stream.seek(0)
        self.camera.capture(self.stream, format='jpeg', use_video_port=True)
        self.stream.seek(0)
        image = Image.open(self.stream)
        print "take_picture: ", time.time()-time_begin
        return image

    def capture_video(self, callback):
        if self.thread:
            return
        """self.callback = callback
        rec_func = self._record
        rec_callback = self.on_recording_finished
        self.thread = Thread(target=rec_func, args=(rec_callback,))
        self.thread.start()"""
        self._record(callback)

    def _record(self, callback):
        file_name = 'video.h264'
        self.camera.resolution = (1024, 768)
        self.camera.start_recording(file_name)
        time.sleep(5)
        self.camera.stop_recording()

        callback(file_name)

    def on_recording_finished(self, file_name):
        self.callback(file_name)
        self.thread = None


__camera = Camera()


def get_camera():
    return __camera
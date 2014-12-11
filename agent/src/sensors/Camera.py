import io
import time
import picamera
from PIL import Image


class Camera():
    def __init__(self):
        self.camera = picamera.PiCamera()
        self.camera.raw_format = 'yuv'
        self.camera.resolution = (1440, 1080)
        self.stream = io.BytesIO()

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


__camera = Camera()


def get_camera():
    return __camera
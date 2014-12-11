import io
import time
import picamera
from PIL import Image


class Camera():
    def __init__(self):
        pass

    def take_picture(self, resolution=None):
        time_begin = time.time()
        image = None
        with picamera.PiCamera() as camera:
            stream = io.BytesIO()
            if resolution:
                camera.resolution = resolution
            camera.capture(stream, format='jpeg')
            stream.seek(0)
            image = Image.open(stream)
        print "take_picture: ", time.time()-time_begin
        return image


__camera = Camera()


def get_camera():
    return __camera
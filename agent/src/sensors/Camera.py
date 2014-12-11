import io
import time
import picamera
from PIL import Image

class Camera():
    def take_picture(self):
        image = None
        with picamera.PiCamera() as camera:
            stream = io.BytesIO()
            camera.start_preview()
            camera.capture(stream, format='jpeg')
            stream.seek(0)
            image = Image.open(stream)
        return image

__camera = Camera()

def get_camera():
    return __camera
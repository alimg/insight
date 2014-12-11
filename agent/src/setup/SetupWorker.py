import time
from qrread.QRDecoder import QRDecoder
from sensors import Camera

class SetupWorker():

    def start_setup(self):
        setup_complete = False
        camera = Camera.get_camera()
        decoder = QRDecoder()
        while not setup_complete:
            img = camera.take_picture(resolution=(800, 600))
            qr_data = decoder.decode_image(img)
            if not qr_data:
                print "Setup: QR not detected retrying"
                time.sleep(1)
                continue
            print qr_data
            time.sleep(1)



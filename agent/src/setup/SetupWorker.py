import time
from qrread.QRDecoder import QRDecoder
from sensors import Camera
import WifiUtil

def parse_qr_data(qr_data):
    ar = qr_data.split('\n')
    if len(ar) <3:
        return None
    return {'ssid': ar[0], 'pass': ar[1], 'data': ar[2:]}


class SetupWorker():
    def __init__(self, agentConfig):
        self.agentConfig = agentConfig

    def start_setup(self):
        setup_complete = False
        camera = Camera.get_camera()
        decoder = QRDecoder()
        camera.set_resolution((800, 800))
        while not setup_complete:
            img = camera.take_picture().convert('L')
            img.save("out.jpeg", "jpeg")
            qr_data = decoder.decode_image(img)
            if not qr_data:
                print "Setup: QR not detected retrying"
                # time.sleep(1)
                continue
            print qr_data

            qr_config = parse_qr_data(qr_data)
            if not qr_config:
                continue
            WifiUtil.setup_interface(qr_config['ssid'], qr_config['pass'])
            self.agentConfig.save_user_conf(qr_config)
            break



import time
from qrread.QRDecoder import QRDecoder
from sensors import Camera
import WifiUtil

def parse_qr_data(qr_data):
    ar = qr_data.split('\n')
    if len(ar) <4:
        return None
    return {'SSID': ar[0], 'pass': ar[1], 'encryption': ar[2], 'data': ar[3:]}


class SetupWorker():
    def __init__(self, agent_config, led_controller, timeout=0):
        self.agentConfig = agent_config
        self.led_controller = led_controller
        self.timeout = timeout

    def start_setup(self):
        setup_complete = False
        camera = Camera.get_camera()
        decoder = QRDecoder()
        camera.set_resolution((800, 800))
        num_trials = self.timeout
        if num_trials == 0:
            num_trials = None
        while not setup_complete and (num_trials is None or (num_trials and num_trials > 0)):
            self.led_controller("setup")
            if num_trials and num_trials > 0:
                num_trials -= 1
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
            self.led_controller("config_read")
            if WifiUtil.setup_interface(qr_config['SSID'], qr_config['pass'], qr_config['encryption']) == 0:
                setup_complete = True
                self.agentConfig.save_user_conf(qr_config)


    def _validate_config(self, qr_config):
        # wait until wlan0 got an ip
        for j in range(2):
            for i in range(10):
                time.sleep(3)
                ip = WifiUtil.get_wlan0_ip()
                if ip:
                    setup_complete = True
                    self.agentConfig.save_user_conf(qr_config)
                    break
            if setup_complete:
                break
            WifiUtil.reassociate()



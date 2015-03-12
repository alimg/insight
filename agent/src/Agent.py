import json

import UploadService
import HwController
import CommandClient
import AgentConfig
import EncryptionUtil
from setup import SetupWorker
from setup import WifiUtil


class ConnectionStateListener():
    def __init__(self, agent):
        self.agent = agent

    def connected(self):
        self.agent.hwController.set_led_status("online")

    def disconnected(self):
        self.agent.hwController.set_led_status("offline")


class Agent:
    def __init__(self, args):
        self.args = args
        self.agentConfig = AgentConfig.AgentConfig()
        self.agentConfig.load_config()
        EncryptionUtil.set_key(self.agentConfig.get_device_key())
        self.hwController = HwController.HwController(
            camera_event_handler=lambda event: self.uploadService.upload_photo(event),
            video_event_handler=lambda event: self.uploadService.upload_video(event),
            audio_event_handler=lambda event: self.uploadService.upload_audio(event))
        self.commandClient = CommandClient.CommandClient()
        self.commandClient.connection_listener = ConnectionStateListener(self)
        self.uploadService = UploadService.UploadService(self.agentConfig.get_device_id())
        self.running = True
        self.pir_enabled = True

    def run(self):
        user_conf = self.agentConfig.get_user_conf()
        local_ip = WifiUtil.get_eth0_ip()
        if not local_ip or local_ip == "":
            local_ip = WifiUtil.get_wlan0_ip()
        print "Local IP: ", local_ip
        print "User conf: ", user_conf

        #self.hwController.set_setup_button_listener(lambda: self.begin_setup())
        if not user_conf:
            self.hwController.set_led_status("setup")
            worker = SetupWorker.SetupWorker(self.agentConfig)
            worker.start_setup()
        if not local_ip or local_ip == "":
            self.hwController.set_led_status("offline")
        else:
            self.hwController.set_led_status("online")

        self.commandClient.add_command_handler(lambda command: self.process_command(command))

        self.commandClient.start()
        self.hwController.start()
        self.uploadService.start()

    def stop(self):
        self.running = False
        self.commandClient.stop()
        self.uploadService.stop()
        self.hwController.stop()

    def wait(self):
        while self.running:
            self.uploadService.join(1000)
            self.hwController.join(1000)
            self.commandClient.join(1000)

    def process_command(self, data):
        command = json.loads(data)
        if command["action"] == 'get_device_id':
            self.commandClient.send_message({"action": "device_id", "value": self.agentConfig.get_device_id()})
        elif command["action"] == 'pong':
            print "pong"
        elif command["action"] == 'config_change' or command["action"] == 'config':
            print command
            self.pir_enabled = command["alarm_threshold"] < 1
            self.hwController.pir_enabled = self.pir_enabled
        else:
            self.hwController.process_command(command)

import UploadService, HwController, CommandClient, AgentConfig
from setup import SetupWorker
from setup import WifiUtil
import json


class Agent:
    def __init__(self, args):
        self.args = args
        self.agentConfig = AgentConfig.AgentConfig()
        self.hwController = HwController.HwController(
            camera_event_handler=lambda event: self.uploadService.upload_photo(event),
            audio_event_handler=lambda event: self.uploadService.upload_audio(event))
        self.commandClient = CommandClient.CommandClient()
        self.uploadService = UploadService.UploadService()
        self.running = True

    def run(self):
        self.agentConfig.load_config()
        user_conf = self.agentConfig.get_user_conf()
        local_ip = WifiUtil.get_wlan0_ip()
        print "Local IP: ", local_ip
        print "User conf: ", user_conf
        if not user_conf:
            worker = SetupWorker.SetupWorker(self.agentConfig)
            worker.start_setup()

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
        else:
            self.hwController.process_command(command)

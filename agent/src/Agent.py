import UploadService, HwController, CommandClient, AgentConfig
from setup import SetupWorker

class Agent:
    def __init__(self, args):
        self.args = args
        self.agentConfig = AgentConfig.AgentConfig()
        self.hwController = HwController.HwController(lambda event: self.uploadService.upload_photo(event))
        self.commandClient = CommandClient.CommandClient()
        self.uploadService = UploadService.UploadService()
        self.running = True

    def run(self):
        userConf = self.agentConfig.get_user_conf()
        if not userConf:
            worker = SetupWorker.SetupWorker()
            worker.start_setup()

        self.commandClient.add_command_handler(lambda command: self.hwController.process_command(command))

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

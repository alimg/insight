import ConfigParser
DATA_STORAGE_DIR = "data/"
CONF_FILE = "conf/agent.conf"
DEVICE_CONF_FILE = "conf/device.conf"


class AgentConfig():
    def __init__(self):
        self.config = ConfigParser.ConfigParser()
        self.device_conf = ConfigParser.ConfigParser()

    def load_config(self):
        self.config.read(CONF_FILE)
        self.device_conf.read(DEVICE_CONF_FILE)

    def get_user_conf(self):
        if not self.config.has_section("User"):
            return None
        return self.config.items("User")

    def save_user_conf(self, user_conf):
        if not self.config.has_section("User"):
            self.config.add_section("User")

        for key in user_conf.keys():
            self.config.set('User', key, user_conf[key])
        with open(CONF_FILE, 'w') as configfile:
            self.config.write(configfile)

    def get_device_id(self):
        return self.device_conf.getint("Device", "id")

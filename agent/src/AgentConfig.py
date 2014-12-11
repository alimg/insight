import ConfigParser
DATA_STORAGE_DIR = "data/"
CONF_FILE = "conf/agent.conf"


class AgentConfig():
    def __init__(self):
        self.config = ConfigParser.ConfigParser()

    def load_config(self):
        self.config.read(CONF_FILE)

    def get_user_conf(self):
        return self.config["User"]

    def save_user_conf(self, user_conf):
        self.config["User"] = user_conf
        with open(CONF_FILE, 'w') as configfile:
            self.config.write(configfile)


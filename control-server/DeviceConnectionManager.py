import json
from webservice.api import DBUtil


class ConnectionHandler:
    def __init__(self):
        global INSTANCE
        INSTANCE = self
        self.online_devices = {}
        self.online_devices_rev = {}
        self.device_context = {}

    def on_connected(self, context, address):
        print "client connected", address
        context.send_message(json.dumps({'action': 'get_device_id'}))
        self.device_context[address] = context

    def on_receive(self, address, data):
        print "received message", address, data
        try:
            message = json.loads(data)
            if message['action'] == 'device_id':
                self.online_devices[message['value']] = address
                self.online_devices_rev[address] = message['value']
                DBUtil.update_device_address(message['value'], address[0])
        except Exception, e:
            print e

    def on_disconnected(self, address):
        print "client disconnected", address
        dev_id = self.online_devices_rev[address]
        self.online_devices.pop(dev_id, None)
        self.online_devices_rev.pop(address)
        DBUtil.update_device_address(dev_id, "")

    def get_device_ip(self, device_id):
        if device_id in self.online_devices:
            return self.online_devices[device_id]
        return None

    def get_device_context(self, device_address):
        return self.device_context[device_address]


INSTANCE = ConnectionHandler()


def get_instance():
    return INSTANCE
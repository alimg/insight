import json


class ConnectionHandler:

    def __init__(self):
        pass

    def on_connected(self, context, address):
        print "client connected", address
        context.send_command(json.dumps({'action': 'cap_photo'}))

    def on_receive(self, address, data):
        print "received message", address, data

    def on_disconnected(self, address):
        print "client disconnected", address

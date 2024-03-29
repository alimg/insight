from flask import Flask
from flask.ext import restful
from api import User
from api import Device
from api import Events
from flask import request
import os
from api import ServerConstants
from threading import Thread

class HelloWorld(restful.Resource):

    def get(self):
        return {'test': 'Hello World!'}

    def post(self):
        return self.get


class WebService:
    def __init__(self, device_command_listener):
        self.app = Flask(__name__)
        self.app.debug = True
        self.device_command_listener = device_command_listener
        ServerConstants.device_command_listener = device_command_listener
        api = restful.Api(self.app)

        api.add_resource(User.Login, '/login')
        api.add_resource(User.RegisterUser, '/register')
        api.add_resource(Device.RegisterInsight, '/register_insight')
        api.add_resource(Device.SendCommand, '/insight/command')
        api.add_resource(Device.DeviceInfo, '/insight/status')
        api.add_resource(Events.List, '/events/list')
        api.add_resource(Events.GetData, '/events/data')
        api.add_resource(User.ListInsight, '/insight_list')
        api.add_resource(HelloWorld, '/')  # to check if site is up

#        api.add_resource(CommandHandler, '/insight/send_command')
    def start(self):
        self.app.run(host='0.0.0.0', port=5000, debug=True, use_reloader=False)


"""
api.add_resource(User.Login, '/login')
api.add_resource(User.RegisterUser, '/register')
api.add_resource(Device.List, '/device/list')"""

# self.app.run(host='0.0.0.0', port=5000)

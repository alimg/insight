from flask import Flask
from flask.ext import restful
from api import User
from api import Device
from flask import request
import os
from api import ServerConstants
from threading import Thread


class WebService:
    def __init__(self, device_command_listener):
        self.app = Flask(__name__)
        self.app.debug = True
        self.device_command_listener = device_command_listener
        ServerConstants.device_command_listener = lambda (x, y): device_command_listener(x, y)
        api = restful.Api(self.app)

        api.add_resource(User.Login, '/login')
        api.add_resource(User.RegisterUser, '/register')
        api.add_resource(Device.RegisterInsight, '/register_insight')
        api.add_resource(Device.PullImage, '/insight/image')
        api.add_resource(Device.PullSound, '/insight/sound')

    def run(self):
        self.app.run(host='0.0.0.0', port=5000, debug=True, use_reloader=False)


"""
api.add_resource(User.Login, '/login')
api.add_resource(User.RegisterUser, '/register')
api.add_resource(Device.List, '/device/list')"""

# self.app.run(host='0.0.0.0', port=5000)

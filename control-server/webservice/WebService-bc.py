from flask import Flask
from flask.ext import restful
from flask import request

from api import User
from api import Device
from api.CommandHandler import CommandHandler

import os
import paho.mqtt.client as mqtt

class HelloWorld(restful.Resource):
	def get(self):
		return {'hello': 'ok-get'}

	def post(self):
		return {'hello': 'ok-post'}

	def put(self):
		return {'hello': 'put world'}

	def delete(self):
		return {'hello': 'delete world!'}

class WebService:
	def __init__(self):
		self.app = Flask(__name__)
		self.app.debug = True
		api = restful.Api(self.app)

		api.add_resource(User.Login, '/login')
		api.add_resource(User.RegisterUser, '/register')
		api.add_resource(User.ListInsight, '/insight_list')
		api.add_resource(HelloWorld, '/')
		api.add_resource(Device.RegisterInsight, '/register_insight')
		api.add_resource(Device.PullImage, '/insight/image')
		api.add_resource(Device.PullSound, '/insight/sound')
		api.add_resource(CommandHandler, '/insight/send_command')

	def run(self):
		self.app.run(host='0.0.0.0', port=5000)


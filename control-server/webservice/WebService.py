from flask import Flask
from flask.ext import restful
from api import User
from api import Device
from flask import request
import os
import paho.mqtt.client as mqtt

def send_message(topic="test_nar", message="1"):
	if len(topic) == 0:
		topic = "test_nar"
	os.system("mosquitto_pub -h iot.eclipse.org -t /nar/"+topic+" -m "+str(message))
	os.system("mosquitto_pub -h iot.eclipse.org -t /nar/android -m okay")

class HelloWorld(restful.Resource):
	def get(self):
		print [str(x) for x in request.form.values()]
		topic = "test_nar"
		if 'nar_id' in request.args.values():
			topic = request.args['nar_id']
		send_message(topic)
		return {'hello': 'ok-get-p'+topic}
#		topic = request.args['nar_id']
#		if len(topic) == 0:
#			topic = "test_nar"
#		os.system("mosquitto_pub -h iot.eclipse.org -t /nar/"+topic+" -m 1")
#		return {'hello': 'ok-get'}

	def post(self):
		print request.method
		if 'nar_id' in request.form:
			topic = request.form['nar_id']
		else:
			topic = "nar_ptest"
		send_message(topic)
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
		api.add_resource(HelloWorld, '/')
		api.add_resource(Device.RegisterInsight, '/register_insight')
		api.add_resource(Device.PullImage, '/insight/image')
		api.add_resource(Device.PullSound, '/insight/sound')

	def run(self):
		self.app.run(host='0.0.0.0', port=5000)


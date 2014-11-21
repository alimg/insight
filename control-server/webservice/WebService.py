from flask import Flask
from flask.ext import restful
from api import User


class HelloWorld(restful.Resource):
    def get(self):
        return {'hello': 'world'}
    
    def post(self):
        return {'hello': 'post world'}

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

    def run(self):
        self.app.run(host='0.0.0.0', port=5000)


from flask import Flask
from flask.ext import restful
from api import User


class HelloWorld(restful.Resource):
    def get(self):
        return {'hello': 'world'}


class WebService:
    def __init__(self):
        self.app = Flask(__name__)
        self.app.debug = True
        api = restful.Api(self.app)

        api.add_resource(User.Login, '/login')
        api.add_resource(User.RegisterUser, '/register')

    def run(self):
        self.app.run()


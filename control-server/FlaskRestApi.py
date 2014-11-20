#!/usr/bin/python2.7
from flask import Flask
from flask.ext import restful

app = Flask(__name__)
api = restful.Api(app)

class HelloWorld(restful.Resource):
    def get(self):
        return {'hello': 'world'}

class Test(restful.Resource):
	def get(self):
		return 'Tested'

api.add_resource(HelloWorld, '/')
api.add_resource(Test, '/hello')

if __name__ == '__main__':
    app.run(debug=True)

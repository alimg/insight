import MySQLdb
from flask.ext import restful
from flask import request

import DBUtil
import ServerConstants
import SessionUtil

db = MySQLdb.connect(host=ServerConstants.DB_ADDRESS,
                     user=ServerConstants.DB_USER,
                     passwd=ServerConstants.DB_PASSWORD,
                     db=ServerConstants.DB_NAME)

class List(restful.Resource):

    def post(self):
        session_token = request.form['session_token']
        userid = SessionUtil._SESSION[session_token]['user']

        return {'status': '0', 'events': [
            {'id': '1001', 'device_id':'1001', 'date':'2014-11-28 14:09:43', 'type':'unknown', 'priority':'0'}]}
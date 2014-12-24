import MySQLdb
from flask.ext import restful
from flask import request
from flask import make_response
from flask import send_file

from cStringIO import StringIO

from PIL import Image
from PIL import ImageFont
from PIL import ImageDraw

import DBUtil
import ServerConstants
import SessionUtil

db = MySQLdb.connect(host=ServerConstants.DB_ADDRESS,
                     user=ServerConstants.DB_USER,
                     passwd=ServerConstants.DB_PASSWORD,
                     db=ServerConstants.DB_NAME)

class RegisterInsight(restful.Resource):

    def post(self):
        iid = request.form['insight_id']
        uname = request.form['username']
        #in_pass = request.form['insight_pass']
        #password = DBUtil.hash_string(in_id +'|'+ in_pass)
        print iid, uname
        cursor = db.cursor()
        cursor.execute('SELECT id FROM users WHERE name=\'{}\''.format(uname))
        print cursor
        user = cursor.fetchone()
        if user is None or len(user) == 0:
            return {'status': ServerConstants.STATUS_ERROR}

        cursor = db.cursor()
        sql = 'INSERT INTO `device` (`id`, `userid`) ' \
                'VALUES (\'{}\', \'{}\')'.format(iid, user[0])
        print sql
        cursor.execute(sql)

        db.commit()

        return {'status': '0'}

class PullImage(restful.Resource):
    def post(self):
#        iid = request.form['insight_id']
 #       uid = request.form['username']
        act = request.form['act']
        print act
        if act == 'take':
            f=ServerConstants.device_command_listener
            f('{"action":"cap_photo"}')
            return {}
        else:
            output = StringIO()
            img = Image.open(ServerConstants.FILE)
            img.save(output, 'PNG')
            output.seek(0)
            return send_file(output, mimetype='image/png')



class PullSound(restful.Resource):
    def post(self):
        act = request.form['act']
        print act
        if act == 'take':
            f=ServerConstants.device_command_listener
            f('{"action":"cap_audio"}')
            return {}
        else:#ServerConstants.FILE
            return send_file("bell.mp3", mimetype='audio/mp3')

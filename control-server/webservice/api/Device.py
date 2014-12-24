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
        iid = request.form['insight_iid']
        email = request.form['insight_email']
        #in_pass = request.form['insight_pass']
        #password = DBUtil.hash_string(in_id +'|'+ in_pass)

        cursor = db.cursor()
        cursor.execute('SELECT id, email FROM users WHERE email=\'{}\''.format(email))

        user = cursor.fetchone()

        cursor = db.cursor()
        sql = 'INSERT INTO `device` (`id`, `userid`) ' \
                'VALUES (\'{}\', \'{}\')'.format(iid, user[0])
        print sql
        cursor.execute(sql)

        db.commit()

        return {'status': '0'}

class PullImage(restful.Resource):
    def post(self):
        iid = request.form['insight_iid']
        email = request.form['insight_email']
#		iid="TEST_INSIGHT"
#		email="mail@mail.com"

        img = Image.new("RGB", (512, 512), "white")
        draw = ImageDraw.Draw(img)
        # font = ImageFont.truetype(<font-file>, <font-size>)
        font = ImageFont.truetype("/root/insight/control-server/webservice/api/sans-serif.ttf", 48)
        # draw.text((x, y),"Sample Text",(r,g,b))
        draw.text((0,   0), "iid: "+iid, (0, 0, 0), font=font)
        draw.text((0, 128), "email: "+email, (0, 0, 0), font=font)
        output = StringIO()
        img.save(output, 'PNG')
        output.seek(0)
        return send_file(output, mimetype='image/png')



class PullSound(restful.Resource):
    def post(self):
        buf = StringIO()
        # read audio
        return send_file(buf, mimetype='sound/mp3')

"""
=======
                     user=ServerConstants.DB_USER,
                     passwd=ServerConstants.DB_PASSWORD,
                     db=ServerConstants.DB_NAME)

class List(restful.Resource):

    def post(self):
        session_token = request.form['session_token']
        userid = SessionUtil._SESSION[session_token]['user']

        return {'status': '0', 'devices': [{'id': '1001', 'name':'Device 1', 'status':'offline'}]}

class Command(restful.Resource):

    def post(self):
        session_token = request.form['session_token']
        userid = SessionUtil._SESSION[session_token]['user']
        device = request.form['device_id']

        command = request.form['command']

        listener = ServerConstants.device_command_listener
        if not listener:
            return {'status': '1'}

        listener(device, command)
        return {'status': '0'}
>>>>>>> Stashed changes
"""

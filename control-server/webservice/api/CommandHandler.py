import MySQLdb

import redis

from flask.ext import restful
from flask import request
from flask import make_response
from flask import send_file

import DBUtil
import ServerConstants
import SessionUtil

db = MySQLdb.connect(host=ServerConstants.DB_ADDRESS,
					 user=ServerConstants.DB_USER,
					 passwd=ServerConstants.DB_PASSWORD,
					 db=ServerConstants.DB_NAME)

r_mq = redis.Redis()

class CommandHandler(restful.Resource):

	def post(self):
		iid = request.form['insight_id']
		uname = request.form['username']
		cmd = request.form['command'];

		print "Command handler:", iid, uname
		cursor = db.cursor()
		cursor.execute("SELECT id FROM users WHERE name='{}'".format(uname))
		user = cursor.fetchone()
		if user is None or len(user) == 0:
			return {'status': ServerConstants.STATUS_ERROR}

		cursor = db.cursor()
		cursor.execute("SELECT * FROM device WHERE id='{}' AND userid='{}'".format(iid, user[0]))
		device = cursor.fetchone()
		if device is None or len(device) == 0:
			return {'status': ServerConstants.STATUS_ERROR}

		r_mq.rpush('command', {'inisight_id':iid, 'command':cmd})

		return {'status': '0'}

class PullImage(restful.Resource):
	def post(self):
		iid = request.form['insight_id']
		uid = request.form['username']
#		iid="TEST_INSIGHT"
#		email="mail@mail.com"

		img = Image.new("RGB", (512, 512), "white")
		draw = ImageDraw.Draw(img)
		# font = ImageFont.truetype(<font-file>, <font-size>)
		font = ImageFont.truetype("/root/insight/control-server/webservice/api/sans-serif.ttf", 48)
		# draw.text((x, y),"Sample Text",(r,g,b))
		draw.text((0,   0), "iid: "+iid, (0, 0, 0), font=font)
		draw.text((0, 128), "uid: "+uid, (0, 0, 0), font=font)
		output = StringIO()
		img.save(output, 'PNG')
		output.seek(0)
		return send_file(output, mimetype='image/png')



class PullSound(restful.Resource):
	def post(self):
		buf = StringIO()
		# read audio
		return send_file(buf, mimetype='sound/mp3')

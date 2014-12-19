import MySQLdb
from flask.ext import restful
from flask import request
from cStringIO import StringIO
from flask import make_response
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
#		iid = request.form['insight_iid']
#		email = request.form['insight_email']
		iid="TEST_INSIGHT"
		email="mail@mail.com"

		img = Image.new("RGB", (512, 512), "white")
		draw = ImageDraw.Draw(img)
		# font = ImageFont.truetype(<font-file>, <font-size>)
		font = ImageFont.truetype("sans-serif.ttf", 48)
		# draw.text((x, y),"Sample Text",(r,g,b))
		draw.text((0,   0), "iid: "+iid, (0, 0, 0), font=font)
		draw.text((0, 128), "email: "+email, (0, 0, 0), font=font)
		output = StringIO()
		img.save(output, 'PNG')
		contents = output.getvalue()
		output.close()
		response = make_response(contents)
		response.headers['Content-Type'] = 'image/png'
		response.headers['Content-Disposition'] = 'attachment; filename=img.png'



class PullSound(restful.Resource):
	def post(self):
		buf = StringIO()
		# read audio
		response = make_response(buf.getvalue())
		buf.close()
		response.headers['Content-Type'] = 'audio/wav'
		response.headers['Content-Disposition'] = 'attachment; filename=sound.wav'
		return response

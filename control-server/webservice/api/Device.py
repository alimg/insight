from contextlib import closing
from cStringIO import StringIO

from flask.ext import restful

from flask import request
from flask import send_file
from PIL import Image

import ServerConstants
from CommandServer import DeviceNotOnlineException


class RegisterInsight(restful.Resource):
    def post(self):
        iid = request.form['insight_id']
        uname = request.form['username']
        # in_pass = request.form['insight_pass']
        #password = DBUtil.hash_string(in_id +'|'+ in_pass)
        print iid, uname
        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                cursor.execute('SELECT id FROM users WHERE name=\'{}\''.format(uname))
                user = cursor.fetchone()
                print cursor.fetchall()
                if user is None or len(user) == 0:
                    return {'status': ServerConstants.STATUS_ERROR}
                sql = 'INSERT INTO `device` (`id`, `userid`) ' \
                      'VALUES (\'{}\', \'{}\')'.format(iid, user[0])
                print sql
                cursor.execute(sql)
                db.commit()

        return {'status': '0'}


class PullImage(restful.Resource):
    def post(self):
        iid = request.form['insight_id']
        #uid = request.form['username']
        act = request.form['act']
        print act
        if act == 'take':
            try:
                ServerConstants.device_command_listener(iid, '{"action":"cap_photo"}')
            except DeviceNotOnlineException:
                return {"status": ServerConstants.STATUS_DEVICE_OFFLINE}
            return {'status': '0'}
        else:
            output = StringIO()
            img = Image.open(ServerConstants.FILE)
            img.save(output, 'PNG')
            output.seek(0)
            return send_file(output, mimetype='image/png')


class PullSound(restful.Resource):
    def post(self):
        iid = request.form['insight_id']
        act = request.form['act']
        print act
        if act == 'take':
            try:
                ServerConstants.device_command_listener(iid, '{"action":"cap_audio"}')
            except DeviceNotOnlineException:
                return {"status": ServerConstants.STATUS_DEVICE_OFFLINE}
            return {'status': '0'}
        else:  # ServerConstants.FILE
            return send_file("bell.mp3", mimetype='audio/mp3')

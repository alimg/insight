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
        print "RegIns:", iid, uname
        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor(buffered=True)) as cursor:
                cursor.execute('SELECT id FROM users WHERE name=\'{}\''.format(uname))
                if cursor.rowcount != 1:
                    return {'status': ServerConstants.STATUS_ERROR}
                user = cursor.fetchone()

                cursor.execute("INSERT INTO `insight`.`devices` (`id`, `name`, `last_response`) VALUES (NULL, '{}', CURRENT_TIMESTAMP);".format(iid))
                db.commit()

                cursor.execute("SELECT id FROM `devices` WHERE name='{}'".format(iid))
                insight = cursor.fetchone();
                cursor.fetchall()
                sql = 'INSERT INTO `registered_devices` (`device_id`, `user_id`) ' \
                      'VALUES (\'{}\', \'{}\')'.format(insight[0], user[0])
                print sql
                cursor.execute(sql)

        return {'status': '0'}


class PullImage(restful.Resource):
    def post(self):
        iid = request.form['insight_id']
        act = request.form['act']
        print act, iid
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
            return send_file(output, mimetype='image/jpeg')


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

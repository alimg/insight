from contextlib import closing
from cStringIO import StringIO

from flask.ext import restful

from flask import request
from flask import send_file
from PIL import Image

import ServerConstants
import SessionUtil
from CommandServer import DeviceNotOnlineException


class RegisterInsight(restful.Resource):
    def post(self):
        user = SessionUtil.get_user_id(request.form['session'])
        if not user:
            return {'status': ServerConstants.STATUS_INVALID_SESSION}
        iid = request.form['insight_id']

        print "RegIns:", iid, user
        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor(buffered=True)) as cursor:
                cursor.execute("SELECT userid FROM `devices` WHERE id='{}'".format(iid))
                if cursor.fetchall()[0][0]:
                    return {'status': ServerConstants.STATUS_ERROR, 'message': 'device already has an owner'}
                sql = "UPDATE `devices` SET `userid`='{}' WHERE id={}".format(user, iid)
                cursor.execute(sql)
                db.commit()

        return {'status': '0'}


class DeviceInfo(restful.Resource):
    def post(self):
        user = SessionUtil.get_user_id(request.form['session'])
        if not user:
            return {'status': ServerConstants.STATUS_INVALID_SESSION}
        iid = request.form['insight_id']

        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor(buffered=True)) as cursor:
                sql = "SELECT address, last_response FROM `devices` WHERE id='{}'".format(iid)
                cursor.execute(sql)
                result = cursor.fetchall()
                if result:
                    result = result[0]
                    return {"status": '0', "address": str(result[0]),
                            "lastResponse": str(result[1])}
        return {"status": ServerConstants.STATUS_ERROR}


def get_latest_event(iid, ftype='jpeg'):
    with closing(ServerConstants.mysql_pool.get_connection()) as db:
        with closing(db.cursor(buffered=True)) as cursor:
            # sql = "SELECT `date`, `filename` FROM `events` WHERE deviceid='{}' AND type='{}'".format(iid, ftype)
            # ignores iid for demo
            sql = "SELECT `date`, `filename` FROM `events` WHERE type='{}', deviceid='{}'".format(ftype, iid)
            print(sql)
            cursor.execute(sql)
            events = cursor.fetchall()
            if cursor.rowcount != 1:
                return {'status': ServerConstants.STATUS_ERROR}
            return events[0]


class SendCommand(restful.Resource):
    def post(self):
        user = SessionUtil.get_user_id(request.form['session'])
        if not user:
            return {'status': ServerConstants.STATUS_INVALID_SESSION}
        iid = request.form['insight_id']
        req_type = request.form['type']

        try:
            if req_type == "photo":
                ServerConstants.device_command_listener(iid, {"action": "cap_photo"})
            elif req_type == "audio":
                ServerConstants.device_command_listener(iid, {"action": "cap_audio"})
            elif req_type == "video":
                ServerConstants.device_command_listener(iid, {"action": "cap_video"})
            elif req_type == "setSystemEnabled":
                enabled = 'true' == request.form['true']
                ServerConstants.device_command_listener(iid, {"action": "config_change", "system_enabled": enabled})
        except DeviceNotOnlineException:
            return {"status": ServerConstants.STATUS_DEVICE_OFFLINE}
        return {'status': '0'}


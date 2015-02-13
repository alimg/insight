from contextlib import closing
from flask.ext import restful
from flask import request

import DBUtil
import ServerConstants
import SessionUtil


class List(restful.Resource):
    def post(self):
        userid = SessionUtil.get_user_id(request.form['session'])
        if not userid:
            return {'status': ServerConstants.STATUS_INVALID_SESSION}
        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                sql = "SELECT id, deviceid, date, type, filename  FROM `events` WHERE userid='{}'".format(userid)
                cursor.execute(sql)
                # events = [{'id': '1001', 'device_id':'1001', 'date':'2014-11-28 14:09:43', 'type':'unknown', 'priority':'0'}]
                events = []
                rows = cursor.fetchall()
                print "list devices: ", rows
                for row in rows:
                    events.append(
                        {'id': row[0],
                         'device_id': row[1],
                         'date': row[2],
                         'type': row[3],
                         'filename': row[4]}
                    )
                return {'status': '0', 'events': events}
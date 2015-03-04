from contextlib import closing
from flask.ext import restful
from flask import request
from flask import send_file
from cStringIO import StringIO
from PIL import Image

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
                sql = "SELECT id, deviceid, date, type, filename, encryption FROM `events` WHERE userid='{}' " \
                      " ORDER BY id DESC limit 0, 30".format(userid)
                cursor.execute(sql)
                events = []
                rows = cursor.fetchall()
                for row in rows:
                    events.append(
                        {'id': row[0],
                         'deviceid': row[1],
                         'date': str(row[2]),
                         'type': row[3],
                         'filename': row[4],
                         'encryption': row[5]}
                    )
                return {'status': '0', 'events': events}


class GetData(restful.Resource):
    def post(self):
        userid = SessionUtil.get_user_id(request.form['session'])
        if not userid:
            return {'status': ServerConstants.STATUS_INVALID_SESSION}

        eventid = request.form['eventid']

        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                sql = "SELECT filename, type FROM `events` WHERE id='{}'".format(eventid)
                cursor.execute(sql)
                rows = cursor.fetchall()
                if rows:
                    filename = ServerConstants.STORAGE_DIR+rows[0][0]
                    event_type = rows[0][1]
                    if event_type == 'jpeg':
                        #output = StringIO()
                        #img = Image.open(filename)
                        #img.save(output, 'JPEG')
                        #output.seek(0)
                        #return send_file(output, mimetype='image/jpeg')
                        return send_file(filename, mimetype='image/jpeg')
                    elif event_type == 'ogg':
                        return send_file(filename, mimetype='audio/ogg')
                    elif event_type == 'h264':
                        return send_file(filename, mimetype='video/h264')

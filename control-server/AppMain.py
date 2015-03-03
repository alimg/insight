from contextlib import closing
from UploadServer import UploadServer
from CommandServer import CommandServer
from webservice import WebService
from webservice.api import ServerConstants
from webservice.api import ParseUtil

import datetime
import json

class AppMain:
    def __init__(self):
        HOST = "0.0.0.0"
        UPLOAD_SERVER_PORT = 5014
        COMMAND_SERVER_PORT = 5013
        self.command_server = CommandServer((HOST, COMMAND_SERVER_PORT))
        self.upload_server = UploadServer((HOST, UPLOAD_SERVER_PORT),
                                          lambda meta_data, file_name: self.on_file_uploaded(meta_data, file_name))
        self.web_service = WebService.WebService(
            lambda device, command: self.command_server.send_command(device, command))
        self.running = True

    def start(self):
        self.command_server.start()
        self.upload_server.start()
        self.web_service.start()

    def stop(self):
        self.running = False
        self.command_server.stop()
        self.upload_server.stop()

    def wait(self):
        while self.running:
            self.command_server.join(1000)
            self.upload_server.join(1000)

    def on_file_uploaded(self, meta_data, file_name):
        with closing(ServerConstants.mysql_pool.get_connection()) as db:
            with closing(db.cursor()) as cursor:
                user_id = ""
                sql = 'SELECT userid FROM devices WHERE id=\'{}\''.format(meta_data["device"])
                cursor.execute(sql)
                rows = cursor.fetchall()
                if rows:
                    user_id = rows[0][0]

                event_time = datetime.datetime.fromtimestamp(int(meta_data["date"]))
                sql = 'INSERT INTO `events` (`id`,`deviceid`,`userid`,`date`,`type`,`data`,`filename`, `encryption`) ' \
                      'VALUES (\'\', \'{}\', \'{}\', \'{}\', \'{}\', \'{}\', \'{}\', \'{}\')'.format(
                            meta_data["device"],
                            user_id,
                            str(event_time),
                            meta_data["type"],
                            "",
                            file_name,
                            meta_data["encryption"])
                cursor.execute(sql)
                event_id = cursor.lastrowid
                db.commit()

                ParseUtil.send_push(str(user_id), json.dumps({"eventId": event_id,
                                                              "type": meta_data["type"],
                                                              "date": str(event_time)}))



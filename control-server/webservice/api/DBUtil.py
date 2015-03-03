import hashlib
from contextlib import closing
import datetime
import ServerConstants


def hash_string(string):
    return hashlib.sha512(string).hexdigest()


def update_device_address(dev_id, address):
    with closing(ServerConstants.mysql_pool.get_connection()) as db:
        with closing(db.cursor(buffered=True)) as cursor:
            sql = "UPDATE `devices` SET `last_response`='{}', `address`='{}' WHERE id={}".format(datetime.datetime.now(), address, dev_id)
            cursor.execute(sql)
            db.commit()


def update_device_last_response(dev_id):
    with closing(ServerConstants.mysql_pool.get_connection()) as db:
        with closing(db.cursor(buffered=True)) as cursor:
            sql = "UPDATE `devices` SET `last_response`='{}' WHERE id={}".format(datetime.datetime.now(), dev_id)
            cursor.execute(sql)
            db.commit()
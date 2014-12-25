STATUS_SUCCESS = '0'
STATUS_ERROR = '1'
STATUS_ALREADY_EXISTS = '2'

DB_NAME = 'insight'
DB_USER = 'root'
DB_PASSWORD = 'root'
DB_ADDRESS = 'localhost'

STORAGE_DIR = '/srv/data'

device_command_listener = None
FILE = '/root/out'

dbconfig = {
    "database": DB_NAME,
    "user": DB_USER,
    "password": DB_PASSWORD,
    "host": DB_ADDRESS
}

mysql_pool = None

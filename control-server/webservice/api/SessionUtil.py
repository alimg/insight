import time
import uuid
_SESSION={}

def generate_token(userid):
    token = str(uuid.uuid4())
    _SESSION[token] = {'user': userid, 'time': time.time()}
    return token



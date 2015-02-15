import time
import uuid

_SESSION = {}


def generate_token(userid):
    token = str(uuid.uuid4())
    _SESSION[token] = {'user': userid, 'time': time.time()}
    return token


def get_user_id(session_token):
    if session_token in _SESSION:
        return _SESSION[session_token]['user']
    return None


def is_valid(session_token):
    if session_token in _SESSION:
        return True
    return False
import hashlib


def hash_string(string):
    return hashlib.sha512(string).hexdigest()

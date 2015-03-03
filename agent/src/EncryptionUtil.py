from Crypto.Cipher import AES
_key = None


def encrypt_file(file_name):
    global _key
    cipher = AES.new('This is a key123', AES.MODE_CBC, 16 * '\x00')
    cipher.encrypt()

def set_key(key):
    global _key
    _key = key
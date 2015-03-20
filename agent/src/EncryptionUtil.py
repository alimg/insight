import os
import random
import struct

from Crypto.Cipher import AES

_key = None


def encrypt_file(file_name):
    global _key
    out_filename = file_name + '.enc'
    chunksize=64*1024
    #iv = ''.join(chr(random.randint(0, 0xFF)) for i in range(16))
    iv = ''.join(chr(0) for i in range(16))
    encryptor = AES.new(_key, AES.MODE_CBC, iv)
    filesize = os.path.getsize(file_name)

    with open(file_name, 'rb') as infile:
        with open(out_filename, 'wb') as outfile:
            outfile.write(struct.pack('<Q', filesize))
            outfile.write(iv)

            while True:
                chunk = infile.read(chunksize)
                if len(chunk) == 0:
                    break
                elif len(chunk) % 16 != 0:
                    chunk += ' ' * (16 - len(chunk) % 16)

                outfile.write(encryptor.encrypt(chunk))
    return out_filename


def set_key(key):
    global _key
    _key = key

import sys

from qrread import QRDecoder


decoder = QRDecoder.QRDecoder()

img = "../data/qrcode.png"	
if len(sys.argv)>1:
	img = sys.argv[1]
print decoder.decode_image_file(img)

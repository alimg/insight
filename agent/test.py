from qrread import QRDecoder

decoder = QRDecoder.QRDecoder()

print decoder.decode_image_file("data/qrcode.png")
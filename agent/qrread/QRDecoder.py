import zbar
from PIL import Image


class QRDecoder:

    def __init__(self):
        self.scanner = zbar.ImageScanner()
        # configure the reader
        self.scanner.parse_config('enable')

    def decode_image_file(self, img_file):
        pil = Image.open(img_file).convert('L')
        width, height = pil.size
        raw = pil.tostring()

        return self.decode_raw(raw, width, height)

    def decode_raw(self, raw, width, height):
        image = zbar.Image(width, height, 'Y800', raw)

        self.scanner.scan(image)

        for symbol in image:
            if symbol.type == zbar.Symbol.QRCODE:
                return symbol.data
        return None



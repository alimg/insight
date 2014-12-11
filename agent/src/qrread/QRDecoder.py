import zbar
from PIL import Image


class QRDecoder:

    def __init__(self):
        self.scanner = zbar.ImageScanner()
        # configure the reader
        self.scanner.parse_config('enable')

    def decode_image_file(self, img_file):
        pil = Image.open(img_file).convert('L')
        return self.decode_image(pil)

    def decode_image(self, pil):
        pil.thumbnail((400, 400), Image.ANTIALIAS)
        width, height = pil.size
        print pil.size
        #pil.save("small.jpeg")
        raw = pil.tostring()
        return self.decode_raw(raw, width, height)

    def decode_raw(self, raw, width, height):
        image = zbar.Image(width, height, 'Y800', raw)

        self.scanner.scan(image)

        for symbol in image:
            if symbol.type == zbar.Symbol.QRCODE:
                return symbol.data
            else:
                print symbol
        return None




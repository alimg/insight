#!/usr/bin/python
from sys import argv
import zbar
import Image
from cv2 import *

TEST_IMG = "test.jpg"

# initialize the camera
cam = VideoCapture(0)   # 0 -> index of camera
s, img = cam.read()
if s:    # frame captured without any errors
#	namedWindow("cam-test",CV_WINDOW_AUTOSIZE)
#	imshow("cam-test",img)
#	waitKey(0)
#	destroyWindow("cam-test")
	imwrite(TEST_IMG,img) #save image
else:
	print "Couldn't get image from cam"
	exit(1)
print type(img)
# create a reader
scanner = zbar.ImageScanner()

# configure the reader
scanner.parse_config('enable')

# obtain image data
pil = Image.open(TEST_IMG).convert('L')
print type(pil)

#pil = Image.open(argv[1]).convert('L')
width, height = pil.size
raw = pil.tostring()

# wrap image data
image = zbar.Image(width, height, 'Y800', raw)

# scan the image for barcodes
scanner.scan(image)

# extract results
for symbol in image:
# do something useful with results
	print 'decoded', symbol.type, 'symbol', '"%s"' % symbol.data

# clean up
del(image)

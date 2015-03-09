import RPi.GPIO as GPIO
from GPIOInterface import *

GPIO.setmode(GPIO.BOARD)
GPIO.setup(PIN_PIR, GPIO.IN)
GPIO.setup(PIN_IR, GPIO.OUT)

GPIO.setup(PIN_LED_R, GPIO.OUT)
GPIO.setup(PIN_LED_G, GPIO.OUT)
GPIO.setup(PIN_LED_B, GPIO.OUT)

GPIO.output(PIN_LED_R, 0)
GPIO.output(PIN_LED_G, 0)
GPIO.output(PIN_LED_B, 0)
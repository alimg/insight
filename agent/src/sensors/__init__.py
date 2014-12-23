import RPi.GPIO as GPIO
from GPIOInterface import *

GPIO.setmode(GPIO.BCM)
GPIO.setup(PIN_PIR, GPIO.IN)
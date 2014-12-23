from Sensor import Sensor
from GPIOInterface import *
import RPi.GPIO as GPIO


class PIRSensor(Sensor):
    def __init__(self, callback):
        Sensor.__init__(self)
        self.callback = callback
        GPIO.add_event_detect(PIN_PIR, GPIO.RISING, callback=lambda channel: self.trigger(channel))

    def trigger(self, channel):
        if GPIO.input(PIN_PIR):
            self.callback()



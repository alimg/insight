import GPIOInterface
import RPi.GPIO as GPIO


class IRController():

    def set_state(self, state):
        try:
            GPIO.output(GPIOInterface.PIN_IR, state)
        except Exception, e:
            print "Error set_RGB: ", e

    def enable(self):
        self.set_state(False)

    def disable(self):
        self.set_state(True)
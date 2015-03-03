import GPIOInterface
import RPi.GPIO as GPIO


class LedController():
    def set_RGB(self, r, g, b):
        GPIO.output(GPIOInterface.PIN_LED_R, r)
        GPIO.output(GPIOInterface.PIN_LED_G, g)
        GPIO.output(GPIOInterface.PIN_LED_B, b)

    def set_status(self, status):
        if status == "online":
            self.set_RGB(0, 1, 0)
        if status == "offline":
            self.set_RGB(1, 0, 0)
        elif status == "setup":
            self.set_RGB(0, 0, 1)
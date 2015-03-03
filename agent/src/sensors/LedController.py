import RPi.GPIO as GPIO


class LedController():
    def set_RGB(self, r, g, b):
        GPIO.output(11, r)
        GPIO.output(13, g)
        GPIO.output(15, b)

    def set_status(self, status):
        if status == "online":
            self.set_RGB(0, 1, 0)
        if status == "offline":
            self.set_RGB(1, 0, 0)
        elif status == "setup":
            self.set_RGB(0, 0, 1)
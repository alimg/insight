from threading import Thread
import time


class DaemonThread(Thread):
    def __init__(self):
        super(DaemonThread, self).__init__()
        self.running = True
        self.daemon = True

    def run(self):
        while self.running:
            try:
                self._run()
            except Exception, e:
                print "%s: Caught exception restarting in 5 seconds: %s" % (self.name, e)
                time.sleep(5)

    def _run(self):
        pass
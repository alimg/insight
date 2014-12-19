import signal
import os
from AppMain import AppMain


def signal_handler(signum, frame):
    print "stopping"
    #os.kill(os.getpid(), signal.SIGHUP)
    app.stop()

app = AppMain()
app.start()

signal.signal(signal.SIGINT, signal_handler)

app.wait()

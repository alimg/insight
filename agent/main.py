import signal
import os
from Agent import Agent


def signal_handler(signum, frame):
    print "stopping"
    os.kill(os.getpid(), signal.SIGHUP)
    agent.stop()

agent = Agent([])
signal.signal(signal.SIGINT, signal_handler)

agent.run()

agent.wait()


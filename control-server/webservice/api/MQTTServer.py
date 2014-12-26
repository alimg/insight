import paho.mqtt.client as mqtt

# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    print "Connected with result code "+str(rc)
    print client.ftype
    client.publish()

# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    print userdata, msg
    print msg.topic+" "+str(msg.payload)
    if msg.topic == 'android':
        if msg.body == 'take_photo':
            pass


# Blocking call that processes network traffic, dispatches callbacks and
# handles reconnecting.
# Other loop*() functions are available that give a threaded interface and a
# manual interface.
client.loop_forever()

class MQTTSendMessage(object):

    def __init__(self, ftype):
        client = mqtt.Client()
        client.data = ftype;
        client.on_connect = on_connect
        client.on_message = on_message

        client.connect("iot.eclipse.org", 1883, 60)


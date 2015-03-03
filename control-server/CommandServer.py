from threading import Thread
import SocketServer
import DeviceConnctionManager


class DeviceNotOnlineException(Exception):
    pass


class CommandServer(Thread, SocketServer.TCPServer):
    def __init__(self, address):
        super(CommandServer, self).__init__()
        self.daemon = True
        SocketServer.TCPServer.allow_reuse_address = True
        SocketServer.TCPServer.__init__(self, address, ClientConnectionHandler)
        self.devices_manager = DeviceConnctionManager.get_instance()

    def run(self):
        self.serve_forever()

    def stop(self):
        self.shutdown()

    def get_connection_handler(self):
        return self.devices_manager

    def send_command(self, device, command):
        print "Send command ", device, " ", command
        ip = self.devices_manager.get_device_ip(device)
        if ip:
            self.devices_manager.get_device_context(ip).send_message(command)
        else:
            raise DeviceNotOnlineException("Device %s is not online" % device)


class ClientConnectionHandler(SocketServer.BaseRequestHandler):
    def __init__(self, request, client_address, server):
        SocketServer.BaseRequestHandler.__init__(self, request, client_address, server)
        self.running = True

    def setup(self):
        self.running = True
        return SocketServer.BaseRequestHandler.setup(self)

    def handle(self):
        self.server.client_connection_handler.on_connected(self, self.client_address)
        while self.running:
            data = self.request.recv(1024)
            if not data:
                break
            self.server.client_connection_handler.on_receive(self.client_address, data)
        self.server.client_connection_handler.on_disconnected(self.client_address)
        self.request.close()

    def send_message(self, data):
        self.request.sendall(data)

    def finish(self):
        self.running = False
        print "finish", self.client_address
        return SocketServer.BaseRequestHandler.finish(self)

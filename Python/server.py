import SocketServer
import serial
import time

from BaseHTTPServer import BaseHTTPRequestHandler

num = 0

try:
    ser = serial.Serial('/dev/ttyUSB' + str(num), timeout=10)
except serial.serialutil.SerialException:
    num = (num + 1) % 2
    ser = serial.Serial('/dev/ttyUSB' + str(num), timeout=10)
ser.baudrate = 115200

class MyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.executeCommand()
        time.sleep(1)
    
    def executeCommand(self):
        try:
            ser.write(self.path[1:])
        except serial.serialutil.SerialException:
            num = (num + 1) % 2
            ser = serial.Serial('/dev/ttyUSB' + str(num), timeout=10)
            self.executeCommand()

httpd = SocketServer.TCPServer(("", 8080), MyHandler)
try:
    httpd.serve_forever()
except KeyboardInterrupt:
    pass
httpd.server_close()
ser.close()
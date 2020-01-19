# ser.write(b'hello')
# ser.close()

import SocketServer
import serial
from BaseHTTPServer import BaseHTTPRequestHandler

ser = serial.Serial('/dev/ttyUSB0')
ser.baudrate = 115200
def some_function():
    print "some_function got called"

class MyHandler(BaseHTTPRequestHandler):
    def do_POST(self):
	ser.write(b'11111')
        self.send_response(200)

httpd = SocketServer.TCPServer(("", 8080), MyHandler)
httpd.serve_forever()

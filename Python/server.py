import SocketServer
import serial
import time

from BaseHTTPServer import BaseHTTPRequestHandler

ser = serial.Serial('/dev/ttyUSB0')
ser.baudrate = 115200

class MyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        ser.write(self.path[1:])
        self.send_response(200)
        time.sleep(1)


httpd = SocketServer.TCPServer(("", 8080), MyHandler)
try:
    httpd.serve_forever()
except KeyboardInterrupt:
    pass
httpd.server_close()
ser.close()
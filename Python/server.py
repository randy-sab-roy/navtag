import serial
import http.server
import socketserver

PORT = 8080
Handler = http.server.SimpleHTTPRequestHandler

# ser = serial.Serial('/dev/ttyUSB0')
# ser.write(b'hello')
# ser.close()

with socketserver.TCPServer(("", PORT), Handler) as httpd:
    print("serving at port", PORT)
    httpd.serve_forever()
#include "httpd.h"
#include <unistd.h>
#include <stdio.h> 
#include <stdlib.h>

FILE *file;

int main(int c, char** v)
{
    file = fopen("/dev/ttyUSB0","w");
    serve_forever("12913");
    fclose(file);
    return 0;
}

void route()
{
    ROUTE_START()

    ROUTE_POST("/")
    {
        for (int i=0; i<payload_size; i++)
          fprintf(file,"%c",*(payload+i));

        printf("HTTP/1.1 200 OK\r\n\r\n");
        printf("Hello! You are using %s", request_header("User-Agent"));
    }
  
    ROUTE_END()
}
#include <Adafruit_NeoPixel.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoOTA.h>

#define NUM_LEDS 24
#define DATA_PIN 14
#define GROUND_PIN 15
#define GROUND2_PIN 12
#define LOWV_PIN 13

#define WIFI_SSID "NavTag"
#define WIFI_PASSWORD "12345678"
#define HOSTNAME "WeMos"

Adafruit_NeoPixel pixels(NUM_LEDS, DATA_PIN, NEO_GRB | NEO_KHZ800);

const uint32_t GREEN = pixels.Color(0, 255, 0);
const uint32_t RED = pixels.Color(255, 0, 0);
const uint32_t BLUE = pixels.Color(0, 0, 255);
const uint32_t BLACK = pixels.Color(0, 0, 0);
const uint32_t WHITE = pixels.Color(255, 255, 255);

void lightAllLeds(uint32_t color)
{
    pixels.clear();
    for(int i = 0; i < NUM_LEDS; i++)
    {
        pixels.setPixelColor(i, color);
    }
    pixels.show();
}

void setupPin()
{
    pinMode(LED_BUILTIN, OUTPUT);
    pinMode(GROUND_PIN, OUTPUT);
    pinMode(GROUND2_PIN, OUTPUT);
    pinMode(LOWV_PIN, OUTPUT);
    pinMode(DATA_PIN, OUTPUT);

    digitalWrite(LED_BUILTIN, HIGH);
    digitalWrite(GROUND_PIN, LOW);
    digitalWrite(GROUND2_PIN, LOW);
    digitalWrite(LOWV_PIN, HIGH);
}

void setupLed()
{
    pixels.setBrightness(100);
    pixels.begin();
    pixels.clear();
    pixels.show();
}

void setupWifi()
{
    Serial.println("");
	Serial.print("Connecting to ");
	Serial.print(WIFI_SSID);
	WiFi.hostname(HOSTNAME);
	WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
	while (WiFi.status() != WL_CONNECTED)
	{
		delay(500);
		Serial.print(".");
	}
	Serial.println("");
	Serial.print("WiFi connected with address ");
	Serial.println(WiFi.localIP());
}

void setupOTA()
{
    ArduinoOTA.setHostname(HOSTNAME);

    ArduinoOTA.onStart([]() {
        pixels.clear();
        pixels.show();
    });

    ArduinoOTA.onEnd([]() {
        pixels.clear();
        pixels.show();
    });

    ArduinoOTA.onProgress([](unsigned int progress, unsigned int total) {
        pixels.clear();
        for(int i = 0; i < NUM_LEDS * progress / total; i++)
        {
            pixels.setPixelColor(i, BLUE);
        }
        pixels.show();
    });

    ArduinoOTA.onError([](ota_error_t error) {
        lightAllLeds(RED);
    });
    ArduinoOTA.begin();
}

void setup()
{
    // Enable serial connection
    Serial.begin(115200);

    // Setup LED
    setupPin();
    setupLed();

    // WiFi connection
    setupWifi();
    setupOTA();

    // Setup server
    // server.begin();
}

void loop()
{
    pixels.setPixelColor(0, GREEN);
    pixels.setPixelColor(1, GREEN);
    pixels.setPixelColor(2, GREEN);
    pixels.setPixelColor(3, GREEN);
    pixels.show();
    delay(1000);
    pixels.setPixelColor(0, RED);
    pixels.setPixelColor(1, RED);
    pixels.setPixelColor(2, RED);
    pixels.setPixelColor(3, RED);
    pixels.show();
    delay(1000);
}

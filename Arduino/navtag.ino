#include <Adafruit_NeoPixel.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

#define NUM_LEDS 6
#define DATA_PIN 14
#define GROUND_PIN 15
#define GROUND2_PIN 12
#define LOWV_PIN 13

#define WIFI_SSID "NavTag"
#define WIFI_PASSWORD "12345678"
#define HOSTNAME "WeMos"

Adafruit_NeoPixel pixels(NUM_LEDS, DATA_PIN, NEO_GRB | NEO_KHZ800);
WiFiServer server(80);
WiFiClient client;
unsigned long lastUpdate = 0;

uint32_t colors[NUM_LEDS];
uint32_t freqs[NUM_LEDS];

const uint32_t BLACK = pixels.Color(0, 0, 0);
const uint32_t RED = pixels.Color(255, 0, 0);
const uint32_t GREEN = pixels.Color(0, 255, 0);
const uint32_t BLUE = pixels.Color(0, 0, 255);
const uint32_t WHITE = pixels.Color(255, 255, 255);

void lightAllLeds(uint32_t color)
{
    pixels.clear();
    for (int i = 0; i < NUM_LEDS; i++)
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
    WiFi.mode(WIFI_STA);
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

void serveClient(WiFiClient &client)
{
	// Wait for client
	for (int i = 0; i < 1000 && !client.available(); i++)
	{
		delay(1);
	}
	if (!client.available())
	{
		return;
	}

	// Read client request
	String req = client.readStringUntil('e');
    int length = req.length();

    // Set colors
    for(int i = 0; (i < length) && (i < NUM_LEDS); i++)
    {
        switch(req[i])
        {
            case '0':
                colors[i] = BLACK;
                break;
            case '1':
                colors[i] = RED;
                break;
            case '2':
                colors[i] = GREEN;
                break;
            case '3':
                colors[i] = BLUE;
                break;
            case '4':
                colors[i] = WHITE;
                break;
            default:
                colors[i] = BLACK;
                break;
        }
    }

    // Set freqs
    for(int i = 0; (i+NUM_LEDS < length) && (i < NUM_LEDS); i++)
    {
        int freq = req[i+NUM_LEDS] - '0';
        if(freq >= 0 && freq <= 9)
        {
            freqs[i] = freq;
        }
    }

    client.flush();
    client.print("OK\n");
}

void updateColors()
{
    for(int i = 0; i < NUM_LEDS; i++)
    {
        pixels.setPixelColor(i, (freqs[i] > 0 && (millis() % (freqs[i] * 200) > ((freqs[i] * 200) / 2))) ? BLACK : colors[i]);
    }
    Serial.print("\n");
    pixels.show();
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

    // Setup server
    server.begin();

    lightAllLeds(GREEN);
    delay(500);
    lightAllLeds(BLACK);
}

void loop()
{
    client = server.available();
    if (client && client.connected())
    {
        serveClient(client);
        client.stop();
    }

    updateColors();
}

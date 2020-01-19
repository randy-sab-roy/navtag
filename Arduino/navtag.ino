#include <Adafruit_NeoPixel.h>

#define NUM_LEDS 15
#define DATA_PIN 13


Adafruit_NeoPixel pixels(NUM_LEDS, DATA_PIN, NEO_GRB | NEO_KHZ800);

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
    pinMode(DATA_PIN, OUTPUT);

    digitalWrite(LED_BUILTIN, HIGH);
}

void setupLed()
{
    pixels.setBrightness(100);
    pixels.begin();
    pixels.clear();
    pixels.show();
}

void setup()
{
    // Enable serial connection
    Serial.begin(115200);

    // Setup LED
    setupPin();
    setupLed();

    lightAllLeds(GREEN);
    delay(500);
    lightAllLeds(BLACK);
}

void loop()
{
    pixels.setPixelColor(3, BLUE);
    pixels.setPixelColor(5, BLUE);
    pixels.setPixelColor(6, BLUE);
    pixels.setPixelColor(7, BLUE);
    pixels.setPixelColor(8, BLUE);
    pixels.setPixelColor(13, BLUE);
    pixels.show();
}
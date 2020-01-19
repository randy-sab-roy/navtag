#include <Adafruit_NeoPixel.h>

#define NUM_LEDS 15
#define DATA_PIN 13


Adafruit_NeoPixel pixels(NUM_LEDS, DATA_PIN, NEO_GRB | NEO_KHZ800);

const uint32_t BLACK = pixels.Color(0, 0, 0);
const uint32_t RED = pixels.Color(255, 0, 0);
const uint32_t GREEN = pixels.Color(0, 255, 0);
const uint32_t BLUE = pixels.Color(0, 0, 255);
const uint32_t WHITE = pixels.Color(255, 255, 255);

uint32_t colors[NUM_LEDS];
uint32_t freqs[NUM_LEDS];

void lightAllLeds(uint32_t color)
{
    pixels.clear();
    for (int i = 0; i < NUM_LEDS; i++)
    {
        pixels.setPixelColor(i, color);
    }
    pixels.show();
}

void processInput()
{
	String req = Serial.readStringUntil('e');

    for(int i = 0; (i < req.length()) && (i < NUM_LEDS); i++)
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
    for(int i = 0; (i+NUM_LEDS < req.length()) && (i < NUM_LEDS); i++)
    {
        int freq = req[i+NUM_LEDS] - '0';
        if(freq >= 0 && freq <= 9)
        {
            freqs[i] = freq;
        }
    }
}

void updateColors()
{
    for(int i = 0; i < NUM_LEDS; i++)
    {
        pixels.setPixelColor(i, (freqs[i] > 0 && (millis() % (freqs[i] * 200) > ((freqs[i] * 200) / 2))) ? BLACK : colors[i]);
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
    if (Serial.available() > 0)
    {
        processInput();
    }

    updateColors();
}

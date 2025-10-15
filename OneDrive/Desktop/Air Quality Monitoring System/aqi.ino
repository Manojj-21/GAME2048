// Include library for LCD and define pins
#include <LiquidCrystal.h>
const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

// Define pins and variable for input sensor and output led and buzzer
const int mq135_aqi_sensor = A0;
const int buzzer = 8;

// Set threshold for AQI
int aqi_ppm = 0;

void setup() {
  // Set direction of input-output pins
  pinMode (mq135_aqi_sensor, INPUT);
  pinMode (buzzer, OUTPUT);

  digitalWrite(buzzer, LOW);

  // Initiate serial and lcd communication
  Serial.begin (9600);
  lcd.clear();
  lcd.begin (16, 2);

  Serial.println("AQI Alert System");
  lcd.setCursor(0, 0);
  lcd.print("AQI Alert System");
  delay(1000);
}

void loop() {
  aqi_ppm = analogRead(mq135_aqi_sensor);

  Serial.print("Air Quality: ");
  Serial.println(aqi_ppm);

  lcd.setCursor(0, 0);
  lcd.print("Air Quality: ");
  lcd.print(aqi_ppm);

  if ((aqi_ppm >= 0) && (aqi_ppm <= 50))
  {
    lcd.setCursor(0, 1);
    lcd.print("AQI Good");
    Serial.println("AQI Good");
    digitalWrite(buzzer, LOW);
  }
  else if ((aqi_ppm >= 51) && (aqi_ppm <= 100))
  {
    lcd.setCursor(0, 1);
    lcd.print("AQI Moderate");
    Serial.println("AQI Moderate");
    digitalWrite(buzzer, LOW);
  }

  else if ((aqi_ppm >= 101) && (aqi_ppm <= 200))
  {
    lcd.setCursor(0, 1);
    lcd.print("AQI Unhealthy");
    Serial.println("AQI Unhealthy");
    digitalWrite(buzzer, LOW);
  }
  else if ((aqi_ppm >= 201) && (aqi_ppm <= 300))
  {
    lcd.setCursor(0, 1);
    lcd.print("AQ V.Unhealthy");
    Serial.println("AQ V.Unhealthy");
    digitalWrite(buzzer, LOW);
  }
  else if (aqi_ppm >= 301)
  {
    lcd.setCursor(0, 1);
    lcd.print("AQI Hazardous");
    Serial.println("AQI Hazardous");
    digitalWrite(buzzer, HIGH);
  }
  delay (700);
}

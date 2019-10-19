
#include <SFE_BMP180.h>
#include <Wire.h>
#include <SoftwareSerial.h>

SoftwareSerial BTserial(10, 11); // RX | TX
SFE_BMP180 tempSensor;
double T;
char status;
int sensorValue = 0;
void setup() {
  
Serial.begin(9600);
BTserial.begin(9600);
if (tempSensor.begin())
    Serial.println("BMP180 init success");
  else
  {
    Serial.println("BMP180 init fail\n\n");
    while(1); // Pause forever.
  }
}
void loop() {
status=tempSensor.startTemperature();
if(status!=0)
{
  delay(status);
}
sensorValue=tempSensor.getTemperature(T);
T-=2.7;   //for simple calibration can be changed according to altitude.
Serial.print(T,2);
Serial.print("\n");
BTserial.write(T);
BTserial.write("\n");

delay(2000);
}

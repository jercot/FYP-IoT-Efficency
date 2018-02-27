#include <dht.h>
#include <SR04.h>
#include <Ethernet.h>
#include <EEPROM.h>
//https://www.arduino.cc/en/Reference/EthernetLocalIP
//http://www.instructables.com/id/Arduino-Ethernet-Shield-Tutorial/

#define DHT11_PIN 7
#define TRIG_PIN 6
#define ECHO_PIN 5
#define TEMP_PIN 0
#define LIGHT_PIN 1
#define FREQUENCY 30
#define VARIANCE 10
#define DELAY 30000
SR04 sr04 = SR04(ECHO_PIN, TRIG_PIN);
dht DHT;
int count = 0;
int prev = 0;
int hum[FREQUENCY];
int ult[FREQUENCY];
float temp[FREQUENCY];
int light[FREQUENCY];

#include <SPI.h>
#include <Ethernet.h>

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char server[] = "fyp-iot-efficiency.eu-west-1.elasticbeanstalk.com";    // AWS
EthernetClient client;

void setup() {
  Serial.begin(9600);
  prev = sr04.Distance();
  if (Ethernet.begin(mac) == 0) {
    //light red if true?
  }
}

void loop() {
  Serial.print(count);
  incr();
  if(count==FREQUENCY) {
    char str[255];
    char str_temp[6];
    dtostrf(calcAveF(temp), 4, 2, str_temp);
    sprintf(str, "/upload?humidity=%d&movement=%d&temp=%s&light=%d", calcAve(hum), calcMov(ult), str_temp, calcAve(light));
    Serial.println();
    Serial.print("Humidity: ");
    Serial.println(calcAve(hum));
    Serial.print("Movement: ");
    Serial.println(calcMov(ult));
    print(ult, "Distance: ");
    Serial.print("Temperature: ");
    Serial.println(calcAveF(temp));
    Serial.print("Light: ");
    Serial.println(calcAve(light));
    Serial.println(str);
    if(!upload(server, str)) 
      Serial.println("Fail");
    else 
      Serial.println("Pass");
    count=0;
  }
  delay(DELAY);
}

byte upload(char *ipBuf, char *upload) {
  int inChar;
  char outBuf[64];
  Serial.print("connecting...");
  if(client.connect(ipBuf,80)) {
    Serial.println("connected");
    sprintf(outBuf,"GET %s HTTP/1.0\r\n\r\n", upload);
    client.write(outBuf);
  } 
  else {
    Serial.println("failed");
    return 0;
  }

  while(client.connected()) {
    while(client.available()) {
      inChar = client.read();
      Serial.write(inChar);
    }
  }
  Serial.println();
  Serial.println("disconnecting.");
  client.stop();
  return 1;
}

void print(int arr[], const char *title) {
  Serial.print(title);
  for(int i=0; i<FREQUENCY; i++) {
    Serial.print(arr[i]);
    if(i<FREQUENCY-1)
      Serial.print(", ");
  }
  Serial.println();
}

int calcAve(int arr[]) {
  int total = 0;
  for(int i=0; i<FREQUENCY; i++) {
    total += arr[i];
  }
  return(total/FREQUENCY);
}

float calcAveF(float arr[]) {
  float total = 0;
  for(int i=0; i<FREQUENCY; i++) {
    total += arr[i];
  }
  return(total/FREQUENCY);
}

int calcMov(int arr[]) {
  for (int i=0; i<FREQUENCY; i++) {
    int curr = arr[i];
    if(curr>=prev+VARIANCE || curr<=prev-VARIANCE) {
      prev = arr[FREQUENCY-1];
      return 1;
    }
    prev = curr;
  }
  return 0;
}

void incr() {
  int chk = DHT.read11(DHT11_PIN);
  hum[count] = DHT.humidity;
  ult[count] = sr04.Distance();
  int tempReading = analogRead(TEMP_PIN);
  double tempK = log(10000.0 * ((1024.0 / tempReading - 1)));
  tempK = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * tempK * tempK )) * tempK );
  temp[count] = tempK - 273.15;
  light[count] = analogRead(LIGHT_PIN);
  count += 1;
}


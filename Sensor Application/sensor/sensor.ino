#include <dht.h>
#include <SR04.h>
#include <Ethernet.h>
#include <EEPROM.h>
#include <SPI.h>
#include <EEPROM.h>

#define DHT11_PIN 7
#define TRIG_PIN 6
#define ECHO_PIN 5
#define TEMP_PIN 0
#define LIGHT_PIN 1
#define READINGS 15
#define VARIANCE 2
#define MINUTES 15
#define GREEN 9
#define YELLOW 2
#define RED 3

SR04 sr04 = SR04(ECHO_PIN, TRIG_PIN);
dht DHT;
int count = 0;
int prev = 0;
int hum[READINGS];
int ult[READINGS];
float temp[READINGS];
int light[READINGS];
byte mac[] = {0xDE, 0xAD, 0xDC, 0xAF, 0xFE, 0xED};
char webServer[] = "fyp-iot-efficiency.eu-west-1.elasticbeanstalk.com"; // set up with mobile?
IPAddress serverIp(192,168,1,6); // change for local
char bucket[21];
char token[21];
byte start;
char siteBucket;
long timeL;
long mill = long(MINUTES)*60*1000;
EthernetClient client;
EthernetServer arduinoServer(32109);

void setup() {
  Serial.begin(9600);
  prev = sr04.Distance();
  pinMode(GREEN, OUTPUT);
  pinMode(YELLOW, OUTPUT);
  pinMode(RED, OUTPUT);
  digitalWrite(RED, HIGH);
  if(Ethernet.begin(mac)!=0)
    digitalWrite(RED, LOW);
  arduinoServer.begin();
  Serial.println(Ethernet.localIP()[3]);
  start = readBucket();
  char tokenReq[50];
  sprintf(tokenReq,"/token?bucket=%s&", bucket);
  digitalWrite(YELLOW, HIGH);
  if(sendRequest(tokenReq, 3))
    digitalWrite(YELLOW, LOW);
  timeL = millis();
}

void loop() {
  Ethernet.maintain();
  int loopCount = (MINUTES*60)/READINGS;
  long loopTime = (long)(count+1) * loopCount * 1000;
  Serial.print(count);
  if(start) {
    if(count==READINGS||millis()>(timeL+mill)) {
      Serial.println();
      createRequest();
      timeL += mill;
      count=0;
    }
    else {
      flash(loopCount+5, timeL+loopTime);
      incr();
    }
  }
  else
    start = readBucket();
}

void flash(int c, long endT) {
  server();
  int pin = GREEN;
  if(token[0]!='1')
    pin = YELLOW;
  else if(!start)
    pin = RED;
    digitalWrite(pin, HIGH);
  delay(500);
  digitalWrite(pin, LOW);
  delay(500);
  if(c>=0&&millis()<endT)
    flash(c-1, endT);
}

byte readBucket() {
  for(int i=0; i<20; i++) {
    bucket[i] = EEPROM.read(i);
    if(!isDigit(bucket[i])&&!isAlpha(bucket[i]))
      return 0;
  }
  return 1;
}

int server() {
  client = arduinoServer.available();
  if (client) {
    if (client.connected()) {
      client.println(F("HTTP/1.1 200 OK"));
      client.println(F("Content-Type: application/json"));
      client.println(F("Connection: close"));
      client.println();
      client.print(F("{\"code\":"));
      char req[70];
      char toke[40] = "token=";
      for(int i=2; i<sizeof(token); i++)
        toke[i+4]=token[i];
      int i = 0;
      while (client.available()&&i<sizeof(req)) {
        char c = client.read();
        req[i] = c;
        i++;
      }
      byte tokenCheck = 1;
        tokenCheck=request('&', req, toke)||request('?', req, toke)||token[0]!='1';
      char set[] = "settings", var[] = "bucket=";
      if(request('/', req, set)&&tokenCheck) {
        if(!request('?', req, var)&&!request('&', req, var)) {
          if(bucket[0]!='\0') {
            client.print(F("1, \"bucket\":\""));
            client.print(bucket);
            client.print(F("\"}"));
          }
          else {
            client.print(F("2, \"bucket\":\""));
            client.print(F("No Bucket Set"));
            client.print(F("\"}"));
          }
        }
        else {
          if(updateBucket(req)) {
            client.print(F("3, \"bucket\":\""));
            client.print(bucket);
            client.print(F("\"}"));
          }
          else {
            client.print(F("4}"));
          }
        }
      }
      else if(!tokenCheck)
        client.print(F("-1}"));
      else
        client.print(F("0}"));
    }
    client.stop();
  }
}

byte request(char start, char *req, char *check) {
  byte compare = 1;
  int j=0, i=0;
  while(req[j]!='\0'&&req[j-1]!=start) {
    j++;
  }
  if(req[j-1]!=start)
    compare = 0;
  while(req[i+j]!='\0'&&check[i]!='\0'&&compare)  {
    if(req[i+j]!=check[i])
      compare = 0;
    i++;
  }
  return compare;
}

byte updateBucket(char *req) {
  int j=0, i=0, k=0;
  while(req[j]!='\0'&&(req[j-1]!='='||req[j-2]!='t')) {
    j++;
  }
  while(req[j+k]!='\0'&&req[j+k-1]!=' '&&k<20) {
    if(!isDigit(req[j+k])&&!isAlpha(req[j+k]))
      return 0;
    k++;
  }
  while(req[j]!='\0'&&req[j-1]!=' '&&i<20) {
    EEPROM.write(i, req[j]);
    bucket[i] = req[j];
    i++;
    j++;
  }
  return 1;
}

void createRequest() {
  digitalWrite(GREEN, HIGH);
  char str[200];
  char humS[50];
  char lightS[50];
  char tempS[50];
  char aveF[6], medF[6], minS[6], maxS[6];
  int minI, maxI;
  float minF, maxF;
  int med = calcMed(hum, &minI, &maxI);
  sprintf(humS, "&hAve=%d&hMed=%d&hMin=%d&hMax=%d", calcAve(hum), med, minI, maxI);
  med = calcMed(light, &minI, &maxI);
  sprintf(lightS, "&lAve=%d&lMed=%d&lMin=%d&lMax=%d", calcAve(light), med, minI, maxI);
  dtostrf(calcAveF(temp), 4, 2, aveF);
  dtostrf(calcMedF(temp, &minF, &maxF), 4, 2, medF);
  dtostrf(minF, 4, 2, minS);
  dtostrf(maxF, 4, 2, maxS);
  sprintf(tempS, "&tAve=%s&tMed=%s&tMin=%s&tMax=%s", aveF, medF, minS, maxS);
  sprintf(str, "/upload?mvmt=%d%s%s%s&bucket=%s", calcMov(ult), humS, lightS, tempS, bucket);
  if(!sendRequest(str, 3)) {
    digitalWrite(RED, HIGH);
  }
  else {
    digitalWrite(RED, LOW);
  }
  digitalWrite(GREEN, LOW);
}

byte sendRequest(char* param, int attempt) {
  char outBuf[200];
  //if(client.connect(serv,80)) {
  if(client.connect(serverIp, 8080)) {
    sprintf(outBuf,"GET %s HTTP/1.0\r\n\r\n", param);
    client.write(outBuf);
  } 
  else {
    if(attempt!=0)
      return sendRequest(param, attempt-1);
    return 0;
  }
  delay(1000);
  char status[32] = {0};
  client.readBytesUntil('\r', status, sizeof(status));
  char endOfHeaders[] = "\r\n\r\n";
  client.find(endOfHeaders);
  int i = 0;
  while(client.available()) {
    char c = client.read();
    token[i++] = c;
  }
  client.stop();
  return 1;
}


int calcAve(int arr[]) {
  int total = 0;
  for(int i=0; i<count; i++) {
    total += arr[i];
  }
  return(total/count);
}

int calcMed(int arr[], int* minT, int* maxT) {
  quickSort(arr, 0, count-1);
  *minT = arr[0];
  *maxT = arr[count-1];
  return ((arr[count/2-1]+arr[count/2])/2);
}

void swap(int* a, int* b) {
    int t = *a;
    *a = *b;
    *b = t;
}

int partition (int arr[], int low, int high) {
    int pivot = arr[high];
    int i = (low - 1);
    for (int j = low; j <= high- 1; j++) {
        if (arr[j] <= pivot) {
            i++;
            swap(&arr[i], &arr[j]);
        }
    }
    swap(&arr[i + 1], &arr[high]);
    return (i + 1);
}

void quickSort(int arr[], int low, int high) {
    if (low<high) {
        int pi = partition(arr, low, high);
        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
}

float calcAveF(float arr[]) {
  float total = 0;
  for(int i=0; i<count; i++) {
    total += arr[i];
  }
  return(total/count);
}

float calcMedF(float arr[], float* minF, float* maxF) {
  quickSortF(arr, 0, count-1);
  *minF = arr[0];
  *maxF = arr[count-1];
  return ((arr[count/2-1]+arr[count/2])/2);
}

void swapF(float* a, float* b) {
    float t = *a;
    *a = *b;
    *b = t;
}

int partitionF(float arr[], int low, int high) {
    float pivot = arr[high];
    int i = (low-1);
    for (int j=low; j <= high-1; j++) {
        if (arr[j] <= pivot) {
            i++;
            swapF(&arr[i], &arr[j]);
        }
    }
    swapF(&arr[i + 1], &arr[high]);
    return (i + 1);
}

void quickSortF(float arr[], int low, int high) {
    if (low<high) {
        int pi = partitionF(arr, low, high);
        quickSortF(arr, low, pi-1);
        quickSortF(arr, pi+1, high);
    }
}

int calcMov(int arr[]) {
  for (int i=0; i<count; i++) {
    int curr = arr[i];
    if(curr>=prev+VARIANCE||curr<=prev-VARIANCE) {
      prev = arr[count-1];
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
  double tempK = log(10000.0*((1024.0/tempReading-1)));
  tempK = 1/(0.001129148+(0.000234125+(0.0000000876741*tempK*tempK))*tempK);
  temp[count] = tempK-273.15;
  light[count] = analogRead(LIGHT_PIN);
  count ++;
}

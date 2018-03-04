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
byte mac[] = {0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED};
char webServer[] = "fyp-iot-efficiency.eu-west-1.elasticbeanstalk.com"; 
char bucket[21];
EthernetClient client;
EthernetServer arduinoServer(32109);

void setup() {
  Serial.begin(9600);
  prev = sr04.Distance();
  pinMode(GREEN, OUTPUT);
  pinMode(YELLOW, OUTPUT);
  pinMode(RED, OUTPUT);
  digitalWrite(RED, HIGH);
  if(Ethernet.begin(mac)!=0) {
    digitalWrite(RED, LOW);
  }
  arduinoServer.begin();
  Serial.println(Ethernet.localIP());
  readBucket();
  Serial.println(bucket);
}

void loop() {
  Serial.print(count);
  incr();
  digitalWrite(GREEN, LOW);
  if(count==READINGS) {
    createRequest();
    Serial.println();
    count=0;
  }
  flash((MINUTES*60)/READINGS);
}

void flash(int c) {
  server();
  digitalWrite(YELLOW, HIGH);
  delay(500);
  digitalWrite(YELLOW, LOW);
  delay(500);
  if(c>=0)
    flash(c-1);
}

byte readBucket() {
  for(int i=0; i<20; i++) {
    bucket[i] = EEPROM.read(i);
  }
  return 1;
}

int server() {
  client = arduinoServer.available();
  if (client) {
    if (client.connected()) {
      client.println("HTTP/1.1 200 OK");
      client.println("Content-Type: application/json");
      client.println("Connection: close");
      client.println();
      client.print("{\"code\":\"");
      char req[42];
      int i = 0;
      while (client.available() && i < sizeof(req)) {
        char c = client.read();
        Serial.print(c);
        req[i] = c;
        i++;
      }
      char set[] = "settings", var[] = "bucket";
      if (request('/', req, set)) {
        if (!request('?', req, var)) {
            client.print("1\", \"bucket\":\"");
            client.print(bucket);
            client.print("\"}");
        }
        else {
          updateBucket(req);
          client.print("2\", \"bucket\":\"");
          client.print(bucket);
          client.print("\"}");
        }
      }
      else {
        client.print("2\"}");
      }
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

int availableMemory() {
    // Use 1024 with ATmega168
    int size = 2048;
    byte *buf;
    while ((buf = (byte *) malloc(--size)) == NULL);
        free(buf);
    return size;
}

void updateBucket(char *req) {
  int j=0, i=0;
  while(req[j]!='\0'&&req[j-1]!='=') {
    j++;
  }
  while(req[j]!='\0'&&req[j-1]!=' '&&i<20) {
    EEPROM.write(i, req[j]);
    bucket[i] = req[j];
    i++;
    j++;
  }
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
  //Serial.print(str);
  if(!upload(webServer, str, 3)) {
    digitalWrite(RED, HIGH);
  }
  else {
    digitalWrite(RED, LOW);
  }
}

byte upload(char *serv, char *param, int attempt) {
  int inChar;
  char outBuf[200];
  if(client.connect(serv,80)) {
    sprintf(outBuf,"GET %s HTTP/1.0\r\n\r\n", param);
    client.write(outBuf);
  } 
  else {
    if(attempt>3)
      upload(serv, param, attempt-1);
    return 0;
  }
  client.stop();
  return 1;
}

int calcAve(int arr[]) {
  int total = 0;
  for(int i=0; i<READINGS; i++) {
    total += arr[i];
  }
  return(total/READINGS);
}

int calcMed(int arr[], int* minT, int* maxT) {
  quickSort(arr, 0, READINGS-1);
  *minT = arr[0];
  *maxT = arr[READINGS-1];
  return ((arr[READINGS/2-1]+arr[READINGS/2])/2);
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
    if (low < high) {
        int pi = partition(arr, low, high);
        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
}

float calcAveF(float arr[]) {
  float total = 0;
  for(int i=0; i<READINGS; i++) {
    total += arr[i];
  }
  return(total/READINGS);
}

float calcMedF(float arr[], float* minF, float* maxF) {
  quickSortF(arr, 0, READINGS-1);
  *minF = arr[0];
  *maxF = arr[READINGS-1];
  return ((arr[READINGS/2-1]+arr[READINGS/2])/2);
}

void swapF(float* a, float* b) {
    float t = *a;
    *a = *b;
    *b = t;
}

int partitionF(float arr[], int low, int high) {
    float pivot = arr[high];
    int i = (low - 1);
    for (int j = low; j <= high- 1; j++) {
        if (arr[j] <= pivot) {
            i++;
            swapF(&arr[i], &arr[j]);
        }
    }
    swapF(&arr[i + 1], &arr[high]);
    return (i + 1);
}

void quickSortF(float arr[], int low, int high) {
    if (low < high) {
        int pi = partitionF(arr, low, high);
        quickSortF(arr, low, pi - 1);
        quickSortF(arr, pi + 1, high);
    }
}

int calcMov(int arr[]) {
  for (int i=0; i<READINGS; i++) {
    int curr = arr[i];
    if(curr>=prev+VARIANCE || curr<=prev-VARIANCE) {
      prev = arr[READINGS-1];
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

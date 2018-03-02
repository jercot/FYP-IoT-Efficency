#include <dht.h>
#include <SR04.h>
#include <Ethernet.h>
#include <EEPROM.h>
#include <SPI.h>

#define DHT11_PIN 7
#define TRIG_PIN 6
#define ECHO_PIN 5
#define TEMP_PIN 0
#define LIGHT_PIN 1
#define READINGS 20
#define VARIANCE 2
#define MINUTES 10
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
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
char webServer[] = "fyp-iot-efficiency.eu-west-1.elasticbeanstalk.com";    // AWS
char bucket[25] = "123JA423SD4A23FX";
EthernetClient client;
EthernetServer arduinoServer(80);

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
  Serial.print("Arduino Server is at ");
  Serial.println(Ethernet.localIP());
}

void loop() {
  Serial.print(count);
  incr();
  digitalWrite(GREEN, LOW);
  if(count==READINGS) {
    digitalWrite(GREEN, HIGH);
    char str[255];
    char humS[72];
    char lightS[72];
    char tempS[72];
    char aveF[6], medF[6], minS[6], maxS[6];
    int minI, maxI;
    float minF, maxF;
    int med = calcMed(hum, &minI, &maxI);
    sprintf(humS, "&humAve=%d&humMed=%d&humMin=%d&humMax=%d", calcAve(hum), med, minI, maxI);
    med = calcMed(light, &minI, &maxI);
    sprintf(lightS, "&lightAve=%d&lightMed=%d&lightMin=%d&lightMax=%d", calcAve(light), med, minI, maxI);
    dtostrf(calcAveF(temp), 4, 2, aveF);
    dtostrf(calcMedF(temp, &minF, &maxF), 4, 2, medF);
    dtostrf(minF, 4, 2, minS);
    dtostrf(maxF, 4, 2, maxS);
    sprintf(tempS, "&tempAve=%s&tempMed=%s&tempMin=%s&tempMax=%s", aveF, medF, minS, maxS);
    sprintf(str, "/upload?movement=%d%s%s%s&bucket=%s", calcMov(ult), humS, lightS, tempS, bucket);
    Serial.println(str);
    if(!upload(webServer, str, 3)) {
      digitalWrite(RED, HIGH);
    }
    else {
      digitalWrite(RED, LOW);
    }
    delay(1000);
    count=0;
  }
  //delay((MINUTES*60000)/READINGS);
  flash((MINUTES*60)/READINGS);
}

void flash(int c) {
  settings();
  digitalWrite(YELLOW, HIGH);
  delay(500);
  digitalWrite(YELLOW, LOW);
  delay(500);
  if(c>=0)
    flash(c-1);
}

int settings() {
  client = arduinoServer.available();
  if (client) {
    if (client.connected()) {
      int i = 0, k = 25;
      String param;
      while (client.available() && i < k) {
        char c = client.read();
        param += c;
        i += 1;
      }
      int s = param.indexOf("/")+1;
      param = param.substring(s, param.indexOf(" ", s));
      s = param.indexOf("?")+1;
      int d = param.indexOf("=")+1;
      if(s!=0 && d!=0) {
        String nameA = param.substring(s, d-1);
        String value = param.substring(" ", d);
        if(nameA.compareTo("update")==0)
          updateBucket(nameA, value);
        else
          client.print("3, ");
      }
      else
        client.print("1, ");
      client.print(bucket);
    }
    client.stop();
  }
}

void updateBucket(String nameA, String value) {
  client.print("2, ");
}

byte upload(char *ipBuf, char *param, int attempt) {
  int inChar;
  char outBuf[64];
  if(client.connect(ipBuf,80)) {
    sprintf(outBuf,"GET %s HTTP/1.0\r\n\r\n", param);
    client.write(outBuf);
  } 
  else {
    if(attempt>3)
      upload(ipBuf, param, attempt-1);
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

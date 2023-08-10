#include <WiFi.h>
#include <HTTPClient.h>
#include <WiFiClientSecure.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <ArduinoJson.h> 
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>


const char* ssid = "MOVISTAR_AF09";
const char* password = "12345678";
const int AnalogPin = 34;
const int AnalogPin2 = 16;
const int UmbralHumedadMinimo = 5;
const int UmbralHumedadMaximo = 35;
const char* bajahumedad = "Alerta: La humedad está bajo ";
const char* altahumedad = "Alerta: La humedad está por encima de ";
const char* unidad = " HR";
const char* normalhumedad = "El nivel de humedad está dentro de los parametros normales";
const char* httpsServer = "https://qap9opok49.execute-api.us-west-2.amazonaws.com/prod/sensorapi";
const char* httpsGetRequest = "https://qap9opok49.execute-api.us-west-2.amazonaws.com/prod/sensorapi?startregister=1&maxregisters=1";
const char* httpsGetTapRequest = "https://t473ll27a2.execute-api.us-west-2.amazonaws.com/prod/llaveapi?startregister=1&maxregisters=1";
const int TapTime = 2;
const int DelayTime = 15;
const int WifiTimeout = 30;
const int fadeAmount = 5;  // Cantidad de cambio en el brillo por iteración
const int channel = 0;     // Canal PWM

WiFiUDP ntpUDP;
WiFiClientSecure espClient;
NTPClient timeClient(ntpUDP);

// Declaración de funciones
void setup();
void loop();
void sensorThread(void *parameter);
void llaveThread(void *parameter);
String obtenerTimestampActualizado(NTPClient& timeClient);

void setup() {
  //Configurar la salida serial
  Serial.begin(9600);
  // Configurar conexión Wi-Fi
  WiFi.begin(ssid, password);

  // Esperar hasta que se conecte a la red WiFi
  unsigned long startAttemptTime = millis();
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Conectándose al Access Point WiFi...");
    
    // Si no se conecta en un tiempo determinado, detener el intento
    if (millis() - startAttemptTime > (WifiTimeout * 1000)) { // n segundos
      Serial.println("No se pudo conectar a la red WiFi. Reinicie y verifique la configuración.");
      while (1); // Bucle infinito
    }
  }
  //Configurar Led
  pinMode(AnalogPin2, OUTPUT); // Establecer el pin del LED como salida
  ledcSetup(channel, 5000, 8); // Canal, Frecuencia, Resolución
  ledcAttachPin(AnalogPin2, channel); // Asignar el pin al canal

  //Confirmar conexion exitosa
  Serial.println("Conectado al Access Point WiFi");
  //LED de Confirmacion de conexion
  ledcWrite(channel, 255); // Asignar el valor a la intensidad del LED
  delay(1000);
  ledcWrite(channel, 0); // Asignar el valor a la intensidad del LED

  //Configurar NTP
  timeClient.begin();
  timeClient.setTimeOffset(-18000);

  //Hilos
  xTaskCreatePinnedToCore(sensorThread, "SensorThread", 8192, NULL, 1, NULL, 0); // Pinned al núcleo 0
  xTaskCreatePinnedToCore(llaveThread, "LlaveThread", 8192, NULL, 1, NULL, 1); // Pinned al núcleo 1

}

void loop() {

}

void sensorThread(void *parameter) {
  int valor_humedad_raw;
  int humedad_relativa;
  while (1) {
    //Obtener el valor del sensor
    Serial.println("Sensor: leyendo sensor...");
    valor_humedad_raw = analogRead (AnalogPin);
    humedad_relativa =  ((4095.0 - valor_humedad_raw) / 4095.0 ) * 100.0;
    Serial.println("Sensor: la humedad relativa obtenida es :" + String(humedad_relativa));
    //iniciar cliente http
    HTTPClient httpGet;
    httpGet.begin(httpsGetRequest);
    int httpResponseCodeGet = httpGet.GET();
    if (httpResponseCodeGet > 0) {
        String responseGet = httpGet.getString();
        Serial.println("Sensor: " + responseGet);
        //Deserializar JSON
        DynamicJsonDocument doc(1024);
        deserializeJson(doc, responseGet);
        //Obtener id del ultimo registro
        int lastRegistroId = doc["lastRegistroId"]; // Obtener lastRegistroId del JSON
        httpGet.end();
        //Fabricar JSON
        DynamicJsonDocument jsonDoc(200);
        jsonDoc["RegistroId"] = lastRegistroId + 1; // Incrementar el lastRegistroId
        jsonDoc["FechayHora"] = obtenerTimestampActualizado(timeClient);
        jsonDoc["medida"] = humedad_relativa;
        if (humedad_relativa < UmbralHumedadMinimo) {
          jsonDoc["comentario"] = bajahumedad + String(UmbralHumedadMinimo) + unidad;
        }else if(humedad_relativa > UmbralHumedadMaximo){
          jsonDoc["comentario"] = altahumedad + String(UmbralHumedadMaximo) + unidad;
        }
        else{
          jsonDoc["comentario"] = normalhumedad;
        }
        //Convertir array JSON a String
        String jsonString;
        serializeJson(jsonDoc, jsonString);
        //Enviar JSON
        HTTPClient httpPost;
        httpPost.begin(httpsServer);
        httpPost.addHeader("Content-Type", "application/json");
        int httpResponseCodePost = httpPost.POST(jsonString);
        //COmprobar respuesta de la nube
        if (httpResponseCodePost > 0) {
          Serial.print("Sensor: Respuesta del servidor: ");
          Serial.println(httpResponseCodePost);
          String responsePost = httpPost.getString();
          Serial.println("Sensor: " + responsePost);
        } else {
          Serial.print("Sensor: Error en la solicitud POST. Código de error: ");
          Serial.println(httpResponseCodePost);
        }

        httpPost.end();
    }
    else {
        //COmprobar respuesta de la nube
        Serial.print("Error en la solicitud GET. Código de error: ");
        Serial.println(httpResponseCodeGet);
        httpGet.end();
    }  
    delay(DelayTime * 1000); // Esperar antes de la siguiente lectura
  }
}

// Función para el hilo de la llave
void llaveThread(void *parameter) {
  int brightness = 0;  // Nivel de brillo del LED (0 - 255)
  int led_write_value = 0;
  int lastProcessedRegistroId = 0; // 
  bool fistloop = true;
  while (1) {
    //Crear objeto ppara comprobar registro de la llave
    HTTPClient httpLlaveGet;
    httpLlaveGet.begin(httpsGetTapRequest);
    //Hacer consulta get
    int httpResponseCodeLlaveGet = httpLlaveGet.GET();
    //Si la consulta no es vacía
    if (httpResponseCodeLlaveGet > 0) {
      String responseGet = httpLlaveGet.getString();
      Serial.println("Llave: " + responseGet);
      //Deserializar JSON
      DynamicJsonDocument doc(1024);
      deserializeJson(doc, responseGet);
      //Obtener id del ultimo registro
      int lastRegistroId = doc["lastRegistroId"]; // Obtener lastRegistroId del JSON
      //Si el id del ultimo registro es 1 o más , que tambien significa que hay registros en la tabla ...
      if (lastRegistroId>0) {
        //Asegurarse que la llave solo se abra una vez hasta que se envia otro comando
        if (lastRegistroId > lastProcessedRegistroId) {
          lastProcessedRegistroId = lastRegistroId;
          //Obtiene nivel grifo y tiempoapertura 
          int nivelgrifo = doc["registers"][0]["nivelgrifo"];
          int tiempoapertura = doc["registers"][0]["tiempoapertura"];
          led_write_value = 255 * (nivelgrifo/100.0);
          //Evitar que la llave se abra automaticamente al inicio
          if (fistloop == false) {
            //Abrir la Llave
            Serial.println("Llave: abriendo la llave " + String(tiempoapertura) + " segundos a una presión de " + String(nivelgrifo) + " ...");
            ledcWrite(channel, led_write_value); // Asignar el valor a la intensidad del LED
            //esperar tiempo de apertura
            delay(tiempoapertura * 1000);
            //cerrar la llave
            Serial.println("Llave: cerrando la llave ...");
            ledcWrite(channel, 0); // Asignar el valor a la intensidad del LED
          }
          
        }
      }
    }
    httpLlaveGet.end();
    delay(TapTime * 1000); // Esperar antes del siguiente loop
    fistloop = false;
  }
}

String obtenerTimestampActualizado(NTPClient& timeClient) {
  // Obtener el timestamp actualizado
  timeClient.update();
  // Obtener EpochTime
  unsigned long epochTime = timeClient.getEpochTime();
  // Obtener formattedTime
  String formattedTime = timeClient.getFormattedTime();
  // Obtener toda la fecha y Hora
  struct tm *ptm = gmtime ((time_t *)&epochTime);
  int currentYear = ptm->tm_year + 1900;
  int currentMonth = ptm->tm_mon + 1;
  int monthDay = ptm->tm_mday;
  // Formatear día y mes con ceros a la izquierda si es necesario
  String formattedDay = (monthDay < 10 ? "0" : "") + String(monthDay);
  String formattedMonth = (currentMonth < 10 ? "0" : "") + String(currentMonth);
  String currentDate = formattedDay + "/" + formattedMonth + "/" + String(currentYear);
  // Combinar formattedTime y currentDate en un único string llamado TimeDate
  String TimeDate = currentDate + " " + formattedTime;
  return TimeDate;
}



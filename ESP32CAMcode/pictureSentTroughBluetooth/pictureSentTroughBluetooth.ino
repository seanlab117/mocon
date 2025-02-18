#include "setup.h"

BluetoothSerial SerialBT;
int pictureNumber = 0;
void setup() {
  
  doBoringSetupStuff();


      

  Serial.begin(115200);
  SerialBT.begin(device_name); //Bluetooth device name
  Serial.printf("The device with name \"%s\" is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str());
    // initialize EEPROM with predefined size
  EEPROM.begin(EEPROM_SIZE);
  

}

void loop() {
  if (Serial.available()) {
    String input = Serial.readString();
    Serial.print(input);
    Serial.write("\n");
    if (input.indexOf("p") ==0){
      Serial.write("will take a photo, send it trough bluetooth and save it to SD card");
      
      camera_fb_t * fb = NULL;
  
      // Take Picture with Camera
      fb = esp_camera_fb_get();  
      if(!fb) {
        Serial.println("Camera capture failed");
        return;
        }
      pictureNumber = EEPROM.read(0) + 1;


      //sending the picture trouh bluetooth
      SerialBT.print("picture arriving,"+String(fb->len));
            //SerialBT.print("picture arriving...\n");

      SerialBT.write(fb->buf, fb->len);
      SerialBT.print("finished sending picture.\n");

      // Path where new picture wilçkl be saved in SD Card
      String path = "/picture" + String(pictureNumber) +".jpg";

      fs::FS &fs = SD_MMC; 
      Serial.printf("Picture file name: %s\n", path.c_str());
      
      File file = fs.open(path.c_str(), FILE_WRITE);
      if(!file){
        Serial.println("Failed to open file in writing mode");
      } 
      else {
        file.write(fb->buf, fb->len); // payload (image), payload length
        Serial.printf("Saved file to path: %s\n", path.c_str());
        EEPROM.write(0, pictureNumber);
        EEPROM.commit();
      }
      file.close();
      esp_camera_fb_return(fb); 

 
    }
    
    //SerialBT.write(Serial.read());
    SerialBT.print(input);
    
    }
  
  if (SerialBT.available()) {
    Serial.write(SerialBT.read());
  }
  delay(20);

}

#include <Servo.h>

Servo servo_1;
Servo servo_2;

char buffer[20];
char bufferIndex = 0;

int posV;
int posH;

int value[2];


void setup(){

  Serial.begin(9600);
  Serial.flush();

  
  servo_1.attach(9);
  servo_2.attach(10);

  
  //Serial.println(">>");

  posH = 90;
  posV = 90;


  servo_1.write(posV);
  servo_2.write(posH);
 
  

  for ( int i = 0 ; i < 20 ; ++i )
  {
    buffer[i] = 0 ;
  }

  delay(1000);

}

void loop() {
   if(Serial.available() )
  {
     buffer[bufferIndex] = (char)Serial.read();

    if(buffer[bufferIndex] == '|')
    {
      //delay(100000);
      
      buffer[bufferIndex] = 0;
      value[0] = atoi(buffer);
      bufferIndex = 0;
      /*
      Serial.print("upDown : ");  
       Serial.print(value[0]);
       Serial.print("\n");
       */
      posV = value[0];
      /*
      if(posV < 55)
        posV = 55;
      */
     
      servo_2.write(posV);
      
      Serial.flush();
    }
    else if(buffer[bufferIndex] == '#')
    {
      buffer[bufferIndex] = 0;
      value[1] = atoi(buffer);
      bufferIndex = 0;


      /*
      //left 
       
       Serial.print("type : ");
       Serial.println(type);
       Serial.print("leftRight : ");
       Serial.println(value[1]);
       
       //setting value
       Serial.print("temp H :");
       Serial.print(tempH);
       Serial.print("   curr :");
       Serial.println(sum);
       
       // current rotate
       Serial.print("posH : ");
       Serial.print(posH);
       Serial.print("stackH : " ) ;
       Serial.println(stackH);
       */

     
      posH = value[1] ;
      servo_1.write(posH);

      
      Serial.flush();
    }
    else
    {
      bufferIndex++;
      bufferIndex = bufferIndex%20;  
    } 

   
  
  }

}

#ifndef _ARDUINOMANAGER_
#define _ARDUINOMANAGER_

#include "ThreadArgument.h"
#define DEVICE "/dev/ttyACM0"
#define BAUDRATE B9600

class ArduinoManager{
	public:
		static void* Connect_SendData(void* args);
};

#endif

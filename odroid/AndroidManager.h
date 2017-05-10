#ifndef _ANDROIDMANAGER_
#define _ANDROIDMANAGER_

#include "ImageManager.h"
#define WIDTH 320 //240
#define HEIGHT 240 //180
#define IMG_SIZE (WIDTH*HEIGHT*3)

class AndroidManager{
	public:
		static void* sendImageData(void* args);
		static void* receiveAngleData(void* args);
		static void sigIntHandler(int signal) ;
		static bool stop ;
};

#endif

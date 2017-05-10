#ifndef _IMAGEMANAGER_
#define _IMAGEMANAGER_

#include "ThreadArgument.h"

class ImageManager{
	public:
		static float xscale;
		static float yscale;
		static float xshift;
		static float yshift;
		static float thresh;
    	static void sampleImage(const IplImage* arr, float idx0, float idx1, CvScalar& res);
	   	static float getRadialX(float x,float y,float cx,float cy,float k);
	   	static float getRadialY(float x,float y,float cx,float cy,float k);
	   	static float calc_shift(float x1,float x2,float cx,float k);
		static Mat make3DImage(const Mat& img);
};

#endif

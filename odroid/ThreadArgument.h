#ifndef _THREADARGUMENT_
#define _THREADARGUMENT_

#include <iostream>
#include <fstream>
#include <vector>
#include <string.h>
#include <string>
#include <unistd.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <opencv2/opencv.hpp>
#include <stdlib.h>
#include <termios.h>
#include <fcntl.h>
#include <math.h>
#include <getopt.h>
#include <errno.h>
#include <csignal>

#define BUFF_SIZE 1024

using namespace cv;
using namespace std;

class ThreadArgument{
	public:
		bool* isConnect;
		string* angleData;
		int clientId;
		ThreadArgument() : isConnect(NULL), clientId(0),angleData(NULL) {}
		ThreadArgument(bool* ic,int ci, string* ad) 
		: isConnect(ic), clientId(ci),angleData(ad) {}
};

#endif 

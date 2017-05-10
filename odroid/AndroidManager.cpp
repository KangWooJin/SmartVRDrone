#include"AndroidManager.h"

bool AndroidManager::stop = false ;

void AndroidManager::sigIntHandler(int signal)
{
	stop = true ;
}
void* AndroidManager::sendImageData(void* args)
{	
	ThreadArgument* threadArgument = static_cast<ThreadArgument*>(args);
	int myId = threadArgument->clientId;
	Size dSize = Size(WIDTH, HEIGHT);

	VideoCapture cap0(0); // LEFT
	//VideoCapture cap0("/dev/video0"); // LEFT
	VideoCapture cap1(1); // RIGHT
	//VideoCapture cap1("dev/video1"); // RIGHT

	if(!cap0.isOpened()){
		cout << " Camera0 is Off" << endl;
		return NULL;
	}
	if(!cap1.isOpened()){
		cout << " Camera1 is Off" << endl;
		return NULL;
	}
	Mat frame0, frame1;	
	uchar* sendBuff = new uchar [IMG_SIZE*2];
	int flag = 1;

	std::signal(SIGINT, sigIntHandler);
	while(*threadArgument->isConnect){
			
		if ( stop )
			break;
		cap0 >> frame0;
		if(frame0.empty()) continue;
		cap1 >> frame1;
		if(frame1.empty()) continue;
		
		resize(frame0, frame0, dSize);
		resize(frame1, frame1, dSize);

//		setsockopt(myId, IPPROTO_TCP, TCP_NODELAY, &flag,sizeof(flag));	
		
		frame0 = ImageManager::make3DImage(frame0);	
		frame1 = ImageManager::make3DImage(frame1);	

		write(myId, frame0.data, IMG_SIZE);
		write(myId, frame1.data, IMG_SIZE);
	}
	cout << "Send Process is closed" << endl;

	pthread_detach(pthread_self());
	return NULL;
}
void* AndroidManager::receiveAngleData(void* args)
{
	ThreadArgument* threadArgument = static_cast<ThreadArgument*>(args);
	bool* isConnect = threadArgument->isConnect;
	string* angleData = threadArgument->angleData;
	int clientId = threadArgument->clientId;
	char readBuff[BUFF_SIZE];

	while(true){
		memset(readBuff, 0, sizeof(readBuff));
		while(read(clientId, readBuff, BUFF_SIZE) == 0);
		cout << readBuff << endl ;
		int i =0;
		string temp = ""; 
		while(readBuff[i] != '\n'){
			temp += readBuff[i];
			i++;
		}
		*angleData = temp;
		cout << "ReceiveData : " << *angleData;
		if(strcmp(readBuff, "EXIT\n")==0){
			*isConnect = false;
			cout << "Receive EXIT, End of Receive Process" << endl;
			break;
		}
	}
	
	pthread_detach(pthread_self());

	return NULL;
}

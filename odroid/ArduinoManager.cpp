#include "ArduinoManager.h"
void* ArduinoManager::Connect_SendData(void* args)
{
	ThreadArgument* threadArgument = static_cast<ThreadArgument*>(args);
	bool* isConnect = threadArgument->isConnect;
	string* angleData = threadArgument->angleData;

	int fd = -1;
	struct termios options;
	fd = open("/dev/ttyACM0", O_RDWR | O_NOCTTY | O_NDELAY);

	if (fd == -1)
	{
		std::cout << "Port Failed to Open";
		return NULL;
	}
	else
	{
		fcntl(fd, F_SETFL, FNDELAY); 
		tcgetattr(fd, &options);
		cfsetispeed(&options, B9600);
		cfsetospeed(&options, B9600);
		options.c_cflag |= (CLOCAL | CREAD);
		options.c_cflag &= ~PARENB;	
		options.c_cflag &= ~CSTOPB;
		options.c_cflag &= ~CSIZE;
		options.c_cflag |= CS8;	
		tcsetattr(fd, TCSANOW, &options);
	}
	char buf[BUFF_SIZE];


	cout << "Arduino FD is : " << fd << endl ;

	while(true){
		if(*angleData != ""){
			int i;
			string angle = *angleData;
			*angleData = "";
			for(i =0; i<angle.length(); i++){
				buf[i] = angle[i];
			}
			buf[i] = '\0';
			if(!strcmp(buf, "EXIT")){
				cout << "End of Arduino Process " << endl;
				break;
			}
			write(fd, buf, strlen(buf));
			fflush(stdout);
			cout << "Send : "  << buf << endl;
		}
	}
	close(fd);

	pthread_detach(pthread_self());
	return NULL;
}


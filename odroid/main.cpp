#include "ConnectionManager.h"

pthread_mutex_t mutex_accident;
bool stop = false ;
void sigIntHandler(int signal)
{
	stop = true ;
}
int main()
{
	pthread_mutex_init(&mutex_accident,NULL);
	ConnectionManager con;	

	try{
		con.openServer();
		//con.startArduinoProcess();
	}catch(ConnectionManager::ConnectionException ce){
		cout << "Server Open is Failed" << endl;
		return 0;
	}	
	std:signal(SIGINT, sigIntHandler);

	while(true){
//	while(!stop){
		try{
			int clientFd = con.takeClient();
			cout << "client ID is " << clientFd << endl;
			pthread_mutex_lock(&mutex_accident);
			con.startArduinoProcess();
			con.startSendProcess();
			con.startReceiveProcess();
			pthread_mutex_unlock(&mutex_accident);
		}catch(ConnectionManager::ConnectionException ce){
			cout << "Client Join Error" << endl;
			break;
		}
	}
	con.closeServer();
	pthread_mutex_destroy(&mutex_accident);

	return 0;
}

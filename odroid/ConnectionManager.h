#ifndef _CONNECTIONMANAGER_
#define _CONNECTIONMANAGER_

#include "AndroidManager.h"
#include "ArduinoManager.h"

#define PORT 11129
#define MAX_PENDING 5

class ConnectionManager{
	private:
		int m_serverFd;
		struct sockaddr_in m_serverAddr;
	//	static void* readProcess(void*);
	public:
		int clientId; // Android
		int equipId; // Arduino
		bool* isConnect;
		string* receiveData;
		typedef int ConnectionException;
		static const ConnectionException exception_socket = 1;
		static const ConnectionException exception_bind = 2;
		static const ConnectionException exception_listen = 3;
		static const ConnectionException exception_accept = 4;
		ConnectionManager();
		~ConnectionManager();
		void openServer() throw (ConnectionException);
		void openServer(int port) throw (ConnectionException);
		void closeServer() throw (ConnectionException);
		int takeClient() throw (ConnectionException);
		void startArduinoProcess();
		void startSendProcess();
		void startReceiveProcess();
};

#endif

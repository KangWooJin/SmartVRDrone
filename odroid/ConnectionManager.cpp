#include "ConnectionManager.h"

extern pthread_mutex_t mutex_accident;

ConnectionManager::ConnectionManager()
 : m_serverFd(0), clientId(-1), equipId(-1), isConnect(new bool), receiveData(new string) { *receiveData = ""; }
ConnectionManager::~ConnectionManager() {}
void ConnectionManager::openServer() throw (ConnectionException)
{	openServer(PORT);	}
void ConnectionManager::openServer(int port) throw (ConnectionException)
{
	cout << " Server is Open! " << endl;
	if((m_serverFd = socket(AF_INET, SOCK_STREAM, 0)) == -1){
		cout << "exception in socket" <<endl;;
		throw exception_socket;
	}
	cout << "Server ID is " << m_serverFd << endl;

	memset(&m_serverAddr, 0, sizeof(struct sockaddr_in));		//서버 주소정보 설정
	m_serverAddr.sin_family = AF_INET;
	m_serverAddr.sin_port = htons(port);
	m_serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	if(bind(m_serverFd, (struct sockaddr*)&m_serverAddr, sizeof(struct sockaddr_in)) < 0) {	//소켓 바인딩. 
		cout << "exception in bind" << endl;
		close(m_serverFd);
		throw exception_bind;
	}

	if(listen(m_serverFd, MAX_PENDING) < 0) {	//소켓 클라이언트의 요청 기다릴수있게 설정
		cout << "exception in listen" << endl;
		close(m_serverFd);
		throw exception_listen;
	}
}
void ConnectionManager::closeServer() throw (ConnectionException)
{	
	close(m_serverFd);
}
int ConnectionManager::takeClient() throw (ConnectionException)
{
	struct sockaddr_in clientAddr;
	socklen_t c_len = sizeof(struct sockaddr_in);
	memset(&clientAddr, 0, sizeof(struct sockaddr_in));

	if((clientId = accept(m_serverFd, (struct sockaddr*)&clientAddr, &c_len)) < 0){
		cout << "exception in accept" <<endl;
		throw exception_accept;
	}
	*isConnect = true;
	cout << "client(" << clientId << ":" << inet_ntoa(clientAddr.sin_addr) << ") is join" << endl;
	return clientId;
}
void ConnectionManager::startSendProcess()
{
	pthread_t pid = 0;
	pthread_create(&pid, NULL, AndroidManager::sendImageData, new ThreadArgument(isConnect, clientId, NULL));
//	pthread_detach(pid);
}
void ConnectionManager::startReceiveProcess()
{
	pthread_t pid = 0;
	pthread_create(&pid, NULL, AndroidManager::receiveAngleData, new ThreadArgument(isConnect, clientId, receiveData));
//	pthread_detach(pid);
}
void ConnectionManager::startArduinoProcess()
{
	pthread_t pid = 0;
	pthread_create(&pid, NULL, ArduinoManager::Connect_SendData, new ThreadArgument(isConnect, clientId, receiveData));
//	pthread_detach(pid);
}

package com.example.kang.smartVRDrone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;



import org.opencv.core.Mat;
import org.opencv.core.*;
import org.opencv.imgproc.Subdiv2D;



import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/*
 * Created by Jeonghee Choi on 2016-05-16.
 */
public class ShowImageActivity extends Activity implements SensorEventListener {
    static {
        System.loadLibrary("opencv_java3");
    }

    // 센서 관련 객체
    SensorManager m_sensor_manager;
    Sensor m_acc_sensor, m_mag_sensor;

    int m_check_count = 0;
    // 출력용 텍스트뷰

    //센서 데이터를 저장할 변수들
    float[] m_acc_data = null, m_mag_data = null;
    float[] m_rotation = new float[9];
    float[] m_result_data = new float[3];


    int rowType = 0 ; // 좌우
    int startFlag = 0 ;
    int degree = 0 ;
    int stackH = 90;
    int stackFlag = 0 ;
    int leftRight;
    int updown;
    int posH ;
    int sum ;

    private float mLastX, mLastY, mLastZ;
    private boolean mInitialized;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private final float NOISE = (float) 0.5;


    final static int WIDTH = 240;
    final static int HEIGHT = 180;

    //final static int WIDTH = 240;
   // final static int HEIGHT = 180;

    final static int IMG_SIZE = WIDTH * HEIGHT * 3;
    Handler handler = null;
    Socket tcpSock;
    DataInputStream dis;
    static PrintWriter socket_out;

    static byte[] leftImageByteArr = null;
    static byte[] rightImageByteArr = null;
/*
    static int MAX_SEND_COUNT = 226;
    static int BUFF_SIZE = 1024;
    static int DATA_SIZE = 1020;
*/
    Thread receiveThread;
    Thread sendThread;

    ImageView iv1 = null;
    ImageView iv2 = null;
    boolean isSuc = true;

    final String angleData = "";
    final int count = 0;
    Bitmap leftImageBmp = null;
    Bitmap rightImageBmp = null;
    Mat mat = null;
    Mat mat2 = null;

    static int sendCount = 0;

    public int pow(int a, int b){
        int res=1;
        for(int i =0; i<b;i++) res *=a;
        return res;
    }

    public Bitmap byteToBitmap(byte[] imageByteArr, int width, int height, int startIndex) {
        Bitmap ImageBmp = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888);
        int imageByteArrayIndex = startIndex;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int bit1 = 255&0xFF;
                int bit4 = imageByteArr[imageByteArrayIndex++]&0xFF;
                int bit3 = imageByteArr[imageByteArrayIndex++]&0xFF;
                int bit2 = imageByteArr[imageByteArrayIndex++]&0xFF;
                int byteToInt = (bit1 << 24) + (bit2 << 16) + (bit3 << 8) + bit4;
                //ImageBmp.setPixel(j, i, byteToInt);
                ImageBmp.setPixel(i, width - j-1, byteToInt);
            }
        }
        return ImageBmp;


    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_activity);

        handler = new Handler(Looper.getMainLooper());


        //Mat temp = new Mat();
        // Mat temp2 = new Mat();

        //   Bitmap drawBitmap=null;
        //  Bitmap drawBitmap2=null;

        //MatOfByte matOfByte = new MatOfByte();
        //MatOfByte matOfByte2 = new MatOfByte();

//        byte[] byteArray = null;
        //      byte[] byteArray2 = null;

        iv1 = (ImageView) findViewById(R.id.leftImageView);
        iv2 = (ImageView) findViewById(R.id.rightImageView);

//소켓 열기
        //받는 스레드
        receiveThread = new Thread(new Runnable() {
                public void run() {

                try {
                        tcpSock = new Socket("192.168.0.43", 11129);
                        socket_out = new PrintWriter(tcpSock.getOutputStream(), true);
                        dis = new DataInputStream(tcpSock.getInputStream());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                try {
                    /*
                    tcpSock = MainActivity.tcpSock;
                    socket_out = MainActivity.socket_out;
                    dis = MainActivity.dis;
                    */
                    byte imageBuff[] = new byte[IMG_SIZE*2];
                    byte readBuff[] = new byte[IMG_SIZE*2];
                    int imageBuffIndex = 0;
                    int needByte=0;
                    int remainByte=0;
                    while (!(receiveThread.isInterrupted())) {
                        int imageNumber = 0;
                        while(true) {
                            int receiveSize = dis.read(readBuff, 0, IMG_SIZE*2);
                            if(receiveSize == IMG_SIZE*2 && imageBuffIndex == 0) System.arraycopy(readBuff, 0, imageBuff, 0, IMG_SIZE*2);
                            else
                            {
                                needByte = (IMG_SIZE*2) - imageBuffIndex;
                                remainByte= receiveSize - needByte; // 양수면 남은거 일처리후 채워줘야함, 음수면 무시
                                if(remainByte > 0){
                                    System.arraycopy(readBuff, 0, imageBuff, imageBuffIndex, needByte);
                                    imageBuffIndex = 0;
                                }
                                else{
                                    System.arraycopy(readBuff, 0, imageBuff, imageBuffIndex, receiveSize);
                                    imageBuffIndex = (imageBuffIndex+receiveSize)%(IMG_SIZE*2);
                                    continue;
                                }
                            }
                            leftImageBmp = byteToBitmap(imageBuff, WIDTH, HEIGHT, 0);
                            rightImageBmp = byteToBitmap(imageBuff, WIDTH, HEIGHT, IMG_SIZE);






                            if(remainByte > 0) {
                                System.arraycopy(readBuff, needByte, imageBuff, imageBuffIndex, remainByte);
                                imageBuffIndex += remainByte;
                            }
                            break;


                            /*
                            int receiveSize = dis.read(readBuff, 0, IMG_SIZE);
                            if(receiveSize == IMG_SIZE && imageBuffIndex == 0) System.arraycopy(readBuff, 0, imageBuff, 0, IMG_SIZE);
                            else{
                                needByte = IMG_SIZE - imageBuffIndex;
                                remainByte= receiveSize - needByte; // 양수면 남은거 일처리후 채워줘야함, 음수면 무시
                                if(remainByte > 0){
                                    System.arraycopy(readBuff, 0, imageBuff, imageBuffIndex, needByte);
                                    imageBuffIndex = 0;
                                }
                                else{
                                    System.arraycopy(readBuff, 0, imageBuff, imageBuffIndex, receiveSize);
                                    imageBuffIndex = (imageBuffIndex+receiveSize)%IMG_SIZE;
                                    continue;
                                }
                            }
                            if (imageNumber == 0) {
                                leftImageBmp = byteToBitmap(imageBuff, 240, 180);
                                //Log.i("LEFT : ", imageBuff[0] +imageBuff[1] + imageBuff[2] + imageBuff[3] + imageBuff[4] + imageBuff[5] + imageBuff[6] + imageBuff[7] + imageBuff[8] + imageBuff[9] + imageBuff[10] +imageBuff[11] + imageBuff[12] + imageBuff[13] + imageBuff[14] + imageBuff[15] + imageBuff[16] + imageBuff[17] + imageBuff[18] + imageBuff[19] + "");
                                if(remainByte > 0) {
                                    System.arraycopy(readBuff, needByte, imageBuff, imageBuffIndex, remainByte);
                                    imageBuffIndex += remainByte;
                                }
                                imageNumber++; // true, false 순
                            } else {
                                rightImageBmp = byteToBitmap(imageBuff, 240, 180);
                                //Log.i("RIGHT : ", imageBuff[0] +imageBuff[1] + imageBuff[2] + imageBuff[3] + imageBuff[4] + imageBuff[5] + imageBuff[6] + imageBuff[7] + imageBuff[8] + imageBuff[9] + imageBuff[10] +imageBuff[11] + imageBuff[12] + imageBuff[13] + imageBuff[14] + imageBuff[15] + imageBuff[16] + imageBuff[17] + imageBuff[18] + imageBuff[19] + "");
                                if(remainByte > 0) {
                                    System.arraycopy(readBuff, needByte, imageBuff, imageBuffIndex, remainByte);
                                    imageBuffIndex += remainByte;
                                }
                                break;
                            }
                            */
                        }


                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                iv1.setImageBitmap(leftImageBmp);
                                iv2.setImageBitmap(rightImageBmp);
                            }
                        });

                        /*
                        final byte data[] = new byte[IMG_SIZE];  //들어온 값 임시 저장
                        byte readBuff[] = new byte[BUFF_SIZE];
                        int receiveCnt = 0;
                        boolean imageNumber = false;
                        boolean isPerfect = true;

                        while(true){
                            int receiveSize = 0;
                            dis.read(readBuff, 0, BUFF_SIZE);     //읽어옴
                            for(int i =0; i<4; i++) receiveSize += readBuff[i] * pow(10, 3-i);

                             Log.i("ReceiveSize : ", "" + receiveSize + "/" + receiveCnt);

                            for(int i =4; i<receiveSize+4; i++){
                                data[(DATA_SIZE*receiveCnt)+i-4] = readBuff[i];
                            }
                            receiveCnt++;
                            if(receiveSize != DATA_SIZE){
                                imageNumber = !imageNumber; // true, false 순
                                if(receiveCnt == MAX_SEND_COUNT && isPerfect) {
                                    receiveCnt = 0;
                                    Log.i("receive state : ", "OK - >" + imageNumber);
                                    if (imageNumber) {
                                        leftImageBmp = byteToBitmap(data, 320, 240);
                                        continue;
                                    } else {
                                        rightImageBmp = byteToBitmap(data, 320, 240);
                                        break;
                                    }
                                }
                                else{
                                    Log.i("receive state : ", "ERROR - >" + imageNumber);
                                    isPerfect = false;
                                    if(!imageNumber) break;
                                }
                            }
                        }

                        if(isPerfect) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    iv1.setImageBitmap(leftImageBmp);
                                    iv2.setImageBitmap(rightImageBmp);
                                    isSuc = true;
                                }
                            });
                            while(!isSuc);
                            isSuc = false;
                        }
*/
                    }
                } catch (Exception e) {
                    Log.i("Error MSG : ", e.toString());
                }
            }
        });
        receiveThread.start();
        // 시스템서비스로부터 SensorManager 객체를 얻는다.
        m_sensor_manager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // SensorManager 를 이용해서 가속센서와 자기장 센서 객체를 얻는다.
        m_acc_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_mag_sensor = m_sensor_manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //소켓 열기
        //보내는 스레드
        /*
        sendThread = new Thread() {
            public void run() {
                while (true) {
                    //Thread.sleep(1000);
                    //socket_out.println(angleData);
                    //Log.i("A", "send 함수의 angleData : " + angleData);
                    Log.i("A", "send 함수의 angleData : ");
                    //prepareReceiveData();
                }
            }
        };
        sendThread.start();
        */

        /*
        try {

            drawImage = Utils.loadResource(this, R.drawable.image11, Imgcodecs.CV_LOAD_IMAGE_COLOR);
            drawImage2 = Utils.loadResource(this, R.drawable.iamge10, Imgcodecs.CV_LOAD_IMAGE_COLOR);

            Imgproc.resize(drawImage, temp, new Size(1280, 1440));  //사이즈 변경
            Imgproc.resize(drawImage2, temp2, new Size(1280, 1440));  //사이즈 변경

            Imgcodecs.imencode(".png", temp, matOfByte);
            Imgcodecs.imencode(".png", temp2, matOfByte2);

            byteArray = matOfByte.toArray();
            byteArray2 = matOfByte2.toArray();

            //drawBitmap = Bitmap.createBitmap(temp.cols(), temp.rows(), Bitmap.Config.RGB_565);  mat -> bitmap
            //drawBitmap2 = Bitmap.createBitmap(temp.cols(), temp.rows(), Bitmap.Config.RGB_565);

            //Utils.matToBitmap(temp, drawBitmap);
            //Utils.matToBitmap(temp2, drawBitmap2);

            //ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  bitmap -> byte[]
            //drawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream) ;
            //byteArray = stream.toByteArray() ;


            //byte[] -> bitemap
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap bmp2 = BitmapFactory.decodeByteArray(byteArray2, 0, byteArray2.length);

            ImageView iv1 = (ImageView) findViewById(R.id.leftImageView);
            iv1.setImageBitmap(bmp);

            ImageView iv2 = (ImageView) findViewById(R.id.rightImageView);
            iv2.setImageBitmap(bmp2);

        } catch (IOException e) {
            e.printStackTrace();
        }
*/


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowImageActivity.this);

                builder.setTitle("종료")        // 제목 설정
                        .setMessage("어플을 종료하시겠습니까?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String exitStr = "EXIT";
                                send(exitStr);  //종료 서버에 알림
                                receiveThread.interrupt();
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

                //String exitStr = "EXIT";
                //send(exitStr);  //종료 서버에 알림
               // receiveThread.interrupt();
                //finish();


            default:
                return false;
        }
    }
    // 해당 액티비티가 포커스를 얻으면 가속 데이터와 자기장 데이터를 얻을 수 있도록
    // 리스너를 등록한다.
    protected void onResume() {
        super.onResume();
        m_check_count = 0;

        // 센서 값을 이 컨텍스트에서 받아볼 수 있도록 리스너를 등록한다.
        m_sensor_manager.registerListener(this, m_acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        m_sensor_manager.registerListener(this, m_mag_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // 해당 액티비티가 포커스를 잃으면 가속 데이터와 자기장 데이터를 얻어도 소용이 없으므로
    // 리스너를 해제한다.
    protected void onPause() {
        super.onPause();
        // 센서 값이 필요하지 않는 시점에 리스너를 해제해준다.
        m_sensor_manager.unregisterListener(this);
    }

    // 정확도 변경시 호출되는 메소드. 센서의 경우 거의 호출되지 않는다.
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    // 측정한 값을 전달해주는 메소드.
    public void onSensorChanged(SensorEvent event)
    {



        if ( sendCount == 1000000 )
            sendCount = 0 ;

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                // 가속 센서가 전달한 데이터인 경우
                // 수치 데이터를 복사한다.
                m_acc_data = event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // 자기장 센서가 전달한 데이터인 경우
                // 수치 데이터를 복사한다.
                m_mag_data = event.values.clone();
            }

              String str = "";
            // 데이터가 존재하는 경우
            if (m_acc_data != null && m_mag_data != null) {
                // 가속 데이터와 자기장 데이터로 회전 매트릭스를 얻는다.
                SensorManager.getRotationMatrix(m_rotation, null, m_acc_data, m_mag_data);
                // 회전 매트릭스로 방향 데이터를 얻는다.
                SensorManager.getOrientation(m_rotation, m_result_data);

                // Radian 값을 Degree 값으로 변환한다.
                m_result_data[0] = (float) Math.toDegrees(m_result_data[0]);

                // 0 이하의 값인 경우 360을 더한다.
                if (m_result_data[0] < 0) m_result_data[0] += 360;

                // 첫번째 데이터인 방위값으로 문자열을 구성하여 텍스트뷰에 출력한다.
                leftRight = (int)m_result_data[0];





                if ( startFlag == 0 )
                {
                    startFlag = 1 ;

                    degree = leftRight - 90 ;

                    if ( 90 <= leftRight && leftRight <= 270 )
                    {
                        rowType = 2;
                        // degree = 90 ;
                    }
                    else if ( leftRight < 90 )
                    {
                        rowType = 1;
                    }
                    else
                    {
                        rowType = 3;
                    }
                }

                if ( rowType == 1 )
                {
                    stackFlag = 0 ;

                    if ( 360 + degree <= leftRight && leftRight <= 360 )// 0~tempH
                    {
                        posH = -degree - (360-leftRight);
                    }
                    else if ( 0 <= leftRight && leftRight <= 180+degree ) // tempH~180
                    {
                        posH = leftRight - degree ;
                    }
                    else
                    {
                        posH = stackH;
                        stackFlag = 1 ;
                    }
                }
                else if ( rowType == 2 )
                {
                    sum = leftRight - degree ;

                    stackFlag = 0 ;

                    if ( sum < 0 )
                    {
                        stackFlag = 1 ;

                        posH = stackH ;
                    }
                    else if ( sum > 180 )
                    {
                        stackFlag = 1 ;

                        posH = stackH;
                    }
                    else
                    {
                        posH = sum ;
                    }
                }
                else //type 3
                {
                    stackFlag = 0 ;

                    if ( degree <= leftRight && leftRight <= 360 )// 0~tempH
                    {
                        posH = leftRight - degree ;
                    }
                    else if ( 180 <= degree-leftRight && degree-leftRight <= degree ) // tempH~180
                    {
                        posH = (360-degree)+ leftRight ;
                    }
                    else
                    {
                        posH = stackH;
                        stackFlag = 1 ;
                    }
                }


                if ( stackFlag == 0) {
                    posH = 180 - posH; //좌우반전
                }

               ;


                stackH = posH ;




                // 세번째 데이터인 좌우 회전 값을 Degree 로 변환한 후 문자열을 구성하여 출력한다.
                updown = (int)Math.toDegrees(m_result_data[2]);

                if ( updown <= 0 )
                {
                    updown = 180 + updown ;
                }
                else
                {
                    updown = 180 - updown ;
                }


                str = updown + "|" + posH + "#";

            }


        sendCount++;

        if ( sendCount % 4 != 0 )
            return ;

            send(str);
            Log.i("A", updown + "|" + posH + "#");

        }


    //서버로 메시지 전송 함수
    static public void send(String msg)
    {
        if(socket_out != null && msg != null)
        {
            socket_out.println(msg);
            Log.i("A", "send 함수의 msg : " + msg);
        }
    }

}

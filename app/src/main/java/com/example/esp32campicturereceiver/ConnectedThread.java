package com.example.esp32campicturereceiver;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    private Handler mHandler;
    String TAG = "bluetooth setup";

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        boolean intoPictureReceptionMode = false;

        // Keep listening to the InputStream until an exception occurs.
        int sizeOfTheImage =0;
        int totalNumberOfBytesReceived =0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                String readMessage = new String(mmBuffer, 0, numBytes);






                if(intoPictureReceptionMode){
                    //now we will write the arriving packets into a ByteArrayOutputStream  and count them.
                    //when the total number of bytes received will be >= to sizeOfTheImage we will now the transmission of the picture has ended
                    outputStream.write(mmBuffer,0,numBytes);
                    totalNumberOfBytesReceived+=numBytes;
                    if (totalNumberOfBytesReceived>=sizeOfTheImage){ // image transmission is finished
                        Log.d(TAG,"finished receving picture");
                        readMessage=""; // to get out of the picture receiving mode at the next loop iteration
                        byte [] imageUnderTheFormOfAnArrayOfBytes = outputStream.toByteArray(); // converting the outputstream to a byte array
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageUnderTheFormOfAnArrayOfBytes,0,imageUnderTheFormOfAnArrayOfBytes.length);
                        Message readMsg = mHandler.obtainMessage(
                                1, numBytes, 1, // we use to arg2 to tell the handler we are dealing with an image
                                bitmap);

                        //cleaning for the next picture
                        sizeOfTheImage=0;
                        totalNumberOfBytesReceived=0;
                        outputStream = new ByteArrayOutputStream();
                        intoPictureReceptionMode=false;



                        readMsg.sendToTarget();
                    }

                }
                else{

                Message readMsg = mHandler.obtainMessage(
                        1, numBytes, -1,
                        readMessage);
                readMsg.sendToTarget();
                }


                if (readMessage.contains("picture arriving")){ // getting the image and sending it to the handler
                    //1st step getting the size of the image
                    Log.d(TAG,"getting into picture reception mode");
                    intoPictureReceptionMode=true;
                    try{
                        sizeOfTheImage =Integer.parseInt( readMessage.split(",",2)[1]);
                    }
                    catch (Exception e){
                        Log.d(TAG,"wasn't able to extract size of the incoming picture, will switch to text reception mode");
                        intoPictureReceptionMode=false;
                    }
                    readMessage=""; // to not get into this condition on the next iteration
                }
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}

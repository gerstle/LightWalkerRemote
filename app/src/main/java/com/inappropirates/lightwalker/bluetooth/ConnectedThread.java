package com.inappropirates.lightwalker.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.inappropirates.lightwalker.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ConnectedThread extends Thread
{
    private BroadcastReceiver receiver;
    private BluetoothSocket mmSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private Boolean connected = true;
    private Handler statusHandler;
    private static Object sharedLock = new Object();

    public ConnectedThread(Context context, Handler statusHandler, BluetoothSocket socket, String socketType)
    {
        Log.d(Util.TAG, "create ConnectedThread: " + socketType);

        this.statusHandler = statusHandler;

        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try
        {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e)
        {
            Log.e(Util.TAG, "temp sockets not created", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void setHandler(Handler statusHandler)
    {
        this.statusHandler = statusHandler;
    }

    public void run()
    {
        Log.i(Util.TAG, "BEGIN connectedThread");
        byte[] buffer = new byte[1024];
        int bytes, byteIndex = 0;
        Message m = null;

        while (connected)
        {
            boolean eom = false;
            try
            {
                while (!eom && (mmInStream != null))
                {
                    // Read from bluetooth
                    if (mmInStream.available() > 0)
                    {
                        bytes = mmInStream.read(buffer, byteIndex, 1024 - byteIndex);
                        byteIndex += bytes;
                        if (buffer[byteIndex - 1] == '\r')
                            eom = true;
                    }
                }

            } catch (IOException e)
            {
                Log.e(Util.TAG, "io exception disconnected", e);
                cancel();
            } catch (Exception e)
            {
                Log.e(Util.TAG, "disconnected", e);
                cancel();
            }

            if (byteIndex > 0)
            {
//                Log.i(Util.TAG, "Received: " + new String(buffer, 0, (byteIndex - 1)));
                String message = new String(buffer).substring(0, byteIndex - 1);
                m = statusHandler.obtainMessage(BluetoothMessageEnum.READ.ordinal(), message);
                m.sendToTarget();
            }
            byteIndex = 0;
        }
    }

    public void cancel()
    {
        synchronized (sharedLock)
        {
            if (mmInStream != null)
            {
                try
                {
                    mmInStream.close();
                } catch (IOException e)
                {
                    Log.e(Util.TAG, "close() of input stream failed", e);
                }
                mmInStream = null;
            }

            if (mmOutStream != null)
            {
                try
                {
                    mmOutStream.close();
                } catch (IOException e)
                {
                    Log.e(Util.TAG, "close() of output stream failed", e);
                }
                mmOutStream = null;
            }

            if (mmSocket != null)
            {
                try
                {
                    mmSocket.close();
                } catch (IOException e)
                {
                    Log.e(Util.TAG, "close() of connect socket failed", e);
                }
                mmSocket = null;
            }

            if (connected)
            {
                connected = false;

                // Send a failure message back to the Activity
                Message msg = statusHandler.obtainMessage(BluetoothMessageEnum.TOAST.ordinal());
                Bundle bundle = new Bundle();
                bundle.putString(BluetoothHandler.TOAST, "Device connection was lost");
                msg.setData(bundle);
                statusHandler.sendMessage(msg);
                statusHandler.obtainMessage(BluetoothMessageEnum.STATE_CHANGE.ordinal(), BluetoothStatusEnum.DISCONNECTED.ordinal(), -1).sendToTarget();
            }
        }
    }

    public boolean send(String message)
    {
        synchronized (sharedLock)
        {
            if (connected && mmOutStream != null)
            {
                try
                {
                    mmOutStream.write(message.getBytes());
                    mmOutStream.write("\r".getBytes());
                    mmOutStream.flush();
                    return true;
                } catch (IOException e)
                {
                    Log.e(Util.TAG, "error writing!");
                    e.printStackTrace();
                    return false;
                }
            }
            return false;
        }
    }
}

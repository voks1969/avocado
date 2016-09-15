/**
 * Created by voks1969 on 5/12/2016.
 */

package com.example.voks1969.aidlsample;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * This AddService class exposes the remote service (functions in AIDL file, which we need to expose to other apps) to the client
 */
public class MyService extends Service {
    private static final String TAG = "AddService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent) {

        return new IMyService.Stub() {
            /**
             * In the AIDL file we just add the declaration of the function
             * here is the real implementation of the add() function below
             */
            public int add(int ValueFirst, int valueSecond) throws RemoteException {
                Log.i(TAG, String.format("AddService.add(%d, %d)",ValueFirst, valueSecond));
                return (ValueFirst + valueSecond);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}

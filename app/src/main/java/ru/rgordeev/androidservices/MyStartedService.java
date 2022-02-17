package ru.rgordeev.androidservices;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyStartedService extends Service {

    private static final String TAG = MyStartedService.class.getSimpleName();

    private Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate, Thread name " + Thread.currentThread().getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand, Thread name " + Thread.currentThread().getName());

        // Perform Tasks [ Short Duration Task: Don't block the UI ]

        int sleepTime = intent.getIntExtra("sleepTime", 1);
        messenger = (Messenger) intent.getExtras().get("messenger");

        new MyAsyncTask().execute(sleepTime);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "onBind, Thread name " + Thread.currentThread().getName());
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy, Thread name " + Thread.currentThread().getName());
    }

    // AsyncTask class declaration
    class MyAsyncTask extends AsyncTask<Integer, String, Void> {

        private String[] cats = new String[] {"cat1","cat2","cat3"};
        private final String TAG = MyAsyncTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.i(TAG, "onPreExecute, Thread name " + Thread.currentThread().getName());
        }

        @Override // Perform our Long Running Task
        protected Void doInBackground(Integer... params) {
            Log.i(TAG, "doInBackground, Thread name " + Thread.currentThread().getName());

            int sleepTime = params[0];

            int ctr = 1;

            // Dummy Long Operation
            while(ctr <= sleepTime) {
                publishProgress(cats[ctr % 3]);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctr++;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Toast.makeText(MyStartedService.this, values[0], Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Counter Value " + values[0]+ " onProgressUpdate, Thread name " + Thread.currentThread().getName());

            Message message = Message.obtain();
            message.obj = values[0];

            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            stopSelf(); // Destroy the Service from within the Service class itself
            Log.i(TAG, "onPostExecute, Thread name " + Thread.currentThread().getName());

        }
    }
}


/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.mobileperf.battery;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MyJobService extends JobService {
    private static final String LOG_TAG = "MyJobService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "MyJobService created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "MyJobService destroyed");
    }

    @Override
    public boolean onStartJob(@NonNull JobParameters params) {
        // This is where you would implement all of the logic for your job. Note that this runs
        // on the main thread, so you will want to use a separate thread for asynchronous work
        // (as we demonstrate below to establish a network connection).
        // If you use a separate thread, return true to indicate that you need a "reschedule" to
        // return to the job at some point in the future to finish processing the work. Otherwise,
        // return false when finished.
        Log.i(LOG_TAG, "Totally and completely working on job " + params.getJobId());
        // First, check the network, and then attempt to connect.
        if (isNetworkConnected()) {
            SimplerDownloadTask task = new SimplerDownloadTask(params);
            FutureTask<String> futureString = new FutureTask<>(task);
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(futureString);
            try {
                Log.i(LOG_TAG, futureString.get());
            } catch (Exception err) {
                err.printStackTrace();
            }
            return true;
        } else {
            Log.i(LOG_TAG, "No connection on job " + params.getJobId() + "; sad face");
        }
        return false;
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters params) {
        // Called if the job must be stopped before jobFinished() has been called. This may
        // happen if the requirements are no longer being met, such as the user no longer
        // connecting to WiFi, or the device no longer being idle. Use this callback to resolve
        // anything that may cause your application to misbehave from the job being halted.
        // Return true if the job should be rescheduled based on the retry criteria specified
        // when the job was created or return false to drop the job. Regardless of the value
        // returned, your job must stop executing.
        Log.i(LOG_TAG, "Whelp, something changed, so I'm calling it on job " + params.getJobId());
        return false;
    }

    /**
     * Determines if the device is currently online.
     */
    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetwork() != null;
    }

    /**
     *  Uses AsyncTask to create a task away from the main UI thread. This task creates a
     *  HTTPUrlConnection, and then downloads the contents of the webpage as an InputStream.
     *  The InputStream is then converted to a String, which is logged by the
     *  onPostExecute() method.
     */

    private static class SimplerDownloadTask implements Callable<String> {

        @SuppressWarnings("FieldCanBeLocal")
        private JobParameters mJobParams;

        public SimplerDownloadTask(@NonNull JobParameters... params) {
            mJobParams = params[0];
        }

        @Override
        public String call() {
            InputStream inputStream;
            Reader reader;
            final int length = 50;
            char[] buffer = new char[length];
            final int readTimeout = 10000;
            final int connectTimeout = 15000;
            final String requestType = "GET";
            int response, readCount;
            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(readTimeout);
                connection.setConnectTimeout(connectTimeout);
                connection.setRequestMethod(requestType);
                connection.connect();
                response = connection.getResponseCode();
                Log.d(LOG_TAG, "The response is: " + response);
                inputStream = connection.getInputStream();
                reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8.name());
                readCount = reader.read(buffer);
                if (readCount != length) Log.w(LOG_TAG, "Read count was " + readCount);
                inputStream.close();
                connection.disconnect();
            } catch (Exception err) {
                err.printStackTrace();
            }
            return new String(buffer);
        }
    }
}

package com.example.android.mydiaryapplication;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by OKUNIYI MONSURU on 6/27/2018.
 */

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor primaryExecutor;
    private final Executor mainThreadExecutor;

    private AppExecutors(Executor primaryExecutor, Executor mainThreadExecutor) {
        this.primaryExecutor = primaryExecutor;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    /**
     *Ensures that only one instance of this class is created
     */
    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor getPrimaryExecutor() {
        return primaryExecutor;
    }

    public Executor getMainThreadExecutor(){
        return mainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}

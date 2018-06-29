package com.example.android.mydiaryapplication;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by OKUNIYI MONSURU on 6/27/2018.
 */

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor mainExecutor;

    private AppExecutors(Executor mainExecutor) {
        this.mainExecutor = mainExecutor;
    }

    /**
     *Ensures that only one instance of this class is created
     */
    public static AppExecutors getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor mainExecutor() {
        return mainExecutor;
    }
}

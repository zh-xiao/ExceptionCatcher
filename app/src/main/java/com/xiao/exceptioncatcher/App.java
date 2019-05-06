package com.xiao.exceptioncatcher;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ExceptionCatcher.start(new ExceptionCatcher.ThrowableHandler() {
            @Override
            public void handleThrowable(Throwable throwable) {
            }
        });
    }
}

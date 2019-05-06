package com.xiao.exceptioncatcher;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class ExceptionCatcher {

    private static Handler sHandler;

    /**
     * 开发者可以自定义异常处理 eg.提示用户,上报
     */
    interface ThrowableHandler {
        void handleThrowable(Throwable throwable);
    }

    /**
     * 接管异常,防止应用崩溃
     *
     * @param throwableHandler 开发者可以自定义异常处理 eg.提示用户,上报
     */
    public static void start(final ThrowableHandler throwableHandler) {
        //接管主线程异常
        sHandler = new Handler(Looper.getMainLooper());
        sHandler.post(new Runnable() {
            @Override
            public void run() {
                //死循环,确保主线程发生异常(不影响主线程继续运行的异常)可以catch住然后继续运行
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Exception exception) {
                        Log.e("AndroidRuntime","FATAL EXCEPTION: Thread-main",exception);
                        throwableHandler.handleThrowable(exception);
                        if (isFatalException(exception)) {
                            //如果异常影响到主线程继续运行,干掉应用
                            System.exit(0);
                        }
                    }
                }
            }
        });

        //接管全局异常(因为前面已经接管了主线程,所以这里接管的只是子线程异常)
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, final Throwable e) {
                sHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        throwableHandler.handleThrowable(e);
                    }
                });
            }
        });
    }

    /**
     * 判断异常是否会影响到主线程继续执行
     *
     * @param exception
     * @return
     */
    public static boolean isFatalException(Exception exception) {
        StackTraceElement[] elements = exception.getStackTrace();
        for (int i = elements.length - 1; i > -1; i--) {
            if (elements.length - i > 20) {
                return false;
            }
            StackTraceElement element = elements[i];
            if ("android.view.Choreographer".equals(element.getClassName())
                    && "Choreographer.java".equals(element.getFileName())
                    && "doFrame".equals(element.getMethodName())) {
                return true;
            }
        }
        return false;
    }
}

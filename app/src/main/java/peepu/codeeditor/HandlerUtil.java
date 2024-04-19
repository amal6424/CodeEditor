package peepu.codeeditor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;


public abstract class HandlerUtil {
    private HandlerThread handlerThread;
    private Handler handler;

    public HandlerUtil(String name) {
        
        
        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                onResponse(msg);
            }
        };
        this.handlerThread = new HandlerThread(name) {
            @Override
            public void run() {
                onRun();
            }
        };
    }

    public Handler getHandler() {
        return this.handler;
    }

    public abstract void onResponse(Message msg);

    public abstract void onRun();

    public void sendMessage(int arg1, int arg2, Object object){
        this.handler.sendMessage(getMessage(arg1,arg2,object));
    }

    public static Message getMessage(int arg1, int arg2, Object object) {
        Message message = new Message();
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = object;
        return message;
    }

    public void start() {
        handlerThread.start();
    }
}

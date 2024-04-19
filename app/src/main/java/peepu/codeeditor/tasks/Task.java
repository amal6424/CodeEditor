package peepu.codeeditor.tasks;

import android.content.Context;
import android.os.Message;

import androidx.appcompat.app.AlertDialog;

import peepu.codeeditor.HandlerUtil;
import peepu.codeeditor.dialogs.ProgressDialog;

public abstract class Task extends HandlerUtil {
    private final int PROGRESS = 10;
    private final int START = 14;
    private final int COMPLETE = 11;
    private final int MAX_PROGRESS = 12;
    private final int MESSAGE = 13;
    private final int ERROR = 15;
    private final int TITLE = 16;

    private ProgressDialog pg;
    private Context ctx;

    private boolean errorOnExecution = false;

    public Task(Context ctx,String name){
        super(name);
        pg = new ProgressDialog(ctx);
        this.ctx = ctx;
        pg.setTitle(name);
        pg.setMessage("Please wait");
    }

    @Override
    public void onRun() {
        startTask();
        Object obj = onExecution();
        finishTask(obj);
    }
    private void startTask(){
        sendMessage(START,0,null);
    }
    private void finishTask(Object obj){
        sendMessage(COMPLETE, 0, obj);
    }
    @Override
    public void onResponse(Message msg) {
        switch(msg.arg1){
            case PROGRESS:
                pg.setProgress(msg.arg2);
                break;
            case COMPLETE:
                pg.dismiss();
                if(!errorOnExecution){
                    onPostExecute(msg.obj);
                }
                break;
            case MAX_PROGRESS:
                pg.setMaxProgressValue(msg.arg2);
                break;
            case MESSAGE:
                pg.setMessage(msg.obj.toString());
                break;
            case ERROR:
                errorOnExecution = true;
                showErrorMessage(msg.obj.toString());
                break;
            case START:
                pg.show();
                onPreExecute();
                errorOnExecution = false;
                break;
            case TITLE:
                pg.setTitle(msg.obj.toString());
                break;
        }
    }
    public void sendMessage(String mesaage){
        Message msg = new Message();
        msg.arg1 = MESSAGE;
        msg.obj = mesaage;
        getHandler().sendMessage(msg);
    }
    public void sendErrorMessage(String mesaage){
        Message msg = new Message();
        msg.arg1 = ERROR;
        msg.obj = mesaage;
        getHandler().sendMessage(msg);
    }
    private void showErrorMessage(String msg){
        AlertDialog.Builder ab = new AlertDialog.Builder(ctx);
        ab.setTitle("Error");
        ab.setMessage(msg);
        ab.show();
    }
    public abstract void onPreExecute();
    public abstract void onPostExecute(Object result);
    public abstract Object onExecution();

    @Override
    public void start() {
        onPreExecute();
        super.start();
    }
}

package peepu.codeeditor.dialogs;
import peepu.codeeditor.R;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDialog {
    private Context ctx;
    private AlertDialog ab;
    private TextView title, message;
    private ProgressBar progressBar;

    public ProgressDialog(Context ctx){
        this.ctx = ctx;
        ab = new AlertDialog.Builder(ctx).create();
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.progress_dialog, null, false);
        title = view.findViewById(R.id.title);
        message = view.findViewById(R.id.message);
        progressBar = view.findViewById(R.id.progressBar);

        ab.setView(view);

    }
    public void setTitle(String text){
        title.setText(text);
    }
    public void setMessage(String text){
        message.setText(text);
    }
    public ProgressBar getProgressBar(){
        return progressBar;
    }
    public void setMaxProgressValue(int max){
        progressBar.setMax(max);
    }
    public void setProgress(int pg){
        progressBar.setProgress(pg);
    }
    public void setCancelable(boolean cancel){
        ab.setCancelable(cancel);
    }
    public void show(){
        ab.show();
    }
    public void dismiss(){
        ab.cancel();
    }

}

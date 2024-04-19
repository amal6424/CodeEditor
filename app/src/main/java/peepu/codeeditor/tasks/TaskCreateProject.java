package peepu.codeeditor.tasks;

import android.content.Context;
import android.os.Message;
import android.widget.Toast;

import peepu.codeeditor.HandlerUtil;
import peepu.codeeditor.project.Project;

public abstract class TaskCreateProject extends Task {
    private Context ctx;
    private Project project;
    public TaskCreateProject(Context ctx, Project project) {
        super(ctx, project.getProjectName());
        this.ctx = ctx;
        this.project = project;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public abstract void onPostExecute(Object result);

    @Override
    public Object onExecution() {
        try{
            if (project.getProjectName().equals("")) {
                sendErrorMessage("This Project Cannot be created because of empty name");
                return null;
            }
            if (!project.create()) {
                sendErrorMessage("This project cannot be created");
                return null;
            }
        }catch (Exception e){
            sendErrorMessage(e.toString());
        }
        return null;
    }
}

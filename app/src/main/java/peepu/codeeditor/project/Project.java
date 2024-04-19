package peepu.codeeditor.project;

import android.util.Log;

import java.io.File;

import peepu.codeeditor.Tag;

public class Project {
    private Type type = Type.JAVA;
    public static enum Type{
        NATIVE, JAVA, UNKNOWN
    }
    private File path;
    public Project(File path){
        this.path = path;
        validateProject();
    }
    public Project(String path){
        this.path = new File(path);
        validateProject();
    }
    public boolean isValidProject(){
        validateProject();
        switch (type){
            case NATIVE:
                return true;
            case JAVA:
                return true;
            case UNKNOWN:
                return false;
        }
        return false;
    }
    public String getProjectName(){
        return path.getName();
    }

    public void setPath(File path) {
        this.path = path;
    }
    public File getBuildPath(){
        return new File(path, "/build");
    }

    public File getPath() {
        //String out = "/data/data/peepu.codeeditor/usr";
        //return new File(out);
        return path;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    public static interface ProjectListener{
        void onProjectOpened(Project project);
        void onProjectClosed(Project project);
    }
    public boolean create() throws Exception{
        Log.d(Tag.DEBUG, path.toString());
        if(!path.exists()){
            if(!path.mkdirs()){
                return false;
            }
        }
        switch (type){
            case JAVA:
                ProjectCreator.createJavaConsoleApp(this);
                break;
            case NATIVE:
                ProjectCreator.createNativeConsoleApp(this);
                try {
                	new ProjectSettings(this).writeSettings();
                } catch(Exception err) {
                	
                }
                break;
        }
        return true;
    }
    
    public File getSrcPath(){
        switch(type){
            case JAVA:
            return new File(getPath(), "/src/main/java");
            case NATIVE:
            return new File(getPath(), "/src/main/native");
            case UNKNOWN:
            return null;
        }
        return null;
    }

    private void validateProject(){
        File java = new File(path, "src/main/java");
        File cpp = new File(path, "src/main/native");
        if(java.exists()&&java.isDirectory()){
            type = Type.JAVA;
        }else if(cpp.exists()&&cpp.isDirectory()){
            type = Type.NATIVE;
        }else {
            type = Type.UNKNOWN;
        }
    }
}

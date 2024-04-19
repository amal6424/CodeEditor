package peepu.codeeditor.project;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProjectSettings {
    private Project project;
    private File buildSettingFile;
    private JSONObject json = null;
    private final String compileOptions ="compileOptions";
    private final String buildOptions = "buildOptions";
    public ProjectSettings(Project project){
        this.project = project;
        init();
    }
    private void init(){
        File buildPath = project.getBuildPath();
        if(!buildPath.exists()){
            buildPath.mkdirs();
        }
        buildSettingFile = new File(buildPath, "settings.json");
        if(!buildSettingFile.exists()){
            try{
                createNewDefaultSettings();
                    }catch(Exception ee){}
        }else{
            try{
                json = new JSONObject(FileUtils.readFileToString(buildSettingFile));
                //JSONObject array = new JSONObject(json);
            }catch(Exception e){
                try{
                createNewDefaultSettings();
                    }catch(Exception ee){}
            }
        }
        
    }
    private void createNewDefaultSettings()throws Exception{
        JSONArray cOpts = new JSONArray("[]");
        cOpts.put("-Wall");
        cOpts.put("-fPIE");
        JSONArray bOpts = new JSONArray("[]");
        bOpts.put("-pie");
        bOpts.put("-lc++_static");

        json = new JSONObject("{}");
        json.put(compileOptions, cOpts);
        json.put(buildOptions, bOpts);
    }
    public void addCompileOption(String option) throws Exception{
        JSONArray array=json.getJSONArray(compileOptions);
        array.put(option);
    }
    public void addBuildOption(String option) throws Exception{
        JSONArray array=json.getJSONArray(buildOptions);
        array.put(option);
    }
    public void writeSettings()throws Exception{
        FileUtils.write(buildSettingFile, json.toString());
    }
    public String getCompileOptions()throws Exception{
        JSONArray array = json.getJSONArray(compileOptions);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<array.length();i++){
            sb.append(array.get(i)).append(" ");
        }
        return sb.toString();
    }
    public String getBuildOptions()throws Exception{
        JSONArray array = json.getJSONArray(buildOptions);
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<array.length();i++){
            sb.append(array.get(i)).append(" ");
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        return json.toString();
    }
    
}

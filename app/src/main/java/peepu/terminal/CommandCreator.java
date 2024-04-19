package peepu.terminal;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import peepu.codeeditor.project.Project;
import peepu.codeeditor.project.ProjectSettings;

public class CommandCreator {
    public String lulu;
    public static void createMakeFile(Project project, List<File> srcFiles) throws Exception{
        ProjectSettings settings = new ProjectSettings(project);
        String buildDir = project.getPath().toString()+"/build";
        String objects = getObjects(srcFiles);
        StringBuilder shellCode = new StringBuilder()
        .append("cp "+buildDir+"/main $TMPDIR/main\n")
        .append("chmod 755 $TMPDIR/main\n")
        .append("echo \"\n\n\"\n")
        .append("$TMPDIR/main\n");
        
        StringBuilder makeCode = new StringBuilder("all: main\n")
        .append("\tsh run.sh\n")
        .append("main: ").append(objects).append("\n")
        .append("\tg++")
        .append(" -o main ").append(objects)
        .append(" ")
        .append(settings.getBuildOptions().substring(0, settings.getBuildOptions().length()-1))
        .append("\n")
        
        .append(createObjectRules(srcFiles, settings));
        
        File makeFile = new File(buildDir,"Makefile");
        File shellFile = new File(buildDir,"run.sh");
        if(!makeFile.getParentFile().exists()){
            makeFile.getParentFile().mkdirs();
        }
        FileUtils.write(makeFile, makeCode.toString());
        FileUtils.write(shellFile, shellCode);
        
    }
    private static String getObjects(List<File> files){
        StringBuilder objects = new StringBuilder();
        for(File file: files){
            objects.append(file.getName()).append(".o ");
        }
        objects.deleteCharAt(objects.length()-1);
        return objects.toString();
    }
    private static String createObjectRules(List<File> files, ProjectSettings settings)throws Exception {
    	StringBuilder rule = new StringBuilder();
        for(File file:files){
            String obj = file.getName()+".o";
            rule.append(obj).append(": ")
            .append(file).append("\n")
            
            .append("\tg++ ")
            .append(settings.getCompileOptions())
            .append("-c -o ")
            .append(obj).append(" ")
            .append(file).append("\n");
        }
        return rule.toString();
    }
    
}

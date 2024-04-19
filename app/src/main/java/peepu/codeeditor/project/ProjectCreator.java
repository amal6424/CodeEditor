package peepu.codeeditor.project;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ProjectCreator {
    public static void createJavaConsoleApp(Project project) throws Exception{
        File file = new File(project.getPath(), "src/main/java");
        if(!file.mkdirs()){
            throw new Exception("cannot create files");
        }else{
            file = new File(file, "Main.java");
            StringBuilder sb = new StringBuilder();
            sb.append("public class Main{\n")
                    .append("    public static void main(String[] args){\n")
                    .append("        System.out.println(\"Hello, World!\");\n")
                    .append("    }\n")
                    .append("}");
            writeString(sb.toString(), file);
        }
    }
    public static void createNativeConsoleApp(Project project) throws Exception{
        File file = new File(project.getPath(), "src/main/native");
        if(!file.mkdirs()){
            throw new Exception("cannot create files");
        }else{
            file = new File(file, "main.cpp");
            StringBuilder sb = new StringBuilder();
            sb.append("#include <stdio.h>\n\n")
                    .append("int main(int argc, char** argv){\n")
                    .append("    printf(\"Hello, World!\\n\");\n")
                    .append("    return 0;\n")
                    .append("}");
            writeString(sb.toString(), file);
        }
    }
    private static void writeString(String string, File file) throws IOException {
        FileUtils.writeByteArrayToFile(file, string.getBytes());
    }
}

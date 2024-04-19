package peepu.codeeditor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;

public class Utils {
    public static void startFragment(AppCompatActivity activity, Fragment fragment, boolean attachToBackStack, int targetContainer){
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(attachToBackStack){
            transaction.addToBackStack(""+System.nanoTime());
        }
        transaction.replace(targetContainer, fragment);
        transaction.commit();
    }
    public static void unZip(InputStream inputStream, String outDir)throws Exception{
        ZipInputStream in = new ZipInputStream(inputStream);
        ZipEntry entry;
        while((entry = in.getNextEntry())!=null){
            File out = new File(outDir, entry.getName());
            if(entry.isDirectory()){
                out.mkdirs();
            }else{
                FileOutputStream fOut = new FileOutputStream(out);
                byte buffer[] = new byte[4096];
                int len;
                while((len=in.read(buffer))>0) {
                	fOut.write(buffer,0,len);
                }
                fOut.close();
                fOut.flush();
            }
            in.closeEntry();
        }
        in.close();
    }
    public static void unZip(File file, String outDir) throws Exception{
        unZip(new FileInputStream(file), outDir);
    }
    
    public static void setExecutable(File file, boolean recursive){
        file.setExecutable(true);
        if(file.isDirectory()&&recursive){
            File files[] = file.listFiles();
            if(files != null){
                for(File f:files){
                    setExecutable(f,recursive);
                }
            }
        }
    }
}

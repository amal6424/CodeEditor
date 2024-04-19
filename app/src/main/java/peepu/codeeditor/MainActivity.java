package peepu.codeeditor;

import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.tyron.javacompletion.JavaCompletions;
import com.tyron.javacompletion.options.JavaCompletionOptionsImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import peepu.codeeditor.databinding.ActivityMainBinding;
import peepu.codeeditor.fragments.FCodeEditor;
import peepu.codeeditor.fragments.FProjectManager;
import peepu.codeeditor.project.Project;
import peepu.codeeditor.tasks.Task;
import peepu.terminal.CommandCreator;

public class MainActivity extends AppCompatActivity  {

	private ActivityMainBinding binding;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FProjectManager fProjectManager = new FProjectManager();
    private FCodeEditor fCodeEditor;
    private FileManager fileManager;
    
    private Menu menuAction;
    

    private boolean isProjectOpen = false;
    private Context ctx;
    
    public static JavaCompletions javaCompletions = new JavaCompletions();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(Tag.DEBUG, "Creating activity");
        super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        checkGCC();
        
        Log.d(Tag.DEBUG, "Activity created");
        ctx = this;
        //testZip();
    }
    
    private void init(){
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.mainDrawer, android.R.string.yes, android.R.string.yes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.mainDrawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        fProjectManager.setProjectListener(new Project.ProjectListener() {
            @Override
            public void onProjectOpened(Project project) {
                    //try{javaCompletions.initialize(project.getPath().toURI(), new JavaCompletionOptionsImpl());}catch(Exception e){}
                fCodeEditor =  new FCodeEditor(project);
                isProjectOpen = true;
                Utils.startFragment(MainActivity.this, fCodeEditor,true, R.id.main_fragment_container);
                setTitle(project.getProjectName());
                fileManager = new FileManager(MainActivity.this,project.getPath(),fCodeEditor);
                binding.mainNavigation.addView(fileManager.getView());
                updateMenu();
            }
            @Override
            public void onProjectClosed(Project project) {
                isProjectOpen = false;
                    fileManager = null;
                setTitle(R.string.app_name);
                binding.mainNavigation.removeAllViews();
                updateMenu();
            }
        });
        Utils.startFragment(this, fProjectManager, false, R.id.main_fragment_container);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_action_run:
               //Toast.makeText(MainActivity.this,fProjectManager._getCurrentProject().getProjectName(),0).show();
               try{
                    runProject(fProjectManager._getCurrentProject());
                }catch(Exception e){}
            break;
            case R.id.menu_action_undo:
                fCodeEditor.undo();
            break;
            case R.id.menu_action_redo:
                fCodeEditor.redo();
            break;
            case R.id.menu_action_settings:
                toast(ctx, "settings");
            break;
        }
        return actionBarDrawerToggle.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(fProjectManager != null){
            //fProjectManager.
        }
    }
    
    @Override
    protected void onDestroy() {
        Log.d(Tag.DEBUG, "Activity Destroyed");
        super.onDestroy();
    }
    long time = System.currentTimeMillis();
    @Override
    public void onBackPressed() {
        long time2 = System.currentTimeMillis();
        if(isProjectOpen && (time2 - time)<2000) {
            getSupportFragmentManager().popBackStack();
            fProjectManager.closeCurrentProject();
            return;
        }else if(isProjectOpen) {
            //Toast.makeText(this, "Press again to close project", Toast.LENGTH_SHORT).show();
            toast(ctx, "Press again to close project");
            time = System.currentTimeMillis();
            return;
        } else{
            
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        menuAction = menu;
        MenuItem item = menuAction.findItem(R.id.menu_action_run);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item = menuAction.findItem(R.id.menu_action_undo);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item = menuAction.findItem(R.id.menu_action_redo);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        updateMenu();
        return super.onCreateOptionsMenu(menu);
    }
    private void updateMenu(){
        if(menuAction!=null){
            MenuItem runButton, undoButton,redoButton;
            runButton=menuAction.findItem(R.id.menu_action_run);
            undoButton=menuAction.findItem(R.id.menu_action_undo);
            redoButton=menuAction.findItem(R.id.menu_action_redo);
            if(isProjectOpen){
                runButton.setEnabled(true);
                runButton.setVisible(true);
                undoButton.setEnabled(true);
                undoButton.setVisible(true);
                redoButton.setEnabled(true);
                redoButton.setVisible(true);
            }else{
                runButton.setEnabled(false);
                runButton.setVisible(false);
                undoButton.setEnabled(false);
                undoButton.setVisible(false);
                redoButton.setEnabled(false);
                redoButton.setVisible(false);
            }
        }
    }
    
    private void extractZip(){
        
        Task extractTask = new Task(ctx,"Setting Compiler."){
            @Override
            public void onPreExecute() {
                
            }
            @Override
            public void onPostExecute(Object result) {
                if(result == null){
                    toast(ctx,"Success confuguring compiler");
                    
                }else{
                    toast(ctx, result.toString());
                    
                }
            }
            @Override
            public Object onExecution() {
                File file = new File("/storage/emulated/0/Learn/gcc/gcc.zip");
                String out = "/data/data/peepu.codeeditor/usr";
                Exception ex = null;
                try{
                    Utils.unZip(getAssets().open("gcc.zip"),out);
                    Utils.setExecutable(new File(out), true);
                }catch(Exception e){
                    ex = e;
                }
                return ex;
            }
            
        };
        extractTask.start();
    }
    private void runProject(Project project){
        switch(project.getType()){
            case JAVA:
            break;
            case NATIVE:
            fCodeEditor.saveAllFiles();
            startTermActivity(project);
            break;
        }
        //toast(ctx, project.getProjectName()+" is running. "+ty);
        //changePerms();
        //testZip();
    }
    private void startTermActivity(Project project){
        Task task = new Task(ctx, "collection"){
            @Override
            public void onPreExecute() {
                // TODO: Implement this method
            }
            @Override
            public void onPostExecute(Object result) {
                if(result!=null){
                    Intent intent = new Intent(ctx, TermActivity.class);
                    intent.putExtra("cmd","make");
                    intent.putExtra("home",project.getPath()+"/build");
                    startActivity(intent);
                }
            }
            @Override
            public Object onExecution() {
                List<File> srcList=null;
                try{
                    srcList = collectSources(project.getSrcPath());
                    CommandCreator.createMakeFile(project, srcList);
                }catch(Exception e){
                    sendErrorMessage(e.toString());
                }
                return srcList;
            }
        };
        task.start();
        
    }
    private List<File> collectSources(File project){
        File[] ls = project.listFiles();
        List<File> srcList = new ArrayList<>();
        if(ls!=null){
            for(File file:ls){
                if(file.isFile()&&file.getName().endsWith(".cpp")){
                    srcList.add(file);
                }else{
                    srcList.addAll(collectSources(file));
                }
            }
        }
        return srcList;
    }
    private void checkGCC(){
        String out = "/data/data/peepu.codeeditor/usr/gcc/lib/gcc/aarch64-linux-android/10.2.0/include/acc_prof.h";
        
        File f  = new File(out);
        if(!f.exists()){
            extractZip();
        }
    }
    public static native void toast(Context ctx, String msg);
    static{
        System.loadLibrary("native-lib");
    }
}

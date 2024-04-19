package peepu.codeeditor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import org.apache.commons.io.comparator.DirectoryFileComparator;
import org.apache.commons.io.comparator.NameFileComparator;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import peepu.codeeditor.fragments.FCodeEditor;
import peepu.codeeditor.project.TabLayoutManager;
import peepu.codeeditor.tasks.Task;

public class FileManager {
    private MainActivity ctx;
    private ListView fileList;
    private peepu.codeeditor.databinding.DrawerFileManagerBinding binding;
    private File homePath;
    private File currentPath;

    private FCodeEditor fCodeEditor;
    
    public FileManager(MainActivity ctx, File homePath, FCodeEditor fCodeEditor){
        this.ctx = ctx;
        binding = peepu.codeeditor.databinding.DrawerFileManagerBinding.inflate(LayoutInflater.from(ctx));
        fileList = binding.drawerFileList;
        this.homePath = homePath;
        this.fCodeEditor = fCodeEditor;
        init();
    }
    private void init(){
        initAdapter(homePath);
        binding.projectNameTextView.setText(homePath.getName());
        binding.projectSettingsView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg) {
                PopupMenu popupMenu = new PopupMenu(ctx,arg);
                    popupMenu.inflate(R.menu.project_settings_menu);
                    
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(ctx,"settings",0).show();
                            Intent intent = new Intent(ctx, ProjectSettingsActivity.class);
                            intent.putExtra("project", fCodeEditor.getProject().getPath().toString());
                            ctx.startActivity(intent);
                            return true;
                        }
                        
                    });
                    popupMenu.show();
            }
            
        });
        fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FileListAdapter adapter = (FileListAdapter) fileList.getAdapter();
                File file = adapter.getItem(position);
                if(file.isDirectory()&&!file.toString().equals("..")){
                    initAdapter(__getLastAvailableDir(file));
                }else if(file.toString().equals("..")){
                    initAdapter(currentPath.getParentFile());
                }else if(file.isFile()){
                    TabLayoutManager tabLayoutManager = fCodeEditor.getTabLayoutManager();
                    try{
                        tabLayoutManager.addNewTab(file);
                    }catch (Exception e){

                    }
                }
            }
        });
    }
    private File __getLastAvailableDir(File ff){
        File[] list = ff.listFiles();
        if(list != null&& list.length> 1){
            return ff;
        }else if(list.length== 1&&list[0].isDirectory()){
            return __getLastAvailableDir(list[0]);
        }else{
            return ff;
        }
    }
    private void initAdapter(File path){
        currentPath = new File(path.toString());
        Task task = new Task(ctx, "AdapterSetup") {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Object result) {
                FileListAdapter adapter = (FileListAdapter)result;
                fileList.setAdapter(adapter);
            }

            @Override
            public FileListAdapter onExecution() {
                List<File> files = getSortedList(path);
                if(!path.toString().equals(homePath.toString())){
                    files.add(0,new File(".."));
                }
                FileListAdapter fileArrayAdapter = new FileListAdapter(files);
                return fileArrayAdapter;
            }
        };
        task.start();
    }
    private List<File> getSortedList(File path){
        File[] files = path.listFiles();
        if(files != null){
            Arrays.sort(files, NameFileComparator.NAME_INSENSITIVE_COMPARATOR);
            Arrays.sort(files, DirectoryFileComparator.DIRECTORY_COMPARATOR);
            List<File> list = new ArrayList<>(Arrays.asList(files));
            return list;
        }
        return new ArrayList<>();
    }
    public View getView(){
        return binding.getRoot();
    }

    private class FileListAdapter extends BaseAdapter{

        private List<File> files;
        public FileListAdapter(List<File> files){
            this.files = files;
        }
        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public File getItem(int i) {
            return files.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            TextView textView;
            ImageView imageView;
            View view;
            if(convertView != null){
                view = convertView;
            }else {
                view = LayoutInflater.from(ctx).inflate(R.layout.adapter_file_list, viewGroup, false);
            }
            textView = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageview);
            if(getItem(position).isFile()){
                imageView.setImageResource(R.drawable.ic_source);
            }else{
                imageView.setImageResource(R.drawable.ic_folder);
            }
            textView.setText(getItem(position).getName());
            return view;
        }
    }
}

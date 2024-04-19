package peepu.codeeditor.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import peepu.codeeditor.R;
import peepu.codeeditor.databinding.DialogCreateProjectBinding;
import peepu.codeeditor.databinding.FragmentProjectManagerBinding;
import peepu.codeeditor.project.AdapterProjectList;
import peepu.codeeditor.project.Project;
import peepu.codeeditor.tasks.Task;
import peepu.codeeditor.tasks.TaskCreateProject;

public class FProjectManager extends Fragment {

    private FragmentProjectManagerBinding binding;
    private File projectParentPath = new File("/sdcard/CodeEditor");
    private AdapterProjectList adapter;

    private Project currentProject;

    private Project.ProjectListener projectListener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProjectManagerBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initProjects();
        init();
    }
    private void init(){
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Project project = adapter.getItem(position);
                openProject(project);
            }
        });
    }
    private void initProjects(){
        Task task = new Task(requireActivity(), "getProjectList") {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Object result) {
                adapter = (AdapterProjectList)result;
                binding.listView.setAdapter(adapter);
            }

            @Override
            public Object onExecution() {
                return new AdapterProjectList(getProjectList(), requireActivity());
            }
        };
        task.start();
        binding.buttonCreateProject.setOnClickListener(v->{
            showProjectCreateDialog();
        });
    }
    private List<Project> getProjectList(){
        List<Project> projects = new ArrayList<>();

        File path = projectParentPath;
        if(path.exists()&&path.isDirectory()){
            File[] paths = path.listFiles();
            if(paths !=null){
                for(File file: paths){
                    Project project = new Project(file);
                    if(project.isValidProject()){
                        projects.add(project);
                    }
                }
            }
        }
        return projects;
    }
    @Override
    public void onResume() {
        super.onResume();
        initProjects();
    }
    
    private void showProjectCreateDialog(){
        final Project project = new Project(new File("Lulu"));
        project.setType(Project.Type.JAVA);
        final DialogCreateProjectBinding projectBinding = DialogCreateProjectBinding.inflate(LayoutInflater.from(requireActivity()));
        String[] strings = {"Native C/C++", "Java"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.dropdown_item, strings);
        projectBinding.autoCompleteTextView.setText("Java");
        projectBinding.autoCompleteTextView.setAdapter(adapter);
        projectBinding.autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                switch (pos){
                    case 0:
                        project.setType(Project.Type.NATIVE);
                        break;
                    case 1:
                        project.setType(Project.Type.JAVA);
                        break;
                }
            }
            
        });
        
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        final AlertDialog dialog = builder.setCancelable(false)
                .setView(projectBinding.getRoot()).create();
        projectBinding.buttonClose.setOnClickListener(v->{
            dialog.cancel();
        });
        projectBinding.buttonCreate.setOnClickListener(v->{
            String name = projectBinding.editText.getText().toString();
            if(name.equals("")){
                project.setPath(new File(""));
            }else {
                project.setPath(new File(projectParentPath, name));
            }
            TaskCreateProject task = new TaskCreateProject(requireActivity(), project){
                @Override
                public void onPostExecute(Object result) {
                    initProjects();
                    openProject(project);
                }
            };
            task.start();
            dialog.cancel();
        });
        dialog.show();
    }
    private void openProject(Project project){
        if(projectListener != null){
            projectListener.onProjectOpened(project);
            currentProject = project;
        }
    }
    public void closeCurrentProject(){
        if(projectListener != null && currentProject != null){
            projectListener.onProjectClosed(currentProject);
        }
    }

    public void setProjectListener(Project.ProjectListener listener){
        this.projectListener = listener;
    }
    public Project _getCurrentProject(){
        return currentProject;
    }
    
    private void showMessage(String msg){
        Toast.makeText(requireActivity(), msg,0).show();
    }

}

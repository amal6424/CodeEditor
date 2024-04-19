package peepu.codeeditor.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;

import peepu.codeeditor.R;
import peepu.codeeditor.databinding.FragmentCodeEditorBinding;
import peepu.codeeditor.project.Project;
import peepu.codeeditor.project.TabLayoutManager;
import peepu.codeeditor.views.TabItemContent;

public class FCodeEditor extends Fragment {
    private FragmentCodeEditorBinding binding;
    private Project project;
    private TabLayoutManager tabLayoutManager;

    public FCodeEditor(Project project){
        this.project = project;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        binding = FragmentCodeEditorBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        init();
    }
    private void init(){
        TabLayout tabLayout = binding.editorTabLayout;
        LinearLayout layout = binding.editorLinearLayout;
        tabLayoutManager =  new TabLayoutManager(tabLayout, layout, requireActivity());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayoutManager.currentTab(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                PopupMenu menu = new PopupMenu(requireActivity(),tab.getCustomView());
                menu.inflate(R.menu.tab_popup);
                    if(tabLayoutManager.getEditor(tab).isEdited()){
                        MenuItem item = menu.getMenu().findItem(R.id.saveButton);
                        item.setEnabled(true);
                    }else{
                        MenuItem item = menu.getMenu().findItem(R.id.saveButton);
                        item.setEnabled(false);
                    }
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case android.R.id.closeButton:
                                tabLayoutManager.removeTab(tab);
                                break;
                            case R.id.saveButton:
                                tabLayoutManager.saveFile();
                                //tabLayoutManager.getEditor(tab).setEdited(false);
                                break;
                        }
                        return true;
                    }
                });
                menu.show();

            }
        });

    }
    public void undo(){
        tabLayoutManager.undo();
    }
    public void redo(){
        tabLayoutManager.redo();
    }
    public void saveAllFiles(){
        tabLayoutManager.saveAllFiles();
    }
    public TabLayoutManager getTabLayoutManager(){
        return tabLayoutManager;
    }
    public Project getProject(){
        return project;
    }
}

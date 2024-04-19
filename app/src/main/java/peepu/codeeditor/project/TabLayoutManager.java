package peepu.codeeditor.project;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;

import com.peepu.codeeditor.lang.LanguageC;
import com.peepu.codeeditor.lang.LanguageCpp;
import com.peepu.codeeditor.lang.LanguageJava;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import peepu.codeeditor.R;
import peepu.codeeditor.views.CodeEditor;

public class TabLayoutManager {
    private TabLayout tabLayout;
    private LinearLayout layout;
    private Context ctx;

    private HashMap<TabLayout.Tab, View> listMap = new HashMap<>();
    private List<File> files = new ArrayList<>();

    public TabLayoutManager(TabLayout tabLayout, LinearLayout layout, Context ctx) {
        this.tabLayout = tabLayout;
        this.layout = layout;
        this.ctx = ctx;
    }

    public void addNewTab(File file) throws IOException {
        int location = 0;
        if(!files.contains(file)){
            files.add(file);
            location = files.indexOf(file);

            CodeEditor editor = new CodeEditor(ctx);
            if(file.getName().toLowerCase().endsWith(".java")){
                editor.setLanguage(LanguageJava.getInstance());
            }else if(file.getName().toLowerCase().endsWith(".cpp")){
                editor.setLanguage(LanguageCpp.getInstance());
            }else if(file.getName().toLowerCase().endsWith(".c")){
                editor.setLanguage(LanguageC.getInstance());
            }
            editor.openFile(file);
            TabLayout.Tab tab = tabLayout.newTab();
            MaterialTextView textView = new MaterialTextView(ctx);
            textView.setMinWidth(100);
            textView.setText(file.getName());
            textView.setTextColor(ctx.getResources().getColor(R.color.blue, ctx.getTheme()));
            tab.setCustomView(textView);

            tabLayout.addTab(tab);
            layout.removeAllViews();
            layout.addView(editor);
            listMap.put(tab, editor);

        }
        location = files.indexOf(file);
        if(tabLayout.getSelectedTabPosition() == location){
            return;
        }
        
        TabLayout.Tab tab = tabLayout.getTabAt(location);
        View view = listMap.get(tab);
        tabLayout.selectTab(tab);
        layout.removeAllViews();
        if(view==null){
            //Toast.makeText(ctx, "view is null",0).show();
            return;
        }
        layout.addView(view);
    }
    public void currentTab(TabLayout.Tab tab){
        View view = listMap.get(tab);
        layout.removeAllViews();
        if(view==null){
            //Toast.makeText(ctx, "view is null",0).show();
            return;
        }
        layout.addView(view);
    }
    public void removeTab(TabLayout.Tab tab){
        int pos = tab.getPosition();
        tabLayout.removeTab(tab);
        layout.removeAllViews();
        files.remove(pos);
        listMap.remove(tab);
        if(pos>0){
            currentTab(tabLayout.getTabAt(pos-1));
        }
    }
    public void undo(){
        int location = tabLayout.getSelectedTabPosition();
        if(listMap.size()>0){
            CodeEditor editor =(CodeEditor)listMap.get(tabLayout.getTabAt(location));
            editor.undo();
        }
    }
    public void redo(){
        int location = tabLayout.getSelectedTabPosition();
        if(listMap.size()>0){
            CodeEditor editor =(CodeEditor)listMap.get(tabLayout.getTabAt(location));
            editor.redo();
        }
    }
    public void saveAllFiles(){
        for(int i=0;i<listMap.size();i++){
            CodeEditor editor =(CodeEditor)listMap.get(tabLayout.getTabAt(i));
            editor.saveFile();
        }
    }
    public void saveFile(){
        int location = tabLayout.getSelectedTabPosition();
        CodeEditor editor =(CodeEditor)listMap.get(tabLayout.getTabAt(location));
        editor.saveFile();
        
    }
    public CodeEditor getEditor(TabLayout.Tab tab){
        return (CodeEditor)listMap.get(tab);
    }
}

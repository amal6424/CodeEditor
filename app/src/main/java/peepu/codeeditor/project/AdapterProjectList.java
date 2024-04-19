package peepu.codeeditor.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import peepu.codeeditor.R;

public class AdapterProjectList extends BaseAdapter {
    private Context ctx;
    private List<Project> projects;

    public AdapterProjectList(List<Project> projects, Context ctx){
        this.projects = projects;
        this.ctx = ctx;
    }
    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Project getItem(int i) {
        return projects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View convertView = view;
        TextView tv;
        if(convertView == null){
            convertView = LayoutInflater.from(ctx).inflate(R.layout.adapter_project_list, viewGroup, false);
        }
        tv = convertView.findViewById(R.id.textView);
        tv.setText(projects.get(position).getProjectName());
        return convertView;
    }
}

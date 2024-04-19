package peepu.codeeditor.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import peepu.codeeditor.R;

public class TabItemContent {
    private Context ctx;
    public TextView textView;
    private View view;
    public TabItemContent(Context ctx){
        view = LayoutInflater.from(ctx).inflate(R.layout.tab_item, null, false);
        textView = view.findViewById(R.id.tab_text);
    }

    public View getView() {
        return view;
    }
}

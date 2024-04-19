package com.peepu.codeeditor.view.autocomplete;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.tyron.javacompletion.completion.CompletionCandidate;
import peepu.codeeditor.R;
import com.peepu.codeeditor.util.Flag;
import com.peepu.codeeditor.view.FreeScrollingTextField;

import java.util.ArrayList;
import java.util.List;

import static com.peepu.codeeditor.util.DLog.log;

/**
 * Adapter定义
 */
public class AutoPanelAdapter extends BaseAdapter {

    private final int PADDING = 20;
    private int _h;
    private Flag _abort;
    private DisplayMetrics dm;
    private ImmutableList<CompletionCandidate> listItems;
    private Bitmap bitmap;
    private Context _context;
    private AutoCompletePanel mAutoComplete;
    private FreeScrollingTextField mTextFiled;

    public AutoPanelAdapter(Context context, AutoCompletePanel panel, FreeScrollingTextField textField) {
        _context = context;
        mAutoComplete = panel;
        mTextFiled = textField;
        _abort = new Flag();
        //listItems = new ImmutableList<CompletionCandidate>();
        dm = context.getResources().getDisplayMetrics();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_method);
    }

    public void abort() {
        _abort.set();
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public CompletionCandidate getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View tempView = null;
        return tempView;
    }

    public void restart() {
        _abort.clear();
    }

    /**
     * 计算列表高
     *
     * @return
     */
    public int getItemHeight() {
        if (_h != 0)
            return _h;
        LayoutInflater inflater = LayoutInflater.from(_context);
        View rootView = inflater.inflate(R.layout.auto_panel_item, null);
        rootView.measure(0, 0);
        _h = rootView.getMeasuredHeight();

        return _h;
    }
}

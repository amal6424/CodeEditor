package peepu.codeeditor.views;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.Toast;

import com.peepu.codeeditor.lang.Language;
import com.peepu.codeeditor.lang.LanguageJava;
import com.peepu.codeeditor.util.Document;
import com.peepu.codeeditor.util.DocumentProvider;
import com.peepu.codeeditor.util.Lexer;
import com.peepu.codeeditor.view.ColorScheme;
import com.peepu.codeeditor.view.FreeScrollingTextField;
import com.peepu.codeeditor.view.YoyoNavigationMethod;
import com.peepu.codeeditor.view.autocomplete.AutoCompletePanel;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class CodeEditor extends FreeScrollingTextField {
    
    private Document _inputtingDoc;
    private boolean _isWordWrap;
    private Context mContext;
    private String _lastSelectFile;
    private int _index;
    private Toast toast;
    
    private Document mDoc;
    
    public CodeEditor(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CodeEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public CodeEditor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }
    
    private void init() {
        setVerticalScrollBarEnabled(true);
        setTypeface(Typeface.MONOSPACE);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        //设置字体大小
        float size = TypedValue.applyDimension(2, BASE_TEXT_SIZE_PIXELS, dm);
        setTextSize((int) size);
        setShowLineNumbers(true);
        //setAutoCompete(true);
        setHighlightCurrentRow(true);
        setWordWrap(false);
        setAutoComplete(true);
        setAutoIndent(true);
        setSuggestion(false);
        //setUseGboard(true);
        //setAutoIndentWidth(2);
        //setLanguage(LanguageJava.getInstance());
        setNavigationMethod(new YoyoNavigationMethod(this));
        int textColor = Color.BLACK;// 默认文字颜色
        int selectionText = Color.argb(255, 0, 120, 215);//选择文字颜色
        setTextColor(textColor);
        setTextHighlightColor(selectionText);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO: Implement this method
        super.onLayout(changed, left, top, right, bottom);
        if (_index != 0 && right > 0) {
            moveCaret(_index);
            _index = 0;
        }
    }

    public void setKeywordColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.KEYWORD, color);
    }

    public void setBaseWordColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.NAME, color);
    }

    public void setStringColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.STRING, color);
    }

    public void setCommentColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.COMMENT, color);
    }

    public void setBackgroundColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.BACKGROUND, color);
    }

    public void setTextColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.FOREGROUND, color);
    }

    public void setTextHighlightColor(int color) {
        getColorScheme().setColor(ColorScheme.Colorable.SELECTION_BACKGROUND, color);
    }

    public void setLanguage(Language language){
        AutoCompletePanel.setLanguage(language);
        Lexer.setLanguage(language);
    }

    public String getSelectedText() {
        // TODO: Implement this method
        return hDoc.subSequence(getSelectionStart(), getSelectionEnd() - getSelectionStart()).toString();
    }

    public void gotoLine(int line) {
        if (line > hDoc.getRowCount()) {
            line = hDoc.getRowCount();

        }
        int i = getText().getLineOffset(line - 1);
        setSelection(i);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        final int filteredMetaState = event.getMetaState() & ~KeyEvent.META_CTRL_MASK;
        if (KeyEvent.metaStateHasNoModifiers(filteredMetaState)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_A:
                    selectAll();
                    return true;
                case KeyEvent.KEYCODE_X:
                    cut();
                    return true;
                case KeyEvent.KEYCODE_C:
                    copy();
                    return true;
                case KeyEvent.KEYCODE_V:
                    paste();
                    return true;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public void setWordWrap(boolean enable) {
        // TODO: Implement this method
        _isWordWrap = enable;
        super.setWordWrap(enable);
    }

    public DocumentProvider getText() {
        return createDocumentProvider();
    }

    public void setText(CharSequence c) {
        Document doc = new Document(this);
        doc.setWordWrap(_isWordWrap);
        doc.setText(c);
        setDocumentProvider(new DocumentProvider(doc));
    }
    public void openFile(File file){
        
        mDoc = new Document(this);
        mDoc.setWordWrap(_isWordWrap);
        try{
            mDoc.setText(FileUtils.readFileToString(file));
        }catch(IOException e){
            showToast(e.toString());
        }
        setDocumentProvider(new DocumentProvider(mDoc));
        setOpenedFile(file.toString());
    }

    public File getOpenedFile() {
        if (_lastSelectFile != null)
            return new File(_lastSelectFile);

        return null;
    }

    public void setOpenedFile(String file) {
        _lastSelectFile = file;
    }

    public void insert(int idx, String text) {
        selectText(false);
        moveCaret(idx);
        paste(text);
    }

    public void replaceAll(CharSequence c) {
        replaceText(0, getLength() - 1, c.toString());
    }

    public void setSelection(int index) {
        selectText(false);
        if (!hasLayout())
            moveCaret(index);
        else
            _index = index;
    }

    public void undo() {

        DocumentProvider doc = createDocumentProvider();
        int newPosition = doc.undo();

        if (newPosition >= 0) {
            //TODO editor.setEdited(false);
            // if reached original condition of file
            setEdited(true);
            respan();
            selectText(false);
            moveCaret(newPosition);
            invalidate();
        }

    }

    public void redo() {

        DocumentProvider doc = createDocumentProvider();
        int newPosition = doc.redo();

        if (newPosition >= 0) {
            setEdited(true);

            respan();
            selectText(false);
            moveCaret(newPosition);
            invalidate();
        }

    }
    public boolean saveFile(){
        if(isEdited){
            File file = new File(_lastSelectFile);
            try{
                FileUtils.write(file, getText().toString());
                setEdited(false);
                return true;
            }catch(Exception e){
                showToast(e.toString());
                return false;
            }
        }
        return true;
    }
/*
    public void open(String filename) {
        _lastSelectFile = filename;

        File inputFile = new File(filename);
        _inputtingDoc = new Document(this);
        _inputtingDoc.setWordWrap(this.isWordWrap());
        ReadThread readThread = new ReadThread(inputFile.getAbsolutePath(), handler);
        readThread.start();
    }

    /**
     * 保存文件
     * * @param file
     */
/*
    public void save(String file) {
        WriteThread writeThread = new WriteThread(getText().toString(), file, handler);
        writeThread.start();
    }


 */
    private void showToast(CharSequence text) {
        if (toast == null) {
            toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
        }
        toast.show();
    }
    public boolean isFileEdited(){
        return isEdited;
    }
}

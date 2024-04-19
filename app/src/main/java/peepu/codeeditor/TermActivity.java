package peepu.codeeditor;

import android.text.TextUtils;
import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.TermView;
import jackpal.androidterm.compat.ActionBarCompat;
import jackpal.androidterm.compat.ActivityCompat;
import jackpal.androidterm.compat.AndroidCompat;
import jackpal.androidterm.compat.MenuItemCompat;
import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.emulatorview.UpdateCallback;
import jackpal.androidterm.emulatorview.compat.ClipboardManagerCompat;
import jackpal.androidterm.emulatorview.compat.ClipboardManagerCompatFactory;
import jackpal.androidterm.emulatorview.compat.KeycodeConstants;
import jackpal.androidterm.util.SessionList;
import jackpal.androidterm.util.TermSettings;

import java.io.IOException;
import java.text.Collator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;


import android.widget.LinearLayout;
import peepu.codeeditor.databinding.ActivityTermBinding;

public class TermActivity extends Activity{
    private ActivityTermBinding binding;

    private EmulatorView emulatorView;
    private TermSettings termSettings;
    private TermSession mSession;
    private LinearLayout container;
    String initCmd="";
    String home = "";
    
    
    private final static int SELECT_TEXT_ID = 0;
    private final static int COPY_ALL_ID = 1;
    private final static int PASTE_ID = 2;
    private final static int SEND_CONTROL_KEY_ID = 3;
    private final static int SEND_FN_KEY_ID = 4;

    private boolean mAlreadyStarted = false;
    private boolean mStopServiceOnFinish = false;

    private Intent TSIntent;

    public static final int REQUEST_CHOOSE_WINDOW = 1;
    public static final String EXTRA_WINDOW_ID = "jackpal.androidterm.window_id";
    private int onResumeSelectWindow = -1;
    private ComponentName mPrivateAlias;
    // Available on API 12 and later
    private static final int WIFI_MODE_FULL_HIGH_PERF = 3;

    private boolean mBackKeyPressed;

    private static final String ACTION_PATH_BROADCAST = "jackpal.androidterm.broadcast.APPEND_TO_PATH";
    private static final String ACTION_PATH_PREPEND_BROADCAST = "jackpal.androidterm.broadcast.PREPEND_TO_PATH";
    private static final String PERMISSION_PATH_BROADCAST = "jackpal.androidterm.permission.APPEND_TO_PATH";
    private static final String PERMISSION_PATH_PREPEND_BROADCAST = "jackpal.androidterm.permission.PREPEND_TO_PATH";
    private int mPendingPathBroadcasts = 0;
    
    // Available on API 12 and later
    private static final int FLAG_INCLUDE_STOPPED_PACKAGES = 0x20;

    

    private ActionBarCompat mActionBar;
    private int mActionBarMode = TermSettings.ACTION_BAR_MODE_NONE;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCmd=getIntent().getExtras().getString("cmd");
        home=getIntent().getExtras().getString("home");
        binding = ActivityTermBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        container = binding.emulatorView;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        termSettings = new TermSettings(getResources(), mPrefs);
        mSession = createTermSession();
        emulatorView = createEmulatorView(mSession);
        container.addView(emulatorView);
        
    }
    
    private EmulatorView createEmulatorView(TermSession session) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        TermView termView = new TermView(this, session, metrics);
        termView.updatePrefs(termSettings);
        EmulatorView memulatorView = termView;
        memulatorView.setExtGestureListener(new EmulatorViewGestureListener(memulatorView));
        registerForContextMenu(memulatorView);
        return memulatorView;
    }
    private TermSession createTermSession() {
        termSettings.setHomePath(home);
    	TermSession session = createTermSession(this, termSettings, getInitialCommand() );
        return session;
    }
    private TermSession createTermSession(Context context, TermSettings settings, String initialCommand) {
        ShellTermSession session= null;
        try{
            session= new ShellTermSession(settings, initialCommand);
            session.initializeEmulator(64, 64);
            session.setProcessExitMessage("do you want to exit?");
            mSession = session;
        }catch(Exception e){}
        return session;
    }
    
    private String getInitialCommand(){
        StringBuilder sb = new StringBuilder()
        .append("cd $HOME\n")
        .append("clear\n")
        .append(initCmd);
        return sb.toString();
    }
    
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.setHeaderTitle(R.string.edit_text);
      menu.add(0, SELECT_TEXT_ID, 0, R.string.select_text);
      menu.add(0, COPY_ALL_ID, 0, R.string.copy_all);
      menu.add(0, PASTE_ID, 0, R.string.paste);
      //menu.add(0, SEND_CONTROL_KEY_ID, 0, R.string.send_control_key);
      //menu.add(0, SEND_FN_KEY_ID, 0, R.string.send_fn_key);
      if (!canPaste()) {
          menu.getItem(PASTE_ID).setEnabled(false);
      }
    }
        @Override
    public boolean onContextItemSelected(MenuItem item) {
          switch (item.getItemId()) {
          case SELECT_TEXT_ID:
            emulatorView.toggleSelectingText();
            return true;
          case COPY_ALL_ID:
            doCopyAll();
            return true;
          case PASTE_ID:
            doPaste();
            return true;
          case SEND_CONTROL_KEY_ID:
            //doSendControlKey();
            return true;
          case SEND_FN_KEY_ID:
            //doSendFnKey();
            return true;
          default:
            return super.onContextItemSelected(item);
          }
        }
    private boolean canPaste() {
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        if (clip.hasText()) {
            return true;
        }
        return false;
    }
    private void doCopyAll() {
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        clip.setText(mSession.getTranscriptText().trim());
    }

    private void doPaste() {
        if (!canPaste()) {
            return;
        }
        ClipboardManagerCompat clip = ClipboardManagerCompatFactory
                .getManager(getApplicationContext());
        CharSequence paste = clip.getText();
        mSession.write(paste.toString());
    }
    
    private void doToggleSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)
            getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

    }
    private boolean mHaveFullHwKeyboard = false;
    private void doUIToggle(int x, int y, int width, int height) {
        switch (mActionBarMode) {
        case TermSettings.ACTION_BAR_MODE_NONE:
            if (AndroidCompat.SDK >= 11 && (mHaveFullHwKeyboard || y < height / 2)) {
                //openOptionsMenu();
                return;
            } else {
                doToggleSoftKeyboard();
            }
            break;
        case TermSettings.ACTION_BAR_MODE_ALWAYS_VISIBLE:
            if (!mHaveFullHwKeyboard) {
                doToggleSoftKeyboard();
            }
            break;
        case TermSettings.ACTION_BAR_MODE_HIDES:
            if (mHaveFullHwKeyboard || y < height / 2) {
                //doToggleActionBar();
                return;
            } else {
                doToggleSoftKeyboard();
            }
            break;
        }
        emulatorView.requestFocus();
    }
    
    private class EmulatorViewGestureListener extends SimpleOnGestureListener {
        private EmulatorView view;

        public EmulatorViewGestureListener(EmulatorView view) {
            this.view = view;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // Let the EmulatorView handle taps if mouse tracking is active
            if (view.isMouseTrackingActive()) return false;

            //Check for link at tap location
            String link = view.getURLat(e.getX(), e.getY());
            if(link != null){}
                //execURL(link);
            else
                doUIToggle((int) e.getX(), (int) e.getY(), view.getVisibleWidth(), view.getVisibleHeight());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float absVelocityX = Math.abs(velocityX);
            float absVelocityY = Math.abs(velocityY);
            if (absVelocityX > Math.max(1000.0f, 2.0 * absVelocityY)) {
                // Assume user wanted side to side movement
                /*if (velocityX > 0) {
                    // Left to right swipe -- previous window
                    mViewFlipper.showPrevious();
                } else {
                    // Right to left swipe -- next window
                    mViewFlipper.showNext();
                }*/
                return true;
            } else {
                return false;
            }
        }
    }
    
}

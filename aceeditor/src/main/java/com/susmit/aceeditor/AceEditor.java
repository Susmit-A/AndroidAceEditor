package com.susmit.aceeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

public class AceEditor extends WebView
{
    private PopupWindow pw;
    private TextReceivedListener received;
    private LayoutInflater inflater;
    private float x;
    private float y;

    @SuppressLint("SetJavaScriptEnabled")
    public AceEditor(Context context)
    {
        super(context);

        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        pw = new PopupWindow(context);
        pw.setHeight(getResources().getDisplayMetrics().heightPixels/15);
        pw.setWidth(75*getResources().getDisplayMetrics().widthPixels/100);
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw.setElevation(50.0f);
        pw.setOutsideTouchable(true);
        pw.setContentView(inflater.inflate(R.layout.webview_dialog,null));
        setTextReceivedListener(new TextReceivedListener() {
            @Override
            public void onReceivedText(String text) {

            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                received.onReceivedText(message);
                return true;
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        setOnTouchListener(new View.OnTouchListener()
        {
            float downTime;
            int xtimes;
            int ytimes;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        downTime = event.getEventTime();
                        x=event.getX();
                        y=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float tot = SystemClock.uptimeMillis() - downTime;
                        x = event.getX();
                        y = event.getY();
                        if(tot <= 500)
                            v.performClick();
                        else
                            pw.showAtLocation(v, Gravity.NO_GRAVITY,(int)x - getResources().getDisplayMetrics().widthPixels/3,getResources().getDisplayMetrics().heightPixels/12 + (int)y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xtimes = (int) (x - event.getX()) / 25;
                        ytimes = (int) (y - event.getY()) / 60;
                        if (xtimes > 0) {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, xtimes, KeyEvent.META_SHIFT_ON));
                            x=event.getX();
                        }
                        else if(xtimes < 0)
                        {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT, -xtimes, KeyEvent.META_SHIFT_ON));
                            x=event.getX();
                        }

                        if (ytimes > 0) {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, ytimes, KeyEvent.META_SHIFT_ON));
                            y=event.getY();
                        }
                        else if(ytimes < 0) {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, -ytimes, KeyEvent.META_SHIFT_ON));
                            y=event.getY();
                        }
                        break;
                }
                return false;
            }
        });
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!pw.isShowing())
                    pw.showAtLocation(v, Gravity.NO_GRAVITY,(int)x - getResources().getDisplayMetrics().widthPixels/3,getResources().getDisplayMetrics().heightPixels/12 + (int)y);
                return true;
            }
        });
        getSettings().setJavaScriptEnabled(true);
        loadUrl("file:///android_asset/index.html");
    }









    @SuppressLint("SetJavaScriptEnabled")
    public AceEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        pw = new PopupWindow(context);
        pw.setHeight(getResources().getDisplayMetrics().heightPixels/15);
        pw.setWidth(75*getResources().getDisplayMetrics().widthPixels/100);
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw.setElevation(50.0f);
        pw.setOutsideTouchable(true);
        pw.setContentView(inflater.inflate(R.layout.webview_dialog,null));
        setTextReceivedListener(new TextReceivedListener() {
            @Override
            public void onReceivedText(String text) {

            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                received.onReceivedText(message);
                return true;
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loadUrl("javascript:gettext()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        setOnTouchListener(new View.OnTouchListener()
        {
            float downTime;
            int xtimes;
            int ytimes;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        downTime = event.getEventTime();
                        x=event.getX();
                        y=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        float tot = SystemClock.uptimeMillis() - downTime;
                        x = event.getX();
                        y = event.getY();
                        if(tot <= 500)
                            v.performClick();
                        else
                            pw.showAtLocation(v, Gravity.NO_GRAVITY,(int)x - getResources().getDisplayMetrics().widthPixels/3,getResources().getDisplayMetrics().heightPixels/12 + (int)y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xtimes = (int) (x - event.getX()) / 25;
                        ytimes = (int) (y - event.getY()) / 60;
                        if (xtimes > 0) {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, xtimes, KeyEvent.META_SHIFT_ON));
                            x=event.getX();
                        }
                        else if(xtimes < 0)
                        {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT, -xtimes, KeyEvent.META_SHIFT_ON));
                            x=event.getX();
                        }

                        if (ytimes > 0) {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, ytimes, KeyEvent.META_SHIFT_ON));
                            y=event.getY();
                        }
                        else if(ytimes < 0) {
                            v.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, -ytimes, KeyEvent.META_SHIFT_ON));
                            y=event.getY();
                        }
                        break;
                }
                return false;
            }
        });
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!pw.isShowing())
                    pw.showAtLocation(v, Gravity.NO_GRAVITY,(int)x - getResources().getDisplayMetrics().widthPixels/3,getResources().getDisplayMetrics().heightPixels/12 + (int)y);
                return true;
            }
        });
        getSettings().setJavaScriptEnabled(true);
        loadUrl("file:///android_asset/index.html");
    }

    public void setTextReceivedListener(TextReceivedListener listener)
    {
        this.received = listener;
    }

    public void requestText()
    {
        loadUrl("javascript:gettext()");
    }
}

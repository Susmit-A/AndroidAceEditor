package com.susmit.aceeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.util.AttributeSet;
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
    Context context;
    private PopupWindow pw;
    private View popupView;
    private ResultReceivedListener received;
    private LayoutInflater inflater;
    private float x;
    private float y;
    private boolean actAfterSelect;
    private int requestedValue;

    @SuppressLint("SetJavaScriptEnabled")
    public AceEditor(Context context)
    {
        super(context);
        this.context = context;
        initialize();
    }


    @SuppressLint("SetJavaScriptEnabled")
    public AceEditor(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        initialize();
    }

    private void initialize()
    {
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        actAfterSelect = true;
        initPopup();

        setResultReceivedListener(new ResultReceivedListener() {
            @Override
            public void onReceived(String text, int FLAG_VALUE) {

            }
        });

        setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                received.onReceived(message, requestedValue);
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
                            if(actAfterSelect)
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

    private void initPopup()
    {
        pw = new PopupWindow(context);
        pw.setHeight(getResources().getDisplayMetrics().heightPixels/15);
        pw.setWidth(75*getResources().getDisplayMetrics().widthPixels/100);
        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pw.setElevation(50.0f);
        pw.setOutsideTouchable(true);
        pw.setTouchable(true);

        popupView = inflater.inflate(R.layout.webview_dialog_set_1,null);

        final View optSet1 = popupView.findViewById(R.id.optSet1);
        final View optSet2 = popupView.findViewById(R.id.optSet2);

        popupView.findViewById(R.id.nextOptSet).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                optSet1.setVisibility(GONE);
                optSet2.setVisibility(VISIBLE);
            }
        });
        popupView.findViewById(R.id.prevOptSet).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                optSet2.setVisibility(GONE);
                optSet1.setVisibility(VISIBLE);
            }
        });

        popupView.findViewById(R.id.cut).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AceEditor.this.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_X, 0, KeyEvent.META_CTRL_ON));
                AceEditor.this.requestFocus();
                pw.dismiss();
            }
        });
        popupView.findViewById(R.id.copy).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AceEditor.this.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_C, 0, KeyEvent.META_CTRL_ON));
                AceEditor.this.requestFocus();
                pw.dismiss();
            }
        });
        popupView.findViewById(R.id.paste).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AceEditor.this.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V, 0, KeyEvent.META_CTRL_ON));
                AceEditor.this.requestFocus();
                pw.dismiss();
            }
        });
        popupView.findViewById(R.id.selectall).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AceEditor.this.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A, 0, KeyEvent.META_CTRL_ON));
                popupView.findViewById(R.id.prevOptSet).performClick();
            }
        });
        popupView.findViewById(R.id.undo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AceEditor.this.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, KeyEvent.META_CTRL_ON));
            }
        });
        popupView.findViewById(R.id.redo).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AceEditor.this.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_Z, 0, KeyEvent.META_CTRL_ON|KeyEvent.META_SHIFT_ON));
            }
        });
        pw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                optSet2.setVisibility(GONE);
                optSet1.setVisibility(VISIBLE);
            }
        });
        pw.setContentView(popupView);
    }

    public void setResultReceivedListener(ResultReceivedListener listener)
    {
        this.received = listener;
    }

    public void requestText()
    {
        requestedValue = Request.VALUE_TEXT;
        loadUrl("javascript:alert(editor.getValue());");
    }

    public void showOptionsAfterSelection(boolean show)
    {
        actAfterSelect = show;
    }

    public void setText(String text)
    {
        loadUrl("javascript:editor.setValue(\"" + text +"\");");
    }

    public void setFontSize(int fontSizeInpx)
    {
        loadUrl("document.getElementById('editor').style.fontSize='" + String.valueOf(fontSizeInpx) + "px';");
    }

    public void insertTextAtCursor(String text)
    {
        loadUrl("javascript:editor.insert(\"" + text +"\");");
    }

    public void requestLines()
    {
        requestedValue = Request.VALUE_LINES;
        loadUrl("javascript:alert(editor.session.getLength());");
    }

    public void requsetSelectedText()
    {
        requestedValue = Request.VALUE_SELECTED_TEXT;
        loadUrl("javascript:alert(editor.getCopyText());");
    }

    public void setTheme(Theme theme)
    {
        loadUrl("javascript:editor.setTheme(\"ace/theme/" + theme.name().toLowerCase() + "\");");
    }

    public void setMode(Mode mode)
    {
        loadUrl("javascript:editor.session.setMode(\"ace/mode/" + mode.name().toLowerCase() + "\");");
    }

    public static class Request{
        public static int VALUE_TEXT = 0;
        public static int VALUE_LINES = 1;
        public static int VALUE_SELECTED_TEXT = 2;
    }

    public static enum Theme
    {
        AMBIANCE, CHAOS, CHROME, CLOUDS,
        CLOUDS_MIDNIGHT, COBALT, CRIMSON_EDITOR, DAWN,
        DRACULA, DREAMWEAVER, ECLIPSE, GITHUB,
        GOB, GRUVBOX, IDLE_FINGERS, IPLASTIC,
        KATZENMILCH, KR_THEME, KUROIR, MERBIVORE,
        MERBIVORE_SOFT, MONO_INDUSTRIAL, MONOKAI, PASTEL_ON_DARK,
        SOLARIZED_DARK, SOLARIZED_LIGHT, SQLSERVER, TERMINAL,
        TEXTMATE, TOMORROW, TOMORROW_NIGHT, TOMORROW_NIGHT_BLUE,
        TOMORROW_NIGHT_BRIGHT, TOMORROW_NIGHT_EIGHTIES, TWILIGHT, VIBRANT_INK,
        XCODE;
    }

    public static enum Mode
    {
        ABAP, ABC, ActionScript, ADA, Apache_Conf,
        AsciiDoc, Assembly_x86, AutoHotKey, BatchFile, C9Search,
        C_Cpp, Cirru, Clojure, Cobol, coffee, ColdFusion,
        CSharp, CSS, Curly, D, Dart, Diff, Dockerfile, Dot,
        Dummy, DummySyntax, Eiffel, EJS, Elixir, Elm, Erlang,
        Forth, FTL, Gcode, Gherkin, Gitignore, Glsl, golang, Groovy,
        HAML, Handlebars, Haskell, haXe, HTML, HTML_Ruby, INI,
        Io, Jack, Jade, Java, JavaScript, JSON, JSONiq, JSP, JSX,
        Julia, LaTeX, LESS, Liquid, Lisp, LiveScript, LogiQL,
        LSL, Lua, LuaPage, Lucene, Makefile, Markdown, Mask, MATLAB,
        MEL, MUSHCode, MySQL, Nix, ObjectiveC, OCaml, Pascal, Perl,
        pgSQL, PHP, Powershell, Praat, Prolog, Properties, Protobuf,
        Python, R, RDoc, RHTML, Ruby, Rust, SASS, SCAD, Scala,
        Scheme, SCSS, SH, SJS, Smarty, snippets, Soy_Template, Space,
        SQL, Stylus, SVG, Tcl, Tex, Text, Textile, Toml, Twig, Typescript,
        Vala, VBScript, Velocity, Verilog, VHDL, XML, XQuery, YAML;
    }

}

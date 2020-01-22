//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package processing.core;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Build.VERSION;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.LayoutRes;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import processing.a2d.PGraphicsAndroid2D;
import processing.android.ActivityAPI;
import processing.android.AppComponent;
import processing.android.CompatUtils;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.data.StringList;
import processing.data.Table;
import processing.data.XML;
import processing.event.Event;
import processing.event.MouseEvent;
import processing.event.TouchEvent;
import processing.event.TouchEvent.Pointer;
import processing.opengl.PGL;
import processing.opengl.PGraphics2D;
import processing.opengl.PGraphics3D;
import processing.opengl.PShader;

public class PApplet implements ActivityAPI, PConstants {
    public static final boolean DEBUG = false;
    public static final int SDK;
    protected PSurface surface;
    @LayoutRes
    public int parentLayout = -1;
    public PGraphics g;
    public int displayWidth;
    public int displayHeight;
    public String sketchPath;
    public static final int DEFAULT_WIDTH = -1;
    public static final int DEFAULT_HEIGHT = -1;
    protected boolean surfaceChanged;
    public int[] pixels;
    public int width = -1;
    public int height = -1;
    public float displayDensity = 1.0F;
    public int pixelDensity = 1;
    public int pixelWidth;
    public int pixelHeight;
    public int mouseX;
    public int mouseY;
    public int pmouseX;
    public int pmouseY;
    public boolean mousePressed;
    public boolean touchIsStarted;
    public Pointer[] touches = new Pointer[0];
    protected int dmouseX;
    protected int dmouseY;
    protected int emouseX;
    protected int emouseY;
    protected int mousePointerId;
    protected int touchPointerId;
    public char key;
    public int keyCode;
    public boolean keyPressed;
    public boolean focused = false;
    protected boolean keyRepeatEnabled = false;
    boolean keyboardIsOpen = false;
    private boolean requestedBackPress = false;
    public boolean handledBackPressed = true;
    protected HashMap<String, String> permissionMethods = new HashMap();
    protected ArrayList<String> reqPermissions = new ArrayList();
    long millisOffset = System.currentTimeMillis();
    protected boolean insideDraw;
    protected long frameRateLastNanos = 0L;
    public float frameRate = 10.0F;
    protected boolean looping;
    protected boolean redraw;
    public int frameCount;
    public boolean finished;
    protected boolean exitCalled;
    boolean insideSettings;
    String renderer = "processing.core.PGraphicsAndroid2D";
    int smooth = 1;
    boolean fullScreen = false;
    int display = -1;
    int windowColor = -2236963;
    static final String ERROR_MIN_MAX = "Cannot use min() or max() on an empty array.";
    public static final String ARGS_EDITOR_LOCATION = "--editor-location";
    public static final String ARGS_EXTERNAL = "--external";
    public static final String ARGS_LOCATION = "--location";
    public static final String ARGS_DISPLAY = "--display";
    public static final String ARGS_BGCOLOR = "--bgcolor";
    public static final String ARGS_PRESENT = "--present";
    public static final String ARGS_EXCLUSIVE = "--exclusive";
    public static final String ARGS_STOP_COLOR = "--stop-color";
    public static final String ARGS_HIDE_STOP = "--hide-stop";
    public static final String ARGS_SKETCH_FOLDER = "--sketch-path";
    public static final String EXTERNAL_STOP = "__STOP__";
    public static final String EXTERNAL_MOVE = "__MOVE__";
    boolean external = false;
    HashMap<String, PApplet.RegisteredMethods> registerMap = new HashMap();
    PApplet.InternalEventQueue eventQueue = new PApplet.InternalEventQueue();
    Random internalRandom;
    static final int PERLIN_YWRAPB = 4;
    static final int PERLIN_YWRAP = 16;
    static final int PERLIN_ZWRAPB = 8;
    static final int PERLIN_ZWRAP = 256;
    static final int PERLIN_SIZE = 4095;
    int perlin_octaves = 4;
    float perlin_amp_falloff = 0.5F;
    int perlin_TWOPI;
    int perlin_PI;
    float[] perlin_cosTable;
    float[] perlin;
    Random perlinRandom;
    public int requestImageMax = 4;
    volatile int requestImageCount;
    protected static HashMap<String, Pattern> matchPatterns;
    private static NumberFormat int_nf;
    private static int int_nf_digits;
    private static boolean int_nf_commas;
    private static NumberFormat float_nf;
    private static int float_nf_left;
    private static int float_nf_right;
    private static boolean float_nf_commas;
    public static final byte[] ICON_IMAGE;

    public PApplet() {
    }

    public PSurface getSurface() {
        return this.surface;
    }

    public Context getContext() {
        return this.surface.getContext();
    }

    public Activity getActivity() {
        return this.surface.getActivity();
    }

    public void initSurface(AppComponent component, SurfaceHolder holder) {
        this.parentLayout = -1;
        this.initSurface((LayoutInflater)null, (ViewGroup)null, (Bundle)null, component, holder);
    }

    public void initSurface(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, AppComponent component, SurfaceHolder holder) {
        component.initDimensions();
        this.displayWidth = component.getDisplayWidth();
        this.displayHeight = component.getDisplayHeight();
        this.displayDensity = component.getDisplayDensity();
        this.handleSettings();
        boolean parentSize = false;
        if (this.parentLayout == -1) {
            if (this.fullScreen || this.width == -1 || this.height == -1) {
                this.width = this.displayWidth;
                this.height = this.displayHeight;
            }
        } else if (this.fullScreen || this.width == -1 || this.height == -1) {
            this.width = 100;
            this.height = 100;
            parentSize = true;
        }

        this.pixelWidth = this.width * this.pixelDensity;
        this.pixelHeight = this.height * this.pixelDensity;
        String rendererName = this.sketchRenderer();
        this.g = this.makeGraphics(this.width, this.height, rendererName, true);
        this.surface = this.g.createSurface(component, holder, false);
        if (this.parentLayout == -1) {
            this.setFullScreenVisibility();
            this.surface.initView(this.width, this.height);
        } else {
            this.surface.initView(this.width, this.height, parentSize, inflater, container, savedInstanceState);
        }

        this.finished = false;
        this.looping = true;
        this.redraw = true;
        this.sketchPath = this.surface.getFilesDir().getAbsolutePath();
        this.surface.startThread();
    }

    private void setFullScreenVisibility() {
        if (this.fullScreen) {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    short visibility;
                    if (PApplet.SDK < 19) {
                        visibility = 2;
                    } else {
                        visibility = 5894;
                    }

                    PApplet.this.surface.setSystemUiVisibility(visibility);
                }
            });
        }

    }

    public void onResume() {
        if (this.parentLayout == -1) {
            this.setFullScreenVisibility();
        }

        this.handleMethods("resume");
        if (0 < this.frameCount) {
            this.resume();
        }

        this.handledBackPressed = true;
        if (this.g != null) {
            this.g.restoreState();
        }

        this.surface.resumeThread();
    }

    public void onPause() {
        this.surface.pauseThread();
        this.closeKeyboard();
        if (this.g != null) {
            this.g.saveState();
        }

        this.handleMethods("pause");
        this.pause();
    }

    public void onStart() {
        this.start();
    }

    public void onStop() {
        this.stop();
    }

    public void onCreate(Bundle savedInstanceState) {
        this.create();
    }

    public void onDestroy() {
        this.handleMethods("onDestroy");
        this.surface.stopThread();
        this.dispose();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.handleMethods("onActivityResult", new Object[]{requestCode, resultCode, data});
    }

    public void onNewIntent(Intent intent) {
        this.handleMethods("onNewIntent", new Object[]{intent});
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    }

    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    public void setHasOptionsMenu(boolean hasMenu) {
        this.surface.setHasOptionsMenu(hasMenu);
    }

    public synchronized void onBackPressed() {
        this.requestedBackPress = true;
    }

    public FragmentManager getFragmentManager() {
        return this.getActivity() != null ? this.getActivity().getFragmentManager() : null;
    }

    public Window getWindow() {
        return this.getActivity() != null ? this.getActivity().getWindow() : null;
    }

    public void startActivity(Intent intent) {
        this.surface.startActivity(intent);
    }

    public void runOnUiThread(Runnable action) {
        this.surface.runOnUiThread(action);
    }

    public boolean hasPermission(String permission) {
        return this.surface.hasPermission(permission);
    }

    public void requestPermission(String permission) {
        if (!this.hasPermission(permission)) {
            this.reqPermissions.add(permission);
        }

    }

    public void requestPermission(String permission, String callback) {
        this.requestPermission(permission, callback, this);
    }

    public void requestPermission(String permission, String callback, Object target) {
        this.registerWithArgs(callback, target, new Class[]{Boolean.TYPE});
        if (this.hasPermission(permission)) {
            this.handleMethods(callback, new Object[]{true});
        } else {
            this.permissionMethods.put(permission, callback);
            this.reqPermissions.add(permission);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for(int i = 0; i < grantResults.length; ++i) {
                boolean granted = grantResults[i] == 0;
                this.handlePermissionsResult(permissions[i], granted);
            }
        }

    }

    private void handlePermissionsResult(String permission, final boolean granted) {
        String methodName = (String)this.permissionMethods.get(permission);
        final PApplet.RegisteredMethods meth = (PApplet.RegisteredMethods)this.registerMap.get(methodName);
        if (meth != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    meth.handle(new Object[]{granted});
                }
            });
        }

    }

    private void handlePermissions() {
        if (0 < this.reqPermissions.size()) {
            String[] req = (String[])this.reqPermissions.toArray(new String[this.reqPermissions.size()]);
            this.surface.requestPermissions(req);
            this.reqPermissions.clear();
        }

    }

    private synchronized void handleBackPressed() {
        if (this.requestedBackPress) {
            this.requestedBackPress = false;
            this.backPressed();
            if (!this.handledBackPressed) {
                if (this.getActivity() != null) {
                    this.getActivity().finish();
                }

                this.handledBackPressed = false;
            }
        }

    }

    boolean insideSettings(String method, Object... args) {
        if (this.insideSettings) {
            return true;
        } else {
            String url = "https://processing.org/reference/" + method + "_.html";
            if (!this.external) {
                StringList argList = new StringList(args);
                System.err.println("When not using the PDE, " + method + "() can only be used inside settings().");
                System.err.println("Remove the " + method + "() method from setup(), and add the following:");
                System.err.println("public void settings() {");
                System.err.println("  " + method + "(" + argList.join(", ") + ");");
                System.err.println("}");
            }

            throw new IllegalStateException(method + "() cannot be used here, see " + url);
        }
    }

    void handleSettings() {
        this.insideSettings = true;
        this.settings();
        this.insideSettings = false;
    }

    public void settings() {
    }

    public final int sketchWidth() {
        return this.width;
    }

    public final int sketchHeight() {
        return this.height;
    }

    public final String sketchRenderer() {
        return this.renderer;
    }

    public int sketchSmooth() {
        return this.smooth;
    }

    public final boolean sketchFullScreen() {
        return this.fullScreen;
    }

    public final int sketchDisplay() {
        return this.display;
    }

    public final String sketchOutputPath() {
        return null;
    }

    public final OutputStream sketchOutputStream() {
        return null;
    }

    public final int sketchWindowColor() {
        return this.windowColor;
    }

    public final int sketchPixelDensity() {
        return this.pixelDensity;
    }

    public void surfaceChanged() {
        this.surfaceChanged = true;
        this.g.surfaceChanged();
    }

    public void surfaceWindowFocusChanged(boolean hasFocus) {
        this.focused = hasFocus;
        if (this.focused) {
            this.focusGained();
        } else {
            this.focusLost();
        }

    }

    public boolean surfaceTouchEvent(MotionEvent event) {
        this.nativeMotionEvent(event);
        return true;
    }

    public void surfaceKeyDown(int code, KeyEvent event) {
        this.nativeKeyEvent(event);
    }

    public void surfaceKeyUp(int code, KeyEvent event) {
        this.nativeKeyEvent(event);
    }

    public void start() {
    }

    public void stop() {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void backPressed() {
        this.handledBackPressed = false;
    }

    public void registerMethod(String methodName, Object target) {
        if (methodName.equals("mouseEvent")) {
            this.registerWithArgs("mouseEvent", target, new Class[]{MouseEvent.class});
        } else if (methodName.equals("keyEvent")) {
            this.registerWithArgs("keyEvent", target, new Class[]{processing.event.KeyEvent.class});
        } else if (methodName.equals("touchEvent")) {
            this.registerWithArgs("touchEvent", target, new Class[]{TouchEvent.class});
        } else if (methodName.equals("onDestroy")) {
            this.registerNoArgs(methodName, target);
        } else if (methodName.equals("onActivityResult")) {
            this.registerWithArgs("onActivityResult", target, new Class[]{Integer.TYPE, Integer.TYPE, Intent.class});
        } else if (methodName.equals("onNewIntent")) {
            this.registerWithArgs("onNewIntent", target, new Class[]{Intent.class});
        } else {
            this.registerNoArgs(methodName, target);
        }

    }

    private void registerNoArgs(String name, Object o) {
        PApplet.RegisteredMethods meth = (PApplet.RegisteredMethods)this.registerMap.get(name);
        if (meth == null) {
            meth = new PApplet.RegisteredMethods();
            this.registerMap.put(name, meth);
        }

        Class c = o.getClass();

        try {
            Method method = c.getMethod(name);
            meth.add(o, method);
        } catch (NoSuchMethodException var6) {
            this.die("There is no public " + name + "() method in the class " + o.getClass().getName());
        } catch (Exception var7) {
            this.die("Could not register " + name + " + () for " + o, var7);
        }

    }

    private void registerWithArgs(String name, Object o, Class<?>[] cargs) {
        PApplet.RegisteredMethods meth = (PApplet.RegisteredMethods)this.registerMap.get(name);
        if (meth == null) {
            meth = new PApplet.RegisteredMethods();
            this.registerMap.put(name, meth);
        }

        Class c = o.getClass();

        try {
            Method method = c.getMethod(name, cargs);
            meth.add(o, method);
        } catch (NoSuchMethodException var7) {
            this.die("There is no public " + name + "() method in the class " + o.getClass().getName());
        } catch (Exception var8) {
            this.die("Could not register " + name + " + () for " + o, var8);
        }

    }

    public void unregisterMethod(String name, Object target) {
        PApplet.RegisteredMethods meth = (PApplet.RegisteredMethods)this.registerMap.get(name);
        if (meth == null) {
            this.die("No registered methods with the name " + name + "() were found.");
        }

        try {
            meth.remove(target);
        } catch (Exception var5) {
            this.die("Could not unregister " + name + "() for " + target, var5);
        }

    }

    protected void handleMethods(String methodName) {
        PApplet.RegisteredMethods meth = (PApplet.RegisteredMethods)this.registerMap.get(methodName);
        if (meth != null) {
            meth.handle();
        }

    }

    protected void handleMethods(String methodName, final Object[] args) {
        final PApplet.RegisteredMethods meth = (PApplet.RegisteredMethods)this.registerMap.get(methodName);
        if (meth != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    meth.handle(args);
                }
            });
        }

    }

    /** @deprecated */
    @Deprecated
    public void registerSize(Object o) {
        System.err.println("The registerSize() command is no longer supported.");
    }

    /** @deprecated */
    @Deprecated
    public void registerPre(Object o) {
        this.registerNoArgs("pre", o);
    }

    /** @deprecated */
    @Deprecated
    public void registerDraw(Object o) {
        this.registerNoArgs("draw", o);
    }

    /** @deprecated */
    @Deprecated
    public void registerPost(Object o) {
        this.registerNoArgs("post", o);
    }

    /** @deprecated */
    @Deprecated
    public void registerDispose(Object o) {
        this.registerNoArgs("dispose", o);
    }

    /** @deprecated */
    @Deprecated
    public void unregisterSize(Object o) {
        System.err.println("The unregisterSize() command is no longer supported.");
    }

    /** @deprecated */
    @Deprecated
    public void unregisterPre(Object o) {
        this.unregisterMethod("pre", o);
    }

    /** @deprecated */
    @Deprecated
    public void unregisterDraw(Object o) {
        this.unregisterMethod("draw", o);
    }

    /** @deprecated */
    @Deprecated
    public void unregisterPost(Object o) {
        this.unregisterMethod("post", o);
    }

    /** @deprecated */
    @Deprecated
    public void unregisterDispose(Object o) {
        this.unregisterMethod("dispose", o);
    }

    public void setup() {
    }

    public void calculate() {
    }

    public void draw() {
        this.finished = true;
    }

    public void fullScreen() {
        if (!this.fullScreen && this.insideSettings("fullScreen")) {
            this.fullScreen = true;
        }

    }

    public void fullScreen(int display) {
        if (!this.fullScreen && this.insideSettings("fullScreen", display)) {
            this.fullScreen = true;
        }

    }

    public void fullScreen(String renderer) {
        if ((!this.fullScreen || !renderer.equals(this.renderer)) && this.insideSettings("fullScreen", renderer)) {
            this.fullScreen = true;
            this.renderer = renderer;
        }

    }

    public void fullScreen(String renderer, int display) {
        if ((!this.fullScreen || !renderer.equals(this.renderer)) && this.insideSettings("fullScreen", renderer, display)) {
            this.fullScreen = true;
            this.renderer = renderer;
        }

    }

    public void size(int iwidth, int iheight) {
        if ((iwidth != this.width || iheight != this.height) && this.insideSettings("size", iwidth, iheight)) {
            this.width = iwidth;
            this.height = iheight;
        }

    }

    public void size(int iwidth, int iheight, String irenderer) {
        if ((iwidth != this.width || iheight != this.height || !this.renderer.equals(irenderer)) && this.insideSettings("size", iwidth, iheight, irenderer)) {
            this.width = iwidth;
            this.height = iheight;
            this.renderer = irenderer;
        }

    }

    public void setSize(int width, int height) {
        if (this.fullScreen) {
            this.displayWidth = width;
            this.displayHeight = height;
        }

        this.width = width;
        this.height = height;
        this.pixelWidth = width * this.pixelDensity;
        this.pixelHeight = height * this.pixelDensity;
        this.g.setSize(this.sketchWidth(), this.sketchHeight());
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public void smooth() {
        this.smooth(1);
    }

    public void smooth(int level) {
        if (this.insideSettings) {
            this.smooth = level;
        } else if (this.smooth != level) {
            this.smoothWarning("smooth");
        }

    }

    public void noSmooth() {
        if (this.insideSettings) {
            this.smooth = 0;
        } else if (this.smooth != 0) {
            this.smoothWarning("noSmooth");
        }

    }

    private void smoothWarning(String method) {
        String where = this.external ? "setup" : "settings";
        PGraphics.showWarning("%s() can only be used inside %s()", new Object[]{method, where});
    }

    public PGraphics getGraphics() {
        return this.g;
    }

    public void orientation(int which) {
        this.surface.setOrientation(which);
    }

    public void size(int iwidth, int iheight, String irenderer, String ipath) {
        if ((iwidth != this.width || iheight != this.height || !this.renderer.equals(irenderer)) && this.insideSettings("size", iwidth, iheight, irenderer, ipath)) {
            this.width = iwidth;
            this.height = iheight;
            this.renderer = irenderer;
        }

    }

    public PGraphics createGraphics(int iwidth, int iheight) {
        return this.createGraphics(iwidth, iheight, "processing.core.PGraphicsAndroid2D");
    }

    public PGraphics createGraphics(int iwidth, int iheight, String irenderer) {
        return this.makeGraphics(iwidth, iheight, irenderer, false);
    }

    protected PGraphics makeGraphics(int w, int h, String renderer, boolean primary) {
        PGraphics pg = null;
        if (renderer.equals("processing.core.PGraphicsAndroid2D")) {
            pg = new PGraphicsAndroid2D();
        } else if (renderer.equals("processing.opengl.PGraphics2D")) {
            if (!primary && !this.g.isGL()) {
                throw new RuntimeException("createGraphics() with P2D requires size() to use P2D or P3D");
            }

            pg = new PGraphics2D();
        } else if (renderer.equals("processing.opengl.PGraphics3D")) {
            if (!primary && !this.g.isGL()) {
                throw new RuntimeException("createGraphics() with P3D or OPENGL requires size() to use P2D or P3D");
            }

            pg = new PGraphics3D();
        } else {
            Class<?> rendererClass = null;
            Constructor constructor = null;

            try {
                rendererClass = Thread.currentThread().getContextClassLoader().loadClass(renderer);
            } catch (ClassNotFoundException var14) {
                throw new RuntimeException("Missing renderer class");
            }

            if (rendererClass != null) {
                try {
                    constructor = rendererClass.getConstructor();
                } catch (NoSuchMethodException var13) {
                    throw new RuntimeException("Missing renderer constructor");
                }

                if (constructor != null) {
                    try {
                        pg = (PGraphics)constructor.newInstance();
                    } catch (InvocationTargetException var9) {
                        this.printStackTrace(var9);
                        throw new RuntimeException(var9.getMessage());
                    } catch (IllegalAccessException var10) {
                        this.printStackTrace(var10);
                        throw new RuntimeException(var10.getMessage());
                    } catch (InstantiationException var11) {
                        this.printStackTrace(var11);
                        throw new RuntimeException(var11.getMessage());
                    } catch (IllegalArgumentException var12) {
                        this.printStackTrace(var12);
                    }
                }
            }
        }

        ((PGraphics)pg).setParent(this);
        ((PGraphics)pg).setPrimary(primary);
        ((PGraphics)pg).setSize(w, h);
        return (PGraphics)pg;
    }

    public PImage createImage(int wide, int high, int format) {
        PImage image = new PImage(wide, high, format);
        image.parent = this;
        return image;
    }

    public void handleDraw() {
        if (this.g != null) {
            if (this.surfaceChanged || this.parentLayout == -1) {
                if (this.looping || this.redraw) {
                    if (this.insideDraw) {
                        System.err.println("handleDraw() called before finishing");
                        System.exit(1);
                    }

                    this.insideDraw = true;
                    if (!this.handleSpecialDraw()) {
                        this.g.beginDraw();
                        long now = System.nanoTime();
                        if (this.frameCount == 0) {
                            this.setup();
                        } else {
                            double rate = 1000000.0D / ((double)(now - this.frameRateLastNanos) / 1000000.0D);
                            float instantaneousRate = (float)(rate / 1000.0D);
                            this.frameRate = this.frameRate * 0.9F + instantaneousRate * 0.1F;
                            if (this.frameCount != 0) {
                                this.handleMethods("pre");
                            }

                            this.pmouseX = this.dmouseX;
                            this.pmouseY = this.dmouseY;
                            this.draw();
                            this.dmouseX = this.mouseX;
                            this.dmouseY = this.mouseY;
                            this.dequeueEvents();
                            this.handleMethods("draw");
                            this.handlePermissions();
                            this.handleBackPressed();
                            this.redraw = false;
                        }

                        this.g.endDraw();
                        this.insideDraw = false;
                        if (this.frameCount != 0) {
                            this.handleMethods("post");
                        }

                        this.frameRateLastNanos = now;
                        ++this.frameCount;
                    }
                }
            }
        }
    }

    protected boolean handleSpecialDraw() {
        boolean handled = false;
        if (this.g.restoringState()) {
            this.g.beginDraw();
            this.g.endDraw();
            handled = true;
        } else if (this.g.requestedNoLoop) {
            this.looping = false;
            this.g.beginDraw();
            this.g.endDraw();
            this.g.requestedNoLoop = false;
            handled = true;
        }

        if (handled) {
            this.insideDraw = false;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void redraw() {
        if (!this.looping) {
            this.redraw = true;
        }

    }

    public synchronized void loop() {
        if (!this.looping) {
            this.looping = true;
        }

    }

    public synchronized void noLoop() {
        if (this.looping) {
            if (this.g.requestNoLoop()) {
                this.g.requestedNoLoop = true;
            } else {
                this.looping = false;
            }
        }

    }

    public boolean isLooping() {
        return this.looping;
    }

    public void postEvent(Event pe) {
        this.eventQueue.add(pe);
        if (!this.looping) {
            this.dequeueEvents();
        }

    }

    protected void dequeueEvents() {
        while(this.eventQueue.available()) {
            Event e = this.eventQueue.remove();
            switch(e.getFlavor()) {
                case 1:
                    this.handleKeyEvent((processing.event.KeyEvent)e);
                    break;
                case 2:
                    this.handleMouseEvent((MouseEvent)e);
                    break;
                case 3:
                    this.handleTouchEvent((TouchEvent)e);
            }
        }

    }

    protected void handleMouseEvent(MouseEvent event) {
        if (event.getAction() == 4 || event.getAction() == 5) {
            this.pmouseX = this.emouseX;
            this.pmouseY = this.emouseY;
            this.mouseX = event.getX();
            this.mouseY = event.getY();
        }

        if (event.getAction() == 1) {
            this.mouseX = event.getX();
            this.mouseY = event.getY();
            this.pmouseX = this.mouseX;
            this.pmouseY = this.mouseY;
            this.dmouseX = this.mouseX;
            this.dmouseY = this.mouseY;
        }

        switch(event.getAction()) {
            case 1:
                this.mousePressed = true;
                break;
            case 2:
                this.mousePressed = false;
        }

        this.handleMethods("mouseEvent", new Object[]{event});
        switch(event.getAction()) {
            case 1:
                this.mousePressed(event);
                break;
            case 2:
                this.mouseReleased(event);
                break;
            case 3:
                this.mouseClicked(event);
                break;
            case 4:
                this.mouseDragged(event);
                break;
            case 5:
                this.mouseMoved(event);
                break;
            case 6:
                this.mouseEntered(event);
                break;
            case 7:
                this.mouseExited(event);
        }

        if (event.getAction() == 4 || event.getAction() == 5) {
            this.emouseX = this.mouseX;
            this.emouseY = this.mouseY;
        }

        if (event.getAction() == 1) {
            this.emouseX = this.mouseX;
            this.emouseY = this.mouseY;
        }

    }

    protected void handleTouchEvent(TouchEvent event) {
        this.touches = event.getTouches(this.touches);
        switch(event.getAction()) {
            case 1:
                this.touchIsStarted = true;
                break;
            case 2:
                this.touchIsStarted = false;
        }

        this.handleMethods("touchEvent", new Object[]{event});
        switch(event.getAction()) {
            case 1:
                this.touchStarted(event);
                break;
            case 2:
                this.touchEnded(event);
                break;
            case 3:
                this.touchCancelled(event);
                break;
            case 4:
                this.touchMoved(event);
        }

    }

    protected void nativeMotionEvent(MotionEvent motionEvent) {
        int metaState = motionEvent.getMetaState();
        int modifiers = 0;
        if ((metaState & 1) != 0) {
            modifiers |= 1;
        }

        if ((metaState & 4096) != 0) {
            modifiers |= 2;
        }

        if ((metaState & 65536) != 0) {
            modifiers |= 4;
        }

        if ((metaState & 2) != 0) {
            modifiers |= 8;
        }

        int state = motionEvent.getButtonState();
        int button;
        switch(state) {
            case 1:
                button = 21;
                break;
            case 2:
                button = 22;
                break;
            case 3:
            default:
                button = state;
                break;
            case 4:
                button = 3;
        }

        this.enqueueMouseEvents(motionEvent, button, modifiers);
        this.enqueueTouchEvents(motionEvent, button, modifiers);
    }

    protected void enqueueTouchEvents(MotionEvent event, int button, int modifiers) {
        int action = event.getAction();
        int actionMasked = action & 255;
        int paction = false;
        byte paction;
        switch(actionMasked) {
            case 0:
                paction = 1;
                break;
            case 1:
                paction = 2;
                break;
            case 2:
                paction = 4;
                break;
            case 3:
            case 4:
            default:
                paction = 3;
                break;
            case 5:
                paction = 1;
                break;
            case 6:
                paction = 2;
        }

        if (paction == 1 || paction == 2) {
            this.touchPointerId = event.getPointerId(0);
        }

        int pointerCount = event.getPointerCount();
        int h;
        if (actionMasked == 2) {
            int historySize = event.getHistorySize();

            for(h = 0; h < historySize; ++h) {
                TouchEvent touchEvent = new TouchEvent(event, event.getHistoricalEventTime(h), paction, modifiers, button);
                touchEvent.setNumPointers(pointerCount);

                for(int p = 0; p < pointerCount; ++p) {
                    touchEvent.setPointer(p, event.getPointerId(p), event.getHistoricalX(p, h), event.getHistoricalY(p, h), event.getHistoricalSize(p, h), event.getHistoricalPressure(p, h));
                }

                this.postEvent(touchEvent);
            }
        }

        TouchEvent touchEvent = new TouchEvent(event, event.getEventTime(), paction, modifiers, button);
        if (actionMasked == 1) {
            touchEvent.setNumPointers(0);
        } else {
            touchEvent.setNumPointers(pointerCount);

            for(h = 0; h < event.getPointerCount(); ++h) {
                touchEvent.setPointer(h, event.getPointerId(h), event.getX(h), event.getY(h), event.getSize(h), event.getPressure(h));
            }
        }

        this.postEvent(touchEvent);
    }

    protected void enqueueMouseEvents(MotionEvent event, int button, int modifiers) {
        int action = event.getAction();
        int clickCount = 1;
        int index;
        switch(action & 255) {
            case 0:
                this.mousePointerId = event.getPointerId(0);
                this.postEvent(new MouseEvent(event, event.getEventTime(), 1, modifiers, (int)event.getX(), (int)event.getY(), button, clickCount));
                break;
            case 1:
                index = event.findPointerIndex(this.mousePointerId);
                if (index != -1) {
                    this.postEvent(new MouseEvent(event, event.getEventTime(), 2, modifiers, (int)event.getX(index), (int)event.getY(index), button, clickCount));
                }
                break;
            case 2:
                index = event.findPointerIndex(this.mousePointerId);
                if (index != -1) {
                    this.postEvent(new MouseEvent(event, event.getEventTime(), 4, modifiers, (int)event.getX(index), (int)event.getY(index), button, clickCount));
                }
        }

    }

    public void mousePressed() {
    }

    public void mousePressed(MouseEvent event) {
        this.mousePressed();
    }

    public void mouseReleased() {
    }

    public void mouseReleased(MouseEvent event) {
        this.mouseReleased();
    }

    public void mouseClicked() {
    }

    public void mouseClicked(MouseEvent event) {
        this.mouseClicked();
    }

    public void mouseDragged() {
    }

    public void mouseDragged(MouseEvent event) {
        this.mouseDragged();
    }

    public void mouseMoved() {
    }

    public void mouseMoved(MouseEvent event) {
        this.mouseMoved();
    }

    public void mouseEntered() {
    }

    public void mouseEntered(MouseEvent event) {
        this.mouseEntered();
    }

    public void mouseExited() {
    }

    public void mouseExited(MouseEvent event) {
        this.mouseExited();
    }

    public void touchStarted() {
    }

    public void touchStarted(TouchEvent event) {
        this.touchStarted();
    }

    public void touchMoved() {
    }

    public void touchMoved(TouchEvent event) {
        this.touchMoved();
    }

    public void touchEnded() {
    }

    public void touchEnded(TouchEvent event) {
        this.touchEnded();
    }

    public void touchCancelled() {
    }

    public void touchCancelled(TouchEvent event) {
        this.touchCancelled();
    }

    public boolean wallpaperPreview() {
        return this.surface.getEngine().isPreview();
    }

    public float wallpaperOffset() {
        return this.surface.getEngine().getXOffset();
    }

    public int wallpaperHomeCount() {
        float step = this.surface.getEngine().getXOffsetStep();
        return 0.0F < step ? (int)(1.0F + 1.0F / step) : 1;
    }

    public boolean wearAmbient() {
        return this.surface.getEngine().isInAmbientMode();
    }

    public boolean wearInteractive() {
        return !this.surface.getEngine().isInAmbientMode();
    }

    public boolean wearRound() {
        return this.surface.getEngine().isRound();
    }

    public boolean wearSquare() {
        return !this.surface.getEngine().isRound();
    }

    public Rect wearInsets() {
        return this.surface.getEngine().getInsets();
    }

    public boolean wearLowBit() {
        return this.surface.getEngine().useLowBitAmbient();
    }

    public boolean wearBurnIn() {
        return this.surface.getEngine().requireBurnInProtection();
    }

    protected void handleKeyEvent(processing.event.KeyEvent event) {
        if (this.keyRepeatEnabled || !event.isAutoRepeat()) {
            this.key = event.getKey();
            this.keyCode = event.getKeyCode();
            switch(event.getAction()) {
                case 1:
                    this.keyPressed = true;
                    this.keyPressed(event);
                    break;
                case 2:
                    this.keyPressed = false;
                    this.keyReleased(event);
            }

            this.handleMethods("keyEvent", new Object[]{event});
        }
    }

    protected void nativeKeyEvent(KeyEvent event) {
        char key = (char)event.getUnicodeChar();
        if (key == 0 || key == '\uffff') {
            key = '\uffff';
        }

        int keyCode = event.getKeyCode();
        int keAction = 0;
        int action = event.getAction();
        if (action == 0) {
            keAction = 1;
        } else if (action == 1) {
            keAction = 2;
        }

        int keModifiers = 0;
        processing.event.KeyEvent ke = new processing.event.KeyEvent(event, event.getEventTime(), keAction, keModifiers, key, keyCode, 0 < event.getRepeatCount());
        this.postEvent(ke);
    }

    public void openKeyboard() {
        Context context = this.surface.getContext();
        InputMethodManager imm = (InputMethodManager)context.getSystemService("input_method");
        imm.toggleSoftInput(2, 0);
        this.keyboardIsOpen = true;
    }

    public void closeKeyboard() {
        if (this.keyboardIsOpen) {
            Context context = this.surface.getContext();
            InputMethodManager imm = (InputMethodManager)context.getSystemService("input_method");
            imm.toggleSoftInput(1, 0);
            this.keyboardIsOpen = false;
            if (this.parentLayout == -1) {
                this.setFullScreenVisibility();
            }
        }

    }

    public void keyPressed() {
    }

    public void keyPressed(processing.event.KeyEvent event) {
        this.keyPressed();
    }

    public void keyReleased() {
    }

    public void keyReleased(processing.event.KeyEvent event) {
        this.keyReleased();
    }

    public void keyTyped() {
    }

    public void keyTyped(processing.event.KeyEvent event) {
        this.keyTyped();
    }

    public void focusGained() {
    }

    public void focusLost() {
    }

    public int millis() {
        return (int)(System.currentTimeMillis() - this.millisOffset);
    }

    public static int second() {
        return Calendar.getInstance().get(13);
    }

    public static int minute() {
        return Calendar.getInstance().get(12);
    }

    public static int hour() {
        return Calendar.getInstance().get(11);
    }

    public static int day() {
        return Calendar.getInstance().get(5);
    }

    public static int month() {
        return Calendar.getInstance().get(2) + 1;
    }

    public static int year() {
        return Calendar.getInstance().get(1);
    }

    public void delay(int napTime) {
        try {
            Thread.sleep((long)napTime);
        } catch (InterruptedException var3) {
        }

    }

    public void frameRate(float fps) {
        this.surface.setFrameRate(fps);
    }

    public void link(String here) {
        this.link(here, (String)null);
    }

    public void link(String url, String frameTitle) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        this.surface.startActivity(viewIntent);
    }

    public static void open(String filename) {
        open(new String[]{filename});
    }

    public static Process open(String[] argv) {
        return exec(argv);
    }

    public static Process exec(String[] argv) {
        try {
            return Runtime.getRuntime().exec(argv);
        } catch (Exception var2) {
            throw new RuntimeException("Could not open " + join(argv, ' '));
        }
    }

    protected void printStackTrace(Throwable t) {
        t.printStackTrace();
    }

    public void die(String what) {
        this.stop();
        throw new RuntimeException(what);
    }

    public void die(String what, Exception e) {
        if (e != null) {
            e.printStackTrace();
        }

        this.die(what);
    }

    public void create() {
    }

    public void exit() {
        this.surface.finish();
    }

    public final void dispose() {
        this.finished = true;
        if (this.surface != null) {
            this.surface.stopThread();
            this.surface.dispose();
        }

        if (this.g != null) {
            this.g.clearState();
            this.g.dispose();
        }

        this.handleMethods("dispose");
    }

    public void method(String name) {
        try {
            Method method = this.getClass().getMethod(name);
            method.invoke(this);
        } catch (IllegalArgumentException var3) {
            this.printStackTrace(var3);
        } catch (IllegalAccessException var4) {
            this.printStackTrace(var4);
        } catch (InvocationTargetException var5) {
            var5.getTargetException().printStackTrace();
        } catch (NoSuchMethodException var6) {
            System.err.println("There is no public " + name + "() method in the class " + this.getClass().getName());
        } catch (Exception var7) {
            this.printStackTrace(var7);
        }

    }

    public void thread(final String name) {
        Thread later = new Thread() {
            public void run() {
                PApplet.this.method(name);
            }
        };
        later.start();
    }

    public void save(String filename) {
        this.g.save(this.savePath(filename));
    }

    public void saveFrame() {
        try {
            this.g.save(this.savePath("screen-" + nf(this.frameCount, 4) + ".tif"));
        } catch (SecurityException var2) {
            System.err.println("Can't use saveFrame() when running in a browser, unless using a signed applet.");
        }

    }

    public void saveFrame(String what) {
        try {
            this.g.save(this.savePath(this.insertFrame(what)));
        } catch (SecurityException var3) {
            System.err.println("Can't use saveFrame() when running in a browser, unless using a signed applet.");
        }

    }

    protected String insertFrame(String what) {
        int first = what.indexOf(35);
        int last = what.lastIndexOf(35);
        if (first != -1 && last - first > 0) {
            String prefix = what.substring(0, first);
            int count = last - first + 1;
            String suffix = what.substring(last + 1);
            return prefix + nf(this.frameCount, count) + suffix;
        } else {
            return what;
        }
    }

    public static void print(byte what) {
        System.out.print(what);
        System.out.flush();
    }

    public static void print(boolean what) {
        System.out.print(what);
        System.out.flush();
    }

    public static void print(char what) {
        System.out.print(what);
        System.out.flush();
    }

    public static void print(int what) {
        System.out.print(what);
        System.out.flush();
    }

    public static void print(float what) {
        System.out.print(what);
        System.out.flush();
    }

    public static void print(String what) {
        System.out.print(what);
        System.out.flush();
    }

    public static void print(Object... variables) {
        StringBuilder sb = new StringBuilder();
        Object[] var2 = variables;
        int var3 = variables.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Object o = var2[var4];
            if (sb.length() != 0) {
                sb.append(" ");
            }

            if (o == null) {
                sb.append("null");
            } else {
                sb.append(o.toString());
            }
        }

        System.out.print(sb.toString());
    }

    public static void println() {
        System.out.println();
    }

    public static void println(byte what) {
        print(what);
        System.out.println();
    }

    public static void println(boolean what) {
        print(what);
        System.out.println();
    }

    public static void println(char what) {
        print(what);
        System.out.println();
    }

    public static void println(int what) {
        print(what);
        System.out.println();
    }

    public static void println(float what) {
        print(what);
        System.out.println();
    }

    public static void println(String what) {
        print(what);
        System.out.println();
    }

    public static void println(Object... variables) {
        print(variables);
        println();
    }

    public static void println(Object what) {
        if (what == null) {
            System.out.println("null");
        } else {
            String name = what.getClass().getName();
            if (name.charAt(0) == '[') {
                switch(name.charAt(1)) {
                    case 'B':
                        byte[] bb = (byte[])((byte[])what);

                        for(int i = 0; i < bb.length; ++i) {
                            System.out.println("[" + i + "] " + bb[i]);
                        }

                        return;
                    case 'C':
                        char[] cc = (char[])((char[])what);

                        for(int i = 0; i < cc.length; ++i) {
                            System.out.println("[" + i + "] '" + cc[i] + "'");
                        }

                        return;
                    case 'F':
                        float[] ff = (float[])((float[])what);

                        for(int i = 0; i < ff.length; ++i) {
                            System.out.println("[" + i + "] " + ff[i]);
                        }

                        return;
                    case 'I':
                        int[] ii = (int[])((int[])what);

                        for(int i = 0; i < ii.length; ++i) {
                            System.out.println("[" + i + "] " + ii[i]);
                        }

                        return;
                    case 'L':
                        Object[] poo = (Object[])((Object[])what);

                        for(int i = 0; i < poo.length; ++i) {
                            if (poo[i] instanceof String) {
                                System.out.println("[" + i + "] \"" + poo[i] + "\"");
                            } else {
                                System.out.println("[" + i + "] " + poo[i]);
                            }
                        }

                        return;
                    case 'Z':
                        boolean[] zz = (boolean[])((boolean[])what);

                        for(int i = 0; i < zz.length; ++i) {
                            System.out.println("[" + i + "] " + zz[i]);
                        }

                        return;
                    case '[':
                        System.out.println(what);
                        break;
                    default:
                        System.out.println(what);
                }
            } else {
                System.out.println(what);
            }
        }

    }

    public static void printArray(Object what) {
        if (what == null) {
            System.out.println("null");
        } else {
            String name = what.getClass().getName();
            if (name.charAt(0) == '[') {
                label89:
                switch(name.charAt(1)) {
                    case 'B':
                        byte[] bb = (byte[])((byte[])what);
                        int i = 0;

                        while(true) {
                            if (i >= bb.length) {
                                break label89;
                            }

                            System.out.println("[" + i + "] " + bb[i]);
                            ++i;
                        }
                    case 'C':
                        char[] cc = (char[])((char[])what);
                        int i = 0;

                        while(true) {
                            if (i >= cc.length) {
                                break label89;
                            }

                            System.out.println("[" + i + "] '" + cc[i] + "'");
                            ++i;
                        }
                    case 'D':
                        double[] dd = (double[])((double[])what);
                        int i = 0;

                        while(true) {
                            if (i >= dd.length) {
                                break label89;
                            }

                            System.out.println("[" + i + "] " + dd[i]);
                            ++i;
                        }
                    case 'E':
                    case 'G':
                    case 'H':
                    case 'K':
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'T':
                    case 'U':
                    case 'V':
                    case 'W':
                    case 'X':
                    case 'Y':
                    default:
                        System.out.println(what);
                        break;
                    case 'F':
                        float[] ff = (float[])((float[])what);
                        int i = 0;

                        while(true) {
                            if (i >= ff.length) {
                                break label89;
                            }

                            System.out.println("[" + i + "] " + ff[i]);
                            ++i;
                        }
                    case 'I':
                        int[] ii = (int[])((int[])what);
                        int i = 0;

                        while(true) {
                            if (i >= ii.length) {
                                break label89;
                            }

                            System.out.println("[" + i + "] " + ii[i]);
                            ++i;
                        }
                    case 'J':
                        long[] jj = (long[])((long[])what);
                        int i = 0;

                        while(true) {
                            if (i >= jj.length) {
                                break label89;
                            }

                            System.out.println("[" + i + "] " + jj[i]);
                            ++i;
                        }
                    case 'L':
                        Object[] poo = (Object[])((Object[])what);
                        int i = 0;

                        while(true) {
                            if (i >= poo.length) {
                                break label89;
                            }

                            if (poo[i] instanceof String) {
                                System.out.println("[" + i + "] \"" + poo[i] + "\"");
                            } else {
                                System.out.println("[" + i + "] " + poo[i]);
                            }

                            ++i;
                        }
                    case 'Z':
                        boolean[] zz = (boolean[])((boolean[])what);
                        int i = 0;

                        while(true) {
                            if (i >= zz.length) {
                                break label89;
                            }

                            System.out.println("[" + i + "] " + zz[i]);
                            ++i;
                        }
                    case '[':
                        System.out.println(what);
                }
            } else {
                System.out.println(what);
            }
        }

        System.out.flush();
    }

    public static final float abs(float n) {
        return n < 0.0F ? -n : n;
    }

    public static final int abs(int n) {
        return n < 0 ? -n : n;
    }

    public static final float sq(float a) {
        return a * a;
    }

    public static final float sqrt(float a) {
        return (float)Math.sqrt((double)a);
    }

    public static final float log(float a) {
        return (float)Math.log((double)a);
    }

    public static final float exp(float a) {
        return (float)Math.exp((double)a);
    }

    public static final float pow(float a, float b) {
        return (float)Math.pow((double)a, (double)b);
    }

    public static final int max(int a, int b) {
        return a > b ? a : b;
    }

    public static final float max(float a, float b) {
        return a > b ? a : b;
    }

    public static final int max(int a, int b, int c) {
        return a > b ? (a > c ? a : c) : (b > c ? b : c);
    }

    public static final float max(float a, float b, float c) {
        return a > b ? (a > c ? a : c) : (b > c ? b : c);
    }

    public static final int max(int[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        } else {
            int max = list[0];

            for(int i = 1; i < list.length; ++i) {
                if (list[i] > max) {
                    max = list[i];
                }
            }

            return max;
        }
    }

    public static final float max(float[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        } else {
            float max = list[0];

            for(int i = 1; i < list.length; ++i) {
                if (list[i] > max) {
                    max = list[i];
                }
            }

            return max;
        }
    }

    public static final int min(int a, int b) {
        return a < b ? a : b;
    }

    public static final float min(float a, float b) {
        return a < b ? a : b;
    }

    public static final int min(int a, int b, int c) {
        return a < b ? (a < c ? a : c) : (b < c ? b : c);
    }

    public static final float min(float a, float b, float c) {
        return a < b ? (a < c ? a : c) : (b < c ? b : c);
    }

    public static final int min(int[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        } else {
            int min = list[0];

            for(int i = 1; i < list.length; ++i) {
                if (list[i] < min) {
                    min = list[i];
                }
            }

            return min;
        }
    }

    public static final float min(float[] list) {
        if (list.length == 0) {
            throw new ArrayIndexOutOfBoundsException("Cannot use min() or max() on an empty array.");
        } else {
            float min = list[0];

            for(int i = 1; i < list.length; ++i) {
                if (list[i] < min) {
                    min = list[i];
                }
            }

            return min;
        }
    }

    public static final int constrain(int amt, int low, int high) {
        return amt < low ? low : (amt > high ? high : amt);
    }

    public static final float constrain(float amt, float low, float high) {
        return amt < low ? low : (amt > high ? high : amt);
    }

    public static final float sin(float angle) {
        return (float)Math.sin((double)angle);
    }

    public static final float cos(float angle) {
        return (float)Math.cos((double)angle);
    }

    public static final float tan(float angle) {
        return (float)Math.tan((double)angle);
    }

    public static final float asin(float value) {
        return (float)Math.asin((double)value);
    }

    public static final float acos(float value) {
        return (float)Math.acos((double)value);
    }

    public static final float atan(float value) {
        return (float)Math.atan((double)value);
    }

    public static final float atan2(float a, float b) {
        return (float)Math.atan2((double)a, (double)b);
    }

    public static final float degrees(float radians) {
        return radians * 57.295776F;
    }

    public static final float radians(float degrees) {
        return degrees * 0.017453292F;
    }

    public static final int ceil(float what) {
        return (int)Math.ceil((double)what);
    }

    public static final int floor(float what) {
        return (int)Math.floor((double)what);
    }

    public static final int round(float what) {
        return Math.round(what);
    }

    public static final float mag(float a, float b) {
        return (float)Math.sqrt((double)(a * a + b * b));
    }

    public static final float mag(float a, float b, float c) {
        return (float)Math.sqrt((double)(a * a + b * b + c * c));
    }

    public static final float dist(float x1, float y1, float x2, float y2) {
        return sqrt(sq(x2 - x1) + sq(y2 - y1));
    }

    public static final float dist(float x1, float y1, float z1, float x2, float y2, float z2) {
        return sqrt(sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1));
    }

    public static final float lerp(float start, float stop, float amt) {
        return start + (stop - start) * amt;
    }

    public static final float norm(float value, float start, float stop) {
        return (value - start) / (stop - start);
    }

    public static final float map(float value, float istart, float istop, float ostart, float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public final float random(float high) {
        if (high != 0.0F && high == high) {
            if (this.internalRandom == null) {
                this.internalRandom = new Random();
            }

            float value = 0.0F;

            do {
                value = this.internalRandom.nextFloat() * high;
            } while(value == high);

            return value;
        } else {
            return 0.0F;
        }
    }

    public final float randomGaussian() {
        if (this.internalRandom == null) {
            this.internalRandom = new Random();
        }

        return (float)this.internalRandom.nextGaussian();
    }

    public final float random(float low, float high) {
        if (low >= high) {
            return low;
        } else {
            float diff = high - low;
            float value = 0.0F;

            do {
                value = this.random(diff) + low;
            } while(value == high);

            return value;
        }
    }

    public final void randomSeed(long seed) {
        if (this.internalRandom == null) {
            this.internalRandom = new Random();
        }

        this.internalRandom.setSeed(seed);
    }

    public float noise(float x) {
        return this.noise(x, 0.0F, 0.0F);
    }

    public float noise(float x, float y) {
        return this.noise(x, y, 0.0F);
    }

    public float noise(float x, float y, float z) {
        int xi;
        if (this.perlin == null) {
            if (this.perlinRandom == null) {
                this.perlinRandom = new Random();
            }

            this.perlin = new float[4096];

            for(xi = 0; xi < 4096; ++xi) {
                this.perlin[xi] = this.perlinRandom.nextFloat();
            }

            this.perlin_cosTable = PGraphics.cosLUT;
            this.perlin_TWOPI = this.perlin_PI = 720;
            this.perlin_PI >>= 1;
        }

        if (x < 0.0F) {
            x = -x;
        }

        if (y < 0.0F) {
            y = -y;
        }

        if (z < 0.0F) {
            z = -z;
        }

        xi = (int)x;
        int yi = (int)y;
        int zi = (int)z;
        float xf = x - (float)xi;
        float yf = y - (float)yi;
        float zf = z - (float)zi;
        float r = 0.0F;
        float ampl = 0.5F;

        for(int i = 0; i < this.perlin_octaves; ++i) {
            int of = xi + (yi << 4) + (zi << 8);
            float rxf = this.noise_fsc(xf);
            float ryf = this.noise_fsc(yf);
            float n1 = this.perlin[of & 4095];
            n1 += rxf * (this.perlin[of + 1 & 4095] - n1);
            float n2 = this.perlin[of + 16 & 4095];
            n2 += rxf * (this.perlin[of + 16 + 1 & 4095] - n2);
            n1 += ryf * (n2 - n1);
            of += 256;
            n2 = this.perlin[of & 4095];
            n2 += rxf * (this.perlin[of + 1 & 4095] - n2);
            float n3 = this.perlin[of + 16 & 4095];
            n3 += rxf * (this.perlin[of + 16 + 1 & 4095] - n3);
            n2 += ryf * (n3 - n2);
            n1 += this.noise_fsc(zf) * (n2 - n1);
            r += n1 * ampl;
            ampl *= this.perlin_amp_falloff;
            xi <<= 1;
            xf *= 2.0F;
            yi <<= 1;
            yf *= 2.0F;
            zi <<= 1;
            zf *= 2.0F;
            if (xf >= 1.0F) {
                ++xi;
                --xf;
            }

            if (yf >= 1.0F) {
                ++yi;
                --yf;
            }

            if (zf >= 1.0F) {
                ++zi;
                --zf;
            }
        }

        return r;
    }

    private float noise_fsc(float i) {
        return 0.5F * (1.0F - this.perlin_cosTable[(int)(i * (float)this.perlin_PI) % this.perlin_TWOPI]);
    }

    public void noiseDetail(int lod) {
        if (lod > 0) {
            this.perlin_octaves = lod;
        }

    }

    public void noiseDetail(int lod, float falloff) {
        if (lod > 0) {
            this.perlin_octaves = lod;
        }

        if (falloff > 0.0F) {
            this.perlin_amp_falloff = falloff;
        }

    }

    public void noiseSeed(long seed) {
        if (this.perlinRandom == null) {
            this.perlinRandom = new Random();
        }

        this.perlinRandom.setSeed(seed);
        this.perlin = null;
    }

    public PImage loadImage(String filename) {
        InputStream stream = this.createInput(filename);
        if (stream == null) {
            System.err.println("Could not find the image " + filename + ".");
            return null;
        } else {
            Bitmap bitmap = null;

            try {
                bitmap = BitmapFactory.decodeStream(stream);
            } finally {
                try {
                    stream.close();
                    stream = null;
                } catch (IOException var10) {
                }

            }

            if (bitmap == null) {
                System.err.println("Could not load the image because the bitmap was empty.");
                return null;
            } else {
                PImage image = new PImage(bitmap);
                image.parent = this;
                return image;
            }
        }
    }

    public PImage requestImage(String filename) {
        PImage vessel = this.createImage(0, 0, 2);
        PApplet.AsyncImageLoader ail = new PApplet.AsyncImageLoader(filename, vessel);
        ail.start();
        return vessel;
    }

    public XML createXML(String name) {
        try {
            return new XML(name);
        } catch (Exception var3) {
            this.printStackTrace(var3);
            return null;
        }
    }

    public XML loadXML(String filename) {
        return this.loadXML(filename, (String)null);
    }

    public XML loadXML(String filename, String options) {
        try {
            return new XML(this.createInput(filename), options);
        } catch (Exception var4) {
            this.printStackTrace(var4);
            return null;
        }
    }

    public XML parseXML(String xmlString) {
        return this.parseXML(xmlString, (String)null);
    }

    public XML parseXML(String xmlString, String options) {
        try {
            return XML.parse(xmlString, options);
        } catch (Exception var4) {
            this.printStackTrace(var4);
            return null;
        }
    }

    public boolean saveXML(XML xml, String filename) {
        return this.saveXML(xml, filename, (String)null);
    }

    public boolean saveXML(XML xml, String filename, String options) {
        return xml.save(this.saveFile(filename), options);
    }

    public JSONObject parseJSONObject(String input) {
        return new JSONObject(new StringReader(input));
    }

    public JSONObject loadJSONObject(String filename) {
        return new JSONObject(this.createReader(filename));
    }

    public static JSONObject loadJSONObject(File file) {
        return new JSONObject(createReader(file));
    }

    public boolean saveJSONObject(JSONObject json, String filename) {
        return this.saveJSONObject(json, filename, (String)null);
    }

    public boolean saveJSONObject(JSONObject json, String filename, String options) {
        return json.save(this.saveFile(filename), options);
    }

    public JSONArray parseJSONArray(String input) {
        return new JSONArray(new StringReader(input));
    }

    public JSONArray loadJSONArray(String filename) {
        return new JSONArray(this.createReader(filename));
    }

    public static JSONArray loadJSONArray(File file) {
        return new JSONArray(createReader(file));
    }

    public boolean saveJSONArray(JSONArray json, String filename) {
        return this.saveJSONArray(json, filename, (String)null);
    }

    public boolean saveJSONArray(JSONArray json, String filename, String options) {
        return json.save(this.saveFile(filename), options);
    }

    public Table createTable() {
        return new Table();
    }

    public Table loadTable(String filename) {
        return this.loadTable(filename, (String)null);
    }

    public Table loadTable(String filename, String options) {
        try {
            String ext = checkExtension(filename);
            if (ext != null && (ext.equals("csv") || ext.equals("tsv"))) {
                if (options == null) {
                    options = ext;
                } else {
                    options = ext + "," + options;
                }
            }

            return new Table(this.createInput(filename), options);
        } catch (IOException var4) {
            this.printStackTrace(var4);
            return null;
        }
    }

    public boolean saveTable(Table table, String filename) {
        return this.saveTable(table, filename, (String)null);
    }

    public boolean saveTable(Table table, String filename, String options) {
        try {
            table.save(this.saveFile(filename), options);
            return true;
        } catch (IOException var5) {
            this.printStackTrace(var5);
            return false;
        }
    }

    public PFont loadFont(String filename) {
        try {
            InputStream input = this.createInput(filename);
            return new PFont(input);
        } catch (Exception var3) {
            this.die("Could not load font " + filename + ". Make sure that the font has been copied to the data folder of your sketch.", var3);
            return null;
        }
    }

    protected PFont createDefaultFont(float size) {
        return this.createFont("SansSerif", size, true, (char[])null);
    }

    public PFont createFont(String name, float size) {
        return this.createFont(name, size, true, (char[])null);
    }

    public PFont createFont(String name, float size, boolean smooth) {
        return this.createFont(name, size, smooth, (char[])null);
    }

    public PFont createFont(String name, float size, boolean smooth, char[] charset) {
        String lowerName = name.toLowerCase();
        Typeface baseFont = null;
        if (!lowerName.endsWith(".otf") && !lowerName.endsWith(".ttf")) {
            baseFont = (Typeface)PFont.findNative(name);
        } else {
            AssetManager assets = this.surface.getAssets();
            baseFont = Typeface.createFromAsset(assets, name);
        }

        return new PFont(baseFont, round(size), smooth, charset);
    }

    public String[] listPaths(String path, String... options) {
        File[] list = this.listFiles(path, options);
        int offset = 0;
        String[] outgoing = options;
        int i = options.length;

        for(int var7 = 0; var7 < i; ++var7) {
            String opt = outgoing[var7];
            if (opt.equals("relative")) {
                if (!path.endsWith(File.pathSeparator)) {
                    path = path + File.pathSeparator;
                }

                offset = path.length();
                break;
            }
        }

        outgoing = new String[list.length];

        for(i = 0; i < list.length; ++i) {
            outgoing[i] = list[i].getAbsolutePath().substring(offset);
        }

        return outgoing;
    }

    public File[] listFiles(String path, String... options) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = this.sketchFile(path);
        }

        return listFiles(file, options);
    }

    public static File[] listFiles(File base, String... options) {
        boolean recursive = false;
        String[] extensions = null;
        boolean directories = true;
        boolean files = true;
        boolean hidden = false;
        String[] var7 = options;
        int var8 = options.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            String opt = var7[var9];
            if (opt.equals("recursive")) {
                recursive = true;
            } else if (opt.startsWith("extension=")) {
                extensions = new String[]{opt.substring(10)};
            } else if (opt.startsWith("extensions=")) {
                extensions = split(opt.substring(10), ',');
            } else if (opt.equals("files")) {
                directories = false;
            } else if (opt.equals("directories")) {
                files = false;
            } else if (opt.equals("hidden")) {
                hidden = true;
            } else if (!opt.equals("relative")) {
                throw new RuntimeException(opt + " is not a listFiles() option");
            }
        }

        if (extensions != null) {
            for(int i = 0; i < extensions.length; ++i) {
                extensions[i] = "." + extensions[i];
            }
        }

        if (!files && !directories) {
            files = true;
            directories = true;
        }

        if (!base.canRead()) {
            return null;
        } else {
            List<File> outgoing = new ArrayList();
            listFilesImpl(base, recursive, extensions, hidden, directories, files, outgoing);
            return (File[])outgoing.toArray(new File[0]);
        }
    }

    static void listFilesImpl(File folder, boolean recursive, String[] extensions, boolean hidden, boolean directories, boolean files, List<File> list) {
        File[] items = folder.listFiles();
        if (items != null) {
            File[] var8 = items;
            int var9 = items.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                File item = var8[var10];
                String name = item.getName();
                if (hidden || name.charAt(0) != '.') {
                    if (item.isDirectory()) {
                        if (recursive) {
                            listFilesImpl(item, recursive, extensions, hidden, directories, files, list);
                        }

                        if (directories) {
                            list.add(item);
                        }
                    } else if (files) {
                        if (extensions == null) {
                            list.add(item);
                        } else {
                            String[] var13 = extensions;
                            int var14 = extensions.length;

                            for(int var15 = 0; var15 < var14; ++var15) {
                                String ext = var13[var15];
                                if (item.getName().toLowerCase().endsWith(ext)) {
                                    list.add(item);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static String checkExtension(String filename) {
        if (filename.toLowerCase().endsWith(".gz")) {
            filename = filename.substring(0, filename.length() - 3);
        }

        int dotIndex = filename.lastIndexOf(46);
        return dotIndex != -1 ? filename.substring(dotIndex + 1).toLowerCase() : null;
    }

    public BufferedReader createReader(String filename) {
        try {
            InputStream is = this.createInput(filename);
            if (is == null) {
                System.err.println(filename + " does not exist or could not be read");
                return null;
            } else {
                return createReader(is);
            }
        } catch (Exception var3) {
            if (filename == null) {
                System.err.println("Filename passed to reader() was null");
            } else {
                System.err.println("Couldn't create a reader for " + filename);
            }

            return null;
        }
    }

    public static BufferedReader createReader(File file) {
        try {
            InputStream is = new FileInputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                is = new GZIPInputStream((InputStream)is);
            }

            return createReader((InputStream)is);
        } catch (Exception var2) {
            if (file == null) {
                throw new RuntimeException("File passed to createReader() was null");
            } else {
                var2.printStackTrace();
                throw new RuntimeException("Couldn't create a reader for " + file.getAbsolutePath());
            }
        }
    }

    public static BufferedReader createReader(InputStream input) {
        InputStreamReader isr = new InputStreamReader(input, CompatUtils.getCharsetUTF8());
        BufferedReader reader = new BufferedReader(isr);

        try {
            reader.mark(1);
            int c = reader.read();
            if (c != 65279) {
                reader.reset();
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return reader;
    }

    public PrintWriter createWriter(String filename) {
        return createWriter(this.saveFile(filename));
    }

    public static PrintWriter createWriter(File file) {
        try {
            OutputStream output = new FileOutputStream(file);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                output = new GZIPOutputStream((OutputStream)output);
            }

            return createWriter((OutputStream)output);
        } catch (Exception var2) {
            if (file == null) {
                throw new RuntimeException("File passed to createWriter() was null");
            } else {
                var2.printStackTrace();
                throw new RuntimeException("Couldn't create a writer for " + file.getAbsolutePath());
            }
        }
    }

    public static PrintWriter createWriter(OutputStream output) {
        BufferedOutputStream bos = new BufferedOutputStream(output, 8192);
        OutputStreamWriter osw = new OutputStreamWriter(bos, CompatUtils.getCharsetUTF8());
        return new PrintWriter(osw);
    }

    public InputStream createInput(String filename) {
        InputStream input = this.createInputRaw(filename);
        String lower = filename.toLowerCase();
        if (input != null && (lower.endsWith(".gz") || lower.endsWith(".svgz"))) {
            try {
                return new BufferedInputStream(new GZIPInputStream(input));
            } catch (IOException var5) {
                this.printStackTrace(var5);
                return null;
            }
        } else {
            return new BufferedInputStream(input);
        }
    }

    public InputStream createInputRaw(String filename) {
        InputStream stream = null;
        if (filename == null) {
            return null;
        } else if (filename.length() == 0) {
            return null;
        } else {
            if (filename.indexOf(":") != -1) {
                try {
                    URL url = new URL(filename);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setDoInput(true);
                    con.connect();
                    return con.getInputStream();
                } catch (MalformedURLException var10) {
                } catch (FileNotFoundException var11) {
                } catch (IOException var12) {
                    this.printStackTrace(var12);
                    return null;
                }
            }

            AssetManager assets = this.surface.getAssets();

            try {
                stream = assets.open(filename);
                if (stream != null) {
                    return stream;
                }
            } catch (IOException var9) {
            }

            File absFile = new File(filename);
            FileInputStream stream;
            if (absFile.exists()) {
                try {
                    stream = new FileInputStream(absFile);
                    if (stream != null) {
                        return stream;
                    }
                } catch (FileNotFoundException var8) {
                }
            }

            File sketchFile = new File(this.sketchPath(filename));
            if (sketchFile.exists()) {
                try {
                    stream = new FileInputStream(sketchFile);
                    if (stream != null) {
                        return stream;
                    }
                } catch (FileNotFoundException var7) {
                }
            }

            return this.surface.openFileInput(filename);
        }
    }

    public static InputStream createInput(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File passed to createInput() was null");
        } else {
            try {
                InputStream input = new FileInputStream(file);
                return file.getName().toLowerCase().endsWith(".gz") ? new BufferedInputStream(new GZIPInputStream(input)) : new BufferedInputStream(input);
            } catch (IOException var2) {
                System.err.println("Could not createInput() for " + file);
                var2.printStackTrace();
                return null;
            }
        }
    }

    public byte[] loadBytes(String filename) {
        InputStream is = this.createInput(filename);
        if (is != null) {
            return loadBytes(is);
        } else {
            System.err.println("The file \"" + filename + "\" is missing or inaccessible, make sure the URL is valid or that the file has been added to your sketch and is readable.");
            return null;
        }
    }

    public static byte[] loadBytes(InputStream input) {
        try {
            BufferedInputStream bis = new BufferedInputStream(input);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            for(int c = bis.read(); c != -1; c = bis.read()) {
                out.write(c);
            }

            return out.toByteArray();
        } catch (IOException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static byte[] loadBytes(File file) {
        InputStream is = createInput(file);
        return loadBytes(is);
    }

    public static String[] loadStrings(File file) {
        InputStream is = createInput(file);
        return is != null ? loadStrings(is) : null;
    }

    public static String[] loadStrings(BufferedReader reader) {
        try {
            String[] lines = new String[100];
            int lineCount = 0;

            String[] output;
            for(String line = null; (line = reader.readLine()) != null; lines[lineCount++] = line) {
                if (lineCount == lines.length) {
                    output = new String[lineCount << 1];
                    System.arraycopy(lines, 0, output, 0, lineCount);
                    lines = output;
                }
            }

            reader.close();
            if (lineCount == lines.length) {
                return lines;
            } else {
                output = new String[lineCount];
                System.arraycopy(lines, 0, output, 0, lineCount);
                return output;
            }
        } catch (IOException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public String[] loadStrings(String filename) {
        InputStream is = this.createInput(filename);
        if (is != null) {
            return loadStrings(is);
        } else {
            System.err.println("The file \"" + filename + "\" is missing or inaccessible, make sure the URL is valid or that the file has been added to your sketch and is readable.");
            return null;
        }
    }

    public static String[] loadStrings(InputStream input) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String[] lines = new String[100];
            int lineCount = 0;

            String[] output;
            for(String line = null; (line = reader.readLine()) != null; lines[lineCount++] = line) {
                if (lineCount == lines.length) {
                    output = new String[lineCount << 1];
                    System.arraycopy(lines, 0, output, 0, lineCount);
                    lines = output;
                }
            }

            reader.close();
            if (lineCount == lines.length) {
                return lines;
            } else {
                output = new String[lineCount];
                System.arraycopy(lines, 0, output, 0, lineCount);
                return output;
            }
        } catch (IOException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public OutputStream createOutput(String filename) {
        try {
            File file = new File(filename);
            if (!file.isAbsolute()) {
                file = new File(this.sketchPath(filename));
            }

            FileOutputStream fos = new FileOutputStream(file);
            return (OutputStream)(file.getName().toLowerCase().endsWith(".gz") ? new GZIPOutputStream(fos) : fos);
        } catch (IOException var4) {
            this.printStackTrace(var4);
            return null;
        }
    }

    public static OutputStream createOutput(File file) {
        try {
            createPath(file);
            OutputStream output = new FileOutputStream(file);
            return file.getName().toLowerCase().endsWith(".gz") ? new BufferedOutputStream(new GZIPOutputStream(output)) : new BufferedOutputStream(output);
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    public boolean saveStream(String targetFilename, String sourceLocation) {
        return this.saveStream(this.saveFile(targetFilename), sourceLocation);
    }

    public boolean saveStream(File targetFile, String sourceLocation) {
        return saveStream(targetFile, this.createInputRaw(sourceLocation));
    }

    public boolean saveStream(String targetFilename, InputStream sourceStream) {
        return saveStream(this.saveFile(targetFilename), sourceStream);
    }

    public static boolean saveStream(File target, InputStream source) {
        File tempFile = null;

        try {
            createPath(target);
            tempFile = createTempFile(target);
            FileOutputStream targetStream = new FileOutputStream(tempFile);
            saveStream((OutputStream)targetStream, (InputStream)source);
            targetStream.close();
            targetStream = null;
            if (target.exists() && !target.delete()) {
                System.err.println("Could not replace " + target.getAbsolutePath() + ".");
            }

            if (!tempFile.renameTo(target)) {
                System.err.println("Could not rename temporary file " + tempFile.getAbsolutePath());
                return false;
            } else {
                return true;
            }
        } catch (IOException var4) {
            if (tempFile != null) {
                tempFile.delete();
            }

            var4.printStackTrace();
            return false;
        }
    }

    public static void saveStream(OutputStream target, InputStream source) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(source, 16384);
        BufferedOutputStream bos = new BufferedOutputStream(target);
        byte[] buffer = new byte[8192];

        int bytesRead;
        while((bytesRead = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }

        bos.flush();
    }

    public void saveBytes(String filename, byte[] data) {
        saveBytes(this.saveFile(filename), data);
    }

    private static File createTempFile(File file) throws IOException {
        File parentDir = file.getParentFile();
        String name = file.getName();
        String suffix = null;
        int dot = name.lastIndexOf(46);
        String prefix;
        if (dot == -1) {
            prefix = name;
        } else {
            prefix = name.substring(0, dot);
            suffix = name.substring(dot);
        }

        if (prefix.length() < 3) {
            prefix = prefix + "processing";
        }

        return File.createTempFile(prefix, suffix, parentDir);
    }

    public static void saveBytes(File file, byte[] data) {
        File tempFile = null;

        try {
            tempFile = createTempFile(file);
            OutputStream output = createOutput(tempFile);
            saveBytes(output, data);
            output.close();
            output = null;
            if (file.exists() && !file.delete()) {
                System.err.println("Could not replace " + file.getAbsolutePath());
            }

            if (!tempFile.renameTo(file)) {
                System.err.println("Could not rename temporary file " + tempFile.getAbsolutePath());
            }
        } catch (IOException var4) {
            System.err.println("error saving bytes to " + file);
            if (tempFile != null) {
                tempFile.delete();
            }

            var4.printStackTrace();
        }

    }

    public static void saveBytes(OutputStream output, byte[] data) {
        try {
            output.write(data);
            output.flush();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void saveStrings(String filename, String[] strings) {
        saveStrings(this.saveFile(filename), strings);
    }

    public static void saveStrings(File file, String[] strings) {
        try {
            String location = file.getAbsolutePath();
            createPath(location);
            OutputStream output = new FileOutputStream(location);
            if (file.getName().toLowerCase().endsWith(".gz")) {
                output = new GZIPOutputStream((OutputStream)output);
            }

            saveStrings((OutputStream)output, strings);
            ((OutputStream)output).close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public static void saveStrings(OutputStream output, String[] strings) {
        try {
            OutputStreamWriter osw = new OutputStreamWriter(output, "UTF-8");
            PrintWriter writer = new PrintWriter(osw);

            for(int i = 0; i < strings.length; ++i) {
                writer.println(strings[i]);
            }

            writer.flush();
        } catch (UnsupportedEncodingException var5) {
        }

    }

    public String sketchPath(String where) {
        if (this.sketchPath == null) {
            return where;
        } else {
            try {
                if ((new File(where)).isAbsolute()) {
                    return where;
                }
            } catch (Exception var3) {
            }

            return this.surface.getFileStreamPath(where).getAbsolutePath();
        }
    }

    public File sketchFile(String where) {
        return new File(this.sketchPath(where));
    }

    public String savePath(String where) {
        if (where == null) {
            return null;
        } else {
            String filename = this.sketchPath(where);
            createPath(filename);
            return filename;
        }
    }

    public File saveFile(String where) {
        return new File(this.savePath(where));
    }

    public String dataPath(String where) {
        boolean isAsset = false;
        AssetManager assets = this.surface.getAssets();
        InputStream is = null;

        try {
            is = assets.open(where);
            isAsset = true;
        } catch (IOException var14) {
        } finally {
            try {
                is.close();
            } catch (Exception var13) {
            }

        }

        return isAsset ? where : this.sketchPath(where);
    }

    public File dataFile(String where) {
        return new File(this.dataPath(where));
    }

    public static void createPath(String path) {
        createPath(new File(path));
    }

    public static void createPath(File file) {
        try {
            String parent = file.getParent();
            if (parent != null) {
                File unit = new File(parent);
                if (!unit.exists()) {
                    unit.mkdirs();
                }
            }
        } catch (SecurityException var3) {
            System.err.println("You don't have permissions to create " + file.getAbsolutePath());
        }

    }

    public static String getExtension(String filename) {
        String lower = filename.toLowerCase();
        int dot = filename.lastIndexOf(46);
        String extension;
        if (dot == -1) {
            extension = "unknown";
        }

        extension = lower.substring(dot + 1);
        int question = extension.indexOf(63);
        if (question != -1) {
            extension = extension.substring(0, question);
        }

        return extension;
    }

    public static String urlEncode(String what) {
        try {
            return URLEncoder.encode(what, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

    public static String urlDecode(String what) {
        try {
            return URLDecoder.decode(what, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

    public static byte[] sort(byte[] what) {
        return sort(what, what.length);
    }

    public static byte[] sort(byte[] what, int count) {
        byte[] outgoing = new byte[what.length];
        System.arraycopy(what, 0, outgoing, 0, what.length);
        Arrays.sort(outgoing, 0, count);
        return outgoing;
    }

    public static char[] sort(char[] what) {
        return sort(what, what.length);
    }

    public static char[] sort(char[] what, int count) {
        char[] outgoing = new char[what.length];
        System.arraycopy(what, 0, outgoing, 0, what.length);
        Arrays.sort(outgoing, 0, count);
        return outgoing;
    }

    public static int[] sort(int[] what) {
        return sort(what, what.length);
    }

    public static int[] sort(int[] what, int count) {
        int[] outgoing = new int[what.length];
        System.arraycopy(what, 0, outgoing, 0, what.length);
        Arrays.sort(outgoing, 0, count);
        return outgoing;
    }

    public static float[] sort(float[] what) {
        return sort(what, what.length);
    }

    public static float[] sort(float[] what, int count) {
        float[] outgoing = new float[what.length];
        System.arraycopy(what, 0, outgoing, 0, what.length);
        Arrays.sort(outgoing, 0, count);
        return outgoing;
    }

    public static String[] sort(String[] what) {
        return sort(what, what.length);
    }

    public static String[] sort(String[] what, int count) {
        String[] outgoing = new String[what.length];
        System.arraycopy(what, 0, outgoing, 0, what.length);
        Arrays.sort(outgoing, 0, count);
        return outgoing;
    }

    public static void arrayCopy(Object src, int srcPosition, Object dst, int dstPosition, int length) {
        System.arraycopy(src, srcPosition, dst, dstPosition, length);
    }

    public static void arrayCopy(Object src, Object dst, int length) {
        System.arraycopy(src, 0, dst, 0, length);
    }

    public static void arrayCopy(Object src, Object dst) {
        System.arraycopy(src, 0, dst, 0, Array.getLength(src));
    }

    public static boolean[] expand(boolean[] list) {
        return expand(list, list.length << 1);
    }

    public static boolean[] expand(boolean[] list, int newSize) {
        boolean[] temp = new boolean[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static byte[] expand(byte[] list) {
        return expand(list, list.length << 1);
    }

    public static byte[] expand(byte[] list, int newSize) {
        byte[] temp = new byte[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static char[] expand(char[] list) {
        return expand(list, list.length << 1);
    }

    public static char[] expand(char[] list, int newSize) {
        char[] temp = new char[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static int[] expand(int[] list) {
        return expand(list, list.length << 1);
    }

    public static int[] expand(int[] list, int newSize) {
        int[] temp = new int[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static long[] expand(long[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public static long[] expand(long[] list, int newSize) {
        long[] temp = new long[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static float[] expand(float[] list) {
        return expand(list, list.length << 1);
    }

    public static float[] expand(float[] list, int newSize) {
        float[] temp = new float[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static double[] expand(double[] list) {
        return expand(list, list.length > 0 ? list.length << 1 : 1);
    }

    public static double[] expand(double[] list, int newSize) {
        double[] temp = new double[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static String[] expand(String[] list) {
        return expand(list, list.length << 1);
    }

    public static String[] expand(String[] list, int newSize) {
        String[] temp = new String[newSize];
        System.arraycopy(list, 0, temp, 0, Math.min(newSize, list.length));
        return temp;
    }

    public static Object expand(Object array) {
        return expand(array, Array.getLength(array) << 1);
    }

    public static Object expand(Object list, int newSize) {
        Class<?> type = list.getClass().getComponentType();
        Object temp = Array.newInstance(type, newSize);
        System.arraycopy(list, 0, temp, 0, Math.min(Array.getLength(list), newSize));
        return temp;
    }

    public static byte[] append(byte[] b, byte value) {
        b = expand(b, b.length + 1);
        b[b.length - 1] = value;
        return b;
    }

    public static char[] append(char[] b, char value) {
        b = expand(b, b.length + 1);
        b[b.length - 1] = value;
        return b;
    }

    public static int[] append(int[] b, int value) {
        b = expand(b, b.length + 1);
        b[b.length - 1] = value;
        return b;
    }

    public static float[] append(float[] b, float value) {
        b = expand(b, b.length + 1);
        b[b.length - 1] = value;
        return b;
    }

    public static String[] append(String[] b, String value) {
        b = expand(b, b.length + 1);
        b[b.length - 1] = value;
        return b;
    }

    public static Object append(Object b, Object value) {
        int length = Array.getLength(b);
        b = expand(b, length + 1);
        Array.set(b, length, value);
        return b;
    }

    public static boolean[] shorten(boolean[] list) {
        return subset((boolean[])list, 0, list.length - 1);
    }

    public static byte[] shorten(byte[] list) {
        return subset((byte[])list, 0, list.length - 1);
    }

    public static char[] shorten(char[] list) {
        return subset((char[])list, 0, list.length - 1);
    }

    public static int[] shorten(int[] list) {
        return subset((int[])list, 0, list.length - 1);
    }

    public static float[] shorten(float[] list) {
        return subset((float[])list, 0, list.length - 1);
    }

    public static String[] shorten(String[] list) {
        return subset((String[])list, 0, list.length - 1);
    }

    public static Object shorten(Object list) {
        int length = Array.getLength(list);
        return subset((Object)list, 0, length - 1);
    }

    public static final boolean[] splice(boolean[] list, boolean v, int index) {
        boolean[] outgoing = new boolean[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = v;
        System.arraycopy(list, index, outgoing, index + 1, list.length - index);
        return outgoing;
    }

    public static final boolean[] splice(boolean[] list, boolean[] v, int index) {
        boolean[] outgoing = new boolean[list.length + v.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(v, 0, outgoing, index, v.length);
        System.arraycopy(list, index, outgoing, index + v.length, list.length - index);
        return outgoing;
    }

    public static final byte[] splice(byte[] list, byte v, int index) {
        byte[] outgoing = new byte[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = v;
        System.arraycopy(list, index, outgoing, index + 1, list.length - index);
        return outgoing;
    }

    public static final byte[] splice(byte[] list, byte[] v, int index) {
        byte[] outgoing = new byte[list.length + v.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(v, 0, outgoing, index, v.length);
        System.arraycopy(list, index, outgoing, index + v.length, list.length - index);
        return outgoing;
    }

    public static final char[] splice(char[] list, char v, int index) {
        char[] outgoing = new char[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = v;
        System.arraycopy(list, index, outgoing, index + 1, list.length - index);
        return outgoing;
    }

    public static final char[] splice(char[] list, char[] v, int index) {
        char[] outgoing = new char[list.length + v.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(v, 0, outgoing, index, v.length);
        System.arraycopy(list, index, outgoing, index + v.length, list.length - index);
        return outgoing;
    }

    public static final int[] splice(int[] list, int v, int index) {
        int[] outgoing = new int[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = v;
        System.arraycopy(list, index, outgoing, index + 1, list.length - index);
        return outgoing;
    }

    public static final int[] splice(int[] list, int[] v, int index) {
        int[] outgoing = new int[list.length + v.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(v, 0, outgoing, index, v.length);
        System.arraycopy(list, index, outgoing, index + v.length, list.length - index);
        return outgoing;
    }

    public static final float[] splice(float[] list, float v, int index) {
        float[] outgoing = new float[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = v;
        System.arraycopy(list, index, outgoing, index + 1, list.length - index);
        return outgoing;
    }

    public static final float[] splice(float[] list, float[] v, int index) {
        float[] outgoing = new float[list.length + v.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(v, 0, outgoing, index, v.length);
        System.arraycopy(list, index, outgoing, index + v.length, list.length - index);
        return outgoing;
    }

    public static final String[] splice(String[] list, String v, int index) {
        String[] outgoing = new String[list.length + 1];
        System.arraycopy(list, 0, outgoing, 0, index);
        outgoing[index] = v;
        System.arraycopy(list, index, outgoing, index + 1, list.length - index);
        return outgoing;
    }

    public static final String[] splice(String[] list, String[] v, int index) {
        String[] outgoing = new String[list.length + v.length];
        System.arraycopy(list, 0, outgoing, 0, index);
        System.arraycopy(v, 0, outgoing, index, v.length);
        System.arraycopy(list, index, outgoing, index + v.length, list.length - index);
        return outgoing;
    }

    public static final Object splice(Object list, Object v, int index) {
        Object[] outgoing = null;
        int length = Array.getLength(list);
        if (v.getClass().getName().charAt(0) == '[') {
            int vlength = Array.getLength(v);
            outgoing = new Object[length + vlength];
            System.arraycopy(list, 0, outgoing, 0, index);
            System.arraycopy(v, 0, outgoing, index, vlength);
            System.arraycopy(list, index, outgoing, index + vlength, length - index);
        } else {
            outgoing = new Object[length + 1];
            System.arraycopy(list, 0, outgoing, 0, index);
            Array.set(outgoing, index, v);
            System.arraycopy(list, index, outgoing, index + 1, length - index);
        }

        return outgoing;
    }

    public static boolean[] subset(boolean[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static boolean[] subset(boolean[] list, int start, int count) {
        boolean[] output = new boolean[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static byte[] subset(byte[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static byte[] subset(byte[] list, int start, int count) {
        byte[] output = new byte[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static char[] subset(char[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static char[] subset(char[] list, int start, int count) {
        char[] output = new char[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static int[] subset(int[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static int[] subset(int[] list, int start, int count) {
        int[] output = new int[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static long[] subset(long[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static long[] subset(long[] list, int start, int count) {
        long[] output = new long[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static float[] subset(float[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static float[] subset(float[] list, int start, int count) {
        float[] output = new float[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static double[] subset(double[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static double[] subset(double[] list, int start, int count) {
        double[] output = new double[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static String[] subset(String[] list, int start) {
        return subset(list, start, list.length - start);
    }

    public static String[] subset(String[] list, int start, int count) {
        String[] output = new String[count];
        System.arraycopy(list, start, output, 0, count);
        return output;
    }

    public static Object subset(Object list, int start) {
        int length = Array.getLength(list);
        return subset(list, start, length - start);
    }

    public static Object subset(Object list, int start, int count) {
        Class<?> type = list.getClass().getComponentType();
        Object outgoing = Array.newInstance(type, count);
        System.arraycopy(list, start, outgoing, 0, count);
        return outgoing;
    }

    public static boolean[] concat(boolean[] a, boolean[] b) {
        boolean[] c = new boolean[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static char[] concat(char[] a, char[] b) {
        char[] c = new char[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static int[] concat(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static float[] concat(float[] a, float[] b) {
        float[] c = new float[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static String[] concat(String[] a, String[] b) {
        String[] c = new String[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static Object concat(Object a, Object b) {
        Class<?> type = a.getClass().getComponentType();
        int alength = Array.getLength(a);
        int blength = Array.getLength(b);
        Object outgoing = Array.newInstance(type, alength + blength);
        System.arraycopy(a, 0, outgoing, 0, alength);
        System.arraycopy(b, 0, outgoing, alength, blength);
        return outgoing;
    }

    public static boolean[] reverse(boolean[] list) {
        boolean[] outgoing = new boolean[list.length];
        int length1 = list.length - 1;

        for(int i = 0; i < list.length; ++i) {
            outgoing[i] = list[length1 - i];
        }

        return outgoing;
    }

    public static byte[] reverse(byte[] list) {
        byte[] outgoing = new byte[list.length];
        int length1 = list.length - 1;

        for(int i = 0; i < list.length; ++i) {
            outgoing[i] = list[length1 - i];
        }

        return outgoing;
    }

    public static char[] reverse(char[] list) {
        char[] outgoing = new char[list.length];
        int length1 = list.length - 1;

        for(int i = 0; i < list.length; ++i) {
            outgoing[i] = list[length1 - i];
        }

        return outgoing;
    }

    public static int[] reverse(int[] list) {
        int[] outgoing = new int[list.length];
        int length1 = list.length - 1;

        for(int i = 0; i < list.length; ++i) {
            outgoing[i] = list[length1 - i];
        }

        return outgoing;
    }

    public static float[] reverse(float[] list) {
        float[] outgoing = new float[list.length];
        int length1 = list.length - 1;

        for(int i = 0; i < list.length; ++i) {
            outgoing[i] = list[length1 - i];
        }

        return outgoing;
    }

    public static String[] reverse(String[] list) {
        String[] outgoing = new String[list.length];
        int length1 = list.length - 1;

        for(int i = 0; i < list.length; ++i) {
            outgoing[i] = list[length1 - i];
        }

        return outgoing;
    }

    public static Object reverse(Object list) {
        Class<?> type = list.getClass().getComponentType();
        int length = Array.getLength(list);
        Object outgoing = Array.newInstance(type, length);

        for(int i = 0; i < length; ++i) {
            Array.set(outgoing, i, Array.get(list, length - 1 - i));
        }

        return outgoing;
    }

    public static String trim(String str) {
        return str.replace(' ', ' ').trim();
    }

    public static String[] trim(String[] array) {
        String[] outgoing = new String[array.length];

        for(int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                outgoing[i] = array[i].replace(' ', ' ').trim();
            }
        }

        return outgoing;
    }

    public static String join(String[] str, char separator) {
        return join(str, String.valueOf(separator));
    }

    public static String join(String[] str, String separator) {
        StringBuffer buffer = new StringBuffer();

        for(int i = 0; i < str.length; ++i) {
            if (i != 0) {
                buffer.append(separator);
            }

            buffer.append(str[i]);
        }

        return buffer.toString();
    }

    public static String[] splitTokens(String what) {
        return splitTokens(what, " \t\n\r\f ");
    }

    public static String[] splitTokens(String what, String delim) {
        StringTokenizer toker = new StringTokenizer(what, delim);
        String[] pieces = new String[toker.countTokens()];

        for(int var4 = 0; toker.hasMoreTokens(); pieces[var4++] = toker.nextToken()) {
        }

        return pieces;
    }

    public static String[] split(String what, char delim) {
        if (what == null) {
            return null;
        } else {
            char[] chars = what.toCharArray();
            int splitCount = 0;

            for(int i = 0; i < chars.length; ++i) {
                if (chars[i] == delim) {
                    ++splitCount;
                }
            }

            String[] splits;
            if (splitCount == 0) {
                splits = new String[]{new String(what)};
                return splits;
            } else {
                splits = new String[splitCount + 1];
                int splitIndex = 0;
                int startIndex = 0;

                for(int i = 0; i < chars.length; ++i) {
                    if (chars[i] == delim) {
                        splits[splitIndex++] = new String(chars, startIndex, i - startIndex);
                        startIndex = i + 1;
                    }
                }

                splits[splitIndex] = new String(chars, startIndex, chars.length - startIndex);
                return splits;
            }
        }
    }

    public static String[] split(String what, String delim) {
        ArrayList<String> items = new ArrayList();

        int index;
        int offset;
        for(offset = 0; (index = what.indexOf(delim, offset)) != -1; offset = index + delim.length()) {
            items.add(what.substring(offset, index));
        }

        items.add(what.substring(offset));
        String[] outgoing = new String[items.size()];
        items.toArray(outgoing);
        return outgoing;
    }

    static Pattern matchPattern(String regexp) {
        Pattern p = null;
        if (matchPatterns == null) {
            matchPatterns = new HashMap();
        } else {
            p = (Pattern)matchPatterns.get(regexp);
        }

        if (p == null) {
            if (matchPatterns.size() == 10) {
                matchPatterns.clear();
            }

            p = Pattern.compile(regexp, 40);
            matchPatterns.put(regexp, p);
        }

        return p;
    }

    public static String[] match(String what, String regexp) {
        Pattern p = matchPattern(regexp);
        Matcher m = p.matcher(what);
        if (!m.find()) {
            return null;
        } else {
            int count = m.groupCount() + 1;
            String[] groups = new String[count];

            for(int i = 0; i < count; ++i) {
                groups[i] = m.group(i);
            }

            return groups;
        }
    }

    public static String[][] matchAll(String what, String regexp) {
        Pattern p = matchPattern(regexp);
        Matcher m = p.matcher(what);
        ArrayList<String[]> results = new ArrayList();
        int count = m.groupCount() + 1;

        int i;
        while(m.find()) {
            String[] groups = new String[count];

            for(i = 0; i < count; ++i) {
                groups[i] = m.group(i);
            }

            results.add(groups);
        }

        if (results.isEmpty()) {
            return (String[][])null;
        } else {
            String[][] matches = new String[results.size()][count];

            for(i = 0; i < matches.length; ++i) {
                matches[i] = (String[])((String[])results.get(i));
            }

            return matches;
        }
    }

    public static final boolean parseBoolean(int what) {
        return what != 0;
    }

    public static final boolean parseBoolean(String what) {
        return new Boolean(what);
    }

    public static final boolean[] parseBoolean(byte[] what) {
        boolean[] outgoing = new boolean[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = what[i] != 0;
        }

        return outgoing;
    }

    public static final boolean[] parseBoolean(int[] what) {
        boolean[] outgoing = new boolean[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = what[i] != 0;
        }

        return outgoing;
    }

    public static final boolean[] parseBoolean(String[] what) {
        boolean[] outgoing = new boolean[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = new Boolean(what[i]);
        }

        return outgoing;
    }

    public static final byte parseByte(boolean what) {
        return (byte)(what ? 1 : 0);
    }

    public static final byte parseByte(char what) {
        return (byte)what;
    }

    public static final byte parseByte(int what) {
        return (byte)what;
    }

    public static final byte parseByte(float what) {
        return (byte)((int)what);
    }

    public static final byte[] parseByte(boolean[] what) {
        byte[] outgoing = new byte[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = (byte)(what[i] ? 1 : 0);
        }

        return outgoing;
    }

    public static final byte[] parseByte(char[] what) {
        byte[] outgoing = new byte[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = (byte)what[i];
        }

        return outgoing;
    }

    public static final byte[] parseByte(int[] what) {
        byte[] outgoing = new byte[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = (byte)what[i];
        }

        return outgoing;
    }

    public static final byte[] parseByte(float[] what) {
        byte[] outgoing = new byte[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = (byte)((int)what[i]);
        }

        return outgoing;
    }

    public static final char parseChar(byte what) {
        return (char)(what & 255);
    }

    public static final char parseChar(int what) {
        return (char)what;
    }

    public static final char[] parseChar(byte[] what) {
        char[] outgoing = new char[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = (char)(what[i] & 255);
        }

        return outgoing;
    }

    public static final char[] parseChar(int[] what) {
        char[] outgoing = new char[what.length];

        for(int i = 0; i < what.length; ++i) {
            outgoing[i] = (char)what[i];
        }

        return outgoing;
    }

    public static final int parseInt(boolean what) {
        return what ? 1 : 0;
    }

    public static final int parseInt(byte what) {
        return what & 255;
    }

    public static final int parseInt(char what) {
        return what;
    }

    public static final int parseInt(float what) {
        return (int)what;
    }

    public static final int parseInt(String what) {
        return parseInt((String)what, 0);
    }

    public static final int parseInt(String what, int otherwise) {
        try {
            int offset = what.indexOf(46);
            return offset == -1 ? Integer.parseInt(what) : Integer.parseInt(what.substring(0, offset));
        } catch (NumberFormatException var3) {
            return otherwise;
        }
    }

    public static final int[] parseInt(boolean[] what) {
        int[] list = new int[what.length];

        for(int i = 0; i < what.length; ++i) {
            list[i] = what[i] ? 1 : 0;
        }

        return list;
    }

    public static final int[] parseInt(byte[] what) {
        int[] list = new int[what.length];

        for(int i = 0; i < what.length; ++i) {
            list[i] = what[i] & 255;
        }

        return list;
    }

    public static final int[] parseInt(char[] what) {
        int[] list = new int[what.length];

        for(int i = 0; i < what.length; ++i) {
            list[i] = what[i];
        }

        return list;
    }

    public static int[] parseInt(float[] what) {
        int[] inties = new int[what.length];

        for(int i = 0; i < what.length; ++i) {
            inties[i] = (int)what[i];
        }

        return inties;
    }

    public static int[] parseInt(String[] what) {
        return parseInt((String[])what, 0);
    }

    public static int[] parseInt(String[] what, int missing) {
        int[] output = new int[what.length];

        for(int i = 0; i < what.length; ++i) {
            try {
                output[i] = Integer.parseInt(what[i]);
            } catch (NumberFormatException var5) {
                output[i] = missing;
            }
        }

        return output;
    }

    public static final float parseFloat(int what) {
        return (float)what;
    }

    public static final float parseFloat(String what) {
        return parseFloat(what, 0.0F / 0.0);
    }

    public static final float parseFloat(String what, float otherwise) {
        try {
            return new Float(what);
        } catch (NumberFormatException var3) {
            return otherwise;
        }
    }

    public static final float[] parseByte(byte[] what) {
        float[] floaties = new float[what.length];

        for(int i = 0; i < what.length; ++i) {
            floaties[i] = (float)what[i];
        }

        return floaties;
    }

    public static final float[] parseFloat(int[] what) {
        float[] floaties = new float[what.length];

        for(int i = 0; i < what.length; ++i) {
            floaties[i] = (float)what[i];
        }

        return floaties;
    }

    public static final float[] parseFloat(String[] what) {
        return parseFloat(what, 0.0F / 0.0);
    }

    public static final float[] parseFloat(String[] what, float missing) {
        float[] output = new float[what.length];

        for(int i = 0; i < what.length; ++i) {
            try {
                output[i] = new Float(what[i]);
            } catch (NumberFormatException var5) {
                output[i] = missing;
            }
        }

        return output;
    }

    public static final String str(boolean x) {
        return String.valueOf(x);
    }

    public static final String str(byte x) {
        return String.valueOf(x);
    }

    public static final String str(char x) {
        return String.valueOf(x);
    }

    public static final String str(int x) {
        return String.valueOf(x);
    }

    public static final String str(float x) {
        return String.valueOf(x);
    }

    public static final String[] str(boolean[] x) {
        String[] s = new String[x.length];

        for(int i = 0; i < x.length; ++i) {
            s[i] = String.valueOf(x[i]);
        }

        return s;
    }

    public static final String[] str(byte[] x) {
        String[] s = new String[x.length];

        for(int i = 0; i < x.length; ++i) {
            s[i] = String.valueOf(x[i]);
        }

        return s;
    }

    public static final String[] str(char[] x) {
        String[] s = new String[x.length];

        for(int i = 0; i < x.length; ++i) {
            s[i] = String.valueOf(x[i]);
        }

        return s;
    }

    public static final String[] str(int[] x) {
        String[] s = new String[x.length];

        for(int i = 0; i < x.length; ++i) {
            s[i] = String.valueOf(x[i]);
        }

        return s;
    }

    public static final String[] str(float[] x) {
        String[] s = new String[x.length];

        for(int i = 0; i < x.length; ++i) {
            s[i] = String.valueOf(x[i]);
        }

        return s;
    }

    public static String[] nf(int[] num, int digits) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nf(num[i], digits);
        }

        return formatted;
    }

    public static String nf(int num, int digits) {
        if (int_nf != null && int_nf_digits == digits && !int_nf_commas) {
            return int_nf.format((long)num);
        } else {
            int_nf = NumberFormat.getInstance();
            int_nf.setGroupingUsed(false);
            int_nf_commas = false;
            int_nf.setMinimumIntegerDigits(digits);
            int_nf_digits = digits;
            return int_nf.format((long)num);
        }
    }

    public static String[] nfc(int[] num) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nfc(num[i]);
        }

        return formatted;
    }

    public static String nfc(int num) {
        if (int_nf != null && int_nf_digits == 0 && int_nf_commas) {
            return int_nf.format((long)num);
        } else {
            int_nf = NumberFormat.getInstance();
            int_nf.setGroupingUsed(true);
            int_nf_commas = true;
            int_nf.setMinimumIntegerDigits(0);
            int_nf_digits = 0;
            return int_nf.format((long)num);
        }
    }

    public static String nfs(int num, int digits) {
        return num < 0 ? nf(num, digits) : ' ' + nf(num, digits);
    }

    public static String[] nfs(int[] num, int digits) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nfs(num[i], digits);
        }

        return formatted;
    }

    public static String nfp(int num, int digits) {
        return num < 0 ? nf(num, digits) : '+' + nf(num, digits);
    }

    public static String[] nfp(int[] num, int digits) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nfp(num[i], digits);
        }

        return formatted;
    }

    public static String[] nf(float[] num, int left, int right) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nf(num[i], left, right);
        }

        return formatted;
    }

    public static String nf(float num, int left, int right) {
        if (float_nf != null && float_nf_left == left && float_nf_right == right && !float_nf_commas) {
            return float_nf.format((double)num);
        } else {
            float_nf = NumberFormat.getInstance();
            float_nf.setGroupingUsed(false);
            float_nf_commas = false;
            if (left != 0) {
                float_nf.setMinimumIntegerDigits(left);
            }

            if (right != 0) {
                float_nf.setMinimumFractionDigits(right);
                float_nf.setMaximumFractionDigits(right);
            }

            float_nf_left = left;
            float_nf_right = right;
            return float_nf.format((double)num);
        }
    }

    public static String[] nfc(float[] num, int right) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nfc(num[i], right);
        }

        return formatted;
    }

    public static String nfc(float num, int right) {
        if (float_nf != null && float_nf_left == 0 && float_nf_right == right && float_nf_commas) {
            return float_nf.format((double)num);
        } else {
            float_nf = NumberFormat.getInstance();
            float_nf.setGroupingUsed(true);
            float_nf_commas = true;
            if (right != 0) {
                float_nf.setMinimumFractionDigits(right);
                float_nf.setMaximumFractionDigits(right);
            }

            float_nf_left = 0;
            float_nf_right = right;
            return float_nf.format((double)num);
        }
    }

    public static String[] nfs(float[] num, int left, int right) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nfs(num[i], left, right);
        }

        return formatted;
    }

    public static String nfs(float num, int left, int right) {
        return num < 0.0F ? nf(num, left, right) : ' ' + nf(num, left, right);
    }

    public static String[] nfp(float[] num, int left, int right) {
        String[] formatted = new String[num.length];

        for(int i = 0; i < formatted.length; ++i) {
            formatted[i] = nfp(num[i], left, right);
        }

        return formatted;
    }

    public static String nfp(float num, int left, int right) {
        return num < 0.0F ? nf(num, left, right) : '+' + nf(num, left, right);
    }

    public static final String hex(byte what) {
        return hex(what, 2);
    }

    public static final String hex(char what) {
        return hex(what, 4);
    }

    public static final String hex(int what) {
        return hex(what, 8);
    }

    public static final String hex(int what, int digits) {
        String stuff = Integer.toHexString(what).toUpperCase();
        if (digits > 8) {
            digits = 8;
        }

        int length = stuff.length();
        if (length > digits) {
            return stuff.substring(length - digits);
        } else {
            return length < digits ? "00000000".substring(8 - (digits - length)) + stuff : stuff;
        }
    }

    public static final int unhex(String what) {
        return (int)Long.parseLong(what, 16);
    }

    public static final String binary(byte what) {
        return binary(what, 8);
    }

    public static final String binary(char what) {
        return binary(what, 16);
    }

    public static final String binary(int what) {
        return binary(what, 32);
    }

    public static final String binary(int what, int digits) {
        String stuff = Integer.toBinaryString(what);
        if (digits > 32) {
            digits = 32;
        }

        int length = stuff.length();
        if (length > digits) {
            return stuff.substring(length - digits);
        } else if (length < digits) {
            int offset = 32 - (digits - length);
            return "00000000000000000000000000000000".substring(offset) + stuff;
        } else {
            return stuff;
        }
    }

    public static final int unbinary(String what) {
        return Integer.parseInt(what, 2);
    }

    public final int color(int gray) {
        if (this.g == null) {
            if (gray > 255) {
                gray = 255;
            } else if (gray < 0) {
                gray = 0;
            }

            return -16777216 | gray << 16 | gray << 8 | gray;
        } else {
            return this.g.color(gray);
        }
    }

    public final int color(float fgray) {
        if (this.g == null) {
            int gray = (int)fgray;
            if (gray > 255) {
                gray = 255;
            } else if (gray < 0) {
                gray = 0;
            }

            return -16777216 | gray << 16 | gray << 8 | gray;
        } else {
            return this.g.color(fgray);
        }
    }

    public final int color(int gray, int alpha) {
        if (this.g == null) {
            if (alpha > 255) {
                alpha = 255;
            } else if (alpha < 0) {
                alpha = 0;
            }

            return gray > 255 ? alpha << 24 | gray & 16777215 : alpha << 24 | gray << 16 | gray << 8 | gray;
        } else {
            return this.g.color(gray, alpha);
        }
    }

    public final int color(float fgray, float falpha) {
        if (this.g == null) {
            int gray = (int)fgray;
            int alpha = (int)falpha;
            if (gray > 255) {
                gray = 255;
            } else if (gray < 0) {
                gray = 0;
            }

            boolean var5;
            if (alpha > 255) {
                var5 = true;
            } else if (alpha < 0) {
                var5 = false;
            }

            return -16777216 | gray << 16 | gray << 8 | gray;
        } else {
            return this.g.color(fgray, falpha);
        }
    }

    public final int color(int x, int y, int z) {
        if (this.g == null) {
            if (x > 255) {
                x = 255;
            } else if (x < 0) {
                x = 0;
            }

            if (y > 255) {
                y = 255;
            } else if (y < 0) {
                y = 0;
            }

            if (z > 255) {
                z = 255;
            } else if (z < 0) {
                z = 0;
            }

            return -16777216 | x << 16 | y << 8 | z;
        } else {
            return this.g.color(x, y, z);
        }
    }

    public final int color(float x, float y, float z) {
        if (this.g == null) {
            if (x > 255.0F) {
                x = 255.0F;
            } else if (x < 0.0F) {
                x = 0.0F;
            }

            if (y > 255.0F) {
                y = 255.0F;
            } else if (y < 0.0F) {
                y = 0.0F;
            }

            if (z > 255.0F) {
                z = 255.0F;
            } else if (z < 0.0F) {
                z = 0.0F;
            }

            return -16777216 | (int)x << 16 | (int)y << 8 | (int)z;
        } else {
            return this.g.color(x, y, z);
        }
    }

    public final int color(int x, int y, int z, int a) {
        if (this.g == null) {
            if (a > 255) {
                a = 255;
            } else if (a < 0) {
                a = 0;
            }

            if (x > 255) {
                x = 255;
            } else if (x < 0) {
                x = 0;
            }

            if (y > 255) {
                y = 255;
            } else if (y < 0) {
                y = 0;
            }

            if (z > 255) {
                z = 255;
            } else if (z < 0) {
                z = 0;
            }

            return a << 24 | x << 16 | y << 8 | z;
        } else {
            return this.g.color(x, y, z, a);
        }
    }

    public final int color(float x, float y, float z, float a) {
        if (this.g == null) {
            if (a > 255.0F) {
                a = 255.0F;
            } else if (a < 0.0F) {
                a = 0.0F;
            }

            if (x > 255.0F) {
                x = 255.0F;
            } else if (x < 0.0F) {
                x = 0.0F;
            }

            if (y > 255.0F) {
                y = 255.0F;
            } else if (y < 0.0F) {
                y = 0.0F;
            }

            if (z > 255.0F) {
                z = 255.0F;
            } else if (z < 0.0F) {
                z = 0.0F;
            }

            return (int)a << 24 | (int)x << 16 | (int)y << 8 | (int)z;
        } else {
            return this.g.color(x, y, z, a);
        }
    }

    public static int blendColor(int c1, int c2, int mode) {
        return PImage.blendColor(c1, c2, mode);
    }

    public static void main(String[] args) {
    }

    public void loadPixels() {
        this.g.loadPixels();
        this.pixels = this.g.pixels;
    }

    public void updatePixels() {
        this.g.updatePixels();
    }

    public void updatePixels(int x1, int y1, int x2, int y2) {
        this.g.updatePixels(x1, y1, x2, y2);
    }

    public void setCache(PImage image, Object storage) {
        this.g.setCache(image, storage);
    }

    public Object getCache(PImage image) {
        return this.g.getCache(image);
    }

    public void removeCache(PImage image) {
        this.g.removeCache(image);
    }

    public void flush() {
        this.g.flush();
    }

    public PGL beginPGL() {
        return this.g.beginPGL();
    }

    public void endPGL() {
        this.g.endPGL();
    }

    public void hint(int which) {
        this.g.hint(which);
    }

    public void beginShape() {
        this.g.beginShape();
    }

    public void beginShape(int kind) {
        this.g.beginShape(kind);
    }

    public void edge(boolean edge) {
        this.g.edge(edge);
    }

    public void normal(float nx, float ny, float nz) {
        this.g.normal(nx, ny, nz);
    }

    public void attribPosition(String name, float x, float y, float z) {
        this.g.attribPosition(name, x, y, z);
    }

    public void attribNormal(String name, float nx, float ny, float nz) {
        this.g.attribNormal(name, nx, ny, nz);
    }

    public void attribColor(String name, int color) {
        this.g.attribColor(name, color);
    }

    public void attrib(String name, float... values) {
        this.g.attrib(name, values);
    }

    public void attrib(String name, int... values) {
        this.g.attrib(name, values);
    }

    public void attrib(String name, boolean... values) {
        this.g.attrib(name, values);
    }

    public void textureMode(int mode) {
        this.g.textureMode(mode);
    }

    public void textureWrap(int wrap) {
        this.g.textureWrap(wrap);
    }

    public void texture(PImage image) {
        this.g.texture(image);
    }

    public void noTexture() {
        this.g.noTexture();
    }

    public void vertex(float x, float y) {
        this.g.vertex(x, y);
    }

    public void vertex(float x, float y, float z) {
        this.g.vertex(x, y, z);
    }

    public void vertex(float[] v) {
        this.g.vertex(v);
    }

    public void vertex(float x, float y, float u, float v) {
        this.g.vertex(x, y, u, v);
    }

    public void vertex(float x, float y, float z, float u, float v) {
        this.g.vertex(x, y, z, u, v);
    }

    public void breakShape() {
        this.g.breakShape();
    }

    public void beginContour() {
        this.g.beginContour();
    }

    public void endContour() {
        this.g.endContour();
    }

    public void endShape() {
        this.g.endShape();
    }

    public void endShape(int mode) {
        this.g.endShape(mode);
    }

    public void clip(float a, float b, float c, float d) {
        this.g.clip(a, b, c, d);
    }

    public void noClip() {
        this.g.noClip();
    }

    public void blendMode(int mode) {
        this.g.blendMode(mode);
    }

    public PShape loadShape(String filename) {
        return this.g.loadShape(filename);
    }

    public PShape createShape() {
        return this.g.createShape();
    }

    public PShape createShape(int type) {
        return this.g.createShape(type);
    }

    public PShape createShape(int kind, float... p) {
        return this.g.createShape(kind, p);
    }

    public PShader loadShader(String fragFilename) {
        return this.g.loadShader(fragFilename);
    }

    public PShader loadShader(String fragFilename, String vertFilename) {
        return this.g.loadShader(fragFilename, vertFilename);
    }

    public void shader(PShader shader) {
        this.g.shader(shader);
    }

    public void shader(PShader shader, int kind) {
        this.g.shader(shader, kind);
    }

    public void resetShader() {
        this.g.resetShader();
    }

    public void resetShader(int kind) {
        this.g.resetShader(kind);
    }

    public PShader getShader(int kind) {
        return this.g.getShader(kind);
    }

    public void filter(PShader shader) {
        this.g.filter(shader);
    }

    public void bezierVertex(float x2, float y2, float x3, float y3, float x4, float y4) {
        this.g.bezierVertex(x2, y2, x3, y3, x4, y4);
    }

    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.g.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    public void quadraticVertex(float cx, float cy, float x3, float y3) {
        this.g.quadraticVertex(cx, cy, x3, y3);
    }

    public void quadraticVertex(float cx, float cy, float cz, float x3, float y3, float z3) {
        this.g.quadraticVertex(cx, cy, cz, x3, y3, z3);
    }

    public void curveVertex(float x, float y) {
        this.g.curveVertex(x, y);
    }

    public void curveVertex(float x, float y, float z) {
        this.g.curveVertex(x, y, z);
    }

    public void point(float x, float y) {
        this.g.point(x, y);
    }

    public void point(float x, float y, float z) {
        this.g.point(x, y, z);
    }

    public void line(float x1, float y1, float x2, float y2) {
        this.g.line(x1, y1, x2, y2);
    }

    public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.g.line(x1, y1, z1, x2, y2, z2);
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.g.triangle(x1, y1, x2, y2, x3, y3);
    }

    public void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.g.quad(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void rectMode(int mode) {
        this.g.rectMode(mode);
    }

    public void rect(float a, float b, float c, float d) {
        this.g.rect(a, b, c, d);
    }

    public void rect(float a, float b, float c, float d, float r) {
        this.g.rect(a, b, c, d, r);
    }

    public void rect(float a, float b, float c, float d, float tl, float tr, float br, float bl) {
        this.g.rect(a, b, c, d, tl, tr, br, bl);
    }

    public void square(float x, float y, float extent) {
        this.g.square(x, y, extent);
    }

    public void ellipseMode(int mode) {
        this.g.ellipseMode(mode);
    }

    public void ellipse(float a, float b, float c, float d) {
        this.g.ellipse(a, b, c, d);
    }

    public void arc(float a, float b, float c, float d, float start, float stop) {
        this.g.arc(a, b, c, d, start, stop);
    }

    public void arc(float a, float b, float c, float d, float start, float stop, int mode) {
        this.g.arc(a, b, c, d, start, stop, mode);
    }

    public void circle(float x, float y, float extent) {
        this.g.circle(x, y, extent);
    }

    public void box(float size) {
        this.g.box(size);
    }

    public void box(float w, float h, float d) {
        this.g.box(w, h, d);
    }

    public void sphereDetail(int res) {
        this.g.sphereDetail(res);
    }

    public void sphereDetail(int ures, int vres) {
        this.g.sphereDetail(ures, vres);
    }

    public void sphere(float r) {
        this.g.sphere(r);
    }

    public float bezierPoint(float a, float b, float c, float d, float t) {
        return this.g.bezierPoint(a, b, c, d, t);
    }

    public float bezierTangent(float a, float b, float c, float d, float t) {
        return this.g.bezierTangent(a, b, c, d, t);
    }

    public void bezierDetail(int detail) {
        this.g.bezierDetail(detail);
    }

    public void bezier(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.g.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void bezier(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.g.bezier(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    public float curvePoint(float a, float b, float c, float d, float t) {
        return this.g.curvePoint(a, b, c, d, t);
    }

    public float curveTangent(float a, float b, float c, float d, float t) {
        return this.g.curveTangent(a, b, c, d, t);
    }

    public void curveDetail(int detail) {
        this.g.curveDetail(detail);
    }

    public void curveTightness(float tightness) {
        this.g.curveTightness(tightness);
    }

    public void curve(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.g.curve(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void curve(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.g.curve(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    public void imageMode(int mode) {
        this.g.imageMode(mode);
    }

    public void image(PImage image, float x, float y) {
        this.g.image(image, x, y);
    }

    public void image(PImage image, float x, float y, float c, float d) {
        this.g.image(image, x, y, c, d);
    }

    public void image(PImage image, float a, float b, float c, float d, int u1, int v1, int u2, int v2) {
        this.g.image(image, a, b, c, d, u1, v1, u2, v2);
    }

    public void shapeMode(int mode) {
        this.g.shapeMode(mode);
    }

    public void shape(PShape shape) {
        this.g.shape(shape);
    }

    public void shape(PShape shape, float x, float y) {
        this.g.shape(shape, x, y);
    }

    public void shape(PShape shape, float x, float y, float c, float d) {
        this.g.shape(shape, x, y, c, d);
    }

    public void textAlign(int align) {
        this.g.textAlign(align);
    }

    public void textAlign(int alignX, int alignY) {
        this.g.textAlign(alignX, alignY);
    }

    public float textAscent() {
        return this.g.textAscent();
    }

    public float textDescent() {
        return this.g.textDescent();
    }

    public void textFont(PFont which) {
        this.g.textFont(which);
    }

    public void textFont(PFont which, float size) {
        this.g.textFont(which, size);
    }

    public void textLeading(float leading) {
        this.g.textLeading(leading);
    }

    public void textMode(int mode) {
        this.g.textMode(mode);
    }

    public void textSize(float size) {
        this.g.textSize(size);
    }

    public float textWidth(char c) {
        return this.g.textWidth(c);
    }

    public float textWidth(String str) {
        return this.g.textWidth(str);
    }

    public void text(char c, float x, float y) {
        this.g.text(c, x, y);
    }

    public void text(char c, float x, float y, float z) {
        this.g.text(c, x, y, z);
    }

    public void text(String str, float x, float y) {
        this.g.text(str, x, y);
    }

    public void text(String str, float x, float y, float z) {
        this.g.text(str, x, y, z);
    }

    public void text(String str, float x1, float y1, float x2, float y2) {
        this.g.text(str, x1, y1, x2, y2);
    }

    public void text(int num, float x, float y) {
        this.g.text(num, x, y);
    }

    public void text(int num, float x, float y, float z) {
        this.g.text(num, x, y, z);
    }

    public void text(float num, float x, float y) {
        this.g.text(num, x, y);
    }

    public void text(float num, float x, float y, float z) {
        this.g.text(num, x, y, z);
    }

    public void push() {
        this.g.push();
    }

    public void pop() {
        this.g.pop();
    }

    public void pushMatrix() {
        this.g.pushMatrix();
    }

    public void popMatrix() {
        this.g.popMatrix();
    }

    public void translate(float tx, float ty) {
        this.g.translate(tx, ty);
    }

    public void translate(float tx, float ty, float tz) {
        this.g.translate(tx, ty, tz);
    }

    public void rotate(float angle) {
        this.g.rotate(angle);
    }

    public void rotateX(float angle) {
        this.g.rotateX(angle);
    }

    public void rotateY(float angle) {
        this.g.rotateY(angle);
    }

    public void rotateZ(float angle) {
        this.g.rotateZ(angle);
    }

    public void rotate(float angle, float vx, float vy, float vz) {
        this.g.rotate(angle, vx, vy, vz);
    }

    public void scale(float s) {
        this.g.scale(s);
    }

    public void scale(float sx, float sy) {
        this.g.scale(sx, sy);
    }

    public void scale(float x, float y, float z) {
        this.g.scale(x, y, z);
    }

    public void shearX(float angle) {
        this.g.shearX(angle);
    }

    public void shearY(float angle) {
        this.g.shearY(angle);
    }

    public void resetMatrix() {
        this.g.resetMatrix();
    }

    public void applyMatrix(PMatrix source) {
        this.g.applyMatrix(source);
    }

    public void applyMatrix(PMatrix2D source) {
        this.g.applyMatrix(source);
    }

    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.g.applyMatrix(n00, n01, n02, n10, n11, n12);
    }

    public void applyMatrix(PMatrix3D source) {
        this.g.applyMatrix(source);
    }

    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.g.applyMatrix(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
    }

    public PMatrix getMatrix() {
        return this.g.getMatrix();
    }

    public PMatrix2D getMatrix(PMatrix2D target) {
        return this.g.getMatrix(target);
    }

    public PMatrix3D getMatrix(PMatrix3D target) {
        return this.g.getMatrix(target);
    }

    public PMatrix3D getObjectMatrix() {
        return this.g.getObjectMatrix();
    }

    public PMatrix3D getObjectMatrix(PMatrix3D target) {
        return this.g.getObjectMatrix(target);
    }

    public PMatrix3D getEyeMatrix() {
        return this.g.getEyeMatrix();
    }

    public PMatrix3D getEyeMatrix(PMatrix3D target) {
        return this.g.getEyeMatrix(target);
    }

    public void setMatrix(PMatrix source) {
        this.g.setMatrix(source);
    }

    public void setMatrix(PMatrix2D source) {
        this.g.setMatrix(source);
    }

    public void setMatrix(PMatrix3D source) {
        this.g.setMatrix(source);
    }

    public void printMatrix() {
        this.g.printMatrix();
    }

    public void cameraUp() {
        this.g.cameraUp();
    }

    public void beginCamera() {
        this.g.beginCamera();
    }

    public void endCamera() {
        this.g.endCamera();
    }

    public void camera() {
        this.g.camera();
    }

    public void camera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        this.g.camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void printCamera() {
        this.g.printCamera();
    }

    public void eye() {
        this.g.eye();
    }

    public void ortho() {
        this.g.ortho();
    }

    public void ortho(float left, float right, float bottom, float top) {
        this.g.ortho(left, right, bottom, top);
    }

    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        this.g.ortho(left, right, bottom, top, near, far);
    }

    public void perspective() {
        this.g.perspective();
    }

    public void perspective(float fovy, float aspect, float zNear, float zFar) {
        this.g.perspective(fovy, aspect, zNear, zFar);
    }

    public void frustum(float left, float right, float bottom, float top, float near, float far) {
        this.g.frustum(left, right, bottom, top, near, far);
    }

    public void printProjection() {
        this.g.printProjection();
    }

    public float screenX(float x, float y) {
        return this.g.screenX(x, y);
    }

    public float screenY(float x, float y) {
        return this.g.screenY(x, y);
    }

    public float screenX(float x, float y, float z) {
        return this.g.screenX(x, y, z);
    }

    public float screenY(float x, float y, float z) {
        return this.g.screenY(x, y, z);
    }

    public float screenZ(float x, float y, float z) {
        return this.g.screenZ(x, y, z);
    }

    public float modelX(float x, float y, float z) {
        return this.g.modelX(x, y, z);
    }

    public float modelY(float x, float y, float z) {
        return this.g.modelY(x, y, z);
    }

    public float modelZ(float x, float y, float z) {
        return this.g.modelZ(x, y, z);
    }

    public void pushStyle() {
        this.g.pushStyle();
    }

    public void popStyle() {
        this.g.popStyle();
    }

    public void style(PStyle s) {
        this.g.style(s);
    }

    public void strokeWeight(float weight) {
        this.g.strokeWeight(weight);
    }

    public void strokeJoin(int join) {
        this.g.strokeJoin(join);
    }

    public void strokeCap(int cap) {
        this.g.strokeCap(cap);
    }

    public void noStroke() {
        this.g.noStroke();
    }

    public void stroke(int rgb) {
        this.g.stroke(rgb);
    }

    public void stroke(int rgb, float alpha) {
        this.g.stroke(rgb, alpha);
    }

    public void stroke(float gray) {
        this.g.stroke(gray);
    }

    public void stroke(float gray, float alpha) {
        this.g.stroke(gray, alpha);
    }

    public void stroke(float x, float y, float z) {
        this.g.stroke(x, y, z);
    }

    public void stroke(float x, float y, float z, float a) {
        this.g.stroke(x, y, z, a);
    }

    public void noTint() {
        this.g.noTint();
    }

    public void tint(int rgb) {
        this.g.tint(rgb);
    }

    public void tint(int rgb, float alpha) {
        this.g.tint(rgb, alpha);
    }

    public void tint(float gray) {
        this.g.tint(gray);
    }

    public void tint(float gray, float alpha) {
        this.g.tint(gray, alpha);
    }

    public void tint(float x, float y, float z) {
        this.g.tint(x, y, z);
    }

    public void tint(float x, float y, float z, float a) {
        this.g.tint(x, y, z, a);
    }

    public void noFill() {
        this.g.noFill();
    }

    public void fill(int rgb) {
        this.g.fill(rgb);
    }

    public void fill(int rgb, float alpha) {
        this.g.fill(rgb, alpha);
    }

    public void fill(float gray) {
        this.g.fill(gray);
    }

    public void fill(float gray, float alpha) {
        this.g.fill(gray, alpha);
    }

    public void fill(float x, float y, float z) {
        this.g.fill(x, y, z);
    }

    public void fill(float x, float y, float z, float a) {
        this.g.fill(x, y, z, a);
    }

    public void ambient(int rgb) {
        this.g.ambient(rgb);
    }

    public void ambient(float gray) {
        this.g.ambient(gray);
    }

    public void ambient(float x, float y, float z) {
        this.g.ambient(x, y, z);
    }

    public void specular(int rgb) {
        this.g.specular(rgb);
    }

    public void specular(float gray) {
        this.g.specular(gray);
    }

    public void specular(float x, float y, float z) {
        this.g.specular(x, y, z);
    }

    public void shininess(float shine) {
        this.g.shininess(shine);
    }

    public void emissive(int rgb) {
        this.g.emissive(rgb);
    }

    public void emissive(float gray) {
        this.g.emissive(gray);
    }

    public void emissive(float x, float y, float z) {
        this.g.emissive(x, y, z);
    }

    public void lights() {
        this.g.lights();
    }

    public void noLights() {
        this.g.noLights();
    }

    public void ambientLight(float red, float green, float blue) {
        this.g.ambientLight(red, green, blue);
    }

    public void ambientLight(float red, float green, float blue, float x, float y, float z) {
        this.g.ambientLight(red, green, blue, x, y, z);
    }

    public void directionalLight(float red, float green, float blue, float nx, float ny, float nz) {
        this.g.directionalLight(red, green, blue, nx, ny, nz);
    }

    public void pointLight(float red, float green, float blue, float x, float y, float z) {
        this.g.pointLight(red, green, blue, x, y, z);
    }

    public void spotLight(float red, float green, float blue, float x, float y, float z, float nx, float ny, float nz, float angle, float concentration) {
        this.g.spotLight(red, green, blue, x, y, z, nx, ny, nz, angle, concentration);
    }

    public void lightFalloff(float constant, float linear, float quadratic) {
        this.g.lightFalloff(constant, linear, quadratic);
    }

    public void lightSpecular(float x, float y, float z) {
        this.g.lightSpecular(x, y, z);
    }

    public void background(int rgb) {
        this.g.background(rgb);
    }

    public void background(int rgb, float alpha) {
        this.g.background(rgb, alpha);
    }

    public void background(float gray) {
        this.g.background(gray);
    }

    public void background(float gray, float alpha) {
        this.g.background(gray, alpha);
    }

    public void background(float x, float y, float z) {
        this.g.background(x, y, z);
    }

    public void background(float x, float y, float z, float a) {
        this.g.background(x, y, z, a);
    }

    public void clear() {
        this.g.clear();
    }

    public void background(PImage image) {
        this.g.background(image);
    }

    public void colorMode(int mode) {
        this.g.colorMode(mode);
    }

    public void colorMode(int mode, float max) {
        this.g.colorMode(mode, max);
    }

    public void colorMode(int mode, float maxX, float maxY, float maxZ) {
        this.g.colorMode(mode, maxX, maxY, maxZ);
    }

    public void colorMode(int mode, float maxX, float maxY, float maxZ, float maxA) {
        this.g.colorMode(mode, maxX, maxY, maxZ, maxA);
    }

    public final float alpha(int what) {
        return this.g.alpha(what);
    }

    public final float red(int what) {
        return this.g.red(what);
    }

    public final float green(int what) {
        return this.g.green(what);
    }

    public final float blue(int what) {
        return this.g.blue(what);
    }

    public final float hue(int what) {
        return this.g.hue(what);
    }

    public final float saturation(int what) {
        return this.g.saturation(what);
    }

    public final float brightness(int what) {
        return this.g.brightness(what);
    }

    public int lerpColor(int c1, int c2, float amt) {
        return this.g.lerpColor(c1, c2, amt);
    }

    public static int lerpColor(int c1, int c2, float amt, int mode) {
        return PGraphics.lerpColor(c1, c2, amt, mode);
    }

    public static void showDepthWarning(String method) {
        PGraphics.showDepthWarning(method);
    }

    public static void showDepthWarningXYZ(String method) {
        PGraphics.showDepthWarningXYZ(method);
    }

    public static void showMethodWarning(String method) {
        PGraphics.showMethodWarning(method);
    }

    public static void showVariationWarning(String str) {
        PGraphics.showVariationWarning(str);
    }

    public static void showMissingWarning(String method) {
        PGraphics.showMissingWarning(method);
    }

    public boolean displayable() {
        return this.g.displayable();
    }

    public boolean isGL() {
        return this.g.isGL();
    }

    public Object getNative() {
        return this.g.getNative();
    }

    public void setNative(Object nativeObject) {
        this.g.setNative(nativeObject);
    }

    public int get(int x, int y) {
        return this.g.get(x, y);
    }

    public PImage get(int x, int y, int w, int h) {
        return this.g.get(x, y, w, h);
    }

    public PImage get() {
        return this.g.get();
    }

    public void set(int x, int y, int c) {
        this.g.set(x, y, c);
    }

    public void set(int x, int y, PImage img) {
        this.g.set(x, y, img);
    }

    public void mask(int[] alpha) {
        this.g.mask(alpha);
    }

    public void mask(PImage alpha) {
        this.g.mask(alpha);
    }

    public void filter(int kind) {
        this.g.filter(kind);
    }

    public void filter(int kind, float param) {
        this.g.filter(kind, param);
    }

    public void copy(int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        this.g.copy(sx, sy, sw, sh, dx, dy, dw, dh);
    }

    public void copy(PImage src, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        this.g.copy(src, sx, sy, sw, sh, dx, dy, dw, dh);
    }

    public void blend(int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh, int mode) {
        this.g.blend(sx, sy, sw, sh, dx, dy, dw, dh, mode);
    }

    public void blend(PImage src, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh, int mode) {
        this.g.blend(src, sx, sy, sw, sh, dx, dy, dw, dh, mode);
    }

    static {
        SDK = VERSION.SDK_INT;
        ICON_IMAGE = new byte[]{71, 73, 70, 56, 57, 97, 16, 0, 16, 0, -77, 0, 0, 0, 0, 0, -1, -1, -1, 12, 12, 13, -15, -15, -14, 45, 57, 74, 54, 80, 111, 47, 71, 97, 62, 88, 117, 1, 14, 27, 7, 41, 73, 15, 52, 85, 2, 31, 55, 4, 54, 94, 18, 69, 109, 37, 87, 126, -1, -1, -1, 33, -7, 4, 1, 0, 0, 15, 0, 44, 0, 0, 0, 0, 16, 0, 16, 0, 0, 4, 122, -16, -107, 114, -86, -67, 83, 30, -42, 26, -17, -100, -45, 56, -57, -108, 48, 40, 122, -90, 104, 67, -91, -51, 32, -53, 77, -78, -100, 47, -86, 12, 76, -110, -20, -74, -101, 97, -93, 27, 40, 20, -65, 65, 48, -111, 99, -20, -112, -117, -123, -47, -105, 24, 114, -112, 74, 69, 84, 25, 93, 88, -75, 9, 46, 2, 49, 88, -116, -67, 7, -19, -83, 60, 38, 3, -34, 2, 66, -95, 27, -98, 13, 4, -17, 55, 33, 109, 11, 11, -2, -128, 121, 123, 62, 91, 120, -128, 127, 122, 115, 102, 2, 119, 0, -116, -113, -119, 6, 102, 121, -108, -126, 5, 18, 6, 4, -102, -101, -100, 114, 15, 17, 0, 59};
    }

    class AsyncImageLoader extends Thread {
        String filename;
        PImage vessel;

        public AsyncImageLoader(String filename, PImage vessel) {
            this.filename = filename;
            this.vessel = vessel;
        }

        public void run() {
            while(PApplet.this.requestImageCount == PApplet.this.requestImageMax) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException var2) {
                }
            }

            ++PApplet.this.requestImageCount;
            PImage actual = PApplet.this.loadImage(this.filename);
            if (actual == null) {
                this.vessel.width = -1;
                this.vessel.height = -1;
            } else {
                this.vessel.width = actual.width;
                this.vessel.height = actual.height;
                this.vessel.format = actual.format;
                this.vessel.pixels = actual.pixels;
                this.vessel.bitmap = actual.bitmap;
                this.vessel.pixelWidth = actual.width;
                this.vessel.pixelHeight = actual.height;
                this.vessel.pixelDensity = 1;
            }

            --PApplet.this.requestImageCount;
        }
    }

    class InternalEventQueue {
        protected Event[] queue = new Event[10];
        protected int offset;
        protected int count;

        InternalEventQueue() {
        }

        synchronized void add(Event e) {
            if (this.count == this.queue.length) {
                this.queue = (Event[])((Event[])PApplet.expand((Object)this.queue));
            }

            this.queue[this.count++] = e;
        }

        synchronized Event remove() {
            if (this.offset == this.count) {
                throw new RuntimeException("Nothing left on the event queue.");
            } else {
                Event outgoing = this.queue[this.offset++];
                if (this.offset == this.count) {
                    this.offset = 0;
                    this.count = 0;
                }

                return outgoing;
            }
        }

        synchronized boolean available() {
            return this.count != 0;
        }
    }

    class RegisteredMethods {
        int count;
        Object[] objects;
        Method[] methods;
        Object[] emptyArgs = new Object[0];

        RegisteredMethods() {
        }

        void handle() {
            this.handle(this.emptyArgs);
        }

        void handle(Object[] args) {
            for(int i = 0; i < this.count; ++i) {
                try {
                    this.methods[i].invoke(this.objects[i], args);
                } catch (Exception var6) {
                    Object t;
                    if (var6 instanceof InvocationTargetException) {
                        InvocationTargetException ite = (InvocationTargetException)var6;
                        t = ite.getCause();
                    } else {
                        t = var6;
                    }

                    if (t instanceof RuntimeException) {
                        throw (RuntimeException)t;
                    }

                    ((Throwable)t).printStackTrace();
                }
            }

        }

        void add(Object object, Method method) {
            if (this.findIndex(object) == -1) {
                if (this.objects == null) {
                    this.objects = new Object[5];
                    this.methods = new Method[5];
                } else if (this.count == this.objects.length) {
                    this.objects = (Object[])((Object[])PApplet.expand((Object)this.objects));
                    this.methods = (Method[])((Method[])PApplet.expand((Object)this.methods));
                }

                this.objects[this.count] = object;
                this.methods[this.count] = method;
                ++this.count;
            } else {
                PApplet.this.die(method.getName() + "() already added for this instance of " + object.getClass().getName());
            }

        }

        public void remove(Object object) {
            int index = this.findIndex(object);
            if (index != -1) {
                --this.count;

                for(int i = index; i < this.count; ++i) {
                    this.objects[i] = this.objects[i + 1];
                    this.methods[i] = this.methods[i + 1];
                }

                this.objects[this.count] = null;
                this.methods[this.count] = null;
            }

        }

        protected int findIndex(Object object) {
            for(int i = 0; i < this.count; ++i) {
                if (this.objects[i] == object) {
                    return i;
                }
            }

            return -1;
        }
    }
}

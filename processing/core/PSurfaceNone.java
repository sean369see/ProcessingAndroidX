package processing.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.support.v4.os.ResultReceiver;
import android.support.wearable.watchface.WatchFaceService;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import processing.android.AppComponent;
import processing.android.PFragment;
import processing.android.PermissionRequestor;
import processing.android.ServiceEngine;

public class PSurfaceNone implements PSurface, PConstants {
    protected PApplet sketch;
    protected PGraphics graphics;
    protected AppComponent component;
    protected Activity activity;
    protected boolean surfaceReady;
    protected SurfaceView surfaceView;
    protected View view;
    protected WallpaperService wallpaper;
    protected WatchFaceService watchface;
    protected boolean requestedThreadStart = false;
    protected Thread thread;
    protected boolean paused;
    protected Object pauseObject = new Object();
    protected float frameRateTarget = 60.0F;
    protected long frameRatePeriod = 16666666L;

    public PSurfaceNone() {
    }

    public AppComponent getComponent() {
        return this.component;
    }

    public Context getContext() {
        if (this.component.getKind() == 0) {
            return this.activity;
        } else if (this.component.getKind() == 1) {
            return this.wallpaper;
        } else {
            return this.component.getKind() == 2 ? this.watchface : null;
        }
    }

    public Activity getActivity() {
        return this.activity;
    }

    public ServiceEngine getEngine() {
        return this.component.getEngine();
    }

    public View getRootView() {
        return this.view;
    }

    public String getName() {
        if (this.component.getKind() == 0) {
            return this.activity.getComponentName().getPackageName();
        } else if (this.component.getKind() == 1) {
            return this.wallpaper.getPackageName();
        } else {
            return this.component.getKind() == 2 ? this.watchface.getPackageName() : "";
        }
    }

    public View getResource(int id) {
        return this.activity.findViewById(id);
    }

    public Rect getVisibleFrame() {
        Rect frame = new Rect();
        if (this.view != null) {
            this.view.getWindowVisibleDisplayFrame(frame);
        }

        return frame;
    }

    public void dispose() {
        this.sketch = null;
        this.graphics = null;
        if (this.activity != null) {
        }

        if (this.view != null) {
            this.view.destroyDrawingCache();
        }

        if (this.component != null) {
            this.component.dispose();
        }

        if (this.surfaceView != null) {
            this.surfaceView.getHolder().getSurface().release();
        }

    }

    public void setRootView(View view) {
        this.view = view;
    }

    public SurfaceView getSurfaceView() {
        return this.surfaceView;
    }

    public SurfaceHolder getSurfaceHolder() {
        SurfaceView view = this.getSurfaceView();
        return view == null ? null : view.getHolder();
    }

    public void initView(int sketchWidth, int sketchHeight) {
        if (this.component.getKind() == 0) {
            int displayWidth = this.component.getDisplayWidth();
            int displayHeight = this.component.getDisplayHeight();
            Object rootView;
            if (sketchWidth == displayWidth && sketchHeight == displayHeight) {
                rootView = this.getSurfaceView();
            } else {
                RelativeLayout overallLayout = new RelativeLayout(this.activity);
                LayoutParams lp = new LayoutParams(-2, -2);
                lp.addRule(13);
                LinearLayout layout = new LinearLayout(this.activity);
                layout.addView(this.getSurfaceView(), sketchWidth, sketchHeight);
                overallLayout.addView(layout, lp);
                overallLayout.setBackgroundColor(this.sketch.sketchWindowColor());
                rootView = overallLayout;
            }

            this.setRootView((View)rootView);
        } else if (this.component.getKind() == 1) {
            this.setRootView(this.getSurfaceView());
        }

    }

    public void initView(int sketchWidth, int sketchHeight, boolean parentSize, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(this.sketch.parentLayout, container, false);
        View view = this.getSurfaceView();
        if (parentSize) {
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(-1, -1);
            lp.weight = 1.0F;
            lp.setMargins(0, 0, 0, 0);
            view.setPadding(0, 0, 0, 0);
            rootView.addView(view, lp);
        } else {
            RelativeLayout layout = new RelativeLayout(this.activity);
            LayoutParams lp = new LayoutParams(-2, -2);
            lp.addRule(13);
            layout.addView(view, sketchWidth, sketchHeight);
            rootView.addView(layout, lp);
        }

        rootView.setBackgroundColor(this.sketch.sketchWindowColor());
        this.setRootView(rootView);
    }

    public void startActivity(Intent intent) {
        this.component.startActivity(intent);
    }

    public void runOnUiThread(Runnable action) {
        if (this.component.getKind() == 0) {
            this.activity.runOnUiThread(action);
        }

    }

    public void setOrientation(int which) {
        if (this.component.getKind() == 0) {
            if (which == 1) {
                this.activity.setRequestedOrientation(1);
            } else if (which == 2) {
                this.activity.setRequestedOrientation(0);
            }
        }

    }

    public void setHasOptionsMenu(boolean hasMenu) {
        if (this.component.getKind() == 0) {
            ((PFragment)this.component).setHasOptionsMenu(hasMenu);
        }

    }

    public File getFilesDir() {
        if (this.component.getKind() == 0) {
            return this.activity.getFilesDir();
        } else if (this.component.getKind() == 1) {
            return this.wallpaper.getFilesDir();
        } else {
            return this.component.getKind() == 2 ? this.watchface.getFilesDir() : null;
        }
    }

    public File getFileStreamPath(String path) {
        if (this.component.getKind() == 0) {
            return this.activity.getFileStreamPath(path);
        } else if (this.component.getKind() == 1) {
            return this.wallpaper.getFileStreamPath(path);
        } else {
            return this.component.getKind() == 2 ? this.watchface.getFileStreamPath(path) : null;
        }
    }

    public InputStream openFileInput(String filename) {
        if (this.component.getKind() == 0) {
            try {
                return this.activity.openFileInput(filename);
            } catch (FileNotFoundException var3) {
                var3.printStackTrace();
            }
        }

        return null;
    }

    public AssetManager getAssets() {
        if (this.component.getKind() == 0) {
            return this.activity.getAssets();
        } else if (this.component.getKind() == 1) {
            return this.wallpaper.getBaseContext().getAssets();
        } else {
            return this.component.getKind() == 2 ? this.watchface.getBaseContext().getAssets() : null;
        }
    }

    public void setSystemUiVisibility(int visibility) {
        int kind = this.component.getKind();
        if (kind == 0 || kind == 1) {
            this.surfaceView.setSystemUiVisibility(visibility);
        }

    }

    public void finish() {
        if (this.component != null) {
            if (this.component.getKind() == 0) {
                this.activity.finish();
            } else if (this.component.getKind() == 1) {
                this.wallpaper.stopSelf();
            } else if (this.component.getKind() == 2) {
                this.watchface.stopSelf();
            }

        }
    }

    public Thread createThread() {
        return new PSurfaceNone.AnimationThread();
    }

    public void startThread() {
        if (!this.surfaceReady) {
            this.requestedThreadStart = true;
        } else if (this.thread == null) {
            this.thread = this.createThread();
            this.thread.start();
            this.requestedThreadStart = false;
        } else {
            throw new IllegalStateException("Thread already started in " + this.getClass().getSimpleName());
        }
    }

    public void pauseThread() {
        if (this.surfaceReady) {
            this.paused = true;
        }
    }

    public void resumeThread() {
        if (this.surfaceReady) {
            if (this.thread == null) {
                this.thread = this.createThread();
                this.thread.start();
            }

            this.paused = false;
            synchronized(this.pauseObject) {
                this.pauseObject.notifyAll();
            }
        }
    }

    public boolean stopThread() {
        if (!this.surfaceReady) {
            return true;
        } else if (this.thread == null) {
            return false;
        } else {
            this.thread.interrupt();
            this.thread = null;
            return true;
        }
    }

    public boolean isStopped() {
        return this.thread == null;
    }

    public void setFrameRate(float fps) {
        this.frameRateTarget = fps;
        this.frameRatePeriod = (long)(1.0E9D / (double)this.frameRateTarget);
    }

    protected void checkPause() throws InterruptedException {
        synchronized(this.pauseObject) {
            while(this.paused) {
                this.pauseObject.wait();
            }

        }
    }

    protected void callDraw() {
        this.component.requestDraw();
        if (this.component.canDraw() && this.sketch != null) {
            this.sketch.handleDraw();
        }

    }

    public boolean hasPermission(String permission) {
        int res = ContextCompat.checkSelfPermission(this.getContext(), permission);
        return res == 0;
    }

    public void requestPermissions(String[] permissions) {
        if (this.component.isService()) {
            final ServiceEngine eng = this.getEngine();
            if (eng != null) {
                ResultReceiver resultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) {
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        String[] outPermissions = resultData.getStringArray("permissions");
                        int[] grantResults = resultData.getIntArray("grantResults");
                        eng.onRequestPermissionsResult(resultCode, outPermissions, grantResults);
                    }
                };
                Intent permIntent = new Intent(this.getContext(), PermissionRequestor.class);
                permIntent.putExtra("resultReceiver", resultReceiver);
                permIntent.putExtra("permissions", permissions);
                permIntent.putExtra("requestCode", 1);
                permIntent.addFlags(268435456);
                this.startActivity(permIntent);
            }
        } else if (this.activity != null) {
            ActivityCompat.requestPermissions(this.activity, permissions, 1);
        }

    }

    public class AnimationThread extends Thread {
        public AnimationThread() {
            super("Animation Thread");
        }

        public void run() {
            long beforeTime = System.nanoTime();
            long overSleepTime = 0L;
            int noDelays = 0;
            int NO_DELAYS_PER_YIELD = true;
            if (PSurfaceNone.this.sketch != null) {
                PSurfaceNone.this.sketch.start();

                for(; Thread.currentThread() == PSurfaceNone.this.thread && PSurfaceNone.this.sketch != null && !PSurfaceNone.this.sketch.finished; beforeTime = System.nanoTime()) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    try {
                        PSurfaceNone.this.checkPause();
                    } catch (InterruptedException var15) {
                        return;
                    }

                    PSurfaceNone.this.callDraw();
                    long afterTime = System.nanoTime();
                    long timeDiff = afterTime - beforeTime;
                    long sleepTime = PSurfaceNone.this.frameRatePeriod - timeDiff - overSleepTime;
                    if (sleepTime > 0L) {
                        try {
                            Thread.sleep(sleepTime / 1000000L, (int)(sleepTime % 1000000L));
                            noDelays = 0;
                        } catch (InterruptedException var14) {
                        }

                        overSleepTime = System.nanoTime() - afterTime - sleepTime;
                    } else {
                        overSleepTime = 0L;
                        ++noDelays;
                        if (noDelays > 15) {
                            Thread.yield();
                            noDelays = 0;
                        }
                    }
                }

            }
        }
    }
}

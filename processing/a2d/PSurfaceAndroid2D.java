package processing.a2d;

import android.content.Context;
import android.service.wallpaper.WallpaperService;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import processing.android.AppComponent;
import processing.android.PFragment;
import processing.core.PGraphics;
import processing.core.PSurfaceNone;

public class PSurfaceAndroid2D extends PSurfaceNone {
    public PSurfaceAndroid2D() {
    }

    public PSurfaceAndroid2D(PGraphics graphics, AppComponent component, SurfaceHolder holder) {
        this.sketch = graphics.parent;
        this.graphics = graphics;
        this.component = component;
        if (component.getKind() == 0) {
            PFragment frag = (PFragment)component;
            this.activity = frag.getActivity();
            this.surfaceView = new PSurfaceAndroid2D.SurfaceViewAndroid2D(this.activity, (SurfaceHolder)null);
        } else if (component.getKind() == 1) {
            this.wallpaper = (WallpaperService)component;
            this.surfaceView = new PSurfaceAndroid2D.SurfaceViewAndroid2D(this.wallpaper, holder);
        } else if (component.getKind() == 2) {
            this.watchface = (CanvasWatchFaceService)component;
            this.surfaceView = null;
            this.surfaceReady = true;
        }

    }

    public class SurfaceViewAndroid2D extends SurfaceView implements Callback {
        SurfaceHolder holder;

        public SurfaceViewAndroid2D(Context context, SurfaceHolder holder) {
            super(context);
            this.holder = holder;
            SurfaceHolder h = this.getHolder();
            h.addCallback(this);
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
            this.requestFocus();
            PSurfaceAndroid2D.this.surfaceReady = false;
        }

        public SurfaceHolder getHolder() {
            return this.holder == null ? super.getHolder() : this.holder;
        }

        public void surfaceCreated(SurfaceHolder holder) {
            PSurfaceAndroid2D.this.surfaceReady = true;
            if (PSurfaceAndroid2D.this.requestedThreadStart) {
                PSurfaceAndroid2D.this.startThread();
            }

        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int iwidth, int iheight) {
            PSurfaceAndroid2D.this.sketch.surfaceChanged();
            PSurfaceAndroid2D.this.sketch.setSize(iwidth, iheight);
        }

        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            PSurfaceAndroid2D.this.sketch.surfaceWindowFocusChanged(hasFocus);
        }

        public boolean onTouchEvent(MotionEvent event) {
            return PSurfaceAndroid2D.this.sketch.surfaceTouchEvent(event);
        }

        public boolean onKeyDown(int code, KeyEvent event) {
            PSurfaceAndroid2D.this.sketch.surfaceKeyDown(code, event);
            return super.onKeyDown(code, event);
        }

        public boolean onKeyUp(int code, KeyEvent event) {
            PSurfaceAndroid2D.this.sketch.surfaceKeyUp(code, event);
            return super.onKeyUp(code, event);
        }
    }
}

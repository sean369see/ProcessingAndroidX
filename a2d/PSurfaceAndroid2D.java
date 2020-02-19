/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2016 The Processing Foundation

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

/*
  AndroidX modification project by Xuan "Sean" Li
*/

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

// comparison import QQQ
import processing.core.PApplet;

import processing.core.PGraphics;
import processing.core.PSurfaceNone;

public class PSurfaceAndroid2D extends PSurfaceNone {

    public PSurfaceAndroid2D() { }

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
            // Set as ready here, as watch faces don't have a surface view with a
            // surfaceCreate() event to do it.
            this.surfaceReady = true;
        }

    }


    // SurfaceView

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

            // Comparison qqq
            // Solves screen flickering:
            // https://github.com/processing/processing-android/issues/570
            setBackgroundColor(Color.argb(0, 0, 0, 0));
            getHolder().setFormat(PixelFormat.TRANSPARENT);
        }

        @Override
        public SurfaceHolder getHolder() {
            return this.holder == null ? super.getHolder() : this.holder;
        }

        // part of SurfaceHolder.Callback
        public void surfaceCreated(SurfaceHolder holder) {
            PSurfaceAndroid2D.this.surfaceReady = true;
            if (PSurfaceAndroid2D.this.requestedThreadStart) {
                // Only start the thread once the surface has been created, otherwise it will not be able to draw
                PSurfaceAndroid2D.this.startThread();
            }
            
            // qqq
            if (PApplet.DEBUG) {
                System.out.println("surfaceCreated()");
            }
        }

        // part of SurfaceHolder.Callback
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (PApplet.DEBUG) {
                System.out.println("surfaceDestroyed()");
            }        
        }

        // part of SurfaceHolder.Callback
        public void surfaceChanged(SurfaceHolder holder, int format, int iwidth, int iheight) {
            if (PApplet.DEBUG) {
                System.out.println("SketchSurfaceView.surfaceChanged() " + iwidth + " " + iheight);
            }            

            PSurfaceAndroid2D.this.sketch.surfaceChanged();
            PSurfaceAndroid2D.this.sketch.setSize(iwidth, iheight);
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            PSurfaceAndroid2D.this.sketch.surfaceWindowFocusChanged(hasFocus);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return PSurfaceAndroid2D.this.sketch.surfaceTouchEvent(event);
        }

        @Override
        public boolean onKeyDown(int code, KeyEvent event) {
            PSurfaceAndroid2D.this.sketch.surfaceKeyDown(code, event);
            return super.onKeyDown(code, event);
        }

        @Override
        public boolean onKeyUp(int code, KeyEvent event) {
            PSurfaceAndroid2D.this.sketch.surfaceKeyUp(code, event);
            return super.onKeyUp(code, event);
        }


    }
}

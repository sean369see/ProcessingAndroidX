/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2016-17 The Processing Foundation

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

package processing.android;


import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import processing.core.PApplet;

import android.util.DisplayMetrics;
import android.view.Display;
import android.graphics.Point;
import android.graphics.Rect;

public class PWallpaper extends WallpaperService implements AppComponent {
    private Point size;
    private DisplayMetrics metrics;
    private PWallpaper.WallpaperEngine engine;

    public PWallpaper() {
    }

    public void initDimensions() {
        this.metrics = new DisplayMetrics();
        this.size = new Point();
        WindowManager wm = (WindowManager)this.getSystemService("window");
        Display display = wm.getDefaultDisplay();
        CompatUtils.getDisplayParams(display, this.metrics, this.size);
    }

    public int getDisplayWidth() {
        return this.size.x;
    }

    public int getDisplayHeight() {
        return this.size.y;
    }

    public float getDisplayDensity() {
        return this.metrics.density;
    }

    public int getKind() {
        return WALLPAPER;        
        //return 1;
    }

    public PApplet createSketch() {
        return new PApplet();
    }

    public void setSketch(PApplet sketch) {
        this.engine.sketch = sketch;
    }

    public PApplet getSketch() {
        return this.engine.sketch;
    }

    public boolean isService() {
        return true;
    }

    public ServiceEngine getEngine() {
        return this.engine;
    }

    public void requestDraw() {
    }

    public boolean canDraw() {
        return true;
    }

    public void dispose() {
    }

    public void requestPermissions() {
    }

    @Override
    public Engine onCreateEngine() {
        this.engine = new PWallpaper.WallpaperEngine();
        return this.engine;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.engine != null) {
            this.engine.onDestroy();
        }

    }

    public class WallpaperEngine extends Engine implements ServiceEngine {
        PApplet sketch;
        private float xOffset;
        private float xOffsetStep;
        private float yOffset;
        private float yOffsetStep;
        private int xPixelOffset;
        private int yPixelOffset;

        public WallpaperEngine() {
            super(PWallpaper.this);
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.sketch = PWallpaper.this.createSketch();
            this.sketch.initSurface(PWallpaper.this, this.getSurfaceHolder());
            if (this.isPreview()) {
                PWallpaper.this.requestPermissions();
            }

            this.setTouchEventsEnabled(true);
        }

        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            // When the surface of a live wallpaper changes (eg: after a screen rotation) the same sketch
            // continues to run (unlike the case of regular apps, where its re-created) so we need to
            // force a reset of the renderer so the backing FBOs (in the case of the OpenGL renderers)
            // get reinitalized with the correct size.
            this.sketch.g.reset();
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (this.sketch != null) {
                if (visible) {
                    this.sketch.onResume();
                } else {
                    this.sketch.onPause();
                }
            }

            super.onVisibilityChanged(visible);
        }

        /*
         * Store the position of the touch event so we can use it for drawing
         * later
         */        
        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (this.sketch != null) {
                this.sketch.surfaceTouchEvent(event);
            }

        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, 
                                     float xOffsetStep, float yOffsetStep, 
                                     int xPixelOffset, int yPixelOffset) {
            
            if (this.sketch != null) {
                this.xOffset = xOffset;
                this.yOffset = yOffset;
                this.xOffsetStep = xOffsetStep;
                this.yOffsetStep = yOffsetStep;
                this.xPixelOffset = xPixelOffset;
                this.yPixelOffset = yPixelOffset;
            }

        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            // This is called immediately before a surface is being destroyed.
            // After returning from this call, you should no longer try to access this
            // surface. If you have a rendering thread that directly accesses the
            // surface, you must ensure that thread is no longer touching the Surface
            // before returning from this function.
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            if (this.sketch != null) {
                this.sketch.onDestroy();
            }

        }

        @Override
        public float getXOffset() {
            return this.xOffset;
        }

        @Override
        public float getYOffset() {
            return this.yOffset;
        }

        @Override
        public float getXOffsetStep() {
            return this.xOffsetStep;
        }

        @Override
        public float getYOffsetStep() {
            return this.yOffsetStep;
        }

        @Override
        public int getXPixelOffset() {
            return this.xPixelOffset;
        }

        @Override
        public int getYPixelOffset() {
            return this.yPixelOffset;
        }

        @Override
        public boolean isInAmbientMode() {
            return false;
        }

        @Override
        public boolean isRound() {
            return false;
        }

        @Override
        public Rect getInsets() {
            return null;
        }

        @Override
        public boolean useLowBitAmbient() {
            return false;
        }

        @Override
        public boolean requireBurnInProtection() {
            return false;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (this.sketch != null) {
                this.sketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

        }

        
    }
}


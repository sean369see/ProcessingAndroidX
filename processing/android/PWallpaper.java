package processing.android;

import android.graphics.Point;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import android.service.wallpaper.WallpaperService.Engine;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import processing.core.PApplet;

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
        return 1;
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

    public Engine onCreateEngine() {
        this.engine = new PWallpaper.WallpaperEngine();
        return this.engine;
    }

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
            this.sketch.g.reset();
            super.onSurfaceChanged(holder, format, width, height);
        }

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

        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            if (this.sketch != null) {
                this.sketch.surfaceTouchEvent(event);
            }

        }

        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            if (this.sketch != null) {
                this.xOffset = xOffset;
                this.yOffset = yOffset;
                this.xOffsetStep = xOffsetStep;
                this.yOffsetStep = yOffsetStep;
                this.xPixelOffset = xPixelOffset;
                this.yPixelOffset = yPixelOffset;
            }

        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        public void onDestroy() {
            super.onDestroy();
            if (this.sketch != null) {
                this.sketch.onDestroy();
            }

        }

        public float getXOffset() {
            return this.xOffset;
        }

        public float getYOffset() {
            return this.yOffset;
        }

        public float getXOffsetStep() {
            return this.xOffsetStep;
        }

        public float getYOffsetStep() {
            return this.yOffsetStep;
        }

        public int getXPixelOffset() {
            return this.xPixelOffset;
        }

        public int getYPixelOffset() {
            return this.yPixelOffset;
        }

        public boolean isInAmbientMode() {
            return false;
        }

        public boolean isRound() {
            return false;
        }

        public Rect getInsets() {
            return null;
        }

        public boolean useLowBitAmbient() {
            return false;
        }

        public boolean requireBurnInProtection() {
            return false;
        }

        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (this.sketch != null) {
                this.sketch.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

        }
    }
}


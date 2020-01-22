package processing.opengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.opengl.GLSurfaceView.EGLContextFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.service.wallpaper.WallpaperService;
import android.support.wearable.watchface.Gles2WatchFaceService;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import processing.android.AppComponent;
import processing.android.PFragment;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PSurfaceNone;

public class PSurfaceGLES extends PSurfaceNone {
    public PGLES pgl;
    private PSurfaceGLES.SurfaceViewGLES glsurf;

    public PSurfaceGLES() {
    }

    public PSurfaceGLES(PGraphics graphics, AppComponent component, SurfaceHolder holder) {
        this.sketch = graphics.parent;
        this.graphics = graphics;
        this.component = component;
        this.pgl = (PGLES)((PGraphicsOpenGL)graphics).pgl;
        if (component.getKind() == 0) {
            PFragment frag = (PFragment)component;
            this.activity = frag.getActivity();
            this.surfaceView = new PSurfaceGLES.SurfaceViewGLES(this.activity, (SurfaceHolder)null);
        } else if (component.getKind() == 1) {
            this.wallpaper = (WallpaperService)component;
            this.surfaceView = new PSurfaceGLES.SurfaceViewGLES(this.wallpaper, holder);
        } else if (component.getKind() == 2) {
            this.watchface = (Gles2WatchFaceService)component;
            this.surfaceReady = true;
        }

        this.glsurf = (PSurfaceGLES.SurfaceViewGLES)this.surfaceView;
    }

    public void dispose() {
        super.dispose();
        if (this.glsurf != null) {
            this.glsurf.dispose();
            this.glsurf = null;
        }

    }

    protected void callDraw() {
        this.component.requestDraw();
        if (this.component.canDraw() && this.glsurf != null) {
            this.glsurf.requestRender();
        }

    }

    public PSurfaceGLES.RendererGLES getRenderer() {
        return new PSurfaceGLES.RendererGLES();
    }

    public PSurfaceGLES.ContextFactoryGLES getContextFactory() {
        return new PSurfaceGLES.ContextFactoryGLES();
    }

    public PSurfaceGLES.ConfigChooserGLES getConfigChooser(int samples) {
        return new PSurfaceGLES.ConfigChooserGLES(5, 6, 5, 4, 16, 1, samples);
    }

    public PSurfaceGLES.ConfigChooserGLES getConfigChooser(int r, int g, int b, int a, int d, int s, int samples) {
        return new PSurfaceGLES.ConfigChooserGLES(r, g, b, a, d, s, samples);
    }

    protected class ConfigChooserGLES implements EGLConfigChooser {
        public int redTarget;
        public int greenTarget;
        public int blueTarget;
        public int alphaTarget;
        public int depthTarget;
        public int stencilTarget;
        public int redBits;
        public int greenBits;
        public int blueBits;
        public int alphaBits;
        public int depthBits;
        public int stencilBits;
        public int[] tempValue = new int[1];
        public int numSamples;
        protected int[] attribsNoMSAA = new int[]{12352, 4, 12338, 0, 12344};

        public ConfigChooserGLES(int rbits, int gbits, int bbits, int abits, int dbits, int sbits, int samples) {
            this.redTarget = rbits;
            this.greenTarget = gbits;
            this.blueTarget = bbits;
            this.alphaTarget = abits;
            this.depthTarget = dbits;
            this.stencilTarget = sbits;
            this.numSamples = samples;
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            EGLConfig[] configs = null;
            if (1 < this.numSamples) {
                int[] attribs = new int[]{12352, 4, 12338, 1, 12337, this.numSamples, 12344};
                configs = this.chooseConfigWithAttribs(egl, display, attribs);
                if (configs == null) {
                    int[] attribsCov = new int[]{12352, 4, 12512, 1, 12513, this.numSamples, 12344};
                    configs = this.chooseConfigWithAttribs(egl, display, attribsCov);
                    if (configs == null) {
                        configs = this.chooseConfigWithAttribs(egl, display, this.attribsNoMSAA);
                    } else {
                        PGLES.usingMultisampling = true;
                        PGLES.usingCoverageMultisampling = true;
                        PGLES.multisampleCount = this.numSamples;
                    }
                } else {
                    PGLES.usingMultisampling = true;
                    PGLES.usingCoverageMultisampling = false;
                    PGLES.multisampleCount = this.numSamples;
                }
            } else {
                configs = this.chooseConfigWithAttribs(egl, display, this.attribsNoMSAA);
            }

            if (configs == null) {
                throw new IllegalArgumentException("No EGL configs match configSpec");
            } else {
                return this.chooseBestConfig(egl, display, configs);
            }
        }

        public EGLConfig chooseBestConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs) {
            EGLConfig bestConfig = null;
            float bestScore = 3.4028235E38F;
            EGLConfig[] var6 = configs;
            int var7 = configs.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                EGLConfig config = var6[var8];
                int gl = this.findConfigAttrib(egl, display, config, 12352, 0);
                boolean isGLES2 = (gl & 4) != 0;
                if (isGLES2) {
                    int d = this.findConfigAttrib(egl, display, config, 12325, 0);
                    int s = this.findConfigAttrib(egl, display, config, 12326, 0);
                    int r = this.findConfigAttrib(egl, display, config, 12324, 0);
                    int g = this.findConfigAttrib(egl, display, config, 12323, 0);
                    int b = this.findConfigAttrib(egl, display, config, 12322, 0);
                    int a = this.findConfigAttrib(egl, display, config, 12321, 0);
                    float score = 0.2F * (float)PApplet.abs(r - this.redTarget) + 0.2F * (float)PApplet.abs(g - this.greenTarget) + 0.2F * (float)PApplet.abs(b - this.blueTarget) + 0.15F * (float)PApplet.abs(a - this.alphaTarget) + 0.15F * (float)PApplet.abs(d - this.depthTarget) + 0.1F * (float)PApplet.abs(s - this.stencilTarget);
                    if (score < bestScore) {
                        bestConfig = config;
                        bestScore = score;
                        this.redBits = r;
                        this.greenBits = g;
                        this.blueBits = b;
                        this.alphaBits = a;
                        this.depthBits = d;
                        this.stencilBits = s;
                    }
                }
            }

            return bestConfig;
        }

        protected String printConfig(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int r = this.findConfigAttrib(egl, display, config, 12324, 0);
            int g = this.findConfigAttrib(egl, display, config, 12323, 0);
            int b = this.findConfigAttrib(egl, display, config, 12322, 0);
            int a = this.findConfigAttrib(egl, display, config, 12321, 0);
            int d = this.findConfigAttrib(egl, display, config, 12325, 0);
            int s = this.findConfigAttrib(egl, display, config, 12326, 0);
            int type = this.findConfigAttrib(egl, display, config, 12352, 0);
            int nat = this.findConfigAttrib(egl, display, config, 12333, 0);
            int bufSize = this.findConfigAttrib(egl, display, config, 12320, 0);
            int bufSurf = this.findConfigAttrib(egl, display, config, 12422, 0);
            return String.format("EGLConfig rgba=%d%d%d%d depth=%d stencil=%d", r, g, b, a, d, s) + " type=" + type + " native=" + nat + " buffer size=" + bufSize + " buffer surface=" + bufSurf + String.format(" caveat=0x%04x", this.findConfigAttrib(egl, display, config, 12327, 0));
        }

        protected int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
            return egl.eglGetConfigAttrib(display, config, attribute, this.tempValue) ? this.tempValue[0] : defaultValue;
        }

        protected EGLConfig[] chooseConfigWithAttribs(EGL10 egl, EGLDisplay display, int[] configAttribs) {
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, configAttribs, (EGLConfig[])null, 0, configCounts);
            int count = configCounts[0];
            if (count <= 0) {
                return null;
            } else {
                EGLConfig[] configs = new EGLConfig[count];
                egl.eglChooseConfig(display, configAttribs, configs, count, configCounts);
                return configs;
            }
        }
    }

    protected class ContextFactoryGLES implements EGLContextFactory {
        protected ContextFactoryGLES() {
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            int[] attrib_list = new int[]{12440, 2, 12344};
            EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
            return context;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            egl.eglDestroyContext(display, context);
        }
    }

    protected class RendererGLES implements Renderer {
        public RendererGLES() {
        }

        public void onDrawFrame(GL10 igl) {
            PSurfaceGLES.this.pgl.getGL(igl);
            PSurfaceGLES.this.sketch.handleDraw();
        }

        public void onSurfaceChanged(GL10 igl, int iwidth, int iheight) {
            PSurfaceGLES.this.pgl.getGL(igl);
            PSurfaceGLES.this.sketch.surfaceChanged();
            PSurfaceGLES.this.sketch.setSize(iwidth, iheight);
        }

        public void onSurfaceCreated(GL10 igl, EGLConfig config) {
            PSurfaceGLES.this.pgl.init(igl);
        }
    }

    public class SurfaceViewGLES extends GLSurfaceView {
        SurfaceHolder holder;

        public SurfaceViewGLES(Context context, SurfaceHolder holder) {
            super(context);
            this.holder = holder;
            ActivityManager activityManager = (ActivityManager)context.getSystemService("activity");
            ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            boolean supportsGLES2 = configurationInfo.reqGlEsVersion >= 131072;
            if (!supportsGLES2) {
                throw new RuntimeException("OpenGL ES 2.0 is not supported by this device.");
            } else {
                SurfaceHolder h = this.getHolder();
                h.addCallback(this);
                this.setEGLContextClientVersion(2);
                this.setPreserveEGLContextOnPause(true);
                int samples = PSurfaceGLES.this.sketch.sketchSmooth();
                if (1 < samples) {
                    this.setEGLConfigChooser(PSurfaceGLES.this.getConfigChooser(samples));
                }

                this.setRenderer(PSurfaceGLES.this.getRenderer());
                this.setRenderMode(0);
                this.setFocusable(true);
                this.setFocusableInTouchMode(true);
                this.requestFocus();
                PSurfaceGLES.this.surfaceReady = false;
            }
        }

        public SurfaceHolder getHolder() {
            return this.holder == null ? super.getHolder() : this.holder;
        }

        public void dispose() {
            super.destroyDrawingCache();
            super.onDetachedFromWindow();
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            super.surfaceChanged(holder, format, w, h);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            super.surfaceCreated(holder);
            PSurfaceGLES.this.surfaceReady = true;
            if (PSurfaceGLES.this.requestedThreadStart) {
                PSurfaceGLES.this.startThread();
            }

        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            super.surfaceDestroyed(holder);
        }

        public void onWindowFocusChanged(boolean hasFocus) {
            super.onWindowFocusChanged(hasFocus);
            PSurfaceGLES.this.sketch.surfaceWindowFocusChanged(hasFocus);
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean fullscreen = PSurfaceGLES.this.sketch.width == PSurfaceGLES.this.sketch.displayWidth && PSurfaceGLES.this.sketch.height == PSurfaceGLES.this.sketch.displayHeight;
            if (fullscreen && PApplet.SDK < 19) {
                this.setSystemUiVisibility(2);
            }

            return PSurfaceGLES.this.sketch.surfaceTouchEvent(event);
        }

        public boolean onKeyDown(int code, KeyEvent event) {
            PSurfaceGLES.this.sketch.surfaceKeyDown(code, event);
            return super.onKeyDown(code, event);
        }

        public boolean onKeyUp(int code, KeyEvent event) {
            PSurfaceGLES.this.sketch.surfaceKeyUp(code, event);
            return super.onKeyUp(code, event);
        }
    }
}

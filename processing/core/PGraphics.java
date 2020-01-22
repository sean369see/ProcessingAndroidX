package processing.core;


import android.graphics.Color;
import android.view.SurfaceHolder;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import processing.android.AppComponent;
import processing.core.PFont.Glyph;
import processing.opengl.PGL;
import processing.opengl.PShader;

public class PGraphics extends PImage implements PConstants {
    public int pixelCount;
    public int smooth;
    protected boolean settingsInited;
    protected boolean reapplySettings;
    protected PGraphics raw;
    protected String path;
    protected boolean primaryGraphics;
    protected boolean[] hints = new boolean[13];
    protected WeakHashMap<PImage, Object> cacheMap = new WeakHashMap();
    public static final int R = 3;
    public static final int G = 4;
    public static final int B = 5;
    public static final int A = 6;
    public static final int U = 7;
    public static final int V = 8;
    public static final int NX = 9;
    public static final int NY = 10;
    public static final int NZ = 11;
    public static final int EDGE = 12;
    public static final int SR = 13;
    public static final int SG = 14;
    public static final int SB = 15;
    public static final int SA = 16;
    public static final int SW = 17;
    public static final int TX = 18;
    public static final int TY = 19;
    public static final int TZ = 20;
    public static final int VX = 21;
    public static final int VY = 22;
    public static final int VZ = 23;
    public static final int VW = 24;
    public static final int AR = 25;
    public static final int AG = 26;
    public static final int AB = 27;
    public static final int DR = 3;
    public static final int DG = 4;
    public static final int DB = 5;
    public static final int DA = 6;
    public static final int SPR = 28;
    public static final int SPG = 29;
    public static final int SPB = 30;
    public static final int SHINE = 31;
    public static final int ER = 32;
    public static final int EG = 33;
    public static final int EB = 34;
    public static final int BEEN_LIT = 35;
    public static final int HAS_NORMAL = 36;
    public static final int VERTEX_FIELD_COUNT = 37;
    public int colorMode;
    public float colorModeX;
    public float colorModeY;
    public float colorModeZ;
    public float colorModeA;
    boolean colorModeScale;
    boolean colorModeDefault;
    public boolean tint;
    public int tintColor;
    protected boolean tintAlpha;
    protected float tintR;
    protected float tintG;
    protected float tintB;
    protected float tintA;
    protected int tintRi;
    protected int tintGi;
    protected int tintBi;
    protected int tintAi;
    public boolean fill;
    public int fillColor = -1;
    protected boolean fillAlpha;
    protected float fillR;
    protected float fillG;
    protected float fillB;
    protected float fillA;
    protected int fillRi;
    protected int fillGi;
    protected int fillBi;
    protected int fillAi;
    public boolean stroke;
    public int strokeColor = -16777216;
    protected boolean strokeAlpha;
    protected float strokeR;
    protected float strokeG;
    protected float strokeB;
    protected float strokeA;
    protected int strokeRi;
    protected int strokeGi;
    protected int strokeBi;
    protected int strokeAi;
    protected static final float DEFAULT_STROKE_WEIGHT = 1.0F;
    protected static final int DEFAULT_STROKE_JOIN = 8;
    protected static final int DEFAULT_STROKE_CAP = 2;
    public float strokeWeight = 1.0F;
    public int strokeJoin = 8;
    public int strokeCap = 2;
    public int rectMode;
    public int ellipseMode;
    public int shapeMode;
    public int imageMode = 0;
    public PFont textFont;
    public int textAlign = 21;
    public int textAlignY = 0;
    public int textMode = 4;
    public float textSize;
    public float textLeading;
    public int ambientColor;
    public float ambientR;
    public float ambientG;
    public float ambientB;
    public boolean setAmbient;
    public int specularColor;
    public float specularR;
    public float specularG;
    public float specularB;
    public int emissiveColor;
    public float emissiveR;
    public float emissiveG;
    public float emissiveB;
    public float shininess;
    static final int STYLE_STACK_DEPTH = 64;
    PStyle[] styleStack = new PStyle[64];
    int styleStackDepth;
    public int backgroundColor = -3355444;
    protected boolean backgroundAlpha;
    protected float backgroundR;
    protected float backgroundG;
    protected float backgroundB;
    protected float backgroundA;
    protected int backgroundRi;
    protected int backgroundGi;
    protected int backgroundBi;
    protected int backgroundAi;
    protected int blendMode;
    static final int MATRIX_STACK_DEPTH = 32;
    protected float calcR;
    protected float calcG;
    protected float calcB;
    protected float calcA;
    protected int calcRi;
    protected int calcGi;
    protected int calcBi;
    protected int calcAi;
    protected int calcColor;
    protected boolean calcAlpha;
    int cacheHsbKey;
    float[] cacheHsbValue = new float[3];
    protected int shape;
    static final int DEFAULT_VERTICES = 512;
    protected float[][] vertices = new float[512][37];
    protected int vertexCount;
    protected boolean bezierInited = false;
    public int bezierDetail = 20;
    protected PMatrix3D bezierBasisMatrix = new PMatrix3D(-1.0F, 3.0F, -3.0F, 1.0F, 3.0F, -6.0F, 3.0F, 0.0F, -3.0F, 3.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F);
    protected PMatrix3D bezierDrawMatrix;
    protected boolean curveInited = false;
    public int curveDetail = 20;
    public float curveTightness = 0.0F;
    protected PMatrix3D curveBasisMatrix;
    protected PMatrix3D curveDrawMatrix;
    protected PMatrix3D bezierBasisInverse;
    protected PMatrix3D curveToBezierMatrix;
    protected float[][] curveVertices;
    protected int curveVertexCount;
    protected static final float[] sinLUT = new float[720];
    protected static final float[] cosLUT = new float[720];
    protected static final float SINCOS_PRECISION = 0.5F;
    protected static final int SINCOS_LENGTH = 720;
    protected char[] textBuffer = new char[8192];
    protected char[] textWidthBuffer = new char[8192];
    protected int textBreakCount;
    protected int[] textBreakStart;
    protected int[] textBreakStop;
    public boolean edge = true;
    protected static final int NORMAL_MODE_AUTO = 0;
    protected static final int NORMAL_MODE_SHAPE = 1;
    protected static final int NORMAL_MODE_VERTEX = 2;
    protected int normalMode;
    protected boolean autoNormal;
    public float normalX;
    public float normalY;
    public float normalZ;
    public int textureMode = 2;
    public float textureU;
    public float textureV;
    public PImage textureImage = null;
    protected float[] sphereX;
    protected float[] sphereY;
    protected float[] sphereZ;
    public int sphereDetailU = 0;
    public int sphereDetailV = 0;
    protected String restoreFilename;
    protected int restoreWidth;
    protected int restoreHeight;
    protected int restoreCount;
    protected boolean restartedLoopingAfterResume = false;
    protected boolean restoredSurface = true;
    protected boolean requestedNoLoop = false;
    static float[] lerpColorHSB1;
    static float[] lerpColorHSB2;
    static float[] lerpColorHSB3;
    protected static HashMap<String, Object> warnings;
    protected static PGraphics.AsyncImageSaver asyncImageSaver;

    public PGraphics() {
    }

    public void setParent(PApplet parent) {
        this.parent = parent;
    }

    public void setPrimary(boolean primary) {
        this.primaryGraphics = primary;
        if (this.primaryGraphics) {
            this.format = 1;
        }

    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFrameRate(float framerate) {
    }

    public void surfaceChanged() {
    }

    public void reset() {
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
        this.pixelWidth = this.width * this.pixelDensity;
        this.pixelHeight = this.height * this.pixelDensity;
        this.reapplySettings = true;
    }

    public void dispose() {
        this.parent = null;
    }

    public PSurface createSurface(AppComponent component, SurfaceHolder holder, boolean reset) {
        return null;
    }

    public void setCache(PImage image, Object storage) {
        this.cacheMap.put(image, storage);
    }

    public Object getCache(PImage image) {
        return this.cacheMap.get(image);
    }

    public void removeCache(PImage image) {
        this.cacheMap.remove(image);
    }

    public void requestFocus() {
    }

    public void beginDraw() {
    }

    public void endDraw() {
    }

    public void flush() {
    }

    public PGL beginPGL() {
        showMethodWarning("beginPGL");
        return null;
    }

    public void endPGL() {
        showMethodWarning("endPGL");
    }

    protected void checkSettings() {
        if (!this.settingsInited) {
            this.defaultSettings();
        }

        if (this.reapplySettings) {
            this.reapplySettings();
        }

    }

    protected void defaultSettings() {
        this.colorMode(1, 255.0F);
        this.fill(255);
        this.stroke(0);
        this.strokeWeight(1.0F);
        this.strokeJoin(8);
        this.strokeCap(2);
        this.shape = 0;
        this.rectMode(0);
        this.ellipseMode(3);
        this.autoNormal = true;
        this.textFont = null;
        this.textSize = 12.0F;
        this.textLeading = 14.0F;
        this.textAlign = 21;
        this.textMode = 4;
        if (this.primaryGraphics) {
            this.background(this.backgroundColor);
        }

        this.blendMode(1);
        this.settingsInited = true;
        this.reapplySettings = false;
    }

    protected void reapplySettings() {
        if (this.settingsInited) {
            this.colorMode(this.colorMode, this.colorModeX, this.colorModeY, this.colorModeZ);
            if (this.fill) {
                this.fill(this.fillColor);
            } else {
                this.noFill();
            }

            if (this.stroke) {
                this.stroke(this.strokeColor);
                this.strokeWeight(this.strokeWeight);
                this.strokeCap(this.strokeCap);
                this.strokeJoin(this.strokeJoin);
            } else {
                this.noStroke();
            }

            if (this.tint) {
                this.tint(this.tintColor);
            } else {
                this.noTint();
            }

            if (this.textFont != null) {
                float saveLeading = this.textLeading;
                this.textFont(this.textFont, this.textSize);
                this.textLeading(saveLeading);
            }

            this.textMode(this.textMode);
            this.textAlign(this.textAlign, this.textAlignY);
            this.background(this.backgroundColor);
            this.blendMode(this.blendMode);
            this.reapplySettings = false;
        }
    }

    protected void clearState() {
    }

    protected void saveState() {
    }

    protected void restoreState() {
        this.restoredSurface = false;
        if (!this.parent.looping) {
            this.parent.loop();
            this.restartedLoopingAfterResume = true;
        }

    }

    protected boolean restoringState() {
        return !this.restoredSurface && this.restartedLoopingAfterResume;
    }

    protected void restoreSurface() {
        if (this.restoredSurface && this.restartedLoopingAfterResume) {
            this.restartedLoopingAfterResume = false;
            this.parent.noLoop();
        }

    }

    protected boolean requestNoLoop() {
        return false;
    }

    protected boolean isLooping() {
        return this.parent.isLooping() && (!this.requestNoLoop() || !this.requestedNoLoop);
    }

    public void hint(int which) {
        if (which == 1 || which == -1) {
            showWarning("hint(ENABLE_NATIVE_FONTS) no longer supported. Use createFont() instead.");
        }

        if (which == -11) {
            this.parent.keyRepeatEnabled = true;
        } else if (which == 11) {
            this.parent.keyRepeatEnabled = false;
        }

        if (which > 0) {
            this.hints[which] = true;
        } else {
            this.hints[-which] = false;
        }

    }

    public void beginShape() {
        this.beginShape(20);
    }

    public void beginShape(int kind) {
        this.shape = kind;
    }

    public void edge(boolean edge) {
        this.edge = edge;
    }

    public void normal(float nx, float ny, float nz) {
        this.normalX = nx;
        this.normalY = ny;
        this.normalZ = nz;
        if (this.shape != 0) {
            if (this.normalMode == 0) {
                this.normalMode = 1;
            } else if (this.normalMode == 1) {
                this.normalMode = 2;
            }
        }

    }

    public void attribPosition(String name, float x, float y, float z) {
        showMissingWarning("attrib");
    }

    public void attribNormal(String name, float nx, float ny, float nz) {
        showMissingWarning("attrib");
    }

    public void attribColor(String name, int color) {
        showMissingWarning("attrib");
    }

    public void attrib(String name, float... values) {
        showMissingWarning("attrib");
    }

    public void attrib(String name, int... values) {
        showMissingWarning("attrib");
    }

    public void attrib(String name, boolean... values) {
        showMissingWarning("attrib");
    }

    public void textureMode(int mode) {
        this.textureMode = mode;
    }

    public void textureWrap(int wrap) {
        showMissingWarning("textureWrap");
    }

    public void texture(PImage image) {
        this.textureImage = image;
    }

    public void noTexture() {
        this.textureImage = null;
    }

    protected void vertexCheck() {
        if (this.vertexCount == this.vertices.length) {
            float[][] temp = new float[this.vertexCount << 1][37];
            System.arraycopy(this.vertices, 0, temp, 0, this.vertexCount);
            this.vertices = temp;
        }

    }

    public void vertex(float x, float y) {
        this.vertexCheck();
        float[] vertex = this.vertices[this.vertexCount];
        this.curveVertexCount = 0;
        vertex[0] = x;
        vertex[1] = y;
        vertex[2] = 0.0F;
        vertex[12] = this.edge ? 1.0F : 0.0F;
        boolean textured = this.textureImage != null;
        if (this.fill || textured) {
            if (!textured) {
                vertex[3] = this.fillR;
                vertex[4] = this.fillG;
                vertex[5] = this.fillB;
                vertex[6] = this.fillA;
            } else if (this.tint) {
                vertex[3] = this.tintR;
                vertex[4] = this.tintG;
                vertex[5] = this.tintB;
                vertex[6] = this.tintA;
            } else {
                vertex[3] = 1.0F;
                vertex[4] = 1.0F;
                vertex[5] = 1.0F;
                vertex[6] = 1.0F;
            }
        }

        if (this.stroke) {
            vertex[13] = this.strokeR;
            vertex[14] = this.strokeG;
            vertex[15] = this.strokeB;
            vertex[16] = this.strokeA;
            vertex[17] = this.strokeWeight;
        }

        vertex[7] = this.textureU;
        vertex[8] = this.textureV;
        if (this.autoNormal) {
            float norm2 = this.normalX * this.normalX + this.normalY * this.normalY + this.normalZ * this.normalZ;
            if (norm2 < 1.0E-4F) {
                vertex[36] = 0.0F;
            } else {
                if (Math.abs(norm2 - 1.0F) > 1.0E-4F) {
                    float norm = PApplet.sqrt(norm2);
                    this.normalX /= norm;
                    this.normalY /= norm;
                    this.normalZ /= norm;
                }

                vertex[36] = 1.0F;
            }
        } else {
            vertex[36] = 1.0F;
        }

        vertex[9] = this.normalX;
        vertex[10] = this.normalY;
        vertex[11] = this.normalZ;
        ++this.vertexCount;
    }

    public void vertex(float x, float y, float z) {
        this.vertexCheck();
        float[] vertex = this.vertices[this.vertexCount];
        if (this.shape == 20 && this.vertexCount > 0) {
            float[] pvertex = this.vertices[this.vertexCount - 1];
            if (Math.abs(pvertex[0] - x) < 1.0E-4F && Math.abs(pvertex[1] - y) < 1.0E-4F && Math.abs(pvertex[2] - z) < 1.0E-4F) {
                return;
            }
        }

        this.curveVertexCount = 0;
        vertex[0] = x;
        vertex[1] = y;
        vertex[2] = z;
        vertex[12] = this.edge ? 1.0F : 0.0F;
        boolean textured = this.textureImage != null;
        if (this.fill || textured) {
            if (!textured) {
                vertex[3] = this.fillR;
                vertex[4] = this.fillG;
                vertex[5] = this.fillB;
                vertex[6] = this.fillA;
            } else if (this.tint) {
                vertex[3] = this.tintR;
                vertex[4] = this.tintG;
                vertex[5] = this.tintB;
                vertex[6] = this.tintA;
            } else {
                vertex[3] = 1.0F;
                vertex[4] = 1.0F;
                vertex[5] = 1.0F;
                vertex[6] = 1.0F;
            }
        }

        if (this.stroke) {
            vertex[13] = this.strokeR;
            vertex[14] = this.strokeG;
            vertex[15] = this.strokeB;
            vertex[16] = this.strokeA;
            vertex[17] = this.strokeWeight;
        }

        vertex[7] = this.textureU;
        vertex[8] = this.textureV;
        if (this.autoNormal) {
            float norm2 = this.normalX * this.normalX + this.normalY * this.normalY + this.normalZ * this.normalZ;
            if (norm2 < 1.0E-4F) {
                vertex[36] = 0.0F;
            } else {
                if (Math.abs(norm2 - 1.0F) > 1.0E-4F) {
                    float norm = PApplet.sqrt(norm2);
                    this.normalX /= norm;
                    this.normalY /= norm;
                    this.normalZ /= norm;
                }

                vertex[36] = 1.0F;
            }
        } else {
            vertex[36] = 1.0F;
        }

        vertex[9] = this.normalX;
        vertex[10] = this.normalY;
        vertex[11] = this.normalZ;
        ++this.vertexCount;
    }

    public void vertex(float[] v) {
        this.vertexCheck();
        this.curveVertexCount = 0;
        float[] vertex = this.vertices[this.vertexCount];
        System.arraycopy(v, 0, vertex, 0, 37);
        ++this.vertexCount;
    }

    public void vertex(float x, float y, float u, float v) {
        this.vertexTexture(u, v);
        this.vertex(x, y);
    }

    public void vertex(float x, float y, float z, float u, float v) {
        this.vertexTexture(u, v);
        this.vertex(x, y, z);
    }

    protected void vertexTexture(float u, float v) {
        if (this.textureImage == null) {
            throw new RuntimeException("You must first call texture() before using u and v coordinates with vertex()");
        } else {
            if (this.textureMode == 2) {
                u /= (float)this.textureImage.width;
                v /= (float)this.textureImage.height;
            }

            this.textureU = u;
            this.textureV = v;
        }
    }

    public void breakShape() {
        showWarning("This renderer cannot currently handle concave shapes, or shapes with holes.");
    }

    public void beginContour() {
        showMissingWarning("beginContour");
    }

    public void endContour() {
        showMissingWarning("endContour");
    }

    public void endShape() {
        this.endShape(1);
    }

    public void endShape(int mode) {
    }

    public void clip(float a, float b, float c, float d) {
        if (this.imageMode == 0) {
            if (c < 0.0F) {
                a += c;
                c = -c;
            }

            if (d < 0.0F) {
                b += d;
                d = -d;
            }

            this.clipImpl(a, b, a + c, b + d);
        } else {
            float x1;
            if (this.imageMode == 1) {
                if (c < a) {
                    x1 = a;
                    a = c;
                    c = x1;
                }

                if (d < b) {
                    x1 = b;
                    b = d;
                    d = x1;
                }

                this.clipImpl(a, b, c, d);
            } else if (this.imageMode == 3) {
                if (c < 0.0F) {
                    c = -c;
                }

                if (d < 0.0F) {
                    d = -d;
                }

                x1 = a - c / 2.0F;
                float y1 = b - d / 2.0F;
                this.clipImpl(x1, y1, x1 + c, y1 + d);
            }
        }

    }

    protected void clipImpl(float x1, float y1, float x2, float y2) {
        showMissingWarning("clip");
    }

    public void noClip() {
        showMissingWarning("noClip");
    }

    public void blendMode(int mode) {
        this.blendMode = mode;
        this.blendModeImpl();
    }

    protected void blendModeImpl() {
        if (this.blendMode != 1) {
            showMissingWarning("blendMode");
        }

    }

    public PShape loadShape(String filename) {
        showMissingWarning("loadShape");
        return null;
    }

    public PShape createShape() {
        return this.createShape(3);
    }

    public PShape createShape(int type) {
        if (type != 0 && type != 2 && type != 3) {
            String msg = "Only GROUP, PShape.PATH, and PShape.GEOMETRY work with createShape()";
            throw new IllegalArgumentException("Only GROUP, PShape.PATH, and PShape.GEOMETRY work with createShape()");
        } else {
            return this.createShapeFamily(type);
        }
    }

    protected PShape createShapeFamily(int type) {
        return new PShape(this, type);
    }

    public PShape createShape(int kind, float... p) {
        int len = p.length;
        if (kind == 2) {
            if (this.is3D() && len != 2 && len != 3) {
                throw new IllegalArgumentException("Use createShape(POINT, x, y) or createShape(POINT, x, y, z)");
            } else if (len != 2) {
                throw new IllegalArgumentException("Use createShape(POINT, x, y)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 4) {
            if (this.is3D() && len != 4 && len != 6) {
                throw new IllegalArgumentException("Use createShape(LINE, x1, y1, x2, y2) or createShape(LINE, x1, y1, z1, x2, y2, z1)");
            } else if (len != 4) {
                throw new IllegalArgumentException("Use createShape(LINE, x1, y1, x2, y2)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 8) {
            if (len != 6) {
                throw new IllegalArgumentException("Use createShape(TRIANGLE, x1, y1, x2, y2, x3, y3)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 16) {
            if (len != 8) {
                throw new IllegalArgumentException("Use createShape(QUAD, x1, y1, x2, y2, x3, y3, x4, y4)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 30) {
            if (len != 4 && len != 5 && len != 8 && len != 9) {
                throw new IllegalArgumentException("Wrong number of parameters for createShape(RECT), see the reference");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 31) {
            if (len != 4 && len != 5) {
                throw new IllegalArgumentException("Use createShape(ELLIPSE, x, y, w, h) or createShape(ELLIPSE, x, y, w, h, mode)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 32) {
            if (len != 6 && len != 7) {
                throw new IllegalArgumentException("Use createShape(ARC, x, y, w, h, start, stop)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 41) {
            if (!this.is3D()) {
                throw new IllegalArgumentException("createShape(BOX) is not supported in 2D");
            } else if (len != 1 && len != 3) {
                throw new IllegalArgumentException("Use createShape(BOX, size) or createShape(BOX, width, height, depth)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else if (kind == 40) {
            if (!this.is3D()) {
                throw new IllegalArgumentException("createShape(SPHERE) is not supported in 2D");
            } else if (len != 1) {
                throw new IllegalArgumentException("Use createShape(SPHERE, radius)");
            } else {
                return this.createShapePrimitive(kind, p);
            }
        } else {
            throw new IllegalArgumentException("Unknown shape type passed to createShape()");
        }
    }

    protected PShape createShapePrimitive(int kind, float... p) {
        return new PShape(this, kind, p);
    }

    public PShader loadShader(String fragFilename) {
        showMissingWarning("loadShader");
        return null;
    }

    public PShader loadShader(String fragFilename, String vertFilename) {
        showMissingWarning("loadShader");
        return null;
    }

    public void shader(PShader shader) {
        showMissingWarning("shader");
    }

    public void shader(PShader shader, int kind) {
        showMissingWarning("shader");
    }

    public void resetShader() {
        showMissingWarning("resetShader");
    }

    public void resetShader(int kind) {
        showMissingWarning("resetShader");
    }

    public PShader getShader(int kind) {
        showMissingWarning("getShader");
        return null;
    }

    public void filter(PShader shader) {
        showMissingWarning("filter");
    }

    protected void bezierVertexCheck() {
        this.bezierVertexCheck(this.shape, this.vertexCount);
    }

    protected void bezierVertexCheck(int shape, int vertexCount) {
        if (shape != 0 && shape == 20) {
            if (vertexCount == 0) {
                throw new RuntimeException("vertex() must be used at least oncebefore bezierVertex() or quadraticVertex()");
            }
        } else {
            throw new RuntimeException("beginShape() or beginShape(POLYGON) must be used before bezierVertex() or quadraticVertex()");
        }
    }

    public void bezierVertex(float x2, float y2, float x3, float y3, float x4, float y4) {
        this.bezierInitCheck();
        this.bezierVertexCheck();
        PMatrix3D draw = this.bezierDrawMatrix;
        float[] prev = this.vertices[this.vertexCount - 1];
        float x1 = prev[0];
        float y1 = prev[1];
        float xplot1 = draw.m10 * x1 + draw.m11 * x2 + draw.m12 * x3 + draw.m13 * x4;
        float xplot2 = draw.m20 * x1 + draw.m21 * x2 + draw.m22 * x3 + draw.m23 * x4;
        float xplot3 = draw.m30 * x1 + draw.m31 * x2 + draw.m32 * x3 + draw.m33 * x4;
        float yplot1 = draw.m10 * y1 + draw.m11 * y2 + draw.m12 * y3 + draw.m13 * y4;
        float yplot2 = draw.m20 * y1 + draw.m21 * y2 + draw.m22 * y3 + draw.m23 * y4;
        float yplot3 = draw.m30 * y1 + draw.m31 * y2 + draw.m32 * y3 + draw.m33 * y4;

        for(int j = 0; j < this.bezierDetail; ++j) {
            x1 += xplot1;
            xplot1 += xplot2;
            xplot2 += xplot3;
            y1 += yplot1;
            yplot1 += yplot2;
            yplot2 += yplot3;
            this.vertex(x1, y1);
        }

    }

    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.bezierInitCheck();
        this.bezierVertexCheck();
        PMatrix3D draw = this.bezierDrawMatrix;
        float[] prev = this.vertices[this.vertexCount - 1];
        float x1 = prev[0];
        float y1 = prev[1];
        float z1 = prev[2];
        float xplot1 = draw.m10 * x1 + draw.m11 * x2 + draw.m12 * x3 + draw.m13 * x4;
        float xplot2 = draw.m20 * x1 + draw.m21 * x2 + draw.m22 * x3 + draw.m23 * x4;
        float xplot3 = draw.m30 * x1 + draw.m31 * x2 + draw.m32 * x3 + draw.m33 * x4;
        float yplot1 = draw.m10 * y1 + draw.m11 * y2 + draw.m12 * y3 + draw.m13 * y4;
        float yplot2 = draw.m20 * y1 + draw.m21 * y2 + draw.m22 * y3 + draw.m23 * y4;
        float yplot3 = draw.m30 * y1 + draw.m31 * y2 + draw.m32 * y3 + draw.m33 * y4;
        float zplot1 = draw.m10 * z1 + draw.m11 * z2 + draw.m12 * z3 + draw.m13 * z4;
        float zplot2 = draw.m20 * z1 + draw.m21 * z2 + draw.m22 * z3 + draw.m23 * z4;
        float zplot3 = draw.m30 * z1 + draw.m31 * z2 + draw.m32 * z3 + draw.m33 * z4;

        for(int j = 0; j < this.bezierDetail; ++j) {
            x1 += xplot1;
            xplot1 += xplot2;
            xplot2 += xplot3;
            y1 += yplot1;
            yplot1 += yplot2;
            yplot2 += yplot3;
            z1 += zplot1;
            zplot1 += zplot2;
            zplot2 += zplot3;
            this.vertex(x1, y1, z1);
        }

    }

    public void quadraticVertex(float cx, float cy, float x3, float y3) {
        this.bezierVertexCheck();
        float[] prev = this.vertices[this.vertexCount - 1];
        float x1 = prev[0];
        float y1 = prev[1];
        this.bezierVertex(x1 + (cx - x1) * 2.0F / 3.0F, y1 + (cy - y1) * 2.0F / 3.0F, x3 + (cx - x3) * 2.0F / 3.0F, y3 + (cy - y3) * 2.0F / 3.0F, x3, y3);
    }

    public void quadraticVertex(float cx, float cy, float cz, float x3, float y3, float z3) {
        this.bezierVertexCheck();
        float[] prev = this.vertices[this.vertexCount - 1];
        float x1 = prev[0];
        float y1 = prev[1];
        float z1 = prev[2];
        this.bezierVertex(x1 + (cx - x1) * 2.0F / 3.0F, y1 + (cy - y1) * 2.0F / 3.0F, z1 + (cz - z1) * 2.0F / 3.0F, x3 + (cx - x3) * 2.0F / 3.0F, y3 + (cy - y3) * 2.0F / 3.0F, z3 + (cz - z3) * 2.0F / 3.0F, x3, y3, z3);
    }

    protected void curveVertexCheck() {
        this.curveVertexCheck(this.shape);
    }

    protected void curveVertexCheck(int shape) {
        if (shape != 20) {
            throw new RuntimeException("You must use beginShape() or beginShape(POLYGON) before curveVertex()");
        } else {
            if (this.curveVertices == null) {
                this.curveVertices = new float[128][3];
            }

            if (this.curveVertexCount == this.curveVertices.length) {
                float[][] temp = new float[this.curveVertexCount << 1][3];
                System.arraycopy(this.curveVertices, 0, temp, 0, this.curveVertexCount);
                this.curveVertices = temp;
            }

            this.curveInitCheck();
        }
    }

    public void curveVertex(float x, float y) {
        this.curveVertexCheck();
        float[] vertex = this.curveVertices[this.curveVertexCount];
        vertex[0] = x;
        vertex[1] = y;
        ++this.curveVertexCount;
        if (this.curveVertexCount > 3) {
            this.curveVertexSegment(this.curveVertices[this.curveVertexCount - 4][0], this.curveVertices[this.curveVertexCount - 4][1], this.curveVertices[this.curveVertexCount - 3][0], this.curveVertices[this.curveVertexCount - 3][1], this.curveVertices[this.curveVertexCount - 2][0], this.curveVertices[this.curveVertexCount - 2][1], this.curveVertices[this.curveVertexCount - 1][0], this.curveVertices[this.curveVertexCount - 1][1]);
        }

    }

    public void curveVertex(float x, float y, float z) {
        this.curveVertexCheck();
        float[] vertex = this.curveVertices[this.curveVertexCount];
        vertex[0] = x;
        vertex[1] = y;
        vertex[2] = z;
        ++this.curveVertexCount;
        if (this.curveVertexCount > 3) {
            this.curveVertexSegment(this.curveVertices[this.curveVertexCount - 4][0], this.curveVertices[this.curveVertexCount - 4][1], this.curveVertices[this.curveVertexCount - 4][2], this.curveVertices[this.curveVertexCount - 3][0], this.curveVertices[this.curveVertexCount - 3][1], this.curveVertices[this.curveVertexCount - 3][2], this.curveVertices[this.curveVertexCount - 2][0], this.curveVertices[this.curveVertexCount - 2][1], this.curveVertices[this.curveVertexCount - 2][2], this.curveVertices[this.curveVertexCount - 1][0], this.curveVertices[this.curveVertexCount - 1][1], this.curveVertices[this.curveVertexCount - 1][2]);
        }

    }

    protected void curveVertexSegment(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float x0 = x2;
        float y0 = y2;
        PMatrix3D draw = this.curveDrawMatrix;
        float xplot1 = draw.m10 * x1 + draw.m11 * x2 + draw.m12 * x3 + draw.m13 * x4;
        float xplot2 = draw.m20 * x1 + draw.m21 * x2 + draw.m22 * x3 + draw.m23 * x4;
        float xplot3 = draw.m30 * x1 + draw.m31 * x2 + draw.m32 * x3 + draw.m33 * x4;
        float yplot1 = draw.m10 * y1 + draw.m11 * y2 + draw.m12 * y3 + draw.m13 * y4;
        float yplot2 = draw.m20 * y1 + draw.m21 * y2 + draw.m22 * y3 + draw.m23 * y4;
        float yplot3 = draw.m30 * y1 + draw.m31 * y2 + draw.m32 * y3 + draw.m33 * y4;
        int savedCount = this.curveVertexCount;
        this.vertex(x2, y2);

        for(int j = 0; j < this.curveDetail; ++j) {
            x0 += xplot1;
            xplot1 += xplot2;
            xplot2 += xplot3;
            y0 += yplot1;
            yplot1 += yplot2;
            yplot2 += yplot3;
            this.vertex(x0, y0);
        }

        this.curveVertexCount = savedCount;
    }

    protected void curveVertexSegment(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        float x0 = x2;
        float y0 = y2;
        float z0 = z2;
        PMatrix3D draw = this.curveDrawMatrix;
        float xplot1 = draw.m10 * x1 + draw.m11 * x2 + draw.m12 * x3 + draw.m13 * x4;
        float xplot2 = draw.m20 * x1 + draw.m21 * x2 + draw.m22 * x3 + draw.m23 * x4;
        float xplot3 = draw.m30 * x1 + draw.m31 * x2 + draw.m32 * x3 + draw.m33 * x4;
        float yplot1 = draw.m10 * y1 + draw.m11 * y2 + draw.m12 * y3 + draw.m13 * y4;
        float yplot2 = draw.m20 * y1 + draw.m21 * y2 + draw.m22 * y3 + draw.m23 * y4;
        float yplot3 = draw.m30 * y1 + draw.m31 * y2 + draw.m32 * y3 + draw.m33 * y4;
        int savedCount = this.curveVertexCount;
        float zplot1 = draw.m10 * z1 + draw.m11 * z2 + draw.m12 * z3 + draw.m13 * z4;
        float zplot2 = draw.m20 * z1 + draw.m21 * z2 + draw.m22 * z3 + draw.m23 * z4;
        float zplot3 = draw.m30 * z1 + draw.m31 * z2 + draw.m32 * z3 + draw.m33 * z4;
        this.vertex(x2, y2, z2);

        for(int j = 0; j < this.curveDetail; ++j) {
            x0 += xplot1;
            xplot1 += xplot2;
            xplot2 += xplot3;
            y0 += yplot1;
            yplot1 += yplot2;
            yplot2 += yplot3;
            z0 += zplot1;
            zplot1 += zplot2;
            zplot2 += zplot3;
            this.vertex(x0, y0, z0);
        }

        this.curveVertexCount = savedCount;
    }

    public void point(float x, float y) {
        this.beginShape(3);
        this.vertex(x, y);
        this.endShape();
    }

    public void point(float x, float y, float z) {
        this.beginShape(3);
        this.vertex(x, y, z);
        this.endShape();
    }

    public void line(float x1, float y1, float x2, float y2) {
        this.beginShape(5);
        this.vertex(x1, y1);
        this.vertex(x2, y2);
        this.endShape();
    }

    public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.beginShape(5);
        this.vertex(x1, y1, z1);
        this.vertex(x2, y2, z2);
        this.endShape();
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.beginShape(9);
        this.vertex(x1, y1);
        this.vertex(x2, y2);
        this.vertex(x3, y3);
        this.endShape();
    }

    public void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.beginShape(17);
        this.vertex(x1, y1);
        this.vertex(x2, y2);
        this.vertex(x3, y3);
        this.vertex(x4, y4);
        this.endShape();
    }

    public void rectMode(int mode) {
        this.rectMode = mode;
    }

    public void rect(float a, float b, float c, float d) {
        float hradius;
        float vradius;
        switch(this.rectMode) {
            case 0:
                c += a;
                d += b;
            case 1:
            default:
                break;
            case 2:
                hradius = c;
                vradius = d;
                c += a;
                d += b;
                a -= hradius;
                b -= vradius;
                break;
            case 3:
                hradius = c / 2.0F;
                vradius = d / 2.0F;
                c = a + hradius;
                d = b + vradius;
                a -= hradius;
                b -= vradius;
        }

        float temp;
        if (a > c) {
            temp = a;
            a = c;
            c = temp;
        }

        if (b > d) {
            temp = b;
            b = d;
            d = temp;
        }

        this.rectImpl(a, b, c, d);
    }

    protected void rectImpl(float x1, float y1, float x2, float y2) {
        this.quad(x1, y1, x2, y1, x2, y2, x1, y2);
    }

    public void rect(float a, float b, float c, float d, float r) {
        this.rect(a, b, c, d, r, r, r, r);
    }

    public void rect(float a, float b, float c, float d, float tl, float tr, float br, float bl) {
        float hradius;
        float vradius;
        switch(this.rectMode) {
            case 0:
                c += a;
                d += b;
            case 1:
            default:
                break;
            case 2:
                hradius = c;
                vradius = d;
                c += a;
                d += b;
                a -= hradius;
                b -= vradius;
                break;
            case 3:
                hradius = c / 2.0F;
                vradius = d / 2.0F;
                c = a + hradius;
                d = b + vradius;
                a -= hradius;
                b -= vradius;
        }

        float maxRounding;
        if (a > c) {
            maxRounding = a;
            a = c;
            c = maxRounding;
        }

        if (b > d) {
            maxRounding = b;
            b = d;
            d = maxRounding;
        }

        maxRounding = PApplet.min((c - a) / 2.0F, (d - b) / 2.0F);
        if (tl > maxRounding) {
            tl = maxRounding;
        }

        if (tr > maxRounding) {
            tr = maxRounding;
        }

        if (br > maxRounding) {
            br = maxRounding;
        }

        if (bl > maxRounding) {
            bl = maxRounding;
        }

        this.rectImpl(a, b, c, d, tl, tr, br, bl);
    }

    protected void rectImpl(float x1, float y1, float x2, float y2, float tl, float tr, float br, float bl) {
        this.beginShape();
        if (tr != 0.0F) {
            this.vertex(x2 - tr, y1);
            this.quadraticVertex(x2, y1, x2, y1 + tr);
        } else {
            this.vertex(x2, y1);
        }

        if (br != 0.0F) {
            this.vertex(x2, y2 - br);
            this.quadraticVertex(x2, y2, x2 - br, y2);
        } else {
            this.vertex(x2, y2);
        }

        if (bl != 0.0F) {
            this.vertex(x1 + bl, y2);
            this.quadraticVertex(x1, y2, x1, y2 - bl);
        } else {
            this.vertex(x1, y2);
        }

        if (tl != 0.0F) {
            this.vertex(x1, y1 + tl);
            this.quadraticVertex(x1, y1, x1 + tl, y1);
        } else {
            this.vertex(x1, y1);
        }

        this.endShape(2);
    }

    public void square(float x, float y, float extent) {
        this.rect(x, y, extent, extent);
    }

    public void ellipseMode(int mode) {
        this.ellipseMode = mode;
    }

    public void ellipse(float a, float b, float c, float d) {
        float x = a;
        float y = b;
        float w = c;
        float h = d;
        if (this.ellipseMode == 1) {
            w = c - a;
            h = d - b;
        } else if (this.ellipseMode == 2) {
            x = a - c;
            y = b - d;
            w = c * 2.0F;
            h = d * 2.0F;
        } else if (this.ellipseMode == 3) {
            x = a - c / 2.0F;
            y = b - d / 2.0F;
        }

        if (w < 0.0F) {
            x += w;
            w = -w;
        }

        if (h < 0.0F) {
            y += h;
            h = -h;
        }

        this.ellipseImpl(x, y, w, h);
    }

    protected void ellipseImpl(float x, float y, float w, float h) {
    }

    public void arc(float a, float b, float c, float d, float start, float stop) {
        this.arc(a, b, c, d, start, stop, 0);
    }

    public void arc(float a, float b, float c, float d, float start, float stop, int mode) {
        float x = a;
        float y = b;
        float w = c;
        float h = d;
        if (this.ellipseMode == 1) {
            w = c - a;
            h = d - b;
        } else if (this.ellipseMode == 2) {
            x = a - c;
            y = b - d;
            w = c * 2.0F;
            h = d * 2.0F;
        } else if (this.ellipseMode == 3) {
            x = a - c / 2.0F;
            y = b - d / 2.0F;
        }

        if (!Float.isInfinite(start) && !Float.isInfinite(stop) && stop > start) {
            while(start < 0.0F) {
                start += 6.2831855F;
                stop += 6.2831855F;
            }

            if (stop - start > 6.2831855F) {
                start = 0.0F;
                stop = 6.2831855F;
            }

            this.arcImpl(x, y, w, h, start, stop, mode);
        }

    }

    protected void arcImpl(float x, float y, float w, float h, float start, float stop, int mode) {
        showMissingWarning("arc");
    }

    public void circle(float x, float y, float extent) {
        this.ellipse(x, y, extent, extent);
    }

    public void box(float size) {
        this.box(size, size, size);
    }

    public void box(float w, float h, float d) {
        float x1 = -w / 2.0F;
        float x2 = w / 2.0F;
        float y1 = -h / 2.0F;
        float y2 = h / 2.0F;
        float z1 = -d / 2.0F;
        float z2 = d / 2.0F;
        this.beginShape(17);
        this.normal(0.0F, 0.0F, 1.0F);
        this.vertex(x1, y1, z1);
        this.vertex(x2, y1, z1);
        this.vertex(x2, y2, z1);
        this.vertex(x1, y2, z1);
        this.normal(1.0F, 0.0F, 0.0F);
        this.vertex(x2, y1, z1);
        this.vertex(x2, y1, z2);
        this.vertex(x2, y2, z2);
        this.vertex(x2, y2, z1);
        this.normal(0.0F, 0.0F, -1.0F);
        this.vertex(x2, y1, z2);
        this.vertex(x1, y1, z2);
        this.vertex(x1, y2, z2);
        this.vertex(x2, y2, z2);
        this.normal(-1.0F, 0.0F, 0.0F);
        this.vertex(x1, y1, z2);
        this.vertex(x1, y1, z1);
        this.vertex(x1, y2, z1);
        this.vertex(x1, y2, z2);
        this.normal(0.0F, 1.0F, 0.0F);
        this.vertex(x1, y1, z2);
        this.vertex(x2, y1, z2);
        this.vertex(x2, y1, z1);
        this.vertex(x1, y1, z1);
        this.normal(0.0F, -1.0F, 0.0F);
        this.vertex(x1, y2, z1);
        this.vertex(x2, y2, z1);
        this.vertex(x2, y2, z2);
        this.vertex(x1, y2, z2);
        this.endShape();
    }

    public void sphereDetail(int res) {
        this.sphereDetail(res, res);
    }

    public void sphereDetail(int ures, int vres) {
        if (ures < 3) {
            ures = 3;
        }

        if (vres < 2) {
            vres = 2;
        }

        if (ures != this.sphereDetailU || vres != this.sphereDetailV) {
            float delta = 720.0F / (float)ures;
            float[] cx = new float[ures];
            float[] cz = new float[ures];

            int vertCount;
            for(vertCount = 0; vertCount < ures; ++vertCount) {
                cx[vertCount] = cosLUT[(int)((float)vertCount * delta) % 720];
                cz[vertCount] = sinLUT[(int)((float)vertCount * delta) % 720];
            }

            vertCount = ures * (vres - 1) + 2;
            int currVert = 0;
            this.sphereX = new float[vertCount];
            this.sphereY = new float[vertCount];
            this.sphereZ = new float[vertCount];
            float angle_step = 360.0F / (float)vres;
            float angle = angle_step;

            for(int i = 1; i < vres; ++i) {
                float curradius = sinLUT[(int)angle % 720];
                float currY = cosLUT[(int)angle % 720];

                for(int j = 0; j < ures; ++j) {
                    this.sphereX[currVert] = cx[j] * curradius;
                    this.sphereY[currVert] = currY;
                    this.sphereZ[currVert++] = cz[j] * curradius;
                }

                angle += angle_step;
            }

            this.sphereDetailU = ures;
            this.sphereDetailV = vres;
        }
    }

    public void sphere(float r) {
        if (this.sphereDetailU < 3 || this.sphereDetailV < 2) {
            this.sphereDetail(30);
        }

        this.edge(false);
        this.beginShape(10);

        int v1;
        for(v1 = 0; v1 < this.sphereDetailU; ++v1) {
            this.normal(0.0F, -1.0F, 0.0F);
            this.vertex(0.0F, -r, 0.0F);
            this.normal(this.sphereX[v1], this.sphereY[v1], this.sphereZ[v1]);
            this.vertex(r * this.sphereX[v1], r * this.sphereY[v1], r * this.sphereZ[v1]);
        }

        this.normal(0.0F, -r, 0.0F);
        this.vertex(0.0F, -r, 0.0F);
        this.normal(this.sphereX[0], this.sphereY[0], this.sphereZ[0]);
        this.vertex(r * this.sphereX[0], r * this.sphereY[0], r * this.sphereZ[0]);
        this.endShape();
        int voff = 0;

        int v2;
        int i;
        for(i = 2; i < this.sphereDetailV; ++i) {
            int v11 = voff;
            v1 = voff;
            voff += this.sphereDetailU;
            v2 = voff;
            this.beginShape(10);

            for(int j = 0; j < this.sphereDetailU; ++j) {
                this.normal(this.sphereX[v1], this.sphereY[v1], this.sphereZ[v1]);
                this.vertex(r * this.sphereX[v1], r * this.sphereY[v1], r * this.sphereZ[v1++]);
                this.normal(this.sphereX[v2], this.sphereY[v2], this.sphereZ[v2]);
                this.vertex(r * this.sphereX[v2], r * this.sphereY[v2], r * this.sphereZ[v2++]);
            }

            this.normal(this.sphereX[v11], this.sphereY[v11], this.sphereZ[v11]);
            this.vertex(r * this.sphereX[v11], r * this.sphereY[v11], r * this.sphereZ[v11]);
            this.normal(this.sphereX[voff], this.sphereY[voff], this.sphereZ[voff]);
            this.vertex(r * this.sphereX[voff], r * this.sphereY[voff], r * this.sphereZ[voff]);
            this.endShape();
        }

        this.beginShape(10);

        for(i = 0; i < this.sphereDetailU; ++i) {
            v2 = voff + i;
            this.normal(this.sphereX[v2], this.sphereY[v2], this.sphereZ[v2]);
            this.vertex(r * this.sphereX[v2], r * this.sphereY[v2], r * this.sphereZ[v2]);
            this.normal(0.0F, 1.0F, 0.0F);
            this.vertex(0.0F, r, 0.0F);
        }

        this.normal(this.sphereX[voff], this.sphereY[voff], this.sphereZ[voff]);
        this.vertex(r * this.sphereX[voff], r * this.sphereY[voff], r * this.sphereZ[voff]);
        this.normal(0.0F, 1.0F, 0.0F);
        this.vertex(0.0F, r, 0.0F);
        this.endShape();
        this.edge(true);
    }

    public float bezierPoint(float a, float b, float c, float d, float t) {
        float t1 = t - 1.0F;
        return t * (3.0F * t1 * (b * t1 - c * t) + d * t * t) - a * t1 * t1 * t1;
    }

    public float bezierTangent(float a, float b, float c, float d, float t) {
        return 3.0F * t * t * (-a + 3.0F * b - 3.0F * c + d) + 6.0F * t * (a - 2.0F * b + c) + 3.0F * (-a + b);
    }

    protected void bezierInitCheck() {
        if (!this.bezierInited) {
            this.bezierInit();
        }

    }

    protected void bezierInit() {
        this.bezierDetail(this.bezierDetail);
        this.bezierInited = true;
    }

    public void bezierDetail(int detail) {
        this.bezierDetail = detail;
        if (this.bezierDrawMatrix == null) {
            this.bezierDrawMatrix = new PMatrix3D();
        }

        this.splineForward(detail, this.bezierDrawMatrix);
        this.bezierDrawMatrix.apply(this.bezierBasisMatrix);
    }

    public void bezier(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.beginShape();
        this.vertex(x1, y1);
        this.bezierVertex(x2, y2, x3, y3, x4, y4);
        this.endShape();
    }

    public void bezier(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.beginShape();
        this.vertex(x1, y1, z1);
        this.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4);
        this.endShape();
    }

    public float curvePoint(float a, float b, float c, float d, float t) {
        this.curveInitCheck();
        float tt = t * t;
        float ttt = t * tt;
        PMatrix3D cb = this.curveBasisMatrix;
        return a * (ttt * cb.m00 + tt * cb.m10 + t * cb.m20 + cb.m30) + b * (ttt * cb.m01 + tt * cb.m11 + t * cb.m21 + cb.m31) + c * (ttt * cb.m02 + tt * cb.m12 + t * cb.m22 + cb.m32) + d * (ttt * cb.m03 + tt * cb.m13 + t * cb.m23 + cb.m33);
    }

    public float curveTangent(float a, float b, float c, float d, float t) {
        this.curveInitCheck();
        float tt3 = t * t * 3.0F;
        float t2 = t * 2.0F;
        PMatrix3D cb = this.curveBasisMatrix;
        return a * (tt3 * cb.m00 + t2 * cb.m10 + cb.m20) + b * (tt3 * cb.m01 + t2 * cb.m11 + cb.m21) + c * (tt3 * cb.m02 + t2 * cb.m12 + cb.m22) + d * (tt3 * cb.m03 + t2 * cb.m13 + cb.m23);
    }

    public void curveDetail(int detail) {
        this.curveDetail = detail;
        this.curveInit();
    }

    public void curveTightness(float tightness) {
        this.curveTightness = tightness;
        this.curveInit();
    }

    protected void curveInitCheck() {
        if (!this.curveInited) {
            this.curveInit();
        }

    }

    protected void curveInit() {
        if (this.curveDrawMatrix == null) {
            this.curveBasisMatrix = new PMatrix3D();
            this.curveDrawMatrix = new PMatrix3D();
            this.curveInited = true;
        }

        float s = this.curveTightness;
        this.curveBasisMatrix.set((s - 1.0F) / 2.0F, (s + 3.0F) / 2.0F, (-3.0F - s) / 2.0F, (1.0F - s) / 2.0F, 1.0F - s, (-5.0F - s) / 2.0F, s + 2.0F, (s - 1.0F) / 2.0F, (s - 1.0F) / 2.0F, 0.0F, (1.0F - s) / 2.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F);
        this.splineForward(this.curveDetail, this.curveDrawMatrix);
        if (this.bezierBasisInverse == null) {
            this.bezierBasisInverse = this.bezierBasisMatrix.get();
            this.bezierBasisInverse.invert();
            this.curveToBezierMatrix = new PMatrix3D();
        }

        this.curveToBezierMatrix.set(this.curveBasisMatrix);
        this.curveToBezierMatrix.preApply(this.bezierBasisInverse);
        this.curveDrawMatrix.apply(this.curveBasisMatrix);
    }

    public void curve(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.beginShape();
        this.curveVertex(x1, y1);
        this.curveVertex(x2, y2);
        this.curveVertex(x3, y3);
        this.curveVertex(x4, y4);
        this.endShape();
    }

    public void curve(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.beginShape();
        this.curveVertex(x1, y1, z1);
        this.curveVertex(x2, y2, z2);
        this.curveVertex(x3, y3, z3);
        this.curveVertex(x4, y4, z4);
        this.endShape();
    }

    protected void splineForward(int segments, PMatrix3D matrix) {
        float f = 1.0F / (float)segments;
        float ff = f * f;
        float fff = ff * f;
        matrix.set(0.0F, 0.0F, 0.0F, 1.0F, fff, ff, f, 0.0F, 6.0F * fff, 2.0F * ff, 0.0F, 0.0F, 6.0F * fff, 0.0F, 0.0F, 0.0F);
    }

    public void smooth() {
        this.smooth(1);
    }

    public void smooth(int quality) {
        if (this.primaryGraphics) {
            this.parent.smooth(quality);
        } else if (this.settingsInited) {
            if (this.smooth != quality) {
                this.smoothWarning("smooth");
            }
        } else {
            this.smooth = quality;
        }

    }

    public void noSmooth() {
        this.smooth(0);
    }

    private void smoothWarning(String method) {
        showWarning("%s() can only be used before beginDraw()", method);
    }

    public void imageMode(int mode) {
        if (mode != 0 && mode != 1 && mode != 3) {
            String msg = "imageMode() only works with CORNER, CORNERS, or CENTER";
            throw new RuntimeException(msg);
        } else {
            this.imageMode = mode;
        }
    }

    public void image(PImage image, float x, float y) {
        if (image.width != -1 && image.height != -1) {
            if (image.width != 0 && image.height != 0) {
                if (this.imageMode != 0 && this.imageMode != 1) {
                    if (this.imageMode == 3) {
                        float x1 = x - (float)(image.width / 2);
                        float y1 = y - (float)(image.height / 2);
                        this.imageImpl(image, x1, y1, x1 + (float)image.width, y1 + (float)image.height, 0, 0, image.width, image.height);
                    }
                } else {
                    this.imageImpl(image, x, y, x + (float)image.width, y + (float)image.height, 0, 0, image.width, image.height);
                }

            }
        }
    }

    public void image(PImage image, float x, float y, float c, float d) {
        this.image(image, x, y, c, d, 0, 0, image.width, image.height);
    }

    public void image(PImage image, float a, float b, float c, float d, int u1, int v1, int u2, int v2) {
        if (image.width != -1 && image.height != -1) {
            if (this.imageMode == 0) {
                if (c < 0.0F) {
                    a += c;
                    c = -c;
                }

                if (d < 0.0F) {
                    b += d;
                    d = -d;
                }

                this.imageImpl(image, a, b, a + c, b + d, u1, v1, u2, v2);
            } else {
                float x1;
                if (this.imageMode == 1) {
                    if (c < a) {
                        x1 = a;
                        a = c;
                        c = x1;
                    }

                    if (d < b) {
                        x1 = b;
                        b = d;
                        d = x1;
                    }

                    this.imageImpl(image, a, b, c, d, u1, v1, u2, v2);
                } else if (this.imageMode == 3) {
                    if (c < 0.0F) {
                        c = -c;
                    }

                    if (d < 0.0F) {
                        d = -d;
                    }

                    x1 = a - c / 2.0F;
                    float y1 = b - d / 2.0F;
                    this.imageImpl(image, x1, y1, x1 + c, y1 + d, u1, v1, u2, v2);
                }
            }

        }
    }

    protected void imageImpl(PImage image, float x1, float y1, float x2, float y2, int u1, int v1, int u2, int v2) {
        boolean savedStroke = this.stroke;
        boolean savedFill = this.fill;
        int savedTextureMode = this.textureMode;
        this.stroke = false;
        this.fill = true;
        this.textureMode = 2;
        float savedFillR = this.fillR;
        float savedFillG = this.fillG;
        float savedFillB = this.fillB;
        float savedFillA = this.fillA;
        if (this.tint) {
            this.fillR = this.tintR;
            this.fillG = this.tintG;
            this.fillB = this.tintB;
            this.fillA = this.tintA;
        } else {
            this.fillR = 1.0F;
            this.fillG = 1.0F;
            this.fillB = 1.0F;
            this.fillA = 1.0F;
        }

        this.beginShape(17);
        this.texture(image);
        this.vertex(x1, y1, (float)u1, (float)v1);
        this.vertex(x1, y2, (float)u1, (float)v2);
        this.vertex(x2, y2, (float)u2, (float)v2);
        this.vertex(x2, y1, (float)u2, (float)v1);
        this.endShape();
        this.stroke = savedStroke;
        this.fill = savedFill;
        this.textureMode = savedTextureMode;
        this.fillR = savedFillR;
        this.fillG = savedFillG;
        this.fillB = savedFillB;
        this.fillA = savedFillA;
    }

    public void shapeMode(int mode) {
        this.shapeMode = mode;
    }

    public void shape(PShape shape) {
        if (shape.isVisible()) {
            if (this.shapeMode == 3) {
                this.pushMatrix();
                this.translate(-shape.getWidth() / 2.0F, -shape.getHeight() / 2.0F);
            }

            shape.draw(this);
            if (this.shapeMode == 3) {
                this.popMatrix();
            }
        }

    }

    public void shape(PShape shape, float x, float y) {
        if (shape.isVisible()) {
            this.pushMatrix();
            if (this.shapeMode == 3) {
                this.translate(x - shape.getWidth() / 2.0F, y - shape.getHeight() / 2.0F);
            } else if (this.shapeMode == 0 || this.shapeMode == 1) {
                this.translate(x, y);
            }

            shape.draw(this);
            this.popMatrix();
        }

    }

    protected void shape(PShape shape, float x, float y, float z) {
        showMissingWarning("shape");
    }

    public void shape(PShape shape, float x, float y, float c, float d) {
        if (shape.isVisible()) {
            this.pushMatrix();
            if (this.shapeMode == 3) {
                this.translate(x - c / 2.0F, y - d / 2.0F);
                this.scale(c / shape.getWidth(), d / shape.getHeight());
            } else if (this.shapeMode == 0) {
                this.translate(x, y);
                this.scale(c / shape.getWidth(), d / shape.getHeight());
            } else if (this.shapeMode == 1) {
                c -= x;
                d -= y;
                this.translate(x, y);
                this.scale(c / shape.getWidth(), d / shape.getHeight());
            }

            shape.draw(this);
            this.popMatrix();
        }

    }

    protected void shape(PShape shape, float x, float y, float z, float c, float d, float e) {
        showMissingWarning("shape");
    }

    public void textAlign(int align) {
        this.textAlign(align, 0);
    }

    public void textAlign(int alignX, int alignY) {
        this.textAlign = alignX;
        this.textAlignY = alignY;
    }

    public float textAscent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textAscent");
        }

        return this.textFont.ascent() * this.textSize;
    }

    public float textDescent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textDescent");
        }

        return this.textFont.descent() * this.textSize;
    }

    public void textFont(PFont which) {
        if (which == null) {
            throw new RuntimeException("A null PFont was passed to textFont()");
        } else {
            this.textFontImpl(which, (float)which.getDefaultSize());
        }
    }

    public void textFont(PFont which, float size) {
        if (which == null) {
            throw new RuntimeException("A null PFont was passed to textFont()");
        } else {
            if (size <= 0.0F) {
                System.err.println("textFont: ignoring size " + size + " px:the text size must be larger than zero");
                size = this.textSize;
            }

            this.textFontImpl(which, size);
        }
    }

    protected void textFontImpl(PFont which, float size) {
        this.textFont = which;
        this.handleTextSize(size);
    }

    public void textLeading(float leading) {
        this.textLeading = leading;
    }

    public void textMode(int mode) {
        if (mode != 21 && mode != 22) {
            if (mode == 256) {
                showWarning("textMode(SCREEN) has been removed from Processing 2.0.");
            }

            if (this.textModeCheck(mode)) {
                this.textMode = mode;
            } else {
                String modeStr = String.valueOf(mode);
                switch(mode) {
                    case 4:
                        modeStr = "MODEL";
                        break;
                    case 5:
                        modeStr = "SHAPE";
                }

                showWarning("textMode(" + modeStr + ") is not supported by this renderer.");
            }

        } else {
            showWarning("Since Processing beta, textMode() is now textAlign().");
        }
    }

    protected boolean textModeCheck(int mode) {
        return true;
    }

    public void textSize(float size) {
        if (size <= 0.0F) {
            System.err.println("textSize(" + size + ") ignored: the text size must be larger than zero");
        } else {
            if (this.textFont == null) {
                this.defaultFontOrDeath("textSize", size);
            }

            this.textSizeImpl(size);
        }
    }

    protected void textSizeImpl(float size) {
        this.handleTextSize(size);
    }

    protected void handleTextSize(float size) {
        this.textSize = size;
        this.textLeading = (this.textAscent() + this.textDescent()) * 1.275F;
    }

    public float textWidth(char c) {
        this.textWidthBuffer[0] = c;
        return this.textWidthImpl(this.textWidthBuffer, 0, 1);
    }

    public float textWidth(String str) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textWidth");
        }

        int length = str.length();
        if (length > this.textWidthBuffer.length) {
            this.textWidthBuffer = new char[length + 10];
        }

        str.getChars(0, length, this.textWidthBuffer, 0);
        float wide = 0.0F;
        int index = 0;

        int start;
        for(start = 0; index < length; ++index) {
            if (this.textWidthBuffer[index] == '\n') {
                wide = Math.max(wide, this.textWidthImpl(this.textWidthBuffer, start, index));
                start = index + 1;
            }
        }

        if (start < length) {
            wide = Math.max(wide, this.textWidthImpl(this.textWidthBuffer, start, index));
        }

        return wide;
    }

    protected float textWidthImpl(char[] buffer, int start, int stop) {
        float wide = 0.0F;

        for(int i = start; i < stop; ++i) {
            wide += this.textFont.width(buffer[i]) * this.textSize;
        }

        return wide;
    }

    public void text(char c, float x, float y) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }

        if (this.textAlignY == 3) {
            y += this.textAscent() / 2.0F;
        } else if (this.textAlignY == 101) {
            y += this.textAscent();
        } else if (this.textAlignY == 102) {
            y -= this.textDescent();
        }

        this.textBuffer[0] = c;
        this.textLineAlignImpl(this.textBuffer, 0, 1, x, y);
    }

    public void text(char c, float x, float y, float z) {
        if (z != 0.0F) {
            this.translate(0.0F, 0.0F, z);
        }

        this.text(c, x, y);
        if (z != 0.0F) {
            this.translate(0.0F, 0.0F, -z);
        }

    }

    public void text(String str, float x, float y) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }

        int length = str.length();
        if (length > this.textBuffer.length) {
            this.textBuffer = new char[length + 10];
        }

        str.getChars(0, length, this.textBuffer, 0);
        float high = 0.0F;

        int start;
        for(start = 0; start < length; ++start) {
            if (this.textBuffer[start] == '\n') {
                high += this.textLeading;
            }
        }

        if (this.textAlignY == 3) {
            y += (this.textAscent() - high) / 2.0F;
        } else if (this.textAlignY == 101) {
            y += this.textAscent();
        } else if (this.textAlignY == 102) {
            y -= this.textDescent() + high;
        }

        start = 0;

        int index;
        for(index = 0; index < length; ++index) {
            if (this.textBuffer[index] == '\n') {
                this.textLineAlignImpl(this.textBuffer, start, index, x, y);
                start = index + 1;
                y += this.textLeading;
            }
        }

        if (start < length) {
            this.textLineAlignImpl(this.textBuffer, start, index, x, y);
        }

    }

    public void text(String str, float x, float y, float z) {
        if (z != 0.0F) {
            this.translate(0.0F, 0.0F, z);
        }

        this.text(str, x, y);
        if (z != 0.0F) {
            this.translate(0.0F, 0.0F, -z);
        }

    }

    public void text(String str, float x1, float y1, float x2, float y2) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }

        float hradius;
        float vradius;
        switch(this.rectMode) {
            case 0:
                x2 += x1;
                y2 += y1;
            case 1:
            default:
                break;
            case 2:
                hradius = x2;
                vradius = y2;
                x2 += x1;
                y2 += y1;
                x1 -= hradius;
                y1 -= vradius;
                break;
            case 3:
                hradius = x2 / 2.0F;
                vradius = y2 / 2.0F;
                x2 = x1 + hradius;
                y2 = y1 + vradius;
                x1 -= hradius;
                y1 -= vradius;
        }

        float boxWidth;
        if (x2 < x1) {
            boxWidth = x1;
            x1 = x2;
            x2 = boxWidth;
        }

        if (y2 < y1) {
            boxWidth = y1;
            y1 = y2;
            y2 = boxWidth;
        }

        boxWidth = x2 - x1;
        float spaceWidth = this.textWidth(' ');
        if (this.textBreakStart == null) {
            this.textBreakStart = new int[20];
            this.textBreakStop = new int[20];
        }

        this.textBreakCount = 0;
        int length = str.length();
        if (length + 1 > this.textBuffer.length) {
            this.textBuffer = new char[length + 1];
        }

        str.getChars(0, length, this.textBuffer, 0);
        this.textBuffer[length++] = '\n';
        int sentenceStart = 0;

        for(int i = 0; i < length; ++i) {
            if (this.textBuffer[i] == '\n') {
                boolean legit = this.textSentence(this.textBuffer, sentenceStart, i, boxWidth, spaceWidth);
                if (!legit) {
                    break;
                }

                sentenceStart = i + 1;
            }
        }

        float lineX = x1;
        if (this.textAlign == 3) {
            lineX = x1 + boxWidth / 2.0F;
        } else if (this.textAlign == 22) {
            lineX = x2;
        }

        float boxHeight = y2 - y1;
        float topAndBottom = this.textAscent() + this.textDescent();
        int lineFitCount = 1 + PApplet.floor((boxHeight - topAndBottom) / this.textLeading);
        int lineCount = Math.min(this.textBreakCount, lineFitCount);
        float y;
        if (this.textAlignY == 3) {
            y = this.textAscent() + this.textLeading * (float)(lineCount - 1);
            float y = y1 + this.textAscent() + (boxHeight - y) / 2.0F;

            for(int i = 0; i < lineCount; ++i) {
                this.textLineAlignImpl(this.textBuffer, this.textBreakStart[i], this.textBreakStop[i], lineX, y);
                y += this.textLeading;
            }
        } else {
            int i;
            if (this.textAlignY == 102) {
                y = y2 - this.textDescent() - this.textLeading * (float)(lineCount - 1);

                for(i = 0; i < lineCount; ++i) {
                    this.textLineAlignImpl(this.textBuffer, this.textBreakStart[i], this.textBreakStop[i], lineX, y);
                    y += this.textLeading;
                }
            } else {
                y = y1 + this.textAscent();

                for(i = 0; i < lineCount; ++i) {
                    this.textLineAlignImpl(this.textBuffer, this.textBreakStart[i], this.textBreakStop[i], lineX, y);
                    y += this.textLeading;
                }
            }
        }

    }

    protected boolean textSentence(char[] buffer, int start, int stop, float boxWidth, float spaceWidth) {
        float runningX = 0.0F;
        int lineStart = start;
        int wordStart = start;
        int index = start;

        while(true) {
            while(index <= stop) {
                if (buffer[index] != ' ' && index != stop) {
                    ++index;
                } else {
                    float wordWidth = this.textWidthImpl(buffer, wordStart, index);
                    if (runningX + wordWidth <= boxWidth) {
                        if (index == stop) {
                            this.textSentenceBreak(lineStart, index);
                            ++index;
                        } else {
                            runningX += wordWidth + spaceWidth;
                            wordStart = index + 1;
                            ++index;
                        }
                    } else {
                        if (runningX != 0.0F) {
                            index = wordStart;
                            this.textSentenceBreak(lineStart, wordStart);

                            while(index < stop && buffer[index] == ' ') {
                                ++index;
                            }
                        } else {
                            do {
                                --index;
                                if (index == wordStart) {
                                    return false;
                                }

                                wordWidth = this.textWidthImpl(buffer, wordStart, index);
                            } while(wordWidth > boxWidth);

                            this.textSentenceBreak(lineStart, index);
                        }

                        lineStart = index;
                        wordStart = index;
                        runningX = 0.0F;
                    }
                }
            }

            return true;
        }
    }

    protected void textSentenceBreak(int start, int stop) {
        if (this.textBreakCount == this.textBreakStart.length) {
            this.textBreakStart = PApplet.expand(this.textBreakStart);
            this.textBreakStop = PApplet.expand(this.textBreakStop);
        }

        this.textBreakStart[this.textBreakCount] = start;
        this.textBreakStop[this.textBreakCount] = stop;
        ++this.textBreakCount;
    }

    public void text(int num, float x, float y) {
        this.text(String.valueOf(num), x, y);
    }

    public void text(int num, float x, float y, float z) {
        this.text(String.valueOf(num), x, y, z);
    }

    public void text(float num, float x, float y) {
        this.text(PApplet.nfs(num, 0, 3), x, y);
    }

    public void text(float num, float x, float y, float z) {
        this.text(PApplet.nfs(num, 0, 3), x, y, z);
    }

    protected void textLineAlignImpl(char[] buffer, int start, int stop, float x, float y) {
        if (this.textAlign == 3) {
            x -= this.textWidthImpl(buffer, start, stop) / 2.0F;
        } else if (this.textAlign == 22) {
            x -= this.textWidthImpl(buffer, start, stop);
        }

        this.textLineImpl(buffer, start, stop, x, y);
    }

    protected void textLineImpl(char[] buffer, int start, int stop, float x, float y) {
        for(int index = start; index < stop; ++index) {
            this.textCharImpl(buffer[index], x, y);
            x += this.textWidth(buffer[index]);
        }

    }

    protected void textCharImpl(char ch, float x, float y) {
        Glyph glyph = this.textFont.getGlyph(ch);
        if (glyph != null && this.textMode == 4) {
            float high = (float)glyph.height / (float)this.textFont.size;
            float bwidth = (float)glyph.width / (float)this.textFont.size;
            float lextent = (float)glyph.leftExtent / (float)this.textFont.size;
            float textent = (float)glyph.topExtent / (float)this.textFont.size;
            float x1 = x + lextent * this.textSize;
            float y1 = y - textent * this.textSize;
            float x2 = x1 + bwidth * this.textSize;
            float y2 = y1 + high * this.textSize;
            this.textCharModelImpl(glyph.image, x1, y1, x2, y2, glyph.width, glyph.height);
        }

    }

    protected void textCharModelImpl(PImage glyph, float x1, float y1, float x2, float y2, int u2, int v2) {
        boolean savedTint = this.tint;
        int savedTintColor = this.tintColor;
        this.tint(this.fillColor);
        this.imageImpl(glyph, x1, y1, x2, y2, 0, 0, u2, v2);
        if (savedTint) {
            this.tint(savedTintColor);
        } else {
            this.noTint();
        }

    }

    public void push() {
        this.pushStyle();
        this.pushMatrix();
    }

    public void pop() {
        this.popStyle();
        this.popMatrix();
    }

    public void pushMatrix() {
        showMethodWarning("pushMatrix");
    }

    public void popMatrix() {
        showMethodWarning("popMatrix");
    }

    public void translate(float tx, float ty) {
        showMissingWarning("translate");
    }

    public void translate(float tx, float ty, float tz) {
        showMissingWarning("translate");
    }

    public void rotate(float angle) {
        showMissingWarning("rotate");
    }

    public void rotateX(float angle) {
        showMethodWarning("rotateX");
    }

    public void rotateY(float angle) {
        showMethodWarning("rotateY");
    }

    public void rotateZ(float angle) {
        showMethodWarning("rotateZ");
    }

    public void rotate(float angle, float vx, float vy, float vz) {
        showMissingWarning("rotate");
    }

    public void scale(float s) {
        showMissingWarning("scale");
    }

    public void scale(float sx, float sy) {
        showMissingWarning("scale");
    }

    public void scale(float x, float y, float z) {
        showMissingWarning("scale");
    }

    public void shearX(float angle) {
        showMissingWarning("shearX");
    }

    public void shearY(float angle) {
        showMissingWarning("shearY");
    }

    public void resetMatrix() {
        showMethodWarning("resetMatrix");
    }

    public void applyMatrix(PMatrix source) {
        if (source instanceof PMatrix2D) {
            this.applyMatrix((PMatrix2D)source);
        } else if (source instanceof PMatrix3D) {
            this.applyMatrix((PMatrix3D)source);
        }

    }

    public void applyMatrix(PMatrix2D source) {
        this.applyMatrix(source.m00, source.m01, source.m02, source.m10, source.m11, source.m12);
    }

    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        showMissingWarning("applyMatrix");
    }

    public void applyMatrix(PMatrix3D source) {
        this.applyMatrix(source.m00, source.m01, source.m02, source.m03, source.m10, source.m11, source.m12, source.m13, source.m20, source.m21, source.m22, source.m23, source.m30, source.m31, source.m32, source.m33);
    }

    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        showMissingWarning("applyMatrix");
    }

    public PMatrix getMatrix() {
        showMissingWarning("getMatrix");
        return null;
    }

    public PMatrix2D getMatrix(PMatrix2D target) {
        showMissingWarning("getMatrix");
        return null;
    }

    public PMatrix3D getMatrix(PMatrix3D target) {
        showMissingWarning("getMatrix");
        return null;
    }

    public PMatrix3D getObjectMatrix() {
        showMissingWarning("getObjectMatrix");
        return null;
    }

    public PMatrix3D getObjectMatrix(PMatrix3D target) {
        showMissingWarning("getObjectMatrix");
        return null;
    }

    public PMatrix3D getEyeMatrix() {
        showMissingWarning("getEyeMatrix");
        return null;
    }

    public PMatrix3D getEyeMatrix(PMatrix3D target) {
        showMissingWarning("getEyeMatrix");
        return null;
    }

    public void setMatrix(PMatrix source) {
        if (source instanceof PMatrix2D) {
            this.setMatrix((PMatrix2D)source);
        } else if (source instanceof PMatrix3D) {
            this.setMatrix((PMatrix3D)source);
        }

    }

    public void setMatrix(PMatrix2D source) {
        showMissingWarning("setMatrix");
    }

    public void setMatrix(PMatrix3D source) {
        showMissingWarning("setMatrix");
    }

    public void printMatrix() {
        showMethodWarning("printMatrix");
    }

    public void cameraUp() {
        showMethodWarning("cameraUp");
    }

    public void beginCamera() {
        showMethodWarning("beginCamera");
    }

    public void endCamera() {
        showMethodWarning("endCamera");
    }

    public void camera() {
        showMissingWarning("camera");
    }

    public void camera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        showMissingWarning("camera");
    }

    public void printCamera() {
        showMethodWarning("printCamera");
    }

    public void eye() {
        showMethodWarning("eye");
    }

    public void ortho() {
        showMissingWarning("ortho");
    }

    public void ortho(float left, float right, float bottom, float top) {
        showMissingWarning("ortho");
    }

    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        showMissingWarning("ortho");
    }

    public void perspective() {
        showMissingWarning("perspective");
    }

    public void perspective(float fovy, float aspect, float zNear, float zFar) {
        showMissingWarning("perspective");
    }

    public void frustum(float left, float right, float bottom, float top, float near, float far) {
        showMethodWarning("frustum");
    }

    public void printProjection() {
        showMethodWarning("printCamera");
    }

    public float screenX(float x, float y) {
        showMissingWarning("screenX");
        return 0.0F;
    }

    public float screenY(float x, float y) {
        showMissingWarning("screenY");
        return 0.0F;
    }

    public float screenX(float x, float y, float z) {
        showMissingWarning("screenX");
        return 0.0F;
    }

    public float screenY(float x, float y, float z) {
        showMissingWarning("screenY");
        return 0.0F;
    }

    public float screenZ(float x, float y, float z) {
        showMissingWarning("screenZ");
        return 0.0F;
    }

    public float modelX(float x, float y, float z) {
        showMissingWarning("modelX");
        return 0.0F;
    }

    public float modelY(float x, float y, float z) {
        showMissingWarning("modelY");
        return 0.0F;
    }

    public float modelZ(float x, float y, float z) {
        showMissingWarning("modelZ");
        return 0.0F;
    }

    public void pushStyle() {
        if (this.styleStackDepth == this.styleStack.length) {
            this.styleStack = (PStyle[])((PStyle[])PApplet.expand(this.styleStack));
        }

        if (this.styleStack[this.styleStackDepth] == null) {
            this.styleStack[this.styleStackDepth] = new PStyle();
        }

        PStyle s = this.styleStack[this.styleStackDepth++];
        this.getStyle(s);
    }

    public void popStyle() {
        if (this.styleStackDepth == 0) {
            throw new RuntimeException("Too many popStyle() without enough pushStyle()");
        } else {
            --this.styleStackDepth;
            this.style(this.styleStack[this.styleStackDepth]);
        }
    }

    public void style(PStyle s) {
        this.imageMode(s.imageMode);
        this.rectMode(s.rectMode);
        this.ellipseMode(s.ellipseMode);
        this.shapeMode(s.shapeMode);
        this.blendMode(s.blendMode);
        if (s.tint) {
            this.tint(s.tintColor);
        } else {
            this.noTint();
        }

        if (s.fill) {
            this.fill(s.fillColor);
        } else {
            this.noFill();
        }

        if (s.stroke) {
            this.stroke(s.strokeColor);
        } else {
            this.noStroke();
        }

        this.strokeWeight(s.strokeWeight);
        this.strokeCap(s.strokeCap);
        this.strokeJoin(s.strokeJoin);
        this.colorMode(1, 1.0F);
        this.ambient(s.ambientR, s.ambientG, s.ambientB);
        this.emissive(s.emissiveR, s.emissiveG, s.emissiveB);
        this.specular(s.specularR, s.specularG, s.specularB);
        this.shininess(s.shininess);
        this.colorMode(s.colorMode, s.colorModeX, s.colorModeY, s.colorModeZ, s.colorModeA);
        if (s.textFont != null) {
            this.textFont(s.textFont, s.textSize);
            this.textLeading(s.textLeading);
        }

        this.textAlign(s.textAlign, s.textAlignY);
        this.textMode(s.textMode);
    }

    public PStyle getStyle() {
        return this.getStyle((PStyle)null);
    }

    public PStyle getStyle(PStyle s) {
        if (s == null) {
            s = new PStyle();
        }

        s.imageMode = this.imageMode;
        s.rectMode = this.rectMode;
        s.ellipseMode = this.ellipseMode;
        s.shapeMode = this.shapeMode;
        s.blendMode = this.blendMode;
        s.colorMode = this.colorMode;
        s.colorModeX = this.colorModeX;
        s.colorModeY = this.colorModeY;
        s.colorModeZ = this.colorModeZ;
        s.colorModeA = this.colorModeA;
        s.tint = this.tint;
        s.tintColor = this.tintColor;
        s.fill = this.fill;
        s.fillColor = this.fillColor;
        s.stroke = this.stroke;
        s.strokeColor = this.strokeColor;
        s.strokeWeight = this.strokeWeight;
        s.strokeCap = this.strokeCap;
        s.strokeJoin = this.strokeJoin;
        s.ambientR = this.ambientR;
        s.ambientG = this.ambientG;
        s.ambientB = this.ambientB;
        s.specularR = this.specularR;
        s.specularG = this.specularG;
        s.specularB = this.specularB;
        s.emissiveR = this.emissiveR;
        s.emissiveG = this.emissiveG;
        s.emissiveB = this.emissiveB;
        s.shininess = this.shininess;
        s.textFont = this.textFont;
        s.textAlign = this.textAlign;
        s.textAlignY = this.textAlignY;
        s.textMode = this.textMode;
        s.textSize = this.textSize;
        s.textLeading = this.textLeading;
        return s;
    }

    public void strokeWeight(float weight) {
        this.strokeWeight = weight;
    }

    public void strokeJoin(int join) {
        this.strokeJoin = join;
    }

    public void strokeCap(int cap) {
        this.strokeCap = cap;
    }

    public void noStroke() {
        this.stroke = false;
    }

    public void stroke(int rgb) {
        this.colorCalc(rgb);
        this.strokeFromCalc();
    }

    public void stroke(int rgb, float alpha) {
        this.colorCalc(rgb, alpha);
        this.strokeFromCalc();
    }

    public void stroke(float gray) {
        this.colorCalc(gray);
        this.strokeFromCalc();
    }

    public void stroke(float gray, float alpha) {
        this.colorCalc(gray, alpha);
        this.strokeFromCalc();
    }

    public void stroke(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.strokeFromCalc();
    }

    public void stroke(float x, float y, float z, float a) {
        this.colorCalc(x, y, z, a);
        this.strokeFromCalc();
    }

    protected void strokeFromCalc() {
        this.stroke = true;
        this.strokeR = this.calcR;
        this.strokeG = this.calcG;
        this.strokeB = this.calcB;
        this.strokeA = this.calcA;
        this.strokeRi = this.calcRi;
        this.strokeGi = this.calcGi;
        this.strokeBi = this.calcBi;
        this.strokeAi = this.calcAi;
        this.strokeColor = this.calcColor;
        this.strokeAlpha = this.calcAlpha;
    }

    public void noTint() {
        this.tint = false;
    }

    public void tint(int rgb) {
        this.colorCalc(rgb);
        this.tintFromCalc();
    }

    public void tint(int rgb, float alpha) {
        this.colorCalc(rgb, alpha);
        this.tintFromCalc();
    }

    public void tint(float gray) {
        this.colorCalc(gray);
        this.tintFromCalc();
    }

    public void tint(float gray, float alpha) {
        this.colorCalc(gray, alpha);
        this.tintFromCalc();
    }

    public void tint(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.tintFromCalc();
    }

    public void tint(float x, float y, float z, float a) {
        this.colorCalc(x, y, z, a);
        this.tintFromCalc();
    }

    protected void tintFromCalc() {
        this.tint = true;
        this.tintR = this.calcR;
        this.tintG = this.calcG;
        this.tintB = this.calcB;
        this.tintA = this.calcA;
        this.tintRi = this.calcRi;
        this.tintGi = this.calcGi;
        this.tintBi = this.calcBi;
        this.tintAi = this.calcAi;
        this.tintColor = this.calcColor;
        this.tintAlpha = this.calcAlpha;
    }

    public void noFill() {
        this.fill = false;
    }

    public void fill(int rgb) {
        this.colorCalc(rgb);
        this.fillFromCalc();
    }

    public void fill(int rgb, float alpha) {
        this.colorCalc(rgb, alpha);
        this.fillFromCalc();
    }

    public void fill(float gray) {
        this.colorCalc(gray);
        this.fillFromCalc();
    }

    public void fill(float gray, float alpha) {
        this.colorCalc(gray, alpha);
        this.fillFromCalc();
    }

    public void fill(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.fillFromCalc();
    }

    public void fill(float x, float y, float z, float a) {
        this.colorCalc(x, y, z, a);
        this.fillFromCalc();
    }

    protected void fillFromCalc() {
        this.fill = true;
        this.fillR = this.calcR;
        this.fillG = this.calcG;
        this.fillB = this.calcB;
        this.fillA = this.calcA;
        this.fillRi = this.calcRi;
        this.fillGi = this.calcGi;
        this.fillBi = this.calcBi;
        this.fillAi = this.calcAi;
        this.fillColor = this.calcColor;
        this.fillAlpha = this.calcAlpha;
    }

    public void ambient(int rgb) {
        this.colorCalc(rgb);
        this.ambientFromCalc();
    }

    public void ambient(float gray) {
        this.colorCalc(gray);
        this.ambientFromCalc();
    }

    public void ambient(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.ambientFromCalc();
    }

    protected void ambientFromCalc() {
        this.ambientColor = this.calcColor;
        this.ambientR = this.calcR;
        this.ambientG = this.calcG;
        this.ambientB = this.calcB;
        this.setAmbient = true;
    }

    public void specular(int rgb) {
        this.colorCalc(rgb);
        this.specularFromCalc();
    }

    public void specular(float gray) {
        this.colorCalc(gray);
        this.specularFromCalc();
    }

    public void specular(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.specularFromCalc();
    }

    protected void specularFromCalc() {
        this.specularColor = this.calcColor;
        this.specularR = this.calcR;
        this.specularG = this.calcG;
        this.specularB = this.calcB;
    }

    public void shininess(float shine) {
        this.shininess = shine;
    }

    public void emissive(int rgb) {
        this.colorCalc(rgb);
        this.emissiveFromCalc();
    }

    public void emissive(float gray) {
        this.colorCalc(gray);
        this.emissiveFromCalc();
    }

    public void emissive(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.emissiveFromCalc();
    }

    protected void emissiveFromCalc() {
        this.emissiveColor = this.calcColor;
        this.emissiveR = this.calcR;
        this.emissiveG = this.calcG;
        this.emissiveB = this.calcB;
    }

    public void lights() {
        showMethodWarning("lights");
    }

    public void noLights() {
        showMethodWarning("noLights");
    }

    public void ambientLight(float red, float green, float blue) {
        showMethodWarning("ambientLight");
    }

    public void ambientLight(float red, float green, float blue, float x, float y, float z) {
        showMethodWarning("ambientLight");
    }

    public void directionalLight(float red, float green, float blue, float nx, float ny, float nz) {
        showMethodWarning("directionalLight");
    }

    public void pointLight(float red, float green, float blue, float x, float y, float z) {
        showMethodWarning("pointLight");
    }

    public void spotLight(float red, float green, float blue, float x, float y, float z, float nx, float ny, float nz, float angle, float concentration) {
        showMethodWarning("spotLight");
    }

    public void lightFalloff(float constant, float linear, float quadratic) {
        showMethodWarning("lightFalloff");
    }

    public void lightSpecular(float x, float y, float z) {
        showMethodWarning("lightSpecular");
    }

    public void background(int rgb) {
        this.colorCalc(rgb);
        this.backgroundFromCalc();
    }

    public void background(int rgb, float alpha) {
        this.colorCalc(rgb, alpha);
        this.backgroundFromCalc();
    }

    public void background(float gray) {
        this.colorCalc(gray);
        this.backgroundFromCalc();
    }

    public void background(float gray, float alpha) {
        if (this.format == 1) {
            this.background(gray);
        } else {
            this.colorCalc(gray, alpha);
            this.backgroundFromCalc();
        }

    }

    public void background(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.backgroundFromCalc();
    }

    public void background(float x, float y, float z, float a) {
        this.colorCalc(x, y, z, a);
        this.backgroundFromCalc();
    }

    public void clear() {
        this.background(0.0F, 0.0F, 0.0F, 0.0F);
    }

    protected void backgroundFromCalc() {
        this.backgroundR = this.calcR;
        this.backgroundG = this.calcG;
        this.backgroundB = this.calcB;
        this.backgroundA = this.format == 1 ? this.colorModeA : this.calcA;
        this.backgroundRi = this.calcRi;
        this.backgroundGi = this.calcGi;
        this.backgroundBi = this.calcBi;
        this.backgroundAi = this.format == 1 ? 255 : this.calcAi;
        this.backgroundAlpha = this.format == 1 ? false : this.calcAlpha;
        this.backgroundColor = this.calcColor;
        this.backgroundImpl();
    }

    public void background(PImage image) {
        if (image.width == this.width && image.height == this.height) {
            if (image.format != 1 && image.format != 2) {
                throw new RuntimeException("background images should be RGB or ARGB");
            } else {
                this.backgroundColor = 0;
                this.backgroundImpl(image);
            }
        } else {
            throw new RuntimeException("background image must be the same size as your application");
        }
    }

    protected void backgroundImpl(PImage image) {
        this.set(0, 0, image);
    }

    protected void backgroundImpl() {
        this.pushStyle();
        this.pushMatrix();
        this.resetMatrix();
        this.fill(this.backgroundColor);
        this.rect(0.0F, 0.0F, (float)this.width, (float)this.height);
        this.popMatrix();
        this.popStyle();
    }

    public void colorMode(int mode) {
        this.colorMode(mode, this.colorModeX, this.colorModeY, this.colorModeZ, this.colorModeA);
    }

    public void colorMode(int mode, float max) {
        this.colorMode(mode, max, max, max, max);
    }

    public void colorMode(int mode, float maxX, float maxY, float maxZ) {
        this.colorMode(mode, maxX, maxY, maxZ, this.colorModeA);
    }

    public void colorMode(int mode, float maxX, float maxY, float maxZ, float maxA) {
        this.colorMode = mode;
        this.colorModeX = maxX;
        this.colorModeY = maxY;
        this.colorModeZ = maxZ;
        this.colorModeA = maxA;
        this.colorModeScale = maxA != 1.0F || maxX != maxY || maxY != maxZ || maxZ != maxA;
        this.colorModeDefault = this.colorMode == 1 && this.colorModeA == 255.0F && this.colorModeX == 255.0F && this.colorModeY == 255.0F && this.colorModeZ == 255.0F;
    }

    protected void colorCalc(int rgb) {
        if ((rgb & -16777216) == 0 && (float)rgb <= this.colorModeX) {
            this.colorCalc((float)rgb);
        } else {
            this.colorCalcARGB(rgb, this.colorModeA);
        }

    }

    protected void colorCalc(int rgb, float alpha) {
        if ((rgb & -16777216) == 0 && (float)rgb <= this.colorModeX) {
            this.colorCalc((float)rgb, alpha);
        } else {
            this.colorCalcARGB(rgb, alpha);
        }

    }

    protected void colorCalc(float gray) {
        this.colorCalc(gray, this.colorModeA);
    }

    protected void colorCalc(float gray, float alpha) {
        if (gray > this.colorModeX) {
            gray = this.colorModeX;
        }

        if (alpha > this.colorModeA) {
            alpha = this.colorModeA;
        }

        if (gray < 0.0F) {
            gray = 0.0F;
        }

        if (alpha < 0.0F) {
            alpha = 0.0F;
        }

        this.calcR = this.colorModeScale ? gray / this.colorModeX : gray;
        this.calcG = this.calcR;
        this.calcB = this.calcR;
        this.calcA = this.colorModeScale ? alpha / this.colorModeA : alpha;
        this.calcRi = (int)(this.calcR * 255.0F);
        this.calcGi = (int)(this.calcG * 255.0F);
        this.calcBi = (int)(this.calcB * 255.0F);
        this.calcAi = (int)(this.calcA * 255.0F);
        this.calcColor = this.calcAi << 24 | this.calcRi << 16 | this.calcGi << 8 | this.calcBi;
        this.calcAlpha = this.calcAi != 255;
    }

    protected void colorCalc(float x, float y, float z) {
        this.colorCalc(x, y, z, this.colorModeA);
    }

    protected void colorCalc(float x, float y, float z, float a) {
        if (x > this.colorModeX) {
            x = this.colorModeX;
        }

        if (y > this.colorModeY) {
            y = this.colorModeY;
        }

        if (z > this.colorModeZ) {
            z = this.colorModeZ;
        }

        if (a > this.colorModeA) {
            a = this.colorModeA;
        }

        if (x < 0.0F) {
            x = 0.0F;
        }

        if (y < 0.0F) {
            y = 0.0F;
        }

        if (z < 0.0F) {
            z = 0.0F;
        }

        if (a < 0.0F) {
            a = 0.0F;
        }

        switch(this.colorMode) {
            case 1:
                if (this.colorModeScale) {
                    this.calcR = x / this.colorModeX;
                    this.calcG = y / this.colorModeY;
                    this.calcB = z / this.colorModeZ;
                    this.calcA = a / this.colorModeA;
                } else {
                    this.calcR = x;
                    this.calcG = y;
                    this.calcB = z;
                    this.calcA = a;
                }
                break;
            case 3:
                x /= this.colorModeX;
                y /= this.colorModeY;
                z /= this.colorModeZ;
                this.calcA = this.colorModeScale ? a / this.colorModeA : a;
                if (y == 0.0F) {
                    this.calcR = this.calcG = this.calcB = z;
                } else {
                    float which = (x - (float)((int)x)) * 6.0F;
                    float f = which - (float)((int)which);
                    float p = z * (1.0F - y);
                    float q = z * (1.0F - y * f);
                    float t = z * (1.0F - y * (1.0F - f));
                    switch((int)which) {
                        case 0:
                            this.calcR = z;
                            this.calcG = t;
                            this.calcB = p;
                            break;
                        case 1:
                            this.calcR = q;
                            this.calcG = z;
                            this.calcB = p;
                            break;
                        case 2:
                            this.calcR = p;
                            this.calcG = z;
                            this.calcB = t;
                            break;
                        case 3:
                            this.calcR = p;
                            this.calcG = q;
                            this.calcB = z;
                            break;
                        case 4:
                            this.calcR = t;
                            this.calcG = p;
                            this.calcB = z;
                            break;
                        case 5:
                            this.calcR = z;
                            this.calcG = p;
                            this.calcB = q;
                    }
                }
        }

        this.calcRi = (int)(255.0F * this.calcR);
        this.calcGi = (int)(255.0F * this.calcG);
        this.calcBi = (int)(255.0F * this.calcB);
        this.calcAi = (int)(255.0F * this.calcA);
        this.calcColor = this.calcAi << 24 | this.calcRi << 16 | this.calcGi << 8 | this.calcBi;
        this.calcAlpha = this.calcAi != 255;
    }

    protected void colorCalcARGB(int argb, float alpha) {
        if (alpha == this.colorModeA) {
            this.calcAi = argb >> 24 & 255;
            this.calcColor = argb;
        } else {
            this.calcAi = (int)((float)(argb >> 24 & 255) * (alpha / this.colorModeA));
            this.calcColor = this.calcAi << 24 | argb & 16777215;
        }

        this.calcRi = argb >> 16 & 255;
        this.calcGi = argb >> 8 & 255;
        this.calcBi = argb & 255;
        this.calcA = (float)this.calcAi / 255.0F;
        this.calcR = (float)this.calcRi / 255.0F;
        this.calcG = (float)this.calcGi / 255.0F;
        this.calcB = (float)this.calcBi / 255.0F;
        this.calcAlpha = this.calcAi != 255;
    }

    public final int color(int gray) {
        if ((gray & -16777216) == 0 && (float)gray <= this.colorModeX) {
            if (this.colorModeDefault) {
                if (gray > 255) {
                    gray = 255;
                } else if (gray < 0) {
                    gray = 0;
                }

                return -16777216 | gray << 16 | gray << 8 | gray;
            }

            this.colorCalc(gray);
        } else {
            this.colorCalcARGB(gray, this.colorModeA);
        }

        return this.calcColor;
    }

    public final int color(float gray) {
        this.colorCalc(gray);
        return this.calcColor;
    }

    public final int color(int gray, int alpha) {
        if (this.colorModeDefault) {
            if (gray > 255) {
                gray = 255;
            } else if (gray < 0) {
                gray = 0;
            }

            if (alpha > 255) {
                alpha = 255;
            } else if (alpha < 0) {
                alpha = 0;
            }

            return (alpha & 255) << 24 | gray << 16 | gray << 8 | gray;
        } else {
            this.colorCalc(gray, (float)alpha);
            return this.calcColor;
        }
    }

    public final int color(int rgb, float alpha) {
        if ((rgb & -16777216) == 0 && (float)rgb <= this.colorModeX) {
            this.colorCalc(rgb, alpha);
        } else {
            this.colorCalcARGB(rgb, alpha);
        }

        return this.calcColor;
    }

    public final int color(float gray, float alpha) {
        this.colorCalc(gray, alpha);
        return this.calcColor;
    }

    public final int color(int x, int y, int z) {
        if (this.colorModeDefault) {
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
            this.colorCalc((float)x, (float)y, (float)z);
            return this.calcColor;
        }
    }

    public final int color(float x, float y, float z) {
        this.colorCalc(x, y, z);
        return this.calcColor;
    }

    public final int color(int x, int y, int z, int a) {
        if (this.colorModeDefault) {
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
            this.colorCalc((float)x, (float)y, (float)z, (float)a);
            return this.calcColor;
        }
    }

    public final int color(float x, float y, float z, float a) {
        this.colorCalc(x, y, z, a);
        return this.calcColor;
    }

    public final float alpha(int what) {
        float c = (float)(what >> 24 & 255);
        return this.colorModeA == 255.0F ? c : c / 255.0F * this.colorModeA;
    }

    public final float red(int what) {
        float c = (float)(what >> 16 & 255);
        return this.colorModeDefault ? c : c / 255.0F * this.colorModeX;
    }

    public final float green(int what) {
        float c = (float)(what >> 8 & 255);
        return this.colorModeDefault ? c : c / 255.0F * this.colorModeY;
    }

    public final float blue(int what) {
        float c = (float)(what & 255);
        return this.colorModeDefault ? c : c / 255.0F * this.colorModeZ;
    }

    public final float hue(int what) {
        if (what != this.cacheHsbKey) {
            Color.RGBToHSV(what >> 16 & 255, what >> 8 & 255, what & 255, this.cacheHsbValue);
            this.cacheHsbKey = what;
        }

        return this.cacheHsbValue[0] / 360.0F * this.colorModeX;
    }

    public final float saturation(int what) {
        if (what != this.cacheHsbKey) {
            Color.RGBToHSV(what >> 16 & 255, what >> 8 & 255, what & 255, this.cacheHsbValue);
            this.cacheHsbKey = what;
        }

        return this.cacheHsbValue[1] * this.colorModeY;
    }

    public final float brightness(int what) {
        if (what != this.cacheHsbKey) {
            Color.RGBToHSV(what >> 16 & 255, what >> 8 & 255, what & 255, this.cacheHsbValue);
            this.cacheHsbKey = what;
        }

        return this.cacheHsbValue[2] * this.colorModeZ;
    }

    public int lerpColor(int c1, int c2, float amt) {
        return lerpColor(c1, c2, amt, this.colorMode);
    }

    public static int lerpColor(int c1, int c2, float amt, int mode) {
        float a1;
        float a2;
        if (mode == 1) {
            a1 = (float)(c1 >> 24 & 255);
            a2 = (float)(c1 >> 16 & 255);
            float g1 = (float)(c1 >> 8 & 255);
            float b1 = (float)(c1 & 255);
            float a2 = (float)(c2 >> 24 & 255);
            float r2 = (float)(c2 >> 16 & 255);
            float g2 = (float)(c2 >> 8 & 255);
            float b2 = (float)(c2 & 255);
            return (int)(a1 + (a2 - a1) * amt) << 24 | (int)(a2 + (r2 - a2) * amt) << 16 | (int)(g1 + (g2 - g1) * amt) << 8 | (int)(b1 + (b2 - b1) * amt);
        } else if (mode == 3) {
            if (lerpColorHSB1 == null) {
                lerpColorHSB1 = new float[3];
                lerpColorHSB2 = new float[3];
                lerpColorHSB3 = new float[3];
            }

            a1 = (float)(c1 >> 24 & 255);
            a2 = (float)(c2 >> 24 & 255);
            int alfa = (int)(a1 + (a2 - a1) * amt) << 24;
            Color.RGBToHSV(c1 >> 16 & 255, c1 >> 8 & 255, c1 & 255, lerpColorHSB1);
            Color.RGBToHSV(c2 >> 16 & 255, c2 >> 8 & 255, c2 & 255, lerpColorHSB2);
            lerpColorHSB3[0] = PApplet.lerp(lerpColorHSB1[0], lerpColorHSB2[0], amt);
            lerpColorHSB3[1] = PApplet.lerp(lerpColorHSB1[1], lerpColorHSB2[1], amt);
            lerpColorHSB3[2] = PApplet.lerp(lerpColorHSB1[2], lerpColorHSB2[2], amt);
            return Color.HSVToColor(alfa, lerpColorHSB3);
        } else {
            return 0;
        }
    }

    public void beginRaw(PGraphics rawGraphics) {
        this.raw = rawGraphics;
        rawGraphics.beginDraw();
    }

    public void endRaw() {
        if (this.raw != null) {
            this.flush();
            this.raw.endDraw();
            this.raw.dispose();
            this.raw = null;
        }

    }

    public boolean haveRaw() {
        return this.raw != null;
    }

    public PGraphics getRaw() {
        return this.raw;
    }

    public static void showWarning(String msg) {
        if (warnings == null) {
            warnings = new HashMap();
        }

        if (!warnings.containsKey(msg)) {
            System.err.println(msg);
            warnings.put(msg, new Object());
        }

    }

    public static void showWarning(String msg, Object... args) {
        showWarning(String.format(msg, args));
    }

    public static void showDepthWarning(String method) {
        showWarning(method + "() can only be used with a renderer that supports 3D, such as P3D or OPENGL.");
    }

    public static void showDepthWarningXYZ(String method) {
        showWarning(method + "() with x, y, and z coordinates can only be used with a renderer that supports 3D, such as P3D or OPENGL. Use a version without a z-coordinate instead.");
    }

    public static void showMethodWarning(String method) {
        showWarning(method + "() is not available with this renderer.");
    }

    public static void showVariationWarning(String str) {
        showWarning(str + " is not available with this renderer.");
    }

    public static void showMissingWarning(String method) {
        showWarning(method + "(), or this particular variation of it, is not available with this renderer.");
    }

    public static void showException(String msg) {
        throw new RuntimeException(msg);
    }

    protected void defaultFontOrDeath(String method) {
        this.defaultFontOrDeath(method, 12.0F);
    }

    protected void defaultFontOrDeath(String method, float size) {
        if (this.parent != null) {
            this.textFont = this.parent.createDefaultFont(size);
        } else {
            throw new RuntimeException("Use textFont() before " + method + "()");
        }
    }

    public boolean displayable() {
        return true;
    }

    public boolean is2D() {
        return true;
    }

    public boolean is3D() {
        return false;
    }

    public boolean isGL() {
        return false;
    }

    public boolean save(String filename) {
        if (this.hints[12]) {
            return super.save(filename);
        } else {
            if (asyncImageSaver == null) {
                asyncImageSaver = new PGraphics.AsyncImageSaver();
            }

            if (!this.loaded) {
                this.loadPixels();
            }

            PImage target = asyncImageSaver.getAvailableTarget(this.pixelWidth, this.pixelHeight, this.format);
            if (target == null) {
                return false;
            } else {
                int count = PApplet.min(this.pixels.length, target.pixels.length);
                System.arraycopy(this.pixels, 0, target.pixels, 0, count);
                asyncImageSaver.saveTargetAsync(this, target, filename);
                return true;
            }
        }
    }

    protected void processImageBeforeAsyncSave(PImage image) {
    }

    static {
        for(int i = 0; i < 720; ++i) {
            sinLUT[i] = (float)Math.sin((double)((float)i * 0.017453292F * 0.5F));
            cosLUT[i] = (float)Math.cos((double)((float)i * 0.017453292F * 0.5F));
        }

    }

    protected static class AsyncImageSaver {
        static final int TARGET_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        BlockingQueue<PImage> targetPool;
        ExecutorService saveExecutor;
        int targetsCreated;
        static final int TIME_AVG_FACTOR = 32;
        volatile long avgNanos;
        long lastTime;
        int lastFrameCount;

        public AsyncImageSaver() {
            this.targetPool = new ArrayBlockingQueue(TARGET_COUNT);
            this.saveExecutor = Executors.newFixedThreadPool(TARGET_COUNT);
            this.targetsCreated = 0;
            this.avgNanos = 0L;
            this.lastTime = 0L;
            this.lastFrameCount = 0;
        }

        public void dispose() {
            this.saveExecutor.shutdown();

            try {
                this.saveExecutor.awaitTermination(5000L, TimeUnit.SECONDS);
            } catch (InterruptedException var2) {
            }

        }

        public boolean hasAvailableTarget() {
            return this.targetsCreated < TARGET_COUNT || this.targetPool.isEmpty();
        }

        public PImage getAvailableTarget(int requestedWidth, int requestedHeight, int format) {
            try {
                PImage target;
                if (this.targetsCreated < TARGET_COUNT && this.targetPool.isEmpty()) {
                    target = new PImage(requestedWidth, requestedHeight);
                    ++this.targetsCreated;
                } else {
                    target = (PImage)this.targetPool.take();
                    if (target.width != requestedWidth || target.height != requestedHeight) {
                        target.width = requestedWidth;
                        target.height = requestedHeight;
                        target.pixels = new int[requestedWidth * requestedHeight];
                    }
                }

                target.format = format;
                return target;
            } catch (InterruptedException var5) {
                return null;
            }
        }

        public void returnUnusedTarget(PImage target) {
            this.targetPool.offer(target);
        }

        public void saveTargetAsync(final PGraphics renderer, final PImage target, final String filename) {
            target.parent = renderer.parent;
            if (target.parent.frameCount - 1 == this.lastFrameCount && TARGET_COUNT > 1) {
                long avgTimePerFrame = this.avgNanos / (long)Math.max(1, TARGET_COUNT - 1);
                long now = System.nanoTime();
                long delay = (long)PApplet.round((float)(this.lastTime + avgTimePerFrame - now) / 1000000.0F);

                try {
                    if (delay > 0L) {
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException var12) {
                }
            }

            this.lastFrameCount = target.parent.frameCount;
            this.lastTime = System.nanoTime();

            try {
                this.saveExecutor.submit(new Runnable() {
                    public void run() {
                        try {
                            long startTime = System.nanoTime();
                            renderer.processImageBeforeAsyncSave(target);
                            target.save(filename);
                            long saveNanos = System.nanoTime() - startTime;
                            synchronized(AsyncImageSaver.this) {
                                if (AsyncImageSaver.this.avgNanos == 0L) {
                                    AsyncImageSaver.this.avgNanos = saveNanos;
                                } else if (saveNanos < AsyncImageSaver.this.avgNanos) {
                                    AsyncImageSaver.this.avgNanos = (AsyncImageSaver.this.avgNanos * 31L + saveNanos) / 32L;
                                } else {
                                    AsyncImageSaver.this.avgNanos = saveNanos;
                                }
                            }
                        } finally {
                            AsyncImageSaver.this.targetPool.offer(target);
                        }

                    }
                });
            } catch (RejectedExecutionException var11) {
            }

        }
    }
}

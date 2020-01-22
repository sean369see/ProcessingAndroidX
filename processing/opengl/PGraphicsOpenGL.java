package processing.opengl;

import android.content.Context;
import android.os.Environment;
import android.view.SurfaceHolder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import processing.android.AppComponent;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PSurface;
import processing.core.PVector;
import processing.core.PFont.Glyph;
import processing.opengl.FontTexture.TextureInfo;
import processing.opengl.LinePath.PathIterator;
import processing.opengl.PGL.FontOutline;
import processing.opengl.Texture.Parameters;

public class PGraphicsOpenGL extends PGraphics {
    public PGL pgl = this.createPGL(this);
    public PGraphicsOpenGL currentPG;
    protected WeakHashMap<PFont, FontTexture> fontMap;
    private static ReferenceQueue<Object> refQueue = new ReferenceQueue();
    private static List<PGraphicsOpenGL.Disposable<? extends Object>> reachableWeakReferences = new LinkedList();
    private static final int MAX_DRAIN_GLRES_ITERATIONS = 10;
    public boolean initialized;
    protected static final int FLUSH_CONTINUOUSLY = 0;
    protected static final int FLUSH_WHEN_FULL = 1;
    protected static final int IMMEDIATE = 0;
    protected static final int RETAINED = 1;
    protected int flushMode = 1;
    protected VertexBuffer bufPolyVertex;
    protected VertexBuffer bufPolyColor;
    protected VertexBuffer bufPolyNormal;
    protected VertexBuffer bufPolyTexcoord;
    protected VertexBuffer bufPolyAmbient;
    protected VertexBuffer bufPolySpecular;
    protected VertexBuffer bufPolyEmissive;
    protected VertexBuffer bufPolyShininess;
    protected VertexBuffer bufPolyIndex;
    protected boolean polyBuffersCreated = false;
    protected int polyBuffersContext;
    protected VertexBuffer bufLineVertex;
    protected VertexBuffer bufLineColor;
    protected VertexBuffer bufLineAttrib;
    protected VertexBuffer bufLineIndex;
    protected boolean lineBuffersCreated = false;
    protected int lineBuffersContext;
    protected VertexBuffer bufPointVertex;
    protected VertexBuffer bufPointColor;
    protected VertexBuffer bufPointAttrib;
    protected VertexBuffer bufPointIndex;
    protected boolean pointBuffersCreated = false;
    protected int pointBuffersContext;
    protected PGraphicsOpenGL.AttributeMap polyAttribs;
    protected static final int INIT_VERTEX_BUFFER_SIZE = 256;
    protected static final int INIT_INDEX_BUFFER_SIZE = 512;
    protected static boolean glParamsRead = false;
    public static boolean npotTexSupported;
    public static boolean autoMipmapGenSupported;
    public static boolean fboMultisampleSupported;
    public static boolean packedDepthStencilSupported;
    public static boolean anisoSamplingSupported;
    public static boolean blendEqSupported;
    public static boolean readBufferSupported;
    public static boolean drawBufferSupported;
    public static int maxTextureSize;
    public static int maxSamples;
    public static float maxAnisoAmount;
    public static int depthBits;
    public static int stencilBits;
    public static String OPENGL_VENDOR;
    public static String OPENGL_RENDERER;
    public static String OPENGL_VERSION;
    public static String OPENGL_EXTENSIONS;
    public static String GLSL_VERSION;
    protected static URL defColorShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/ColorVert.glsl");
    protected static URL defTextureShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/TexVert.glsl");
    protected static URL defLightShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/LightVert.glsl");
    protected static URL defTexlightShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/TexLightVert.glsl");
    protected static URL defColorShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/ColorFrag.glsl");
    protected static URL defTextureShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/TexFrag.glsl");
    protected static URL defLightShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/LightFrag.glsl");
    protected static URL defTexlightShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/TexLightFrag.glsl");
    protected static URL defLineShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/LineVert.glsl");
    protected static URL defLineShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/LineFrag.glsl");
    protected static URL defPointShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/PointVert.glsl");
    protected static URL defPointShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/PointFrag.glsl");
    protected static URL maskShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/MaskFrag.glsl");
    protected PShader defColorShader;
    protected PShader defTextureShader;
    protected PShader defLightShader;
    protected PShader defTexlightShader;
    protected PShader defLineShader;
    protected PShader defPointShader;
    protected PShader maskShader;
    protected PShader polyShader;
    protected PShader lineShader;
    protected PShader pointShader;
    protected PGraphicsOpenGL.InGeometry inGeo;
    protected PGraphicsOpenGL.TessGeometry tessGeo;
    protected PGraphicsOpenGL.TexCache texCache;
    protected PGraphicsOpenGL.Tessellator tessellator;
    protected PGraphicsOpenGL.DepthSorter sorter;
    protected boolean isDepthSortingEnabled;
    protected PGraphicsOpenGL.AsyncPixelReader asyncPixelReader;
    protected boolean asyncPixelReaderInitialized;
    protected static final Set<PGraphicsOpenGL.AsyncPixelReader> ongoingPixelTransfers = new HashSet();
    protected static final List<PGraphicsOpenGL.AsyncPixelReader> ongoingPixelTransfersIterable = new ArrayList();
    public float cameraFOV;
    public float cameraX;
    public float cameraY;
    public float cameraZ;
    public float cameraNear;
    public float cameraFar;
    public float cameraAspect;
    public float defCameraFOV;
    public float defCameraX;
    public float defCameraY;
    public float defCameraZ;
    public float defCameraNear;
    public float defCameraFar;
    public float defCameraAspect;
    protected float eyeDist;
    protected boolean manipulatingCamera;
    protected boolean cameraUp = false;
    public PMatrix3D projection;
    public PMatrix3D camera;
    public PMatrix3D cameraInv;
    public PMatrix3D modelview;
    public PMatrix3D modelviewInv;
    public PMatrix3D projmodelview;
    protected float[] glProjection;
    protected float[] glModelview;
    protected float[] glProjmodelview;
    protected float[] glNormal;
    protected static PMatrix3D identity = new PMatrix3D();
    protected boolean sized;
    protected boolean changed;
    protected static final int MATRIX_STACK_DEPTH = 32;
    protected int modelviewStackDepth;
    protected int projectionStackDepth;
    protected float[][] modelviewStack = new float[32][16];
    protected float[][] modelviewInvStack = new float[32][16];
    protected float[][] cameraStack = new float[32][16];
    protected float[][] cameraInvStack = new float[32][16];
    protected float[][] projectionStack = new float[32][16];
    public boolean lights;
    public int lightCount = 0;
    public int[] lightType;
    public float[] lightPosition;
    public float[] lightNormal;
    public float[] lightAmbient;
    public float[] lightDiffuse;
    public float[] lightSpecular;
    public float[] lightFalloffCoefficients;
    public float[] lightSpotParameters;
    public float[] currentLightSpecular;
    public float currentLightFalloffConstant;
    public float currentLightFalloffLinear;
    public float currentLightFalloffQuadratic;
    protected int textureWrap = 0;
    protected int textureSampling = 5;
    protected boolean clip = false;
    protected int[] clipRect = new int[]{0, 0, 0, 0};
    FontTexture textTex;
    protected static final int FB_STACK_DEPTH = 16;
    protected int fbStackDepth;
    protected FrameBuffer[] fbStack;
    protected FrameBuffer drawFramebuffer;
    protected FrameBuffer readFramebuffer;
    protected FrameBuffer currentFramebuffer;
    protected FrameBuffer offscreenFramebuffer;
    protected FrameBuffer multisampleFramebuffer;
    protected boolean offscreenMultisample;
    protected boolean pixOpChangedFB;
    protected Texture texture = null;
    protected Texture ptexture = null;
    protected IntBuffer pixelBuffer;
    protected int[] nativePixels;
    protected IntBuffer nativePixelBuffer;
    protected Texture filterTexture = null;
    protected PImage filterImage;
    protected boolean drawing = false;
    protected boolean smoothDisabled = false;
    protected int smoothCallCount = 0;
    protected int lastSmoothCall = -10;
    protected int lastBlendMode = -1;
    protected static final int OP_NONE = 0;
    protected static final int OP_READ = 1;
    protected static final int OP_WRITE = 2;
    protected int pixelsOp = 0;
    protected IntBuffer viewport;
    protected boolean openContour = false;
    protected boolean breakShape = false;
    protected boolean defaultEdges = false;
    protected static final int EDGE_MIDDLE = 0;
    protected static final int EDGE_START = 1;
    protected static final int EDGE_STOP = 2;
    protected static final int EDGE_SINGLE = 3;
    protected static final int EDGE_CLOSE = -1;
    protected static final int MIN_POINT_ACCURACY = 20;
    protected static final int MAX_POINT_ACCURACY = 200;
    protected static final float POINT_ACCURACY_FACTOR = 10.0F;
    protected static final float[][] QUAD_POINT_SIGNS = new float[][]{{-1.0F, 1.0F}, {-1.0F, -1.0F}, {1.0F, -1.0F}, {1.0F, 1.0F}};
    protected static IntBuffer intBuffer;
    protected static FloatBuffer floatBuffer;
    static final String OPENGL_THREAD_ERROR = "Cannot run the OpenGL renderer outside the main thread, change your code\nso the drawing calls are all inside the main thread, \nor use the default renderer instead.";
    static final String BLEND_DRIVER_ERROR = "blendMode(%1$s) is not supported by this hardware (or driver)";
    static final String BLEND_RENDERER_ERROR = "blendMode(%1$s) is not supported by this renderer";
    static final String ALREADY_BEGAN_CONTOUR_ERROR = "Already called beginContour()";
    static final String NO_BEGIN_CONTOUR_ERROR = "Need to call beginContour() first";
    static final String UNSUPPORTED_SMOOTH_LEVEL_ERROR = "Smooth level %1$s is not available. Using %2$s instead";
    static final String UNSUPPORTED_SMOOTH_ERROR = "Smooth is not supported by this hardware (or driver)";
    static final String TOO_MANY_SMOOTH_CALLS_ERROR = "The smooth/noSmooth functions are being called too often.\nThis results in screen flickering, so they will be disabled\nfor the rest of the sketch's execution";
    static final String UNSUPPORTED_SHAPE_FORMAT_ERROR = "Unsupported shape format";
    static final String MISSING_UV_TEXCOORDS_ERROR = "No uv texture coordinates supplied with vertex() call";
    static final String INVALID_FILTER_SHADER_ERROR = "Your shader cannot be used as a filter because is of type POINT or LINES";
    static final String INCONSISTENT_SHADER_TYPES = "The vertex and fragment shaders have different types";
    static final String WRONG_SHADER_TYPE_ERROR = "shader() called with a wrong shader";
    static final String SHADER_NEED_LIGHT_ATTRIBS = "The provided shader needs light attributes (ambient, diffuse, etc.), but the current scene is unlit, so the default shader will be used instead";
    static final String MISSING_FRAGMENT_SHADER = "The fragment shader is missing, cannot create shader object";
    static final String MISSING_VERTEX_SHADER = "The vertex shader is missing, cannot create shader object";
    static final String UNKNOWN_SHADER_KIND_ERROR = "Unknown shader kind";
    static final String NO_TEXLIGHT_SHADER_ERROR = "Your shader needs to be of TEXLIGHT type to render this geometry properly, using default shader instead.";
    static final String NO_LIGHT_SHADER_ERROR = "Your shader needs to be of LIGHT type to render this geometry properly, using default shader instead.";
    static final String NO_TEXTURE_SHADER_ERROR = "Your shader needs to be of TEXTURE type to render this geometry properly, using default shader instead.";
    static final String NO_COLOR_SHADER_ERROR = "Your shader needs to be of COLOR type to render this geometry properly, using default shader instead.";
    static final String TESSELLATION_ERROR = "Tessellation Error: %1$s";
    static final String GL_THREAD_NOT_CURRENT = "You are trying to draw outside OpenGL's animation thread.\nPlace all drawing commands in the draw() function, or inside\nyour own functions as long as they are called from draw(),\nbut not in event handling functions such as keyPressed()\nor mousePressed().";

    static void drainRefQueueBounded() {
        for(int iterations = 0; iterations < 10; ++iterations) {
            PGraphicsOpenGL.Disposable<? extends Object> res = (PGraphicsOpenGL.Disposable)refQueue.poll();
            if (res == null) {
                break;
            }

            res.dispose();
        }

    }

    public PGraphicsOpenGL() {
        if (intBuffer == null) {
            intBuffer = PGL.allocateIntBuffer(2);
            floatBuffer = PGL.allocateFloatBuffer(2);
        }

        this.viewport = PGL.allocateIntBuffer(4);
        this.polyAttribs = newAttributeMap();
        this.inGeo = newInGeometry(this, this.polyAttribs, 0);
        this.tessGeo = newTessGeometry(this, this.polyAttribs, 0);
        this.texCache = newTexCache(this);
        this.projection = new PMatrix3D();
        this.camera = new PMatrix3D();
        this.cameraInv = new PMatrix3D();
        this.modelview = new PMatrix3D();
        this.modelviewInv = new PMatrix3D();
        this.projmodelview = new PMatrix3D();
        this.lightType = new int[PGL.MAX_LIGHTS];
        this.lightPosition = new float[4 * PGL.MAX_LIGHTS];
        this.lightNormal = new float[3 * PGL.MAX_LIGHTS];
        this.lightAmbient = new float[3 * PGL.MAX_LIGHTS];
        this.lightDiffuse = new float[3 * PGL.MAX_LIGHTS];
        this.lightSpecular = new float[3 * PGL.MAX_LIGHTS];
        this.lightFalloffCoefficients = new float[3 * PGL.MAX_LIGHTS];
        this.lightSpotParameters = new float[2 * PGL.MAX_LIGHTS];
        this.currentLightSpecular = new float[3];
        this.initialized = false;
    }

    public void setParent(PApplet parent) {
        super.setParent(parent);
        if (this.pgl != null) {
            this.pgl.sketch = parent;
        }

    }

    public void setPrimary(boolean primary) {
        super.setPrimary(primary);
        this.pgl.setPrimary(primary);
        this.format = 2;
        if (primary) {
            this.fbStack = new FrameBuffer[16];
            this.fontMap = new WeakHashMap();
            this.tessellator = new PGraphicsOpenGL.Tessellator();
        } else {
            this.tessellator = this.getPrimaryPG().tessellator;
        }

    }

    public void surfaceChanged() {
        this.changed = true;
    }

    public void reset() {
        this.pgl.resetFBOLayer();
        this.restartPGL();
    }

    public void setSize(int iwidth, int iheight) {
        this.sized = iwidth != this.width || iheight != this.height;
        super.setSize(iwidth, iheight);
        this.updatePixelSize();
        this.defCameraFOV = 1.0471976F;
        this.defCameraX = (float)this.width / 2.0F;
        this.defCameraY = (float)this.height / 2.0F;
        this.defCameraZ = this.defCameraY / (float)Math.tan((double)(this.defCameraFOV / 2.0F));
        this.defCameraNear = this.defCameraZ / 10.0F;
        this.defCameraFar = this.defCameraZ * 10.0F;
        this.defCameraAspect = (float)this.width / (float)this.height;
        this.cameraFOV = this.defCameraFOV;
        this.cameraX = this.defCameraX;
        this.cameraY = this.defCameraY;
        this.cameraZ = this.defCameraZ;
        this.cameraNear = this.defCameraNear;
        this.cameraFar = this.defCameraFar;
        this.cameraAspect = this.defCameraAspect;
    }

    public void dispose() {
        if (this.asyncPixelReader != null) {
            this.asyncPixelReader.dispose();
            this.asyncPixelReader = null;
        }

        if (!this.primaryGraphics) {
            this.deleteSurfaceTextures();
            FrameBuffer ofb = this.offscreenFramebuffer;
            FrameBuffer mfb = this.multisampleFramebuffer;
            if (ofb != null) {
                ofb.dispose();
            }

            if (mfb != null) {
                mfb.dispose();
            }
        }

        this.pgl.dispose();
        super.dispose();
    }

    protected void setFlushMode(int mode) {
        this.flushMode = mode;
    }

    protected void updatePixelSize() {
        float f = this.pgl.getPixelScale();
        this.pixelWidth = (int)((float)this.width * f);
        this.pixelHeight = (int)((float)this.height * f);
    }

    protected PGL createPGL(PGraphicsOpenGL pg) {
        return new PGLES(pg);
    }

    public PSurface createSurface(AppComponent component, SurfaceHolder holder, boolean reset) {
        if (reset) {
            this.pgl.resetFBOLayer();
        }

        return new PSurfaceGLES(this, component, holder);
    }

    public void setFrameRate(float frameRate) {
        this.pgl.setFrameRate(frameRate);
    }

    protected boolean isLooping() {
        return super.isLooping();
    }

    public boolean saveImpl(String filename) {
        return super.save(filename);
    }

    public void setCache(PImage image, Object storage) {
        if (image instanceof PGraphicsOpenGL) {
            this.getPrimaryPG().cacheMap.put(image, new WeakReference(storage));
        } else {
            this.getPrimaryPG().cacheMap.put(image, storage);
        }
    }

    public Object getCache(PImage image) {
        Object storage = this.getPrimaryPG().cacheMap.get(image);
        return storage != null && storage.getClass() == WeakReference.class ? ((WeakReference)storage).get() : storage;
    }

    public void removeCache(PImage image) {
        this.getPrimaryPG().cacheMap.remove(image);
    }

    protected void setFontTexture(PFont font, FontTexture fontTexture) {
        this.getPrimaryPG().fontMap.put(font, fontTexture);
    }

    protected FontTexture getFontTexture(PFont font) {
        return (FontTexture)this.getPrimaryPG().fontMap.get(font);
    }

    protected void removeFontTexture(PFont font) {
        this.getPrimaryPG().fontMap.remove(font);
    }

    protected void pushFramebuffer() {
        PGraphicsOpenGL ppg = this.getPrimaryPG();
        if (ppg.fbStackDepth == 16) {
            throw new RuntimeException("Too many pushFramebuffer calls");
        } else {
            ppg.fbStack[ppg.fbStackDepth] = ppg.currentFramebuffer;
            ++ppg.fbStackDepth;
        }
    }

    protected void setFramebuffer(FrameBuffer fbo) {
        PGraphicsOpenGL ppg = this.getPrimaryPG();
        if (ppg.currentFramebuffer != fbo) {
            ppg.currentFramebuffer = fbo;
            if (ppg.currentFramebuffer != null) {
                ppg.currentFramebuffer.bind();
            }
        }

    }

    protected void popFramebuffer() {
        PGraphicsOpenGL ppg = this.getPrimaryPG();
        if (ppg.fbStackDepth == 0) {
            throw new RuntimeException("popFramebuffer call is unbalanced.");
        } else {
            --ppg.fbStackDepth;
            FrameBuffer fbo = ppg.fbStack[ppg.fbStackDepth];
            if (ppg.currentFramebuffer != fbo) {
                ppg.currentFramebuffer.finish();
                ppg.currentFramebuffer = fbo;
                if (ppg.currentFramebuffer != null) {
                    ppg.currentFramebuffer.bind();
                }
            }

        }
    }

    protected FrameBuffer getCurrentFB() {
        return this.getPrimaryPG().currentFramebuffer;
    }

    protected void createPolyBuffers() {
        if (!this.polyBuffersCreated || this.polyBuffersContextIsOutdated()) {
            this.polyBuffersContext = this.pgl.getCurrentContext();
            this.bufPolyVertex = new VertexBuffer(this, PGL.ARRAY_BUFFER, 3, PGL.SIZEOF_FLOAT);
            this.bufPolyColor = new VertexBuffer(this, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
            this.bufPolyNormal = new VertexBuffer(this, PGL.ARRAY_BUFFER, 3, PGL.SIZEOF_FLOAT);
            this.bufPolyTexcoord = new VertexBuffer(this, PGL.ARRAY_BUFFER, 2, PGL.SIZEOF_FLOAT);
            this.bufPolyAmbient = new VertexBuffer(this, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
            this.bufPolySpecular = new VertexBuffer(this, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
            this.bufPolyEmissive = new VertexBuffer(this, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
            this.bufPolyShininess = new VertexBuffer(this, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_FLOAT);
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
            this.bufPolyIndex = new VertexBuffer(this, PGL.ELEMENT_ARRAY_BUFFER, 1, PGL.SIZEOF_INDEX, true);
            this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
            this.polyBuffersCreated = true;
        }

        boolean created = false;
        Iterator var2 = this.polyAttribs.keySet().iterator();

        while(true) {
            PGraphicsOpenGL.VertexAttribute attrib;
            do {
                if (!var2.hasNext()) {
                    if (created) {
                        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
                    }

                    return;
                }

                String name = (String)var2.next();
                attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
            } while(attrib.bufferCreated() && !this.polyBuffersContextIsOutdated());

            attrib.createBuffer(this.pgl);
            created = true;
        }
    }

    protected void updatePolyBuffers(boolean lit, boolean tex, boolean needNormals, boolean needTexCoords) {
        this.createPolyBuffers();
        int size = this.tessGeo.polyVertexCount;
        int sizef = size * PGL.SIZEOF_FLOAT;
        int sizei = size * PGL.SIZEOF_INT;
        this.tessGeo.updatePolyVerticesBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyVertex.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.polyVerticesBuffer, PGL.STATIC_DRAW);
        this.tessGeo.updatePolyColorsBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyColor.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polyColorsBuffer, PGL.STATIC_DRAW);
        if (lit) {
            this.tessGeo.updatePolyAmbientBuffer();
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyAmbient.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polyAmbientBuffer, PGL.STATIC_DRAW);
            this.tessGeo.updatePolySpecularBuffer();
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolySpecular.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polySpecularBuffer, PGL.STATIC_DRAW);
            this.tessGeo.updatePolyEmissiveBuffer();
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyEmissive.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polyEmissiveBuffer, PGL.STATIC_DRAW);
            this.tessGeo.updatePolyShininessBuffer();
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyShininess.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, sizef, this.tessGeo.polyShininessBuffer, PGL.STATIC_DRAW);
        }

        if (lit || needNormals) {
            this.tessGeo.updatePolyNormalsBuffer();
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyNormal.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, 3 * sizef, this.tessGeo.polyNormalsBuffer, PGL.STATIC_DRAW);
        }

        if (tex || needTexCoords) {
            this.tessGeo.updatePolyTexCoordsBuffer();
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyTexcoord.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, 2 * sizef, this.tessGeo.polyTexCoordsBuffer, PGL.STATIC_DRAW);
        }

        Iterator var8 = this.polyAttribs.keySet().iterator();

        while(var8.hasNext()) {
            String name = (String)var8.next();
            PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
            this.tessGeo.updateAttribBuffer(name);
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, attrib.buf.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, attrib.sizeInBytes(size), (Buffer)this.tessGeo.polyAttribBuffers.get(name), PGL.STATIC_DRAW);
        }

        this.tessGeo.updatePolyIndicesBuffer();
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, this.bufPolyIndex.glId);
        this.pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, this.tessGeo.polyIndexCount * PGL.SIZEOF_INDEX, this.tessGeo.polyIndicesBuffer, PGL.STATIC_DRAW);
    }

    protected void unbindPolyBuffers() {
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
    }

    protected boolean polyBuffersContextIsOutdated() {
        return !this.pgl.contextIsCurrent(this.polyBuffersContext);
    }

    protected void createLineBuffers() {
        if (!this.lineBuffersCreated || this.lineBufferContextIsOutdated()) {
            this.lineBuffersContext = this.pgl.getCurrentContext();
            this.bufLineVertex = new VertexBuffer(this, PGL.ARRAY_BUFFER, 3, PGL.SIZEOF_FLOAT);
            this.bufLineColor = new VertexBuffer(this, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
            this.bufLineAttrib = new VertexBuffer(this, PGL.ARRAY_BUFFER, 4, PGL.SIZEOF_FLOAT);
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
            this.bufLineIndex = new VertexBuffer(this, PGL.ELEMENT_ARRAY_BUFFER, 1, PGL.SIZEOF_INDEX, true);
            this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
            this.lineBuffersCreated = true;
        }

    }

    protected void updateLineBuffers() {
        this.createLineBuffers();
        int size = this.tessGeo.lineVertexCount;
        int sizef = size * PGL.SIZEOF_FLOAT;
        int sizei = size * PGL.SIZEOF_INT;
        this.tessGeo.updateLineVerticesBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineVertex.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.lineVerticesBuffer, PGL.STATIC_DRAW);
        this.tessGeo.updateLineColorsBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineColor.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.lineColorsBuffer, PGL.STATIC_DRAW);
        this.tessGeo.updateLineDirectionsBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineAttrib.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.lineDirectionsBuffer, PGL.STATIC_DRAW);
        this.tessGeo.updateLineIndicesBuffer();
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, this.bufLineIndex.glId);
        this.pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, this.tessGeo.lineIndexCount * PGL.SIZEOF_INDEX, this.tessGeo.lineIndicesBuffer, PGL.STATIC_DRAW);
    }

    protected void unbindLineBuffers() {
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
    }

    protected boolean lineBufferContextIsOutdated() {
        return !this.pgl.contextIsCurrent(this.lineBuffersContext);
    }

    protected void createPointBuffers() {
        if (!this.pointBuffersCreated || this.pointBuffersContextIsOutdated()) {
            this.pointBuffersContext = this.pgl.getCurrentContext();
            this.bufPointVertex = new VertexBuffer(this, PGL.ARRAY_BUFFER, 3, PGL.SIZEOF_FLOAT);
            this.bufPointColor = new VertexBuffer(this, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
            this.bufPointAttrib = new VertexBuffer(this, PGL.ARRAY_BUFFER, 2, PGL.SIZEOF_FLOAT);
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
            this.bufPointIndex = new VertexBuffer(this, PGL.ELEMENT_ARRAY_BUFFER, 1, PGL.SIZEOF_INDEX, true);
            this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
            this.pointBuffersCreated = true;
        }

    }

    protected void updatePointBuffers() {
        this.createPointBuffers();
        int size = this.tessGeo.pointVertexCount;
        int sizef = size * PGL.SIZEOF_FLOAT;
        int sizei = size * PGL.SIZEOF_INT;
        this.tessGeo.updatePointVerticesBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointVertex.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.pointVerticesBuffer, PGL.STATIC_DRAW);
        this.tessGeo.updatePointColorsBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointColor.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.pointColorsBuffer, PGL.STATIC_DRAW);
        this.tessGeo.updatePointOffsetsBuffer();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointAttrib.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 2 * sizef, this.tessGeo.pointOffsetsBuffer, PGL.STATIC_DRAW);
        this.tessGeo.updatePointIndicesBuffer();
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, this.bufPointIndex.glId);
        this.pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, this.tessGeo.pointIndexCount * PGL.SIZEOF_INDEX, this.tessGeo.pointIndicesBuffer, PGL.STATIC_DRAW);
    }

    protected void unbindPointBuffers() {
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
    }

    protected boolean pointBuffersContextIsOutdated() {
        return !this.pgl.contextIsCurrent(this.pointBuffersContext);
    }

    public void beginDraw() {
        if (this.primaryGraphics) {
            this.initPrimary();
            this.setCurrentPG(this);
        } else {
            this.pgl.getGL(this.getPrimaryPGL());
            this.getPrimaryPG().setCurrentPG(this);
        }

        this.report("top beginDraw()");
        if (this.checkGLThread()) {
            if (!this.drawing) {
                if (!this.primaryGraphics && this.getPrimaryPG().texCache.containsTexture(this)) {
                    this.getPrimaryPG().flush();
                }

                if (!glParamsRead) {
                    this.getGLParameters();
                }

                this.setViewport();
                if (this.primaryGraphics) {
                    this.beginOnscreenDraw();
                } else {
                    this.beginOffscreenDraw();
                }

                this.checkSettings();
                this.drawing = true;
                this.report("bot beginDraw()");
            }
        }
    }

    public void endDraw() {
        this.report("top endDraw()");
        if (this.drawing) {
            this.flush();
            if (this.primaryGraphics) {
                this.endOnscreenDraw();
            } else {
                this.endOffscreenDraw();
            }

            if (this.primaryGraphics) {
                this.setCurrentPG((PGraphicsOpenGL)null);
            } else {
                this.getPrimaryPG().setCurrentPG();
            }

            this.drawing = false;
            this.report("bot endDraw()");
        }
    }

    protected PGraphicsOpenGL getPrimaryPG() {
        return this.primaryGraphics ? this : (PGraphicsOpenGL)this.parent.g;
    }

    protected void setCurrentPG(PGraphicsOpenGL pg) {
        this.currentPG = pg;
    }

    protected void setCurrentPG() {
        this.currentPG = this;
    }

    protected PGraphicsOpenGL getCurrentPG() {
        return this.currentPG;
    }

    protected PGL getPrimaryPGL() {
        return this.primaryGraphics ? this.pgl : ((PGraphicsOpenGL)this.parent.g).pgl;
    }

    public PGL beginPGL() {
        this.flush();
        this.pgl.beginGL();
        return this.pgl;
    }

    public void endPGL() {
        this.pgl.endGL();
        this.restoreGL();
    }

    public void updateProjmodelview() {
        this.projmodelview.set(this.projection);
        this.projmodelview.apply(this.modelview);
    }

    protected void restartPGL() {
        this.initialized = false;
    }

    protected void restoreGL() {
        this.blendMode(this.blendMode);
        if (this.hints[2]) {
            this.pgl.disable(PGL.DEPTH_TEST);
        } else {
            this.pgl.enable(PGL.DEPTH_TEST);
        }

        this.pgl.depthFunc(PGL.LEQUAL);
        if (this.smooth < 1) {
            this.pgl.disable(PGL.MULTISAMPLE);
        } else {
            this.pgl.enable(PGL.MULTISAMPLE);
            this.pgl.disable(PGL.POLYGON_SMOOTH);
        }

        this.pgl.viewport(this.viewport.get(0), this.viewport.get(1), this.viewport.get(2), this.viewport.get(3));
        if (this.clip) {
            this.pgl.enable(PGL.SCISSOR_TEST);
            this.pgl.scissor(this.clipRect[0], this.clipRect[1], this.clipRect[2], this.clipRect[3]);
        } else {
            this.pgl.disable(PGL.SCISSOR_TEST);
        }

        this.pgl.frontFace(this.cameraUp ? PGL.CCW : PGL.CW);
        this.pgl.disable(PGL.CULL_FACE);
        this.pgl.activeTexture(PGL.TEXTURE0);
        if (this.hints[5]) {
            this.pgl.depthMask(false);
        } else {
            this.pgl.depthMask(true);
        }

        FrameBuffer fb = this.getCurrentFB();
        if (fb != null) {
            fb.bind();
            if (drawBufferSupported) {
                this.pgl.drawBuffer(fb.getDefaultDrawBuffer());
            }
        }

    }

    protected void beginBindFramebuffer(int target, int framebuffer) {
    }

    protected void endBindFramebuffer(int target, int framebuffer) {
        FrameBuffer fb = this.getCurrentFB();
        if (framebuffer == 0 && fb != null && fb.glFbo != 0) {
            fb.bind();
        }

    }

    protected void beginReadPixels() {
        this.beginPixelsOp(1);
    }

    protected void endReadPixels() {
        this.endPixelsOp();
    }

    protected void beginPixelsOp(int op) {
        FrameBuffer pixfb = null;
        FrameBuffer currfb = this.getCurrentFB();
        FrameBuffer ofb;
        FrameBuffer mfb;
        if (this.primaryGraphics) {
            ofb = this.readFramebuffer;
            mfb = this.drawFramebuffer;
            if (currfb == ofb || currfb == mfb) {
                if (op == 1) {
                    if (this.pgl.isFBOBacked() && this.pgl.isMultisampled()) {
                        this.pgl.syncBackTexture();
                        pixfb = ofb;
                    } else {
                        pixfb = mfb;
                    }
                } else if (op == 2) {
                    pixfb = mfb;
                }
            }
        } else {
            ofb = this.offscreenFramebuffer;
            mfb = this.multisampleFramebuffer;
            if (currfb == ofb || currfb == mfb) {
                if (op == 1) {
                    if (this.offscreenMultisample) {
                        int mask = PGL.COLOR_BUFFER_BIT;
                        if (this.hints[10]) {
                            mask |= PGL.DEPTH_BUFFER_BIT | PGL.STENCIL_BUFFER_BIT;
                        }

                        if (ofb != null && mfb != null) {
                            mfb.copy(ofb, mask);
                        }
                    }

                    pixfb = ofb;
                } else if (op == 2) {
                    pixfb = this.offscreenMultisample ? mfb : ofb;
                }
            }
        }

        if (pixfb != null && pixfb != this.getCurrentFB()) {
            this.pushFramebuffer();
            this.setFramebuffer(pixfb);
            this.pixOpChangedFB = true;
        }

        if (op == 1) {
            if (readBufferSupported) {
                this.pgl.readBuffer(this.getCurrentFB().getDefaultDrawBuffer());
            }
        } else if (op == 2 && drawBufferSupported) {
            this.pgl.drawBuffer(this.getCurrentFB().getDefaultDrawBuffer());
        }

        this.pixelsOp = op;
    }

    protected void endPixelsOp() {
        if (this.pixOpChangedFB) {
            this.popFramebuffer();
            this.pixOpChangedFB = false;
        }

        if (readBufferSupported) {
            this.pgl.readBuffer(this.getCurrentFB().getDefaultReadBuffer());
        }

        if (drawBufferSupported) {
            this.pgl.drawBuffer(this.getCurrentFB().getDefaultDrawBuffer());
        }

        this.pixelsOp = 0;
    }

    protected void updateGLProjection() {
        if (this.glProjection == null) {
            this.glProjection = new float[16];
        }

        this.glProjection[0] = this.projection.m00;
        this.glProjection[1] = this.projection.m10;
        this.glProjection[2] = this.projection.m20;
        this.glProjection[3] = this.projection.m30;
        this.glProjection[4] = this.projection.m01;
        this.glProjection[5] = this.projection.m11;
        this.glProjection[6] = this.projection.m21;
        this.glProjection[7] = this.projection.m31;
        this.glProjection[8] = this.projection.m02;
        this.glProjection[9] = this.projection.m12;
        this.glProjection[10] = this.projection.m22;
        this.glProjection[11] = this.projection.m32;
        this.glProjection[12] = this.projection.m03;
        this.glProjection[13] = this.projection.m13;
        this.glProjection[14] = this.projection.m23;
        this.glProjection[15] = this.projection.m33;
    }

    protected void updateGLModelview() {
        if (this.glModelview == null) {
            this.glModelview = new float[16];
        }

        this.glModelview[0] = this.modelview.m00;
        this.glModelview[1] = this.modelview.m10;
        this.glModelview[2] = this.modelview.m20;
        this.glModelview[3] = this.modelview.m30;
        this.glModelview[4] = this.modelview.m01;
        this.glModelview[5] = this.modelview.m11;
        this.glModelview[6] = this.modelview.m21;
        this.glModelview[7] = this.modelview.m31;
        this.glModelview[8] = this.modelview.m02;
        this.glModelview[9] = this.modelview.m12;
        this.glModelview[10] = this.modelview.m22;
        this.glModelview[11] = this.modelview.m32;
        this.glModelview[12] = this.modelview.m03;
        this.glModelview[13] = this.modelview.m13;
        this.glModelview[14] = this.modelview.m23;
        this.glModelview[15] = this.modelview.m33;
    }

    protected void updateGLProjmodelview() {
        if (this.glProjmodelview == null) {
            this.glProjmodelview = new float[16];
        }

        this.glProjmodelview[0] = this.projmodelview.m00;
        this.glProjmodelview[1] = this.projmodelview.m10;
        this.glProjmodelview[2] = this.projmodelview.m20;
        this.glProjmodelview[3] = this.projmodelview.m30;
        this.glProjmodelview[4] = this.projmodelview.m01;
        this.glProjmodelview[5] = this.projmodelview.m11;
        this.glProjmodelview[6] = this.projmodelview.m21;
        this.glProjmodelview[7] = this.projmodelview.m31;
        this.glProjmodelview[8] = this.projmodelview.m02;
        this.glProjmodelview[9] = this.projmodelview.m12;
        this.glProjmodelview[10] = this.projmodelview.m22;
        this.glProjmodelview[11] = this.projmodelview.m32;
        this.glProjmodelview[12] = this.projmodelview.m03;
        this.glProjmodelview[13] = this.projmodelview.m13;
        this.glProjmodelview[14] = this.projmodelview.m23;
        this.glProjmodelview[15] = this.projmodelview.m33;
    }

    protected void updateGLNormal() {
        if (this.glNormal == null) {
            this.glNormal = new float[9];
        }

        this.glNormal[0] = this.modelviewInv.m00;
        this.glNormal[1] = this.modelviewInv.m01;
        this.glNormal[2] = this.modelviewInv.m02;
        this.glNormal[3] = this.modelviewInv.m10;
        this.glNormal[4] = this.modelviewInv.m11;
        this.glNormal[5] = this.modelviewInv.m12;
        this.glNormal[6] = this.modelviewInv.m20;
        this.glNormal[7] = this.modelviewInv.m21;
        this.glNormal[8] = this.modelviewInv.m22;
    }

    protected void defaultSettings() {
        super.defaultSettings();
        this.manipulatingCamera = false;
        this.textureMode(2);
        this.ambient(255);
        this.specular(125);
        this.emissive(0);
        this.shininess(1.0F);
        this.setAmbient = false;
    }

    public void hint(int which) {
        boolean oldValue = this.hints[PApplet.abs(which)];
        super.hint(which);
        boolean newValue = this.hints[PApplet.abs(which)];
        if (oldValue != newValue) {
            if (which == 2) {
                this.flush();
                this.pgl.disable(PGL.DEPTH_TEST);
            } else if (which == -2) {
                this.flush();
                this.pgl.enable(PGL.DEPTH_TEST);
            } else if (which == 5) {
                this.flush();
                this.pgl.depthMask(false);
            } else if (which == -5) {
                this.flush();
                this.pgl.depthMask(true);
            } else if (which == -6) {
                this.flush();
                this.setFlushMode(1);
            } else if (which == 6) {
                if (this.is2D()) {
                    PGraphics.showWarning("Optimized strokes can only be disabled in 3D");
                } else {
                    this.flush();
                    this.setFlushMode(0);
                }
            } else if (which == -7) {
                if (0 < this.tessGeo.lineVertexCount && 0 < this.tessGeo.lineIndexCount) {
                    this.flush();
                }
            } else if (which == 7) {
                if (0 < this.tessGeo.lineVertexCount && 0 < this.tessGeo.lineIndexCount) {
                    this.flush();
                }
            } else if (which == 3) {
                if (this.is3D()) {
                    this.flush();
                    if (this.sorter == null) {
                        this.sorter = new PGraphicsOpenGL.DepthSorter(this);
                    }

                    this.isDepthSortingEnabled = true;
                } else {
                    PGraphics.showWarning("Depth sorting can only be enabled in 3D");
                }
            } else if (which == -3) {
                if (this.is3D()) {
                    this.flush();
                    this.isDepthSortingEnabled = false;
                }
            } else if (which == 10) {
                this.restartPGL();
            } else if (which == -10) {
                this.restartPGL();
            }

        }
    }

    protected boolean getHint(int which) {
        if (which > 0) {
            return this.hints[which];
        } else {
            return !this.hints[-which];
        }
    }

    protected PShape createShapeFamily(int type) {
        PShape shape = new PShapeOpenGL(this, type);
        if (this.is3D()) {
            shape.set3D(true);
        }

        return shape;
    }

    protected PShape createShapePrimitive(int kind, float... p) {
        PShape shape = new PShapeOpenGL(this, kind, p);
        if (this.is3D()) {
            shape.set3D(true);
        }

        return shape;
    }

    public void beginShape(int kind) {
        this.shape = kind;
        this.inGeo.clear();
        this.curveVertexCount = 0;
        this.breakShape = false;
        this.defaultEdges = true;
        super.noTexture();
        this.normalMode = 0;
    }

    public void endShape(int mode) {
        this.tessellate(mode);
        if (this.flushMode != 0 && (this.flushMode != 1 || !this.tessGeo.isFull())) {
            this.loaded = false;
        } else {
            this.flush();
        }

    }

    protected void endShape(int[] indices) {
        if (this.shape != 8 && this.shape != 9) {
            throw new RuntimeException("Indices and edges can only be set for TRIANGLE shapes");
        } else {
            this.tessellate(indices);
            if (this.flushMode != 0 && (this.flushMode != 1 || !this.tessGeo.isFull())) {
                this.loaded = false;
            } else {
                this.flush();
            }

        }
    }

    public void textureWrap(int wrap) {
        this.textureWrap = wrap;
    }

    public void textureSampling(int sampling) {
        this.textureSampling = sampling;
    }

    public void beginContour() {
        if (this.openContour) {
            PGraphics.showWarning("Already called beginContour()");
        } else {
            this.openContour = true;
            this.breakShape = true;
        }
    }

    public void endContour() {
        if (!this.openContour) {
            PGraphics.showWarning("Need to call beginContour() first");
        } else {
            this.openContour = false;
        }
    }

    public void vertex(float x, float y) {
        this.vertexImpl(x, y, 0.0F, 0.0F, 0.0F);
        if (this.textureImage != null) {
            PGraphics.showWarning("No uv texture coordinates supplied with vertex() call");
        }

    }

    public void vertex(float x, float y, float u, float v) {
        this.vertexImpl(x, y, 0.0F, u, v);
    }

    public void vertex(float x, float y, float z) {
        this.vertexImpl(x, y, z, 0.0F, 0.0F);
        if (this.textureImage != null) {
            PGraphics.showWarning("No uv texture coordinates supplied with vertex() call");
        }

    }

    public void vertex(float x, float y, float z, float u, float v) {
        this.vertexImpl(x, y, z, u, v);
    }

    public void attribPosition(String name, float x, float y, float z) {
        PGraphicsOpenGL.VertexAttribute attrib = this.attribImpl(name, 0, PGL.FLOAT, 3);
        if (attrib != null) {
            attrib.set(x, y, z);
        }

    }

    public void attribNormal(String name, float nx, float ny, float nz) {
        PGraphicsOpenGL.VertexAttribute attrib = this.attribImpl(name, 1, PGL.FLOAT, 3);
        if (attrib != null) {
            attrib.set(nx, ny, nz);
        }

    }

    public void attribColor(String name, int color) {
        PGraphicsOpenGL.VertexAttribute attrib = this.attribImpl(name, 2, PGL.INT, 1);
        if (attrib != null) {
            attrib.set(new int[]{color});
        }

    }

    public void attrib(String name, float... values) {
        PGraphicsOpenGL.VertexAttribute attrib = this.attribImpl(name, 3, PGL.FLOAT, values.length);
        if (attrib != null) {
            attrib.set(values);
        }

    }

    public void attrib(String name, int... values) {
        PGraphicsOpenGL.VertexAttribute attrib = this.attribImpl(name, 3, PGL.INT, values.length);
        if (attrib != null) {
            attrib.set(values);
        }

    }

    public void attrib(String name, boolean... values) {
        PGraphicsOpenGL.VertexAttribute attrib = this.attribImpl(name, 3, PGL.BOOL, values.length);
        if (attrib != null) {
            attrib.set(values);
        }

    }

    protected PGraphicsOpenGL.VertexAttribute attribImpl(String name, int kind, int type, int size) {
        if (4 < size) {
            PGraphics.showWarning("Vertex attributes cannot have more than 4 values");
            return null;
        } else {
            PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
            if (attrib == null) {
                attrib = new PGraphicsOpenGL.VertexAttribute(this, name, kind, type, size);
                this.polyAttribs.put(name, attrib);
                this.inGeo.initAttrib(attrib);
                this.tessGeo.initAttrib(attrib);
            }

            if (attrib.kind != kind) {
                PGraphics.showWarning("The attribute kind cannot be changed after creation");
                return null;
            } else if (attrib.type != type) {
                PGraphics.showWarning("The attribute type cannot be changed after creation");
                return null;
            } else if (attrib.size != size) {
                PGraphics.showWarning("New value for vertex attribute has wrong number of values");
                return null;
            } else {
                return attrib;
            }
        }
    }

    protected void vertexImpl(float x, float y, float z, float u, float v) {
        boolean textured = this.textureImage != null;
        int fcolor = 0;
        if (this.fill || textured) {
            if (!textured) {
                fcolor = this.fillColor;
            } else if (this.tint) {
                fcolor = this.tintColor;
            } else {
                fcolor = -1;
            }
        }

        int scolor = 0;
        float sweight = 0.0F;
        if (this.stroke) {
            scolor = this.strokeColor;
            sweight = this.strokeWeight;
        }

        if (textured && this.textureMode == 2) {
            u /= (float)this.textureImage.width;
            v /= (float)this.textureImage.height;
        }

        this.inGeo.addVertex(x, y, z, fcolor, this.normalX, this.normalY, this.normalZ, u, v, scolor, sweight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess, 0, this.vertexBreak());
    }

    protected boolean vertexBreak() {
        if (this.breakShape) {
            this.breakShape = false;
            return true;
        } else {
            return false;
        }
    }

    protected void clipImpl(float x1, float y1, float x2, float y2) {
        this.flush();
        this.pgl.enable(PGL.SCISSOR_TEST);
        float h = y2 - y1;
        this.clipRect[0] = (int)x1;
        this.clipRect[1] = (int)((float)this.height - y1 - h);
        this.clipRect[2] = (int)(x2 - x1);
        this.clipRect[3] = (int)h;
        this.pgl.scissor(this.clipRect[0], this.clipRect[1], this.clipRect[2], this.clipRect[3]);
        this.clip = true;
    }

    public void noClip() {
        if (this.clip) {
            this.flush();
            this.pgl.disable(PGL.SCISSOR_TEST);
            this.clip = false;
        }

    }

    protected void tessellate(int mode) {
        this.tessellator.setInGeometry(this.inGeo);
        this.tessellator.setTessGeometry(this.tessGeo);
        this.tessellator.setFill(this.fill || this.textureImage != null);
        this.tessellator.setTexCache(this.texCache, this.textureImage);
        this.tessellator.setStroke(this.stroke);
        this.tessellator.setStrokeColor(this.strokeColor);
        this.tessellator.setStrokeWeight(this.strokeWeight);
        this.tessellator.setStrokeCap(this.strokeCap);
        this.tessellator.setStrokeJoin(this.strokeJoin);
        this.tessellator.setRenderer(this);
        this.tessellator.setTransform(this.modelview);
        this.tessellator.set3D(this.is3D());
        if (this.shape == 3) {
            this.tessellator.tessellatePoints();
        } else if (this.shape == 5) {
            this.tessellator.tessellateLines();
        } else if (this.shape == 50) {
            this.tessellator.tessellateLineStrip();
        } else if (this.shape == 51) {
            this.tessellator.tessellateLineLoop();
        } else if (this.shape != 8 && this.shape != 9) {
            if (this.shape == 11) {
                if (this.stroke && this.defaultEdges) {
                    this.inGeo.addTriangleFanEdges();
                }

                if (this.normalMode == 0) {
                    this.inGeo.calcTriangleFanNormals();
                }

                this.tessellator.tessellateTriangleFan();
            } else if (this.shape == 10) {
                if (this.stroke && this.defaultEdges) {
                    this.inGeo.addTriangleStripEdges();
                }

                if (this.normalMode == 0) {
                    this.inGeo.calcTriangleStripNormals();
                }

                this.tessellator.tessellateTriangleStrip();
            } else if (this.shape != 16 && this.shape != 17) {
                if (this.shape == 18) {
                    if (this.stroke && this.defaultEdges) {
                        this.inGeo.addQuadStripEdges();
                    }

                    if (this.normalMode == 0) {
                        this.inGeo.calcQuadStripNormals();
                    }

                    this.tessellator.tessellateQuadStrip();
                } else if (this.shape == 20) {
                    this.tessellator.tessellatePolygon(true, mode == 2, this.normalMode == 0);
                }
            } else {
                if (this.stroke && this.defaultEdges) {
                    this.inGeo.addQuadsEdges();
                }

                if (this.normalMode == 0) {
                    this.inGeo.calcQuadsNormals();
                }

                this.tessellator.tessellateQuads();
            }
        } else {
            if (this.stroke && this.defaultEdges) {
                this.inGeo.addTrianglesEdges();
            }

            if (this.normalMode == 0) {
                this.inGeo.calcTrianglesNormals();
            }

            this.tessellator.tessellateTriangles();
        }

    }

    protected void tessellate(int[] indices) {
        this.tessellator.setInGeometry(this.inGeo);
        this.tessellator.setTessGeometry(this.tessGeo);
        this.tessellator.setFill(this.fill || this.textureImage != null);
        this.tessellator.setStroke(this.stroke);
        this.tessellator.setStrokeColor(this.strokeColor);
        this.tessellator.setStrokeWeight(this.strokeWeight);
        this.tessellator.setStrokeCap(this.strokeCap);
        this.tessellator.setStrokeJoin(this.strokeJoin);
        this.tessellator.setTexCache(this.texCache, this.textureImage);
        this.tessellator.setTransform(this.modelview);
        this.tessellator.set3D(this.is3D());
        if (this.stroke && this.defaultEdges) {
            this.inGeo.addTrianglesEdges();
        }

        if (this.normalMode == 0) {
            this.inGeo.calcTrianglesNormals();
        }

        this.tessellator.tessellateTriangles(indices);
    }

    public void flush() {
        boolean hasPolys = 0 < this.tessGeo.polyVertexCount && 0 < this.tessGeo.polyIndexCount;
        boolean hasLines = 0 < this.tessGeo.lineVertexCount && 0 < this.tessGeo.lineIndexCount;
        boolean hasPoints = 0 < this.tessGeo.pointVertexCount && 0 < this.tessGeo.pointIndexCount;
        boolean hasPixels = this.modified && this.pixels != null;
        if (hasPixels) {
            this.flushPixels();
        }

        if (hasPoints || hasLines || hasPolys) {
            PMatrix3D modelview0 = null;
            PMatrix3D modelviewInv0 = null;
            if (this.flushMode == 1) {
                modelview0 = this.modelview;
                modelviewInv0 = this.modelviewInv;
                this.modelview = this.modelviewInv = identity;
                this.projmodelview.set(this.projection);
            }

            if (hasPolys && !this.isDepthSortingEnabled) {
                this.flushPolys();
                if (this.raw != null) {
                    this.rawPolys();
                }
            }

            if (this.is3D()) {
                if (hasLines) {
                    this.flushLines();
                    if (this.raw != null) {
                        this.rawLines();
                    }
                }

                if (hasPoints) {
                    this.flushPoints();
                    if (this.raw != null) {
                        this.rawPoints();
                    }
                }
            }

            if (hasPolys && this.isDepthSortingEnabled) {
                this.flushSortedPolys();
                if (this.raw != null) {
                    this.rawSortedPolys();
                }
            }

            if (this.flushMode == 1) {
                this.modelview = modelview0;
                this.modelviewInv = modelviewInv0;
                this.updateProjmodelview();
            }

            this.loaded = false;
        }

        this.tessGeo.clear();
        this.texCache.clear();
    }

    protected void flushPixels() {
        this.drawPixels(this.mx1, this.my1, this.mx2 - this.mx1, this.my2 - this.my1);
        this.modified = false;
    }

    protected void flushPolys() {
        boolean customShader = this.polyShader != null;
        boolean needNormals = customShader ? this.polyShader.accessNormals() : false;
        boolean needTexCoords = customShader ? this.polyShader.accessTexCoords() : false;
        this.updatePolyBuffers(this.lights, this.texCache.hasTextures, needNormals, needTexCoords);

        for(int i = 0; i < this.texCache.size; ++i) {
            Texture tex = this.texCache.getTexture(i);
            PShader shader = this.getPolyShader(this.lights, tex != null);
            shader.bind();
            int first = this.texCache.firstCache[i];
            int last = this.texCache.lastCache[i];
            PGraphicsOpenGL.IndexCache cache = this.tessGeo.polyIndexCache;

            for(int n = first; n <= last; ++n) {
                int ioffset = n == first ? this.texCache.firstIndex[i] : cache.indexOffset[n];
                int icount = n == last ? this.texCache.lastIndex[i] - ioffset + 1 : cache.indexOffset[n] + cache.indexCount[n] - ioffset;
                int voffset = cache.vertexOffset[n];
                shader.setVertexAttribute(this.bufPolyVertex.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
                shader.setColorAttribute(this.bufPolyColor.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                if (this.lights) {
                    shader.setNormalAttribute(this.bufPolyNormal.glId, 3, PGL.FLOAT, 0, 3 * voffset * PGL.SIZEOF_FLOAT);
                    shader.setAmbientAttribute(this.bufPolyAmbient.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                    shader.setSpecularAttribute(this.bufPolySpecular.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                    shader.setEmissiveAttribute(this.bufPolyEmissive.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                    shader.setShininessAttribute(this.bufPolyShininess.glId, 1, PGL.FLOAT, 0, voffset * PGL.SIZEOF_FLOAT);
                }

                if (this.lights || needNormals) {
                    shader.setNormalAttribute(this.bufPolyNormal.glId, 3, PGL.FLOAT, 0, 3 * voffset * PGL.SIZEOF_FLOAT);
                }

                if (tex != null || needTexCoords) {
                    shader.setTexcoordAttribute(this.bufPolyTexcoord.glId, 2, PGL.FLOAT, 0, 2 * voffset * PGL.SIZEOF_FLOAT);
                    shader.setTexture(tex);
                }

                Iterator var14 = this.polyAttribs.values().iterator();

                while(var14.hasNext()) {
                    PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)var14.next();
                    if (attrib.active(shader)) {
                        attrib.bind(this.pgl);
                        shader.setAttributeVBO(attrib.glLoc, attrib.buf.glId, attrib.tessSize, attrib.type, attrib.isColor(), 0, attrib.sizeInBytes(voffset));
                    }
                }

                shader.draw(this.bufPolyIndex.glId, icount, ioffset);
            }

            Iterator var16 = this.polyAttribs.values().iterator();

            while(var16.hasNext()) {
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)var16.next();
                if (attrib.active(shader)) {
                    attrib.unbind(this.pgl);
                }
            }

            shader.unbind();
        }

        this.unbindPolyBuffers();
    }

    protected void flushSortedPolys() {
        boolean customShader = this.polyShader != null;
        boolean needNormals = customShader ? this.polyShader.accessNormals() : false;
        boolean needTexCoords = customShader ? this.polyShader.accessTexCoords() : false;
        this.sorter.sort(this.tessGeo);
        int triangleCount = this.tessGeo.polyIndexCount / 3;
        int[] texMap = this.sorter.texMap;
        int[] voffsetMap = this.sorter.voffsetMap;
        int[] vertexOffset = this.tessGeo.polyIndexCache.vertexOffset;
        this.updatePolyBuffers(this.lights, this.texCache.hasTextures, needNormals, needTexCoords);
        int ti = 0;

        while(ti < triangleCount) {
            int startTi = ti;
            int texId = texMap[ti];
            int voffsetId = voffsetMap[ti];

            do {
                ++ti;
            } while(ti < triangleCount && texId == texMap[ti] && voffsetId == voffsetMap[ti]);

            Texture tex = this.texCache.getTexture(texId);
            int voffset = vertexOffset[voffsetId];
            int ioffset = 3 * startTi;
            int icount = 3 * (ti - startTi);
            PShader shader = this.getPolyShader(this.lights, tex != null);
            shader.bind();
            shader.setVertexAttribute(this.bufPolyVertex.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.setColorAttribute(this.bufPolyColor.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
            if (this.lights) {
                shader.setNormalAttribute(this.bufPolyNormal.glId, 3, PGL.FLOAT, 0, 3 * voffset * PGL.SIZEOF_FLOAT);
                shader.setAmbientAttribute(this.bufPolyAmbient.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                shader.setSpecularAttribute(this.bufPolySpecular.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                shader.setEmissiveAttribute(this.bufPolyEmissive.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                shader.setShininessAttribute(this.bufPolyShininess.glId, 1, PGL.FLOAT, 0, voffset * PGL.SIZEOF_FLOAT);
            }

            if (this.lights || needNormals) {
                shader.setNormalAttribute(this.bufPolyNormal.glId, 3, PGL.FLOAT, 0, 3 * voffset * PGL.SIZEOF_FLOAT);
            }

            if (tex != null || needTexCoords) {
                shader.setTexcoordAttribute(this.bufPolyTexcoord.glId, 2, PGL.FLOAT, 0, 2 * voffset * PGL.SIZEOF_FLOAT);
                shader.setTexture(tex);
            }

            Iterator var18 = this.polyAttribs.values().iterator();

            PGraphicsOpenGL.VertexAttribute attrib;
            while(var18.hasNext()) {
                attrib = (PGraphicsOpenGL.VertexAttribute)var18.next();
                if (attrib.active(shader)) {
                    attrib.bind(this.pgl);
                    shader.setAttributeVBO(attrib.glLoc, attrib.buf.glId, attrib.tessSize, attrib.type, attrib.isColor(), 0, attrib.sizeInBytes(voffset));
                }
            }

            shader.draw(this.bufPolyIndex.glId, icount, ioffset);
            var18 = this.polyAttribs.values().iterator();

            while(var18.hasNext()) {
                attrib = (PGraphicsOpenGL.VertexAttribute)var18.next();
                if (attrib.active(shader)) {
                    attrib.unbind(this.pgl);
                }
            }

            shader.unbind();
        }

        this.unbindPolyBuffers();
    }

    void rawPolys() {
        this.raw.colorMode(1);
        this.raw.noStroke();
        this.raw.beginShape(9);
        float[] vertices = this.tessGeo.polyVertices;
        int[] color = this.tessGeo.polyColors;
        float[] uv = this.tessGeo.polyTexCoords;
        short[] indices = this.tessGeo.polyIndices;

        for(int i = 0; i < this.texCache.size; ++i) {
            PImage textureImage = this.texCache.getTextureImage(i);
            int first = this.texCache.firstCache[i];
            int last = this.texCache.lastCache[i];
            PGraphicsOpenGL.IndexCache cache = this.tessGeo.polyIndexCache;

            for(int n = first; n <= last; ++n) {
                int ioffset = n == first ? this.texCache.firstIndex[i] : cache.indexOffset[n];
                int icount = n == last ? this.texCache.lastIndex[i] - ioffset + 1 : cache.indexOffset[n] + cache.indexCount[n] - ioffset;
                int voffset = cache.vertexOffset[n];

                for(int tr = ioffset / 3; tr < (ioffset + icount) / 3; ++tr) {
                    int i0 = voffset + indices[3 * tr + 0];
                    int i1 = voffset + indices[3 * tr + 1];
                    int i2 = voffset + indices[3 * tr + 2];
                    float[] pt0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    float[] pt1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    float[] pt2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    int argb0 = PGL.nativeToJavaARGB(color[i0]);
                    int argb1 = PGL.nativeToJavaARGB(color[i1]);
                    int argb2 = PGL.nativeToJavaARGB(color[i2]);
                    if (this.flushMode == 0) {
                        float[] src0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                        float[] src1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                        float[] src2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                        PApplet.arrayCopy(vertices, 4 * i0, src0, 0, 4);
                        PApplet.arrayCopy(vertices, 4 * i1, src1, 0, 4);
                        PApplet.arrayCopy(vertices, 4 * i2, src2, 0, 4);
                        this.modelview.mult(src0, pt0);
                        this.modelview.mult(src1, pt1);
                        this.modelview.mult(src2, pt2);
                    } else {
                        PApplet.arrayCopy(vertices, 4 * i0, pt0, 0, 4);
                        PApplet.arrayCopy(vertices, 4 * i1, pt1, 0, 4);
                        PApplet.arrayCopy(vertices, 4 * i2, pt2, 0, 4);
                    }

                    float sy1;
                    float sx2;
                    float sy2;
                    float sx0;
                    float sy0;
                    float sx1;
                    if (textureImage != null) {
                        this.raw.texture(textureImage);
                        if (this.raw.is3D()) {
                            this.raw.fill(argb0);
                            this.raw.vertex(pt0[0], pt0[1], pt0[2], uv[2 * i0 + 0], uv[2 * i0 + 1]);
                            this.raw.fill(argb1);
                            this.raw.vertex(pt1[0], pt1[1], pt1[2], uv[2 * i1 + 0], uv[2 * i1 + 1]);
                            this.raw.fill(argb2);
                            this.raw.vertex(pt2[0], pt2[1], pt2[2], uv[2 * i2 + 0], uv[2 * i2 + 1]);
                        } else if (this.raw.is2D()) {
                            sx0 = this.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                            sy0 = this.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                            sx1 = this.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                            sy1 = this.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                            sx2 = this.screenXImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                            sy2 = this.screenYImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                            this.raw.fill(argb0);
                            this.raw.vertex(sx0, sy0, uv[2 * i0 + 0], uv[2 * i0 + 1]);
                            this.raw.fill(argb1);
                            this.raw.vertex(sx1, sy1, uv[2 * i1 + 0], uv[2 * i1 + 1]);
                            this.raw.fill(argb1);
                            this.raw.vertex(sx2, sy2, uv[2 * i2 + 0], uv[2 * i2 + 1]);
                        }
                    } else if (this.raw.is3D()) {
                        this.raw.fill(argb0);
                        this.raw.vertex(pt0[0], pt0[1], pt0[2]);
                        this.raw.fill(argb1);
                        this.raw.vertex(pt1[0], pt1[1], pt1[2]);
                        this.raw.fill(argb2);
                        this.raw.vertex(pt2[0], pt2[1], pt2[2]);
                    } else if (this.raw.is2D()) {
                        sx0 = this.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        sy0 = this.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        sx1 = this.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        sy1 = this.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        sx2 = this.screenXImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                        sy2 = this.screenYImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                        this.raw.fill(argb0);
                        this.raw.vertex(sx0, sy0);
                        this.raw.fill(argb1);
                        this.raw.vertex(sx1, sy1);
                        this.raw.fill(argb2);
                        this.raw.vertex(sx2, sy2);
                    }
                }
            }
        }

        this.raw.endShape();
    }

    void rawSortedPolys() {
        this.raw.colorMode(1);
        this.raw.noStroke();
        this.raw.beginShape(9);
        float[] vertices = this.tessGeo.polyVertices;
        int[] color = this.tessGeo.polyColors;
        float[] uv = this.tessGeo.polyTexCoords;
        short[] indices = this.tessGeo.polyIndices;
        this.sorter.sort(this.tessGeo);
        int[] triangleIndices = this.sorter.triangleIndices;
        int[] texMap = this.sorter.texMap;
        int[] voffsetMap = this.sorter.voffsetMap;
        int[] vertexOffset = this.tessGeo.polyIndexCache.vertexOffset;

        for(int i = 0; i < this.tessGeo.polyIndexCount / 3; ++i) {
            int ti = triangleIndices[i];
            PImage tex = this.texCache.getTextureImage(texMap[ti]);
            int voffset = vertexOffset[voffsetMap[ti]];
            int i0 = voffset + indices[3 * ti + 0];
            int i1 = voffset + indices[3 * ti + 1];
            int i2 = voffset + indices[3 * ti + 2];
            float[] pt0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
            float[] pt1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
            float[] pt2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
            int argb0 = PGL.nativeToJavaARGB(color[i0]);
            int argb1 = PGL.nativeToJavaARGB(color[i1]);
            int argb2 = PGL.nativeToJavaARGB(color[i2]);
            if (this.flushMode == 0) {
                float[] src0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                float[] src1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                float[] src2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                PApplet.arrayCopy(vertices, 4 * i0, src0, 0, 4);
                PApplet.arrayCopy(vertices, 4 * i1, src1, 0, 4);
                PApplet.arrayCopy(vertices, 4 * i2, src2, 0, 4);
                this.modelview.mult(src0, pt0);
                this.modelview.mult(src1, pt1);
                this.modelview.mult(src2, pt2);
            } else {
                PApplet.arrayCopy(vertices, 4 * i0, pt0, 0, 4);
                PApplet.arrayCopy(vertices, 4 * i1, pt1, 0, 4);
                PApplet.arrayCopy(vertices, 4 * i2, pt2, 0, 4);
            }

            float sy1;
            float sx2;
            float sy2;
            float sx0;
            float sy0;
            float sx1;
            if (tex != null) {
                this.raw.texture(tex);
                if (this.raw.is3D()) {
                    this.raw.fill(argb0);
                    this.raw.vertex(pt0[0], pt0[1], pt0[2], uv[2 * i0 + 0], uv[2 * i0 + 1]);
                    this.raw.fill(argb1);
                    this.raw.vertex(pt1[0], pt1[1], pt1[2], uv[2 * i1 + 0], uv[2 * i1 + 1]);
                    this.raw.fill(argb2);
                    this.raw.vertex(pt2[0], pt2[1], pt2[2], uv[2 * i2 + 0], uv[2 * i2 + 1]);
                } else if (this.raw.is2D()) {
                    sx0 = this.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    sy0 = this.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    sx1 = this.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                    sy1 = this.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                    sx2 = this.screenXImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                    sy2 = this.screenYImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                    this.raw.fill(argb0);
                    this.raw.vertex(sx0, sy0, uv[2 * i0 + 0], uv[2 * i0 + 1]);
                    this.raw.fill(argb1);
                    this.raw.vertex(sx1, sy1, uv[2 * i1 + 0], uv[2 * i1 + 1]);
                    this.raw.fill(argb1);
                    this.raw.vertex(sx2, sy2, uv[2 * i2 + 0], uv[2 * i2 + 1]);
                }
            } else if (this.raw.is3D()) {
                this.raw.fill(argb0);
                this.raw.vertex(pt0[0], pt0[1], pt0[2]);
                this.raw.fill(argb1);
                this.raw.vertex(pt1[0], pt1[1], pt1[2]);
                this.raw.fill(argb2);
                this.raw.vertex(pt2[0], pt2[1], pt2[2]);
            } else if (this.raw.is2D()) {
                sx0 = this.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                sy0 = this.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                sx1 = this.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                sy1 = this.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                sx2 = this.screenXImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                sy2 = this.screenYImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                this.raw.fill(argb0);
                this.raw.vertex(sx0, sy0);
                this.raw.fill(argb1);
                this.raw.vertex(sx1, sy1);
                this.raw.fill(argb2);
                this.raw.vertex(sx2, sy2);
            }
        }

        this.raw.endShape();
    }

    protected void flushLines() {
        this.updateLineBuffers();
        PShader shader = this.getLineShader();
        shader.bind();
        PGraphicsOpenGL.IndexCache cache = this.tessGeo.lineIndexCache;

        for(int n = 0; n < cache.size; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];
            shader.setVertexAttribute(this.bufLineVertex.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.setColorAttribute(this.bufLineColor.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
            shader.setLineAttribute(this.bufLineAttrib.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.draw(this.bufLineIndex.glId, icount, ioffset);
        }

        shader.unbind();
        this.unbindLineBuffers();
    }

    void rawLines() {
        this.raw.colorMode(1);
        this.raw.noFill();
        this.raw.strokeCap(this.strokeCap);
        this.raw.strokeJoin(this.strokeJoin);
        this.raw.beginShape(5);
        float[] vertices = this.tessGeo.lineVertices;
        int[] color = this.tessGeo.lineColors;
        float[] attribs = this.tessGeo.lineDirections;
        short[] indices = this.tessGeo.lineIndices;
        PGraphicsOpenGL.IndexCache cache = this.tessGeo.lineIndexCache;

        for(int n = 0; n < cache.size; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];

            for(int ln = ioffset / 6; ln < (ioffset + icount) / 6; ++ln) {
                int i0 = voffset + indices[6 * ln + 0];
                int i1 = voffset + indices[6 * ln + 5];
                float sw0 = 2.0F * attribs[4 * i0 + 3];
                float sw1 = 2.0F * attribs[4 * i1 + 3];
                if (!zero(sw0)) {
                    float[] pt0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    float[] pt1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    int argb0 = PGL.nativeToJavaARGB(color[i0]);
                    int argb1 = PGL.nativeToJavaARGB(color[i1]);
                    if (this.flushMode == 0) {
                        float[] src0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                        float[] src1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                        PApplet.arrayCopy(vertices, 4 * i0, src0, 0, 4);
                        PApplet.arrayCopy(vertices, 4 * i1, src1, 0, 4);
                        this.modelview.mult(src0, pt0);
                        this.modelview.mult(src1, pt1);
                    } else {
                        PApplet.arrayCopy(vertices, 4 * i0, pt0, 0, 4);
                        PApplet.arrayCopy(vertices, 4 * i1, pt1, 0, 4);
                    }

                    if (this.raw.is3D()) {
                        this.raw.strokeWeight(sw0);
                        this.raw.stroke(argb0);
                        this.raw.vertex(pt0[0], pt0[1], pt0[2]);
                        this.raw.strokeWeight(sw1);
                        this.raw.stroke(argb1);
                        this.raw.vertex(pt1[0], pt1[1], pt1[2]);
                    } else if (this.raw.is2D()) {
                        float sx0 = this.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        float sy0 = this.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        float sx1 = this.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        float sy1 = this.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        this.raw.strokeWeight(sw0);
                        this.raw.stroke(argb0);
                        this.raw.vertex(sx0, sy0);
                        this.raw.strokeWeight(sw1);
                        this.raw.stroke(argb1);
                        this.raw.vertex(sx1, sy1);
                    }
                }
            }
        }

        this.raw.endShape();
    }

    protected void flushPoints() {
        this.updatePointBuffers();
        PShader shader = this.getPointShader();
        shader.bind();
        PGraphicsOpenGL.IndexCache cache = this.tessGeo.pointIndexCache;

        for(int n = 0; n < cache.size; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];
            shader.setVertexAttribute(this.bufPointVertex.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.setColorAttribute(this.bufPointColor.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
            shader.setPointAttribute(this.bufPointAttrib.glId, 2, PGL.FLOAT, 0, 2 * voffset * PGL.SIZEOF_FLOAT);
            shader.draw(this.bufPointIndex.glId, icount, ioffset);
        }

        shader.unbind();
        this.unbindPointBuffers();
    }

    void rawPoints() {
        this.raw.colorMode(1);
        this.raw.noFill();
        this.raw.strokeCap(this.strokeCap);
        this.raw.beginShape(3);
        float[] vertices = this.tessGeo.pointVertices;
        int[] color = this.tessGeo.pointColors;
        float[] attribs = this.tessGeo.pointOffsets;
        short[] indices = this.tessGeo.pointIndices;
        PGraphicsOpenGL.IndexCache cache = this.tessGeo.pointIndexCache;

        for(int n = 0; n < cache.size; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];

            int perim;
            for(int pt = ioffset; pt < (ioffset + icount) / 3; pt += perim) {
                float size = attribs[2 * pt + 2];
                float weight;
                if (0.0F < size) {
                    weight = size / 0.5F;
                    perim = PApplet.min(200, PApplet.max(20, (int)(6.2831855F * weight / 10.0F))) + 1;
                } else {
                    weight = -size / 0.5F;
                    perim = 5;
                }

                int i0 = voffset + indices[3 * pt];
                int argb0 = PGL.nativeToJavaARGB(color[i0]);
                float[] pt0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                if (this.flushMode == 0) {
                    float[] src0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    PApplet.arrayCopy(vertices, 4 * i0, src0, 0, 4);
                    this.modelview.mult(src0, pt0);
                } else {
                    PApplet.arrayCopy(vertices, 4 * i0, pt0, 0, 4);
                }

                if (this.raw.is3D()) {
                    this.raw.strokeWeight(weight);
                    this.raw.stroke(argb0);
                    this.raw.vertex(pt0[0], pt0[1], pt0[2]);
                } else if (this.raw.is2D()) {
                    float sx0 = this.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    float sy0 = this.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    this.raw.strokeWeight(weight);
                    this.raw.stroke(argb0);
                    this.raw.vertex(sx0, sy0);
                }
            }
        }

        this.raw.endShape();
    }

    public void bezierVertex(float x2, float y2, float x3, float y3, float x4, float y4) {
        this.bezierVertexImpl(x2, y2, 0.0F, x3, y3, 0.0F, x4, y4, 0.0F);
    }

    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.bezierVertexImpl(x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    protected void bezierVertexImpl(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.bezierVertexCheck(this.shape, this.inGeo.vertexCount);
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addBezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4, this.vertexBreak());
    }

    public void quadraticVertex(float cx, float cy, float x3, float y3) {
        this.quadraticVertexImpl(cx, cy, 0.0F, x3, y3, 0.0F);
    }

    public void quadraticVertex(float cx, float cy, float cz, float x3, float y3, float z3) {
        this.quadraticVertexImpl(cx, cy, cz, x3, y3, z3);
    }

    protected void quadraticVertexImpl(float cx, float cy, float cz, float x3, float y3, float z3) {
        this.bezierVertexCheck(this.shape, this.inGeo.vertexCount);
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addQuadraticVertex(cx, cy, cz, x3, y3, z3, this.vertexBreak());
    }

    public void curveVertex(float x, float y) {
        this.curveVertexImpl(x, y, 0.0F);
    }

    public void curveVertex(float x, float y, float z) {
        this.curveVertexImpl(x, y, z);
    }

    protected void curveVertexImpl(float x, float y, float z) {
        this.curveVertexCheck(this.shape);
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addCurveVertex(x, y, z, this.vertexBreak());
    }

    public void point(float x, float y) {
        this.pointImpl(x, y, 0.0F);
    }

    public void point(float x, float y, float z) {
        this.pointImpl(x, y, z);
    }

    protected void pointImpl(float x, float y, float z) {
        this.beginShape(3);
        this.defaultEdges = false;
        this.normalMode = 1;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addPoint(x, y, z, this.fill, this.stroke);
        this.endShape();
    }

    public void line(float x1, float y1, float x2, float y2) {
        this.lineImpl(x1, y1, 0.0F, x2, y2, 0.0F);
    }

    public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.lineImpl(x1, y1, z1, x2, y2, z2);
    }

    protected void lineImpl(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.beginShape(5);
        this.defaultEdges = false;
        this.normalMode = 1;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addLine(x1, y1, z1, x2, y2, z2, this.fill, this.stroke);
        this.endShape();
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.beginShape(9);
        this.defaultEdges = false;
        this.normalMode = 1;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addTriangle(x1, y1, 0.0F, x2, y2, 0.0F, x3, y3, 0.0F, this.fill, this.stroke);
        this.endShape();
    }

    public void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.beginShape(17);
        this.defaultEdges = false;
        this.normalMode = 1;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addQuad(x1, y1, 0.0F, x2, y2, 0.0F, x3, y3, 0.0F, x4, y4, 0.0F, this.stroke);
        this.endShape();
    }

    protected void rectImpl(float x1, float y1, float x2, float y2, float tl, float tr, float br, float bl) {
        this.beginShape(20);
        this.defaultEdges = false;
        this.normalMode = 1;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addRect(x1, y1, x2, y2, tl, tr, br, bl, this.stroke);
        this.endShape(2);
    }

    public void ellipseImpl(float a, float b, float c, float d) {
        this.beginShape(11);
        this.defaultEdges = false;
        this.normalMode = 1;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addEllipse(a, b, c, d, this.fill, this.stroke);
        this.endShape();
    }

    protected void arcImpl(float x, float y, float w, float h, float start, float stop, int mode) {
        this.beginShape(11);
        this.defaultEdges = false;
        this.normalMode = 1;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addArc(x, y, w, h, start, stop, this.fill, this.stroke, mode);
        this.endShape();
    }

    public void box(float w, float h, float d) {
        this.beginShape(17);
        this.defaultEdges = false;
        this.normalMode = 2;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.addBox(w, h, d, this.fill, this.stroke);
        this.endShape();
    }

    public void sphere(float r) {
        if (this.sphereDetailU < 3 || this.sphereDetailV < 2) {
            this.sphereDetail(30);
        }

        this.beginShape(9);
        this.defaultEdges = false;
        this.normalMode = 2;
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        int[] indices = this.inGeo.addSphere(r, this.sphereDetailU, this.sphereDetailV, this.fill, this.stroke);
        this.endShape(indices);
    }

    protected void shape(PShape shape, float x, float y, float z) {
        if (shape.isVisible()) {
            this.flush();
            this.pushMatrix();
            if (this.shapeMode == 3) {
                this.translate(x - shape.getWidth() / 2.0F, y - shape.getHeight() / 2.0F, z - shape.getDepth() / 2.0F);
            } else if (this.shapeMode == 0 || this.shapeMode == 1) {
                this.translate(x, y, z);
            }

            shape.draw(this);
            this.popMatrix();
        }

    }

    protected void shape(PShape shape, float x, float y, float z, float c, float d, float e) {
        if (shape.isVisible()) {
            this.flush();
            this.pushMatrix();
            if (this.shapeMode == 3) {
                this.translate(x - c / 2.0F, y - d / 2.0F, z - e / 2.0F);
                this.scale(c / shape.getWidth(), d / shape.getHeight(), e / shape.getDepth());
            } else if (this.shapeMode == 0) {
                this.translate(x, y, z);
                this.scale(c / shape.getWidth(), d / shape.getHeight(), e / shape.getDepth());
            } else if (this.shapeMode == 1) {
                c -= x;
                d -= y;
                e -= z;
                this.translate(x, y, z);
                this.scale(c / shape.getWidth(), d / shape.getHeight(), e / shape.getDepth());
            }

            shape.draw(this);
            this.popMatrix();
        }

    }

    public PShape loadShape(String filename) {
        String ext = PApplet.getExtension(filename);
        if (PGraphics2D.isSupportedExtension(ext)) {
            return PGraphics2D.loadShapeImpl(this, filename, ext);
        } else if (PGraphics3D.isSupportedExtension(ext)) {
            return PGraphics3D.loadShapeImpl(this, filename, ext);
        } else {
            PGraphics.showWarning("Unsupported shape format");
            return null;
        }
    }

    protected boolean textModeCheck(int mode) {
        return mode == 4 || mode == 5 && PGL.SHAPE_TEXT_SUPPORTED;
    }

    public void text(char c, float x, float y) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }

        int sign = this.cameraUp ? -1 : 1;
        if (this.textAlignY == 3) {
            y += (float)sign * this.textAscent() / 2.0F;
        } else if (this.textAlignY == 101) {
            y += (float)sign * this.textAscent();
        } else if (this.textAlignY == 102) {
            y -= (float)sign * this.textDescent();
        }

        this.textBuffer[0] = c;
        this.textLineAlignImpl(this.textBuffer, 0, 1, x, y);
    }

    public void text(String str, float x, float y) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }

        int sign = this.cameraUp ? -1 : 1;
        int length = str.length();
        if (length > this.textBuffer.length) {
            this.textBuffer = new char[length + 10];
        }

        str.getChars(0, length, this.textBuffer, 0);
        float high = 0.0F;

        int start;
        for(start = 0; start < length; ++start) {
            if (this.textBuffer[start] == '\n') {
                high += (float)sign * this.textLeading;
            }
        }

        if (this.textAlignY == 3) {
            y += (float)sign * (this.textAscent() - high) / 2.0F;
        } else if (this.textAlignY == 101) {
            y += (float)sign * this.textAscent();
        } else if (this.textAlignY == 102) {
            y -= (float)sign * this.textDescent() + high;
        }

        start = 0;

        int index;
        for(index = 0; index < length; ++index) {
            if (this.textBuffer[index] == '\n') {
                this.textLineAlignImpl(this.textBuffer, start, index, x, y);
                start = index + 1;
                y += (float)sign * this.textLeading;
            }
        }

        if (start < length) {
            this.textLineAlignImpl(this.textBuffer, start, index, x, y);
        }

    }

    public void text(String str, float x1, float y1, float x2, float y2) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }

        int sign = this.cameraUp ? -1 : 1;
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
            float y = this.cameraUp ? y2 - this.textAscent() - (boxHeight - y) / 2.0F : y1 + this.textAscent() + (boxHeight - y) / 2.0F;

            for(int i = 0; i < lineCount; ++i) {
                this.textLineAlignImpl(this.textBuffer, this.textBreakStart[i], this.textBreakStop[i], lineX, y);
                y += (float)sign * this.textLeading;
            }
        } else {
            int i;
            if (this.textAlignY == 102) {
                y = this.cameraUp ? y1 + this.textDescent() + this.textLeading * (float)(lineCount - 1) : y2 - this.textDescent() - this.textLeading * (float)(lineCount - 1);

                for(i = 0; i < lineCount; ++i) {
                    this.textLineAlignImpl(this.textBuffer, this.textBreakStart[i], this.textBreakStop[i], lineX, y);
                    y += (float)sign * this.textLeading;
                }
            } else {
                y = this.cameraUp ? y2 - this.textAscent() : y1 + this.textAscent();

                for(i = 0; i < lineCount; ++i) {
                    this.textLineAlignImpl(this.textBuffer, this.textBreakStart[i], this.textBreakStop[i], lineX, y);
                    y += (float)sign * this.textLeading;
                }
            }
        }

    }

    public float textAscent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textAscent");
        }

        Object font = this.textFont.getNative();
        float ascent = 0.0F;
        if (font != null) {
            ascent = (float)this.pgl.getFontAscent(font);
        }

        if (ascent == 0.0F) {
            ascent = super.textAscent();
        }

        return ascent;
    }

    public float textDescent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textDescent");
        }

        Object font = this.textFont.getNative();
        float descent = 0.0F;
        if (font != null) {
            descent = (float)this.pgl.getFontDescent(font);
        }

        if (descent == 0.0F) {
            descent = super.textDescent();
        }

        return descent;
    }

    protected float textWidthImpl(char[] buffer, int start, int stop) {
        Object font = this.textFont.getNative();
        float twidth = 0.0F;
        if (font != null) {
            twidth = (float)this.pgl.getTextWidth(font, buffer, start, stop);
        }

        if (twidth == 0.0F) {
            twidth = super.textWidthImpl(buffer, start, stop);
        }

        return twidth;
    }

    protected void handleTextSize(float size) {
        Object font = this.textFont.getNative();
        if (font != null) {
            Object dfont = this.pgl.getDerivedFont(font, size);
            if (dfont != null) {
                this.textFont.setNative(dfont);
            }
        }

        super.handleTextSize(size);
    }

    protected void textLineImpl(char[] buffer, int start, int stop, float x, float y) {
        if (this.textMode == 4) {
            this.textTex = this.getFontTexture(this.textFont);
            if (this.textTex == null || this.textTex.contextIsOutdated()) {
                this.textTex = new FontTexture(this, this.textFont, this.is3D());
                this.setFontTexture(this.textFont, this.textTex);
            }

            this.textTex.begin();
            int savedTextureMode = this.textureMode;
            boolean savedStroke = this.stroke;
            float savedNormalX = this.normalX;
            float savedNormalY = this.normalY;
            float savedNormalZ = this.normalZ;
            boolean savedTint = this.tint;
            int savedTintColor = this.tintColor;
            int savedBlendMode = this.blendMode;
            this.textureMode = 1;
            this.stroke = false;
            this.normalX = 0.0F;
            this.normalY = 0.0F;
            this.normalZ = 1.0F;
            this.tint = true;
            this.tintColor = this.fillColor;
            this.blendMode(1);
            super.textLineImpl(buffer, start, stop, x, y);
            this.textureMode = savedTextureMode;
            this.stroke = savedStroke;
            this.normalX = savedNormalX;
            this.normalY = savedNormalY;
            this.normalZ = savedNormalZ;
            this.tint = savedTint;
            this.tintColor = savedTintColor;
            this.blendMode(savedBlendMode);
            this.textTex.end();
        } else if (this.textMode == 5) {
            super.textLineImpl(buffer, start, stop, x, y);
        }

    }

    protected void textCharImpl(char ch, float x, float y) {
        Glyph glyph = this.textFont.getGlyph(ch);
        if (glyph != null) {
            if (this.textMode == 4) {
                TextureInfo tinfo = this.textTex.getTexInfo(glyph);
                if (tinfo == null) {
                    tinfo = this.textTex.addToTexture(this, glyph);
                }

                float high = (float)glyph.height / (float)this.textFont.getSize();
                float bwidth = (float)glyph.width / (float)this.textFont.getSize();
                float lextent = (float)glyph.leftExtent / (float)this.textFont.getSize();
                float textent = (float)glyph.topExtent / (float)this.textFont.getSize();
                int sign = this.cameraUp ? -1 : 1;
                float x1 = x + lextent * this.textSize;
                float y1 = y - (float)sign * textent * this.textSize;
                float x2 = x1 + bwidth * this.textSize;
                float y2 = y1 + (float)sign * high * this.textSize;
                this.textCharModelImpl(tinfo, x1, y1, x2, y2);
            } else if (this.textMode == 5) {
                this.textCharShapeImpl(ch, x, y);
            }
        }

    }

    protected void textCharModelImpl(TextureInfo info, float x0, float y0, float x1, float y1) {
        this.beginShape(17);
        this.texture(this.textTex.getTexture(info));
        this.vertex(x0, y0, info.u0, info.v0);
        this.vertex(x1, y0, info.u1, info.v0);
        this.vertex(x1, y1, info.u1, info.v1);
        this.vertex(x0, y1, info.u0, info.v1);
        this.endShape();
    }

    protected void textCharShapeImpl(char ch, float x, float y) {
        boolean strokeSaved = this.stroke;
        this.stroke = false;
        FontOutline outline = this.pgl.createFontOutline(ch, this.textFont.getNative());
        float[] textPoints = new float[6];
        float lastX = 0.0F;
        float lastY = 0.0F;
        boolean open = false;
        this.beginShape();

        for(; !outline.isDone(); outline.next()) {
            int type = outline.currentSegment(textPoints);
            if (!open) {
                this.beginContour();
                open = true;
            }

            if (type != PGL.SEG_MOVETO && type != PGL.SEG_LINETO) {
                int i;
                float t;
                if (type == PGL.SEG_QUADTO) {
                    for(i = 1; i < this.bezierDetail; ++i) {
                        t = (float)i / (float)this.bezierDetail;
                        this.vertex(x + this.bezierPoint(lastX, lastX + (float)((double)((textPoints[0] - lastX) * 2.0F) / 3.0D), textPoints[2] + (float)((double)((textPoints[0] - textPoints[2]) * 2.0F) / 3.0D), textPoints[2], t), y + this.bezierPoint(lastY, lastY + (float)((double)((textPoints[1] - lastY) * 2.0F) / 3.0D), textPoints[3] + (float)((double)((textPoints[1] - textPoints[3]) * 2.0F) / 3.0D), textPoints[3], t));
                    }

                    lastX = textPoints[2];
                    lastY = textPoints[3];
                } else if (type != PGL.SEG_CUBICTO) {
                    if (type == PGL.SEG_CLOSE) {
                        this.endContour();
                        open = false;
                    }
                } else {
                    for(i = 1; i < this.bezierDetail; ++i) {
                        t = (float)i / (float)this.bezierDetail;
                        this.vertex(x + this.bezierPoint(lastX, textPoints[0], textPoints[2], textPoints[4], t), y + this.bezierPoint(lastY, textPoints[1], textPoints[3], textPoints[5], t));
                    }

                    lastX = textPoints[4];
                    lastY = textPoints[5];
                }
            } else {
                this.vertex(x + textPoints[0], y + textPoints[1]);
                lastX = textPoints[0];
                lastY = textPoints[1];
            }
        }

        this.endShape();
        this.stroke = strokeSaved;
    }

    public void pushMatrix() {
        if (this.modelviewStackDepth == 32) {
            throw new RuntimeException("Too many calls to pushMatrix().");
        } else {
            this.modelview.get(this.modelviewStack[this.modelviewStackDepth]);
            this.modelviewInv.get(this.modelviewInvStack[this.modelviewStackDepth]);
            this.camera.get(this.cameraStack[this.modelviewStackDepth]);
            this.cameraInv.get(this.cameraInvStack[this.modelviewStackDepth]);
            ++this.modelviewStackDepth;
        }
    }

    public void popMatrix() {
        if (this.modelviewStackDepth == 0) {
            throw new RuntimeException("Too many calls to popMatrix(), and not enough to pushMatrix().");
        } else {
            --this.modelviewStackDepth;
            this.modelview.set(this.modelviewStack[this.modelviewStackDepth]);
            this.modelviewInv.set(this.modelviewInvStack[this.modelviewStackDepth]);
            this.camera.set(this.cameraStack[this.modelviewStackDepth]);
            this.cameraInv.set(this.cameraInvStack[this.modelviewStackDepth]);
            this.updateProjmodelview();
        }
    }

    public void translate(float tx, float ty) {
        this.translateImpl(tx, ty, 0.0F);
    }

    public void translate(float tx, float ty, float tz) {
        this.translateImpl(tx, ty, tz);
    }

    protected void translateImpl(float tx, float ty, float tz) {
        this.modelview.translate(tx, ty, tz);
        invTranslate(this.modelviewInv, tx, ty, tz);
        this.projmodelview.translate(tx, ty, tz);
    }

    protected static void invTranslate(PMatrix3D matrix, float tx, float ty, float tz) {
        matrix.preApply(1.0F, 0.0F, 0.0F, -tx, 0.0F, 1.0F, 0.0F, -ty, 0.0F, 0.0F, 1.0F, -tz, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected static void invTranslate(PMatrix2D matrix, float tx, float ty) {
        matrix.preApply(1.0F, 0.0F, -tx, 0.0F, 1.0F, -ty);
    }

    protected static float matrixScale(PMatrix matrix) {
        float factor = 1.0F;
        if (matrix != null) {
            float volumeScaleFactor;
            if (matrix instanceof PMatrix2D) {
                PMatrix2D tr = (PMatrix2D)matrix;
                volumeScaleFactor = Math.abs(tr.m00 * tr.m11 - tr.m01 * tr.m10);
                factor = (float)Math.sqrt((double)volumeScaleFactor);
            } else if (matrix instanceof PMatrix3D) {
                PMatrix3D tr = (PMatrix3D)matrix;
                volumeScaleFactor = Math.abs(tr.m00 * (tr.m11 * tr.m22 - tr.m12 * tr.m21) + tr.m01 * (tr.m12 * tr.m20 - tr.m10 * tr.m22) + tr.m02 * (tr.m10 * tr.m21 - tr.m11 * tr.m20));
                factor = (float)Math.pow((double)volumeScaleFactor, 0.3333333432674408D);
            }
        }

        return factor;
    }

    public void rotate(float angle) {
        this.rotateImpl(angle, 0.0F, 0.0F, 1.0F);
    }

    public void rotateX(float angle) {
        this.rotateImpl(angle, 1.0F, 0.0F, 0.0F);
    }

    public void rotateY(float angle) {
        this.rotateImpl(angle, 0.0F, 1.0F, 0.0F);
    }

    public void rotateZ(float angle) {
        this.rotateImpl(angle, 0.0F, 0.0F, 1.0F);
    }

    public void rotate(float angle, float v0, float v1, float v2) {
        this.rotateImpl(angle, v0, v1, v2);
    }

    protected void rotateImpl(float angle, float v0, float v1, float v2) {
        float norm2 = v0 * v0 + v1 * v1 + v2 * v2;
        if (!zero(norm2)) {
            if (diff(norm2, 1.0F)) {
                float norm = PApplet.sqrt(norm2);
                v0 /= norm;
                v1 /= norm;
                v2 /= norm;
            }

            this.modelview.rotate(angle, v0, v1, v2);
            invRotate(this.modelviewInv, angle, v0, v1, v2);
            this.updateProjmodelview();
        }
    }

    protected static void invRotate(PMatrix3D matrix, float angle, float v0, float v1, float v2) {
        float c = PApplet.cos(-angle);
        float s = PApplet.sin(-angle);
        float t = 1.0F - c;
        matrix.preApply(t * v0 * v0 + c, t * v0 * v1 - s * v2, t * v0 * v2 + s * v1, 0.0F, t * v0 * v1 + s * v2, t * v1 * v1 + c, t * v1 * v2 - s * v0, 0.0F, t * v0 * v2 - s * v1, t * v1 * v2 + s * v0, t * v2 * v2 + c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected static void invRotate(PMatrix2D matrix, float angle) {
        matrix.rotate(-angle);
    }

    public void scale(float s) {
        this.scaleImpl(s, s, s);
    }

    public void scale(float sx, float sy) {
        this.scaleImpl(sx, sy, 1.0F);
    }

    public void scale(float sx, float sy, float sz) {
        this.scaleImpl(sx, sy, sz);
    }

    protected void scaleImpl(float sx, float sy, float sz) {
        this.modelview.scale(sx, sy, sz);
        invScale(this.modelviewInv, sx, sy, sz);
        this.projmodelview.scale(sx, sy, sz);
    }

    protected static void invScale(PMatrix3D matrix, float x, float y, float z) {
        matrix.preApply(1.0F / x, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / y, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected static void invScale(PMatrix2D matrix, float x, float y) {
        matrix.preApply(1.0F / x, 0.0F, 0.0F, 1.0F / y, 0.0F, 0.0F);
    }

    public void shearX(float angle) {
        float t = (float)Math.tan((double)angle);
        this.applyMatrixImpl(1.0F, t, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void shearY(float angle) {
        float t = (float)Math.tan((double)angle);
        this.applyMatrixImpl(1.0F, 0.0F, 0.0F, 0.0F, t, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void resetMatrix() {
        this.modelview.reset();
        this.modelviewInv.reset();
        this.projmodelview.set(this.projection);
        this.camera.reset();
        this.cameraInv.reset();
    }

    public void applyMatrix(PMatrix2D source) {
        this.applyMatrixImpl(source.m00, source.m01, 0.0F, source.m02, source.m10, source.m11, 0.0F, source.m12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.applyMatrixImpl(n00, n01, 0.0F, n02, n10, n11, 0.0F, n12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void applyMatrix(PMatrix3D source) {
        this.applyMatrixImpl(source.m00, source.m01, source.m02, source.m03, source.m10, source.m11, source.m12, source.m13, source.m20, source.m21, source.m22, source.m23, source.m30, source.m31, source.m32, source.m33);
    }

    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.applyMatrixImpl(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
    }

    protected void applyMatrixImpl(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.modelview.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
        this.modelviewInv.set(this.modelview);
        this.modelviewInv.invert();
        this.projmodelview.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
    }

    protected void begin2D() {
    }

    protected void end2D() {
    }

    public PMatrix getMatrix() {
        return this.modelview.get();
    }

    public PMatrix3D getMatrix(PMatrix3D target) {
        if (target == null) {
            target = new PMatrix3D();
        }

        target.set(this.modelview);
        return target;
    }

    public void setMatrix(PMatrix2D source) {
        this.resetMatrix();
        this.applyMatrix(source);
    }

    public void setMatrix(PMatrix3D source) {
        this.resetMatrix();
        this.applyMatrix(source);
    }

    public void printMatrix() {
        this.modelview.print();
    }

    public void pushProjection() {
        if (this.projectionStackDepth == 32) {
            throw new RuntimeException("Too many calls to pushMatrix().");
        } else {
            this.projection.get(this.projectionStack[this.projectionStackDepth]);
            ++this.projectionStackDepth;
        }
    }

    public void popProjection() {
        this.flush();
        if (this.projectionStackDepth == 0) {
            throw new RuntimeException("Too many calls to popMatrix(), and not enough to pushMatrix().");
        } else {
            --this.projectionStackDepth;
            this.projection.set(this.projectionStack[this.projectionStackDepth]);
            this.updateProjmodelview();
        }
    }

    public void resetProjection() {
        this.flush();
        this.projection.reset();
        this.updateProjmodelview();
    }

    public void applyProjection(PMatrix3D mat) {
        this.flush();
        this.projection.apply(mat);
        this.updateProjmodelview();
    }

    public void applyProjection(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.flush();
        this.projection.apply(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
        this.updateProjmodelview();
    }

    public void setProjection(PMatrix3D mat) {
        this.flush();
        this.projection.set(mat);
        this.updateProjmodelview();
    }

    protected boolean orthoProjection() {
        return zero(this.projection.m01) && zero(this.projection.m02) && zero(this.projection.m10) && zero(this.projection.m12) && zero(this.projection.m20) && zero(this.projection.m21) && zero(this.projection.m30) && zero(this.projection.m31) && zero(this.projection.m32) && same(this.projection.m33, 1.0F);
    }

    protected boolean nonOrthoProjection() {
        return nonZero(this.projection.m01) || nonZero(this.projection.m02) || nonZero(this.projection.m10) || nonZero(this.projection.m12) || nonZero(this.projection.m20) || nonZero(this.projection.m21) || nonZero(this.projection.m30) || nonZero(this.projection.m31) || nonZero(this.projection.m32) || diff(this.projection.m33, 1.0F);
    }

    protected static boolean same(float a, float b) {
        return Math.abs(a - b) < PGL.FLOAT_EPS;
    }

    protected static boolean diff(float a, float b) {
        return PGL.FLOAT_EPS <= Math.abs(a - b);
    }

    protected static boolean zero(float a) {
        return Math.abs(a) < PGL.FLOAT_EPS;
    }

    protected static boolean nonZero(float a) {
        return PGL.FLOAT_EPS <= Math.abs(a);
    }

    public void beginCamera() {
        if (this.manipulatingCamera) {
            throw new RuntimeException("beginCamera() cannot be called again before endCamera()");
        } else {
            this.manipulatingCamera = true;
        }
    }

    public void endCamera() {
        if (!this.manipulatingCamera) {
            throw new RuntimeException("Cannot call endCamera() without first calling beginCamera()");
        } else {
            this.camera.set(this.modelview);
            this.cameraInv.set(this.modelviewInv);
            this.manipulatingCamera = false;
        }
    }

    public void camera() {
        this.camera(this.defCameraX, this.defCameraY, this.defCameraZ, this.defCameraX, this.defCameraY, 0.0F, 0.0F, 1.0F, 0.0F);
    }

    public void camera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        this.cameraX = eyeX;
        this.cameraY = eyeY;
        this.cameraZ = eyeZ;
        float z0 = eyeX - centerX;
        float z1 = eyeY - centerY;
        float z2 = eyeZ - centerZ;
        this.eyeDist = PApplet.sqrt(z0 * z0 + z1 * z1 + z2 * z2);
        if (nonZero(this.eyeDist)) {
            z0 /= this.eyeDist;
            z1 /= this.eyeDist;
            z2 /= this.eyeDist;
        }

        float x0 = upY * z2 - upZ * z1;
        float x1 = -upX * z2 + upZ * z0;
        float x2 = upX * z1 - upY * z0;
        float y0 = z1 * x2 - z2 * x1;
        float y1 = -z0 * x2 + z2 * x0;
        float y2 = z0 * x1 - z1 * x0;
        float xmag = PApplet.sqrt(x0 * x0 + x1 * x1 + x2 * x2);
        if (nonZero(xmag)) {
            x0 /= xmag;
            x1 /= xmag;
            x2 /= xmag;
        }

        float ymag = PApplet.sqrt(y0 * y0 + y1 * y1 + y2 * y2);
        if (nonZero(ymag)) {
            y0 /= ymag;
            y1 /= ymag;
            y2 /= ymag;
        }

        this.modelview.set(x0, x1, x2, 0.0F, y0, y1, y2, 0.0F, z0, z1, z2, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
        float tx = -eyeX;
        float ty = -eyeY;
        float tz = -eyeZ;
        this.modelview.translate(tx, ty, tz);
        this.modelviewInv.set(x0, y0, z0, 0.0F, x1, y1, z1, 0.0F, x2, y2, z2, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
        this.modelviewInv.translate(-tx, -ty, -tz);
        this.camera.set(this.modelview);
        this.cameraInv.set(this.modelviewInv);
        this.updateProjmodelview();
    }

    public void printCamera() {
        this.camera.print();
    }

    public void cameraUp() {
        this.cameraUp = true;
    }

    protected void defaultCamera() {
        this.camera();
    }

    public void ortho() {
        this.ortho((float)(-this.width) / 2.0F, (float)this.width / 2.0F, (float)(-this.height) / 2.0F, (float)this.height / 2.0F, 0.0F, this.eyeDist * 10.0F);
    }

    public void ortho(float left, float right, float bottom, float top) {
        this.ortho(left, right, bottom, top, 0.0F, this.eyeDist * 10.0F);
    }

    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        float w = right - left;
        float h = top - bottom;
        float d = far - near;
        this.flush();
        float x = 2.0F / w;
        float y = 2.0F / h;
        float z = -2.0F / d;
        float tx = -(right + left) / w;
        float ty = -(top + bottom) / h;
        float tz = -(far + near) / d;
        this.projection.set(x, 0.0F, 0.0F, tx, 0.0F, -y, 0.0F, ty, 0.0F, 0.0F, z, tz, 0.0F, 0.0F, 0.0F, 1.0F);
        this.updateProjmodelview();
    }

    public void perspective() {
        this.perspective(this.defCameraFOV, this.defCameraAspect, this.defCameraNear, this.defCameraFar);
    }

    public void perspective(float fov, float aspect, float zNear, float zFar) {
        float ymax = zNear * (float)Math.tan((double)(fov / 2.0F));
        float ymin = -ymax;
        float xmin = ymin * aspect;
        float xmax = ymax * aspect;
        this.frustum(xmin, xmax, ymin, ymax, zNear, zFar);
    }

    public void frustum(float left, float right, float bottom, float top, float znear, float zfar) {
        this.flush();
        this.cameraFOV = 2.0F * (float)Math.atan2((double)top, (double)znear);
        this.cameraAspect = left / bottom;
        this.cameraNear = znear;
        this.cameraFar = zfar;
        float n2 = 2.0F * znear;
        float w = right - left;
        float h = top - bottom;
        float d = zfar - znear;
        this.projection.set(n2 / w, 0.0F, (right + left) / w, 0.0F, 0.0F, -n2 / h, (top + bottom) / h, 0.0F, 0.0F, 0.0F, -(zfar + znear) / d, -(n2 * zfar) / d, 0.0F, 0.0F, -1.0F, 0.0F);
        this.updateProjmodelview();
    }

    public void printProjection() {
        this.projection.print();
    }

    protected void defaultPerspective() {
        this.perspective();
    }

    public float screenX(float x, float y) {
        return this.screenXImpl(x, y, 0.0F);
    }

    public float screenY(float x, float y) {
        return this.screenYImpl(x, y, 0.0F);
    }

    public float screenX(float x, float y, float z) {
        return this.screenXImpl(x, y, z);
    }

    public float screenY(float x, float y, float z) {
        return this.screenYImpl(x, y, z);
    }

    public float screenZ(float x, float y, float z) {
        return this.screenZImpl(x, y, z);
    }

    protected float screenXImpl(float x, float y, float z) {
        float ax = this.modelview.m00 * x + this.modelview.m01 * y + this.modelview.m02 * z + this.modelview.m03;
        float ay = this.modelview.m10 * x + this.modelview.m11 * y + this.modelview.m12 * z + this.modelview.m13;
        float az = this.modelview.m20 * x + this.modelview.m21 * y + this.modelview.m22 * z + this.modelview.m23;
        float aw = this.modelview.m30 * x + this.modelview.m31 * y + this.modelview.m32 * z + this.modelview.m33;
        return this.screenXImpl(ax, ay, az, aw);
    }

    protected float screenXImpl(float x, float y, float z, float w) {
        float ox = this.projection.m00 * x + this.projection.m01 * y + this.projection.m02 * z + this.projection.m03 * w;
        float ow = this.projection.m30 * x + this.projection.m31 * y + this.projection.m32 * z + this.projection.m33 * w;
        if (nonZero(ow)) {
            ox /= ow;
        }

        float sx = (float)this.width * (1.0F + ox) / 2.0F;
        return sx;
    }

    protected float screenYImpl(float x, float y, float z) {
        float ax = this.modelview.m00 * x + this.modelview.m01 * y + this.modelview.m02 * z + this.modelview.m03;
        float ay = this.modelview.m10 * x + this.modelview.m11 * y + this.modelview.m12 * z + this.modelview.m13;
        float az = this.modelview.m20 * x + this.modelview.m21 * y + this.modelview.m22 * z + this.modelview.m23;
        float aw = this.modelview.m30 * x + this.modelview.m31 * y + this.modelview.m32 * z + this.modelview.m33;
        return this.screenYImpl(ax, ay, az, aw);
    }

    protected float screenYImpl(float x, float y, float z, float w) {
        float oy = this.projection.m10 * x + this.projection.m11 * y + this.projection.m12 * z + this.projection.m13 * w;
        float ow = this.projection.m30 * x + this.projection.m31 * y + this.projection.m32 * z + this.projection.m33 * w;
        if (nonZero(ow)) {
            oy /= ow;
        }

        float sy = (float)this.height * (1.0F + oy) / 2.0F;
        sy = (float)this.height - sy;
        return sy;
    }

    protected float screenZImpl(float x, float y, float z) {
        float ax = this.modelview.m00 * x + this.modelview.m01 * y + this.modelview.m02 * z + this.modelview.m03;
        float ay = this.modelview.m10 * x + this.modelview.m11 * y + this.modelview.m12 * z + this.modelview.m13;
        float az = this.modelview.m20 * x + this.modelview.m21 * y + this.modelview.m22 * z + this.modelview.m23;
        float aw = this.modelview.m30 * x + this.modelview.m31 * y + this.modelview.m32 * z + this.modelview.m33;
        return this.screenZImpl(ax, ay, az, aw);
    }

    protected float screenZImpl(float x, float y, float z, float w) {
        float oz = this.projection.m20 * x + this.projection.m21 * y + this.projection.m22 * z + this.projection.m23 * w;
        float ow = this.projection.m30 * x + this.projection.m31 * y + this.projection.m32 * z + this.projection.m33 * w;
        if (nonZero(ow)) {
            oz /= ow;
        }

        float sz = (oz + 1.0F) / 2.0F;
        return sz;
    }

    public float modelX(float x, float y, float z) {
        float ax = this.modelview.m00 * x + this.modelview.m01 * y + this.modelview.m02 * z + this.modelview.m03;
        float ay = this.modelview.m10 * x + this.modelview.m11 * y + this.modelview.m12 * z + this.modelview.m13;
        float az = this.modelview.m20 * x + this.modelview.m21 * y + this.modelview.m22 * z + this.modelview.m23;
        float aw = this.modelview.m30 * x + this.modelview.m31 * y + this.modelview.m32 * z + this.modelview.m33;
        float ox = this.cameraInv.m00 * ax + this.cameraInv.m01 * ay + this.cameraInv.m02 * az + this.cameraInv.m03 * aw;
        float ow = this.cameraInv.m30 * ax + this.cameraInv.m31 * ay + this.cameraInv.m32 * az + this.cameraInv.m33 * aw;
        return nonZero(ow) ? ox / ow : ox;
    }

    public float modelY(float x, float y, float z) {
        float ax = this.modelview.m00 * x + this.modelview.m01 * y + this.modelview.m02 * z + this.modelview.m03;
        float ay = this.modelview.m10 * x + this.modelview.m11 * y + this.modelview.m12 * z + this.modelview.m13;
        float az = this.modelview.m20 * x + this.modelview.m21 * y + this.modelview.m22 * z + this.modelview.m23;
        float aw = this.modelview.m30 * x + this.modelview.m31 * y + this.modelview.m32 * z + this.modelview.m33;
        float oy = this.cameraInv.m10 * ax + this.cameraInv.m11 * ay + this.cameraInv.m12 * az + this.cameraInv.m13 * aw;
        float ow = this.cameraInv.m30 * ax + this.cameraInv.m31 * ay + this.cameraInv.m32 * az + this.cameraInv.m33 * aw;
        return nonZero(ow) ? oy / ow : oy;
    }

    public float modelZ(float x, float y, float z) {
        float ax = this.modelview.m00 * x + this.modelview.m01 * y + this.modelview.m02 * z + this.modelview.m03;
        float ay = this.modelview.m10 * x + this.modelview.m11 * y + this.modelview.m12 * z + this.modelview.m13;
        float az = this.modelview.m20 * x + this.modelview.m21 * y + this.modelview.m22 * z + this.modelview.m23;
        float aw = this.modelview.m30 * x + this.modelview.m31 * y + this.modelview.m32 * z + this.modelview.m33;
        float oz = this.cameraInv.m20 * ax + this.cameraInv.m21 * ay + this.cameraInv.m22 * az + this.cameraInv.m23 * aw;
        float ow = this.cameraInv.m30 * ax + this.cameraInv.m31 * ay + this.cameraInv.m32 * az + this.cameraInv.m33 * aw;
        return nonZero(ow) ? oz / ow : oz;
    }

    public void popStyle() {
        boolean savedSetAmbient = this.setAmbient;
        super.popStyle();
        if (!savedSetAmbient) {
            this.setAmbient = false;
        }

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

    protected void fillFromCalc() {
        super.fillFromCalc();
        if (!this.setAmbient) {
            this.ambientFromCalc();
            this.setAmbient = false;
        }

    }

    public void lights() {
        this.enableLighting();
        this.lightCount = 0;
        int colorModeSaved = this.colorMode;
        this.colorMode = 1;
        this.lightFalloff(1.0F, 0.0F, 0.0F);
        this.lightSpecular(0.0F, 0.0F, 0.0F);
        this.ambientLight(this.colorModeX * 0.5F, this.colorModeY * 0.5F, this.colorModeZ * 0.5F);
        this.directionalLight(this.colorModeX * 0.5F, this.colorModeY * 0.5F, this.colorModeZ * 0.5F, 0.0F, 0.0F, -1.0F);
        this.colorMode = colorModeSaved;
    }

    public void noLights() {
        this.disableLighting();
        this.lightCount = 0;
    }

    public void ambientLight(float r, float g, float b) {
        this.ambientLight(r, g, b, 0.0F, 0.0F, 0.0F);
    }

    public void ambientLight(float r, float g, float b, float x, float y, float z) {
        this.enableLighting();
        if (this.lightCount == PGL.MAX_LIGHTS) {
            throw new RuntimeException("can only create " + PGL.MAX_LIGHTS + " lights");
        } else {
            this.lightType[this.lightCount] = 0;
            this.lightPosition(this.lightCount, x, y, z, false);
            this.lightNormal(this.lightCount, 0.0F, 0.0F, 0.0F);
            this.lightAmbient(this.lightCount, r, g, b);
            this.noLightDiffuse(this.lightCount);
            this.noLightSpecular(this.lightCount);
            this.noLightSpot(this.lightCount);
            this.lightFalloff(this.lightCount, this.currentLightFalloffConstant, this.currentLightFalloffLinear, this.currentLightFalloffQuadratic);
            ++this.lightCount;
        }
    }

    public void directionalLight(float r, float g, float b, float dx, float dy, float dz) {
        this.enableLighting();
        if (this.lightCount == PGL.MAX_LIGHTS) {
            throw new RuntimeException("can only create " + PGL.MAX_LIGHTS + " lights");
        } else {
            this.lightType[this.lightCount] = 1;
            this.lightPosition(this.lightCount, 0.0F, 0.0F, 0.0F, true);
            this.lightNormal(this.lightCount, dx, dy, dz);
            this.noLightAmbient(this.lightCount);
            this.lightDiffuse(this.lightCount, r, g, b);
            this.lightSpecular(this.lightCount, this.currentLightSpecular[0], this.currentLightSpecular[1], this.currentLightSpecular[2]);
            this.noLightSpot(this.lightCount);
            this.noLightFalloff(this.lightCount);
            ++this.lightCount;
        }
    }

    public void pointLight(float r, float g, float b, float x, float y, float z) {
        this.enableLighting();
        if (this.lightCount == PGL.MAX_LIGHTS) {
            throw new RuntimeException("can only create " + PGL.MAX_LIGHTS + " lights");
        } else {
            this.lightType[this.lightCount] = 2;
            this.lightPosition(this.lightCount, x, y, z, false);
            this.lightNormal(this.lightCount, 0.0F, 0.0F, 0.0F);
            this.noLightAmbient(this.lightCount);
            this.lightDiffuse(this.lightCount, r, g, b);
            this.lightSpecular(this.lightCount, this.currentLightSpecular[0], this.currentLightSpecular[1], this.currentLightSpecular[2]);
            this.noLightSpot(this.lightCount);
            this.lightFalloff(this.lightCount, this.currentLightFalloffConstant, this.currentLightFalloffLinear, this.currentLightFalloffQuadratic);
            ++this.lightCount;
        }
    }

    public void spotLight(float r, float g, float b, float x, float y, float z, float dx, float dy, float dz, float angle, float concentration) {
        this.enableLighting();
        if (this.lightCount == PGL.MAX_LIGHTS) {
            throw new RuntimeException("can only create " + PGL.MAX_LIGHTS + " lights");
        } else {
            this.lightType[this.lightCount] = 3;
            this.lightPosition(this.lightCount, x, y, z, false);
            this.lightNormal(this.lightCount, dx, dy, dz);
            this.noLightAmbient(this.lightCount);
            this.lightDiffuse(this.lightCount, r, g, b);
            this.lightSpecular(this.lightCount, this.currentLightSpecular[0], this.currentLightSpecular[1], this.currentLightSpecular[2]);
            this.lightSpot(this.lightCount, angle, concentration);
            this.lightFalloff(this.lightCount, this.currentLightFalloffConstant, this.currentLightFalloffLinear, this.currentLightFalloffQuadratic);
            ++this.lightCount;
        }
    }

    public void lightFalloff(float constant, float linear, float quadratic) {
        this.currentLightFalloffConstant = constant;
        this.currentLightFalloffLinear = linear;
        this.currentLightFalloffQuadratic = quadratic;
    }

    public void lightSpecular(float x, float y, float z) {
        this.colorCalc(x, y, z);
        this.currentLightSpecular[0] = this.calcR;
        this.currentLightSpecular[1] = this.calcG;
        this.currentLightSpecular[2] = this.calcB;
    }

    protected void enableLighting() {
        this.flush();
        this.lights = true;
    }

    protected void disableLighting() {
        this.flush();
        this.lights = false;
    }

    protected void lightPosition(int num, float x, float y, float z, boolean dir) {
        this.lightPosition[4 * num + 0] = x * this.modelview.m00 + y * this.modelview.m01 + z * this.modelview.m02 + this.modelview.m03;
        this.lightPosition[4 * num + 1] = x * this.modelview.m10 + y * this.modelview.m11 + z * this.modelview.m12 + this.modelview.m13;
        this.lightPosition[4 * num + 2] = x * this.modelview.m20 + y * this.modelview.m21 + z * this.modelview.m22 + this.modelview.m23;
        this.lightPosition[4 * num + 3] = dir ? 0.0F : 1.0F;
    }

    protected void lightNormal(int num, float dx, float dy, float dz) {
        float nx = dx * this.modelviewInv.m00 + dy * this.modelviewInv.m10 + dz * this.modelviewInv.m20;
        float ny = dx * this.modelviewInv.m01 + dy * this.modelviewInv.m11 + dz * this.modelviewInv.m21;
        float nz = dx * this.modelviewInv.m02 + dy * this.modelviewInv.m12 + dz * this.modelviewInv.m22;
        float d = PApplet.dist(0.0F, 0.0F, 0.0F, nx, ny, nz);
        if (0.0F < d) {
            float invn = 1.0F / d;
            this.lightNormal[3 * num + 0] = invn * nx;
            this.lightNormal[3 * num + 1] = invn * ny;
            this.lightNormal[3 * num + 2] = invn * nz;
        } else {
            this.lightNormal[3 * num + 0] = 0.0F;
            this.lightNormal[3 * num + 1] = 0.0F;
            this.lightNormal[3 * num + 2] = 0.0F;
        }

    }

    protected void lightAmbient(int num, float r, float g, float b) {
        this.colorCalc(r, g, b);
        this.lightAmbient[3 * num + 0] = this.calcR;
        this.lightAmbient[3 * num + 1] = this.calcG;
        this.lightAmbient[3 * num + 2] = this.calcB;
    }

    protected void noLightAmbient(int num) {
        this.lightAmbient[3 * num + 0] = 0.0F;
        this.lightAmbient[3 * num + 1] = 0.0F;
        this.lightAmbient[3 * num + 2] = 0.0F;
    }

    protected void lightDiffuse(int num, float r, float g, float b) {
        this.colorCalc(r, g, b);
        this.lightDiffuse[3 * num + 0] = this.calcR;
        this.lightDiffuse[3 * num + 1] = this.calcG;
        this.lightDiffuse[3 * num + 2] = this.calcB;
    }

    protected void noLightDiffuse(int num) {
        this.lightDiffuse[3 * num + 0] = 0.0F;
        this.lightDiffuse[3 * num + 1] = 0.0F;
        this.lightDiffuse[3 * num + 2] = 0.0F;
    }

    protected void lightSpecular(int num, float r, float g, float b) {
        this.lightSpecular[3 * num + 0] = r;
        this.lightSpecular[3 * num + 1] = g;
        this.lightSpecular[3 * num + 2] = b;
    }

    protected void noLightSpecular(int num) {
        this.lightSpecular[3 * num + 0] = 0.0F;
        this.lightSpecular[3 * num + 1] = 0.0F;
        this.lightSpecular[3 * num + 2] = 0.0F;
    }

    protected void lightFalloff(int num, float c0, float c1, float c2) {
        this.lightFalloffCoefficients[3 * num + 0] = c0;
        this.lightFalloffCoefficients[3 * num + 1] = c1;
        this.lightFalloffCoefficients[3 * num + 2] = c2;
    }

    protected void noLightFalloff(int num) {
        this.lightFalloffCoefficients[3 * num + 0] = 1.0F;
        this.lightFalloffCoefficients[3 * num + 1] = 0.0F;
        this.lightFalloffCoefficients[3 * num + 2] = 0.0F;
    }

    protected void lightSpot(int num, float angle, float exponent) {
        this.lightSpotParameters[2 * num + 0] = Math.max(0.0F, PApplet.cos(angle));
        this.lightSpotParameters[2 * num + 1] = exponent;
    }

    protected void noLightSpot(int num) {
        this.lightSpotParameters[2 * num + 0] = 0.0F;
        this.lightSpotParameters[2 * num + 1] = 0.0F;
    }

    protected void backgroundImpl(PImage image) {
        this.backgroundImpl();
        this.set(0, 0, image);
        this.backgroundA = 1.0F;
        this.loaded = false;
    }

    protected void backgroundImpl() {
        this.flush();
        this.pgl.clearBackground(this.backgroundR, this.backgroundG, this.backgroundB, this.backgroundA, !this.hints[5], true);
        this.loaded = false;
    }

    protected void report(String where) {
        if (!this.hints[4]) {
            int err = this.pgl.getError();
            if (err != 0) {
                String errString = this.pgl.errorString(err);
                String msg = "OpenGL error " + err + " at " + where + ": " + errString;
                PGraphics.showWarning(msg);
            }
        }

    }

    public boolean isGL() {
        return true;
    }

    public void loadPixels() {
        if (!this.primaryGraphics || !this.sized) {
            boolean needEndDraw = false;
            if (!this.drawing) {
                this.beginDraw();
                needEndDraw = true;
            }

            if (!this.loaded) {
                this.flush();
            }

            this.allocatePixels();
            if (!this.loaded) {
                this.readPixels();
            }

            this.loaded = true;
            if (needEndDraw) {
                this.endDraw();
            }

        }
    }

    protected void allocatePixels() {
        this.updatePixelSize();
        if (this.pixels == null || this.pixels.length != this.pixelWidth * this.pixelHeight) {
            this.pixels = new int[this.pixelWidth * this.pixelHeight];
            this.pixelBuffer = PGL.allocateIntBuffer(this.pixels);
            this.loaded = false;
        }

    }

    protected void readPixels() {
        this.updatePixelSize();
        this.beginPixelsOp(1);

        try {
            this.pgl.readPixelsImpl(0, 0, this.pixelWidth, this.pixelHeight, PGL.RGBA, PGL.UNSIGNED_BYTE, this.pixelBuffer);
        } catch (IndexOutOfBoundsException var3) {
        }

        this.endPixelsOp();

        try {
            PGL.getIntArray(this.pixelBuffer, this.pixels);
            PGL.nativeToJavaARGB(this.pixels, this.pixelWidth, this.pixelHeight);
        } catch (ArrayIndexOutOfBoundsException var2) {
        }

    }

    protected void drawPixels(int x, int y, int w, int h) {
        this.drawPixels(this.pixels, x, y, w, h);
    }

    protected void drawPixels(int[] pixBuffer, int x, int y, int w, int h) {
        int f = (int)this.pgl.getPixelScale();
        int len = f * w * f * h;
        if (this.nativePixels == null || this.nativePixels.length < len) {
            this.nativePixels = new int[len];
            this.nativePixelBuffer = PGL.allocateIntBuffer(this.nativePixels);
        }

        int tw;
        int th;
        try {
            if (0 >= x && 0 >= y && w >= this.width && h >= this.height) {
                PApplet.arrayCopy(pixBuffer, 0, this.nativePixels, 0, len);
            } else {
                int offset0 = f * (y * this.width + x);
                tw = 0;

                for(th = f * y; th < f * (y + h); ++th) {
                    System.arraycopy(pixBuffer, offset0, this.nativePixels, tw, f * w);
                    offset0 += f * this.width;
                    tw += f * w;
                }
            }

            PGL.javaToNativeARGB(this.nativePixels, f * w, f * h);
        } catch (ArrayIndexOutOfBoundsException var11) {
        }

        PGL.putIntArray(this.nativePixelBuffer, this.nativePixels);
        if (this.primaryGraphics && !this.pgl.isFBOBacked()) {
            this.loadTextureImpl(2, false);
        }

        boolean needToDrawTex = this.primaryGraphics && (!this.pgl.isFBOBacked() || this.pgl.isFBOBacked() && this.pgl.isMultisampled()) || this.offscreenMultisample;
        if (this.texture != null) {
            if (needToDrawTex) {
                tw = PApplet.min(this.texture.glWidth - f * x, f * w);
                th = PApplet.min(this.texture.glHeight - f * y, f * h);
                this.pgl.copyToTexture(this.texture.glTarget, this.texture.glFormat, this.texture.glName, f * x, f * y, tw, th, this.nativePixelBuffer);
                this.beginPixelsOp(2);
                this.drawTexture(x, y, w, h);
                this.endPixelsOp();
            } else {
                this.pgl.copyToTexture(this.texture.glTarget, this.texture.glFormat, this.texture.glName, f * x, f * (this.height - (y + h)), f * w, f * h, this.nativePixelBuffer);
            }

        }
    }

    protected void clearState() {
        super.clearState();
        if (this.restoreFilename != null) {
            File cacheFile = new File(this.restoreFilename);
            cacheFile.delete();
        }

    }

    protected void saveState() {
        super.saveState();
        this.pgl.queueEvent(new Runnable() {
            public void run() {
                Context context = PGraphicsOpenGL.this.parent.getContext();
                if (context != null && !PGraphicsOpenGL.this.parent.getSurface().getComponent().isService()) {
                    try {
                        PGraphicsOpenGL.this.restoreWidth = PGraphicsOpenGL.this.pixelWidth;
                        PGraphicsOpenGL.this.restoreHeight = PGraphicsOpenGL.this.pixelHeight;
                        int[] restorePixels = new int[PGraphicsOpenGL.this.restoreWidth * PGraphicsOpenGL.this.restoreHeight];
                        IntBuffer buf = IntBuffer.wrap(restorePixels);
                        buf.position(0);
                        PGraphicsOpenGL.this.beginPixelsOp(1);
                        PGraphicsOpenGL.this.pgl.readPixelsImpl(0, 0, PGraphicsOpenGL.this.pixelWidth, PGraphicsOpenGL.this.pixelHeight, PGL.RGBA, PGL.UNSIGNED_BYTE, buf);
                        PGraphicsOpenGL.this.endPixelsOp();
                        File cacheDir = "mounted" != Environment.getExternalStorageState() && Environment.isExternalStorageRemovable() ? context.getCacheDir() : context.getExternalCacheDir();
                        File cacheFile = new File(cacheDir + File.separator + "restore_pixels");
                        PGraphicsOpenGL.this.restoreFilename = cacheFile.getAbsolutePath();
                        FileOutputStream stream = new FileOutputStream(cacheFile);
                        ObjectOutputStream dout = new ObjectOutputStream(stream);
                        dout.writeObject(restorePixels);
                        dout.flush();
                        stream.getFD().sync();
                        stream.close();
                    } catch (Exception var8) {
                        PGraphics.showWarning("Could not save screen contents to cache");
                        var8.printStackTrace();
                    }

                }
            }
        });
    }

    protected void restoreSurface() {
        if (this.changed) {
            this.changed = false;
            if (this.restoreFilename != null && this.restoreWidth == this.pixelWidth && this.restoreHeight == this.pixelHeight) {
                this.restoreCount = 2;
            }
        } else if (this.restoreCount > 0) {
            --this.restoreCount;
            if (this.restoreCount == 0) {
                Context context = this.parent.getContext();
                if (context == null) {
                    return;
                }

                try {
                    File cacheFile = new File(this.restoreFilename);
                    FileInputStream inStream = new FileInputStream(cacheFile);
                    ObjectInputStream din = new ObjectInputStream(inStream);
                    int[] restorePixels = (int[])((int[])din.readObject());
                    if (restorePixels.length == this.pixelWidth * this.pixelHeight) {
                        PGL.nativeToJavaARGB(restorePixels, this.pixelWidth, this.pixelHeight);
                        this.drawPixels(restorePixels, 0, 0, this.pixelWidth, this.pixelHeight);
                    }

                    inStream.close();
                    cacheFile.delete();
                } catch (Exception var9) {
                    PGraphics.showWarning("Could not restore screen contents from cache");
                    var9.printStackTrace();
                } finally {
                    this.restoreFilename = null;
                    this.restoreWidth = -1;
                    this.restoreHeight = -1;
                    this.restoredSurface = true;
                }
            }
        }

        super.restoreSurface();
    }

    protected boolean requestNoLoop() {
        return true;
    }

    public int get(int x, int y) {
        this.loadPixels();
        return super.get(x, y);
    }

    protected void getImpl(int sourceX, int sourceY, int sourceWidth, int sourceHeight, PImage target, int targetX, int targetY) {
        this.loadPixels();
        super.getImpl(sourceX, sourceY, sourceWidth, sourceHeight, target, targetX, targetY);
    }

    public void set(int x, int y, int argb) {
        this.loadPixels();
        super.set(x, y, argb);
    }

    protected void setImpl(PImage sourceImage, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int targetX, int targetY) {
        this.updatePixelSize();
        if (sourceImage.pixels == null) {
            this.loadPixels();
            sourceImage.loadPixels();
            int sourceOffset = sourceY * sourceImage.pixelWidth + sourceX;
            int targetOffset = targetY * this.pixelWidth + targetX;

            for(int y = sourceY; y < sourceY + sourceHeight; ++y) {
                System.arraycopy(sourceImage.pixels, sourceOffset, this.pixels, targetOffset, sourceWidth);
                sourceOffset += sourceImage.pixelWidth;
                targetOffset += this.pixelWidth;
            }
        }

        this.copy(sourceImage, sourceX, sourceY, sourceWidth, sourceHeight, targetX, targetY, sourceWidth, sourceHeight);
    }

    public boolean save(String filename) {
        return this.saveImpl(filename);
    }

    protected void processImageBeforeAsyncSave(PImage image) {
        if (image.format == -1) {
            PGL.nativeToJavaARGB(image.pixels, image.width, image.height);
            image.format = 2;
        } else if (image.format == -2) {
            PGL.nativeToJavaRGB(image.pixels, image.width, image.height);
            image.format = 1;
        }

    }

    protected static void completeFinishedPixelTransfers() {
        ongoingPixelTransfersIterable.addAll(ongoingPixelTransfers);

        PGraphicsOpenGL.AsyncPixelReader pixelReader;
        for(Iterator var0 = ongoingPixelTransfersIterable.iterator(); var0.hasNext(); pixelReader.calledThisFrame = false) {
            pixelReader = (PGraphicsOpenGL.AsyncPixelReader)var0.next();
            if (!pixelReader.calledThisFrame) {
                pixelReader.completeFinishedTransfers();
            }
        }

        ongoingPixelTransfersIterable.clear();
    }

    protected static void completeAllPixelTransfers() {
        ongoingPixelTransfersIterable.addAll(ongoingPixelTransfers);
        Iterator var0 = ongoingPixelTransfersIterable.iterator();

        while(var0.hasNext()) {
            PGraphicsOpenGL.AsyncPixelReader pixelReader = (PGraphicsOpenGL.AsyncPixelReader)var0.next();
            pixelReader.completeAllTransfers();
        }

        ongoingPixelTransfersIterable.clear();
    }

    public void loadTexture() {
        boolean needEndDraw = false;
        if (!this.drawing) {
            this.beginDraw();
            needEndDraw = true;
        }

        this.flush();
        if (this.primaryGraphics) {
            this.updatePixelSize();
            if (this.pgl.isFBOBacked()) {
                this.pgl.syncBackTexture();
            } else {
                this.loadTextureImpl(2, false);
                if (this.nativePixels == null || this.nativePixels.length < this.pixelWidth * this.pixelHeight) {
                    this.nativePixels = new int[this.pixelWidth * this.pixelHeight];
                    this.nativePixelBuffer = PGL.allocateIntBuffer(this.nativePixels);
                }

                this.beginPixelsOp(1);

                try {
                    this.pgl.readPixelsImpl(0, 0, this.pixelWidth, this.pixelHeight, PGL.RGBA, PGL.UNSIGNED_BYTE, this.nativePixelBuffer);
                } catch (IndexOutOfBoundsException var4) {
                }

                this.endPixelsOp();
                if (this.texture != null) {
                    this.texture.setNative(this.nativePixelBuffer, 0, 0, this.pixelWidth, this.pixelHeight);
                }
            }
        } else if (this.offscreenMultisample) {
            FrameBuffer ofb = this.offscreenFramebuffer;
            FrameBuffer mfb = this.multisampleFramebuffer;
            if (ofb != null && mfb != null) {
                mfb.copyColor(ofb);
            }
        }

        if (needEndDraw) {
            this.endDraw();
        }

    }

    public void updateTexture() {
        if (this.texture != null) {
            this.texture.updateTexels();
        }

    }

    public void updateTexture(int x, int y, int w, int h) {
        if (this.texture != null) {
            this.texture.updateTexels(x, y, w, h);
        }

    }

    public void updateDisplay() {
        this.flush();
        this.beginPixelsOp(2);
        this.drawTexture();
        this.endPixelsOp();
    }

    protected void loadTextureImpl(int sampling, boolean mipmap) {
        this.updatePixelSize();
        if (this.pixelWidth != 0 && this.pixelHeight != 0) {
            if (this.texture == null || this.texture.contextIsOutdated()) {
                Parameters params = new Parameters(2, sampling, mipmap);
                this.texture = new Texture(this, this.pixelWidth, this.pixelHeight, params);
                this.texture.invertedY(!this.cameraUp);
                this.texture.colorBuffer(true);
                this.setCache(this, this.texture);
            }

        }
    }

    protected void createPTexture() {
        this.updatePixelSize();
        if (this.texture != null) {
            this.ptexture = new Texture(this, this.pixelWidth, this.pixelHeight, this.texture.getParameters());
            this.ptexture.invertedY(!this.cameraUp);
            this.ptexture.colorBuffer(true);
        }

    }

    protected void swapOffscreenTextures() {
        FrameBuffer ofb = this.offscreenFramebuffer;
        if (this.texture != null && this.ptexture != null && ofb != null) {
            int temp = this.texture.glName;
            this.texture.glName = this.ptexture.glName;
            this.ptexture.glName = temp;
            ofb.setColorBuffer(this.texture);
        }

    }

    protected void drawTexture() {
        if (this.texture != null) {
            this.pgl.disable(PGL.BLEND);
            this.pgl.drawTexture(this.texture.glTarget, this.texture.glName, this.texture.glWidth, this.texture.glHeight, 0, 0, this.width, this.height);
            this.pgl.enable(PGL.BLEND);
        }

    }

    protected void drawTexture(int x, int y, int w, int h) {
        if (this.texture != null) {
            this.pgl.disable(PGL.BLEND);
            this.pgl.drawTexture(this.texture.glTarget, this.texture.glName, this.texture.glWidth, this.texture.glHeight, 0, 0, this.width, this.height, x, y, x + w, y + h, x, this.height - (y + h), x + w, this.height - y);
            this.pgl.enable(PGL.BLEND);
        }

    }

    protected void drawPTexture() {
        if (this.ptexture != null) {
            this.pgl.disable(PGL.BLEND);
            this.pgl.drawTexture(this.ptexture.glTarget, this.ptexture.glName, this.ptexture.glWidth, this.ptexture.glHeight, 0, 0, this.width, this.height);
            this.pgl.enable(PGL.BLEND);
        }

    }

    public void mask(PImage alpha) {
        this.updatePixelSize();
        if (alpha.width == this.pixelWidth && alpha.height == this.pixelHeight) {
            PGraphicsOpenGL ppg = this.getPrimaryPG();
            if (ppg.maskShader == null) {
                ppg.maskShader = new PShader(this.parent, defTextureShaderVertURL, maskShaderFragURL);
            }

            ppg.maskShader.set("mask", alpha);
            this.filter(ppg.maskShader);
        } else {
            throw new RuntimeException("The PImage used with mask() must be the same size as the applet.");
        }
    }

    public void filter(int kind) {
        PImage temp = this.get();
        temp.filter(kind);
        this.set(0, 0, temp);
    }

    public void filter(int kind, float param) {
        PImage temp = this.get();
        temp.filter(kind, param);
        this.set(0, 0, temp);
    }

    public void filter(PShader shader) {
        if (!shader.isPolyShader()) {
            PGraphics.showWarning("Your shader cannot be used as a filter because is of type POINT or LINES");
        } else {
            boolean needEndDraw = false;
            if (this.primaryGraphics) {
                this.pgl.enableFBOLayer();
            } else if (!this.drawing) {
                this.beginDraw();
                needEndDraw = true;
            }

            this.loadTexture();
            if (this.filterTexture == null || this.filterTexture.contextIsOutdated()) {
                this.filterTexture = new Texture(this, this.texture.width, this.texture.height, this.texture.getParameters());
                this.filterTexture.invertedY(!this.cameraUp);
                this.filterImage = this.wrapTexture(this.filterTexture);
            }

            this.filterTexture.set(this.texture);
            this.pgl.depthMask(false);
            this.pgl.disable(PGL.DEPTH_TEST);
            this.begin2D();
            boolean prevLights = this.lights;
            this.lights = false;
            int prevTextureMode = this.textureMode;
            this.textureMode = 1;
            boolean prevStroke = this.stroke;
            this.stroke = false;
            int prevBlendMode = this.blendMode;
            this.blendMode(0);
            PShader prevShader = this.polyShader;
            this.polyShader = shader;
            this.beginShape(17);
            this.texture(this.filterImage);
            this.vertex(0.0F, 0.0F, 0.0F, 0.0F);
            this.vertex((float)this.width, 0.0F, 1.0F, 0.0F);
            this.vertex((float)this.width, (float)this.height, 1.0F, 1.0F);
            this.vertex(0.0F, (float)this.height, 0.0F, 1.0F);
            this.endShape();
            this.end2D();
            this.polyShader = prevShader;
            this.stroke = prevStroke;
            this.lights = prevLights;
            this.textureMode = prevTextureMode;
            this.blendMode(prevBlendMode);
            if (!this.hints[2]) {
                this.pgl.enable(PGL.DEPTH_TEST);
            }

            if (!this.hints[5]) {
                this.pgl.depthMask(true);
            }

            if (needEndDraw) {
                this.endDraw();
            }

        }
    }

    public void copy(int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        if (this.primaryGraphics) {
            this.pgl.enableFBOLayer();
        }

        this.loadTexture();
        if (this.filterTexture == null || this.filterTexture.contextIsOutdated()) {
            this.filterTexture = new Texture(this, this.texture.width, this.texture.height, this.texture.getParameters());
            this.filterTexture.invertedY(!this.cameraUp);
            this.filterImage = this.wrapTexture(this.filterTexture);
        }

        this.filterTexture.put(this.texture, sx, this.height - (sy + sh), sw, this.height - sy);
        this.copy(this.filterImage, sx, sy, sw, sh, dx, dy, dw, dh);
    }

    public void copy(PImage src, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        boolean needEndDraw = false;
        if (!this.drawing) {
            this.beginDraw();
            needEndDraw = true;
        }

        this.flush();
        Texture tex = this.getTexture(src);
        boolean invX = tex.invertedX();
        boolean invY = tex.invertedY();
        int scrX0;
        int scrX1;
        if (invX) {
            scrX0 = dx + dw;
            scrX1 = dx;
        } else {
            scrX0 = dx;
            scrX1 = dx + dw;
        }

        int texX1 = sx + sw;
        int scrY0;
        int scrY1;
        int texY0;
        int texY1;
        if (invY) {
            scrY0 = this.height - (dy + dh);
            scrY1 = this.height - dy;
            texY0 = tex.height - (sy + sh);
            texY1 = tex.height - sy;
        } else {
            scrY0 = this.height - dy;
            scrY1 = this.height - (dy + dh);
            texY0 = sy;
            texY1 = sy + sh;
        }

        this.pgl.drawTexture(tex.glTarget, tex.glName, tex.glWidth, tex.glHeight, 0, 0, this.width, this.height, sx, texY0, texX1, texY1, scrX0, scrY0, scrX1, scrY1);
        if (needEndDraw) {
            this.endDraw();
        }

    }

    protected void blendModeImpl() {
        if (this.blendMode != this.lastBlendMode) {
            this.flush();
        }

        this.pgl.enable(PGL.BLEND);
        if (this.blendMode == 0) {
            if (blendEqSupported) {
                this.pgl.blendEquation(PGL.FUNC_ADD);
            }

            this.pgl.blendFunc(PGL.ONE, PGL.ZERO);
        } else if (this.blendMode == 1) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }

            this.pgl.blendFuncSeparate(PGL.SRC_ALPHA, PGL.ONE_MINUS_SRC_ALPHA, PGL.ONE, PGL.ONE);
        } else if (this.blendMode == 2) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }

            this.pgl.blendFuncSeparate(PGL.SRC_ALPHA, PGL.ONE, PGL.ONE, PGL.ONE);
        } else if (this.blendMode == 4) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_REVERSE_SUBTRACT, PGL.FUNC_ADD);
                this.pgl.blendFuncSeparate(PGL.SRC_ALPHA, PGL.ONE, PGL.ONE, PGL.ONE);
            } else {
                PGraphics.showWarning("blendMode(%1$s) is not supported by this hardware (or driver)", new Object[]{"SUBTRACT"});
            }
        } else if (this.blendMode == 8) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_MAX, PGL.FUNC_ADD);
                this.pgl.blendFuncSeparate(PGL.ONE, PGL.ONE, PGL.ONE, PGL.ONE);
            } else {
                PGraphics.showWarning("blendMode(%1$s) is not supported by this hardware (or driver)", new Object[]{"LIGHTEST"});
            }
        } else if (this.blendMode == 16) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_MIN, PGL.FUNC_ADD);
                this.pgl.blendFuncSeparate(PGL.ONE, PGL.ONE, PGL.ONE, PGL.ONE);
            } else {
                PGraphics.showWarning("blendMode(%1$s) is not supported by this hardware (or driver)", new Object[]{"DARKEST"});
            }
        } else if (this.blendMode == 64) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }

            this.pgl.blendFuncSeparate(PGL.ONE_MINUS_DST_COLOR, PGL.ONE_MINUS_SRC_COLOR, PGL.ONE, PGL.ONE);
        } else if (this.blendMode == 128) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }

            this.pgl.blendFuncSeparate(PGL.ZERO, PGL.SRC_COLOR, PGL.ONE, PGL.ONE);
        } else if (this.blendMode == 256) {
            if (blendEqSupported) {
                this.pgl.blendEquationSeparate(PGL.FUNC_ADD, PGL.FUNC_ADD);
            }

            this.pgl.blendFuncSeparate(PGL.ONE_MINUS_DST_COLOR, PGL.ONE, PGL.ONE, PGL.ONE);
        } else if (this.blendMode == 32) {
            PGraphics.showWarning("blendMode(%1$s) is not supported by this renderer", new Object[]{"DIFFERENCE"});
        } else if (this.blendMode == 512) {
            PGraphics.showWarning("blendMode(%1$s) is not supported by this renderer", new Object[]{"OVERLAY"});
        } else if (this.blendMode == 1024) {
            PGraphics.showWarning("blendMode(%1$s) is not supported by this renderer", new Object[]{"HARD_LIGHT"});
        } else if (this.blendMode == 2048) {
            PGraphics.showWarning("blendMode(%1$s) is not supported by this renderer", new Object[]{"SOFT_LIGHT"});
        } else if (this.blendMode == 4096) {
            PGraphics.showWarning("blendMode(%1$s) is not supported by this renderer", new Object[]{"DODGE"});
        } else if (this.blendMode == 8192) {
            PGraphics.showWarning("blendMode(%1$s) is not supported by this renderer", new Object[]{"BURN"});
        }

        this.lastBlendMode = this.blendMode;
    }

    public Texture getTexture() {
        return this.getTexture(true);
    }

    public Texture getTexture(boolean load) {
        if (load) {
            this.loadTexture();
        }

        return this.texture;
    }

    public Texture getTexture(PImage img) {
        Texture tex = (Texture)this.initCache(img);
        if (tex == null) {
            return null;
        } else {
            if (img.isModified()) {
                if (img.width != tex.width || img.height != tex.height) {
                    tex.init(img.width, img.height);
                }

                this.updateTexture(img, tex);
            }

            if (tex.hasBuffers()) {
                tex.bufferUpdate();
            }

            this.checkTexture(tex);
            return tex;
        }
    }

    public FrameBuffer getFrameBuffer() {
        return this.getFrameBuffer(false);
    }

    public FrameBuffer getFrameBuffer(boolean multi) {
        return multi ? this.multisampleFramebuffer : this.offscreenFramebuffer;
    }

    protected Object initCache(PImage img) {
        if (!this.checkGLThread()) {
            return null;
        } else {
            Texture tex = (Texture)this.getCache(img);
            if (tex == null || tex.contextIsOutdated()) {
                tex = this.addTexture(img);
                if (tex != null) {
                    boolean dispose = img.pixels == null;
                    img.loadPixels();
                    tex.set(img.pixels, img.format);
                    img.setModified();
                    if (dispose) {
                        img.pixels = null;
                        img.loaded = false;
                    }
                }
            }

            return tex;
        }
    }

    protected void bindFrontTexture() {
        if (this.primaryGraphics) {
            this.pgl.bindFrontTexture();
        } else {
            if (this.ptexture == null) {
                this.createPTexture();
            }

            this.ptexture.bind();
        }

    }

    protected void unbindFrontTexture() {
        if (this.primaryGraphics) {
            this.pgl.unbindFrontTexture();
        } else {
            this.ptexture.unbind();
        }

    }

    protected Texture addTexture(PImage img) {
        Parameters params = new Parameters(2, this.textureSampling, this.getHint(-8), this.textureWrap);
        return this.addTexture(img, params);
    }

    protected Texture addTexture(PImage img, Parameters params) {
        if (img.width != 0 && img.height != 0) {
            if (img.parent == null) {
                img.parent = this.parent;
            }

            Texture tex = new Texture(this, img.pixelWidth, img.pixelHeight, params);
            tex.invertedY(this.cameraUp);
            this.setCache(img, tex);
            return tex;
        } else {
            return null;
        }
    }

    protected void checkTexture(Texture tex) {
        if (!tex.colorBuffer() && (tex.usingMipmaps == this.hints[8] || tex.currentSampling() != this.textureSampling)) {
            if (this.hints[8]) {
                tex.usingMipmaps(false, this.textureSampling);
            } else {
                tex.usingMipmaps(true, this.textureSampling);
            }
        }

        if (tex.usingRepeat && this.textureWrap == 0 || !tex.usingRepeat && this.textureWrap == 1) {
            if (this.textureWrap == 0) {
                tex.usingRepeat(false);
            } else {
                tex.usingRepeat(true);
            }
        }

    }

    protected PImage wrapTexture(Texture tex) {
        PImage img = new PImage();
        img.parent = this.parent;
        img.width = tex.width;
        img.height = tex.height;
        img.format = 2;
        this.setCache(img, tex);
        return img;
    }

    protected void updateTexture(PImage img, Texture tex) {
        if (tex != null && img.isModified()) {
            int x = img.getModifiedX1();
            int y = img.getModifiedY1();
            int w = img.getModifiedX2() - x;
            int h = img.getModifiedY2() - y;
            tex.set(img.pixels, x, y, w, h, img.format);
        }

        img.setModified(false);
    }

    protected void deleteSurfaceTextures() {
        if (this.texture != null) {
            this.texture.dispose();
        }

        if (this.ptexture != null) {
            this.ptexture.dispose();
        }

        if (this.filterTexture != null) {
            this.filterTexture.dispose();
        }

    }

    protected boolean checkGLThread() {
        return true;
    }

    public void resize(int wide, int high) {
        PGraphics.showMethodWarning("resize");
    }

    protected void initPrimary() {
        if (!this.initialized) {
            this.pgl.initSurface(this.smooth);
            if (this.texture != null) {
                this.removeCache(this);
                this.texture = null;
                this.ptexture = null;
            }

            this.initialized = true;
        }
    }

    protected void beginOnscreenDraw() {
        this.updatePixelSize();
        this.restoreSurface();
        this.pgl.beginRender();
        if (this.drawFramebuffer == null) {
            this.drawFramebuffer = new FrameBuffer(this, this.pixelWidth, this.pixelHeight, true);
        }

        this.drawFramebuffer.setFBO(this.pgl.getDrawFramebuffer());
        if (this.readFramebuffer == null) {
            this.readFramebuffer = new FrameBuffer(this, this.pixelWidth, this.pixelHeight, true);
        }

        this.readFramebuffer.setFBO(this.pgl.getReadFramebuffer());
        if (this.currentFramebuffer == null) {
            this.setFramebuffer(this.drawFramebuffer);
        }

        if (this.pgl.isFBOBacked()) {
            this.texture = this.pgl.wrapBackTexture(this.texture);
            this.ptexture = this.pgl.wrapFrontTexture(this.ptexture);
        }

    }

    protected void endOnscreenDraw() {
        this.pgl.endRender(this.parent.sketchWindowColor());
    }

    protected void initOffscreen() {
        this.loadTextureImpl(this.textureSampling, false);
        FrameBuffer ofb = this.offscreenFramebuffer;
        FrameBuffer mfb = this.multisampleFramebuffer;
        if (ofb != null) {
            ofb.dispose();
            ofb = null;
        }

        if (mfb != null) {
            mfb.dispose();
            mfb = null;
        }

        boolean packed = depthBits == 24 && stencilBits == 8 && packedDepthStencilSupported;
        if (fboMultisampleSupported && 1 < PGL.smoothToSamples(this.smooth)) {
            mfb = new FrameBuffer(this, this.texture.glWidth, this.texture.glHeight, PGL.smoothToSamples(this.smooth), 0, depthBits, stencilBits, packed, false);
            mfb.clear();
            this.multisampleFramebuffer = mfb;
            this.offscreenMultisample = true;
            if (this.hints[10]) {
                ofb = new FrameBuffer(this, this.texture.glWidth, this.texture.glHeight, 1, 1, depthBits, stencilBits, packed, false);
            } else {
                ofb = new FrameBuffer(this, this.texture.glWidth, this.texture.glHeight, 1, 1, 0, 0, false, false);
            }
        } else {
            this.smooth = 0;
            ofb = new FrameBuffer(this, this.texture.glWidth, this.texture.glHeight, 1, 1, depthBits, stencilBits, packed, false);
            this.offscreenMultisample = false;
        }

        ofb.setColorBuffer(this.texture);
        ofb.clear();
        this.offscreenFramebuffer = ofb;
        this.initialized = true;
    }

    protected void beginOffscreenDraw() {
        FrameBuffer mfb;
        if (!this.initialized) {
            this.initOffscreen();
        } else {
            mfb = this.offscreenFramebuffer;
            FrameBuffer mfb = this.multisampleFramebuffer;
            boolean outdated = mfb != null && mfb.contextIsOutdated();
            boolean outdatedMulti = mfb != null && mfb.contextIsOutdated();
            if (!outdated && !outdatedMulti) {
                this.swapOffscreenTextures();
            } else {
                this.restartPGL();
                this.initOffscreen();
            }
        }

        this.pushFramebuffer();
        if (this.offscreenMultisample) {
            mfb = this.multisampleFramebuffer;
            if (mfb != null) {
                this.setFramebuffer(mfb);
            }
        } else {
            mfb = this.offscreenFramebuffer;
            if (mfb != null) {
                this.setFramebuffer(mfb);
            }
        }

        this.drawPTexture();
        if (this.clip) {
            this.pgl.enable(PGL.SCISSOR_TEST);
            this.pgl.scissor(this.clipRect[0], this.clipRect[1], this.clipRect[2], this.clipRect[3]);
        } else {
            this.pgl.disable(PGL.SCISSOR_TEST);
        }

    }

    protected void endOffscreenDraw() {
        if (this.offscreenMultisample) {
            FrameBuffer ofb = this.offscreenFramebuffer;
            FrameBuffer mfb = this.multisampleFramebuffer;
            if (ofb != null && mfb != null) {
                mfb.copyColor(ofb);
            }
        }

        this.popFramebuffer();
        if (this.backgroundA == 1.0F) {
            this.pgl.colorMask(false, false, false, true);
            this.pgl.clearColor(0.0F, 0.0F, 0.0F, this.backgroundA);
            this.pgl.clear(PGL.COLOR_BUFFER_BIT);
            this.pgl.colorMask(true, true, true, true);
        }

        if (this.texture != null) {
            this.texture.updateTexels();
        }

        this.getPrimaryPG().restoreGL();
    }

    protected void setViewport() {
        this.viewport.put(0, 0);
        this.viewport.put(1, 0);
        this.viewport.put(2, this.width);
        this.viewport.put(3, this.height);
        this.pgl.viewport(this.viewport.get(0), this.viewport.get(1), this.viewport.get(2), this.viewport.get(3));
    }

    protected void checkSettings() {
        super.checkSettings();
        this.setGLSettings();
    }

    protected void setGLSettings() {
        this.inGeo.clear();
        this.tessGeo.clear();
        this.texCache.clear();
        super.noTexture();
        this.blendModeImpl();
        if (this.hints[2]) {
            this.pgl.disable(PGL.DEPTH_TEST);
        } else {
            this.pgl.enable(PGL.DEPTH_TEST);
        }

        this.pgl.depthFunc(PGL.LEQUAL);
        if (this.hints[6]) {
            this.flushMode = 0;
        } else {
            this.flushMode = 1;
        }

        if (this.primaryGraphics) {
        }

        if (this.smooth < 1) {
            this.pgl.disable(PGL.MULTISAMPLE);
        } else if (!OPENGL_RENDERER.equals("VideoCore IV HW")) {
            this.pgl.enable(PGL.MULTISAMPLE);
        }

        if (!OPENGL_RENDERER.equals("VideoCore IV HW")) {
            this.pgl.disable(PGL.POLYGON_SMOOTH);
        }

        if (!this.sized && this.parent.frameCount != 0) {
            this.modelview.set(this.camera);
            this.modelviewInv.set(this.cameraInv);
            this.updateProjmodelview();
        } else {
            if (this.primaryGraphics) {
                this.background(this.backgroundColor);
            } else {
                this.background(0 | this.backgroundColor & 16777215);
            }

            this.defaultPerspective();
            this.defaultCamera();
            this.sized = false;
        }

        if (this.is3D()) {
            this.noLights();
            this.lightFalloff(1.0F, 0.0F, 0.0F);
            this.lightSpecular(0.0F, 0.0F, 0.0F);
        }

        this.pgl.frontFace(this.cameraUp ? PGL.CCW : PGL.CW);
        this.pgl.disable(PGL.CULL_FACE);
        this.pgl.activeTexture(PGL.TEXTURE0);
        this.normalX = this.normalY = 0.0F;
        this.normalZ = 1.0F;
        this.pgl.clearDepthStencil();
        if (this.hints[5]) {
            this.pgl.depthMask(false);
        } else {
            this.pgl.depthMask(true);
        }

        this.pixelsOp = 0;
        this.modified = false;
        this.loaded = false;
    }

    protected void getGLParameters() {
        OPENGL_VENDOR = this.pgl.getString(PGL.VENDOR);
        OPENGL_RENDERER = this.pgl.getString(PGL.RENDERER);
        OPENGL_VERSION = this.pgl.getString(PGL.VERSION);
        OPENGL_EXTENSIONS = this.pgl.getString(PGL.EXTENSIONS);
        GLSL_VERSION = this.pgl.getString(PGL.SHADING_LANGUAGE_VERSION);
        npotTexSupported = this.pgl.hasNpotTexSupport();
        autoMipmapGenSupported = this.pgl.hasAutoMipmapGenSupport();
        fboMultisampleSupported = this.pgl.hasFboMultisampleSupport();
        packedDepthStencilSupported = this.pgl.hasPackedDepthStencilSupport();
        anisoSamplingSupported = this.pgl.hasAnisoSamplingSupport();
        readBufferSupported = this.pgl.hasReadBuffer();
        drawBufferSupported = this.pgl.hasDrawBuffer();

        try {
            this.pgl.blendEquation(PGL.FUNC_ADD);
            blendEqSupported = true;
        } catch (Exception var2) {
            blendEqSupported = false;
        }

        depthBits = this.pgl.getDepthBits();
        stencilBits = this.pgl.getStencilBits();
        this.pgl.getIntegerv(PGL.MAX_TEXTURE_SIZE, intBuffer);
        maxTextureSize = intBuffer.get(0);
        if (!OPENGL_RENDERER.equals("VideoCore IV HW")) {
            this.pgl.getIntegerv(PGL.MAX_SAMPLES, intBuffer);
            maxSamples = intBuffer.get(0);
        }

        if (anisoSamplingSupported) {
            this.pgl.getFloatv(PGL.MAX_TEXTURE_MAX_ANISOTROPY, floatBuffer);
            maxAnisoAmount = floatBuffer.get(0);
        }

        if (OPENGL_RENDERER.equals("VideoCore IV HW") || OPENGL_RENDERER.equals("Gallium 0.4 on VC4")) {
            defLightShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/LightVert-vc4.glsl");
            defTexlightShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/TexLightVert-vc4.glsl");
        }

        glParamsRead = true;
    }

    public PShader loadShader(String fragFilename) {
        if (fragFilename != null && !fragFilename.equals("")) {
            int type = PShader.getShaderType(this.parent.loadStrings(fragFilename), 2);
            PShader shader = new PShader(this.parent);
            shader.setType(type);
            shader.setFragmentShader(fragFilename);
            String[] vertSource;
            if (type == 0) {
                vertSource = this.pgl.loadVertexShader(defPointShaderVertURL);
                shader.setVertexShader(vertSource);
            } else if (type == 1) {
                vertSource = this.pgl.loadVertexShader(defLineShaderVertURL);
                shader.setVertexShader(vertSource);
            } else if (type == 6) {
                vertSource = this.pgl.loadVertexShader(defTexlightShaderVertURL);
                shader.setVertexShader(vertSource);
            } else if (type == 4) {
                vertSource = this.pgl.loadVertexShader(defLightShaderVertURL);
                shader.setVertexShader(vertSource);
            } else if (type == 5) {
                vertSource = this.pgl.loadVertexShader(defTextureShaderVertURL);
                shader.setVertexShader(vertSource);
            } else if (type == 3) {
                vertSource = this.pgl.loadVertexShader(defColorShaderVertURL);
                shader.setVertexShader(vertSource);
            } else {
                vertSource = this.pgl.loadVertexShader(defTextureShaderVertURL);
                shader.setVertexShader(vertSource);
            }

            return shader;
        } else {
            PGraphics.showWarning("The fragment shader is missing, cannot create shader object");
            return null;
        }
    }

    public PShader loadShader(String fragFilename, String vertFilename) {
        PShader shader = null;
        if (fragFilename != null && !fragFilename.equals("")) {
            if (vertFilename != null && !vertFilename.equals("")) {
                shader = new PShader(this.parent, vertFilename, fragFilename);
            } else {
                PGraphics.showWarning("The vertex shader is missing, cannot create shader object");
            }
        } else {
            PGraphics.showWarning("The fragment shader is missing, cannot create shader object");
        }

        return shader;
    }

    public void shader(PShader shader) {
        this.flush();
        if (shader != null) {
            shader.init();
        }

        if (shader.isPolyShader()) {
            this.polyShader = shader;
        } else if (shader.isLineShader()) {
            this.lineShader = shader;
        } else if (shader.isPointShader()) {
            this.pointShader = shader;
        } else {
            PGraphics.showWarning("Unknown shader kind");
        }

    }

    public void shader(PShader shader, int kind) {
        this.flush();
        if (shader != null) {
            shader.init();
        }

        if (kind == 9) {
            this.polyShader = shader;
        } else if (kind == 5) {
            this.lineShader = shader;
        } else if (kind == 3) {
            this.pointShader = shader;
        } else {
            PGraphics.showWarning("Unknown shader kind");
        }

    }

    public void resetShader() {
        this.resetShader(9);
    }

    public void resetShader(int kind) {
        this.flush();
        if (kind != 9 && kind != 17 && kind != 20) {
            if (kind == 5) {
                this.lineShader = null;
            } else if (kind == 3) {
                this.pointShader = null;
            } else {
                PGraphics.showWarning("Unknown shader kind");
            }
        } else {
            this.polyShader = null;
        }

    }

    protected PShader getPolyShader(boolean lit, boolean tex) {
        PGraphicsOpenGL ppg = this.getPrimaryPG();
        boolean useDefault = this.polyShader == null;
        if (this.polyShader != null) {
            this.updateShader(this.polyShader);
        }

        PShader shader;
        if (lit) {
            if (tex) {
                if (!useDefault && this.isPolyShaderTexLight(this.polyShader)) {
                    shader = this.polyShader;
                } else {
                    if (ppg.defTexlightShader == null) {
                        ppg.defTexlightShader = this.loadShaderFromURL(defTexlightShaderFragURL, defTexlightShaderVertURL);
                    }

                    shader = ppg.defTexlightShader;
                }
            } else if (!useDefault && this.isPolyShaderLight(this.polyShader)) {
                shader = this.polyShader;
            } else {
                if (ppg.defLightShader == null) {
                    ppg.defLightShader = this.loadShaderFromURL(defLightShaderFragURL, defLightShaderVertURL);
                }

                shader = ppg.defLightShader;
            }
        } else {
            if (this.isPolyShaderUsingLights(this.polyShader)) {
                PGraphics.showWarning("The provided shader needs light attributes (ambient, diffuse, etc.), but the current scene is unlit, so the default shader will be used instead");
                useDefault = true;
            }

            if (tex) {
                if (!useDefault && this.isPolyShaderTex(this.polyShader)) {
                    shader = this.polyShader;
                } else {
                    if (ppg.defTextureShader == null) {
                        ppg.defTextureShader = this.loadShaderFromURL(defTextureShaderFragURL, defTextureShaderVertURL);
                    }

                    shader = ppg.defTextureShader;
                }
            } else if (!useDefault && this.isPolyShaderColor(this.polyShader)) {
                shader = this.polyShader;
            } else {
                if (ppg.defColorShader == null) {
                    ppg.defColorShader = this.loadShaderFromURL(defColorShaderFragURL, defColorShaderVertURL);
                }

                shader = ppg.defColorShader;
            }
        }

        if (shader != this.polyShader) {
            this.updateShader(shader);
        }

        this.updateShader(shader);
        return shader;
    }

    protected void updateShader(PShader shader) {
        shader.setRenderer(this);
        shader.loadAttributes();
        shader.loadUniforms();
    }

    protected PShader loadShaderFromURL(URL fragURL, URL vertURL) {
        String[] vertSource = this.pgl.loadVertexShader(vertURL);
        String[] fragSource = this.pgl.loadFragmentShader(fragURL);
        return new PShader(this.parent, vertSource, fragSource);
    }

    protected boolean isPolyShaderTexLight(PShader shader) {
        return shader.checkPolyType(6);
    }

    protected boolean isPolyShaderLight(PShader shader) {
        return shader.checkPolyType(4);
    }

    protected boolean isPolyShaderTex(PShader shader) {
        return shader.checkPolyType(5);
    }

    protected boolean isPolyShaderColor(PShader shader) {
        return shader.checkPolyType(3);
    }

    protected boolean isPolyShaderUsingLights(PShader shader) {
        return shader != null && shader.accessLightAttribs();
    }

    protected PShader getLineShader() {
        PGraphicsOpenGL ppg = this.getPrimaryPG();
        PShader shader;
        if (this.lineShader == null) {
            if (ppg.defLineShader == null) {
                String[] vertSource = this.pgl.loadVertexShader(defLineShaderVertURL);
                String[] fragSource = this.pgl.loadFragmentShader(defLineShaderFragURL);
                ppg.defLineShader = new PShader(this.parent, vertSource, fragSource);
            }

            shader = ppg.defLineShader;
        } else {
            shader = this.lineShader;
        }

        shader.setRenderer(this);
        shader.loadAttributes();
        shader.loadUniforms();
        return shader;
    }

    protected PShader getPointShader() {
        PGraphicsOpenGL ppg = this.getPrimaryPG();
        PShader shader;
        if (this.pointShader == null) {
            if (ppg.defPointShader == null) {
                String[] vertSource = this.pgl.loadVertexShader(defPointShaderVertURL);
                String[] fragSource = this.pgl.loadFragmentShader(defPointShaderFragURL);
                ppg.defPointShader = new PShader(this.parent, vertSource, fragSource);
            }

            shader = ppg.defPointShader;
        } else {
            shader = this.pointShader;
        }

        shader.setRenderer(this);
        shader.loadAttributes();
        shader.loadUniforms();
        return shader;
    }

    protected static int expandArraySize(int currSize, int newMinSize) {
        int newSize;
        for(newSize = currSize; newSize < newMinSize; newSize <<= 1) {
        }

        return newSize;
    }

    protected static PGraphicsOpenGL.AttributeMap newAttributeMap() {
        return new PGraphicsOpenGL.AttributeMap();
    }

    protected static PGraphicsOpenGL.InGeometry newInGeometry(PGraphicsOpenGL pg, PGraphicsOpenGL.AttributeMap attr, int mode) {
        return new PGraphicsOpenGL.InGeometry(pg, attr, mode);
    }

    protected static PGraphicsOpenGL.TessGeometry newTessGeometry(PGraphicsOpenGL pg, PGraphicsOpenGL.AttributeMap attr, int mode) {
        return new PGraphicsOpenGL.TessGeometry(pg, attr, mode);
    }

    protected static PGraphicsOpenGL.TexCache newTexCache(PGraphicsOpenGL pg) {
        return new PGraphicsOpenGL.TexCache(pg);
    }

    protected static class DepthSorter {
        static final int X = 0;
        static final int Y = 1;
        static final int Z = 2;
        static final int W = 3;
        static final int X0 = 0;
        static final int Y0 = 1;
        static final int Z0 = 2;
        static final int X1 = 3;
        static final int Y1 = 4;
        static final int Z1 = 5;
        static final int X2 = 6;
        static final int Y2 = 7;
        static final int Z2 = 8;
        int[] triangleIndices = new int[0];
        int[] texMap = new int[0];
        int[] voffsetMap = new int[0];
        float[] minXBuffer = new float[0];
        float[] minYBuffer = new float[0];
        float[] minZBuffer = new float[0];
        float[] maxXBuffer = new float[0];
        float[] maxYBuffer = new float[0];
        float[] maxZBuffer = new float[0];
        float[] screenVertices = new float[0];
        float[] triA = new float[9];
        float[] triB = new float[9];
        BitSet marked = new BitSet();
        BitSet swapped = new BitSet();
        PGraphicsOpenGL pg;

        DepthSorter(PGraphicsOpenGL pg) {
            this.pg = pg;
        }

        void checkIndexBuffers(int newTriangleCount) {
            if (this.triangleIndices.length < newTriangleCount) {
                int newSize = (newTriangleCount / 4 + 1) * 5;
                this.triangleIndices = new int[newSize];
                this.texMap = new int[newSize];
                this.voffsetMap = new int[newSize];
                this.minXBuffer = new float[newSize];
                this.minYBuffer = new float[newSize];
                this.minZBuffer = new float[newSize];
                this.maxXBuffer = new float[newSize];
                this.maxYBuffer = new float[newSize];
                this.maxZBuffer = new float[newSize];
            }

        }

        void checkVertexBuffer(int newVertexCount) {
            int coordCount = 3 * newVertexCount;
            if (this.screenVertices.length < coordCount) {
                int newSize = (coordCount / 4 + 1) * 5;
                this.screenVertices = new float[newSize];
            }

        }

        void sort(PGraphicsOpenGL.TessGeometry tessGeo) {
            int triangleCount = tessGeo.polyIndexCount / 3;
            this.checkIndexBuffers(triangleCount);
            int[] triangleIndices = this.triangleIndices;
            int[] texMap = this.texMap;
            int[] voffsetMap = this.voffsetMap;

            int polyVertexCount;
            for(polyVertexCount = 0; polyVertexCount < triangleCount; triangleIndices[polyVertexCount] = polyVertexCount++) {
            }

            PGraphicsOpenGL.TexCache texCache = this.pg.texCache;
            PGraphicsOpenGL.IndexCache indexCache = tessGeo.polyIndexCache;

            int i;
            int activeTid;
            int testTid;
            for(int i = 0; i < texCache.size; ++i) {
                int first = texCache.firstCache[i];
                i = texCache.lastCache[i];

                for(activeTid = first; activeTid <= i; ++activeTid) {
                    int ioffset = activeTid == first ? texCache.firstIndex[i] : indexCache.indexOffset[activeTid];
                    int icount = activeTid == i ? texCache.lastIndex[i] - ioffset + 1 : indexCache.indexOffset[activeTid] + indexCache.indexCount[activeTid] - ioffset;

                    for(testTid = ioffset / 3; testTid < (ioffset + icount) / 3; ++testTid) {
                        texMap[testTid] = i;
                        voffsetMap[testTid] = activeTid;
                    }
                }
            }

            polyVertexCount = tessGeo.polyVertexCount;
            this.checkVertexBuffer(polyVertexCount);
            float[] screenVertices = this.screenVertices;
            float[] polyVertices = tessGeo.polyVertices;
            PMatrix3D projection = this.pg.projection;

            float minXA;
            float maxXA;
            for(i = 0; i < polyVertexCount; ++i) {
                float x = polyVertices[4 * i + 0];
                float y = polyVertices[4 * i + 1];
                float z = polyVertices[4 * i + 2];
                float w = polyVertices[4 * i + 3];
                float ox = projection.m00 * x + projection.m01 * y + projection.m02 * z + projection.m03 * w;
                float oy = projection.m10 * x + projection.m11 * y + projection.m12 * z + projection.m13 * w;
                minXA = projection.m20 * x + projection.m21 * y + projection.m22 * z + projection.m23 * w;
                maxXA = projection.m30 * x + projection.m31 * y + projection.m32 * z + projection.m33 * w;
                if (PGraphicsOpenGL.nonZero(maxXA)) {
                    ox /= maxXA;
                    oy /= maxXA;
                    minXA /= maxXA;
                }

                screenVertices[3 * i + 0] = ox;
                screenVertices[3 * i + 1] = oy;
                screenVertices[3 * i + 2] = -minXA;
            }

            float[] screenVertices = this.screenVertices;
            int[] vertexOffset = tessGeo.polyIndexCache.vertexOffset;
            short[] polyIndices = tessGeo.polyIndices;
            float[] triA = this.triA;
            float[] triB = this.triB;

            for(activeTid = 0; activeTid < triangleCount; ++activeTid) {
                fetchTriCoords(triA, activeTid, vertexOffset, voffsetMap, screenVertices, polyIndices);
                this.minXBuffer[activeTid] = PApplet.min(triA[0], triA[3], triA[6]);
                this.maxXBuffer[activeTid] = PApplet.max(triA[0], triA[3], triA[6]);
                this.minYBuffer[activeTid] = PApplet.min(triA[1], triA[4], triA[7]);
                this.maxYBuffer[activeTid] = PApplet.max(triA[1], triA[4], triA[7]);
                this.minZBuffer[activeTid] = PApplet.min(triA[2], triA[5], triA[8]);
                this.maxZBuffer[activeTid] = PApplet.max(triA[2], triA[5], triA[8]);
            }

            sortByMinZ(0, triangleCount - 1, triangleIndices, this.minZBuffer);
            activeTid = 0;
            BitSet marked = this.marked;
            BitSet swapped = this.swapped;
            marked.clear();

            int tti;
            label103:
            for(; activeTid < triangleCount; ++activeTid) {
                testTid = activeTid + 1;
                boolean draw = false;
                swapped.clear();
                int ati = triangleIndices[activeTid];
                minXA = this.minXBuffer[ati];
                maxXA = this.maxXBuffer[ati];
                float minYA = this.minYBuffer[ati];
                float maxYA = this.maxYBuffer[ati];
                float maxZA = this.maxZBuffer[ati];
                fetchTriCoords(triA, ati, vertexOffset, voffsetMap, screenVertices, polyIndices);

                while(true) {
                    while(true) {
                        if (draw || testTid >= triangleCount) {
                            continue label103;
                        }

                        tti = triangleIndices[testTid];
                        if (maxZA <= this.minZBuffer[tti] && !marked.get(tti)) {
                            draw = true;
                        } else if (maxXA > this.minXBuffer[tti] && maxYA > this.minYBuffer[tti] && minXA < this.maxXBuffer[tti] && minYA < this.maxYBuffer[tti]) {
                            fetchTriCoords(triB, tti, vertexOffset, voffsetMap, screenVertices, polyIndices);
                            if (side(triB, triA, -1.0F) > 0) {
                                ++testTid;
                            } else if (side(triA, triB, 1.0F) > 0) {
                                ++testTid;
                            } else if (!swapped.get(tti)) {
                                swapped.set(ati);
                                marked.set(tti);
                                rotateRight(triangleIndices, activeTid, testTid);
                                ati = tti;
                                System.arraycopy(triB, 0, triA, 0, 9);
                                minXA = this.minXBuffer[tti];
                                maxXA = this.maxXBuffer[tti];
                                minYA = this.minYBuffer[tti];
                                maxYA = this.maxYBuffer[tti];
                                maxZA = this.maxZBuffer[tti];
                                testTid = activeTid + 1;
                            } else {
                                ++testTid;
                            }
                        } else {
                            ++testTid;
                        }
                    }
                }
            }

            for(testTid = 0; testTid < triangleCount; ++testTid) {
                int mappedId = triangleIndices[testTid];
                if (testTid != mappedId) {
                    short i0 = polyIndices[3 * testTid + 0];
                    short i1 = polyIndices[3 * testTid + 1];
                    short i2 = polyIndices[3 * testTid + 2];
                    int texId = texMap[testTid];
                    int voffsetId = voffsetMap[testTid];
                    int currId = testTid;
                    tti = mappedId;

                    do {
                        triangleIndices[currId] = currId;
                        polyIndices[3 * currId + 0] = polyIndices[3 * tti + 0];
                        polyIndices[3 * currId + 1] = polyIndices[3 * tti + 1];
                        polyIndices[3 * currId + 2] = polyIndices[3 * tti + 2];
                        texMap[currId] = texMap[tti];
                        voffsetMap[currId] = voffsetMap[tti];
                        currId = tti;
                        tti = triangleIndices[tti];
                    } while(tti != testTid);

                    triangleIndices[currId] = currId;
                    polyIndices[3 * currId + 0] = i0;
                    polyIndices[3 * currId + 1] = i1;
                    polyIndices[3 * currId + 2] = i2;
                    texMap[currId] = texId;
                    voffsetMap[currId] = voffsetId;
                }
            }

        }

        static void fetchTriCoords(float[] tri, int ti, int[] vertexOffset, int[] voffsetMap, float[] screenVertices, short[] polyIndices) {
            int voffset = vertexOffset[voffsetMap[ti]];
            int i0 = 3 * (voffset + polyIndices[3 * ti + 0]);
            int i1 = 3 * (voffset + polyIndices[3 * ti + 1]);
            int i2 = 3 * (voffset + polyIndices[3 * ti + 2]);
            tri[0] = screenVertices[i0 + 0];
            tri[1] = screenVertices[i0 + 1];
            tri[2] = screenVertices[i0 + 2];
            tri[3] = screenVertices[i1 + 0];
            tri[4] = screenVertices[i1 + 1];
            tri[5] = screenVertices[i1 + 2];
            tri[6] = screenVertices[i2 + 0];
            tri[7] = screenVertices[i2 + 1];
            tri[8] = screenVertices[i2 + 2];
        }

        static void sortByMinZ(int leftTid, int rightTid, int[] triangleIndices, float[] minZBuffer) {
            swap(triangleIndices, leftTid, (leftTid + rightTid) / 2);
            int k = leftTid;
            float leftMinZ = minZBuffer[triangleIndices[leftTid]];

            for(int tid = leftTid + 1; tid <= rightTid; ++tid) {
                float minZ = minZBuffer[triangleIndices[tid]];
                if (minZ < leftMinZ) {
                    ++k;
                    swap(triangleIndices, k, tid);
                }
            }

            swap(triangleIndices, leftTid, k);
            if (leftTid < k - 1) {
                sortByMinZ(leftTid, k - 1, triangleIndices, minZBuffer);
            }

            if (k + 1 < rightTid) {
                sortByMinZ(k + 1, rightTid, triangleIndices, minZBuffer);
            }

        }

        static int side(float[] tri1, float[] tri2, float tz) {
            float distTest = tri1[3] - tri1[0];
            float distA = tri1[6] - tri1[0];
            float distB = tri1[4] - tri1[1];
            float distC = tri1[7] - tri1[1];
            float absA = tri1[5] - tri1[2];
            float absB = tri1[8] - tri1[2];
            float Dx = distB * absB - absA * distC;
            float Dy = absA * distA - distTest * absB;
            float Dz = distTest * distC - distB * distA;
            float absC = 1.0F / (float)Math.sqrt((double)(Dx * Dx + Dy * Dy + Dz * Dz));
            Dx *= absC;
            Dy *= absC;
            Dz *= absC;
            float Dw = -dot(Dx, Dy, Dz, tri1[0], tri1[1], tri1[2]);
            distTest = dot(Dx, Dy, Dz, tri1[0], tri1[1], tri1[2] + 100.0F * tz) + Dw;
            distA = dot(Dx, Dy, Dz, tri2[0], tri2[1], tri2[2]) + Dw;
            distB = dot(Dx, Dy, Dz, tri2[3], tri2[4], tri2[5]) + Dw;
            distC = dot(Dx, Dy, Dz, tri2[6], tri2[7], tri2[8]) + Dw;
            absA = PApplet.abs(distA);
            absB = PApplet.abs(distB);
            absC = PApplet.abs(distC);
            float eps = PApplet.max(absA, absB, absC) * 0.1F;
            float sideA = (absA < eps ? 0.0F : distA) * distTest;
            float sideB = (absB < eps ? 0.0F : distB) * distTest;
            float sideC = (absC < eps ? 0.0F : distC) * distTest;
            boolean sameSide = sideA >= 0.0F && sideB >= 0.0F && sideC >= 0.0F;
            boolean notSameSide = sideA <= 0.0F && sideB <= 0.0F && sideC <= 0.0F;
            return sameSide ? 1 : (notSameSide ? -1 : 0);
        }

        static float dot(float a1, float a2, float a3, float b1, float b2, float b3) {
            return a1 * b1 + a2 * b2 + a3 * b3;
        }

        static void swap(int[] array, int i1, int i2) {
            int temp = array[i1];
            array[i1] = array[i2];
            array[i2] = temp;
        }

        static void rotateRight(int[] array, int i1, int i2) {
            if (i1 != i2) {
                int temp = array[i2];
                System.arraycopy(array, i1, array, i1 + 1, i2 - i1);
                array[i1] = temp;
            }
        }
    }

    protected static class Tessellator {
        PGraphicsOpenGL.InGeometry in;
        PGraphicsOpenGL.TessGeometry tess;
        PGraphicsOpenGL.TexCache texCache;
        PImage prevTexImage;
        PImage newTexImage;
        int firstTexIndex;
        int firstTexCache;
        processing.opengl.PGL.Tessellator gluTess;
        PGraphicsOpenGL.Tessellator.TessellatorCallback callback;
        boolean fill;
        boolean stroke;
        int strokeColor;
        float strokeWeight;
        int strokeJoin;
        int strokeCap;
        boolean accurate2DStrokes = true;
        PMatrix transform = null;
        float transformScale;
        boolean is2D = false;
        boolean is3D = true;
        protected PGraphicsOpenGL pg;
        int[] rawIndices = new int[512];
        int rawSize;
        int[] dupIndices;
        int dupCount;
        int firstPolyIndexCache;
        int lastPolyIndexCache;
        int firstLineIndexCache;
        int lastLineIndexCache;
        int firstPointIndexCache;
        int lastPointIndexCache;
        float[] strokeVertices;
        int[] strokeColors;
        float[] strokeWeights;
        int pathVertexCount;
        float[] pathVertices;
        int[] pathColors;
        float[] pathWeights;
        int beginPath;

        public Tessellator() {
        }

        void initGluTess() {
            if (this.gluTess == null) {
                this.callback = new PGraphicsOpenGL.Tessellator.TessellatorCallback(this.tess.polyAttribs);
                this.gluTess = this.pg.pgl.createTessellator(this.callback);
            }

        }

        void setInGeometry(PGraphicsOpenGL.InGeometry in) {
            this.in = in;
            this.firstPolyIndexCache = -1;
            this.lastPolyIndexCache = -1;
            this.firstLineIndexCache = -1;
            this.lastLineIndexCache = -1;
            this.firstPointIndexCache = -1;
            this.lastPointIndexCache = -1;
        }

        void setTessGeometry(PGraphicsOpenGL.TessGeometry tess) {
            this.tess = tess;
        }

        void setFill(boolean fill) {
            this.fill = fill;
        }

        void setTexCache(PGraphicsOpenGL.TexCache texCache, PImage newTexImage) {
            this.texCache = texCache;
            this.newTexImage = newTexImage;
        }

        void setStroke(boolean stroke) {
            this.stroke = stroke;
        }

        void setStrokeColor(int color) {
            this.strokeColor = PGL.javaToNativeARGB(color);
        }

        void setStrokeWeight(float weight) {
            this.strokeWeight = weight;
        }

        void setStrokeCap(int strokeCap) {
            this.strokeCap = strokeCap;
        }

        void setStrokeJoin(int strokeJoin) {
            this.strokeJoin = strokeJoin;
        }

        void setAccurate2DStrokes(boolean accurate) {
            this.accurate2DStrokes = accurate;
        }

        protected void setRenderer(PGraphicsOpenGL pg) {
            this.pg = pg;
        }

        void set3D(boolean value) {
            if (value) {
                this.is2D = false;
                this.is3D = true;
            } else {
                this.is2D = true;
                this.is3D = false;
            }

        }

        void setTransform(PMatrix transform) {
            this.transform = transform;
            this.transformScale = -1.0F;
        }

        void resetCurveVertexCount() {
            this.pg.curveVertexCount = 0;
        }

        void tessellatePoints() {
            if (this.strokeCap == 2) {
                this.tessellateRoundPoints();
            } else {
                this.tessellateSquarePoints();
            }

        }

        void tessellateRoundPoints() {
            int nInVert = this.in.vertexCount;
            if (this.stroke && 1 <= nInVert) {
                int nPtVert = PApplet.min(200, PApplet.max(20, (int)(6.2831855F * this.strokeWeight / 10.0F))) + 1;
                if (PGL.MAX_VERTEX_INDEX1 <= nPtVert) {
                    throw new RuntimeException("Error in point tessellation.");
                }

                this.updateTex();
                int nvertTot = nPtVert * nInVert;
                int nindTot = 3 * (nPtVert - 1) * nInVert;
                if (this.is3D) {
                    this.tessellateRoundPoints3D(nvertTot, nindTot, nPtVert);
                } else if (this.is2D) {
                    this.beginNoTex();
                    this.tessellateRoundPoints2D(nvertTot, nindTot, nPtVert);
                    this.endNoTex();
                }
            }

        }

        void tessellateRoundPoints3D(int nvertTot, int nindTot, int nPtVert) {
            int perim = nPtVert - 1;
            this.tess.pointVertexCheck(nvertTot);
            this.tess.pointIndexCheck(nindTot);
            int vertIdx = this.tess.firstPointVertex;
            int attribIdx = this.tess.firstPointVertex;
            int indIdx = this.tess.firstPointIndex;
            PGraphicsOpenGL.IndexCache cache = this.tess.pointIndexCache;
            int index = this.in.renderMode == 1 ? cache.addNew() : cache.getLast();
            this.firstPointIndexCache = index;

            for(int i = 0; i < this.in.vertexCount; ++i) {
                int count = cache.vertexCount[index];
                if (PGL.MAX_VERTEX_INDEX1 <= count + nPtVert) {
                    index = cache.addNew();
                    count = 0;
                }

                for(int k = 0; k < nPtVert; ++k) {
                    this.tess.setPointVertex(vertIdx, this.in, i);
                    ++vertIdx;
                }

                this.tess.pointOffsets[2 * attribIdx + 0] = 0.0F;
                this.tess.pointOffsets[2 * attribIdx + 1] = 0.0F;
                ++attribIdx;
                float val = 0.0F;
                float inc = 720.0F / (float)perim;

                int k;
                for(k = 0; k < perim; ++k) {
                    this.tess.pointOffsets[2 * attribIdx + 0] = 0.5F * PGraphicsOpenGL.cosLUT[(int)val] * this.transformScale() * this.strokeWeight;
                    this.tess.pointOffsets[2 * attribIdx + 1] = 0.5F * PGraphicsOpenGL.sinLUT[(int)val] * this.transformScale() * this.strokeWeight;
                    val = (val + inc) % 720.0F;
                    ++attribIdx;
                }

                for(k = 1; k < nPtVert - 1; ++k) {
                    this.tess.pointIndices[indIdx++] = (short)(count + 0);
                    this.tess.pointIndices[indIdx++] = (short)(count + k);
                    this.tess.pointIndices[indIdx++] = (short)(count + k + 1);
                }

                this.tess.pointIndices[indIdx++] = (short)(count + 0);
                this.tess.pointIndices[indIdx++] = (short)(count + 1);
                this.tess.pointIndices[indIdx++] = (short)(count + nPtVert - 1);
                cache.incCounts(index, 3 * (nPtVert - 1), nPtVert);
            }

            this.lastPointIndexCache = index;
        }

        void tessellateRoundPoints2D(int nvertTot, int nindTot, int nPtVert) {
            int perim = nPtVert - 1;
            this.tess.polyVertexCheck(nvertTot);
            this.tess.polyIndexCheck(nindTot);
            int vertIdx = this.tess.firstPolyVertex;
            int indIdx = this.tess.firstPolyIndex;
            PGraphicsOpenGL.IndexCache cache = this.tess.polyIndexCache;
            int index = this.in.renderMode == 1 ? cache.addNew() : cache.getLast();
            this.firstPointIndexCache = index;
            if (this.firstPolyIndexCache == -1) {
                this.firstPolyIndexCache = index;
            }

            for(int i = 0; i < this.in.vertexCount; ++i) {
                int count = cache.vertexCount[index];
                if (PGL.MAX_VERTEX_INDEX1 <= count + nPtVert) {
                    index = cache.addNew();
                    count = 0;
                }

                float x0 = this.in.vertices[3 * i + 0];
                float y0 = this.in.vertices[3 * i + 1];
                int rgba = this.in.strokeColors[i];
                float val = 0.0F;
                float inc = 720.0F / (float)perim;
                this.tess.setPolyVertex(vertIdx, x0, y0, 0.0F, rgba, false);
                ++vertIdx;

                int k;
                for(k = 0; k < perim; ++k) {
                    this.tess.setPolyVertex(vertIdx, x0 + 0.5F * PGraphicsOpenGL.cosLUT[(int)val] * this.strokeWeight, y0 + 0.5F * PGraphicsOpenGL.sinLUT[(int)val] * this.strokeWeight, 0.0F, rgba, false);
                    ++vertIdx;
                    val = (val + inc) % 720.0F;
                }

                for(k = 1; k < nPtVert - 1; ++k) {
                    this.tess.polyIndices[indIdx++] = (short)(count + 0);
                    this.tess.polyIndices[indIdx++] = (short)(count + k);
                    this.tess.polyIndices[indIdx++] = (short)(count + k + 1);
                }

                this.tess.polyIndices[indIdx++] = (short)(count + 0);
                this.tess.polyIndices[indIdx++] = (short)(count + 1);
                this.tess.polyIndices[indIdx++] = (short)(count + nPtVert - 1);
                cache.incCounts(index, 3 * (nPtVert - 1), nPtVert);
            }

            this.lastPointIndexCache = this.lastPolyIndexCache = index;
        }

        void tessellateSquarePoints() {
            int nInVert = this.in.vertexCount;
            if (this.stroke && 1 <= nInVert) {
                this.updateTex();
                int nvertTot = 5 * nInVert;
                int nindTot = 12 * nInVert;
                if (this.is3D) {
                    this.tessellateSquarePoints3D(nvertTot, nindTot);
                } else if (this.is2D) {
                    this.beginNoTex();
                    this.tessellateSquarePoints2D(nvertTot, nindTot);
                    this.endNoTex();
                }
            }

        }

        void tessellateSquarePoints3D(int nvertTot, int nindTot) {
            this.tess.pointVertexCheck(nvertTot);
            this.tess.pointIndexCheck(nindTot);
            int vertIdx = this.tess.firstPointVertex;
            int attribIdx = this.tess.firstPointVertex;
            int indIdx = this.tess.firstPointIndex;
            PGraphicsOpenGL.IndexCache cache = this.tess.pointIndexCache;
            int index = this.in.renderMode == 1 ? cache.addNew() : cache.getLast();
            this.firstPointIndexCache = index;

            for(int i = 0; i < this.in.vertexCount; ++i) {
                int nvert = 5;
                int count = cache.vertexCount[index];
                if (PGL.MAX_VERTEX_INDEX1 <= count + nvert) {
                    index = cache.addNew();
                    count = 0;
                }

                int k;
                for(k = 0; k < nvert; ++k) {
                    this.tess.setPointVertex(vertIdx, this.in, i);
                    ++vertIdx;
                }

                this.tess.pointOffsets[2 * attribIdx + 0] = 0.0F;
                this.tess.pointOffsets[2 * attribIdx + 1] = 0.0F;
                ++attribIdx;

                for(k = 0; k < 4; ++k) {
                    this.tess.pointOffsets[2 * attribIdx + 0] = 0.5F * PGraphicsOpenGL.QUAD_POINT_SIGNS[k][0] * this.transformScale() * this.strokeWeight;
                    this.tess.pointOffsets[2 * attribIdx + 1] = 0.5F * PGraphicsOpenGL.QUAD_POINT_SIGNS[k][1] * this.transformScale() * this.strokeWeight;
                    ++attribIdx;
                }

                for(k = 1; k < nvert - 1; ++k) {
                    this.tess.pointIndices[indIdx++] = (short)(count + 0);
                    this.tess.pointIndices[indIdx++] = (short)(count + k);
                    this.tess.pointIndices[indIdx++] = (short)(count + k + 1);
                }

                this.tess.pointIndices[indIdx++] = (short)(count + 0);
                this.tess.pointIndices[indIdx++] = (short)(count + 1);
                this.tess.pointIndices[indIdx++] = (short)(count + nvert - 1);
                cache.incCounts(index, 12, 5);
            }

            this.lastPointIndexCache = index;
        }

        void tessellateSquarePoints2D(int nvertTot, int nindTot) {
            this.tess.polyVertexCheck(nvertTot);
            this.tess.polyIndexCheck(nindTot);
            boolean clamp = this.clampSquarePoints2D();
            int vertIdx = this.tess.firstPolyVertex;
            int indIdx = this.tess.firstPolyIndex;
            PGraphicsOpenGL.IndexCache cache = this.tess.polyIndexCache;
            int index = this.in.renderMode == 1 ? cache.addNew() : cache.getLast();
            this.firstPointIndexCache = index;
            if (this.firstPolyIndexCache == -1) {
                this.firstPolyIndexCache = index;
            }

            for(int i = 0; i < this.in.vertexCount; ++i) {
                int nvert = 5;
                int count = cache.vertexCount[index];
                if (PGL.MAX_VERTEX_INDEX1 <= count + nvert) {
                    index = cache.addNew();
                    count = 0;
                }

                float x0 = this.in.vertices[3 * i + 0];
                float y0 = this.in.vertices[3 * i + 1];
                int rgba = this.in.strokeColors[i];
                this.tess.setPolyVertex(vertIdx, x0, y0, 0.0F, rgba, clamp);
                ++vertIdx;

                int k;
                for(k = 0; k < nvert - 1; ++k) {
                    this.tess.setPolyVertex(vertIdx, x0 + 0.5F * PGraphicsOpenGL.QUAD_POINT_SIGNS[k][0] * this.strokeWeight, y0 + 0.5F * PGraphicsOpenGL.QUAD_POINT_SIGNS[k][1] * this.strokeWeight, 0.0F, rgba, clamp);
                    ++vertIdx;
                }

                for(k = 1; k < nvert - 1; ++k) {
                    this.tess.polyIndices[indIdx++] = (short)(count + 0);
                    this.tess.polyIndices[indIdx++] = (short)(count + k);
                    this.tess.polyIndices[indIdx++] = (short)(count + k + 1);
                }

                this.tess.polyIndices[indIdx++] = (short)(count + 0);
                this.tess.polyIndices[indIdx++] = (short)(count + 1);
                this.tess.polyIndices[indIdx++] = (short)(count + nvert - 1);
                cache.incCounts(index, 12, 5);
            }

            this.lastPointIndexCache = this.lastPolyIndexCache = index;
        }

        boolean clamp2D() {
            return this.is2D && this.tess.renderMode == 0 && PGraphicsOpenGL.zero(this.pg.modelview.m01) && PGraphicsOpenGL.zero(this.pg.modelview.m10);
        }

        boolean clampSquarePoints2D() {
            return this.clamp2D();
        }

        void tessellateLines() {
            int nInVert = this.in.vertexCount;
            if (this.stroke && 2 <= nInVert) {
                this.strokeVertices = this.in.vertices;
                this.strokeColors = this.in.strokeColors;
                this.strokeWeights = this.in.strokeWeights;
                this.updateTex();
                int lineCount = nInVert / 2;
                if (this.is3D) {
                    this.tessellateLines3D(lineCount);
                } else if (this.is2D) {
                    this.beginNoTex();
                    this.tessellateLines2D(lineCount);
                    this.endNoTex();
                }
            }

        }

        void tessellateLines3D(int lineCount) {
            int nvert = lineCount * 4;
            int nind = lineCount * 2 * 3;
            int vcount0 = this.tess.lineVertexCount;
            int icount0 = this.tess.lineIndexCount;
            this.tess.lineVertexCheck(nvert);
            this.tess.lineIndexCheck(nind);
            int index = this.in.renderMode == 1 ? this.tess.lineIndexCache.addNew() : this.tess.lineIndexCache.getLast();
            this.firstLineIndexCache = index;
            int[] tmp = new int[]{0, 0};
            this.tess.lineIndexCache.setCounter(tmp);

            for(int ln = 0; ln < lineCount; ++ln) {
                int i0 = 2 * ln + 0;
                int i1 = 2 * ln + 1;
                index = this.addLineSegment3D(i0, i1, i0 - 2, i1 - 1, index, (short[])null, false);
            }

            this.tess.lineIndexCache.setCounter((int[])null);
            this.tess.lineIndexCount = icount0 + tmp[0];
            this.tess.lineVertexCount = vcount0 + tmp[1];
            this.lastLineIndexCache = index;
        }

        void tessellateLines2D(int lineCount) {
            int nvert = lineCount * 4;
            int nind = lineCount * 2 * 3;
            int ln;
            int i0;
            if (this.noCapsJoins(nvert)) {
                this.tess.polyVertexCheck(nvert);
                this.tess.polyIndexCheck(nind);
                int index = this.in.renderMode == 1 ? this.tess.polyIndexCache.addNew() : this.tess.polyIndexCache.getLast();
                this.firstLineIndexCache = index;
                if (this.firstPolyIndexCache == -1) {
                    this.firstPolyIndexCache = index;
                }

                boolean clamp = this.clampLines2D(lineCount);

                for(ln = 0; ln < lineCount; ++ln) {
                    i0 = 2 * ln + 0;
                    int i1 = 2 * ln + 1;
                    index = this.addLineSegment2D(i0, i1, index, false, clamp);
                }

                this.lastLineIndexCache = this.lastPolyIndexCache = index;
            } else {
                LinePath path = new LinePath(1);

                for(int ln = 0; ln < lineCount; ++ln) {
                    ln = 2 * ln + 0;
                    i0 = 2 * ln + 1;
                    path.moveTo(this.in.vertices[3 * ln + 0], this.in.vertices[3 * ln + 1], this.in.strokeColors[ln]);
                    path.lineTo(this.in.vertices[3 * i0 + 0], this.in.vertices[3 * i0 + 1], this.in.strokeColors[i0]);
                }

                this.tessellateLinePath(path);
            }

        }

        boolean clampLines2D(int lineCount) {
            boolean res = this.clamp2D();
            if (res) {
                for(int ln = 0; ln < lineCount; ++ln) {
                    int i0 = 2 * ln + 0;
                    int i1 = 2 * ln + 1;
                    res = this.segmentIsAxisAligned(i0, i1);
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateLineStrip() {
            int nInVert = this.in.vertexCount;
            if (this.stroke && 2 <= nInVert) {
                this.strokeVertices = this.in.vertices;
                this.strokeColors = this.in.strokeColors;
                this.strokeWeights = this.in.strokeWeights;
                this.updateTex();
                int lineCount = nInVert - 1;
                if (this.is3D) {
                    this.tessellateLineStrip3D(lineCount);
                } else if (this.is2D) {
                    this.beginNoTex();
                    this.tessellateLineStrip2D(lineCount);
                    this.endNoTex();
                }
            }

        }

        void tessellateLineStrip3D(int lineCount) {
            int nBevelTr = this.noCapsJoins() ? 0 : lineCount - 1;
            int nvert = lineCount * 4 + nBevelTr * 3;
            int nind = lineCount * 2 * 3 + nBevelTr * 2 * 3;
            int vcount0 = this.tess.lineVertexCount;
            int icount0 = this.tess.lineIndexCount;
            this.tess.lineVertexCheck(nvert);
            this.tess.lineIndexCheck(nind);
            int index = this.in.renderMode == 1 ? this.tess.lineIndexCache.addNew() : this.tess.lineIndexCache.getLast();
            this.firstLineIndexCache = index;
            int i0 = 0;
            short[] lastInd = new short[]{-1, -1};
            int[] tmp = new int[]{0, 0};
            this.tess.lineIndexCache.setCounter(tmp);

            for(int ln = 0; ln < lineCount; ++ln) {
                int i1 = ln + 1;
                if (0 < nBevelTr) {
                    index = this.addLineSegment3D(i0, i1, i1 - 2, i1 - 1, index, lastInd, false);
                } else {
                    index = this.addLineSegment3D(i0, i1, i1 - 2, i1 - 1, index, (short[])null, false);
                }

                i0 = i1;
            }

            this.tess.lineIndexCache.setCounter((int[])null);
            this.tess.lineIndexCount = icount0 + tmp[0];
            this.tess.lineVertexCount = vcount0 + tmp[1];
            this.lastLineIndexCache = index;
        }

        void tessellateLineStrip2D(int lineCount) {
            int nvert = lineCount * 4;
            int nind = lineCount * 2 * 3;
            int i0;
            if (this.noCapsJoins(nvert)) {
                this.tess.polyVertexCheck(nvert);
                this.tess.polyIndexCheck(nind);
                int index = this.in.renderMode == 1 ? this.tess.polyIndexCache.addNew() : this.tess.polyIndexCache.getLast();
                this.firstLineIndexCache = index;
                if (this.firstPolyIndexCache == -1) {
                    this.firstPolyIndexCache = index;
                }

                i0 = 0;
                boolean clamp = this.clampLineStrip2D(lineCount);

                for(int ln = 0; ln < lineCount; ++ln) {
                    int i1 = ln + 1;
                    index = this.addLineSegment2D(i0, i1, index, false, clamp);
                    i0 = i1;
                }

                this.lastLineIndexCache = this.lastPolyIndexCache = index;
            } else {
                LinePath path = new LinePath(1);
                path.moveTo(this.in.vertices[0], this.in.vertices[1], this.in.strokeColors[0]);

                for(i0 = 0; i0 < lineCount; ++i0) {
                    int i1 = i0 + 1;
                    path.lineTo(this.in.vertices[3 * i1 + 0], this.in.vertices[3 * i1 + 1], this.in.strokeColors[i1]);
                }

                this.tessellateLinePath(path);
            }

        }

        boolean clampLineStrip2D(int lineCount) {
            boolean res = this.clamp2D();
            if (res) {
                for(int ln = 0; ln < lineCount; ++ln) {
                    res = this.segmentIsAxisAligned(0, ln + 1);
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateLineLoop() {
            int nInVert = this.in.vertexCount;
            if (this.stroke && 2 <= nInVert) {
                this.strokeVertices = this.in.vertices;
                this.strokeColors = this.in.strokeColors;
                this.strokeWeights = this.in.strokeWeights;
                this.updateTex();
                if (this.is3D) {
                    this.tessellateLineLoop3D(nInVert);
                } else if (this.is2D) {
                    this.beginNoTex();
                    this.tessellateLineLoop2D(nInVert);
                    this.endNoTex();
                }
            }

        }

        void tessellateLineLoop3D(int lineCount) {
            int nBevelTr = this.noCapsJoins() ? 0 : lineCount;
            int nvert = lineCount * 4 + nBevelTr * 3;
            int nind = lineCount * 2 * 3 + nBevelTr * 2 * 3;
            int vcount0 = this.tess.lineVertexCount;
            int icount0 = this.tess.lineIndexCount;
            this.tess.lineVertexCheck(nvert);
            this.tess.lineIndexCheck(nind);
            int index = this.in.renderMode == 1 ? this.tess.lineIndexCache.addNew() : this.tess.lineIndexCache.getLast();
            this.firstLineIndexCache = index;
            int i0 = 0;
            int i1 = -1;
            short[] lastInd = new short[]{-1, -1};
            int[] tmp = new int[]{0, 0};
            this.tess.lineIndexCache.setCounter(tmp);

            for(int ln = 0; ln < lineCount - 1; ++ln) {
                i1 = ln + 1;
                if (0 < nBevelTr) {
                    index = this.addLineSegment3D(i0, i1, i1 - 2, i1 - 1, index, lastInd, false);
                } else {
                    index = this.addLineSegment3D(i0, i1, i1 - 2, i1 - 1, index, (short[])null, false);
                }

                i0 = i1;
            }

            index = this.addLineSegment3D(this.in.vertexCount - 1, 0, i1 - 2, i1 - 1, index, lastInd, false);
            if (0 < nBevelTr) {
                index = this.addBevel3D(0, 1, this.in.vertexCount - 1, 0, index, lastInd, false);
            }

            this.tess.lineIndexCache.setCounter((int[])null);
            this.tess.lineIndexCount = icount0 + tmp[0];
            this.tess.lineVertexCount = vcount0 + tmp[1];
            this.lastLineIndexCache = index;
        }

        void tessellateLineLoop2D(int lineCount) {
            int nvert = lineCount * 4;
            int nind = lineCount * 2 * 3;
            int i0;
            if (this.noCapsJoins(nvert)) {
                this.tess.polyVertexCheck(nvert);
                this.tess.polyIndexCheck(nind);
                int index = this.in.renderMode == 1 ? this.tess.polyIndexCache.addNew() : this.tess.polyIndexCache.getLast();
                this.firstLineIndexCache = index;
                if (this.firstPolyIndexCache == -1) {
                    this.firstPolyIndexCache = index;
                }

                i0 = 0;
                boolean clamp = this.clampLineLoop2D(lineCount);

                for(int ln = 0; ln < lineCount - 1; ++ln) {
                    int i1 = ln + 1;
                    index = this.addLineSegment2D(i0, i1, index, false, clamp);
                    i0 = i1;
                }

                index = this.addLineSegment2D(0, this.in.vertexCount - 1, index, false, clamp);
                this.lastLineIndexCache = this.lastPolyIndexCache = index;
            } else {
                LinePath path = new LinePath(1);
                path.moveTo(this.in.vertices[0], this.in.vertices[1], this.in.strokeColors[0]);

                for(i0 = 0; i0 < lineCount - 1; ++i0) {
                    int i1 = i0 + 1;
                    path.lineTo(this.in.vertices[3 * i1 + 0], this.in.vertices[3 * i1 + 1], this.in.strokeColors[i1]);
                }

                path.closePath();
                this.tessellateLinePath(path);
            }

        }

        boolean clampLineLoop2D(int lineCount) {
            boolean res = this.clamp2D();
            if (res) {
                for(int ln = 0; ln < lineCount; ++ln) {
                    res = this.segmentIsAxisAligned(0, ln + 1);
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateEdges() {
            if (this.stroke) {
                if (this.in.edgeCount == 0) {
                    return;
                }

                this.strokeVertices = this.in.vertices;
                this.strokeColors = this.in.strokeColors;
                this.strokeWeights = this.in.strokeWeights;
                if (this.is3D) {
                    this.tessellateEdges3D();
                } else if (this.is2D) {
                    this.beginNoTex();
                    this.tessellateEdges2D();
                    this.endNoTex();
                }
            }

        }

        void tessellateEdges3D() {
            boolean bevel = !this.noCapsJoins();
            int nInVert = this.in.getNumEdgeVertices(bevel);
            int nInInd = this.in.getNumEdgeIndices(bevel);
            int vcount0 = this.tess.lineVertexCount;
            int icount0 = this.tess.lineIndexCount;
            this.tess.lineVertexCheck(nInVert);
            this.tess.lineIndexCheck(nInInd);
            int index = this.in.renderMode == 1 ? this.tess.lineIndexCache.addNew() : this.tess.lineIndexCache.getLast();
            this.firstLineIndexCache = index;
            int fi0 = 0;
            int fi1 = 0;
            short[] lastInd = new short[]{-1, -1};
            int pi0 = -1;
            int pi1 = -1;
            int[] tmp = new int[]{0, 0};
            this.tess.lineIndexCache.setCounter(tmp);

            for(int i = 0; i <= this.in.edgeCount - 1; ++i) {
                int[] edge = this.in.edges[i];
                int i0 = edge[0];
                int i1 = edge[1];
                if (bevel) {
                    if (edge[2] == -1) {
                        index = this.addBevel3D(fi0, fi1, pi0, pi1, index, lastInd, false);
                    } else {
                        index = this.addLineSegment3D(i0, i1, pi0, pi1, index, lastInd, false);
                    }
                } else if (edge[2] != -1) {
                    index = this.addLineSegment3D(i0, i1, pi0, pi1, index, (short[])null, false);
                }

                if (edge[2] == 1) {
                    fi0 = i0;
                    fi1 = i1;
                }

                if (edge[2] != 2 && edge[2] != 3 && edge[2] != -1) {
                    pi0 = i0;
                    pi1 = i1;
                } else {
                    lastInd[0] = lastInd[1] = -1;
                    pi0 = -1;
                    pi1 = -1;
                }
            }

            this.tess.lineIndexCache.setCounter((int[])null);
            this.tess.lineIndexCount = icount0 + tmp[0];
            this.tess.lineVertexCount = vcount0 + tmp[1];
            this.lastLineIndexCache = index;
        }

        void tessellateEdges2D() {
            int nInVert = this.in.getNumEdgeVertices(false);
            int index;
            int i;
            if (this.noCapsJoins(nInVert)) {
                int nInInd = this.in.getNumEdgeIndices(false);
                this.tess.polyVertexCheck(nInVert);
                this.tess.polyIndexCheck(nInInd);
                index = this.in.renderMode == 1 ? this.tess.polyIndexCache.addNew() : this.tess.polyIndexCache.getLast();
                this.firstLineIndexCache = index;
                if (this.firstPolyIndexCache == -1) {
                    this.firstPolyIndexCache = index;
                }

                boolean clamp = this.clampEdges2D();

                for(i = 0; i <= this.in.edgeCount - 1; ++i) {
                    int[] edge = this.in.edges[i];
                    if (edge[2] != -1) {
                        int i0 = edge[0];
                        int i1 = edge[1];
                        index = this.addLineSegment2D(i0, i1, index, false, clamp);
                    }
                }

                this.lastLineIndexCache = this.lastPolyIndexCache = index;
            } else {
                LinePath path = new LinePath(1);

                for(index = 0; index <= this.in.edgeCount - 1; ++index) {
                    int[] edge = this.in.edges[index];
                    i = edge[0];
                    int i1 = edge[1];
                    switch(edge[2]) {
                        case -1:
                            path.closePath();
                            break;
                        case 0:
                            path.lineTo(this.strokeVertices[3 * i1 + 0], this.strokeVertices[3 * i1 + 1], this.strokeColors[i1]);
                            break;
                        case 1:
                            path.moveTo(this.strokeVertices[3 * i + 0], this.strokeVertices[3 * i + 1], this.strokeColors[i]);
                            path.lineTo(this.strokeVertices[3 * i1 + 0], this.strokeVertices[3 * i1 + 1], this.strokeColors[i1]);
                            break;
                        case 2:
                            path.lineTo(this.strokeVertices[3 * i1 + 0], this.strokeVertices[3 * i1 + 1], this.strokeColors[i1]);
                            path.moveTo(this.strokeVertices[3 * i1 + 0], this.strokeVertices[3 * i1 + 1], this.strokeColors[i1]);
                            break;
                        case 3:
                            path.moveTo(this.strokeVertices[3 * i + 0], this.strokeVertices[3 * i + 1], this.strokeColors[i]);
                            path.lineTo(this.strokeVertices[3 * i1 + 0], this.strokeVertices[3 * i1 + 1], this.strokeColors[i1]);
                            path.moveTo(this.strokeVertices[3 * i1 + 0], this.strokeVertices[3 * i1 + 1], this.strokeColors[i1]);
                    }
                }

                this.tessellateLinePath(path);
            }

        }

        boolean clampEdges2D() {
            boolean res = this.clamp2D();
            if (res) {
                for(int i = 0; i <= this.in.edgeCount - 1; ++i) {
                    int[] edge = this.in.edges[i];
                    if (edge[2] != -1) {
                        int i0 = edge[0];
                        int i1 = edge[1];
                        res = this.segmentIsAxisAligned(this.strokeVertices, i0, i1);
                        if (!res) {
                            break;
                        }
                    }
                }
            }

            return res;
        }

        int addLineSegment3D(int i0, int i1, int pi0, int pi1, int index, short[] lastInd, boolean constStroke) {
            PGraphicsOpenGL.IndexCache cache = this.tess.lineIndexCache;
            int count = cache.vertexCount[index];
            boolean addBevel = lastInd != null && -1 < lastInd[0] && -1 < lastInd[1];
            boolean newCache = false;
            if (PGL.MAX_VERTEX_INDEX1 <= count + 4 + (addBevel ? 1 : 0)) {
                index = cache.addNew();
                count = 0;
                newCache = true;
            }

            int iidx = cache.indexOffset[index] + cache.indexCount[index];
            int vidx = cache.vertexOffset[index] + cache.vertexCount[index];
            int color;
            int color0 = color = constStroke ? this.strokeColor : this.strokeColors[i0];
            float weight = constStroke ? this.strokeWeight : this.strokeWeights[i0];
            weight *= this.transformScale();
            this.tess.setLineVertex(vidx++, this.strokeVertices, i0, i1, color, weight / 2.0F);
            this.tess.lineIndices[iidx++] = (short)(count + 0);
            this.tess.setLineVertex(vidx++, this.strokeVertices, i0, i1, color, -weight / 2.0F);
            this.tess.lineIndices[iidx++] = (short)(count + 1);
            color = constStroke ? this.strokeColor : this.strokeColors[i1];
            weight = constStroke ? this.strokeWeight : this.strokeWeights[i1];
            weight *= this.transformScale();
            this.tess.setLineVertex(vidx++, this.strokeVertices, i1, i0, color, -weight / 2.0F);
            this.tess.lineIndices[iidx++] = (short)(count + 2);
            this.tess.lineIndices[iidx++] = (short)(count + 2);
            this.tess.lineIndices[iidx++] = (short)(count + 1);
            this.tess.setLineVertex(vidx++, this.strokeVertices, i1, i0, color, weight / 2.0F);
            this.tess.lineIndices[iidx++] = (short)(count + 3);
            cache.incCounts(index, 6, 4);
            if (lastInd != null) {
                if (-1 < lastInd[0] && -1 < lastInd[1]) {
                    if (newCache) {
                        if (-1 < pi0 && -1 < pi1) {
                            color = constStroke ? this.strokeColor : this.strokeColors[pi0];
                            weight = constStroke ? this.strokeWeight : this.strokeWeights[pi0];
                            weight *= this.transformScale();
                            this.tess.setLineVertex(vidx++, this.strokeVertices, pi1, color);
                            this.tess.setLineVertex(vidx++, this.strokeVertices, pi1, pi0, color, -weight / 2.0F);
                            this.tess.setLineVertex(vidx, this.strokeVertices, pi1, pi0, color, weight / 2.0F);
                            this.tess.lineIndices[iidx++] = (short)(count + 4);
                            this.tess.lineIndices[iidx++] = (short)(count + 5);
                            this.tess.lineIndices[iidx++] = (short)(count + 0);
                            this.tess.lineIndices[iidx++] = (short)(count + 4);
                            this.tess.lineIndices[iidx++] = (short)(count + 6);
                            this.tess.lineIndices[iidx] = (short)(count + 1);
                            cache.incCounts(index, 6, 3);
                        }
                    } else {
                        this.tess.setLineVertex(vidx, this.strokeVertices, i0, color0);
                        this.tess.lineIndices[iidx++] = (short)(count + 4);
                        this.tess.lineIndices[iidx++] = lastInd[0];
                        this.tess.lineIndices[iidx++] = (short)(count + 0);
                        this.tess.lineIndices[iidx++] = (short)(count + 4);
                        this.tess.lineIndices[iidx++] = lastInd[1];
                        this.tess.lineIndices[iidx] = (short)(count + 1);
                        cache.incCounts(index, 6, 1);
                    }
                }

                lastInd[0] = (short)(count + 2);
                lastInd[1] = (short)(count + 3);
            }

            return index;
        }

        int addBevel3D(int fi0, int fi1, int pi0, int pi1, int index, short[] lastInd, boolean constStroke) {
            PGraphicsOpenGL.IndexCache cache = this.tess.lineIndexCache;
            int count = cache.vertexCount[index];
            boolean newCache = false;
            if (PGL.MAX_VERTEX_INDEX1 <= count + 3) {
                index = cache.addNew();
                count = 0;
                newCache = true;
            }

            int iidx = cache.indexOffset[index] + cache.indexCount[index];
            int vidx = cache.vertexOffset[index] + cache.vertexCount[index];
            int color = constStroke ? this.strokeColor : this.strokeColors[fi0];
            float weight = constStroke ? this.strokeWeight : this.strokeWeights[fi0];
            weight *= this.transformScale();
            this.tess.setLineVertex(vidx++, this.strokeVertices, fi0, color);
            this.tess.setLineVertex(vidx++, this.strokeVertices, fi0, fi1, color, weight / 2.0F);
            this.tess.setLineVertex(vidx++, this.strokeVertices, fi0, fi1, color, -weight / 2.0F);
            int extra = 0;
            if (newCache && -1 < pi0 && -1 < pi1) {
                color = constStroke ? this.strokeColor : this.strokeColors[pi1];
                weight = constStroke ? this.strokeWeight : this.strokeWeights[pi1];
                weight *= this.transformScale();
                this.tess.setLineVertex(vidx++, this.strokeVertices, pi1, pi0, color, -weight / 2.0F);
                this.tess.setLineVertex(vidx, this.strokeVertices, pi1, pi0, color, weight / 2.0F);
                lastInd[0] = (short)(count + 3);
                lastInd[1] = (short)(count + 4);
                extra = 2;
            }

            this.tess.lineIndices[iidx++] = (short)(count + 0);
            this.tess.lineIndices[iidx++] = lastInd[0];
            this.tess.lineIndices[iidx++] = (short)(count + 1);
            this.tess.lineIndices[iidx++] = (short)(count + 0);
            this.tess.lineIndices[iidx++] = (short)(count + 2);
            this.tess.lineIndices[iidx] = lastInd[1];
            cache.incCounts(index, 6, 3 + extra);
            return index;
        }

        int addLineSegment2D(int i0, int i1, int index, boolean constStroke, boolean clamp) {
            PGraphicsOpenGL.IndexCache cache = this.tess.polyIndexCache;
            int count = cache.vertexCount[index];
            if (PGL.MAX_VERTEX_INDEX1 <= count + 4) {
                index = cache.addNew();
                count = 0;
            }

            int iidx = cache.indexOffset[index] + cache.indexCount[index];
            int vidx = cache.vertexOffset[index] + cache.vertexCount[index];
            int color = constStroke ? this.strokeColor : this.strokeColors[i0];
            float weight = constStroke ? this.strokeWeight : this.strokeWeights[i0];
            if (this.subPixelStroke(weight)) {
                clamp = false;
            }

            float x0 = this.strokeVertices[3 * i0 + 0];
            float y0 = this.strokeVertices[3 * i0 + 1];
            float x1 = this.strokeVertices[3 * i1 + 0];
            float y1 = this.strokeVertices[3 * i1 + 1];
            float dirx = x1 - x0;
            float diry = y1 - y0;
            float llen = PApplet.sqrt(dirx * dirx + diry * diry);
            float normx = 0.0F;
            float normy = 0.0F;
            float dirdx = 0.0F;
            float dirdy = 0.0F;
            if (PGraphicsOpenGL.nonZero(llen)) {
                normx = -diry / llen;
                normy = dirx / llen;
                dirdx = dirx / llen * PApplet.min(0.75F, weight / 2.0F);
                dirdy = diry / llen * PApplet.min(0.75F, weight / 2.0F);
            }

            float normdx = normx * weight / 2.0F;
            float normdy = normy * weight / 2.0F;
            this.tess.setPolyVertex(vidx++, x0 + normdx - dirdx, y0 + normdy - dirdy, 0.0F, color, clamp);
            this.tess.polyIndices[iidx++] = (short)(count + 0);
            this.tess.setPolyVertex(vidx++, x0 - normdx - dirdx, y0 - normdy - dirdy, 0.0F, color, clamp);
            this.tess.polyIndices[iidx++] = (short)(count + 1);
            float xac;
            float yac;
            float xbc;
            float ybc;
            if (clamp) {
                xac = this.tess.polyVertices[4 * (vidx - 2) + 0];
                yac = this.tess.polyVertices[4 * (vidx - 2) + 1];
                xbc = this.tess.polyVertices[4 * (vidx - 1) + 0];
                ybc = this.tess.polyVertices[4 * (vidx - 1) + 1];
                if (PGraphicsOpenGL.same(xac, xbc) && PGraphicsOpenGL.same(yac, ybc)) {
                    this.unclampLine2D(vidx - 2, x0 + normdx - dirdx, y0 + normdy - dirdy);
                    this.unclampLine2D(vidx - 1, x0 - normdx - dirdx, y0 - normdy - dirdy);
                }
            }

            if (!constStroke) {
                color = this.strokeColors[i1];
                weight = this.strokeWeights[i1];
                normdx = normx * weight / 2.0F;
                normdy = normy * weight / 2.0F;
                if (this.subPixelStroke(weight)) {
                    clamp = false;
                }
            }

            this.tess.setPolyVertex(vidx++, x1 - normdx + dirdx, y1 - normdy + dirdy, 0.0F, color, clamp);
            this.tess.polyIndices[iidx++] = (short)(count + 2);
            this.tess.polyIndices[iidx++] = (short)(count + 2);
            this.tess.polyIndices[iidx++] = (short)(count + 0);
            this.tess.setPolyVertex(vidx++, x1 + normdx + dirdx, y1 + normdy + dirdy, 0.0F, color, clamp);
            this.tess.polyIndices[iidx++] = (short)(count + 3);
            if (clamp) {
                xac = this.tess.polyVertices[4 * (vidx - 2) + 0];
                yac = this.tess.polyVertices[4 * (vidx - 2) + 1];
                xbc = this.tess.polyVertices[4 * (vidx - 1) + 0];
                ybc = this.tess.polyVertices[4 * (vidx - 1) + 1];
                if (PGraphicsOpenGL.same(xac, xbc) && PGraphicsOpenGL.same(yac, ybc)) {
                    this.unclampLine2D(vidx - 2, x1 - normdx + dirdx, y1 - normdy + dirdy);
                    this.unclampLine2D(vidx - 1, x1 + normdx + dirdx, y1 + normdy + dirdy);
                }
            }

            cache.incCounts(index, 6, 4);
            return index;
        }

        void unclampLine2D(int tessIdx, float x, float y) {
            PMatrix3D mm = this.pg.modelview;
            int index = 4 * tessIdx;
            this.tess.polyVertices[index++] = x * mm.m00 + y * mm.m01 + mm.m03;
            this.tess.polyVertices[index++] = x * mm.m10 + y * mm.m11 + mm.m13;
        }

        boolean noCapsJoins(int nInVert) {
            if (!this.accurate2DStrokes) {
                return true;
            } else {
                return PGL.MAX_CAPS_JOINS_LENGTH <= nInVert ? true : this.noCapsJoins();
            }
        }

        boolean subPixelStroke(float weight) {
            float sw = this.transformScale() * weight;
            return PApplet.abs(sw - (float)((int)sw)) > 0.0F;
        }

        boolean noCapsJoins() {
            return this.tess.renderMode == 0 && this.transformScale() * this.strokeWeight < PGL.MIN_CAPS_JOINS_WEIGHT;
        }

        float transformScale() {
            return -1.0F < this.transformScale ? this.transformScale : (this.transformScale = PGraphicsOpenGL.matrixScale(this.transform));
        }

        boolean segmentIsAxisAligned(int i0, int i1) {
            return PGraphicsOpenGL.zero(this.in.vertices[3 * i0 + 0] - this.in.vertices[3 * i1 + 0]) || PGraphicsOpenGL.zero(this.in.vertices[3 * i0 + 1] - this.in.vertices[3 * i1 + 1]);
        }

        boolean segmentIsAxisAligned(float[] vertices, int i0, int i1) {
            return PGraphicsOpenGL.zero(vertices[3 * i0 + 0] - vertices[3 * i1 + 0]) || PGraphicsOpenGL.zero(vertices[3 * i0 + 1] - vertices[3 * i1 + 1]);
        }

        void tessellateTriangles() {
            this.beginTex();
            int nTri = this.in.vertexCount / 3;
            if (this.fill && 1 <= nTri) {
                int nInInd = 3 * nTri;
                this.setRawSize(nInInd);
                int idx = 0;
                boolean clamp = this.clampTriangles();

                for(int i = 0; i < 3 * nTri; this.rawIndices[idx++] = i++) {
                }

                this.splitRawIndices(clamp);
            }

            this.endTex();
            this.tessellateEdges();
        }

        boolean clampTriangles() {
            boolean res = this.clamp2D();
            if (res) {
                int nTri = this.in.vertexCount / 3;

                for(int i = 0; i < nTri; ++i) {
                    int i0 = 3 * i + 0;
                    int i1 = 3 * i + 1;
                    int i2 = 3 * i + 2;
                    int count = 0;
                    if (this.segmentIsAxisAligned(i0, i1)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i0, i2)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i1, i2)) {
                        ++count;
                    }

                    res = 1 < count;
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateTriangles(int[] indices) {
            this.beginTex();
            int nInVert = this.in.vertexCount;
            if (this.fill && 3 <= nInVert) {
                int nInInd = indices.length;
                this.setRawSize(nInInd);
                PApplet.arrayCopy(indices, this.rawIndices, nInInd);
                boolean clamp = this.clampTriangles(indices);
                this.splitRawIndices(clamp);
            }

            this.endTex();
            this.tessellateEdges();
        }

        boolean clampTriangles(int[] indices) {
            boolean res = this.clamp2D();
            if (res) {
                int nTri = indices.length;

                for(int i = 0; i < nTri; ++i) {
                    int i0 = indices[3 * i + 0];
                    int i1 = indices[3 * i + 1];
                    int i2 = indices[3 * i + 2];
                    int count = 0;
                    if (this.segmentIsAxisAligned(i0, i1)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i0, i2)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i1, i2)) {
                        ++count;
                    }

                    res = 1 < count;
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateTriangleFan() {
            this.beginTex();
            int nInVert = this.in.vertexCount;
            if (this.fill && 3 <= nInVert) {
                int nInInd = 3 * (nInVert - 2);
                this.setRawSize(nInInd);
                int idx = 0;
                boolean clamp = this.clampTriangleFan();

                for(int i = 1; i < this.in.vertexCount - 1; ++i) {
                    this.rawIndices[idx++] = 0;
                    this.rawIndices[idx++] = i;
                    this.rawIndices[idx++] = i + 1;
                }

                this.splitRawIndices(clamp);
            }

            this.endTex();
            this.tessellateEdges();
        }

        boolean clampTriangleFan() {
            boolean res = this.clamp2D();
            if (res) {
                for(int i = 1; i < this.in.vertexCount - 1; ++i) {
                    int i0 = 0;
                    int i2 = i + 1;
                    int count = 0;
                    if (this.segmentIsAxisAligned(i0, i)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i0, i2)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i, i2)) {
                        ++count;
                    }

                    res = 1 < count;
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateTriangleStrip() {
            this.beginTex();
            int nInVert = this.in.vertexCount;
            if (this.fill && 3 <= nInVert) {
                int nInInd = 3 * (nInVert - 2);
                this.setRawSize(nInInd);
                int idx = 0;
                boolean clamp = this.clampTriangleStrip();

                for(int i = 1; i < this.in.vertexCount - 1; ++i) {
                    this.rawIndices[idx++] = i;
                    if (i % 2 == 0) {
                        this.rawIndices[idx++] = i - 1;
                        this.rawIndices[idx++] = i + 1;
                    } else {
                        this.rawIndices[idx++] = i + 1;
                        this.rawIndices[idx++] = i - 1;
                    }
                }

                this.splitRawIndices(clamp);
            }

            this.endTex();
            this.tessellateEdges();
        }

        boolean clampTriangleStrip() {
            boolean res = this.clamp2D();
            if (res) {
                for(int i = 1; i < this.in.vertexCount - 1; ++i) {
                    int i1;
                    int i2;
                    if (i % 2 == 0) {
                        i1 = i - 1;
                        i2 = i + 1;
                    } else {
                        i1 = i + 1;
                        i2 = i - 1;
                    }

                    int count = 0;
                    if (this.segmentIsAxisAligned(i, i1)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i, i2)) {
                        ++count;
                    }

                    if (this.segmentIsAxisAligned(i1, i2)) {
                        ++count;
                    }

                    res = 1 < count;
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateQuads() {
            this.beginTex();
            int quadCount = this.in.vertexCount / 4;
            if (this.fill && 1 <= quadCount) {
                int nInInd = 6 * quadCount;
                this.setRawSize(nInInd);
                int idx = 0;
                boolean clamp = this.clampQuads(quadCount);

                for(int qd = 0; qd < quadCount; ++qd) {
                    int i0 = 4 * qd + 0;
                    int i1 = 4 * qd + 1;
                    int i2 = 4 * qd + 2;
                    int i3 = 4 * qd + 3;
                    this.rawIndices[idx++] = i0;
                    this.rawIndices[idx++] = i1;
                    this.rawIndices[idx++] = i2;
                    this.rawIndices[idx++] = i2;
                    this.rawIndices[idx++] = i3;
                    this.rawIndices[idx++] = i0;
                }

                this.splitRawIndices(clamp);
            }

            this.endTex();
            this.tessellateEdges();
        }

        boolean clampQuads(int quadCount) {
            boolean res = this.clamp2D();
            if (res) {
                for(int qd = 0; qd < quadCount; ++qd) {
                    int i0 = 4 * qd + 0;
                    int i1 = 4 * qd + 1;
                    int i2 = 4 * qd + 2;
                    int i3 = 4 * qd + 3;
                    res = this.segmentIsAxisAligned(i0, i1) && this.segmentIsAxisAligned(i1, i2) && this.segmentIsAxisAligned(i2, i3);
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void tessellateQuadStrip() {
            this.beginTex();
            int quadCount = this.in.vertexCount / 2 - 1;
            if (this.fill && 1 <= quadCount) {
                int nInInd = 6 * quadCount;
                this.setRawSize(nInInd);
                int idx = 0;
                boolean clamp = this.clampQuadStrip(quadCount);

                for(int qd = 1; qd < quadCount + 1; ++qd) {
                    int i0 = 2 * (qd - 1);
                    int i1 = 2 * (qd - 1) + 1;
                    int i2 = 2 * qd + 1;
                    int i3 = 2 * qd;
                    this.rawIndices[idx++] = i0;
                    this.rawIndices[idx++] = i1;
                    this.rawIndices[idx++] = i3;
                    this.rawIndices[idx++] = i1;
                    this.rawIndices[idx++] = i2;
                    this.rawIndices[idx++] = i3;
                }

                this.splitRawIndices(clamp);
            }

            this.endTex();
            this.tessellateEdges();
        }

        boolean clampQuadStrip(int quadCount) {
            boolean res = this.clamp2D();
            if (res) {
                for(int qd = 1; qd < quadCount + 1; ++qd) {
                    int i0 = 2 * (qd - 1);
                    int i1 = 2 * (qd - 1) + 1;
                    int i2 = 2 * qd + 1;
                    int i3 = 2 * qd;
                    res = this.segmentIsAxisAligned(i0, i1) && this.segmentIsAxisAligned(i1, i2) && this.segmentIsAxisAligned(i2, i3);
                    if (!res) {
                        break;
                    }
                }
            }

            return res;
        }

        void splitRawIndices(boolean clamp) {
            this.tess.polyIndexCheck(this.rawSize);
            int offset = this.tess.firstPolyIndex;
            int inInd0 = 0;
            int inInd1 = false;
            int inMaxVert0 = 0;
            int inMaxVert1 = 0;
            int inMaxVertRef = inMaxVert0;
            int inMaxVertRel = -1;
            this.dupCount = 0;
            PGraphicsOpenGL.IndexCache cache = this.tess.polyIndexCache;
            int index = this.in.renderMode == 1 ? cache.addNew() : cache.getLast();
            this.firstPolyIndexCache = index;
            int trCount = this.rawSize / 3;

            for(int tr = 0; tr < trCount; ++tr) {
                if (index == -1) {
                    index = cache.addNew();
                }

                int i0 = this.rawIndices[3 * tr + 0];
                int i1 = this.rawIndices[3 * tr + 1];
                int i2 = this.rawIndices[3 * tr + 2];
                int ii0 = i0 - inMaxVertRef;
                int ii1 = i1 - inMaxVertRef;
                int ii2 = i2 - inMaxVertRef;
                int count = cache.vertexCount[index];
                int ri0;
                if (ii0 < 0) {
                    this.addDupIndex(ii0);
                    ri0 = ii0;
                } else {
                    ri0 = count + ii0;
                }

                int ri1;
                if (ii1 < 0) {
                    this.addDupIndex(ii1);
                    ri1 = ii1;
                } else {
                    ri1 = count + ii1;
                }

                int ri2;
                if (ii2 < 0) {
                    this.addDupIndex(ii2);
                    ri2 = ii2;
                } else {
                    ri2 = count + ii2;
                }

                this.tess.polyIndices[offset + 3 * tr + 0] = (short)ri0;
                this.tess.polyIndices[offset + 3 * tr + 1] = (short)ri1;
                this.tess.polyIndices[offset + 3 * tr + 2] = (short)ri2;
                int inInd1 = 3 * tr + 2;
                inMaxVert1 = PApplet.max(inMaxVert1, PApplet.max(i0, i1, i2));
                inMaxVert0 = PApplet.min(inMaxVert0, PApplet.min(i0, i1, i2));
                inMaxVertRel = PApplet.max(inMaxVertRel, PApplet.max(ri0, ri1, ri2));
                if (PGL.MAX_VERTEX_INDEX1 - 3 <= inMaxVertRel + this.dupCount && inMaxVertRel + this.dupCount < PGL.MAX_VERTEX_INDEX1 || tr == trCount - 1) {
                    int nondupCount = 0;
                    if (0 < this.dupCount) {
                        int i;
                        for(i = inInd0; i <= inInd1; ++i) {
                            int ri = this.tess.polyIndices[offset + i];
                            if (ri < 0) {
                                this.tess.polyIndices[offset + i] = (short)(inMaxVertRel + 1 + this.dupIndexPos(ri));
                            }
                        }

                        if (inMaxVertRef <= inMaxVert1) {
                            this.tess.addPolyVertices(this.in, inMaxVertRef, inMaxVert1, clamp);
                            nondupCount = inMaxVert1 - inMaxVertRef + 1;
                        }

                        for(i = 0; i < this.dupCount; ++i) {
                            this.tess.addPolyVertex(this.in, this.dupIndices[i] + inMaxVertRef, clamp);
                        }
                    } else {
                        this.tess.addPolyVertices(this.in, inMaxVert0, inMaxVert1, clamp);
                        nondupCount = inMaxVert1 - inMaxVert0 + 1;
                    }

                    cache.incCounts(index, inInd1 - inInd0 + 1, nondupCount + this.dupCount);
                    this.lastPolyIndexCache = index;
                    index = -1;
                    inMaxVertRel = -1;
                    inMaxVertRef = inMaxVert1 + 1;
                    inMaxVert0 = inMaxVertRef;
                    inInd0 = inInd1 + 1;
                    if (this.dupIndices != null) {
                        Arrays.fill(this.dupIndices, 0, this.dupCount, 0);
                    }

                    this.dupCount = 0;
                }
            }

        }

        void addDupIndex(int idx) {
            if (this.dupIndices == null) {
                this.dupIndices = new int[16];
            }

            int i;
            if (this.dupIndices.length == this.dupCount) {
                i = this.dupCount << 1;
                int[] temp = new int[i];
                PApplet.arrayCopy(this.dupIndices, 0, temp, 0, this.dupCount);
                this.dupIndices = temp;
            }

            if (idx < this.dupIndices[0]) {
                for(i = this.dupCount; i > 0; --i) {
                    this.dupIndices[i] = this.dupIndices[i - 1];
                }

                this.dupIndices[0] = idx;
                ++this.dupCount;
            } else if (this.dupIndices[this.dupCount - 1] < idx) {
                this.dupIndices[this.dupCount] = idx;
                ++this.dupCount;
            } else {
                for(i = 0; i < this.dupCount - 1 && this.dupIndices[i] != idx; ++i) {
                    if (this.dupIndices[i] < idx && idx < this.dupIndices[i + 1]) {
                        for(int j = this.dupCount; j > i + 1; --j) {
                            this.dupIndices[j] = this.dupIndices[j - 1];
                        }

                        this.dupIndices[i + 1] = idx;
                        ++this.dupCount;
                        break;
                    }
                }
            }

        }

        int dupIndexPos(int idx) {
            for(int i = 0; i < this.dupCount; ++i) {
                if (this.dupIndices[i] == idx) {
                    return i;
                }
            }

            return 0;
        }

        void setRawSize(int size) {
            int size0 = this.rawIndices.length;
            if (size0 < size) {
                int size1 = PGraphicsOpenGL.expandArraySize(size0, size);
                this.expandRawIndices(size1);
            }

            this.rawSize = size;
        }

        void expandRawIndices(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.rawIndices, 0, temp, 0, this.rawSize);
            this.rawIndices = temp;
        }

        void beginTex() {
            this.setFirstTexIndex(this.tess.polyIndexCount, this.tess.polyIndexCache.size - 1);
        }

        void endTex() {
            this.setLastTexIndex(this.tess.lastPolyIndex, this.tess.polyIndexCache.size - 1);
        }

        void beginNoTex() {
            this.newTexImage = null;
            this.setFirstTexIndex(this.tess.polyIndexCount, this.tess.polyIndexCache.size - 1);
        }

        void endNoTex() {
            this.setLastTexIndex(this.tess.lastPolyIndex, this.tess.polyIndexCache.size - 1);
        }

        void updateTex() {
            this.beginTex();
            this.endTex();
        }

        void setFirstTexIndex(int firstIndex, int firstCache) {
            if (this.texCache != null) {
                this.firstTexIndex = firstIndex;
                this.firstTexCache = PApplet.max(0, firstCache);
            }

        }

        void setLastTexIndex(int lastIndex, int lastCache) {
            if (this.texCache != null) {
                if (this.prevTexImage == this.newTexImage && this.texCache.size != 0) {
                    this.texCache.setLastIndex(lastIndex, lastCache);
                } else {
                    this.texCache.addTexture(this.newTexImage, this.firstTexIndex, this.firstTexCache, lastIndex, lastCache);
                }

                this.prevTexImage = this.newTexImage;
            }

        }

        void tessellatePolygon(boolean solid, boolean closed, boolean calcNormals) {
            this.beginTex();
            int nInVert = this.in.vertexCount;
            if (3 <= nInVert) {
                this.firstPolyIndexCache = -1;
                this.initGluTess();
                boolean clamp = this.clampPolygon();
                this.callback.init(this.in.renderMode == 1, false, calcNormals, clamp);
                if (this.fill) {
                    this.gluTess.beginPolygon();
                    if (solid) {
                        this.gluTess.setWindingRule(PGL.TESS_WINDING_NONZERO);
                    } else {
                        this.gluTess.setWindingRule(PGL.TESS_WINDING_ODD);
                    }

                    this.gluTess.beginContour();
                }

                if (this.stroke) {
                    this.beginPolygonStroke();
                    this.beginStrokePath();
                }

                int i = 0;
                int c = 0;

                while(i < this.in.vertexCount) {
                    int code = 0;
                    boolean brk = false;
                    if (this.in.codes != null && c < this.in.codeCount) {
                        code = this.in.codes[c++];
                        if (code == 4 && c < this.in.codeCount) {
                            brk = true;
                            code = this.in.codes[c++];
                        }
                    }

                    if (brk) {
                        if (this.stroke) {
                            this.endStrokePath(closed);
                            this.beginStrokePath();
                        }

                        if (this.fill) {
                            this.gluTess.endContour();
                            this.gluTess.beginContour();
                        }
                    }

                    if (code == 1) {
                        this.addBezierVertex(i);
                        i += 3;
                    } else if (code == 2) {
                        this.addQuadraticVertex(i);
                        i += 2;
                    } else if (code == 3) {
                        this.addCurveVertex(i);
                        ++i;
                    } else {
                        this.addVertex(i);
                        ++i;
                    }
                }

                if (this.stroke) {
                    this.endStrokePath(closed);
                    this.endPolygonStroke();
                }

                if (this.fill) {
                    this.gluTess.endContour();
                    this.gluTess.endPolygon();
                }
            }

            this.endTex();
            if (this.stroke) {
                this.tessellateStrokePath();
            }

        }

        void addBezierVertex(int i) {
            this.pg.curveVertexCount = 0;
            this.pg.bezierInitCheck();
            this.pg.bezierVertexCheck(20, i);
            PMatrix3D draw = this.pg.bezierDrawMatrix;
            int i1 = i - 1;
            float x1 = this.in.vertices[3 * i1 + 0];
            float y1 = this.in.vertices[3 * i1 + 1];
            float z1 = this.in.vertices[3 * i1 + 2];
            int strokeColor = 0;
            float strokeWeight = 0.0F;
            if (this.stroke) {
                strokeColor = this.in.strokeColors[i];
                strokeWeight = this.in.strokeWeights[i];
            }

            double[] vertexT = this.fill ? this.collectVertexAttributes(i) : null;
            float x2 = this.in.vertices[3 * i + 0];
            float y2 = this.in.vertices[3 * i + 1];
            float z2 = this.in.vertices[3 * i + 2];
            float x3 = this.in.vertices[3 * (i + 1) + 0];
            float y3 = this.in.vertices[3 * (i + 1) + 1];
            float z3 = this.in.vertices[3 * (i + 1) + 2];
            float x4 = this.in.vertices[3 * (i + 2) + 0];
            float y4 = this.in.vertices[3 * (i + 2) + 1];
            float z4 = this.in.vertices[3 * (i + 2) + 2];
            float xplot1 = draw.m10 * x1 + draw.m11 * x2 + draw.m12 * x3 + draw.m13 * x4;
            float xplot2 = draw.m20 * x1 + draw.m21 * x2 + draw.m22 * x3 + draw.m23 * x4;
            float xplot3 = draw.m30 * x1 + draw.m31 * x2 + draw.m32 * x3 + draw.m33 * x4;
            float yplot1 = draw.m10 * y1 + draw.m11 * y2 + draw.m12 * y3 + draw.m13 * y4;
            float yplot2 = draw.m20 * y1 + draw.m21 * y2 + draw.m22 * y3 + draw.m23 * y4;
            float yplot3 = draw.m30 * y1 + draw.m31 * y2 + draw.m32 * y3 + draw.m33 * y4;
            float zplot1 = draw.m10 * z1 + draw.m11 * z2 + draw.m12 * z3 + draw.m13 * z4;
            float zplot2 = draw.m20 * z1 + draw.m21 * z2 + draw.m22 * z3 + draw.m23 * z4;
            float zplot3 = draw.m30 * z1 + draw.m31 * z2 + draw.m32 * z3 + draw.m33 * z4;

            for(int j = 0; j < this.pg.bezierDetail; ++j) {
                x1 += xplot1;
                xplot1 += xplot2;
                xplot2 += xplot3;
                y1 += yplot1;
                yplot1 += yplot2;
                yplot2 += yplot3;
                z1 += zplot1;
                zplot1 += zplot2;
                zplot2 += zplot3;
                if (this.fill) {
                    double[] vertex = Arrays.copyOf(vertexT, vertexT.length);
                    vertex[0] = (double)x1;
                    vertex[1] = (double)y1;
                    vertex[2] = (double)z1;
                    this.gluTess.addVertex(vertex);
                }

                if (this.stroke) {
                    this.addStrokeVertex(x1, y1, z1, strokeColor, strokeWeight);
                }
            }

        }

        void addQuadraticVertex(int i) {
            this.pg.curveVertexCount = 0;
            this.pg.bezierInitCheck();
            this.pg.bezierVertexCheck(20, i);
            PMatrix3D draw = this.pg.bezierDrawMatrix;
            int i1 = i - 1;
            float x1 = this.in.vertices[3 * i1 + 0];
            float y1 = this.in.vertices[3 * i1 + 1];
            float z1 = this.in.vertices[3 * i1 + 2];
            int strokeColor = 0;
            float strokeWeight = 0.0F;
            if (this.stroke) {
                strokeColor = this.in.strokeColors[i];
                strokeWeight = this.in.strokeWeights[i];
            }

            double[] vertexT = this.fill ? this.collectVertexAttributes(i) : null;
            float cx = this.in.vertices[3 * i + 0];
            float cy = this.in.vertices[3 * i + 1];
            float cz = this.in.vertices[3 * i + 2];
            float x = this.in.vertices[3 * (i + 1) + 0];
            float y = this.in.vertices[3 * (i + 1) + 1];
            float z = this.in.vertices[3 * (i + 1) + 2];
            float x2 = x1 + (cx - x1) * 2.0F / 3.0F;
            float y2 = y1 + (cy - y1) * 2.0F / 3.0F;
            float z2 = z1 + (cz - z1) * 2.0F / 3.0F;
            float x3 = x + (cx - x) * 2.0F / 3.0F;
            float y3 = y + (cy - y) * 2.0F / 3.0F;
            float z3 = z + (cz - z) * 2.0F / 3.0F;
            float xplot1 = draw.m10 * x1 + draw.m11 * x2 + draw.m12 * x3 + draw.m13 * x;
            float xplot2 = draw.m20 * x1 + draw.m21 * x2 + draw.m22 * x3 + draw.m23 * x;
            float xplot3 = draw.m30 * x1 + draw.m31 * x2 + draw.m32 * x3 + draw.m33 * x;
            float yplot1 = draw.m10 * y1 + draw.m11 * y2 + draw.m12 * y3 + draw.m13 * y;
            float yplot2 = draw.m20 * y1 + draw.m21 * y2 + draw.m22 * y3 + draw.m23 * y;
            float yplot3 = draw.m30 * y1 + draw.m31 * y2 + draw.m32 * y3 + draw.m33 * y;
            float zplot1 = draw.m10 * z1 + draw.m11 * z2 + draw.m12 * z3 + draw.m13 * z;
            float zplot2 = draw.m20 * z1 + draw.m21 * z2 + draw.m22 * z3 + draw.m23 * z;
            float zplot3 = draw.m30 * z1 + draw.m31 * z2 + draw.m32 * z3 + draw.m33 * z;

            for(int j = 0; j < this.pg.bezierDetail; ++j) {
                x1 += xplot1;
                xplot1 += xplot2;
                xplot2 += xplot3;
                y1 += yplot1;
                yplot1 += yplot2;
                yplot2 += yplot3;
                z1 += zplot1;
                zplot1 += zplot2;
                zplot2 += zplot3;
                if (this.fill) {
                    double[] vertex = Arrays.copyOf(vertexT, vertexT.length);
                    vertex[0] = (double)x1;
                    vertex[1] = (double)y1;
                    vertex[2] = (double)z1;
                    this.gluTess.addVertex(vertex);
                }

                if (this.stroke) {
                    this.addStrokeVertex(x1, y1, z1, strokeColor, strokeWeight);
                }
            }

        }

        void addCurveVertex(int i) {
            this.pg.curveVertexCheck(20);
            float[] vertex = this.pg.curveVertices[this.pg.curveVertexCount];
            vertex[0] = this.in.vertices[3 * i + 0];
            vertex[1] = this.in.vertices[3 * i + 1];
            vertex[2] = this.in.vertices[3 * i + 2];
            this.pg.curveVertexCount++;
            float[] v1;
            if (this.pg.curveVertexCount == 3) {
                v1 = this.pg.curveVertices[this.pg.curveVertexCount - 2];
                this.addCurveInitialVertex(i, v1[0], v1[1], v1[2]);
            }

            if (this.pg.curveVertexCount > 3) {
                v1 = this.pg.curveVertices[this.pg.curveVertexCount - 4];
                float[] v2 = this.pg.curveVertices[this.pg.curveVertexCount - 3];
                float[] v3 = this.pg.curveVertices[this.pg.curveVertexCount - 2];
                float[] v4 = this.pg.curveVertices[this.pg.curveVertexCount - 1];
                this.addCurveVertexSegment(i, v1[0], v1[1], v1[2], v2[0], v2[1], v2[2], v3[0], v3[1], v3[2], v4[0], v4[1], v4[2]);
            }

        }

        void addCurveInitialVertex(int i, float x, float y, float z) {
            if (this.fill) {
                double[] vertex0 = this.collectVertexAttributes(i);
                vertex0[0] = (double)x;
                vertex0[1] = (double)y;
                vertex0[2] = (double)z;
                this.gluTess.addVertex(vertex0);
            }

            if (this.stroke) {
                this.addStrokeVertex(x, y, z, this.in.strokeColors[i], this.strokeWeight);
            }

        }

        void addCurveVertexSegment(int i, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
            int strokeColor = 0;
            float strokeWeight = 0.0F;
            if (this.stroke) {
                strokeColor = this.in.strokeColors[i];
                strokeWeight = this.in.strokeWeights[i];
            }

            double[] vertexT = this.fill ? this.collectVertexAttributes(i) : null;
            float x = x2;
            float y = y2;
            float z = z2;
            PMatrix3D draw = this.pg.curveDrawMatrix;
            float xplot1 = draw.m10 * x1 + draw.m11 * x2 + draw.m12 * x3 + draw.m13 * x4;
            float xplot2 = draw.m20 * x1 + draw.m21 * x2 + draw.m22 * x3 + draw.m23 * x4;
            float xplot3 = draw.m30 * x1 + draw.m31 * x2 + draw.m32 * x3 + draw.m33 * x4;
            float yplot1 = draw.m10 * y1 + draw.m11 * y2 + draw.m12 * y3 + draw.m13 * y4;
            float yplot2 = draw.m20 * y1 + draw.m21 * y2 + draw.m22 * y3 + draw.m23 * y4;
            float yplot3 = draw.m30 * y1 + draw.m31 * y2 + draw.m32 * y3 + draw.m33 * y4;
            float zplot1 = draw.m10 * z1 + draw.m11 * z2 + draw.m12 * z3 + draw.m13 * z4;
            float zplot2 = draw.m20 * z1 + draw.m21 * z2 + draw.m22 * z3 + draw.m23 * z4;
            float zplot3 = draw.m30 * z1 + draw.m31 * z2 + draw.m32 * z3 + draw.m33 * z4;

            for(int j = 0; j < this.pg.curveDetail; ++j) {
                x += xplot1;
                xplot1 += xplot2;
                xplot2 += xplot3;
                y += yplot1;
                yplot1 += yplot2;
                yplot2 += yplot3;
                z += zplot1;
                zplot1 += zplot2;
                zplot2 += zplot3;
                if (this.fill) {
                    double[] vertex1 = Arrays.copyOf(vertexT, vertexT.length);
                    vertex1[0] = (double)x;
                    vertex1[1] = (double)y;
                    vertex1[2] = (double)z;
                    this.gluTess.addVertex(vertex1);
                }

                if (this.stroke) {
                    this.addStrokeVertex(x, y, z, strokeColor, strokeWeight);
                }
            }

        }

        void addVertex(int i) {
            this.pg.curveVertexCount = 0;
            float x = this.in.vertices[3 * i + 0];
            float y = this.in.vertices[3 * i + 1];
            float z = this.in.vertices[3 * i + 2];
            if (this.fill) {
                double[] vertex = this.collectVertexAttributes(i);
                vertex[0] = (double)x;
                vertex[1] = (double)y;
                vertex[2] = (double)z;
                this.gluTess.addVertex(vertex);
            }

            if (this.stroke) {
                this.addStrokeVertex(x, y, z, this.in.strokeColors[i], this.in.strokeWeights[i]);
            }

        }

        double[] collectVertexAttributes(int i) {
            int COORD_COUNT = true;
            int ATTRIB_COUNT = true;
            double[] avect = this.in.getAttribVector(i);
            double[] r = new double[25 + avect.length];
            int j = 3;
            int fcol = this.in.colors[i];
            int j = j + 1;
            r[j] = (double)(fcol >> 24 & 255);
            r[j++] = (double)(fcol >> 16 & 255);
            r[j++] = (double)(fcol >> 8 & 255);
            r[j++] = (double)(fcol >> 0 & 255);
            r[j++] = (double)this.in.normals[3 * i + 0];
            r[j++] = (double)this.in.normals[3 * i + 1];
            r[j++] = (double)this.in.normals[3 * i + 2];
            r[j++] = (double)this.in.texcoords[2 * i + 0];
            r[j++] = (double)this.in.texcoords[2 * i + 1];
            int acol = this.in.ambient[i];
            r[j++] = (double)(acol >> 24 & 255);
            r[j++] = (double)(acol >> 16 & 255);
            r[j++] = (double)(acol >> 8 & 255);
            r[j++] = (double)(acol >> 0 & 255);
            int scol = this.in.specular[i];
            r[j++] = (double)(scol >> 24 & 255);
            r[j++] = (double)(scol >> 16 & 255);
            r[j++] = (double)(scol >> 8 & 255);
            r[j++] = (double)(scol >> 0 & 255);
            int ecol = this.in.emissive[i];
            r[j++] = (double)(ecol >> 24 & 255);
            r[j++] = (double)(ecol >> 16 & 255);
            r[j++] = (double)(ecol >> 8 & 255);
            r[j++] = (double)(ecol >> 0 & 255);
            r[j++] = (double)this.in.shininess[i];
            System.arraycopy(avect, 0, r, j, avect.length);
            return r;
        }

        void beginPolygonStroke() {
            this.pathVertexCount = 0;
            if (this.pathVertices == null) {
                this.pathVertices = new float[3 * PGL.DEFAULT_IN_VERTICES];
                this.pathColors = new int[PGL.DEFAULT_IN_VERTICES];
                this.pathWeights = new float[PGL.DEFAULT_IN_VERTICES];
            }

        }

        void endPolygonStroke() {
        }

        void beginStrokePath() {
            this.beginPath = this.pathVertexCount;
        }

        void endStrokePath(boolean closed) {
            int idx = this.pathVertexCount;
            if (this.beginPath + 1 < idx) {
                boolean begin = this.beginPath == idx - 2;
                boolean end = begin || !closed;
                this.in.addEdge(idx - 2, idx - 1, begin, end);
                if (!end) {
                    this.in.addEdge(idx - 1, this.beginPath, false, false);
                    this.in.closeEdge(idx - 1, this.beginPath);
                }
            }

        }

        void addStrokeVertex(float x, float y, float z, int c, float w) {
            int idx = this.pathVertexCount;
            if (this.beginPath + 1 < idx) {
                this.in.addEdge(idx - 2, idx - 1, this.beginPath == idx - 2, false);
            }

            if (this.pathVertexCount == this.pathVertices.length / 3) {
                int newSize = this.pathVertexCount << 1;
                float[] vtemp = new float[3 * newSize];
                PApplet.arrayCopy(this.pathVertices, 0, vtemp, 0, 3 * this.pathVertexCount);
                this.pathVertices = vtemp;
                int[] ctemp = new int[newSize];
                PApplet.arrayCopy(this.pathColors, 0, ctemp, 0, this.pathVertexCount);
                this.pathColors = ctemp;
                float[] wtemp = new float[newSize];
                PApplet.arrayCopy(this.pathWeights, 0, wtemp, 0, this.pathVertexCount);
                this.pathWeights = wtemp;
            }

            this.pathVertices[3 * idx + 0] = x;
            this.pathVertices[3 * idx + 1] = y;
            this.pathVertices[3 * idx + 2] = z;
            this.pathColors[idx] = c;
            this.pathWeights[idx] = w;
            ++this.pathVertexCount;
        }

        void tessellateStrokePath() {
            if (this.in.edgeCount != 0) {
                this.strokeVertices = this.pathVertices;
                this.strokeColors = this.pathColors;
                this.strokeWeights = this.pathWeights;
                if (this.is3D) {
                    this.tessellateEdges3D();
                } else if (this.is2D) {
                    this.beginNoTex();
                    this.tessellateEdges2D();
                    this.endNoTex();
                }

            }
        }

        boolean clampPolygon() {
            return false;
        }

        public void tessellateLinePath(LinePath path) {
            this.initGluTess();
            boolean clamp = this.clampLinePath();
            this.callback.init(this.in.renderMode == 1, true, false, clamp);
            int cap = this.strokeCap == 2 ? 1 : (this.strokeCap == 4 ? 2 : 0);
            int join = this.strokeJoin == 2 ? 1 : (this.strokeJoin == 32 ? 2 : 0);
            LinePath strokedPath = LinePath.createStrokedPath(path, this.strokeWeight, cap, join);
            this.gluTess.beginPolygon();
            float[] coords = new float[6];
            PathIterator iter = strokedPath.getPathIterator();
            int rule = iter.getWindingRule();
            switch(rule) {
                case 0:
                    this.gluTess.setWindingRule(PGL.TESS_WINDING_ODD);
                    break;
                case 1:
                    this.gluTess.setWindingRule(PGL.TESS_WINDING_NONZERO);
            }

            for(; !iter.isDone(); iter.next()) {
                switch(iter.currentSegment(coords)) {
                    case 0:
                        this.gluTess.beginContour();
                    case 1:
                        double[] vertex = new double[]{(double)coords[0], (double)coords[1], 0.0D, (double)coords[2], (double)coords[3], (double)coords[4], (double)coords[5], 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D};
                        this.gluTess.addVertex(vertex);
                        break;
                    case 2:
                        this.gluTess.endContour();
                }
            }

            this.gluTess.endPolygon();
        }

        boolean clampLinePath() {
            return this.clamp2D() && this.strokeCap == 4 && this.strokeJoin == 32 && !this.subPixelStroke(this.strokeWeight);
        }

        protected class TessellatorCallback implements processing.opengl.PGL.TessellatorCallback {
            PGraphicsOpenGL.AttributeMap attribs;
            boolean calcNormals;
            boolean strokeTess;
            boolean clampXY;
            PGraphicsOpenGL.IndexCache cache;
            int cacheIndex;
            int vertFirst;
            int vertCount;
            int vertOffset;
            int primitive;

            public TessellatorCallback(PGraphicsOpenGL.AttributeMap attribs) {
                this.attribs = attribs;
            }

            public void init(boolean addCache, boolean strokeTess, boolean calcNorm, boolean clampXY) {
                this.strokeTess = strokeTess;
                this.calcNormals = calcNorm;
                this.clampXY = clampXY;
                this.cache = Tessellator.this.tess.polyIndexCache;
                if (addCache) {
                    this.cache.addNew();
                }

            }

            public void begin(int type) {
                this.cacheIndex = this.cache.getLast();
                if (Tessellator.this.firstPolyIndexCache == -1) {
                    Tessellator.this.firstPolyIndexCache = this.cacheIndex;
                }

                if (this.strokeTess && Tessellator.this.firstLineIndexCache == -1) {
                    Tessellator.this.firstLineIndexCache = this.cacheIndex;
                }

                this.vertFirst = this.cache.vertexCount[this.cacheIndex];
                this.vertOffset = this.cache.vertexOffset[this.cacheIndex];
                this.vertCount = 0;
                if (type == PGL.TRIANGLE_FAN) {
                    this.primitive = 11;
                } else if (type == PGL.TRIANGLE_STRIP) {
                    this.primitive = 10;
                } else if (type == PGL.TRIANGLES) {
                    this.primitive = 9;
                }

            }

            public void end() {
                if (PGL.MAX_VERTEX_INDEX1 <= this.vertFirst + this.vertCount) {
                    this.cacheIndex = this.cache.addNew();
                    this.vertFirst = this.cache.vertexCount[this.cacheIndex];
                    this.vertOffset = this.cache.vertexOffset[this.cacheIndex];
                }

                int indCount;
                indCount = 0;
                int i;
                label63:
                switch(this.primitive) {
                    case 9:
                        indCount = this.vertCount;

                        for(i = 0; i < this.vertCount; ++i) {
                            this.addIndex(i);
                        }

                        if (this.calcNormals) {
                            for(i = 0; i < this.vertCount / 3; ++i) {
                                int i0 = 3 * i + 0;
                                int i1 = 3 * i + 1;
                                int i2 = 3 * i + 2;
                                this.calcTriNormal(i0, i1, i2);
                            }
                        }
                        break;
                    case 10:
                        indCount = 3 * (this.vertCount - 2);
                        i = 1;

                        while(true) {
                            if (i >= this.vertCount - 1) {
                                break label63;
                            }

                            if (i % 2 == 0) {
                                this.addIndex(i + 1);
                                this.addIndex(i);
                                this.addIndex(i - 1);
                                if (this.calcNormals) {
                                    this.calcTriNormal(i + 1, i, i - 1);
                                }
                            } else {
                                this.addIndex(i - 1);
                                this.addIndex(i);
                                this.addIndex(i + 1);
                                if (this.calcNormals) {
                                    this.calcTriNormal(i - 1, i, i + 1);
                                }
                            }

                            ++i;
                        }
                    case 11:
                        indCount = 3 * (this.vertCount - 2);

                        for(i = 1; i < this.vertCount - 1; ++i) {
                            this.addIndex(0);
                            this.addIndex(i);
                            this.addIndex(i + 1);
                            if (this.calcNormals) {
                                this.calcTriNormal(0, i, i + 1);
                            }
                        }
                }

                this.cache.incCounts(this.cacheIndex, indCount, this.vertCount);
                Tessellator.this.lastPolyIndexCache = this.cacheIndex;
                if (this.strokeTess) {
                    Tessellator.this.lastLineIndexCache = this.cacheIndex;
                }

            }

            protected void addIndex(int tessIdx) {
                Tessellator.this.tess.polyIndexCheck();
                Tessellator.this.tess.polyIndices[Tessellator.this.tess.polyIndexCount - 1] = (short)(this.vertFirst + tessIdx);
            }

            protected void calcTriNormal(int tessIdx0, int tessIdx1, int tessIdx2) {
                Tessellator.this.tess.calcPolyNormal(this.vertFirst + this.vertOffset + tessIdx0, this.vertFirst + this.vertOffset + tessIdx1, this.vertFirst + this.vertOffset + tessIdx2);
            }

            public void vertex(Object data) {
                if (data instanceof double[]) {
                    double[] d = (double[])((double[])data);
                    int l = d.length;
                    if (l < 25) {
                        throw new RuntimeException("TessCallback vertex() data is too small");
                    } else if (this.vertCount < PGL.MAX_VERTEX_INDEX1) {
                        Tessellator.this.tess.addPolyVertex(d, this.clampXY);
                        ++this.vertCount;
                    } else {
                        throw new RuntimeException("The tessellator is generating too many vertices, reduce complexity of shape.");
                    }
                } else {
                    throw new RuntimeException("TessCallback vertex() data not understood");
                }
            }

            public void error(int errnum) {
                String estring = Tessellator.this.pg.pgl.tessError(errnum);
                PGraphics.showWarning("Tessellation Error: %1$s", new Object[]{estring});
            }

            public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
                int n = ((double[])((double[])data[0])).length;
                double[] vertex = new double[n];
                vertex[0] = coords[0];
                vertex[1] = coords[1];
                vertex[2] = coords[2];

                int pos;
                int i;
                for(pos = 3; pos < n; ++pos) {
                    vertex[pos] = 0.0D;

                    for(i = 0; i < 4; ++i) {
                        double[] vertData = (double[])((double[])data[i]);
                        if (vertData != null) {
                            vertex[pos] += (double)weight[i] * vertData[pos];
                        }
                    }
                }

                this.normalize(vertex, 7);
                if (25 < n) {
                    pos = 25;

                    for(i = 0; i < this.attribs.size(); ++i) {
                        PGraphicsOpenGL.VertexAttribute attrib = this.attribs.get(i);
                        if (attrib.isNormal()) {
                            this.normalize(vertex, pos);
                            pos += 3;
                        } else {
                            pos += attrib.size;
                        }
                    }
                }

                outData[0] = vertex;
            }

            private void normalize(double[] v, int i) {
                double sum = v[i] * v[i] + v[i + 1] * v[i + 1] + v[i + 2] * v[i + 2];
                double len = Math.sqrt(sum);
                if (0.0D < len) {
                    v[i] /= len;
                    v[i + 1] /= len;
                    v[i + 2] /= len;
                }

            }
        }
    }

    protected static class TessGeometry {
        int renderMode;
        PGraphicsOpenGL pg;
        PGraphicsOpenGL.AttributeMap polyAttribs;
        int polyVertexCount;
        int firstPolyVertex;
        int lastPolyVertex;
        FloatBuffer polyVerticesBuffer;
        IntBuffer polyColorsBuffer;
        FloatBuffer polyNormalsBuffer;
        FloatBuffer polyTexCoordsBuffer;
        IntBuffer polyAmbientBuffer;
        IntBuffer polySpecularBuffer;
        IntBuffer polyEmissiveBuffer;
        FloatBuffer polyShininessBuffer;
        HashMap<String, Buffer> polyAttribBuffers = new HashMap();
        int polyIndexCount;
        int firstPolyIndex;
        int lastPolyIndex;
        ShortBuffer polyIndicesBuffer;
        PGraphicsOpenGL.IndexCache polyIndexCache = new PGraphicsOpenGL.IndexCache();
        int lineVertexCount;
        int firstLineVertex;
        int lastLineVertex;
        FloatBuffer lineVerticesBuffer;
        IntBuffer lineColorsBuffer;
        FloatBuffer lineDirectionsBuffer;
        int lineIndexCount;
        int firstLineIndex;
        int lastLineIndex;
        ShortBuffer lineIndicesBuffer;
        PGraphicsOpenGL.IndexCache lineIndexCache = new PGraphicsOpenGL.IndexCache();
        int pointVertexCount;
        int firstPointVertex;
        int lastPointVertex;
        FloatBuffer pointVerticesBuffer;
        IntBuffer pointColorsBuffer;
        FloatBuffer pointOffsetsBuffer;
        int pointIndexCount;
        int firstPointIndex;
        int lastPointIndex;
        ShortBuffer pointIndicesBuffer;
        PGraphicsOpenGL.IndexCache pointIndexCache = new PGraphicsOpenGL.IndexCache();
        float[] polyVertices;
        int[] polyColors;
        float[] polyNormals;
        float[] polyTexCoords;
        int[] polyAmbient;
        int[] polySpecular;
        int[] polyEmissive;
        float[] polyShininess;
        short[] polyIndices;
        float[] lineVertices;
        int[] lineColors;
        float[] lineDirections;
        short[] lineIndices;
        float[] pointVertices;
        int[] pointColors;
        float[] pointOffsets;
        short[] pointIndices;
        HashMap<String, float[]> fpolyAttribs = new HashMap();
        HashMap<String, int[]> ipolyAttribs = new HashMap();
        HashMap<String, byte[]> bpolyAttribs = new HashMap();

        TessGeometry(PGraphicsOpenGL pg, PGraphicsOpenGL.AttributeMap attr, int mode) {
            this.pg = pg;
            this.polyAttribs = attr;
            this.renderMode = mode;
            this.allocate();
        }

        void allocate() {
            this.polyVertices = new float[4 * PGL.DEFAULT_TESS_VERTICES];
            this.polyColors = new int[PGL.DEFAULT_TESS_VERTICES];
            this.polyNormals = new float[3 * PGL.DEFAULT_TESS_VERTICES];
            this.polyTexCoords = new float[2 * PGL.DEFAULT_TESS_VERTICES];
            this.polyAmbient = new int[PGL.DEFAULT_TESS_VERTICES];
            this.polySpecular = new int[PGL.DEFAULT_TESS_VERTICES];
            this.polyEmissive = new int[PGL.DEFAULT_TESS_VERTICES];
            this.polyShininess = new float[PGL.DEFAULT_TESS_VERTICES];
            this.polyIndices = new short[PGL.DEFAULT_TESS_VERTICES];
            this.lineVertices = new float[4 * PGL.DEFAULT_TESS_VERTICES];
            this.lineColors = new int[PGL.DEFAULT_TESS_VERTICES];
            this.lineDirections = new float[4 * PGL.DEFAULT_TESS_VERTICES];
            this.lineIndices = new short[PGL.DEFAULT_TESS_VERTICES];
            this.pointVertices = new float[4 * PGL.DEFAULT_TESS_VERTICES];
            this.pointColors = new int[PGL.DEFAULT_TESS_VERTICES];
            this.pointOffsets = new float[2 * PGL.DEFAULT_TESS_VERTICES];
            this.pointIndices = new short[PGL.DEFAULT_TESS_VERTICES];
            this.polyVerticesBuffer = PGL.allocateFloatBuffer(this.polyVertices);
            this.polyColorsBuffer = PGL.allocateIntBuffer(this.polyColors);
            this.polyNormalsBuffer = PGL.allocateFloatBuffer(this.polyNormals);
            this.polyTexCoordsBuffer = PGL.allocateFloatBuffer(this.polyTexCoords);
            this.polyAmbientBuffer = PGL.allocateIntBuffer(this.polyAmbient);
            this.polySpecularBuffer = PGL.allocateIntBuffer(this.polySpecular);
            this.polyEmissiveBuffer = PGL.allocateIntBuffer(this.polyEmissive);
            this.polyShininessBuffer = PGL.allocateFloatBuffer(this.polyShininess);
            this.polyIndicesBuffer = PGL.allocateShortBuffer(this.polyIndices);
            this.lineVerticesBuffer = PGL.allocateFloatBuffer(this.lineVertices);
            this.lineColorsBuffer = PGL.allocateIntBuffer(this.lineColors);
            this.lineDirectionsBuffer = PGL.allocateFloatBuffer(this.lineDirections);
            this.lineIndicesBuffer = PGL.allocateShortBuffer(this.lineIndices);
            this.pointVerticesBuffer = PGL.allocateFloatBuffer(this.pointVertices);
            this.pointColorsBuffer = PGL.allocateIntBuffer(this.pointColors);
            this.pointOffsetsBuffer = PGL.allocateFloatBuffer(this.pointOffsets);
            this.pointIndicesBuffer = PGL.allocateShortBuffer(this.pointIndices);
            this.clear();
        }

        void initAttrib(PGraphicsOpenGL.VertexAttribute attrib) {
            if (attrib.type == PGL.FLOAT && !this.fpolyAttribs.containsKey(attrib.name)) {
                float[] temp = new float[attrib.tessSize * PGL.DEFAULT_TESS_VERTICES];
                this.fpolyAttribs.put(attrib.name, temp);
                this.polyAttribBuffers.put(attrib.name, PGL.allocateFloatBuffer(temp));
            } else if (attrib.type == PGL.INT && !this.ipolyAttribs.containsKey(attrib.name)) {
                int[] temp = new int[attrib.tessSize * PGL.DEFAULT_TESS_VERTICES];
                this.ipolyAttribs.put(attrib.name, temp);
                this.polyAttribBuffers.put(attrib.name, PGL.allocateIntBuffer(temp));
            } else if (attrib.type == PGL.BOOL && !this.bpolyAttribs.containsKey(attrib.name)) {
                byte[] temp = new byte[attrib.tessSize * PGL.DEFAULT_TESS_VERTICES];
                this.bpolyAttribs.put(attrib.name, temp);
                this.polyAttribBuffers.put(attrib.name, PGL.allocateByteBuffer(temp));
            }

        }

        void clear() {
            this.firstPolyVertex = this.lastPolyVertex = this.polyVertexCount = 0;
            this.firstPolyIndex = this.lastPolyIndex = this.polyIndexCount = 0;
            this.firstLineVertex = this.lastLineVertex = this.lineVertexCount = 0;
            this.firstLineIndex = this.lastLineIndex = this.lineIndexCount = 0;
            this.firstPointVertex = this.lastPointVertex = this.pointVertexCount = 0;
            this.firstPointIndex = this.lastPointIndex = this.pointIndexCount = 0;
            this.polyIndexCache.clear();
            this.lineIndexCache.clear();
            this.pointIndexCache.clear();
        }

        void polyVertexCheck() {
            if (this.polyVertexCount == this.polyVertices.length / 4) {
                int newSize = this.polyVertexCount << 1;
                this.expandPolyVertices(newSize);
                this.expandPolyColors(newSize);
                this.expandPolyNormals(newSize);
                this.expandPolyTexCoords(newSize);
                this.expandPolyAmbient(newSize);
                this.expandPolySpecular(newSize);
                this.expandPolyEmissive(newSize);
                this.expandPolyShininess(newSize);
                this.expandAttributes(newSize);
            }

            this.firstPolyVertex = this.polyVertexCount++;
            this.lastPolyVertex = this.polyVertexCount - 1;
        }

        void polyVertexCheck(int count) {
            int oldSize = this.polyVertices.length / 4;
            if (this.polyVertexCount + count > oldSize) {
                int newSize = PGraphicsOpenGL.expandArraySize(oldSize, this.polyVertexCount + count);
                this.expandPolyVertices(newSize);
                this.expandPolyColors(newSize);
                this.expandPolyNormals(newSize);
                this.expandPolyTexCoords(newSize);
                this.expandPolyAmbient(newSize);
                this.expandPolySpecular(newSize);
                this.expandPolyEmissive(newSize);
                this.expandPolyShininess(newSize);
                this.expandAttributes(newSize);
            }

            this.firstPolyVertex = this.polyVertexCount;
            this.polyVertexCount += count;
            this.lastPolyVertex = this.polyVertexCount - 1;
        }

        void polyIndexCheck(int count) {
            int oldSize = this.polyIndices.length;
            if (this.polyIndexCount + count > oldSize) {
                int newSize = PGraphicsOpenGL.expandArraySize(oldSize, this.polyIndexCount + count);
                this.expandPolyIndices(newSize);
            }

            this.firstPolyIndex = this.polyIndexCount;
            this.polyIndexCount += count;
            this.lastPolyIndex = this.polyIndexCount - 1;
        }

        void polyIndexCheck() {
            if (this.polyIndexCount == this.polyIndices.length) {
                int newSize = this.polyIndexCount << 1;
                this.expandPolyIndices(newSize);
            }

            this.firstPolyIndex = this.polyIndexCount++;
            this.lastPolyIndex = this.polyIndexCount - 1;
        }

        void lineVertexCheck(int count) {
            int oldSize = this.lineVertices.length / 4;
            if (this.lineVertexCount + count > oldSize) {
                int newSize = PGraphicsOpenGL.expandArraySize(oldSize, this.lineVertexCount + count);
                this.expandLineVertices(newSize);
                this.expandLineColors(newSize);
                this.expandLineDirections(newSize);
            }

            this.firstLineVertex = this.lineVertexCount;
            this.lineVertexCount += count;
            this.lastLineVertex = this.lineVertexCount - 1;
        }

        void lineIndexCheck(int count) {
            int oldSize = this.lineIndices.length;
            if (this.lineIndexCount + count > oldSize) {
                int newSize = PGraphicsOpenGL.expandArraySize(oldSize, this.lineIndexCount + count);
                this.expandLineIndices(newSize);
            }

            this.firstLineIndex = this.lineIndexCount;
            this.lineIndexCount += count;
            this.lastLineIndex = this.lineIndexCount - 1;
        }

        void pointVertexCheck(int count) {
            int oldSize = this.pointVertices.length / 4;
            if (this.pointVertexCount + count > oldSize) {
                int newSize = PGraphicsOpenGL.expandArraySize(oldSize, this.pointVertexCount + count);
                this.expandPointVertices(newSize);
                this.expandPointColors(newSize);
                this.expandPointOffsets(newSize);
            }

            this.firstPointVertex = this.pointVertexCount;
            this.pointVertexCount += count;
            this.lastPointVertex = this.pointVertexCount - 1;
        }

        void pointIndexCheck(int count) {
            int oldSize = this.pointIndices.length;
            if (this.pointIndexCount + count > oldSize) {
                int newSize = PGraphicsOpenGL.expandArraySize(oldSize, this.pointIndexCount + count);
                this.expandPointIndices(newSize);
            }

            this.firstPointIndex = this.pointIndexCount;
            this.pointIndexCount += count;
            this.lastPointIndex = this.pointIndexCount - 1;
        }

        boolean isFull() {
            return PGL.FLUSH_VERTEX_COUNT <= this.polyVertexCount || PGL.FLUSH_VERTEX_COUNT <= this.lineVertexCount || PGL.FLUSH_VERTEX_COUNT <= this.pointVertexCount;
        }

        void getPolyVertexMin(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x = PApplet.min(v.x, this.polyVertices[index++]);
                v.y = PApplet.min(v.y, this.polyVertices[index++]);
                v.z = PApplet.min(v.z, this.polyVertices[index]);
            }

        }

        void getLineVertexMin(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x = PApplet.min(v.x, this.lineVertices[index++]);
                v.y = PApplet.min(v.y, this.lineVertices[index++]);
                v.z = PApplet.min(v.z, this.lineVertices[index]);
            }

        }

        void getPointVertexMin(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x = PApplet.min(v.x, this.pointVertices[index++]);
                v.y = PApplet.min(v.y, this.pointVertices[index++]);
                v.z = PApplet.min(v.z, this.pointVertices[index]);
            }

        }

        void getPolyVertexMax(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x = PApplet.max(v.x, this.polyVertices[index++]);
                v.y = PApplet.max(v.y, this.polyVertices[index++]);
                v.z = PApplet.max(v.z, this.polyVertices[index]);
            }

        }

        void getLineVertexMax(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x = PApplet.max(v.x, this.lineVertices[index++]);
                v.y = PApplet.max(v.y, this.lineVertices[index++]);
                v.z = PApplet.max(v.z, this.lineVertices[index]);
            }

        }

        void getPointVertexMax(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x = PApplet.max(v.x, this.pointVertices[index++]);
                v.y = PApplet.max(v.y, this.pointVertices[index++]);
                v.z = PApplet.max(v.z, this.pointVertices[index]);
            }

        }

        int getPolyVertexSum(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x += this.polyVertices[index++];
                v.y += this.polyVertices[index++];
                v.z += this.polyVertices[index];
            }

            return last - first + 1;
        }

        int getLineVertexSum(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x += this.lineVertices[index++];
                v.y += this.lineVertices[index++];
                v.z += this.lineVertices[index];
            }

            return last - first + 1;
        }

        int getPointVertexSum(PVector v, int first, int last) {
            for(int i = first; i <= last; ++i) {
                int index = 4 * i;
                v.x += this.pointVertices[index++];
                v.y += this.pointVertices[index++];
                v.z += this.pointVertices[index];
            }

            return last - first + 1;
        }

        protected void updatePolyVerticesBuffer() {
            this.updatePolyVerticesBuffer(0, this.polyVertexCount);
        }

        protected void updatePolyVerticesBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.polyVerticesBuffer, this.polyVertices, 4 * offset, 4 * size);
        }

        protected void updatePolyColorsBuffer() {
            this.updatePolyColorsBuffer(0, this.polyVertexCount);
        }

        protected void updatePolyColorsBuffer(int offset, int size) {
            PGL.updateIntBuffer(this.polyColorsBuffer, this.polyColors, offset, size);
        }

        protected void updatePolyNormalsBuffer() {
            this.updatePolyNormalsBuffer(0, this.polyVertexCount);
        }

        protected void updatePolyNormalsBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.polyNormalsBuffer, this.polyNormals, 3 * offset, 3 * size);
        }

        protected void updatePolyTexCoordsBuffer() {
            this.updatePolyTexCoordsBuffer(0, this.polyVertexCount);
        }

        protected void updatePolyTexCoordsBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.polyTexCoordsBuffer, this.polyTexCoords, 2 * offset, 2 * size);
        }

        protected void updatePolyAmbientBuffer() {
            this.updatePolyAmbientBuffer(0, this.polyVertexCount);
        }

        protected void updatePolyAmbientBuffer(int offset, int size) {
            PGL.updateIntBuffer(this.polyAmbientBuffer, this.polyAmbient, offset, size);
        }

        protected void updatePolySpecularBuffer() {
            this.updatePolySpecularBuffer(0, this.polyVertexCount);
        }

        protected void updatePolySpecularBuffer(int offset, int size) {
            PGL.updateIntBuffer(this.polySpecularBuffer, this.polySpecular, offset, size);
        }

        protected void updatePolyEmissiveBuffer() {
            this.updatePolyEmissiveBuffer(0, this.polyVertexCount);
        }

        protected void updatePolyEmissiveBuffer(int offset, int size) {
            PGL.updateIntBuffer(this.polyEmissiveBuffer, this.polyEmissive, offset, size);
        }

        protected void updatePolyShininessBuffer() {
            this.updatePolyShininessBuffer(0, this.polyVertexCount);
        }

        protected void updatePolyShininessBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.polyShininessBuffer, this.polyShininess, offset, size);
        }

        protected void updateAttribBuffer(String name) {
            this.updateAttribBuffer(name, 0, this.polyVertexCount);
        }

        protected void updateAttribBuffer(String name, int offset, int size) {
            PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
            if (attrib.type == PGL.FLOAT) {
                FloatBuffer buffer = (FloatBuffer)this.polyAttribBuffers.get(name);
                float[] array = (float[])this.fpolyAttribs.get(name);
                PGL.updateFloatBuffer(buffer, array, attrib.tessSize * offset, attrib.tessSize * size);
            } else if (attrib.type == PGL.INT) {
                IntBuffer buffer = (IntBuffer)this.polyAttribBuffers.get(name);
                int[] array = (int[])this.ipolyAttribs.get(name);
                PGL.updateIntBuffer(buffer, array, attrib.tessSize * offset, attrib.tessSize * size);
            } else if (attrib.type == PGL.BOOL) {
                ByteBuffer buffer = (ByteBuffer)this.polyAttribBuffers.get(name);
                byte[] array = (byte[])this.bpolyAttribs.get(name);
                PGL.updateByteBuffer(buffer, array, attrib.tessSize * offset, attrib.tessSize * size);
            }

        }

        protected void updatePolyIndicesBuffer() {
            this.updatePolyIndicesBuffer(0, this.polyIndexCount);
        }

        protected void updatePolyIndicesBuffer(int offset, int size) {
            PGL.updateShortBuffer(this.polyIndicesBuffer, this.polyIndices, offset, size);
        }

        protected void updateLineVerticesBuffer() {
            this.updateLineVerticesBuffer(0, this.lineVertexCount);
        }

        protected void updateLineVerticesBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.lineVerticesBuffer, this.lineVertices, 4 * offset, 4 * size);
        }

        protected void updateLineColorsBuffer() {
            this.updateLineColorsBuffer(0, this.lineVertexCount);
        }

        protected void updateLineColorsBuffer(int offset, int size) {
            PGL.updateIntBuffer(this.lineColorsBuffer, this.lineColors, offset, size);
        }

        protected void updateLineDirectionsBuffer() {
            this.updateLineDirectionsBuffer(0, this.lineVertexCount);
        }

        protected void updateLineDirectionsBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.lineDirectionsBuffer, this.lineDirections, 4 * offset, 4 * size);
        }

        protected void updateLineIndicesBuffer() {
            this.updateLineIndicesBuffer(0, this.lineIndexCount);
        }

        protected void updateLineIndicesBuffer(int offset, int size) {
            PGL.updateShortBuffer(this.lineIndicesBuffer, this.lineIndices, offset, size);
        }

        protected void updatePointVerticesBuffer() {
            this.updatePointVerticesBuffer(0, this.pointVertexCount);
        }

        protected void updatePointVerticesBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.pointVerticesBuffer, this.pointVertices, 4 * offset, 4 * size);
        }

        protected void updatePointColorsBuffer() {
            this.updatePointColorsBuffer(0, this.pointVertexCount);
        }

        protected void updatePointColorsBuffer(int offset, int size) {
            PGL.updateIntBuffer(this.pointColorsBuffer, this.pointColors, offset, size);
        }

        protected void updatePointOffsetsBuffer() {
            this.updatePointOffsetsBuffer(0, this.pointVertexCount);
        }

        protected void updatePointOffsetsBuffer(int offset, int size) {
            PGL.updateFloatBuffer(this.pointOffsetsBuffer, this.pointOffsets, 2 * offset, 2 * size);
        }

        protected void updatePointIndicesBuffer() {
            this.updatePointIndicesBuffer(0, this.pointIndexCount);
        }

        protected void updatePointIndicesBuffer(int offset, int size) {
            PGL.updateShortBuffer(this.pointIndicesBuffer, this.pointIndices, offset, size);
        }

        void expandPolyVertices(int n) {
            float[] temp = new float[4 * n];
            PApplet.arrayCopy(this.polyVertices, 0, temp, 0, 4 * this.polyVertexCount);
            this.polyVertices = temp;
            this.polyVerticesBuffer = PGL.allocateFloatBuffer(this.polyVertices);
        }

        void expandPolyColors(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.polyColors, 0, temp, 0, this.polyVertexCount);
            this.polyColors = temp;
            this.polyColorsBuffer = PGL.allocateIntBuffer(this.polyColors);
        }

        void expandPolyNormals(int n) {
            float[] temp = new float[3 * n];
            PApplet.arrayCopy(this.polyNormals, 0, temp, 0, 3 * this.polyVertexCount);
            this.polyNormals = temp;
            this.polyNormalsBuffer = PGL.allocateFloatBuffer(this.polyNormals);
        }

        void expandPolyTexCoords(int n) {
            float[] temp = new float[2 * n];
            PApplet.arrayCopy(this.polyTexCoords, 0, temp, 0, 2 * this.polyVertexCount);
            this.polyTexCoords = temp;
            this.polyTexCoordsBuffer = PGL.allocateFloatBuffer(this.polyTexCoords);
        }

        void expandPolyAmbient(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.polyAmbient, 0, temp, 0, this.polyVertexCount);
            this.polyAmbient = temp;
            this.polyAmbientBuffer = PGL.allocateIntBuffer(this.polyAmbient);
        }

        void expandPolySpecular(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.polySpecular, 0, temp, 0, this.polyVertexCount);
            this.polySpecular = temp;
            this.polySpecularBuffer = PGL.allocateIntBuffer(this.polySpecular);
        }

        void expandPolyEmissive(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.polyEmissive, 0, temp, 0, this.polyVertexCount);
            this.polyEmissive = temp;
            this.polyEmissiveBuffer = PGL.allocateIntBuffer(this.polyEmissive);
        }

        void expandPolyShininess(int n) {
            float[] temp = new float[n];
            PApplet.arrayCopy(this.polyShininess, 0, temp, 0, this.polyVertexCount);
            this.polyShininess = temp;
            this.polyShininessBuffer = PGL.allocateFloatBuffer(this.polyShininess);
        }

        void expandAttributes(int n) {
            Iterator var2 = this.polyAttribs.keySet().iterator();

            while(var2.hasNext()) {
                String name = (String)var2.next();
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                if (attrib.type == PGL.FLOAT) {
                    this.expandFloatAttribute(attrib, n);
                } else if (attrib.type == PGL.INT) {
                    this.expandIntAttribute(attrib, n);
                } else if (attrib.type == PGL.BOOL) {
                    this.expandBoolAttribute(attrib, n);
                }
            }

        }

        void expandFloatAttribute(PGraphicsOpenGL.VertexAttribute attrib, int n) {
            float[] array = (float[])this.fpolyAttribs.get(attrib.name);
            float[] temp = new float[attrib.tessSize * n];
            PApplet.arrayCopy(array, 0, temp, 0, attrib.tessSize * this.polyVertexCount);
            this.fpolyAttribs.put(attrib.name, temp);
            this.polyAttribBuffers.put(attrib.name, PGL.allocateFloatBuffer(temp));
        }

        void expandIntAttribute(PGraphicsOpenGL.VertexAttribute attrib, int n) {
            int[] array = (int[])this.ipolyAttribs.get(attrib.name);
            int[] temp = new int[attrib.tessSize * n];
            PApplet.arrayCopy(array, 0, temp, 0, attrib.tessSize * this.polyVertexCount);
            this.ipolyAttribs.put(attrib.name, temp);
            this.polyAttribBuffers.put(attrib.name, PGL.allocateIntBuffer(temp));
        }

        void expandBoolAttribute(PGraphicsOpenGL.VertexAttribute attrib, int n) {
            byte[] array = (byte[])this.bpolyAttribs.get(attrib.name);
            byte[] temp = new byte[attrib.tessSize * n];
            PApplet.arrayCopy(array, 0, temp, 0, attrib.tessSize * this.polyVertexCount);
            this.bpolyAttribs.put(attrib.name, temp);
            this.polyAttribBuffers.put(attrib.name, PGL.allocateByteBuffer(temp));
        }

        void expandPolyIndices(int n) {
            short[] temp = new short[n];
            PApplet.arrayCopy(this.polyIndices, 0, temp, 0, this.polyIndexCount);
            this.polyIndices = temp;
            this.polyIndicesBuffer = PGL.allocateShortBuffer(this.polyIndices);
        }

        void expandLineVertices(int n) {
            float[] temp = new float[4 * n];
            PApplet.arrayCopy(this.lineVertices, 0, temp, 0, 4 * this.lineVertexCount);
            this.lineVertices = temp;
            this.lineVerticesBuffer = PGL.allocateFloatBuffer(this.lineVertices);
        }

        void expandLineColors(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.lineColors, 0, temp, 0, this.lineVertexCount);
            this.lineColors = temp;
            this.lineColorsBuffer = PGL.allocateIntBuffer(this.lineColors);
        }

        void expandLineDirections(int n) {
            float[] temp = new float[4 * n];
            PApplet.arrayCopy(this.lineDirections, 0, temp, 0, 4 * this.lineVertexCount);
            this.lineDirections = temp;
            this.lineDirectionsBuffer = PGL.allocateFloatBuffer(this.lineDirections);
        }

        void expandLineIndices(int n) {
            short[] temp = new short[n];
            PApplet.arrayCopy(this.lineIndices, 0, temp, 0, this.lineIndexCount);
            this.lineIndices = temp;
            this.lineIndicesBuffer = PGL.allocateShortBuffer(this.lineIndices);
        }

        void expandPointVertices(int n) {
            float[] temp = new float[4 * n];
            PApplet.arrayCopy(this.pointVertices, 0, temp, 0, 4 * this.pointVertexCount);
            this.pointVertices = temp;
            this.pointVerticesBuffer = PGL.allocateFloatBuffer(this.pointVertices);
        }

        void expandPointColors(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.pointColors, 0, temp, 0, this.pointVertexCount);
            this.pointColors = temp;
            this.pointColorsBuffer = PGL.allocateIntBuffer(this.pointColors);
        }

        void expandPointOffsets(int n) {
            float[] temp = new float[2 * n];
            PApplet.arrayCopy(this.pointOffsets, 0, temp, 0, 2 * this.pointVertexCount);
            this.pointOffsets = temp;
            this.pointOffsetsBuffer = PGL.allocateFloatBuffer(this.pointOffsets);
        }

        void expandPointIndices(int n) {
            short[] temp = new short[n];
            PApplet.arrayCopy(this.pointIndices, 0, temp, 0, this.pointIndexCount);
            this.pointIndices = temp;
            this.pointIndicesBuffer = PGL.allocateShortBuffer(this.pointIndices);
        }

        void trim() {
            if (0 < this.polyVertexCount && this.polyVertexCount < this.polyVertices.length / 4) {
                this.trimPolyVertices();
                this.trimPolyColors();
                this.trimPolyNormals();
                this.trimPolyTexCoords();
                this.trimPolyAmbient();
                this.trimPolySpecular();
                this.trimPolyEmissive();
                this.trimPolyShininess();
                this.trimPolyAttributes();
            }

            if (0 < this.polyIndexCount && this.polyIndexCount < this.polyIndices.length) {
                this.trimPolyIndices();
            }

            if (0 < this.lineVertexCount && this.lineVertexCount < this.lineVertices.length / 4) {
                this.trimLineVertices();
                this.trimLineColors();
                this.trimLineDirections();
            }

            if (0 < this.lineIndexCount && this.lineIndexCount < this.lineIndices.length) {
                this.trimLineIndices();
            }

            if (0 < this.pointVertexCount && this.pointVertexCount < this.pointVertices.length / 4) {
                this.trimPointVertices();
                this.trimPointColors();
                this.trimPointOffsets();
            }

            if (0 < this.pointIndexCount && this.pointIndexCount < this.pointIndices.length) {
                this.trimPointIndices();
            }

        }

        void trimPolyVertices() {
            float[] temp = new float[4 * this.polyVertexCount];
            PApplet.arrayCopy(this.polyVertices, 0, temp, 0, 4 * this.polyVertexCount);
            this.polyVertices = temp;
            this.polyVerticesBuffer = PGL.allocateFloatBuffer(this.polyVertices);
        }

        void trimPolyColors() {
            int[] temp = new int[this.polyVertexCount];
            PApplet.arrayCopy(this.polyColors, 0, temp, 0, this.polyVertexCount);
            this.polyColors = temp;
            this.polyColorsBuffer = PGL.allocateIntBuffer(this.polyColors);
        }

        void trimPolyNormals() {
            float[] temp = new float[3 * this.polyVertexCount];
            PApplet.arrayCopy(this.polyNormals, 0, temp, 0, 3 * this.polyVertexCount);
            this.polyNormals = temp;
            this.polyNormalsBuffer = PGL.allocateFloatBuffer(this.polyNormals);
        }

        void trimPolyTexCoords() {
            float[] temp = new float[2 * this.polyVertexCount];
            PApplet.arrayCopy(this.polyTexCoords, 0, temp, 0, 2 * this.polyVertexCount);
            this.polyTexCoords = temp;
            this.polyTexCoordsBuffer = PGL.allocateFloatBuffer(this.polyTexCoords);
        }

        void trimPolyAmbient() {
            int[] temp = new int[this.polyVertexCount];
            PApplet.arrayCopy(this.polyAmbient, 0, temp, 0, this.polyVertexCount);
            this.polyAmbient = temp;
            this.polyAmbientBuffer = PGL.allocateIntBuffer(this.polyAmbient);
        }

        void trimPolySpecular() {
            int[] temp = new int[this.polyVertexCount];
            PApplet.arrayCopy(this.polySpecular, 0, temp, 0, this.polyVertexCount);
            this.polySpecular = temp;
            this.polySpecularBuffer = PGL.allocateIntBuffer(this.polySpecular);
        }

        void trimPolyEmissive() {
            int[] temp = new int[this.polyVertexCount];
            PApplet.arrayCopy(this.polyEmissive, 0, temp, 0, this.polyVertexCount);
            this.polyEmissive = temp;
            this.polyEmissiveBuffer = PGL.allocateIntBuffer(this.polyEmissive);
        }

        void trimPolyShininess() {
            float[] temp = new float[this.polyVertexCount];
            PApplet.arrayCopy(this.polyShininess, 0, temp, 0, this.polyVertexCount);
            this.polyShininess = temp;
            this.polyShininessBuffer = PGL.allocateFloatBuffer(this.polyShininess);
        }

        void trimPolyAttributes() {
            Iterator var1 = this.polyAttribs.keySet().iterator();

            while(var1.hasNext()) {
                String name = (String)var1.next();
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                if (attrib.type == PGL.FLOAT) {
                    this.trimFloatAttribute(attrib);
                } else if (attrib.type == PGL.INT) {
                    this.trimIntAttribute(attrib);
                } else if (attrib.type == PGL.BOOL) {
                    this.trimBoolAttribute(attrib);
                }
            }

        }

        void trimFloatAttribute(PGraphicsOpenGL.VertexAttribute attrib) {
            float[] array = (float[])this.fpolyAttribs.get(attrib.name);
            float[] temp = new float[attrib.tessSize * this.polyVertexCount];
            PApplet.arrayCopy(array, 0, temp, 0, attrib.tessSize * this.polyVertexCount);
            this.fpolyAttribs.put(attrib.name, temp);
            this.polyAttribBuffers.put(attrib.name, PGL.allocateFloatBuffer(temp));
        }

        void trimIntAttribute(PGraphicsOpenGL.VertexAttribute attrib) {
            int[] array = (int[])this.ipolyAttribs.get(attrib.name);
            int[] temp = new int[attrib.tessSize * this.polyVertexCount];
            PApplet.arrayCopy(array, 0, temp, 0, attrib.tessSize * this.polyVertexCount);
            this.ipolyAttribs.put(attrib.name, temp);
            this.polyAttribBuffers.put(attrib.name, PGL.allocateIntBuffer(temp));
        }

        void trimBoolAttribute(PGraphicsOpenGL.VertexAttribute attrib) {
            byte[] array = (byte[])this.bpolyAttribs.get(attrib.name);
            byte[] temp = new byte[attrib.tessSize * this.polyVertexCount];
            PApplet.arrayCopy(array, 0, temp, 0, attrib.tessSize * this.polyVertexCount);
            this.bpolyAttribs.put(attrib.name, temp);
            this.polyAttribBuffers.put(attrib.name, PGL.allocateByteBuffer(temp));
        }

        void trimPolyIndices() {
            short[] temp = new short[this.polyIndexCount];
            PApplet.arrayCopy(this.polyIndices, 0, temp, 0, this.polyIndexCount);
            this.polyIndices = temp;
            this.polyIndicesBuffer = PGL.allocateShortBuffer(this.polyIndices);
        }

        void trimLineVertices() {
            float[] temp = new float[4 * this.lineVertexCount];
            PApplet.arrayCopy(this.lineVertices, 0, temp, 0, 4 * this.lineVertexCount);
            this.lineVertices = temp;
            this.lineVerticesBuffer = PGL.allocateFloatBuffer(this.lineVertices);
        }

        void trimLineColors() {
            int[] temp = new int[this.lineVertexCount];
            PApplet.arrayCopy(this.lineColors, 0, temp, 0, this.lineVertexCount);
            this.lineColors = temp;
            this.lineColorsBuffer = PGL.allocateIntBuffer(this.lineColors);
        }

        void trimLineDirections() {
            float[] temp = new float[4 * this.lineVertexCount];
            PApplet.arrayCopy(this.lineDirections, 0, temp, 0, 4 * this.lineVertexCount);
            this.lineDirections = temp;
            this.lineDirectionsBuffer = PGL.allocateFloatBuffer(this.lineDirections);
        }

        void trimLineIndices() {
            short[] temp = new short[this.lineIndexCount];
            PApplet.arrayCopy(this.lineIndices, 0, temp, 0, this.lineIndexCount);
            this.lineIndices = temp;
            this.lineIndicesBuffer = PGL.allocateShortBuffer(this.lineIndices);
        }

        void trimPointVertices() {
            float[] temp = new float[4 * this.pointVertexCount];
            PApplet.arrayCopy(this.pointVertices, 0, temp, 0, 4 * this.pointVertexCount);
            this.pointVertices = temp;
            this.pointVerticesBuffer = PGL.allocateFloatBuffer(this.pointVertices);
        }

        void trimPointColors() {
            int[] temp = new int[this.pointVertexCount];
            PApplet.arrayCopy(this.pointColors, 0, temp, 0, this.pointVertexCount);
            this.pointColors = temp;
            this.pointColorsBuffer = PGL.allocateIntBuffer(this.pointColors);
        }

        void trimPointOffsets() {
            float[] temp = new float[2 * this.pointVertexCount];
            PApplet.arrayCopy(this.pointOffsets, 0, temp, 0, 2 * this.pointVertexCount);
            this.pointOffsets = temp;
            this.pointOffsetsBuffer = PGL.allocateFloatBuffer(this.pointOffsets);
        }

        void trimPointIndices() {
            short[] temp = new short[this.pointIndexCount];
            PApplet.arrayCopy(this.pointIndices, 0, temp, 0, this.pointIndexCount);
            this.pointIndices = temp;
            this.pointIndicesBuffer = PGL.allocateShortBuffer(this.pointIndices);
        }

        void incPolyIndices(int first, int last, int inc) {
            for(int i = first; i <= last; ++i) {
                short[] var10000 = this.polyIndices;
                var10000[i] = (short)(var10000[i] + inc);
            }

        }

        void incLineIndices(int first, int last, int inc) {
            for(int i = first; i <= last; ++i) {
                short[] var10000 = this.lineIndices;
                var10000[i] = (short)(var10000[i] + inc);
            }

        }

        void incPointIndices(int first, int last, int inc) {
            for(int i = first; i <= last; ++i) {
                short[] var10000 = this.pointIndices;
                var10000[i] = (short)(var10000[i] + inc);
            }

        }

        void calcPolyNormal(int i0, int i1, int i2) {
            int index = 4 * i0;
            float x0 = this.polyVertices[index++];
            float y0 = this.polyVertices[index++];
            float z0 = this.polyVertices[index];
            index = 4 * i1;
            float x1 = this.polyVertices[index++];
            float y1 = this.polyVertices[index++];
            float z1 = this.polyVertices[index];
            index = 4 * i2;
            float x2 = this.polyVertices[index++];
            float y2 = this.polyVertices[index++];
            float z2 = this.polyVertices[index];
            float v12x = x2 - x1;
            float v12y = y2 - y1;
            float v12z = z2 - z1;
            float v10x = x0 - x1;
            float v10y = y0 - y1;
            float v10z = z0 - z1;
            float nx = v12y * v10z - v10y * v12z;
            float ny = v12z * v10x - v10z * v12x;
            float nz = v12x * v10y - v10x * v12y;
            float d = PApplet.sqrt(nx * nx + ny * ny + nz * nz);
            nx /= d;
            ny /= d;
            nz /= d;
            index = 3 * i0;
            this.polyNormals[index++] = nx;
            this.polyNormals[index++] = ny;
            this.polyNormals[index] = nz;
            index = 3 * i1;
            this.polyNormals[index++] = nx;
            this.polyNormals[index++] = ny;
            this.polyNormals[index] = nz;
            index = 3 * i2;
            this.polyNormals[index++] = nx;
            this.polyNormals[index++] = ny;
            this.polyNormals[index] = nz;
        }

        void setPointVertex(int tessIdx, PGraphicsOpenGL.InGeometry in, int inIdx) {
            int index = 3 * inIdx;
            float x = in.vertices[index++];
            float y = in.vertices[index++];
            float z = in.vertices[index];
            if (this.renderMode == 0 && this.pg.flushMode == 1) {
                PMatrix3D mm = this.pg.modelview;
                index = 4 * tessIdx;
                this.pointVertices[index++] = x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03;
                this.pointVertices[index++] = x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13;
                this.pointVertices[index++] = x * mm.m20 + y * mm.m21 + z * mm.m22 + mm.m23;
                this.pointVertices[index] = x * mm.m30 + y * mm.m31 + z * mm.m32 + mm.m33;
            } else {
                index = 4 * tessIdx;
                this.pointVertices[index++] = x;
                this.pointVertices[index++] = y;
                this.pointVertices[index++] = z;
                this.pointVertices[index] = 1.0F;
            }

            this.pointColors[tessIdx] = in.strokeColors[inIdx];
        }

        void setLineVertex(int tessIdx, float[] vertices, int inIdx0, int rgba) {
            int index = 3 * inIdx0;
            float x0 = vertices[index++];
            float y0 = vertices[index++];
            float z0 = vertices[index];
            if (this.renderMode == 0 && this.pg.flushMode == 1) {
                PMatrix3D mm = this.pg.modelview;
                index = 4 * tessIdx;
                this.lineVertices[index++] = x0 * mm.m00 + y0 * mm.m01 + z0 * mm.m02 + mm.m03;
                this.lineVertices[index++] = x0 * mm.m10 + y0 * mm.m11 + z0 * mm.m12 + mm.m13;
                this.lineVertices[index++] = x0 * mm.m20 + y0 * mm.m21 + z0 * mm.m22 + mm.m23;
                this.lineVertices[index] = x0 * mm.m30 + y0 * mm.m31 + z0 * mm.m32 + mm.m33;
            } else {
                index = 4 * tessIdx;
                this.lineVertices[index++] = x0;
                this.lineVertices[index++] = y0;
                this.lineVertices[index++] = z0;
                this.lineVertices[index] = 1.0F;
            }

            this.lineColors[tessIdx] = rgba;
            index = 4 * tessIdx;
            this.lineDirections[index++] = 0.0F;
            this.lineDirections[index++] = 0.0F;
            this.lineDirections[index++] = 0.0F;
            this.lineDirections[index] = 0.0F;
        }

        void setLineVertex(int tessIdx, float[] vertices, int inIdx0, int inIdx1, int rgba, float weight) {
            int index = 3 * inIdx0;
            float x0 = vertices[index++];
            float y0 = vertices[index++];
            float z0 = vertices[index];
            index = 3 * inIdx1;
            float x1 = vertices[index++];
            float y1 = vertices[index++];
            float z1 = vertices[index];
            float dx = x1 - x0;
            float dy = y1 - y0;
            float dz = z1 - z0;
            if (this.renderMode == 0 && this.pg.flushMode == 1) {
                PMatrix3D mm = this.pg.modelview;
                index = 4 * tessIdx;
                this.lineVertices[index++] = x0 * mm.m00 + y0 * mm.m01 + z0 * mm.m02 + mm.m03;
                this.lineVertices[index++] = x0 * mm.m10 + y0 * mm.m11 + z0 * mm.m12 + mm.m13;
                this.lineVertices[index++] = x0 * mm.m20 + y0 * mm.m21 + z0 * mm.m22 + mm.m23;
                this.lineVertices[index] = x0 * mm.m30 + y0 * mm.m31 + z0 * mm.m32 + mm.m33;
                index = 4 * tessIdx;
                this.lineDirections[index++] = dx * mm.m00 + dy * mm.m01 + dz * mm.m02;
                this.lineDirections[index++] = dx * mm.m10 + dy * mm.m11 + dz * mm.m12;
                this.lineDirections[index] = dx * mm.m20 + dy * mm.m21 + dz * mm.m22;
            } else {
                index = 4 * tessIdx;
                this.lineVertices[index++] = x0;
                this.lineVertices[index++] = y0;
                this.lineVertices[index++] = z0;
                this.lineVertices[index] = 1.0F;
                index = 4 * tessIdx;
                this.lineDirections[index++] = dx;
                this.lineDirections[index++] = dy;
                this.lineDirections[index] = dz;
            }

            this.lineColors[tessIdx] = rgba;
            this.lineDirections[4 * tessIdx + 3] = weight;
        }

        void addPolyVertex(double[] d, boolean clampXY) {
            int fcolor = (int)d[3] << 24 | (int)d[4] << 16 | (int)d[5] << 8 | (int)d[6];
            int acolor = (int)d[12] << 24 | (int)d[13] << 16 | (int)d[14] << 8 | (int)d[15];
            int scolor = (int)d[16] << 24 | (int)d[17] << 16 | (int)d[18] << 8 | (int)d[19];
            int ecolor = (int)d[20] << 24 | (int)d[21] << 16 | (int)d[22] << 8 | (int)d[23];
            this.addPolyVertex((float)d[0], (float)d[1], (float)d[2], fcolor, (float)d[7], (float)d[8], (float)d[9], (float)d[10], (float)d[11], acolor, scolor, ecolor, (float)d[24], clampXY);
            if (25 < d.length) {
                PMatrix3D mm = this.pg.modelview;
                PMatrix3D nm = this.pg.modelviewInv;
                int tessIdx = this.polyVertexCount - 1;
                int pos = 25;

                for(int i = 0; i < this.polyAttribs.size(); ++i) {
                    PGraphicsOpenGL.VertexAttribute attrib = this.polyAttribs.get(i);
                    String name = attrib.name;
                    int index = attrib.tessSize * tessIdx;
                    if (attrib.isColor()) {
                        int color = (int)d[pos + 0] << 24 | (int)d[pos + 1] << 16 | (int)d[pos + 2] << 8 | (int)d[pos + 3];
                        int[] tessValues = (int[])this.ipolyAttribs.get(name);
                        tessValues[index] = color;
                        pos += 4;
                    } else {
                        float[] farray;
                        float y;
                        float z;
                        float x;
                        if (attrib.isPosition()) {
                            farray = (float[])this.fpolyAttribs.get(name);
                            x = (float)d[pos++];
                            y = (float)d[pos++];
                            z = (float)d[pos++];
                            if (this.renderMode == 0 && this.pg.flushMode == 1) {
                                if (clampXY) {
                                    farray[index++] = (float)PApplet.ceil(x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03);
                                    farray[index++] = (float)PApplet.ceil(x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13);
                                } else {
                                    farray[index++] = x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03;
                                    farray[index++] = x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13;
                                }

                                farray[index++] = x * mm.m20 + y * mm.m21 + z * mm.m22 + mm.m23;
                                farray[index] = x * mm.m30 + y * mm.m31 + z * mm.m32 + mm.m33;
                            } else {
                                farray[index++] = x;
                                farray[index++] = y;
                                farray[index++] = z;
                                farray[index] = 1.0F;
                            }
                        } else if (attrib.isNormal()) {
                            farray = (float[])this.fpolyAttribs.get(name);
                            x = (float)d[pos + 0];
                            y = (float)d[pos + 1];
                            z = (float)d[pos + 2];
                            if (this.renderMode == 0 && this.pg.flushMode == 1) {
                                farray[index++] = x * nm.m00 + y * nm.m10 + z * nm.m20;
                                farray[index++] = x * nm.m01 + y * nm.m11 + z * nm.m21;
                                farray[index] = x * nm.m02 + y * nm.m12 + z * nm.m22;
                            } else {
                                farray[index++] = x;
                                farray[index++] = y;
                                farray[index] = z;
                            }

                            pos += 3;
                        } else {
                            int n;
                            if (attrib.isFloat()) {
                                farray = (float[])this.fpolyAttribs.get(name);

                                for(n = 0; n < attrib.size; ++n) {
                                    farray[index++] = (float)d[pos++];
                                }
                            } else if (attrib.isInt()) {
                                int[] iarray = (int[])this.ipolyAttribs.get(name);

                                for(n = 0; n < attrib.size; ++n) {
                                    iarray[index++] = (int)d[pos++];
                                }
                            } else if (attrib.isBool()) {
                                byte[] barray = (byte[])this.bpolyAttribs.get(name);

                                for(n = 0; n < attrib.size; ++n) {
                                    barray[index++] = (byte)((int)d[pos++]);
                                }
                            }

                            pos += attrib.size;
                        }
                    }
                }
            }

        }

        void addPolyVertex(float x, float y, float z, int rgba, float nx, float ny, float nz, float u, float v, int am, int sp, int em, float shine, boolean clampXY) {
            this.polyVertexCheck();
            int tessIdx = this.polyVertexCount - 1;
            this.setPolyVertex(tessIdx, x, y, z, rgba, nx, ny, nz, u, v, am, sp, em, shine, clampXY);
        }

        void setPolyVertex(int tessIdx, float x, float y, float z, int rgba, boolean clampXY) {
            this.setPolyVertex(tessIdx, x, y, z, rgba, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F, clampXY);
        }

        void setPolyVertex(int tessIdx, float x, float y, float z, int rgba, float nx, float ny, float nz, float u, float v, int am, int sp, int em, float shine, boolean clampXY) {
            int index;
            if (this.renderMode == 0 && this.pg.flushMode == 1) {
                PMatrix3D mm = this.pg.modelview;
                PMatrix3D nm = this.pg.modelviewInv;
                index = 4 * tessIdx;
                if (clampXY) {
                    this.polyVertices[index++] = (float)PApplet.ceil(x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03);
                    this.polyVertices[index++] = (float)PApplet.ceil(x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13);
                } else {
                    this.polyVertices[index++] = x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03;
                    this.polyVertices[index++] = x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13;
                }

                this.polyVertices[index++] = x * mm.m20 + y * mm.m21 + z * mm.m22 + mm.m23;
                this.polyVertices[index] = x * mm.m30 + y * mm.m31 + z * mm.m32 + mm.m33;
                index = 3 * tessIdx;
                this.polyNormals[index++] = nx * nm.m00 + ny * nm.m10 + nz * nm.m20;
                this.polyNormals[index++] = nx * nm.m01 + ny * nm.m11 + nz * nm.m21;
                this.polyNormals[index] = nx * nm.m02 + ny * nm.m12 + nz * nm.m22;
            } else {
                index = 4 * tessIdx;
                this.polyVertices[index++] = x;
                this.polyVertices[index++] = y;
                this.polyVertices[index++] = z;
                this.polyVertices[index] = 1.0F;
                index = 3 * tessIdx;
                this.polyNormals[index++] = nx;
                this.polyNormals[index++] = ny;
                this.polyNormals[index] = nz;
            }

            this.polyColors[tessIdx] = rgba;
            index = 2 * tessIdx;
            this.polyTexCoords[index++] = u;
            this.polyTexCoords[index] = v;
            this.polyAmbient[tessIdx] = am;
            this.polySpecular[tessIdx] = sp;
            this.polyEmissive[tessIdx] = em;
            this.polyShininess[tessIdx] = shine;
        }

        void addPolyVertices(PGraphicsOpenGL.InGeometry in, boolean clampXY) {
            this.addPolyVertices(in, 0, in.vertexCount - 1, clampXY);
        }

        void addPolyVertex(PGraphicsOpenGL.InGeometry in, int i, boolean clampXY) {
            this.addPolyVertices(in, i, i, clampXY);
        }

        void addPolyVertices(PGraphicsOpenGL.InGeometry in, int i0, int i1, boolean clampXY) {
            int index = 0;
            int nvert = i1 - i0 + 1;
            this.polyVertexCheck(nvert);
            if (this.renderMode == 0 && this.pg.flushMode == 1) {
                this.modelviewCoords(in, i0, index, nvert, clampXY);
            } else if (nvert <= PGL.MIN_ARRAYCOPY_SIZE) {
                this.copyFewCoords(in, i0, index, nvert);
            } else {
                this.copyManyCoords(in, i0, index, nvert);
            }

            if (nvert <= PGL.MIN_ARRAYCOPY_SIZE) {
                this.copyFewAttribs(in, i0, index, nvert);
            } else {
                this.copyManyAttribs(in, i0, index, nvert);
            }

        }

        private void modelviewCoords(PGraphicsOpenGL.InGeometry in, int i0, int index, int nvert, boolean clampXY) {
            PMatrix3D mm = this.pg.modelview;
            PMatrix3D nm = this.pg.modelviewInv;

            for(int i = 0; i < nvert; ++i) {
                int inIdx = i0 + i;
                int tessIdx = this.firstPolyVertex + i;
                index = 3 * inIdx;
                float x = in.vertices[index++];
                float y = in.vertices[index++];
                float z = in.vertices[index];
                index = 3 * inIdx;
                float nx = in.normals[index++];
                float ny = in.normals[index++];
                float nz = in.normals[index];
                index = 4 * tessIdx;
                if (clampXY) {
                    this.polyVertices[index++] = (float)PApplet.ceil(x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03);
                    this.polyVertices[index++] = (float)PApplet.ceil(x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13);
                } else {
                    this.polyVertices[index++] = x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03;
                    this.polyVertices[index++] = x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13;
                }

                this.polyVertices[index++] = x * mm.m20 + y * mm.m21 + z * mm.m22 + mm.m23;
                this.polyVertices[index] = x * mm.m30 + y * mm.m31 + z * mm.m32 + mm.m33;
                index = 3 * tessIdx;
                this.polyNormals[index++] = nx * nm.m00 + ny * nm.m10 + nz * nm.m20;
                this.polyNormals[index++] = nx * nm.m01 + ny * nm.m11 + nz * nm.m21;
                this.polyNormals[index] = nx * nm.m02 + ny * nm.m12 + nz * nm.m22;
                Iterator var17 = this.polyAttribs.keySet().iterator();

                while(var17.hasNext()) {
                    String name = (String)var17.next();
                    PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                    if (!attrib.isColor() && !attrib.isOther()) {
                        float[] inValues = (float[])in.fattribs.get(name);
                        index = 3 * inIdx;
                        x = inValues[index++];
                        y = inValues[index++];
                        z = inValues[index];
                        float[] tessValues = (float[])this.fpolyAttribs.get(name);
                        if (attrib.isPosition()) {
                            index = 4 * tessIdx;
                            if (clampXY) {
                                tessValues[index++] = (float)PApplet.ceil(x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03);
                                tessValues[index++] = (float)PApplet.ceil(x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13);
                            } else {
                                tessValues[index++] = x * mm.m00 + y * mm.m01 + z * mm.m02 + mm.m03;
                                tessValues[index++] = x * mm.m10 + y * mm.m11 + z * mm.m12 + mm.m13;
                            }

                            tessValues[index++] = x * mm.m20 + y * mm.m21 + z * mm.m22 + mm.m23;
                            tessValues[index] = x * mm.m30 + y * mm.m31 + z * mm.m32 + mm.m33;
                        } else {
                            index = 3 * tessIdx;
                            tessValues[index++] = x * nm.m00 + y * nm.m10 + z * nm.m20;
                            tessValues[index++] = x * nm.m01 + y * nm.m11 + z * nm.m21;
                            tessValues[index] = x * nm.m02 + y * nm.m12 + z * nm.m22;
                        }
                    }
                }
            }

        }

        private void copyFewCoords(PGraphicsOpenGL.InGeometry in, int i0, int index, int nvert) {
            for(int i = 0; i < nvert; ++i) {
                int inIdx = i0 + i;
                int tessIdx = this.firstPolyVertex + i;
                index = 3 * inIdx;
                float x = in.vertices[index++];
                float y = in.vertices[index++];
                float z = in.vertices[index];
                index = 3 * inIdx;
                float nx = in.normals[index++];
                float ny = in.normals[index++];
                float nz = in.normals[index];
                index = 4 * tessIdx;
                this.polyVertices[index++] = x;
                this.polyVertices[index++] = y;
                this.polyVertices[index++] = z;
                this.polyVertices[index] = 1.0F;
                index = 3 * tessIdx;
                this.polyNormals[index++] = nx;
                this.polyNormals[index++] = ny;
                this.polyNormals[index] = nz;
                Iterator var14 = this.polyAttribs.keySet().iterator();

                while(var14.hasNext()) {
                    String name = (String)var14.next();
                    PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                    if (!attrib.isColor() && !attrib.isOther()) {
                        float[] inValues = (float[])in.fattribs.get(name);
                        index = 3 * inIdx;
                        x = inValues[index++];
                        y = inValues[index++];
                        z = inValues[index];
                        float[] tessValues = (float[])this.fpolyAttribs.get(name);
                        if (attrib.isPosition()) {
                            index = 4 * tessIdx;
                            tessValues[index++] = x;
                            tessValues[index++] = y;
                            tessValues[index++] = z;
                            tessValues[index] = 1.0F;
                        } else {
                            index = 3 * tessIdx;
                            tessValues[index++] = x;
                            tessValues[index++] = y;
                            tessValues[index] = z;
                        }
                    }
                }
            }

        }

        private void copyManyCoords(PGraphicsOpenGL.InGeometry in, int i0, int index, int nvert) {
            for(int i = 0; i < nvert; ++i) {
                int inIdx = i0 + i;
                int tessIdx = this.firstPolyVertex + i;
                PApplet.arrayCopy(in.vertices, 3 * inIdx, this.polyVertices, 4 * tessIdx, 3);
                this.polyVertices[4 * tessIdx + 3] = 1.0F;
                Iterator var8 = this.polyAttribs.keySet().iterator();

                while(var8.hasNext()) {
                    String name = (String)var8.next();
                    PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                    if (attrib.isPosition()) {
                        float[] inValues = (float[])in.fattribs.get(name);
                        float[] tessValues = (float[])this.fpolyAttribs.get(name);
                        PApplet.arrayCopy(inValues, 3 * inIdx, tessValues, 4 * tessIdx, 3);
                        tessValues[4 * tessIdx + 3] = 1.0F;
                    }
                }
            }

            PApplet.arrayCopy(in.normals, 3 * i0, this.polyNormals, 3 * this.firstPolyVertex, 3 * nvert);
            Iterator var13 = this.polyAttribs.keySet().iterator();

            while(var13.hasNext()) {
                String name = (String)var13.next();
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                if (attrib.isNormal()) {
                    float[] inValues = (float[])in.fattribs.get(name);
                    float[] tessValues = (float[])this.fpolyAttribs.get(name);
                    PApplet.arrayCopy(inValues, 3 * i0, tessValues, 3 * this.firstPolyVertex, 3 * nvert);
                }
            }

        }

        private void copyFewAttribs(PGraphicsOpenGL.InGeometry in, int i0, int index, int nvert) {
            label66:
            for(int i = 0; i < nvert; ++i) {
                int inIdx = i0 + i;
                int tessIdx = this.firstPolyVertex + i;
                index = 2 * inIdx;
                float u = in.texcoords[index++];
                float v = in.texcoords[index];
                this.polyColors[tessIdx] = in.colors[inIdx];
                index = 2 * tessIdx;
                this.polyTexCoords[index++] = u;
                this.polyTexCoords[index] = v;
                this.polyAmbient[tessIdx] = in.ambient[inIdx];
                this.polySpecular[tessIdx] = in.specular[inIdx];
                this.polyEmissive[tessIdx] = in.emissive[inIdx];
                this.polyShininess[tessIdx] = in.shininess[inIdx];
                Iterator var10 = this.polyAttribs.keySet().iterator();

                while(true) {
                    while(true) {
                        String name;
                        PGraphicsOpenGL.VertexAttribute attrib;
                        do {
                            do {
                                if (!var10.hasNext()) {
                                    continue label66;
                                }

                                name = (String)var10.next();
                                attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                            } while(attrib.isPosition());
                        } while(attrib.isNormal());

                        int index0 = attrib.size * inIdx;
                        int index1 = attrib.size * tessIdx;
                        int n;
                        if (attrib.isFloat()) {
                            float[] inValues = (float[])in.fattribs.get(name);
                            float[] tessValues = (float[])this.fpolyAttribs.get(name);

                            for(n = 0; n < attrib.size; ++n) {
                                tessValues[index1++] = inValues[index0++];
                            }
                        } else if (attrib.isInt()) {
                            int[] inValues = (int[])in.iattribs.get(name);
                            int[] tessValues = (int[])this.ipolyAttribs.get(name);

                            for(n = 0; n < attrib.size; ++n) {
                                tessValues[index1++] = inValues[index0++];
                            }
                        } else if (attrib.isBool()) {
                            byte[] inValues = (byte[])in.battribs.get(name);
                            byte[] tessValues = (byte[])this.bpolyAttribs.get(name);

                            for(n = 0; n < attrib.size; ++n) {
                                tessValues[index1++] = inValues[index0++];
                            }
                        }
                    }
                }
            }

        }

        private void copyManyAttribs(PGraphicsOpenGL.InGeometry in, int i0, int index, int nvert) {
            PApplet.arrayCopy(in.colors, i0, this.polyColors, this.firstPolyVertex, nvert);
            PApplet.arrayCopy(in.texcoords, 2 * i0, this.polyTexCoords, 2 * this.firstPolyVertex, 2 * nvert);
            PApplet.arrayCopy(in.ambient, i0, this.polyAmbient, this.firstPolyVertex, nvert);
            PApplet.arrayCopy(in.specular, i0, this.polySpecular, this.firstPolyVertex, nvert);
            PApplet.arrayCopy(in.emissive, i0, this.polyEmissive, this.firstPolyVertex, nvert);
            PApplet.arrayCopy(in.shininess, i0, this.polyShininess, this.firstPolyVertex, nvert);
            Iterator var5 = this.polyAttribs.keySet().iterator();

            while(var5.hasNext()) {
                String name = (String)var5.next();
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                if (!attrib.isPosition() && !attrib.isNormal()) {
                    Object inValues = null;
                    Object tessValues = null;
                    if (attrib.isFloat()) {
                        inValues = in.fattribs.get(name);
                        tessValues = this.fpolyAttribs.get(name);
                    } else if (attrib.isInt()) {
                        inValues = in.iattribs.get(name);
                        tessValues = this.ipolyAttribs.get(name);
                    } else if (attrib.isBool()) {
                        inValues = in.battribs.get(name);
                        tessValues = this.bpolyAttribs.get(name);
                    }

                    PApplet.arrayCopy(inValues, attrib.size * i0, tessValues, attrib.tessSize * this.firstPolyVertex, attrib.size * nvert);
                }
            }

        }

        void applyMatrixOnPolyGeometry(PMatrix tr, int first, int last) {
            if (tr instanceof PMatrix2D) {
                this.applyMatrixOnPolyGeometry((PMatrix2D)tr, first, last);
            } else if (tr instanceof PMatrix3D) {
                this.applyMatrixOnPolyGeometry((PMatrix3D)tr, first, last);
            }

        }

        void applyMatrixOnLineGeometry(PMatrix tr, int first, int last) {
            if (tr instanceof PMatrix2D) {
                this.applyMatrixOnLineGeometry((PMatrix2D)tr, first, last);
            } else if (tr instanceof PMatrix3D) {
                this.applyMatrixOnLineGeometry((PMatrix3D)tr, first, last);
            }

        }

        void applyMatrixOnPointGeometry(PMatrix tr, int first, int last) {
            if (tr instanceof PMatrix2D) {
                this.applyMatrixOnPointGeometry((PMatrix2D)tr, first, last);
            } else if (tr instanceof PMatrix3D) {
                this.applyMatrixOnPointGeometry((PMatrix3D)tr, first, last);
            }

        }

        void applyMatrixOnPolyGeometry(PMatrix2D tr, int first, int last) {
            if (first < last) {
                for(int i = first; i <= last; ++i) {
                    int index = 4 * i;
                    float x = this.polyVertices[index++];
                    float y = this.polyVertices[index];
                    index = 3 * i;
                    float nx = this.polyNormals[index++];
                    float ny = this.polyNormals[index];
                    index = 4 * i;
                    this.polyVertices[index++] = x * tr.m00 + y * tr.m01 + tr.m02;
                    this.polyVertices[index] = x * tr.m10 + y * tr.m11 + tr.m12;
                    index = 3 * i;
                    this.polyNormals[index++] = nx * tr.m00 + ny * tr.m01;
                    this.polyNormals[index] = nx * tr.m10 + ny * tr.m11;
                    Iterator var10 = this.polyAttribs.keySet().iterator();

                    while(var10.hasNext()) {
                        String name = (String)var10.next();
                        PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                        if (!attrib.isColor() && !attrib.isOther()) {
                            float[] values = (float[])this.fpolyAttribs.get(name);
                            if (attrib.isPosition()) {
                                index = 4 * i;
                                x = values[index++];
                                y = values[index];
                                index = 4 * i;
                                values[index++] = x * tr.m00 + y * tr.m01 + tr.m02;
                                values[index] = x * tr.m10 + y * tr.m11 + tr.m12;
                            } else {
                                index = 3 * i;
                                nx = values[index++];
                                ny = values[index];
                                index = 3 * i;
                                values[index++] = nx * tr.m00 + ny * tr.m01;
                                values[index] = nx * tr.m10 + ny * tr.m11;
                            }
                        }
                    }
                }
            }

        }

        void applyMatrixOnLineGeometry(PMatrix2D tr, int first, int last) {
            if (first < last) {
                float scaleFactor = PGraphicsOpenGL.matrixScale(tr);

                for(int i = first; i <= last; ++i) {
                    int index = 4 * i;
                    float x = this.lineVertices[index++];
                    float y = this.lineVertices[index];
                    index = 4 * i;
                    float dx = this.lineDirections[index++];
                    float dy = this.lineDirections[index];
                    index = 4 * i;
                    this.lineVertices[index++] = x * tr.m00 + y * tr.m01 + tr.m02;
                    this.lineVertices[index] = x * tr.m10 + y * tr.m11 + tr.m12;
                    index = 4 * i;
                    this.lineDirections[index++] = dx * tr.m00 + dy * tr.m01;
                    this.lineDirections[index] = dx * tr.m10 + dy * tr.m11;
                    float[] var10000 = this.lineDirections;
                    var10000[index + 2] *= scaleFactor;
                }
            }

        }

        void applyMatrixOnPointGeometry(PMatrix2D tr, int first, int last) {
            if (first < last) {
                float matrixScale = PGraphicsOpenGL.matrixScale(tr);

                for(int i = first; i <= last; ++i) {
                    int index = 4 * i;
                    float x = this.pointVertices[index++];
                    float y = this.pointVertices[index];
                    index = 4 * i;
                    this.pointVertices[index++] = x * tr.m00 + y * tr.m01 + tr.m02;
                    this.pointVertices[index] = x * tr.m10 + y * tr.m11 + tr.m12;
                    index = 2 * i;
                    float[] var10000 = this.pointOffsets;
                    int var10001 = index++;
                    var10000[var10001] *= matrixScale;
                    var10000 = this.pointOffsets;
                    var10000[index] *= matrixScale;
                }
            }

        }

        void applyMatrixOnPolyGeometry(PMatrix3D tr, int first, int last) {
            if (first < last) {
                for(int i = first; i <= last; ++i) {
                    int index = 4 * i;
                    float x = this.polyVertices[index++];
                    float y = this.polyVertices[index++];
                    float z = this.polyVertices[index++];
                    float w = this.polyVertices[index];
                    index = 3 * i;
                    float nx = this.polyNormals[index++];
                    float ny = this.polyNormals[index++];
                    float nz = this.polyNormals[index];
                    index = 4 * i;
                    this.polyVertices[index++] = x * tr.m00 + y * tr.m01 + z * tr.m02 + w * tr.m03;
                    this.polyVertices[index++] = x * tr.m10 + y * tr.m11 + z * tr.m12 + w * tr.m13;
                    this.polyVertices[index++] = x * tr.m20 + y * tr.m21 + z * tr.m22 + w * tr.m23;
                    this.polyVertices[index] = x * tr.m30 + y * tr.m31 + z * tr.m32 + w * tr.m33;
                    index = 3 * i;
                    this.polyNormals[index++] = nx * tr.m00 + ny * tr.m01 + nz * tr.m02;
                    this.polyNormals[index++] = nx * tr.m10 + ny * tr.m11 + nz * tr.m12;
                    this.polyNormals[index] = nx * tr.m20 + ny * tr.m21 + nz * tr.m22;
                    Iterator var13 = this.polyAttribs.keySet().iterator();

                    while(var13.hasNext()) {
                        String name = (String)var13.next();
                        PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.polyAttribs.get(name);
                        if (!attrib.isColor() && !attrib.isOther()) {
                            float[] values = (float[])this.fpolyAttribs.get(name);
                            if (attrib.isPosition()) {
                                index = 4 * i;
                                x = values[index++];
                                y = values[index++];
                                z = values[index++];
                                w = values[index];
                                index = 4 * i;
                                values[index++] = x * tr.m00 + y * tr.m01 + z * tr.m02 + w * tr.m03;
                                values[index++] = x * tr.m10 + y * tr.m11 + z * tr.m12 + w * tr.m13;
                                values[index++] = x * tr.m20 + y * tr.m21 + z * tr.m22 + w * tr.m23;
                                values[index] = x * tr.m30 + y * tr.m31 + z * tr.m32 + w * tr.m33;
                            } else {
                                index = 3 * i;
                                nx = values[index++];
                                ny = values[index++];
                                nz = values[index];
                                index = 3 * i;
                                values[index++] = nx * tr.m00 + ny * tr.m01 + nz * tr.m02;
                                values[index++] = nx * tr.m10 + ny * tr.m11 + nz * tr.m12;
                                values[index] = nx * tr.m20 + ny * tr.m21 + nz * tr.m22;
                            }
                        }
                    }
                }
            }

        }

        void applyMatrixOnLineGeometry(PMatrix3D tr, int first, int last) {
            if (first < last) {
                float scaleFactor = PGraphicsOpenGL.matrixScale(tr);

                for(int i = first; i <= last; ++i) {
                    int index = 4 * i;
                    float x = this.lineVertices[index++];
                    float y = this.lineVertices[index++];
                    float z = this.lineVertices[index++];
                    float w = this.lineVertices[index];
                    index = 4 * i;
                    float dx = this.lineDirections[index++];
                    float dy = this.lineDirections[index++];
                    float dz = this.lineDirections[index];
                    index = 4 * i;
                    this.lineVertices[index++] = x * tr.m00 + y * tr.m01 + z * tr.m02 + w * tr.m03;
                    this.lineVertices[index++] = x * tr.m10 + y * tr.m11 + z * tr.m12 + w * tr.m13;
                    this.lineVertices[index++] = x * tr.m20 + y * tr.m21 + z * tr.m22 + w * tr.m23;
                    this.lineVertices[index] = x * tr.m30 + y * tr.m31 + z * tr.m32 + w * tr.m33;
                    index = 4 * i;
                    this.lineDirections[index++] = dx * tr.m00 + dy * tr.m01 + dz * tr.m02;
                    this.lineDirections[index++] = dx * tr.m10 + dy * tr.m11 + dz * tr.m12;
                    this.lineDirections[index++] = dx * tr.m20 + dy * tr.m21 + dz * tr.m22;
                    float[] var10000 = this.lineDirections;
                    var10000[index] *= scaleFactor;
                }
            }

        }

        void applyMatrixOnPointGeometry(PMatrix3D tr, int first, int last) {
            if (first < last) {
                float matrixScale = PGraphicsOpenGL.matrixScale(tr);

                for(int i = first; i <= last; ++i) {
                    int index = 4 * i;
                    float x = this.pointVertices[index++];
                    float y = this.pointVertices[index++];
                    float z = this.pointVertices[index++];
                    float w = this.pointVertices[index];
                    index = 4 * i;
                    this.pointVertices[index++] = x * tr.m00 + y * tr.m01 + z * tr.m02 + w * tr.m03;
                    this.pointVertices[index++] = x * tr.m10 + y * tr.m11 + z * tr.m12 + w * tr.m13;
                    this.pointVertices[index++] = x * tr.m20 + y * tr.m21 + z * tr.m22 + w * tr.m23;
                    this.pointVertices[index] = x * tr.m30 + y * tr.m31 + z * tr.m32 + w * tr.m33;
                    index = 2 * i;
                    float[] var10000 = this.pointOffsets;
                    int var10001 = index++;
                    var10000[var10001] *= matrixScale;
                    var10000 = this.pointOffsets;
                    var10000[index] *= matrixScale;
                }
            }

        }
    }

    protected static class InGeometry {
        PGraphicsOpenGL pg;
        int renderMode;
        PGraphicsOpenGL.AttributeMap attribs;
        int vertexCount;
        int codeCount;
        int edgeCount;
        float[] vertices;
        int[] colors;
        float[] normals;
        float[] texcoords;
        int[] strokeColors;
        float[] strokeWeights;
        int[] codes;
        int[][] edges;
        int[] ambient;
        int[] specular;
        int[] emissive;
        float[] shininess;
        HashMap<String, float[]> fattribs;
        HashMap<String, int[]> iattribs;
        HashMap<String, byte[]> battribs;
        int fillColor;
        int strokeColor;
        float strokeWeight;
        int ambientColor;
        int specularColor;
        int emissiveColor;
        float shininessFactor;
        float normalX;
        float normalY;
        float normalZ;

        InGeometry(PGraphicsOpenGL pg, PGraphicsOpenGL.AttributeMap attr, int mode) {
            this.pg = pg;
            this.attribs = attr;
            this.renderMode = mode;
            this.allocate();
        }

        void clear() {
            this.vertexCount = 0;
            this.codeCount = 0;
            this.edgeCount = 0;
        }

        void clearEdges() {
            this.edgeCount = 0;
        }

        void allocate() {
            this.vertices = new float[3 * PGL.DEFAULT_IN_VERTICES];
            this.colors = new int[PGL.DEFAULT_IN_VERTICES];
            this.normals = new float[3 * PGL.DEFAULT_IN_VERTICES];
            this.texcoords = new float[2 * PGL.DEFAULT_IN_VERTICES];
            this.strokeColors = new int[PGL.DEFAULT_IN_VERTICES];
            this.strokeWeights = new float[PGL.DEFAULT_IN_VERTICES];
            this.ambient = new int[PGL.DEFAULT_IN_VERTICES];
            this.specular = new int[PGL.DEFAULT_IN_VERTICES];
            this.emissive = new int[PGL.DEFAULT_IN_VERTICES];
            this.shininess = new float[PGL.DEFAULT_IN_VERTICES];
            this.edges = new int[PGL.DEFAULT_IN_EDGES][3];
            this.fattribs = new HashMap();
            this.iattribs = new HashMap();
            this.battribs = new HashMap();
            this.clear();
        }

        void initAttrib(PGraphicsOpenGL.VertexAttribute attrib) {
            if (attrib.type == PGL.FLOAT) {
                float[] temp = new float[attrib.size * PGL.DEFAULT_IN_VERTICES];
                this.fattribs.put(attrib.name, temp);
            } else if (attrib.type == PGL.INT) {
                int[] temp = new int[attrib.size * PGL.DEFAULT_IN_VERTICES];
                this.iattribs.put(attrib.name, temp);
            } else if (attrib.type == PGL.BOOL) {
                byte[] temp = new byte[attrib.size * PGL.DEFAULT_IN_VERTICES];
                this.battribs.put(attrib.name, temp);
            }

        }

        void vertexCheck() {
            if (this.vertexCount == this.vertices.length / 3) {
                int newSize = this.vertexCount << 1;
                this.expandVertices(newSize);
                this.expandColors(newSize);
                this.expandNormals(newSize);
                this.expandTexCoords(newSize);
                this.expandStrokeColors(newSize);
                this.expandStrokeWeights(newSize);
                this.expandAmbient(newSize);
                this.expandSpecular(newSize);
                this.expandEmissive(newSize);
                this.expandShininess(newSize);
                this.expandAttribs(newSize);
            }

        }

        void codeCheck() {
            if (this.codeCount == this.codes.length) {
                int newLen = this.codeCount << 1;
                this.expandCodes(newLen);
            }

        }

        void edgeCheck() {
            if (this.edgeCount == this.edges.length) {
                int newLen = this.edgeCount << 1;
                this.expandEdges(newLen);
            }

        }

        float getVertexX(int idx) {
            return this.vertices[3 * idx + 0];
        }

        float getVertexY(int idx) {
            return this.vertices[3 * idx + 1];
        }

        float getVertexZ(int idx) {
            return this.vertices[3 * idx + 2];
        }

        float getLastVertexX() {
            return this.vertices[3 * (this.vertexCount - 1) + 0];
        }

        float getLastVertexY() {
            return this.vertices[3 * (this.vertexCount - 1) + 1];
        }

        float getLastVertexZ() {
            return this.vertices[3 * (this.vertexCount - 1) + 2];
        }

        int getNumEdgeClosures() {
            int count = 0;

            for(int i = 0; i < this.edgeCount; ++i) {
                if (this.edges[i][2] == -1) {
                    ++count;
                }
            }

            return count;
        }

        int getNumEdgeVertices(boolean bevel) {
            int segVert = this.edgeCount;
            int bevVert = 0;
            if (bevel) {
                for(int i = 0; i < this.edgeCount; ++i) {
                    int[] edge = this.edges[i];
                    if (edge[2] == 0 || edge[2] == 1) {
                        bevVert += 3;
                    }

                    if (edge[2] == -1) {
                        bevVert += 5;
                        --segVert;
                    }
                }
            } else {
                segVert -= this.getNumEdgeClosures();
            }

            return 4 * segVert + bevVert;
        }

        int getNumEdgeIndices(boolean bevel) {
            int segInd = this.edgeCount;
            int bevInd = 0;
            if (bevel) {
                for(int i = 0; i < this.edgeCount; ++i) {
                    int[] edge = this.edges[i];
                    if (edge[2] == 0 || edge[2] == 1) {
                        ++bevInd;
                    }

                    if (edge[2] == -1) {
                        ++bevInd;
                        --segInd;
                    }
                }
            } else {
                segInd -= this.getNumEdgeClosures();
            }

            return 6 * (segInd + bevInd);
        }

        void getVertexMin(PVector v) {
            for(int i = 0; i < this.vertexCount; ++i) {
                int index = 4 * i;
                v.x = PApplet.min(v.x, this.vertices[index++]);
                v.y = PApplet.min(v.y, this.vertices[index++]);
                v.z = PApplet.min(v.z, this.vertices[index]);
            }

        }

        void getVertexMax(PVector v) {
            for(int i = 0; i < this.vertexCount; ++i) {
                int index = 4 * i;
                v.x = PApplet.max(v.x, this.vertices[index++]);
                v.y = PApplet.max(v.y, this.vertices[index++]);
                v.z = PApplet.max(v.z, this.vertices[index]);
            }

        }

        int getVertexSum(PVector v) {
            for(int i = 0; i < this.vertexCount; ++i) {
                int index = 4 * i;
                v.x += this.vertices[index++];
                v.y += this.vertices[index++];
                v.z += this.vertices[index];
            }

            return this.vertexCount;
        }

        double[] getAttribVector(int idx) {
            double[] vector = new double[this.attribs.numComp];
            int vidx = 0;

            for(int i = 0; i < this.attribs.size(); ++i) {
                PGraphicsOpenGL.VertexAttribute attrib = this.attribs.get(i);
                String name = attrib.name;
                int aidx = attrib.size * idx;
                int n;
                int[] iarray;
                if (attrib.isColor()) {
                    iarray = (int[])this.iattribs.get(name);
                    n = iarray[aidx];
                    vector[vidx++] = (double)(n >> 24 & 255);
                    vector[vidx++] = (double)(n >> 16 & 255);
                    vector[vidx++] = (double)(n >> 8 & 255);
                    vector[vidx++] = (double)(n >> 0 & 255);
                } else if (attrib.isFloat()) {
                    float[] farray = (float[])this.fattribs.get(name);

                    for(n = 0; n < attrib.size; ++n) {
                        vector[vidx++] = (double)farray[aidx++];
                    }
                } else if (attrib.isInt()) {
                    iarray = (int[])this.iattribs.get(name);

                    for(n = 0; n < attrib.size; ++n) {
                        vector[vidx++] = (double)iarray[aidx++];
                    }
                } else if (attrib.isBool()) {
                    byte[] barray = (byte[])this.battribs.get(name);

                    for(n = 0; n < attrib.size; ++n) {
                        vector[vidx++] = (double)barray[aidx++];
                    }
                }
            }

            return vector;
        }

        void expandVertices(int n) {
            float[] temp = new float[3 * n];
            PApplet.arrayCopy(this.vertices, 0, temp, 0, 3 * this.vertexCount);
            this.vertices = temp;
        }

        void expandColors(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.colors, 0, temp, 0, this.vertexCount);
            this.colors = temp;
        }

        void expandNormals(int n) {
            float[] temp = new float[3 * n];
            PApplet.arrayCopy(this.normals, 0, temp, 0, 3 * this.vertexCount);
            this.normals = temp;
        }

        void expandTexCoords(int n) {
            float[] temp = new float[2 * n];
            PApplet.arrayCopy(this.texcoords, 0, temp, 0, 2 * this.vertexCount);
            this.texcoords = temp;
        }

        void expandStrokeColors(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.strokeColors, 0, temp, 0, this.vertexCount);
            this.strokeColors = temp;
        }

        void expandStrokeWeights(int n) {
            float[] temp = new float[n];
            PApplet.arrayCopy(this.strokeWeights, 0, temp, 0, this.vertexCount);
            this.strokeWeights = temp;
        }

        void expandAmbient(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.ambient, 0, temp, 0, this.vertexCount);
            this.ambient = temp;
        }

        void expandSpecular(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.specular, 0, temp, 0, this.vertexCount);
            this.specular = temp;
        }

        void expandEmissive(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.emissive, 0, temp, 0, this.vertexCount);
            this.emissive = temp;
        }

        void expandShininess(int n) {
            float[] temp = new float[n];
            PApplet.arrayCopy(this.shininess, 0, temp, 0, this.vertexCount);
            this.shininess = temp;
        }

        void expandAttribs(int n) {
            Iterator var2 = this.attribs.keySet().iterator();

            while(var2.hasNext()) {
                String name = (String)var2.next();
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.attribs.get(name);
                if (attrib.type == PGL.FLOAT) {
                    this.expandFloatAttrib(attrib, n);
                } else if (attrib.type == PGL.INT) {
                    this.expandIntAttrib(attrib, n);
                } else if (attrib.type == PGL.BOOL) {
                    this.expandBoolAttrib(attrib, n);
                }
            }

        }

        void expandFloatAttrib(PGraphicsOpenGL.VertexAttribute attrib, int n) {
            float[] values = (float[])this.fattribs.get(attrib.name);
            float[] temp = new float[attrib.size * n];
            PApplet.arrayCopy(values, 0, temp, 0, attrib.size * this.vertexCount);
            this.fattribs.put(attrib.name, temp);
        }

        void expandIntAttrib(PGraphicsOpenGL.VertexAttribute attrib, int n) {
            int[] values = (int[])this.iattribs.get(attrib.name);
            int[] temp = new int[attrib.size * n];
            PApplet.arrayCopy(values, 0, temp, 0, attrib.size * this.vertexCount);
            this.iattribs.put(attrib.name, temp);
        }

        void expandBoolAttrib(PGraphicsOpenGL.VertexAttribute attrib, int n) {
            byte[] values = (byte[])this.battribs.get(attrib.name);
            byte[] temp = new byte[attrib.size * n];
            PApplet.arrayCopy(values, 0, temp, 0, attrib.size * this.vertexCount);
            this.battribs.put(attrib.name, temp);
        }

        void expandCodes(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.codes, 0, temp, 0, this.codeCount);
            this.codes = temp;
        }

        void expandEdges(int n) {
            int[][] temp = new int[n][3];
            PApplet.arrayCopy(this.edges, 0, temp, 0, this.edgeCount);
            this.edges = temp;
        }

        void trim() {
            if (0 < this.vertexCount && this.vertexCount < this.vertices.length / 3) {
                this.trimVertices();
                this.trimColors();
                this.trimNormals();
                this.trimTexCoords();
                this.trimStrokeColors();
                this.trimStrokeWeights();
                this.trimAmbient();
                this.trimSpecular();
                this.trimEmissive();
                this.trimShininess();
                this.trimAttribs();
            }

            if (0 < this.codeCount && this.codeCount < this.codes.length) {
                this.trimCodes();
            }

            if (0 < this.edgeCount && this.edgeCount < this.edges.length) {
                this.trimEdges();
            }

        }

        void trimVertices() {
            float[] temp = new float[3 * this.vertexCount];
            PApplet.arrayCopy(this.vertices, 0, temp, 0, 3 * this.vertexCount);
            this.vertices = temp;
        }

        void trimColors() {
            int[] temp = new int[this.vertexCount];
            PApplet.arrayCopy(this.colors, 0, temp, 0, this.vertexCount);
            this.colors = temp;
        }

        void trimNormals() {
            float[] temp = new float[3 * this.vertexCount];
            PApplet.arrayCopy(this.normals, 0, temp, 0, 3 * this.vertexCount);
            this.normals = temp;
        }

        void trimTexCoords() {
            float[] temp = new float[2 * this.vertexCount];
            PApplet.arrayCopy(this.texcoords, 0, temp, 0, 2 * this.vertexCount);
            this.texcoords = temp;
        }

        void trimStrokeColors() {
            int[] temp = new int[this.vertexCount];
            PApplet.arrayCopy(this.strokeColors, 0, temp, 0, this.vertexCount);
            this.strokeColors = temp;
        }

        void trimStrokeWeights() {
            float[] temp = new float[this.vertexCount];
            PApplet.arrayCopy(this.strokeWeights, 0, temp, 0, this.vertexCount);
            this.strokeWeights = temp;
        }

        void trimAmbient() {
            int[] temp = new int[this.vertexCount];
            PApplet.arrayCopy(this.ambient, 0, temp, 0, this.vertexCount);
            this.ambient = temp;
        }

        void trimSpecular() {
            int[] temp = new int[this.vertexCount];
            PApplet.arrayCopy(this.specular, 0, temp, 0, this.vertexCount);
            this.specular = temp;
        }

        void trimEmissive() {
            int[] temp = new int[this.vertexCount];
            PApplet.arrayCopy(this.emissive, 0, temp, 0, this.vertexCount);
            this.emissive = temp;
        }

        void trimShininess() {
            float[] temp = new float[this.vertexCount];
            PApplet.arrayCopy(this.shininess, 0, temp, 0, this.vertexCount);
            this.shininess = temp;
        }

        void trimCodes() {
            int[] temp = new int[this.codeCount];
            PApplet.arrayCopy(this.codes, 0, temp, 0, this.codeCount);
            this.codes = temp;
        }

        void trimEdges() {
            int[][] temp = new int[this.edgeCount][3];
            PApplet.arrayCopy(this.edges, 0, temp, 0, this.edgeCount);
            this.edges = temp;
        }

        void trimAttribs() {
            Iterator var1 = this.attribs.keySet().iterator();

            while(var1.hasNext()) {
                String name = (String)var1.next();
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.attribs.get(name);
                if (attrib.type == PGL.FLOAT) {
                    this.trimFloatAttrib(attrib);
                } else if (attrib.type == PGL.INT) {
                    this.trimIntAttrib(attrib);
                } else if (attrib.type == PGL.BOOL) {
                    this.trimBoolAttrib(attrib);
                }
            }

        }

        void trimFloatAttrib(PGraphicsOpenGL.VertexAttribute attrib) {
            float[] values = (float[])this.fattribs.get(attrib.name);
            float[] temp = new float[attrib.size * this.vertexCount];
            PApplet.arrayCopy(values, 0, temp, 0, attrib.size * this.vertexCount);
            this.fattribs.put(attrib.name, temp);
        }

        void trimIntAttrib(PGraphicsOpenGL.VertexAttribute attrib) {
            int[] values = (int[])this.iattribs.get(attrib.name);
            int[] temp = new int[attrib.size * this.vertexCount];
            PApplet.arrayCopy(values, 0, temp, 0, attrib.size * this.vertexCount);
            this.iattribs.put(attrib.name, temp);
        }

        void trimBoolAttrib(PGraphicsOpenGL.VertexAttribute attrib) {
            byte[] values = (byte[])this.battribs.get(attrib.name);
            byte[] temp = new byte[attrib.size * this.vertexCount];
            PApplet.arrayCopy(values, 0, temp, 0, attrib.size * this.vertexCount);
            this.battribs.put(attrib.name, temp);
        }

        int addVertex(float x, float y, boolean brk) {
            return this.addVertex(x, y, 0.0F, this.fillColor, this.normalX, this.normalY, this.normalZ, 0.0F, 0.0F, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, 0, brk);
        }

        int addVertex(float x, float y, int code, boolean brk) {
            return this.addVertex(x, y, 0.0F, this.fillColor, this.normalX, this.normalY, this.normalZ, 0.0F, 0.0F, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, code, brk);
        }

        int addVertex(float x, float y, float u, float v, boolean brk) {
            return this.addVertex(x, y, 0.0F, this.fillColor, this.normalX, this.normalY, this.normalZ, u, v, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, 0, brk);
        }

        int addVertex(float x, float y, float u, float v, int code, boolean brk) {
            return this.addVertex(x, y, 0.0F, this.fillColor, this.normalX, this.normalY, this.normalZ, u, v, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, code, brk);
        }

        int addVertex(float x, float y, float z, boolean brk) {
            return this.addVertex(x, y, z, this.fillColor, this.normalX, this.normalY, this.normalZ, 0.0F, 0.0F, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, 0, brk);
        }

        int addVertex(float x, float y, float z, int code, boolean brk) {
            return this.addVertex(x, y, z, this.fillColor, this.normalX, this.normalY, this.normalZ, 0.0F, 0.0F, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, code, brk);
        }

        int addVertex(float x, float y, float z, float u, float v, boolean brk) {
            return this.addVertex(x, y, z, this.fillColor, this.normalX, this.normalY, this.normalZ, u, v, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, 0, brk);
        }

        int addVertex(float x, float y, float z, float u, float v, int code, boolean brk) {
            return this.addVertex(x, y, z, this.fillColor, this.normalX, this.normalY, this.normalZ, u, v, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininessFactor, code, brk);
        }

        int addVertex(float x, float y, float z, int fcolor, float nx, float ny, float nz, float u, float v, int scolor, float sweight, int am, int sp, int em, float shine, int code, boolean brk) {
            this.vertexCheck();
            int index = 3 * this.vertexCount;
            this.vertices[index++] = x;
            this.vertices[index++] = y;
            this.vertices[index] = z;
            this.colors[this.vertexCount] = PGL.javaToNativeARGB(fcolor);
            index = 3 * this.vertexCount;
            this.normals[index++] = nx;
            this.normals[index++] = ny;
            this.normals[index] = nz;
            index = 2 * this.vertexCount;
            this.texcoords[index++] = u;
            this.texcoords[index] = v;
            this.strokeColors[this.vertexCount] = PGL.javaToNativeARGB(scolor);
            this.strokeWeights[this.vertexCount] = sweight;
            this.ambient[this.vertexCount] = PGL.javaToNativeARGB(am);
            this.specular[this.vertexCount] = PGL.javaToNativeARGB(sp);
            this.emissive[this.vertexCount] = PGL.javaToNativeARGB(em);
            this.shininess[this.vertexCount] = shine;
            Iterator var19 = this.attribs.keySet().iterator();

            while(var19.hasNext()) {
                String name = (String)var19.next();
                PGraphicsOpenGL.VertexAttribute attrib = (PGraphicsOpenGL.VertexAttribute)this.attribs.get(name);
                index = attrib.size * this.vertexCount;
                if (attrib.type == PGL.FLOAT) {
                    float[] values = (float[])this.fattribs.get(name);
                    attrib.add(values, index);
                } else if (attrib.type == PGL.INT) {
                    int[] values = (int[])this.iattribs.get(name);
                    attrib.add(values, index);
                } else if (attrib.type == PGL.BOOL) {
                    byte[] values = (byte[])this.battribs.get(name);
                    attrib.add(values, index);
                }
            }

            if (brk || code == 0 && this.codes != null || code == 1 || code == 2 || code == 3) {
                if (this.codes == null) {
                    this.codes = new int[PApplet.max(PGL.DEFAULT_IN_VERTICES, this.vertexCount)];
                    Arrays.fill(this.codes, 0, this.vertexCount, 0);
                    this.codeCount = this.vertexCount;
                }

                if (brk) {
                    this.codeCheck();
                    this.codes[this.codeCount] = 4;
                    ++this.codeCount;
                }

                if (code != -1) {
                    this.codeCheck();
                    this.codes[this.codeCount] = code;
                    ++this.codeCount;
                }
            }

            ++this.vertexCount;
            return this.vertexCount - 1;
        }

        public void addBezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, boolean brk) {
            this.addVertex(x2, y2, z2, 1, brk);
            this.addVertex(x3, y3, z3, -1, false);
            this.addVertex(x4, y4, z4, -1, false);
        }

        public void addQuadraticVertex(float cx, float cy, float cz, float x3, float y3, float z3, boolean brk) {
            this.addVertex(cx, cy, cz, 2, brk);
            this.addVertex(x3, y3, z3, -1, false);
        }

        public void addCurveVertex(float x, float y, float z, boolean brk) {
            this.addVertex(x, y, z, 3, brk);
        }

        float[][] getVertexData() {
            float[][] data = new float[this.vertexCount][37];

            for(int i = 0; i < this.vertexCount; ++i) {
                float[] vert = data[i];
                vert[0] = this.vertices[3 * i + 0];
                vert[1] = this.vertices[3 * i + 1];
                vert[2] = this.vertices[3 * i + 2];
                vert[3] = (float)(this.colors[i] >> 16 & 255) / 255.0F;
                vert[4] = (float)(this.colors[i] >> 8 & 255) / 255.0F;
                vert[5] = (float)(this.colors[i] >> 0 & 255) / 255.0F;
                vert[6] = (float)(this.colors[i] >> 24 & 255) / 255.0F;
                vert[7] = this.texcoords[2 * i + 0];
                vert[8] = this.texcoords[2 * i + 1];
                vert[9] = this.normals[3 * i + 0];
                vert[10] = this.normals[3 * i + 1];
                vert[11] = this.normals[3 * i + 2];
                vert[13] = (float)(this.strokeColors[i] >> 16 & 255) / 255.0F;
                vert[14] = (float)(this.strokeColors[i] >> 8 & 255) / 255.0F;
                vert[15] = (float)(this.strokeColors[i] >> 0 & 255) / 255.0F;
                vert[16] = (float)(this.strokeColors[i] >> 24 & 255) / 255.0F;
                vert[17] = this.strokeWeights[i];
            }

            return data;
        }

        boolean hasBezierVertex() {
            for(int i = 0; i < this.codeCount; ++i) {
                if (this.codes[i] == 1) {
                    return true;
                }
            }

            return false;
        }

        boolean hasQuadraticVertex() {
            for(int i = 0; i < this.codeCount; ++i) {
                if (this.codes[i] == 2) {
                    return true;
                }
            }

            return false;
        }

        boolean hasCurveVertex() {
            for(int i = 0; i < this.codeCount; ++i) {
                if (this.codes[i] == 3) {
                    return true;
                }
            }

            return false;
        }

        int addEdge(int i, int j, boolean start, boolean end) {
            this.edgeCheck();
            int[] edge = this.edges[this.edgeCount];
            edge[0] = i;
            edge[1] = j;
            edge[2] = (start ? 1 : 0) + 2 * (end ? 1 : 0);
            ++this.edgeCount;
            return this.edgeCount - 1;
        }

        int closeEdge(int i, int j) {
            this.edgeCheck();
            int[] edge = this.edges[this.edgeCount];
            edge[0] = i;
            edge[1] = j;
            edge[2] = -1;
            ++this.edgeCount;
            return this.edgeCount - 1;
        }

        void addTrianglesEdges() {
            for(int i = 0; i < this.vertexCount / 3; ++i) {
                int i0 = 3 * i + 0;
                int i1 = 3 * i + 1;
                int i2 = 3 * i + 2;
                this.addEdge(i0, i1, true, false);
                this.addEdge(i1, i2, false, false);
                this.addEdge(i2, i0, false, false);
                this.closeEdge(i2, i0);
            }

        }

        void addTriangleFanEdges() {
            for(int i = 1; i < this.vertexCount - 1; ++i) {
                int i0 = 0;
                int i2 = i + 1;
                this.addEdge(i0, i, true, false);
                this.addEdge(i, i2, false, false);
                this.addEdge(i2, i0, false, false);
                this.closeEdge(i2, i0);
            }

        }

        void addTriangleStripEdges() {
            for(int i = 1; i < this.vertexCount - 1; ++i) {
                int i1;
                int i2;
                if (i % 2 == 0) {
                    i1 = i - 1;
                    i2 = i + 1;
                } else {
                    i1 = i + 1;
                    i2 = i - 1;
                }

                this.addEdge(i, i1, true, false);
                this.addEdge(i1, i2, false, false);
                this.addEdge(i2, i, false, false);
                this.closeEdge(i2, i);
            }

        }

        void addQuadsEdges() {
            for(int i = 0; i < this.vertexCount / 4; ++i) {
                int i0 = 4 * i + 0;
                int i1 = 4 * i + 1;
                int i2 = 4 * i + 2;
                int i3 = 4 * i + 3;
                this.addEdge(i0, i1, true, false);
                this.addEdge(i1, i2, false, false);
                this.addEdge(i2, i3, false, false);
                this.addEdge(i3, i0, false, false);
                this.closeEdge(i3, i0);
            }

        }

        void addQuadStripEdges() {
            for(int qd = 1; qd < this.vertexCount / 2; ++qd) {
                int i0 = 2 * (qd - 1);
                int i1 = 2 * (qd - 1) + 1;
                int i2 = 2 * qd + 1;
                int i3 = 2 * qd;
                this.addEdge(i0, i1, true, false);
                this.addEdge(i1, i2, false, false);
                this.addEdge(i2, i3, false, false);
                this.addEdge(i3, i0, false, false);
                this.closeEdge(i3, i0);
            }

        }

        void calcTriangleNormal(int i0, int i1, int i2) {
            int index = 3 * i0;
            float x0 = this.vertices[index++];
            float y0 = this.vertices[index++];
            float z0 = this.vertices[index];
            index = 3 * i1;
            float x1 = this.vertices[index++];
            float y1 = this.vertices[index++];
            float z1 = this.vertices[index];
            index = 3 * i2;
            float x2 = this.vertices[index++];
            float y2 = this.vertices[index++];
            float z2 = this.vertices[index];
            float v12x = x2 - x1;
            float v12y = y2 - y1;
            float v12z = z2 - z1;
            float v10x = x0 - x1;
            float v10y = y0 - y1;
            float v10z = z0 - z1;
            float nx = v12y * v10z - v10y * v12z;
            float ny = v12z * v10x - v10z * v12x;
            float nz = v12x * v10y - v10x * v12y;
            float d = PApplet.sqrt(nx * nx + ny * ny + nz * nz);
            nx /= d;
            ny /= d;
            nz /= d;
            index = 3 * i0;
            this.normals[index++] = nx;
            this.normals[index++] = ny;
            this.normals[index] = nz;
            index = 3 * i1;
            this.normals[index++] = nx;
            this.normals[index++] = ny;
            this.normals[index] = nz;
            index = 3 * i2;
            this.normals[index++] = nx;
            this.normals[index++] = ny;
            this.normals[index] = nz;
        }

        void calcTrianglesNormals() {
            for(int i = 0; i < this.vertexCount / 3; ++i) {
                int i0 = 3 * i + 0;
                int i1 = 3 * i + 1;
                int i2 = 3 * i + 2;
                this.calcTriangleNormal(i0, i1, i2);
            }

        }

        void calcTriangleFanNormals() {
            for(int i = 1; i < this.vertexCount - 1; ++i) {
                int i0 = 0;
                int i2 = i + 1;
                this.calcTriangleNormal(i0, i, i2);
            }

        }

        void calcTriangleStripNormals() {
            for(int i = 1; i < this.vertexCount - 1; ++i) {
                int i0;
                int i2;
                if (i % 2 == 1) {
                    i0 = i - 1;
                    i2 = i + 1;
                } else {
                    i0 = i + 1;
                    i2 = i - 1;
                }

                this.calcTriangleNormal(i0, i, i2);
            }

        }

        void calcQuadsNormals() {
            for(int i = 0; i < this.vertexCount / 4; ++i) {
                int i0 = 4 * i + 0;
                int i1 = 4 * i + 1;
                int i2 = 4 * i + 2;
                int i3 = 4 * i + 3;
                this.calcTriangleNormal(i0, i1, i2);
                this.calcTriangleNormal(i2, i3, i0);
            }

        }

        void calcQuadStripNormals() {
            for(int qd = 1; qd < this.vertexCount / 2; ++qd) {
                int i0 = 2 * (qd - 1);
                int i1 = 2 * (qd - 1) + 1;
                int i2 = 2 * qd;
                int i3 = 2 * qd + 1;
                this.calcTriangleNormal(i0, i1, i2);
                this.calcTriangleNormal(i2, i1, i3);
            }

        }

        void setMaterial(int fillColor, int strokeColor, float strokeWeight, int ambientColor, int specularColor, int emissiveColor, float shininessFactor) {
            this.fillColor = fillColor;
            this.strokeColor = strokeColor;
            this.strokeWeight = strokeWeight;
            this.ambientColor = ambientColor;
            this.specularColor = specularColor;
            this.emissiveColor = emissiveColor;
            this.shininessFactor = shininessFactor;
        }

        void setNormal(float normalX, float normalY, float normalZ) {
            this.normalX = normalX;
            this.normalY = normalY;
            this.normalZ = normalZ;
        }

        void addPoint(float x, float y, float z, boolean fill, boolean stroke) {
            this.addVertex(x, y, z, 0, true);
        }

        void addLine(float x1, float y1, float z1, float x2, float y2, float z2, boolean fill, boolean stroke) {
            int idx1 = this.addVertex(x1, y1, z1, 0, true);
            int idx2 = this.addVertex(x2, y2, z2, 0, false);
            if (stroke) {
                this.addEdge(idx1, idx2, true, true);
            }

        }

        void addTriangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, boolean fill, boolean stroke) {
            int idx1 = this.addVertex(x1, y1, z1, 0, true);
            int idx2 = this.addVertex(x2, y2, z2, 0, false);
            int idx3 = this.addVertex(x3, y3, z3, 0, false);
            if (stroke) {
                this.addEdge(idx1, idx2, true, false);
                this.addEdge(idx2, idx3, false, false);
                this.addEdge(idx3, idx1, false, false);
                this.closeEdge(idx3, idx1);
            }

        }

        void addQuad(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, boolean stroke) {
            int idx1 = this.addVertex(x1, y1, z1, 0.0F, 0.0F, 0, true);
            int idx2 = this.addVertex(x2, y2, z2, 1.0F, 0.0F, 0, false);
            int idx3 = this.addVertex(x3, y3, z3, 1.0F, 1.0F, 0, false);
            int idx4 = this.addVertex(x4, y4, z4, 0.0F, 1.0F, 0, false);
            if (stroke) {
                this.addEdge(idx1, idx2, true, false);
                this.addEdge(idx2, idx3, false, false);
                this.addEdge(idx3, idx4, false, false);
                this.addEdge(idx4, idx1, false, false);
                this.closeEdge(idx4, idx1);
            }

        }

        void addRect(float a, float b, float c, float d, boolean stroke) {
            this.addQuad(a, b, 0.0F, c, b, 0.0F, c, d, 0.0F, a, d, 0.0F, stroke);
        }

        void addRect(float a, float b, float c, float d, float tl, float tr, float br, float bl, boolean stroke) {
            if (PGraphicsOpenGL.nonZero(tr)) {
                this.addVertex(c - tr, b, 0, true);
                this.addQuadraticVertex(c, b, 0.0F, c, b + tr, 0.0F, false);
            } else {
                this.addVertex(c, b, 0, true);
            }

            if (PGraphicsOpenGL.nonZero(br)) {
                this.addVertex(c, d - br, 0, false);
                this.addQuadraticVertex(c, d, 0.0F, c - br, d, 0.0F, false);
            } else {
                this.addVertex(c, d, 0, false);
            }

            if (PGraphicsOpenGL.nonZero(bl)) {
                this.addVertex(a + bl, d, 0, false);
                this.addQuadraticVertex(a, d, 0.0F, a, d - bl, 0.0F, false);
            } else {
                this.addVertex(a, d, 0, false);
            }

            if (PGraphicsOpenGL.nonZero(tl)) {
                this.addVertex(a, b + tl, 0, false);
                this.addQuadraticVertex(a, b, 0.0F, a + tl, b, 0.0F, false);
            } else {
                this.addVertex(a, b, 0, false);
            }

        }

        void addEllipse(float x, float y, float w, float h, boolean fill, boolean stroke) {
            float radiusH = w / 2.0F;
            float radiusV = h / 2.0F;
            float centerX = x + radiusH;
            float centerY = y + radiusV;
            float sx1 = this.pg.screenX(x, y);
            float sy1 = this.pg.screenY(x, y);
            float sx2 = this.pg.screenX(x + w, y + h);
            float sy2 = this.pg.screenY(x + w, y + h);
            int accuracy = PApplet.min(200, PApplet.max(20, (int)(6.2831855F * PApplet.dist(sx1, sy1, sx2, sy2) / 10.0F)));
            float inc = 720.0F / (float)accuracy;
            if (fill) {
                this.addVertex(centerX, centerY, 0, true);
            }

            int idx = 0;
            int pidx = 0;
            int idx0 = 0;
            float val = 0.0F;

            for(int i = 0; i < accuracy; ++i) {
                idx = this.addVertex(centerX + PGraphicsOpenGL.cosLUT[(int)val] * radiusH, centerY + PGraphicsOpenGL.sinLUT[(int)val] * radiusV, 0, i == 0 && !fill);
                val = (val + inc) % 720.0F;
                if (0 < i) {
                    if (stroke) {
                        this.addEdge(pidx, idx, i == 1, false);
                    }
                } else {
                    idx0 = idx;
                }

                pidx = idx;
            }

            this.addVertex(centerX + PGraphicsOpenGL.cosLUT[0] * radiusH, centerY + PGraphicsOpenGL.sinLUT[0] * radiusV, 0, false);
            if (stroke) {
                this.addEdge(idx, idx0, false, false);
                this.closeEdge(idx, idx0);
            }

        }

        void addArc(float x, float y, float w, float h, float start, float stop, boolean fill, boolean stroke, int arcMode) {
            float hr = w / 2.0F;
            float vr = h / 2.0F;
            float centerX = x + hr;
            float centerY = y + vr;
            int startLUT = (int)(0.5F + start / 6.2831855F * 720.0F);
            int stopLUT = (int)(0.5F + stop / 6.2831855F * 720.0F);
            int length = PApplet.constrain(stopLUT - startLUT, 0, 720);
            boolean fullCircle = length == 720;
            if (fullCircle && arcMode == 2) {
                --length;
                --stopLUT;
            }

            startLUT %= 720;
            if (startLUT < 0) {
                startLUT += 720;
            }

            stopLUT %= 720;
            if (stopLUT < 0) {
                stopLUT += 720;
            }

            int idx0;
            float sx1;
            if (arcMode != 2 && arcMode != 1) {
                idx0 = this.addVertex(centerX, centerY, 0, true);
            } else {
                float relX = (PGraphicsOpenGL.cosLUT[startLUT] + PGraphicsOpenGL.cosLUT[stopLUT]) * 0.5F * hr;
                sx1 = (PGraphicsOpenGL.sinLUT[startLUT] + PGraphicsOpenGL.sinLUT[stopLUT]) * 0.5F * vr;
                idx0 = this.addVertex(centerX + relX, centerY + sx1, 0, true);
            }

            sx1 = this.pg.screenX(x, y);
            float sy1 = this.pg.screenY(x, y);
            float sx2 = this.pg.screenX(x + w, y + h);
            float sy2 = this.pg.screenY(x + w, y + h);
            int accuracy = PApplet.min(200, PApplet.max(20, (int)(6.2831855F * PApplet.dist(sx1, sy1, sx2, sy2) / 10.0F)));
            int inc = PApplet.max(1, 720 / accuracy);
            int idx = idx0;
            int i = -inc;

            int pidx;
            do {
                i += inc;
                i = PApplet.min(i, length);
                int ii = startLUT + i;
                if (ii >= 720) {
                    ii -= 720;
                }

                pidx = idx;
                idx = this.addVertex(centerX + PGraphicsOpenGL.cosLUT[ii] * hr, centerY + PGraphicsOpenGL.sinLUT[ii] * vr, 0, i == 0 && !fill);
                if (stroke) {
                    if (arcMode != 2 && arcMode != 3) {
                        if (0 < i) {
                            this.addEdge(pidx, idx, i == PApplet.min(inc, length), i == length && !fullCircle);
                        }
                    } else {
                        this.addEdge(pidx, idx, i == 0, false);
                    }
                }
            } while(i < length);

            if (stroke) {
                if (arcMode != 2 && arcMode != 3) {
                    if (fullCircle) {
                        this.closeEdge(pidx, idx);
                    }
                } else {
                    this.addEdge(idx, idx0, false, false);
                    this.closeEdge(idx, idx0);
                }
            }

        }

        void addBox(float w, float h, float d, boolean fill, boolean stroke) {
            boolean invertNormX = h > 0.0F != d > 0.0F;
            boolean invertNormY = w > 0.0F != d > 0.0F;
            boolean invertNormZ = w > 0.0F != h > 0.0F;
            int normX = invertNormX ? -1 : 1;
            int normY = invertNormY ? -1 : 1;
            int normZ = invertNormZ ? -1 : 1;
            float x1 = -w / 2.0F;
            float x2 = w / 2.0F;
            float y1 = -h / 2.0F;
            float y2 = h / 2.0F;
            float z1 = -d / 2.0F;
            float z2 = d / 2.0F;
            int idx1 = false;
            int idx2 = false;
            int idx3 = false;
            int idx4 = false;
            if (fill || stroke) {
                this.setNormal(0.0F, 0.0F, (float)(-normZ));
                int idx1 = this.addVertex(x1, y1, z1, 0.0F, 0.0F, 0, true);
                int idx2 = this.addVertex(x1, y2, z1, 0.0F, 1.0F, 0, false);
                int idx3 = this.addVertex(x2, y2, z1, 1.0F, 1.0F, 0, false);
                int idx4 = this.addVertex(x2, y1, z1, 1.0F, 0.0F, 0, false);
                if (stroke) {
                    this.addEdge(idx1, idx2, true, false);
                    this.addEdge(idx2, idx3, false, false);
                    this.addEdge(idx3, idx4, false, false);
                    this.addEdge(idx4, idx1, false, false);
                    this.closeEdge(idx4, idx1);
                }

                this.setNormal(0.0F, 0.0F, (float)normZ);
                idx1 = this.addVertex(x1, y2, z2, 1.0F, 1.0F, 0, false);
                idx2 = this.addVertex(x1, y1, z2, 1.0F, 0.0F, 0, false);
                idx3 = this.addVertex(x2, y1, z2, 0.0F, 0.0F, 0, false);
                idx4 = this.addVertex(x2, y2, z2, 0.0F, 1.0F, 0, false);
                if (stroke) {
                    this.addEdge(idx1, idx2, true, false);
                    this.addEdge(idx2, idx3, false, false);
                    this.addEdge(idx3, idx4, false, false);
                    this.addEdge(idx4, idx1, false, false);
                    this.closeEdge(idx4, idx1);
                }

                this.setNormal((float)normX, 0.0F, 0.0F);
                idx1 = this.addVertex(x2, y1, z1, 0.0F, 0.0F, 0, false);
                idx2 = this.addVertex(x2, y2, z1, 0.0F, 1.0F, 0, false);
                idx3 = this.addVertex(x2, y2, z2, 1.0F, 1.0F, 0, false);
                idx4 = this.addVertex(x2, y1, z2, 1.0F, 0.0F, 0, false);
                if (stroke) {
                    this.addEdge(idx1, idx2, true, false);
                    this.addEdge(idx2, idx3, false, false);
                    this.addEdge(idx3, idx4, false, false);
                    this.addEdge(idx4, idx1, false, false);
                    this.closeEdge(idx4, idx1);
                }

                this.setNormal((float)(-normX), 0.0F, 0.0F);
                idx1 = this.addVertex(x1, y2, z1, 1.0F, 1.0F, 0, false);
                idx2 = this.addVertex(x1, y1, z1, 1.0F, 0.0F, 0, false);
                idx3 = this.addVertex(x1, y1, z2, 0.0F, 0.0F, 0, false);
                idx4 = this.addVertex(x1, y2, z2, 0.0F, 1.0F, 0, false);
                if (stroke) {
                    this.addEdge(idx1, idx2, true, false);
                    this.addEdge(idx2, idx3, false, false);
                    this.addEdge(idx3, idx4, false, false);
                    this.addEdge(idx4, idx1, false, false);
                    this.closeEdge(idx4, idx1);
                }

                this.setNormal(0.0F, (float)(-normY), 0.0F);
                idx1 = this.addVertex(x2, y1, z1, 1.0F, 1.0F, 0, false);
                idx2 = this.addVertex(x2, y1, z2, 1.0F, 0.0F, 0, false);
                idx3 = this.addVertex(x1, y1, z2, 0.0F, 0.0F, 0, false);
                idx4 = this.addVertex(x1, y1, z1, 0.0F, 1.0F, 0, false);
                if (stroke) {
                    this.addEdge(idx1, idx2, true, false);
                    this.addEdge(idx2, idx3, false, false);
                    this.addEdge(idx3, idx4, false, false);
                    this.addEdge(idx4, idx1, false, false);
                    this.closeEdge(idx4, idx1);
                }

                this.setNormal(0.0F, (float)normY, 0.0F);
                idx1 = this.addVertex(x1, y2, z1, 0.0F, 0.0F, 0, false);
                idx2 = this.addVertex(x1, y2, z2, 0.0F, 1.0F, 0, false);
                idx3 = this.addVertex(x2, y2, z2, 1.0F, 1.0F, 0, false);
                idx4 = this.addVertex(x2, y2, z1, 1.0F, 0.0F, 0, false);
                if (stroke) {
                    this.addEdge(idx1, idx2, true, false);
                    this.addEdge(idx2, idx3, false, false);
                    this.addEdge(idx3, idx4, false, false);
                    this.addEdge(idx4, idx1, false, false);
                    this.closeEdge(idx4, idx1);
                }
            }

        }

        int[] addSphere(float r, int detailU, int detailV, boolean fill, boolean stroke) {
            int nind = 3 * detailU + (6 * detailU + 3) * (detailV - 2) + 3 * detailU;
            int[] indices = new int[nind];
            int vertCount = false;
            int indCount = 0;
            float du = 1.0F / (float)detailU;
            float dv = 1.0F / (float)detailV;
            float u = 1.0F;
            float v = 1.0F;

            int offset;
            for(offset = 0; offset < detailU; ++offset) {
                this.setNormal(0.0F, 1.0F, 0.0F);
                this.addVertex(0.0F, r, 0.0F, u, v, 0, true);
                u -= du;
            }

            int vert0 = detailU;
            u = 1.0F;
            v -= dv;

            for(offset = 0; offset < detailU; ++offset) {
                this.setNormal(this.pg.sphereX[offset], this.pg.sphereY[offset], this.pg.sphereZ[offset]);
                this.addVertex(r * this.pg.sphereX[offset], r * this.pg.sphereY[offset], r * this.pg.sphereZ[offset], u, v, 0, false);
                u -= du;
            }

            int vertCount = detailU + detailU;
            this.setNormal(this.pg.sphereX[0], this.pg.sphereY[0], this.pg.sphereZ[0]);
            this.addVertex(r * this.pg.sphereX[0], r * this.pg.sphereY[0], r * this.pg.sphereZ[0], u, v, 0, false);
            ++vertCount;

            int i;
            int i0;
            for(offset = 0; offset < detailU; ++offset) {
                i = vert0 + offset;
                i0 = vert0 + offset - detailU;
                indices[3 * offset + 0] = i;
                indices[3 * offset + 1] = i0;
                indices[3 * offset + 2] = i + 1;
                this.addEdge(i0, i, true, true);
                this.addEdge(i, i + 1, true, true);
            }

            int indCount = indCount + 3 * detailU;
            offset = 0;

            int i1;
            for(i = 2; i < detailV; ++i) {
                offset += detailU;
                vert0 = vertCount;
                u = 1.0F;
                v -= dv;

                for(i0 = 0; i0 < detailU; ++i0) {
                    i1 = offset + i0;
                    this.setNormal(this.pg.sphereX[i1], this.pg.sphereY[i1], this.pg.sphereZ[i1]);
                    this.addVertex(r * this.pg.sphereX[i1], r * this.pg.sphereY[i1], r * this.pg.sphereZ[i1], u, v, 0, false);
                    u -= du;
                }

                vertCount += detailU;
                int vert1 = vertCount;
                this.setNormal(this.pg.sphereX[offset], this.pg.sphereY[offset], this.pg.sphereZ[offset]);
                this.addVertex(r * this.pg.sphereX[offset], r * this.pg.sphereY[offset], r * this.pg.sphereZ[offset], u, v, 0, false);
                ++vertCount;

                for(i0 = 0; i0 < detailU; ++i0) {
                    i1 = vert0 + i0;
                    int i0 = vert0 + i0 - detailU - 1;
                    indices[indCount + 6 * i0 + 0] = i1;
                    indices[indCount + 6 * i0 + 1] = i0;
                    indices[indCount + 6 * i0 + 2] = i0 + 1;
                    indices[indCount + 6 * i0 + 3] = i1;
                    indices[indCount + 6 * i0 + 4] = i0 + 1;
                    indices[indCount + 6 * i0 + 5] = i1 + 1;
                    this.addEdge(i0, i1, true, true);
                    this.addEdge(i1, i1 + 1, true, true);
                    this.addEdge(i0 + 1, i1, true, true);
                }

                indCount += 6 * detailU;
                indices[indCount + 0] = vert1;
                indices[indCount + 1] = vert1 - detailU;
                indices[indCount + 2] = vert1 - 1;
                indCount += 3;
            }

            u = 1.0F;
            v = 0.0F;

            for(i = 0; i < detailU; ++i) {
                this.setNormal(0.0F, -1.0F, 0.0F);
                this.addVertex(0.0F, -r, 0.0F, u, v, 0, false);
                u -= du;
            }

            int var10000 = vertCount + detailU;

            for(i = 0; i < detailU; ++i) {
                i0 = vert0 + i;
                i1 = vert0 + i + detailU + 1;
                indices[indCount + 3 * i + 0] = i1;
                indices[indCount + 3 * i + 1] = i0;
                indices[indCount + 3 * i + 2] = i0 + 1;
                this.addEdge(i0, i1, true, true);
            }

            var10000 = indCount + 3 * detailU;
            return indices;
        }
    }

    protected static class IndexCache {
        int size;
        int[] indexCount;
        int[] indexOffset;
        int[] vertexCount;
        int[] vertexOffset;
        int[] counter;

        IndexCache() {
            this.allocate();
        }

        void allocate() {
            this.size = 0;
            this.indexCount = new int[2];
            this.indexOffset = new int[2];
            this.vertexCount = new int[2];
            this.vertexOffset = new int[2];
            this.counter = null;
        }

        void clear() {
            this.size = 0;
        }

        int addNew() {
            this.arrayCheck();
            this.init(this.size);
            ++this.size;
            return this.size - 1;
        }

        int addNew(int index) {
            this.arrayCheck();
            this.indexCount[this.size] = this.indexCount[index];
            this.indexOffset[this.size] = this.indexOffset[index];
            this.vertexCount[this.size] = this.vertexCount[index];
            this.vertexOffset[this.size] = this.vertexOffset[index];
            ++this.size;
            return this.size - 1;
        }

        int getLast() {
            if (this.size == 0) {
                this.arrayCheck();
                this.init(0);
                this.size = 1;
            }

            return this.size - 1;
        }

        void setCounter(int[] counter) {
            this.counter = counter;
        }

        void incCounts(int index, int icount, int vcount) {
            int[] var10000 = this.indexCount;
            var10000[index] += icount;
            var10000 = this.vertexCount;
            var10000[index] += vcount;
            if (this.counter != null) {
                var10000 = this.counter;
                var10000[0] += icount;
                var10000 = this.counter;
                var10000[1] += vcount;
            }

        }

        void init(int n) {
            if (0 < n) {
                this.indexOffset[n] = this.indexOffset[n - 1] + this.indexCount[n - 1];
                this.vertexOffset[n] = this.vertexOffset[n - 1] + this.vertexCount[n - 1];
            } else {
                this.indexOffset[n] = 0;
                this.vertexOffset[n] = 0;
            }

            this.indexCount[n] = 0;
            this.vertexCount[n] = 0;
        }

        void arrayCheck() {
            if (this.size == this.indexCount.length) {
                int newSize = this.size << 1;
                this.expandIndexCount(newSize);
                this.expandIndexOffset(newSize);
                this.expandVertexCount(newSize);
                this.expandVertexOffset(newSize);
            }

        }

        void expandIndexCount(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.indexCount, 0, temp, 0, this.size);
            this.indexCount = temp;
        }

        void expandIndexOffset(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.indexOffset, 0, temp, 0, this.size);
            this.indexOffset = temp;
        }

        void expandVertexCount(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.vertexCount, 0, temp, 0, this.size);
            this.vertexCount = temp;
        }

        void expandVertexOffset(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.vertexOffset, 0, temp, 0, this.size);
            this.vertexOffset = temp;
        }
    }

    protected static class TexCache {
        PGraphicsOpenGL pg;
        int size;
        PImage[] textures;
        int[] firstIndex;
        int[] lastIndex;
        int[] firstCache;
        int[] lastCache;
        boolean hasTextures;

        TexCache(PGraphicsOpenGL pg) {
            this.pg = pg;
            this.allocate();
        }

        void allocate() {
            this.textures = new PImage[PGL.DEFAULT_IN_TEXTURES];
            this.firstIndex = new int[PGL.DEFAULT_IN_TEXTURES];
            this.lastIndex = new int[PGL.DEFAULT_IN_TEXTURES];
            this.firstCache = new int[PGL.DEFAULT_IN_TEXTURES];
            this.lastCache = new int[PGL.DEFAULT_IN_TEXTURES];
            this.size = 0;
            this.hasTextures = false;
        }

        void clear() {
            Arrays.fill(this.textures, 0, this.size, (Object)null);
            this.size = 0;
            this.hasTextures = false;
        }

        boolean containsTexture(PImage img) {
            for(int i = 0; i < this.size; ++i) {
                if (this.textures[i] == img) {
                    return true;
                }
            }

            return false;
        }

        PImage getTextureImage(int i) {
            return this.textures[i];
        }

        Texture getTexture(int i) {
            PImage img = this.textures[i];
            Texture tex = null;
            if (img != null) {
                tex = this.pg.getTexture(img);
            }

            return tex;
        }

        void addTexture(PImage img, int firsti, int firstb, int lasti, int lastb) {
            this.arrayCheck();
            this.textures[this.size] = img;
            this.firstIndex[this.size] = firsti;
            this.lastIndex[this.size] = lasti;
            this.firstCache[this.size] = firstb;
            this.lastCache[this.size] = lastb;
            this.hasTextures |= img != null;
            ++this.size;
        }

        void setLastIndex(int lasti, int lastb) {
            this.lastIndex[this.size - 1] = lasti;
            this.lastCache[this.size - 1] = lastb;
        }

        void arrayCheck() {
            if (this.size == this.textures.length) {
                int newSize = this.size << 1;
                this.expandTextures(newSize);
                this.expandFirstIndex(newSize);
                this.expandLastIndex(newSize);
                this.expandFirstCache(newSize);
                this.expandLastCache(newSize);
            }

        }

        void expandTextures(int n) {
            PImage[] temp = new PImage[n];
            PApplet.arrayCopy(this.textures, 0, temp, 0, this.size);
            this.textures = temp;
        }

        void expandFirstIndex(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.firstIndex, 0, temp, 0, this.size);
            this.firstIndex = temp;
        }

        void expandLastIndex(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.lastIndex, 0, temp, 0, this.size);
            this.lastIndex = temp;
        }

        void expandFirstCache(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.firstCache, 0, temp, 0, this.size);
            this.firstCache = temp;
        }

        void expandLastCache(int n) {
            int[] temp = new int[n];
            PApplet.arrayCopy(this.lastCache, 0, temp, 0, this.size);
            this.lastCache = temp;
        }
    }

    protected static class VertexAttribute {
        static final int POSITION = 0;
        static final int NORMAL = 1;
        static final int COLOR = 2;
        static final int OTHER = 3;
        PGraphicsOpenGL pg;
        String name;
        int kind;
        int type;
        int size;
        int tessSize;
        int elementSize;
        VertexBuffer buf;
        int glLoc;
        float[] fvalues;
        int[] ivalues;
        byte[] bvalues;
        boolean modified;
        int firstModified;
        int lastModified;
        boolean active;

        VertexAttribute(PGraphicsOpenGL pg, String name, int kind, int type, int size) {
            this.pg = pg;
            this.name = name;
            this.kind = kind;
            this.type = type;
            this.size = size;
            if (kind == 0) {
                this.tessSize = 4;
            } else {
                this.tessSize = size;
            }

            if (type == PGL.FLOAT) {
                this.elementSize = PGL.SIZEOF_FLOAT;
                this.fvalues = new float[size];
            } else if (type == PGL.INT) {
                this.elementSize = PGL.SIZEOF_INT;
                this.ivalues = new int[size];
            } else if (type == PGL.BOOL) {
                this.elementSize = PGL.SIZEOF_INT;
                this.bvalues = new byte[size];
            }

            this.buf = null;
            this.glLoc = -1;
            this.modified = false;
            this.firstModified = 2147483647;
            this.lastModified = -2147483648;
            this.active = true;
        }

        public boolean diff(PGraphicsOpenGL.VertexAttribute attr) {
            return !this.name.equals(attr.name) || this.kind != attr.kind || this.type != attr.type || this.size != attr.size || this.tessSize != attr.tessSize || this.elementSize != attr.elementSize;
        }

        boolean isPosition() {
            return this.kind == 0;
        }

        boolean isNormal() {
            return this.kind == 1;
        }

        boolean isColor() {
            return this.kind == 2;
        }

        boolean isOther() {
            return this.kind == 3;
        }

        boolean isFloat() {
            return this.type == PGL.FLOAT;
        }

        boolean isInt() {
            return this.type == PGL.INT;
        }

        boolean isBool() {
            return this.type == PGL.BOOL;
        }

        boolean bufferCreated() {
            return this.buf != null && 0 < this.buf.glId;
        }

        void createBuffer(PGL pgl) {
            this.buf = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, this.size, this.elementSize, false);
        }

        void deleteBuffer(PGL pgl) {
            if (this.buf.glId != 0) {
                PGraphicsOpenGL.intBuffer.put(0, this.buf.glId);
                if (pgl.threadIsCurrent()) {
                    pgl.deleteBuffers(1, PGraphicsOpenGL.intBuffer);
                }
            }

        }

        void bind(PGL pgl) {
            pgl.enableVertexAttribArray(this.glLoc);
        }

        void unbind(PGL pgl) {
            pgl.disableVertexAttribArray(this.glLoc);
        }

        boolean active(PShader shader) {
            if (this.active && this.glLoc == -1) {
                this.glLoc = shader.getAttributeLoc(this.name);
                if (this.glLoc == -1) {
                    this.active = false;
                }
            }

            return this.active;
        }

        int sizeInBytes(int length) {
            return length * this.tessSize * this.elementSize;
        }

        void set(float x, float y, float z) {
            this.fvalues[0] = x;
            this.fvalues[1] = y;
            this.fvalues[2] = z;
        }

        void set(int c) {
            this.ivalues[0] = c;
        }

        void set(float[] values) {
            PApplet.arrayCopy(values, 0, this.fvalues, 0, this.size);
        }

        void set(int[] values) {
            PApplet.arrayCopy(values, 0, this.ivalues, 0, this.size);
        }

        void set(boolean[] values) {
            for(int i = 0; i < values.length; ++i) {
                this.bvalues[i] = (byte)(values[i] ? 1 : 0);
            }

        }

        void add(float[] dstValues, int dstIdx) {
            PApplet.arrayCopy(this.fvalues, 0, dstValues, dstIdx, this.size);
        }

        void add(int[] dstValues, int dstIdx) {
            PApplet.arrayCopy(this.ivalues, 0, dstValues, dstIdx, this.size);
        }

        void add(byte[] dstValues, int dstIdx) {
            PApplet.arrayCopy(this.bvalues, 0, dstValues, dstIdx, this.size);
        }
    }

    protected static class AttributeMap extends HashMap<String, PGraphicsOpenGL.VertexAttribute> {
        public ArrayList<String> names = new ArrayList();
        public int numComp = 0;

        protected AttributeMap() {
        }

        public PGraphicsOpenGL.VertexAttribute put(String key, PGraphicsOpenGL.VertexAttribute value) {
            PGraphicsOpenGL.VertexAttribute prev = (PGraphicsOpenGL.VertexAttribute)super.put(key, value);
            this.names.add(key);
            if (value.kind == 2) {
                this.numComp += 4;
            } else {
                this.numComp += value.size;
            }

            return prev;
        }

        public PGraphicsOpenGL.VertexAttribute get(int i) {
            return (PGraphicsOpenGL.VertexAttribute)super.get(this.names.get(i));
        }
    }

    protected class AsyncPixelReader {
        static final int OPENGL_NATIVE = -1;
        static final int OPENGL_NATIVE_OPAQUE = -2;
        static final int BUFFER_COUNT = 3;
        int[] pbos;
        long[] fences;
        String[] filenames;
        int[] widths;
        int[] heights;
        int head;
        int tail;
        int size;
        boolean supportsAsyncTransfers;
        boolean calledThisFrame;

        public AsyncPixelReader() {
            this.supportsAsyncTransfers = PGraphicsOpenGL.this.pgl.hasPBOs() && PGraphicsOpenGL.this.pgl.hasSynchronization();
            if (this.supportsAsyncTransfers) {
                this.pbos = new int[3];
                this.fences = new long[3];
                this.filenames = new String[3];
                this.widths = new int[3];
                this.heights = new int[3];
                IntBuffer intBuffer = PGL.allocateIntBuffer(3);
                intBuffer.rewind();
                PGraphicsOpenGL.this.pgl.genBuffers(3, intBuffer);

                for(int i = 0; i < 3; ++i) {
                    this.pbos[i] = intBuffer.get(i);
                }
            }

        }

        public void dispose() {
            if (this.fences != null) {
                while(true) {
                    if (this.size <= 0) {
                        this.fences = null;
                        break;
                    }

                    PGraphicsOpenGL.this.pgl.deleteSync(this.fences[this.tail]);
                    --this.size;
                    this.tail = (this.tail + 1) % 3;
                }
            }

            if (this.pbos != null) {
                for(int i = 0; i < 3; ++i) {
                    IntBuffer intBuffer = PGL.allocateIntBuffer(this.pbos);
                    PGraphicsOpenGL.this.pgl.deleteBuffers(3, intBuffer);
                }

                this.pbos = null;
            }

            this.filenames = null;
            this.widths = null;
            this.heights = null;
            this.size = 0;
            this.head = 0;
            this.tail = 0;
            this.calledThisFrame = false;
            PGraphicsOpenGL.ongoingPixelTransfers.remove(this);
        }

        public void readAndSaveAsync(String filename) {
            if (this.size > 0) {
                boolean shouldRead = this.size == 3;
                if (!shouldRead) {
                    shouldRead = this.isLastTransferComplete();
                }

                if (shouldRead) {
                    this.endTransfer();
                }
            } else {
                PGraphicsOpenGL.ongoingPixelTransfers.add(this);
            }

            this.beginTransfer(filename);
            this.calledThisFrame = true;
        }

        public void completeFinishedTransfers() {
            if (this.size > 0 && PGraphicsOpenGL.asyncImageSaver.hasAvailableTarget()) {
                boolean needEndDraw = false;
                if (!PGraphicsOpenGL.this.drawing) {
                    PGraphicsOpenGL.this.beginDraw();
                    needEndDraw = true;
                }

                while(PGraphicsOpenGL.asyncImageSaver.hasAvailableTarget() && this.isLastTransferComplete()) {
                    this.endTransfer();
                }

                if (this.size <= 0) {
                    PGraphicsOpenGL.ongoingPixelTransfers.remove(this);
                }

                if (needEndDraw) {
                    PGraphicsOpenGL.this.endDraw();
                }

            }
        }

        protected void completeAllTransfers() {
            if (this.size > 0) {
                boolean needEndDraw = false;
                if (!PGraphicsOpenGL.this.drawing) {
                    PGraphicsOpenGL.this.beginDraw();
                    needEndDraw = true;
                }

                while(this.size > 0) {
                    this.endTransfer();
                }

                PGraphicsOpenGL.ongoingPixelTransfers.remove(this);
                if (needEndDraw) {
                    PGraphicsOpenGL.this.endDraw();
                }

            }
        }

        public boolean isLastTransferComplete() {
            if (this.size <= 0) {
                return false;
            } else {
                int status = PGraphicsOpenGL.this.pgl.clientWaitSync(this.fences[this.tail], 0, 0L);
                return status == PGL.ALREADY_SIGNALED || status == PGL.CONDITION_SATISFIED;
            }
        }

        public void beginTransfer(String filename) {
            if (this.widths[this.head] != PGraphicsOpenGL.this.pixelWidth || this.heights[this.head] != PGraphicsOpenGL.this.pixelHeight) {
                if (this.widths[this.head] * this.heights[this.head] != PGraphicsOpenGL.this.pixelWidth * PGraphicsOpenGL.this.pixelHeight) {
                    PGraphicsOpenGL.this.pgl.bindBuffer(PGL.PIXEL_PACK_BUFFER, this.pbos[this.head]);
                    PGraphicsOpenGL.this.pgl.bufferData(PGL.PIXEL_PACK_BUFFER, 4 * PGraphicsOpenGL.this.pixelWidth * PGraphicsOpenGL.this.pixelHeight, (Buffer)null, PGL.STREAM_READ);
                }

                this.widths[this.head] = PGraphicsOpenGL.this.pixelWidth;
                this.heights[this.head] = PGraphicsOpenGL.this.pixelHeight;
                PGraphicsOpenGL.this.pgl.bindBuffer(PGL.PIXEL_PACK_BUFFER, 0);
            }

            PGraphicsOpenGL.this.pgl.bindBuffer(PGL.PIXEL_PACK_BUFFER, this.pbos[this.head]);
            PGraphicsOpenGL.this.pgl.readPixels(0, 0, PGraphicsOpenGL.this.pixelWidth, PGraphicsOpenGL.this.pixelHeight, PGL.RGBA, PGL.UNSIGNED_BYTE, 0L);
            PGraphicsOpenGL.this.pgl.bindBuffer(PGL.PIXEL_PACK_BUFFER, 0);
            this.fences[this.head] = PGraphicsOpenGL.this.pgl.fenceSync(PGL.SYNC_GPU_COMMANDS_COMPLETE, 0);
            this.filenames[this.head] = filename;
            this.head = (this.head + 1) % 3;
            ++this.size;
        }

        public void endTransfer() {
            PGraphicsOpenGL.this.pgl.deleteSync(this.fences[this.tail]);
            PGraphicsOpenGL.this.pgl.bindBuffer(PGL.PIXEL_PACK_BUFFER, this.pbos[this.tail]);
            ByteBuffer readBuffer = PGraphicsOpenGL.this.pgl.mapBuffer(PGL.PIXEL_PACK_BUFFER, PGL.READ_ONLY);
            if (readBuffer != null) {
                int format = PGraphicsOpenGL.this.primaryGraphics ? -2 : -1;
                PImage target = PGraphicsOpenGL.asyncImageSaver.getAvailableTarget(this.widths[this.tail], this.heights[this.tail], format);
                if (target == null) {
                    return;
                }

                readBuffer.rewind();
                readBuffer.asIntBuffer().get(target.pixels);
                PGraphicsOpenGL.this.pgl.unmapBuffer(PGL.PIXEL_PACK_BUFFER);
                PGraphicsOpenGL.asyncImageSaver.saveTargetAsync(PGraphicsOpenGL.this, target, this.filenames[this.tail]);
            }

            PGraphicsOpenGL.this.pgl.bindBuffer(PGL.PIXEL_PACK_BUFFER, 0);
            --this.size;
            this.tail = (this.tail + 1) % 3;
        }
    }

    protected static class GLResourceFrameBuffer extends PGraphicsOpenGL.Disposable<FrameBuffer> {
        int glFbo;
        int glDepth;
        int glStencil;
        int glDepthStencil;
        int glMultisample;
        private PGL pgl;
        private int context;

        public GLResourceFrameBuffer(FrameBuffer fb) {
            super(fb);
            this.pgl = fb.pg.getPrimaryPGL();
            if (!fb.screenFb) {
                this.pgl.genFramebuffers(1, PGraphicsOpenGL.intBuffer);
                fb.glFbo = PGraphicsOpenGL.intBuffer.get(0);
                if (fb.multisample) {
                    this.pgl.genRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                    fb.glMultisample = PGraphicsOpenGL.intBuffer.get(0);
                }

                if (fb.packedDepthStencil) {
                    this.pgl.genRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                    fb.glDepthStencil = PGraphicsOpenGL.intBuffer.get(0);
                } else {
                    if (0 < fb.depthBits) {
                        this.pgl.genRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                        fb.glDepth = PGraphicsOpenGL.intBuffer.get(0);
                    }

                    if (0 < fb.stencilBits) {
                        this.pgl.genRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                        fb.glStencil = PGraphicsOpenGL.intBuffer.get(0);
                    }
                }

                this.glFbo = fb.glFbo;
                this.glDepth = fb.glDepth;
                this.glStencil = fb.glStencil;
                this.glDepthStencil = fb.glDepthStencil;
                this.glMultisample = fb.glMultisample;
            }

            this.context = fb.context;
        }

        public void disposeNative() {
            if (this.pgl != null) {
                if (this.glFbo != 0) {
                    PGraphicsOpenGL.intBuffer.put(0, this.glFbo);
                    this.pgl.deleteFramebuffers(1, PGraphicsOpenGL.intBuffer);
                    this.glFbo = 0;
                }

                if (this.glDepth != 0) {
                    PGraphicsOpenGL.intBuffer.put(0, this.glDepth);
                    this.pgl.deleteRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                    this.glDepth = 0;
                }

                if (this.glStencil != 0) {
                    PGraphicsOpenGL.intBuffer.put(0, this.glStencil);
                    this.pgl.deleteRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                    this.glStencil = 0;
                }

                if (this.glDepthStencil != 0) {
                    PGraphicsOpenGL.intBuffer.put(0, this.glDepthStencil);
                    this.pgl.deleteRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                    this.glDepthStencil = 0;
                }

                if (this.glMultisample != 0) {
                    PGraphicsOpenGL.intBuffer.put(0, this.glMultisample);
                    this.pgl.deleteRenderbuffers(1, PGraphicsOpenGL.intBuffer);
                    this.glMultisample = 0;
                }

                this.pgl = null;
            }

        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PGraphicsOpenGL.GLResourceFrameBuffer)) {
                return false;
            } else {
                PGraphicsOpenGL.GLResourceFrameBuffer other = (PGraphicsOpenGL.GLResourceFrameBuffer)obj;
                return other.glFbo == this.glFbo && other.glDepth == this.glDepth && other.glStencil == this.glStencil && other.glDepthStencil == this.glDepthStencil && other.glMultisample == this.glMultisample && other.context == this.context;
            }
        }

        public int hashCode() {
            int result = 17;
            int result = 31 * result + this.glFbo;
            result = 31 * result + this.glDepth;
            result = 31 * result + this.glStencil;
            result = 31 * result + this.glDepthStencil;
            result = 31 * result + this.glMultisample;
            result = 31 * result + this.context;
            return result;
        }
    }

    protected static class GLResourceShader extends PGraphicsOpenGL.Disposable<PShader> {
        int glProgram;
        int glVertex;
        int glFragment;
        private PGL pgl;
        private int context;

        public GLResourceShader(PShader sh) {
            super(sh);
            this.pgl = sh.pgl.graphics.getPrimaryPGL();
            sh.glProgram = this.pgl.createProgram();
            sh.glVertex = this.pgl.createShader(PGL.VERTEX_SHADER);
            sh.glFragment = this.pgl.createShader(PGL.FRAGMENT_SHADER);
            this.glProgram = sh.glProgram;
            this.glVertex = sh.glVertex;
            this.glFragment = sh.glFragment;
            this.context = sh.context;
        }

        public void disposeNative() {
            if (this.pgl != null) {
                if (this.glFragment != 0) {
                    this.pgl.deleteShader(this.glFragment);
                    this.glFragment = 0;
                }

                if (this.glVertex != 0) {
                    this.pgl.deleteShader(this.glVertex);
                    this.glVertex = 0;
                }

                if (this.glProgram != 0) {
                    this.pgl.deleteProgram(this.glProgram);
                    this.glProgram = 0;
                }

                this.pgl = null;
            }

        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PGraphicsOpenGL.GLResourceShader)) {
                return false;
            } else {
                PGraphicsOpenGL.GLResourceShader other = (PGraphicsOpenGL.GLResourceShader)obj;
                return other.glProgram == this.glProgram && other.glVertex == this.glVertex && other.glFragment == this.glFragment && other.context == this.context;
            }
        }

        public int hashCode() {
            int result = 17;
            int result = 31 * result + this.glProgram;
            result = 31 * result + this.glVertex;
            result = 31 * result + this.glFragment;
            result = 31 * result + this.context;
            return result;
        }
    }

    protected static class GLResourceVertexBuffer extends PGraphicsOpenGL.Disposable<VertexBuffer> {
        int glId;
        private PGL pgl;
        private int context;

        public GLResourceVertexBuffer(VertexBuffer vbo) {
            super(vbo);
            this.pgl = vbo.pgl.graphics.getPrimaryPGL();
            this.pgl.genBuffers(1, PGraphicsOpenGL.intBuffer);
            vbo.glId = PGraphicsOpenGL.intBuffer.get(0);
            this.glId = vbo.glId;
            this.context = vbo.context;
        }

        public void disposeNative() {
            if (this.pgl != null) {
                if (this.glId != 0) {
                    PGraphicsOpenGL.intBuffer.put(0, this.glId);
                    this.pgl.deleteBuffers(1, PGraphicsOpenGL.intBuffer);
                    this.glId = 0;
                }

                this.pgl = null;
            }

        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PGraphicsOpenGL.GLResourceVertexBuffer)) {
                return false;
            } else {
                PGraphicsOpenGL.GLResourceVertexBuffer other = (PGraphicsOpenGL.GLResourceVertexBuffer)obj;
                return other.glId == this.glId && other.context == this.context;
            }
        }

        public int hashCode() {
            int result = 17;
            int result = 31 * result + this.glId;
            result = 31 * result + this.context;
            return result;
        }
    }

    protected static class GLResourceTexture extends PGraphicsOpenGL.Disposable<Texture> {
        int glName;
        private PGL pgl;
        private int context;

        public GLResourceTexture(Texture tex) {
            super(tex);
            this.pgl = tex.pg.getPrimaryPGL();
            this.pgl.genTextures(1, PGraphicsOpenGL.intBuffer);
            tex.glName = PGraphicsOpenGL.intBuffer.get(0);
            this.glName = tex.glName;
            this.context = tex.context;
        }

        public void disposeNative() {
            if (this.pgl != null) {
                if (this.glName != 0) {
                    PGraphicsOpenGL.intBuffer.put(0, this.glName);
                    this.pgl.deleteTextures(1, PGraphicsOpenGL.intBuffer);
                    this.glName = 0;
                }

                this.pgl = null;
            }

        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PGraphicsOpenGL.GLResourceTexture)) {
                return false;
            } else {
                PGraphicsOpenGL.GLResourceTexture other = (PGraphicsOpenGL.GLResourceTexture)obj;
                return other.glName == this.glName && other.context == this.context;
            }
        }

        public int hashCode() {
            int result = 17;
            int result = 31 * result + this.glName;
            result = 31 * result + this.context;
            return result;
        }
    }

    private abstract static class Disposable<T> extends WeakReference<T> {
        protected Disposable(T obj) {
            super(obj, PGraphicsOpenGL.refQueue);
            PGraphicsOpenGL.drainRefQueueBounded();
            PGraphicsOpenGL.reachableWeakReferences.add(this);
        }

        public void dispose() {
            PGraphicsOpenGL.reachableWeakReferences.remove(this);
            this.disposeNative();
        }

        public abstract void disposeNative();
    }
}

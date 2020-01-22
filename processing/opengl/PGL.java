package processing.opengl;

import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.regex.Pattern;
import processing.core.PApplet;
import processing.core.PGraphics;

public abstract class PGL {
    protected PGraphicsOpenGL graphics;
    protected PApplet sketch;
    protected Thread glThread;
    protected int glContext;
    public boolean primaryPGL;
    public static int REQUESTED_DEPTH_BITS = 24;
    public static int REQUESTED_STENCIL_BITS = 8;
    public static int REQUESTED_ALPHA_BITS = 8;
    protected static boolean USE_DIRECT_BUFFERS = true;
    protected static int MIN_DIRECT_BUFFER_SIZE = 1;
    protected static boolean MIPMAPS_ENABLED = true;
    protected static int DEFAULT_IN_VERTICES = 64;
    protected static int DEFAULT_IN_EDGES = 128;
    protected static int DEFAULT_IN_TEXTURES = 64;
    protected static int DEFAULT_TESS_VERTICES = 64;
    protected static int DEFAULT_TESS_INDICES = 128;
    protected static int MAX_LIGHTS = 8;
    protected static int MAX_VERTEX_INDEX = 32767;
    protected static int MAX_VERTEX_INDEX1;
    protected static int FLUSH_VERTEX_COUNT;
    protected static int MIN_FONT_TEX_SIZE;
    protected static int MAX_FONT_TEX_SIZE;
    protected static float MIN_CAPS_JOINS_WEIGHT;
    protected static int MAX_CAPS_JOINS_LENGTH;
    protected static int MIN_ARRAYCOPY_SIZE;
    protected static float STROKE_DISPLACEMENT;
    protected IntBuffer firstFrame;
    protected static boolean SINGLE_BUFFERED;
    protected boolean fboLayerEnabled = false;
    protected boolean fboLayerCreated = false;
    protected boolean fboLayerEnabledReq = false;
    protected boolean fboLayerDisableReq = false;
    protected boolean fbolayerResetReq = false;
    public int reqNumSamples;
    protected int numSamples;
    protected IntBuffer glColorFbo;
    protected IntBuffer glColorTex;
    protected IntBuffer glDepthStencil;
    protected IntBuffer glDepth;
    protected IntBuffer glStencil;
    protected IntBuffer glMultiFbo;
    protected IntBuffer glMultiColor;
    protected IntBuffer glMultiDepthStencil;
    protected IntBuffer glMultiDepth;
    protected IntBuffer glMultiStencil;
    protected int fboWidth;
    protected int fboHeight;
    protected int backTex;
    protected int frontTex;
    protected boolean usingFrontTex = false;
    protected boolean needSepFrontTex = false;
    protected boolean loadedTex2DShader = false;
    protected int tex2DShaderProgram;
    protected int tex2DVertShader;
    protected int tex2DFragShader;
    protected int tex2DShaderContext;
    protected int tex2DVertLoc;
    protected int tex2DTCoordLoc;
    protected int tex2DSamplerLoc;
    protected int tex2DGeoVBO;
    protected boolean loadedTexRectShader = false;
    protected int texRectShaderProgram;
    protected int texRectVertShader;
    protected int texRectFragShader;
    protected int texRectShaderContext;
    protected int texRectVertLoc;
    protected int texRectTCoordLoc;
    protected int texRectSamplerLoc;
    protected int texRectGeoVBO;
    protected float[] texCoords = new float[]{-1.0F, -1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F};
    protected FloatBuffer texData;
    protected static final String SHADER_PREPROCESSOR_DIRECTIVE = "#ifdef GL_ES\nprecision mediump float;\nprecision mediump int;\n#endif\n";
    protected static String[] texVertShaderSource;
    protected static String[] tex2DFragShaderSource;
    protected static String[] texRectFragShaderSource;
    protected boolean[] texturingTargets = new boolean[]{false, false};
    protected int maxTexUnits;
    protected int activeTexUnit = 0;
    protected int[][] boundTextures;
    protected float targetFps = 60.0F;
    protected float currentFps = 60.0F;
    protected boolean setFps = false;
    protected ByteBuffer byteBuffer;
    protected IntBuffer intBuffer;
    protected IntBuffer viewBuffer;
    protected IntBuffer colorBuffer;
    protected FloatBuffer depthBuffer;
    protected ByteBuffer stencilBuffer;
    protected int geomCount = 0;
    protected int pgeomCount;
    protected boolean clearColor = false;
    protected boolean pclearColor;
    protected boolean clearDepth = false;
    protected boolean pclearDepth;
    protected boolean clearStencil = false;
    protected boolean pclearStencil;
    public static final String WIKI = " Read http://wiki.processing.org/w/OpenGL_Issues for help.";
    public static final String FRAMEBUFFER_ERROR = "Framebuffer error (%1$s), rendering will probably not work as expected Read http://wiki.processing.org/w/OpenGL_Issues for help.";
    public static final String MISSING_FBO_ERROR = "Framebuffer objects are not supported by this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.";
    public static final String MISSING_GLSL_ERROR = "GLSL shaders are not supported by this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.";
    public static final String MISSING_GLFUNC_ERROR = "GL function %1$s is not available on this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.";
    public static final String UNSUPPORTED_GLPROF_ERROR = "Unsupported OpenGL profile.";
    public static final String TEXUNIT_ERROR = "Number of texture units not supported by this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.";
    public static final String NONPRIMARY_ERROR = "The renderer is trying to call a PGL function that can only be called on a primary PGL. This is most likely due to a bug in the renderer's code, please report it with an issue on Processing's github page https://github.com/processing/processing/issues?state=open if using any of the built-in OpenGL renderers. If you are using a contributed library, contact the library's developers.";
    protected static final String DEPTH_READING_NOT_ENABLED_ERROR = "Reading depth and stencil values from this multisampled buffer is not enabled. You can enable it by calling hint(ENABLE_DEPTH_READING) once. If your sketch becomes too slow, disable multisampling with noSmooth() instead.";
    protected static int SIZEOF_SHORT;
    protected static int SIZEOF_INT;
    protected static int SIZEOF_FLOAT;
    protected static int SIZEOF_BYTE;
    protected static int SIZEOF_INDEX;
    protected static int INDEX_TYPE;
    protected static float FLOAT_EPS;
    protected static boolean BIG_ENDIAN;
    protected boolean presentMode = false;
    protected boolean showStopButton = true;
    public float presentX;
    public float presentY;
    protected IntBuffer closeButtonTex;
    protected int stopButtonColor;
    protected int stopButtonWidth = 28;
    protected int stopButtonHeight = 12;
    protected int stopButtonX = 21;
    protected int closeButtonY = 21;
    protected static int[] closeButtonPix;
    protected static final String GLSL_ID_REGEX = "(?<![0-9A-Z_a-z])(%s)(?![0-9A-Z_a-z]|\\s*\\()";
    protected static final String GLSL_FN_REGEX = "(?<![0-9A-Z_a-z])(%s)(?=\\s*\\()";
    protected static boolean SHAPE_TEXT_SUPPORTED;
    protected static int SEG_MOVETO;
    protected static int SEG_LINETO;
    protected static int SEG_QUADTO;
    protected static int SEG_CUBICTO;
    protected static int SEG_CLOSE;
    public static int FALSE;
    public static int TRUE;
    public static int INT;
    public static int BYTE;
    public static int SHORT;
    public static int FLOAT;
    public static int BOOL;
    public static int UNSIGNED_INT;
    public static int UNSIGNED_BYTE;
    public static int UNSIGNED_SHORT;
    public static int RGB;
    public static int RGBA;
    public static int ALPHA;
    public static int LUMINANCE;
    public static int LUMINANCE_ALPHA;
    public static int UNSIGNED_SHORT_5_6_5;
    public static int UNSIGNED_SHORT_4_4_4_4;
    public static int UNSIGNED_SHORT_5_5_5_1;
    public static int RGBA4;
    public static int RGB5_A1;
    public static int RGB565;
    public static int RGB8;
    public static int RGBA8;
    public static int ALPHA8;
    public static int READ_ONLY;
    public static int WRITE_ONLY;
    public static int READ_WRITE;
    public static int TESS_WINDING_NONZERO;
    public static int TESS_WINDING_ODD;
    public static int TESS_EDGE_FLAG;
    public static int GENERATE_MIPMAP_HINT;
    public static int FASTEST;
    public static int NICEST;
    public static int DONT_CARE;
    public static int VENDOR;
    public static int RENDERER;
    public static int VERSION;
    public static int EXTENSIONS;
    public static int SHADING_LANGUAGE_VERSION;
    public static int MAX_SAMPLES;
    public static int SAMPLES;
    public static int ALIASED_LINE_WIDTH_RANGE;
    public static int ALIASED_POINT_SIZE_RANGE;
    public static int DEPTH_BITS;
    public static int STENCIL_BITS;
    public static int CCW;
    public static int CW;
    public static int VIEWPORT;
    public static int ARRAY_BUFFER;
    public static int ELEMENT_ARRAY_BUFFER;
    public static int PIXEL_PACK_BUFFER;
    public static int MAX_VERTEX_ATTRIBS;
    public static int STATIC_DRAW;
    public static int DYNAMIC_DRAW;
    public static int STREAM_DRAW;
    public static int STREAM_READ;
    public static int BUFFER_SIZE;
    public static int BUFFER_USAGE;
    public static int POINTS;
    public static int LINE_STRIP;
    public static int LINE_LOOP;
    public static int LINES;
    public static int TRIANGLE_FAN;
    public static int TRIANGLE_STRIP;
    public static int TRIANGLES;
    public static int CULL_FACE;
    public static int FRONT;
    public static int BACK;
    public static int FRONT_AND_BACK;
    public static int POLYGON_OFFSET_FILL;
    public static int UNPACK_ALIGNMENT;
    public static int PACK_ALIGNMENT;
    public static int TEXTURE_2D;
    public static int TEXTURE_RECTANGLE;
    public static int TEXTURE_BINDING_2D;
    public static int TEXTURE_BINDING_RECTANGLE;
    public static int MAX_TEXTURE_SIZE;
    public static int TEXTURE_MAX_ANISOTROPY;
    public static int MAX_TEXTURE_MAX_ANISOTROPY;
    public static int MAX_VERTEX_TEXTURE_IMAGE_UNITS;
    public static int MAX_TEXTURE_IMAGE_UNITS;
    public static int MAX_COMBINED_TEXTURE_IMAGE_UNITS;
    public static int NUM_COMPRESSED_TEXTURE_FORMATS;
    public static int COMPRESSED_TEXTURE_FORMATS;
    public static int NEAREST;
    public static int LINEAR;
    public static int LINEAR_MIPMAP_NEAREST;
    public static int LINEAR_MIPMAP_LINEAR;
    public static int CLAMP_TO_EDGE;
    public static int REPEAT;
    public static int TEXTURE0;
    public static int TEXTURE1;
    public static int TEXTURE2;
    public static int TEXTURE3;
    public static int TEXTURE_MIN_FILTER;
    public static int TEXTURE_MAG_FILTER;
    public static int TEXTURE_WRAP_S;
    public static int TEXTURE_WRAP_T;
    public static int TEXTURE_WRAP_R;
    public static int TEXTURE_CUBE_MAP;
    public static int TEXTURE_CUBE_MAP_POSITIVE_X;
    public static int TEXTURE_CUBE_MAP_POSITIVE_Y;
    public static int TEXTURE_CUBE_MAP_POSITIVE_Z;
    public static int TEXTURE_CUBE_MAP_NEGATIVE_X;
    public static int TEXTURE_CUBE_MAP_NEGATIVE_Y;
    public static int TEXTURE_CUBE_MAP_NEGATIVE_Z;
    public static int VERTEX_SHADER;
    public static int FRAGMENT_SHADER;
    public static int INFO_LOG_LENGTH;
    public static int SHADER_SOURCE_LENGTH;
    public static int COMPILE_STATUS;
    public static int LINK_STATUS;
    public static int VALIDATE_STATUS;
    public static int SHADER_TYPE;
    public static int DELETE_STATUS;
    public static int FLOAT_VEC2;
    public static int FLOAT_VEC3;
    public static int FLOAT_VEC4;
    public static int FLOAT_MAT2;
    public static int FLOAT_MAT3;
    public static int FLOAT_MAT4;
    public static int INT_VEC2;
    public static int INT_VEC3;
    public static int INT_VEC4;
    public static int BOOL_VEC2;
    public static int BOOL_VEC3;
    public static int BOOL_VEC4;
    public static int SAMPLER_2D;
    public static int SAMPLER_CUBE;
    public static int LOW_FLOAT;
    public static int MEDIUM_FLOAT;
    public static int HIGH_FLOAT;
    public static int LOW_INT;
    public static int MEDIUM_INT;
    public static int HIGH_INT;
    public static int CURRENT_VERTEX_ATTRIB;
    public static int VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
    public static int VERTEX_ATTRIB_ARRAY_ENABLED;
    public static int VERTEX_ATTRIB_ARRAY_SIZE;
    public static int VERTEX_ATTRIB_ARRAY_STRIDE;
    public static int VERTEX_ATTRIB_ARRAY_TYPE;
    public static int VERTEX_ATTRIB_ARRAY_NORMALIZED;
    public static int VERTEX_ATTRIB_ARRAY_POINTER;
    public static int BLEND;
    public static int ONE;
    public static int ZERO;
    public static int SRC_ALPHA;
    public static int DST_ALPHA;
    public static int ONE_MINUS_SRC_ALPHA;
    public static int ONE_MINUS_DST_COLOR;
    public static int ONE_MINUS_SRC_COLOR;
    public static int DST_COLOR;
    public static int SRC_COLOR;
    public static int SAMPLE_ALPHA_TO_COVERAGE;
    public static int SAMPLE_COVERAGE;
    public static int KEEP;
    public static int REPLACE;
    public static int INCR;
    public static int DECR;
    public static int INVERT;
    public static int INCR_WRAP;
    public static int DECR_WRAP;
    public static int NEVER;
    public static int ALWAYS;
    public static int EQUAL;
    public static int LESS;
    public static int LEQUAL;
    public static int GREATER;
    public static int GEQUAL;
    public static int NOTEQUAL;
    public static int FUNC_ADD;
    public static int FUNC_MIN;
    public static int FUNC_MAX;
    public static int FUNC_REVERSE_SUBTRACT;
    public static int FUNC_SUBTRACT;
    public static int DITHER;
    public static int CONSTANT_COLOR;
    public static int CONSTANT_ALPHA;
    public static int ONE_MINUS_CONSTANT_COLOR;
    public static int ONE_MINUS_CONSTANT_ALPHA;
    public static int SRC_ALPHA_SATURATE;
    public static int SCISSOR_TEST;
    public static int STENCIL_TEST;
    public static int DEPTH_TEST;
    public static int DEPTH_WRITEMASK;
    public static int COLOR_BUFFER_BIT;
    public static int DEPTH_BUFFER_BIT;
    public static int STENCIL_BUFFER_BIT;
    public static int FRAMEBUFFER;
    public static int COLOR_ATTACHMENT0;
    public static int COLOR_ATTACHMENT1;
    public static int COLOR_ATTACHMENT2;
    public static int COLOR_ATTACHMENT3;
    public static int RENDERBUFFER;
    public static int DEPTH_ATTACHMENT;
    public static int STENCIL_ATTACHMENT;
    public static int READ_FRAMEBUFFER;
    public static int DRAW_FRAMEBUFFER;
    public static int DEPTH24_STENCIL8;
    public static int DEPTH_COMPONENT;
    public static int DEPTH_COMPONENT16;
    public static int DEPTH_COMPONENT24;
    public static int DEPTH_COMPONENT32;
    public static int STENCIL_INDEX;
    public static int STENCIL_INDEX1;
    public static int STENCIL_INDEX4;
    public static int STENCIL_INDEX8;
    public static int DEPTH_STENCIL;
    public static int FRAMEBUFFER_COMPLETE;
    public static int FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
    public static int FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
    public static int FRAMEBUFFER_INCOMPLETE_DIMENSIONS;
    public static int FRAMEBUFFER_INCOMPLETE_FORMATS;
    public static int FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
    public static int FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
    public static int FRAMEBUFFER_UNSUPPORTED;
    public static int FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE;
    public static int FRAMEBUFFER_ATTACHMENT_OBJECT_NAME;
    public static int FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL;
    public static int FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE;
    public static int RENDERBUFFER_WIDTH;
    public static int RENDERBUFFER_HEIGHT;
    public static int RENDERBUFFER_RED_SIZE;
    public static int RENDERBUFFER_GREEN_SIZE;
    public static int RENDERBUFFER_BLUE_SIZE;
    public static int RENDERBUFFER_ALPHA_SIZE;
    public static int RENDERBUFFER_DEPTH_SIZE;
    public static int RENDERBUFFER_STENCIL_SIZE;
    public static int RENDERBUFFER_INTERNAL_FORMAT;
    public static int MULTISAMPLE;
    public static int LINE_SMOOTH;
    public static int POLYGON_SMOOTH;
    public static int SYNC_GPU_COMMANDS_COMPLETE;
    public static int ALREADY_SIGNALED;
    public static int CONDITION_SATISFIED;

    public PGL() {
    }

    public PGL(PGraphicsOpenGL pg) {
        this.graphics = pg;
        if (this.glColorTex == null) {
            this.glColorFbo = allocateIntBuffer(1);
            this.glColorTex = allocateIntBuffer(2);
            this.glDepthStencil = allocateIntBuffer(1);
            this.glDepth = allocateIntBuffer(1);
            this.glStencil = allocateIntBuffer(1);
            this.glMultiFbo = allocateIntBuffer(1);
            this.glMultiColor = allocateIntBuffer(1);
            this.glMultiDepthStencil = allocateIntBuffer(1);
            this.glMultiDepth = allocateIntBuffer(1);
            this.glMultiStencil = allocateIntBuffer(1);
        }

        this.byteBuffer = allocateByteBuffer(1);
        this.intBuffer = allocateIntBuffer(1);
        this.viewBuffer = allocateIntBuffer(4);
    }

    public void dispose() {
        this.destroyFBOLayer();
        this.graphics = null;
        this.sketch = null;
    }

    public void setPrimary(boolean primary) {
        this.primaryPGL = primary;
    }

    public static int smoothToSamples(int smooth) {
        if (smooth == 0) {
            return 1;
        } else {
            return smooth == 1 ? 2 : smooth;
        }
    }

    public abstract Object getNative();

    public void setFrameRate(float fps) {
        this.targetFps = fps;
        this.currentFps = fps;
        this.setFps = true;
    }

    public float getFrameRate() {
        return this.currentFps;
    }

    protected abstract void initSurface(int var1);

    protected abstract void reinitSurface();

    protected abstract void registerListeners();

    protected int getReadFramebuffer() {
        return this.fboLayerEnabled ? this.glColorFbo.get(0) : 0;
    }

    protected int getDrawFramebuffer() {
        if (this.fboLayerEnabled) {
            return 1 < this.numSamples ? this.glMultiFbo.get(0) : this.glColorFbo.get(0);
        } else {
            return 0;
        }
    }

    protected int getDefaultDrawBuffer() {
        return this.fboLayerEnabled ? COLOR_ATTACHMENT0 : BACK;
    }

    protected int getDefaultReadBuffer() {
        return this.fboLayerEnabled ? COLOR_ATTACHMENT0 : FRONT;
    }

    protected boolean isFBOBacked() {
        return this.fboLayerEnabled;
    }

    /** @deprecated */
    @Deprecated
    public void requestFBOLayer() {
        this.enableFBOLayer();
    }

    public void enableFBOLayer() {
        this.fboLayerEnabledReq = true;
    }

    public void disableFBOLayer() {
        this.fboLayerDisableReq = true;
    }

    public void resetFBOLayer() {
        this.fbolayerResetReq = true;
    }

    public abstract void queueEvent(Runnable var1);

    protected boolean isMultisampled() {
        return 1 < this.numSamples;
    }

    protected abstract int getDepthBits();

    protected abstract int getStencilBits();

    protected boolean getDepthTest() {
        this.intBuffer.rewind();
        this.getBooleanv(DEPTH_TEST, this.intBuffer);
        return this.intBuffer.get(0) != 0;
    }

    protected boolean getDepthWriteMask() {
        this.intBuffer.rewind();
        this.getBooleanv(DEPTH_WRITEMASK, this.intBuffer);
        return this.intBuffer.get(0) != 0;
    }

    protected Texture wrapBackTexture(Texture texture) {
        if (texture == null) {
            texture = new Texture(this.graphics);
            texture.init(this.graphics.width, this.graphics.height, this.glColorTex.get(this.backTex), TEXTURE_2D, RGBA, this.fboWidth, this.fboHeight, NEAREST, NEAREST, CLAMP_TO_EDGE, CLAMP_TO_EDGE);
            texture.invertedY(!this.graphics.cameraUp);
            texture.colorBuffer(true);
            this.graphics.setCache(this.graphics, texture);
        } else {
            texture.glName = this.glColorTex.get(this.backTex);
        }

        return texture;
    }

    protected Texture wrapFrontTexture(Texture texture) {
        if (texture == null) {
            texture = new Texture(this.graphics);
            texture.init(this.graphics.width, this.graphics.height, this.glColorTex.get(this.frontTex), TEXTURE_2D, RGBA, this.fboWidth, this.fboHeight, NEAREST, NEAREST, CLAMP_TO_EDGE, CLAMP_TO_EDGE);
            texture.invertedY(!this.graphics.cameraUp);
            texture.colorBuffer(true);
        } else {
            texture.glName = this.glColorTex.get(this.frontTex);
        }

        return texture;
    }

    protected void bindFrontTexture() {
        this.usingFrontTex = true;
        if (!this.texturingIsEnabled(TEXTURE_2D)) {
            this.enableTexturing(TEXTURE_2D);
        }

        this.bindTexture(TEXTURE_2D, this.glColorTex.get(this.frontTex));
    }

    protected void unbindFrontTexture() {
        if (this.textureIsBound(TEXTURE_2D, this.glColorTex.get(this.frontTex))) {
            if (!this.texturingIsEnabled(TEXTURE_2D)) {
                this.enableTexturing(TEXTURE_2D);
                this.bindTexture(TEXTURE_2D, 0);
                this.disableTexturing(TEXTURE_2D);
            } else {
                this.bindTexture(TEXTURE_2D, 0);
            }
        }

    }

    protected void syncBackTexture() {
        if (this.usingFrontTex) {
            this.needSepFrontTex = true;
        }

        if (1 < this.numSamples) {
            this.bindFramebufferImpl(READ_FRAMEBUFFER, this.glMultiFbo.get(0));
            this.bindFramebufferImpl(DRAW_FRAMEBUFFER, this.glColorFbo.get(0));
            int mask = COLOR_BUFFER_BIT;
            if (this.graphics.getHint(10)) {
                mask |= DEPTH_BUFFER_BIT | STENCIL_BUFFER_BIT;
            }

            this.blitFramebuffer(0, 0, this.fboWidth, this.fboHeight, 0, 0, this.fboWidth, this.fboHeight, mask, NEAREST);
        }

    }

    protected abstract float getPixelScale();

    public void initPresentMode(float x, float y, int stopColor) {
        this.presentMode = true;
        this.showStopButton = stopColor != 0;
        this.stopButtonColor = stopColor;
        this.presentX = x;
        this.presentY = y;
        this.enableFBOLayer();
    }

    public boolean presentMode() {
        return this.presentMode;
    }

    public float presentX() {
        return this.presentX;
    }

    public float presentY() {
        return this.presentY;
    }

    public boolean insideStopButton(float x, float y) {
        if (!this.showStopButton) {
            return false;
        } else {
            return (float)this.stopButtonX < x && x < (float)(this.stopButtonX + this.stopButtonWidth) && (float)(-(this.closeButtonY + this.stopButtonHeight)) < y && y < (float)(-this.closeButtonY);
        }
    }

    protected void clearDepthStencil() {
        if (!this.pclearDepth && !this.pclearStencil) {
            this.depthMask(true);
            this.clearDepth(1.0F);
            this.clearStencil(0);
            this.clear(DEPTH_BUFFER_BIT | STENCIL_BUFFER_BIT);
        } else if (!this.pclearDepth) {
            this.depthMask(true);
            this.clearDepth(1.0F);
            this.clear(DEPTH_BUFFER_BIT);
        } else if (!this.pclearStencil) {
            this.clearStencil(0);
            this.clear(STENCIL_BUFFER_BIT);
        }

    }

    protected void clearBackground(float r, float g, float b, float a, boolean depth, boolean stencil) {
        this.clearColor(r, g, b, a);
        if (depth && stencil) {
            this.clearDepth(1.0F);
            this.clearStencil(0);
            this.clear(DEPTH_BUFFER_BIT | STENCIL_BUFFER_BIT | COLOR_BUFFER_BIT);
            if (0 < this.sketch.frameCount) {
                this.clearColor = true;
                this.clearDepth = true;
                this.clearStencil = true;
            }
        } else if (depth) {
            this.clearDepth(1.0F);
            this.clear(DEPTH_BUFFER_BIT | COLOR_BUFFER_BIT);
            if (0 < this.sketch.frameCount) {
                this.clearColor = true;
                this.clearDepth = true;
            }
        } else if (stencil) {
            this.clearStencil(0);
            this.clear(STENCIL_BUFFER_BIT | COLOR_BUFFER_BIT);
            if (0 < this.sketch.frameCount) {
                this.clearColor = true;
                this.clearStencil = true;
            }
        } else {
            this.clear(COLOR_BUFFER_BIT);
            if (0 < this.sketch.frameCount) {
                this.clearColor = true;
            }
        }

        if (this.fboLayerEnabled) {
            this.clearFrontColorBuffer();
        }

    }

    protected void beginRender() {
        if (this.sketch == null) {
            this.sketch = this.graphics.parent;
        }

        this.pgeomCount = this.geomCount;
        this.geomCount = 0;
        this.pclearColor = this.clearColor;
        this.clearColor = false;
        this.pclearDepth = this.clearDepth;
        this.clearDepth = false;
        this.pclearStencil = this.clearStencil;
        this.clearStencil = false;
        if (this.fboLayerEnabledReq) {
            this.fboLayerEnabled = true;
            this.fboLayerEnabledReq = false;
        }

        if (this.fboLayerEnabled) {
            if (this.fbolayerResetReq) {
                this.destroyFBOLayer();
                this.fbolayerResetReq = false;
            }

            if (!this.fboLayerCreated) {
                this.createFBOLayer();
            }

            this.bindFramebufferImpl(FRAMEBUFFER, this.glColorFbo.get(0));
            this.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_2D, this.glColorTex.get(this.backTex), 0);
            if (1 < this.numSamples) {
                this.bindFramebufferImpl(FRAMEBUFFER, this.glMultiFbo.get(0));
            }

            int x;
            float scale;
            if (this.sketch.frameCount == 0) {
                x = this.graphics.backgroundColor;
                float ba = (float)(x >> 24 & 255) / 255.0F;
                scale = (float)(x >> 16 & 255) / 255.0F;
                float bg = (float)(x >> 8 & 255) / 255.0F;
                float bb = (float)(x & 255) / 255.0F;
                this.clearColor(scale, bg, bb, ba);
                this.clear(COLOR_BUFFER_BIT);
            } else if (!this.pclearColor || !this.graphics.isLooping()) {
                x = 0;
                int y = 0;
                if (this.presentMode) {
                    x = (int)this.presentX;
                    y = (int)this.presentY;
                }

                scale = this.getPixelScale();
                this.drawTexture(TEXTURE_2D, this.glColorTex.get(this.frontTex), this.fboWidth, this.fboHeight, x, y, this.graphics.width, this.graphics.height, 0, 0, (int)(scale * (float)this.graphics.width), (int)(scale * (float)this.graphics.height), 0, 0, this.graphics.width, this.graphics.height);
            }
        } else if (SINGLE_BUFFERED && this.sketch.frameCount == 1) {
            this.restoreFirstFrame();
        }

    }

    protected void endRender(int windowColor) {
        if (this.fboLayerEnabled) {
            this.syncBackTexture();
            this.bindFramebufferImpl(FRAMEBUFFER, 0);
            float scale;
            if (this.presentMode) {
                float wa = (float)(windowColor >> 24 & 255) / 255.0F;
                float wr = (float)(windowColor >> 16 & 255) / 255.0F;
                scale = (float)(windowColor >> 8 & 255) / 255.0F;
                float wb = (float)(windowColor & 255) / 255.0F;
                this.clearDepth(1.0F);
                this.clearColor(wr, scale, wb, wa);
                this.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
                if (this.showStopButton) {
                    if (this.closeButtonTex == null) {
                        this.closeButtonTex = allocateIntBuffer(1);
                        this.genTextures(1, this.closeButtonTex);
                        this.bindTexture(TEXTURE_2D, this.closeButtonTex.get(0));
                        this.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST);
                        this.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST);
                        this.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE);
                        this.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE);
                        this.texImage2D(TEXTURE_2D, 0, RGBA, this.stopButtonWidth, this.stopButtonHeight, 0, RGBA, UNSIGNED_BYTE, (Buffer)null);
                        int[] color = new int[closeButtonPix.length];
                        PApplet.arrayCopy(closeButtonPix, color);
                        float ba = (float)(this.stopButtonColor >> 24 & 255) / 255.0F;
                        float br = (float)(this.stopButtonColor >> 16 & 255) / 255.0F;
                        float bg = (float)(this.stopButtonColor >> 8 & 255) / 255.0F;
                        float bb = (float)(this.stopButtonColor >> 0 & 255) / 255.0F;

                        for(int i = 0; i < color.length; ++i) {
                            int c = closeButtonPix[i];
                            int a = (int)(ba * (float)(c >> 24 & 255));
                            int r = (int)(br * (float)(c >> 16 & 255));
                            int g = (int)(bg * (float)(c >> 8 & 255));
                            int b = (int)(bb * (float)(c >> 0 & 255));
                            color[i] = javaToNativeARGB(a << 24 | r << 16 | g << 8 | b);
                        }

                        IntBuffer buf = allocateIntBuffer(color);
                        this.copyToTexture(TEXTURE_2D, RGBA, this.closeButtonTex.get(0), 0, 0, this.stopButtonWidth, this.stopButtonHeight, (IntBuffer)buf);
                        this.bindTexture(TEXTURE_2D, 0);
                    }

                    this.drawTexture(TEXTURE_2D, this.closeButtonTex.get(0), this.stopButtonWidth, this.stopButtonHeight, 0, 0, this.stopButtonX + this.stopButtonWidth, this.closeButtonY + this.stopButtonHeight, 0, this.stopButtonHeight, this.stopButtonWidth, 0, this.stopButtonX, this.closeButtonY, this.stopButtonX + this.stopButtonWidth, this.closeButtonY + this.stopButtonHeight);
                }
            } else {
                this.clearDepth(1.0F);
                this.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
                this.clear(COLOR_BUFFER_BIT | DEPTH_BUFFER_BIT);
            }

            this.disable(BLEND);
            int x = 0;
            int y = 0;
            if (this.presentMode) {
                x = (int)this.presentX;
                y = (int)this.presentY;
            }

            scale = this.getPixelScale();
            this.drawTexture(TEXTURE_2D, this.glColorTex.get(this.backTex), this.fboWidth, this.fboHeight, x, y, this.graphics.width, this.graphics.height, 0, 0, (int)(scale * (float)this.graphics.width), (int)(scale * (float)this.graphics.height), 0, 0, this.graphics.width, this.graphics.height);
            int temp = this.frontTex;
            this.frontTex = this.backTex;
            this.backTex = temp;
            if (this.fboLayerDisableReq) {
                this.fboLayerEnabled = false;
                this.fboLayerDisableReq = false;
            }
        } else {
            if (SINGLE_BUFFERED && this.sketch.frameCount == 0) {
                this.saveFirstFrame();
            }

            if (!this.clearColor && 0 < this.sketch.frameCount || !this.graphics.isLooping()) {
                this.enableFBOLayer();
                if (SINGLE_BUFFERED) {
                    this.createFBOLayer();
                }
            }
        }

    }

    protected abstract void getGL(PGL var1);

    protected abstract boolean canDraw();

    protected abstract void requestFocus();

    protected abstract void requestDraw();

    protected abstract void swapBuffers();

    public boolean threadIsCurrent() {
        return Thread.currentThread() == this.glThread;
    }

    public void setThread(Thread thread) {
        this.glThread = thread;
    }

    protected void beginGL() {
    }

    protected void endGL() {
    }

    private void createFBOLayer() {
        float scale = this.getPixelScale();
        if (this.hasNpotTexSupport()) {
            this.fboWidth = (int)(scale * (float)this.graphics.width);
            this.fboHeight = (int)(scale * (float)this.graphics.height);
        } else {
            this.fboWidth = nextPowerOfTwo((int)(scale * (float)this.graphics.width));
            this.fboHeight = nextPowerOfTwo((int)(scale * (float)this.graphics.height));
        }

        if (this.hasFboMultisampleSupport()) {
            int maxs = this.maxSamples();
            this.numSamples = PApplet.min(this.reqNumSamples, maxs);
        } else {
            this.numSamples = 1;
        }

        boolean multisample = 1 < this.numSamples;
        boolean packed = this.hasPackedDepthStencilSupport();
        int depthBits = PApplet.min(REQUESTED_DEPTH_BITS, this.getDepthBits());
        int stencilBits = PApplet.min(REQUESTED_STENCIL_BITS, this.getStencilBits());
        this.backTex = 0;
        this.frontTex = 1;
        boolean savedFirstFrame = SINGLE_BUFFERED && this.sketch.frameCount == 0 && this.firstFrame != null;
        this.genTextures(2, this.glColorTex);

        int argb;
        for(argb = 0; argb < 2; ++argb) {
            this.bindTexture(TEXTURE_2D, this.glColorTex.get(argb));
            this.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST);
            this.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST);
            this.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE);
            this.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE);
            this.texImage2D(TEXTURE_2D, 0, RGBA, this.fboWidth, this.fboHeight, 0, RGBA, UNSIGNED_BYTE, (Buffer)null);
            if (argb == this.frontTex && savedFirstFrame) {
                this.texSubImage2D(TEXTURE_2D, 0, 0, 0, this.graphics.width, this.graphics.height, RGBA, UNSIGNED_BYTE, this.firstFrame);
            } else {
                this.initTexture(TEXTURE_2D, RGBA, this.fboWidth, this.fboHeight, this.graphics.backgroundColor);
            }
        }

        this.bindTexture(TEXTURE_2D, 0);
        this.genFramebuffers(1, this.glColorFbo);
        this.bindFramebufferImpl(FRAMEBUFFER, this.glColorFbo.get(0));
        this.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_2D, this.glColorTex.get(this.backTex), 0);
        if (!multisample || this.graphics.getHint(10)) {
            this.createDepthAndStencilBuffer(false, depthBits, stencilBits, packed);
        }

        if (multisample) {
            this.genFramebuffers(1, this.glMultiFbo);
            this.bindFramebufferImpl(FRAMEBUFFER, this.glMultiFbo.get(0));
            this.genRenderbuffers(1, this.glMultiColor);
            this.bindRenderbuffer(RENDERBUFFER, this.glMultiColor.get(0));
            this.renderbufferStorageMultisample(RENDERBUFFER, this.numSamples, RGBA8, this.fboWidth, this.fboHeight);
            this.framebufferRenderbuffer(FRAMEBUFFER, COLOR_ATTACHMENT0, RENDERBUFFER, this.glMultiColor.get(0));
            this.createDepthAndStencilBuffer(true, depthBits, stencilBits, packed);
        }

        this.validateFramebuffer();
        this.clearDepth(1.0F);
        this.clearStencil(0);
        argb = this.graphics.backgroundColor;
        float ba = (float)(argb >> 24 & 255) / 255.0F;
        float br = (float)(argb >> 16 & 255) / 255.0F;
        float bg = (float)(argb >> 8 & 255) / 255.0F;
        float bb = (float)(argb & 255) / 255.0F;
        this.clearColor(br, bg, bb, ba);
        this.clear(DEPTH_BUFFER_BIT | STENCIL_BUFFER_BIT | COLOR_BUFFER_BIT);
        this.bindFramebufferImpl(FRAMEBUFFER, 0);
        this.initFBOLayer();
        this.fboLayerCreated = true;
    }

    protected abstract void initFBOLayer();

    protected void saveFirstFrame() {
        this.firstFrame = allocateDirectIntBuffer(this.graphics.width * this.graphics.height);
        if (this.hasReadBuffer()) {
            this.readBuffer(BACK);
        }

        this.readPixelsImpl(0, 0, this.graphics.width, this.graphics.height, RGBA, UNSIGNED_BYTE, this.firstFrame);
    }

    protected void restoreFirstFrame() {
        if (this.firstFrame != null) {
            IntBuffer tex = allocateIntBuffer(1);
            this.genTextures(1, tex);
            float scale = this.getPixelScale();
            int w;
            int h;
            if (this.hasNpotTexSupport()) {
                w = (int)(scale * (float)this.graphics.width);
                h = (int)(scale * (float)this.graphics.height);
            } else {
                w = nextPowerOfTwo((int)(scale * (float)this.graphics.width));
                h = nextPowerOfTwo((int)(scale * (float)this.graphics.height));
            }

            this.bindTexture(TEXTURE_2D, tex.get(0));
            this.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST);
            this.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST);
            this.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE);
            this.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE);
            this.texImage2D(TEXTURE_2D, 0, RGBA, w, h, 0, RGBA, UNSIGNED_BYTE, (Buffer)null);
            this.texSubImage2D(TEXTURE_2D, 0, 0, 0, this.graphics.width, this.graphics.height, RGBA, UNSIGNED_BYTE, this.firstFrame);
            this.drawTexture(TEXTURE_2D, tex.get(0), w, h, 0, 0, this.graphics.width, this.graphics.height, 0, 0, (int)(scale * (float)this.graphics.width), (int)(scale * (float)this.graphics.height), 0, 0, this.graphics.width, this.graphics.height);
            this.deleteTextures(1, tex);
            this.firstFrame.clear();
            this.firstFrame = null;
        }
    }

    protected void destroyFBOLayer() {
        if (this.threadIsCurrent() && this.fboLayerCreated) {
            this.deleteFramebuffers(1, this.glColorFbo);
            this.deleteTextures(2, this.glColorTex);
            this.deleteRenderbuffers(1, this.glDepthStencil);
            this.deleteRenderbuffers(1, this.glDepth);
            this.deleteRenderbuffers(1, this.glStencil);
            this.deleteFramebuffers(1, this.glMultiFbo);
            this.deleteRenderbuffers(1, this.glMultiColor);
            this.deleteRenderbuffers(1, this.glMultiDepthStencil);
            this.deleteRenderbuffers(1, this.glMultiDepth);
            this.deleteRenderbuffers(1, this.glMultiStencil);
        }

        this.fboLayerCreated = false;
    }

    private void createDepthAndStencilBuffer(boolean multisample, int depthBits, int stencilBits, boolean packed) {
        if (packed && depthBits == 24 && stencilBits == 8) {
            IntBuffer depthStencilBuf = multisample ? this.glMultiDepthStencil : this.glDepthStencil;
            this.genRenderbuffers(1, depthStencilBuf);
            this.bindRenderbuffer(RENDERBUFFER, depthStencilBuf.get(0));
            if (multisample) {
                this.renderbufferStorageMultisample(RENDERBUFFER, this.numSamples, DEPTH24_STENCIL8, this.fboWidth, this.fboHeight);
            } else {
                this.renderbufferStorage(RENDERBUFFER, DEPTH24_STENCIL8, this.fboWidth, this.fboHeight);
            }

            this.framebufferRenderbuffer(FRAMEBUFFER, DEPTH_ATTACHMENT, RENDERBUFFER, depthStencilBuf.get(0));
            this.framebufferRenderbuffer(FRAMEBUFFER, STENCIL_ATTACHMENT, RENDERBUFFER, depthStencilBuf.get(0));
        } else {
            int stencilIndex;
            IntBuffer stencilBuf;
            if (0 < depthBits) {
                stencilIndex = DEPTH_COMPONENT16;
                if (depthBits == 32) {
                    stencilIndex = DEPTH_COMPONENT32;
                } else if (depthBits == 24) {
                    stencilIndex = DEPTH_COMPONENT24;
                } else if (depthBits == 16) {
                    stencilIndex = DEPTH_COMPONENT16;
                }

                stencilBuf = multisample ? this.glMultiDepth : this.glDepth;
                this.genRenderbuffers(1, stencilBuf);
                this.bindRenderbuffer(RENDERBUFFER, stencilBuf.get(0));
                if (multisample) {
                    this.renderbufferStorageMultisample(RENDERBUFFER, this.numSamples, stencilIndex, this.fboWidth, this.fboHeight);
                } else {
                    this.renderbufferStorage(RENDERBUFFER, stencilIndex, this.fboWidth, this.fboHeight);
                }

                this.framebufferRenderbuffer(FRAMEBUFFER, DEPTH_ATTACHMENT, RENDERBUFFER, stencilBuf.get(0));
            }

            if (0 < stencilBits) {
                stencilIndex = STENCIL_INDEX1;
                if (stencilBits == 8) {
                    stencilIndex = STENCIL_INDEX8;
                } else if (stencilBits == 4) {
                    stencilIndex = STENCIL_INDEX4;
                } else if (stencilBits == 1) {
                    stencilIndex = STENCIL_INDEX1;
                }

                stencilBuf = multisample ? this.glMultiStencil : this.glStencil;
                this.genRenderbuffers(1, stencilBuf);
                this.bindRenderbuffer(RENDERBUFFER, stencilBuf.get(0));
                if (multisample) {
                    this.renderbufferStorageMultisample(RENDERBUFFER, this.numSamples, stencilIndex, this.fboWidth, this.fboHeight);
                } else {
                    this.renderbufferStorage(RENDERBUFFER, stencilIndex, this.fboWidth, this.fboHeight);
                }

                this.framebufferRenderbuffer(FRAMEBUFFER, STENCIL_ATTACHMENT, RENDERBUFFER, stencilBuf.get(0));
            }
        }

    }

    protected void clearFrontColorBuffer() {
    }

    protected int createEmptyContext() {
        return -1;
    }

    protected int getCurrentContext() {
        return this.glContext;
    }

    protected boolean contextIsCurrent(int other) {
        return other == -1 || other == this.glContext;
    }

    protected void enableTexturing(int target) {
        if (target == TEXTURE_2D) {
            this.texturingTargets[0] = true;
        } else if (target == TEXTURE_RECTANGLE) {
            this.texturingTargets[1] = true;
        }

    }

    protected void disableTexturing(int target) {
        if (target == TEXTURE_2D) {
            this.texturingTargets[0] = false;
        } else if (target == TEXTURE_RECTANGLE) {
            this.texturingTargets[1] = false;
        }

    }

    protected boolean texturingIsEnabled(int target) {
        if (target == TEXTURE_2D) {
            return this.texturingTargets[0];
        } else {
            return target == TEXTURE_RECTANGLE ? this.texturingTargets[1] : false;
        }
    }

    protected boolean textureIsBound(int target, int id) {
        if (this.boundTextures == null) {
            return false;
        } else if (target == TEXTURE_2D) {
            return this.boundTextures[this.activeTexUnit][0] == id;
        } else if (target == TEXTURE_RECTANGLE) {
            return this.boundTextures[this.activeTexUnit][1] == id;
        } else {
            return false;
        }
    }

    protected void initTexture(int target, int format, int width, int height) {
        this.initTexture(target, format, width, height, 0);
    }

    protected void initTexture(int target, int format, int width, int height, int initColor) {
        int[] glcolor = new int[256];
        Arrays.fill(glcolor, javaToNativeARGB(initColor));
        IntBuffer texels = allocateDirectIntBuffer(256);
        texels.put(glcolor);
        texels.rewind();

        for(int y = 0; y < height; y += 16) {
            int h = PApplet.min(16, height - y);

            for(int x = 0; x < width; x += 16) {
                int w = PApplet.min(16, width - x);
                this.texSubImage2D(target, 0, x, y, w, h, format, UNSIGNED_BYTE, texels);
            }
        }

    }

    protected void copyToTexture(int target, int format, int id, int x, int y, int w, int h, int[] buffer) {
        this.copyToTexture(target, format, id, x, y, w, h, IntBuffer.wrap(buffer));
    }

    protected void copyToTexture(int target, int format, int id, int x, int y, int w, int h, IntBuffer buffer) {
        this.activeTexture(TEXTURE0);
        boolean enabledTex = false;
        if (!this.texturingIsEnabled(target)) {
            this.enableTexturing(target);
            enabledTex = true;
        }

        this.bindTexture(target, id);
        this.texSubImage2D(target, 0, x, y, w, h, format, UNSIGNED_BYTE, buffer);
        this.bindTexture(target, 0);
        if (enabledTex) {
            this.disableTexturing(target);
        }

    }

    public void drawTexture(int target, int id, int width, int height, int X0, int Y0, int X1, int Y1) {
        this.drawTexture(target, id, width, height, 0, 0, width, height, 1, X0, Y0, X1, Y1, X0, Y0, X1, Y1);
    }

    public void drawTexture(int target, int id, int texW, int texH, int viewX, int viewY, int viewW, int viewH, int texX0, int texY0, int texX1, int texY1, int scrX0, int scrY0, int scrX1, int scrY1) {
        int viewF = (int)this.getPixelScale();
        this.drawTexture(target, id, texW, texH, viewX, viewY, viewW, viewH, viewF, texX0, texY0, texX1, texY1, scrX0, scrY0, scrX1, scrY1);
    }

    public void drawTexture(int target, int id, int texW, int texH, int viewX, int viewY, int viewW, int viewH, int viewF, int texX0, int texY0, int texX1, int texY1, int scrX0, int scrY0, int scrX1, int scrY1) {
        if (target == TEXTURE_2D) {
            this.drawTexture2D(id, texW, texH, viewX, viewY, viewW, viewH, viewF, texX0, texY0, texX1, texY1, scrX0, scrY0, scrX1, scrY1);
        } else if (target == TEXTURE_RECTANGLE) {
            this.drawTextureRect(id, texW, texH, viewX, viewY, viewW, viewH, viewF, texX0, texY0, texX1, texY1, scrX0, scrY0, scrX1, scrY1);
        }

    }

    protected PGL initTex2DShader() {
        PGL ppgl = this.primaryPGL ? this : this.graphics.getPrimaryPGL();
        if (!ppgl.loadedTex2DShader || ppgl.tex2DShaderContext != ppgl.glContext) {
            String[] preprocVertSrc = preprocessVertexSource(texVertShaderSource, this.getGLSLVersion());
            String vertSource = PApplet.join(preprocVertSrc, "\n");
            String[] preprocFragSrc = preprocessFragmentSource(tex2DFragShaderSource, this.getGLSLVersion());
            String fragSource = PApplet.join(preprocFragSrc, "\n");
            ppgl.tex2DVertShader = this.createShader(VERTEX_SHADER, vertSource);
            ppgl.tex2DFragShader = this.createShader(FRAGMENT_SHADER, fragSource);
            if (0 < ppgl.tex2DVertShader && 0 < ppgl.tex2DFragShader) {
                ppgl.tex2DShaderProgram = this.createProgram(ppgl.tex2DVertShader, ppgl.tex2DFragShader);
            }

            if (0 < ppgl.tex2DShaderProgram) {
                ppgl.tex2DVertLoc = this.getAttribLocation(ppgl.tex2DShaderProgram, "position");
                ppgl.tex2DTCoordLoc = this.getAttribLocation(ppgl.tex2DShaderProgram, "texCoord");
                ppgl.tex2DSamplerLoc = this.getUniformLocation(ppgl.tex2DShaderProgram, "texMap");
            }

            ppgl.loadedTex2DShader = true;
            ppgl.tex2DShaderContext = ppgl.glContext;
            this.genBuffers(1, this.intBuffer);
            ppgl.tex2DGeoVBO = this.intBuffer.get(0);
            this.bindBuffer(ARRAY_BUFFER, ppgl.tex2DGeoVBO);
            this.bufferData(ARRAY_BUFFER, 16 * SIZEOF_FLOAT, (Buffer)null, STATIC_DRAW);
        }

        if (this.texData == null) {
            this.texData = allocateDirectFloatBuffer(this.texCoords.length);
        }

        return ppgl;
    }

    protected void drawTexture2D(int id, int texW, int texH, int viewX, int viewY, int viewW, int viewH, int viewF, int texX0, int texY0, int texX1, int texY1, int scrX0, int scrY0, int scrX1, int scrY1) {
        PGL ppgl = this.initTex2DShader();
        if (0 < ppgl.tex2DShaderProgram) {
            boolean depthTest = this.getDepthTest();
            this.disable(DEPTH_TEST);
            boolean depthMask = this.getDepthWriteMask();
            this.depthMask(false);
            this.viewBuffer.rewind();
            this.getIntegerv(VIEWPORT, this.viewBuffer);
            this.viewportImpl(viewF * viewX, viewF * viewY, viewF * viewW, viewF * viewH);
            this.useProgram(ppgl.tex2DShaderProgram);
            this.enableVertexAttribArray(ppgl.tex2DVertLoc);
            this.enableVertexAttribArray(ppgl.tex2DTCoordLoc);
            this.texCoords[0] = 2.0F * (float)scrX0 / (float)viewW - 1.0F;
            this.texCoords[1] = 2.0F * (float)scrY0 / (float)viewH - 1.0F;
            this.texCoords[2] = (float)texX0 / (float)texW;
            this.texCoords[3] = (float)texY0 / (float)texH;
            this.texCoords[4] = 2.0F * (float)scrX1 / (float)viewW - 1.0F;
            this.texCoords[5] = 2.0F * (float)scrY0 / (float)viewH - 1.0F;
            this.texCoords[6] = (float)texX1 / (float)texW;
            this.texCoords[7] = (float)texY0 / (float)texH;
            this.texCoords[8] = 2.0F * (float)scrX0 / (float)viewW - 1.0F;
            this.texCoords[9] = 2.0F * (float)scrY1 / (float)viewH - 1.0F;
            this.texCoords[10] = (float)texX0 / (float)texW;
            this.texCoords[11] = (float)texY1 / (float)texH;
            this.texCoords[12] = 2.0F * (float)scrX1 / (float)viewW - 1.0F;
            this.texCoords[13] = 2.0F * (float)scrY1 / (float)viewH - 1.0F;
            this.texCoords[14] = (float)texX1 / (float)texW;
            this.texCoords[15] = (float)texY1 / (float)texH;
            this.texData.rewind();
            this.texData.put(this.texCoords);
            this.activeTexture(TEXTURE0);
            boolean enabledTex = false;
            if (!this.texturingIsEnabled(TEXTURE_2D)) {
                this.enableTexturing(TEXTURE_2D);
                enabledTex = true;
            }

            this.bindTexture(TEXTURE_2D, id);
            this.uniform1i(ppgl.tex2DSamplerLoc, 0);
            this.texData.position(0);
            this.bindBuffer(ARRAY_BUFFER, ppgl.tex2DGeoVBO);
            this.bufferData(ARRAY_BUFFER, 16 * SIZEOF_FLOAT, this.texData, STATIC_DRAW);
            this.vertexAttribPointer(ppgl.tex2DVertLoc, 2, FLOAT, false, 4 * SIZEOF_FLOAT, 0);
            this.vertexAttribPointer(ppgl.tex2DTCoordLoc, 2, FLOAT, false, 4 * SIZEOF_FLOAT, 2 * SIZEOF_FLOAT);
            this.drawArrays(TRIANGLE_STRIP, 0, 4);
            this.bindBuffer(ARRAY_BUFFER, 0);
            this.bindTexture(TEXTURE_2D, 0);
            if (enabledTex) {
                this.disableTexturing(TEXTURE_2D);
            }

            this.disableVertexAttribArray(ppgl.tex2DVertLoc);
            this.disableVertexAttribArray(ppgl.tex2DTCoordLoc);
            this.useProgram(0);
            if (depthTest) {
                this.enable(DEPTH_TEST);
            } else {
                this.disable(DEPTH_TEST);
            }

            this.depthMask(depthMask);
            this.viewportImpl(this.viewBuffer.get(0), this.viewBuffer.get(1), this.viewBuffer.get(2), this.viewBuffer.get(3));
        }

    }

    protected PGL initTexRectShader() {
        PGL ppgl = this.primaryPGL ? this : this.graphics.getPrimaryPGL();
        if (!ppgl.loadedTexRectShader || ppgl.texRectShaderContext != ppgl.glContext) {
            String[] preprocVertSrc = preprocessVertexSource(texVertShaderSource, this.getGLSLVersion());
            String vertSource = PApplet.join(preprocVertSrc, "\n");
            String[] preprocFragSrc = preprocessFragmentSource(texRectFragShaderSource, this.getGLSLVersion());
            String fragSource = PApplet.join(preprocFragSrc, "\n");
            ppgl.texRectVertShader = this.createShader(VERTEX_SHADER, vertSource);
            ppgl.texRectFragShader = this.createShader(FRAGMENT_SHADER, fragSource);
            if (0 < ppgl.texRectVertShader && 0 < ppgl.texRectFragShader) {
                ppgl.texRectShaderProgram = this.createProgram(ppgl.texRectVertShader, ppgl.texRectFragShader);
            }

            if (0 < ppgl.texRectShaderProgram) {
                ppgl.texRectVertLoc = this.getAttribLocation(ppgl.texRectShaderProgram, "position");
                ppgl.texRectTCoordLoc = this.getAttribLocation(ppgl.texRectShaderProgram, "texCoord");
                ppgl.texRectSamplerLoc = this.getUniformLocation(ppgl.texRectShaderProgram, "texMap");
            }

            ppgl.loadedTexRectShader = true;
            ppgl.texRectShaderContext = ppgl.glContext;
            this.genBuffers(1, this.intBuffer);
            ppgl.texRectGeoVBO = this.intBuffer.get(0);
            this.bindBuffer(ARRAY_BUFFER, ppgl.texRectGeoVBO);
            this.bufferData(ARRAY_BUFFER, 16 * SIZEOF_FLOAT, (Buffer)null, STATIC_DRAW);
        }

        return ppgl;
    }

    protected void drawTextureRect(int id, int texW, int texH, int viewX, int viewY, int viewW, int viewH, int viewF, int texX0, int texY0, int texX1, int texY1, int scrX0, int scrY0, int scrX1, int scrY1) {
        PGL ppgl = this.initTexRectShader();
        if (this.texData == null) {
            this.texData = allocateDirectFloatBuffer(this.texCoords.length);
        }

        if (0 < ppgl.texRectShaderProgram) {
            boolean depthTest = this.getDepthTest();
            this.disable(DEPTH_TEST);
            boolean depthMask = this.getDepthWriteMask();
            this.depthMask(false);
            this.viewBuffer.rewind();
            this.getIntegerv(VIEWPORT, this.viewBuffer);
            this.viewportImpl(viewF * viewX, viewF * viewY, viewF * viewW, viewF * viewH);
            this.useProgram(ppgl.texRectShaderProgram);
            this.enableVertexAttribArray(ppgl.texRectVertLoc);
            this.enableVertexAttribArray(ppgl.texRectTCoordLoc);
            this.texCoords[0] = 2.0F * (float)scrX0 / (float)viewW - 1.0F;
            this.texCoords[1] = 2.0F * (float)scrY0 / (float)viewH - 1.0F;
            this.texCoords[2] = (float)texX0;
            this.texCoords[3] = (float)texY0;
            this.texCoords[4] = 2.0F * (float)scrX1 / (float)viewW - 1.0F;
            this.texCoords[5] = 2.0F * (float)scrY0 / (float)viewH - 1.0F;
            this.texCoords[6] = (float)texX1;
            this.texCoords[7] = (float)texY0;
            this.texCoords[8] = 2.0F * (float)scrX0 / (float)viewW - 1.0F;
            this.texCoords[9] = 2.0F * (float)scrY1 / (float)viewH - 1.0F;
            this.texCoords[10] = (float)texX0;
            this.texCoords[11] = (float)texY1;
            this.texCoords[12] = 2.0F * (float)scrX1 / (float)viewW - 1.0F;
            this.texCoords[13] = 2.0F * (float)scrY1 / (float)viewH - 1.0F;
            this.texCoords[14] = (float)texX1;
            this.texCoords[15] = (float)texY1;
            this.texData.rewind();
            this.texData.put(this.texCoords);
            this.activeTexture(TEXTURE0);
            boolean enabledTex = false;
            if (!this.texturingIsEnabled(TEXTURE_RECTANGLE)) {
                this.enableTexturing(TEXTURE_RECTANGLE);
                enabledTex = true;
            }

            this.bindTexture(TEXTURE_RECTANGLE, id);
            this.uniform1i(ppgl.texRectSamplerLoc, 0);
            this.texData.position(0);
            this.bindBuffer(ARRAY_BUFFER, ppgl.texRectGeoVBO);
            this.bufferData(ARRAY_BUFFER, 16 * SIZEOF_FLOAT, this.texData, STATIC_DRAW);
            this.vertexAttribPointer(ppgl.texRectVertLoc, 2, FLOAT, false, 4 * SIZEOF_FLOAT, 0);
            this.vertexAttribPointer(ppgl.texRectTCoordLoc, 2, FLOAT, false, 4 * SIZEOF_FLOAT, 2 * SIZEOF_FLOAT);
            this.drawArrays(TRIANGLE_STRIP, 0, 4);
            this.bindBuffer(ARRAY_BUFFER, 0);
            this.bindTexture(TEXTURE_RECTANGLE, 0);
            if (enabledTex) {
                this.disableTexturing(TEXTURE_RECTANGLE);
            }

            this.disableVertexAttribArray(ppgl.texRectVertLoc);
            this.disableVertexAttribArray(ppgl.texRectTCoordLoc);
            this.useProgram(0);
            if (depthTest) {
                this.enable(DEPTH_TEST);
            } else {
                this.disable(DEPTH_TEST);
            }

            this.depthMask(depthMask);
            this.viewportImpl(this.viewBuffer.get(0), this.viewBuffer.get(1), this.viewBuffer.get(2), this.viewBuffer.get(3));
        }

    }

    protected int getColorValue(int scrX, int scrY) {
        if (this.colorBuffer == null) {
            this.colorBuffer = IntBuffer.allocate(1);
        }

        this.colorBuffer.rewind();
        this.readPixels(scrX, this.graphics.height - scrY - 1, 1, 1, RGBA, UNSIGNED_BYTE, this.colorBuffer);
        return this.colorBuffer.get();
    }

    protected float getDepthValue(int scrX, int scrY) {
        if (this.depthBuffer == null) {
            this.depthBuffer = FloatBuffer.allocate(1);
        }

        this.depthBuffer.rewind();
        this.readPixels(scrX, this.graphics.height - scrY - 1, 1, 1, DEPTH_COMPONENT, FLOAT, this.depthBuffer);
        return this.depthBuffer.get(0);
    }

    protected byte getStencilValue(int scrX, int scrY) {
        if (this.stencilBuffer == null) {
            this.stencilBuffer = ByteBuffer.allocate(1);
        }

        this.stencilBuffer.rewind();
        this.readPixels(scrX, this.graphics.height - scrY - 1, 1, 1, STENCIL_INDEX, UNSIGNED_BYTE, this.stencilBuffer);
        return this.stencilBuffer.get(0);
    }

    protected static boolean isPowerOfTwo(int val) {
        return (val & val - 1) == 0;
    }

    protected static int nextPowerOfTwo(int val) {
        int ret;
        for(ret = 1; ret < val; ret <<= 1) {
        }

        return ret;
    }

    protected static int nativeToJavaARGB(int color) {
        if (BIG_ENDIAN) {
            return color >>> 8 | color << 24;
        } else {
            int rb = color & 16711935;
            return color & -16711936 | rb << 16 | rb >> 16;
        }
    }

    protected static void nativeToJavaARGB(int[] pixels, int width, int height) {
        int index = 0;
        int yindex = (height - 1) * width;

        int x;
        int pixi;
        int rbi;
        for(x = 0; x < height / 2; ++x) {
            for(pixi = 0; pixi < width; ++pixi) {
                rbi = pixels[yindex];
                int pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = rbi >>> 8 | rbi << 24;
                    pixels[yindex] = pixi >>> 8 | pixi << 24;
                } else {
                    int rbi = pixi & 16711935;
                    int rby = rbi & 16711935;
                    pixels[index] = rbi & -16711936 | rby << 16 | rby >> 16;
                    pixels[yindex] = pixi & -16711936 | rbi << 16 | rbi >> 16;
                }

                ++index;
                ++yindex;
            }

            yindex -= width * 2;
        }

        if (height % 2 == 1) {
            index = height / 2 * width;

            for(x = 0; x < width; ++x) {
                pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = pixi >>> 8 | pixi << 24;
                } else {
                    rbi = pixi & 16711935;
                    pixels[index] = pixi & -16711936 | rbi << 16 | rbi >> 16;
                }

                ++index;
            }
        }

    }

    protected static int nativeToJavaRGB(int color) {
        if (BIG_ENDIAN) {
            return color >>> 8 | -16777216;
        } else {
            int rb = color & 16711935;
            return -16777216 | rb << 16 | color & '\uff00' | rb >> 16;
        }
    }

    protected static void nativeToJavaRGB(int[] pixels, int width, int height) {
        int index = 0;
        int yindex = (height - 1) * width;

        int x;
        int pixi;
        int rbi;
        for(x = 0; x < height / 2; ++x) {
            for(pixi = 0; pixi < width; ++pixi) {
                rbi = pixels[yindex];
                int pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = rbi >>> 8 | -16777216;
                    pixels[yindex] = pixi >>> 8 | -16777216;
                } else {
                    int rbi = pixi & 16711935;
                    int rby = rbi & 16711935;
                    pixels[index] = -16777216 | rby << 16 | rbi & '\uff00' | rby >> 16;
                    pixels[yindex] = -16777216 | rbi << 16 | pixi & '\uff00' | rbi >> 16;
                }

                ++index;
                ++yindex;
            }

            yindex -= width * 2;
        }

        if (height % 2 == 1) {
            index = height / 2 * width;

            for(x = 0; x < width; ++x) {
                pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = pixi >>> 8 | -16777216;
                } else {
                    rbi = pixi & 16711935;
                    pixels[index] = -16777216 | rbi << 16 | pixi & '\uff00' | rbi >> 16;
                }

                ++index;
            }
        }

    }

    protected static int javaToNativeARGB(int color) {
        if (BIG_ENDIAN) {
            return color >>> 24 | color << 8;
        } else {
            int rb = color & 16711935;
            return color & -16711936 | rb << 16 | rb >> 16;
        }
    }

    protected static void javaToNativeARGB(int[] pixels, int width, int height) {
        int index = 0;
        int yindex = (height - 1) * width;

        int x;
        int pixi;
        int rbi;
        for(x = 0; x < height / 2; ++x) {
            for(pixi = 0; pixi < width; ++pixi) {
                rbi = pixels[yindex];
                int pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = rbi >>> 24 | rbi << 8;
                    pixels[yindex] = pixi >>> 24 | pixi << 8;
                } else {
                    int rbi = pixi & 16711935;
                    int rby = rbi & 16711935;
                    pixels[index] = rbi & -16711936 | rby << 16 | rby >> 16;
                    pixels[yindex] = pixi & -16711936 | rbi << 16 | rbi >> 16;
                }

                ++index;
                ++yindex;
            }

            yindex -= width * 2;
        }

        if (height % 2 == 1) {
            index = height / 2 * width;

            for(x = 0; x < width; ++x) {
                pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = pixi >>> 24 | pixi << 8;
                } else {
                    rbi = pixi & 16711935;
                    pixels[index] = pixi & -16711936 | rbi << 16 | rbi >> 16;
                }

                ++index;
            }
        }

    }

    protected static int javaToNativeRGB(int color) {
        if (BIG_ENDIAN) {
            return 255 | color << 8;
        } else {
            int rb = color & 16711935;
            return -16777216 | rb << 16 | color & '\uff00' | rb >> 16;
        }
    }

    protected static void javaToNativeRGB(int[] pixels, int width, int height) {
        int index = 0;
        int yindex = (height - 1) * width;

        int x;
        int pixi;
        int rbi;
        for(x = 0; x < height / 2; ++x) {
            for(pixi = 0; pixi < width; ++pixi) {
                rbi = pixels[yindex];
                int pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = 255 | rbi << 8;
                    pixels[yindex] = 255 | pixi << 8;
                } else {
                    int rbi = pixi & 16711935;
                    int rby = rbi & 16711935;
                    pixels[index] = -16777216 | rby << 16 | rbi & '\uff00' | rby >> 16;
                    pixels[yindex] = -16777216 | rbi << 16 | pixi & '\uff00' | rbi >> 16;
                }

                ++index;
                ++yindex;
            }

            yindex -= width * 2;
        }

        if (height % 2 == 1) {
            index = height / 2 * width;

            for(x = 0; x < width; ++x) {
                pixi = pixels[index];
                if (BIG_ENDIAN) {
                    pixels[index] = 255 | pixi << 8;
                } else {
                    rbi = pixi & 16711935;
                    pixels[index] = -16777216 | rbi << 16 | pixi & '\uff00' | rbi >> 16;
                }

                ++index;
            }
        }

    }

    protected static int qualityToSamples(int quality) {
        if (quality <= 1) {
            return 1;
        } else {
            int n = 2 * (quality / 2);
            return n;
        }
    }

    protected abstract int getGLSLVersion();

    protected String[] loadVertexShader(String filename) {
        return this.sketch.loadStrings(filename);
    }

    protected String[] loadFragmentShader(String filename) {
        return this.sketch.loadStrings(filename);
    }

    protected String[] loadFragmentShader(URL url) {
        try {
            return PApplet.loadStrings(url.openStream());
        } catch (IOException var3) {
            PGraphics.showException("Cannot load fragment shader " + url.getFile());
            return null;
        }
    }

    protected String[] loadVertexShader(URL url) {
        try {
            return PApplet.loadStrings(url.openStream());
        } catch (IOException var3) {
            PGraphics.showException("Cannot load vertex shader " + url.getFile());
            return null;
        }
    }

    protected String[] loadVertexShader(String filename, int version) {
        return this.loadVertexShader(filename);
    }

    protected String[] loadFragmentShader(String filename, int version) {
        return this.loadFragmentShader(filename);
    }

    protected String[] loadFragmentShader(URL url, int version) {
        return this.loadFragmentShader(url);
    }

    protected String[] loadVertexShader(URL url, int version) {
        return this.loadVertexShader(url);
    }

    protected static String[] preprocessFragmentSource(String[] fragSrc0, int version) {
        if (containsVersionDirective(fragSrc0)) {
            return fragSrc0;
        } else {
            String[] fragSrc;
            Pattern[] search;
            String[] replace;
            byte offset;
            if (version < 130) {
                search = new Pattern[0];
                replace = new String[0];
                offset = 1;
                fragSrc = preprocessShaderSource(fragSrc0, search, replace, offset);
                fragSrc[0] = "#version " + version;
            } else {
                search = new Pattern[]{Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?![0-9A-Z_a-z]|\\s*\\()", "varying|attribute")), Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?![0-9A-Z_a-z]|\\s*\\()", "texture")), Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?=\\s*\\()", "textureRect|texture2D|texture3D|textureCube")), Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?![0-9A-Z_a-z]|\\s*\\()", "gl_FragColor"))};
                replace = new String[]{"in", "texMap", "texture", "_fragColor"};
                offset = 2;
                fragSrc = preprocessShaderSource(fragSrc0, search, replace, offset);
                fragSrc[0] = "#version " + version;
                fragSrc[1] = "out vec4 _fragColor;";
            }

            return fragSrc;
        }
    }

    protected static String[] preprocessVertexSource(String[] vertSrc0, int version) {
        if (containsVersionDirective(vertSrc0)) {
            return vertSrc0;
        } else {
            String[] vertSrc;
            Pattern[] search;
            String[] replace;
            byte offset;
            if (version < 130) {
                search = new Pattern[0];
                replace = new String[0];
                offset = 1;
                vertSrc = preprocessShaderSource(vertSrc0, search, replace, offset);
                vertSrc[0] = "#version " + version;
            } else {
                search = new Pattern[]{Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?![0-9A-Z_a-z]|\\s*\\()", "varying")), Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?![0-9A-Z_a-z]|\\s*\\()", "attribute")), Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?![0-9A-Z_a-z]|\\s*\\()", "texture")), Pattern.compile(String.format("(?<![0-9A-Z_a-z])(%s)(?=\\s*\\()", "textureRect|texture2D|texture3D|textureCube"))};
                replace = new String[]{"out", "in", "texMap", "texture"};
                offset = 1;
                vertSrc = preprocessShaderSource(vertSrc0, search, replace, offset);
                vertSrc[0] = "#version " + version;
            }

            return vertSrc;
        }
    }

    protected static String[] preprocessShaderSource(String[] src0, Pattern[] search, String[] replace, int offset) {
        String[] src = new String[src0.length + offset];

        for(int i = 0; i < src0.length; ++i) {
            String line = src0[i];
            int versionIndex = line.indexOf("#version");
            if (versionIndex >= 0) {
                line = line.substring(0, versionIndex);
            }

            for(int j = 0; j < search.length; ++j) {
                line = search[j].matcher(line).replaceAll(replace[j]);
            }

            src[i + offset] = line;
        }

        return src;
    }

    protected static boolean containsVersionDirective(String[] shSrc) {
        for(int i = 0; i < shSrc.length; ++i) {
            String line = shSrc[i];
            int versionIndex = line.indexOf("#version");
            if (versionIndex >= 0) {
                int commentIndex = line.indexOf("//");
                if (commentIndex < 0 || versionIndex < commentIndex) {
                    return true;
                }
            }
        }

        return false;
    }

    protected int createShader(int shaderType, String source) {
        int shader = this.createShader(shaderType);
        if (shader != 0) {
            this.shaderSource(shader, source);
            this.compileShader(shader);
            if (!this.compiled(shader)) {
                System.err.println("Could not compile shader " + shaderType + ":");
                System.err.println(this.getShaderInfoLog(shader));
                this.deleteShader(shader);
                shader = 0;
            }
        }

        return shader;
    }

    protected int createProgram(int vertexShader, int fragmentShader) {
        int program = this.createProgram();
        if (program != 0) {
            this.attachShader(program, vertexShader);
            this.attachShader(program, fragmentShader);
            this.linkProgram(program);
            if (!this.linked(program)) {
                System.err.println("Could not link program: ");
                System.err.println(this.getProgramInfoLog(program));
                this.deleteProgram(program);
                program = 0;
            }
        }

        return program;
    }

    protected boolean compiled(int shader) {
        this.intBuffer.rewind();
        this.getShaderiv(shader, COMPILE_STATUS, this.intBuffer);
        return this.intBuffer.get(0) != 0;
    }

    protected boolean linked(int program) {
        this.intBuffer.rewind();
        this.getProgramiv(program, LINK_STATUS, this.intBuffer);
        return this.intBuffer.get(0) != 0;
    }

    protected boolean validateFramebuffer() {
        int status = this.checkFramebufferStatus(FRAMEBUFFER);
        if (status == FRAMEBUFFER_COMPLETE) {
            return true;
        } else {
            if (status == FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
                System.err.println(String.format("Framebuffer error (%1$s), rendering will probably not work as expected Read http://wiki.processing.org/w/OpenGL_Issues for help.", "incomplete attachment"));
            } else if (status == FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
                System.err.println(String.format("Framebuffer error (%1$s), rendering will probably not work as expected Read http://wiki.processing.org/w/OpenGL_Issues for help.", "incomplete missing attachment"));
            } else if (status == FRAMEBUFFER_INCOMPLETE_DIMENSIONS) {
                System.err.println(String.format("Framebuffer error (%1$s), rendering will probably not work as expected Read http://wiki.processing.org/w/OpenGL_Issues for help.", "incomplete dimensions"));
            } else if (status == FRAMEBUFFER_INCOMPLETE_FORMATS) {
                System.err.println(String.format("Framebuffer error (%1$s), rendering will probably not work as expected Read http://wiki.processing.org/w/OpenGL_Issues for help.", "incomplete formats"));
            } else if (status == FRAMEBUFFER_UNSUPPORTED) {
                System.err.println(String.format("Framebuffer error (%1$s), rendering will probably not work as expected Read http://wiki.processing.org/w/OpenGL_Issues for help.", "framebuffer unsupported"));
            } else {
                System.err.println(String.format("Framebuffer error (%1$s), rendering will probably not work as expected Read http://wiki.processing.org/w/OpenGL_Issues for help.", "unknown error"));
            }

            return false;
        }
    }

    protected boolean isES() {
        return this.getString(VERSION).trim().toLowerCase().contains("opengl es");
    }

    protected int[] getGLVersion() {
        String version = this.getString(VERSION).trim().toLowerCase();
        String ES = "opengl es";
        int esPosition = version.indexOf(ES);
        if (esPosition >= 0) {
            version = version.substring(esPosition + ES.length()).trim();
        }

        int[] res = new int[]{0, 0, 0};
        String[] parts = version.split(" ");

        for(int i = 0; i < parts.length; ++i) {
            if (0 < parts[i].indexOf(".")) {
                String[] nums = parts[i].split("\\.");

                try {
                    res[0] = Integer.parseInt(nums[0]);
                } catch (NumberFormatException var11) {
                }

                if (1 < nums.length) {
                    try {
                        res[1] = Integer.parseInt(nums[1]);
                    } catch (NumberFormatException var10) {
                    }
                }

                if (2 < nums.length) {
                    try {
                        res[2] = Integer.parseInt(nums[2]);
                    } catch (NumberFormatException var9) {
                    }
                }
                break;
            }
        }

        return res;
    }

    protected boolean hasFBOs() {
        int major = this.getGLVersion()[0];
        if (major >= 2) {
            return true;
        } else {
            String ext = this.getString(EXTENSIONS);
            return ext.indexOf("_framebuffer_object") != -1 && ext.indexOf("_vertex_shader") != -1 && ext.indexOf("_shader_objects") != -1 && ext.indexOf("_shading_language") != -1;
        }
    }

    protected boolean hasShaders() {
        int major = this.getGLVersion()[0];
        if (major >= 2) {
            return true;
        } else {
            String ext = this.getString(EXTENSIONS);
            return ext.indexOf("_fragment_shader") != -1 && ext.indexOf("_vertex_shader") != -1 && ext.indexOf("_shader_objects") != -1 && ext.indexOf("_shading_language") != -1;
        }
    }

    protected boolean hasNpotTexSupport() {
        int major = this.getGLVersion()[0];
        if (major < 3) {
            String ext = this.getString(EXTENSIONS);
            return -1 < ext.indexOf("_texture_non_power_of_two");
        } else {
            return true;
        }
    }

    protected boolean hasAutoMipmapGenSupport() {
        int major = this.getGLVersion()[0];
        if (major < 3) {
            String ext = this.getString(EXTENSIONS);
            return -1 < ext.indexOf("_generate_mipmap");
        } else {
            return true;
        }
    }

    protected boolean hasFboMultisampleSupport() {
        int major = this.getGLVersion()[0];
        if (major < 3) {
            String ext = this.getString(EXTENSIONS);
            return -1 < ext.indexOf("_framebuffer_multisample");
        } else {
            return true;
        }
    }

    protected boolean hasPackedDepthStencilSupport() {
        int major = this.getGLVersion()[0];
        if (major < 3) {
            String ext = this.getString(EXTENSIONS);
            return -1 < ext.indexOf("_packed_depth_stencil");
        } else {
            return true;
        }
    }

    protected boolean hasAnisoSamplingSupport() {
        int major = this.getGLVersion()[0];
        if (major < 3) {
            String ext = this.getString(EXTENSIONS);
            return -1 < ext.indexOf("_texture_filter_anisotropic");
        } else {
            return true;
        }
    }

    protected boolean hasSynchronization() {
        int[] version = this.getGLVersion();
        if (this.isES()) {
            return version[0] >= 3;
        } else {
            return version[0] > 3 || version[0] == 3 && version[1] >= 2;
        }
    }

    protected boolean hasPBOs() {
        int[] version = this.getGLVersion();
        if (this.isES()) {
            return version[0] >= 3;
        } else {
            return version[0] > 2 || version[0] == 2 && version[1] >= 1;
        }
    }

    protected boolean hasReadBuffer() {
        int[] version = this.getGLVersion();
        if (this.isES()) {
            return version[0] >= 3;
        } else {
            return version[0] >= 2;
        }
    }

    protected boolean hasDrawBuffer() {
        int[] version = this.getGLVersion();
        if (this.isES()) {
            return version[0] >= 3;
        } else {
            return version[0] >= 2;
        }
    }

    protected int maxSamples() {
        this.intBuffer.rewind();
        this.getIntegerv(MAX_SAMPLES, this.intBuffer);
        return this.intBuffer.get(0);
    }

    protected int getMaxTexUnits() {
        this.intBuffer.rewind();
        this.getIntegerv(MAX_TEXTURE_IMAGE_UNITS, this.intBuffer);
        return this.intBuffer.get(0);
    }

    protected static ByteBuffer allocateDirectByteBuffer(int size) {
        int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_BYTE;
        return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder());
    }

    protected static ByteBuffer allocateByteBuffer(int size) {
        return USE_DIRECT_BUFFERS ? allocateDirectByteBuffer(size) : ByteBuffer.allocate(size);
    }

    protected static ByteBuffer allocateByteBuffer(byte[] arr) {
        if (USE_DIRECT_BUFFERS) {
            ByteBuffer buf = allocateDirectByteBuffer(arr.length);
            buf.put(arr);
            buf.position(0);
            return buf;
        } else {
            return ByteBuffer.wrap(arr);
        }
    }

    protected static ByteBuffer updateByteBuffer(ByteBuffer buf, byte[] arr, boolean wrap) {
        if (USE_DIRECT_BUFFERS) {
            if (buf == null || buf.capacity() < arr.length) {
                buf = allocateDirectByteBuffer(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        } else if (wrap) {
            buf = ByteBuffer.wrap(arr);
        } else {
            if (buf == null || buf.capacity() < arr.length) {
                buf = ByteBuffer.allocate(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

        return buf;
    }

    protected static void updateByteBuffer(ByteBuffer buf, byte[] arr, int offset, int size) {
        if (USE_DIRECT_BUFFERS || buf.hasArray() && buf.array() != arr) {
            buf.position(offset);
            buf.put(arr, offset, size);
            buf.rewind();
        }

    }

    protected static void getByteArray(ByteBuffer buf, byte[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.get(arr);
            buf.rewind();
        }

    }

    protected static void putByteArray(ByteBuffer buf, byte[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

    }

    protected static void fillByteBuffer(ByteBuffer buf, int i0, int i1, byte val) {
        int n = i1 - i0;
        byte[] temp = new byte[n];
        Arrays.fill(temp, 0, n, val);
        buf.position(i0);
        buf.put(temp, 0, n);
        buf.rewind();
    }

    protected static ShortBuffer allocateDirectShortBuffer(int size) {
        int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_SHORT;
        return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder()).asShortBuffer();
    }

    protected static ShortBuffer allocateShortBuffer(int size) {
        return USE_DIRECT_BUFFERS ? allocateDirectShortBuffer(size) : ShortBuffer.allocate(size);
    }

    protected static ShortBuffer allocateShortBuffer(short[] arr) {
        if (USE_DIRECT_BUFFERS) {
            ShortBuffer buf = allocateDirectShortBuffer(arr.length);
            buf.put(arr);
            buf.position(0);
            return buf;
        } else {
            return ShortBuffer.wrap(arr);
        }
    }

    protected static ShortBuffer updateShortBuffer(ShortBuffer buf, short[] arr, boolean wrap) {
        if (USE_DIRECT_BUFFERS) {
            if (buf == null || buf.capacity() < arr.length) {
                buf = allocateDirectShortBuffer(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        } else if (wrap) {
            buf = ShortBuffer.wrap(arr);
        } else {
            if (buf == null || buf.capacity() < arr.length) {
                buf = ShortBuffer.allocate(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

        return buf;
    }

    protected static void updateShortBuffer(ShortBuffer buf, short[] arr, int offset, int size) {
        if (USE_DIRECT_BUFFERS || buf.hasArray() && buf.array() != arr) {
            buf.position(offset);
            buf.put(arr, offset, size);
            buf.rewind();
        }

    }

    protected static void getShortArray(ShortBuffer buf, short[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.get(arr);
            buf.rewind();
        }

    }

    protected static void putShortArray(ShortBuffer buf, short[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

    }

    protected static void fillShortBuffer(ShortBuffer buf, int i0, int i1, short val) {
        int n = i1 - i0;
        short[] temp = new short[n];
        Arrays.fill(temp, 0, n, val);
        buf.position(i0);
        buf.put(temp, 0, n);
        buf.rewind();
    }

    protected static IntBuffer allocateDirectIntBuffer(int size) {
        int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_INT;
        return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder()).asIntBuffer();
    }

    protected static IntBuffer allocateIntBuffer(int size) {
        return USE_DIRECT_BUFFERS ? allocateDirectIntBuffer(size) : IntBuffer.allocate(size);
    }

    protected static IntBuffer allocateIntBuffer(int[] arr) {
        if (USE_DIRECT_BUFFERS) {
            IntBuffer buf = allocateDirectIntBuffer(arr.length);
            buf.put(arr);
            buf.position(0);
            return buf;
        } else {
            return IntBuffer.wrap(arr);
        }
    }

    protected static IntBuffer updateIntBuffer(IntBuffer buf, int[] arr, boolean wrap) {
        if (USE_DIRECT_BUFFERS) {
            if (buf == null || buf.capacity() < arr.length) {
                buf = allocateDirectIntBuffer(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        } else if (wrap) {
            buf = IntBuffer.wrap(arr);
        } else {
            if (buf == null || buf.capacity() < arr.length) {
                buf = IntBuffer.allocate(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

        return buf;
    }

    protected static void updateIntBuffer(IntBuffer buf, int[] arr, int offset, int size) {
        if (USE_DIRECT_BUFFERS || buf.hasArray() && buf.array() != arr) {
            buf.position(offset);
            buf.put(arr, offset, size);
            buf.rewind();
        }

    }

    protected static void getIntArray(IntBuffer buf, int[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.get(arr);
            buf.rewind();
        }

    }

    protected static void putIntArray(IntBuffer buf, int[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

    }

    protected static void fillIntBuffer(IntBuffer buf, int i0, int i1, int val) {
        int n = i1 - i0;
        int[] temp = new int[n];
        Arrays.fill(temp, 0, n, val);
        buf.position(i0);
        buf.put(temp, 0, n);
        buf.rewind();
    }

    protected static FloatBuffer allocateDirectFloatBuffer(int size) {
        int bytes = PApplet.max(MIN_DIRECT_BUFFER_SIZE, size) * SIZEOF_FLOAT;
        return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    protected static FloatBuffer allocateFloatBuffer(int size) {
        return USE_DIRECT_BUFFERS ? allocateDirectFloatBuffer(size) : FloatBuffer.allocate(size);
    }

    protected static FloatBuffer allocateFloatBuffer(float[] arr) {
        if (USE_DIRECT_BUFFERS) {
            FloatBuffer buf = allocateDirectFloatBuffer(arr.length);
            buf.put(arr);
            buf.position(0);
            return buf;
        } else {
            return FloatBuffer.wrap(arr);
        }
    }

    protected static FloatBuffer updateFloatBuffer(FloatBuffer buf, float[] arr, boolean wrap) {
        if (USE_DIRECT_BUFFERS) {
            if (buf == null || buf.capacity() < arr.length) {
                buf = allocateDirectFloatBuffer(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        } else if (wrap) {
            buf = FloatBuffer.wrap(arr);
        } else {
            if (buf == null || buf.capacity() < arr.length) {
                buf = FloatBuffer.allocate(arr.length);
            }

            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

        return buf;
    }

    protected static void updateFloatBuffer(FloatBuffer buf, float[] arr, int offset, int size) {
        if (USE_DIRECT_BUFFERS || buf.hasArray() && buf.array() != arr) {
            buf.position(offset);
            buf.put(arr, offset, size);
            buf.rewind();
        }

    }

    protected static void getFloatArray(FloatBuffer buf, float[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.get(arr);
            buf.rewind();
        }

    }

    protected static void putFloatArray(FloatBuffer buf, float[] arr) {
        if (!buf.hasArray() || buf.array() != arr) {
            buf.position(0);
            buf.put(arr);
            buf.rewind();
        }

    }

    protected static void fillFloatBuffer(FloatBuffer buf, int i0, int i1, float val) {
        int n = i1 - i0;
        float[] temp = new float[n];
        Arrays.fill(temp, 0, n, val);
        buf.position(i0);
        buf.put(temp, 0, n);
        buf.rewind();
    }

    protected abstract int getFontAscent(Object var1);

    protected abstract int getFontDescent(Object var1);

    protected abstract int getTextWidth(Object var1, char[] var2, int var3, int var4);

    protected abstract Object getDerivedFont(Object var1, float var2);

    protected abstract PGL.Tessellator createTessellator(PGL.TessellatorCallback var1);

    protected String tessError(int err) {
        return "";
    }

    protected abstract PGL.FontOutline createFontOutline(char var1, Object var2);

    public abstract void flush();

    public abstract void finish();

    public abstract void hint(int var1, int var2);

    public abstract void enable(int var1);

    public abstract void disable(int var1);

    public abstract void getBooleanv(int var1, IntBuffer var2);

    public abstract void getIntegerv(int var1, IntBuffer var2);

    public abstract void getFloatv(int var1, FloatBuffer var2);

    public abstract boolean isEnabled(int var1);

    public abstract String getString(int var1);

    public abstract int getError();

    public abstract String errorString(int var1);

    public abstract void genBuffers(int var1, IntBuffer var2);

    public abstract void deleteBuffers(int var1, IntBuffer var2);

    public abstract void bindBuffer(int var1, int var2);

    public abstract void bufferData(int var1, int var2, Buffer var3, int var4);

    public abstract void bufferSubData(int var1, int var2, int var3, Buffer var4);

    public abstract void isBuffer(int var1);

    public abstract void getBufferParameteriv(int var1, int var2, IntBuffer var3);

    public abstract ByteBuffer mapBuffer(int var1, int var2);

    public abstract ByteBuffer mapBufferRange(int var1, int var2, int var3, int var4);

    public abstract void unmapBuffer(int var1);

    public abstract long fenceSync(int var1, int var2);

    public abstract void deleteSync(long var1);

    public abstract int clientWaitSync(long var1, int var3, long var4);

    public abstract void depthRangef(float var1, float var2);

    public abstract void viewport(int var1, int var2, int var3, int var4);

    protected abstract void viewportImpl(int var1, int var2, int var3, int var4);

    public void readPixels(int x, int y, int width, int height, int format, int type, Buffer buffer) {
        boolean multisampled = this.isMultisampled() || this.graphics.offscreenMultisample;
        boolean depthReadingEnabled = this.graphics.getHint(10);
        boolean depthRequested = format == STENCIL_INDEX || format == DEPTH_COMPONENT || format == DEPTH_STENCIL;
        if (multisampled && depthRequested && !depthReadingEnabled) {
            PGraphics.showWarning("Reading depth and stencil values from this multisampled buffer is not enabled. You can enable it by calling hint(ENABLE_DEPTH_READING) once. If your sketch becomes too slow, disable multisampling with noSmooth() instead.");
        } else {
            this.graphics.beginReadPixels();
            this.readPixelsImpl(x, y, width, height, format, type, buffer);
            this.graphics.endReadPixels();
        }
    }

    public void readPixels(int x, int y, int width, int height, int format, int type, long offset) {
        boolean multisampled = this.isMultisampled() || this.graphics.offscreenMultisample;
        boolean depthReadingEnabled = this.graphics.getHint(10);
        boolean depthRequested = format == STENCIL_INDEX || format == DEPTH_COMPONENT || format == DEPTH_STENCIL;
        if (multisampled && depthRequested && !depthReadingEnabled) {
            PGraphics.showWarning("Reading depth and stencil values from this multisampled buffer is not enabled. You can enable it by calling hint(ENABLE_DEPTH_READING) once. If your sketch becomes too slow, disable multisampling with noSmooth() instead.");
        } else {
            this.graphics.beginReadPixels();
            this.readPixelsImpl(x, y, width, height, format, type, offset);
            this.graphics.endReadPixels();
        }
    }

    protected abstract void readPixelsImpl(int var1, int var2, int var3, int var4, int var5, int var6, Buffer var7);

    protected abstract void readPixelsImpl(int var1, int var2, int var3, int var4, int var5, int var6, long var7);

    public abstract void vertexAttrib1f(int var1, float var2);

    public abstract void vertexAttrib2f(int var1, float var2, float var3);

    public abstract void vertexAttrib3f(int var1, float var2, float var3, float var4);

    public abstract void vertexAttrib4f(int var1, float var2, float var3, float var4, float var5);

    public abstract void vertexAttrib1fv(int var1, FloatBuffer var2);

    public abstract void vertexAttrib2fv(int var1, FloatBuffer var2);

    public abstract void vertexAttrib3fv(int var1, FloatBuffer var2);

    public abstract void vertexAttrib4fv(int var1, FloatBuffer var2);

    public abstract void vertexAttribPointer(int var1, int var2, int var3, boolean var4, int var5, int var6);

    public abstract void enableVertexAttribArray(int var1);

    public abstract void disableVertexAttribArray(int var1);

    public void drawArrays(int mode, int first, int count) {
        this.geomCount += count;
        this.drawArraysImpl(mode, first, count);
    }

    public abstract void drawArraysImpl(int var1, int var2, int var3);

    public void drawElements(int mode, int count, int type, int offset) {
        this.geomCount += count;
        this.drawElementsImpl(mode, count, type, offset);
    }

    public abstract void drawElementsImpl(int var1, int var2, int var3, int var4);

    public abstract void lineWidth(float var1);

    public abstract void frontFace(int var1);

    public abstract void cullFace(int var1);

    public abstract void polygonOffset(float var1, float var2);

    public abstract void pixelStorei(int var1, int var2);

    public abstract void texImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Buffer var9);

    public abstract void copyTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

    public abstract void texSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Buffer var9);

    public abstract void copyTexSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

    public abstract void compressedTexImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, Buffer var8);

    public abstract void compressedTexSubImage2D(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Buffer var9);

    public abstract void texParameteri(int var1, int var2, int var3);

    public abstract void texParameterf(int var1, int var2, float var3);

    public abstract void texParameteriv(int var1, int var2, IntBuffer var3);

    public abstract void texParameterfv(int var1, int var2, FloatBuffer var3);

    public abstract void generateMipmap(int var1);

    public abstract void genTextures(int var1, IntBuffer var2);

    public abstract void deleteTextures(int var1, IntBuffer var2);

    public abstract void getTexParameteriv(int var1, int var2, IntBuffer var3);

    public abstract void getTexParameterfv(int var1, int var2, FloatBuffer var3);

    public abstract boolean isTexture(int var1);

    public void activeTexture(int texture) {
        this.activeTexUnit = texture - TEXTURE0;
        this.activeTextureImpl(texture);
    }

    protected abstract void activeTextureImpl(int var1);

    public void bindTexture(int target, int texture) {
        this.bindTextureImpl(target, texture);
        if (this.boundTextures == null) {
            this.maxTexUnits = this.getMaxTexUnits();
            this.boundTextures = new int[this.maxTexUnits][2];
        }

        if (this.maxTexUnits <= this.activeTexUnit) {
            throw new RuntimeException("Number of texture units not supported by this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.");
        } else {
            if (target == TEXTURE_2D) {
                this.boundTextures[this.activeTexUnit][0] = texture;
            } else if (target == TEXTURE_RECTANGLE) {
                this.boundTextures[this.activeTexUnit][1] = texture;
            }

        }
    }

    protected abstract void bindTextureImpl(int var1, int var2);

    public abstract int createShader(int var1);

    public abstract void shaderSource(int var1, String var2);

    public abstract void compileShader(int var1);

    public abstract void releaseShaderCompiler();

    public abstract void deleteShader(int var1);

    public abstract void shaderBinary(int var1, IntBuffer var2, int var3, Buffer var4, int var5);

    public abstract int createProgram();

    public abstract void attachShader(int var1, int var2);

    public abstract void detachShader(int var1, int var2);

    public abstract void linkProgram(int var1);

    public abstract void useProgram(int var1);

    public abstract void deleteProgram(int var1);

    public abstract String getActiveAttrib(int var1, int var2, IntBuffer var3, IntBuffer var4);

    public abstract int getAttribLocation(int var1, String var2);

    public abstract void bindAttribLocation(int var1, int var2, String var3);

    public abstract int getUniformLocation(int var1, String var2);

    public abstract String getActiveUniform(int var1, int var2, IntBuffer var3, IntBuffer var4);

    public abstract void uniform1i(int var1, int var2);

    public abstract void uniform2i(int var1, int var2, int var3);

    public abstract void uniform3i(int var1, int var2, int var3, int var4);

    public abstract void uniform4i(int var1, int var2, int var3, int var4, int var5);

    public abstract void uniform1f(int var1, float var2);

    public abstract void uniform2f(int var1, float var2, float var3);

    public abstract void uniform3f(int var1, float var2, float var3, float var4);

    public abstract void uniform4f(int var1, float var2, float var3, float var4, float var5);

    public abstract void uniform1iv(int var1, int var2, IntBuffer var3);

    public abstract void uniform2iv(int var1, int var2, IntBuffer var3);

    public abstract void uniform3iv(int var1, int var2, IntBuffer var3);

    public abstract void uniform4iv(int var1, int var2, IntBuffer var3);

    public abstract void uniform1fv(int var1, int var2, FloatBuffer var3);

    public abstract void uniform2fv(int var1, int var2, FloatBuffer var3);

    public abstract void uniform3fv(int var1, int var2, FloatBuffer var3);

    public abstract void uniform4fv(int var1, int var2, FloatBuffer var3);

    public abstract void uniformMatrix2fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public abstract void uniformMatrix3fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public abstract void uniformMatrix4fv(int var1, int var2, boolean var3, FloatBuffer var4);

    public abstract void validateProgram(int var1);

    public abstract boolean isShader(int var1);

    public abstract void getShaderiv(int var1, int var2, IntBuffer var3);

    public abstract void getAttachedShaders(int var1, int var2, IntBuffer var3, IntBuffer var4);

    public abstract String getShaderInfoLog(int var1);

    public abstract String getShaderSource(int var1);

    public abstract void getShaderPrecisionFormat(int var1, int var2, IntBuffer var3, IntBuffer var4);

    public abstract void getVertexAttribfv(int var1, int var2, FloatBuffer var3);

    public abstract void getVertexAttribiv(int var1, int var2, IntBuffer var3);

    public abstract void getVertexAttribPointerv(int var1, int var2, ByteBuffer var3);

    public abstract void getUniformfv(int var1, int var2, FloatBuffer var3);

    public abstract void getUniformiv(int var1, int var2, IntBuffer var3);

    public abstract boolean isProgram(int var1);

    public abstract void getProgramiv(int var1, int var2, IntBuffer var3);

    public abstract String getProgramInfoLog(int var1);

    public abstract void scissor(int var1, int var2, int var3, int var4);

    public abstract void sampleCoverage(float var1, boolean var2);

    public abstract void stencilFunc(int var1, int var2, int var3);

    public abstract void stencilFuncSeparate(int var1, int var2, int var3, int var4);

    public abstract void stencilOp(int var1, int var2, int var3);

    public abstract void stencilOpSeparate(int var1, int var2, int var3, int var4);

    public abstract void depthFunc(int var1);

    public abstract void blendEquation(int var1);

    public abstract void blendEquationSeparate(int var1, int var2);

    public abstract void blendFunc(int var1, int var2);

    public abstract void blendFuncSeparate(int var1, int var2, int var3, int var4);

    public abstract void blendColor(float var1, float var2, float var3, float var4);

    public abstract void colorMask(boolean var1, boolean var2, boolean var3, boolean var4);

    public abstract void depthMask(boolean var1);

    public abstract void stencilMask(int var1);

    public abstract void stencilMaskSeparate(int var1, int var2);

    public abstract void clearColor(float var1, float var2, float var3, float var4);

    public abstract void clearDepth(float var1);

    public abstract void clearStencil(int var1);

    public abstract void clear(int var1);

    public void bindFramebuffer(int target, int framebuffer) {
        this.graphics.beginBindFramebuffer(target, framebuffer);
        this.bindFramebufferImpl(target, framebuffer);
        this.graphics.endBindFramebuffer(target, framebuffer);
    }

    protected abstract void bindFramebufferImpl(int var1, int var2);

    public abstract void deleteFramebuffers(int var1, IntBuffer var2);

    public abstract void genFramebuffers(int var1, IntBuffer var2);

    public abstract void bindRenderbuffer(int var1, int var2);

    public abstract void deleteRenderbuffers(int var1, IntBuffer var2);

    public abstract void genRenderbuffers(int var1, IntBuffer var2);

    public abstract void renderbufferStorage(int var1, int var2, int var3, int var4);

    public abstract void framebufferRenderbuffer(int var1, int var2, int var3, int var4);

    public abstract void framebufferTexture2D(int var1, int var2, int var3, int var4, int var5);

    public abstract int checkFramebufferStatus(int var1);

    public abstract boolean isFramebuffer(int var1);

    public abstract void getFramebufferAttachmentParameteriv(int var1, int var2, int var3, IntBuffer var4);

    public abstract boolean isRenderbuffer(int var1);

    public abstract void getRenderbufferParameteriv(int var1, int var2, IntBuffer var3);

    public abstract void blitFramebuffer(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

    public abstract void renderbufferStorageMultisample(int var1, int var2, int var3, int var4, int var5);

    public abstract void readBuffer(int var1);

    public abstract void drawBuffer(int var1);

    static {
        MAX_VERTEX_INDEX1 = MAX_VERTEX_INDEX + 1;
        FLUSH_VERTEX_COUNT = MAX_VERTEX_INDEX1;
        MIN_FONT_TEX_SIZE = 256;
        MAX_FONT_TEX_SIZE = 1024;
        MIN_CAPS_JOINS_WEIGHT = 2.0F;
        MAX_CAPS_JOINS_LENGTH = 5000;
        MIN_ARRAYCOPY_SIZE = 2;
        STROKE_DISPLACEMENT = 0.999F;
        SINGLE_BUFFERED = false;
        texVertShaderSource = new String[]{"attribute vec2 position;", "attribute vec2 texCoord;", "varying vec2 vertTexCoord;", "void main() {", "  gl_Position = vec4(position, 0, 1);", "  vertTexCoord = texCoord;", "}"};
        tex2DFragShaderSource = new String[]{"#ifdef GL_ES\nprecision mediump float;\nprecision mediump int;\n#endif\n", "uniform sampler2D texMap;", "varying vec2 vertTexCoord;", "void main() {", "  gl_FragColor = texture2D(texMap, vertTexCoord.st);", "}"};
        texRectFragShaderSource = new String[]{"#ifdef GL_ES\nprecision mediump float;\nprecision mediump int;\n#endif\n", "uniform sampler2DRect texMap;", "varying vec2 vertTexCoord;", "void main() {", "  gl_FragColor = texture2DRect(texMap, vertTexCoord.st);", "}"};
        SIZEOF_SHORT = 2;
        SIZEOF_INT = 4;
        SIZEOF_FLOAT = 4;
        SIZEOF_BYTE = 1;
        SIZEOF_INDEX = SIZEOF_SHORT;
        INDEX_TYPE = 5123;
        FLOAT_EPS = 1.4E-45F;
        float eps = 1.0F;

        do {
            eps /= 2.0F;
        } while((double)((float)(1.0D + (double)eps / 2.0D)) != 1.0D);

        FLOAT_EPS = eps;
        BIG_ENDIAN = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
        closeButtonPix = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, -1, 0, 0, 0, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, -1, 0, -1, -1, 0, 0, -1, -1, 0, -1, -1, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, -1, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, -1, 0, -1, -1, 0, 0, -1, -1, 0, -1, -1, 0, 0, 0, -1, -1, -1, 0, 0, 0, 0, 0, -1, -1, -1, 0, 0, 0, -1, -1, -1, 0, 0, 0, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0};
    }

    protected interface FontOutline {
        boolean isDone();

        int currentSegment(float[] var1);

        void next();
    }

    protected interface TessellatorCallback {
        void begin(int var1);

        void end();

        void vertex(Object var1);

        void combine(double[] var1, Object[] var2, float[] var3, Object[] var4);

        void error(int var1);
    }

    protected interface Tessellator {
        void setCallback(int var1);

        void setWindingRule(int var1);

        void setProperty(int var1, int var2);

        void beginPolygon();

        void beginPolygon(Object var1);

        void endPolygon();

        void beginContour();

        void endContour();

        void addVertex(double[] var1);

        void addVertex(double[] var1, int var2, Object var3);
    }
}

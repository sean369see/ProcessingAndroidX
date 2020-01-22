package processing.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.SurfaceView;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import processing.opengl.PGL.FontOutline;
import processing.opengl.PGL.TessellatorCallback;
import processing.opengl.tess.PGLU;
import processing.opengl.tess.PGLUtessellator;
import processing.opengl.tess.PGLUtessellatorCallbackAdapter;

public class PGLES extends PGL {
    public GL10 gl;
    public PGLU glu = new PGLU();
    public EGLContext context;
    public GLSurfaceView glview;
    public static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    public static final int EGL_OPENGL_ES2_BIT = 4;
    public static final int EGL_COVERAGE_BUFFERS_NV = 12512;
    public static final int EGL_COVERAGE_SAMPLES_NV = 12513;
    public static final int GL_COVERAGE_BUFFER_BIT_NV = 32768;
    public static boolean usingMultisampling;
    public static boolean usingCoverageMultisampling;
    public static int multisampleCount;

    public PGLES(PGraphicsOpenGL pg) {
        super(pg);
    }

    public GLSurfaceView getNative() {
        return this.glview;
    }

    public void queueEvent(Runnable runnable) {
        if (this.glview != null) {
            this.glview.queueEvent(runnable);
        }

    }

    protected void initSurface(int antialias) {
        SurfaceView surf = this.sketch.getSurface().getSurfaceView();
        if (surf != null) {
            this.glview = (GLSurfaceView)surf;
        }

        this.reqNumSamples = qualityToSamples(antialias);
        this.registerListeners();
    }

    protected void reinitSurface() {
    }

    protected void registerListeners() {
    }

    protected int getDepthBits() {
        this.intBuffer.rewind();
        this.getIntegerv(DEPTH_BITS, this.intBuffer);
        return this.intBuffer.get(0);
    }

    protected int getStencilBits() {
        this.intBuffer.rewind();
        this.getIntegerv(STENCIL_BITS, this.intBuffer);
        return this.intBuffer.get(0);
    }

    protected int getDefaultDrawBuffer() {
        return this.fboLayerEnabled ? COLOR_ATTACHMENT0 : FRONT;
    }

    protected int getDefaultReadBuffer() {
        return this.fboLayerEnabled ? COLOR_ATTACHMENT0 : FRONT;
    }

    public void init(GL10 igl) {
        this.gl = igl;
        this.context = ((EGL10)EGLContext.getEGL()).eglGetCurrentContext();
        this.glContext = this.context.hashCode();
        this.glThread = Thread.currentThread();
        if (!this.hasFBOs()) {
            throw new RuntimeException("Framebuffer objects are not supported by this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.");
        } else if (!this.hasShaders()) {
            throw new RuntimeException("GLSL shaders are not supported by this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.");
        }
    }

    protected float getPixelScale() {
        return 1.0F;
    }

    protected void getGL(PGL pgl) {
        PGLES pgles = (PGLES)pgl;
        this.gl = pgles.gl;
        this.setThread(pgles.glThread);
    }

    public void getGL(GL10 igl) {
        this.gl = igl;
        this.glThread = Thread.currentThread();
    }

    protected boolean canDraw() {
        return true;
    }

    protected void requestFocus() {
    }

    protected void requestDraw() {
    }

    protected void swapBuffers() {
    }

    protected int getGLSLVersion() {
        return 100;
    }

    protected void initFBOLayer() {
        if (0 < this.sketch.frameCount) {
            IntBuffer buf = allocateDirectIntBuffer(this.fboWidth * this.fboHeight);
            if (this.hasReadBuffer()) {
                this.readBuffer(BACK);
            }

            this.readPixelsImpl(0, 0, this.fboWidth, this.fboHeight, RGBA, UNSIGNED_BYTE, buf);
            this.bindTexture(TEXTURE_2D, this.glColorTex.get(this.frontTex));
            this.texSubImage2D(TEXTURE_2D, 0, 0, 0, this.fboWidth, this.fboHeight, RGBA, UNSIGNED_BYTE, buf);
            this.bindTexture(TEXTURE_2D, this.glColorTex.get(this.backTex));
            this.texSubImage2D(TEXTURE_2D, 0, 0, 0, this.fboWidth, this.fboHeight, RGBA, UNSIGNED_BYTE, buf);
            this.bindTexture(TEXTURE_2D, 0);
            this.bindFramebufferImpl(FRAMEBUFFER, 0);
        }

    }

    protected void clearFrontColorBuffer() {
        this.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_2D, this.glColorTex.get(this.frontTex), 0);
        this.clear(COLOR_BUFFER_BIT);
        this.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_2D, this.glColorTex.get(this.backTex), 0);
    }

    protected PGLES.Tessellator createTessellator(TessellatorCallback callback) {
        return new PGLES.Tessellator(callback);
    }

    protected String tessError(int err) {
        return PGLU.gluErrorString(err);
    }

    protected FontOutline createFontOutline(char ch, Object font) {
        return null;
    }

    public void flush() {
        GLES20.glFlush();
    }

    public void finish() {
        GLES20.glFinish();
    }

    public void hint(int target, int hint) {
        GLES20.glHint(target, hint);
    }

    public void enable(int value) {
        if (-1 < value) {
            GLES20.glEnable(value);
        }

    }

    public void disable(int value) {
        if (-1 < value) {
            GLES20.glDisable(value);
        }

    }

    public void getBooleanv(int name, IntBuffer values) {
        if (-1 < name) {
            GLES20.glGetBooleanv(name, values);
        } else {
            fillIntBuffer(values, 0, values.capacity(), 0);
        }

    }

    public void getIntegerv(int value, IntBuffer data) {
        if (-1 < value) {
            GLES20.glGetIntegerv(value, data);
        } else {
            fillIntBuffer(data, 0, data.capacity() - 1, 0);
        }

    }

    public void getFloatv(int value, FloatBuffer data) {
        if (-1 < value) {
            GLES20.glGetFloatv(value, data);
        } else {
            fillFloatBuffer(data, 0, data.capacity() - 1, 0.0F);
        }

    }

    public boolean isEnabled(int value) {
        return GLES20.glIsEnabled(value);
    }

    public String getString(int name) {
        return GLES20.glGetString(name);
    }

    public int getError() {
        return GLES20.glGetError();
    }

    public String errorString(int err) {
        return GLU.gluErrorString(err);
    }

    public void genBuffers(int n, IntBuffer buffers) {
        GLES20.glGenBuffers(n, buffers);
    }

    public void deleteBuffers(int n, IntBuffer buffers) {
        GLES20.glDeleteBuffers(n, buffers);
    }

    public void bindBuffer(int target, int buffer) {
        GLES20.glBindBuffer(target, buffer);
    }

    public void bufferData(int target, int size, Buffer data, int usage) {
        GLES20.glBufferData(target, size, data, usage);
    }

    public void bufferSubData(int target, int offset, int size, Buffer data) {
        GLES20.glBufferSubData(target, offset, size, data);
    }

    public void isBuffer(int buffer) {
        GLES20.glIsBuffer(buffer);
    }

    public void getBufferParameteriv(int target, int value, IntBuffer data) {
        GLES20.glGetBufferParameteriv(target, value, data);
    }

    public ByteBuffer mapBuffer(int target, int access) {
        throw new RuntimeException(String.format("GL function %1$s is not available on this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.", "glMapBuffer"));
    }

    public ByteBuffer mapBufferRange(int target, int offset, int length, int access) {
        throw new RuntimeException(String.format("GL function %1$s is not available on this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.", "glMapBufferRange"));
    }

    public void unmapBuffer(int target) {
        throw new RuntimeException(String.format("GL function %1$s is not available on this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.", "glUnmapBuffer"));
    }

    public void depthRangef(float n, float f) {
        GLES20.glDepthRangef(n, f);
    }

    public void viewport(int x, int y, int w, int h) {
        float scale = this.getPixelScale();
        this.viewportImpl((int)scale * x, (int)(scale * (float)y), (int)(scale * (float)w), (int)(scale * (float)h));
    }

    protected void viewportImpl(int x, int y, int w, int h) {
        GLES20.glViewport(x, y, w, h);
    }

    public void readPixelsImpl(int x, int y, int width, int height, int format, int type, Buffer buffer) {
        GLES20.glReadPixels(x, y, width, height, format, type, buffer);
    }

    protected void readPixelsImpl(int x, int y, int width, int height, int format, int type, long offset) {
    }

    public void vertexAttrib1f(int index, float value) {
        GLES20.glVertexAttrib1f(index, value);
    }

    public void vertexAttrib2f(int index, float value0, float value1) {
        GLES20.glVertexAttrib2f(index, value0, value1);
    }

    public void vertexAttrib3f(int index, float value0, float value1, float value2) {
        GLES20.glVertexAttrib3f(index, value0, value1, value2);
    }

    public void vertexAttrib4f(int index, float value0, float value1, float value2, float value3) {
        GLES20.glVertexAttrib4f(index, value0, value1, value2, value3);
    }

    public void vertexAttrib1fv(int index, FloatBuffer values) {
        GLES20.glVertexAttrib1fv(index, values);
    }

    public void vertexAttrib2fv(int index, FloatBuffer values) {
        GLES20.glVertexAttrib2fv(index, values);
    }

    public void vertexAttrib3fv(int index, FloatBuffer values) {
        GLES20.glVertexAttrib3fv(index, values);
    }

    public void vertexAttrib4fv(int index, FloatBuffer values) {
        GLES20.glVertexAttrib4fv(index, values);
    }

    public void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, int offset) {
        GLES20.glVertexAttribPointer(index, size, type, normalized, stride, offset);
    }

    public void enableVertexAttribArray(int index) {
        GLES20.glEnableVertexAttribArray(index);
    }

    public void disableVertexAttribArray(int index) {
        GLES20.glDisableVertexAttribArray(index);
    }

    public void drawArraysImpl(int mode, int first, int count) {
        GLES20.glDrawArrays(mode, first, count);
    }

    public void drawElementsImpl(int mode, int count, int type, int offset) {
        GLES20.glDrawElements(mode, count, type, offset);
    }

    public void lineWidth(float width) {
        GLES20.glLineWidth(width);
    }

    public void frontFace(int dir) {
        GLES20.glFrontFace(dir);
    }

    public void cullFace(int mode) {
        GLES20.glCullFace(mode);
    }

    public void polygonOffset(float factor, float units) {
        GLES20.glPolygonOffset(factor, units);
    }

    public void pixelStorei(int pname, int param) {
        GLES20.glPixelStorei(pname, param);
    }

    public void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, Buffer data) {
        GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, data);
    }

    public void copyTexImage2D(int target, int level, int internalFormat, int x, int y, int width, int height, int border) {
        GLES20.glCopyTexImage2D(target, level, internalFormat, x, y, width, height, border);
    }

    public void texSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int type, Buffer data) {
        GLES20.glTexSubImage2D(target, level, xOffset, yOffset, width, height, format, type, data);
    }

    public void copyTexSubImage2D(int target, int level, int xOffset, int yOffset, int x, int y, int width, int height) {
        GLES20.glCopyTexSubImage2D(target, level, x, y, xOffset, yOffset, width, height);
    }

    public void compressedTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int imageSize, Buffer data) {
        GLES20.glCompressedTexImage2D(target, level, internalFormat, width, height, border, imageSize, data);
    }

    public void compressedTexSubImage2D(int target, int level, int xOffset, int yOffset, int width, int height, int format, int imageSize, Buffer data) {
        GLES20.glCompressedTexSubImage2D(target, level, xOffset, yOffset, width, height, format, imageSize, data);
    }

    public void texParameteri(int target, int pname, int param) {
        GLES20.glTexParameteri(target, pname, param);
    }

    public void texParameterf(int target, int pname, float param) {
        GLES20.glTexParameterf(target, pname, param);
    }

    public void texParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glTexParameteriv(target, pname, params);
    }

    public void texParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glTexParameterfv(target, pname, params);
    }

    public void generateMipmap(int target) {
        GLES20.glGenerateMipmap(target);
    }

    public void genTextures(int n, IntBuffer textures) {
        GLES20.glGenTextures(n, textures);
    }

    public void deleteTextures(int n, IntBuffer textures) {
        GLES20.glDeleteTextures(n, textures);
    }

    public void getTexParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetTexParameteriv(target, pname, params);
    }

    public void getTexParameterfv(int target, int pname, FloatBuffer params) {
        GLES20.glGetTexParameterfv(target, pname, params);
    }

    public boolean isTexture(int texture) {
        return GLES20.glIsTexture(texture);
    }

    protected void activeTextureImpl(int texture) {
        GLES20.glActiveTexture(texture);
    }

    protected void bindTextureImpl(int target, int texture) {
        GLES20.glBindTexture(target, texture);
    }

    public int createShader(int type) {
        return GLES20.glCreateShader(type);
    }

    public void shaderSource(int shader, String source) {
        GLES20.glShaderSource(shader, source);
    }

    public void compileShader(int shader) {
        GLES20.glCompileShader(shader);
    }

    public void releaseShaderCompiler() {
        GLES20.glReleaseShaderCompiler();
    }

    public void deleteShader(int shader) {
        GLES20.glDeleteShader(shader);
    }

    public void shaderBinary(int count, IntBuffer shaders, int binaryFormat, Buffer binary, int length) {
        GLES20.glShaderBinary(count, shaders, binaryFormat, binary, length);
    }

    public int createProgram() {
        return GLES20.glCreateProgram();
    }

    public void attachShader(int program, int shader) {
        GLES20.glAttachShader(program, shader);
    }

    public void detachShader(int program, int shader) {
        GLES20.glDetachShader(program, shader);
    }

    public void linkProgram(int program) {
        GLES20.glLinkProgram(program);
    }

    public void useProgram(int program) {
        GLES20.glUseProgram(program);
    }

    public void deleteProgram(int program) {
        GLES20.glDeleteProgram(program);
    }

    public String getActiveAttrib(int program, int index, IntBuffer size, IntBuffer type) {
        int[] tmp = new int[]{0, 0, 0};
        byte[] namebuf = new byte[1024];
        GLES20.glGetActiveAttrib(program, index, 1024, tmp, 0, tmp, 1, tmp, 2, namebuf, 0);
        size.put(tmp[1]);
        type.put(tmp[2]);
        String name = new String(namebuf, 0, tmp[0]);
        return name;
    }

    public int getAttribLocation(int program, String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    public void bindAttribLocation(int program, int index, String name) {
        GLES20.glBindAttribLocation(program, index, name);
    }

    public int getUniformLocation(int program, String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    public String getActiveUniform(int program, int index, IntBuffer size, IntBuffer type) {
        int[] tmp = new int[]{0, 0, 0};
        byte[] namebuf = new byte[1024];
        GLES20.glGetActiveUniform(program, index, 1024, tmp, 0, tmp, 1, tmp, 2, namebuf, 0);
        size.put(tmp[1]);
        type.put(tmp[2]);
        String name = new String(namebuf, 0, tmp[0]);
        return name;
    }

    public void uniform1i(int location, int value) {
        GLES20.glUniform1i(location, value);
    }

    public void uniform2i(int location, int value0, int value1) {
        GLES20.glUniform2i(location, value0, value1);
    }

    public void uniform3i(int location, int value0, int value1, int value2) {
        GLES20.glUniform3i(location, value0, value1, value2);
    }

    public void uniform4i(int location, int value0, int value1, int value2, int value3) {
        GLES20.glUniform4i(location, value0, value1, value2, value3);
    }

    public void uniform1f(int location, float value) {
        GLES20.glUniform1f(location, value);
    }

    public void uniform2f(int location, float value0, float value1) {
        GLES20.glUniform2f(location, value0, value1);
    }

    public void uniform3f(int location, float value0, float value1, float value2) {
        GLES20.glUniform3f(location, value0, value1, value2);
    }

    public void uniform4f(int location, float value0, float value1, float value2, float value3) {
        GLES20.glUniform4f(location, value0, value1, value2, value3);
    }

    public void uniform1iv(int location, int count, IntBuffer v) {
        GLES20.glUniform1iv(location, count, v);
    }

    public void uniform2iv(int location, int count, IntBuffer v) {
        GLES20.glUniform2iv(location, count, v);
    }

    public void uniform3iv(int location, int count, IntBuffer v) {
        GLES20.glUniform3iv(location, count, v);
    }

    public void uniform4iv(int location, int count, IntBuffer v) {
        GLES20.glUniform4iv(location, count, v);
    }

    public void uniform1fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform1fv(location, count, v);
    }

    public void uniform2fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform2fv(location, count, v);
    }

    public void uniform3fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform3fv(location, count, v);
    }

    public void uniform4fv(int location, int count, FloatBuffer v) {
        GLES20.glUniform4fv(location, count, v);
    }

    public void uniformMatrix2fv(int location, int count, boolean transpose, FloatBuffer mat) {
        GLES20.glUniformMatrix2fv(location, count, transpose, mat);
    }

    public void uniformMatrix3fv(int location, int count, boolean transpose, FloatBuffer mat) {
        GLES20.glUniformMatrix3fv(location, count, transpose, mat);
    }

    public void uniformMatrix4fv(int location, int count, boolean transpose, FloatBuffer mat) {
        GLES20.glUniformMatrix4fv(location, count, transpose, mat);
    }

    public void validateProgram(int program) {
        GLES20.glValidateProgram(program);
    }

    public boolean isShader(int shader) {
        return GLES20.glIsShader(shader);
    }

    public void getShaderiv(int shader, int pname, IntBuffer params) {
        GLES20.glGetShaderiv(shader, pname, params);
    }

    public void getAttachedShaders(int program, int maxCount, IntBuffer count, IntBuffer shaders) {
        GLES20.glGetAttachedShaders(program, maxCount, count, shaders);
    }

    public String getShaderInfoLog(int shader) {
        return GLES20.glGetShaderInfoLog(shader);
    }

    public String getShaderSource(int shader) {
        int[] len = new int[]{0};
        byte[] buf = new byte[1024];
        GLES20.glGetShaderSource(shader, 1024, len, 0, buf, 0);
        return new String(buf, 0, len[0]);
    }

    public void getShaderPrecisionFormat(int shaderType, int precisionType, IntBuffer range, IntBuffer precision) {
        GLES20.glGetShaderPrecisionFormat(shaderType, precisionType, range, precision);
    }

    public void getVertexAttribfv(int index, int pname, FloatBuffer params) {
        GLES20.glGetVertexAttribfv(index, pname, params);
    }

    public void getVertexAttribiv(int index, int pname, IntBuffer params) {
        GLES20.glGetVertexAttribiv(index, pname, params);
    }

    public void getVertexAttribPointerv(int index, int pname, ByteBuffer data) {
        throw new RuntimeException(String.format("GL function %1$s is not available on this hardware (or driver) Read http://wiki.processing.org/w/OpenGL_Issues for help.", "glGetVertexAttribPointerv()"));
    }

    public void getUniformfv(int program, int location, FloatBuffer params) {
        GLES20.glGetUniformfv(program, location, params);
    }

    public void getUniformiv(int program, int location, IntBuffer params) {
        GLES20.glGetUniformiv(program, location, params);
    }

    public boolean isProgram(int program) {
        return GLES20.glIsProgram(program);
    }

    public void getProgramiv(int program, int pname, IntBuffer params) {
        GLES20.glGetProgramiv(program, pname, params);
    }

    public String getProgramInfoLog(int program) {
        return GLES20.glGetProgramInfoLog(program);
    }

    public void scissor(int x, int y, int w, int h) {
        GLES20.glScissor(x, y, w, h);
    }

    public void sampleCoverage(float value, boolean invert) {
        GLES20.glSampleCoverage(value, invert);
    }

    public void stencilFunc(int func, int ref, int mask) {
        GLES20.glStencilFunc(func, ref, mask);
    }

    public void stencilFuncSeparate(int face, int func, int ref, int mask) {
        GLES20.glStencilFuncSeparate(face, func, ref, mask);
    }

    public void stencilOp(int sfail, int dpfail, int dppass) {
        GLES20.glStencilOp(sfail, dpfail, dppass);
    }

    public void stencilOpSeparate(int face, int sfail, int dpfail, int dppass) {
        GLES20.glStencilOpSeparate(face, sfail, dpfail, dppass);
    }

    public void depthFunc(int func) {
        GLES20.glDepthFunc(func);
    }

    public void blendEquation(int mode) {
        GLES20.glBlendEquation(mode);
    }

    public void blendEquationSeparate(int modeRGB, int modeAlpha) {
        GLES20.glBlendEquationSeparate(modeRGB, modeAlpha);
    }

    public void blendFunc(int src, int dst) {
        GLES20.glBlendFunc(src, dst);
    }

    public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
        GLES20.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
    }

    public void blendColor(float red, float green, float blue, float alpha) {
        GLES20.glBlendColor(red, green, blue, alpha);
    }

    public void colorMask(boolean r, boolean g, boolean b, boolean a) {
        GLES20.glColorMask(r, g, b, a);
    }

    public void depthMask(boolean mask) {
        GLES20.glDepthMask(mask);
    }

    public void stencilMask(int mask) {
        GLES20.glStencilMask(mask);
    }

    public void stencilMaskSeparate(int face, int mask) {
        GLES20.glStencilMaskSeparate(face, mask);
    }

    public void clear(int buf) {
        if (usingMultisampling && usingCoverageMultisampling) {
            buf |= 32768;
        }

        GLES20.glClear(buf);
    }

    public void clearColor(float r, float g, float b, float a) {
        GLES20.glClearColor(r, g, b, a);
    }

    public void clearDepth(float d) {
        GLES20.glClearDepthf(d);
    }

    public void clearStencil(int s) {
        GLES20.glClearStencil(s);
    }

    protected void bindFramebufferImpl(int target, int framebuffer) {
        GLES20.glBindFramebuffer(target, framebuffer);
    }

    public void deleteFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glDeleteFramebuffers(n, framebuffers);
    }

    public void genFramebuffers(int n, IntBuffer framebuffers) {
        GLES20.glGenFramebuffers(n, framebuffers);
    }

    public void bindRenderbuffer(int target, int renderbuffer) {
        GLES20.glBindRenderbuffer(target, renderbuffer);
    }

    public void deleteRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glDeleteRenderbuffers(n, renderbuffers);
    }

    public void genRenderbuffers(int n, IntBuffer renderbuffers) {
        GLES20.glGenRenderbuffers(n, renderbuffers);
    }

    public void renderbufferStorage(int target, int internalFormat, int width, int height) {
        GLES20.glRenderbufferStorage(target, internalFormat, width, height);
    }

    public void framebufferRenderbuffer(int target, int attachment, int rendbuferfTarget, int renderbuffer) {
        GLES20.glFramebufferRenderbuffer(target, attachment, rendbuferfTarget, renderbuffer);
    }

    public void framebufferTexture2D(int target, int attachment, int texTarget, int texture, int level) {
        GLES20.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
    }

    public int checkFramebufferStatus(int target) {
        return GLES20.glCheckFramebufferStatus(target);
    }

    public boolean isFramebuffer(int framebuffer) {
        return GLES20.glIsFramebuffer(framebuffer);
    }

    public void getFramebufferAttachmentParameteriv(int target, int attachment, int pname, IntBuffer params) {
        GLES20.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params);
    }

    public boolean isRenderbuffer(int renderbuffer) {
        return GLES20.glIsRenderbuffer(renderbuffer);
    }

    public void getRenderbufferParameteriv(int target, int pname, IntBuffer params) {
        GLES20.glGetRenderbufferParameteriv(target, pname, params);
    }

    public void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
    }

    public void renderbufferStorageMultisample(int target, int samples, int format, int width, int height) {
    }

    public void readBuffer(int buf) {
    }

    public void drawBuffer(int buf) {
    }

    protected int getFontAscent(Object font) {
        return 0;
    }

    protected int getFontDescent(Object font) {
        return 0;
    }

    protected int getTextWidth(Object font, char[] buffer, int start, int stop) {
        return 0;
    }

    protected Object getDerivedFont(Object font, float size) {
        return null;
    }

    public long fenceSync(int condition, int flags) {
        return 0L;
    }

    public void deleteSync(long sync) {
    }

    public int clientWaitSync(long sync, int flags, long timeout) {
        return 0;
    }

    static {
        SINGLE_BUFFERED = true;
        MIN_DIRECT_BUFFER_SIZE = 1;
        INDEX_TYPE = 5123;
        MIPMAPS_ENABLED = false;
        DEFAULT_IN_VERTICES = 16;
        DEFAULT_IN_EDGES = 32;
        DEFAULT_IN_TEXTURES = 16;
        DEFAULT_TESS_VERTICES = 16;
        DEFAULT_TESS_INDICES = 32;
        MIN_FONT_TEX_SIZE = 128;
        MAX_FONT_TEX_SIZE = 512;
        MAX_CAPS_JOINS_LENGTH = 1000;
        usingMultisampling = false;
        usingCoverageMultisampling = false;
        multisampleCount = 1;
        SHAPE_TEXT_SUPPORTED = false;
        FALSE = 0;
        TRUE = 1;
        INT = 5124;
        BYTE = 5120;
        SHORT = 5122;
        FLOAT = 5126;
        BOOL = 35670;
        UNSIGNED_INT = 5125;
        UNSIGNED_BYTE = 5121;
        UNSIGNED_SHORT = 5123;
        RGB = 6407;
        RGBA = 6408;
        ALPHA = 6406;
        LUMINANCE = 6409;
        LUMINANCE_ALPHA = 6410;
        UNSIGNED_SHORT_5_6_5 = 33635;
        UNSIGNED_SHORT_4_4_4_4 = 32819;
        UNSIGNED_SHORT_5_5_5_1 = 32820;
        RGBA4 = 32854;
        RGB5_A1 = 32855;
        RGB565 = 36194;
        RGB8 = 32849;
        RGBA8 = 32856;
        ALPHA8 = -1;
        READ_ONLY = -1;
        WRITE_ONLY = 35001;
        READ_WRITE = -1;
        TESS_WINDING_NONZERO = 100131;
        TESS_WINDING_ODD = 100130;
        TESS_EDGE_FLAG = 100104;
        GENERATE_MIPMAP_HINT = 33170;
        FASTEST = 4353;
        NICEST = 4354;
        DONT_CARE = 4352;
        VENDOR = 7936;
        RENDERER = 7937;
        VERSION = 7938;
        EXTENSIONS = 7939;
        SHADING_LANGUAGE_VERSION = 35724;
        MAX_SAMPLES = -1;
        SAMPLES = 32937;
        ALIASED_LINE_WIDTH_RANGE = 33902;
        ALIASED_POINT_SIZE_RANGE = 33901;
        DEPTH_BITS = 3414;
        STENCIL_BITS = 3415;
        CCW = 2305;
        CW = 2304;
        VIEWPORT = 2978;
        ARRAY_BUFFER = 34962;
        ELEMENT_ARRAY_BUFFER = 34963;
        MAX_VERTEX_ATTRIBS = 34921;
        STATIC_DRAW = 35044;
        DYNAMIC_DRAW = 35048;
        STREAM_DRAW = 35040;
        BUFFER_SIZE = 34660;
        BUFFER_USAGE = 34661;
        POINTS = 0;
        LINE_STRIP = 3;
        LINE_LOOP = 2;
        LINES = 1;
        TRIANGLE_FAN = 6;
        TRIANGLE_STRIP = 5;
        TRIANGLES = 4;
        CULL_FACE = 2884;
        FRONT = 1028;
        BACK = 1029;
        FRONT_AND_BACK = 1032;
        POLYGON_OFFSET_FILL = 32823;
        UNPACK_ALIGNMENT = 3317;
        PACK_ALIGNMENT = 3333;
        TEXTURE_2D = 3553;
        TEXTURE_RECTANGLE = -1;
        TEXTURE_BINDING_2D = 32873;
        TEXTURE_BINDING_RECTANGLE = -1;
        MAX_TEXTURE_SIZE = 3379;
        TEXTURE_MAX_ANISOTROPY = 34046;
        MAX_TEXTURE_MAX_ANISOTROPY = 34047;
        MAX_VERTEX_TEXTURE_IMAGE_UNITS = 35660;
        MAX_TEXTURE_IMAGE_UNITS = 34930;
        MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661;
        NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
        COMPRESSED_TEXTURE_FORMATS = 34467;
        NEAREST = 9728;
        LINEAR = 9729;
        LINEAR_MIPMAP_NEAREST = 9985;
        LINEAR_MIPMAP_LINEAR = 9987;
        CLAMP_TO_EDGE = 33071;
        REPEAT = 10497;
        TEXTURE0 = 33984;
        TEXTURE1 = 33985;
        TEXTURE2 = 33986;
        TEXTURE3 = 33987;
        TEXTURE_MIN_FILTER = 10241;
        TEXTURE_MAG_FILTER = 10240;
        TEXTURE_WRAP_S = 10242;
        TEXTURE_WRAP_T = 10243;
        TEXTURE_WRAP_R = 32882;
        TEXTURE_CUBE_MAP = 34067;
        TEXTURE_CUBE_MAP_POSITIVE_X = 34069;
        TEXTURE_CUBE_MAP_POSITIVE_Y = 34071;
        TEXTURE_CUBE_MAP_POSITIVE_Z = 34073;
        TEXTURE_CUBE_MAP_NEGATIVE_X = 34070;
        TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072;
        TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074;
        VERTEX_SHADER = 35633;
        FRAGMENT_SHADER = 35632;
        INFO_LOG_LENGTH = 35716;
        SHADER_SOURCE_LENGTH = 35720;
        COMPILE_STATUS = 35713;
        LINK_STATUS = 35714;
        VALIDATE_STATUS = 35715;
        SHADER_TYPE = 35663;
        DELETE_STATUS = 35712;
        FLOAT_VEC2 = 35664;
        FLOAT_VEC3 = 35665;
        FLOAT_VEC4 = 35666;
        FLOAT_MAT2 = 35674;
        FLOAT_MAT3 = 35675;
        FLOAT_MAT4 = 35676;
        INT_VEC2 = 35667;
        INT_VEC3 = 35668;
        INT_VEC4 = 35669;
        BOOL_VEC2 = 35671;
        BOOL_VEC3 = 35672;
        BOOL_VEC4 = 35673;
        SAMPLER_2D = 35678;
        SAMPLER_CUBE = 35680;
        LOW_FLOAT = 36336;
        MEDIUM_FLOAT = 36337;
        HIGH_FLOAT = 36338;
        LOW_INT = 36339;
        MEDIUM_INT = 36340;
        HIGH_INT = 36341;
        CURRENT_VERTEX_ATTRIB = 34342;
        VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975;
        VERTEX_ATTRIB_ARRAY_ENABLED = 34338;
        VERTEX_ATTRIB_ARRAY_SIZE = 34339;
        VERTEX_ATTRIB_ARRAY_STRIDE = 34340;
        VERTEX_ATTRIB_ARRAY_TYPE = 34341;
        VERTEX_ATTRIB_ARRAY_NORMALIZED = 34922;
        VERTEX_ATTRIB_ARRAY_POINTER = 34373;
        BLEND = 3042;
        ONE = 1;
        ZERO = 0;
        SRC_ALPHA = 770;
        DST_ALPHA = 772;
        ONE_MINUS_SRC_ALPHA = 771;
        ONE_MINUS_DST_COLOR = 775;
        ONE_MINUS_SRC_COLOR = 769;
        DST_COLOR = 774;
        SRC_COLOR = 768;
        SAMPLE_ALPHA_TO_COVERAGE = 32926;
        SAMPLE_COVERAGE = 32928;
        KEEP = 7680;
        REPLACE = 7681;
        INCR = 7682;
        DECR = 7683;
        INVERT = 5386;
        INCR_WRAP = 34055;
        DECR_WRAP = 34056;
        NEVER = 512;
        ALWAYS = 519;
        EQUAL = 514;
        LESS = 513;
        LEQUAL = 515;
        GREATER = 516;
        GEQUAL = 518;
        NOTEQUAL = 517;
        FUNC_ADD = 32774;
        FUNC_MIN = 32775;
        FUNC_MAX = 32776;
        FUNC_REVERSE_SUBTRACT = 32779;
        FUNC_SUBTRACT = 32778;
        DITHER = 3024;
        CONSTANT_COLOR = 32769;
        CONSTANT_ALPHA = 32771;
        ONE_MINUS_CONSTANT_COLOR = 32770;
        ONE_MINUS_CONSTANT_ALPHA = 32772;
        SRC_ALPHA_SATURATE = 776;
        SCISSOR_TEST = 3089;
        STENCIL_TEST = 2960;
        DEPTH_TEST = 2929;
        DEPTH_WRITEMASK = 2930;
        COLOR_BUFFER_BIT = 16384;
        DEPTH_BUFFER_BIT = 256;
        STENCIL_BUFFER_BIT = 1024;
        FRAMEBUFFER = 36160;
        COLOR_ATTACHMENT0 = 36064;
        COLOR_ATTACHMENT1 = -1;
        COLOR_ATTACHMENT2 = -1;
        COLOR_ATTACHMENT3 = -1;
        RENDERBUFFER = 36161;
        DEPTH_ATTACHMENT = 36096;
        STENCIL_ATTACHMENT = 36128;
        READ_FRAMEBUFFER = -1;
        DRAW_FRAMEBUFFER = -1;
        DEPTH24_STENCIL8 = 35056;
        DEPTH_COMPONENT = 6402;
        DEPTH_COMPONENT16 = 33189;
        DEPTH_COMPONENT24 = 33190;
        DEPTH_COMPONENT32 = 33191;
        STENCIL_INDEX = 6401;
        STENCIL_INDEX1 = 36166;
        STENCIL_INDEX4 = 36167;
        STENCIL_INDEX8 = 36168;
        DEPTH_STENCIL = 34041;
        FRAMEBUFFER_COMPLETE = 36053;
        FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
        FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
        FRAMEBUFFER_INCOMPLETE_DIMENSIONS = 36057;
        FRAMEBUFFER_INCOMPLETE_FORMATS = 36058;
        FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = -1;
        FRAMEBUFFER_INCOMPLETE_READ_BUFFER = -1;
        FRAMEBUFFER_UNSUPPORTED = 36061;
        FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE = 36048;
        FRAMEBUFFER_ATTACHMENT_OBJECT_NAME = 36049;
        FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL = 36050;
        FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE = 36051;
        RENDERBUFFER_WIDTH = 36162;
        RENDERBUFFER_HEIGHT = 36163;
        RENDERBUFFER_RED_SIZE = 36176;
        RENDERBUFFER_GREEN_SIZE = 36177;
        RENDERBUFFER_BLUE_SIZE = 36178;
        RENDERBUFFER_ALPHA_SIZE = 36179;
        RENDERBUFFER_DEPTH_SIZE = 36180;
        RENDERBUFFER_STENCIL_SIZE = 36181;
        RENDERBUFFER_INTERNAL_FORMAT = 36164;
        MULTISAMPLE = -1;
        LINE_SMOOTH = -1;
        POLYGON_SMOOTH = -1;
    }

    protected class Tessellator implements processing.opengl.PGL.Tessellator {
        protected PGLUtessellator tess;
        protected TessellatorCallback callback;
        protected PGLES.Tessellator.GLUCallback gluCallback;

        public Tessellator(TessellatorCallback callback) {
            this.callback = callback;
            this.tess = PGLU.gluNewTess();
            this.gluCallback = new PGLES.Tessellator.GLUCallback();
            PGLU.gluTessCallback(this.tess, 100100, this.gluCallback);
            PGLU.gluTessCallback(this.tess, 100102, this.gluCallback);
            PGLU.gluTessCallback(this.tess, 100101, this.gluCallback);
            PGLU.gluTessCallback(this.tess, 100105, this.gluCallback);
            PGLU.gluTessCallback(this.tess, 100103, this.gluCallback);
        }

        public void setCallback(int flag) {
            PGLU.gluTessCallback(this.tess, flag, this.gluCallback);
        }

        public void setWindingRule(int rule) {
            this.setProperty(100140, rule);
        }

        public void setProperty(int property, int value) {
            PGLU.gluTessProperty(this.tess, property, (double)value);
        }

        public void beginPolygon() {
            this.beginPolygon((Object)null);
        }

        public void beginPolygon(Object data) {
            PGLU.gluTessBeginPolygon(this.tess, data);
        }

        public void endPolygon() {
            PGLU.gluTessEndPolygon(this.tess);
        }

        public void beginContour() {
            PGLU.gluTessBeginContour(this.tess);
        }

        public void endContour() {
            PGLU.gluTessEndContour(this.tess);
        }

        public void addVertex(double[] v) {
            this.addVertex(v, 0, v);
        }

        public void addVertex(double[] v, int n, Object data) {
            PGLU.gluTessVertex(this.tess, v, n, data);
        }

        protected class GLUCallback extends PGLUtessellatorCallbackAdapter {
            protected GLUCallback() {
            }

            public void begin(int type) {
                Tessellator.this.callback.begin(type);
            }

            public void end() {
                Tessellator.this.callback.end();
            }

            public void vertex(Object data) {
                Tessellator.this.callback.vertex(data);
            }

            public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
                Tessellator.this.callback.combine(coords, data, weight, outData);
            }

            public void error(int errnum) {
                Tessellator.this.callback.error(errnum);
            }
        }
    }
}

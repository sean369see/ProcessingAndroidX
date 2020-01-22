package processing.opengl;

import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL.GLResourceTexture;

public class Texture implements PConstants {
    protected static final int TEX2D = 0;
    protected static final int TEXRECT = 1;
    protected static final int POINT = 2;
    protected static final int LINEAR = 3;
    protected static final int BILINEAR = 4;
    protected static final int TRILINEAR = 5;
    protected static final int MAX_UPDATES = 10;
    protected static final int MIN_MEMORY = 5;
    public int width;
    public int height;
    public int glName;
    public int glTarget;
    public int glFormat;
    public int glMinFilter;
    public int glMagFilter;
    public int glWrapS;
    public int glWrapT;
    public int glWidth;
    public int glHeight;
    private GLResourceTexture glres;
    protected PGraphicsOpenGL pg;
    protected PGL pgl;
    protected int context;
    protected boolean colorBuffer;
    protected boolean usingMipmaps;
    protected boolean usingRepeat;
    protected float maxTexcoordU;
    protected float maxTexcoordV;
    protected boolean bound;
    protected boolean invertedX;
    protected boolean invertedY;
    protected int[] rgbaPixels;
    protected IntBuffer pixelBuffer;
    protected int[] edgePixels;
    protected IntBuffer edgeBuffer;
    protected FrameBuffer tempFbo;
    protected int pixBufUpdateCount;
    protected int rgbaPixUpdateCount;
    protected boolean modified;
    protected int mx1;
    protected int my1;
    protected int mx2;
    protected int my2;
    protected Object bufferSource;
    protected LinkedList<Texture.BufferData> bufferCache;
    protected LinkedList<Texture.BufferData> usedBuffers;
    protected Method disposeBufferMethod;
    public static final int MAX_BUFFER_CACHE_SIZE = 3;

    public Texture(PGraphicsOpenGL pg) {
        this.rgbaPixels = null;
        this.pixelBuffer = null;
        this.edgePixels = null;
        this.edgeBuffer = null;
        this.tempFbo = null;
        this.pixBufUpdateCount = 0;
        this.rgbaPixUpdateCount = 0;
        this.bufferCache = null;
        this.usedBuffers = null;
        this.pg = pg;
        this.pgl = pg.pgl;
        this.context = this.pgl.createEmptyContext();
        this.colorBuffer = false;
        this.glName = 0;
    }

    public Texture(PGraphicsOpenGL pg, int width, int height) {
        this(pg, width, height, new Texture.Parameters());
    }

    public Texture(PGraphicsOpenGL pg, int width, int height, Object params) {
        this.rgbaPixels = null;
        this.pixelBuffer = null;
        this.edgePixels = null;
        this.edgeBuffer = null;
        this.tempFbo = null;
        this.pixBufUpdateCount = 0;
        this.rgbaPixUpdateCount = 0;
        this.bufferCache = null;
        this.usedBuffers = null;
        this.pg = pg;
        this.pgl = pg.pgl;
        this.context = this.pgl.createEmptyContext();
        this.colorBuffer = false;
        this.glName = 0;
        this.init(width, height, (Texture.Parameters)params);
    }

    public void init(int width, int height) {
        Texture.Parameters params;
        if (0 < this.glName) {
            params = this.getParameters();
        } else {
            params = new Texture.Parameters();
        }

        this.init(width, height, params);
    }

    public void init(int width, int height, Texture.Parameters params) {
        this.setParameters(params);
        this.setSize(width, height);
        this.allocate();
    }

    public void init(int width, int height, int glName, int glTarget, int glFormat, int glWidth, int glHeight, int glMinFilter, int glMagFilter, int glWrapS, int glWrapT) {
        this.width = width;
        this.height = height;
        this.glName = glName;
        this.glTarget = glTarget;
        this.glFormat = glFormat;
        this.glWidth = glWidth;
        this.glHeight = glHeight;
        this.glMinFilter = glMinFilter;
        this.glMagFilter = glMagFilter;
        this.glWrapS = glWrapS;
        this.glWrapT = glWrapT;
        this.maxTexcoordU = (float)width / (float)glWidth;
        this.maxTexcoordV = (float)height / (float)glHeight;
        this.usingMipmaps = glMinFilter == PGL.LINEAR_MIPMAP_NEAREST || glMinFilter == PGL.LINEAR_MIPMAP_LINEAR;
        this.usingRepeat = glWrapS == PGL.REPEAT || glWrapT == PGL.REPEAT;
    }

    public void resize(int wide, int high) {
        this.dispose();
        Texture tex = new Texture(this.pg, wide, high, this.getParameters());
        tex.set(this);
        this.copyObject(tex);
        this.tempFbo = null;
    }

    public boolean available() {
        return 0 < this.glName;
    }

    public void set(Texture tex) {
        this.copyTexture(tex, 0, 0, tex.width, tex.height, true);
    }

    public void set(Texture tex, int x, int y, int w, int h) {
        this.copyTexture(tex, x, y, w, h, true);
    }

    public void set(int texTarget, int texName, int texWidth, int texHeight, int w, int h) {
        this.copyTexture(texTarget, texName, texWidth, texHeight, 0, 0, w, h, true);
    }

    public void set(int texTarget, int texName, int texWidth, int texHeight, int target, int tex, int x, int y, int w, int h) {
        this.copyTexture(texTarget, texName, texWidth, texHeight, x, y, w, h, true);
    }

    public void set(int[] pixels) {
        this.set(pixels, 0, 0, this.width, this.height, 2);
    }

    public void set(int[] pixels, int format) {
        this.set(pixels, 0, 0, this.width, this.height, format);
    }

    public void set(int[] pixels, int x, int y, int w, int h) {
        this.set(pixels, x, y, w, h, 2);
    }

    public void set(int[] pixels, int x, int y, int w, int h, int format) {
        if (pixels == null) {
            PGraphics.showWarning("The pixels array is null.");
        } else if (pixels.length < w * h) {
            PGraphics.showWarning("The pixel array has a length of " + pixels.length + ", but it should be at least " + w * h);
        } else if (pixels.length != 0 && w != 0 && h != 0) {
            boolean enabledTex = false;
            if (!this.pgl.texturingIsEnabled(this.glTarget)) {
                this.pgl.enableTexturing(this.glTarget);
                enabledTex = true;
            }

            this.pgl.bindTexture(this.glTarget, this.glName);
            this.loadPixels(w * h);
            this.convertToRGBA(pixels, format, w, h);
            if (this.invertedX) {
                this.flipArrayOnX(this.rgbaPixels, 1);
            }

            if (this.invertedY) {
                this.flipArrayOnY(this.rgbaPixels, 1);
            }

            this.updatePixelBuffer(this.rgbaPixels);
            this.pgl.texSubImage2D(this.glTarget, 0, x, y, w, h, PGL.RGBA, PGL.UNSIGNED_BYTE, this.pixelBuffer);
            this.fillEdges(x, y, w, h);
            if (this.usingMipmaps) {
                if (PGraphicsOpenGL.autoMipmapGenSupported) {
                    this.pgl.generateMipmap(this.glTarget);
                } else {
                    this.manualMipmap();
                }
            }

            this.pgl.bindTexture(this.glTarget, 0);
            if (enabledTex) {
                this.pgl.disableTexturing(this.glTarget);
            }

            this.releasePixelBuffer();
            this.releaseRGBAPixels();
            this.updateTexels(x, y, w, h);
        }
    }

    public void setNative(int[] pixels) {
        this.setNative((int[])pixels, 0, 0, this.width, this.height);
    }

    public void setNative(int[] pixels, int x, int y, int w, int h) {
        this.updatePixelBuffer(pixels);
        this.setNative(this.pixelBuffer, x, y, w, h);
        this.releasePixelBuffer();
    }

    public void setNative(IntBuffer pixBuf, int x, int y, int w, int h) {
        if (pixBuf == null) {
            pixBuf = null;
            PGraphics.showWarning("The pixel buffer is null.");
        } else if (pixBuf.capacity() < w * h) {
            PGraphics.showWarning("The pixel bufer has a length of " + pixBuf.capacity() + ", but it should be at least " + w * h);
        } else if (pixBuf.capacity() != 0) {
            boolean enabledTex = false;
            if (!this.pgl.texturingIsEnabled(this.glTarget)) {
                this.pgl.enableTexturing(this.glTarget);
                enabledTex = true;
            }

            this.pgl.bindTexture(this.glTarget, this.glName);
            this.pgl.texSubImage2D(this.glTarget, 0, x, y, w, h, PGL.RGBA, PGL.UNSIGNED_BYTE, pixBuf);
            this.fillEdges(x, y, w, h);
            if (this.usingMipmaps) {
                if (PGraphicsOpenGL.autoMipmapGenSupported) {
                    this.pgl.generateMipmap(this.glTarget);
                } else {
                    this.manualMipmap();
                }
            }

            this.pgl.bindTexture(this.glTarget, 0);
            if (enabledTex) {
                this.pgl.disableTexturing(this.glTarget);
            }

            this.updateTexels(x, y, w, h);
        }
    }

    public void get(int[] pixels) {
        if (pixels == null) {
            throw new RuntimeException("Trying to copy texture to null pixels array");
        } else if (pixels.length != this.width * this.height) {
            throw new RuntimeException("Trying to copy texture to pixels array of wrong size");
        } else {
            if (this.tempFbo == null) {
                this.tempFbo = new FrameBuffer(this.pg, this.glWidth, this.glHeight);
            }

            this.tempFbo.setColorBuffer(this);
            this.pg.pushFramebuffer();
            this.pg.setFramebuffer(this.tempFbo);
            this.tempFbo.readPixels();
            this.pg.popFramebuffer();
            this.tempFbo.getPixels(pixels);
            this.convertToARGB(pixels);
            if (this.invertedX) {
                this.flipArrayOnX(pixels, 1);
            }

            if (this.invertedY) {
                this.flipArrayOnY(pixels, 1);
            }

        }
    }

    public void put(Texture tex) {
        this.copyTexture(tex, 0, 0, tex.width, tex.height, false);
    }

    public void put(Texture tex, int x, int y, int w, int h) {
        this.copyTexture(tex, x, y, w, h, false);
    }

    public void put(int texTarget, int texName, int texWidth, int texHeight, int w, int h) {
        this.copyTexture(texTarget, texName, texWidth, texHeight, 0, 0, w, h, false);
    }

    public void put(int texTarget, int texName, int texWidth, int texHeight, int target, int tex, int x, int y, int w, int h) {
        this.copyTexture(texTarget, texName, texWidth, texHeight, x, y, w, h, false);
    }

    public boolean usingMipmaps() {
        return this.usingMipmaps;
    }

    public void usingMipmaps(boolean mipmaps, int sampling) {
        int glMagFilter0 = this.glMagFilter;
        int glMinFilter0 = this.glMinFilter;
        if (mipmaps) {
            if (sampling == 2) {
                this.glMagFilter = PGL.NEAREST;
                this.glMinFilter = PGL.NEAREST;
                this.usingMipmaps = false;
            } else if (sampling == 3) {
                this.glMagFilter = PGL.NEAREST;
                this.glMinFilter = PGL.MIPMAPS_ENABLED ? PGL.LINEAR_MIPMAP_NEAREST : PGL.LINEAR;
                this.usingMipmaps = true;
            } else if (sampling == 4) {
                this.glMagFilter = PGL.LINEAR;
                this.glMinFilter = PGL.MIPMAPS_ENABLED ? PGL.LINEAR_MIPMAP_NEAREST : PGL.LINEAR;
                this.usingMipmaps = true;
            } else {
                if (sampling != 5) {
                    throw new RuntimeException("Unknown texture filtering mode");
                }

                this.glMagFilter = PGL.LINEAR;
                this.glMinFilter = PGL.MIPMAPS_ENABLED ? PGL.LINEAR_MIPMAP_LINEAR : PGL.LINEAR;
                this.usingMipmaps = true;
            }
        } else {
            this.usingMipmaps = false;
            if (sampling == 2) {
                this.glMagFilter = PGL.NEAREST;
                this.glMinFilter = PGL.NEAREST;
            } else if (sampling == 3) {
                this.glMagFilter = PGL.NEAREST;
                this.glMinFilter = PGL.LINEAR;
            } else {
                if (sampling != 4 && sampling != 5) {
                    throw new RuntimeException("Unknown texture filtering mode");
                }

                this.glMagFilter = PGL.LINEAR;
                this.glMinFilter = PGL.LINEAR;
            }
        }

        if (glMagFilter0 != this.glMagFilter || glMinFilter0 != this.glMinFilter) {
            this.bind();
            this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_MIN_FILTER, this.glMinFilter);
            this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_MAG_FILTER, this.glMagFilter);
            if (this.usingMipmaps) {
                if (PGraphicsOpenGL.autoMipmapGenSupported) {
                    this.pgl.generateMipmap(this.glTarget);
                } else {
                    this.manualMipmap();
                }
            }

            this.unbind();
        }

    }

    public boolean usingRepeat() {
        return this.usingRepeat;
    }

    public void usingRepeat(boolean repeat) {
        if (repeat) {
            this.glWrapS = PGL.REPEAT;
            this.glWrapT = PGL.REPEAT;
            this.usingRepeat = true;
        } else {
            this.glWrapS = PGL.CLAMP_TO_EDGE;
            this.glWrapT = PGL.CLAMP_TO_EDGE;
            this.usingRepeat = false;
        }

        this.bind();
        this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_WRAP_S, this.glWrapS);
        this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_WRAP_T, this.glWrapT);
        this.unbind();
    }

    public float maxTexcoordU() {
        return this.maxTexcoordU;
    }

    public float maxTexcoordV() {
        return this.maxTexcoordV;
    }

    public boolean invertedX() {
        return this.invertedX;
    }

    public void invertedX(boolean v) {
        this.invertedX = v;
    }

    public boolean invertedY() {
        return this.invertedY;
    }

    public void invertedY(boolean v) {
        this.invertedY = v;
    }

    public int currentSampling() {
        if (this.glMagFilter == PGL.NEAREST && this.glMinFilter == PGL.NEAREST) {
            return 2;
        } else if (this.glMagFilter == PGL.NEAREST && this.glMinFilter == (PGL.MIPMAPS_ENABLED ? PGL.LINEAR_MIPMAP_NEAREST : PGL.LINEAR)) {
            return 3;
        } else if (this.glMagFilter == PGL.LINEAR && this.glMinFilter == (PGL.MIPMAPS_ENABLED ? PGL.LINEAR_MIPMAP_NEAREST : PGL.LINEAR)) {
            return 4;
        } else {
            return this.glMagFilter == PGL.LINEAR && this.glMinFilter == PGL.LINEAR_MIPMAP_LINEAR ? 5 : -1;
        }
    }

    public void bind() {
        if (!this.pgl.texturingIsEnabled(this.glTarget)) {
            this.pgl.enableTexturing(this.glTarget);
        }

        this.pgl.bindTexture(this.glTarget, this.glName);
        this.bound = true;
    }

    public void unbind() {
        if (this.pgl.textureIsBound(this.glTarget, this.glName)) {
            if (!this.pgl.texturingIsEnabled(this.glTarget)) {
                this.pgl.enableTexturing(this.glTarget);
                this.pgl.bindTexture(this.glTarget, 0);
                this.pgl.disableTexturing(this.glTarget);
            } else {
                this.pgl.bindTexture(this.glTarget, 0);
            }
        }

        this.bound = false;
    }

    public boolean bound() {
        return this.bound;
    }

    public boolean isModified() {
        return this.modified;
    }

    public void setModified() {
        this.modified = true;
    }

    public void setModified(boolean m) {
        this.modified = m;
    }

    public int getModifiedX1() {
        return this.mx1;
    }

    public int getModifiedX2() {
        return this.mx2;
    }

    public int getModifiedY1() {
        return this.my1;
    }

    public int getModifiedY2() {
        return this.my2;
    }

    public void updateTexels() {
        this.updateTexelsImpl(0, 0, this.width, this.height);
    }

    public void updateTexels(int x, int y, int w, int h) {
        this.updateTexelsImpl(x, y, w, h);
    }

    protected void updateTexelsImpl(int x, int y, int w, int h) {
        int x2 = x + w;
        int y2 = y + h;
        if (!this.modified) {
            this.mx1 = PApplet.max(0, x);
            this.mx2 = PApplet.min(this.width - 1, x2);
            this.my1 = PApplet.max(0, y);
            this.my2 = PApplet.min(this.height - 1, y2);
            this.modified = true;
        } else {
            if (x < this.mx1) {
                this.mx1 = PApplet.max(0, x);
            }

            if (x > this.mx2) {
                this.mx2 = PApplet.min(this.width - 1, x);
            }

            if (y < this.my1) {
                this.my1 = PApplet.max(0, y);
            }

            if (y > this.my2) {
                this.my2 = y;
            }

            if (x2 < this.mx1) {
                this.mx1 = PApplet.max(0, x2);
            }

            if (x2 > this.mx2) {
                this.mx2 = PApplet.min(this.width - 1, x2);
            }

            if (y2 < this.my1) {
                this.my1 = PApplet.max(0, y2);
            }

            if (y2 > this.my2) {
                this.my2 = PApplet.min(this.height - 1, y2);
            }
        }

    }

    protected void loadPixels(int len) {
        if (this.rgbaPixels == null || this.rgbaPixels.length < len) {
            this.rgbaPixels = new int[len];
        }

    }

    protected void updatePixelBuffer(int[] pixels) {
        this.pixelBuffer = PGL.updateIntBuffer(this.pixelBuffer, pixels, true);
        ++this.pixBufUpdateCount;
    }

    protected void manualMipmap() {
    }

    public void setBufferSource(Object source) {
        this.bufferSource = source;
        this.getSourceMethods();
    }

    public void copyBufferFromSource(Object natRef, ByteBuffer byteBuf, int w, int h) {
        if (this.bufferCache == null) {
            this.bufferCache = new LinkedList();
        }

        if (this.bufferCache.size() + 1 <= 3) {
            this.bufferCache.add(new Texture.BufferData(natRef, byteBuf.asIntBuffer(), w, h));
        } else {
            try {
                this.usedBuffers.add(new Texture.BufferData(natRef, byteBuf.asIntBuffer(), w, h));
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

    }

    public void disposeSourceBuffer() {
        if (this.usedBuffers != null) {
            while(0 < this.usedBuffers.size()) {
                Texture.BufferData data = null;

                try {
                    data = (Texture.BufferData)this.usedBuffers.remove(0);
                } catch (NoSuchElementException var3) {
                    PGraphics.showWarning("Cannot remove used buffer");
                }

                if (data != null) {
                    data.dispose();
                }
            }

        }
    }

    public void getBufferPixels(int[] pixels) {
        Texture.BufferData data = null;
        if (this.usedBuffers != null && 0 < this.usedBuffers.size()) {
            data = (Texture.BufferData)this.usedBuffers.getLast();
        } else if (this.bufferCache != null && 0 < this.bufferCache.size()) {
            data = (Texture.BufferData)this.bufferCache.getLast();
        }

        if (data != null) {
            if (data.w != this.width || data.h != this.height) {
                this.init(data.w, data.h);
            }

            data.rgbBuf.rewind();
            data.rgbBuf.get(pixels);
            this.convertToARGB(pixels);
            if (this.usedBuffers == null) {
                this.usedBuffers = new LinkedList();
            }

            while(0 < this.bufferCache.size()) {
                data = (Texture.BufferData)this.bufferCache.remove(0);
                this.usedBuffers.add(data);
            }
        }

    }

    public boolean hasBufferSource() {
        return this.bufferSource != null;
    }

    public boolean hasBuffers() {
        return this.bufferSource != null && this.bufferCache != null && 0 < this.bufferCache.size();
    }

    protected boolean bufferUpdate() {
        Texture.BufferData data = null;

        try {
            data = (Texture.BufferData)this.bufferCache.remove(0);
        } catch (NoSuchElementException var3) {
            PGraphics.showWarning("Don't have pixel data to copy to texture");
        }

        if (data == null) {
            return false;
        } else {
            if (data.w != this.width || data.h != this.height) {
                this.init(data.w, data.h);
            }

            data.rgbBuf.rewind();
            this.setNative((IntBuffer)data.rgbBuf, 0, 0, this.width, this.height);
            if (this.usedBuffers == null) {
                this.usedBuffers = new LinkedList();
            }

            this.usedBuffers.add(data);
            return true;
        }
    }

    protected void getSourceMethods() {
        try {
            this.disposeBufferMethod = this.bufferSource.getClass().getMethod("disposeBuffer", Object.class);
        } catch (Exception var2) {
            throw new RuntimeException("Provided source object doesn't have a disposeBuffer method.");
        }
    }

    protected void flipArrayOnX(int[] intArray, int mult) {
        int index = 0;
        int xindex = mult * (this.width - 1);

        for(int x = 0; x < this.width / 2; ++x) {
            for(int y = 0; y < this.height; ++y) {
                int i = index + mult * y * this.width;
                int j = xindex + mult * y * this.width;

                for(int c = 0; c < mult; ++c) {
                    int temp = intArray[i];
                    intArray[i] = intArray[j];
                    intArray[j] = temp;
                    ++i;
                    ++j;
                }
            }

            index += mult;
            xindex -= mult;
        }

    }

    protected void flipArrayOnY(int[] intArray, int mult) {
        int index = 0;
        int yindex = mult * (this.height - 1) * this.width;

        for(int y = 0; y < this.height / 2; ++y) {
            for(int x = 0; x < mult * this.width; ++x) {
                int temp = intArray[index];
                intArray[index] = intArray[yindex];
                intArray[yindex] = temp;
                ++index;
                ++yindex;
            }

            yindex -= mult * this.width * 2;
        }

    }

    protected void convertToRGBA(int[] pixels, int format, int w, int h) {
        int i;
        int pixel;
        if (PGL.BIG_ENDIAN) {
            label64:
            switch(format) {
                case 1:
                    i = 0;

                    while(true) {
                        if (i >= pixels.length) {
                            break label64;
                        }

                        pixel = pixels[i];
                        this.rgbaPixels[i] = pixel << 8 | 255;
                        ++i;
                    }
                case 2:
                    for(i = 0; i < pixels.length; ++i) {
                        pixel = pixels[i];
                        this.rgbaPixels[i] = pixel << 8 | pixel >> 24 & 255;
                    }
                case 3:
                default:
                    break;
                case 4:
                    for(i = 0; i < pixels.length; ++i) {
                        this.rgbaPixels[i] = -256 | pixels[i];
                    }
            }
        } else {
            label46:
            switch(format) {
                case 1:
                    i = 0;

                    while(true) {
                        if (i >= pixels.length) {
                            break label46;
                        }

                        pixel = pixels[i];
                        this.rgbaPixels[i] = -16777216 | (pixel & 255) << 16 | (pixel & 16711680) >> 16 | pixel & '\uff00';
                        ++i;
                    }
                case 2:
                    for(i = 0; i < pixels.length; ++i) {
                        pixel = pixels[i];
                        this.rgbaPixels[i] = (pixel & 255) << 16 | (pixel & 16711680) >> 16 | pixel & -16711936;
                    }
                case 3:
                default:
                    break;
                case 4:
                    for(i = 0; i < pixels.length; ++i) {
                        this.rgbaPixels[i] = pixels[i] << 24 | 16777215;
                    }
            }
        }

        ++this.rgbaPixUpdateCount;
    }

    protected void convertToARGB(int[] pixels) {
        int t = 0;
        int p = 0;
        int y;
        int x;
        int pixel;
        if (PGL.BIG_ENDIAN) {
            for(y = 0; y < this.height; ++y) {
                for(x = 0; x < this.width; ++x) {
                    pixel = pixels[p++];
                    pixels[t++] = pixel >>> 8 | pixel << 24 & -16777216;
                }
            }
        } else {
            for(y = 0; y < this.height; ++y) {
                for(x = 0; x < this.width; ++x) {
                    pixel = pixels[p++];
                    pixels[t++] = (pixel & 255) << 16 | (pixel & 16711680) >> 16 | pixel & -16711936;
                }
            }
        }

    }

    protected void setSize(int w, int h) {
        this.width = w;
        this.height = h;
        if (PGraphicsOpenGL.npotTexSupported) {
            this.glWidth = w;
            this.glHeight = h;
        } else {
            this.glWidth = PGL.nextPowerOfTwo(w);
            this.glHeight = PGL.nextPowerOfTwo(h);
        }

        if (this.glWidth <= PGraphicsOpenGL.maxTextureSize && this.glHeight <= PGraphicsOpenGL.maxTextureSize) {
            this.maxTexcoordU = (float)this.width / (float)this.glWidth;
            this.maxTexcoordV = (float)this.height / (float)this.glHeight;
        } else {
            this.glWidth = this.glHeight = 0;
            throw new RuntimeException("Image width and height cannot be larger than " + PGraphicsOpenGL.maxTextureSize + " with this graphics card.");
        }
    }

    protected void allocate() {
        this.dispose();
        boolean enabledTex = false;
        if (!this.pgl.texturingIsEnabled(this.glTarget)) {
            this.pgl.enableTexturing(this.glTarget);
            enabledTex = true;
        }

        this.context = this.pgl.getCurrentContext();
        this.glres = new GLResourceTexture(this);
        this.pgl.bindTexture(this.glTarget, this.glName);
        this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_MIN_FILTER, this.glMinFilter);
        this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_MAG_FILTER, this.glMagFilter);
        this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_WRAP_S, this.glWrapS);
        this.pgl.texParameteri(this.glTarget, PGL.TEXTURE_WRAP_T, this.glWrapT);
        if (PGraphicsOpenGL.anisoSamplingSupported) {
            this.pgl.texParameterf(this.glTarget, PGL.TEXTURE_MAX_ANISOTROPY, PGraphicsOpenGL.maxAnisoAmount);
        }

        this.pgl.texImage2D(this.glTarget, 0, this.glFormat, this.glWidth, this.glHeight, 0, PGL.RGBA, PGL.UNSIGNED_BYTE, (Buffer)null);
        this.pgl.initTexture(this.glTarget, PGL.RGBA, this.width, this.height);
        this.pgl.bindTexture(this.glTarget, 0);
        if (enabledTex) {
            this.pgl.disableTexturing(this.glTarget);
        }

        this.bound = false;
    }

    protected void dispose() {
        if (this.glres != null) {
            this.glres.dispose();
            this.glres = null;
            this.glName = 0;
        }

    }

    protected boolean contextIsOutdated() {
        boolean outdated = !this.pgl.contextIsCurrent(this.context);
        if (outdated) {
            this.dispose();
        }

        return outdated;
    }

    public void colorBuffer(boolean value) {
        this.colorBuffer = value;
    }

    public boolean colorBuffer() {
        return this.colorBuffer;
    }

    protected void copyTexture(Texture tex, int x, int y, int w, int h, boolean scale) {
        if (tex == null) {
            throw new RuntimeException("Source texture is null");
        } else {
            if (this.tempFbo == null) {
                this.tempFbo = new FrameBuffer(this.pg, this.glWidth, this.glHeight);
            }

            this.tempFbo.setColorBuffer(this);
            this.tempFbo.disableDepthTest();
            this.pg.pushFramebuffer();
            this.pg.setFramebuffer(this.tempFbo);
            this.pg.pushStyle();
            this.pg.blendMode(0);
            if (scale) {
                this.pgl.drawTexture(tex.glTarget, tex.glName, tex.glWidth, tex.glHeight, 0, 0, this.tempFbo.width, this.tempFbo.height, 1, x, y, x + w, y + h, 0, 0, this.width, this.height);
            } else {
                this.pgl.drawTexture(tex.glTarget, tex.glName, tex.glWidth, tex.glHeight, 0, 0, this.tempFbo.width, this.tempFbo.height, 1, x, y, x + w, y + h, x, y, x + w, y + h);
            }

            this.pgl.flush();
            this.pg.popStyle();
            this.pg.popFramebuffer();
            this.updateTexels(x, y, w, h);
        }
    }

    protected void copyTexture(int texTarget, int texName, int texWidth, int texHeight, int x, int y, int w, int h, boolean scale) {
        if (this.tempFbo == null) {
            this.tempFbo = new FrameBuffer(this.pg, this.glWidth, this.glHeight);
        }

        this.tempFbo.setColorBuffer(this);
        this.tempFbo.disableDepthTest();
        this.pg.pushFramebuffer();
        this.pg.setFramebuffer(this.tempFbo);
        this.pg.pushStyle();
        this.pg.blendMode(0);
        if (scale) {
            this.pgl.drawTexture(texTarget, texName, texWidth, texHeight, 0, 0, this.tempFbo.width, this.tempFbo.height, x, y, w, h, 0, 0, this.width, this.height);
        } else {
            this.pgl.drawTexture(texTarget, texName, texWidth, texHeight, 0, 0, this.tempFbo.width, this.tempFbo.height, x, y, w, h, x, y, w, h);
        }

        this.pgl.flush();
        this.pg.popStyle();
        this.pg.popFramebuffer();
        this.updateTexels(x, y, w, h);
    }

    protected void copyObject(Texture src) {
        this.dispose();
        this.width = src.width;
        this.height = src.height;
        this.glName = src.glName;
        this.glTarget = src.glTarget;
        this.glFormat = src.glFormat;
        this.glMinFilter = src.glMinFilter;
        this.glMagFilter = src.glMagFilter;
        this.glWidth = src.glWidth;
        this.glHeight = src.glHeight;
        this.usingMipmaps = src.usingMipmaps;
        this.usingRepeat = src.usingRepeat;
        this.maxTexcoordU = src.maxTexcoordU;
        this.maxTexcoordV = src.maxTexcoordV;
        this.invertedX = src.invertedX;
        this.invertedY = src.invertedY;
    }

    protected void releasePixelBuffer() {
        double freeMB = (double)Runtime.getRuntime().freeMemory() / 1000000.0D;
        if (this.pixBufUpdateCount < 10 || freeMB < 5.0D) {
            this.pixelBuffer = null;
        }

    }

    protected void releaseRGBAPixels() {
        double freeMB = (double)Runtime.getRuntime().freeMemory() / 1000000.0D;
        if (this.rgbaPixUpdateCount < 10 || freeMB < 5.0D) {
            this.rgbaPixels = null;
        }

    }

    public Texture.Parameters getParameters() {
        Texture.Parameters res = new Texture.Parameters();
        if (this.glTarget == PGL.TEXTURE_2D) {
            res.target = 0;
        }

        if (this.glFormat == PGL.RGB) {
            res.format = 1;
        } else if (this.glFormat == PGL.RGBA) {
            res.format = 2;
        } else if (this.glFormat == PGL.ALPHA) {
            res.format = 4;
        }

        if (this.glMagFilter == PGL.NEAREST && this.glMinFilter == PGL.NEAREST) {
            res.sampling = 2;
            res.mipmaps = false;
        } else if (this.glMagFilter == PGL.NEAREST && this.glMinFilter == PGL.LINEAR) {
            res.sampling = 3;
            res.mipmaps = false;
        } else if (this.glMagFilter == PGL.NEAREST && this.glMinFilter == PGL.LINEAR_MIPMAP_NEAREST) {
            res.sampling = 3;
            res.mipmaps = true;
        } else if (this.glMagFilter == PGL.LINEAR && this.glMinFilter == PGL.LINEAR) {
            res.sampling = 4;
            res.mipmaps = false;
        } else if (this.glMagFilter == PGL.LINEAR && this.glMinFilter == PGL.LINEAR_MIPMAP_NEAREST) {
            res.sampling = 4;
            res.mipmaps = true;
        } else if (this.glMagFilter == PGL.LINEAR && this.glMinFilter == PGL.LINEAR_MIPMAP_LINEAR) {
            res.sampling = 5;
            res.mipmaps = true;
        }

        if (this.glWrapS == PGL.CLAMP_TO_EDGE) {
            res.wrapU = 0;
        } else if (this.glWrapS == PGL.REPEAT) {
            res.wrapU = 1;
        }

        if (this.glWrapT == PGL.CLAMP_TO_EDGE) {
            res.wrapV = 0;
        } else if (this.glWrapT == PGL.REPEAT) {
            res.wrapV = 1;
        }

        return res;
    }

    protected void setParameters(Texture.Parameters params) {
        if (params.target == 0) {
            this.glTarget = PGL.TEXTURE_2D;
            if (params.format == 1) {
                this.glFormat = PGL.RGB;
            } else if (params.format == 2) {
                this.glFormat = PGL.RGBA;
            } else {
                if (params.format != 4) {
                    throw new RuntimeException("Unknown texture format");
                }

                this.glFormat = PGL.ALPHA;
            }

            boolean mipmaps = params.mipmaps && PGL.MIPMAPS_ENABLED;
            if (mipmaps && !PGraphicsOpenGL.autoMipmapGenSupported) {
                PGraphics.showWarning("Mipmaps were requested but automatic mipmap generation is not supported and manual generation still not implemented, so mipmaps will be disabled.");
                mipmaps = false;
            }

            if (params.sampling == 2) {
                this.glMagFilter = PGL.NEAREST;
                this.glMinFilter = PGL.NEAREST;
            } else if (params.sampling == 3) {
                this.glMagFilter = PGL.NEAREST;
                this.glMinFilter = mipmaps ? PGL.LINEAR_MIPMAP_NEAREST : PGL.LINEAR;
            } else if (params.sampling == 4) {
                this.glMagFilter = PGL.LINEAR;
                this.glMinFilter = mipmaps ? PGL.LINEAR_MIPMAP_NEAREST : PGL.LINEAR;
            } else {
                if (params.sampling != 5) {
                    throw new RuntimeException("Unknown texture filtering mode");
                }

                this.glMagFilter = PGL.LINEAR;
                this.glMinFilter = mipmaps ? PGL.LINEAR_MIPMAP_LINEAR : PGL.LINEAR;
            }

            if (params.wrapU == 0) {
                this.glWrapS = PGL.CLAMP_TO_EDGE;
            } else {
                if (params.wrapU != 1) {
                    throw new RuntimeException("Unknown wrapping mode");
                }

                this.glWrapS = PGL.REPEAT;
            }

            if (params.wrapV == 0) {
                this.glWrapT = PGL.CLAMP_TO_EDGE;
            } else {
                if (params.wrapV != 1) {
                    throw new RuntimeException("Unknown wrapping mode");
                }

                this.glWrapT = PGL.REPEAT;
            }

            this.usingMipmaps = this.glMinFilter == PGL.LINEAR_MIPMAP_NEAREST || this.glMinFilter == PGL.LINEAR_MIPMAP_LINEAR;
            this.usingRepeat = this.glWrapS == PGL.REPEAT || this.glWrapT == PGL.REPEAT;
            this.invertedX = false;
            this.invertedY = false;
        } else {
            throw new RuntimeException("Unknown texture target");
        }
    }

    protected void fillEdges(int x, int y, int w, int h) {
        if ((this.width < this.glWidth || this.height < this.glHeight) && (x + w == this.width || y + h == this.height)) {
            int eh;
            int i;
            int c;
            if (x + w == this.width) {
                eh = this.glWidth - this.width;
                this.edgePixels = new int[h * eh];

                for(i = 0; i < h; ++i) {
                    c = this.rgbaPixels[i * w + (w - 1)];
                    Arrays.fill(this.edgePixels, i * eh, (i + 1) * eh, c);
                }

                this.edgeBuffer = PGL.updateIntBuffer(this.edgeBuffer, this.edgePixels, true);
                this.pgl.texSubImage2D(this.glTarget, 0, this.width, y, eh, h, PGL.RGBA, PGL.UNSIGNED_BYTE, this.edgeBuffer);
            }

            if (y + h == this.height) {
                eh = this.glHeight - this.height;
                this.edgePixels = new int[eh * w];

                for(i = 0; i < eh; ++i) {
                    System.arraycopy(this.rgbaPixels, (h - 1) * w, this.edgePixels, i * w, w);
                }

                this.edgeBuffer = PGL.updateIntBuffer(this.edgeBuffer, this.edgePixels, true);
                this.pgl.texSubImage2D(this.glTarget, 0, x, this.height, w, eh, PGL.RGBA, PGL.UNSIGNED_BYTE, this.edgeBuffer);
            }

            if (x + w == this.width && y + h == this.height) {
                eh = this.glWidth - this.width;
                i = this.glHeight - this.height;
                c = this.rgbaPixels[w * h - 1];
                this.edgePixels = new int[i * eh];
                Arrays.fill(this.edgePixels, 0, i * eh, c);
                this.edgeBuffer = PGL.updateIntBuffer(this.edgeBuffer, this.edgePixels, true);
                this.pgl.texSubImage2D(this.glTarget, 0, this.width, this.height, eh, i, PGL.RGBA, PGL.UNSIGNED_BYTE, this.edgeBuffer);
            }
        }

    }

    protected class BufferData {
        int w;
        int h;
        Object natBuf;
        IntBuffer rgbBuf;

        BufferData(Object nat, IntBuffer rgb, int w, int h) {
            this.natBuf = nat;
            this.rgbBuf = rgb;
            this.w = w;
            this.h = h;
        }

        void dispose() {
            try {
                Texture.this.disposeBufferMethod.invoke(Texture.this.bufferSource, this.natBuf);
                this.natBuf = null;
                this.rgbBuf = null;
            } catch (Exception var2) {
                var2.printStackTrace();
            }

        }
    }

    public static class Parameters {
        public int target;
        public int format;
        public int sampling;
        public boolean mipmaps;
        public int wrapU;
        public int wrapV;

        public Parameters() {
            this.target = 0;
            this.format = 2;
            this.sampling = 4;
            this.mipmaps = true;
            this.wrapU = 0;
            this.wrapV = 0;
        }

        public Parameters(int format) {
            this.target = 0;
            this.format = format;
            this.sampling = 4;
            this.mipmaps = true;
            this.wrapU = 0;
            this.wrapV = 0;
        }

        public Parameters(int format, int sampling) {
            this.target = 0;
            this.format = format;
            this.sampling = sampling;
            this.mipmaps = true;
            this.wrapU = 0;
            this.wrapV = 0;
        }

        public Parameters(int format, int sampling, boolean mipmaps) {
            this.target = 0;
            this.format = format;
            this.mipmaps = mipmaps;
            if (sampling == 5 && !mipmaps) {
                this.sampling = 4;
            } else {
                this.sampling = sampling;
            }

            this.wrapU = 0;
            this.wrapV = 0;
        }

        public Parameters(int format, int sampling, boolean mipmaps, int wrap) {
            this.target = 0;
            this.format = format;
            this.mipmaps = mipmaps;
            if (sampling == 5 && !mipmaps) {
                this.sampling = 4;
            } else {
                this.sampling = sampling;
            }

            this.wrapU = wrap;
            this.wrapV = wrap;
        }

        public Parameters(Texture.Parameters src) {
            this.set(src);
        }

        public void set(int format) {
            this.format = format;
        }

        public void set(int format, int sampling) {
            this.format = format;
            this.sampling = sampling;
        }

        public void set(int format, int sampling, boolean mipmaps) {
            this.format = format;
            this.sampling = sampling;
            this.mipmaps = mipmaps;
        }

        public void set(Texture.Parameters src) {
            this.target = src.target;
            this.format = src.format;
            this.sampling = src.sampling;
            this.mipmaps = src.mipmaps;
            this.wrapU = src.wrapU;
            this.wrapV = src.wrapV;
        }
    }
}

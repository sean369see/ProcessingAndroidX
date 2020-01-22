package processing.opengl;

import java.nio.IntBuffer;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.opengl.PGraphicsOpenGL.GLResourceFrameBuffer;

public class FrameBuffer implements PConstants {
    protected PGraphicsOpenGL pg;
    protected PGL pgl;
    protected int context;
    public int glFbo;
    public int glDepth;
    public int glStencil;
    public int glDepthStencil;
    public int glMultisample;
    public int width;
    public int height;
    private GLResourceFrameBuffer glres;
    protected int depthBits;
    protected int stencilBits;
    protected boolean packedDepthStencil;
    protected boolean multisample;
    protected int nsamples;
    protected int numColorBuffers;
    protected Texture[] colorBufferTex;
    protected boolean screenFb;
    protected boolean noDepth;
    protected IntBuffer pixelBuffer;

    FrameBuffer(PGraphicsOpenGL pg) {
        this.pg = pg;
        this.pgl = pg.pgl;
        this.context = this.pgl.createEmptyContext();
    }

    FrameBuffer(PGraphicsOpenGL pg, int w, int h, int samples, int colorBuffers, int depthBits, int stencilBits, boolean packedDepthStencil, boolean screen) {
        this(pg);
        this.glFbo = 0;
        this.glDepth = 0;
        this.glStencil = 0;
        this.glDepthStencil = 0;
        this.glMultisample = 0;
        if (screen) {
            colorBuffers = 0;
            samples = 0;
            stencilBits = 0;
            depthBits = 0;
        }

        this.width = w;
        this.height = h;
        if (1 < samples) {
            this.multisample = true;
            this.nsamples = samples;
        } else {
            this.multisample = false;
            this.nsamples = 1;
        }

        this.numColorBuffers = colorBuffers;
        this.colorBufferTex = new Texture[this.numColorBuffers];

        for(int i = 0; i < this.numColorBuffers; ++i) {
            this.colorBufferTex[i] = null;
        }

        if (depthBits < 1 && stencilBits < 1) {
            this.depthBits = 0;
            this.stencilBits = 0;
            this.packedDepthStencil = false;
        } else if (packedDepthStencil) {
            this.depthBits = 24;
            this.stencilBits = 8;
            this.packedDepthStencil = true;
        } else {
            this.depthBits = depthBits;
            this.stencilBits = stencilBits;
            this.packedDepthStencil = false;
        }

        this.screenFb = screen;
        this.allocate();
        this.noDepth = false;
        this.pixelBuffer = null;
    }

    FrameBuffer(PGraphicsOpenGL pg, int w, int h) {
        this(pg, w, h, 1, 1, 0, 0, false, false);
    }

    FrameBuffer(PGraphicsOpenGL pg, int w, int h, boolean screen) {
        this(pg, w, h, 1, 1, 0, 0, false, screen);
    }

    public void clear() {
        this.pg.pushFramebuffer();
        this.pg.setFramebuffer(this);
        this.pgl.clearDepth(1.0F);
        this.pgl.clearStencil(0);
        this.pgl.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.pgl.clear(PGL.DEPTH_BUFFER_BIT | PGL.STENCIL_BUFFER_BIT | PGL.COLOR_BUFFER_BIT);
        this.pg.popFramebuffer();
    }

    public void copyColor(FrameBuffer dest) {
        this.copy(dest, PGL.COLOR_BUFFER_BIT);
    }

    public void copyDepth(FrameBuffer dest) {
        this.copy(dest, PGL.DEPTH_BUFFER_BIT);
    }

    public void copyStencil(FrameBuffer dest) {
        this.copy(dest, PGL.STENCIL_BUFFER_BIT);
    }

    public void copy(FrameBuffer dest, int mask) {
        this.pgl.bindFramebufferImpl(PGL.READ_FRAMEBUFFER, this.glFbo);
        this.pgl.bindFramebufferImpl(PGL.DRAW_FRAMEBUFFER, dest.glFbo);
        this.pgl.blitFramebuffer(0, 0, this.width, this.height, 0, 0, dest.width, dest.height, mask, PGL.NEAREST);
        this.pgl.bindFramebufferImpl(PGL.READ_FRAMEBUFFER, this.pg.getCurrentFB().glFbo);
        this.pgl.bindFramebufferImpl(PGL.DRAW_FRAMEBUFFER, this.pg.getCurrentFB().glFbo);
    }

    public void bind() {
        this.pgl.bindFramebufferImpl(PGL.FRAMEBUFFER, this.glFbo);
    }

    public void disableDepthTest() {
        this.noDepth = true;
    }

    public void finish() {
        if (this.noDepth) {
            if (this.pg.getHint(-2)) {
                this.pgl.enable(PGL.DEPTH_TEST);
            } else {
                this.pgl.disable(PGL.DEPTH_TEST);
            }
        }

    }

    public void readPixels() {
        if (this.pixelBuffer == null) {
            this.createPixelBuffer();
        }

        this.pixelBuffer.rewind();
        this.pgl.readPixels(0, 0, this.width, this.height, PGL.RGBA, PGL.UNSIGNED_BYTE, this.pixelBuffer);
    }

    public void getPixels(int[] pixels) {
        if (this.pixelBuffer != null) {
            this.pixelBuffer.get(pixels, 0, pixels.length);
            this.pixelBuffer.rewind();
        }

    }

    public IntBuffer getPixelBuffer() {
        return this.pixelBuffer;
    }

    public boolean hasDepthBuffer() {
        return 0 < this.depthBits;
    }

    public boolean hasStencilBuffer() {
        return 0 < this.stencilBits;
    }

    public void setFBO(int id) {
        if (this.screenFb) {
            this.glFbo = id;
        }

    }

    public void setColorBuffer(Texture tex) {
        this.setColorBuffers(new Texture[]{tex}, 1);
    }

    public void setColorBuffers(Texture[] textures) {
        this.setColorBuffers(textures, textures.length);
    }

    public void setColorBuffers(Texture[] textures, int n) {
        if (!this.screenFb) {
            if (this.numColorBuffers != PApplet.min(n, textures.length)) {
                throw new RuntimeException("Wrong number of textures to set the color buffers.");
            } else {
                int i;
                for(i = 0; i < this.numColorBuffers; ++i) {
                    this.colorBufferTex[i] = textures[i];
                }

                this.pg.pushFramebuffer();
                this.pg.setFramebuffer(this);

                for(i = 0; i < this.numColorBuffers; ++i) {
                    this.pgl.framebufferTexture2D(PGL.FRAMEBUFFER, PGL.COLOR_ATTACHMENT0 + i, PGL.TEXTURE_2D, 0, 0);
                }

                for(i = 0; i < this.numColorBuffers; ++i) {
                    this.pgl.framebufferTexture2D(PGL.FRAMEBUFFER, PGL.COLOR_ATTACHMENT0 + i, this.colorBufferTex[i].glTarget, this.colorBufferTex[i].glName, 0);
                }

                this.pgl.validateFramebuffer();
                this.pg.popFramebuffer();
            }
        }
    }

    public void swapColorBuffers() {
        int i;
        for(i = 0; i < this.numColorBuffers - 1; ++i) {
            int i1 = i + 1;
            Texture tmp = this.colorBufferTex[i];
            this.colorBufferTex[i] = this.colorBufferTex[i1];
            this.colorBufferTex[i1] = tmp;
        }

        this.pg.pushFramebuffer();
        this.pg.setFramebuffer(this);

        for(i = 0; i < this.numColorBuffers; ++i) {
            this.pgl.framebufferTexture2D(PGL.FRAMEBUFFER, PGL.COLOR_ATTACHMENT0 + i, this.colorBufferTex[i].glTarget, this.colorBufferTex[i].glName, 0);
        }

        this.pgl.validateFramebuffer();
        this.pg.popFramebuffer();
    }

    public int getDefaultReadBuffer() {
        return this.screenFb ? this.pgl.getDefaultReadBuffer() : PGL.COLOR_ATTACHMENT0;
    }

    public int getDefaultDrawBuffer() {
        return this.screenFb ? this.pgl.getDefaultDrawBuffer() : PGL.COLOR_ATTACHMENT0;
    }

    protected void allocate() {
        this.dispose();
        this.context = this.pgl.getCurrentContext();
        this.glres = new GLResourceFrameBuffer(this);
        if (this.screenFb) {
            this.glFbo = 0;
        } else {
            if (this.multisample) {
                this.initColorBufferMultisample();
            }

            if (this.packedDepthStencil) {
                this.initPackedDepthStencilBuffer();
            } else {
                if (0 < this.depthBits) {
                    this.initDepthBuffer();
                }

                if (0 < this.stencilBits) {
                    this.initStencilBuffer();
                }
            }
        }

    }

    protected void dispose() {
        if (!this.screenFb) {
            if (this.glres != null) {
                this.glres.dispose();
                this.glFbo = 0;
                this.glDepth = 0;
                this.glStencil = 0;
                this.glMultisample = 0;
                this.glDepthStencil = 0;
                this.glres = null;
            }

        }
    }

    protected boolean contextIsOutdated() {
        if (this.screenFb) {
            return false;
        } else {
            boolean outdated = !this.pgl.contextIsCurrent(this.context);
            if (outdated) {
                this.dispose();

                for(int i = 0; i < this.numColorBuffers; ++i) {
                    this.colorBufferTex[i] = null;
                }
            }

            return outdated;
        }
    }

    protected void initColorBufferMultisample() {
        if (!this.screenFb) {
            this.pg.pushFramebuffer();
            this.pg.setFramebuffer(this);
            this.pgl.bindRenderbuffer(PGL.RENDERBUFFER, this.glMultisample);
            this.pgl.renderbufferStorageMultisample(PGL.RENDERBUFFER, this.nsamples, PGL.RGBA8, this.width, this.height);
            this.pgl.framebufferRenderbuffer(PGL.FRAMEBUFFER, PGL.COLOR_ATTACHMENT0, PGL.RENDERBUFFER, this.glMultisample);
            this.pg.popFramebuffer();
        }
    }

    protected void initPackedDepthStencilBuffer() {
        if (!this.screenFb) {
            if (this.width != 0 && this.height != 0) {
                this.pg.pushFramebuffer();
                this.pg.setFramebuffer(this);
                this.pgl.bindRenderbuffer(PGL.RENDERBUFFER, this.glDepthStencil);
                if (this.multisample) {
                    this.pgl.renderbufferStorageMultisample(PGL.RENDERBUFFER, this.nsamples, PGL.DEPTH24_STENCIL8, this.width, this.height);
                } else {
                    this.pgl.renderbufferStorage(PGL.RENDERBUFFER, PGL.DEPTH24_STENCIL8, this.width, this.height);
                }

                this.pgl.framebufferRenderbuffer(PGL.FRAMEBUFFER, PGL.DEPTH_ATTACHMENT, PGL.RENDERBUFFER, this.glDepthStencil);
                this.pgl.framebufferRenderbuffer(PGL.FRAMEBUFFER, PGL.STENCIL_ATTACHMENT, PGL.RENDERBUFFER, this.glDepthStencil);
                this.pg.popFramebuffer();
            } else {
                throw new RuntimeException("PFramebuffer: size undefined.");
            }
        }
    }

    protected void initDepthBuffer() {
        if (!this.screenFb) {
            if (this.width != 0 && this.height != 0) {
                this.pg.pushFramebuffer();
                this.pg.setFramebuffer(this);
                this.pgl.bindRenderbuffer(PGL.RENDERBUFFER, this.glDepth);
                int glConst = PGL.DEPTH_COMPONENT16;
                if (this.depthBits == 16) {
                    glConst = PGL.DEPTH_COMPONENT16;
                } else if (this.depthBits == 24) {
                    glConst = PGL.DEPTH_COMPONENT24;
                } else if (this.depthBits == 32) {
                    glConst = PGL.DEPTH_COMPONENT32;
                }

                if (this.multisample) {
                    this.pgl.renderbufferStorageMultisample(PGL.RENDERBUFFER, this.nsamples, glConst, this.width, this.height);
                } else {
                    this.pgl.renderbufferStorage(PGL.RENDERBUFFER, glConst, this.width, this.height);
                }

                this.pgl.framebufferRenderbuffer(PGL.FRAMEBUFFER, PGL.DEPTH_ATTACHMENT, PGL.RENDERBUFFER, this.glDepth);
                this.pg.popFramebuffer();
            } else {
                throw new RuntimeException("PFramebuffer: size undefined.");
            }
        }
    }

    protected void initStencilBuffer() {
        if (!this.screenFb) {
            if (this.width != 0 && this.height != 0) {
                this.pg.pushFramebuffer();
                this.pg.setFramebuffer(this);
                this.pgl.bindRenderbuffer(PGL.RENDERBUFFER, this.glStencil);
                int glConst = PGL.STENCIL_INDEX1;
                if (this.stencilBits == 1) {
                    glConst = PGL.STENCIL_INDEX1;
                } else if (this.stencilBits == 4) {
                    glConst = PGL.STENCIL_INDEX4;
                } else if (this.stencilBits == 8) {
                    glConst = PGL.STENCIL_INDEX8;
                }

                if (this.multisample) {
                    this.pgl.renderbufferStorageMultisample(PGL.RENDERBUFFER, this.nsamples, glConst, this.width, this.height);
                } else {
                    this.pgl.renderbufferStorage(PGL.RENDERBUFFER, glConst, this.width, this.height);
                }

                this.pgl.framebufferRenderbuffer(PGL.FRAMEBUFFER, PGL.STENCIL_ATTACHMENT, PGL.RENDERBUFFER, this.glStencil);
                this.pg.popFramebuffer();
            } else {
                throw new RuntimeException("PFramebuffer: size undefined.");
            }
        }
    }

    protected void createPixelBuffer() {
        this.pixelBuffer = IntBuffer.allocate(this.width * this.height);
        this.pixelBuffer.rewind();
    }
}

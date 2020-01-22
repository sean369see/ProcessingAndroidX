package processing.opengl;

import java.util.Arrays;
import java.util.HashMap;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PFont.Glyph;
import processing.opengl.Texture.Parameters;

class FontTexture implements PConstants {
    protected PGL pgl;
    protected boolean is3D;
    protected int minSize;
    protected int maxSize;
    protected int offsetX;
    protected int offsetY;
    protected int lineHeight;
    protected Texture[] textures = null;
    protected PImage[] images = null;
    protected int lastTex;
    protected FontTexture.TextureInfo[] glyphTexinfos;
    protected HashMap<Glyph, FontTexture.TextureInfo> texinfoMap;

    public FontTexture(PGraphicsOpenGL pg, PFont font, boolean is3D) {
        this.pgl = pg.pgl;
        this.is3D = is3D;
        this.initTexture(pg, font);
    }

    protected void allocate() {
    }

    protected void dispose() {
        for(int i = 0; i < this.textures.length; ++i) {
            this.textures[i].dispose();
        }

    }

    protected void initTexture(PGraphicsOpenGL pg, PFont font) {
        this.lastTex = -1;
        int spow = PGL.nextPowerOfTwo(font.getSize());
        this.minSize = PApplet.min(PGraphicsOpenGL.maxTextureSize, PApplet.max(PGL.MIN_FONT_TEX_SIZE, spow));
        this.maxSize = PApplet.min(PGraphicsOpenGL.maxTextureSize, PApplet.max(PGL.MAX_FONT_TEX_SIZE, 2 * spow));
        if (this.maxSize < spow) {
            PGraphics.showWarning("The font size is too large to be properly displayed with OpenGL");
        }

        this.addTexture(pg);
        this.offsetX = 0;
        this.offsetY = 0;
        this.lineHeight = 0;
        this.texinfoMap = new HashMap();
        this.glyphTexinfos = new FontTexture.TextureInfo[font.getGlyphCount()];
        this.addAllGlyphsToTexture(pg, font);
    }

    public boolean addTexture(PGraphicsOpenGL pg) {
        int w = this.maxSize;
        int h;
        boolean resize;
        if (-1 < this.lastTex && this.textures[this.lastTex].glHeight < this.maxSize) {
            h = PApplet.min(2 * this.textures[this.lastTex].glHeight, this.maxSize);
            resize = true;
        } else {
            h = this.minSize;
            resize = false;
        }

        Texture tex;
        if (this.is3D) {
            tex = new Texture(pg, w, h, new Parameters(2, 4, false));
        } else {
            tex = new Texture(pg, w, h, new Parameters(2, 3, false));
        }

        if (this.textures == null) {
            this.textures = new Texture[1];
            this.textures[0] = tex;
            this.images = new PImage[1];
            this.images[0] = pg.wrapTexture(tex);
            this.lastTex = 0;
        } else if (resize) {
            Texture tex0 = this.textures[this.lastTex];
            tex.put(tex0);
            this.textures[this.lastTex] = tex;
            pg.setCache(this.images[this.lastTex], tex);
            this.images[this.lastTex].width = tex.width;
            this.images[this.lastTex].height = tex.height;
        } else {
            this.lastTex = this.textures.length;
            Texture[] tempTex = new Texture[this.lastTex + 1];
            PApplet.arrayCopy(this.textures, tempTex, this.textures.length);
            tempTex[this.lastTex] = tex;
            this.textures = tempTex;
            PImage[] tempImg = new PImage[this.textures.length];
            PApplet.arrayCopy(this.images, tempImg, this.images.length);
            tempImg[this.lastTex] = pg.wrapTexture(tex);
            this.images = tempImg;
        }

        tex.bind();
        return resize;
    }

    public void begin() {
    }

    public void end() {
        for(int i = 0; i < this.textures.length; ++i) {
            this.pgl.disableTexturing(this.textures[i].glTarget);
        }

    }

    public PImage getTexture(FontTexture.TextureInfo info) {
        return this.images[info.texIndex];
    }

    public void addAllGlyphsToTexture(PGraphicsOpenGL pg, PFont font) {
        for(int i = 0; i < font.getGlyphCount(); ++i) {
            this.addToTexture(pg, i, font.getGlyph(i));
        }

    }

    public void updateGlyphsTexCoords() {
        for(int i = 0; i < this.glyphTexinfos.length; ++i) {
            FontTexture.TextureInfo tinfo = this.glyphTexinfos[i];
            if (tinfo != null && tinfo.texIndex == this.lastTex) {
                tinfo.updateUV();
            }
        }

    }

    public FontTexture.TextureInfo getTexInfo(Glyph glyph) {
        FontTexture.TextureInfo info = (FontTexture.TextureInfo)this.texinfoMap.get(glyph);
        return info;
    }

    public FontTexture.TextureInfo addToTexture(PGraphicsOpenGL pg, Glyph glyph) {
        int n = this.glyphTexinfos.length;
        if (n == 0) {
            this.glyphTexinfos = new FontTexture.TextureInfo[1];
        }

        this.addToTexture(pg, n, glyph);
        return this.glyphTexinfos[n];
    }

    public boolean contextIsOutdated() {
        boolean outdated = false;

        int i;
        for(i = 0; i < this.textures.length; ++i) {
            if (this.textures[i].contextIsOutdated()) {
                outdated = true;
            }
        }

        if (outdated) {
            for(i = 0; i < this.textures.length; ++i) {
                this.textures[i].dispose();
            }
        }

        return outdated;
    }

    protected void addToTexture(PGraphicsOpenGL pg, int idx, Glyph glyph) {
        int w = 1 + glyph.width + 1;
        int h = 1 + glyph.height + 1;
        int[] rgba = new int[w * h];
        int t = false;
        int p = 0;
        int y;
        int x;
        int t;
        if (PGL.BIG_ENDIAN) {
            Arrays.fill(rgba, 0, w, -256);
            t = w;

            for(y = 0; y < glyph.height; ++y) {
                rgba[t++] = -256;

                for(x = 0; x < glyph.width; ++x) {
                    rgba[t++] = -256 | glyph.image.pixels[p++];
                }

                rgba[t++] = -256;
            }

            Arrays.fill(rgba, (h - 1) * w, h * w, -256);
        } else {
            Arrays.fill(rgba, 0, w, 16777215);
            t = w;

            for(y = 0; y < glyph.height; ++y) {
                rgba[t++] = 16777215;

                for(x = 0; x < glyph.width; ++x) {
                    rgba[t++] = glyph.image.pixels[p++] << 24 | 16777215;
                }

                rgba[t++] = 16777215;
            }

            Arrays.fill(rgba, (h - 1) * w, h * w, 16777215);
        }

        if (this.offsetX + w > this.textures[this.lastTex].glWidth) {
            this.offsetX = 0;
            this.offsetY += this.lineHeight;
        }

        this.lineHeight = Math.max(this.lineHeight, h);
        boolean resized = false;
        if (this.offsetY + this.lineHeight > this.textures[this.lastTex].glHeight) {
            resized = this.addTexture(pg);
            if (resized) {
                this.updateGlyphsTexCoords();
            } else {
                this.offsetX = 0;
                this.offsetY = 0;
                this.lineHeight = 0;
            }
        }

        FontTexture.TextureInfo tinfo = new FontTexture.TextureInfo(this.lastTex, this.offsetX, this.offsetY, w, h, rgba);
        this.offsetX += w;
        if (idx == this.glyphTexinfos.length) {
            FontTexture.TextureInfo[] temp = new FontTexture.TextureInfo[this.glyphTexinfos.length + 1];
            System.arraycopy(this.glyphTexinfos, 0, temp, 0, this.glyphTexinfos.length);
            this.glyphTexinfos = temp;
        }

        this.glyphTexinfos[idx] = tinfo;
        this.texinfoMap.put(glyph, tinfo);
    }

    class TextureInfo {
        int texIndex;
        int width;
        int height;
        int[] crop;
        float u0;
        float u1;
        float v0;
        float v1;
        int[] pixels;

        TextureInfo(int tidx, int cropX, int cropY, int cropW, int cropH, int[] pix) {
            this.texIndex = tidx;
            this.crop = new int[4];
            this.crop[0] = cropX + 1;
            this.crop[1] = cropY + 1 + cropH - 2;
            this.crop[2] = cropW - 2;
            this.crop[3] = -cropH + 2;
            this.pixels = pix;
            this.updateUV();
            this.updateTex();
        }

        void updateUV() {
            this.width = FontTexture.this.textures[this.texIndex].glWidth;
            this.height = FontTexture.this.textures[this.texIndex].glHeight;
            this.u0 = (float)this.crop[0] / (float)this.width;
            this.u1 = this.u0 + (float)this.crop[2] / (float)this.width;
            this.v0 = (float)(this.crop[1] + this.crop[3]) / (float)this.height;
            this.v1 = this.v0 - (float)this.crop[3] / (float)this.height;
        }

        void updateTex() {
            FontTexture.this.textures[this.texIndex].setNative(this.pixels, this.crop[0] - 1, this.crop[1] + this.crop[3] - 1, this.crop[2] + 2, -this.crop[3] + 2);
        }
    }
}

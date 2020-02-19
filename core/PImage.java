/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-16 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

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

package processing.core;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class PImage implements PConstants, Cloneable {
    public int format;
    public int[] pixels;
    public int width;
    public int height;
    public int pixelDensity = 1;
    public int pixelWidth;
    public int pixelHeight;
    public PApplet parent;
    protected Bitmap bitmap;
    protected HashMap<PGraphics, Object> cacheMap;
    protected HashMap<PGraphics, Object> paramMap;
    protected boolean modified;
    protected int mx1;
    protected int my1;
    protected int mx2;
    protected int my2;
    public boolean loaded = false;
    private int fracU;
    private int ifU;
    private int fracV;
    private int ifV;
    private int u1;
    private int u2;
    private int v1;
    private int v2;
    private int sX;
    private int sY;
    private int iw;
    private int iw1;
    private int ih1;
    private int ul;
    private int ll;
    private int ur;
    private int lr;
    private int cUL;
    private int cLL;
    private int cUR;
    private int cLR;
    private int srcXOffset;
    private int srcYOffset;
    private int r;
    private int g;
    private int b;
    private int a;
    private int[] srcBuffer;
    static final int PRECISIONB = 15;
    static final int PRECISIONF = 32768;
    static final int PREC_MAXVAL = 32767;
    static final int PREC_ALPHA_SHIFT = 9;
    static final int PREC_RED_SHIFT = 1;
    private int blurRadius;
    private int blurKernelSize;
    private int[] blurKernel;
    private int[][] blurMult;
    public static final int ALPHA_MASK = -16777216;
    public static final int RED_MASK = 16711680;
    public static final int GREEN_MASK = 65280;
    public static final int BLUE_MASK = 255;
    private static final int RB_MASK = 16711935;
    private static final int GN_MASK = 65280;
    static byte[] TIFF_HEADER = new byte[]{77, 77, 0, 42, 0, 0, 0, 8, 0, 9, 0, -2, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2, 0, 3, 0, 0, 0, 3, 0, 0, 0, 122, 1, 6, 0, 3, 0, 0, 0, 1, 0, 2, 0, 0, 1, 17, 0, 4, 0, 0, 0, 1, 0, 0, 3, 0, 1, 21, 0, 3, 0, 0, 0, 1, 0, 3, 0, 0, 1, 22, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 23, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 8, 0, 8};
    static final String TIFF_ERROR = "Error: Processing can only read its own TIFF files.";
    protected String[] saveImageFormats;

    public PImage() {
        this.format = 2;
    }

    public PImage(int width, int height) {
        this.init(width, height, 1);
    }

    public PImage(int width, int height, int format) {
        this.init(width, height, format);
    }

    public void init(int width, int height, int format) {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        this.format = format;
        this.pixelWidth = width * this.pixelDensity;
        this.pixelHeight = height * this.pixelDensity;
        this.pixels = new int[this.pixelWidth * this.pixelHeight];
    }

    protected void checkAlpha() {
        if (this.pixels != null) {
            for(int i = 0; i < this.pixels.length; ++i) {
                if ((this.pixels[i] & -16777216) != -16777216) {
                    this.format = 2;
                    break;
                }
            }

        }
    }

    public PImage(Object nativeObject) {
        Bitmap bitmap = (Bitmap)nativeObject;
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.pixels = null;
        this.format = bitmap.hasAlpha() ? 2 : 1;
        this.pixelDensity = 1;
        this.pixelWidth = this.width;
        this.pixelHeight = this.height;
    }

    public Object getNative() {
        return this.bitmap;
    }

    public void setNative(Object nativeObject) {
        Bitmap bitmap = (Bitmap)nativeObject;
        this.bitmap = bitmap;
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

    public void loadPixels() {
        if (this.pixels == null || this.pixels.length != this.width * this.height) {
            this.pixels = new int[this.width * this.height];
        }

        if (this.bitmap != null) {
            if (this.modified) {
                if (!this.bitmap.isMutable()) {
                    this.bitmap = this.bitmap.copy(Config.ARGB_8888, true);
                }

                this.bitmap.setPixels(this.pixels, 0, this.width, this.mx1, this.my1, this.mx2 - this.mx1, this.my2 - this.my1);
                this.modified = false;
            } else {
                this.bitmap.getPixels(this.pixels, 0, this.width, 0, 0, this.width, this.height);
            }
        }

        this.setLoaded();
    }

    public void updatePixels() {
        this.updatePixelsImpl(0, 0, this.width, this.height);
    }

    public void updatePixels(int x, int y, int w, int h) {
        this.updatePixelsImpl(x, y, w, h);
    }

    protected void updatePixelsImpl(int x, int y, int w, int h) {
        int x2 = x + w;
        int y2 = y + h;
        if (!this.modified) {
            this.mx1 = PApplet.max(0, x);
            this.mx2 = PApplet.min(this.width, x2);
            this.my1 = PApplet.max(0, y);
            this.my2 = PApplet.min(this.height, y2);
            this.modified = true;
        } else {
            if (x < this.mx1) {
                this.mx1 = PApplet.max(0, x);
            }

            if (x > this.mx2) {
                this.mx2 = PApplet.min(this.width, x);
            }

            if (y < this.my1) {
                this.my1 = PApplet.max(0, y);
            }

            if (y > this.my2) {
                this.my2 = PApplet.min(this.height, y);
            }

            if (x2 < this.mx1) {
                this.mx1 = PApplet.max(0, x2);
            }

            if (x2 > this.mx2) {
                this.mx2 = PApplet.min(this.width, x2);
            }

            if (y2 < this.my1) {
                this.my1 = PApplet.max(0, y2);
            }

            if (y2 > this.my2) {
                this.my2 = PApplet.min(this.height, y2);
            }
        }

    }

    public Object clone() throws CloneNotSupportedException {
        return this.get();
    }

    public void resize(int w, int h) {
        if (this.bitmap != null) {
            if (w <= 0 && h <= 0) {
                throw new IllegalArgumentException("width or height must be > 0 for resize");
            } else {
                float diff;
                if (w == 0) {
                    diff = (float)h / (float)this.height;
                    w = (int)((float)this.width * diff);
                } else if (h == 0) {
                    diff = (float)w / (float)this.width;
                    h = (int)((float)this.height * diff);
                }

                this.bitmap = Bitmap.createScaledBitmap(this.bitmap, w, h, true);
                if (this.pixels != null) {
                    this.pixels = new int[w * h];
                    this.bitmap.getPixels(this.pixels, 0, w, 0, 0, w, h);
                }

                this.width = w;
                this.height = h;
                this.pixelWidth = w * this.pixelDensity;
                this.pixelHeight = h * this.pixelDensity;
            }
        }
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setLoaded() {
        this.loaded = true;
    }

    public void setLoaded(boolean l) {
        this.loaded = l;
    }

    public int get(int x, int y) {
        if (x >= 0 && y >= 0 && x < this.width && y < this.height) {
            if (this.pixels == null) {
                return this.bitmap.getPixel(x, y);
            } else {
                switch(this.format) {
                    case 1:
                        return this.pixels[y * this.width + x] | -16777216;
                    case 2:
                        return this.pixels[y * this.width + x];
                    case 3:
                    default:
                        return 0;
                    case 4:
                        return this.pixels[y * this.width + x] << 24 | 16777215;
                }
            }
        } else {
            return 0;
        }
    }

    public PImage get(int x, int y, int w, int h) {
        int targetX = 0;
        int targetY = 0;
        int targetWidth = w;
        int targetHeight = h;
        boolean cropped = false;
        if (x < 0) {
            w += x;
            targetX = -x;
            cropped = true;
            x = 0;
        }

        if (y < 0) {
            h += y;
            targetY = -y;
            cropped = true;
            y = 0;
        }

        if (x + w > this.width) {
            w = this.width - x;
            cropped = true;
        }

        if (y + h > this.height) {
            h = this.height - y;
            cropped = true;
        }

        if (w < 0) {
            w = 0;
        }

        if (h < 0) {
            h = 0;
        }

        int targetFormat = this.format;
        if (cropped && this.format == 1) {
            targetFormat = 2;
        }

        PImage target = new PImage(targetWidth, targetHeight, targetFormat);
        target.parent = this.parent;
        if (w > 0 && h > 0) {
            this.getImpl(x, y, w, h, target, targetX, targetY);
            Bitmap nat = Bitmap.createBitmap(target.pixels, targetWidth, targetHeight, Config.ARGB_8888);
            target.setNative(nat);
        }

        return target;
    }

    protected void getImpl(int sourceX, int sourceY, int sourceWidth, int sourceHeight, PImage target, int targetX, int targetY) {
        if (this.bitmap != null) {
            this.bitmap.getPixels(target.pixels, targetY * target.width + targetX, target.width, sourceX, sourceY, sourceWidth, sourceHeight);
        } else if (this.pixels != null) {
            int sourceIndex = sourceY * this.width + sourceX;
            int targetIndex = targetY * target.width + targetX;

            for(int row = 0; row < sourceHeight; ++row) {
                System.arraycopy(this.pixels, sourceIndex, target.pixels, targetIndex, sourceWidth);
                sourceIndex += this.width;
                targetIndex += target.width;
            }
        }

    }

    public PImage get() {
        return this.get(0, 0, this.width, this.height);
    }

    public void set(int x, int y, int c) {
        if (this.pixels == null) {
            this.bitmap.setPixel(x, y, c);
        } else {
            if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
                return;
            }

            this.pixels[y * this.width + x] = c;
            this.updatePixelsImpl(x, y, 1, 1);
        }

    }

    public void set(int x, int y, PImage img) {
        if (img.format == 4) {
            throw new IllegalArgumentException("set() not available for ALPHA images");
        } else {
            int sx = 0;
            int sy = 0;
            int sw = img.width;
            int sh = img.height;
            if (x < 0) {
                sx -= x;
                sw += x;
                x = 0;
            }

            if (y < 0) {
                sy -= y;
                sh += y;
                y = 0;
            }

            if (x + sw > this.width) {
                sw = this.width - x;
            }

            if (y + sh > this.height) {
                sh = this.height - y;
            }

            if (sw > 0 && sh > 0) {
                this.setImpl(img, sx, sy, sw, sh, x, y);
            }
        }
    }

    protected void setImpl(PImage sourceImage, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int targetX, int targetY) {
        if (sourceImage.pixels == null) {
            sourceImage.loadPixels();
        }

        int srcOffset;
        if (this.pixels == null) {
            if (!this.bitmap.isMutable()) {
                this.bitmap = this.bitmap.copy(Config.ARGB_8888, true);
            }

            srcOffset = sourceY * sourceImage.width + sourceX;
            this.bitmap.setPixels(sourceImage.pixels, srcOffset, sourceImage.width, targetX, targetY, sourceWidth, sourceHeight);
        } else {
            srcOffset = sourceY * sourceImage.width + sourceX;
            int dstOffset = targetY * this.width + targetX;

            for(int y = sourceY; y < sourceY + sourceHeight; ++y) {
                System.arraycopy(sourceImage.pixels, srcOffset, this.pixels, dstOffset, sourceWidth);
                srcOffset += sourceImage.width;
                dstOffset += this.width;
            }

            this.updatePixelsImpl(targetX, targetY, sourceWidth, sourceHeight);
        }

    }

    public void mask(int[] alpha) {
        this.loadPixels();
        if (alpha.length != this.pixels.length) {
            throw new RuntimeException("The PImage used with mask() must be the same size as the applet.");
        } else {
            for(int i = 0; i < this.pixels.length; ++i) {
                this.pixels[i] = (alpha[i] & 255) << 24 | this.pixels[i] & 16777215;
            }

            this.format = 2;
            this.updatePixels();
        }
    }

    public void mask(PImage alpha) {
        if (alpha.pixels == null) {
            alpha.loadPixels();
            this.mask(alpha.pixels);
            alpha.pixels = null;
        } else {
            this.mask(alpha.pixels);
        }

    }

    public void filter(int kind) {
        this.loadPixels();
        int[] var10000;
        int i;
        label51:
        switch(kind) {
            case 1:
                for(i = 0; i < this.pixels.length; ++i) {
                    var10000 = this.pixels;
                    var10000[i] |= -16777216;
                }

                this.format = 1;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 14:
            default:
                break;
            case 11:
                this.filter(11, 1.0F);
                break;
            case 12:
                int col;
                if (this.format == 4) {
                    for(i = 0; i < this.pixels.length; ++i) {
                        col = 255 - this.pixels[i];
                        this.pixels[i] = -16777216 | col << 16 | col << 8 | col;
                    }

                    this.format = 1;
                    break;
                } else {
                    i = 0;

                    while(true) {
                        if (i >= this.pixels.length) {
                            break label51;
                        }

                        col = this.pixels[i];
                        int lum = 77 * (col >> 16 & 255) + 151 * (col >> 8 & 255) + 28 * (col & 255) >> 8;
                        this.pixels[i] = col & -16777216 | lum << 16 | lum << 8 | lum;
                        ++i;
                    }
                }
            case 13:
                i = 0;

                while(true) {
                    if (i >= this.pixels.length) {
                        break label51;
                    }

                    var10000 = this.pixels;
                    var10000[i] ^= 16777215;
                    ++i;
                }
            case 15:
                throw new RuntimeException("Use filter(POSTERIZE, int levels) instead of filter(POSTERIZE)");
            case 16:
                this.filter(16, 0.5F);
                break;
            case 17:
                this.dilate(true);
                break;
            case 18:
                this.dilate(false);
        }

        this.updatePixels();
    }

    public void filter(int kind, float param) {
        this.loadPixels();
        int i;
        int rlevel;
        int glevel;
        label53:
        switch(kind) {
            case 11:
                if (this.format == 4) {
                    this.blurAlpha(param);
                } else if (this.format == 2) {
                    this.blurARGB(param);
                } else {
                    this.blurRGB(param);
                }
                break;
            case 12:
                throw new RuntimeException("Use filter(GRAY) instead of filter(GRAY, param)");
            case 13:
                throw new RuntimeException("Use filter(INVERT) instead of filter(INVERT, param)");
            case 14:
                throw new RuntimeException("Use filter(OPAQUE) instead of filter(OPAQUE, param)");
            case 15:
                int levels = (int)param;
                if (levels >= 2 && levels <= 255) {
                    int levels1 = levels - 1;
                    i = 0;

                    while(true) {
                        if (i >= this.pixels.length) {
                            break label53;
                        }

                        rlevel = this.pixels[i] >> 16 & 255;
                        glevel = this.pixels[i] >> 8 & 255;
                        int blevel = this.pixels[i] & 255;
                        rlevel = (rlevel * levels >> 8) * 255 / levels1;
                        glevel = (glevel * levels >> 8) * 255 / levels1;
                        blevel = (blevel * levels >> 8) * 255 / levels1;
                        this.pixels[i] = -16777216 & this.pixels[i] | rlevel << 16 | glevel << 8 | blevel;
                        ++i;
                    }
                }

                throw new RuntimeException("Levels must be between 2 and 255 for filter(POSTERIZE, levels)");
            case 16:
                i = (int)(param * 255.0F);
                rlevel = 0;

                while(true) {
                    if (rlevel >= this.pixels.length) {
                        break label53;
                    }

                    glevel = Math.max((this.pixels[rlevel] & 16711680) >> 16, Math.max((this.pixels[rlevel] & '\uff00') >> 8, this.pixels[rlevel] & 255));
                    this.pixels[rlevel] = this.pixels[rlevel] & -16777216 | (glevel < i ? 0 : 16777215);
                    ++rlevel;
                }
            case 17:
                throw new RuntimeException("Use filter(ERODE) instead of filter(ERODE, param)");
            case 18:
                throw new RuntimeException("Use filter(DILATE) instead of filter(DILATE, param)");
        }

        this.updatePixels();
    }

    protected void buildBlurKernel(float r) {
        int radius = (int)(r * 3.5F);
        radius = radius < 1 ? 1 : (radius < 248 ? radius : 248);
        if (this.blurRadius != radius) {
            this.blurRadius = radius;
            this.blurKernelSize = 1 + this.blurRadius << 1;
            this.blurKernel = new int[this.blurKernelSize];
            this.blurMult = new int[this.blurKernelSize][256];
            int j = 1;

            int[] bm;
            for(int radiusi = radius - 1; j < radius; ++j) {
                int bki;
                this.blurKernel[radius + j] = this.blurKernel[radiusi] = bki = radiusi * radiusi;
                bm = this.blurMult[radius + j];
                int[] bmi = this.blurMult[radiusi--];

                for(int j = 0; j < 256; ++j) {
                    bm[j] = bmi[j] = bki * j;
                }
            }

            int bk = this.blurKernel[radius] = radius * radius;
            bm = this.blurMult[radius];

            for(j = 0; j < 256; ++j) {
                bm[j] = bk * j;
            }
        }

    }

    protected void blurAlpha(float r) {
        int[] b2 = new int[this.pixels.length];
        int yi = 0;
        this.buildBlurKernel(r);

        int sum;
        int cb;
        int read;
        int ri;
        int bk0;
        int y;
        int x;
        int i;
        for(y = 0; y < this.height; ++y) {
            for(x = 0; x < this.width; ++x) {
                sum = 0;
                cb = 0;
                read = x - this.blurRadius;
                if (read < 0) {
                    bk0 = -read;
                    read = 0;
                } else {
                    if (read >= this.width) {
                        break;
                    }

                    bk0 = 0;
                }

                for(i = bk0; i < this.blurKernelSize && read < this.width; ++i) {
                    int c = this.pixels[read + yi];
                    int[] bm = this.blurMult[i];
                    cb += bm[c & 255];
                    sum += this.blurKernel[i];
                    ++read;
                }

                ri = yi + x;
                b2[ri] = cb / sum;
            }

            yi += this.width;
        }

        yi = 0;
        int ym = -this.blurRadius;
        int ymi = ym * this.width;

        for(y = 0; y < this.height; ++y) {
            for(x = 0; x < this.width; ++x) {
                sum = 0;
                cb = 0;
                if (ym < 0) {
                    bk0 = ri = -ym;
                    read = x;
                } else {
                    if (ym >= this.height) {
                        break;
                    }

                    bk0 = 0;
                    ri = ym;
                    read = x + ymi;
                }

                for(i = bk0; i < this.blurKernelSize && ri < this.height; ++i) {
                    int[] bm = this.blurMult[i];
                    cb += bm[b2[read]];
                    sum += this.blurKernel[i];
                    ++ri;
                    read += this.width;
                }

                this.pixels[x + yi] = cb / sum;
            }

            yi += this.width;
            ymi += this.width;
            ++ym;
        }

    }

    protected void blurRGB(float r) {
        int[] r2 = new int[this.pixels.length];
        int[] g2 = new int[this.pixels.length];
        int[] b2 = new int[this.pixels.length];
        int yi = 0;
        this.buildBlurKernel(r);

        int sum;
        int cr;
        int cg;
        int cb;
        int read;
        int ri;
        int bk0;
        int y;
        int x;
        int i;
        for(y = 0; y < this.height; ++y) {
            for(x = 0; x < this.width; ++x) {
                sum = 0;
                cr = 0;
                cg = 0;
                cb = 0;
                read = x - this.blurRadius;
                if (read < 0) {
                    bk0 = -read;
                    read = 0;
                } else {
                    if (read >= this.width) {
                        break;
                    }

                    bk0 = 0;
                }

                for(i = bk0; i < this.blurKernelSize && read < this.width; ++i) {
                    int c = this.pixels[read + yi];
                    int[] bm = this.blurMult[i];
                    cr += bm[(c & 16711680) >> 16];
                    cg += bm[(c & '\uff00') >> 8];
                    cb += bm[c & 255];
                    sum += this.blurKernel[i];
                    ++read;
                }

                ri = yi + x;
                r2[ri] = cr / sum;
                g2[ri] = cg / sum;
                b2[ri] = cb / sum;
            }

            yi += this.width;
        }

        yi = 0;
        int ym = -this.blurRadius;
        int ymi = ym * this.width;

        for(y = 0; y < this.height; ++y) {
            for(x = 0; x < this.width; ++x) {
                sum = 0;
                cr = 0;
                cg = 0;
                cb = 0;
                if (ym < 0) {
                    bk0 = ri = -ym;
                    read = x;
                } else {
                    if (ym >= this.height) {
                        break;
                    }

                    bk0 = 0;
                    ri = ym;
                    read = x + ymi;
                }

                for(i = bk0; i < this.blurKernelSize && ri < this.height; ++i) {
                    int[] bm = this.blurMult[i];
                    cr += bm[r2[read]];
                    cg += bm[g2[read]];
                    cb += bm[b2[read]];
                    sum += this.blurKernel[i];
                    ++ri;
                    read += this.width;
                }

                this.pixels[x + yi] = -16777216 | cr / sum << 16 | cg / sum << 8 | cb / sum;
            }

            yi += this.width;
            ymi += this.width;
            ++ym;
        }

    }

    protected void blurARGB(float r) {
        int wh = this.pixels.length;
        int[] r2 = new int[wh];
        int[] g2 = new int[wh];
        int[] b2 = new int[wh];
        int[] a2 = new int[wh];
        int yi = 0;
        this.buildBlurKernel(r);

        int sum;
        int cr;
        int cg;
        int cb;
        int ca;
        int read;
        int ri;
        int bk0;
        int y;
        int x;
        int i;
        for(y = 0; y < this.height; ++y) {
            for(x = 0; x < this.width; ++x) {
                sum = 0;
                ca = 0;
                cr = 0;
                cg = 0;
                cb = 0;
                read = x - this.blurRadius;
                if (read < 0) {
                    bk0 = -read;
                    read = 0;
                } else {
                    if (read >= this.width) {
                        break;
                    }

                    bk0 = 0;
                }

                for(i = bk0; i < this.blurKernelSize && read < this.width; ++i) {
                    int c = this.pixels[read + yi];
                    int[] bm = this.blurMult[i];
                    ca += bm[(c & -16777216) >>> 24];
                    cr += bm[(c & 16711680) >> 16];
                    cg += bm[(c & '\uff00') >> 8];
                    cb += bm[c & 255];
                    sum += this.blurKernel[i];
                    ++read;
                }

                ri = yi + x;
                a2[ri] = ca / sum;
                r2[ri] = cr / sum;
                g2[ri] = cg / sum;
                b2[ri] = cb / sum;
            }

            yi += this.width;
        }

        yi = 0;
        int ym = -this.blurRadius;
        int ymi = ym * this.width;

        for(y = 0; y < this.height; ++y) {
            for(x = 0; x < this.width; ++x) {
                sum = 0;
                ca = 0;
                cr = 0;
                cg = 0;
                cb = 0;
                if (ym < 0) {
                    bk0 = ri = -ym;
                    read = x;
                } else {
                    if (ym >= this.height) {
                        break;
                    }

                    bk0 = 0;
                    ri = ym;
                    read = x + ymi;
                }

                for(i = bk0; i < this.blurKernelSize && ri < this.height; ++i) {
                    int[] bm = this.blurMult[i];
                    ca += bm[a2[read]];
                    cr += bm[r2[read]];
                    cg += bm[g2[read]];
                    cb += bm[b2[read]];
                    sum += this.blurKernel[i];
                    ++ri;
                    read += this.width;
                }

                this.pixels[x + yi] = ca / sum << 24 | cr / sum << 16 | cg / sum << 8 | cb / sum;
            }

            yi += this.width;
            ymi += this.width;
            ++ym;
        }

    }

    protected void dilate(boolean isInverted) {
        int currIdx = 0;
        int maxIdx = this.pixels.length;
        int[] out = new int[maxIdx];
        int currRowIdx;
        int maxRowIdx;
        int colOrig;
        int colOut;
        int idxLeft;
        int idxRight;
        int idxUp;
        int idxDown;
        int colUp;
        int colLeft;
        int colDown;
        int colRight;
        int currLum;
        int lumLeft;
        int lumRight;
        int lumUp;
        int lumDown;
        if (!isInverted) {
            while(currIdx < maxIdx) {
                currRowIdx = currIdx;

                for(maxRowIdx = currIdx + this.width; currIdx < maxRowIdx; out[currIdx++] = colOut) {
                    colOrig = colOut = this.pixels[currIdx];
                    idxLeft = currIdx - 1;
                    idxRight = currIdx + 1;
                    idxUp = currIdx - this.width;
                    idxDown = currIdx + this.width;
                    if (idxLeft < currRowIdx) {
                        idxLeft = currIdx;
                    }

                    if (idxRight >= maxRowIdx) {
                        idxRight = currIdx;
                    }

                    if (idxUp < 0) {
                        idxUp = 0;
                    }

                    if (idxDown >= maxIdx) {
                        idxDown = currIdx;
                    }

                    colUp = this.pixels[idxUp];
                    colLeft = this.pixels[idxLeft];
                    colDown = this.pixels[idxDown];
                    colRight = this.pixels[idxRight];
                    currLum = 77 * (colOrig >> 16 & 255) + 151 * (colOrig >> 8 & 255) + 28 * (colOrig & 255);
                    lumLeft = 77 * (colLeft >> 16 & 255) + 151 * (colLeft >> 8 & 255) + 28 * (colLeft & 255);
                    lumRight = 77 * (colRight >> 16 & 255) + 151 * (colRight >> 8 & 255) + 28 * (colRight & 255);
                    lumUp = 77 * (colUp >> 16 & 255) + 151 * (colUp >> 8 & 255) + 28 * (colUp & 255);
                    lumDown = 77 * (colDown >> 16 & 255) + 151 * (colDown >> 8 & 255) + 28 * (colDown & 255);
                    if (lumLeft > currLum) {
                        colOut = colLeft;
                        currLum = lumLeft;
                    }

                    if (lumRight > currLum) {
                        colOut = colRight;
                        currLum = lumRight;
                    }

                    if (lumUp > currLum) {
                        colOut = colUp;
                        currLum = lumUp;
                    }

                    if (lumDown > currLum) {
                        colOut = colDown;
                    }
                }
            }
        } else {
            while(currIdx < maxIdx) {
                currRowIdx = currIdx;

                for(maxRowIdx = currIdx + this.width; currIdx < maxRowIdx; out[currIdx++] = colOut) {
                    colOrig = colOut = this.pixels[currIdx];
                    idxLeft = currIdx - 1;
                    idxRight = currIdx + 1;
                    idxUp = currIdx - this.width;
                    idxDown = currIdx + this.width;
                    if (idxLeft < currRowIdx) {
                        idxLeft = currIdx;
                    }

                    if (idxRight >= maxRowIdx) {
                        idxRight = currIdx;
                    }

                    if (idxUp < 0) {
                        idxUp = 0;
                    }

                    if (idxDown >= maxIdx) {
                        idxDown = currIdx;
                    }

                    colUp = this.pixels[idxUp];
                    colLeft = this.pixels[idxLeft];
                    colDown = this.pixels[idxDown];
                    colRight = this.pixels[idxRight];
                    currLum = 77 * (colOrig >> 16 & 255) + 151 * (colOrig >> 8 & 255) + 28 * (colOrig & 255);
                    lumLeft = 77 * (colLeft >> 16 & 255) + 151 * (colLeft >> 8 & 255) + 28 * (colLeft & 255);
                    lumRight = 77 * (colRight >> 16 & 255) + 151 * (colRight >> 8 & 255) + 28 * (colRight & 255);
                    lumUp = 77 * (colUp >> 16 & 255) + 151 * (colUp >> 8 & 255) + 28 * (colUp & 255);
                    lumDown = 77 * (colDown >> 16 & 255) + 151 * (colDown >> 8 & 255) + 28 * (colDown & 255);
                    if (lumLeft < currLum) {
                        colOut = colLeft;
                        currLum = lumLeft;
                    }

                    if (lumRight < currLum) {
                        colOut = colRight;
                        currLum = lumRight;
                    }

                    if (lumUp < currLum) {
                        colOut = colUp;
                        currLum = lumUp;
                    }

                    if (lumDown < currLum) {
                        colOut = colDown;
                    }
                }
            }
        }

        System.arraycopy(out, 0, this.pixels, 0, maxIdx);
    }

    public void copy(int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        this.blend(this, sx, sy, sw, sh, dx, dy, dw, dh, 0);
    }

    public void copy(PImage src, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        this.blend(src, sx, sy, sw, sh, dx, dy, dw, dh, 0);
    }

    public static int blendColor(int c1, int c2, int mode) {
        switch(mode) {
            case 0:
                return c2;
            case 1:
                return blend_blend(c1, c2);
            case 2:
                return blend_add_pin(c1, c2);
            case 4:
                return blend_sub_pin(c1, c2);
            case 8:
                return blend_lightest(c1, c2);
            case 16:
                return blend_darkest(c1, c2);
            case 32:
                return blend_difference(c1, c2);
            case 64:
                return blend_exclusion(c1, c2);
            case 128:
                return blend_multiply(c1, c2);
            case 256:
                return blend_screen(c1, c2);
            case 512:
                return blend_overlay(c1, c2);
            case 1024:
                return blend_hard_light(c1, c2);
            case 2048:
                return blend_soft_light(c1, c2);
            case 4096:
                return blend_dodge(c1, c2);
            case 8192:
                return blend_burn(c1, c2);
            default:
                return 0;
        }
    }

    public void blend(int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh, int mode) {
        this.blend(this, sx, sy, sw, sh, dx, dy, dw, dh, mode);
    }

    public void blend(PImage src, int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh, int mode) {
        int sx2 = sx + sw;
        int sy2 = sy + sh;
        int dx2 = dx + dw;
        int dy2 = dy + dh;
        this.loadPixels();
        if (src == this) {
            if (this.intersect(sx, sy, sx2, sy2, dx, dy, dx2, dy2)) {
                this.blit_resize(this.get(sx, sy, sw, sh), 0, 0, sw, sh, this.pixels, this.pixelWidth, this.pixelHeight, dx, dy, dx2, dy2, mode);
            } else {
                this.blit_resize(src, sx, sy, sx2, sy2, this.pixels, this.pixelWidth, this.pixelHeight, dx, dy, dx2, dy2, mode);
            }
        } else {
            src.loadPixels();
            this.blit_resize(src, sx, sy, sx2, sy2, this.pixels, this.pixelWidth, this.pixelHeight, dx, dy, dx2, dy2, mode);
        }

        this.updatePixels();
    }

    private boolean intersect(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
        int sw = sx2 - sx1 + 1;
        int sh = sy2 - sy1 + 1;
        int dw = dx2 - dx1 + 1;
        int dh = dy2 - dy1 + 1;
        int h;
        if (dx1 < sx1) {
            dw += dx1 - sx1;
            if (dw > sw) {
                dw = sw;
            }
        } else {
            h = sw + sx1 - dx1;
            if (dw > h) {
                dw = h;
            }
        }

        if (dy1 < sy1) {
            dh += dy1 - sy1;
            if (dh > sh) {
                dh = sh;
            }
        } else {
            h = sh + sy1 - dy1;
            if (dh > h) {
                dh = h;
            }
        }

        return dw > 0 && dh > 0;
    }

    private void blit_resize(PImage img, int srcX1, int srcY1, int srcX2, int srcY2, int[] destPixels, int screenW, int screenH, int destX1, int destY1, int destX2, int destY2, int mode) {
        if (srcX1 < 0) {
            srcX1 = 0;
        }

        if (srcY1 < 0) {
            srcY1 = 0;
        }

        if (srcX2 > img.pixelWidth) {
            srcX2 = img.pixelWidth;
        }

        if (srcY2 > img.pixelHeight) {
            srcY2 = img.pixelHeight;
        }

        int srcW = srcX2 - srcX1;
        int srcH = srcY2 - srcY1;
        int destW = destX2 - destX1;
        int destH = destY2 - destY1;
        boolean smooth = true;
        if (!smooth) {
            ++srcW;
            ++srcH;
        }

        if (destW > 0 && destH > 0 && srcW > 0 && srcH > 0 && destX1 < screenW && destY1 < screenH && srcX1 < img.pixelWidth && srcY1 < img.pixelHeight) {
            int dx = (int)((float)srcW / (float)destW * 32768.0F);
            int dy = (int)((float)srcH / (float)destH * 32768.0F);
            this.srcXOffset = destX1 < 0 ? -destX1 * dx : srcX1 * '耀';
            this.srcYOffset = destY1 < 0 ? -destY1 * dy : srcY1 * '耀';
            if (destX1 < 0) {
                destW += destX1;
                destX1 = 0;
            }

            if (destY1 < 0) {
                destH += destY1;
                destY1 = 0;
            }

            destW = min(destW, screenW - destX1);
            destH = min(destH, screenH - destY1);
            int destOffset = destY1 * screenW + destX1;
            this.srcBuffer = img.pixels;
            int y;
            int x;
            if (smooth) {
                this.iw = img.pixelWidth;
                this.iw1 = img.pixelWidth - 1;
                this.ih1 = img.pixelHeight - 1;
                switch(mode) {
                    case 0:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = this.filter_bilinear();
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 1:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_blend(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 2:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_add_pin(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 4:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_sub_pin(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 8:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_lightest(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 16:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_darkest(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 32:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_difference(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 64:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_exclusion(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 128:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_multiply(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 256:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_screen(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 512:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_overlay(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 1024:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_hard_light(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 2048:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_soft_light(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 4096:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_dodge(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 8192:
                        for(y = 0; y < destH; ++y) {
                            this.filter_new_scanline();

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_burn(destPixels[destOffset + x], this.filter_bilinear());
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }
                }
            } else {
                switch(mode) {
                    case 0:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = this.srcBuffer[this.sY + (this.sX >> 15)];
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 1:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_blend(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 2:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_add_pin(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 4:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_sub_pin(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 8:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_lightest(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 16:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_darkest(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 32:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_difference(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 64:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_exclusion(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 128:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_multiply(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 256:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_screen(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 512:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_overlay(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 1024:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_hard_light(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 2048:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_soft_light(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 4096:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_dodge(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }

                        return;
                    case 8192:
                        for(y = 0; y < destH; ++y) {
                            this.sX = this.srcXOffset;
                            this.sY = (this.srcYOffset >> 15) * img.pixelWidth;

                            for(x = 0; x < destW; ++x) {
                                destPixels[destOffset + x] = blend_burn(destPixels[destOffset + x], this.srcBuffer[this.sY + (this.sX >> 15)]);
                                this.sX += dx;
                            }

                            destOffset += screenW;
                            this.srcYOffset += dy;
                        }
                }
            }

        }
    }

    private void filter_new_scanline() {
        this.sX = this.srcXOffset;
        this.fracV = this.srcYOffset & 32767;
        this.ifV = 32767 - this.fracV + 1;
        this.v1 = (this.srcYOffset >> 15) * this.iw;
        this.v2 = min((this.srcYOffset >> 15) + 1, this.ih1) * this.iw;
    }

    private int filter_bilinear() {
        this.fracU = this.sX & 32767;
        this.ifU = 32767 - this.fracU + 1;
        this.ul = this.ifU * this.ifV >> 15;
        this.ll = this.ifU - this.ul;
        this.ur = this.ifV - this.ul;
        this.lr = '耀' - this.ul - this.ll - this.ur;
        this.u1 = this.sX >> 15;
        this.u2 = min(this.u1 + 1, this.iw1);
        this.cUL = this.srcBuffer[this.v1 + this.u1];
        this.cUR = this.srcBuffer[this.v1 + this.u2];
        this.cLL = this.srcBuffer[this.v2 + this.u1];
        this.cLR = this.srcBuffer[this.v2 + this.u2];
        this.r = this.ul * ((this.cUL & 16711680) >> 16) + this.ll * ((this.cLL & 16711680) >> 16) + this.ur * ((this.cUR & 16711680) >> 16) + this.lr * ((this.cLR & 16711680) >> 16) << 1 & 16711680;
        this.g = this.ul * (this.cUL & '\uff00') + this.ll * (this.cLL & '\uff00') + this.ur * (this.cUR & '\uff00') + this.lr * (this.cLR & '\uff00') >>> 15 & '\uff00';
        this.b = this.ul * (this.cUL & 255) + this.ll * (this.cLL & 255) + this.ur * (this.cUR & 255) + this.lr * (this.cLR & 255) >>> 15;
        this.a = this.ul * ((this.cUL & -16777216) >>> 24) + this.ll * ((this.cLL & -16777216) >>> 24) + this.ur * ((this.cUR & -16777216) >>> 24) + this.lr * ((this.cLR & -16777216) >>> 24) << 9 & -16777216;
        return this.a | this.r | this.g | this.b;
    }

    private static int min(int a, int b) {
        return a < b ? a : b;
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    private static int blend_blend(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + (src & 16711935) * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + (src & '\uff00') * s_a >>> 8 & '\uff00';
    }

    private static int blend_add_pin(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int rb = (dst & 16711935) + ((src & 16711935) * s_a >>> 8 & 16711935);
        int gn = (dst & '\uff00') + ((src & '\uff00') * s_a >>> 8);
        return min((dst >>> 24) + a, 255) << 24 | min(rb & -65536, 16711680) | min(gn & 16776960, 65280) | min(rb & '\uffff', 255);
    }

    private static int blend_sub_pin(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int rb = (src & 16711935) * s_a >>> 8;
        int gn = (src & '\uff00') * s_a >>> 8;
        return min((dst >>> 24) + a, 255) << 24 | max((dst & 16711680) - (rb & 16711680), 0) | max((dst & '\uff00') - (gn & '\uff00'), 0) | max((dst & 255) - (rb & 255), 0);
    }

    private static int blend_lightest(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int rb = max(src & 16711680, dst & 16711680) | max(src & 255, dst & 255);
        int gn = max(src & '\uff00', dst & '\uff00');
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + rb * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + gn * s_a >>> 8 & '\uff00';
    }

    private static int blend_darkest(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int rb = min(src & 16711680, dst & 16711680) | min(src & 255, dst & 255);
        int gn = min(src & '\uff00', dst & '\uff00');
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + rb * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + gn * s_a >>> 8 & '\uff00';
    }

    private static int blend_difference(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int r = (dst & 16711680) - (src & 16711680);
        int b = (dst & 255) - (src & 255);
        int g = (dst & '\uff00') - (src & '\uff00');
        int rb = (r < 0 ? -r : r) | (b < 0 ? -b : b);
        int gn = g < 0 ? -g : g;
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + rb * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + gn * s_a >>> 8 & '\uff00';
    }

    private static int blend_exclusion(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int d_rb = dst & 16711935;
        int d_gn = dst & '\uff00';
        int s_gn = src & '\uff00';
        int f_r = (dst & 16711680) >> 16;
        int f_b = dst & 255;
        int rb_sub = ((src & 16711680) * (f_r + (f_r >= 127 ? 1 : 0)) | (src & 255) * (f_b + (f_b >= 127 ? 1 : 0))) >>> 7 & 33489407;
        int gn_sub = s_gn * (d_gn + (d_gn >= 32512 ? 256 : 0)) >>> 15 & 130816;
        return min((dst >>> 24) + a, 255) << 24 | d_rb * d_a + (d_rb + (src & 16711935) - rb_sub) * s_a >>> 8 & 16711935 | d_gn * d_a + (d_gn + s_gn - gn_sub) * s_a >>> 8 & '\uff00';
    }

    private static int blend_multiply(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int d_gn = dst & '\uff00';
        int f_r = (dst & 16711680) >> 16;
        int f_b = dst & 255;
        int rb = ((src & 16711680) * (f_r + 1) | (src & 255) * (f_b + 1)) >>> 8 & 16711935;
        int gn = (src & '\uff00') * (d_gn + 256) >>> 16 & '\uff00';
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + rb * s_a >>> 8 & 16711935 | d_gn * d_a + gn * s_a >>> 8 & '\uff00';
    }

    private static int blend_screen(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int d_rb = dst & 16711935;
        int d_gn = dst & '\uff00';
        int s_gn = src & '\uff00';
        int f_r = (dst & 16711680) >> 16;
        int f_b = dst & 255;
        int rb_sub = ((src & 16711680) * (f_r + 1) | (src & 255) * (f_b + 1)) >>> 8 & 16711935;
        int gn_sub = s_gn * (d_gn + 256) >>> 16 & '\uff00';
        return min((dst >>> 24) + a, 255) << 24 | d_rb * d_a + (d_rb + (src & 16711935) - rb_sub) * s_a >>> 8 & 16711935 | d_gn * d_a + (d_gn + s_gn - gn_sub) * s_a >>> 8 & '\uff00';
    }

    private static int blend_overlay(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int d_r = dst & 16711680;
        int d_g = dst & '\uff00';
        int d_b = dst & 255;
        int s_r = src & 16711680;
        int s_g = src & '\uff00';
        int s_b = src & 255;
        int r = d_r < 8388608 ? d_r * ((s_r >>> 16) + 1) >>> 7 : 16711680 - ((256 - (s_r >>> 16)) * (16711680 - d_r) >>> 7);
        int g = d_g < 32768 ? d_g * (s_g + 256) >>> 15 : '\uff00' - ((65536 - s_g) * ('\uff00' - d_g) >>> 15);
        int b = d_b < 128 ? d_b * (s_b + 1) >>> 7 : '\uff00' - ((256 - s_b) * (255 - d_b) << 1) >>> 8;
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + ((r | b) & 16711935) * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + (g & '\uff00') * s_a >>> 8 & '\uff00';
    }

    private static int blend_hard_light(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int d_r = dst & 16711680;
        int d_g = dst & '\uff00';
        int d_b = dst & 255;
        int s_r = src & 16711680;
        int s_g = src & '\uff00';
        int s_b = src & 255;
        int r = s_r < 8388608 ? s_r * ((d_r >>> 16) + 1) >>> 7 : 16711680 - ((256 - (d_r >>> 16)) * (16711680 - s_r) >>> 7);
        int g = s_g < 32768 ? s_g * (d_g + 256) >>> 15 : '\uff00' - ((65536 - d_g) * ('\uff00' - s_g) >>> 15);
        int b = s_b < 128 ? s_b * (d_b + 1) >>> 7 : '\uff00' - ((256 - d_b) * (255 - s_b) << 1) >>> 8;
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + ((r | b) & 16711935) * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + (g & '\uff00') * s_a >>> 8 & '\uff00';
    }

    private static int blend_soft_light(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int d_r = dst & 16711680;
        int d_g = dst & '\uff00';
        int d_b = dst & 255;
        int s_r1 = src & 255;
        int s_g1 = src & 255;
        int s_b1 = src & 255;
        int d_r1 = (d_r >> 16) + ((float)s_r1 < 7.0F ? 1 : 0);
        int d_g1 = (d_g >> 8) + ((float)s_g1 < 7.0F ? 1 : 0);
        int d_b1 = d_b + ((float)s_b1 < 7.0F ? 1 : 0);
        int r = (s_r1 * d_r >> 7) + 255 * d_r1 * (d_r1 + 1) - (s_r1 * d_r1 * d_r1 << 1) & 16711680;
        int g = (s_g1 * d_g << 1) + 255 * d_g1 * (d_g1 + 1) - (s_g1 * d_g1 * d_g1 << 1) >>> 8 & '\uff00';
        int b = (s_b1 * d_b << 9) + 255 * d_b1 * (d_b1 + 1) - (s_b1 * d_b1 * d_b1 << 1) >>> 16;
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + (r | b) * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + g * s_a >>> 8 & '\uff00';
    }

    private static int blend_dodge(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int r = (dst & 16711680) / (256 - ((src & 16711680) >> 16));
        int g = ((dst & '\uff00') << 8) / (256 - ((src & '\uff00') >> 8));
        int b = ((dst & 255) << 8) / (256 - (src & 255));
        int rb = (r > 65280 ? 16711680 : r << 8 & 16711680) | (b > 255 ? 255 : b);
        int gn = g > 65280 ? '\uff00' : g & '\uff00';
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + rb * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + gn * s_a >>> 8 & '\uff00';
    }

    private static int blend_burn(int dst, int src) {
        int a = src >>> 24;
        int s_a = a + (a >= 127 ? 1 : 0);
        int d_a = 256 - s_a;
        int r = (16711680 - (dst & 16711680)) / (1 + (src & 255));
        int g = ('\uff00' - (dst & '\uff00') << 8) / (1 + (src & 255));
        int b = (255 - (dst & 255) << 8) / (1 + (src & 255));
        int rb = 16711935 - (r > 65280 ? 16711680 : r << 8 & 16711680) - (b > 255 ? 255 : b);
        int gn = '\uff00' - (g > 65280 ? '\uff00' : g & '\uff00');
        return min((dst >>> 24) + a, 255) << 24 | (dst & 16711935) * d_a + rb * s_a >>> 8 & 16711935 | (dst & '\uff00') * d_a + gn * s_a >>> 8 & '\uff00';
    }

    protected static PImage loadTIFF(byte[] tiff) {
        if (tiff[42] == tiff[102] && tiff[43] == tiff[103]) {
            int width = (tiff[30] & 255) << 8 | tiff[31] & 255;
            int height = (tiff[42] & 255) << 8 | tiff[43] & 255;
            int count = (tiff[114] & 255) << 24 | (tiff[115] & 255) << 16 | (tiff[116] & 255) << 8 | tiff[117] & 255;
            if (count != width * height * 3) {
                System.err.println("Error: Processing can only read its own TIFF files. (" + width + ", " + height + ")");
                return null;
            } else {
                for(int i = 0; i < TIFF_HEADER.length; ++i) {
                    if (i != 30 && i != 31 && i != 42 && i != 43 && i != 102 && i != 103 && i != 114 && i != 115 && i != 116 && i != 117 && tiff[i] != TIFF_HEADER[i]) {
                        System.err.println("Error: Processing can only read its own TIFF files. (" + i + ")");
                        return null;
                    }
                }

                PImage outgoing = new PImage(width, height, 1);
                int index = 768;
                count /= 3;

                for(int i = 0; i < count; ++i) {
                    outgoing.pixels[i] = -16777216 | (tiff[index++] & 255) << 16 | (tiff[index++] & 255) << 8 | tiff[index++] & 255;
                }

                return outgoing;
            }
        } else {
            System.err.println("Error: Processing can only read its own TIFF files.");
            return null;
        }
    }

    protected boolean saveTIFF(OutputStream output) {
        try {
            byte[] tiff = new byte[768];
            System.arraycopy(TIFF_HEADER, 0, tiff, 0, TIFF_HEADER.length);
            tiff[30] = (byte)(this.width >> 8 & 255);
            tiff[31] = (byte)(this.width & 255);
            tiff[42] = tiff[102] = (byte)(this.height >> 8 & 255);
            tiff[43] = tiff[103] = (byte)(this.height & 255);
            int count = this.width * this.height * 3;
            tiff[114] = (byte)(count >> 24 & 255);
            tiff[115] = (byte)(count >> 16 & 255);
            tiff[116] = (byte)(count >> 8 & 255);
            tiff[117] = (byte)(count & 255);
            output.write(tiff);

            for(int i = 0; i < this.pixels.length; ++i) {
                output.write(this.pixels[i] >> 16 & 255);
                output.write(this.pixels[i] >> 8 & 255);
                output.write(this.pixels[i] & 255);
            }

            output.flush();
            return true;
        } catch (IOException var5) {
            var5.printStackTrace();
            return false;
        }
    }

    protected boolean saveTGA(OutputStream output) {
        byte[] header = new byte[18];
        if (this.format == 4) {
            header[2] = 11;
            header[16] = 8;
            header[17] = 40;
        } else if (this.format == 1) {
            header[2] = 10;
            header[16] = 24;
            header[17] = 32;
        } else {
            if (this.format != 2) {
                throw new RuntimeException("Image format not recognized inside save()");
            }

            header[2] = 10;
            header[16] = 32;
            header[17] = 40;
        }

        header[12] = (byte)(this.width & 255);
        header[13] = (byte)(this.width >> 8);
        header[14] = (byte)(this.height & 255);
        header[15] = (byte)(this.height >> 8);

        try {
            output.write(header);
            int maxLen = this.height * this.width;
            int index = 0;
            int[] currChunk = new int[128];
            int col;
            boolean isRLE;
            int rle;
            int i;
            if (this.format == 4) {
                for(; index < maxLen; index += rle) {
                    isRLE = false;
                    rle = 1;

                    for(currChunk[0] = col = this.pixels[index] & 255; index + rle < maxLen; ++rle) {
                        if (col != (this.pixels[index + rle] & 255) || rle == 128) {
                            isRLE = rle > 1;
                            break;
                        }
                    }

                    if (isRLE) {
                        output.write(128 | rle - 1);
                        output.write(col);
                    } else {
                        for(rle = 1; index + rle < maxLen; ++rle) {
                            i = this.pixels[index + rle] & 255;
                            if ((col == i || rle >= 128) && rle >= 3) {
                                if (col == i) {
                                    rle -= 2;
                                }
                                break;
                            }

                            col = i;
                            currChunk[rle] = i;
                        }

                        output.write(rle - 1);

                        for(i = 0; i < rle; ++i) {
                            output.write(currChunk[i]);
                        }
                    }
                }
            } else {
                for(; index < maxLen; index += rle) {
                    isRLE = false;
                    currChunk[0] = col = this.pixels[index];

                    for(rle = 1; index + rle < maxLen; ++rle) {
                        if (col != this.pixels[index + rle] || rle == 128) {
                            isRLE = rle > 1;
                            break;
                        }
                    }

                    if (isRLE) {
                        output.write(128 | rle - 1);
                        output.write(col & 255);
                        output.write(col >> 8 & 255);
                        output.write(col >> 16 & 255);
                        if (this.format == 2) {
                            output.write(col >>> 24 & 255);
                        }
                    } else {
                        for(rle = 1; index + rle < maxLen; ++rle) {
                            if ((col == this.pixels[index + rle] || rle >= 128) && rle >= 3) {
                                if (col == this.pixels[index + rle]) {
                                    rle -= 2;
                                }
                                break;
                            }

                            currChunk[rle] = col = this.pixels[index + rle];
                        }

                        output.write(rle - 1);
                        if (this.format == 2) {
                            for(i = 0; i < rle; ++i) {
                                col = currChunk[i];
                                output.write(col & 255);
                                output.write(col >> 8 & 255);
                                output.write(col >> 16 & 255);
                                output.write(col >>> 24 & 255);
                            }
                        } else {
                            for(i = 0; i < rle; ++i) {
                                col = currChunk[i];
                                output.write(col & 255);
                                output.write(col >> 8 & 255);
                                output.write(col >> 16 & 255);
                            }
                        }
                    }
                }
            }

            output.flush();
            return true;
        } catch (IOException var10) {
            var10.printStackTrace();
            return false;
        }
    }

    public boolean save(String path) {
        boolean success = false;
        this.loadPixels();

        try {
            OutputStream output = new BufferedOutputStream(this.parent.createOutput(path), 16384);
            String lower = path.toLowerCase();
            String extension = lower.substring(lower.lastIndexOf(46) + 1);
            Bitmap outgoing;
            if (!extension.equals("jpg") && !extension.equals("jpeg")) {
                if (extension.equals("png")) {
                    outgoing = Bitmap.createBitmap(this.pixels, this.width, this.height, Config.ARGB_8888);
                    success = outgoing.compress(CompressFormat.PNG, 100, output);
                } else if (extension.equals("tga")) {
                    success = this.saveTGA(output);
                } else {
                    if (!extension.equals("tif") && !extension.equals("tiff")) {
                        path = path + ".tif";
                    }

                    success = this.saveTIFF(output);
                }
            } else {
                outgoing = Bitmap.createBitmap(this.pixels, this.width, this.height, Config.ARGB_8888);
                success = outgoing.compress(CompressFormat.JPEG, 100, output);
            }

            output.flush();
            output.close();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        if (!success) {
            System.err.println("Could not write the image to " + path);
        }

        return success;
    }
}

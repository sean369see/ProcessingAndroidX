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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;

public class PFont implements PConstants {
    protected int glyphCount;
    protected PFont.Glyph[] glyphs;
    protected String name;
    protected String psname;
    protected int size;
    protected boolean smooth;
    protected int ascent;
    protected int descent;
    protected int[] ascii;
    protected boolean lazy;
    protected Typeface typeface;
    protected boolean subsetting;
    protected boolean typefaceSearched;
    protected static Typeface[] typefaces;
    Bitmap lazyBitmap;
    Canvas lazyCanvas;
    Paint lazyPaint;
    int[] lazySamples;
    protected HashMap<PGraphics, Object> cacheMap;
    static final char[] EXTRA_CHARS = new char[]{'\u0080', '\u0081', '\u0082', '\u0083', '\u0084', '\u0085', '\u0086', '\u0087', '\u0088', '\u0089', '\u008a', '\u008b', '\u008c', '\u008d', '\u008e', '\u008f', '\u0090', '\u0091', '\u0092', '\u0093', '\u0094', '\u0095', '\u0096', '\u0097', '\u0098', '\u0099', '\u009a', '\u009b', '\u009c', '\u009d', '\u009e', '\u009f', ' ', '¡', '¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '\u00ad', '®', '¯', '°', '±', '´', 'µ', '¶', '·', '¸', 'º', '»', '¿', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', '×', 'Ø', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'ß', 'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', '÷', 'ø', 'ù', 'ú', 'û', 'ü', 'ý', 'ÿ', 'Ă', 'ă', 'Ą', 'ą', 'Ć', 'ć', 'Č', 'č', 'Ď', 'ď', 'Đ', 'đ', 'Ę', 'ę', 'Ě', 'ě', 'ı', 'Ĺ', 'ĺ', 'Ľ', 'ľ', 'Ł', 'ł', 'Ń', 'ń', 'Ň', 'ň', 'Ő', 'ő', 'Œ', 'œ', 'Ŕ', 'ŕ', 'Ř', 'ř', 'Ś', 'ś', 'Ş', 'ş', 'Š', 'š', 'Ţ', 'ţ', 'Ť', 'ť', 'Ů', 'ů', 'Ű', 'ű', 'Ÿ', 'Ź', 'ź', 'Ż', 'ż', 'Ž', 'ž', 'ƒ', 'ˆ', 'ˇ', '˘', '˙', '˚', '˛', '˜', '˝', 'Ω', 'π', '–', '—', '‘', '’', '‚', '“', '”', '„', '†', '‡', '•', '…', '‰', '‹', '›', '⁄', '€', '™', '∂', '∆', '∏', '∑', '√', '∞', '∫', '≈', '≠', '≤', '≥', '◊', '\uf8ff', 'ﬁ', 'ﬂ'};
    public static char[] CHARSET;
    static HashMap<String, Typeface> typefaceMap;
    static String[] fontList;

    public PFont() {
    }

    public PFont(Typeface font, int size, boolean smooth) {
        this(font, size, smooth, (char[])null);
    }

    public PFont(Typeface font, int size, boolean smooth, char[] charset) {
        this.typeface = font;
        this.smooth = smooth;
        this.name = "";
        this.psname = "";
        this.size = size;
        int initialCount = 10;
        this.glyphs = new PFont.Glyph[initialCount];
        this.ascii = new int[128];
        Arrays.fill(this.ascii, -1);
        int mbox3 = size * 3;
        this.lazyBitmap = Bitmap.createBitmap(mbox3, mbox3, Config.ARGB_8888);
        this.lazyCanvas = new Canvas(this.lazyBitmap);
        this.lazyPaint = new Paint();
        this.lazyPaint.setAntiAlias(smooth);
        this.lazyPaint.setTypeface(font);
        this.lazyPaint.setTextSize((float)size);
        this.lazySamples = new int[mbox3 * mbox3];
        if (charset == null) {
            this.lazy = true;
        } else {
            Arrays.sort(charset);
            this.glyphs = new PFont.Glyph[charset.length];
            this.glyphCount = 0;
            char[] var7 = charset;
            int var8 = charset.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                char c = var7[var9];
                PFont.Glyph glyf = new PFont.Glyph(c);
                if (glyf.value < 128) {
                    this.ascii[glyf.value] = this.glyphCount;
                }

                glyf.index = this.glyphCount;
                this.glyphs[this.glyphCount++] = glyf;
            }
        }

        if (this.ascent == 0) {
            new PFont.Glyph('d');
            if (this.ascent == 0) {
                this.ascent = PApplet.round(this.lazyPaint.ascent());
            }
        }

        if (this.descent == 0) {
            new PFont.Glyph('p');
            if (this.descent == 0) {
                this.descent = PApplet.round(this.lazyPaint.descent());
            }
        }

    }

    public PFont(InputStream input) throws IOException {
        DataInputStream is = new DataInputStream(input);
        this.glyphCount = is.readInt();
        int version = is.readInt();
        this.size = is.readInt();
        is.readInt();
        this.ascent = is.readInt();
        this.descent = is.readInt();
        this.glyphs = new PFont.Glyph[this.glyphCount];
        this.ascii = new int[128];
        Arrays.fill(this.ascii, -1);

        for(int i = 0; i < this.glyphCount; ++i) {
            PFont.Glyph glyph = new PFont.Glyph(is);
            if (glyph.value < 128) {
                this.ascii[glyph.value] = i;
            }

            glyph.index = i;
            this.glyphs[i] = glyph;
        }

        if (this.ascent == 0 && this.descent == 0) {
            throw new RuntimeException("Please use \"Create Font\" to re-create this font.");
        } else {
            PFont.Glyph[] var8 = this.glyphs;
            int var9 = var8.length;

            for(int var6 = 0; var6 < var9; ++var6) {
                PFont.Glyph glyph = var8[var6];
                glyph.readBitmap(is);
            }

            if (version >= 10) {
                this.name = is.readUTF();
                this.psname = is.readUTF();
            }

            if (version == 11) {
                this.smooth = is.readBoolean();
            }

        }
    }

    public void save(OutputStream output) throws IOException {
        DataOutputStream os = new DataOutputStream(output);
        os.writeInt(this.glyphCount);
        if (this.name == null || this.psname == null) {
            this.name = "";
            this.psname = "";
        }

        os.writeInt(11);
        os.writeInt(this.size);
        os.writeInt(0);
        os.writeInt(this.ascent);
        os.writeInt(this.descent);

        int i;
        for(i = 0; i < this.glyphCount; ++i) {
            this.glyphs[i].writeHeader(os);
        }

        for(i = 0; i < this.glyphCount; ++i) {
            this.glyphs[i].writeBitmap(os);
        }

        os.writeUTF(this.name);
        os.writeUTF(this.psname);
        os.writeBoolean(this.smooth);
        os.flush();
    }

    protected void addGlyph(char c) {
        PFont.Glyph glyph = new PFont.Glyph(c);
        if (this.glyphCount == this.glyphs.length) {
            this.glyphs = (PFont.Glyph[])((PFont.Glyph[])PApplet.expand(this.glyphs));
        }

        if (this.glyphCount == 0) {
            glyph.index = 0;
            this.glyphs[this.glyphCount] = glyph;
            if (glyph.value < 128) {
                this.ascii[glyph.value] = 0;
            }
        } else if (this.glyphs[this.glyphCount - 1].value < glyph.value) {
            this.glyphs[this.glyphCount] = glyph;
            if (glyph.value < 128) {
                this.ascii[glyph.value] = this.glyphCount;
            }
        } else {
            for(int i = 0; i < this.glyphCount; ++i) {
                if (this.glyphs[i].value > c) {
                    for(int j = this.glyphCount; j > i; --j) {
                        this.glyphs[j] = this.glyphs[j - 1];
                        if (this.glyphs[j].value < 128) {
                            this.ascii[this.glyphs[j].value] = j;
                        }
                    }

                    glyph.index = i;
                    this.glyphs[i] = glyph;
                    if (c < 128) {
                        this.ascii[c] = i;
                    }
                    break;
                }
            }
        }

        ++this.glyphCount;
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.size;
    }

    public int getDefaultSize() {
        return this.size;
    }

    public boolean isSmooth() {
        return this.smooth;
    }

    public void setSubsetting() {
        this.subsetting = true;
    }

    public String getPostScriptName() {
        return this.psname;
    }

    public void setNative(Object typeface) {
        this.typeface = (Typeface)typeface;
    }

    public Object getNative() {
        return this.subsetting ? null : this.typeface;
    }

    public static Object findNative(String name) {
        loadTypefaces();
        return typefaceMap.get(name);
    }

    public PFont.Glyph getGlyph(char c) {
        int index = this.index(c);
        return index == -1 ? null : this.glyphs[index];
    }

    protected int index(char c) {
        if (this.lazy) {
            int index = this.indexActual(c);
            if (index != -1) {
                return index;
            } else {
                this.addGlyph(c);
                return this.indexActual(c);
            }
        } else {
            return this.indexActual(c);
        }
    }

    protected int indexActual(char c) {
        if (this.glyphCount == 0) {
            return -1;
        } else {
            return c < 128 ? this.ascii[c] : this.indexHunt(c, 0, this.glyphCount - 1);
        }
    }

    protected int indexHunt(int c, int start, int stop) {
        int pivot = (start + stop) / 2;
        if (c == this.glyphs[pivot].value) {
            return pivot;
        } else if (start >= stop) {
            return -1;
        } else {
            return c < this.glyphs[pivot].value ? this.indexHunt(c, start, pivot - 1) : this.indexHunt(c, pivot + 1, stop);
        }
    }

    public float kern(char a, char b) {
        return 0.0F;
    }

    public float ascent() {
        return (float)this.ascent / (float)this.size;
    }

    public float descent() {
        return (float)this.descent / (float)this.size;
    }

    public float width(char c) {
        if (c == ' ') {
            return this.width('i');
        } else {
            int cc = this.index(c);
            return cc == -1 ? 0.0F : (float)this.glyphs[cc].setWidth / (float)this.size;
        }
    }

    public void setCache(PGraphics renderer, Object storage) {
        if (this.cacheMap == null) {
            this.cacheMap = new HashMap();
        }

        this.cacheMap.put(renderer, storage);
    }

    public Object getCache(PGraphics renderer) {
        return this.cacheMap == null ? null : this.cacheMap.get(renderer);
    }

    public void removeCache(PGraphics renderer) {
        if (this.cacheMap != null) {
            this.cacheMap.remove(renderer);
        }

    }

    public int getGlyphCount() {
        return this.glyphCount;
    }

    public PFont.Glyph getGlyph(int i) {
        return this.glyphs[i];
    }

    public static String[] list() {
        loadTypefaces();
        return fontList;
    }

    public static void loadTypefaces() {
        if (typefaceMap == null) {
            typefaceMap = new HashMap();
            typefaceMap.put("Serif", Typeface.create(Typeface.SERIF, 0));
            typefaceMap.put("Serif-Bold", Typeface.create(Typeface.SERIF, 1));
            typefaceMap.put("Serif-Italic", Typeface.create(Typeface.SERIF, 2));
            typefaceMap.put("Serif-BoldItalic", Typeface.create(Typeface.SERIF, 3));
            typefaceMap.put("SansSerif", Typeface.create(Typeface.SANS_SERIF, 0));
            typefaceMap.put("SansSerif-Bold", Typeface.create(Typeface.SANS_SERIF, 1));
            typefaceMap.put("SansSerif-Italic", Typeface.create(Typeface.SANS_SERIF, 2));
            typefaceMap.put("SansSerif-BoldItalic", Typeface.create(Typeface.SANS_SERIF, 3));
            typefaceMap.put("Monospaced", Typeface.create(Typeface.MONOSPACE, 0));
            typefaceMap.put("Monospaced-Bold", Typeface.create(Typeface.MONOSPACE, 1));
            typefaceMap.put("Monospaced-Italic", Typeface.create(Typeface.MONOSPACE, 2));
            typefaceMap.put("Monospaced-BoldItalic", Typeface.create(Typeface.MONOSPACE, 3));
            fontList = new String[typefaceMap.size()];
            typefaceMap.keySet().toArray(fontList);
        }

    }

    static {
        CHARSET = new char[94 + EXTRA_CHARS.length];
        int index = 0;

        int i;
        for(i = 33; i <= 126; ++i) {
            CHARSET[index++] = (char)i;
        }

        for(i = 0; i < EXTRA_CHARS.length; ++i) {
            CHARSET[index++] = EXTRA_CHARS[i];
        }

    }

    public class Glyph {
        public PImage image;
        public int value;
        public int height;
        public int width;
        public int index;
        public int setWidth;
        public int topExtent;
        public int leftExtent;
        public boolean fromStream = false;

        protected Glyph() {
        }

        protected Glyph(DataInputStream is) throws IOException {
            this.readHeader(is);
        }

        protected void readHeader(DataInputStream is) throws IOException {
            this.value = is.readInt();
            this.height = is.readInt();
            this.width = is.readInt();
            this.setWidth = is.readInt();
            this.topExtent = is.readInt();
            this.leftExtent = is.readInt();
            is.readInt();
            if (this.value == 100 && PFont.this.ascent == 0) {
                PFont.this.ascent = this.topExtent;
            }

            if (this.value == 112 && PFont.this.descent == 0) {
                PFont.this.descent = -this.topExtent + this.height;
            }

        }

        protected void writeHeader(DataOutputStream os) throws IOException {
            os.writeInt(this.value);
            os.writeInt(this.height);
            os.writeInt(this.width);
            os.writeInt(this.setWidth);
            os.writeInt(this.topExtent);
            os.writeInt(this.leftExtent);
            os.writeInt(0);
        }

        protected void readBitmap(DataInputStream is) throws IOException {
            this.image = new PImage(this.width, this.height, 4);
            int bitmapSize = this.width * this.height;
            byte[] temp = new byte[bitmapSize];
            is.readFully(temp);
            int w = this.width;
            int h = this.height;
            int[] pixels = this.image.pixels;

            for(int y = 0; y < h; ++y) {
                for(int x = 0; x < w; ++x) {
                    pixels[y * this.width + x] = temp[y * w + x] & 255;
                }
            }

            this.fromStream = true;
        }

        protected void writeBitmap(DataOutputStream os) throws IOException {
            int[] pixels = this.image.pixels;

            for(int y = 0; y < this.height; ++y) {
                for(int x = 0; x < this.width; ++x) {
                    os.write(pixels[y * this.width + x] & 255);
                }
            }

        }

        protected Glyph(char c) {
            int mbox3 = PFont.this.size * 3;
            PFont.this.lazyCanvas.drawColor(-1);
            PFont.this.lazyPaint.setColor(-16777216);
            PFont.this.lazyCanvas.drawText(String.valueOf(c), (float)PFont.this.size, (float)(PFont.this.size * 2), PFont.this.lazyPaint);
            PFont.this.lazyBitmap.getPixels(PFont.this.lazySamples, 0, mbox3, 0, 0, mbox3, mbox3);
            int minX = 1000;
            int maxX = 0;
            int minY = 1000;
            int maxY = 0;
            boolean pixelFound = false;

            int xx;
            int x;
            for(int y = 0; y < mbox3; ++y) {
                for(xx = 0; xx < mbox3; ++xx) {
                    x = PFont.this.lazySamples[y * mbox3 + xx] & 255;
                    if (x != 255) {
                        if (xx < minX) {
                            minX = xx;
                        }

                        if (y < minY) {
                            minY = y;
                        }

                        if (xx > maxX) {
                            maxX = xx;
                        }

                        if (y > maxY) {
                            maxY = y;
                        }

                        pixelFound = true;
                    }
                }
            }

            if (!pixelFound) {
                minY = 0;
                minX = 0;
                maxY = 0;
                maxX = 0;
            }

            this.value = c;
            this.height = maxY - minY + 1;
            this.width = maxX - minX + 1;
            this.setWidth = (int)PFont.this.lazyPaint.measureText(String.valueOf(c));
            this.topExtent = PFont.this.size * 2 - minY;
            this.leftExtent = minX - PFont.this.size;
            this.image = new PImage(this.width, this.height, 4);
            int[] pixels = this.image.pixels;

            for(xx = minY; xx <= maxY; ++xx) {
                for(x = minX; x <= maxX; ++x) {
                    int val = 255 - (PFont.this.lazySamples[xx * mbox3 + x] & 255);
                    int pindex = (xx - minY) * this.width + (x - minX);
                    pixels[pindex] = val;
                }
            }

            if (this.value == 100 && PFont.this.ascent == 0) {
                PFont.this.ascent = this.topExtent;
            }

            if (this.value == 112 && PFont.this.descent == 0) {
                PFont.this.descent = -this.topExtent + this.height;
            }

        }
    }
}

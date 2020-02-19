/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-16 The Processing Foundation
  Copyright (c) 2005-12 Ben Fry and Casey Reas

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


package processing.a2d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

import processing.android.AppComponent;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PShapeSVG;
import processing.core.PSurface;
import processing.data.XML;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.Region.Op;
import android.os.Environment;
import android.os.Build.VERSION;
import android.view.SurfaceHolder;



public class PGraphicsAndroid2D extends PGraphics {
    public static boolean useBitmap = true;
    public Canvas canvas;
    boolean breakShape;
    float[] curveCoordX;
    float[] curveCoordY;
    float[] curveDrawX;
    float[] curveDrawY;
    protected static final int MATRIX_STACK_DEPTH = 32;
    protected float[][] transformStack = new float[32][6];
    public PMatrix2D transform = new PMatrix2D();
    protected Matrix transformMatrix = new Matrix();
    protected float[] transformArray = new float[9];
    int transformCount;
    Path path = new Path();
    RectF rect = new RectF();
    Paint fillPaint = new Paint();
    Paint strokePaint;
    Paint tintPaint;
    protected boolean sized;
    protected boolean changed;
    Rect imageImplSrcRect;
    RectF imageImplDstRect;
    float[] screenPoint;
    static int[] getset = new int[1];

    public PGraphicsAndroid2D() {
        this.fillPaint.setStyle(Style.FILL);
        this.strokePaint = new Paint();
        this.strokePaint.setStyle(Style.STROKE);
        this.tintPaint = new Paint(2);
    }

    public void surfaceChanged() {
        this.changed = true;
    }

    public void setSize(int iwidth, int iheight) {
        this.sized = iwidth != this.width || iheight != this.height;
        super.setSize(iwidth, iheight);
    }

    public void dispose() {
        if (this.bitmap != null) {
            this.bitmap.recycle();
        }

    }

    public PSurface createSurface(AppComponent component, SurfaceHolder holder, boolean reset) {
        return new PSurfaceAndroid2D(this, component, holder);
    }

    @SuppressLint({"NewApi"})
    protected Canvas checkCanvas() {
        if ((this.canvas == null || this.sized) && (useBitmap || !this.primaryGraphics)) {
            if (this.bitmap != null && this.bitmap.getWidth() * this.bitmap.getHeight() >= this.width * this.height && VERSION.SDK_INT >= 19) {
                this.bitmap.reconfigure(this.width, this.height, this.bitmap.getConfig());
            } else {
                if (this.bitmap != null) {
                    this.bitmap.recycle();
                }

                this.bitmap = Bitmap.createBitmap(this.width, this.height, Config.ARGB_8888);
            }

            this.canvas = new Canvas(this.bitmap);
            this.sized = false;
        }

        this.restoreSurface();
        return this.canvas;
    }

    public void beginDraw() {
        this.canvas = this.checkCanvas();
        this.checkSettings();
        this.resetMatrix();
        this.vertexCount = 0;
    }

    public void endDraw() {
        if (this.bitmap != null) {
            if (this.primaryGraphics) {
                SurfaceHolder holder = this.parent.getSurface().getSurfaceHolder();
                if (holder != null) {
                    Canvas screen = null;

                    try {
                        screen = holder.lockCanvas((Rect)null);
                        if (screen != null) {
                            screen.drawBitmap(this.bitmap, new Matrix(), (Paint)null);
                        }
                    } catch (Exception var16) {
                        var16.printStackTrace();
                    } finally {
                        if (screen != null) {
                            try {
                                holder.unlockCanvasAndPost(screen);
                            } catch (IllegalStateException var14) {
                            } catch (IllegalArgumentException var15) {
                            }
                        }

                    }
                }
            } else {
                this.loadPixels();
            }

            this.setModified();
            super.updatePixels();
        }
    }

    public void beginShape(int kind) {
        this.shape = kind;
        this.vertexCount = 0;
        this.curveVertexCount = 0;
    }

    public void texture(PImage image) {
        showMethodWarning("texture");
    }

    public void vertex(float x, float y) {
        if (this.shape == 20) {
            if (this.vertexCount == 0) {
                this.path.reset();
                this.path.moveTo(x, y);
                this.vertexCount = 1;
            } else if (this.breakShape) {
                this.path.moveTo(x, y);
                this.breakShape = false;
            } else {
                this.path.lineTo(x, y);
            }
        } else {
            this.curveVertexCount = 0;
            if (this.vertexCount == this.vertices.length) {
                float[][] temp = new float[this.vertexCount << 1][37];
                System.arraycopy(this.vertices, 0, temp, 0, this.vertexCount);
                this.vertices = temp;
            }

            this.vertices[this.vertexCount][0] = x;
            this.vertices[this.vertexCount][1] = y;
            ++this.vertexCount;
            switch(this.shape) {
                case 3:
                default:
                    break;
                case 5:
                    if (this.vertexCount % 2 == 0) {
                        this.line(this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], x, y);
                        this.vertexCount = 0;
                    }
                    break;
                case 9:
                    if (this.vertexCount % 3 == 0) {
                        this.triangle(this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1], this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], x, y);
                        this.vertexCount = 0;
                    }
                    break;
                case 10:
                    if (this.vertexCount >= 3) {
                        this.triangle(this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], x, y, this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1]);
                    }
                    break;
                case 11:
                    if (this.vertexCount >= 3) {
                        this.triangle(this.vertices[0][0], this.vertices[0][1], this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], x, y);
                    }
                    break;
                case 16:
                case 17:
                    if (this.vertexCount % 4 == 0) {
                        this.quad(this.vertices[this.vertexCount - 4][0], this.vertices[this.vertexCount - 4][1], this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1], this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], x, y);
                        this.vertexCount = 0;
                    }
                    break;
                case 18:
                    if (this.vertexCount >= 4 && this.vertexCount % 2 == 0) {
                        this.quad(this.vertices[this.vertexCount - 4][0], this.vertices[this.vertexCount - 4][1], this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], x, y, this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1]);
                    }
                    break;
                case 50:
                case 51:
                    if (this.vertexCount >= 2) {
                        this.line(this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], x, y);
                    }
            }
        }

    }

    public void vertex(float x, float y, float z) {
        showDepthWarningXYZ("vertex");
    }

    public void vertex(float x, float y, float u, float v) {
        showVariationWarning("vertex(x, y, u, v)");
    }

    public void vertex(float x, float y, float z, float u, float v) {
        showDepthWarningXYZ("vertex");
    }

    public void breakShape() {
        this.breakShape = true;
    }

    public void endShape(int mode) {
        if (this.shape == 3 && this.stroke && this.vertexCount > 0) {
            Matrix m = this.getMatrixImp();
            float y;
            if (this.strokeWeight == 1.0F && m.isIdentity()) {
                if (this.screenPoint == null) {
                    this.screenPoint = new float[2];
                }

                for(int i = 0; i < this.vertexCount; ++i) {
                    this.screenPoint[0] = this.vertices[i][0];
                    this.screenPoint[1] = this.vertices[i][1];
                    m.mapPoints(this.screenPoint);
                    this.set(PApplet.round(this.screenPoint[0]), PApplet.round(this.screenPoint[1]), this.strokeColor);
                    float x = this.vertices[i][0];
                    y = this.vertices[i][1];
                    this.set(PApplet.round(this.screenX(x, y)), PApplet.round(this.screenY(x, y)), this.strokeColor);
                }
            } else {
                float sw = this.strokeWeight / 2.0F;
                this.strokePaint.setStyle(Style.FILL);

                for(int i = 0; i < this.vertexCount; ++i) {
                    y = this.vertices[i][0];
                    float y = this.vertices[i][1];
                    this.rect.set(y - sw, y - sw, y + sw, y + sw);
                    this.canvas.drawOval(this.rect, this.strokePaint);
                }

                this.strokePaint.setStyle(Style.STROKE);
            }
        } else if (this.shape == 20) {
            if (!this.path.isEmpty()) {
                if (mode == 2) {
                    this.path.close();
                }

                this.drawPath();
            }
        } else if (this.shape == 51 && this.vertexCount >= 2) {
            this.line(this.vertices[this.vertexCount - 1][0], this.vertices[this.vertexCount - 1][1], this.vertices[0][0], this.vertices[0][1]);
        }

        this.shape = 0;
    }

    protected void clipImpl(float x1, float y1, float x2, float y2) {
        this.canvas.clipRect(x1, y1, x2, y2);
    }

    public void noClip() {
        this.canvas.clipRect(0.0F, 0.0F, (float)this.width, (float)this.height, Op.REPLACE);
    }

    public void bezierVertex(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.bezierVertexCheck();
        this.path.cubicTo(x1, y1, x2, y2, x3, y3);
    }

    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        showDepthWarningXYZ("bezierVertex");
    }

    public void quadraticVertex(float ctrlX, float ctrlY, float endX, float endY) {
        this.bezierVertexCheck();
        this.path.quadTo(ctrlX, ctrlY, endX, endY);
    }

    public void quadraticVertex(float x2, float y2, float z2, float x4, float y4, float z4) {
        showDepthWarningXYZ("quadVertex");
    }

    protected void curveVertexCheck() {
        super.curveVertexCheck();
        if (this.curveCoordX == null) {
            this.curveCoordX = new float[4];
            this.curveCoordY = new float[4];
            this.curveDrawX = new float[4];
            this.curveDrawY = new float[4];
        }

    }

    protected void curveVertexSegment(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.curveCoordX[0] = x1;
        this.curveCoordY[0] = y1;
        this.curveCoordX[1] = x2;
        this.curveCoordY[1] = y2;
        this.curveCoordX[2] = x3;
        this.curveCoordY[2] = y3;
        this.curveCoordX[3] = x4;
        this.curveCoordY[3] = y4;
        this.curveToBezierMatrix.mult(this.curveCoordX, this.curveDrawX);
        this.curveToBezierMatrix.mult(this.curveCoordY, this.curveDrawY);
        if (this.vertexCount == 0) {
            this.path.moveTo(this.curveDrawX[0], this.curveDrawY[0]);
            this.vertexCount = 1;
        }

        this.path.cubicTo(this.curveDrawX[1], this.curveDrawY[1], this.curveDrawX[2], this.curveDrawY[2], this.curveDrawX[3], this.curveDrawY[3]);
    }

    public void curveVertex(float x, float y, float z) {
        showDepthWarningXYZ("curveVertex");
    }

    public void point(float x, float y) {
        this.beginShape(3);
        this.vertex(x, y);
        this.endShape();
    }

    public void line(float x1, float y1, float x2, float y2) {
        if (this.stroke) {
            this.canvas.drawLine(x1, y1, x2, y2, this.strokePaint);
        }

    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.path.reset();
        this.path.moveTo(x1, y1);
        this.path.lineTo(x2, y2);
        this.path.lineTo(x3, y3);
        this.path.close();
        this.drawPath();
    }

    public void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.path.reset();
        this.path.moveTo(x1, y1);
        this.path.lineTo(x2, y2);
        this.path.lineTo(x3, y3);
        this.path.lineTo(x4, y4);
        this.path.close();
        this.drawPath();
    }

    protected void rectImpl(float x1, float y1, float x2, float y2) {
        if (this.fill) {
            this.canvas.drawRect(x1, y1, x2, y2, this.fillPaint);
        }

        if (this.stroke) {
            this.canvas.drawRect(x1, y1, x2, y2, this.strokePaint);
        }

    }

    protected void ellipseImpl(float x, float y, float w, float h) {
        this.rect.set(x, y, x + w, y + h);
        if (this.fill) {
            this.canvas.drawOval(this.rect, this.fillPaint);
        }

        if (this.stroke) {
            this.canvas.drawOval(this.rect, this.strokePaint);
        }

    }

    protected void arcImpl(float x, float y, float w, float h, float start, float stop, int mode) {
        if (stop - start >= 6.2831855F) {
            this.ellipseImpl(x, y, w, h);
        } else {
            start *= 57.295776F;

            for(stop *= 57.295776F; start < 0.0F; stop += 360.0F) {
                start += 360.0F;
            }

            float sweep;
            if (start > stop) {
                sweep = start;
                start = stop;
                stop = sweep;
            }

            sweep = stop - start;
            this.rect.set(x, y, x + w, y + h);
            if (mode == 0) {
                if (this.fill) {
                    this.canvas.drawArc(this.rect, start, sweep, true, this.fillPaint);
                }

                if (this.stroke) {
                    this.canvas.drawArc(this.rect, start, sweep, false, this.strokePaint);
                }
            } else if (mode == 1) {
                if (this.fill) {
                    this.canvas.drawArc(this.rect, start, sweep, false, this.fillPaint);
                    this.canvas.drawArc(this.rect, start, sweep, false, this.strokePaint);
                }

                if (this.stroke) {
                    this.canvas.drawArc(this.rect, start, sweep, false, this.strokePaint);
                }
            } else if (mode == 2) {
                float endAngle = start + sweep;
                float halfRectWidth = this.rect.width() / 2.0F;
                float halfRectHeight = this.rect.height() / 2.0F;
                float centerX = this.rect.centerX();
                float centerY = this.rect.centerY();
                float startX = (float)((double)halfRectWidth * Math.cos(Math.toRadians((double)start))) + centerX;
                float startY = (float)((double)halfRectHeight * Math.sin(Math.toRadians((double)start))) + centerY;
                float endX = (float)((double)halfRectWidth * Math.cos(Math.toRadians((double)endAngle))) + centerX;
                float endY = (float)((double)halfRectHeight * Math.sin(Math.toRadians((double)endAngle))) + centerY;
                if (this.fill) {
                    this.canvas.drawArc(this.rect, start, sweep, false, this.fillPaint);
                    this.canvas.drawArc(this.rect, start, sweep, false, this.strokePaint);
                    this.canvas.drawLine(startX, startY, endX, endY, this.strokePaint);
                }

                if (this.stroke) {
                    this.canvas.drawArc(this.rect, start, sweep, false, this.strokePaint);
                    this.canvas.drawLine(startX, startY, endX, endY, this.strokePaint);
                }
            } else if (mode == 3) {
                if (this.fill) {
                    this.canvas.drawArc(this.rect, start, sweep, true, this.fillPaint);
                }

                if (this.stroke) {
                    this.canvas.drawArc(this.rect, start, sweep, true, this.strokePaint);
                }
            }
        }

    }

    protected void drawPath() {
        if (this.fill) {
            this.canvas.drawPath(this.path, this.fillPaint);
        }

        if (this.stroke) {
            this.canvas.drawPath(this.path, this.strokePaint);
        }

    }

    public void box(float w, float h, float d) {
        showMethodWarning("box");
    }

    public void sphere(float r) {
        showMethodWarning("sphere");
    }

    public void bezierDetail(int detail) {
    }

    public void curveDetail(int detail) {
    }

    public void smooth(int quality) {
        super.smooth(quality);
        this.strokePaint.setAntiAlias(true);
        this.fillPaint.setAntiAlias(true);
    }

    public void noSmooth() {
        super.noSmooth();
        this.strokePaint.setAntiAlias(false);
        this.fillPaint.setAntiAlias(false);
    }

    protected void imageImpl(PImage src, float x1, float y1, float x2, float y2, int u1, int v1, int u2, int v2) {
        Bitmap bitmap = (Bitmap)src.getNative();
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap = null;
        }

        if (bitmap == null && src.format == 4) {
            bitmap = Bitmap.createBitmap(src.width, src.height, Config.ARGB_8888);
            int[] px = new int[src.pixels.length];

            for(int i = 0; i < px.length; ++i) {
                px[i] = src.pixels[i] << 24 | 16777215;
            }

            bitmap.setPixels(px, 0, src.width, 0, 0, src.width, src.height);
            this.modified = false;
            src.setNative(bitmap);
        }

        if (bitmap == null || src.width != bitmap.getWidth() || src.height != bitmap.getHeight()) {
            if (bitmap != null) {
                bitmap.recycle();
            }

            bitmap = Bitmap.createBitmap(src.width, src.height, Config.ARGB_8888);
            this.modified = true;
            src.setNative(bitmap);
        }

        if (src.isModified()) {
            if (!bitmap.isMutable()) {
                bitmap.recycle();
                bitmap = Bitmap.createBitmap(src.width, src.height, Config.ARGB_8888);
                src.setNative(bitmap);
            }

            if (src.pixels != null) {
                bitmap.setPixels(src.pixels, 0, src.width, 0, 0, src.width, src.height);
            }

            src.setModified(false);
        }

        if (this.imageImplSrcRect == null) {
            this.imageImplSrcRect = new Rect(u1, v1, u2, v2);
            this.imageImplDstRect = new RectF(x1, y1, x2, y2);
        } else {
            this.imageImplSrcRect.set(u1, v1, u2, v2);
            this.imageImplDstRect.set(x1, y1, x2, y2);
        }

        this.canvas.drawBitmap(bitmap, this.imageImplSrcRect, this.imageImplDstRect, this.tint ? this.tintPaint : null);
        MemoryInfo mi = new MemoryInfo();
        Activity activity = this.parent.getSurface().getActivity();
        if (activity != null) {
            ActivityManager activityManager = (ActivityManager)activity.getSystemService("activity");
            activityManager.getMemoryInfo(mi);
            if (mi.lowMemory) {
                bitmap.recycle();
                src.setNative((Object)null);
            }

        }
    }

    public PShape loadShape(String filename) {
        String extension = PApplet.getExtension(filename);
        PShapeSVG svg = null;
        if (extension.equals("svg")) {
            svg = new PShapeSVG(this.parent.loadXML(filename));
        } else if (extension.equals("svgz")) {
            try {
                InputStream input = new GZIPInputStream(this.parent.createInput(filename));
                XML xml = new XML(input);
                svg = new PShapeSVG(xml);
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        } else {
            PGraphics.showWarning("Unsupported format");
        }

        return svg;
    }

    public void textFont(PFont which) {
        super.textFont(which);
        this.fillPaint.setTypeface((Typeface)which.getNative());
        this.fillPaint.setTextSize((float)which.getDefaultSize());
    }

    public void textFont(PFont which, float size) {
        super.textFont(which, size);
        this.fillPaint.setTypeface((Typeface)which.getNative());
        this.fillPaint.setTextSize(size);
    }

    protected boolean textModeCheck(int mode) {
        return mode == 4;
    }

    public void textSize(float size) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textSize", size);
        }

        Typeface font = (Typeface)this.textFont.getNative();
        if (font != null) {
            this.fillPaint.setTextSize(size);
        }

        this.handleTextSize(size);
    }

    protected void beginTextScreenMode() {
        this.loadPixels();
    }

    protected void endTextScreenMode() {
        this.updatePixels();
    }

    protected float textWidthImpl(char[] buffer, int start, int stop) {
        Typeface font = (Typeface)this.textFont.getNative();
        if (font == null) {
            return super.textWidthImpl(buffer, start, stop);
        } else {
            int length = stop - start;
            return this.fillPaint.measureText(buffer, start, length);
        }
    }

    protected void textLineImpl(char[] buffer, int start, int stop, float x, float y) {
        Typeface font = (Typeface)this.textFont.getNative();
        if (font == null) {
            showWarning("Inefficient font rendering: use createFont() with a TTF/OTF instead of loadFont().");
            super.textLineImpl(buffer, start, stop, x, y);
        } else {
            this.fillPaint.setAntiAlias(this.textFont.isSmooth());
            int length = stop - start;
            this.canvas.drawText(buffer, start, length, x, y, this.fillPaint);
            this.fillPaint.setAntiAlias(0 < this.smooth);
        }
    }

    public void pushMatrix() {
        if (this.transformCount == this.transformStack.length) {
            throw new RuntimeException("pushMatrix() cannot use push more than " + this.transformStack.length + " times");
        } else {
            this.transform.get(this.transformStack[this.transformCount]);
            ++this.transformCount;
        }
    }

    public void popMatrix() {
        if (this.transformCount == 0) {
            throw new RuntimeException("missing a popMatrix() to go with that pushMatrix()");
        } else {
            --this.transformCount;
            this.transform.set(this.transformStack[this.transformCount]);
            this.updateTransformMatrix();
            this.canvas.setMatrix(this.transformMatrix);
        }
    }

    public void translate(float tx, float ty) {
        this.transform.translate(tx, ty);
        this.canvas.translate(tx, ty);
    }

    public void rotate(float angle) {
        this.transform.rotate(angle);
        this.canvas.rotate(angle * 57.295776F);
    }

    public void rotateX(float angle) {
        showDepthWarning("rotateX");
    }

    public void rotateY(float angle) {
        showDepthWarning("rotateY");
    }

    public void rotateZ(float angle) {
        showDepthWarning("rotateZ");
    }

    public void rotate(float angle, float vx, float vy, float vz) {
        showVariationWarning("rotate");
    }

    public void scale(float s) {
        this.transform.scale(s, s);
        this.canvas.scale(s, s);
    }

    public void scale(float sx, float sy) {
        this.transform.scale(sx, sy);
        this.canvas.scale(sx, sy);
    }

    public void scale(float sx, float sy, float sz) {
        showDepthWarningXYZ("scale");
    }

    public void shearX(float angle) {
        float t = (float)Math.tan((double)angle);
        this.transform.apply(1.0F, t, 0.0F, 0.0F, 1.0F, 0.0F);
        this.canvas.skew(t, 0.0F);
    }

    public void shearY(float angle) {
        float t = (float)Math.tan((double)angle);
        this.transform.apply(1.0F, 0.0F, 0.0F, t, 1.0F, 0.0F);
        this.canvas.skew(0.0F, t);
    }

    public void resetMatrix() {
        this.transform.reset();
        this.canvas.setMatrix((Matrix)null);
    }

    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.transform.apply(n00, n01, n02, n10, n11, n12);
        this.updateTransformMatrix();
        this.canvas.concat(this.transformMatrix);
    }

    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        showVariationWarning("applyMatrix");
    }

    public PMatrix getMatrix() {
        return this.getMatrix((PMatrix2D)null);
    }

    public PMatrix2D getMatrix(PMatrix2D target) {
        if (target == null) {
            target = new PMatrix2D();
        }

        target.set(this.transform);
        return target;
    }

    public PMatrix3D getMatrix(PMatrix3D target) {
        showVariationWarning("getMatrix");
        return target;
    }

    public void setMatrix(PMatrix2D source) {
        this.transform.set(source);
        this.updateTransformMatrix();
        this.canvas.setMatrix(this.transformMatrix);
    }

    public void setMatrix(PMatrix3D source) {
        showVariationWarning("setMatrix");
    }

    public void printMatrix() {
        this.getMatrix((PMatrix2D)null).print();
    }

    protected Matrix getMatrixImp() {
        Matrix m = new Matrix();
        this.updateTransformMatrix();
        m.set(this.transformMatrix);
        return m;
    }

    public void updateTransformMatrix() {
        this.transformArray[0] = this.transform.m00;
        this.transformArray[1] = this.transform.m01;
        this.transformArray[2] = this.transform.m02;
        this.transformArray[3] = this.transform.m10;
        this.transformArray[4] = this.transform.m11;
        this.transformArray[5] = this.transform.m12;
        this.transformArray[6] = 0.0F;
        this.transformArray[7] = 0.0F;
        this.transformArray[8] = 1.0F;
        this.transformMatrix.setValues(this.transformArray);
    }

    public float screenX(float x, float y) {
        if (this.screenPoint == null) {
            this.screenPoint = new float[2];
        }

        this.screenPoint[0] = x;
        this.screenPoint[1] = y;
        this.getMatrixImp().mapPoints(this.screenPoint);
        return this.screenPoint[0];
    }

    public float screenY(float x, float y) {
        if (this.screenPoint == null) {
            this.screenPoint = new float[2];
        }

        this.screenPoint[0] = x;
        this.screenPoint[1] = y;
        this.getMatrixImp().mapPoints(this.screenPoint);
        return this.screenPoint[1];
    }

    public float screenX(float x, float y, float z) {
        showDepthWarningXYZ("screenX");
        return 0.0F;
    }

    public float screenY(float x, float y, float z) {
        showDepthWarningXYZ("screenY");
        return 0.0F;
    }

    public float screenZ(float x, float y, float z) {
        showDepthWarningXYZ("screenZ");
        return 0.0F;
    }

    public void strokeCap(int cap) {
        super.strokeCap(cap);
        if (this.strokeCap == 2) {
            this.strokePaint.setStrokeCap(Cap.ROUND);
        } else if (this.strokeCap == 4) {
            this.strokePaint.setStrokeCap(Cap.SQUARE);
        } else {
            this.strokePaint.setStrokeCap(Cap.BUTT);
        }

    }

    public void strokeJoin(int join) {
        super.strokeJoin(join);
        if (this.strokeJoin == 8) {
            this.strokePaint.setStrokeJoin(Join.MITER);
        } else if (this.strokeJoin == 2) {
            this.strokePaint.setStrokeJoin(Join.ROUND);
        } else {
            this.strokePaint.setStrokeJoin(Join.BEVEL);
        }

    }

    public void strokeWeight(float weight) {
        super.strokeWeight(weight);
        this.strokePaint.setStrokeWidth(weight);
    }

    protected void strokeFromCalc() {
        super.strokeFromCalc();
        this.strokePaint.setColor(this.strokeColor);
        this.strokePaint.setShader((Shader)null);
    }

    protected void tintFromCalc() {
        super.tintFromCalc();
        this.tintPaint.setColorFilter(new PorterDuffColorFilter(this.tintColor, Mode.MULTIPLY));
    }

    protected void fillFromCalc() {
        super.fillFromCalc();
        this.fillPaint.setColor(this.fillColor);
        this.fillPaint.setShader((Shader)null);
    }

    public void backgroundImpl() {
        this.canvas.drawColor(this.backgroundColor);
    }

    public void beginRaw(PGraphics recorderRaw) {
        showMethodWarning("beginRaw");
    }

    public void endRaw() {
        showMethodWarning("endRaw");
    }

    public void loadPixels() {
        if (this.bitmap == null) {
            throw new RuntimeException("The pixels array is not available in this renderer withouth a backing bitmap");
        } else {
            if (this.pixels == null || this.pixels.length != this.width * this.height) {
                this.pixels = new int[this.width * this.height];
            }

            this.bitmap.getPixels(this.pixels, 0, this.width, 0, 0, this.width, this.height);
        }
    }

    public void updatePixels() {
        if (this.bitmap == null) {
            throw new RuntimeException("The pixels array is not available in this renderer withouth a backing bitmap");
        } else {
            this.bitmap.setPixels(this.pixels, 0, this.width, 0, 0, this.width, this.height);
        }
    }

    public void updatePixels(int x, int y, int c, int d) {
        if (x != 0 || y != 0 || c != this.width || d != this.height) {
            showVariationWarning("updatePixels(x, y, w, h)");
        }

        this.updatePixels();
    }

    public void resize(int wide, int high) {
        showMethodWarning("resize");
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
        Context context = this.parent.getContext();
        if (context != null && this.bitmap != null && !this.parent.getSurface().getComponent().isService()) {
            try {
                this.restoreWidth = this.pixelWidth;
                this.restoreHeight = this.pixelHeight;
                int size = this.bitmap.getHeight() * this.bitmap.getRowBytes();
                ByteBuffer restoreBitmap = ByteBuffer.allocate(size);
                this.bitmap.copyPixelsToBuffer(restoreBitmap);
                File cacheDir = "mounted" != Environment.getExternalStorageState() && Environment.isExternalStorageRemovable() ? context.getCacheDir() : context.getExternalCacheDir();
                File cacheFile = new File(cacheDir + File.separator + "restore_pixels");
                this.restoreFilename = cacheFile.getAbsolutePath();
                FileOutputStream stream = new FileOutputStream(cacheFile);
                ObjectOutputStream dout = new ObjectOutputStream(stream);
                byte[] array = new byte[size];
                restoreBitmap.rewind();
                restoreBitmap.get(array);
                dout.writeObject(array);
                dout.flush();
                stream.getFD().sync();
                stream.close();
            } catch (Exception var9) {
                PGraphics.showWarning("Could not save screen contents to cache");
                var9.printStackTrace();
            }

        }
    }

    protected void restoreSurface() {
        if (this.changed) {
            this.changed = false;
            if (this.restoreFilename != null && this.restoreWidth == this.pixelWidth && this.restoreHeight == this.pixelHeight) {
                this.restoreCount = 1;
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
                    byte[] array = (byte[])((byte[])din.readObject());
                    ByteBuffer restoreBitmap = ByteBuffer.wrap(array);
                    if (restoreBitmap.capacity() == this.bitmap.getHeight() * this.bitmap.getRowBytes()) {
                        restoreBitmap.rewind();
                        this.bitmap.copyPixelsFromBuffer(restoreBitmap);
                    }

                    inStream.close();
                    cacheFile.delete();
                } catch (Exception var10) {
                    PGraphics.showWarning("Could not restore screen contents from cache");
                    var10.printStackTrace();
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

    public int get(int x, int y) {
        return this.bitmap != null && x >= 0 && y >= 0 && x < this.width && y < this.height ? this.bitmap.getPixel(x, y) : 0;
    }

    public PImage get() {
        return this.get(0, 0, this.width, this.height);
    }

    public void set(int x, int y, int argb) {
        if (this.bitmap != null && x >= 0 && y >= 0 && x < this.width && y < this.height) {
            this.bitmap.setPixel(x, y, argb);
        }
    }

    public void set(int x, int y, PImage src) {
        if (src.format == 4) {
            throw new RuntimeException("set() not available for ALPHA images");
        } else {
            Bitmap bitmap = (Bitmap)src.getNative();
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(src.width, src.height, Config.ARGB_8888);
                src.setNative(bitmap);
                src.setModified();
            }

            if (src.width != bitmap.getWidth() || src.height != bitmap.getHeight()) {
                bitmap.recycle();
                bitmap = Bitmap.createBitmap(src.width, src.height, Config.ARGB_8888);
                src.setNative(bitmap);
                src.setModified();
            }

            if (src.isModified()) {
                if (!bitmap.isMutable()) {
                    bitmap.recycle();
                    bitmap = Bitmap.createBitmap(src.width, src.height, Config.ARGB_8888);
                    this.setNative(bitmap);
                }

                bitmap.setPixels(src.pixels, 0, src.width, 0, 0, src.width, src.height);
                src.setModified(false);
            }

            this.pushMatrix();
            this.canvas.setMatrix((Matrix)null);
            this.canvas.drawBitmap(bitmap, (float)x, (float)y, (Paint)null);
            this.popMatrix();
        }
    }

    public void mask(int[] alpha) {
        showMethodWarning("mask");
    }

    public void mask(PImage alpha) {
        showMethodWarning("mask");
    }

    public void copy(int sx, int sy, int sw, int sh, int dx, int dy, int dw, int dh) {
        if (this.bitmap == null) {
            throw new RuntimeException("The pixels array is not available in this renderer withouth a backing bitmap");
        } else {
            this.rect.set((float)sx, (float)sy, (float)(sx + sw), (float)(sy + sh));
            Rect src = new Rect(dx, dy, dx + dw, dy + dh);
            this.canvas.drawBitmap(this.bitmap, src, this.rect, (Paint)null);
        }
    }
}

package processing.opengl;

import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PShapeSVG;
import processing.opengl.FontTexture.TextureInfo;
import processing.opengl.PGL.Tessellator;
import processing.opengl.PGL.TessellatorCallback;

public final class PGraphics2DX extends PGraphicsOpenGL {
    static final String NON_2D_SHAPE_ERROR = "The shape object is not 2D, cannot be displayed with this renderer";
    static final String STROKE_PERSPECTIVE_ERROR = "Strokes cannot be perspective-corrected in 2D";
    static final String NON_2D_SHADER_ERROR = "This shader cannot be used for 2D rendering";
    static final String WRONG_SHADER_PARAMS = "The P2D renderer does not accept shaders of different tyes";
    protected static final int SHADER2D = 7;
    public static boolean premultiplyMatrices = true;
    protected boolean useParentImpl = false;
    protected boolean initialized;
    protected Tessellator tess;
    protected PShader twoShader;
    protected PShader defTwoShader;
    protected int positionLoc;
    protected int colorLoc;
    protected int texCoordLoc;
    protected int texFactorLoc;
    protected int transformLoc;
    protected int texScaleLoc;
    protected static URL defP2DShaderVertURL = PGraphicsOpenGL.class.getResource("/assets/shaders/P2DVert.glsl");
    protected static URL defP2DShaderFragURL = PGraphicsOpenGL.class.getResource("/assets/shaders/P2DFrag.glsl");
    private float cx1;
    private float cy1;
    private float cx2;
    private float cy2;
    private float cx3;
    private float cy3;
    private float cx4;
    private float cy4;
    private int curveVerts;
    private final int maxVerts = 6000;
    private final int vertSize = 28;
    private float[] vertexData = new float['ꐐ'];
    private int usedVerts = 0;
    private float depth = 1.0F;
    private int imageTex;
    private int tex;
    private int vbo;
    private int texWidth;
    private int texHeight;
    private float smallestDepthIncrement = (float)Math.pow(2.0D, -14.0D);
    private float largestNumberLessThanOne = 1.0F - (float)Math.pow(2.0D, -11.0D);
    private int shapeType;
    private int vertCount;
    private PGraphics2DX.TessVertex[] shapeVerts = new PGraphics2DX.TessVertex[16];
    private int[] contours = new int[2];
    private int contourCount;
    private double[] tempDoubles = new double[3];
    private boolean knownConvexPolygon = false;
    float ellipseDetailMultiplier = 1.0F;
    private static final float LINE_DETAIL_LIMIT = 1.0F;
    private PGraphics2DX.StrokeRenderer sr = new PGraphics2DX.StrokeRenderer();

    public PGraphics2DX() {
        this.initTess();
        this.initVerts();
    }

    public boolean is2D() {
        return true;
    }

    public boolean is3D() {
        return false;
    }

    public void beginDraw() {
        super.beginDraw();
        if (!this.useParentImpl) {
            this.pgl.depthFunc(PGL.LESS);
            this.depth = 1.0F;
        }

    }

    public void flush() {
        super.flush();
        this.flushBuffer();
    }

    public void useOldP2D() {
        this.useParentImpl = true;
        this.pgl.depthFunc(PGL.LEQUAL);
    }

    public void useNewP2D() {
        this.useParentImpl = false;
        this.pgl.depthFunc(PGL.LESS);
    }

    public void hint(int which) {
        if (which == 7) {
            showWarning("Strokes cannot be perspective-corrected in 2D");
        } else {
            super.hint(which);
        }
    }

    public void ortho() {
        showMethodWarning("ortho");
    }

    public void ortho(float left, float right, float bottom, float top) {
        showMethodWarning("ortho");
    }

    public void ortho(float left, float right, float bottom, float top, float near, float far) {
        showMethodWarning("ortho");
    }

    public void perspective() {
        showMethodWarning("perspective");
    }

    public void perspective(float fov, float aspect, float zNear, float zFar) {
        showMethodWarning("perspective");
    }

    public void frustum(float left, float right, float bottom, float top, float znear, float zfar) {
        showMethodWarning("frustum");
    }

    protected void defaultPerspective() {
        super.ortho(0.0F, (float)this.width, (float)(-this.height), 0.0F, -1.0F, 1.0F);
    }

    public void beginCamera() {
        showMethodWarning("beginCamera");
    }

    public void endCamera() {
        showMethodWarning("endCamera");
    }

    public void camera() {
        showMethodWarning("camera");
    }

    public void camera(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        showMethodWarning("camera");
    }

    protected void defaultCamera() {
        this.eyeDist = 1.0F;
        this.resetMatrix();
    }

    public void shape(PShape shape) {
        if (shape.is2D()) {
            if (!this.useParentImpl) {
                this.useOldP2D();
                super.shape(shape);
                this.useNewP2D();
            } else {
                super.shape(shape);
            }
        } else {
            showWarning("The shape object is not 2D, cannot be displayed with this renderer");
        }

    }

    public void shape(PShape shape, float x, float y) {
        if (shape.is2D()) {
            if (!this.useParentImpl) {
                this.useOldP2D();
                super.shape(shape, x, y);
                this.useNewP2D();
            } else {
                super.shape(shape, x, y);
            }
        } else {
            showWarning("The shape object is not 2D, cannot be displayed with this renderer");
        }

    }

    public void shape(PShape shape, float a, float b, float c, float d) {
        if (shape.is2D()) {
            if (!this.useParentImpl) {
                this.useOldP2D();
                super.shape(shape, a, b, c, d);
                this.useNewP2D();
            } else {
                super.shape(shape, a, b, c, d);
            }
        } else {
            showWarning("The shape object is not 2D, cannot be displayed with this renderer");
        }

    }

    public void shape(PShape shape, float x, float y, float z) {
        showDepthWarningXYZ("shape");
    }

    public void shape(PShape shape, float x, float y, float z, float c, float d, float e) {
        showDepthWarningXYZ("shape");
    }

    protected static boolean isSupportedExtension(String extension) {
        return extension.equals("svg") || extension.equals("svgz");
    }

    protected static PShape loadShapeImpl(PGraphics pg, String filename, String extension) {
        if (!extension.equals("svg") && !extension.equals("svgz")) {
            return null;
        } else {
            PShapeSVG svg = new PShapeSVG(pg.parent.loadXML(filename));
            return PShapeOpenGL.createShape((PGraphicsOpenGL)pg, svg);
        }
    }

    public float modelX(float x, float y, float z) {
        showDepthWarning("modelX");
        return 0.0F;
    }

    public float modelY(float x, float y, float z) {
        showDepthWarning("modelY");
        return 0.0F;
    }

    public float modelZ(float x, float y, float z) {
        showDepthWarning("modelZ");
        return 0.0F;
    }

    public void texture(PImage image) {
        super.texture(image);
        if (image != null) {
            Texture t = this.currentPG.getTexture(image);
            this.texWidth = t.width;
            this.texHeight = t.height;
            this.imageTex = t.glName;
            this.textureImpl(this.imageTex);
        }
    }

    public void beginShape(int kind) {
        if (this.useParentImpl) {
            super.beginShape(kind);
        } else {
            this.shapeType = kind;
            this.vertCount = 0;
            this.contourCount = 0;
        }
    }

    public void endShape(int mode) {
        if (this.useParentImpl) {
            super.endShape(mode);
        } else {
            this.appendContour(this.vertCount);
            int i;
            int i;
            if (this.fill) {
                this.incrementDepth();
                if (this.shapeType == 20) {
                    if (this.knownConvexPolygon) {
                        for(i = 2; i < this.vertCount; ++i) {
                            this.check(3);
                            this.vertexImpl(this.shapeVerts[0]);
                            this.vertexImpl(this.shapeVerts[i - 1]);
                            this.vertexImpl(this.shapeVerts[i]);
                        }

                        this.knownConvexPolygon = false;
                    } else {
                        this.tess.beginPolygon(this);
                        this.tess.beginContour();
                        i = 0;

                        for(i = 0; i < this.vertCount; ++i) {
                            if (this.contours[i] == i) {
                                this.tess.endContour();
                                this.tess.beginContour();
                                ++i;
                            }

                            this.tempDoubles[0] = (double)this.shapeVerts[i].x;
                            this.tempDoubles[1] = (double)this.shapeVerts[i].y;
                            this.tess.addVertex(this.tempDoubles, 0, this.shapeVerts[i]);
                        }

                        this.tess.endContour();
                        this.tess.endPolygon();
                    }
                } else if (this.shapeType == 18) {
                    for(i = 0; i <= this.vertCount - 4; i += 2) {
                        this.check(6);
                        this.vertexImpl(this.shapeVerts[i + 0]);
                        this.vertexImpl(this.shapeVerts[i + 1]);
                        this.vertexImpl(this.shapeVerts[i + 2]);
                        this.vertexImpl(this.shapeVerts[i + 1]);
                        this.vertexImpl(this.shapeVerts[i + 2]);
                        this.vertexImpl(this.shapeVerts[i + 3]);
                    }
                } else if (this.shapeType == 17) {
                    for(i = 0; i <= this.vertCount - 4; i += 4) {
                        this.check(6);
                        this.vertexImpl(this.shapeVerts[i + 0]);
                        this.vertexImpl(this.shapeVerts[i + 1]);
                        this.vertexImpl(this.shapeVerts[i + 2]);
                        this.vertexImpl(this.shapeVerts[i + 0]);
                        this.vertexImpl(this.shapeVerts[i + 2]);
                        this.vertexImpl(this.shapeVerts[i + 3]);
                    }
                } else if (this.shapeType == 10) {
                    for(i = 0; i <= this.vertCount - 3; ++i) {
                        this.check(3);
                        this.vertexImpl(this.shapeVerts[i + 0]);
                        this.vertexImpl(this.shapeVerts[i + 1]);
                        this.vertexImpl(this.shapeVerts[i + 2]);
                    }
                } else if (this.shapeType == 11) {
                    for(i = 0; i <= this.vertCount - 3; ++i) {
                        this.check(3);
                        this.vertexImpl(this.shapeVerts[0]);
                        this.vertexImpl(this.shapeVerts[i + 1]);
                        this.vertexImpl(this.shapeVerts[i + 2]);
                    }

                    if (this.vertCount >= 3) {
                        this.check(3);
                        this.vertexImpl(this.shapeVerts[0]);
                        this.vertexImpl(this.shapeVerts[this.vertCount - 1]);
                        this.vertexImpl(this.shapeVerts[1]);
                    }
                } else if (this.shapeType == 9) {
                    for(i = 0; i <= this.vertCount - 3; i += 3) {
                        this.check(3);
                        this.vertexImpl(this.shapeVerts[i + 0]);
                        this.vertexImpl(this.shapeVerts[i + 1]);
                        this.vertexImpl(this.shapeVerts[i + 2]);
                    }
                }
            }

            if (this.stroke) {
                this.incrementDepth();
                if (this.shapeType == 20) {
                    if (this.vertCount < 3) {
                        return;
                    }

                    i = 0;
                    this.sr.beginLine();

                    for(i = 0; i < this.vertCount; ++i) {
                        if (this.contours[i] == i) {
                            this.sr.endLine(mode == 2);
                            this.sr.beginLine();
                            ++i;
                        }

                        this.sr.lineVertex(this.shapeVerts[i].x, this.shapeVerts[i].y);
                    }

                    this.sr.endLine(mode == 2);
                } else if (this.shapeType == 18) {
                    for(i = 0; i <= this.vertCount - 4; i += 2) {
                        this.sr.beginLine();
                        this.sr.lineVertex(this.shapeVerts[i + 0].x, this.shapeVerts[i + 0].y);
                        this.sr.lineVertex(this.shapeVerts[i + 1].x, this.shapeVerts[i + 1].y);
                        this.sr.lineVertex(this.shapeVerts[i + 3].x, this.shapeVerts[i + 3].y);
                        this.sr.lineVertex(this.shapeVerts[i + 2].x, this.shapeVerts[i + 2].y);
                        this.sr.endLine(true);
                    }
                } else if (this.shapeType == 17) {
                    for(i = 0; i <= this.vertCount - 4; i += 4) {
                        this.sr.beginLine();
                        this.sr.lineVertex(this.shapeVerts[i + 0].x, this.shapeVerts[i + 0].y);
                        this.sr.lineVertex(this.shapeVerts[i + 1].x, this.shapeVerts[i + 1].y);
                        this.sr.lineVertex(this.shapeVerts[i + 2].x, this.shapeVerts[i + 2].y);
                        this.sr.lineVertex(this.shapeVerts[i + 3].x, this.shapeVerts[i + 3].y);
                        this.sr.endLine(true);
                    }
                } else if (this.shapeType == 10) {
                    for(i = 0; i <= this.vertCount - 3; ++i) {
                        this.sr.beginLine();
                        this.sr.lineVertex(this.shapeVerts[i + 0].x, this.shapeVerts[i + 0].y);
                        this.sr.lineVertex(this.shapeVerts[i + 1].x, this.shapeVerts[i + 1].y);
                        this.sr.lineVertex(this.shapeVerts[i + 2].x, this.shapeVerts[i + 2].y);
                        this.sr.endLine(true);
                    }
                } else if (this.shapeType == 11) {
                    for(i = 0; i <= this.vertCount - 3; ++i) {
                        this.sr.beginLine();
                        this.sr.lineVertex(this.shapeVerts[0].x, this.shapeVerts[0].y);
                        this.sr.lineVertex(this.shapeVerts[i + 1].x, this.shapeVerts[i + 1].y);
                        this.sr.lineVertex(this.shapeVerts[i + 2].x, this.shapeVerts[i + 2].y);
                        this.sr.endLine(true);
                    }

                    if (this.vertCount >= 3) {
                        this.sr.beginLine();
                        this.sr.lineVertex(this.shapeVerts[0].x, this.shapeVerts[0].y);
                        this.sr.lineVertex(this.shapeVerts[this.vertCount - 1].x, this.shapeVerts[this.vertCount - 1].y);
                        this.sr.lineVertex(this.shapeVerts[1].x, this.shapeVerts[1].y);
                        this.sr.endLine(true);
                    }
                } else if (this.shapeType == 9) {
                    for(i = 0; i <= this.vertCount - 3; i += 3) {
                        this.sr.beginLine();
                        this.sr.lineVertex(this.shapeVerts[i + 0].x, this.shapeVerts[i + 0].y);
                        this.sr.lineVertex(this.shapeVerts[i + 1].x, this.shapeVerts[i + 1].y);
                        this.sr.lineVertex(this.shapeVerts[i + 2].x, this.shapeVerts[i + 2].y);
                        this.sr.endLine(true);
                    }
                } else if (this.shapeType == 5) {
                    for(i = 0; i <= this.vertCount - 2; i += 2) {
                        PGraphics2DX.TessVertex s1 = this.shapeVerts[i + 0];
                        PGraphics2DX.TessVertex s2 = this.shapeVerts[i + 1];
                        this.singleLine(s1.x, s1.y, s2.x, s2.y, this.strokeColor);
                    }
                } else if (this.shapeType == 3) {
                    for(i = 0; i <= this.vertCount - 1; ++i) {
                        this.singlePoint(this.shapeVerts[i].x, this.shapeVerts[i].y, this.strokeColor);
                    }
                }
            }

        }
    }

    public void beginContour() {
        super.beginContour();
        if (!this.useParentImpl) {
            this.appendContour(this.vertCount);
        }
    }

    public void vertex(float x, float y) {
        if (this.useParentImpl) {
            super.vertex(x, y);
        } else {
            this.curveVerts = 0;
            this.shapeVertex(x, y, 0.0F, 0.0F, this.fillColor, 0.0F);
        }
    }

    public void vertex(float x, float y, float u, float v) {
        if (this.useParentImpl) {
            super.vertex(x, y, u, v);
        } else {
            this.curveVerts = 0;
            this.textureImpl(this.imageTex);
            this.shapeVertex(x, y, u, v, this.tint ? this.tintColor : -1, 1.0F);
        }
    }

    public void vertex(float x, float y, float z) {
        showDepthWarningXYZ("vertex");
    }

    public void vertex(float x, float y, float z, float u, float v) {
        showDepthWarningXYZ("vertex");
    }

    public void bezierVertex(float x2, float y2, float x3, float y3, float x4, float y4) {
        if (this.useParentImpl) {
            super.bezierVertex(x2, y2, x3, y3, x4, y4);
        } else {
            this.bezierInitCheck();
            PMatrix3D draw = this.bezierDrawMatrix;
            float x1 = this.shapeVerts[this.vertCount - 1].x;
            float y1 = this.shapeVerts[this.vertCount - 1].y;
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
                this.shapeVertex(x1, y1, 0.0F, 0.0F, this.fillColor, 0.0F);
            }

        }
    }

    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        showDepthWarningXYZ("bezierVertex");
    }

    public void quadraticVertex(float cx, float cy, float x3, float y3) {
        if (this.useParentImpl) {
            super.quadraticVertex(cx, cy, x3, y3);
        } else {
            float x1 = this.shapeVerts[this.vertCount - 1].x;
            float y1 = this.shapeVerts[this.vertCount - 1].y;
            this.bezierVertex(x1 + (cx - x1) * 2.0F / 3.0F, y1 + (cy - y1) * 2.0F / 3.0F, x3 + (cx - x3) * 2.0F / 3.0F, y3 + (cy - y3) * 2.0F / 3.0F, x3, y3);
        }
    }

    public void quadraticVertex(float x2, float y2, float z2, float x4, float y4, float z4) {
        showDepthWarningXYZ("quadVertex");
    }

    public void curveVertex(float x, float y) {
        if (this.useParentImpl) {
            super.curveVertex(x, y);
        } else {
            this.curveInitCheck();
            this.cx1 = this.cx2;
            this.cx2 = this.cx3;
            this.cx3 = this.cx4;
            this.cy1 = this.cy2;
            this.cy2 = this.cy3;
            this.cy3 = this.cy4;
            this.cx4 = x;
            this.cy4 = y;
            ++this.curveVerts;
            if (this.curveVerts > 3) {
                PApplet.println("drawing curve...");
                PMatrix3D draw = this.curveDrawMatrix;
                float xplot1 = draw.m10 * this.cx1 + draw.m11 * this.cx2 + draw.m12 * this.cx3 + draw.m13 * this.cx4;
                float xplot2 = draw.m20 * this.cx1 + draw.m21 * this.cx2 + draw.m22 * this.cx3 + draw.m23 * this.cx4;
                float xplot3 = draw.m30 * this.cx1 + draw.m31 * this.cx2 + draw.m32 * this.cx3 + draw.m33 * this.cx4;
                float yplot1 = draw.m10 * this.cy1 + draw.m11 * this.cy2 + draw.m12 * this.cy3 + draw.m13 * this.cy4;
                float yplot2 = draw.m20 * this.cy1 + draw.m21 * this.cy2 + draw.m22 * this.cy3 + draw.m23 * this.cy4;
                float yplot3 = draw.m30 * this.cy1 + draw.m31 * this.cy2 + draw.m32 * this.cy3 + draw.m33 * this.cy4;
                float x0 = this.cx2;
                float y0 = this.cy2;
                if (this.curveVerts == 4) {
                    this.shapeVertex(x0, y0, 0.0F, 0.0F, this.fillColor, 0.0F);
                }

                for(int j = 0; j < this.curveDetail; ++j) {
                    x0 += xplot1;
                    xplot1 += xplot2;
                    xplot2 += xplot3;
                    y0 += yplot1;
                    yplot1 += yplot2;
                    yplot2 += yplot3;
                    this.shapeVertex(x0, y0, 0.0F, 0.0F, this.fillColor, 0.0F);
                }
            }

        }
    }

    public void curveVertex(float x, float y, float z) {
        showDepthWarningXYZ("curveVertex");
    }

    public void quad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        if (this.useParentImpl) {
            super.quad(x1, y1, x2, y2, x3, y3, x4, y4);
        } else {
            this.beginShape(17);
            this.vertex(x1, y1);
            this.vertex(x2, y2);
            this.vertex(x3, y3);
            this.vertex(x4, y4);
            this.endShape();
        }
    }

    public void triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (this.useParentImpl) {
            super.triangle(x1, y1, x2, y2, x3, y3);
        } else {
            this.beginShape(9);
            this.vertex(x1, y1);
            this.vertex(x2, y2);
            this.vertex(x3, y3);
            this.endShape();
        }
    }

    public void ellipseImpl(float a, float b, float c, float d) {
        if (this.useParentImpl) {
            super.ellipseImpl(a, b, c, d);
        } else {
            this.beginShape(20);
            float rx = c * 0.5F;
            float ry = d * 0.5F;
            float x = a + rx;
            float y = b + ry;
            int segments = this.circleDetail(PApplet.max(rx, ry) + (this.stroke ? this.strokeWeight : 0.0F), 6.2831855F);
            float step = 6.2831855F / (float)segments;
            float angle = 0.0F;

            for(int i = 0; i < segments; ++i) {
                angle += step;
                this.shapeVertex(x + PApplet.sin(angle) * rx, y + PApplet.cos(angle) * ry, 0.0F, 0.0F, this.fillColor, 0.0F);
            }

            this.knownConvexPolygon = true;
            this.endShape(2);
        }
    }

    public void line(float x1, float y1, float x2, float y2) {
        if (this.useParentImpl) {
            super.line(x1, y1, x2, y2);
        } else {
            this.incrementDepth();
            this.singleLine(x1, y1, x2, y2, this.strokeColor);
        }
    }

    public void point(float x, float y) {
        if (this.useParentImpl) {
            super.point(x, y);
        } else {
            this.incrementDepth();
            this.singlePoint(x, y, this.strokeColor);
        }
    }

    protected void arcImpl(float x, float y, float w, float h, float start, float stop, int mode) {
        if (this.useParentImpl) {
            super.arcImpl(x, y, w, h, start, stop, mode);
        } else {
            w *= 0.5F;
            h *= 0.5F;
            x += w;
            y += h;
            float diff = stop - start;
            int segments = this.circleDetail(PApplet.max(w, h), diff);
            float step = diff / (float)segments;
            this.beginShape(20);
            if (mode == 0 || mode == 3) {
                this.vertex(x, y);
            }

            if (mode == 0) {
                this.appendContour(this.vertCount);
            }

            for(int i = 0; i <= segments; ++i) {
                float s = PApplet.cos(start) * w;
                float c = PApplet.sin(start) * h;
                this.vertex(x + s, y + c);
                start += step;
            }

            this.knownConvexPolygon = true;
            if (mode != 2 && mode != 3) {
                this.endShape();
            } else {
                this.endShape(2);
            }

        }
    }

    protected void rectImpl(float x1, float y1, float x2, float y2, float tl, float tr, float br, float bl) {
        if (this.useParentImpl) {
            super.rectImpl(x1, y1, x2, y2, tl, tr, br, bl);
        } else {
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

            this.knownConvexPolygon = true;
            this.endShape(2);
        }
    }

    public void box(float w, float h, float d) {
        showMethodWarning("box");
    }

    public void sphere(float r) {
        showMethodWarning("sphere");
    }

    public void loadPixels() {
        super.loadPixels();
        this.allocatePixels();
        this.readPixels();
    }

    public void updatePixels() {
        super.updatePixels();
        this.image(this, 0.0F, 0.0F, (float)(this.width * 2), (float)(this.height * 2), 0, 0, this.pixelWidth, this.pixelHeight);
        this.flushBuffer();
    }

    protected void textCharModelImpl(TextureInfo info, float x0, float y0, float x1, float y1) {
        this.incrementDepth();
        this.check(6);
        this.textureImpl(this.textTex.textures[info.texIndex].glName);
        this.vertexImpl(x0, y0, info.u0, info.v0, this.fillColor, 1.0F);
        this.vertexImpl(x1, y0, info.u1, info.v0, this.fillColor, 1.0F);
        this.vertexImpl(x0, y1, info.u0, info.v1, this.fillColor, 1.0F);
        this.vertexImpl(x1, y0, info.u1, info.v0, this.fillColor, 1.0F);
        this.vertexImpl(x0, y1, info.u0, info.v1, this.fillColor, 1.0F);
        this.vertexImpl(x1, y1, info.u1, info.v1, this.fillColor, 1.0F);
    }

    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.preMatrixChanged();
        super.applyMatrix(n00, n01, n02, n10, n11, n12);
        this.postMatrixChanged();
    }

    public void applyMatrix(PMatrix2D source) {
        this.preMatrixChanged();
        super.applyMatrix(source);
        this.postMatrixChanged();
    }

    public void applyProjection(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.preMatrixChanged();
        super.applyProjection(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
        this.postMatrixChanged();
    }

    public void applyProjection(PMatrix3D mat) {
        this.preMatrixChanged();
        super.applyProjection(mat);
        this.postMatrixChanged();
    }

    public void popMatrix() {
        this.preMatrixChanged();
        super.popMatrix();
        this.postMatrixChanged();
    }

    public void popProjection() {
        this.preMatrixChanged();
        super.popProjection();
        this.postMatrixChanged();
    }

    public void pushMatrix() {
        this.preMatrixChanged();
        super.pushMatrix();
        this.postMatrixChanged();
    }

    public void pushProjection() {
        this.preMatrixChanged();
        super.pushProjection();
        this.postMatrixChanged();
    }

    public void resetMatrix() {
        this.preMatrixChanged();
        super.resetMatrix();
        this.postMatrixChanged();
    }

    public void resetProjection() {
        this.preMatrixChanged();
        super.resetProjection();
        this.postMatrixChanged();
    }

    public void rotate(float angle) {
        this.preMatrixChanged();
        super.rotate(angle);
        this.postMatrixChanged();
    }

    public void scale(float s) {
        this.preMatrixChanged();
        super.scale(s);
        this.postMatrixChanged();
    }

    public void scale(float sx, float sy) {
        this.preMatrixChanged();
        super.scale(sx, sy);
        this.postMatrixChanged();
    }

    public void setMatrix(PMatrix2D source) {
        this.preMatrixChanged();
        super.setMatrix(source);
        this.postMatrixChanged();
    }

    public void setProjection(PMatrix3D mat) {
        this.preMatrixChanged();
        super.setProjection(mat);
        this.postMatrixChanged();
    }

    public void shearX(float angle) {
        this.preMatrixChanged();
        super.shearX(angle);
        this.postMatrixChanged();
    }

    public void shearY(float angle) {
        this.preMatrixChanged();
        super.shearY(angle);
        this.postMatrixChanged();
    }

    public void translate(float tx, float ty) {
        this.preMatrixChanged();
        super.translate(tx, ty);
        this.postMatrixChanged();
    }

    public void updateProjmodelview() {
        this.preMatrixChanged();
        super.updateProjmodelview();
        this.postMatrixChanged();
    }

    public void updateGLModelview() {
        this.preMatrixChanged();
        super.updateGLModelview();
        this.postMatrixChanged();
    }

    public void updateGLProjection() {
        this.preMatrixChanged();
        super.updateGLProjection();
        this.postMatrixChanged();
    }

    public void updateGLProjmodelview() {
        this.preMatrixChanged();
        super.updateGLProjmodelview();
        this.postMatrixChanged();
    }

    protected void begin2D() {
        this.pushProjection();
        this.defaultPerspective();
        this.pushMatrix();
        this.defaultCamera();
    }

    protected void end2D() {
        this.popMatrix();
        this.popProjection();
    }

    public void filter(PShader shader) {
        if (!this.useParentImpl) {
            this.useOldP2D();
            super.filter(shader);
            this.useNewP2D();
        } else {
            super.filter(shader);
        }

    }

    public PShader loadShader(String fragFilename) {
        if (fragFilename != null && !fragFilename.equals("")) {
            PShader shader = new PShader(this.parent);
            shader.setFragmentShader(fragFilename);
            String[] vertSource = this.pgl.loadVertexShader(defP2DShaderVertURL);
            shader.setVertexShader(vertSource);
            return shader;
        } else {
            PGraphics.showWarning("The fragment shader is missing, cannot create shader object");
            return null;
        }
    }

    public void shader(PShader shader) {
        if (this.useParentImpl) {
            super.shader(shader);
        } else {
            this.flushBuffer();
            if (shader != null) {
                shader.init();
            }

            boolean res = this.checkShaderLocs(shader);
            if (res) {
                this.twoShader = shader;
                shader.type = 7;
            } else {
                PGraphics.showWarning("This shader cannot be used for 2D rendering");
            }

        }
    }

    public void shader(PShader shader, int kind) {
        if (this.useParentImpl) {
            super.shader(shader, kind);
        } else {
            PGraphics.showWarning("The P2D renderer does not accept shaders of different tyes");
        }
    }

    public void resetShader() {
        if (this.useParentImpl) {
            super.resetShader();
        } else {
            this.flushBuffer();
            this.twoShader = null;
        }
    }

    public void resetShader(int kind) {
        if (this.useParentImpl) {
            super.resetShader(kind);
        } else {
            PGraphics.showWarning("The P2D renderer does not accept shaders of different tyes");
        }
    }

    public void translate(float tx, float ty, float tz) {
        showDepthWarningXYZ("translate");
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

    public void applyMatrix(PMatrix3D source) {
        showVariationWarning("applyMatrix");
    }

    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        showVariationWarning("applyMatrix");
    }

    public void scale(float sx, float sy, float sz) {
        showDepthWarningXYZ("scale");
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

    public PMatrix3D getMatrix(PMatrix3D target) {
        showVariationWarning("getMatrix");
        return target;
    }

    public void setMatrix(PMatrix3D source) {
        showVariationWarning("setMatrix");
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

    public void lightSpecular(float v1, float v2, float v3) {
        showMethodWarning("lightSpecular");
    }

    private void incrementDepth() {
        if (this.depth < -this.largestNumberLessThanOne) {
            this.flushBuffer();
            this.pgl.clear(PGL.DEPTH_BUFFER_BIT);
            this.depth = 1.0F;
        }

        this.depth -= this.smallestDepthIncrement;
    }

    private void initTess() {
        TessellatorCallback callback = new TessellatorCallback() {
            public void begin(int type) {
            }

            public void end() {
            }

            public void vertex(Object data) {
                if (PGraphics2DX.this.usedVerts % 3 == 0) {
                    PGraphics2DX.this.check(3);
                }

                PGraphics2DX.TessVertex vert = (PGraphics2DX.TessVertex)data;
                PGraphics2DX.this.vertexImpl(vert.x, vert.y, vert.u, vert.v, vert.c, vert.f);
            }

            public void combine(double[] coords, Object[] data, float[] weights, Object[] outData) {
                float r = 0.0F;
                float g = 0.0F;
                float b = 0.0F;
                float a = 0.0F;

                int c;
                for(c = 0; c < data.length; ++c) {
                    int cx = ((PGraphics2DX.TessVertex)data[c]).c;
                    a += weights[c] * (float)(cx >> 24 & 255);
                    r += weights[c] * (float)(cx >> 16 & 255);
                    g += weights[c] * (float)(cx >> 8 & 255);
                    b += weights[c] * (float)(cx & 255);
                }

                c = ((int)a << 24) + ((int)r << 16) + ((int)g << 8) + (int)b;
                float u = 0.0F;
                float v = 0.0F;
                float f = 0.0F;

                for(int i = 0; i < data.length; ++i) {
                    u += weights[i] * ((PGraphics2DX.TessVertex)data[i]).u;
                    v += weights[i] * ((PGraphics2DX.TessVertex)data[i]).v;
                    f += weights[i] * ((PGraphics2DX.TessVertex)data[i]).f;
                }

                outData[0] = PGraphics2DX.this.new TessVertex((float)coords[0], (float)coords[1], u, v, c, f);
            }

            public void error(int err) {
                PApplet.println("glu error: " + err);
            }
        };
        this.tess = this.pgl.createTessellator(callback);
        this.tess.setCallback(PGL.TESS_EDGE_FLAG);
        this.tess.setWindingRule(PGL.TESS_WINDING_NONZERO);
    }

    private void initVerts() {
        for(int i = 0; i < this.shapeVerts.length; ++i) {
            this.shapeVerts[i] = new PGraphics2DX.TessVertex();
        }

    }

    private void flushBuffer() {
        if (this.usedVerts != 0) {
            if (this.vbo == 0) {
                IntBuffer vboBuff = IntBuffer.allocate(1);
                this.pgl.genBuffers(1, vboBuff);
                this.vbo = vboBuff.get(0);
            }

            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.vbo);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, this.usedVerts * 28, FloatBuffer.wrap(this.vertexData), PGL.DYNAMIC_DRAW);
            PShader shader = this.getShader();
            shader.bind();
            this.setAttribs();
            this.loadUniforms();
            this.pgl.drawArrays(PGL.TRIANGLES, 0, this.usedVerts);
            this.usedVerts = 0;
            shader.unbind();
        }
    }

    private boolean checkShaderLocs(PShader shader) {
        int positionLoc = shader.getAttributeLoc("position");
        if (positionLoc == -1) {
            positionLoc = shader.getAttributeLoc("vertex");
        }

        int transformLoc = shader.getUniformLoc("transform");
        if (transformLoc == -1) {
            transformLoc = shader.getUniformLoc("transformMatrix");
        }

        return positionLoc != -1 && transformLoc != -1;
    }

    private void loadShaderLocs(PShader shader) {
        this.positionLoc = shader.getAttributeLoc("position");
        if (this.positionLoc == -1) {
            this.positionLoc = shader.getAttributeLoc("vertex");
        }

        this.colorLoc = shader.getAttributeLoc("color");
        this.texCoordLoc = shader.getAttributeLoc("texCoord");
        this.texFactorLoc = shader.getAttributeLoc("texFactor");
        this.transformLoc = shader.getUniformLoc("transform");
        if (this.transformLoc == -1) {
            this.transformLoc = shader.getUniformLoc("transformMatrix");
        }

        this.texScaleLoc = shader.getUniformLoc("texScale");
        if (this.texScaleLoc == -1) {
            this.texScaleLoc = shader.getUniformLoc("texOffset");
        }

    }

    private PShader getShader() {
        PShader shader;
        if (this.twoShader == null) {
            if (this.defTwoShader == null) {
                String[] vertSource = this.pgl.loadVertexShader(defP2DShaderVertURL);
                String[] fragSource = this.pgl.loadFragmentShader(defP2DShaderFragURL);
                this.defTwoShader = new PShader(this.parent, vertSource, fragSource);
            }

            shader = this.defTwoShader;
        } else {
            shader = this.twoShader;
        }

        this.loadShaderLocs(shader);
        return shader;
    }

    protected PShader getPolyShader(boolean lit, boolean tex) {
        return super.getPolyShader(lit, tex);
    }

    private void setAttribs() {
        this.pgl.vertexAttribPointer(this.positionLoc, 3, PGL.FLOAT, false, 28, 0);
        this.pgl.enableVertexAttribArray(this.positionLoc);
        if (-1 < this.texCoordLoc) {
            this.pgl.vertexAttribPointer(this.texCoordLoc, 2, PGL.FLOAT, false, 28, 12);
            this.pgl.enableVertexAttribArray(this.texCoordLoc);
        }

        this.pgl.vertexAttribPointer(this.colorLoc, 4, PGL.UNSIGNED_BYTE, true, 28, 20);
        this.pgl.enableVertexAttribArray(this.colorLoc);
        if (-1 < this.texFactorLoc) {
            this.pgl.vertexAttribPointer(this.texFactorLoc, 1, PGL.FLOAT, false, 28, 24);
            this.pgl.enableVertexAttribArray(this.texFactorLoc);
        }

    }

    private void loadUniforms() {
        if (premultiplyMatrices) {
            this.pgl.uniformMatrix4fv(this.transformLoc, 1, true, FloatBuffer.wrap((new PMatrix3D()).get((float[])null)));
        } else {
            this.pgl.uniformMatrix4fv(this.transformLoc, 1, true, FloatBuffer.wrap(this.projmodelview.get((float[])null)));
        }

        this.pgl.activeTexture(PGL.TEXTURE0);
        this.pgl.bindTexture(PGL.TEXTURE_2D, this.tex);
        if (-1 < this.texScaleLoc) {
            if (this.tex == this.imageTex) {
                this.pgl.uniform2f(this.texScaleLoc, 1.0F / (float)this.texWidth, 1.0F / (float)this.texHeight);
            } else {
                this.pgl.uniform2f(this.texScaleLoc, 1.0F, 1.0F);
            }
        }

    }

    private void textureImpl(int glId) {
        if (glId != this.tex) {
            this.flushBuffer();
            this.tex = glId;
        }
    }

    private void check(int newVerts) {
        if (this.usedVerts + newVerts > 6000) {
            this.flushBuffer();
        }

    }

    private void vertexImpl(float x, float y, float u, float v, int c, float f) {
        int idx = this.usedVerts * 7;
        if (premultiplyMatrices) {
            this.vertexData[idx + 0] = this.projmodelview.m00 * x + this.projmodelview.m01 * y + this.projmodelview.m03;
            this.vertexData[idx + 1] = this.projmodelview.m10 * x + this.projmodelview.m11 * y + this.projmodelview.m13;
        } else {
            this.vertexData[idx + 0] = x;
            this.vertexData[idx + 1] = y;
        }

        this.vertexData[idx + 2] = this.depth;
        this.vertexData[idx + 3] = u;
        this.vertexData[idx + 4] = v;
        this.vertexData[idx + 5] = Float.intBitsToFloat(c);
        this.vertexData[idx + 6] = f;
        ++this.usedVerts;
    }

    private void vertexImpl(PGraphics2DX.TessVertex vert) {
        this.vertexImpl(vert.x, vert.y, vert.u, vert.v, vert.c, vert.f);
    }

    private void appendContour(int vertIndex) {
        if (this.contourCount >= this.contours.length) {
            this.contours = PApplet.expand(this.contours, this.contours.length * 2);
        }

        this.contours[this.contourCount] = vertIndex;
        ++this.contourCount;
    }

    private void shapeVertex(float x, float y, float u, float v, int c, float f) {
        int i;
        for(i = 0; i < this.vertCount; ++i) {
            if (this.shapeVerts[i].x == x && this.shapeVerts[i].y == y) {
                return;
            }
        }

        if (this.vertCount >= this.shapeVerts.length) {
            this.shapeVerts = (PGraphics2DX.TessVertex[])((PGraphics2DX.TessVertex[])PApplet.expand(this.shapeVerts, this.shapeVerts.length * 2));

            for(i = this.shapeVerts.length / 2; i < this.shapeVerts.length; ++i) {
                this.shapeVerts[i] = new PGraphics2DX.TessVertex();
            }
        }

        this.shapeVerts[this.vertCount].set(x, y, u, v, c, f);
        ++this.vertCount;
    }

    private void preMatrixChanged() {
        if (!premultiplyMatrices) {
            this.flushBuffer();
        }

    }

    private void postMatrixChanged() {
        float sxi = this.projmodelview.m00 * (float)this.width / 2.0F;
        float syi = this.projmodelview.m10 * (float)this.height / 2.0F;
        float sxj = this.projmodelview.m01 * (float)this.width / 2.0F;
        float syj = this.projmodelview.m11 * (float)this.height / 2.0F;
        float Imag = PApplet.sqrt(sxi * sxi + syi * syi);
        float Jmag = PApplet.sqrt(sxj * sxj + syj * syj);
        this.ellipseDetailMultiplier = PApplet.max(Imag, Jmag);
    }

    private void triangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        this.check(3);
        this.vertexImpl(x1, y1, 0.0F, 0.0F, color, 0.0F);
        this.vertexImpl(x2, y2, 0.0F, 0.0F, color, 0.0F);
        this.vertexImpl(x3, y3, 0.0F, 0.0F, color, 0.0F);
    }

    private void singleLine(float x1, float y1, float x2, float y2, int color) {
        float r = this.strokeWeight * 0.5F;
        float dx = x2 - x1;
        float dy = y2 - y1;
        float d = PApplet.sqrt(dx * dx + dy * dy);
        float tx = dy / d * r;
        float ty = dx / d * r;
        if (this.strokeCap == 4) {
            x1 -= ty;
            x2 += ty;
            y1 -= tx;
            y2 += tx;
        }

        this.triangle(x1 - tx, y1 + ty, x1 + tx, y1 - ty, x2 - tx, y2 + ty, color);
        this.triangle(x2 + tx, y2 - ty, x2 - tx, y2 + ty, x1 + tx, y1 - ty, color);
        if (r >= 1.0F && this.strokeCap == 2) {
            float angle = PApplet.atan2(dx, dy);
            int segments = this.circleDetail(r, 1.5707964F);
            float step = 1.5707964F / (float)segments;
            float psin = ty;
            float pcos = tx;

            for(int i = 1; i < segments; ++i) {
                angle += step;
                float nsin = PApplet.sin(angle) * r;
                float ncos = PApplet.cos(angle) * r;
                this.triangle(x2, y2, x2 + psin, y2 + pcos, x2 + nsin, y2 + ncos, color);
                this.triangle(x2, y2, x2 - pcos, y2 + psin, x2 - ncos, y2 + nsin, color);
                this.triangle(x1, y1, x1 - psin, y1 - pcos, x1 - nsin, y1 - ncos, color);
                this.triangle(x1, y1, x1 + pcos, y1 - psin, x1 + ncos, y1 - nsin, color);
                psin = nsin;
                pcos = ncos;
            }

            this.triangle(x2, y2, x2 + psin, y2 + pcos, x2 + tx, y2 - ty, color);
            this.triangle(x2, y2, x2 - pcos, y2 + psin, x2 + ty, y2 + tx, color);
            this.triangle(x1, y1, x1 - psin, y1 - pcos, x1 - tx, y1 + ty, color);
            this.triangle(x1, y1, x1 + pcos, y1 - psin, x1 - ty, y1 - tx, color);
        }

    }

    private void singlePoint(float x, float y, int color) {
        float r = this.strokeWeight * 0.5F;
        if (this.strokeCap == 2) {
            int segments = this.circleDetail(r);
            float step = 0.7853982F / (float)segments;
            float x1 = 0.0F;
            float y1 = r;
            float angle = 0.0F;

            for(int i = 0; i < segments; ++i) {
                angle += step;
                float x2;
                float y2;
                if (i < segments - 1) {
                    x2 = PApplet.sin(angle) * r;
                    y2 = PApplet.cos(angle) * r;
                } else {
                    x2 = y2 = PApplet.sin(0.7853982F) * r;
                }

                this.triangle(x, y, x + x1, y + y1, x + x2, y + y2, this.strokeColor);
                this.triangle(x, y, x + x1, y - y1, x + x2, y - y2, this.strokeColor);
                this.triangle(x, y, x - x1, y + y1, x - x2, y + y2, this.strokeColor);
                this.triangle(x, y, x - x1, y - y1, x - x2, y - y2, this.strokeColor);
                this.triangle(x, y, x + y1, y + x1, x + y2, y + x2, this.strokeColor);
                this.triangle(x, y, x + y1, y - x1, x + y2, y - x2, this.strokeColor);
                this.triangle(x, y, x - y1, y + x1, x - y2, y + x2, this.strokeColor);
                this.triangle(x, y, x - y1, y - x1, x - y2, y - x2, this.strokeColor);
                x1 = x2;
                y1 = y2;
            }
        } else {
            this.triangle(x - r, y - r, x + r, y - r, x - r, y + r, color);
            this.triangle(x + r, y - r, x - r, y + r, x + r, y + r, color);
        }

    }

    int circleDetail(float radius, float delta) {
        radius *= this.ellipseDetailMultiplier;
        return (int)(PApplet.min(127.0F, PApplet.sqrt(radius) / 0.7853982F * PApplet.abs(delta) * 0.75F) + 1.0F);
    }

    int circleDetail(float radius) {
        return this.circleDetail(radius, 0.7853982F);
    }

    private class TessVertex {
        float x;
        float y;
        float u;
        float v;
        int c;
        float f;

        public TessVertex() {
        }

        public TessVertex(float x, float y, float u, float v, int c, float f) {
            this.set(x, y, u, v, c, f);
        }

        public void set(float x, float y, float u, float v, int c, float f) {
            this.x = x;
            this.y = y;
            this.u = u;
            this.v = v;
            this.c = c;
            this.f = f;
        }

        public String toString() {
            return this.x + ", " + this.y;
        }
    }

    private class StrokeRenderer {
        int lineVertexCount;
        float fx;
        float fy;
        float sx;
        float sy;
        float sdx;
        float sdy;
        float px;
        float py;
        float pdx;
        float pdy;
        float lx;
        float ly;
        float r;

        private StrokeRenderer() {
        }

        void beginLine() {
            this.lineVertexCount = 0;
            this.r = PGraphics2DX.this.strokeWeight * 0.5F;
        }

        void lineVertex(float x, float y) {
            if (this.lineVertexCount <= 0 || x != this.lx || y != this.ly) {
                if (this.lineVertexCount == 0) {
                    this.fx = x;
                    this.fy = y;
                } else if (this.r < 1.0F) {
                    PGraphics2DX.this.singleLine(this.lx, this.ly, x, y, PGraphics2DX.this.strokeColor);
                } else if (this.lineVertexCount == 1) {
                    this.sx = x;
                    this.sy = y;
                } else {
                    float angle1 = PApplet.atan2(this.lx - this.px, this.ly - this.py);
                    float angle2 = PApplet.atan2(this.lx - x, this.ly - y);
                    float diff = angle1 - angle2;
                    diff += diff > 3.1415927F ? -6.2831855F : (diff < -3.1415927F ? 6.2831855F : 0.0F);
                    float dx;
                    float dy;
                    float d;
                    float tx;
                    float ty;
                    if (PGraphics2DX.this.strokeJoin != 32 && PGraphics2DX.this.strokeJoin != 2 && PApplet.abs(diff) >= 0.20943952F && PApplet.abs(diff) <= 3.1405928F) {
                        dx = 1.5707964F - diff / 2.0F;
                        dy = this.r / PApplet.cos(dx);
                        d = (angle1 + angle2) / 2.0F;
                        tx = PApplet.sin(d) * dy;
                        ty = PApplet.cos(d) * dy;
                        if (PApplet.abs(angle1 - angle2) < 3.1415927F) {
                            tx *= -1.0F;
                            ty *= -1.0F;
                        }

                        if (this.lineVertexCount == 2) {
                            this.sdx = tx;
                            this.sdy = ty;
                        } else {
                            PGraphics2DX.this.triangle(this.px - this.pdx, this.py - this.pdy, this.px + this.pdx, this.py + this.pdy, this.lx - tx, this.ly - ty, PGraphics2DX.this.strokeColor);
                            PGraphics2DX.this.triangle(this.px + this.pdx, this.py + this.pdy, this.lx - tx, this.ly - ty, this.lx + tx, this.ly + ty, PGraphics2DX.this.strokeColor);
                        }

                        this.pdx = tx;
                        this.pdy = ty;
                    } else {
                        dx = this.lx - this.px;
                        dy = this.ly - this.py;
                        d = PApplet.sqrt(dx * dx + dy * dy);
                        tx = dy / d * this.r;
                        ty = -dx / d * this.r;
                        if (this.lineVertexCount == 2) {
                            this.sdx = tx;
                            this.sdy = ty;
                        } else {
                            PGraphics2DX.this.triangle(this.px - this.pdx, this.py - this.pdy, this.px + this.pdx, this.py + this.pdy, this.lx - tx, this.ly - ty, PGraphics2DX.this.strokeColor);
                            PGraphics2DX.this.triangle(this.px + this.pdx, this.py + this.pdy, this.lx - tx, this.ly - ty, this.lx + tx, this.ly + ty, PGraphics2DX.this.strokeColor);
                        }

                        dx = x - this.lx;
                        dy = y - this.ly;
                        d = PApplet.sqrt(dx * dx + dy * dy);
                        float nx = dy / d * this.r;
                        float ny = -dx / d * this.r;
                        if (PGraphics2DX.this.strokeJoin == 2) {
                            float theta1 = diff > 0.0F ? angle1 - 1.5707964F : angle1 + 1.5707964F;
                            float theta2 = diff > 0.0F ? angle2 + 1.5707964F : angle2 - 1.5707964F;
                            float delta = theta2 - theta1;
                            delta += delta > 3.1415927F ? -6.2831855F : (delta < -3.1415927F ? 6.2831855F : 0.0F);
                            float ax1 = diff < 0.0F ? this.lx + tx : this.lx - tx;
                            float ay1 = diff < 0.0F ? this.ly + ty : this.ly - ty;
                            float ax2 = diff < 0.0F ? this.lx + nx : this.lx - nx;
                            float ay2 = diff < 0.0F ? this.ly + ny : this.ly - ny;
                            this.arcJoin(this.lx, this.ly, theta1, delta, ax1, ay1, ax2, ay2);
                        } else if (diff < 0.0F) {
                            PGraphics2DX.this.triangle(this.lx, this.ly, this.lx + tx, this.ly + ty, this.lx + nx, this.ly + ny, PGraphics2DX.this.strokeColor);
                        } else {
                            PGraphics2DX.this.triangle(this.lx, this.ly, this.lx - tx, this.ly - ty, this.lx - nx, this.ly - ny, PGraphics2DX.this.strokeColor);
                        }

                        this.pdx = nx;
                        this.pdy = ny;
                    }
                }

                this.px = this.lx;
                this.py = this.ly;
                this.lx = x;
                this.ly = y;
                ++this.lineVertexCount;
            }
        }

        void endLine(boolean closed) {
            if (this.lineVertexCount >= 2) {
                if (this.lineVertexCount == 2) {
                    PGraphics2DX.this.singleLine(this.px, this.py, this.lx, this.ly, PGraphics2DX.this.strokeColor);
                } else if (this.r < 1.0F) {
                    if (closed) {
                        PGraphics2DX.this.singleLine(this.lx, this.ly, this.fx, this.fy, PGraphics2DX.this.strokeColor);
                    }

                } else {
                    if (closed) {
                        this.lineVertex(this.fx, this.fy);
                        this.lineVertex(this.sx, this.sy);
                        PGraphics2DX.this.triangle(this.px - this.pdx, this.py - this.pdy, this.px + this.pdx, this.py + this.pdy, this.sx - this.sdx, this.sy - this.sdy, PGraphics2DX.this.strokeColor);
                        PGraphics2DX.this.triangle(this.px + this.pdx, this.py + this.pdy, this.sx - this.sdx, this.sy - this.sdy, this.sx + this.sdx, this.sy + this.sdy, PGraphics2DX.this.strokeColor);
                    } else {
                        float dx = this.lx - this.px;
                        float dy = this.ly - this.py;
                        float d = PApplet.sqrt(dx * dx + dy * dy);
                        float tx = dy / d * this.r;
                        float ty = -dx / d * this.r;
                        if (PGraphics2DX.this.strokeCap == 4) {
                            this.lx -= ty;
                            this.ly += tx;
                        }

                        PGraphics2DX.this.triangle(this.px - this.pdx, this.py - this.pdy, this.px + this.pdx, this.py + this.pdy, this.lx - tx, this.ly - ty, PGraphics2DX.this.strokeColor);
                        PGraphics2DX.this.triangle(this.px + this.pdx, this.py + this.pdy, this.lx - tx, this.ly - ty, this.lx + tx, this.ly + ty, PGraphics2DX.this.strokeColor);
                        if (PGraphics2DX.this.strokeCap == 2) {
                            this.lineCap(this.lx, this.ly, PApplet.atan2(dx, dy));
                        }

                        dx = this.fx - this.sx;
                        dy = this.fy - this.sy;
                        d = PApplet.sqrt(dx * dx + dy * dy);
                        tx = dy / d * this.r;
                        ty = -dx / d * this.r;
                        if (PGraphics2DX.this.strokeCap == 4) {
                            this.fx -= ty;
                            this.fy += tx;
                        }

                        PGraphics2DX.this.triangle(this.sx - this.sdx, this.sy - this.sdy, this.sx + this.sdx, this.sy + this.sdy, this.fx + tx, this.fy + ty, PGraphics2DX.this.strokeColor);
                        PGraphics2DX.this.triangle(this.sx + this.sdx, this.sy + this.sdy, this.fx + tx, this.fy + ty, this.fx - tx, this.fy - ty, PGraphics2DX.this.strokeColor);
                        if (PGraphics2DX.this.strokeCap == 2) {
                            this.lineCap(this.fx, this.fy, PApplet.atan2(dx, dy));
                        }
                    }

                }
            }
        }

        void arcJoin(float x, float y, float start, float delta, float x1, float y1, float x3, float y3) {
            int segments = PGraphics2DX.this.circleDetail(this.r, delta);
            float step = delta / (float)segments;

            for(int i = 0; i < segments - 1; ++i) {
                start += step;
                float x2 = x + PApplet.sin(start) * this.r;
                float y2 = y + PApplet.cos(start) * this.r;
                PGraphics2DX.this.triangle(x, y, x1, y1, x2, y2, PGraphics2DX.this.strokeColor);
                x1 = x2;
                y1 = y2;
            }

            PGraphics2DX.this.triangle(x, y, x1, y1, x3, y3, PGraphics2DX.this.strokeColor);
        }

        void arcJoin(float x, float y, float start, float delta) {
            int segments = PGraphics2DX.this.circleDetail(this.r, delta);
            float step = delta / (float)segments;
            float x1 = x + PApplet.sin(start) * this.r;
            float y1 = y + PApplet.cos(start) * this.r;

            for(int i = 0; i < segments; ++i) {
                start += step;
                float x2 = x + PApplet.sin(start) * this.r;
                float y2 = y + PApplet.cos(start) * this.r;
                PGraphics2DX.this.triangle(x, y, x1, y1, x2, y2, PGraphics2DX.this.strokeColor);
                x1 = x2;
                y1 = y2;
            }

        }

        void lineCap(float x, float y, float angle) {
            this.arcJoin(x, y, angle - 1.5707964F, 3.1415927F);
        }
    }
}

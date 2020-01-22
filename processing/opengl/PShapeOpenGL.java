package processing.opengl;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL.AttributeMap;
import processing.opengl.PGraphicsOpenGL.InGeometry;
import processing.opengl.PGraphicsOpenGL.IndexCache;
import processing.opengl.PGraphicsOpenGL.TessGeometry;
import processing.opengl.PGraphicsOpenGL.Tessellator;
import processing.opengl.PGraphicsOpenGL.TexCache;
import processing.opengl.PGraphicsOpenGL.VertexAttribute;

public class PShapeOpenGL extends PShape {
    public static final int POSITION = 0;
    public static final int NORMAL = 1;
    public static final int TEXCOORD = 2;
    public static final int DIRECTION = 3;
    public static final int OFFSET = 4;
    protected static final int TRANSLATE = 0;
    protected static final int ROTATE = 1;
    protected static final int SCALE = 2;
    protected static final int MATRIX = 3;
    protected PGraphicsOpenGL pg;
    protected PGL pgl;
    protected int context;
    protected PShapeOpenGL root;
    protected InGeometry inGeo;
    protected TessGeometry tessGeo;
    protected Tessellator tessellator;
    protected AttributeMap polyAttribs;
    protected HashSet<PImage> textures;
    protected boolean strokedTexture;
    protected boolean untexChild;
    protected VertexBuffer bufPolyVertex;
    protected VertexBuffer bufPolyColor;
    protected VertexBuffer bufPolyNormal;
    protected VertexBuffer bufPolyTexcoord;
    protected VertexBuffer bufPolyAmbient;
    protected VertexBuffer bufPolySpecular;
    protected VertexBuffer bufPolyEmissive;
    protected VertexBuffer bufPolyShininess;
    protected VertexBuffer bufPolyIndex;
    protected VertexBuffer bufLineVertex;
    protected VertexBuffer bufLineColor;
    protected VertexBuffer bufLineAttrib;
    protected VertexBuffer bufLineIndex;
    protected VertexBuffer bufPointVertex;
    protected VertexBuffer bufPointColor;
    protected VertexBuffer bufPointAttrib;
    protected VertexBuffer bufPointIndex;
    public int glUsage;
    protected int polyVertCopyOffset;
    protected int polyIndCopyOffset;
    protected int lineVertCopyOffset;
    protected int lineIndCopyOffset;
    protected int pointVertCopyOffset;
    protected int pointIndCopyOffset;
    protected int polyIndexOffset;
    protected int polyVertexOffset;
    protected int polyVertexAbs;
    protected int polyVertexRel;
    protected int lineIndexOffset;
    protected int lineVertexOffset;
    protected int lineVertexAbs;
    protected int lineVertexRel;
    protected int pointIndexOffset;
    protected int pointVertexOffset;
    protected int pointVertexAbs;
    protected int pointVertexRel;
    protected int firstPolyIndexCache;
    protected int lastPolyIndexCache;
    protected int firstLineIndexCache;
    protected int lastLineIndexCache;
    protected int firstPointIndexCache;
    protected int lastPointIndexCache;
    protected int firstPolyVertex;
    protected int lastPolyVertex;
    protected int firstLineVertex;
    protected int lastLineVertex;
    protected int firstPointVertex;
    protected int lastPointVertex;
    protected PMatrix transform;
    protected PMatrix transformInv;
    protected PMatrix matrixInv;
    protected boolean tessellated;
    protected boolean needBufferInit;
    protected boolean solid;
    protected boolean breakShape;
    protected boolean shapeCreated;
    protected boolean hasPolys;
    protected boolean hasLines;
    protected boolean hasPoints;
    protected int bezierDetail;
    protected int curveDetail;
    protected float curveTightness;
    protected int savedBezierDetail;
    protected int savedCurveDetail;
    protected float savedCurveTightness;
    protected float normalX;
    protected float normalY;
    protected float normalZ;
    protected static final int NORMAL_MODE_AUTO = 0;
    protected static final int NORMAL_MODE_SHAPE = 1;
    protected static final int NORMAL_MODE_VERTEX = 2;
    protected int normalMode;
    protected boolean modified;
    protected boolean modifiedPolyVertices;
    protected boolean modifiedPolyColors;
    protected boolean modifiedPolyNormals;
    protected boolean modifiedPolyTexCoords;
    protected boolean modifiedPolyAmbient;
    protected boolean modifiedPolySpecular;
    protected boolean modifiedPolyEmissive;
    protected boolean modifiedPolyShininess;
    protected boolean modifiedLineVertices;
    protected boolean modifiedLineColors;
    protected boolean modifiedLineAttributes;
    protected boolean modifiedPointVertices;
    protected boolean modifiedPointColors;
    protected boolean modifiedPointAttributes;
    protected int firstModifiedPolyVertex;
    protected int lastModifiedPolyVertex;
    protected int firstModifiedPolyColor;
    protected int lastModifiedPolyColor;
    protected int firstModifiedPolyNormal;
    protected int lastModifiedPolyNormal;
    protected int firstModifiedPolyTexcoord;
    protected int lastModifiedPolyTexcoord;
    protected int firstModifiedPolyAmbient;
    protected int lastModifiedPolyAmbient;
    protected int firstModifiedPolySpecular;
    protected int lastModifiedPolySpecular;
    protected int firstModifiedPolyEmissive;
    protected int lastModifiedPolyEmissive;
    protected int firstModifiedPolyShininess;
    protected int lastModifiedPolyShininess;
    protected int firstModifiedLineVertex;
    protected int lastModifiedLineVertex;
    protected int firstModifiedLineColor;
    protected int lastModifiedLineColor;
    protected int firstModifiedLineAttribute;
    protected int lastModifiedLineAttribute;
    protected int firstModifiedPointVertex;
    protected int lastModifiedPointVertex;
    protected int firstModifiedPointColor;
    protected int lastModifiedPointColor;
    protected int firstModifiedPointAttribute;
    protected int lastModifiedPointAttribute;
    protected boolean savedStroke;
    protected int savedStrokeColor;
    protected float savedStrokeWeight;
    protected int savedStrokeCap;
    protected int savedStrokeJoin;
    protected boolean savedFill;
    protected int savedFillColor;
    protected boolean savedTint;
    protected int savedTintColor;
    protected int savedAmbientColor;
    protected int savedSpecularColor;
    protected int savedEmissiveColor;
    protected float savedShininess;
    protected int savedTextureMode;

    PShapeOpenGL() {
        this.glUsage = PGL.STATIC_DRAW;
        this.needBufferInit = false;
        this.solid = true;
        this.breakShape = false;
        this.shapeCreated = false;
    }

    public PShapeOpenGL(PGraphicsOpenGL pg, int family) {
        this.glUsage = PGL.STATIC_DRAW;
        this.needBufferInit = false;
        this.solid = true;
        this.breakShape = false;
        this.shapeCreated = false;
        this.pg = pg;
        this.family = family;
        this.pgl = pg.pgl;
        this.context = this.pgl.createEmptyContext();
        this.bufPolyVertex = null;
        this.bufPolyColor = null;
        this.bufPolyNormal = null;
        this.bufPolyTexcoord = null;
        this.bufPolyAmbient = null;
        this.bufPolySpecular = null;
        this.bufPolyEmissive = null;
        this.bufPolyShininess = null;
        this.bufPolyIndex = null;
        this.bufLineVertex = null;
        this.bufLineColor = null;
        this.bufLineAttrib = null;
        this.bufLineIndex = null;
        this.bufPointVertex = null;
        this.bufPointColor = null;
        this.bufPointAttrib = null;
        this.bufPointIndex = null;
        this.tessellator = pg.tessellator;
        this.root = this;
        this.parent = null;
        this.tessellated = false;
        if (family == 3 || family == 1 || family == 2) {
            this.polyAttribs = PGraphicsOpenGL.newAttributeMap();
            this.inGeo = PGraphicsOpenGL.newInGeometry(pg, this.polyAttribs, 1);
        }

        this.textureMode = pg.textureMode;
        this.colorMode(pg.colorMode, pg.colorModeX, pg.colorModeY, pg.colorModeZ, pg.colorModeA);
        this.fill = pg.fill;
        this.fillColor = pg.fillColor;
        this.stroke = pg.stroke;
        this.strokeColor = pg.strokeColor;
        this.strokeWeight = pg.strokeWeight;
        this.strokeCap = pg.strokeCap;
        this.strokeJoin = pg.strokeJoin;
        this.tint = pg.tint;
        this.tintColor = pg.tintColor;
        this.setAmbient = pg.setAmbient;
        this.ambientColor = pg.ambientColor;
        this.specularColor = pg.specularColor;
        this.emissiveColor = pg.emissiveColor;
        this.shininess = pg.shininess;
        this.sphereDetailU = pg.sphereDetailU;
        this.sphereDetailV = pg.sphereDetailV;
        this.bezierDetail = pg.bezierDetail;
        this.curveDetail = pg.curveDetail;
        this.curveTightness = pg.curveTightness;
        this.rectMode = pg.rectMode;
        this.ellipseMode = pg.ellipseMode;
        this.normalX = this.normalY = 0.0F;
        this.normalZ = 1.0F;
        this.normalMode = 0;
        this.breakShape = false;
        if (family == 0) {
            this.shapeCreated = true;
        }

        this.perVertexStyles = true;
    }

    public PShapeOpenGL(PGraphicsOpenGL pg, int kind, float... p) {
        this(pg, 1);
        this.setKind(kind);
        this.setParams(p);
    }

    public void addChild(PShape who) {
        if (who instanceof PShapeOpenGL) {
            if (this.family == 0) {
                PShapeOpenGL c3d = (PShapeOpenGL)who;
                super.addChild(c3d);
                c3d.updateRoot(this.root);
                this.markForTessellation();
                if (c3d.family == 0) {
                    if (c3d.textures != null) {
                        Iterator var3 = c3d.textures.iterator();

                        while(var3.hasNext()) {
                            PImage tex = (PImage)var3.next();
                            this.addTexture(tex);
                        }
                    } else {
                        this.untexChild(true);
                    }

                    if (c3d.strokedTexture) {
                        this.strokedTexture(true);
                    }
                } else if (c3d.image != null) {
                    this.addTexture(c3d.image);
                    if (c3d.stroke) {
                        this.strokedTexture(true);
                    }
                } else {
                    this.untexChild(true);
                }
            } else {
                PGraphics.showWarning("Cannot add child shape to non-group shape.");
            }
        } else {
            PGraphics.showWarning("Shape must be OpenGL to be added to the group.");
        }

    }

    public void addChild(PShape who, int idx) {
        if (who instanceof PShapeOpenGL) {
            if (this.family == 0) {
                PShapeOpenGL c3d = (PShapeOpenGL)who;
                super.addChild(c3d, idx);
                c3d.updateRoot(this.root);
                this.markForTessellation();
                if (c3d.family == 0) {
                    if (c3d.textures != null) {
                        Iterator var4 = c3d.textures.iterator();

                        while(var4.hasNext()) {
                            PImage tex = (PImage)var4.next();
                            this.addTexture(tex);
                        }
                    } else {
                        this.untexChild(true);
                    }

                    if (c3d.strokedTexture) {
                        this.strokedTexture(true);
                    }
                } else if (c3d.image != null) {
                    this.addTexture(c3d.image);
                    if (c3d.stroke) {
                        this.strokedTexture(true);
                    }
                } else {
                    this.untexChild(true);
                }
            } else {
                PGraphics.showWarning("Cannot add child shape to non-group shape.");
            }
        } else {
            PGraphics.showWarning("Shape must be OpenGL to be added to the group.");
        }

    }

    public void removeChild(int idx) {
        super.removeChild(idx);
        this.strokedTexture(false);
        this.untexChild(false);
        this.markForTessellation();
    }

    protected void updateRoot(PShape root) {
        this.root = (PShapeOpenGL)root;
        if (this.family == 0) {
            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                child.updateRoot(root);
            }
        }

    }

    public static PShapeOpenGL createShape(PGraphicsOpenGL pg, PShape src) {
        PShapeOpenGL dest = null;
        if (src.getFamily() == 0) {
            dest = (PShapeOpenGL)pg.createShapeFamily(0);
            copyGroup(pg, src, dest);
        } else if (src.getFamily() == 1) {
            dest = (PShapeOpenGL)pg.createShapePrimitive(src.getKind(), src.getParams());
            PShape.copyPrimitive(src, dest);
        } else if (src.getFamily() == 3) {
            dest = (PShapeOpenGL)pg.createShapeFamily(3);
            PShape.copyGeometry(src, dest);
        } else if (src.getFamily() == 2) {
            dest = (PShapeOpenGL)pg.createShapeFamily(2);
            PShape.copyPath(src, dest);
        }

        dest.setName(src.getName());
        dest.width = src.width;
        dest.height = src.height;
        dest.depth = src.depth;
        return dest;
    }

    public static void copyGroup(PGraphicsOpenGL pg, PShape src, PShape dest) {
        copyMatrix(src, dest);
        copyStyles(src, dest);
        copyImage(src, dest);

        for(int i = 0; i < src.getChildCount(); ++i) {
            PShape c = createShape(pg, src.getChild(i));
            dest.addChild(c);
        }

    }

    public float getWidth() {
        PVector min = new PVector(1.0F / 0.0, 1.0F / 0.0, 1.0F / 0.0);
        PVector max = new PVector(-1.0F / 0.0, -1.0F / 0.0, -1.0F / 0.0);
        if (this.shapeCreated) {
            this.getVertexMin(min);
            this.getVertexMax(max);
        }

        this.width = max.x - min.x;
        return this.width;
    }

    public float getHeight() {
        PVector min = new PVector(1.0F / 0.0, 1.0F / 0.0, 1.0F / 0.0);
        PVector max = new PVector(-1.0F / 0.0, -1.0F / 0.0, -1.0F / 0.0);
        if (this.shapeCreated) {
            this.getVertexMin(min);
            this.getVertexMax(max);
        }

        this.height = max.y - min.y;
        return this.height;
    }

    public float getDepth() {
        PVector min = new PVector(1.0F / 0.0, 1.0F / 0.0, 1.0F / 0.0);
        PVector max = new PVector(-1.0F / 0.0, -1.0F / 0.0, -1.0F / 0.0);
        if (this.shapeCreated) {
            this.getVertexMin(min);
            this.getVertexMax(max);
        }

        this.depth = max.z - min.z;
        return this.depth;
    }

    protected void getVertexMin(PVector min) {
        this.updateTessellation();
        if (this.family == 0) {
            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                child.getVertexMin(min);
            }
        } else {
            if (this.hasPolys) {
                this.tessGeo.getPolyVertexMin(min, this.firstPolyVertex, this.lastPolyVertex);
            }

            if (this.is3D()) {
                if (this.hasLines) {
                    this.tessGeo.getLineVertexMin(min, this.firstLineVertex, this.lastLineVertex);
                }

                if (this.hasPoints) {
                    this.tessGeo.getPointVertexMin(min, this.firstPointVertex, this.lastPointVertex);
                }
            }
        }

    }

    protected void getVertexMax(PVector max) {
        this.updateTessellation();
        if (this.family == 0) {
            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                child.getVertexMax(max);
            }
        } else {
            if (this.hasPolys) {
                this.tessGeo.getPolyVertexMax(max, this.firstPolyVertex, this.lastPolyVertex);
            }

            if (this.is3D()) {
                if (this.hasLines) {
                    this.tessGeo.getLineVertexMax(max, this.firstLineVertex, this.lastLineVertex);
                }

                if (this.hasPoints) {
                    this.tessGeo.getPointVertexMax(max, this.firstPointVertex, this.lastPointVertex);
                }
            }
        }

    }

    protected int getVertexSum(PVector sum, int count) {
        this.updateTessellation();
        if (this.family == 0) {
            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                count += child.getVertexSum(sum, count);
            }
        } else {
            if (this.hasPolys) {
                count += this.tessGeo.getPolyVertexSum(sum, this.firstPolyVertex, this.lastPolyVertex);
            }

            if (this.is3D()) {
                if (this.hasLines) {
                    count += this.tessGeo.getLineVertexSum(sum, this.firstLineVertex, this.lastLineVertex);
                }

                if (this.hasPoints) {
                    count += this.tessGeo.getPointVertexSum(sum, this.firstPointVertex, this.lastPointVertex);
                }
            }
        }

        return count;
    }

    public void setTextureMode(int mode) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setTextureMode()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setTextureMode(mode);
                }
            } else {
                this.setTextureModeImpl(mode);
            }

        }
    }

    protected void setTextureModeImpl(int mode) {
        if (this.textureMode != mode) {
            this.textureMode = mode;
            if (this.image != null) {
                float uFactor = (float)this.image.width;
                float vFactor = (float)this.image.height;
                if (this.textureMode == 1) {
                    uFactor = 1.0F / uFactor;
                    vFactor = 1.0F / vFactor;
                }

                this.scaleTextureUV(uFactor, vFactor);
            }

        }
    }

    public void setTexture(PImage tex) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setTexture()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setTexture(tex);
                }
            } else {
                this.setTextureImpl(tex);
            }

        }
    }

    protected void setTextureImpl(PImage tex) {
        PImage image0 = this.image;
        this.image = tex;
        if (this.textureMode == 2 && image0 != this.image) {
            float uFactor = 1.0F;
            float vFactor = 1.0F;
            if (this.image != null) {
                uFactor /= (float)this.image.width;
                vFactor /= (float)this.image.height;
            }

            if (image0 != null) {
                uFactor *= (float)image0.width;
                vFactor *= (float)image0.height;
            }

            this.scaleTextureUV(uFactor, vFactor);
        }

        if (image0 != tex && this.parent != null) {
            ((PShapeOpenGL)this.parent).removeTexture(image0, this);
        }

        if (this.parent != null) {
            ((PShapeOpenGL)this.parent).addTexture(this.image);
            if (this.is2D() && this.stroke) {
                ((PShapeOpenGL)this.parent).strokedTexture(true);
            }
        }

    }

    protected void scaleTextureUV(float uFactor, float vFactor) {
        if (!PGraphicsOpenGL.same(uFactor, 1.0F) || !PGraphicsOpenGL.same(vFactor, 1.0F)) {
            int last1;
            float u;
            for(last1 = 0; last1 < this.inGeo.vertexCount; ++last1) {
                float u = this.inGeo.texcoords[2 * last1 + 0];
                u = this.inGeo.texcoords[2 * last1 + 1];
                this.inGeo.texcoords[2 * last1 + 0] = PApplet.min(1.0F, u * uFactor);
                this.inGeo.texcoords[2 * last1 + 1] = PApplet.min(1.0F, u * uFactor);
            }

            if (this.shapeCreated && this.tessellated && this.hasPolys) {
                last1 = 0;
                if (this.is3D()) {
                    last1 = this.lastPolyVertex + 1;
                } else if (this.is2D()) {
                    last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }
                }

                for(int i = this.firstLineVertex; i < last1; ++i) {
                    u = this.tessGeo.polyTexCoords[2 * i + 0];
                    float v = this.tessGeo.polyTexCoords[2 * i + 1];
                    this.tessGeo.polyTexCoords[2 * i + 0] = PApplet.min(1.0F, u * uFactor);
                    this.tessGeo.polyTexCoords[2 * i + 1] = PApplet.min(1.0F, v * uFactor);
                }

                this.root.setModifiedPolyTexCoords(this.firstPolyVertex, last1 - 1);
            }

        }
    }

    protected void addTexture(PImage tex) {
        if (this.textures == null) {
            this.textures = new HashSet();
        }

        this.textures.add(tex);
        if (this.parent != null) {
            ((PShapeOpenGL)this.parent).addTexture(tex);
        }

    }

    protected void removeTexture(PImage tex, PShapeOpenGL caller) {
        if (this.textures != null && this.textures.contains(tex)) {
            boolean childHasTex = false;

            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                if (child != caller && child.hasTexture(tex)) {
                    childHasTex = true;
                    break;
                }
            }

            if (!childHasTex) {
                this.textures.remove(tex);
                if (this.textures.size() == 0) {
                    this.textures = null;
                }
            }

            if (this.parent != null) {
                ((PShapeOpenGL)this.parent).removeTexture(tex, this);
            }

        }
    }

    protected void strokedTexture(boolean newValue) {
        this.strokedTexture(newValue, (PShapeOpenGL)null);
    }

    protected void strokedTexture(boolean newValue, PShapeOpenGL caller) {
        if (this.strokedTexture != newValue) {
            if (newValue) {
                this.strokedTexture = true;
            } else {
                this.strokedTexture = false;

                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    if (child != caller && child.hasStrokedTexture()) {
                        this.strokedTexture = true;
                        break;
                    }
                }
            }

            if (this.parent != null) {
                ((PShapeOpenGL)this.parent).strokedTexture(newValue, this);
            }

        }
    }

    protected void untexChild(boolean newValue) {
        this.untexChild(newValue, (PShapeOpenGL)null);
    }

    protected void untexChild(boolean newValue, PShapeOpenGL caller) {
        if (this.untexChild != newValue) {
            if (newValue) {
                this.untexChild = true;
            } else {
                this.untexChild = false;

                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    if (child != caller && !child.hasTexture()) {
                        this.untexChild = true;
                        break;
                    }
                }
            }

            if (this.parent != null) {
                ((PShapeOpenGL)this.parent).untexChild(newValue, this);
            }

        }
    }

    protected boolean hasTexture() {
        if (this.family != 0) {
            return this.image != null;
        } else {
            return this.textures != null && 0 < this.textures.size();
        }
    }

    protected boolean hasTexture(PImage tex) {
        if (this.family != 0) {
            return this.image == tex;
        } else {
            return this.textures != null && this.textures.contains(tex);
        }
    }

    protected boolean hasStrokedTexture() {
        if (this.family == 0) {
            return this.strokedTexture;
        } else {
            return this.image != null && this.stroke;
        }
    }

    public void solid(boolean solid) {
        if (this.family == 0) {
            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                child.solid(solid);
            }
        } else {
            this.solid = solid;
        }

    }

    protected void beginContourImpl() {
        this.breakShape = true;
    }

    protected void endContourImpl() {
    }

    public void vertex(float x, float y) {
        this.vertexImpl(x, y, 0.0F, 0.0F, 0.0F);
        if (this.image != null) {
            PGraphics.showWarning("No uv texture coordinates supplied with vertex() call");
        }

    }

    public void vertex(float x, float y, float u, float v) {
        this.vertexImpl(x, y, 0.0F, u, v);
    }

    public void vertex(float x, float y, float z) {
        this.vertexImpl(x, y, z, 0.0F, 0.0F);
        if (this.image != null) {
            PGraphics.showWarning("No uv texture coordinates supplied with vertex() call");
        }

    }

    public void vertex(float x, float y, float z, float u, float v) {
        this.vertexImpl(x, y, z, u, v);
    }

    protected void vertexImpl(float x, float y, float z, float u, float v) {
        if (!this.openShape) {
            PGraphics.showWarning("%1$s can only be called between beginShape() and endShape()", new Object[]{"vertex()"});
        } else if (this.family == 0) {
            PGraphics.showWarning("Cannot add vertices to GROUP shape");
        } else {
            boolean textured = this.image != null;
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

            if (this.textureMode == 2 && this.image != null) {
                u /= (float)this.image.width;
                v /= (float)this.image.height;
            }

            int scolor = 0;
            float sweight = 0.0F;
            if (this.stroke) {
                scolor = this.strokeColor;
                sweight = this.strokeWeight;
            }

            this.inGeo.addVertex(x, y, z, fcolor, this.normalX, this.normalY, this.normalZ, u, v, scolor, sweight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess, 0, this.vertexBreak());
            this.markForTessellation();
        }
    }

    protected boolean vertexBreak() {
        if (this.breakShape) {
            this.breakShape = false;
            return true;
        } else {
            return false;
        }
    }

    public void normal(float nx, float ny, float nz) {
        if (!this.openShape) {
            PGraphics.showWarning("%1$s can only be called between beginShape() and endShape()", new Object[]{"normal()"});
        } else if (this.family == 0) {
            PGraphics.showWarning("Cannot set normal in GROUP shape");
        } else {
            this.normalX = nx;
            this.normalY = ny;
            this.normalZ = nz;
            if (this.normalMode == 0) {
                this.normalMode = 1;
            } else if (this.normalMode == 1) {
                this.normalMode = 2;
            }

        }
    }

    public void attribPosition(String name, float x, float y, float z) {
        VertexAttribute attrib = this.attribImpl(name, 0, PGL.FLOAT, 3);
        if (attrib != null) {
            attrib.set(x, y, z);
        }

    }

    public void attribNormal(String name, float nx, float ny, float nz) {
        VertexAttribute attrib = this.attribImpl(name, 1, PGL.FLOAT, 3);
        if (attrib != null) {
            attrib.set(nx, ny, nz);
        }

    }

    public void attribColor(String name, int color) {
        VertexAttribute attrib = this.attribImpl(name, 2, PGL.INT, 1);
        if (attrib != null) {
            attrib.set(new int[]{color});
        }

    }

    public void attrib(String name, float... values) {
        VertexAttribute attrib = this.attribImpl(name, 3, PGL.FLOAT, values.length);
        if (attrib != null) {
            attrib.set(values);
        }

    }

    public void attrib(String name, int... values) {
        VertexAttribute attrib = this.attribImpl(name, 3, PGL.INT, values.length);
        if (attrib != null) {
            attrib.set(values);
        }

    }

    public void attrib(String name, boolean... values) {
        VertexAttribute attrib = this.attribImpl(name, 3, PGL.BOOL, values.length);
        if (attrib != null) {
            attrib.set(values);
        }

    }

    protected VertexAttribute attribImpl(String name, int kind, int type, int size) {
        if (4 < size) {
            PGraphics.showWarning("Vertex attributes cannot have more than 4 values");
            return null;
        } else {
            VertexAttribute attrib = (VertexAttribute)this.polyAttribs.get(name);
            if (attrib == null) {
                attrib = new VertexAttribute(this.pg, name, kind, type, size);
                this.polyAttribs.put(name, attrib);
                this.inGeo.initAttrib(attrib);
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

    public void endShape(int mode) {
        super.endShape(mode);
        this.inGeo.trim();
        this.close = mode == 2;
        this.markForTessellation();
        this.shapeCreated = true;
    }

    public void setParams(float[] source) {
        if (this.family != 1) {
            PGraphics.showWarning("Parameters can only be set to PRIMITIVE shapes");
        } else {
            super.setParams(source);
            this.markForTessellation();
            this.shapeCreated = true;
        }
    }

    public void setPath(int vcount, float[][] verts, int ccount, int[] codes) {
        if (this.family != 2) {
            PGraphics.showWarning("Vertex coordinates and codes can only be set to PATH shapes");
        } else {
            super.setPath(vcount, verts, ccount, codes);
            this.markForTessellation();
            this.shapeCreated = true;
        }
    }

    public void translate(float tx, float ty) {
        if (this.is3D) {
            this.transform(0, tx, ty, 0.0F);
        } else {
            this.transform(0, tx, ty);
        }

    }

    public void translate(float tx, float ty, float tz) {
        this.transform(0, tx, ty, tz);
    }

    public void rotate(float angle) {
        this.transform(1, angle);
    }

    public void rotateX(float angle) {
        this.rotate(angle, 1.0F, 0.0F, 0.0F);
    }

    public void rotateY(float angle) {
        this.rotate(angle, 0.0F, 1.0F, 0.0F);
    }

    public void rotateZ(float angle) {
        this.transform(1, angle);
    }

    public void rotate(float angle, float v0, float v1, float v2) {
        this.transform(1, angle, v0, v1, v2);
    }

    public void scale(float s) {
        if (this.is3D) {
            this.transform(2, s, s, s);
        } else {
            this.transform(2, s, s);
        }

    }

    public void scale(float x, float y) {
        if (this.is3D) {
            this.transform(2, x, y, 1.0F);
        } else {
            this.transform(2, x, y);
        }

    }

    public void scale(float x, float y, float z) {
        this.transform(2, x, y, z);
    }

    public void applyMatrix(PMatrix2D source) {
        this.transform(3, source.m00, source.m01, source.m02, source.m10, source.m11, source.m12);
    }

    public void applyMatrix(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.transform(3, n00, n01, n02, n10, n11, n12);
    }

    public void applyMatrix(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        this.transform(3, n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
    }

    public void resetMatrix() {
        if (this.shapeCreated && this.matrix != null && this.matrixInv != null) {
            if (this.family == 0) {
                this.updateTessellation();
            }

            if (this.tessellated) {
                this.applyMatrixImpl(this.matrixInv);
            }

            this.matrix.reset();
            this.matrixInv.reset();
        }

    }

    protected void transform(int type, float... args) {
        int dimensions = this.is3D ? 3 : 2;
        boolean invertible = true;
        this.checkMatrix(dimensions);
        if (this.transform == null) {
            if (dimensions == 2) {
                this.transform = new PMatrix2D();
                this.transformInv = new PMatrix2D();
            } else {
                this.transform = new PMatrix3D();
                this.transformInv = new PMatrix3D();
            }
        } else {
            this.transform.reset();
            this.transformInv.reset();
        }

        int ncoords = args.length;
        if (type == 1) {
            ncoords = args.length == 1 ? 2 : 3;
        } else if (type == 3) {
            ncoords = args.length == 6 ? 2 : 3;
        }

        switch(type) {
            case 0:
                if (ncoords == 3) {
                    this.transform.translate(args[0], args[1], args[2]);
                    PGraphicsOpenGL.invTranslate((PMatrix3D)this.transformInv, args[0], args[1], args[2]);
                } else {
                    this.transform.translate(args[0], args[1]);
                    PGraphicsOpenGL.invTranslate((PMatrix2D)this.transformInv, args[0], args[1]);
                }
                break;
            case 1:
                if (ncoords == 3) {
                    this.transform.rotate(args[0], args[1], args[2], args[3]);
                    PGraphicsOpenGL.invRotate((PMatrix3D)this.transformInv, args[0], args[1], args[2], args[3]);
                } else {
                    this.transform.rotate(args[0]);
                    PGraphicsOpenGL.invRotate((PMatrix2D)this.transformInv, -args[0]);
                }
                break;
            case 2:
                if (ncoords == 3) {
                    this.transform.scale(args[0], args[1], args[2]);
                    PGraphicsOpenGL.invScale((PMatrix3D)this.transformInv, args[0], args[1], args[2]);
                } else {
                    this.transform.scale(args[0], args[1]);
                    PGraphicsOpenGL.invScale((PMatrix2D)this.transformInv, args[0], args[1]);
                }
                break;
            case 3:
                if (ncoords == 3) {
                    this.transform.set(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8], args[9], args[10], args[11], args[12], args[13], args[14], args[15]);
                } else {
                    this.transform.set(args[0], args[1], args[2], args[3], args[4], args[5]);
                }

                this.transformInv.set(this.transform);
                invertible = this.transformInv.invert();
        }

        this.matrix.preApply(this.transform);
        if (invertible) {
            this.matrixInv.apply(this.transformInv);
        } else {
            PGraphics.showWarning("Transformation applied on the shape cannot be inverted");
        }

        if (this.tessellated) {
            this.applyMatrixImpl(this.transform);
        }

    }

    protected void applyMatrixImpl(PMatrix matrix) {
        if (this.hasPolys) {
            this.tessGeo.applyMatrixOnPolyGeometry(matrix, this.firstPolyVertex, this.lastPolyVertex);
            this.root.setModifiedPolyVertices(this.firstPolyVertex, this.lastPolyVertex);
            this.root.setModifiedPolyNormals(this.firstPolyVertex, this.lastPolyVertex);
            Iterator var2 = this.polyAttribs.values().iterator();

            label31:
            while(true) {
                VertexAttribute attrib;
                do {
                    if (!var2.hasNext()) {
                        break label31;
                    }

                    attrib = (VertexAttribute)var2.next();
                } while(!attrib.isPosition() && !attrib.isNormal());

                this.root.setModifiedPolyAttrib(attrib, this.firstPolyVertex, this.lastPolyVertex);
            }
        }

        if (this.is3D()) {
            if (this.hasLines) {
                this.tessGeo.applyMatrixOnLineGeometry(matrix, this.firstLineVertex, this.lastLineVertex);
                this.root.setModifiedLineVertices(this.firstLineVertex, this.lastLineVertex);
                this.root.setModifiedLineAttributes(this.firstLineVertex, this.lastLineVertex);
            }

            if (this.hasPoints) {
                this.tessGeo.applyMatrixOnPointGeometry(matrix, this.firstPointVertex, this.lastPointVertex);
                this.root.setModifiedPointVertices(this.firstPointVertex, this.lastPointVertex);
                this.root.setModifiedPointAttributes(this.firstPointVertex, this.lastPointVertex);
            }
        }

    }

    protected void checkMatrix(int dimensions) {
        if (this.matrix == null) {
            if (dimensions == 2) {
                this.matrix = new PMatrix2D();
                this.matrixInv = new PMatrix2D();
            } else {
                this.matrix = new PMatrix3D();
                this.matrixInv = new PMatrix3D();
            }
        } else if (dimensions == 3 && this.matrix instanceof PMatrix2D) {
            this.matrix = new PMatrix3D(this.matrix);
            this.matrixInv = new PMatrix3D(this.matrixInv);
        }

    }

    public void bezierDetail(int detail) {
        this.bezierDetail = detail;
        if (0 < this.inGeo.codeCount) {
            this.markForTessellation();
        }

    }

    public void bezierVertex(float x2, float y2, float x3, float y3, float x4, float y4) {
        this.bezierVertexImpl(x2, y2, 0.0F, x3, y3, 0.0F, x4, y4, 0.0F);
    }

    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        this.bezierVertexImpl(x2, y2, z2, x3, y3, z3, x4, y4, z4);
    }

    protected void bezierVertexImpl(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
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
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addQuadraticVertex(cx, cy, cz, x3, y3, z3, this.vertexBreak());
    }

    public void curveDetail(int detail) {
        this.curveDetail = detail;
        if (0 < this.inGeo.codeCount) {
            this.markForTessellation();
        }

    }

    public void curveTightness(float tightness) {
        this.curveTightness = tightness;
        if (0 < this.inGeo.codeCount) {
            this.markForTessellation();
        }

    }

    public void curveVertex(float x, float y) {
        this.curveVertexImpl(x, y, 0.0F);
    }

    public void curveVertex(float x, float y, float z) {
        this.curveVertexImpl(x, y, z);
    }

    protected void curveVertexImpl(float x, float y, float z) {
        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addCurveVertex(x, y, z, this.vertexBreak());
    }

    public int getVertexCount() {
        if (this.family == 0) {
            return 0;
        } else {
            if (this.family == 1 || this.family == 2) {
                this.updateTessellation();
            }

            return this.inGeo.vertexCount;
        }
    }

    public PVector getVertex(int index, PVector vec) {
        if (vec == null) {
            vec = new PVector();
        }

        vec.x = this.inGeo.vertices[3 * index + 0];
        vec.y = this.inGeo.vertices[3 * index + 1];
        vec.z = this.inGeo.vertices[3 * index + 2];
        return vec;
    }

    public float getVertexX(int index) {
        return this.inGeo.vertices[3 * index + 0];
    }

    public float getVertexY(int index) {
        return this.inGeo.vertices[3 * index + 1];
    }

    public float getVertexZ(int index) {
        return this.inGeo.vertices[3 * index + 2];
    }

    public void setVertex(int index, float x, float y) {
        this.setVertex(index, x, y, 0.0F);
    }

    public void setVertex(int index, float x, float y, float z) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setVertex()"});
        } else {
            if (this.family == 2) {
                if (this.vertexCodes != null && this.vertexCodeCount > 0 && this.vertexCodes[index] != 0) {
                    PGraphics.showWarning("%1$s can not be called on quadratic or bezier vertices", new Object[]{"setVertex()"});
                    return;
                }

                this.vertices[index][0] = x;
                this.vertices[index][1] = y;
                if (this.is3D && this.vertices[index].length > 2) {
                    this.vertices[index][2] = z;
                }
            } else {
                this.inGeo.vertices[3 * index + 0] = x;
                this.inGeo.vertices[3 * index + 1] = y;
                this.inGeo.vertices[3 * index + 2] = z;
            }

            this.markForTessellation();
        }
    }

    public void setVertex(int index, PVector vec) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setVertex()"});
        } else {
            if (this.family == 2) {
                if (this.vertexCodes != null && this.vertexCodeCount > 0 && this.vertexCodes[index] != 0) {
                    PGraphics.showWarning("%1$s can not be called on quadratic or bezier vertices", new Object[]{"setVertex()"});
                    return;
                }

                this.vertices[index][0] = vec.x;
                this.vertices[index][1] = vec.y;
                if (this.is3D && this.vertices[index].length > 2) {
                    this.vertices[index][2] = vec.z;
                }
            } else {
                this.inGeo.vertices[3 * index + 0] = vec.x;
                this.inGeo.vertices[3 * index + 1] = vec.y;
                this.inGeo.vertices[3 * index + 2] = vec.z;
            }

            this.markForTessellation();
        }
    }

    public PVector getNormal(int index, PVector vec) {
        if (vec == null) {
            vec = new PVector();
        }

        vec.x = this.inGeo.normals[3 * index + 0];
        vec.y = this.inGeo.normals[3 * index + 1];
        vec.z = this.inGeo.normals[3 * index + 2];
        return vec;
    }

    public float getNormalX(int index) {
        return this.inGeo.normals[3 * index + 0];
    }

    public float getNormalY(int index) {
        return this.inGeo.normals[3 * index + 1];
    }

    public float getNormalZ(int index) {
        return this.inGeo.normals[3 * index + 2];
    }

    public void setNormal(int index, float nx, float ny, float nz) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setNormal()"});
        } else {
            this.inGeo.normals[3 * index + 0] = nx;
            this.inGeo.normals[3 * index + 1] = ny;
            this.inGeo.normals[3 * index + 2] = nz;
            this.markForTessellation();
        }
    }

    public void setAttrib(String name, int index, float... values) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setNormal()"});
        } else {
            VertexAttribute attrib = this.attribImpl(name, 3, PGL.FLOAT, values.length);
            float[] array = (float[])this.inGeo.fattribs.get(name);

            for(int i = 0; i < values.length; ++i) {
                array[attrib.size * index + i] = values[i];
            }

            this.markForTessellation();
        }
    }

    public void setAttrib(String name, int index, int... values) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setNormal()"});
        } else {
            VertexAttribute attrib = this.attribImpl(name, 3, PGL.INT, values.length);
            int[] array = (int[])this.inGeo.iattribs.get(name);

            for(int i = 0; i < values.length; ++i) {
                array[attrib.size * index + i] = values[i];
            }

            this.markForTessellation();
        }
    }

    public void setAttrib(String name, int index, boolean... values) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setNormal()"});
        } else {
            VertexAttribute attrib = this.attribImpl(name, 3, PGL.BOOL, values.length);
            byte[] array = (byte[])this.inGeo.battribs.get(name);

            for(int i = 0; i < values.length; ++i) {
                array[attrib.size * index + i] = (byte)(values[i] ? 1 : 0);
            }

            this.markForTessellation();
        }
    }

    public float getTextureU(int index) {
        return this.inGeo.texcoords[2 * index + 0];
    }

    public float getTextureV(int index) {
        return this.inGeo.texcoords[2 * index + 1];
    }

    public void setTextureUV(int index, float u, float v) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setTextureUV()"});
        } else {
            if (this.textureMode == 2 && this.image != null) {
                u /= (float)this.image.width;
                v /= (float)this.image.height;
            }

            this.inGeo.texcoords[2 * index + 0] = u;
            this.inGeo.texcoords[2 * index + 1] = v;
            this.markForTessellation();
        }
    }

    public int getFill(int index) {
        return this.family != 0 && this.image == null ? PGL.nativeToJavaARGB(this.inGeo.colors[index]) : 0;
    }

    public void setFill(boolean fill) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setFill()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setFill(fill);
                }
            } else if (this.fill != fill) {
                this.markForTessellation();
            }

            this.fill = fill;
        }
    }

    public void setFill(int fill) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setFill()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setFill(fill);
                }
            } else {
                this.setFillImpl(fill);
            }

        }
    }

    protected void setFillImpl(int fill) {
        if (this.fillColor != fill) {
            this.fillColor = fill;
            if (this.image == null) {
                Arrays.fill(this.inGeo.colors, 0, this.inGeo.vertexCount, PGL.javaToNativeARGB(this.fillColor));
                if (this.shapeCreated && this.tessellated && this.hasPolys) {
                    if (this.is3D()) {
                        Arrays.fill(this.tessGeo.polyColors, this.firstPolyVertex, this.lastPolyVertex + 1, PGL.javaToNativeARGB(this.fillColor));
                        this.root.setModifiedPolyColors(this.firstPolyVertex, this.lastPolyVertex);
                    } else if (this.is2D()) {
                        int last1 = this.lastPolyVertex + 1;
                        if (-1 < this.firstLineVertex) {
                            last1 = this.firstLineVertex;
                        }

                        if (-1 < this.firstPointVertex) {
                            last1 = this.firstPointVertex;
                        }

                        Arrays.fill(this.tessGeo.polyColors, this.firstPolyVertex, last1, PGL.javaToNativeARGB(this.fillColor));
                        this.root.setModifiedPolyColors(this.firstPolyVertex, last1 - 1);
                    }
                }
            }

            if (!this.setAmbient) {
                this.setAmbientImpl(fill);
                this.setAmbient = false;
            }

        }
    }

    public void setFill(int index, int fill) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setFill()"});
        } else {
            if (this.image == null) {
                this.inGeo.colors[index] = PGL.javaToNativeARGB(fill);
                this.markForTessellation();
            }

        }
    }

    public int getTint(int index) {
        return this.family != 0 && this.image != null ? PGL.nativeToJavaARGB(this.inGeo.colors[index]) : 0;
    }

    public void setTint(boolean tint) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setTint()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setTint(this.fill);
                }
            } else if (this.tint && !tint) {
                this.setTintImpl(-1);
            }

            this.tint = tint;
        }
    }

    public void setTint(int tint) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setTint()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setTint(tint);
                }
            } else {
                this.setTintImpl(tint);
            }

        }
    }

    protected void setTintImpl(int tint) {
        if (this.tintColor != tint) {
            this.tintColor = tint;
            if (this.image != null) {
                Arrays.fill(this.inGeo.colors, 0, this.inGeo.vertexCount, PGL.javaToNativeARGB(this.tintColor));
                if (this.shapeCreated && this.tessellated && this.hasPolys) {
                    if (this.is3D()) {
                        Arrays.fill(this.tessGeo.polyColors, this.firstPolyVertex, this.lastPolyVertex + 1, PGL.javaToNativeARGB(this.tintColor));
                        this.root.setModifiedPolyColors(this.firstPolyVertex, this.lastPolyVertex);
                    } else if (this.is2D()) {
                        int last1 = this.lastPolyVertex + 1;
                        if (-1 < this.firstLineVertex) {
                            last1 = this.firstLineVertex;
                        }

                        if (-1 < this.firstPointVertex) {
                            last1 = this.firstPointVertex;
                        }

                        Arrays.fill(this.tessGeo.polyColors, this.firstPolyVertex, last1, PGL.javaToNativeARGB(this.tintColor));
                        this.root.setModifiedPolyColors(this.firstPolyVertex, last1 - 1);
                    }
                }
            }

        }
    }

    public void setTint(int index, int tint) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setTint()"});
        } else {
            if (this.image != null) {
                this.inGeo.colors[index] = PGL.javaToNativeARGB(tint);
                this.markForTessellation();
            }

        }
    }

    public int getStroke(int index) {
        return this.family != 0 ? PGL.nativeToJavaARGB(this.inGeo.strokeColors[index]) : 0;
    }

    public void setStroke(boolean stroke) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setStroke()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setStroke(stroke);
                }

                this.stroke = stroke;
            } else {
                this.setStrokeImpl(stroke);
            }

        }
    }

    protected void setStrokeImpl(boolean stroke) {
        if (this.stroke != stroke) {
            if (stroke) {
                int color = this.strokeColor++;
                this.setStrokeImpl(color);
            }

            this.markForTessellation();
            if (this.is2D() && this.parent != null) {
                ((PShapeOpenGL)this.parent).strokedTexture(stroke && this.image != null);
            }

            this.stroke = stroke;
        }

    }

    public void setStroke(int stroke) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setStroke()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setStroke(stroke);
                }
            } else {
                this.setStrokeImpl(stroke);
            }

        }
    }

    protected void setStrokeImpl(int stroke) {
        if (this.strokeColor != stroke) {
            this.strokeColor = stroke;
            Arrays.fill(this.inGeo.strokeColors, 0, this.inGeo.vertexCount, PGL.javaToNativeARGB(this.strokeColor));
            if (this.shapeCreated && this.tessellated && (this.hasLines || this.hasPoints)) {
                if (this.hasLines) {
                    if (this.is3D()) {
                        Arrays.fill(this.tessGeo.lineColors, this.firstLineVertex, this.lastLineVertex + 1, PGL.javaToNativeARGB(this.strokeColor));
                        this.root.setModifiedLineColors(this.firstLineVertex, this.lastLineVertex);
                    } else if (this.is2D()) {
                        Arrays.fill(this.tessGeo.polyColors, this.firstLineVertex, this.lastLineVertex + 1, PGL.javaToNativeARGB(this.strokeColor));
                        this.root.setModifiedPolyColors(this.firstLineVertex, this.lastLineVertex);
                    }
                }

                if (this.hasPoints) {
                    if (this.is3D()) {
                        Arrays.fill(this.tessGeo.pointColors, this.firstPointVertex, this.lastPointVertex + 1, PGL.javaToNativeARGB(this.strokeColor));
                        this.root.setModifiedPointColors(this.firstPointVertex, this.lastPointVertex);
                    } else if (this.is2D()) {
                        Arrays.fill(this.tessGeo.polyColors, this.firstPointVertex, this.lastPointVertex + 1, PGL.javaToNativeARGB(this.strokeColor));
                        this.root.setModifiedPolyColors(this.firstPointVertex, this.lastPointVertex);
                    }
                }
            }

        }
    }

    public void setStroke(int index, int stroke) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setStroke()"});
        } else {
            this.inGeo.strokeColors[index] = PGL.javaToNativeARGB(stroke);
            this.markForTessellation();
        }
    }

    public float getStrokeWeight(int index) {
        return this.family != 0 ? this.inGeo.strokeWeights[index] : 0.0F;
    }

    public void setStrokeWeight(float weight) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setStrokeWeight()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setStrokeWeight(weight);
                }
            } else {
                this.setStrokeWeightImpl(weight);
            }

        }
    }

    protected void setStrokeWeightImpl(float weight) {
        if (!PGraphicsOpenGL.same(this.strokeWeight, weight)) {
            float oldWeight = this.strokeWeight;
            this.strokeWeight = weight;
            Arrays.fill(this.inGeo.strokeWeights, 0, this.inGeo.vertexCount, this.strokeWeight);
            if (this.shapeCreated && this.tessellated && (this.hasLines || this.hasPoints)) {
                float resizeFactor = weight / oldWeight;
                float[] var10000;
                int i;
                if (this.hasLines) {
                    if (!this.is3D()) {
                        if (this.is2D()) {
                            this.markForTessellation();
                        }
                    } else {
                        for(i = this.firstLineVertex; i <= this.lastLineVertex; ++i) {
                            var10000 = this.tessGeo.lineDirections;
                            var10000[4 * i + 3] *= resizeFactor;
                        }

                        this.root.setModifiedLineAttributes(this.firstLineVertex, this.lastLineVertex);
                    }
                }

                if (this.hasPoints) {
                    if (this.is3D()) {
                        for(i = this.firstPointVertex; i <= this.lastPointVertex; ++i) {
                            var10000 = this.tessGeo.pointOffsets;
                            var10000[2 * i + 0] *= resizeFactor;
                            var10000 = this.tessGeo.pointOffsets;
                            var10000[2 * i + 1] *= resizeFactor;
                        }

                        this.root.setModifiedPointAttributes(this.firstPointVertex, this.lastPointVertex);
                    } else if (this.is2D()) {
                        this.markForTessellation();
                    }
                }
            }

        }
    }

    public void setStrokeWeight(int index, float weight) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setStrokeWeight()"});
        } else {
            this.inGeo.strokeWeights[index] = weight;
            this.markForTessellation();
        }
    }

    public void setStrokeJoin(int join) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setStrokeJoin()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setStrokeJoin(join);
                }
            } else {
                if (this.is2D() && this.strokeJoin != join) {
                    this.markForTessellation();
                }

                this.strokeJoin = join;
            }

        }
    }

    public void setStrokeCap(int cap) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setStrokeCap()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setStrokeCap(cap);
                }
            } else {
                if (this.is2D() && this.strokeCap != cap) {
                    this.markForTessellation();
                }

                this.strokeCap = cap;
            }

        }
    }

    public int getAmbient(int index) {
        return this.family != 0 ? PGL.nativeToJavaARGB(this.inGeo.ambient[index]) : 0;
    }

    public void setAmbient(int ambient) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setAmbient()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setAmbient(ambient);
                }
            } else {
                this.setAmbientImpl(ambient);
            }

        }
    }

    protected void setAmbientImpl(int ambient) {
        if (this.ambientColor != ambient) {
            this.ambientColor = ambient;
            Arrays.fill(this.inGeo.ambient, 0, this.inGeo.vertexCount, PGL.javaToNativeARGB(this.ambientColor));
            if (this.shapeCreated && this.tessellated && this.hasPolys) {
                if (this.is3D()) {
                    Arrays.fill(this.tessGeo.polyAmbient, this.firstPolyVertex, this.lastPolyVertex + 1, PGL.javaToNativeARGB(this.ambientColor));
                    this.root.setModifiedPolyAmbient(this.firstPolyVertex, this.lastPolyVertex);
                } else if (this.is2D()) {
                    int last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }

                    Arrays.fill(this.tessGeo.polyAmbient, this.firstPolyVertex, last1, PGL.javaToNativeARGB(this.ambientColor));
                    this.root.setModifiedPolyColors(this.firstPolyVertex, last1 - 1);
                }
            }

            this.setAmbient = true;
        }
    }

    public void setAmbient(int index, int ambient) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setAmbient()"});
        } else {
            this.inGeo.ambient[index] = PGL.javaToNativeARGB(ambient);
            this.markForTessellation();
            this.setAmbient = true;
        }
    }

    public int getSpecular(int index) {
        return this.family == 0 ? PGL.nativeToJavaARGB(this.inGeo.specular[index]) : 0;
    }

    public void setSpecular(int specular) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setSpecular()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setSpecular(specular);
                }
            } else {
                this.setSpecularImpl(specular);
            }

        }
    }

    protected void setSpecularImpl(int specular) {
        if (this.specularColor != specular) {
            this.specularColor = specular;
            Arrays.fill(this.inGeo.specular, 0, this.inGeo.vertexCount, PGL.javaToNativeARGB(this.specularColor));
            if (this.shapeCreated && this.tessellated && this.hasPolys) {
                if (this.is3D()) {
                    Arrays.fill(this.tessGeo.polySpecular, this.firstPolyVertex, this.lastPolyVertex + 1, PGL.javaToNativeARGB(this.specularColor));
                    this.root.setModifiedPolySpecular(this.firstPolyVertex, this.lastPolyVertex);
                } else if (this.is2D()) {
                    int last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }

                    Arrays.fill(this.tessGeo.polySpecular, this.firstPolyVertex, last1, PGL.javaToNativeARGB(this.specularColor));
                    this.root.setModifiedPolyColors(this.firstPolyVertex, last1 - 1);
                }
            }

        }
    }

    public void setSpecular(int index, int specular) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setSpecular()"});
        } else {
            this.inGeo.specular[index] = PGL.javaToNativeARGB(specular);
            this.markForTessellation();
        }
    }

    public int getEmissive(int index) {
        return this.family == 0 ? PGL.nativeToJavaARGB(this.inGeo.emissive[index]) : 0;
    }

    public void setEmissive(int emissive) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setEmissive()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setEmissive(emissive);
                }
            } else {
                this.setEmissiveImpl(emissive);
            }

        }
    }

    protected void setEmissiveImpl(int emissive) {
        if (this.emissiveColor != emissive) {
            this.emissiveColor = emissive;
            Arrays.fill(this.inGeo.emissive, 0, this.inGeo.vertexCount, PGL.javaToNativeARGB(this.emissiveColor));
            if (this.shapeCreated && this.tessellated && 0 < this.tessGeo.polyVertexCount) {
                if (this.is3D()) {
                    Arrays.fill(this.tessGeo.polyEmissive, this.firstPolyVertex, this.lastPolyVertex + 1, PGL.javaToNativeARGB(this.emissiveColor));
                    this.root.setModifiedPolyEmissive(this.firstPolyVertex, this.lastPolyVertex);
                } else if (this.is2D()) {
                    int last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }

                    Arrays.fill(this.tessGeo.polyEmissive, this.firstPolyVertex, last1, PGL.javaToNativeARGB(this.emissiveColor));
                    this.root.setModifiedPolyColors(this.firstPolyVertex, last1 - 1);
                }
            }

        }
    }

    public void setEmissive(int index, int emissive) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setEmissive()"});
        } else {
            this.inGeo.emissive[index] = PGL.javaToNativeARGB(emissive);
            this.markForTessellation();
        }
    }

    public float getShininess(int index) {
        return this.family == 0 ? this.inGeo.shininess[index] : 0.0F;
    }

    public void setShininess(float shininess) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setShininess()"});
        } else {
            if (this.family == 0) {
                for(int i = 0; i < this.childCount; ++i) {
                    PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                    child.setShininess(shininess);
                }
            } else {
                this.setShininessImpl(shininess);
            }

        }
    }

    protected void setShininessImpl(float shininess) {
        if (!PGraphicsOpenGL.same(this.shininess, shininess)) {
            this.shininess = shininess;
            Arrays.fill(this.inGeo.shininess, 0, this.inGeo.vertexCount, shininess);
            if (this.shapeCreated && this.tessellated && this.hasPolys) {
                if (this.is3D()) {
                    Arrays.fill(this.tessGeo.polyShininess, this.firstPolyVertex, this.lastPolyVertex + 1, shininess);
                    this.root.setModifiedPolyShininess(this.firstPolyVertex, this.lastPolyVertex);
                } else if (this.is2D()) {
                    int last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }

                    Arrays.fill(this.tessGeo.polyShininess, this.firstPolyVertex, last1, shininess);
                    this.root.setModifiedPolyColors(this.firstPolyVertex, last1 - 1);
                }
            }

        }
    }

    public void setShininess(int index, float shine) {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"setShininess()"});
        } else {
            this.inGeo.shininess[index] = shine;
            this.markForTessellation();
        }
    }

    public int[] getVertexCodes() {
        if (this.family == 0) {
            return null;
        } else {
            if (this.family == 1 || this.family == 2) {
                this.updateTessellation();
            }

            return this.inGeo.codes == null ? null : this.inGeo.codes;
        }
    }

    public int getVertexCodeCount() {
        if (this.family == 0) {
            return 0;
        } else {
            if (this.family == 1 || this.family == 2) {
                this.updateTessellation();
            }

            return this.inGeo.codeCount;
        }
    }

    public int getVertexCode(int index) {
        return this.inGeo.codes[index];
    }

    public PShape getTessellation() {
        this.updateTessellation();
        float[] vertices = this.tessGeo.polyVertices;
        float[] normals = this.tessGeo.polyNormals;
        int[] color = this.tessGeo.polyColors;
        float[] uv = this.tessGeo.polyTexCoords;
        short[] indices = this.tessGeo.polyIndices;
        PShape tess = this.pg.createShapeFamily(3);
        tess.set3D(this.is3D);
        tess.beginShape(9);
        tess.noStroke();
        IndexCache cache = this.tessGeo.polyIndexCache;

        for(int n = this.firstPolyIndexCache; n <= this.lastPolyIndexCache; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];

            for(int tr = ioffset / 3; tr < (ioffset + icount) / 3; ++tr) {
                int i0 = voffset + indices[3 * tr + 0];
                int i1 = voffset + indices[3 * tr + 1];
                int i2 = voffset + indices[3 * tr + 2];
                float x0;
                float y0;
                float x1;
                float y1;
                float x2;
                float y2;
                if (this.is3D()) {
                    x0 = vertices[4 * i0 + 0];
                    y0 = vertices[4 * i0 + 1];
                    x1 = vertices[4 * i0 + 2];
                    y1 = vertices[4 * i1 + 0];
                    x2 = vertices[4 * i1 + 1];
                    y2 = vertices[4 * i1 + 2];
                    float x2 = vertices[4 * i2 + 0];
                    float y2 = vertices[4 * i2 + 1];
                    float z2 = vertices[4 * i2 + 2];
                    float nx0 = normals[3 * i0 + 0];
                    float ny0 = normals[3 * i0 + 1];
                    float nz0 = normals[3 * i0 + 2];
                    float nx1 = normals[3 * i1 + 0];
                    float ny1 = normals[3 * i1 + 1];
                    float nz1 = normals[3 * i1 + 2];
                    float nx2 = normals[3 * i2 + 0];
                    float ny2 = normals[3 * i2 + 1];
                    float nz2 = normals[3 * i2 + 2];
                    int argb0 = PGL.nativeToJavaARGB(color[i0]);
                    int argb1 = PGL.nativeToJavaARGB(color[i1]);
                    int argb2 = PGL.nativeToJavaARGB(color[i2]);
                    tess.fill(argb0);
                    tess.normal(nx0, ny0, nz0);
                    tess.vertex(x0, y0, x1, uv[2 * i0 + 0], uv[2 * i0 + 1]);
                    tess.fill(argb1);
                    tess.normal(nx1, ny1, nz1);
                    tess.vertex(y1, x2, y2, uv[2 * i1 + 0], uv[2 * i1 + 1]);
                    tess.fill(argb2);
                    tess.normal(nx2, ny2, nz2);
                    tess.vertex(x2, y2, z2, uv[2 * i2 + 0], uv[2 * i2 + 1]);
                } else if (this.is2D()) {
                    x0 = vertices[4 * i0 + 0];
                    y0 = vertices[4 * i0 + 1];
                    x1 = vertices[4 * i1 + 0];
                    y1 = vertices[4 * i1 + 1];
                    x2 = vertices[4 * i2 + 0];
                    y2 = vertices[4 * i2 + 1];
                    int argb0 = PGL.nativeToJavaARGB(color[i0]);
                    int argb1 = PGL.nativeToJavaARGB(color[i1]);
                    int argb2 = PGL.nativeToJavaARGB(color[i2]);
                    tess.fill(argb0);
                    tess.vertex(x0, y0, uv[2 * i0 + 0], uv[2 * i0 + 1]);
                    tess.fill(argb1);
                    tess.vertex(x1, y1, uv[2 * i1 + 0], uv[2 * i1 + 1]);
                    tess.fill(argb2);
                    tess.vertex(x2, y2, uv[2 * i2 + 0], uv[2 * i2 + 1]);
                }
            }
        }

        tess.endShape();
        return tess;
    }

    public float[] getTessellation(int kind, int data) {
        this.updateTessellation();
        if (kind == 9) {
            int last1;
            if (data == 0) {
                if (this.is3D()) {
                    this.root.setModifiedPolyVertices(this.firstPolyVertex, this.lastPolyVertex);
                } else if (this.is2D()) {
                    last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }

                    this.root.setModifiedPolyVertices(this.firstPolyVertex, last1 - 1);
                }

                return this.tessGeo.polyVertices;
            }

            if (data == 1) {
                if (this.is3D()) {
                    this.root.setModifiedPolyNormals(this.firstPolyVertex, this.lastPolyVertex);
                } else if (this.is2D()) {
                    last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }

                    this.root.setModifiedPolyNormals(this.firstPolyVertex, last1 - 1);
                }

                return this.tessGeo.polyNormals;
            }

            if (data == 2) {
                if (this.is3D()) {
                    this.root.setModifiedPolyTexCoords(this.firstPolyVertex, this.lastPolyVertex);
                } else if (this.is2D()) {
                    last1 = this.lastPolyVertex + 1;
                    if (-1 < this.firstLineVertex) {
                        last1 = this.firstLineVertex;
                    }

                    if (-1 < this.firstPointVertex) {
                        last1 = this.firstPointVertex;
                    }

                    this.root.setModifiedPolyTexCoords(this.firstPolyVertex, last1 - 1);
                }

                return this.tessGeo.polyTexCoords;
            }
        } else if (kind == 5) {
            if (data == 0) {
                if (this.is3D()) {
                    this.root.setModifiedLineVertices(this.firstLineVertex, this.lastLineVertex);
                } else if (this.is2D()) {
                    this.root.setModifiedPolyVertices(this.firstLineVertex, this.lastLineVertex);
                }

                return this.tessGeo.lineVertices;
            }

            if (data == 3) {
                if (this.is2D()) {
                    this.root.setModifiedLineAttributes(this.firstLineVertex, this.lastLineVertex);
                }

                return this.tessGeo.lineDirections;
            }
        } else if (kind == 3) {
            if (data == 0) {
                if (this.is3D()) {
                    this.root.setModifiedPointVertices(this.firstPointVertex, this.lastPointVertex);
                } else if (this.is2D()) {
                    this.root.setModifiedPolyVertices(this.firstPointVertex, this.lastPointVertex);
                }

                return this.tessGeo.pointVertices;
            }

            if (data == 4) {
                if (this.is2D()) {
                    this.root.setModifiedPointAttributes(this.firstPointVertex, this.lastPointVertex);
                }

                return this.tessGeo.pointOffsets;
            }
        }

        return null;
    }

    public boolean contains(float x, float y) {
        if (this.family == 2) {
            boolean c = false;
            int i = 0;

            for(int j = this.inGeo.vertexCount - 1; i < this.inGeo.vertexCount; j = i++) {
                if (this.inGeo.vertices[3 * i + 1] > y != this.inGeo.vertices[3 * j + 1] > y && x < (this.inGeo.vertices[3 * j] - this.inGeo.vertices[3 * i]) * (y - this.inGeo.vertices[3 * i + 1]) / (this.inGeo.vertices[3 * j + 1] - this.inGeo.vertices[3 * i + 1]) + this.inGeo.vertices[3 * i]) {
                    c = !c;
                }
            }

            return c;
        } else {
            throw new IllegalArgumentException("The contains() method is only implemented for paths.");
        }
    }

    protected void updateTessellation() {
        if (!this.root.tessellated) {
            this.root.tessellate();
            this.root.aggregate();
            this.root.initModified();
            this.root.needBufferInit = true;
        }

    }

    protected void markForTessellation() {
        this.root.tessellated = false;
        this.tessellated = false;
    }

    protected void initModified() {
        this.modified = false;
        this.modifiedPolyVertices = false;
        this.modifiedPolyColors = false;
        this.modifiedPolyNormals = false;
        this.modifiedPolyTexCoords = false;
        this.modifiedPolyAmbient = false;
        this.modifiedPolySpecular = false;
        this.modifiedPolyEmissive = false;
        this.modifiedPolyShininess = false;
        this.modifiedLineVertices = false;
        this.modifiedLineColors = false;
        this.modifiedLineAttributes = false;
        this.modifiedPointVertices = false;
        this.modifiedPointColors = false;
        this.modifiedPointAttributes = false;
        this.firstModifiedPolyVertex = 2147483647;
        this.lastModifiedPolyVertex = -2147483648;
        this.firstModifiedPolyColor = 2147483647;
        this.lastModifiedPolyColor = -2147483648;
        this.firstModifiedPolyNormal = 2147483647;
        this.lastModifiedPolyNormal = -2147483648;
        this.firstModifiedPolyTexcoord = 2147483647;
        this.lastModifiedPolyTexcoord = -2147483648;
        this.firstModifiedPolyAmbient = 2147483647;
        this.lastModifiedPolyAmbient = -2147483648;
        this.firstModifiedPolySpecular = 2147483647;
        this.lastModifiedPolySpecular = -2147483648;
        this.firstModifiedPolyEmissive = 2147483647;
        this.lastModifiedPolyEmissive = -2147483648;
        this.firstModifiedPolyShininess = 2147483647;
        this.lastModifiedPolyShininess = -2147483648;
        this.firstModifiedLineVertex = 2147483647;
        this.lastModifiedLineVertex = -2147483648;
        this.firstModifiedLineColor = 2147483647;
        this.lastModifiedLineColor = -2147483648;
        this.firstModifiedLineAttribute = 2147483647;
        this.lastModifiedLineAttribute = -2147483648;
        this.firstModifiedPointVertex = 2147483647;
        this.lastModifiedPointVertex = -2147483648;
        this.firstModifiedPointColor = 2147483647;
        this.lastModifiedPointColor = -2147483648;
        this.firstModifiedPointAttribute = 2147483647;
        this.lastModifiedPointAttribute = -2147483648;
    }

    protected void tessellate() {
        if (this.root == this && this.parent == null) {
            boolean initAttr = false;
            if (this.polyAttribs == null) {
                this.polyAttribs = PGraphicsOpenGL.newAttributeMap();
                initAttr = true;
            }

            if (this.tessGeo == null) {
                this.tessGeo = PGraphicsOpenGL.newTessGeometry(this.pg, this.polyAttribs, 1);
            }

            this.tessGeo.clear();
            if (initAttr) {
                this.collectPolyAttribs();
            }

            for(int i = 0; i < this.polyAttribs.size(); ++i) {
                VertexAttribute attrib = this.polyAttribs.get(i);
                this.tessGeo.initAttrib(attrib);
            }

            this.tessellateImpl();
            this.tessGeo.trim();
        }

    }

    protected void collectPolyAttribs() {
        AttributeMap rootAttribs = this.root.polyAttribs;
        this.tessGeo = this.root.tessGeo;
        int i;
        if (this.family == 0) {
            for(i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                child.collectPolyAttribs();
            }
        } else {
            for(i = 0; i < this.polyAttribs.size(); ++i) {
                VertexAttribute attrib = this.polyAttribs.get(i);
                this.tessGeo.initAttrib(attrib);
                if (rootAttribs.containsKey(attrib.name)) {
                    VertexAttribute rattrib = (VertexAttribute)rootAttribs.get(attrib.name);
                    if (rattrib.diff(attrib)) {
                        throw new RuntimeException("Children shapes cannot have different attributes with same name");
                    }
                } else {
                    rootAttribs.put(attrib.name, attrib);
                }
            }
        }

    }

    protected void tessellateImpl() {
        this.tessGeo = this.root.tessGeo;
        this.firstPolyIndexCache = -1;
        this.lastPolyIndexCache = -1;
        this.firstLineIndexCache = -1;
        this.lastLineIndexCache = -1;
        this.firstPointIndexCache = -1;
        this.lastPointIndexCache = -1;
        if (this.family == 0) {
            if (this.polyAttribs == null) {
                this.polyAttribs = PGraphicsOpenGL.newAttributeMap();
                this.collectPolyAttribs();
            }

            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                child.tessellateImpl();
            }
        } else if (this.shapeCreated) {
            this.inGeo.clearEdges();
            this.tessellator.setInGeometry(this.inGeo);
            this.tessellator.setTessGeometry(this.tessGeo);
            this.tessellator.setFill(this.fill || this.image != null);
            this.tessellator.setTexCache((TexCache)null, (PImage)null);
            this.tessellator.setStroke(this.stroke);
            this.tessellator.setStrokeColor(this.strokeColor);
            this.tessellator.setStrokeWeight(this.strokeWeight);
            this.tessellator.setStrokeCap(this.strokeCap);
            this.tessellator.setStrokeJoin(this.strokeJoin);
            this.tessellator.setRenderer(this.pg);
            this.tessellator.setTransform(this.matrix);
            this.tessellator.set3D(this.is3D());
            if (this.family != 3) {
                if (this.family == 1) {
                    this.inGeo.clear();
                    if (this.kind == 2) {
                        this.tessellatePoint();
                    } else if (this.kind == 4) {
                        this.tessellateLine();
                    } else if (this.kind == 8) {
                        this.tessellateTriangle();
                    } else if (this.kind == 16) {
                        this.tessellateQuad();
                    } else if (this.kind == 30) {
                        this.tessellateRect();
                    } else if (this.kind == 31) {
                        this.tessellateEllipse();
                    } else if (this.kind == 32) {
                        this.tessellateArc();
                    } else if (this.kind == 41) {
                        this.tessellateBox();
                    } else if (this.kind == 40) {
                        this.tessellateSphere();
                    }
                } else if (this.family == 2) {
                    this.inGeo.clear();
                    this.tessellatePath();
                }
            } else if (this.kind == 3) {
                this.tessellator.tessellatePoints();
            } else if (this.kind == 5) {
                this.tessellator.tessellateLines();
            } else if (this.kind == 50) {
                this.tessellator.tessellateLineStrip();
            } else if (this.kind == 51) {
                this.tessellator.tessellateLineLoop();
            } else if (this.kind != 8 && this.kind != 9) {
                if (this.kind == 11) {
                    if (this.stroke) {
                        this.inGeo.addTriangleFanEdges();
                    }

                    if (this.normalMode == 0) {
                        this.inGeo.calcTriangleFanNormals();
                    }

                    this.tessellator.tessellateTriangleFan();
                } else if (this.kind == 10) {
                    if (this.stroke) {
                        this.inGeo.addTriangleStripEdges();
                    }

                    if (this.normalMode == 0) {
                        this.inGeo.calcTriangleStripNormals();
                    }

                    this.tessellator.tessellateTriangleStrip();
                } else if (this.kind != 16 && this.kind != 17) {
                    if (this.kind == 18) {
                        if (this.stroke) {
                            this.inGeo.addQuadStripEdges();
                        }

                        if (this.normalMode == 0) {
                            this.inGeo.calcQuadStripNormals();
                        }

                        this.tessellator.tessellateQuadStrip();
                    } else if (this.kind == 20) {
                        boolean bez = this.inGeo.hasBezierVertex();
                        boolean quad = this.inGeo.hasQuadraticVertex();
                        boolean curv = this.inGeo.hasCurveVertex();
                        if (bez || quad) {
                            this.saveBezierVertexSettings();
                        }

                        if (curv) {
                            this.saveCurveVertexSettings();
                            this.tessellator.resetCurveVertexCount();
                        }

                        this.tessellator.tessellatePolygon(this.solid, this.close, this.normalMode == 0);
                        if (bez || quad) {
                            this.restoreBezierVertexSettings();
                        }

                        if (curv) {
                            this.restoreCurveVertexSettings();
                        }
                    }
                } else {
                    if (this.stroke) {
                        this.inGeo.addQuadsEdges();
                    }

                    if (this.normalMode == 0) {
                        this.inGeo.calcQuadsNormals();
                    }

                    this.tessellator.tessellateQuads();
                }
            } else {
                if (this.stroke) {
                    this.inGeo.addTrianglesEdges();
                }

                if (this.normalMode == 0) {
                    this.inGeo.calcTrianglesNormals();
                }

                this.tessellator.tessellateTriangles();
            }

            if (this.image != null && this.parent != null) {
                ((PShapeOpenGL)this.parent).addTexture(this.image);
            }

            this.firstPolyIndexCache = this.tessellator.firstPolyIndexCache;
            this.lastPolyIndexCache = this.tessellator.lastPolyIndexCache;
            this.firstLineIndexCache = this.tessellator.firstLineIndexCache;
            this.lastLineIndexCache = this.tessellator.lastLineIndexCache;
            this.firstPointIndexCache = this.tessellator.firstPointIndexCache;
            this.lastPointIndexCache = this.tessellator.lastPointIndexCache;
        }

        this.firstPolyVertex = this.lastPolyVertex = -1;
        this.firstLineVertex = this.lastLineVertex = -1;
        this.firstPointVertex = this.lastPointVertex = -1;
        this.tessellated = true;
    }

    protected void tessellatePoint() {
        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;
        if (this.params.length == 2) {
            x = this.params[0];
            y = this.params[1];
            z = 0.0F;
        } else if (this.params.length == 3) {
            x = this.params[0];
            y = this.params[1];
            z = this.params[2];
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addPoint(x, y, z, this.fill, this.stroke);
        this.tessellator.tessellatePoints();
    }

    protected void tessellateLine() {
        float x1 = 0.0F;
        float y1 = 0.0F;
        float z1 = 0.0F;
        float x2 = 0.0F;
        float y2 = 0.0F;
        float z2 = 0.0F;
        if (this.params.length == 4) {
            x1 = this.params[0];
            y1 = this.params[1];
            x2 = this.params[2];
            y2 = this.params[3];
        } else if (this.params.length == 6) {
            x1 = this.params[0];
            y1 = this.params[1];
            z1 = this.params[2];
            x2 = this.params[3];
            y2 = this.params[4];
            z2 = this.params[5];
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addLine(x1, y1, z1, x2, y2, z2, this.fill, this.stroke);
        this.tessellator.tessellateLines();
    }

    protected void tessellateTriangle() {
        float x1 = 0.0F;
        float y1 = 0.0F;
        float x2 = 0.0F;
        float y2 = 0.0F;
        float x3 = 0.0F;
        float y3 = 0.0F;
        if (this.params.length == 6) {
            x1 = this.params[0];
            y1 = this.params[1];
            x2 = this.params[2];
            y2 = this.params[3];
            x3 = this.params[4];
            y3 = this.params[5];
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addTriangle(x1, y1, 0.0F, x2, y2, 0.0F, x3, y3, 0.0F, this.fill, this.stroke);
        this.tessellator.tessellateTriangles();
    }

    protected void tessellateQuad() {
        float x1 = 0.0F;
        float y1 = 0.0F;
        float x2 = 0.0F;
        float y2 = 0.0F;
        float x3 = 0.0F;
        float y3 = 0.0F;
        float x4 = 0.0F;
        float y4 = 0.0F;
        if (this.params.length == 8) {
            x1 = this.params[0];
            y1 = this.params[1];
            x2 = this.params[2];
            y2 = this.params[3];
            x3 = this.params[4];
            y3 = this.params[5];
            x4 = this.params[6];
            y4 = this.params[7];
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addQuad(x1, y1, 0.0F, x2, y2, 0.0F, x3, y3, 0.0F, x4, y4, 0.0F, this.stroke);
        this.tessellator.tessellateQuads();
    }

    protected void tessellateRect() {
        float a = 0.0F;
        float b = 0.0F;
        float c = 0.0F;
        float d = 0.0F;
        float tl = 0.0F;
        float tr = 0.0F;
        float br = 0.0F;
        float bl = 0.0F;
        boolean rounded = false;
        int mode = this.rectMode;
        if (this.params.length != 4 && this.params.length != 5) {
            if (this.params.length == 8) {
                a = this.params[0];
                b = this.params[1];
                c = this.params[2];
                d = this.params[3];
                tl = this.params[4];
                tr = this.params[5];
                br = this.params[6];
                bl = this.params[7];
                rounded = true;
            }
        } else {
            a = this.params[0];
            b = this.params[1];
            c = this.params[2];
            d = this.params[3];
            rounded = false;
            if (this.params.length == 5) {
                tl = this.params[4];
                tr = this.params[4];
                br = this.params[4];
                bl = this.params[4];
                rounded = true;
            }
        }

        float hradius;
        float vradius;
        switch(mode) {
            case 0:
                c += a;
                d += b;
            case 1:
            default:
                break;
            case 2:
                hradius = c;
                vradius = d;
                c += a;
                d += b;
                a -= hradius;
                b -= vradius;
                break;
            case 3:
                hradius = c / 2.0F;
                vradius = d / 2.0F;
                c = a + hradius;
                d = b + vradius;
                a -= hradius;
                b -= vradius;
        }

        float maxRounding;
        if (a > c) {
            maxRounding = a;
            a = c;
            c = maxRounding;
        }

        if (b > d) {
            maxRounding = b;
            b = d;
            d = maxRounding;
        }

        maxRounding = PApplet.min((c - a) / 2.0F, (d - b) / 2.0F);
        if (tl > maxRounding) {
            tl = maxRounding;
        }

        if (tr > maxRounding) {
            tr = maxRounding;
        }

        if (br > maxRounding) {
            br = maxRounding;
        }

        if (bl > maxRounding) {
            bl = maxRounding;
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        if (rounded) {
            this.saveBezierVertexSettings();
            this.inGeo.addRect(a, b, c, d, tl, tr, br, bl, this.stroke);
            this.tessellator.tessellatePolygon(true, true, true);
            this.restoreBezierVertexSettings();
        } else {
            this.inGeo.addRect(a, b, c, d, this.stroke);
            this.tessellator.tessellateQuads();
        }

    }

    protected void tessellateEllipse() {
        float a = 0.0F;
        float b = 0.0F;
        float c = 0.0F;
        float d = 0.0F;
        int mode = this.ellipseMode;
        if (4 <= this.params.length) {
            a = this.params[0];
            b = this.params[1];
            c = this.params[2];
            d = this.params[3];
        }

        float x = a;
        float y = b;
        float w = c;
        float h = d;
        if (mode == 1) {
            w = c - a;
            h = d - b;
        } else if (mode == 2) {
            x = a - c;
            y = b - d;
            w = c * 2.0F;
            h = d * 2.0F;
        } else if (mode == 3) {
            x = a - c / 2.0F;
            y = b - d / 2.0F;
        }

        if (w < 0.0F) {
            x += w;
            w = -w;
        }

        if (h < 0.0F) {
            y += h;
            h = -h;
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
        this.inGeo.addEllipse(x, y, w, h, this.fill, this.stroke);
        this.tessellator.tessellateTriangleFan();
    }

    protected void tessellateArc() {
        float a = 0.0F;
        float b = 0.0F;
        float c = 0.0F;
        float d = 0.0F;
        float start = 0.0F;
        float stop = 0.0F;
        int mode = this.ellipseMode;
        int arcMode = 0;
        if (6 <= this.params.length) {
            a = this.params[0];
            b = this.params[1];
            c = this.params[2];
            d = this.params[3];
            start = this.params[4];
            stop = this.params[5];
            if (this.params.length == 7) {
                arcMode = (int)this.params[6];
            }
        }

        float x = a;
        float y = b;
        float w = c;
        float h = d;
        if (mode == 1) {
            w = c - a;
            h = d - b;
        } else if (mode == 2) {
            x = a - c;
            y = b - d;
            w = c * 2.0F;
            h = d * 2.0F;
        } else if (mode == 3) {
            x = a - c / 2.0F;
            y = b - d / 2.0F;
        }

        if (!Float.isInfinite(start) && !Float.isInfinite(stop) && stop > start) {
            while(start < 0.0F) {
                start += 6.2831855F;
                stop += 6.2831855F;
            }

            if (stop - start > 6.2831855F) {
                stop = start + 6.2831855F;
            }

            this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
            this.inGeo.setNormal(this.normalX, this.normalY, this.normalZ);
            this.inGeo.addArc(x, y, w, h, start, stop, this.fill, this.stroke, arcMode);
            this.tessellator.tessellateTriangleFan();
        }

    }

    protected void tessellateBox() {
        float w = 0.0F;
        float h = 0.0F;
        float d = 0.0F;
        if (this.params.length == 1) {
            w = h = d = this.params[0];
        } else if (this.params.length == 3) {
            w = this.params[0];
            h = this.params[1];
            d = this.params[2];
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        this.inGeo.addBox(w, h, d, this.fill, this.stroke);
        this.tessellator.tessellateQuads();
    }

    protected void tessellateSphere() {
        float r = 0.0F;
        int nu = this.sphereDetailU;
        int nv = this.sphereDetailV;
        if (1 <= this.params.length) {
            r = this.params[0];
            if (this.params.length == 2) {
                nu = nv = (int)this.params[1];
            } else if (this.params.length == 3) {
                nu = (int)this.params[1];
                nv = (int)this.params[2];
            }
        }

        if (nu < 3 || nv < 2) {
            nv = 30;
            nu = 30;
        }

        int savedDetailU = this.pg.sphereDetailU;
        int savedDetailV = this.pg.sphereDetailV;
        if (this.pg.sphereDetailU != nu || this.pg.sphereDetailV != nv) {
            this.pg.sphereDetail(nu, nv);
        }

        this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
        int[] indices = this.inGeo.addSphere(r, nu, nv, this.fill, this.stroke);
        this.tessellator.tessellateTriangles(indices);
        if (0 < savedDetailU && savedDetailU != nu || 0 < savedDetailV && savedDetailV != nv) {
            this.pg.sphereDetail(savedDetailU, savedDetailV);
        }

    }

    protected void tessellatePath() {
        if (this.vertices != null) {
            this.inGeo.setMaterial(this.fillColor, this.strokeColor, this.strokeWeight, this.ambientColor, this.specularColor, this.emissiveColor, this.shininess);
            int idx;
            boolean quad;
            if (this.vertexCodeCount == 0) {
                if (this.vertices[0].length == 2) {
                    for(idx = 0; idx < this.vertexCount; ++idx) {
                        this.inGeo.addVertex(this.vertices[idx][0], this.vertices[idx][1], 0, false);
                    }
                } else {
                    for(idx = 0; idx < this.vertexCount; ++idx) {
                        this.inGeo.addVertex(this.vertices[idx][0], this.vertices[idx][1], this.vertices[idx][2], 0, false);
                    }
                }
            } else {
                idx = 0;
                quad = true;
                int j;
                if (this.vertices[0].length == 2) {
                    for(j = 0; j < this.vertexCodeCount; ++j) {
                        switch(this.vertexCodes[j]) {
                            case 0:
                                this.inGeo.addVertex(this.vertices[idx][0], this.vertices[idx][1], 0, quad);
                                quad = false;
                                ++idx;
                                break;
                            case 1:
                                this.inGeo.addBezierVertex(this.vertices[idx + 0][0], this.vertices[idx + 0][1], 0.0F, this.vertices[idx + 1][0], this.vertices[idx + 1][1], 0.0F, this.vertices[idx + 2][0], this.vertices[idx + 2][1], 0.0F, quad);
                                quad = false;
                                idx += 3;
                                break;
                            case 2:
                                this.inGeo.addQuadraticVertex(this.vertices[idx + 0][0], this.vertices[idx + 0][1], 0.0F, this.vertices[idx + 1][0], this.vertices[idx + 1][1], 0.0F, quad);
                                quad = false;
                                idx += 2;
                                break;
                            case 3:
                                this.inGeo.addCurveVertex(this.vertices[idx][0], this.vertices[idx][1], 0.0F, quad);
                                quad = false;
                                ++idx;
                                break;
                            case 4:
                                quad = true;
                        }
                    }
                } else {
                    for(j = 0; j < this.vertexCodeCount; ++j) {
                        switch(this.vertexCodes[j]) {
                            case 0:
                                this.inGeo.addVertex(this.vertices[idx][0], this.vertices[idx][1], this.vertices[idx][2], quad);
                                quad = false;
                                ++idx;
                                break;
                            case 1:
                                this.inGeo.addBezierVertex(this.vertices[idx + 0][0], this.vertices[idx + 0][1], this.vertices[idx + 0][2], this.vertices[idx + 1][0], this.vertices[idx + 1][1], this.vertices[idx + 1][2], this.vertices[idx + 2][0], this.vertices[idx + 2][1], this.vertices[idx + 2][2], quad);
                                quad = false;
                                idx += 3;
                                break;
                            case 2:
                                this.inGeo.addQuadraticVertex(this.vertices[idx + 0][0], this.vertices[idx + 0][1], this.vertices[idx + 0][2], this.vertices[idx + 1][0], this.vertices[idx + 1][1], this.vertices[idx + 0][2], quad);
                                quad = false;
                                idx += 2;
                                break;
                            case 3:
                                this.inGeo.addCurveVertex(this.vertices[idx][0], this.vertices[idx][1], this.vertices[idx][2], quad);
                                quad = false;
                                ++idx;
                                break;
                            case 4:
                                quad = true;
                        }
                    }
                }
            }

            boolean bez = this.inGeo.hasBezierVertex();
            quad = this.inGeo.hasQuadraticVertex();
            boolean curv = this.inGeo.hasCurveVertex();
            if (bez || quad) {
                this.saveBezierVertexSettings();
            }

            if (curv) {
                this.saveCurveVertexSettings();
                this.tessellator.resetCurveVertexCount();
            }

            this.tessellator.tessellatePolygon(true, this.close, true);
            if (bez || quad) {
                this.restoreBezierVertexSettings();
            }

            if (curv) {
                this.restoreCurveVertexSettings();
            }

        }
    }

    protected void saveBezierVertexSettings() {
        this.savedBezierDetail = this.pg.bezierDetail;
        if (this.pg.bezierDetail != this.bezierDetail) {
            this.pg.bezierDetail(this.bezierDetail);
        }

    }

    protected void restoreBezierVertexSettings() {
        if (this.savedBezierDetail != this.bezierDetail) {
            this.pg.bezierDetail(this.savedBezierDetail);
        }

    }

    protected void saveCurveVertexSettings() {
        this.savedCurveDetail = this.pg.curveDetail;
        this.savedCurveTightness = this.pg.curveTightness;
        if (this.pg.curveDetail != this.curveDetail) {
            this.pg.curveDetail(this.curveDetail);
        }

        if (this.pg.curveTightness != this.curveTightness) {
            this.pg.curveTightness(this.curveTightness);
        }

    }

    protected void restoreCurveVertexSettings() {
        if (this.savedCurveDetail != this.curveDetail) {
            this.pg.curveDetail(this.savedCurveDetail);
        }

        if (this.savedCurveTightness != this.curveTightness) {
            this.pg.curveTightness(this.savedCurveTightness);
        }

    }

    protected void aggregate() {
        if (this.root == this && this.parent == null) {
            this.polyIndexOffset = 0;
            this.polyVertexOffset = 0;
            this.polyVertexAbs = 0;
            this.polyVertexRel = 0;
            this.lineIndexOffset = 0;
            this.lineVertexOffset = 0;
            this.lineVertexAbs = 0;
            this.lineVertexRel = 0;
            this.pointIndexOffset = 0;
            this.pointVertexOffset = 0;
            this.pointVertexAbs = 0;
            this.pointVertexRel = 0;
            this.aggregateImpl();
        }

    }

    protected void aggregateImpl() {
        if (this.family == 0) {
            this.hasPolys = false;
            this.hasLines = false;
            this.hasPoints = false;

            for(int i = 0; i < this.childCount; ++i) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[i];
                child.aggregateImpl();
                this.hasPolys |= child.hasPolys;
                this.hasLines |= child.hasLines;
                this.hasPoints |= child.hasPoints;
            }
        } else {
            this.hasPolys = -1 < this.firstPolyIndexCache && -1 < this.lastPolyIndexCache;
            this.hasLines = -1 < this.firstLineIndexCache && -1 < this.lastLineIndexCache;
            this.hasPoints = -1 < this.firstPointIndexCache && -1 < this.lastPointIndexCache;
        }

        if (this.hasPolys) {
            this.updatePolyIndexCache();
        }

        if (this.is3D()) {
            if (this.hasLines) {
                this.updateLineIndexCache();
            }

            if (this.hasPoints) {
                this.updatePointIndexCache();
            }
        }

        if (this.matrix != null) {
            if (this.hasPolys) {
                this.tessGeo.applyMatrixOnPolyGeometry(this.matrix, this.firstPolyVertex, this.lastPolyVertex);
            }

            if (this.is3D()) {
                if (this.hasLines) {
                    this.tessGeo.applyMatrixOnLineGeometry(this.matrix, this.firstLineVertex, this.lastLineVertex);
                }

                if (this.hasPoints) {
                    this.tessGeo.applyMatrixOnPointGeometry(this.matrix, this.firstPointVertex, this.lastPointVertex);
                }
            }
        }

    }

    protected void updatePolyIndexCache() {
        IndexCache cache = this.tessGeo.polyIndexCache;
        int gindex;
        int ioffset;
        int vcount;
        if (this.family == 0) {
            this.firstPolyIndexCache = this.lastPolyIndexCache = -1;
            gindex = -1;

            for(ioffset = 0; ioffset < this.childCount; ++ioffset) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[ioffset];
                vcount = child.firstPolyIndexCache;
                int count = -1 < vcount ? child.lastPolyIndexCache - vcount + 1 : -1;

                for(int n = vcount; n < vcount + count; ++n) {
                    if (gindex == -1) {
                        gindex = cache.addNew(n);
                        this.firstPolyIndexCache = gindex;
                    } else if (cache.vertexOffset[gindex] == cache.vertexOffset[n]) {
                        cache.incCounts(gindex, cache.indexCount[n], cache.vertexCount[n]);
                    } else {
                        gindex = cache.addNew(n);
                    }
                }

                if (-1 < child.firstPolyVertex) {
                    if (this.firstPolyVertex == -1) {
                        this.firstPolyVertex = 2147483647;
                    }

                    this.firstPolyVertex = PApplet.min(this.firstPolyVertex, child.firstPolyVertex);
                }

                if (-1 < child.lastPolyVertex) {
                    this.lastPolyVertex = PApplet.max(this.lastPolyVertex, child.lastPolyVertex);
                }
            }

            this.lastPolyIndexCache = gindex;
        } else {
            this.firstPolyVertex = this.lastPolyVertex = cache.vertexOffset[this.firstPolyIndexCache];

            for(gindex = this.firstPolyIndexCache; gindex <= this.lastPolyIndexCache; ++gindex) {
                ioffset = cache.indexOffset[gindex];
                int icount = cache.indexCount[gindex];
                vcount = cache.vertexCount[gindex];
                if (PGL.MAX_VERTEX_INDEX1 > this.root.polyVertexRel + vcount && (!this.is2D() || !this.startStrokedTex(gindex))) {
                    this.tessGeo.incPolyIndices(ioffset, ioffset + icount - 1, this.root.polyVertexRel);
                } else {
                    this.root.polyVertexRel = 0;
                    this.root.polyVertexOffset = this.root.polyVertexAbs;
                    cache.indexOffset[gindex] = this.root.polyIndexOffset;
                }

                cache.vertexOffset[gindex] = this.root.polyVertexOffset;
                if (this.is2D()) {
                    this.setFirstStrokeVertex(gindex, this.lastPolyVertex);
                }

                PShapeOpenGL var10000 = this.root;
                var10000.polyIndexOffset += icount;
                var10000 = this.root;
                var10000.polyVertexAbs += vcount;
                var10000 = this.root;
                var10000.polyVertexRel += vcount;
                this.lastPolyVertex += vcount;
            }

            --this.lastPolyVertex;
            if (this.is2D()) {
                this.setLastStrokeVertex(this.lastPolyVertex);
            }
        }

    }

    protected boolean startStrokedTex(int n) {
        return this.image != null && (n == this.firstLineIndexCache || n == this.firstPointIndexCache);
    }

    protected void setFirstStrokeVertex(int n, int vert) {
        if (n == this.firstLineIndexCache && this.firstLineVertex == -1) {
            this.firstLineVertex = this.lastLineVertex = vert;
        }

        if (n == this.firstPointIndexCache && this.firstPointVertex == -1) {
            this.firstPointVertex = this.lastPointVertex = vert;
        }

    }

    protected void setLastStrokeVertex(int vert) {
        if (-1 < this.lastLineVertex) {
            this.lastLineVertex = vert;
        }

        if (-1 < this.lastPointVertex) {
            this.lastPointVertex += vert;
        }

    }

    protected void updateLineIndexCache() {
        IndexCache cache = this.tessGeo.lineIndexCache;
        int gindex;
        int ioffset;
        int vcount;
        if (this.family == 0) {
            this.firstLineIndexCache = this.lastLineIndexCache = -1;
            gindex = -1;

            for(ioffset = 0; ioffset < this.childCount; ++ioffset) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[ioffset];
                vcount = child.firstLineIndexCache;
                int count = -1 < vcount ? child.lastLineIndexCache - vcount + 1 : -1;

                for(int n = vcount; n < vcount + count; ++n) {
                    if (gindex == -1) {
                        gindex = cache.addNew(n);
                        this.firstLineIndexCache = gindex;
                    } else if (cache.vertexOffset[gindex] == cache.vertexOffset[n]) {
                        cache.incCounts(gindex, cache.indexCount[n], cache.vertexCount[n]);
                    } else {
                        gindex = cache.addNew(n);
                    }
                }

                if (-1 < child.firstLineVertex) {
                    if (this.firstLineVertex == -1) {
                        this.firstLineVertex = 2147483647;
                    }

                    this.firstLineVertex = PApplet.min(this.firstLineVertex, child.firstLineVertex);
                }

                if (-1 < child.lastLineVertex) {
                    this.lastLineVertex = PApplet.max(this.lastLineVertex, child.lastLineVertex);
                }
            }

            this.lastLineIndexCache = gindex;
        } else {
            this.firstLineVertex = this.lastLineVertex = cache.vertexOffset[this.firstLineIndexCache];

            for(gindex = this.firstLineIndexCache; gindex <= this.lastLineIndexCache; ++gindex) {
                ioffset = cache.indexOffset[gindex];
                int icount = cache.indexCount[gindex];
                vcount = cache.vertexCount[gindex];
                if (PGL.MAX_VERTEX_INDEX1 <= this.root.lineVertexRel + vcount) {
                    this.root.lineVertexRel = 0;
                    this.root.lineVertexOffset = this.root.lineVertexAbs;
                    cache.indexOffset[gindex] = this.root.lineIndexOffset;
                } else {
                    this.tessGeo.incLineIndices(ioffset, ioffset + icount - 1, this.root.lineVertexRel);
                }

                cache.vertexOffset[gindex] = this.root.lineVertexOffset;
                PShapeOpenGL var10000 = this.root;
                var10000.lineIndexOffset += icount;
                var10000 = this.root;
                var10000.lineVertexAbs += vcount;
                var10000 = this.root;
                var10000.lineVertexRel += vcount;
                this.lastLineVertex += vcount;
            }

            --this.lastLineVertex;
        }

    }

    protected void updatePointIndexCache() {
        IndexCache cache = this.tessGeo.pointIndexCache;
        int gindex;
        int ioffset;
        int vcount;
        if (this.family == 0) {
            this.firstPointIndexCache = this.lastPointIndexCache = -1;
            gindex = -1;

            for(ioffset = 0; ioffset < this.childCount; ++ioffset) {
                PShapeOpenGL child = (PShapeOpenGL)this.children[ioffset];
                vcount = child.firstPointIndexCache;
                int count = -1 < vcount ? child.lastPointIndexCache - vcount + 1 : -1;

                for(int n = vcount; n < vcount + count; ++n) {
                    if (gindex == -1) {
                        gindex = cache.addNew(n);
                        this.firstPointIndexCache = gindex;
                    } else if (cache.vertexOffset[gindex] == cache.vertexOffset[n]) {
                        cache.incCounts(gindex, cache.indexCount[n], cache.vertexCount[n]);
                    } else {
                        gindex = cache.addNew(n);
                    }
                }

                if (-1 < child.firstPointVertex) {
                    if (this.firstPointVertex == -1) {
                        this.firstPointVertex = 2147483647;
                    }

                    this.firstPointVertex = PApplet.min(this.firstPointVertex, child.firstPointVertex);
                }

                if (-1 < child.lastPointVertex) {
                    this.lastPointVertex = PApplet.max(this.lastPointVertex, child.lastPointVertex);
                }
            }

            this.lastPointIndexCache = gindex;
        } else {
            this.firstPointVertex = this.lastPointVertex = cache.vertexOffset[this.firstPointIndexCache];

            for(gindex = this.firstPointIndexCache; gindex <= this.lastPointIndexCache; ++gindex) {
                ioffset = cache.indexOffset[gindex];
                int icount = cache.indexCount[gindex];
                vcount = cache.vertexCount[gindex];
                if (PGL.MAX_VERTEX_INDEX1 <= this.root.pointVertexRel + vcount) {
                    this.root.pointVertexRel = 0;
                    this.root.pointVertexOffset = this.root.pointVertexAbs;
                    cache.indexOffset[gindex] = this.root.pointIndexOffset;
                } else {
                    this.tessGeo.incPointIndices(ioffset, ioffset + icount - 1, this.root.pointVertexRel);
                }

                cache.vertexOffset[gindex] = this.root.pointVertexOffset;
                PShapeOpenGL var10000 = this.root;
                var10000.pointIndexOffset += icount;
                var10000 = this.root;
                var10000.pointVertexAbs += vcount;
                var10000 = this.root;
                var10000.pointVertexRel += vcount;
                this.lastPointVertex += vcount;
            }

            --this.lastPointVertex;
        }

    }

    protected void initBuffers() {
        boolean outdated = this.contextIsOutdated();
        this.context = this.pgl.getCurrentContext();
        if (this.hasPolys && (this.needBufferInit || outdated)) {
            this.initPolyBuffers();
        }

        if (this.hasLines && (this.needBufferInit || outdated)) {
            this.initLineBuffers();
        }

        if (this.hasPoints && (this.needBufferInit || outdated)) {
            this.initPointBuffers();
        }

        this.needBufferInit = false;
    }

    protected void initPolyBuffers() {
        int size = this.tessGeo.polyVertexCount;
        int sizef = size * PGL.SIZEOF_FLOAT;
        int sizei = size * PGL.SIZEOF_INT;
        this.tessGeo.updatePolyVerticesBuffer();
        if (this.bufPolyVertex == null) {
            this.bufPolyVertex = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 4, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyVertex.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.polyVerticesBuffer, this.glUsage);
        this.tessGeo.updatePolyColorsBuffer();
        if (this.bufPolyColor == null) {
            this.bufPolyColor = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyColor.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polyColorsBuffer, this.glUsage);
        this.tessGeo.updatePolyNormalsBuffer();
        if (this.bufPolyNormal == null) {
            this.bufPolyNormal = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 3, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyNormal.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 3 * sizef, this.tessGeo.polyNormalsBuffer, this.glUsage);
        this.tessGeo.updatePolyTexCoordsBuffer();
        if (this.bufPolyTexcoord == null) {
            this.bufPolyTexcoord = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 2, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyTexcoord.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 2 * sizef, this.tessGeo.polyTexCoordsBuffer, this.glUsage);
        this.tessGeo.updatePolyAmbientBuffer();
        if (this.bufPolyAmbient == null) {
            this.bufPolyAmbient = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyAmbient.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polyAmbientBuffer, this.glUsage);
        this.tessGeo.updatePolySpecularBuffer();
        if (this.bufPolySpecular == null) {
            this.bufPolySpecular = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolySpecular.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polySpecularBuffer, this.glUsage);
        this.tessGeo.updatePolyEmissiveBuffer();
        if (this.bufPolyEmissive == null) {
            this.bufPolyEmissive = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyEmissive.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.polyEmissiveBuffer, this.glUsage);
        this.tessGeo.updatePolyShininessBuffer();
        if (this.bufPolyShininess == null) {
            this.bufPolyShininess = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyShininess.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizef, this.tessGeo.polyShininessBuffer, this.glUsage);
        Iterator var4 = this.polyAttribs.keySet().iterator();

        while(var4.hasNext()) {
            String name = (String)var4.next();
            VertexAttribute attrib = (VertexAttribute)this.polyAttribs.get(name);
            this.tessGeo.updateAttribBuffer(attrib.name);
            if (!attrib.bufferCreated()) {
                attrib.createBuffer(this.pgl);
            }

            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, attrib.buf.glId);
            this.pgl.bufferData(PGL.ARRAY_BUFFER, attrib.sizeInBytes(size), (Buffer)this.tessGeo.polyAttribBuffers.get(name), this.glUsage);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
        this.tessGeo.updatePolyIndicesBuffer();
        if (this.bufPolyIndex == null) {
            this.bufPolyIndex = new VertexBuffer(this.pg, PGL.ELEMENT_ARRAY_BUFFER, 1, PGL.SIZEOF_INDEX, true);
        }

        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, this.bufPolyIndex.glId);
        this.pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, this.tessGeo.polyIndexCount * PGL.SIZEOF_INDEX, this.tessGeo.polyIndicesBuffer, this.glUsage);
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
    }

    protected void initLineBuffers() {
        int size = this.tessGeo.lineVertexCount;
        int sizef = size * PGL.SIZEOF_FLOAT;
        int sizei = size * PGL.SIZEOF_INT;
        this.tessGeo.updateLineVerticesBuffer();
        if (this.bufLineVertex == null) {
            this.bufLineVertex = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 4, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineVertex.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.lineVerticesBuffer, this.glUsage);
        this.tessGeo.updateLineColorsBuffer();
        if (this.bufLineColor == null) {
            this.bufLineColor = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineColor.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.lineColorsBuffer, this.glUsage);
        this.tessGeo.updateLineDirectionsBuffer();
        if (this.bufLineAttrib == null) {
            this.bufLineAttrib = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 4, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineAttrib.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.lineDirectionsBuffer, this.glUsage);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
        this.tessGeo.updateLineIndicesBuffer();
        if (this.bufLineIndex == null) {
            this.bufLineIndex = new VertexBuffer(this.pg, PGL.ELEMENT_ARRAY_BUFFER, 1, PGL.SIZEOF_INDEX, true);
        }

        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, this.bufLineIndex.glId);
        this.pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, this.tessGeo.lineIndexCount * PGL.SIZEOF_INDEX, this.tessGeo.lineIndicesBuffer, this.glUsage);
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
    }

    protected void initPointBuffers() {
        int size = this.tessGeo.pointVertexCount;
        int sizef = size * PGL.SIZEOF_FLOAT;
        int sizei = size * PGL.SIZEOF_INT;
        this.tessGeo.updatePointVerticesBuffer();
        if (this.bufPointVertex == null) {
            this.bufPointVertex = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 4, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointVertex.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 4 * sizef, this.tessGeo.pointVerticesBuffer, this.glUsage);
        this.tessGeo.updatePointColorsBuffer();
        if (this.bufPointColor == null) {
            this.bufPointColor = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 1, PGL.SIZEOF_INT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointColor.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, sizei, this.tessGeo.pointColorsBuffer, this.glUsage);
        this.tessGeo.updatePointOffsetsBuffer();
        if (this.bufPointAttrib == null) {
            this.bufPointAttrib = new VertexBuffer(this.pg, PGL.ARRAY_BUFFER, 2, PGL.SIZEOF_FLOAT);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointAttrib.glId);
        this.pgl.bufferData(PGL.ARRAY_BUFFER, 2 * sizef, this.tessGeo.pointOffsetsBuffer, this.glUsage);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
        this.tessGeo.updatePointIndicesBuffer();
        if (this.bufPointIndex == null) {
            this.bufPointIndex = new VertexBuffer(this.pg, PGL.ELEMENT_ARRAY_BUFFER, 1, PGL.SIZEOF_INDEX, true);
        }

        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, this.bufPointIndex.glId);
        this.pgl.bufferData(PGL.ELEMENT_ARRAY_BUFFER, this.tessGeo.pointIndexCount * PGL.SIZEOF_INDEX, this.tessGeo.pointIndicesBuffer, this.glUsage);
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
    }

    protected boolean contextIsOutdated() {
        boolean outdated = !this.pgl.contextIsCurrent(this.context);
        if (outdated) {
            this.bufPolyVertex.dispose();
            this.bufPolyColor.dispose();
            this.bufPolyNormal.dispose();
            this.bufPolyTexcoord.dispose();
            this.bufPolyAmbient.dispose();
            this.bufPolySpecular.dispose();
            this.bufPolyEmissive.dispose();
            this.bufPolyShininess.dispose();
            Iterator var2 = this.polyAttribs.values().iterator();

            while(var2.hasNext()) {
                VertexAttribute attrib = (VertexAttribute)var2.next();
                attrib.buf.dispose();
            }

            this.bufPolyIndex.dispose();
            this.bufLineVertex.dispose();
            this.bufLineColor.dispose();
            this.bufLineAttrib.dispose();
            this.bufLineIndex.dispose();
            this.bufPointVertex.dispose();
            this.bufPointColor.dispose();
            this.bufPointAttrib.dispose();
            this.bufPointIndex.dispose();
        }

        return outdated;
    }

    protected void updateGeometry() {
        this.root.initBuffers();
        if (this.root.modified) {
            this.root.updateGeometryImpl();
        }

    }

    protected void updateGeometryImpl() {
        int offset;
        int size;
        if (this.modifiedPolyVertices) {
            offset = this.firstModifiedPolyVertex;
            size = this.lastModifiedPolyVertex - offset + 1;
            this.copyPolyVertices(offset, size);
            this.modifiedPolyVertices = false;
            this.firstModifiedPolyVertex = 2147483647;
            this.lastModifiedPolyVertex = -2147483648;
        }

        if (this.modifiedPolyColors) {
            offset = this.firstModifiedPolyColor;
            size = this.lastModifiedPolyColor - offset + 1;
            this.copyPolyColors(offset, size);
            this.modifiedPolyColors = false;
            this.firstModifiedPolyColor = 2147483647;
            this.lastModifiedPolyColor = -2147483648;
        }

        if (this.modifiedPolyNormals) {
            offset = this.firstModifiedPolyNormal;
            size = this.lastModifiedPolyNormal - offset + 1;
            this.copyPolyNormals(offset, size);
            this.modifiedPolyNormals = false;
            this.firstModifiedPolyNormal = 2147483647;
            this.lastModifiedPolyNormal = -2147483648;
        }

        if (this.modifiedPolyTexCoords) {
            offset = this.firstModifiedPolyTexcoord;
            size = this.lastModifiedPolyTexcoord - offset + 1;
            this.copyPolyTexCoords(offset, size);
            this.modifiedPolyTexCoords = false;
            this.firstModifiedPolyTexcoord = 2147483647;
            this.lastModifiedPolyTexcoord = -2147483648;
        }

        if (this.modifiedPolyAmbient) {
            offset = this.firstModifiedPolyAmbient;
            size = this.lastModifiedPolyAmbient - offset + 1;
            this.copyPolyAmbient(offset, size);
            this.modifiedPolyAmbient = false;
            this.firstModifiedPolyAmbient = 2147483647;
            this.lastModifiedPolyAmbient = -2147483648;
        }

        if (this.modifiedPolySpecular) {
            offset = this.firstModifiedPolySpecular;
            size = this.lastModifiedPolySpecular - offset + 1;
            this.copyPolySpecular(offset, size);
            this.modifiedPolySpecular = false;
            this.firstModifiedPolySpecular = 2147483647;
            this.lastModifiedPolySpecular = -2147483648;
        }

        if (this.modifiedPolyEmissive) {
            offset = this.firstModifiedPolyEmissive;
            size = this.lastModifiedPolyEmissive - offset + 1;
            this.copyPolyEmissive(offset, size);
            this.modifiedPolyEmissive = false;
            this.firstModifiedPolyEmissive = 2147483647;
            this.lastModifiedPolyEmissive = -2147483648;
        }

        if (this.modifiedPolyShininess) {
            offset = this.firstModifiedPolyShininess;
            size = this.lastModifiedPolyShininess - offset + 1;
            this.copyPolyShininess(offset, size);
            this.modifiedPolyShininess = false;
            this.firstModifiedPolyShininess = 2147483647;
            this.lastModifiedPolyShininess = -2147483648;
        }

        Iterator var6 = this.polyAttribs.keySet().iterator();

        while(var6.hasNext()) {
            String name = (String)var6.next();
            VertexAttribute attrib = (VertexAttribute)this.polyAttribs.get(name);
            if (attrib.modified) {
                int offset = this.firstModifiedPolyVertex;
                int size = this.lastModifiedPolyVertex - offset + 1;
                this.copyPolyAttrib(attrib, offset, size);
                attrib.modified = false;
                attrib.firstModified = 2147483647;
                attrib.lastModified = -2147483648;
            }
        }

        if (this.modifiedLineVertices) {
            offset = this.firstModifiedLineVertex;
            size = this.lastModifiedLineVertex - offset + 1;
            this.copyLineVertices(offset, size);
            this.modifiedLineVertices = false;
            this.firstModifiedLineVertex = 2147483647;
            this.lastModifiedLineVertex = -2147483648;
        }

        if (this.modifiedLineColors) {
            offset = this.firstModifiedLineColor;
            size = this.lastModifiedLineColor - offset + 1;
            this.copyLineColors(offset, size);
            this.modifiedLineColors = false;
            this.firstModifiedLineColor = 2147483647;
            this.lastModifiedLineColor = -2147483648;
        }

        if (this.modifiedLineAttributes) {
            offset = this.firstModifiedLineAttribute;
            size = this.lastModifiedLineAttribute - offset + 1;
            this.copyLineAttributes(offset, size);
            this.modifiedLineAttributes = false;
            this.firstModifiedLineAttribute = 2147483647;
            this.lastModifiedLineAttribute = -2147483648;
        }

        if (this.modifiedPointVertices) {
            offset = this.firstModifiedPointVertex;
            size = this.lastModifiedPointVertex - offset + 1;
            this.copyPointVertices(offset, size);
            this.modifiedPointVertices = false;
            this.firstModifiedPointVertex = 2147483647;
            this.lastModifiedPointVertex = -2147483648;
        }

        if (this.modifiedPointColors) {
            offset = this.firstModifiedPointColor;
            size = this.lastModifiedPointColor - offset + 1;
            this.copyPointColors(offset, size);
            this.modifiedPointColors = false;
            this.firstModifiedPointColor = 2147483647;
            this.lastModifiedPointColor = -2147483648;
        }

        if (this.modifiedPointAttributes) {
            offset = this.firstModifiedPointAttribute;
            size = this.lastModifiedPointAttribute - offset + 1;
            this.copyPointAttributes(offset, size);
            this.modifiedPointAttributes = false;
            this.firstModifiedPointAttribute = 2147483647;
            this.lastModifiedPointAttribute = -2147483648;
        }

        this.modified = false;
    }

    protected void copyPolyVertices(int offset, int size) {
        this.tessGeo.updatePolyVerticesBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyVertex.glId);
        this.tessGeo.polyVerticesBuffer.position(4 * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, 4 * offset * PGL.SIZEOF_FLOAT, 4 * size * PGL.SIZEOF_FLOAT, this.tessGeo.polyVerticesBuffer);
        this.tessGeo.polyVerticesBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolyColors(int offset, int size) {
        this.tessGeo.updatePolyColorsBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyColor.glId);
        this.tessGeo.polyColorsBuffer.position(offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, offset * PGL.SIZEOF_INT, size * PGL.SIZEOF_INT, this.tessGeo.polyColorsBuffer);
        this.tessGeo.polyColorsBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolyNormals(int offset, int size) {
        this.tessGeo.updatePolyNormalsBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyNormal.glId);
        this.tessGeo.polyNormalsBuffer.position(3 * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, 3 * offset * PGL.SIZEOF_FLOAT, 3 * size * PGL.SIZEOF_FLOAT, this.tessGeo.polyNormalsBuffer);
        this.tessGeo.polyNormalsBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolyTexCoords(int offset, int size) {
        this.tessGeo.updatePolyTexCoordsBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyTexcoord.glId);
        this.tessGeo.polyTexCoordsBuffer.position(2 * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, 2 * offset * PGL.SIZEOF_FLOAT, 2 * size * PGL.SIZEOF_FLOAT, this.tessGeo.polyTexCoordsBuffer);
        this.tessGeo.polyTexCoordsBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolyAmbient(int offset, int size) {
        this.tessGeo.updatePolyAmbientBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyAmbient.glId);
        this.tessGeo.polyAmbientBuffer.position(offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, offset * PGL.SIZEOF_INT, size * PGL.SIZEOF_INT, this.tessGeo.polyAmbientBuffer);
        this.tessGeo.polyAmbientBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolySpecular(int offset, int size) {
        this.tessGeo.updatePolySpecularBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolySpecular.glId);
        this.tessGeo.polySpecularBuffer.position(offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, offset * PGL.SIZEOF_INT, size * PGL.SIZEOF_INT, this.tessGeo.polySpecularBuffer);
        this.tessGeo.polySpecularBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolyEmissive(int offset, int size) {
        this.tessGeo.updatePolyEmissiveBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyEmissive.glId);
        this.tessGeo.polyEmissiveBuffer.position(offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, offset * PGL.SIZEOF_INT, size * PGL.SIZEOF_INT, this.tessGeo.polyEmissiveBuffer);
        this.tessGeo.polyEmissiveBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolyShininess(int offset, int size) {
        this.tessGeo.updatePolyShininessBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPolyShininess.glId);
        this.tessGeo.polyShininessBuffer.position(offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, offset * PGL.SIZEOF_FLOAT, size * PGL.SIZEOF_FLOAT, this.tessGeo.polyShininessBuffer);
        this.tessGeo.polyShininessBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPolyAttrib(VertexAttribute attrib, int offset, int size) {
        this.tessGeo.updateAttribBuffer(attrib.name, offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, attrib.buf.glId);
        Buffer buf = (Buffer)this.tessGeo.polyAttribBuffers.get(attrib.name);
        buf.position(attrib.size * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, attrib.sizeInBytes(offset), attrib.sizeInBytes(size), buf);
        buf.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyLineVertices(int offset, int size) {
        this.tessGeo.updateLineVerticesBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineVertex.glId);
        this.tessGeo.lineVerticesBuffer.position(4 * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, 4 * offset * PGL.SIZEOF_FLOAT, 4 * size * PGL.SIZEOF_FLOAT, this.tessGeo.lineVerticesBuffer);
        this.tessGeo.lineVerticesBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyLineColors(int offset, int size) {
        this.tessGeo.updateLineColorsBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineColor.glId);
        this.tessGeo.lineColorsBuffer.position(offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, offset * PGL.SIZEOF_INT, size * PGL.SIZEOF_INT, this.tessGeo.lineColorsBuffer);
        this.tessGeo.lineColorsBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyLineAttributes(int offset, int size) {
        this.tessGeo.updateLineDirectionsBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufLineAttrib.glId);
        this.tessGeo.lineDirectionsBuffer.position(4 * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, 4 * offset * PGL.SIZEOF_FLOAT, 4 * size * PGL.SIZEOF_FLOAT, this.tessGeo.lineDirectionsBuffer);
        this.tessGeo.lineDirectionsBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPointVertices(int offset, int size) {
        this.tessGeo.updatePointVerticesBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointVertex.glId);
        this.tessGeo.pointVerticesBuffer.position(4 * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, 4 * offset * PGL.SIZEOF_FLOAT, 4 * size * PGL.SIZEOF_FLOAT, this.tessGeo.pointVerticesBuffer);
        this.tessGeo.pointVerticesBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPointColors(int offset, int size) {
        this.tessGeo.updatePointColorsBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointColor.glId);
        this.tessGeo.pointColorsBuffer.position(offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, offset * PGL.SIZEOF_INT, size * PGL.SIZEOF_INT, this.tessGeo.pointColorsBuffer);
        this.tessGeo.pointColorsBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void copyPointAttributes(int offset, int size) {
        this.tessGeo.updatePointOffsetsBuffer(offset, size);
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, this.bufPointAttrib.glId);
        this.tessGeo.pointOffsetsBuffer.position(2 * offset);
        this.pgl.bufferSubData(PGL.ARRAY_BUFFER, 2 * offset * PGL.SIZEOF_FLOAT, 2 * size * PGL.SIZEOF_FLOAT, this.tessGeo.pointOffsetsBuffer);
        this.tessGeo.pointOffsetsBuffer.rewind();
        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void setModifiedPolyVertices(int first, int last) {
        if (first < this.firstModifiedPolyVertex) {
            this.firstModifiedPolyVertex = first;
        }

        if (last > this.lastModifiedPolyVertex) {
            this.lastModifiedPolyVertex = last;
        }

        this.modifiedPolyVertices = true;
        this.modified = true;
    }

    protected void setModifiedPolyColors(int first, int last) {
        if (first < this.firstModifiedPolyColor) {
            this.firstModifiedPolyColor = first;
        }

        if (last > this.lastModifiedPolyColor) {
            this.lastModifiedPolyColor = last;
        }

        this.modifiedPolyColors = true;
        this.modified = true;
    }

    protected void setModifiedPolyNormals(int first, int last) {
        if (first < this.firstModifiedPolyNormal) {
            this.firstModifiedPolyNormal = first;
        }

        if (last > this.lastModifiedPolyNormal) {
            this.lastModifiedPolyNormal = last;
        }

        this.modifiedPolyNormals = true;
        this.modified = true;
    }

    protected void setModifiedPolyTexCoords(int first, int last) {
        if (first < this.firstModifiedPolyTexcoord) {
            this.firstModifiedPolyTexcoord = first;
        }

        if (last > this.lastModifiedPolyTexcoord) {
            this.lastModifiedPolyTexcoord = last;
        }

        this.modifiedPolyTexCoords = true;
        this.modified = true;
    }

    protected void setModifiedPolyAmbient(int first, int last) {
        if (first < this.firstModifiedPolyAmbient) {
            this.firstModifiedPolyAmbient = first;
        }

        if (last > this.lastModifiedPolyAmbient) {
            this.lastModifiedPolyAmbient = last;
        }

        this.modifiedPolyAmbient = true;
        this.modified = true;
    }

    protected void setModifiedPolySpecular(int first, int last) {
        if (first < this.firstModifiedPolySpecular) {
            this.firstModifiedPolySpecular = first;
        }

        if (last > this.lastModifiedPolySpecular) {
            this.lastModifiedPolySpecular = last;
        }

        this.modifiedPolySpecular = true;
        this.modified = true;
    }

    protected void setModifiedPolyEmissive(int first, int last) {
        if (first < this.firstModifiedPolyEmissive) {
            this.firstModifiedPolyEmissive = first;
        }

        if (last > this.lastModifiedPolyEmissive) {
            this.lastModifiedPolyEmissive = last;
        }

        this.modifiedPolyEmissive = true;
        this.modified = true;
    }

    protected void setModifiedPolyShininess(int first, int last) {
        if (first < this.firstModifiedPolyShininess) {
            this.firstModifiedPolyShininess = first;
        }

        if (last > this.lastModifiedPolyShininess) {
            this.lastModifiedPolyShininess = last;
        }

        this.modifiedPolyShininess = true;
        this.modified = true;
    }

    protected void setModifiedPolyAttrib(VertexAttribute attrib, int first, int last) {
        if (first < attrib.firstModified) {
            attrib.firstModified = first;
        }

        if (last > attrib.lastModified) {
            attrib.lastModified = last;
        }

        attrib.modified = true;
        this.modified = true;
    }

    protected void setModifiedLineVertices(int first, int last) {
        if (first < this.firstModifiedLineVertex) {
            this.firstModifiedLineVertex = first;
        }

        if (last > this.lastModifiedLineVertex) {
            this.lastModifiedLineVertex = last;
        }

        this.modifiedLineVertices = true;
        this.modified = true;
    }

    protected void setModifiedLineColors(int first, int last) {
        if (first < this.firstModifiedLineColor) {
            this.firstModifiedLineColor = first;
        }

        if (last > this.lastModifiedLineColor) {
            this.lastModifiedLineColor = last;
        }

        this.modifiedLineColors = true;
        this.modified = true;
    }

    protected void setModifiedLineAttributes(int first, int last) {
        if (first < this.firstModifiedLineAttribute) {
            this.firstModifiedLineAttribute = first;
        }

        if (last > this.lastModifiedLineAttribute) {
            this.lastModifiedLineAttribute = last;
        }

        this.modifiedLineAttributes = true;
        this.modified = true;
    }

    protected void setModifiedPointVertices(int first, int last) {
        if (first < this.firstModifiedPointVertex) {
            this.firstModifiedPointVertex = first;
        }

        if (last > this.lastModifiedPointVertex) {
            this.lastModifiedPointVertex = last;
        }

        this.modifiedPointVertices = true;
        this.modified = true;
    }

    protected void setModifiedPointColors(int first, int last) {
        if (first < this.firstModifiedPointColor) {
            this.firstModifiedPointColor = first;
        }

        if (last > this.lastModifiedPointColor) {
            this.lastModifiedPointColor = last;
        }

        this.modifiedPointColors = true;
        this.modified = true;
    }

    protected void setModifiedPointAttributes(int first, int last) {
        if (first < this.firstModifiedPointAttribute) {
            this.firstModifiedPointAttribute = first;
        }

        if (last > this.lastModifiedPointAttribute) {
            this.lastModifiedPointAttribute = last;
        }

        this.modifiedPointAttributes = true;
        this.modified = true;
    }

    public void disableStyle() {
        if (this.openShape) {
            PGraphics.showWarning("%1$s can only be called outside beginShape() and endShape()", new Object[]{"disableStyle()"});
        } else {
            this.savedStroke = this.stroke;
            this.savedStrokeColor = this.strokeColor;
            this.savedStrokeWeight = this.strokeWeight;
            this.savedStrokeCap = this.strokeCap;
            this.savedStrokeJoin = this.strokeJoin;
            this.savedFill = this.fill;
            this.savedFillColor = this.fillColor;
            this.savedTint = this.tint;
            this.savedTintColor = this.tintColor;
            this.savedAmbientColor = this.ambientColor;
            this.savedSpecularColor = this.specularColor;
            this.savedEmissiveColor = this.emissiveColor;
            this.savedShininess = this.shininess;
            this.savedTextureMode = this.textureMode;
            super.disableStyle();
        }
    }

    public void enableStyle() {
        if (this.savedStroke) {
            this.setStroke(true);
            this.setStroke(this.savedStrokeColor);
            this.setStrokeWeight(this.savedStrokeWeight);
            this.setStrokeCap(this.savedStrokeCap);
            this.setStrokeJoin(this.savedStrokeJoin);
        } else {
            this.setStroke(false);
        }

        if (this.savedFill) {
            this.setFill(true);
            this.setFill(this.savedFillColor);
        } else {
            this.setFill(false);
        }

        if (this.savedTint) {
            this.setTint(true);
            this.setTint(this.savedTintColor);
        }

        this.setAmbient(this.savedAmbientColor);
        this.setSpecular(this.savedSpecularColor);
        this.setEmissive(this.savedEmissiveColor);
        this.setShininess(this.savedShininess);
        if (this.image != null) {
            this.setTextureMode(this.savedTextureMode);
        }

        super.enableStyle();
    }

    protected void styles(PGraphics g) {
        if (g instanceof PGraphicsOpenGL) {
            if (g.stroke) {
                this.setStroke(true);
                this.setStroke(g.strokeColor);
                this.setStrokeWeight(g.strokeWeight);
                this.setStrokeCap(g.strokeCap);
                this.setStrokeJoin(g.strokeJoin);
            } else {
                this.setStroke(false);
            }

            if (g.fill) {
                this.setFill(true);
                this.setFill(g.fillColor);
            } else {
                this.setFill(false);
            }

            if (g.tint) {
                this.setTint(true);
                this.setTint(g.tintColor);
            }

            this.setAmbient(g.ambientColor);
            this.setSpecular(g.specularColor);
            this.setEmissive(g.emissiveColor);
            this.setShininess(g.shininess);
            if (this.image != null) {
                this.setTextureMode(g.textureMode);
            }
        } else {
            super.styles(g);
        }

    }

    public void draw(PGraphics g) {
        if (g instanceof PGraphicsOpenGL) {
            PGraphicsOpenGL gl = (PGraphicsOpenGL)g;
            if (this.visible) {
                this.pre(gl);
                this.updateTessellation();
                this.updateGeometry();
                if (this.family == 0) {
                    if (this.fragmentedGroup(gl)) {
                        for(int i = 0; i < this.childCount; ++i) {
                            ((PShapeOpenGL)this.children[i]).draw(gl);
                        }
                    } else {
                        PImage tex = null;
                        if (this.textures != null && this.textures.size() == 1) {
                            tex = (PImage)this.textures.toArray()[0];
                        }

                        this.render(gl, tex);
                    }
                } else {
                    this.render(gl, this.image);
                }

                this.post(gl);
            }
        } else {
            if (this.family == 3) {
                this.inGeoToVertices();
            }

            this.pre(g);
            this.drawImpl(g);
            this.post(g);
        }

    }

    private void inGeoToVertices() {
        this.vertexCount = 0;
        this.vertexCodeCount = 0;
        int v;
        float y;
        float cx;
        if (this.inGeo.codeCount == 0) {
            for(v = 0; v < this.inGeo.vertexCount; ++v) {
                int index = 3 * v;
                y = this.inGeo.vertices[index++];
                cx = this.inGeo.vertices[index];
                super.vertex(y, cx);
            }
        } else {
            int idx = 0;
            boolean insideContour = false;

            for(int j = 0; j < this.inGeo.codeCount; ++j) {
                float x3;
                float y3;
                float x;
                switch(this.inGeo.codes[j]) {
                    case 0:
                        v = 3 * idx;
                        x = this.inGeo.vertices[v++];
                        y = this.inGeo.vertices[v];
                        super.vertex(x, y);
                        ++idx;
                        break;
                    case 1:
                        v = 3 * idx;
                        float x2 = this.inGeo.vertices[v++];
                        float y2 = this.inGeo.vertices[v];
                        v = 3 * (idx + 1);
                        x3 = this.inGeo.vertices[v++];
                        y3 = this.inGeo.vertices[v];
                        v = 3 * (idx + 2);
                        float x4 = this.inGeo.vertices[v++];
                        float y4 = this.inGeo.vertices[v];
                        super.bezierVertex(x2, y2, x3, y3, x4, y4);
                        idx += 3;
                        break;
                    case 2:
                        v = 3 * idx;
                        cx = this.inGeo.vertices[v++];
                        float cy = this.inGeo.vertices[v];
                        v = 3 * (idx + 1);
                        x3 = this.inGeo.vertices[v++];
                        y3 = this.inGeo.vertices[v];
                        super.quadraticVertex(cx, cy, x3, y3);
                        idx += 2;
                        break;
                    case 3:
                        v = 3 * idx;
                        x = this.inGeo.vertices[v++];
                        y = this.inGeo.vertices[v];
                        super.curveVertex(x, y);
                        ++idx;
                        break;
                    case 4:
                        if (insideContour) {
                            super.endContourImpl();
                        }

                        super.beginContourImpl();
                        insideContour = true;
                }
            }

            if (insideContour) {
                super.endContourImpl();
            }
        }

    }

    protected boolean fragmentedGroup(PGraphicsOpenGL g) {
        return g.getHint(6) || this.textures != null && (1 < this.textures.size() || this.untexChild) || this.strokedTexture;
    }

    protected void pre(PGraphics g) {
        if (g instanceof PGraphicsOpenGL) {
            if (!this.style) {
                this.styles(g);
            }
        } else {
            super.pre(g);
        }

    }

    protected void post(PGraphics g) {
        if (!(g instanceof PGraphicsOpenGL)) {
            super.post(g);
        }

    }

    protected void drawGeometry(PGraphics g) {
        this.vertexCount = this.inGeo.vertexCount;
        this.vertices = this.inGeo.getVertexData();
        super.drawGeometry(g);
        this.vertexCount = 0;
        this.vertices = (float[][])null;
    }

    protected void render(PGraphicsOpenGL g, PImage texture) {
        if (this.root == null) {
            throw new RuntimeException("Error rendering PShapeOpenGL, root shape is null");
        } else {
            if (this.hasPolys) {
                this.renderPolys(g, texture);
                if (g.haveRaw()) {
                    this.rawPolys(g, texture);
                }
            }

            if (this.is3D()) {
                if (this.hasLines) {
                    this.renderLines(g);
                    if (g.haveRaw()) {
                        this.rawLines(g);
                    }
                }

                if (this.hasPoints) {
                    this.renderPoints(g);
                    if (g.haveRaw()) {
                        this.rawPoints(g);
                    }
                }
            }

        }
    }

    protected void renderPolys(PGraphicsOpenGL g, PImage textureImage) {
        boolean customShader = g.polyShader != null;
        boolean needNormals = customShader ? g.polyShader.accessNormals() : false;
        boolean needTexCoords = customShader ? g.polyShader.accessTexCoords() : false;
        Texture tex = textureImage != null ? g.getTexture(textureImage) : null;
        boolean renderingFill = false;
        boolean renderingStroke = false;
        PShader shader = null;
        IndexCache cache = this.tessGeo.polyIndexCache;

        for(int n = this.firstPolyIndexCache; n <= this.lastPolyIndexCache; ++n) {
            if (this.is3D() || tex != null && (this.firstLineIndexCache == -1 || n < this.firstLineIndexCache) && (this.firstPointIndexCache == -1 || n < this.firstPointIndexCache)) {
                if (!renderingFill) {
                    shader = g.getPolyShader(g.lights, tex != null);
                    shader.bind();
                    renderingFill = true;
                }
            } else if (!renderingStroke) {
                if (tex != null) {
                    tex.unbind();
                    tex = null;
                }

                if (shader != null && shader.bound()) {
                    shader.unbind();
                }

                shader = g.getPolyShader(g.lights, false);
                shader.bind();
                renderingFill = false;
                renderingStroke = true;
            }

            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];
            shader.setVertexAttribute(this.root.bufPolyVertex.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.setColorAttribute(this.root.bufPolyColor.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
            if (g.lights) {
                shader.setNormalAttribute(this.root.bufPolyNormal.glId, 3, PGL.FLOAT, 0, 3 * voffset * PGL.SIZEOF_FLOAT);
                shader.setAmbientAttribute(this.root.bufPolyAmbient.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                shader.setSpecularAttribute(this.root.bufPolySpecular.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                shader.setEmissiveAttribute(this.root.bufPolyEmissive.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
                shader.setShininessAttribute(this.root.bufPolyShininess.glId, 1, PGL.FLOAT, 0, voffset * PGL.SIZEOF_FLOAT);
            }

            if (g.lights || needNormals) {
                shader.setNormalAttribute(this.root.bufPolyNormal.glId, 3, PGL.FLOAT, 0, 3 * voffset * PGL.SIZEOF_FLOAT);
            }

            if (tex != null || needTexCoords) {
                shader.setTexcoordAttribute(this.root.bufPolyTexcoord.glId, 2, PGL.FLOAT, 0, 2 * voffset * PGL.SIZEOF_FLOAT);
                shader.setTexture(tex);
            }

            Iterator var15 = this.polyAttribs.values().iterator();

            while(var15.hasNext()) {
                VertexAttribute attrib = (VertexAttribute)var15.next();
                if (attrib.active(shader)) {
                    attrib.bind(this.pgl);
                    shader.setAttributeVBO(attrib.glLoc, attrib.buf.glId, attrib.tessSize, attrib.type, attrib.isColor(), 0, attrib.sizeInBytes(voffset));
                }
            }

            shader.draw(this.root.bufPolyIndex.glId, icount, ioffset);
        }

        Iterator var17 = this.polyAttribs.values().iterator();

        while(var17.hasNext()) {
            VertexAttribute attrib = (VertexAttribute)var17.next();
            if (attrib.active(shader)) {
                attrib.unbind(this.pgl);
            }
        }

        if (shader != null && shader.bound()) {
            shader.unbind();
        }

    }

    protected void rawPolys(PGraphicsOpenGL g, PImage textureImage) {
        PGraphics raw = g.getRaw();
        raw.colorMode(1);
        raw.noStroke();
        raw.beginShape(9);
        float[] vertices = this.tessGeo.polyVertices;
        int[] color = this.tessGeo.polyColors;
        float[] uv = this.tessGeo.polyTexCoords;
        short[] indices = this.tessGeo.polyIndices;
        IndexCache cache = this.tessGeo.polyIndexCache;

        for(int n = this.firstPolyIndexCache; n <= this.lastPolyIndexCache; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];

            for(int tr = ioffset / 3; tr < (ioffset + icount) / 3; ++tr) {
                int i0 = voffset + indices[3 * tr + 0];
                int i1 = voffset + indices[3 * tr + 1];
                int i2 = voffset + indices[3 * tr + 2];
                float[] src0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                float[] src1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                float[] src2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                float[] pt0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                float[] pt1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                float[] pt2 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                int argb0 = PGL.nativeToJavaARGB(color[i0]);
                int argb1 = PGL.nativeToJavaARGB(color[i1]);
                int argb2 = PGL.nativeToJavaARGB(color[i2]);
                PApplet.arrayCopy(vertices, 4 * i0, src0, 0, 4);
                PApplet.arrayCopy(vertices, 4 * i1, src1, 0, 4);
                PApplet.arrayCopy(vertices, 4 * i2, src2, 0, 4);
                g.modelview.mult(src0, pt0);
                g.modelview.mult(src1, pt1);
                g.modelview.mult(src2, pt2);
                float sx0;
                float sy0;
                float sx1;
                float sy1;
                float sx2;
                float sy2;
                if (textureImage != null) {
                    raw.texture(textureImage);
                    if (raw.is3D()) {
                        raw.fill(argb0);
                        raw.vertex(pt0[0], pt0[1], pt0[2], uv[2 * i0 + 0], uv[2 * i0 + 1]);
                        raw.fill(argb1);
                        raw.vertex(pt1[0], pt1[1], pt1[2], uv[2 * i1 + 0], uv[2 * i1 + 1]);
                        raw.fill(argb2);
                        raw.vertex(pt2[0], pt2[1], pt2[2], uv[2 * i2 + 0], uv[2 * i2 + 1]);
                    } else if (raw.is2D()) {
                        sx0 = g.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        sy0 = g.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        sx1 = g.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        sy1 = g.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        sx2 = g.screenXImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                        sy2 = g.screenYImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                        raw.fill(argb0);
                        raw.vertex(sx0, sy0, uv[2 * i0 + 0], uv[2 * i0 + 1]);
                        raw.fill(argb1);
                        raw.vertex(sx1, sy1, uv[2 * i1 + 0], uv[2 * i1 + 1]);
                        raw.fill(argb1);
                        raw.vertex(sx2, sy2, uv[2 * i2 + 0], uv[2 * i2 + 1]);
                    }
                } else if (raw.is3D()) {
                    raw.fill(argb0);
                    raw.vertex(pt0[0], pt0[1], pt0[2]);
                    raw.fill(argb1);
                    raw.vertex(pt1[0], pt1[1], pt1[2]);
                    raw.fill(argb2);
                    raw.vertex(pt2[0], pt2[1], pt2[2]);
                } else if (raw.is2D()) {
                    sx0 = g.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    sy0 = g.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    sx1 = g.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                    sy1 = g.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                    sx2 = g.screenXImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                    sy2 = g.screenYImpl(pt2[0], pt2[1], pt2[2], pt2[3]);
                    raw.fill(argb0);
                    raw.vertex(sx0, sy0);
                    raw.fill(argb1);
                    raw.vertex(sx1, sy1);
                    raw.fill(argb2);
                    raw.vertex(sx2, sy2);
                }
            }
        }

        raw.endShape();
    }

    protected void renderLines(PGraphicsOpenGL g) {
        PShader shader = g.getLineShader();
        shader.bind();
        IndexCache cache = this.tessGeo.lineIndexCache;

        for(int n = this.firstLineIndexCache; n <= this.lastLineIndexCache; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];
            shader.setVertexAttribute(this.root.bufLineVertex.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.setColorAttribute(this.root.bufLineColor.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
            shader.setLineAttribute(this.root.bufLineAttrib.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.draw(this.root.bufLineIndex.glId, icount, ioffset);
        }

        shader.unbind();
    }

    protected void rawLines(PGraphicsOpenGL g) {
        PGraphics raw = g.getRaw();
        raw.colorMode(1);
        raw.noFill();
        raw.strokeCap(this.strokeCap);
        raw.strokeJoin(this.strokeJoin);
        raw.beginShape(5);
        float[] vertices = this.tessGeo.lineVertices;
        int[] color = this.tessGeo.lineColors;
        float[] attribs = this.tessGeo.lineDirections;
        short[] indices = this.tessGeo.lineIndices;
        IndexCache cache = this.tessGeo.lineIndexCache;

        for(int n = this.firstLineIndexCache; n <= this.lastLineIndexCache; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];

            for(int ln = ioffset / 6; ln < (ioffset + icount) / 6; ++ln) {
                int i0 = voffset + indices[6 * ln + 0];
                int i1 = voffset + indices[6 * ln + 5];
                float sw0 = 2.0F * attribs[4 * i0 + 3];
                float sw1 = 2.0F * attribs[4 * i1 + 3];
                if (!PGraphicsOpenGL.zero(sw0)) {
                    float[] src0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    float[] src1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    float[] pt0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    float[] pt1 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                    int argb0 = PGL.nativeToJavaARGB(color[i0]);
                    int argb1 = PGL.nativeToJavaARGB(color[i1]);
                    PApplet.arrayCopy(vertices, 4 * i0, src0, 0, 4);
                    PApplet.arrayCopy(vertices, 4 * i1, src1, 0, 4);
                    g.modelview.mult(src0, pt0);
                    g.modelview.mult(src1, pt1);
                    if (raw.is3D()) {
                        raw.strokeWeight(sw0);
                        raw.stroke(argb0);
                        raw.vertex(pt0[0], pt0[1], pt0[2]);
                        raw.strokeWeight(sw1);
                        raw.stroke(argb1);
                        raw.vertex(pt1[0], pt1[1], pt1[2]);
                    } else if (raw.is2D()) {
                        float sx0 = g.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        float sy0 = g.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                        float sx1 = g.screenXImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        float sy1 = g.screenYImpl(pt1[0], pt1[1], pt1[2], pt1[3]);
                        raw.strokeWeight(sw0);
                        raw.stroke(argb0);
                        raw.vertex(sx0, sy0);
                        raw.strokeWeight(sw1);
                        raw.stroke(argb1);
                        raw.vertex(sx1, sy1);
                    }
                }
            }
        }

        raw.endShape();
    }

    protected void renderPoints(PGraphicsOpenGL g) {
        PShader shader = g.getPointShader();
        shader.bind();
        IndexCache cache = this.tessGeo.pointIndexCache;

        for(int n = this.firstPointIndexCache; n <= this.lastPointIndexCache; ++n) {
            int ioffset = cache.indexOffset[n];
            int icount = cache.indexCount[n];
            int voffset = cache.vertexOffset[n];
            shader.setVertexAttribute(this.root.bufPointVertex.glId, 4, PGL.FLOAT, 0, 4 * voffset * PGL.SIZEOF_FLOAT);
            shader.setColorAttribute(this.root.bufPointColor.glId, 4, PGL.UNSIGNED_BYTE, 0, 4 * voffset * PGL.SIZEOF_BYTE);
            shader.setPointAttribute(this.root.bufPointAttrib.glId, 2, PGL.FLOAT, 0, 2 * voffset * PGL.SIZEOF_FLOAT);
            shader.draw(this.root.bufPointIndex.glId, icount, ioffset);
        }

        shader.unbind();
    }

    protected void rawPoints(PGraphicsOpenGL g) {
        PGraphics raw = g.getRaw();
        raw.colorMode(1);
        raw.noFill();
        raw.strokeCap(this.strokeCap);
        raw.beginShape(3);
        float[] vertices = this.tessGeo.pointVertices;
        int[] color = this.tessGeo.pointColors;
        float[] attribs = this.tessGeo.pointOffsets;
        short[] indices = this.tessGeo.pointIndices;
        IndexCache cache = this.tessGeo.pointIndexCache;

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
                float[] src0 = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
                PApplet.arrayCopy(vertices, 4 * i0, src0, 0, 4);
                g.modelview.mult(src0, pt0);
                if (raw.is3D()) {
                    raw.strokeWeight(weight);
                    raw.stroke(argb0);
                    raw.vertex(pt0[0], pt0[1], pt0[2]);
                } else if (raw.is2D()) {
                    float sx0 = g.screenXImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    float sy0 = g.screenYImpl(pt0[0], pt0[1], pt0[2], pt0[3]);
                    raw.strokeWeight(weight);
                    raw.stroke(argb0);
                    raw.vertex(sx0, sy0);
                }
            }
        }

        raw.endShape();
    }
}

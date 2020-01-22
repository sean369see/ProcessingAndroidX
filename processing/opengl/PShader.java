package processing.opengl;

import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL.GLResourceShader;

public class PShader implements PConstants {
    protected static final int POINT = 0;
    protected static final int LINE = 1;
    protected static final int POLY = 2;
    protected static final int COLOR = 3;
    protected static final int LIGHT = 4;
    protected static final int TEXTURE = 5;
    protected static final int TEXLIGHT = 6;
    protected static String pointShaderAttrRegexp = "attribute *vec2 *offset";
    protected static String pointShaderInRegexp = "in *vec2 *offset;";
    protected static String lineShaderAttrRegexp = "attribute *vec4 *direction";
    protected static String lineShaderInRegexp = "in *vec4 *direction";
    protected static String pointShaderDefRegexp = "#define *PROCESSING_POINT_SHADER";
    protected static String lineShaderDefRegexp = "#define *PROCESSING_LINE_SHADER";
    protected static String colorShaderDefRegexp = "#define *PROCESSING_COLOR_SHADER";
    protected static String lightShaderDefRegexp = "#define *PROCESSING_LIGHT_SHADER";
    protected static String texShaderDefRegexp = "#define *PROCESSING_TEXTURE_SHADER";
    protected static String texlightShaderDefRegexp = "#define *PROCESSING_TEXLIGHT_SHADER";
    protected static String polyShaderDefRegexp = "#define *PROCESSING_POLYGON_SHADER";
    protected static String triShaderAttrRegexp = "#define *PROCESSING_TRIANGLES_SHADER";
    protected static String quadShaderAttrRegexp = "#define *PROCESSING_QUADS_SHADER";
    protected PApplet parent;
    protected PGraphicsOpenGL primaryPG;
    protected PGraphicsOpenGL currentPG;
    protected PGL pgl;
    protected int context;
    protected int type;
    public int glProgram;
    public int glVertex;
    public int glFragment;
    private GLResourceShader glres;
    protected URL vertexURL;
    protected URL fragmentURL;
    protected String vertexFilename;
    protected String fragmentFilename;
    protected String[] vertexShaderSource;
    protected String[] fragmentShaderSource;
    protected boolean bound;
    protected HashMap<Integer, PShader.UniformValue> uniformValues;
    protected HashMap<Integer, Texture> textures;
    protected HashMap<Integer, Integer> texUnits;
    protected IntBuffer intBuffer;
    protected FloatBuffer floatBuffer;
    protected boolean loadedAttributes;
    protected boolean loadedUniforms;
    protected int transformMatLoc;
    protected int modelviewMatLoc;
    protected int projectionMatLoc;
    protected int ppixelsLoc;
    protected int ppixelsUnit;
    protected int viewportLoc;
    protected int resolutionLoc;
    protected int perspectiveLoc;
    protected int scaleLoc;
    protected int lightCountLoc;
    protected int lightPositionLoc;
    protected int lightNormalLoc;
    protected int lightAmbientLoc;
    protected int lightDiffuseLoc;
    protected int lightSpecularLoc;
    protected int lightFalloffLoc;
    protected int lightSpotLoc;
    protected Texture texture;
    protected int texUnit;
    protected int textureLoc;
    protected int texMatrixLoc;
    protected int texOffsetLoc;
    protected float[] tcmat;
    protected int vertexLoc;
    protected int colorLoc;
    protected int normalLoc;
    protected int texCoordLoc;
    protected int normalMatLoc;
    protected int directionLoc;
    protected int offsetLoc;
    protected int ambientLoc;
    protected int specularLoc;
    protected int emissiveLoc;
    protected int shininessLoc;

    public PShader() {
        this.uniformValues = null;
        this.loadedAttributes = false;
        this.loadedUniforms = false;
        this.parent = null;
        this.pgl = null;
        this.context = -1;
        this.vertexURL = null;
        this.fragmentURL = null;
        this.vertexFilename = null;
        this.fragmentFilename = null;
        this.glProgram = 0;
        this.glVertex = 0;
        this.glFragment = 0;
        this.intBuffer = PGL.allocateIntBuffer(1);
        this.floatBuffer = PGL.allocateFloatBuffer(1);
        this.bound = false;
        this.type = -1;
    }

    public PShader(PApplet parent) {
        this();
        this.parent = parent;
        this.primaryPG = (PGraphicsOpenGL)parent.g;
        this.pgl = this.primaryPG.pgl;
        this.context = this.pgl.createEmptyContext();
    }

    public PShader(PApplet parent, String vertFilename, String fragFilename) {
        this.uniformValues = null;
        this.loadedAttributes = false;
        this.loadedUniforms = false;
        this.parent = parent;
        this.primaryPG = (PGraphicsOpenGL)parent.g;
        this.pgl = this.primaryPG.pgl;
        this.vertexURL = null;
        this.fragmentURL = null;
        this.vertexFilename = vertFilename;
        this.fragmentFilename = fragFilename;
        this.fragmentShaderSource = this.pgl.loadFragmentShader(fragFilename);
        this.vertexShaderSource = this.pgl.loadVertexShader(vertFilename);
        this.glProgram = 0;
        this.glVertex = 0;
        this.glFragment = 0;
        this.intBuffer = PGL.allocateIntBuffer(1);
        this.floatBuffer = PGL.allocateFloatBuffer(1);
        int vertType = getShaderType(this.vertexShaderSource, -1);
        int fragType = getShaderType(this.fragmentShaderSource, -1);
        if (vertType == -1 && fragType == -1) {
            this.type = 2;
        } else if (vertType == -1) {
            this.type = fragType;
        } else if (fragType == -1) {
            this.type = vertType;
        } else if (fragType == vertType) {
            this.type = vertType;
        } else {
            PGraphics.showWarning("The vertex and fragment shaders have different types");
        }

    }

    public PShader(PApplet parent, URL vertURL, URL fragURL) {
        this.uniformValues = null;
        this.loadedAttributes = false;
        this.loadedUniforms = false;
        this.parent = parent;
        this.primaryPG = (PGraphicsOpenGL)parent.g;
        this.pgl = this.primaryPG.pgl;
        this.vertexURL = vertURL;
        this.fragmentURL = fragURL;
        this.vertexFilename = null;
        this.fragmentFilename = null;
        this.fragmentShaderSource = this.pgl.loadFragmentShader(fragURL);
        this.vertexShaderSource = this.pgl.loadVertexShader(vertURL);
        this.glProgram = 0;
        this.glVertex = 0;
        this.glFragment = 0;
        this.intBuffer = PGL.allocateIntBuffer(1);
        this.floatBuffer = PGL.allocateFloatBuffer(1);
        int vertType = getShaderType(this.vertexShaderSource, -1);
        int fragType = getShaderType(this.fragmentShaderSource, -1);
        if (vertType == -1 && fragType == -1) {
            this.type = 2;
        } else if (vertType == -1) {
            this.type = fragType;
        } else if (fragType == -1) {
            this.type = vertType;
        } else if (fragType == vertType) {
            this.type = vertType;
        } else {
            PGraphics.showWarning("The vertex and fragment shaders have different types");
        }

    }

    public PShader(PApplet parent, String[] vertSource, String[] fragSource) {
        this.uniformValues = null;
        this.loadedAttributes = false;
        this.loadedUniforms = false;
        this.parent = parent;
        this.primaryPG = (PGraphicsOpenGL)parent.g;
        this.pgl = this.primaryPG.pgl;
        this.vertexURL = null;
        this.fragmentURL = null;
        this.vertexFilename = null;
        this.fragmentFilename = null;
        this.vertexShaderSource = vertSource;
        this.fragmentShaderSource = fragSource;
        this.glProgram = 0;
        this.glVertex = 0;
        this.glFragment = 0;
        this.intBuffer = PGL.allocateIntBuffer(1);
        this.floatBuffer = PGL.allocateFloatBuffer(1);
        int vertType = getShaderType(this.vertexShaderSource, -1);
        int fragType = getShaderType(this.fragmentShaderSource, -1);
        if (vertType == -1 && fragType == -1) {
            this.type = 2;
        } else if (vertType == -1) {
            this.type = fragType;
        } else if (fragType == -1) {
            this.type = vertType;
        } else if (fragType == vertType) {
            this.type = vertType;
        } else {
            PGraphics.showWarning("The vertex and fragment shaders have different types");
        }

    }

    public void setVertexShader(String vertFilename) {
        this.vertexFilename = vertFilename;
        this.vertexShaderSource = this.pgl.loadVertexShader(vertFilename);
    }

    public void setVertexShader(URL vertURL) {
        this.vertexURL = vertURL;
        this.vertexShaderSource = this.pgl.loadVertexShader(vertURL);
    }

    public void setVertexShader(String[] vertSource) {
        this.vertexShaderSource = vertSource;
    }

    public void setFragmentShader(String fragFilename) {
        this.fragmentFilename = fragFilename;
        this.fragmentShaderSource = this.pgl.loadFragmentShader(fragFilename);
    }

    public void setFragmentShader(URL fragURL) {
        this.fragmentURL = fragURL;
        this.fragmentShaderSource = this.pgl.loadFragmentShader(fragURL);
    }

    public void setFragmentShader(String[] fragSource) {
        this.fragmentShaderSource = fragSource;
    }

    public void bind() {
        this.init();
        if (!this.bound) {
            this.pgl.useProgram(this.glProgram);
            this.bound = true;
            this.consumeUniforms();
            this.bindTextures();
        }

        if (this.hasType()) {
            this.bindTyped();
        }

    }

    public void unbind() {
        if (this.hasType()) {
            this.unbindTyped();
        }

        if (this.bound) {
            this.unbindTextures();
            this.pgl.useProgram(0);
            this.bound = false;
        }

    }

    public boolean bound() {
        return this.bound;
    }

    public void set(String name, int x) {
        this.setUniformImpl(name, 0, new int[]{x});
    }

    public void set(String name, int x, int y) {
        this.setUniformImpl(name, 1, new int[]{x, y});
    }

    public void set(String name, int x, int y, int z) {
        this.setUniformImpl(name, 2, new int[]{x, y, z});
    }

    public void set(String name, int x, int y, int z, int w) {
        this.setUniformImpl(name, 3, new int[]{x, y, z, w});
    }

    public void set(String name, float x) {
        this.setUniformImpl(name, 4, new float[]{x});
    }

    public void set(String name, float x, float y) {
        this.setUniformImpl(name, 5, new float[]{x, y});
    }

    public void set(String name, float x, float y, float z) {
        this.setUniformImpl(name, 6, new float[]{x, y, z});
    }

    public void set(String name, float x, float y, float z, float w) {
        this.setUniformImpl(name, 7, new float[]{x, y, z, w});
    }

    public void set(String name, PVector vec) {
        this.setUniformImpl(name, 6, new float[]{vec.x, vec.y, vec.z});
    }

    public void set(String name, boolean x) {
        this.setUniformImpl(name, 0, new int[]{x ? 1 : 0});
    }

    public void set(String name, boolean x, boolean y) {
        this.setUniformImpl(name, 1, new int[]{x ? 1 : 0, y ? 1 : 0});
    }

    public void set(String name, boolean x, boolean y, boolean z) {
        this.setUniformImpl(name, 2, new int[]{x ? 1 : 0, y ? 1 : 0, z ? 1 : 0});
    }

    public void set(String name, boolean x, boolean y, boolean z, boolean w) {
        this.setUniformImpl(name, 3, new int[]{x ? 1 : 0, y ? 1 : 0, z ? 1 : 0, w ? 1 : 0});
    }

    public void set(String name, int[] vec) {
        this.set(name, (int[])vec, 1);
    }

    public void set(String name, int[] vec, int ncoords) {
        if (ncoords == 1) {
            this.setUniformImpl(name, 8, vec);
        } else if (ncoords == 2) {
            this.setUniformImpl(name, 9, vec);
        } else if (ncoords == 3) {
            this.setUniformImpl(name, 10, vec);
        } else if (ncoords == 4) {
            this.setUniformImpl(name, 11, vec);
        } else if (4 < ncoords) {
            PGraphics.showWarning("Only up to 4 coordinates per element are supported.");
        } else {
            PGraphics.showWarning("Wrong number of coordinates: it is negative!");
        }

    }

    public void set(String name, float[] vec) {
        this.set(name, (float[])vec, 1);
    }

    public void set(String name, float[] vec, int ncoords) {
        if (ncoords == 1) {
            this.setUniformImpl(name, 12, vec);
        } else if (ncoords == 2) {
            this.setUniformImpl(name, 13, vec);
        } else if (ncoords == 3) {
            this.setUniformImpl(name, 14, vec);
        } else if (ncoords == 4) {
            this.setUniformImpl(name, 15, vec);
        } else if (4 < ncoords) {
            PGraphics.showWarning("Only up to 4 coordinates per element are supported.");
        } else {
            PGraphics.showWarning("Wrong number of coordinates: it is negative!");
        }

    }

    public void set(String name, boolean[] vec) {
        this.set(name, (boolean[])vec, 1);
    }

    public void set(String name, boolean[] boolvec, int ncoords) {
        int[] vec = new int[boolvec.length];

        for(int i = 0; i < boolvec.length; ++i) {
            vec[i] = boolvec[i] ? 1 : 0;
        }

        this.set(name, vec, ncoords);
    }

    public void set(String name, PMatrix2D mat) {
        float[] matv = new float[]{mat.m00, mat.m01, mat.m10, mat.m11};
        this.setUniformImpl(name, 16, matv);
    }

    public void set(String name, PMatrix3D mat) {
        this.set(name, mat, false);
    }

    public void set(String name, PMatrix3D mat, boolean use3x3) {
        float[] matv;
        if (use3x3) {
            matv = new float[]{mat.m00, mat.m01, mat.m02, mat.m10, mat.m11, mat.m12, mat.m20, mat.m21, mat.m22};
            this.setUniformImpl(name, 17, matv);
        } else {
            matv = new float[]{mat.m00, mat.m01, mat.m02, mat.m03, mat.m10, mat.m11, mat.m12, mat.m13, mat.m20, mat.m21, mat.m22, mat.m23, mat.m30, mat.m31, mat.m32, mat.m33};
            this.setUniformImpl(name, 18, matv);
        }

    }

    public void set(String name, PImage tex) {
        this.setUniformImpl(name, 19, tex);
    }

    protected void setup() {
    }

    protected void draw(int idxId, int count, int offset) {
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, idxId);
        this.pgl.drawElements(PGL.TRIANGLES, count, PGL.INDEX_TYPE, offset * PGL.SIZEOF_INDEX);
        this.pgl.bindBuffer(PGL.ELEMENT_ARRAY_BUFFER, 0);
    }

    protected int getAttributeLoc(String name) {
        this.init();
        return this.pgl.getAttribLocation(this.glProgram, name);
    }

    protected int getUniformLoc(String name) {
        this.init();
        return this.pgl.getUniformLocation(this.glProgram, name);
    }

    protected void setAttributeVBO(int loc, int vboId, int size, int type, boolean normalized, int stride, int offset) {
        if (-1 < loc) {
            this.pgl.bindBuffer(PGL.ARRAY_BUFFER, vboId);
            this.pgl.vertexAttribPointer(loc, size, type, normalized, stride, offset);
        }

    }

    protected void setUniformValue(int loc, int x) {
        if (-1 < loc) {
            this.pgl.uniform1i(loc, x);
        }

    }

    protected void setUniformValue(int loc, int x, int y) {
        if (-1 < loc) {
            this.pgl.uniform2i(loc, x, y);
        }

    }

    protected void setUniformValue(int loc, int x, int y, int z) {
        if (-1 < loc) {
            this.pgl.uniform3i(loc, x, y, z);
        }

    }

    protected void setUniformValue(int loc, int x, int y, int z, int w) {
        if (-1 < loc) {
            this.pgl.uniform4i(loc, x, y, z, w);
        }

    }

    protected void setUniformValue(int loc, float x) {
        if (-1 < loc) {
            this.pgl.uniform1f(loc, x);
        }

    }

    protected void setUniformValue(int loc, float x, float y) {
        if (-1 < loc) {
            this.pgl.uniform2f(loc, x, y);
        }

    }

    protected void setUniformValue(int loc, float x, float y, float z) {
        if (-1 < loc) {
            this.pgl.uniform3f(loc, x, y, z);
        }

    }

    protected void setUniformValue(int loc, float x, float y, float z, float w) {
        if (-1 < loc) {
            this.pgl.uniform4f(loc, x, y, z, w);
        }

    }

    protected void setUniformVector(int loc, int[] vec, int ncoords, int length) {
        if (-1 < loc) {
            this.updateIntBuffer(vec);
            if (ncoords == 1) {
                this.pgl.uniform1iv(loc, length, this.intBuffer);
            } else if (ncoords == 2) {
                this.pgl.uniform2iv(loc, length, this.intBuffer);
            } else if (ncoords == 3) {
                this.pgl.uniform3iv(loc, length, this.intBuffer);
            } else if (ncoords == 4) {
                this.pgl.uniform3iv(loc, length, this.intBuffer);
            }
        }

    }

    protected void setUniformVector(int loc, float[] vec, int ncoords, int length) {
        if (-1 < loc) {
            this.updateFloatBuffer(vec);
            if (ncoords == 1) {
                this.pgl.uniform1fv(loc, length, this.floatBuffer);
            } else if (ncoords == 2) {
                this.pgl.uniform2fv(loc, length, this.floatBuffer);
            } else if (ncoords == 3) {
                this.pgl.uniform3fv(loc, length, this.floatBuffer);
            } else if (ncoords == 4) {
                this.pgl.uniform4fv(loc, length, this.floatBuffer);
            }
        }

    }

    protected void setUniformMatrix(int loc, float[] mat) {
        if (-1 < loc) {
            this.updateFloatBuffer(mat);
            if (mat.length == 4) {
                this.pgl.uniformMatrix2fv(loc, 1, false, this.floatBuffer);
            } else if (mat.length == 9) {
                this.pgl.uniformMatrix3fv(loc, 1, false, this.floatBuffer);
            } else if (mat.length == 16) {
                this.pgl.uniformMatrix4fv(loc, 1, false, this.floatBuffer);
            }
        }

    }

    protected void setUniformTex(int loc, Texture tex) {
        if (this.texUnits != null) {
            Integer unit = (Integer)this.texUnits.get(loc);
            if (unit == null) {
                throw new RuntimeException("Cannot find unit for texture " + tex);
            }

            this.pgl.activeTexture(PGL.TEXTURE0 + unit);
            tex.bind();
        }

    }

    protected void setUniformImpl(String name, int type, Object value) {
        int loc = this.getUniformLoc(name);
        if (-1 < loc) {
            if (this.uniformValues == null) {
                this.uniformValues = new HashMap();
            }

            this.uniformValues.put(loc, new PShader.UniformValue(type, value));
        } else {
            PGraphics.showWarning("The shader doesn't have a uniform called \"" + name + "\" OR the uniform was removed during compilation because it was unused.");
        }

    }

    protected void consumeUniforms() {
        if (this.uniformValues != null && 0 < this.uniformValues.size()) {
            int unit = 0;
            Iterator var2 = this.uniformValues.keySet().iterator();

            while(var2.hasNext()) {
                Integer loc = (Integer)var2.next();
                PShader.UniformValue val = (PShader.UniformValue)this.uniformValues.get(loc);
                int[] v;
                if (val.type == 0) {
                    v = (int[])((int[])val.value);
                    this.pgl.uniform1i(loc, v[0]);
                } else if (val.type == 1) {
                    v = (int[])((int[])val.value);
                    this.pgl.uniform2i(loc, v[0], v[1]);
                } else if (val.type == 2) {
                    v = (int[])((int[])val.value);
                    this.pgl.uniform3i(loc, v[0], v[1], v[2]);
                } else if (val.type == 3) {
                    v = (int[])((int[])val.value);
                    this.pgl.uniform4i(loc, v[0], v[1], v[2], v[3]);
                } else {
                    float[] v;
                    if (val.type == 4) {
                        v = (float[])((float[])val.value);
                        this.pgl.uniform1f(loc, v[0]);
                    } else if (val.type == 5) {
                        v = (float[])((float[])val.value);
                        this.pgl.uniform2f(loc, v[0], v[1]);
                    } else if (val.type == 6) {
                        v = (float[])((float[])val.value);
                        this.pgl.uniform3f(loc, v[0], v[1], v[2]);
                    } else if (val.type == 7) {
                        v = (float[])((float[])val.value);
                        this.pgl.uniform4f(loc, v[0], v[1], v[2], v[3]);
                    } else if (val.type == 8) {
                        v = (int[])((int[])val.value);
                        this.updateIntBuffer(v);
                        this.pgl.uniform1iv(loc, v.length, this.intBuffer);
                    } else if (val.type == 9) {
                        v = (int[])((int[])val.value);
                        this.updateIntBuffer(v);
                        this.pgl.uniform2iv(loc, v.length / 2, this.intBuffer);
                    } else if (val.type == 10) {
                        v = (int[])((int[])val.value);
                        this.updateIntBuffer(v);
                        this.pgl.uniform3iv(loc, v.length / 3, this.intBuffer);
                    } else if (val.type == 11) {
                        v = (int[])((int[])val.value);
                        this.updateIntBuffer(v);
                        this.pgl.uniform4iv(loc, v.length / 4, this.intBuffer);
                    } else if (val.type == 12) {
                        v = (float[])((float[])val.value);
                        this.updateFloatBuffer(v);
                        this.pgl.uniform1fv(loc, v.length, this.floatBuffer);
                    } else if (val.type == 13) {
                        v = (float[])((float[])val.value);
                        this.updateFloatBuffer(v);
                        this.pgl.uniform2fv(loc, v.length / 2, this.floatBuffer);
                    } else if (val.type == 14) {
                        v = (float[])((float[])val.value);
                        this.updateFloatBuffer(v);
                        this.pgl.uniform3fv(loc, v.length / 3, this.floatBuffer);
                    } else if (val.type == 15) {
                        v = (float[])((float[])val.value);
                        this.updateFloatBuffer(v);
                        this.pgl.uniform4fv(loc, v.length / 4, this.floatBuffer);
                    } else if (val.type == 16) {
                        v = (float[])((float[])val.value);
                        this.updateFloatBuffer(v);
                        this.pgl.uniformMatrix2fv(loc, 1, false, this.floatBuffer);
                    } else if (val.type == 17) {
                        v = (float[])((float[])val.value);
                        this.updateFloatBuffer(v);
                        this.pgl.uniformMatrix3fv(loc, 1, false, this.floatBuffer);
                    } else if (val.type == 18) {
                        v = (float[])((float[])val.value);
                        this.updateFloatBuffer(v);
                        this.pgl.uniformMatrix4fv(loc, 1, false, this.floatBuffer);
                    } else if (val.type == 19) {
                        PImage img = (PImage)val.value;
                        Texture tex = this.currentPG.getTexture(img);
                        if (this.textures == null) {
                            this.textures = new HashMap();
                        }

                        this.textures.put(loc, tex);
                        if (this.texUnits == null) {
                            this.texUnits = new HashMap();
                        }

                        if (this.texUnits.containsKey(loc)) {
                            unit = (Integer)this.texUnits.get(loc);
                            this.pgl.uniform1i(loc, unit);
                        } else {
                            this.texUnits.put(loc, unit);
                            this.pgl.uniform1i(loc, unit);
                        }

                        ++unit;
                    }
                }
            }

            this.uniformValues.clear();
        }

    }

    protected void updateIntBuffer(int[] vec) {
        this.intBuffer = PGL.updateIntBuffer(this.intBuffer, vec, false);
    }

    protected void updateFloatBuffer(float[] vec) {
        this.floatBuffer = PGL.updateFloatBuffer(this.floatBuffer, vec, false);
    }

    protected void bindTextures() {
        if (this.textures != null && this.texUnits != null) {
            Iterator var1 = this.textures.keySet().iterator();

            while(var1.hasNext()) {
                int loc = (Integer)var1.next();
                Texture tex = (Texture)this.textures.get(loc);
                Integer unit = (Integer)this.texUnits.get(loc);
                if (unit == null) {
                    throw new RuntimeException("Cannot find unit for texture " + tex);
                }

                this.pgl.activeTexture(PGL.TEXTURE0 + unit);
                tex.bind();
            }
        }

    }

    protected void unbindTextures() {
        if (this.textures != null && this.texUnits != null) {
            Iterator var1 = this.textures.keySet().iterator();

            while(var1.hasNext()) {
                int loc = (Integer)var1.next();
                Texture tex = (Texture)this.textures.get(loc);
                Integer unit = (Integer)this.texUnits.get(loc);
                if (unit == null) {
                    throw new RuntimeException("Cannot find unit for texture " + tex);
                }

                this.pgl.activeTexture(PGL.TEXTURE0 + unit);
                tex.unbind();
            }

            this.pgl.activeTexture(PGL.TEXTURE0);
        }

    }

    public void init() {
        if (this.glProgram == 0 || this.contextIsOutdated()) {
            this.create();
            if (this.compile()) {
                this.pgl.attachShader(this.glProgram, this.glVertex);
                this.pgl.attachShader(this.glProgram, this.glFragment);
                this.setup();
                this.pgl.linkProgram(this.glProgram);
                this.validate();
            }
        }

    }

    protected void create() {
        this.context = this.pgl.getCurrentContext();
        this.glres = new GLResourceShader(this);
    }

    protected boolean compile() {
        boolean vertRes = true;
        if (this.hasVertexShader()) {
            vertRes = this.compileVertexShader();
        } else {
            PGraphics.showException("Doesn't have a vertex shader");
        }

        boolean fragRes = true;
        if (this.hasFragmentShader()) {
            fragRes = this.compileFragmentShader();
        } else {
            PGraphics.showException("Doesn't have a fragment shader");
        }

        return vertRes && fragRes;
    }

    protected void validate() {
        this.pgl.getProgramiv(this.glProgram, PGL.LINK_STATUS, this.intBuffer);
        boolean linked = this.intBuffer.get(0) != 0;
        if (!linked) {
            PGraphics.showException("Cannot link shader program:\n" + this.pgl.getProgramInfoLog(this.glProgram));
        }

        this.pgl.validateProgram(this.glProgram);
        this.pgl.getProgramiv(this.glProgram, PGL.VALIDATE_STATUS, this.intBuffer);
        boolean validated = this.intBuffer.get(0) != 0;
        if (!validated) {
            PGraphics.showException("Cannot validate shader program:\n" + this.pgl.getProgramInfoLog(this.glProgram));
        }

    }

    protected boolean contextIsOutdated() {
        boolean outdated = !this.pgl.contextIsCurrent(this.context);
        if (outdated) {
            this.dispose();
        }

        return outdated;
    }

    protected boolean hasVertexShader() {
        return this.vertexShaderSource != null && 0 < this.vertexShaderSource.length;
    }

    protected boolean hasFragmentShader() {
        return this.fragmentShaderSource != null && 0 < this.fragmentShaderSource.length;
    }

    protected boolean compileVertexShader() {
        this.pgl.shaderSource(this.glVertex, PApplet.join(this.vertexShaderSource, "\n"));
        this.pgl.compileShader(this.glVertex);
        this.pgl.getShaderiv(this.glVertex, PGL.COMPILE_STATUS, this.intBuffer);
        boolean compiled = this.intBuffer.get(0) != 0;
        if (!compiled) {
            PGraphics.showException("Cannot compile vertex shader:\n" + this.pgl.getShaderInfoLog(this.glVertex));
            return false;
        } else {
            return true;
        }
    }

    protected boolean compileFragmentShader() {
        this.pgl.shaderSource(this.glFragment, PApplet.join(this.fragmentShaderSource, "\n"));
        this.pgl.compileShader(this.glFragment);
        this.pgl.getShaderiv(this.glFragment, PGL.COMPILE_STATUS, this.intBuffer);
        boolean compiled = this.intBuffer.get(0) != 0;
        if (!compiled) {
            PGraphics.showException("Cannot compile fragment shader:\n" + this.pgl.getShaderInfoLog(this.glFragment));
            return false;
        } else {
            return true;
        }
    }

    protected void dispose() {
        if (this.glres != null) {
            this.glres.dispose();
            this.glVertex = 0;
            this.glFragment = 0;
            this.glProgram = 0;
            this.glres = null;
        }

    }

    protected static int getShaderType(String[] source, int defaultType) {
        for(int i = 0; i < source.length; ++i) {
            String line = source[i].trim();
            if (PApplet.match(line, colorShaderDefRegexp) != null) {
                return 3;
            }

            if (PApplet.match(line, lightShaderDefRegexp) != null) {
                return 4;
            }

            if (PApplet.match(line, texShaderDefRegexp) != null) {
                return 5;
            }

            if (PApplet.match(line, texlightShaderDefRegexp) != null) {
                return 6;
            }

            if (PApplet.match(line, polyShaderDefRegexp) != null) {
                return 2;
            }

            if (PApplet.match(line, triShaderAttrRegexp) != null) {
                return 2;
            }

            if (PApplet.match(line, quadShaderAttrRegexp) != null) {
                return 2;
            }

            if (PApplet.match(line, pointShaderDefRegexp) != null) {
                return 0;
            }

            if (PApplet.match(line, lineShaderDefRegexp) != null) {
                return 1;
            }

            if (PApplet.match(line, pointShaderAttrRegexp) != null) {
                return 0;
            }

            if (PApplet.match(line, pointShaderInRegexp) != null) {
                return 0;
            }

            if (PApplet.match(line, lineShaderAttrRegexp) != null) {
                return 1;
            }

            if (PApplet.match(line, lineShaderInRegexp) != null) {
                return 1;
            }
        }

        return defaultType;
    }

    protected int getType() {
        return this.type;
    }

    protected void setType(int type) {
        this.type = type;
    }

    protected boolean hasType() {
        return 0 <= this.type && this.type <= 6;
    }

    protected boolean isPointShader() {
        return this.type == 0;
    }

    protected boolean isLineShader() {
        return this.type == 1;
    }

    protected boolean isPolyShader() {
        return 2 <= this.type && this.type <= 6;
    }

    protected boolean checkPolyType(int type) {
        if (this.getType() == 2) {
            return true;
        } else if (this.getType() != type) {
            if (type == 6) {
                PGraphics.showWarning("Your shader needs to be of TEXLIGHT type to render this geometry properly, using default shader instead.");
            } else if (type == 4) {
                PGraphics.showWarning("Your shader needs to be of LIGHT type to render this geometry properly, using default shader instead.");
            } else if (type == 5) {
                PGraphics.showWarning("Your shader needs to be of TEXTURE type to render this geometry properly, using default shader instead.");
            } else if (type == 3) {
                PGraphics.showWarning("Your shader needs to be of COLOR type to render this geometry properly, using default shader instead.");
            }

            return false;
        } else {
            return true;
        }
    }

    protected int getLastTexUnit() {
        return this.texUnits == null ? -1 : this.texUnits.size() - 1;
    }

    protected void setRenderer(PGraphicsOpenGL pg) {
        this.currentPG = pg;
    }

    protected void loadAttributes() {
        if (!this.loadedAttributes) {
            this.vertexLoc = this.getAttributeLoc("vertex");
            if (this.vertexLoc == -1) {
                this.vertexLoc = this.getAttributeLoc("position");
            }

            this.colorLoc = this.getAttributeLoc("color");
            this.texCoordLoc = this.getAttributeLoc("texCoord");
            this.normalLoc = this.getAttributeLoc("normal");
            this.ambientLoc = this.getAttributeLoc("ambient");
            this.specularLoc = this.getAttributeLoc("specular");
            this.emissiveLoc = this.getAttributeLoc("emissive");
            this.shininessLoc = this.getAttributeLoc("shininess");
            this.directionLoc = this.getAttributeLoc("direction");
            this.offsetLoc = this.getAttributeLoc("offset");
            this.directionLoc = this.getAttributeLoc("direction");
            this.offsetLoc = this.getAttributeLoc("offset");
            this.loadedAttributes = true;
        }
    }

    protected void loadUniforms() {
        if (!this.loadedUniforms) {
            this.transformMatLoc = this.getUniformLoc("transform");
            if (this.transformMatLoc == -1) {
                this.transformMatLoc = this.getUniformLoc("transformMatrix");
            }

            this.modelviewMatLoc = this.getUniformLoc("modelview");
            if (this.modelviewMatLoc == -1) {
                this.modelviewMatLoc = this.getUniformLoc("modelviewMatrix");
            }

            this.projectionMatLoc = this.getUniformLoc("projection");
            if (this.projectionMatLoc == -1) {
                this.projectionMatLoc = this.getUniformLoc("projectionMatrix");
            }

            this.viewportLoc = this.getUniformLoc("viewport");
            this.resolutionLoc = this.getUniformLoc("resolution");
            this.ppixelsLoc = this.getUniformLoc("ppixels");
            this.normalMatLoc = this.getUniformLoc("normalMatrix");
            this.lightCountLoc = this.getUniformLoc("lightCount");
            this.lightPositionLoc = this.getUniformLoc("lightPosition");
            this.lightNormalLoc = this.getUniformLoc("lightNormal");
            this.lightAmbientLoc = this.getUniformLoc("lightAmbient");
            this.lightDiffuseLoc = this.getUniformLoc("lightDiffuse");
            this.lightSpecularLoc = this.getUniformLoc("lightSpecular");
            this.lightFalloffLoc = this.getUniformLoc("lightFalloff");
            this.lightSpotLoc = this.getUniformLoc("lightSpot");
            this.textureLoc = this.getUniformLoc("texture");
            if (this.textureLoc == -1) {
                this.textureLoc = this.getUniformLoc("texMap");
            }

            this.texMatrixLoc = this.getUniformLoc("texMatrix");
            this.texOffsetLoc = this.getUniformLoc("texOffset");
            this.perspectiveLoc = this.getUniformLoc("perspective");
            this.scaleLoc = this.getUniformLoc("scale");
            this.loadedUniforms = true;
        }
    }

    protected void setCommonUniforms() {
        if (-1 < this.transformMatLoc) {
            this.currentPG.updateGLProjmodelview();
            this.setUniformMatrix(this.transformMatLoc, this.currentPG.glProjmodelview);
        }

        if (-1 < this.modelviewMatLoc) {
            this.currentPG.updateGLModelview();
            this.setUniformMatrix(this.modelviewMatLoc, this.currentPG.glModelview);
        }

        if (-1 < this.projectionMatLoc) {
            this.currentPG.updateGLProjection();
            this.setUniformMatrix(this.projectionMatLoc, this.currentPG.glProjection);
        }

        float w;
        float h;
        if (-1 < this.viewportLoc) {
            w = (float)this.currentPG.viewport.get(0);
            h = (float)this.currentPG.viewport.get(1);
            float w = (float)this.currentPG.viewport.get(2);
            float h = (float)this.currentPG.viewport.get(3);
            this.setUniformValue(this.viewportLoc, w, h, w, h);
        }

        if (-1 < this.resolutionLoc) {
            w = (float)this.currentPG.viewport.get(2);
            h = (float)this.currentPG.viewport.get(3);
            this.setUniformValue(this.resolutionLoc, w, h);
        }

        if (-1 < this.ppixelsLoc) {
            this.ppixelsUnit = this.getLastTexUnit() + 1;
            this.setUniformValue(this.ppixelsLoc, this.ppixelsUnit);
            this.pgl.activeTexture(PGL.TEXTURE0 + this.ppixelsUnit);
            this.currentPG.bindFrontTexture();
        } else {
            this.ppixelsUnit = -1;
        }

    }

    protected void bindTyped() {
        if (this.currentPG == null) {
            this.setRenderer(this.primaryPG.getCurrentPG());
            this.loadAttributes();
            this.loadUniforms();
        }

        this.setCommonUniforms();
        if (-1 < this.vertexLoc) {
            this.pgl.enableVertexAttribArray(this.vertexLoc);
        }

        if (-1 < this.colorLoc) {
            this.pgl.enableVertexAttribArray(this.colorLoc);
        }

        if (-1 < this.texCoordLoc) {
            this.pgl.enableVertexAttribArray(this.texCoordLoc);
        }

        if (-1 < this.normalLoc) {
            this.pgl.enableVertexAttribArray(this.normalLoc);
        }

        if (-1 < this.normalMatLoc) {
            this.currentPG.updateGLNormal();
            this.setUniformMatrix(this.normalMatLoc, this.currentPG.glNormal);
        }

        if (-1 < this.ambientLoc) {
            this.pgl.enableVertexAttribArray(this.ambientLoc);
        }

        if (-1 < this.specularLoc) {
            this.pgl.enableVertexAttribArray(this.specularLoc);
        }

        if (-1 < this.emissiveLoc) {
            this.pgl.enableVertexAttribArray(this.emissiveLoc);
        }

        if (-1 < this.shininessLoc) {
            this.pgl.enableVertexAttribArray(this.shininessLoc);
        }

        int count = this.currentPG.lightCount;
        this.setUniformValue(this.lightCountLoc, count);
        if (0 < count) {
            this.setUniformVector(this.lightPositionLoc, (float[])this.currentPG.lightPosition, 4, count);
            this.setUniformVector(this.lightNormalLoc, (float[])this.currentPG.lightNormal, 3, count);
            this.setUniformVector(this.lightAmbientLoc, (float[])this.currentPG.lightAmbient, 3, count);
            this.setUniformVector(this.lightDiffuseLoc, (float[])this.currentPG.lightDiffuse, 3, count);
            this.setUniformVector(this.lightSpecularLoc, (float[])this.currentPG.lightSpecular, 3, count);
            this.setUniformVector(this.lightFalloffLoc, (float[])this.currentPG.lightFalloffCoefficients, 3, count);
            this.setUniformVector(this.lightSpotLoc, (float[])this.currentPG.lightSpotParameters, 2, count);
        }

        if (-1 < this.directionLoc) {
            this.pgl.enableVertexAttribArray(this.directionLoc);
        }

        if (-1 < this.offsetLoc) {
            this.pgl.enableVertexAttribArray(this.offsetLoc);
        }

        if (-1 < this.perspectiveLoc) {
            if (this.currentPG.getHint(7) && this.currentPG.nonOrthoProjection()) {
                this.setUniformValue(this.perspectiveLoc, 1);
            } else {
                this.setUniformValue(this.perspectiveLoc, 0);
            }
        }

        if (-1 < this.scaleLoc) {
            if (this.currentPG.getHint(6)) {
                this.setUniformValue(this.scaleLoc, 1.0F, 1.0F, 1.0F);
            } else {
                float f = PGL.STROKE_DISPLACEMENT;
                if (this.currentPG.orthoProjection()) {
                    this.setUniformValue(this.scaleLoc, 1.0F, 1.0F, f);
                } else {
                    this.setUniformValue(this.scaleLoc, f, f, f);
                }
            }
        }

    }

    protected void unbindTyped() {
        if (-1 < this.offsetLoc) {
            this.pgl.disableVertexAttribArray(this.offsetLoc);
        }

        if (-1 < this.directionLoc) {
            this.pgl.disableVertexAttribArray(this.directionLoc);
        }

        if (-1 < this.textureLoc && this.texture != null) {
            this.pgl.activeTexture(PGL.TEXTURE0 + this.texUnit);
            this.texture.unbind();
            this.pgl.activeTexture(PGL.TEXTURE0);
            this.texture = null;
        }

        if (-1 < this.ambientLoc) {
            this.pgl.disableVertexAttribArray(this.ambientLoc);
        }

        if (-1 < this.specularLoc) {
            this.pgl.disableVertexAttribArray(this.specularLoc);
        }

        if (-1 < this.emissiveLoc) {
            this.pgl.disableVertexAttribArray(this.emissiveLoc);
        }

        if (-1 < this.shininessLoc) {
            this.pgl.disableVertexAttribArray(this.shininessLoc);
        }

        if (-1 < this.vertexLoc) {
            this.pgl.disableVertexAttribArray(this.vertexLoc);
        }

        if (-1 < this.colorLoc) {
            this.pgl.disableVertexAttribArray(this.colorLoc);
        }

        if (-1 < this.texCoordLoc) {
            this.pgl.disableVertexAttribArray(this.texCoordLoc);
        }

        if (-1 < this.normalLoc) {
            this.pgl.disableVertexAttribArray(this.normalLoc);
        }

        if (-1 < this.ppixelsLoc) {
            this.pgl.enableFBOLayer();
            this.pgl.activeTexture(PGL.TEXTURE0 + this.ppixelsUnit);
            this.currentPG.unbindFrontTexture();
            this.pgl.activeTexture(PGL.TEXTURE0);
        }

        this.pgl.bindBuffer(PGL.ARRAY_BUFFER, 0);
    }

    protected void setTexture(Texture tex) {
        this.texture = tex;
        float scaleu = 1.0F;
        float scalev = 1.0F;
        float dispu = 0.0F;
        float dispv = 0.0F;
        if (tex != null) {
            if (tex.invertedX()) {
                scaleu = -1.0F;
                dispu = 1.0F;
            }

            if (tex.invertedY()) {
                scalev = -1.0F;
                dispv = 1.0F;
            }

            scaleu *= tex.maxTexcoordU();
            dispu *= tex.maxTexcoordU();
            scalev *= tex.maxTexcoordV();
            dispv *= tex.maxTexcoordV();
            this.setUniformValue(this.texOffsetLoc, 1.0F / (float)tex.width, 1.0F / (float)tex.height);
            if (-1 < this.textureLoc) {
                this.texUnit = -1 < this.ppixelsUnit ? this.ppixelsUnit + 1 : this.getLastTexUnit() + 1;
                this.setUniformValue(this.textureLoc, this.texUnit);
                this.pgl.activeTexture(PGL.TEXTURE0 + this.texUnit);
                tex.bind();
            }
        }

        if (-1 < this.texMatrixLoc) {
            if (this.tcmat == null) {
                this.tcmat = new float[16];
            }

            this.tcmat[0] = scaleu;
            this.tcmat[4] = 0.0F;
            this.tcmat[8] = 0.0F;
            this.tcmat[12] = dispu;
            this.tcmat[1] = 0.0F;
            this.tcmat[5] = scalev;
            this.tcmat[9] = 0.0F;
            this.tcmat[13] = dispv;
            this.tcmat[2] = 0.0F;
            this.tcmat[6] = 0.0F;
            this.tcmat[10] = 0.0F;
            this.tcmat[14] = 0.0F;
            this.tcmat[3] = 0.0F;
            this.tcmat[7] = 0.0F;
            this.tcmat[11] = 0.0F;
            this.tcmat[15] = 0.0F;
            this.setUniformMatrix(this.texMatrixLoc, this.tcmat);
        }

    }

    protected boolean supportsTexturing() {
        return -1 < this.textureLoc;
    }

    protected boolean supportLighting() {
        return -1 < this.lightCountLoc || -1 < this.lightPositionLoc || -1 < this.lightNormalLoc;
    }

    protected boolean accessTexCoords() {
        return -1 < this.texCoordLoc;
    }

    protected boolean accessNormals() {
        return -1 < this.normalLoc;
    }

    protected boolean accessLightAttribs() {
        return -1 < this.ambientLoc || -1 < this.specularLoc || -1 < this.emissiveLoc || -1 < this.shininessLoc;
    }

    protected void setVertexAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.vertexLoc, vboId, size, type, false, stride, offset);
    }

    protected void setColorAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.colorLoc, vboId, size, type, true, stride, offset);
    }

    protected void setNormalAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.normalLoc, vboId, size, type, false, stride, offset);
    }

    protected void setTexcoordAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.texCoordLoc, vboId, size, type, false, stride, offset);
    }

    protected void setAmbientAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.ambientLoc, vboId, size, type, true, stride, offset);
    }

    protected void setSpecularAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.specularLoc, vboId, size, type, true, stride, offset);
    }

    protected void setEmissiveAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.emissiveLoc, vboId, size, type, true, stride, offset);
    }

    protected void setShininessAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.shininessLoc, vboId, size, type, false, stride, offset);
    }

    protected void setLineAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.directionLoc, vboId, size, type, false, stride, offset);
    }

    protected void setPointAttribute(int vboId, int size, int type, int stride, int offset) {
        this.setAttributeVBO(this.offsetLoc, vboId, size, type, false, stride, offset);
    }

    protected static class UniformValue {
        static final int INT1 = 0;
        static final int INT2 = 1;
        static final int INT3 = 2;
        static final int INT4 = 3;
        static final int FLOAT1 = 4;
        static final int FLOAT2 = 5;
        static final int FLOAT3 = 6;
        static final int FLOAT4 = 7;
        static final int INT1VEC = 8;
        static final int INT2VEC = 9;
        static final int INT3VEC = 10;
        static final int INT4VEC = 11;
        static final int FLOAT1VEC = 12;
        static final int FLOAT2VEC = 13;
        static final int FLOAT3VEC = 14;
        static final int FLOAT4VEC = 15;
        static final int MAT2 = 16;
        static final int MAT3 = 17;
        static final int MAT4 = 18;
        static final int SAMPLER2D = 19;
        int type;
        Object value;

        UniformValue(int type, Object value) {
            this.type = type;
            this.value = value;
        }
    }
}

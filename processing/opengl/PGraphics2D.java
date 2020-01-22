package processing.opengl;

import processing.core.PGraphics;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PShapeSVG;

public class PGraphics2D extends PGraphicsOpenGL {
    public PGraphics2D() {
    }

    public boolean is2D() {
        return true;
    }

    public boolean is3D() {
        return false;
    }

    public void hint(int which) {
        if (which == 7) {
            showWarning("Strokes cannot be perspective-corrected in 2D.");
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

    public void shape(PShape shape) {
        if (shape.is2D()) {
            super.shape(shape);
        } else {
            showWarning("The shape object is not 2D, cannot be displayed with this renderer");
        }

    }

    public void shape(PShape shape, float x, float y) {
        if (shape.is2D()) {
            super.shape(shape, x, y);
        } else {
            showWarning("The shape object is not 2D, cannot be displayed with this renderer");
        }

    }

    public void shape(PShape shape, float a, float b, float c, float d) {
        if (shape.is2D()) {
            super.shape(shape, a, b, c, d);
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

    public void bezierVertex(float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        showDepthWarningXYZ("bezierVertex");
    }

    public void quadraticVertex(float x2, float y2, float z2, float x4, float y4, float z4) {
        showDepthWarningXYZ("quadVertex");
    }

    public void curveVertex(float x, float y, float z) {
        showDepthWarningXYZ("curveVertex");
    }

    public void box(float w, float h, float d) {
        showMethodWarning("box");
    }

    public void sphere(float r) {
        showMethodWarning("sphere");
    }

    public void vertex(float x, float y, float z) {
        showDepthWarningXYZ("vertex");
    }

    public void vertex(float x, float y, float z, float u, float v) {
        showDepthWarningXYZ("vertex");
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

    public PMatrix2D getMatrix(PMatrix2D target) {
        if (target == null) {
            target = new PMatrix2D();
        }

        target.set(this.modelview.m00, this.modelview.m01, this.modelview.m03, this.modelview.m10, this.modelview.m11, this.modelview.m13);
        return target;
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
}

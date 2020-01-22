package processing.opengl;

import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PShapeOBJ;

public class PGraphics3D extends PGraphicsOpenGL {
    public PGraphics3D() {
    }

    public boolean is2D() {
        return false;
    }

    public boolean is3D() {
        return true;
    }

    protected void defaultPerspective() {
        this.perspective();
    }

    protected void defaultCamera() {
        this.camera();
    }

    protected void begin2D() {
        this.pushProjection();
        this.ortho((float)(-this.width) / 2.0F, (float)this.width / 2.0F, (float)(-this.height) / 2.0F, (float)this.height / 2.0F);
        this.pushMatrix();
        float centerX = (float)this.width / 2.0F;
        float centerY = (float)this.height / 2.0F;
        this.modelview.reset();
        this.modelview.translate(-centerX, -centerY);
        this.modelviewInv.set(this.modelview);
        this.modelviewInv.invert();
        this.camera.set(this.modelview);
        this.cameraInv.set(this.modelviewInv);
        this.updateProjmodelview();
    }

    protected void end2D() {
        this.popMatrix();
        this.popProjection();
    }

    protected static boolean isSupportedExtension(String extension) {
        return extension.equals("obj");
    }

    protected static PShape loadShapeImpl(PGraphics pg, String filename, String extension) {
        PShapeOBJ obj = null;
        if (extension.equals("obj")) {
            obj = new PShapeOBJ(pg.parent, filename);
            int prevTextureMode = pg.textureMode;
            pg.textureMode = 1;
            PShapeOpenGL p3d = PShapeOpenGL.createShape((PGraphicsOpenGL)pg, obj);
            pg.textureMode = prevTextureMode;
            return p3d;
        } else {
            return null;
        }
    }
}

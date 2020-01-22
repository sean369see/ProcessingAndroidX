package processing.a2d;

import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import processing.core.PGraphics;
import processing.core.PShapeSVG;
import processing.core.PShapeSVG.Gradient;
import processing.core.PShapeSVG.LinearGradient;
import processing.core.PShapeSVG.RadialGradient;
import processing.data.XML;

public class PShapeAndroid2D extends PShapeSVG {
    protected Shader strokeGradientPaint;
    protected Shader fillGradientPaint;

    public PShapeAndroid2D(XML svg) {
        super(svg);
    }

    public PShapeAndroid2D(PShapeSVG parent, XML properties, boolean parseKids) {
        super(parent, properties, parseKids);
    }

    protected void setParent(PShapeSVG parent) {
        super.setParent(parent);
        if (parent instanceof PShapeAndroid2D) {
            PShapeAndroid2D pj = (PShapeAndroid2D)parent;
            this.fillGradientPaint = pj.fillGradientPaint;
            this.strokeGradientPaint = pj.strokeGradientPaint;
        } else {
            this.fillGradientPaint = null;
            this.strokeGradientPaint = null;
        }

    }

    protected PShapeSVG createShape(PShapeSVG parent, XML properties, boolean parseKids) {
        return new PShapeAndroid2D(parent, properties, parseKids);
    }

    protected Shader calcGradientPaint(Gradient gradient) {
        int[] colors = new int[gradient.count];
        int opacityMask = (int)(this.opacity * 255.0F) << 24;

        for(int i = 0; i < gradient.count; ++i) {
            colors[i] = opacityMask | gradient.color[i] & 16777215;
        }

        if (gradient instanceof LinearGradient) {
            LinearGradient grad = (LinearGradient)gradient;
            return new android.graphics.LinearGradient(grad.x1, grad.y1, grad.x2, grad.y2, colors, grad.offset, TileMode.CLAMP);
        } else if (gradient instanceof RadialGradient) {
            RadialGradient grad = (RadialGradient)gradient;
            return new android.graphics.RadialGradient(grad.cx, grad.cy, grad.r, colors, grad.offset, TileMode.CLAMP);
        } else {
            return null;
        }
    }

    protected void styles(PGraphics g) {
        super.styles(g);
        if (g instanceof PGraphicsAndroid2D) {
            PGraphicsAndroid2D gg = (PGraphicsAndroid2D)g;
            if (this.strokeGradient != null) {
                if (this.strokeGradientPaint == null) {
                    this.strokeGradientPaint = this.calcGradientPaint(this.strokeGradient);
                }

                gg.strokePaint.setShader(this.strokeGradientPaint);
            }

            if (this.fillGradient != null) {
                if (this.fillGradientPaint == null) {
                    this.fillGradientPaint = this.calcGradientPaint(this.fillGradient);
                }

                gg.fillPaint.setShader(this.fillGradientPaint);
            }
        }

    }
}

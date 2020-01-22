package processing.core;

public class PMatrix2D implements PMatrix {
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;

    public PMatrix2D() {
        this.reset();
    }

    public PMatrix2D(float m00, float m01, float m02, float m10, float m11, float m12) {
        this.set(m00, m01, m02, m10, m11, m12);
    }

    public PMatrix2D(PMatrix matrix) {
        this.set(matrix);
    }

    public void reset() {
        this.set(1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
    }

    public PMatrix2D get() {
        PMatrix2D outgoing = new PMatrix2D();
        outgoing.set((PMatrix)this);
        return outgoing;
    }

    public float[] get(float[] target) {
        if (target == null || target.length != 6) {
            target = new float[6];
        }

        target[0] = this.m00;
        target[1] = this.m01;
        target[2] = this.m02;
        target[3] = this.m10;
        target[4] = this.m11;
        target[5] = this.m12;
        return target;
    }

    public void set(PMatrix matrix) {
        if (matrix instanceof PMatrix2D) {
            PMatrix2D src = (PMatrix2D)matrix;
            this.set(src.m00, src.m01, src.m02, src.m10, src.m11, src.m12);
        } else {
            throw new IllegalArgumentException("PMatrix2D.set() only accepts PMatrix2D objects.");
        }
    }

    public void set(PMatrix3D src) {
    }

    public void set(float[] source) {
        this.m00 = source[0];
        this.m01 = source[1];
        this.m02 = source[2];
        this.m10 = source[3];
        this.m11 = source[4];
        this.m12 = source[5];
    }

    public void set(float m00, float m01, float m02, float m10, float m11, float m12) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
    }

    public void set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
    }

    public void translate(float tx, float ty) {
        this.m02 += tx * this.m00 + ty * this.m01;
        this.m12 += tx * this.m10 + ty * this.m11;
    }

    public void translate(float x, float y, float z) {
        throw new IllegalArgumentException("Cannot use translate(x, y, z) on a PMatrix2D.");
    }

    public void rotate(float angle) {
        float s = this.sin(angle);
        float c = this.cos(angle);
        float temp1 = this.m00;
        float temp2 = this.m01;
        this.m00 = c * temp1 + s * temp2;
        this.m01 = -s * temp1 + c * temp2;
        temp1 = this.m10;
        temp2 = this.m11;
        this.m10 = c * temp1 + s * temp2;
        this.m11 = -s * temp1 + c * temp2;
    }

    public void rotateX(float angle) {
        throw new IllegalArgumentException("Cannot use rotateX() on a PMatrix2D.");
    }

    public void rotateY(float angle) {
        throw new IllegalArgumentException("Cannot use rotateY() on a PMatrix2D.");
    }

    public void rotateZ(float angle) {
        this.rotate(angle);
    }

    public void rotate(float angle, float v0, float v1, float v2) {
        throw new IllegalArgumentException("Cannot use this version of rotate() on a PMatrix2D.");
    }

    public void scale(float s) {
        this.scale(s, s);
    }

    public void scale(float sx, float sy) {
        this.m00 *= sx;
        this.m01 *= sy;
        this.m10 *= sx;
        this.m11 *= sy;
    }

    public void scale(float x, float y, float z) {
        throw new IllegalArgumentException("Cannot use this version of scale() on a PMatrix2D.");
    }

    public void shearX(float angle) {
        this.apply(1.0F, 0.0F, 1.0F, this.tan(angle), 0.0F, 0.0F);
    }

    public void shearY(float angle) {
        this.apply(1.0F, 0.0F, 1.0F, 0.0F, this.tan(angle), 0.0F);
    }

    public void apply(PMatrix source) {
        if (source instanceof PMatrix2D) {
            this.apply((PMatrix2D)source);
        } else if (source instanceof PMatrix3D) {
            this.apply((PMatrix3D)source);
        }

    }

    public void apply(PMatrix2D source) {
        this.apply(source.m00, source.m01, source.m02, source.m10, source.m11, source.m12);
    }

    public void apply(PMatrix3D source) {
        throw new IllegalArgumentException("Cannot use apply(PMatrix3D) on a PMatrix2D.");
    }

    public void apply(float n00, float n01, float n02, float n10, float n11, float n12) {
        float t0 = this.m00;
        float t1 = this.m01;
        this.m00 = n00 * t0 + n10 * t1;
        this.m01 = n01 * t0 + n11 * t1;
        this.m02 += n02 * t0 + n12 * t1;
        t0 = this.m10;
        t1 = this.m11;
        this.m10 = n00 * t0 + n10 * t1;
        this.m11 = n01 * t0 + n11 * t1;
        this.m12 += n02 * t0 + n12 * t1;
    }

    public void apply(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        throw new IllegalArgumentException("Cannot use this version of apply() on a PMatrix2D.");
    }

    public void preApply(PMatrix source) {
        if (source instanceof PMatrix2D) {
            this.preApply((PMatrix2D)source);
        } else if (source instanceof PMatrix3D) {
            this.preApply((PMatrix3D)source);
        }

    }

    public void preApply(PMatrix2D left) {
        this.preApply(left.m00, left.m01, left.m02, left.m10, left.m11, left.m12);
    }

    public void preApply(PMatrix3D left) {
        throw new IllegalArgumentException("Cannot use preApply(PMatrix3D) on a PMatrix2D.");
    }

    public void preApply(float n00, float n01, float n02, float n10, float n11, float n12) {
        float t0 = this.m02;
        float t1 = this.m12;
        n02 += t0 * n00 + t1 * n01;
        n12 += t0 * n10 + t1 * n11;
        this.m02 = n02;
        this.m12 = n12;
        t0 = this.m00;
        t1 = this.m10;
        this.m00 = t0 * n00 + t1 * n01;
        this.m10 = t0 * n10 + t1 * n11;
        t0 = this.m01;
        t1 = this.m11;
        this.m01 = t0 * n00 + t1 * n01;
        this.m11 = t0 * n10 + t1 * n11;
    }

    public void preApply(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        throw new IllegalArgumentException("Cannot use this version of preApply() on a PMatrix2D.");
    }

    public PVector mult(PVector source, PVector target) {
        if (target == null) {
            target = new PVector();
        }

        target.x = this.m00 * source.x + this.m01 * source.y + this.m02;
        target.y = this.m10 * source.x + this.m11 * source.y + this.m12;
        return target;
    }

    public float[] mult(float[] vec, float[] out) {
        if (out == null || out.length != 2) {
            out = new float[2];
        }

        if (vec == out) {
            float tx = this.m00 * vec[0] + this.m01 * vec[1] + this.m02;
            float ty = this.m10 * vec[0] + this.m11 * vec[1] + this.m12;
            out[0] = tx;
            out[1] = ty;
        } else {
            out[0] = this.m00 * vec[0] + this.m01 * vec[1] + this.m02;
            out[1] = this.m10 * vec[0] + this.m11 * vec[1] + this.m12;
        }

        return out;
    }

    public float multX(float x, float y) {
        return this.m00 * x + this.m01 * y + this.m02;
    }

    public float multY(float x, float y) {
        return this.m10 * x + this.m11 * y + this.m12;
    }

    public void transpose() {
    }

    public boolean invert() {
        float determinant = this.determinant();
        if (Math.abs(determinant) <= 1.4E-45F) {
            return false;
        } else {
            float t00 = this.m00;
            float t01 = this.m01;
            float t02 = this.m02;
            float t10 = this.m10;
            float t11 = this.m11;
            float t12 = this.m12;
            this.m00 = t11 / determinant;
            this.m10 = -t10 / determinant;
            this.m01 = -t01 / determinant;
            this.m11 = t00 / determinant;
            this.m02 = (t01 * t12 - t11 * t02) / determinant;
            this.m12 = (t10 * t02 - t00 * t12) / determinant;
            return true;
        }
    }

    public float determinant() {
        return this.m00 * this.m11 - this.m01 * this.m10;
    }

    public void print() {
        int big = (int)this.abs(this.max(PApplet.max(this.abs(this.m00), this.abs(this.m01), this.abs(this.m02)), PApplet.max(this.abs(this.m10), this.abs(this.m11), this.abs(this.m12))));
        int digits = 1;
        if (!Float.isNaN((float)big) && !Float.isInfinite((float)big)) {
            while((big /= 10) != 0) {
                ++digits;
            }
        } else {
            digits = 5;
        }

        System.out.println(PApplet.nfs(this.m00, digits, 4) + " " + PApplet.nfs(this.m01, digits, 4) + " " + PApplet.nfs(this.m02, digits, 4));
        System.out.println(PApplet.nfs(this.m10, digits, 4) + " " + PApplet.nfs(this.m11, digits, 4) + " " + PApplet.nfs(this.m12, digits, 4));
        System.out.println();
    }

    protected boolean isIdentity() {
        return this.m00 == 1.0F && this.m01 == 0.0F && this.m02 == 0.0F && this.m10 == 0.0F && this.m11 == 1.0F && this.m12 == 0.0F;
    }

    protected boolean isWarped() {
        return this.m00 != 1.0F || this.m01 != 0.0F || this.m10 != 0.0F || this.m11 != 1.0F;
    }

    private final float max(float a, float b) {
        return a > b ? a : b;
    }

    private final float abs(float a) {
        return a < 0.0F ? -a : a;
    }

    private final float sin(float angle) {
        return (float)Math.sin((double)angle);
    }

    private final float cos(float angle) {
        return (float)Math.cos((double)angle);
    }

    private final float tan(float angle) {
        return (float)Math.tan((double)angle);
    }
}

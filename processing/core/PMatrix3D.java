package processing.core;

public final class PMatrix3D implements PMatrix {
    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;
    protected PMatrix3D inverseCopy;

    public PMatrix3D() {
        this.reset();
    }

    public PMatrix3D(float m00, float m01, float m02, float m10, float m11, float m12) {
        this.set(m00, m01, m02, 0.0F, m10, m11, m12, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public PMatrix3D(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        this.set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
    }

    public PMatrix3D(PMatrix matrix) {
        this.set(matrix);
    }

    public void reset() {
        this.set(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public PMatrix3D get() {
        PMatrix3D outgoing = new PMatrix3D();
        outgoing.set((PMatrix)this);
        return outgoing;
    }

    public float[] get(float[] target) {
        if (target == null || target.length != 16) {
            target = new float[16];
        }

        target[0] = this.m00;
        target[1] = this.m01;
        target[2] = this.m02;
        target[3] = this.m03;
        target[4] = this.m10;
        target[5] = this.m11;
        target[6] = this.m12;
        target[7] = this.m13;
        target[8] = this.m20;
        target[9] = this.m21;
        target[10] = this.m22;
        target[11] = this.m23;
        target[12] = this.m30;
        target[13] = this.m31;
        target[14] = this.m32;
        target[15] = this.m33;
        return target;
    }

    public void set(PMatrix matrix) {
        if (matrix instanceof PMatrix3D) {
            PMatrix3D src = (PMatrix3D)matrix;
            this.set(src.m00, src.m01, src.m02, src.m03, src.m10, src.m11, src.m12, src.m13, src.m20, src.m21, src.m22, src.m23, src.m30, src.m31, src.m32, src.m33);
        } else {
            PMatrix2D src = (PMatrix2D)matrix;
            this.set(src.m00, src.m01, 0.0F, src.m02, src.m10, src.m11, 0.0F, src.m12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
        }

    }

    public void set(float[] source) {
        if (source.length == 6) {
            this.set(source[0], source[1], source[2], source[3], source[4], source[5]);
        } else if (source.length == 16) {
            this.m00 = source[0];
            this.m01 = source[1];
            this.m02 = source[2];
            this.m03 = source[3];
            this.m10 = source[4];
            this.m11 = source[5];
            this.m12 = source[6];
            this.m13 = source[7];
            this.m20 = source[8];
            this.m21 = source[9];
            this.m22 = source[10];
            this.m23 = source[11];
            this.m30 = source[12];
            this.m31 = source[13];
            this.m32 = source[14];
            this.m33 = source[15];
        }

    }

    public void set(float m00, float m01, float m02, float m10, float m11, float m12) {
        this.set(m00, m01, 0.0F, m02, m10, m11, 0.0F, m12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void set(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public void translate(float tx, float ty) {
        this.translate(tx, ty, 0.0F);
    }

    public void translate(float tx, float ty, float tz) {
        this.m03 += tx * this.m00 + ty * this.m01 + tz * this.m02;
        this.m13 += tx * this.m10 + ty * this.m11 + tz * this.m12;
        this.m23 += tx * this.m20 + ty * this.m21 + tz * this.m22;
        this.m33 += tx * this.m30 + ty * this.m31 + tz * this.m32;
    }

    public void rotate(float angle) {
        this.rotateZ(angle);
    }

    public void rotateX(float angle) {
        float c = cos(angle);
        float s = sin(angle);
        this.apply(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, c, -s, 0.0F, 0.0F, s, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void rotateY(float angle) {
        float c = cos(angle);
        float s = sin(angle);
        this.apply(c, 0.0F, s, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -s, 0.0F, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void rotateZ(float angle) {
        float c = cos(angle);
        float s = sin(angle);
        this.apply(c, -s, 0.0F, 0.0F, s, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void rotate(float angle, float v0, float v1, float v2) {
        float norm2 = v0 * v0 + v1 * v1 + v2 * v2;
        if (norm2 >= 1.0E-4F) {
            float c;
            if (Math.abs(norm2 - 1.0F) > 1.0E-4F) {
                c = PApplet.sqrt(norm2);
                v0 /= c;
                v1 /= c;
                v2 /= c;
            }

            c = cos(angle);
            float s = sin(angle);
            float t = 1.0F - c;
            this.apply(t * v0 * v0 + c, t * v0 * v1 - s * v2, t * v0 * v2 + s * v1, 0.0F, t * v0 * v1 + s * v2, t * v1 * v1 + c, t * v1 * v2 - s * v0, 0.0F, t * v0 * v2 - s * v1, t * v1 * v2 + s * v0, t * v2 * v2 + c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    public void scale(float s) {
        this.scale(s, s, s);
    }

    public void scale(float sx, float sy) {
        this.scale(sx, sy, 1.0F);
    }

    public void scale(float x, float y, float z) {
        this.m00 *= x;
        this.m01 *= y;
        this.m02 *= z;
        this.m10 *= x;
        this.m11 *= y;
        this.m12 *= z;
        this.m20 *= x;
        this.m21 *= y;
        this.m22 *= z;
        this.m30 *= x;
        this.m31 *= y;
        this.m32 *= z;
    }

    public void shearX(float angle) {
        float t = (float)Math.tan((double)angle);
        this.apply(1.0F, t, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void shearY(float angle) {
        float t = (float)Math.tan((double)angle);
        this.apply(1.0F, 0.0F, 0.0F, 0.0F, t, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void apply(PMatrix source) {
        if (source instanceof PMatrix2D) {
            this.apply((PMatrix2D)source);
        } else if (source instanceof PMatrix3D) {
            this.apply((PMatrix3D)source);
        }

    }

    public void apply(PMatrix2D source) {
        this.apply(source.m00, source.m01, 0.0F, source.m02, source.m10, source.m11, 0.0F, source.m12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void apply(PMatrix3D source) {
        this.apply(source.m00, source.m01, source.m02, source.m03, source.m10, source.m11, source.m12, source.m13, source.m20, source.m21, source.m22, source.m23, source.m30, source.m31, source.m32, source.m33);
    }

    public void apply(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.apply(n00, n01, 0.0F, n02, n10, n11, 0.0F, n12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void apply(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        float r00 = this.m00 * n00 + this.m01 * n10 + this.m02 * n20 + this.m03 * n30;
        float r01 = this.m00 * n01 + this.m01 * n11 + this.m02 * n21 + this.m03 * n31;
        float r02 = this.m00 * n02 + this.m01 * n12 + this.m02 * n22 + this.m03 * n32;
        float r03 = this.m00 * n03 + this.m01 * n13 + this.m02 * n23 + this.m03 * n33;
        float r10 = this.m10 * n00 + this.m11 * n10 + this.m12 * n20 + this.m13 * n30;
        float r11 = this.m10 * n01 + this.m11 * n11 + this.m12 * n21 + this.m13 * n31;
        float r12 = this.m10 * n02 + this.m11 * n12 + this.m12 * n22 + this.m13 * n32;
        float r13 = this.m10 * n03 + this.m11 * n13 + this.m12 * n23 + this.m13 * n33;
        float r20 = this.m20 * n00 + this.m21 * n10 + this.m22 * n20 + this.m23 * n30;
        float r21 = this.m20 * n01 + this.m21 * n11 + this.m22 * n21 + this.m23 * n31;
        float r22 = this.m20 * n02 + this.m21 * n12 + this.m22 * n22 + this.m23 * n32;
        float r23 = this.m20 * n03 + this.m21 * n13 + this.m22 * n23 + this.m23 * n33;
        float r30 = this.m30 * n00 + this.m31 * n10 + this.m32 * n20 + this.m33 * n30;
        float r31 = this.m30 * n01 + this.m31 * n11 + this.m32 * n21 + this.m33 * n31;
        float r32 = this.m30 * n02 + this.m31 * n12 + this.m32 * n22 + this.m33 * n32;
        float r33 = this.m30 * n03 + this.m31 * n13 + this.m32 * n23 + this.m33 * n33;
        this.m00 = r00;
        this.m01 = r01;
        this.m02 = r02;
        this.m03 = r03;
        this.m10 = r10;
        this.m11 = r11;
        this.m12 = r12;
        this.m13 = r13;
        this.m20 = r20;
        this.m21 = r21;
        this.m22 = r22;
        this.m23 = r23;
        this.m30 = r30;
        this.m31 = r31;
        this.m32 = r32;
        this.m33 = r33;
    }

    public void preApply(PMatrix source) {
        if (source instanceof PMatrix2D) {
            this.preApply((PMatrix2D)source);
        } else if (source instanceof PMatrix3D) {
            this.preApply((PMatrix3D)source);
        }

    }

    public void preApply(PMatrix2D left) {
        this.preApply(left.m00, left.m01, 0.0F, left.m02, left.m10, left.m11, 0.0F, left.m12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void preApply(PMatrix3D left) {
        this.preApply(left.m00, left.m01, left.m02, left.m03, left.m10, left.m11, left.m12, left.m13, left.m20, left.m21, left.m22, left.m23, left.m30, left.m31, left.m32, left.m33);
    }

    public void preApply(float n00, float n01, float n02, float n10, float n11, float n12) {
        this.preApply(n00, n01, 0.0F, n02, n10, n11, 0.0F, n12, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    public void preApply(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        float r00 = n00 * this.m00 + n01 * this.m10 + n02 * this.m20 + n03 * this.m30;
        float r01 = n00 * this.m01 + n01 * this.m11 + n02 * this.m21 + n03 * this.m31;
        float r02 = n00 * this.m02 + n01 * this.m12 + n02 * this.m22 + n03 * this.m32;
        float r03 = n00 * this.m03 + n01 * this.m13 + n02 * this.m23 + n03 * this.m33;
        float r10 = n10 * this.m00 + n11 * this.m10 + n12 * this.m20 + n13 * this.m30;
        float r11 = n10 * this.m01 + n11 * this.m11 + n12 * this.m21 + n13 * this.m31;
        float r12 = n10 * this.m02 + n11 * this.m12 + n12 * this.m22 + n13 * this.m32;
        float r13 = n10 * this.m03 + n11 * this.m13 + n12 * this.m23 + n13 * this.m33;
        float r20 = n20 * this.m00 + n21 * this.m10 + n22 * this.m20 + n23 * this.m30;
        float r21 = n20 * this.m01 + n21 * this.m11 + n22 * this.m21 + n23 * this.m31;
        float r22 = n20 * this.m02 + n21 * this.m12 + n22 * this.m22 + n23 * this.m32;
        float r23 = n20 * this.m03 + n21 * this.m13 + n22 * this.m23 + n23 * this.m33;
        float r30 = n30 * this.m00 + n31 * this.m10 + n32 * this.m20 + n33 * this.m30;
        float r31 = n30 * this.m01 + n31 * this.m11 + n32 * this.m21 + n33 * this.m31;
        float r32 = n30 * this.m02 + n31 * this.m12 + n32 * this.m22 + n33 * this.m32;
        float r33 = n30 * this.m03 + n31 * this.m13 + n32 * this.m23 + n33 * this.m33;
        this.m00 = r00;
        this.m01 = r01;
        this.m02 = r02;
        this.m03 = r03;
        this.m10 = r10;
        this.m11 = r11;
        this.m12 = r12;
        this.m13 = r13;
        this.m20 = r20;
        this.m21 = r21;
        this.m22 = r22;
        this.m23 = r23;
        this.m30 = r30;
        this.m31 = r31;
        this.m32 = r32;
        this.m33 = r33;
    }

    public PVector mult(PVector source, PVector target) {
        if (target == null) {
            target = new PVector();
        }

        target.set(this.m00 * source.x + this.m01 * source.y + this.m02 * source.z + this.m03, this.m10 * source.x + this.m11 * source.y + this.m12 * source.z + this.m13, this.m20 * source.x + this.m21 * source.y + this.m22 * source.z + this.m23);
        return target;
    }

    public float[] mult(float[] source, float[] target) {
        if (target == null || target.length < 3) {
            target = new float[3];
        }

        if (source == target) {
            throw new RuntimeException("The source and target vectors used in PMatrix3D.mult() cannot be identical.");
        } else {
            if (target.length == 3) {
                target[0] = this.m00 * source[0] + this.m01 * source[1] + this.m02 * source[2] + this.m03;
                target[1] = this.m10 * source[0] + this.m11 * source[1] + this.m12 * source[2] + this.m13;
                target[2] = this.m20 * source[0] + this.m21 * source[1] + this.m22 * source[2] + this.m23;
            } else if (target.length > 3) {
                target[0] = this.m00 * source[0] + this.m01 * source[1] + this.m02 * source[2] + this.m03 * source[3];
                target[1] = this.m10 * source[0] + this.m11 * source[1] + this.m12 * source[2] + this.m13 * source[3];
                target[2] = this.m20 * source[0] + this.m21 * source[1] + this.m22 * source[2] + this.m23 * source[3];
                target[3] = this.m30 * source[0] + this.m31 * source[1] + this.m32 * source[2] + this.m33 * source[3];
            }

            return target;
        }
    }

    public float multX(float x, float y) {
        return this.m00 * x + this.m01 * y + this.m03;
    }

    public float multY(float x, float y) {
        return this.m10 * x + this.m11 * y + this.m13;
    }

    public float multX(float x, float y, float z) {
        return this.m00 * x + this.m01 * y + this.m02 * z + this.m03;
    }

    public float multY(float x, float y, float z) {
        return this.m10 * x + this.m11 * y + this.m12 * z + this.m13;
    }

    public float multZ(float x, float y, float z) {
        return this.m20 * x + this.m21 * y + this.m22 * z + this.m23;
    }

    public float multW(float x, float y, float z) {
        return this.m30 * x + this.m31 * y + this.m32 * z + this.m33;
    }

    public float multX(float x, float y, float z, float w) {
        return this.m00 * x + this.m01 * y + this.m02 * z + this.m03 * w;
    }

    public float multY(float x, float y, float z, float w) {
        return this.m10 * x + this.m11 * y + this.m12 * z + this.m13 * w;
    }

    public float multZ(float x, float y, float z, float w) {
        return this.m20 * x + this.m21 * y + this.m22 * z + this.m23 * w;
    }

    public float multW(float x, float y, float z, float w) {
        return this.m30 * x + this.m31 * y + this.m32 * z + this.m33 * w;
    }

    public void transpose() {
        float temp = this.m01;
        this.m01 = this.m10;
        this.m10 = temp;
        temp = this.m02;
        this.m02 = this.m20;
        this.m20 = temp;
        temp = this.m03;
        this.m03 = this.m30;
        this.m30 = temp;
        temp = this.m12;
        this.m12 = this.m21;
        this.m21 = temp;
        temp = this.m13;
        this.m13 = this.m31;
        this.m31 = temp;
        temp = this.m23;
        this.m23 = this.m32;
        this.m32 = temp;
    }

    public boolean invert() {
        float determinant = this.determinant();
        if (determinant == 0.0F) {
            return false;
        } else {
            float t00 = this.determinant3x3(this.m11, this.m12, this.m13, this.m21, this.m22, this.m23, this.m31, this.m32, this.m33);
            float t01 = -this.determinant3x3(this.m10, this.m12, this.m13, this.m20, this.m22, this.m23, this.m30, this.m32, this.m33);
            float t02 = this.determinant3x3(this.m10, this.m11, this.m13, this.m20, this.m21, this.m23, this.m30, this.m31, this.m33);
            float t03 = -this.determinant3x3(this.m10, this.m11, this.m12, this.m20, this.m21, this.m22, this.m30, this.m31, this.m32);
            float t10 = -this.determinant3x3(this.m01, this.m02, this.m03, this.m21, this.m22, this.m23, this.m31, this.m32, this.m33);
            float t11 = this.determinant3x3(this.m00, this.m02, this.m03, this.m20, this.m22, this.m23, this.m30, this.m32, this.m33);
            float t12 = -this.determinant3x3(this.m00, this.m01, this.m03, this.m20, this.m21, this.m23, this.m30, this.m31, this.m33);
            float t13 = this.determinant3x3(this.m00, this.m01, this.m02, this.m20, this.m21, this.m22, this.m30, this.m31, this.m32);
            float t20 = this.determinant3x3(this.m01, this.m02, this.m03, this.m11, this.m12, this.m13, this.m31, this.m32, this.m33);
            float t21 = -this.determinant3x3(this.m00, this.m02, this.m03, this.m10, this.m12, this.m13, this.m30, this.m32, this.m33);
            float t22 = this.determinant3x3(this.m00, this.m01, this.m03, this.m10, this.m11, this.m13, this.m30, this.m31, this.m33);
            float t23 = -this.determinant3x3(this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m30, this.m31, this.m32);
            float t30 = -this.determinant3x3(this.m01, this.m02, this.m03, this.m11, this.m12, this.m13, this.m21, this.m22, this.m23);
            float t31 = this.determinant3x3(this.m00, this.m02, this.m03, this.m10, this.m12, this.m13, this.m20, this.m22, this.m23);
            float t32 = -this.determinant3x3(this.m00, this.m01, this.m03, this.m10, this.m11, this.m13, this.m20, this.m21, this.m23);
            float t33 = this.determinant3x3(this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22);
            this.m00 = t00 / determinant;
            this.m01 = t10 / determinant;
            this.m02 = t20 / determinant;
            this.m03 = t30 / determinant;
            this.m10 = t01 / determinant;
            this.m11 = t11 / determinant;
            this.m12 = t21 / determinant;
            this.m13 = t31 / determinant;
            this.m20 = t02 / determinant;
            this.m21 = t12 / determinant;
            this.m22 = t22 / determinant;
            this.m23 = t32 / determinant;
            this.m30 = t03 / determinant;
            this.m31 = t13 / determinant;
            this.m32 = t23 / determinant;
            this.m33 = t33 / determinant;
            return true;
        }
    }

    private float determinant3x3(float t00, float t01, float t02, float t10, float t11, float t12, float t20, float t21, float t22) {
        return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
    }

    public float determinant() {
        float f = this.m00 * (this.m11 * this.m22 * this.m33 + this.m12 * this.m23 * this.m31 + this.m13 * this.m21 * this.m32 - this.m13 * this.m22 * this.m31 - this.m11 * this.m23 * this.m32 - this.m12 * this.m21 * this.m33);
        f -= this.m01 * (this.m10 * this.m22 * this.m33 + this.m12 * this.m23 * this.m30 + this.m13 * this.m20 * this.m32 - this.m13 * this.m22 * this.m30 - this.m10 * this.m23 * this.m32 - this.m12 * this.m20 * this.m33);
        f += this.m02 * (this.m10 * this.m21 * this.m33 + this.m11 * this.m23 * this.m30 + this.m13 * this.m20 * this.m31 - this.m13 * this.m21 * this.m30 - this.m10 * this.m23 * this.m31 - this.m11 * this.m20 * this.m33);
        f -= this.m03 * (this.m10 * this.m21 * this.m32 + this.m11 * this.m22 * this.m30 + this.m12 * this.m20 * this.m31 - this.m12 * this.m21 * this.m30 - this.m10 * this.m22 * this.m31 - this.m11 * this.m20 * this.m32);
        return f;
    }

    protected void invTranslate(float tx, float ty, float tz) {
        this.preApply(1.0F, 0.0F, 0.0F, -tx, 0.0F, 1.0F, 0.0F, -ty, 0.0F, 0.0F, 1.0F, -tz, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected void invRotateX(float angle) {
        float c = cos(-angle);
        float s = sin(-angle);
        this.preApply(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, c, -s, 0.0F, 0.0F, s, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected void invRotateY(float angle) {
        float c = cos(-angle);
        float s = sin(-angle);
        this.preApply(c, 0.0F, s, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -s, 0.0F, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected void invRotateZ(float angle) {
        float c = cos(-angle);
        float s = sin(-angle);
        this.preApply(c, -s, 0.0F, 0.0F, s, c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected void invRotate(float angle, float v0, float v1, float v2) {
        float c = cos(-angle);
        float s = sin(-angle);
        float t = 1.0F - c;
        this.preApply(t * v0 * v0 + c, t * v0 * v1 - s * v2, t * v0 * v2 + s * v1, 0.0F, t * v0 * v1 + s * v2, t * v1 * v1 + c, t * v1 * v2 - s * v0, 0.0F, t * v0 * v2 - s * v1, t * v1 * v2 + s * v0, t * v2 * v2 + c, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected void invScale(float x, float y, float z) {
        this.preApply(1.0F / x, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / y, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
    }

    protected boolean invApply(float n00, float n01, float n02, float n03, float n10, float n11, float n12, float n13, float n20, float n21, float n22, float n23, float n30, float n31, float n32, float n33) {
        if (this.inverseCopy == null) {
            this.inverseCopy = new PMatrix3D();
        }

        this.inverseCopy.set(n00, n01, n02, n03, n10, n11, n12, n13, n20, n21, n22, n23, n30, n31, n32, n33);
        if (!this.inverseCopy.invert()) {
            return false;
        } else {
            this.preApply(this.inverseCopy);
            return true;
        }
    }

    public void print() {
        int big = (int)Math.abs(max(max(max(max(abs(this.m00), abs(this.m01)), max(abs(this.m02), abs(this.m03))), max(max(abs(this.m10), abs(this.m11)), max(abs(this.m12), abs(this.m13)))), max(max(max(abs(this.m20), abs(this.m21)), max(abs(this.m22), abs(this.m23))), max(max(abs(this.m30), abs(this.m31)), max(abs(this.m32), abs(this.m33))))));
        int digits = 1;
        if (!Float.isNaN((float)big) && !Float.isInfinite((float)big)) {
            while((big /= 10) != 0) {
                ++digits;
            }
        } else {
            digits = 5;
        }

        System.out.println(PApplet.nfs(this.m00, digits, 4) + " " + PApplet.nfs(this.m01, digits, 4) + " " + PApplet.nfs(this.m02, digits, 4) + " " + PApplet.nfs(this.m03, digits, 4));
        System.out.println(PApplet.nfs(this.m10, digits, 4) + " " + PApplet.nfs(this.m11, digits, 4) + " " + PApplet.nfs(this.m12, digits, 4) + " " + PApplet.nfs(this.m13, digits, 4));
        System.out.println(PApplet.nfs(this.m20, digits, 4) + " " + PApplet.nfs(this.m21, digits, 4) + " " + PApplet.nfs(this.m22, digits, 4) + " " + PApplet.nfs(this.m23, digits, 4));
        System.out.println(PApplet.nfs(this.m30, digits, 4) + " " + PApplet.nfs(this.m31, digits, 4) + " " + PApplet.nfs(this.m32, digits, 4) + " " + PApplet.nfs(this.m33, digits, 4));
        System.out.println();
    }

    private static final float max(float a, float b) {
        return a > b ? a : b;
    }

    private static final float abs(float a) {
        return a < 0.0F ? -a : a;
    }

    private static final float sin(float angle) {
        return (float)Math.sin((double)angle);
    }

    private static final float cos(float angle) {
        return (float)Math.cos((double)angle);
    }
}

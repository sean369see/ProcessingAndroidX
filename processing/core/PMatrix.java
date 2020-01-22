package processing.core;

public interface PMatrix {
    void reset();

    PMatrix get();

    float[] get(float[] var1);

    void set(PMatrix var1);

    void set(float[] var1);

    void set(float var1, float var2, float var3, float var4, float var5, float var6);

    void set(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16);

    void translate(float var1, float var2);

    void translate(float var1, float var2, float var3);

    void rotate(float var1);

    void rotateX(float var1);

    void rotateY(float var1);

    void rotateZ(float var1);

    void rotate(float var1, float var2, float var3, float var4);

    void scale(float var1);

    void scale(float var1, float var2);

    void scale(float var1, float var2, float var3);

    void shearX(float var1);

    void shearY(float var1);

    void apply(PMatrix var1);

    void apply(PMatrix2D var1);

    void apply(PMatrix3D var1);

    void apply(float var1, float var2, float var3, float var4, float var5, float var6);

    void apply(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16);

    void preApply(PMatrix var1);

    void preApply(PMatrix2D var1);

    void preApply(PMatrix3D var1);

    void preApply(float var1, float var2, float var3, float var4, float var5, float var6);

    void preApply(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16);

    PVector mult(PVector var1, PVector var2);

    float[] mult(float[] var1, float[] var2);

    void transpose();

    boolean invert();

    float determinant();
}

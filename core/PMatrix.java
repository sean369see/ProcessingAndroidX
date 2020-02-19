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

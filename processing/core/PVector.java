package processing.core;

import java.io.Serializable;

public class PVector implements Serializable {
    public float x;
    public float y;
    public float z;
    protected transient float[] array;

    public PVector() {
    }

    public PVector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PVector(float x, float y) {
        this.x = x;
        this.y = y;
        this.z = 0.0F;
    }

    public PVector set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public PVector set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public PVector set(PVector v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        return this;
    }

    public PVector set(float[] source) {
        if (source.length >= 2) {
            this.x = source[0];
            this.y = source[1];
        }

        if (source.length >= 3) {
            this.z = source[2];
        }

        return this;
    }

    public static PVector random2D() {
        return random2D((PVector)null, (PApplet)null);
    }

    public static PVector random2D(PApplet parent) {
        return random2D((PVector)null, parent);
    }

    public static PVector random2D(PVector target) {
        return random2D(target, (PApplet)null);
    }

    public static PVector random2D(PVector target, PApplet parent) {
        return parent == null ? fromAngle((float)(Math.random() * 3.141592653589793D * 2.0D), target) : fromAngle(parent.random(6.2831855F), target);
    }

    public static PVector random3D() {
        return random3D((PVector)null, (PApplet)null);
    }

    public static PVector random3D(PApplet parent) {
        return random3D((PVector)null, parent);
    }

    public static PVector random3D(PVector target) {
        return random3D(target, (PApplet)null);
    }

    public static PVector random3D(PVector target, PApplet parent) {
        float angle;
        float vz;
        if (parent == null) {
            angle = (float)(Math.random() * 3.141592653589793D * 2.0D);
            vz = (float)(Math.random() * 2.0D - 1.0D);
        } else {
            angle = parent.random(6.2831855F);
            vz = parent.random(-1.0F, 1.0F);
        }

        float vx = (float)(Math.sqrt((double)(1.0F - vz * vz)) * Math.cos((double)angle));
        float vy = (float)(Math.sqrt((double)(1.0F - vz * vz)) * Math.sin((double)angle));
        if (target == null) {
            target = new PVector(vx, vy, vz);
        } else {
            target.set(vx, vy, vz);
        }

        return target;
    }

    public static PVector fromAngle(float angle) {
        return fromAngle(angle, (PVector)null);
    }

    public static PVector fromAngle(float angle, PVector target) {
        if (target == null) {
            target = new PVector((float)Math.cos((double)angle), (float)Math.sin((double)angle), 0.0F);
        } else {
            target.set((float)Math.cos((double)angle), (float)Math.sin((double)angle), 0.0F);
        }

        return target;
    }

    public PVector copy() {
        return new PVector(this.x, this.y, this.z);
    }

    /** @deprecated */
    @Deprecated
    public PVector get() {
        return this.copy();
    }

    public float[] get(float[] target) {
        if (target == null) {
            return new float[]{this.x, this.y, this.z};
        } else {
            if (target.length >= 2) {
                target[0] = this.x;
                target[1] = this.y;
            }

            if (target.length >= 3) {
                target[2] = this.z;
            }

            return target;
        }
    }

    public float mag() {
        return (float)Math.sqrt((double)(this.x * this.x + this.y * this.y + this.z * this.z));
    }

    public float magSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public PVector add(PVector v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
        return this;
    }

    public PVector add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public PVector add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public static PVector add(PVector v1, PVector v2) {
        return add(v1, v2, (PVector)null);
    }

    public static PVector add(PVector v1, PVector v2, PVector target) {
        if (target == null) {
            target = new PVector(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
        } else {
            target.set(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
        }

        return target;
    }

    public PVector sub(PVector v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
        return this;
    }

    public PVector sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public PVector sub(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public static PVector sub(PVector v1, PVector v2) {
        return sub(v1, v2, (PVector)null);
    }

    public static PVector sub(PVector v1, PVector v2, PVector target) {
        if (target == null) {
            target = new PVector(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        } else {
            target.set(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
        }

        return target;
    }

    public PVector mult(float n) {
        this.x *= n;
        this.y *= n;
        this.z *= n;
        return this;
    }

    public static PVector mult(PVector v, float n) {
        return mult(v, n, (PVector)null);
    }

    public static PVector mult(PVector v, float n, PVector target) {
        if (target == null) {
            target = new PVector(v.x * n, v.y * n, v.z * n);
        } else {
            target.set(v.x * n, v.y * n, v.z * n);
        }

        return target;
    }

    public PVector div(float n) {
        this.x /= n;
        this.y /= n;
        this.z /= n;
        return this;
    }

    public static PVector div(PVector v, float n) {
        return div(v, n, (PVector)null);
    }

    public static PVector div(PVector v, float n, PVector target) {
        if (target == null) {
            target = new PVector(v.x / n, v.y / n, v.z / n);
        } else {
            target.set(v.x / n, v.y / n, v.z / n);
        }

        return target;
    }

    public float dist(PVector v) {
        float dx = this.x - v.x;
        float dy = this.y - v.y;
        float dz = this.z - v.z;
        return (float)Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }

    public static float dist(PVector v1, PVector v2) {
        float dx = v1.x - v2.x;
        float dy = v1.y - v2.y;
        float dz = v1.z - v2.z;
        return (float)Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
    }

    public float dot(PVector v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public float dot(float x, float y, float z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public static float dot(PVector v1, PVector v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public PVector cross(PVector v) {
        return this.cross(v, (PVector)null);
    }

    public PVector cross(PVector v, PVector target) {
        float crossX = this.y * v.z - v.y * this.z;
        float crossY = this.z * v.x - v.z * this.x;
        float crossZ = this.x * v.y - v.x * this.y;
        if (target == null) {
            target = new PVector(crossX, crossY, crossZ);
        } else {
            target.set(crossX, crossY, crossZ);
        }

        return target;
    }

    public static PVector cross(PVector v1, PVector v2, PVector target) {
        float crossX = v1.y * v2.z - v2.y * v1.z;
        float crossY = v1.z * v2.x - v2.z * v1.x;
        float crossZ = v1.x * v2.y - v2.x * v1.y;
        if (target == null) {
            target = new PVector(crossX, crossY, crossZ);
        } else {
            target.set(crossX, crossY, crossZ);
        }

        return target;
    }

    public PVector normalize() {
        float m = this.mag();
        if (m != 0.0F && m != 1.0F) {
            this.div(m);
        }

        return this;
    }

    public PVector normalize(PVector target) {
        if (target == null) {
            target = new PVector();
        }

        float m = this.mag();
        if (m > 0.0F) {
            target.set(this.x / m, this.y / m, this.z / m);
        } else {
            target.set(this.x, this.y, this.z);
        }

        return target;
    }

    public PVector limit(float max) {
        if (this.magSq() > max * max) {
            this.normalize();
            this.mult(max);
        }

        return this;
    }

    public PVector setMag(float len) {
        this.normalize();
        this.mult(len);
        return this;
    }

    public PVector setMag(PVector target, float len) {
        target = this.normalize(target);
        target.mult(len);
        return target;
    }

    public float heading() {
        float angle = (float)Math.atan2((double)this.y, (double)this.x);
        return angle;
    }

    /** @deprecated */
    @Deprecated
    public float heading2D() {
        return this.heading();
    }

    public PVector rotate(float theta) {
        float temp = this.x;
        this.x = this.x * PApplet.cos(theta) - this.y * PApplet.sin(theta);
        this.y = temp * PApplet.sin(theta) + this.y * PApplet.cos(theta);
        return this;
    }

    public PVector lerp(PVector v, float amt) {
        this.x = PApplet.lerp(this.x, v.x, amt);
        this.y = PApplet.lerp(this.y, v.y, amt);
        this.z = PApplet.lerp(this.z, v.z, amt);
        return this;
    }

    public static PVector lerp(PVector v1, PVector v2, float amt) {
        PVector v = v1.copy();
        v.lerp(v2, amt);
        return v;
    }

    public PVector lerp(float x, float y, float z, float amt) {
        this.x = PApplet.lerp(this.x, x, amt);
        this.y = PApplet.lerp(this.y, y, amt);
        this.z = PApplet.lerp(this.z, z, amt);
        return this;
    }

    public static float angleBetween(PVector v1, PVector v2) {
        if (v1.x == 0.0F && v1.y == 0.0F && v1.z == 0.0F) {
            return 0.0F;
        } else if (v2.x == 0.0F && v2.y == 0.0F && v2.z == 0.0F) {
            return 0.0F;
        } else {
            double dot = (double)(v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);
            double v1mag = Math.sqrt((double)(v1.x * v1.x + v1.y * v1.y + v1.z * v1.z));
            double v2mag = Math.sqrt((double)(v2.x * v2.x + v2.y * v2.y + v2.z * v2.z));
            double amt = dot / (v1mag * v2mag);
            if (amt <= -1.0D) {
                return 3.1415927F;
            } else {
                return amt >= 1.0D ? 0.0F : (float)Math.acos(amt);
            }
        }
    }

    public String toString() {
        return "[ " + this.x + ", " + this.y + ", " + this.z + " ]";
    }

    public float[] array() {
        if (this.array == null) {
            this.array = new float[3];
        }

        this.array[0] = this.x;
        this.array[1] = this.y;
        this.array[2] = this.z;
        return this.array;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PVector)) {
            return false;
        } else {
            PVector p = (PVector)obj;
            return this.x == p.x && this.y == p.y && this.z == p.z;
        }
    }

    public int hashCode() {
        int result = 1;
        int result = 31 * result + Float.floatToIntBits(this.x);
        result = 31 * result + Float.floatToIntBits(this.y);
        result = 31 * result + Float.floatToIntBits(this.z);
        return result;
    }
}

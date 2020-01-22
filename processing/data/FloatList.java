package processing.data;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import processing.core.PApplet;

public class FloatList implements Iterable<Float> {
    int count;
    float[] data;

    public FloatList() {
        this.data = new float[10];
    }

    public FloatList(int length) {
        this.data = new float[length];
    }

    public FloatList(float[] list) {
        this.count = list.length;
        this.data = new float[this.count];
        System.arraycopy(list, 0, this.data, 0, this.count);
    }

    public FloatList(Iterable<Object> iter) {
        this(10);
        Iterator var2 = iter.iterator();

        while(var2.hasNext()) {
            Object o = var2.next();
            if (o == null) {
                this.append(0.0F / 0.0);
            } else if (o instanceof Number) {
                this.append(((Number)o).floatValue());
            } else {
                this.append(PApplet.parseFloat(o.toString().trim()));
            }
        }

        this.crop();
    }

    public FloatList(Object... items) {
        float missingValue = 0.0F / 0.0;
        this.count = items.length;
        this.data = new float[this.count];
        int index = 0;
        Object[] var4 = items;
        int var5 = items.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Object o = var4[var6];
            float value = 0.0F / 0.0;
            if (o != null) {
                if (o instanceof Number) {
                    value = ((Number)o).floatValue();
                } else {
                    value = PApplet.parseFloat(o.toString().trim(), 0.0F / 0.0);
                }
            }

            this.data[index++] = value;
        }

    }

    private void crop() {
        if (this.count != this.data.length) {
            this.data = PApplet.subset(this.data, 0, this.count);
        }

    }

    public int size() {
        return this.count;
    }

    public void resize(int length) {
        if (length > this.data.length) {
            float[] temp = new float[length];
            System.arraycopy(this.data, 0, temp, 0, this.count);
            this.data = temp;
        } else if (length > this.count) {
            Arrays.fill(this.data, this.count, length, 0.0F);
        }

        this.count = length;
    }

    public void clear() {
        this.count = 0;
    }

    public float get(int index) {
        if (index >= this.count) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else {
            return this.data[index];
        }
    }

    public void set(int index, float what) {
        if (index >= this.count) {
            this.data = PApplet.expand(this.data, index + 1);

            for(int i = this.count; i < index; ++i) {
                this.data[i] = 0.0F;
            }

            this.count = index + 1;
        }

        this.data[index] = what;
    }

    public void push(float value) {
        this.append(value);
    }

    public float pop() {
        if (this.count == 0) {
            throw new RuntimeException("Can't call pop() on an empty list");
        } else {
            float value = this.get(this.count - 1);
            --this.count;
            return value;
        }
    }

    public float remove(int index) {
        if (index >= 0 && index < this.count) {
            float entry = this.data[index];

            for(int i = index; i < this.count - 1; ++i) {
                this.data[i] = this.data[i + 1];
            }

            --this.count;
            return entry;
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    public int removeValue(int value) {
        int index = this.index((float)value);
        if (index != -1) {
            this.remove(index);
            return index;
        } else {
            return -1;
        }
    }

    public int removeValues(int value) {
        int ii = 0;
        int i;
        if (Float.isNaN((float)value)) {
            for(i = 0; i < this.count; ++i) {
                if (!Float.isNaN(this.data[i])) {
                    this.data[ii++] = this.data[i];
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] != (float)value) {
                    this.data[ii++] = this.data[i];
                }
            }
        }

        i = this.count - ii;
        this.count = ii;
        return i;
    }

    public boolean replaceValue(float value, float newValue) {
        int i;
        if (Float.isNaN(value)) {
            for(i = 0; i < this.count; ++i) {
                if (Float.isNaN(this.data[i])) {
                    this.data[i] = newValue;
                    return true;
                }
            }
        } else {
            i = this.index(value);
            if (i != -1) {
                this.data[i] = newValue;
                return true;
            }
        }

        return false;
    }

    public boolean replaceValues(float value, float newValue) {
        boolean changed = false;
        int i;
        if (Float.isNaN(value)) {
            for(i = 0; i < this.count; ++i) {
                if (Float.isNaN(this.data[i])) {
                    this.data[i] = newValue;
                    changed = true;
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] == value) {
                    this.data[i] = newValue;
                    changed = true;
                }
            }
        }

        return changed;
    }

    public void append(float value) {
        if (this.count == this.data.length) {
            this.data = PApplet.expand(this.data);
        }

        this.data[this.count++] = value;
    }

    public void append(float[] values) {
        float[] var2 = values;
        int var3 = values.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            float v = var2[var4];
            this.append(v);
        }

    }

    public void append(FloatList list) {
        float[] var2 = list.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            float v = var2[var4];
            this.append(v);
        }

    }

    public void appendUnique(float value) {
        if (!this.hasValue(value)) {
            this.append(value);
        }

    }

    public void insert(int index, float value) {
        this.insert(index, new float[]{value});
    }

    public void insert(int index, float[] values) {
        if (index < 0) {
            throw new IllegalArgumentException("insert() index cannot be negative: it was " + index);
        } else if (index >= this.data.length) {
            throw new IllegalArgumentException("insert() index " + index + " is past the end of this list");
        } else {
            float[] temp = new float[this.count + values.length];
            System.arraycopy(this.data, 0, temp, 0, Math.min(this.count, index));
            System.arraycopy(values, 0, temp, index, values.length);
            System.arraycopy(this.data, index, temp, index + values.length, this.count - index);
            this.count += values.length;
            this.data = temp;
        }
    }

    public void insert(int index, FloatList list) {
        this.insert(index, list.values());
    }

    public int index(float what) {
        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == what) {
                return i;
            }
        }

        return -1;
    }

    public boolean hasValue(float value) {
        int i;
        if (Float.isNaN(value)) {
            for(i = 0; i < this.count; ++i) {
                if (Float.isNaN(this.data[i])) {
                    return true;
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] == value) {
                    return true;
                }
            }
        }

        return false;
    }

    private void boundsProblem(int index, String method) {
        String msg = String.format("The list size is %d. You cannot %s() to element %d.", this.count, method, index);
        throw new ArrayIndexOutOfBoundsException(msg);
    }

    public void add(int index, float amount) {
        if (index < this.count) {
            float[] var10000 = this.data;
            var10000[index] += amount;
        } else {
            this.boundsProblem(index, "add");
        }

    }

    public void sub(int index, float amount) {
        if (index < this.count) {
            float[] var10000 = this.data;
            var10000[index] -= amount;
        } else {
            this.boundsProblem(index, "sub");
        }

    }

    public void mult(int index, float amount) {
        if (index < this.count) {
            float[] var10000 = this.data;
            var10000[index] *= amount;
        } else {
            this.boundsProblem(index, "mult");
        }

    }

    public void div(int index, float amount) {
        if (index < this.count) {
            float[] var10000 = this.data;
            var10000[index] /= amount;
        } else {
            this.boundsProblem(index, "div");
        }

    }

    private void checkMinMax(String functionName) {
        if (this.count == 0) {
            String msg = String.format("Cannot use %s() on an empty %s.", functionName, this.getClass().getSimpleName());
            throw new RuntimeException(msg);
        }
    }

    public float min() {
        this.checkMinMax("min");
        int index = this.minIndex();
        return index == -1 ? 0.0F / 0.0 : this.data[index];
    }

    public int minIndex() {
        this.checkMinMax("minIndex");
        float m = 0.0F / 0.0;
        int mi = -1;

        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == this.data[i]) {
                m = this.data[i];
                mi = i;

                for(int j = i + 1; j < this.count; ++j) {
                    float d = this.data[j];
                    if (!Float.isNaN(d) && d < m) {
                        m = this.data[j];
                        mi = j;
                    }
                }

                return mi;
            }
        }

        return mi;
    }

    public float max() {
        this.checkMinMax("max");
        int index = this.maxIndex();
        return index == -1 ? 0.0F / 0.0 : this.data[index];
    }

    public int maxIndex() {
        this.checkMinMax("maxIndex");
        float m = 0.0F / 0.0;
        int mi = -1;

        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == this.data[i]) {
                m = this.data[i];
                mi = i;

                for(int j = i + 1; j < this.count; ++j) {
                    float d = this.data[j];
                    if (!Float.isNaN(d) && d > m) {
                        m = this.data[j];
                        mi = j;
                    }
                }

                return mi;
            }
        }

        return mi;
    }

    public float sum() {
        double amount = this.sumDouble();
        if (amount > 3.4028234663852886E38D) {
            throw new RuntimeException("sum() exceeds 3.4028235E38, use sumDouble()");
        } else if (amount < -3.4028234663852886E38D) {
            throw new RuntimeException("sum() lower than -3.4028235E38, use sumDouble()");
        } else {
            return (float)amount;
        }
    }

    public double sumDouble() {
        double sum = 0.0D;

        for(int i = 0; i < this.count; ++i) {
            sum += (double)this.data[i];
        }

        return sum;
    }

    public void sort() {
        Arrays.sort(this.data, 0, this.count);
    }

    public void sortReverse() {
        (new Sort() {
            public int size() {
                if (FloatList.this.count == 0) {
                    return 0;
                } else {
                    int right = FloatList.this.count - 1;

                    do {
                        if (FloatList.this.data[right] == FloatList.this.data[right]) {
                            for(int i = right; i >= 0; --i) {
                                float v = FloatList.this.data[i];
                                if (v != v) {
                                    FloatList.this.data[i] = FloatList.this.data[right];
                                    FloatList.this.data[right] = v;
                                    --right;
                                }
                            }

                            return right + 1;
                        }

                        --right;
                    } while(right != -1);

                    return 0;
                }
            }

            public int compare(int a, int b) {
                float diff = FloatList.this.data[b] - FloatList.this.data[a];
                return diff == 0.0F ? 0 : (diff < 0.0F ? -1 : 1);
            }

            public void swap(int a, int b) {
                float temp = FloatList.this.data[a];
                FloatList.this.data[a] = FloatList.this.data[b];
                FloatList.this.data[b] = temp;
            }
        }).run();
    }

    public void reverse() {
        int ii = this.count - 1;

        for(int i = 0; i < this.count / 2; ++i) {
            float t = this.data[i];
            this.data[i] = this.data[ii];
            this.data[ii] = t;
            --ii;
        }

    }

    public void shuffle() {
        Random r = new Random();

        int value;
        float temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = r.nextInt(num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public void shuffle(PApplet sketch) {
        int value;
        float temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = (int)sketch.random((float)num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public FloatList copy() {
        FloatList outgoing = new FloatList(this.data);
        outgoing.count = this.count;
        return outgoing;
    }

    public float[] values() {
        this.crop();
        return this.data;
    }

    public Iterator<Float> iterator() {
        return new Iterator<Float>() {
            int index = -1;

            public void remove() {
                FloatList.this.remove(this.index);
                --this.index;
            }

            public Float next() {
                return FloatList.this.data[++this.index];
            }

            public boolean hasNext() {
                return this.index + 1 < FloatList.this.count;
            }
        };
    }

    public float[] array() {
        return this.array((float[])null);
    }

    public float[] array(float[] array) {
        if (array == null || array.length != this.count) {
            array = new float[this.count];
        }

        System.arraycopy(this.data, 0, array, 0, this.count);
        return array;
    }

    public FloatList getPercent() {
        double sum = 0.0D;
        float[] var3 = this.array();
        int i = var3.length;

        for(int var5 = 0; var5 < i; ++var5) {
            float value = var3[var5];
            sum += (double)value;
        }

        FloatList outgoing = new FloatList(this.count);

        for(i = 0; i < this.count; ++i) {
            double percent = (double)this.data[i] / sum;
            outgoing.set(i, (float)percent);
        }

        return outgoing;
    }

    public FloatList getSubset(int start) {
        return this.getSubset(start, this.count - start);
    }

    public FloatList getSubset(int start, int num) {
        float[] subset = new float[num];
        System.arraycopy(this.data, start, subset, 0, num);
        return new FloatList(subset);
    }

    public String join(String separator) {
        if (this.count == 0) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(this.data[0]);

            for(int i = 1; i < this.count; ++i) {
                sb.append(separator);
                sb.append(this.data[i]);
            }

            return sb.toString();
        }
    }

    public void print() {
        for(int i = 0; i < this.count; ++i) {
            System.out.format("[%d] %f%n", i, this.data[i]);
        }

    }

    public void save(File file) {
        PrintWriter writer = PApplet.createWriter(file);
        this.write(writer);
        writer.close();
    }

    public void write(PrintWriter writer) {
        for(int i = 0; i < this.count; ++i) {
            writer.println(this.data[i]);
        }

        writer.flush();
    }

    public String toJSON() {
        return "[ " + this.join(", ") + " ]";
    }

    public String toString() {
        return this.getClass().getSimpleName() + " size=" + this.size() + " " + this.toJSON();
    }
}

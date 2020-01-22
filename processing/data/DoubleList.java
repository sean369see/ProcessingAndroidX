package processing.data;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import processing.core.PApplet;

public class DoubleList implements Iterable<Double> {
    int count;
    double[] data;

    public DoubleList() {
        this.data = new double[10];
    }

    public DoubleList(int length) {
        this.data = new double[length];
    }

    public DoubleList(double[] list) {
        this.count = list.length;
        this.data = new double[this.count];
        System.arraycopy(list, 0, this.data, 0, this.count);
    }

    public DoubleList(Iterable<Object> iter) {
        this(10);
        Iterator var2 = iter.iterator();

        while(var2.hasNext()) {
            Object o = var2.next();
            if (o == null) {
                this.append(0.0D / 0.0);
            } else if (o instanceof Number) {
                this.append(((Number)o).doubleValue());
            } else {
                this.append((double)PApplet.parseFloat(o.toString().trim()));
            }
        }

        this.crop();
    }

    public DoubleList(Object... items) {
        double missingValue = 0.0D / 0.0;
        this.count = items.length;
        this.data = new double[this.count];
        int index = 0;
        Object[] var5 = items;
        int var6 = items.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Object o = var5[var7];
            double value = 0.0D / 0.0;
            if (o != null) {
                if (o instanceof Number) {
                    value = ((Number)o).doubleValue();
                } else {
                    try {
                        value = Double.parseDouble(o.toString().trim());
                    } catch (NumberFormatException var12) {
                        value = 0.0D / 0.0;
                    }
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
            double[] temp = new double[length];
            System.arraycopy(this.data, 0, temp, 0, this.count);
            this.data = temp;
        } else if (length > this.count) {
            Arrays.fill(this.data, this.count, length, 0.0D);
        }

        this.count = length;
    }

    public void clear() {
        this.count = 0;
    }

    public double get(int index) {
        if (index >= this.count) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else {
            return this.data[index];
        }
    }

    public void set(int index, double what) {
        if (index >= this.count) {
            this.data = PApplet.expand(this.data, index + 1);

            for(int i = this.count; i < index; ++i) {
                this.data[i] = 0.0D;
            }

            this.count = index + 1;
        }

        this.data[index] = what;
    }

    public void push(double value) {
        this.append(value);
    }

    public double pop() {
        if (this.count == 0) {
            throw new RuntimeException("Can't call pop() on an empty list");
        } else {
            double value = this.get(this.count - 1);
            --this.count;
            return value;
        }
    }

    public double remove(int index) {
        if (index >= 0 && index < this.count) {
            double entry = this.data[index];

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
        int index = this.index((double)value);
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
        if (Double.isNaN((double)value)) {
            for(i = 0; i < this.count; ++i) {
                if (!Double.isNaN(this.data[i])) {
                    this.data[ii++] = this.data[i];
                }
            }
        } else {
            for(i = 0; i < this.count; ++i) {
                if (this.data[i] != (double)value) {
                    this.data[ii++] = this.data[i];
                }
            }
        }

        i = this.count - ii;
        this.count = ii;
        return i;
    }

    public boolean replaceValue(double value, double newValue) {
        int i;
        if (Double.isNaN(value)) {
            for(i = 0; i < this.count; ++i) {
                if (Double.isNaN(this.data[i])) {
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

    public boolean replaceValues(double value, double newValue) {
        boolean changed = false;
        int i;
        if (Double.isNaN(value)) {
            for(i = 0; i < this.count; ++i) {
                if (Double.isNaN(this.data[i])) {
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

    public void append(double value) {
        if (this.count == this.data.length) {
            this.data = PApplet.expand(this.data);
        }

        this.data[this.count++] = value;
    }

    public void append(double[] values) {
        double[] var2 = values;
        int var3 = values.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            double v = var2[var4];
            this.append(v);
        }

    }

    public void append(DoubleList list) {
        double[] var2 = list.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            double v = var2[var4];
            this.append(v);
        }

    }

    public void appendUnique(double value) {
        if (!this.hasValue(value)) {
            this.append(value);
        }

    }

    public void insert(int index, double value) {
        this.insert(index, new double[]{value});
    }

    public void insert(int index, double[] values) {
        if (index < 0) {
            throw new IllegalArgumentException("insert() index cannot be negative: it was " + index);
        } else if (index >= this.data.length) {
            throw new IllegalArgumentException("insert() index " + index + " is past the end of this list");
        } else {
            double[] temp = new double[this.count + values.length];
            System.arraycopy(this.data, 0, temp, 0, Math.min(this.count, index));
            System.arraycopy(values, 0, temp, index, values.length);
            System.arraycopy(this.data, index, temp, index + values.length, this.count - index);
            this.count += values.length;
            this.data = temp;
        }
    }

    public void insert(int index, DoubleList list) {
        this.insert(index, list.values());
    }

    public int index(double what) {
        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == what) {
                return i;
            }
        }

        return -1;
    }

    public boolean hasValue(double value) {
        int i;
        if (Double.isNaN(value)) {
            for(i = 0; i < this.count; ++i) {
                if (Double.isNaN(this.data[i])) {
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

    public void add(int index, double amount) {
        if (index < this.count) {
            double[] var10000 = this.data;
            var10000[index] += amount;
        } else {
            this.boundsProblem(index, "add");
        }

    }

    public void sub(int index, double amount) {
        if (index < this.count) {
            double[] var10000 = this.data;
            var10000[index] -= amount;
        } else {
            this.boundsProblem(index, "sub");
        }

    }

    public void mult(int index, double amount) {
        if (index < this.count) {
            double[] var10000 = this.data;
            var10000[index] *= amount;
        } else {
            this.boundsProblem(index, "mult");
        }

    }

    public void div(int index, double amount) {
        if (index < this.count) {
            double[] var10000 = this.data;
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

    public double min() {
        this.checkMinMax("min");
        int index = this.minIndex();
        return index == -1 ? 0.0D / 0.0 : this.data[index];
    }

    public int minIndex() {
        this.checkMinMax("minIndex");
        double m = 0.0D / 0.0;
        int mi = -1;

        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == this.data[i]) {
                m = this.data[i];
                mi = i;

                for(int j = i + 1; j < this.count; ++j) {
                    double d = this.data[j];
                    if (!Double.isNaN(d) && d < m) {
                        m = this.data[j];
                        mi = j;
                    }
                }

                return mi;
            }
        }

        return mi;
    }

    public double max() {
        this.checkMinMax("max");
        int index = this.maxIndex();
        return index == -1 ? 0.0D / 0.0 : this.data[index];
    }

    public int maxIndex() {
        this.checkMinMax("maxIndex");
        double m = 0.0D / 0.0;
        int mi = -1;

        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == this.data[i]) {
                m = this.data[i];
                mi = i;

                for(int j = i + 1; j < this.count; ++j) {
                    double d = this.data[j];
                    if (!Double.isNaN(d) && d > m) {
                        m = this.data[j];
                        mi = j;
                    }
                }

                return mi;
            }
        }

        return mi;
    }

    public double sum() {
        double sum = 0.0D;

        for(int i = 0; i < this.count; ++i) {
            sum += this.data[i];
        }

        return sum;
    }

    public void sort() {
        Arrays.sort(this.data, 0, this.count);
    }

    public void sortReverse() {
        (new Sort() {
            public int size() {
                if (DoubleList.this.count == 0) {
                    return 0;
                } else {
                    int right = DoubleList.this.count - 1;

                    do {
                        if (DoubleList.this.data[right] == DoubleList.this.data[right]) {
                            for(int i = right; i >= 0; --i) {
                                double v = DoubleList.this.data[i];
                                if (v != v) {
                                    DoubleList.this.data[i] = DoubleList.this.data[right];
                                    DoubleList.this.data[right] = v;
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
                double diff = DoubleList.this.data[b] - DoubleList.this.data[a];
                return diff == 0.0D ? 0 : (diff < 0.0D ? -1 : 1);
            }

            public void swap(int a, int b) {
                double temp = DoubleList.this.data[a];
                DoubleList.this.data[a] = DoubleList.this.data[b];
                DoubleList.this.data[b] = temp;
            }
        }).run();
    }

    public void reverse() {
        int ii = this.count - 1;

        for(int i = 0; i < this.count / 2; ++i) {
            double t = this.data[i];
            this.data[i] = this.data[ii];
            this.data[ii] = t;
            --ii;
        }

    }

    public void shuffle() {
        Random r = new Random();

        int value;
        double temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = r.nextInt(num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public void shuffle(PApplet sketch) {
        int value;
        double temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = (int)sketch.random((float)num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public DoubleList copy() {
        DoubleList outgoing = new DoubleList(this.data);
        outgoing.count = this.count;
        return outgoing;
    }

    public double[] values() {
        this.crop();
        return this.data;
    }

    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            int index = -1;

            public void remove() {
                DoubleList.this.remove(this.index);
                --this.index;
            }

            public Double next() {
                return DoubleList.this.data[++this.index];
            }

            public boolean hasNext() {
                return this.index + 1 < DoubleList.this.count;
            }
        };
    }

    public double[] array() {
        return this.array((double[])null);
    }

    public double[] array(double[] array) {
        if (array == null || array.length != this.count) {
            array = new double[this.count];
        }

        System.arraycopy(this.data, 0, array, 0, this.count);
        return array;
    }

    public DoubleList getPercent() {
        double sum = 0.0D;
        double[] var3 = this.array();
        int i = var3.length;

        for(int var5 = 0; var5 < i; ++var5) {
            double value = var3[var5];
            sum += value;
        }

        DoubleList outgoing = new DoubleList(this.count);

        for(i = 0; i < this.count; ++i) {
            double percent = this.data[i] / sum;
            outgoing.set(i, percent);
        }

        return outgoing;
    }

    public DoubleList getSubset(int start) {
        return this.getSubset(start, this.count - start);
    }

    public DoubleList getSubset(int start, int num) {
        double[] subset = new double[num];
        System.arraycopy(this.data, start, subset, 0, num);
        return new DoubleList(subset);
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

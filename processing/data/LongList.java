package processing.data;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import processing.core.PApplet;

public class LongList implements Iterable<Long> {
    protected int count;
    protected long[] data;

    public LongList() {
        this.data = new long[10];
    }

    public LongList(int length) {
        this.data = new long[length];
    }

    public LongList(int[] source) {
        this.count = source.length;
        this.data = new long[this.count];
        System.arraycopy(source, 0, this.data, 0, this.count);
    }

    public LongList(Iterable<Object> iter) {
        this(10);
        Iterator var2 = iter.iterator();

        while(var2.hasNext()) {
            Object o = var2.next();
            if (o == null) {
                this.append(0L);
            } else if (o instanceof Number) {
                this.append((long)((Number)o).intValue());
            } else {
                this.append((long)PApplet.parseInt(o.toString().trim()));
            }
        }

        this.crop();
    }

    public LongList(Object... items) {
        int missingValue = false;
        this.count = items.length;
        this.data = new long[this.count];
        int index = 0;
        Object[] var4 = items;
        int var5 = items.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Object o = var4[var6];
            int value = 0;
            if (o != null) {
                if (o instanceof Number) {
                    value = ((Number)o).intValue();
                } else {
                    value = PApplet.parseInt(o.toString().trim(), 0);
                }
            }

            this.data[index++] = (long)value;
        }

    }

    public static LongList fromRange(int stop) {
        return fromRange(0, stop);
    }

    public static LongList fromRange(int start, int stop) {
        int count = stop - start;
        LongList newbie = new LongList(count);

        for(int i = 0; i < count; ++i) {
            newbie.set(i, start + i);
        }

        return newbie;
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
            long[] temp = new long[length];
            System.arraycopy(this.data, 0, temp, 0, this.count);
            this.data = temp;
        } else if (length > this.count) {
            Arrays.fill(this.data, this.count, length, 0L);
        }

        this.count = length;
    }

    public void clear() {
        this.count = 0;
    }

    public long get(int index) {
        if (index >= this.count) {
            throw new ArrayIndexOutOfBoundsException(index);
        } else {
            return this.data[index];
        }
    }

    public void set(int index, int what) {
        if (index >= this.count) {
            this.data = PApplet.expand(this.data, index + 1);

            for(int i = this.count; i < index; ++i) {
                this.data[i] = 0L;
            }

            this.count = index + 1;
        }

        this.data[index] = (long)what;
    }

    public void push(int value) {
        this.append((long)value);
    }

    public long pop() {
        if (this.count == 0) {
            throw new RuntimeException("Can't call pop() on an empty list");
        } else {
            long value = this.get(this.count - 1);
            --this.count;
            return value;
        }
    }

    public long remove(int index) {
        if (index >= 0 && index < this.count) {
            long entry = this.data[index];

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
        int index = this.index(value);
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
        for(i = 0; i < this.count; ++i) {
            if (this.data[i] != (long)value) {
                this.data[ii++] = this.data[i];
            }
        }

        i = this.count - ii;
        this.count = ii;
        return i;
    }

    public void append(long value) {
        if (this.count == this.data.length) {
            this.data = PApplet.expand(this.data);
        }

        this.data[this.count++] = value;
    }

    public void append(int[] values) {
        int[] var2 = values;
        int var3 = values.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int v = var2[var4];
            this.append((long)v);
        }

    }

    public void append(LongList list) {
        long[] var2 = list.values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            long v = var2[var4];
            this.append(v);
        }

    }

    public void appendUnique(int value) {
        if (!this.hasValue(value)) {
            this.append((long)value);
        }

    }

    public void insert(int index, long value) {
        this.insert(index, new long[]{value});
    }

    public void insert(int index, long[] values) {
        if (index < 0) {
            throw new IllegalArgumentException("insert() index cannot be negative: it was " + index);
        } else if (index >= this.data.length) {
            throw new IllegalArgumentException("insert() index " + index + " is past the end of this list");
        } else {
            long[] temp = new long[this.count + values.length];
            System.arraycopy(this.data, 0, temp, 0, Math.min(this.count, index));
            System.arraycopy(values, 0, temp, index, values.length);
            System.arraycopy(this.data, index, temp, index + values.length, this.count - index);
            this.count += values.length;
            this.data = temp;
        }
    }

    public void insert(int index, LongList list) {
        this.insert(index, list.values());
    }

    public int index(int what) {
        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == (long)what) {
                return i;
            }
        }

        return -1;
    }

    public boolean hasValue(int value) {
        for(int i = 0; i < this.count; ++i) {
            if (this.data[i] == (long)value) {
                return true;
            }
        }

        return false;
    }

    public void increment(int index) {
        if (this.count <= index) {
            this.resize(index + 1);
        }

        int var10002 = this.data[index]++;
    }

    private void boundsProblem(int index, String method) {
        String msg = String.format("The list size is %d. You cannot %s() to element %d.", this.count, method, index);
        throw new ArrayIndexOutOfBoundsException(msg);
    }

    public void add(int index, int amount) {
        if (index < this.count) {
            long[] var10000 = this.data;
            var10000[index] += (long)amount;
        } else {
            this.boundsProblem(index, "add");
        }

    }

    public void sub(int index, int amount) {
        if (index < this.count) {
            long[] var10000 = this.data;
            var10000[index] -= (long)amount;
        } else {
            this.boundsProblem(index, "sub");
        }

    }

    public void mult(int index, int amount) {
        if (index < this.count) {
            long[] var10000 = this.data;
            var10000[index] *= (long)amount;
        } else {
            this.boundsProblem(index, "mult");
        }

    }

    public void div(int index, int amount) {
        if (index < this.count) {
            long[] var10000 = this.data;
            var10000[index] /= (long)amount;
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

    public long min() {
        this.checkMinMax("min");
        long outgoing = this.data[0];

        for(int i = 1; i < this.count; ++i) {
            if (this.data[i] < outgoing) {
                outgoing = this.data[i];
            }
        }

        return outgoing;
    }

    public int minIndex() {
        this.checkMinMax("minIndex");
        long value = this.data[0];
        int index = 0;

        for(int i = 1; i < this.count; ++i) {
            if (this.data[i] < value) {
                value = this.data[i];
                index = i;
            }
        }

        return index;
    }

    public long max() {
        this.checkMinMax("max");
        long outgoing = this.data[0];

        for(int i = 1; i < this.count; ++i) {
            if (this.data[i] > outgoing) {
                outgoing = this.data[i];
            }
        }

        return outgoing;
    }

    public int maxIndex() {
        this.checkMinMax("maxIndex");
        long value = this.data[0];
        int index = 0;

        for(int i = 1; i < this.count; ++i) {
            if (this.data[i] > value) {
                value = this.data[i];
                index = i;
            }
        }

        return index;
    }

    public int sum() {
        long amount = this.sumLong();
        if (amount > 2147483647L) {
            throw new RuntimeException("sum() exceeds 2147483647, use sumLong()");
        } else if (amount < -2147483648L) {
            throw new RuntimeException("sum() less than -2147483648, use sumLong()");
        } else {
            return (int)amount;
        }
    }

    public long sumLong() {
        long sum = 0L;

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
                return LongList.this.count;
            }

            public int compare(int a, int b) {
                long diff = LongList.this.data[b] - LongList.this.data[a];
                return diff == 0L ? 0 : (diff < 0L ? -1 : 1);
            }

            public void swap(int a, int b) {
                long temp = LongList.this.data[a];
                LongList.this.data[a] = LongList.this.data[b];
                LongList.this.data[b] = temp;
            }
        }).run();
    }

    public void reverse() {
        int ii = this.count - 1;

        for(int i = 0; i < this.count / 2; ++i) {
            long t = this.data[i];
            this.data[i] = this.data[ii];
            this.data[ii] = t;
            --ii;
        }

    }

    public void shuffle() {
        Random r = new Random();

        int value;
        long temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = r.nextInt(num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public void shuffle(PApplet sketch) {
        int value;
        long temp;
        for(int num = this.count; num > 1; this.data[value] = temp) {
            value = (int)sketch.random((float)num);
            --num;
            temp = this.data[num];
            this.data[num] = this.data[value];
        }

    }

    public LongList copy() {
        LongList outgoing = new LongList(new Object[]{this.data});
        outgoing.count = this.count;
        return outgoing;
    }

    public long[] values() {
        this.crop();
        return this.data;
    }

    public Iterator<Long> iterator() {
        return new Iterator<Long>() {
            int index = -1;

            public void remove() {
                LongList.this.remove(this.index);
                --this.index;
            }

            public Long next() {
                return LongList.this.data[++this.index];
            }

            public boolean hasNext() {
                return this.index + 1 < LongList.this.count;
            }
        };
    }

    public int[] array() {
        return this.array((int[])null);
    }

    public int[] array(int[] array) {
        if (array == null || array.length != this.count) {
            array = new int[this.count];
        }

        System.arraycopy(this.data, 0, array, 0, this.count);
        return array;
    }

    public FloatList getPercent() {
        double sum = 0.0D;
        int[] var3 = this.array();
        int i = var3.length;

        for(int var5 = 0; var5 < i; ++var5) {
            float value = (float)var3[var5];
            sum += (double)value;
        }

        FloatList outgoing = new FloatList(this.count);

        for(i = 0; i < this.count; ++i) {
            double percent = (double)this.data[i] / sum;
            outgoing.set(i, (float)percent);
        }

        return outgoing;
    }

    public LongList getSubset(int start) {
        return this.getSubset(start, this.count - start);
    }

    public LongList getSubset(int start, int num) {
        int[] subset = new int[num];
        System.arraycopy(this.data, start, subset, 0, num);
        return new LongList(subset);
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
            System.out.format("[%d] %d%n", i, this.data[i]);
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

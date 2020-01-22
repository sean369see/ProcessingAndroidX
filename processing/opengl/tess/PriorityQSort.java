package processing.opengl.tess;

import processing.opengl.tess.PriorityQ.Leq;

class PriorityQSort extends PriorityQ {
    PriorityQHeap heap;
    Object[] keys;
    int[] order;
    int size;
    int max;
    boolean initialized;
    Leq leq;

    public PriorityQSort(Leq leq) {
        this.heap = new PriorityQHeap(leq);
        this.keys = new Object[32];
        this.size = 0;
        this.max = 32;
        this.initialized = false;
        this.leq = leq;
    }

    void pqDeletePriorityQ() {
        if (this.heap != null) {
            this.heap.pqDeletePriorityQ();
        }

        this.order = null;
        this.keys = null;
    }

    private static boolean LT(Leq leq, Object x, Object y) {
        return !PriorityQ.LEQ(leq, y, x);
    }

    private static boolean GT(Leq leq, Object x, Object y) {
        return !PriorityQ.LEQ(leq, x, y);
    }

    private static void Swap(int[] array, int a, int b) {
        int tmp = array[a];
        array[a] = array[b];
        array[b] = tmp;
    }

    boolean pqInit() {
        PriorityQSort.Stack[] stack = new PriorityQSort.Stack[50];

        int top;
        for(top = 0; top < stack.length; ++top) {
            stack[top] = new PriorityQSort.Stack();
        }

        int top = 0;
        int seed = 2016473283;
        this.order = new int[this.size + 1];
        int p = 0;
        int r = this.size - 1;
        int piv = 0;

        int i;
        for(i = p; i <= r; ++i) {
            this.order[i] = piv++;
        }

        stack[top].p = p;
        stack[top].r = r;
        top = top + 1;

        while(true) {
            --top;
            if (top < 0) {
                this.max = this.size;
                this.initialized = true;
                this.heap.pqInit();
                return true;
            }

            int p = stack[top].p;
            r = stack[top].r;

            int j;
            while(r > p + 10) {
                seed = Math.abs(seed * 1539415821 + 1);
                i = p + seed % (r - p + 1);
                piv = this.order[i];
                this.order[i] = this.order[p];
                this.order[p] = piv;
                i = p - 1;
                j = r + 1;

                while(true) {
                    ++i;
                    if (!GT(this.leq, this.keys[this.order[i]], this.keys[piv])) {
                        do {
                            --j;
                        } while(LT(this.leq, this.keys[this.order[j]], this.keys[piv]));

                        Swap(this.order, i, j);
                        if (i >= j) {
                            Swap(this.order, i, j);
                            if (i - p < r - j) {
                                stack[top].p = j + 1;
                                stack[top].r = r;
                                ++top;
                                r = i - 1;
                            } else {
                                stack[top].p = p;
                                stack[top].r = i - 1;
                                ++top;
                                p = j + 1;
                            }
                            break;
                        }
                    }
                }
            }

            for(i = p + 1; i <= r; ++i) {
                piv = this.order[i];

                for(j = i; j > p && LT(this.leq, this.keys[this.order[j - 1]], this.keys[piv]); --j) {
                    this.order[j] = this.order[j - 1];
                }

                this.order[j] = piv;
            }
        }
    }

    int pqInsert(Object keyNew) {
        if (this.initialized) {
            return this.heap.pqInsert(keyNew);
        } else {
            int curr = this.size;
            if (++this.size >= this.max) {
                Object[] saveKey = this.keys;
                this.max <<= 1;
                Object[] pqKeys = new Object[this.max];
                System.arraycopy(this.keys, 0, pqKeys, 0, this.keys.length);
                this.keys = pqKeys;
                if (this.keys == null) {
                    this.keys = saveKey;
                    return 2147483647;
                }
            }

            assert curr != 2147483647;

            this.keys[curr] = keyNew;
            return -(curr + 1);
        }
    }

    Object pqExtractMin() {
        if (this.size == 0) {
            return this.heap.pqExtractMin();
        } else {
            Object sortMin = this.keys[this.order[this.size - 1]];
            if (!this.heap.pqIsEmpty()) {
                Object heapMin = this.heap.pqMinimum();
                if (LEQ(this.leq, heapMin, sortMin)) {
                    return this.heap.pqExtractMin();
                }
            }

            do {
                --this.size;
            } while(this.size > 0 && this.keys[this.order[this.size - 1]] == null);

            return sortMin;
        }
    }

    Object pqMinimum() {
        if (this.size == 0) {
            return this.heap.pqMinimum();
        } else {
            Object sortMin = this.keys[this.order[this.size - 1]];
            if (!this.heap.pqIsEmpty()) {
                Object heapMin = this.heap.pqMinimum();
                if (PriorityQ.LEQ(this.leq, heapMin, sortMin)) {
                    return heapMin;
                }
            }

            return sortMin;
        }
    }

    boolean pqIsEmpty() {
        return this.size == 0 && this.heap.pqIsEmpty();
    }

    void pqDelete(int curr) {
        if (curr >= 0) {
            this.heap.pqDelete(curr);
        } else {
            curr = -(curr + 1);

            assert curr < this.max && this.keys[curr] != null;

            for(this.keys[curr] = null; this.size > 0 && this.keys[this.order[this.size - 1]] == null; --this.size) {
            }

        }
    }

    private static class Stack {
        int p;
        int r;

        private Stack() {
        }
    }
}

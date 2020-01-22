package processing.opengl.tess;

import processing.opengl.tess.PriorityQ.Leq;
import processing.opengl.tess.PriorityQ.PQhandleElem;
import processing.opengl.tess.PriorityQ.PQnode;

class PriorityQHeap extends PriorityQ {
    PQnode[] nodes = new PQnode[33];
    PQhandleElem[] handles;
    int size = 0;
    int max = 32;
    int freeList;
    boolean initialized;
    Leq leq;

    public PriorityQHeap(Leq leq) {
        int i;
        for(i = 0; i < this.nodes.length; ++i) {
            this.nodes[i] = new PQnode();
        }

        this.handles = new PQhandleElem[33];

        for(i = 0; i < this.handles.length; ++i) {
            this.handles[i] = new PQhandleElem();
        }

        this.initialized = false;
        this.freeList = 0;
        this.leq = leq;
        this.nodes[1].handle = 1;
        this.handles[1].key = null;
    }

    void pqDeletePriorityQ() {
        this.handles = null;
        this.nodes = null;
    }

    void FloatDown(int curr) {
        PQnode[] n = this.nodes;
        PQhandleElem[] h = this.handles;
        int hCurr = n[curr].handle;

        while(true) {
            int child = curr << 1;
            if (child < this.size && LEQ(this.leq, h[n[child + 1].handle].key, h[n[child].handle].key)) {
                ++child;
            }

            assert child <= this.max;

            int hChild = n[child].handle;
            if (child > this.size || LEQ(this.leq, h[hCurr].key, h[hChild].key)) {
                n[curr].handle = hCurr;
                h[hCurr].node = curr;
                return;
            }

            n[curr].handle = hChild;
            h[hChild].node = curr;
            curr = child;
        }
    }

    void FloatUp(int curr) {
        PQnode[] n = this.nodes;
        PQhandleElem[] h = this.handles;
        int hCurr = n[curr].handle;

        while(true) {
            int parent = curr >> 1;
            int hParent = n[parent].handle;
            if (parent == 0 || LEQ(this.leq, h[hParent].key, h[hCurr].key)) {
                n[curr].handle = hCurr;
                h[hCurr].node = curr;
                return;
            }

            n[curr].handle = hParent;
            h[hParent].node = curr;
            curr = parent;
        }
    }

    boolean pqInit() {
        for(int i = this.size; i >= 1; --i) {
            this.FloatDown(i);
        }

        this.initialized = true;
        return true;
    }

    int pqInsert(Object keyNew) {
        int curr = ++this.size;
        if (curr * 2 > this.max) {
            PQnode[] saveNodes = this.nodes;
            PQhandleElem[] saveHandles = this.handles;
            this.max <<= 1;
            PQnode[] pqNodes = new PQnode[this.max + 1];
            System.arraycopy(this.nodes, 0, pqNodes, 0, this.nodes.length);

            for(int i = this.nodes.length; i < pqNodes.length; ++i) {
                pqNodes[i] = new PQnode();
            }

            this.nodes = pqNodes;
            if (this.nodes == null) {
                this.nodes = saveNodes;
                return 2147483647;
            }

            PQhandleElem[] pqHandles = new PQhandleElem[this.max + 1];
            System.arraycopy(this.handles, 0, pqHandles, 0, this.handles.length);

            for(int i = this.handles.length; i < pqHandles.length; ++i) {
                pqHandles[i] = new PQhandleElem();
            }

            this.handles = pqHandles;
            if (this.handles == null) {
                this.handles = saveHandles;
                return 2147483647;
            }
        }

        int free;
        if (this.freeList == 0) {
            free = curr;
        } else {
            free = this.freeList;
            this.freeList = this.handles[free].node;
        }

        this.nodes[curr].handle = free;
        this.handles[free].node = curr;
        this.handles[free].key = keyNew;
        if (this.initialized) {
            this.FloatUp(curr);
        }

        assert free != 2147483647;

        return free;
    }

    Object pqExtractMin() {
        PQnode[] n = this.nodes;
        PQhandleElem[] h = this.handles;
        int hMin = n[1].handle;
        Object min = h[hMin].key;
        if (this.size > 0) {
            n[1].handle = n[this.size].handle;
            h[n[1].handle].node = 1;
            h[hMin].key = null;
            h[hMin].node = this.freeList;
            this.freeList = hMin;
            if (--this.size > 0) {
                this.FloatDown(1);
            }
        }

        return min;
    }

    void pqDelete(int hCurr) {
        PQnode[] n = this.nodes;
        PQhandleElem[] h = this.handles;

        assert hCurr >= 1 && hCurr <= this.max && h[hCurr].key != null;

        int curr = h[hCurr].node;
        n[curr].handle = n[this.size].handle;
        h[n[curr].handle].node = curr;
        if (curr <= --this.size) {
            if (curr > 1 && !LEQ(this.leq, h[n[curr >> 1].handle].key, h[n[curr].handle].key)) {
                this.FloatUp(curr);
            } else {
                this.FloatDown(curr);
            }
        }

        h[hCurr].key = null;
        h[hCurr].node = this.freeList;
        this.freeList = hCurr;
    }

    Object pqMinimum() {
        return this.handles[this.nodes[1].handle].key;
    }

    boolean pqIsEmpty() {
        return this.size == 0;
    }
}

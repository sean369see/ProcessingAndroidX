package processing.opengl;

import java.nio.Buffer;
import processing.opengl.PGraphicsOpenGL.GLResourceVertexBuffer;

public class VertexBuffer {
    protected static final int INIT_VERTEX_BUFFER_SIZE = 256;
    protected static final int INIT_INDEX_BUFFER_SIZE = 512;
    public int glId;
    int target;
    int elementSize;
    int ncoords;
    boolean index;
    protected PGL pgl;
    protected int context;
    private GLResourceVertexBuffer glres;

    VertexBuffer(PGraphicsOpenGL pg, int target, int ncoords, int esize) {
        this(pg, target, ncoords, esize, false);
    }

    VertexBuffer(PGraphicsOpenGL pg, int target, int ncoords, int esize, boolean index) {
        this.pgl = pg.pgl;
        this.context = this.pgl.createEmptyContext();
        this.target = target;
        this.ncoords = ncoords;
        this.elementSize = esize;
        this.index = index;
        this.create();
        this.init();
    }

    protected void create() {
        this.context = this.pgl.getCurrentContext();
        this.glres = new GLResourceVertexBuffer(this);
    }

    protected void init() {
        int size = this.index ? this.ncoords * 512 * this.elementSize : this.ncoords * 256 * this.elementSize;
        this.pgl.bindBuffer(this.target, this.glId);
        this.pgl.bufferData(this.target, size, (Buffer)null, PGL.STATIC_DRAW);
    }

    protected void dispose() {
        if (this.glres != null) {
            this.glres.dispose();
            this.glId = 0;
            this.glres = null;
        }

    }

    protected boolean contextIsOutdated() {
        boolean outdated = !this.pgl.contextIsCurrent(this.context);
        if (outdated) {
            this.dispose();
        }

        return outdated;
    }
}

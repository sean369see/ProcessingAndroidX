package processing.opengl.tess;

class ActiveRegion {
    GLUhalfEdge eUp;
    DictNode nodeUp;
    int windingNumber;
    boolean inside;
    boolean sentinel;
    boolean dirty;
    boolean fixUpperEdge;

    ActiveRegion() {
    }
}

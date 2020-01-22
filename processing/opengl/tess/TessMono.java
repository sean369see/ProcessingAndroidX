package processing.opengl.tess;

class TessMono {
    TessMono() {
    }

    static boolean __gl_meshTessellateMonoRegion(GLUface face, boolean avoidDegenerateTris) {
        GLUhalfEdge up = face.anEdge;

        assert up.Lnext != up && up.Lnext.Lnext != up;

        while(Geom.VertLeq(up.Sym.Org, up.Org)) {
            up = up.Onext.Sym;
        }

        while(Geom.VertLeq(up.Org, up.Sym.Org)) {
            up = up.Lnext;
        }

        GLUhalfEdge lo = up.Onext.Sym;
        boolean mustConnect = false;

        GLUhalfEdge tempHalfEdge;
        while(up.Lnext != lo) {
            if (avoidDegenerateTris && !mustConnect) {
                if (Geom.EdgeCos(lo.Lnext.Org, lo.Org, lo.Lnext.Lnext.Org) <= -0.99999D) {
                    do {
                        lo = lo.Onext.Sym;
                        mustConnect = true;
                    } while(up.Lnext != lo && Geom.EdgeCos(lo.Lnext.Org, lo.Org, lo.Lnext.Lnext.Org) <= -0.99999D);
                } else if (Geom.EdgeCos(up.Onext.Sym.Org, up.Org, up.Onext.Sym.Onext.Sym.Org) <= -0.99999D) {
                    do {
                        up = up.Lnext;
                        mustConnect = true;
                    } while(up.Lnext != lo && Geom.EdgeCos(up.Onext.Sym.Org, up.Org, up.Onext.Sym.Onext.Sym.Org) <= -0.99999D);
                }

                if (up.Lnext == lo) {
                    break;
                }
            }

            if (Geom.VertLeq(up.Sym.Org, lo.Org)) {
                while(lo.Lnext != up && (Geom.EdgeGoesLeft(lo.Lnext) || Geom.EdgeSign(lo.Org, lo.Sym.Org, lo.Lnext.Sym.Org) <= 0.0D)) {
                    tempHalfEdge = Mesh.__gl_meshConnect(lo.Lnext, lo);
                    mustConnect = false;
                    if (tempHalfEdge == null) {
                        return false;
                    }

                    lo = tempHalfEdge.Sym;
                }

                lo = lo.Onext.Sym;
            } else {
                while(lo.Lnext != up && (Geom.EdgeGoesRight(up.Onext.Sym) || Geom.EdgeSign(up.Sym.Org, up.Org, up.Onext.Sym.Org) >= 0.0D)) {
                    tempHalfEdge = Mesh.__gl_meshConnect(up, up.Onext.Sym);
                    mustConnect = false;
                    if (tempHalfEdge == null) {
                        return false;
                    }

                    up = tempHalfEdge.Sym;
                }

                up = up.Lnext;
            }
        }

        assert lo.Lnext != up;

        while(lo.Lnext.Lnext != up) {
            tempHalfEdge = Mesh.__gl_meshConnect(lo.Lnext, lo);
            if (tempHalfEdge == null) {
                return false;
            }

            lo = tempHalfEdge.Sym;
        }

        return true;
    }

    public static boolean __gl_meshTessellateInterior(GLUmesh mesh, boolean avoidDegenerateTris) {
        GLUface next;
        for(GLUface f = mesh.fHead.next; f != mesh.fHead; f = next) {
            next = f.next;
            if (f.inside && !__gl_meshTessellateMonoRegion(f, avoidDegenerateTris)) {
                return false;
            }
        }

        return true;
    }

    public static void __gl_meshDiscardExterior(GLUmesh mesh) {
        GLUface next;
        for(GLUface f = mesh.fHead.next; f != mesh.fHead; f = next) {
            next = f.next;
            if (!f.inside) {
                Mesh.__gl_meshZapFace(f);
            }
        }

    }

    public static boolean __gl_meshSetWindingNumber(GLUmesh mesh, int value, boolean keepOnlyBoundary) {
        GLUhalfEdge eNext;
        for(GLUhalfEdge e = mesh.eHead.next; e != mesh.eHead; e = eNext) {
            eNext = e.next;
            if (e.Sym.Lface.inside != e.Lface.inside) {
                e.winding = e.Lface.inside ? value : -value;
            } else if (!keepOnlyBoundary) {
                e.winding = 0;
            } else if (!Mesh.__gl_meshDelete(e)) {
                return false;
            }
        }

        return true;
    }
}

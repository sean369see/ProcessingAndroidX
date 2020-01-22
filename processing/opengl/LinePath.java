package processing.opengl;

import processing.core.PMatrix2D;

public class LinePath {
    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;
    public static final byte SEG_MOVETO = 0;
    public static final byte SEG_LINETO = 1;
    public static final byte SEG_CLOSE = 2;
    public static final int JOIN_MITER = 0;
    public static final int JOIN_ROUND = 1;
    public static final int JOIN_BEVEL = 2;
    public static final int CAP_BUTT = 0;
    public static final int CAP_ROUND = 1;
    public static final int CAP_SQUARE = 2;
    private static PMatrix2D identity = new PMatrix2D();
    private static float defaultMiterlimit = 10.0F;
    static final int INIT_SIZE = 20;
    static final int EXPAND_MAX = 500;
    protected byte[] pointTypes;
    protected float[] floatCoords;
    protected int[] pointColors;
    protected int numTypes;
    protected int numCoords;
    protected int windingRule;

    public LinePath() {
        this(1, 20);
    }

    public LinePath(int rule) {
        this(rule, 20);
    }

    public LinePath(int rule, int initialCapacity) {
        this.setWindingRule(rule);
        this.pointTypes = new byte[initialCapacity];
        this.floatCoords = new float[initialCapacity * 2];
        this.pointColors = new int[initialCapacity];
    }

    void needRoom(boolean needMove, int newPoints) {
        if (needMove && this.numTypes == 0) {
            throw new RuntimeException("missing initial moveto in path definition");
        } else {
            int size = this.pointTypes.length;
            int grow;
            if (this.numTypes >= size) {
                grow = size;
                if (size > 500) {
                    grow = 500;
                }

                this.pointTypes = copyOf(this.pointTypes, size + grow);
            }

            size = this.floatCoords.length;
            if (this.numCoords + newPoints * 2 > size) {
                grow = size;
                if (size > 1000) {
                    grow = 1000;
                }

                if (grow < newPoints * 2) {
                    grow = newPoints * 2;
                }

                this.floatCoords = copyOf(this.floatCoords, size + grow);
            }

            size = this.pointColors.length;
            if (this.numCoords / 2 + newPoints > size) {
                grow = size;
                if (size > 500) {
                    grow = 500;
                }

                if (grow < newPoints) {
                    grow = newPoints;
                }

                this.pointColors = copyOf(this.pointColors, size + grow);
            }

        }
    }

    public final void moveTo(float x, float y, int c) {
        if (this.numTypes > 0 && this.pointTypes[this.numTypes - 1] == 0) {
            this.floatCoords[this.numCoords - 2] = x;
            this.floatCoords[this.numCoords - 1] = y;
            this.pointColors[this.numCoords / 2 - 1] = c;
        } else {
            this.needRoom(false, 1);
            this.pointTypes[this.numTypes++] = 0;
            this.floatCoords[this.numCoords++] = x;
            this.floatCoords[this.numCoords++] = y;
            this.pointColors[this.numCoords / 2 - 1] = c;
        }

    }

    public final void lineTo(float x, float y, int c) {
        this.needRoom(true, 1);
        this.pointTypes[this.numTypes++] = 1;
        this.floatCoords[this.numCoords++] = x;
        this.floatCoords[this.numCoords++] = y;
        this.pointColors[this.numCoords / 2 - 1] = c;
    }

    public LinePath.PathIterator getPathIterator() {
        return new LinePath.PathIterator(this);
    }

    public final void closePath() {
        if (this.numTypes == 0 || this.pointTypes[this.numTypes - 1] != 2) {
            this.needRoom(false, 0);
            this.pointTypes[this.numTypes++] = 2;
        }

    }

    public final int getWindingRule() {
        return this.windingRule;
    }

    public final void setWindingRule(int rule) {
        if (rule != 0 && rule != 1) {
            throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");
        } else {
            this.windingRule = rule;
        }
    }

    public final void reset() {
        this.numTypes = this.numCoords = 0;
    }

    public static LinePath createStrokedPath(LinePath src, float weight, int caps, int join) {
        return createStrokedPath(src, weight, caps, join, defaultMiterlimit, (PMatrix2D)null);
    }

    public static LinePath createStrokedPath(LinePath src, float weight, int caps, int join, float miterlimit) {
        return createStrokedPath(src, weight, caps, join, miterlimit, (PMatrix2D)null);
    }

    public static LinePath createStrokedPath(LinePath src, float weight, int caps, int join, float miterlimit, PMatrix2D transform) {
        final LinePath dest = new LinePath();
        strokeTo(src, weight, caps, join, miterlimit, transform, new LineStroker() {
            public void moveTo(int x0, int y0, int c0) {
                dest.moveTo(LinePath.S15_16ToFloat(x0), LinePath.S15_16ToFloat(y0), c0);
            }

            public void lineJoin() {
            }

            public void lineTo(int x1, int y1, int c1) {
                dest.lineTo(LinePath.S15_16ToFloat(x1), LinePath.S15_16ToFloat(y1), c1);
            }

            public void close() {
                dest.closePath();
            }

            public void end() {
            }
        });
        return dest;
    }

    private static void strokeTo(LinePath src, float width, int caps, int join, float miterlimit, PMatrix2D transform, LineStroker lsink) {
        lsink = new LineStroker(lsink, FloatToS15_16(width), caps, join, FloatToS15_16(miterlimit), transform == null ? identity : transform);
        LinePath.PathIterator pi = src.getPathIterator();
        pathTo(pi, lsink);
    }

    private static void pathTo(LinePath.PathIterator pi, LineStroker lsink) {
        for(float[] coords = new float[6]; !pi.isDone(); pi.next()) {
            int color;
            switch(pi.currentSegment(coords)) {
                case 0:
                    color = (int)coords[2] << 24 | (int)coords[3] << 16 | (int)coords[4] << 8 | (int)coords[5];
                    lsink.moveTo(FloatToS15_16(coords[0]), FloatToS15_16(coords[1]), color);
                    break;
                case 1:
                    color = (int)coords[2] << 24 | (int)coords[3] << 16 | (int)coords[4] << 8 | (int)coords[5];
                    lsink.lineJoin();
                    lsink.lineTo(FloatToS15_16(coords[0]), FloatToS15_16(coords[1]), color);
                    break;
                case 2:
                    lsink.lineJoin();
                    lsink.close();
                    break;
                default:
                    throw new InternalError("unknown flattened segment type");
            }
        }

        lsink.end();
    }

    public static float[] copyOf(float[] source, int length) {
        float[] target = new float[length];

        for(int i = 0; i < target.length; ++i) {
            if (i > source.length - 1) {
                target[i] = 0.0F;
            } else {
                target[i] = source[i];
            }
        }

        return target;
    }

    public static byte[] copyOf(byte[] source, int length) {
        byte[] target = new byte[length];

        for(int i = 0; i < target.length; ++i) {
            if (i > source.length - 1) {
                target[i] = 0;
            } else {
                target[i] = source[i];
            }
        }

        return target;
    }

    public static int[] copyOf(int[] source, int length) {
        int[] target = new int[length];

        for(int i = 0; i < target.length; ++i) {
            if (i > source.length - 1) {
                target[i] = 0;
            } else {
                target[i] = source[i];
            }
        }

        return target;
    }

    public static int isqrt(int x) {
        int fracbits = 16;
        int root = 0;
        int remHi = 0;
        int remLo = x;
        int var5 = 15 + fracbits / 2;

        do {
            remHi = remHi << 2 | remLo >>> 30;
            remLo <<= 2;
            root <<= 1;
            int testdiv = (root << 1) + 1;
            if (remHi >= testdiv) {
                remHi -= testdiv;
                ++root;
            }
        } while(var5-- != 0);

        return root;
    }

    public static long lsqrt(long x) {
        int fracbits = 16;
        long root = 0L;
        long remHi = 0L;
        long remLo = x;
        int var9 = 31 + fracbits / 2;

        do {
            remHi = remHi << 2 | remLo >>> 62;
            remLo <<= 2;
            root <<= 1;
            long testDiv = (root << 1) + 1L;
            if (remHi >= testDiv) {
                remHi -= testDiv;
                ++root;
            }
        } while(var9-- != 0);

        return root;
    }

    public static double hypot(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }

    public static int hypot(int x, int y) {
        return (int)(lsqrt((long)x * (long)x + (long)y * (long)y) + 128L >> 8);
    }

    public static long hypot(long x, long y) {
        return lsqrt(x * x + y * y) + 128L >> 8;
    }

    static int FloatToS15_16(float flt) {
        flt = flt * 65536.0F + 0.5F;
        if (flt <= -4.2949673E9F) {
            return -2147483648;
        } else {
            return flt >= 4.2949673E9F ? 2147483647 : (int)Math.floor((double)flt);
        }
    }

    static float S15_16ToFloat(int fix) {
        return (float)fix / 65536.0F;
    }

    public static class PathIterator {
        float[] floatCoords;
        int typeIdx;
        int pointIdx;
        int colorIdx;
        LinePath path;
        static final int[] curvecoords = new int[]{2, 2, 0};

        PathIterator(LinePath p2df) {
            this.path = p2df;
            this.floatCoords = p2df.floatCoords;
            this.pointIdx = 0;
            this.colorIdx = 0;
        }

        public int currentSegment(float[] coords) {
            int type = this.path.pointTypes[this.typeIdx];
            int numCoords = curvecoords[type];
            if (numCoords > 0) {
                System.arraycopy(this.floatCoords, this.pointIdx, coords, 0, numCoords);
                int color = this.path.pointColors[this.colorIdx];
                coords[numCoords + 0] = (float)(color >> 24 & 255);
                coords[numCoords + 1] = (float)(color >> 16 & 255);
                coords[numCoords + 2] = (float)(color >> 8 & 255);
                coords[numCoords + 3] = (float)(color >> 0 & 255);
            }

            return type;
        }

        public int currentSegment(double[] coords) {
            int type = this.path.pointTypes[this.typeIdx];
            int numCoords = curvecoords[type];
            if (numCoords > 0) {
                int color;
                for(color = 0; color < numCoords; ++color) {
                    coords[color] = (double)this.floatCoords[this.pointIdx + color];
                }

                color = this.path.pointColors[this.colorIdx];
                coords[numCoords + 0] = (double)(color >> 24 & 255);
                coords[numCoords + 1] = (double)(color >> 16 & 255);
                coords[numCoords + 2] = (double)(color >> 8 & 255);
                coords[numCoords + 3] = (double)(color >> 0 & 255);
            }

            return type;
        }

        public int getWindingRule() {
            return this.path.getWindingRule();
        }

        public boolean isDone() {
            return this.typeIdx >= this.path.numTypes;
        }

        public void next() {
            int type = this.path.pointTypes[this.typeIdx++];
            if (0 < curvecoords[type]) {
                this.pointIdx += curvecoords[type];
                ++this.colorIdx;
            }

        }
    }
}

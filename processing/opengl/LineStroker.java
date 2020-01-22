package processing.opengl;

import processing.core.PMatrix2D;

public class LineStroker {
    private LineStroker output;
    private int capStyle;
    private int joinStyle;
    private int m00;
    private int m01;
    private int m10;
    private int m11;
    private int lineWidth2;
    private long scaledLineWidth2;
    private int numPenSegments;
    private int[] pen_dx;
    private int[] pen_dy;
    private boolean[] penIncluded;
    private int[] join;
    private int[] offset = new int[2];
    private int[] reverse = new int[100];
    private int[] miter = new int[2];
    private long miterLimitSq;
    private int prev;
    private int rindex;
    private boolean started;
    private boolean lineToOrigin;
    private boolean joinToOrigin;
    private int sx0;
    private int sy0;
    private int sx1;
    private int sy1;
    private int x0;
    private int y0;
    private int scolor0;
    private int pcolor0;
    private int color0;
    private int mx0;
    private int my0;
    private int omx;
    private int omy;
    private int px0;
    private int py0;
    private double m00_2_m01_2;
    private double m10_2_m11_2;
    private double m00_m10_m01_m11;
    private static final long ROUND_JOIN_THRESHOLD = 100000000L;
    private static final long ROUND_JOIN_INTERNAL_THRESHOLD = 1000000000L;
    boolean joinSegment = false;

    public LineStroker() {
    }

    public LineStroker(LineStroker output, int lineWidth, int capStyle, int joinStyle, int miterLimit, PMatrix2D transform) {
        this.setOutput(output);
        this.setParameters(lineWidth, capStyle, joinStyle, miterLimit, transform);
    }

    public void setOutput(LineStroker output) {
        this.output = output;
    }

    public void setParameters(int lineWidth, int capStyle, int joinStyle, int miterLimit, PMatrix2D transform) {
        this.m00 = LinePath.FloatToS15_16(transform.m00);
        this.m01 = LinePath.FloatToS15_16(transform.m01);
        this.m10 = LinePath.FloatToS15_16(transform.m10);
        this.m11 = LinePath.FloatToS15_16(transform.m11);
        this.lineWidth2 = lineWidth >> 1;
        this.scaledLineWidth2 = (long)this.m00 * (long)this.lineWidth2 >> 16;
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        this.m00_2_m01_2 = (double)this.m00 * (double)this.m00 + (double)this.m01 * (double)this.m01;
        this.m10_2_m11_2 = (double)this.m10 * (double)this.m10 + (double)this.m11 * (double)this.m11;
        this.m00_m10_m01_m11 = (double)this.m00 * (double)this.m10 + (double)this.m01 * (double)this.m11;
        double dm00 = (double)this.m00 / 65536.0D;
        double dm01 = (double)this.m01 / 65536.0D;
        double dm10 = (double)this.m10 / 65536.0D;
        double dm11 = (double)this.m11 / 65536.0D;
        double determinant = dm00 * dm11 - dm01 * dm10;
        if (joinStyle == 0) {
            double limit = (double)miterLimit / 65536.0D * ((double)this.lineWidth2 / 65536.0D) * determinant;
            double limitSq = limit * limit;
            this.miterLimitSq = (long)(limitSq * 65536.0D * 65536.0D);
        }

        this.numPenSegments = (int)(3.14159F * (float)lineWidth / 65536.0F);
        if (this.pen_dx == null || this.pen_dx.length < this.numPenSegments) {
            this.pen_dx = new int[this.numPenSegments];
            this.pen_dy = new int[this.numPenSegments];
            this.penIncluded = new boolean[this.numPenSegments];
            this.join = new int[2 * this.numPenSegments];
        }

        for(int i = 0; i < this.numPenSegments; ++i) {
            double r = (double)lineWidth / 2.0D;
            double theta = (double)(i * 2) * 3.141592653589793D / (double)this.numPenSegments;
            double cos = Math.cos(theta);
            double sin = Math.sin(theta);
            this.pen_dx[i] = (int)(r * (dm00 * cos + dm01 * sin));
            this.pen_dy[i] = (int)(r * (dm10 * cos + dm11 * sin));
        }

        this.prev = 2;
        this.rindex = 0;
        this.started = false;
        this.lineToOrigin = false;
    }

    private void computeOffset(int x0, int y0, int x1, int y1, int[] m) {
        long lx = (long)x1 - (long)x0;
        long ly = (long)y1 - (long)y0;
        int dx;
        int dy;
        if (this.m00 > 0 && this.m00 == this.m11 && this.m01 == 0 & this.m10 == 0) {
            long ilen = LinePath.hypot(lx, ly);
            if (ilen == 0L) {
                dy = 0;
                dx = 0;
            } else {
                dx = (int)(ly * this.scaledLineWidth2 / ilen);
                dy = (int)(-(lx * this.scaledLineWidth2) / ilen);
            }
        } else {
            double dlx = (double)(x1 - x0);
            double dly = (double)(y1 - y0);
            double det = (double)this.m00 * (double)this.m11 - (double)this.m01 * (double)this.m10;
            int sdet = det > 0.0D ? 1 : -1;
            double a = dly * (double)this.m00 - dlx * (double)this.m10;
            double b = dly * (double)this.m01 - dlx * (double)this.m11;
            double dh = LinePath.hypot(a, b);
            double div = (double)(sdet * this.lineWidth2) / (65536.0D * dh);
            double ddx = dly * this.m00_2_m01_2 - dlx * this.m00_m10_m01_m11;
            double ddy = dly * this.m00_m10_m01_m11 - dlx * this.m10_2_m11_2;
            dx = (int)(ddx * div);
            dy = (int)(ddy * div);
        }

        m[0] = dx;
        m[1] = dy;
    }

    private void ensureCapacity(int newrindex) {
        if (this.reverse.length < newrindex) {
            int[] tmp = new int[Math.max(newrindex, 6 * this.reverse.length / 5)];
            System.arraycopy(this.reverse, 0, tmp, 0, this.rindex);
            this.reverse = tmp;
        }

    }

    private boolean isCCW(int x0, int y0, int x1, int y1, int x2, int y2) {
        int dx0 = x1 - x0;
        int dy0 = y1 - y0;
        int dx1 = x2 - x1;
        int dy1 = y2 - y1;
        return (long)dx0 * (long)dy1 < (long)dy0 * (long)dx1;
    }

    private boolean side(int x, int y, int x0, int y0, int x1, int y1) {
        long lx = (long)x;
        long ly = (long)y;
        long lx0 = (long)x0;
        long ly0 = (long)y0;
        long lx1 = (long)x1;
        long ly1 = (long)y1;
        return (ly0 - ly1) * lx + (lx1 - lx0) * ly + (lx0 * ly1 - lx1 * ly0) > 0L;
    }

    private int computeRoundJoin(int cx, int cy, int xa, int ya, int xb, int yb, int side, boolean flip, int[] join) {
        int ncoords = 0;
        boolean centerSide;
        if (side == 0) {
            centerSide = this.side(cx, cy, xa, ya, xb, yb);
        } else {
            centerSide = side == 1;
        }

        int px;
        int py;
        int start;
        for(start = 0; start < this.numPenSegments; ++start) {
            px = cx + this.pen_dx[start];
            py = cy + this.pen_dy[start];
            boolean penSide = this.side(px, py, xa, ya, xb, yb);
            if (penSide != centerSide) {
                this.penIncluded[start] = true;
            } else {
                this.penIncluded[start] = false;
            }
        }

        start = -1;
        int end = -1;

        for(int i = 0; i < this.numPenSegments; ++i) {
            if (this.penIncluded[i] && !this.penIncluded[(i + this.numPenSegments - 1) % this.numPenSegments]) {
                start = i;
            }

            if (this.penIncluded[i] && !this.penIncluded[(i + 1) % this.numPenSegments]) {
                end = i;
            }
        }

        if (end < start) {
            end += this.numPenSegments;
        }

        if (start != -1 && end != -1) {
            long dxa = (long)(cx + this.pen_dx[start] - xa);
            long dya = (long)(cy + this.pen_dy[start] - ya);
            long dxb = (long)(cx + this.pen_dx[start] - xb);
            long dyb = (long)(cy + this.pen_dy[start] - yb);
            boolean rev = dxa * dxa + dya * dya > dxb * dxb + dyb * dyb;
            int i = rev ? end : start;
            int incr = rev ? -1 : 1;

            while(true) {
                int idx = i % this.numPenSegments;
                px = cx + this.pen_dx[idx];
                py = cy + this.pen_dy[idx];
                join[ncoords++] = px;
                join[ncoords++] = py;
                if (i == (rev ? start : end)) {
                    break;
                }

                i += incr;
            }
        }

        return ncoords / 2;
    }

    private void drawRoundJoin(int x, int y, int omx, int omy, int mx, int my, int side, int color, boolean flip, boolean rev, long threshold) {
        if ((omx != 0 || omy != 0) && (mx != 0 || my != 0)) {
            long domx = (long)omx - (long)mx;
            long domy = (long)omy - (long)my;
            long len = domx * domx + domy * domy;
            if (len >= threshold) {
                if (rev) {
                    omx = -omx;
                    omy = -omy;
                    mx = -mx;
                    my = -my;
                }

                int bx0 = x + omx;
                int by0 = y + omy;
                int bx1 = x + mx;
                int by1 = y + my;
                int npoints = this.computeRoundJoin(x, y, bx0, by0, bx1, by1, side, flip, this.join);

                for(int i = 0; i < npoints; ++i) {
                    this.emitLineTo(this.join[2 * i], this.join[2 * i + 1], color, rev);
                }

            }
        }
    }

    private void computeMiter(int ix0, int iy0, int ix1, int iy1, int ix0p, int iy0p, int ix1p, int iy1p, int[] m) {
        long x0 = (long)ix0;
        long y0 = (long)iy0;
        long x1 = (long)ix1;
        long y1 = (long)iy1;
        long x0p = (long)ix0p;
        long y0p = (long)iy0p;
        long x1p = (long)ix1p;
        long y1p = (long)iy1p;
        long x10 = x1 - x0;
        long y10 = y1 - y0;
        long x10p = x1p - x0p;
        long y10p = y1p - y0p;
        long den = x10 * y10p - x10p * y10 >> 16;
        if (den == 0L) {
            m[0] = ix0;
            m[1] = iy0;
        } else {
            long t = x1p * (y0 - y0p) - x0 * y10p + x0p * (y1p - y0) >> 16;
            m[0] = (int)(x0 + t * x10 / den);
            m[1] = (int)(y0 + t * y10 / den);
        }
    }

    private void drawMiter(int px0, int py0, int x0, int y0, int x1, int y1, int omx, int omy, int mx, int my, int color, boolean rev) {
        if (mx != omx || my != omy) {
            if (px0 != x0 || py0 != y0) {
                if (x0 != x1 || y0 != y1) {
                    if (rev) {
                        omx = -omx;
                        omy = -omy;
                        mx = -mx;
                        my = -my;
                    }

                    this.computeMiter(px0 + omx, py0 + omy, x0 + omx, y0 + omy, x0 + mx, y0 + my, x1 + mx, y1 + my, this.miter);
                    long dx = (long)this.miter[0] - (long)x0;
                    long dy = (long)this.miter[1] - (long)y0;
                    long a = dy * (long)this.m00 - dx * (long)this.m10 >> 16;
                    long b = dy * (long)this.m01 - dx * (long)this.m11 >> 16;
                    long lenSq = a * a + b * b;
                    if (lenSq < this.miterLimitSq) {
                        this.emitLineTo(this.miter[0], this.miter[1], color, rev);
                    }

                }
            }
        }
    }

    public void moveTo(int x0, int y0, int c0) {
        if (this.lineToOrigin) {
            this.lineToImpl(this.sx0, this.sy0, this.scolor0, this.joinToOrigin);
            this.lineToOrigin = false;
        }

        if (this.prev == 1) {
            this.finish();
        }

        this.sx0 = this.x0 = x0;
        this.sy0 = this.y0 = y0;
        this.scolor0 = this.color0 = c0;
        this.rindex = 0;
        this.started = false;
        this.joinSegment = false;
        this.prev = 0;
    }

    public void lineJoin() {
        this.joinSegment = true;
    }

    public void lineTo(int x1, int y1, int c1) {
        if (this.lineToOrigin) {
            if (x1 == this.sx0 && y1 == this.sy0) {
                return;
            }

            this.lineToImpl(this.sx0, this.sy0, this.scolor0, this.joinToOrigin);
            this.lineToOrigin = false;
        } else {
            if (x1 == this.x0 && y1 == this.y0) {
                return;
            }

            if (x1 == this.sx0 && y1 == this.sy0) {
                this.lineToOrigin = true;
                this.joinToOrigin = this.joinSegment;
                this.joinSegment = false;
                return;
            }
        }

        this.lineToImpl(x1, y1, c1, this.joinSegment);
        this.joinSegment = false;
    }

    private void lineToImpl(int x1, int y1, int c1, boolean joinSegment) {
        this.computeOffset(this.x0, this.y0, x1, y1, this.offset);
        int mx = this.offset[0];
        int my = this.offset[1];
        if (!this.started) {
            this.emitMoveTo(this.x0 + mx, this.y0 + my, this.color0);
            this.sx1 = x1;
            this.sy1 = y1;
            this.mx0 = mx;
            this.my0 = my;
            this.started = true;
        } else {
            boolean ccw = this.isCCW(this.px0, this.py0, this.x0, this.y0, x1, y1);
            if (joinSegment) {
                if (this.joinStyle == 0) {
                    this.drawMiter(this.px0, this.py0, this.x0, this.y0, x1, y1, this.omx, this.omy, mx, my, this.color0, ccw);
                } else if (this.joinStyle == 1) {
                    this.drawRoundJoin(this.x0, this.y0, this.omx, this.omy, mx, my, 0, this.color0, false, ccw, 100000000L);
                }
            } else {
                this.drawRoundJoin(this.x0, this.y0, this.omx, this.omy, mx, my, 0, this.color0, false, ccw, 1000000000L);
            }

            this.emitLineTo(this.x0, this.y0, this.color0, !ccw);
        }

        this.emitLineTo(this.x0 + mx, this.y0 + my, this.color0, false);
        this.emitLineTo(x1 + mx, y1 + my, c1, false);
        this.emitLineTo(this.x0 - mx, this.y0 - my, this.color0, true);
        this.emitLineTo(x1 - mx, y1 - my, c1, true);
        this.omx = mx;
        this.omy = my;
        this.px0 = this.x0;
        this.py0 = this.y0;
        this.pcolor0 = this.color0;
        this.x0 = x1;
        this.y0 = y1;
        this.color0 = c1;
        this.prev = 1;
    }

    public void close() {
        if (this.lineToOrigin) {
            this.lineToOrigin = false;
        }

        if (!this.started) {
            this.finish();
        } else {
            this.computeOffset(this.x0, this.y0, this.sx0, this.sy0, this.offset);
            int mx = this.offset[0];
            int my = this.offset[1];
            boolean ccw = this.isCCW(this.px0, this.py0, this.x0, this.y0, this.sx0, this.sy0);
            if (this.joinSegment) {
                if (this.joinStyle == 0) {
                    this.drawMiter(this.px0, this.py0, this.x0, this.y0, this.sx0, this.sy0, this.omx, this.omy, mx, my, this.pcolor0, ccw);
                } else if (this.joinStyle == 1) {
                    this.drawRoundJoin(this.x0, this.y0, this.omx, this.omy, mx, my, 0, this.color0, false, ccw, 100000000L);
                }
            } else {
                this.drawRoundJoin(this.x0, this.y0, this.omx, this.omy, mx, my, 0, this.color0, false, ccw, 1000000000L);
            }

            this.emitLineTo(this.x0 + mx, this.y0 + my, this.color0);
            this.emitLineTo(this.sx0 + mx, this.sy0 + my, this.scolor0);
            ccw = this.isCCW(this.x0, this.y0, this.sx0, this.sy0, this.sx1, this.sy1);
            if (!ccw) {
                if (this.joinStyle == 0) {
                    this.drawMiter(this.x0, this.y0, this.sx0, this.sy0, this.sx1, this.sy1, mx, my, this.mx0, this.my0, this.color0, false);
                } else if (this.joinStyle == 1) {
                    this.drawRoundJoin(this.sx0, this.sy0, mx, my, this.mx0, this.my0, 0, this.scolor0, false, false, 100000000L);
                }
            }

            this.emitLineTo(this.sx0 + this.mx0, this.sy0 + this.my0, this.scolor0);
            this.emitLineTo(this.sx0 - this.mx0, this.sy0 - this.my0, this.scolor0);
            if (ccw) {
                if (this.joinStyle == 0) {
                    this.drawMiter(this.x0, this.y0, this.sx0, this.sy0, this.sx1, this.sy1, -mx, -my, -this.mx0, -this.my0, this.color0, false);
                } else if (this.joinStyle == 1) {
                    this.drawRoundJoin(this.sx0, this.sy0, -mx, -my, -this.mx0, -this.my0, 0, this.scolor0, true, false, 100000000L);
                }
            }

            this.emitLineTo(this.sx0 - mx, this.sy0 - my, this.scolor0);
            this.emitLineTo(this.x0 - mx, this.y0 - my, this.color0);

            for(int i = this.rindex - 3; i >= 0; i -= 3) {
                this.emitLineTo(this.reverse[i], this.reverse[i + 1], this.reverse[i + 2]);
            }

            this.x0 = this.sx0;
            this.y0 = this.sy0;
            this.rindex = 0;
            this.started = false;
            this.joinSegment = false;
            this.prev = 2;
            this.emitClose();
        }
    }

    public void end() {
        if (this.lineToOrigin) {
            this.lineToImpl(this.sx0, this.sy0, this.scolor0, this.joinToOrigin);
            this.lineToOrigin = false;
        }

        if (this.prev == 1) {
            this.finish();
        }

        this.output.end();
        this.joinSegment = false;
        this.prev = 0;
    }

    long lineLength(long ldx, long ldy) {
        long ldet = (long)this.m00 * (long)this.m11 - (long)this.m01 * (long)this.m10 >> 16;
        long la = (ldy * (long)this.m00 - ldx * (long)this.m10) / ldet;
        long lb = (ldy * (long)this.m01 - ldx * (long)this.m11) / ldet;
        long llen = (long)((int)LinePath.hypot(la, lb));
        return llen;
    }

    private void finish() {
        long ldx;
        long ldy;
        long llen;
        long s;
        int capx;
        int capy;
        if (this.capStyle == 1) {
            this.drawRoundJoin(this.x0, this.y0, this.omx, this.omy, -this.omx, -this.omy, 1, this.color0, false, false, 100000000L);
        } else if (this.capStyle == 2) {
            ldx = (long)(this.px0 - this.x0);
            ldy = (long)(this.py0 - this.y0);
            llen = this.lineLength(ldx, ldy);
            if (0L < llen) {
                s = (long)this.lineWidth2 * 65536L / llen;
                capx = this.x0 - (int)(ldx * s >> 16);
                capy = this.y0 - (int)(ldy * s >> 16);
                this.emitLineTo(capx + this.omx, capy + this.omy, this.color0);
                this.emitLineTo(capx - this.omx, capy - this.omy, this.color0);
            }
        }

        for(int i = this.rindex - 3; i >= 0; i -= 3) {
            this.emitLineTo(this.reverse[i], this.reverse[i + 1], this.reverse[i + 2]);
        }

        this.rindex = 0;
        if (this.capStyle == 1) {
            this.drawRoundJoin(this.sx0, this.sy0, -this.mx0, -this.my0, this.mx0, this.my0, 1, this.scolor0, false, false, 100000000L);
        } else if (this.capStyle == 2) {
            ldx = (long)(this.sx1 - this.sx0);
            ldy = (long)(this.sy1 - this.sy0);
            llen = this.lineLength(ldx, ldy);
            if (0L < llen) {
                s = (long)this.lineWidth2 * 65536L / llen;
                capx = this.sx0 - (int)(ldx * s >> 16);
                capy = this.sy0 - (int)(ldy * s >> 16);
                this.emitLineTo(capx - this.mx0, capy - this.my0, this.scolor0);
                this.emitLineTo(capx + this.mx0, capy + this.my0, this.scolor0);
            }
        }

        this.emitClose();
        this.joinSegment = false;
    }

    private void emitMoveTo(int x0, int y0, int c0) {
        this.output.moveTo(x0, y0, c0);
    }

    private void emitLineTo(int x1, int y1, int c1) {
        this.output.lineTo(x1, y1, c1);
    }

    private void emitLineTo(int x1, int y1, int c1, boolean rev) {
        if (rev) {
            this.ensureCapacity(this.rindex + 3);
            this.reverse[this.rindex++] = x1;
            this.reverse[this.rindex++] = y1;
            this.reverse[this.rindex++] = c1;
        } else {
            this.emitLineTo(x1, y1, c1);
        }

    }

    private void emitClose() {
        this.output.close();
    }
}

/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-19 The Processing Foundation
  Copyright (c) 2006-12 Ben Fry and Casey Reas
  Copyright (c) 2004-06 Michael Chang

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1 as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

/*
  AndroidX modification project by Xuan "Sean" Li
*/
  
package processing.core;

import android.graphics.Matrix;
import java.util.HashMap;
import java.util.Map;
import processing.data.IntDict;
import processing.data.StringDict;
import processing.data.XML;

public class PShapeSVG extends PShape {
    XML element;
    protected float opacity;
    float strokeOpacity;
    float fillOpacity;
    protected float svgWidth;
    protected float svgHeight;
    protected float svgSizeXY;
    protected PShapeSVG.Gradient strokeGradient;
    String strokeName;
    protected PShapeSVG.Gradient fillGradient;
    String fillName;
    protected static IntDict colorNames = new IntDict(new Object[][]{{"aqua", 65535}, {"black", 0}, {"blue", 255}, {"fuchsia", 16711935}, {"gray", 8421504}, {"grey", 8421504}, {"green", 32768}, {"lime", 65280}, {"maroon", 8388608}, {"navy", 128}, {"olive", 8421376}, {"purple", 8388736}, {"red", 16711680}, {"silver", 12632256}, {"teal", 32896}, {"white", 16777215}, {"yellow", 16776960}});

    public PShapeSVG(XML svg) {
        this((PShapeSVG)null, svg, true);
        if (!svg.getName().equals("svg")) {
            if (svg.getName().toLowerCase().equals("html")) {
                throw new RuntimeException("This appears to be a web page, not an SVG file.");
            } else {
                throw new RuntimeException("The root node is not <svg>, it's <" + svg.getName() + ">");
            }
        }
    }

    protected PShapeSVG(PShapeSVG parent, XML properties, boolean parseKids) {
        this.setParent(parent);
        String unitWidth;
        String unitHeight;
        if (properties.getName().equals("svg")) {
            unitWidth = properties.getString("width");
            unitHeight = properties.getString("height");
            if (unitWidth != null) {
                this.width = parseUnitSize(unitWidth, 100.0F);
            }

            if (unitHeight != null) {
                this.height = parseUnitSize(unitHeight, 100.0F);
            }

            String viewBoxStr = properties.getString("viewBox");
            if (viewBoxStr != null) {
                float[] viewBox = PApplet.parseFloat(PApplet.splitTokens(viewBoxStr));
                if (unitWidth != null && unitHeight != null) {
                    if (this.matrix == null) {
                        this.matrix = new PMatrix2D();
                    }

                    this.matrix.scale(this.width / viewBox[2], this.height / viewBox[3]);
                    this.matrix.translate(-viewBox[0], -viewBox[1]);
                } else {
                    this.width = viewBox[2];
                    this.height = viewBox[3];
                }
            }

            if (this.width < 0.0F || this.height < 0.0F) {
                throw new RuntimeException("<svg>: width (" + this.width + ") and height (" + this.height + ") must not be negative.");
            }

            if ((unitWidth == null || unitHeight == null) && viewBoxStr == null) {
                PGraphics.showWarning("The width and/or height is not readable in the <svg> tag of this file.");
                this.width = 1.0F;
                this.height = 1.0F;
            }

            this.svgWidth = this.width;
            this.svgHeight = this.height;
            this.svgSizeXY = PApplet.sqrt((this.svgWidth * this.svgWidth + this.svgHeight * this.svgHeight) / 2.0F);
        }

        this.element = properties;
        this.name = properties.getString("id");
        if (this.name != null) {
            while(true) {
                String[] m = PApplet.match(this.name, "_x([A-Za-z0-9]{2})_");
                if (m == null) {
                    break;
                }

                char repair = (char)PApplet.unhex(m[1]);
                this.name = this.name.replace(m[0], "" + repair);
            }
        }

        unitWidth = properties.getString("display", "inline");
        this.visible = !unitWidth.equals("none");
        unitHeight = properties.getString("transform");
        if (unitHeight != null) {
            if (this.matrix == null) {
                this.matrix = parseTransform(unitHeight);
            } else {
                this.matrix.preApply(parseTransform(unitHeight));
            }
        }

        if (parseKids) {
            this.parseColors(properties);
            this.parseChildren(properties);
        }

    }

    protected void setParent(PShapeSVG parent) {
        this.parent = parent;
        if (parent == null) {
            this.stroke = false;
            this.strokeColor = -16777216;
            this.strokeWeight = 1.0F;
            this.strokeCap = 1;
            this.strokeJoin = 8;
            this.strokeGradient = null;
            this.strokeName = null;
            this.fill = true;
            this.fillColor = -16777216;
            this.fillGradient = null;
            this.fillName = null;
            this.strokeOpacity = 1.0F;
            this.fillOpacity = 1.0F;
            this.opacity = 1.0F;
        } else {
            this.stroke = parent.stroke;
            this.strokeColor = parent.strokeColor;
            this.strokeWeight = parent.strokeWeight;
            this.strokeCap = parent.strokeCap;
            this.strokeJoin = parent.strokeJoin;
            this.strokeGradient = parent.strokeGradient;
            this.strokeName = parent.strokeName;
            this.fill = parent.fill;
            this.fillColor = parent.fillColor;
            this.fillGradient = parent.fillGradient;
            this.fillName = parent.fillName;
            this.svgWidth = parent.svgWidth;
            this.svgHeight = parent.svgHeight;
            this.svgSizeXY = parent.svgSizeXY;
            this.opacity = parent.opacity;
        }

        this.rectMode = 0;
        this.ellipseMode = 0;
    }

    protected PShapeSVG createShape(PShapeSVG parent, XML properties, boolean parseKids) {
        return new PShapeSVG(parent, properties, parseKids);
    }

    protected void parseChildren(XML graphics) {
        XML[] elements = graphics.getChildren();
        this.children = new PShape[elements.length];
        this.childCount = 0;
        XML[] var3 = elements;
        int var4 = elements.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            XML elem = var3[var5];
            PShape kid = this.parseChild(elem);
            if (kid != null) {
                this.addChild(kid);
            }
        }

        this.children = (PShape[])((PShape[])PApplet.subset(this.children, 0, this.childCount));
    }

    protected PShape parseChild(XML elem) {
        String name = elem.getName();
        PShapeSVG shape = null;
        if (name != null) {
            if (name.equals("g")) {
                shape = this.createShape(this, elem, true);
            } else if (name.equals("defs")) {
                shape = this.createShape(this, elem, true);
            } else if (name.equals("line")) {
                shape = this.createShape(this, elem, true);
                shape.parseLine();
            } else if (name.equals("circle")) {
                shape = this.createShape(this, elem, true);
                shape.parseEllipse(true);
            } else if (name.equals("ellipse")) {
                shape = this.createShape(this, elem, true);
                shape.parseEllipse(false);
            } else if (name.equals("rect")) {
                shape = this.createShape(this, elem, true);
                shape.parseRect();
            } else if (name.equals("polygon")) {
                shape = this.createShape(this, elem, true);
                shape.parsePoly(true);
            } else if (name.equals("polyline")) {
                shape = this.createShape(this, elem, true);
                shape.parsePoly(false);
            } else if (name.equals("path")) {
                shape = this.createShape(this, elem, true);
                shape.parsePath();
            } else {
                if (name.equals("radialGradient")) {
                    return new PShapeSVG.RadialGradient(this, elem);
                }

                if (name.equals("linearGradient")) {
                    return new PShapeSVG.LinearGradient(this, elem);
                }

                if (name.equals("font")) {
                    return new PShapeSVG.Font(this, elem);
                }

                if (name.equals("text")) {
                    PGraphics.showWarning("Text and fonts in SVG files are not currently supported, convert text to outlines instead.");
                } else if (name.equals("filter")) {
                    PGraphics.showWarning("Filters are not supported.");
                } else if (name.equals("mask")) {
                    PGraphics.showWarning("Masks are not supported.");
                } else if (name.equals("pattern")) {
                    PGraphics.showWarning("Patterns are not supported.");
                } else if (!name.equals("stop") && !name.equals("sodipodi:namedview")) {
                    if (name.equals("metadata") || name.equals("title") || name.equals("desc")) {
                        return null;
                    }

                    if (!name.startsWith("#")) {
                        PGraphics.showWarning("Ignoring <" + name + "> tag.");
                    }
                }
            }
        }

        return shape;
    }

    protected void parseLine() {
        this.kind = 4;
        this.family = 1;
        this.params = new float[]{getFloatWithUnit(this.element, "x1", this.svgWidth), getFloatWithUnit(this.element, "y1", this.svgHeight), getFloatWithUnit(this.element, "x2", this.svgWidth), getFloatWithUnit(this.element, "y2", this.svgHeight)};
    }

    protected void parseEllipse(boolean circle) {
        this.kind = 31;
        this.family = 1;
        this.params = new float[4];
        this.params[0] = getFloatWithUnit(this.element, "cx", this.svgWidth);
        this.params[1] = getFloatWithUnit(this.element, "cy", this.svgHeight);
        float rx;
        float ry;
        if (circle) {
            rx = ry = getFloatWithUnit(this.element, "r", this.svgSizeXY);
        } else {
            rx = getFloatWithUnit(this.element, "rx", this.svgWidth);
            ry = getFloatWithUnit(this.element, "ry", this.svgHeight);
        }

        float[] var10000 = this.params;
        var10000[0] -= rx;
        var10000 = this.params;
        var10000[1] -= ry;
        this.params[2] = rx * 2.0F;
        this.params[3] = ry * 2.0F;
    }

    protected void parseRect() {
        this.kind = 30;
        this.family = 1;
        this.params = new float[]{getFloatWithUnit(this.element, "x", this.svgWidth), getFloatWithUnit(this.element, "y", this.svgHeight), getFloatWithUnit(this.element, "width", this.svgWidth), getFloatWithUnit(this.element, "height", this.svgHeight)};
    }

    protected void parsePoly(boolean close) {
        this.family = 2;
        this.close = close;
        String pointsAttr = this.element.getString("points");
        if (pointsAttr != null) {
            String[] pointsBuffer = PApplet.splitTokens(pointsAttr);
            this.vertexCount = pointsBuffer.length;
            this.vertices = new float[this.vertexCount][2];

            for(int i = 0; i < this.vertexCount; ++i) {
                String[] pb = PApplet.splitTokens(pointsBuffer[i], ", \t\r\n");
                this.vertices[i][0] = Float.parseFloat(pb[0]);
                this.vertices[i][1] = Float.parseFloat(pb[1]);
            }
        }

    }

    protected void parsePath() {
        this.family = 2;
        this.kind = 0;
        String pathData = this.element.getString("d");
        if (pathData != null && PApplet.trim(pathData).length() != 0) {
            char[] pathDataChars = pathData.toCharArray();
            StringBuilder pathBuffer = new StringBuilder();
            boolean lastSeparate = false;

            for(int i = 0; i < pathDataChars.length; ++i) {
                char c = pathDataChars[i];
                boolean separate = false;
                if (c == 'M' || c == 'm' || c == 'L' || c == 'l' || c == 'H' || c == 'h' || c == 'V' || c == 'v' || c == 'C' || c == 'c' || c == 'S' || c == 's' || c == 'Q' || c == 'q' || c == 'T' || c == 't' || c == 'A' || c == 'a' || c == 'Z' || c == 'z' || c == ',') {
                    separate = true;
                    if (i != 0) {
                        pathBuffer.append("|");
                    }
                }

                if (c == 'Z' || c == 'z') {
                    separate = false;
                }

                if (c == '-' && !lastSeparate && (i == 0 || pathDataChars[i - 1] != 'e')) {
                    pathBuffer.append("|");
                }

                if (c != ',') {
                    pathBuffer.append(c);
                }

                if (separate && c != ',' && c != '-') {
                    pathBuffer.append("|");
                }

                lastSeparate = separate;
            }

            String[] pathTokens = PApplet.splitTokens(pathBuffer.toString(), "| \t\n\r\f ");
            this.vertices = new float[pathTokens.length][2];
            this.vertexCodes = new int[pathTokens.length];
            float cx = 0.0F;
            float cy = 0.0F;
            int i = 0;
            char implicitCommand = 0;
            boolean prevCurve = false;
            float movetoX = 0.0F;
            float movetoY = 0.0F;

            while(i < pathTokens.length) {
                char c = pathTokens[i].charAt(0);
                if ((c >= '0' && c <= '9' || c == '-') && implicitCommand != 0) {
                    c = implicitCommand;
                    --i;
                } else {
                    implicitCommand = c;
                }

                float ctrlX;
                float ctrlY;
                float endX;
                float endY;
                float endX;
                float endY;
                float endX;
                float endY;
                float endY;
                boolean fa;
                boolean fs;
                switch(c) {
                    case 'A':
                        endX = PApplet.parseFloat(pathTokens[i + 1]);
                        endY = PApplet.parseFloat(pathTokens[i + 2]);
                        endX = PApplet.parseFloat(pathTokens[i + 3]);
                        fa = PApplet.parseFloat(pathTokens[i + 4]) != 0.0F;
                        fs = PApplet.parseFloat(pathTokens[i + 5]) != 0.0F;
                        endY = PApplet.parseFloat(pathTokens[i + 6]);
                        endY = PApplet.parseFloat(pathTokens[i + 7]);
                        this.parsePathArcto(cx, cy, endX, endY, endX, fa, fs, endY, endY);
                        cx = endY;
                        cy = endY;
                        i += 8;
                        prevCurve = true;
                        break;
                    case 'B':
                    case 'D':
                    case 'E':
                    case 'F':
                    case 'G':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'N':
                    case 'O':
                    case 'P':
                    case 'R':
                    case 'U':
                    case 'W':
                    case 'X':
                    case 'Y':
                    case '[':
                    case '\\':
                    case ']':
                    case '^':
                    case '_':
                    case '`':
                    case 'b':
                    case 'd':
                    case 'e':
                    case 'f':
                    case 'g':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'n':
                    case 'o':
                    case 'p':
                    case 'r':
                    case 'u':
                    case 'w':
                    case 'x':
                    case 'y':
                    default:
                        String parsed = PApplet.join(PApplet.subset(pathTokens, 0, i), ",");
                        String unparsed = PApplet.join(PApplet.subset(pathTokens, i), ",");
                        System.err.println("parsed: " + parsed);
                        System.err.println("unparsed: " + unparsed);
                        throw new RuntimeException("shape command not handled: " + pathTokens[i]);
                    case 'C':
                        endX = PApplet.parseFloat(pathTokens[i + 1]);
                        endY = PApplet.parseFloat(pathTokens[i + 2]);
                        endX = PApplet.parseFloat(pathTokens[i + 3]);
                        endY = PApplet.parseFloat(pathTokens[i + 4]);
                        endX = PApplet.parseFloat(pathTokens[i + 5]);
                        endY = PApplet.parseFloat(pathTokens[i + 6]);
                        this.parsePathCurveto(endX, endY, endX, endY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 7;
                        prevCurve = true;
                        break;
                    case 'H':
                        cx = PApplet.parseFloat(pathTokens[i + 1]);
                        this.parsePathLineto(cx, cy);
                        i += 2;
                        break;
                    case 'L':
                        cx = PApplet.parseFloat(pathTokens[i + 1]);
                        cy = PApplet.parseFloat(pathTokens[i + 2]);
                        this.parsePathLineto(cx, cy);
                        i += 3;
                        break;
                    case 'M':
                        cx = PApplet.parseFloat(pathTokens[i + 1]);
                        cy = PApplet.parseFloat(pathTokens[i + 2]);
                        movetoX = cx;
                        movetoY = cy;
                        this.parsePathMoveto(cx, cy);
                        implicitCommand = 'L';
                        i += 3;
                        break;
                    case 'Q':
                        ctrlX = PApplet.parseFloat(pathTokens[i + 1]);
                        ctrlY = PApplet.parseFloat(pathTokens[i + 2]);
                        endX = PApplet.parseFloat(pathTokens[i + 3]);
                        endY = PApplet.parseFloat(pathTokens[i + 4]);
                        this.parsePathQuadto(ctrlX, ctrlY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 5;
                        prevCurve = true;
                        break;
                    case 'S':
                        if (!prevCurve) {
                            ctrlX = cx;
                            ctrlY = cy;
                        } else {
                            endX = this.vertices[this.vertexCount - 2][0];
                            endY = this.vertices[this.vertexCount - 2][1];
                            endX = this.vertices[this.vertexCount - 1][0];
                            endY = this.vertices[this.vertexCount - 1][1];
                            ctrlX = endX + (endX - endX);
                            ctrlY = endY + (endY - endY);
                        }

                        endX = PApplet.parseFloat(pathTokens[i + 1]);
                        endY = PApplet.parseFloat(pathTokens[i + 2]);
                        endX = PApplet.parseFloat(pathTokens[i + 3]);
                        endY = PApplet.parseFloat(pathTokens[i + 4]);
                        this.parsePathCurveto(ctrlX, ctrlY, endX, endY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 5;
                        prevCurve = true;
                        break;
                    case 'T':
                        if (!prevCurve) {
                            ctrlX = cx;
                            ctrlY = cy;
                        } else {
                            endX = this.vertices[this.vertexCount - 2][0];
                            endY = this.vertices[this.vertexCount - 2][1];
                            endX = this.vertices[this.vertexCount - 1][0];
                            endY = this.vertices[this.vertexCount - 1][1];
                            ctrlX = endX + (endX - endX);
                            ctrlY = endY + (endY - endY);
                        }

                        endX = PApplet.parseFloat(pathTokens[i + 1]);
                        endY = PApplet.parseFloat(pathTokens[i + 2]);
                        this.parsePathQuadto(ctrlX, ctrlY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 3;
                        prevCurve = true;
                        break;
                    case 'V':
                        cy = PApplet.parseFloat(pathTokens[i + 1]);
                        this.parsePathLineto(cx, cy);
                        i += 2;
                        break;
                    case 'Z':
                    case 'z':
                        cx = movetoX;
                        cy = movetoY;
                        this.close = true;
                        ++i;
                        break;
                    case 'a':
                        endX = PApplet.parseFloat(pathTokens[i + 1]);
                        endY = PApplet.parseFloat(pathTokens[i + 2]);
                        endX = PApplet.parseFloat(pathTokens[i + 3]);
                        fa = PApplet.parseFloat(pathTokens[i + 4]) != 0.0F;
                        fs = PApplet.parseFloat(pathTokens[i + 5]) != 0.0F;
                        endY = cx + PApplet.parseFloat(pathTokens[i + 6]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 7]);
                        this.parsePathArcto(cx, cy, endX, endY, endX, fa, fs, endY, endY);
                        cx = endY;
                        cy = endY;
                        i += 8;
                        prevCurve = true;
                        break;
                    case 'c':
                        endX = cx + PApplet.parseFloat(pathTokens[i + 1]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 2]);
                        endX = cx + PApplet.parseFloat(pathTokens[i + 3]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 4]);
                        endX = cx + PApplet.parseFloat(pathTokens[i + 5]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 6]);
                        this.parsePathCurveto(endX, endY, endX, endY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 7;
                        prevCurve = true;
                        break;
                    case 'h':
                        cx += PApplet.parseFloat(pathTokens[i + 1]);
                        this.parsePathLineto(cx, cy);
                        i += 2;
                        break;
                    case 'l':
                        cx += PApplet.parseFloat(pathTokens[i + 1]);
                        cy += PApplet.parseFloat(pathTokens[i + 2]);
                        this.parsePathLineto(cx, cy);
                        i += 3;
                        break;
                    case 'm':
                        cx += PApplet.parseFloat(pathTokens[i + 1]);
                        cy += PApplet.parseFloat(pathTokens[i + 2]);
                        movetoX = cx;
                        movetoY = cy;
                        this.parsePathMoveto(cx, cy);
                        implicitCommand = 'l';
                        i += 3;
                        break;
                    case 'q':
                        ctrlX = cx + PApplet.parseFloat(pathTokens[i + 1]);
                        ctrlY = cy + PApplet.parseFloat(pathTokens[i + 2]);
                        endX = cx + PApplet.parseFloat(pathTokens[i + 3]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 4]);
                        this.parsePathQuadto(ctrlX, ctrlY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 5;
                        prevCurve = true;
                        break;
                    case 's':
                        if (!prevCurve) {
                            ctrlX = cx;
                            ctrlY = cy;
                        } else {
                            endX = this.vertices[this.vertexCount - 2][0];
                            endY = this.vertices[this.vertexCount - 2][1];
                            endX = this.vertices[this.vertexCount - 1][0];
                            endY = this.vertices[this.vertexCount - 1][1];
                            ctrlX = endX + (endX - endX);
                            ctrlY = endY + (endY - endY);
                        }

                        endX = cx + PApplet.parseFloat(pathTokens[i + 1]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 2]);
                        endX = cx + PApplet.parseFloat(pathTokens[i + 3]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 4]);
                        this.parsePathCurveto(ctrlX, ctrlY, endX, endY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 5;
                        prevCurve = true;
                        break;
                    case 't':
                        if (!prevCurve) {
                            ctrlX = cx;
                            ctrlY = cy;
                        } else {
                            endX = this.vertices[this.vertexCount - 2][0];
                            endY = this.vertices[this.vertexCount - 2][1];
                            endX = this.vertices[this.vertexCount - 1][0];
                            endY = this.vertices[this.vertexCount - 1][1];
                            ctrlX = endX + (endX - endX);
                            ctrlY = endY + (endY - endY);
                        }

                        endX = cx + PApplet.parseFloat(pathTokens[i + 1]);
                        endY = cy + PApplet.parseFloat(pathTokens[i + 2]);
                        this.parsePathQuadto(ctrlX, ctrlY, endX, endY);
                        cx = endX;
                        cy = endY;
                        i += 3;
                        prevCurve = true;
                        break;
                    case 'v':
                        cy += PApplet.parseFloat(pathTokens[i + 1]);
                        this.parsePathLineto(cx, cy);
                        i += 2;
                }
            }

        }
    }

    private void parsePathVertex(float x, float y) {
        if (this.vertexCount == this.vertices.length) {
            float[][] temp = new float[this.vertexCount << 1][2];
            System.arraycopy(this.vertices, 0, temp, 0, this.vertexCount);
            this.vertices = temp;
        }

        this.vertices[this.vertexCount][0] = x;
        this.vertices[this.vertexCount][1] = y;
        ++this.vertexCount;
    }

    private void parsePathCode(int what) {
        if (this.vertexCodeCount == this.vertexCodes.length) {
            this.vertexCodes = PApplet.expand(this.vertexCodes);
        }

        this.vertexCodes[this.vertexCodeCount++] = what;
    }

    private void parsePathMoveto(float px, float py) {
        if (this.vertexCount > 0) {
            this.parsePathCode(4);
        }

        this.parsePathCode(0);
        this.parsePathVertex(px, py);
    }

    private void parsePathLineto(float px, float py) {
        this.parsePathCode(0);
        this.parsePathVertex(px, py);
    }

    private void parsePathCurveto(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.parsePathCode(1);
        this.parsePathVertex(x1, y1);
        this.parsePathVertex(x2, y2);
        this.parsePathVertex(x3, y3);
    }

    private void parsePathQuadto(float cx, float cy, float x2, float y2) {
        this.parsePathCode(2);
        this.parsePathVertex(cx, cy);
        this.parsePathVertex(x2, y2);
    }

    private void parsePathArcto(float x1, float y1, float rx, float ry, float angle, boolean fa, boolean fs, float x2, float y2) {
        if (x1 != x2 || y1 != y2) {
            if (rx != 0.0F && ry != 0.0F) {
                rx = PApplet.abs(rx);
                ry = PApplet.abs(ry);
                float phi = PApplet.radians((angle % 360.0F + 360.0F) % 360.0F);
                float cosPhi = PApplet.cos(phi);
                float sinPhi = PApplet.sin(phi);
                float x1r = (cosPhi * (x1 - x2) + sinPhi * (y1 - y2)) / 2.0F;
                float y1r = (-sinPhi * (x1 - x2) + cosPhi * (y1 - y2)) / 2.0F;
                float cx = x1r * x1r / (rx * rx) + y1r * y1r / (ry * ry);
                float cxr;
                float cyr;
                float cy;
                if (cx > 1.0F) {
                    cy = PApplet.sqrt(cx);
                    rx *= cy;
                    cxr = 0.0F;
                    ry *= cy;
                    cyr = 0.0F;
                } else {
                    cy = (fa == fs ? -1.0F : 1.0F) * PApplet.sqrt(rx * rx * ry * ry / (rx * rx * y1r * y1r + ry * ry * x1r * x1r) - 1.0F);
                    cxr = cy * rx * y1r / ry;
                    cyr = -cy * ry * x1r / rx;
                }

                cx = cosPhi * cxr - sinPhi * cyr + (x1 + x2) / 2.0F;
                cy = sinPhi * cxr + cosPhi * cyr + (y1 + y2) / 2.0F;
                float sx = (x1r - cxr) / rx;
                float inc = (y1r - cyr) / ry;
                float a = (-x1r - cxr) / rx;
                float sinPhi1 = (-y1r - cyr) / ry;
                float phi1 = PApplet.atan2(inc, sx);
                float phiDelta = ((PApplet.atan2(sinPhi1, a) - phi1) % 6.2831855F + 6.2831855F) % 6.2831855F;
                if (!fs) {
                    phiDelta -= 6.2831855F;
                }

                int segmentCount = PApplet.ceil(PApplet.abs(phiDelta) / 6.2831855F * 4.0F);
                inc = phiDelta / (float)segmentCount;
                a = PApplet.sin(inc) * (PApplet.sqrt(4.0F + 3.0F * PApplet.sq(PApplet.tan(inc / 2.0F))) - 1.0F) / 3.0F;
                sinPhi1 = PApplet.sin(phi1);
                float cosPhi1 = PApplet.cos(phi1);
                float p1x = x1;
                float p1y = y1;
                float relq1x = a * (-rx * cosPhi * sinPhi1 - ry * sinPhi * cosPhi1);
                float relq1y = a * (-rx * sinPhi * sinPhi1 + ry * cosPhi * cosPhi1);

                for(int i = 0; i < segmentCount; ++i) {
                    float eta = phi1 + (float)(i + 1) * inc;
                    float sinEta = PApplet.sin(eta);
                    float cosEta = PApplet.cos(eta);
                    float p2x = cx + rx * cosPhi * cosEta - ry * sinPhi * sinEta;
                    float p2y = cy + rx * sinPhi * cosEta + ry * cosPhi * sinEta;
                    float relq2x = a * (-rx * cosPhi * sinEta - ry * sinPhi * cosEta);
                    float relq2y = a * (-rx * sinPhi * sinEta + ry * cosPhi * cosEta);
                    if (i == segmentCount - 1) {
                        p2x = x2;
                        p2y = y2;
                    }

                    this.parsePathCode(1);
                    this.parsePathVertex(p1x + relq1x, p1y + relq1y);
                    this.parsePathVertex(p2x - relq2x, p2y - relq2y);
                    this.parsePathVertex(p2x, p2y);
                    p1x = p2x;
                    relq1x = relq2x;
                    p1y = p2y;
                    relq1y = relq2y;
                }

            } else {
                this.parsePathLineto(x2, y2);
            }
        }
    }

    protected static PMatrix2D parseTransform(String matrixStr) {
        matrixStr = matrixStr.trim();
        PMatrix2D outgoing = null;
        int start = 0;

        int stop;
        for(boolean var3 = true; (stop = matrixStr.indexOf(41, start)) != -1; start = stop + 1) {
            PMatrix2D m = parseSingleTransform(matrixStr.substring(start, stop + 1));
            if (outgoing == null) {
                outgoing = m;
            } else {
                outgoing.apply(m);
            }
        }

        return outgoing;
    }

    protected static PMatrix2D parseSingleTransform(String matrixStr) {
        String[] pieces = PApplet.match(matrixStr, "[,\\s]*(\\w+)\\((.*)\\)");
        if (pieces == null) {
            System.err.println("Could not parse transform " + matrixStr);
            return null;
        } else {
            float[] m = PApplet.parseFloat(PApplet.splitTokens(pieces[2], ", "));
            if (pieces[1].equals("matrix")) {
                return new PMatrix2D(m[0], m[2], m[4], m[1], m[3], m[5]);
            } else {
                float angle;
                float c;
                if (pieces[1].equals("translate")) {
                    angle = m[0];
                    c = m.length == 2 ? m[1] : m[0];
                    return new PMatrix2D(1.0F, 0.0F, angle, 0.0F, 1.0F, c);
                } else if (pieces[1].equals("scale")) {
                    angle = m[0];
                    c = m.length == 2 ? m[1] : m[0];
                    return new PMatrix2D(angle, 0.0F, 0.0F, 0.0F, c, 0.0F);
                } else {
                    if (pieces[1].equals("rotate")) {
                        angle = m[0];
                        if (m.length == 1) {
                            c = PApplet.cos(angle);
                            float s = PApplet.sin(angle);
                            return new PMatrix2D(c, -s, 0.0F, s, c, 0.0F);
                        }

                        if (m.length == 3) {
                            PMatrix2D mat = new PMatrix2D(0.0F, 1.0F, m[1], 1.0F, 0.0F, m[2]);
                            mat.rotate(m[0]);
                            mat.translate(-m[1], -m[2]);
                            return mat;
                        }
                    } else {
                        if (pieces[1].equals("skewX")) {
                            return new PMatrix2D(1.0F, 0.0F, 1.0F, PApplet.tan(m[0]), 0.0F, 0.0F);
                        }

                        if (pieces[1].equals("skewY")) {
                            return new PMatrix2D(1.0F, 0.0F, 1.0F, 0.0F, PApplet.tan(m[0]), 0.0F);
                        }
                    }

                    return null;
                }
            }
        }
    }

    protected void parseColors(XML properties) {
        String styleText;
        if (properties.hasAttribute("opacity")) {
            styleText = properties.getString("opacity");
            this.setOpacity(styleText);
        }

        if (properties.hasAttribute("stroke")) {
            styleText = properties.getString("stroke");
            this.setColor(styleText, false);
        }

        if (properties.hasAttribute("stroke-opacity")) {
            styleText = properties.getString("stroke-opacity");
            this.setStrokeOpacity(styleText);
        }

        if (properties.hasAttribute("stroke-width")) {
            styleText = properties.getString("stroke-width");
            this.setStrokeWeight(styleText);
        }

        if (properties.hasAttribute("stroke-linejoin")) {
            styleText = properties.getString("stroke-linejoin");
            this.setStrokeJoin(styleText);
        }

        if (properties.hasAttribute("stroke-linecap")) {
            styleText = properties.getString("stroke-linecap");
            this.setStrokeCap(styleText);
        }

        if (properties.hasAttribute("fill")) {
            styleText = properties.getString("fill");
            this.setColor(styleText, true);
        }

        if (properties.hasAttribute("fill-opacity")) {
            styleText = properties.getString("fill-opacity");
            this.setFillOpacity(styleText);
        }

        if (properties.hasAttribute("style")) {
            styleText = properties.getString("style");
            String[] styleTokens = PApplet.splitTokens(styleText, ";");

            for(int i = 0; i < styleTokens.length; ++i) {
                String[] tokens = PApplet.splitTokens(styleTokens[i], ":");
                tokens[0] = PApplet.trim(tokens[0]);
                if (tokens[0].equals("fill")) {
                    this.setColor(tokens[1], true);
                } else if (tokens[0].equals("fill-opacity")) {
                    this.setFillOpacity(tokens[1]);
                } else if (tokens[0].equals("stroke")) {
                    this.setColor(tokens[1], false);
                } else if (tokens[0].equals("stroke-width")) {
                    this.setStrokeWeight(tokens[1]);
                } else if (tokens[0].equals("stroke-linecap")) {
                    this.setStrokeCap(tokens[1]);
                } else if (tokens[0].equals("stroke-linejoin")) {
                    this.setStrokeJoin(tokens[1]);
                } else if (tokens[0].equals("stroke-opacity")) {
                    this.setStrokeOpacity(tokens[1]);
                } else if (tokens[0].equals("opacity")) {
                    this.setOpacity(tokens[1]);
                }
            }
        }

    }

    void setOpacity(String opacityText) {
        this.opacity = PApplet.parseFloat(opacityText);
        this.strokeColor = (int)(this.opacity * 255.0F) << 24 | this.strokeColor & 16777215;
        this.fillColor = (int)(this.opacity * 255.0F) << 24 | this.fillColor & 16777215;
    }

    void setStrokeWeight(String lineweight) {
        this.strokeWeight = parseUnitSize(lineweight, this.svgSizeXY);
    }

    void setStrokeOpacity(String opacityText) {
        this.strokeOpacity = PApplet.parseFloat(opacityText);
        this.strokeColor = (int)(this.strokeOpacity * 255.0F) << 24 | this.strokeColor & 16777215;
    }

    void setStrokeJoin(String linejoin) {
        if (!linejoin.equals("inherit")) {
            if (linejoin.equals("miter")) {
                this.strokeJoin = 8;
            } else if (linejoin.equals("round")) {
                this.strokeJoin = 2;
            } else if (linejoin.equals("bevel")) {
                this.strokeJoin = 32;
            }
        }

    }

    void setStrokeCap(String linecap) {
        if (!linecap.equals("inherit")) {
            if (linecap.equals("butt")) {
                this.strokeCap = 1;
            } else if (linecap.equals("round")) {
                this.strokeCap = 2;
            } else if (linecap.equals("square")) {
                this.strokeCap = 4;
            }
        }

    }

    void setFillOpacity(String opacityText) {
        this.fillOpacity = PApplet.parseFloat(opacityText);
        this.fillColor = (int)(this.fillOpacity * 255.0F) << 24 | this.fillColor & 16777215;
    }

    void setColor(String colorText, boolean isFill) {
        colorText = colorText.trim();
        int opacityMask = this.fillColor & -16777216;
        boolean visible = true;
        int color = 0;
        String name = "";
        PShapeSVG.Gradient gradient = null;
        if (colorText.equals("none")) {
            visible = false;
        } else if (colorText.startsWith("url(#")) {
            name = colorText.substring(5, colorText.length() - 1);
            Object object = this.findChild(name);
            if (object instanceof PShapeSVG.Gradient) {
                gradient = (PShapeSVG.Gradient)object;
            } else {
                System.err.println("url " + name + " refers to unexpected data: " + object);
            }
        } else {
            color = opacityMask | parseSimpleColor(colorText);
        }

        if (isFill) {
            this.fill = visible;
            this.fillColor = color;
            this.fillName = name;
            this.fillGradient = gradient;
        } else {
            this.stroke = visible;
            this.strokeColor = color;
            this.strokeName = name;
            this.strokeGradient = gradient;
        }

    }

    protected static int parseSimpleColor(String colorText) {
        colorText = colorText.toLowerCase().trim();
        if (colorNames.hasKey(colorText)) {
            return colorNames.get(colorText);
        } else if (colorText.startsWith("#")) {
            if (colorText.length() == 4) {
                colorText = colorText.replaceAll("^#(.)(.)(.)$", "#$1$1$2$2$3$3");
            }

            return Integer.parseInt(colorText.substring(1), 16) & 16777215;
        } else if (colorText.startsWith("rgb")) {
            return parseRGB(colorText);
        } else {
            System.err.println("Cannot parse \"" + colorText + "\".");
            return 0;
        }
    }

    protected static int parseRGB(String what) {
        int leftParen = what.indexOf(40) + 1;
        int rightParen = what.indexOf(41);
        String sub = what.substring(leftParen, rightParen);
        String[] values = PApplet.splitTokens(sub, ", ");
        int rgbValue = 0;
        if (values.length == 3) {
            for(int i = 0; i < 3; ++i) {
                rgbValue <<= 8;
                if (values[i].endsWith("%")) {
                    rgbValue |= (int)PApplet.constrain(255.0F * parseFloatOrPercent(values[i]), 0.0F, 255.0F);
                } else {
                    rgbValue |= PApplet.constrain(PApplet.parseInt(values[i]), 0, 255);
                }
            }
        } else {
            System.err.println("Could not read color \"" + what + "\".");
        }

        return rgbValue;
    }

    protected static StringDict parseStyleAttributes(String style) {
        StringDict table = new StringDict();
        if (style != null) {
            String[] pieces = style.split(";");

            for(int i = 0; i < pieces.length; ++i) {
                String[] parts = pieces[i].split(":");
                table.set(parts[0], parts[1]);
            }
        }

        return table;
    }

    protected static float getFloatWithUnit(XML element, String attribute, float relativeTo) {
        String val = element.getString(attribute);
        return val == null ? 0.0F : parseUnitSize(val, relativeTo);
    }

    protected static float parseUnitSize(String text, float relativeTo) {
        int len = text.length() - 2;
        if (text.endsWith("pt")) {
            return PApplet.parseFloat(text.substring(0, len)) * 1.25F;
        } else if (text.endsWith("pc")) {
            return PApplet.parseFloat(text.substring(0, len)) * 15.0F;
        } else if (text.endsWith("mm")) {
            return PApplet.parseFloat(text.substring(0, len)) * 3.543307F;
        } else if (text.endsWith("cm")) {
            return PApplet.parseFloat(text.substring(0, len)) * 35.43307F;
        } else if (text.endsWith("in")) {
            return PApplet.parseFloat(text.substring(0, len)) * 90.0F;
        } else if (text.endsWith("px")) {
            return PApplet.parseFloat(text.substring(0, len));
        } else {
            return text.endsWith("%") ? relativeTo * parseFloatOrPercent(text) : PApplet.parseFloat(text);
        }
    }

    protected static float parseFloatOrPercent(String text) {
        text = text.trim();
        return text.endsWith("%") ? Float.parseFloat(text.substring(0, text.length() - 1)) / 100.0F : Float.parseFloat(text);
    }

    public PShape getChild(String name) {
        PShape found = super.getChild(name);
        if (found == null) {
            found = super.getChild(name.replace(' ', '_'));
        }

        if (found != null) {
            found.width = this.width;
            found.height = this.height;
        }

        return found;
    }

    public void print() {
        PApplet.println(this.element.toString());
    }

    public static class FontGlyph extends PShapeSVG {
        public String name;
        char unicode;
        int horizAdvX;

        public FontGlyph(PShapeSVG parent, XML properties, PShapeSVG.Font font) {
            super(parent, properties, true);
            super.parsePath();
            this.name = properties.getString("glyph-name");
            String u = properties.getString("unicode");
            this.unicode = 0;
            if (u != null) {
                if (u.length() == 1) {
                    this.unicode = u.charAt(0);
                } else {
                    System.err.println("unicode for " + this.name + " is more than one char: " + u);
                }
            }

            if (properties.hasAttribute("horiz-adv-x")) {
                this.horizAdvX = properties.getInt("horiz-adv-x");
            } else {
                this.horizAdvX = font.horizAdvX;
            }

        }

        protected boolean isLegit() {
            return this.vertexCount != 0;
        }
    }

    static class FontFace extends PShapeSVG {
        int horizOriginX;
        int horizOriginY;
        int vertOriginX;
        int vertOriginY;
        int vertAdvY;
        String fontFamily;
        int fontWeight;
        String fontStretch;
        int unitsPerEm;
        int[] panose1;
        int ascent;
        int descent;
        int[] bbox;
        int underlineThickness;
        int underlinePosition;

        public FontFace(PShapeSVG parent, XML properties) {
            super(parent, properties, true);
            this.unitsPerEm = properties.getInt("units-per-em", 1000);
        }

        protected void drawShape() {
        }
    }

    public static class Font extends PShapeSVG {
        public PShapeSVG.FontFace face;
        public Map<String, PShapeSVG.FontGlyph> namedGlyphs;
        public Map<Character, PShapeSVG.FontGlyph> unicodeGlyphs;
        public int glyphCount;
        public PShapeSVG.FontGlyph[] glyphs;
        public PShapeSVG.FontGlyph missingGlyph;
        int horizAdvX;

        public Font(PShapeSVG parent, XML properties) {
            super(parent, properties, false);
            XML[] elements = properties.getChildren();
            this.horizAdvX = properties.getInt("horiz-adv-x", 0);
            this.namedGlyphs = new HashMap();
            this.unicodeGlyphs = new HashMap();
            this.glyphCount = 0;
            this.glyphs = new PShapeSVG.FontGlyph[elements.length];

            for(int i = 0; i < elements.length; ++i) {
                String name = elements[i].getName();
                XML elem = elements[i];
                if (name != null) {
                    if (name.equals("glyph")) {
                        PShapeSVG.FontGlyph fg = new PShapeSVG.FontGlyph(this, elem, this);
                        if (fg.isLegit()) {
                            if (fg.name != null) {
                                this.namedGlyphs.put(fg.name, fg);
                            }

                            if (fg.unicode != 0) {
                                this.unicodeGlyphs.put(fg.unicode, fg);
                            }
                        }

                        this.glyphs[this.glyphCount++] = fg;
                    } else if (name.equals("missing-glyph")) {
                        this.missingGlyph = new PShapeSVG.FontGlyph(this, elem, this);
                    } else if (name.equals("font-face")) {
                        this.face = new PShapeSVG.FontFace(this, elem);
                    } else {
                        System.err.println("Ignoring " + name + " inside <font>");
                    }
                }
            }

        }

        protected void drawShape() {
        }

        public void drawString(PGraphics g, String str, float x, float y, float size) {
            g.pushMatrix();
            float s = size / (float)this.face.unitsPerEm;
            g.translate(x, y);
            g.scale(s, -s);
            char[] c = str.toCharArray();

            for(int i = 0; i < c.length; ++i) {
                PShapeSVG.FontGlyph fg = (PShapeSVG.FontGlyph)this.unicodeGlyphs.get(c[i]);
                if (fg != null) {
                    fg.draw(g);
                    g.translate((float)fg.horizAdvX, 0.0F);
                } else {
                    System.err.println("'" + c[i] + "' not available.");
                }
            }

            g.popMatrix();
        }

        public void drawChar(PGraphics g, char c, float x, float y, float size) {
            g.pushMatrix();
            float s = size / (float)this.face.unitsPerEm;
            g.translate(x, y);
            g.scale(s, -s);
            PShapeSVG.FontGlyph fg = (PShapeSVG.FontGlyph)this.unicodeGlyphs.get(c);
            if (fg != null) {
                g.shape(fg);
            }

            g.popMatrix();
        }

        public float textWidth(String str, float size) {
            float w = 0.0F;
            char[] c = str.toCharArray();

            for(int i = 0; i < c.length; ++i) {
                PShapeSVG.FontGlyph fg = (PShapeSVG.FontGlyph)this.unicodeGlyphs.get(c[i]);
                if (fg != null) {
                    w += (float)fg.horizAdvX / (float)this.face.unitsPerEm;
                }
            }

            return w * size;
        }
    }

    public class RadialGradient extends PShapeSVG.Gradient {
        public float cx;
        public float cy;
        public float r;

        public RadialGradient(PShapeSVG parent, XML properties) {
            super(parent, properties);
            this.cx = getFloatWithUnit(properties, "cx", this.svgWidth);
            this.cy = getFloatWithUnit(properties, "cy", this.svgHeight);
            this.r = getFloatWithUnit(properties, "r", this.svgSizeXY);
            String transformStr = properties.getString("gradientTransform");
            if (transformStr != null) {
                float[] t = parseTransform(transformStr).get((float[])null);
                this.transform = new Matrix();
                this.transform.setValues(new float[]{t[0], t[1], t[2], t[3], t[4], t[5], 0.0F, 0.0F, 1.0F});
                float[] t1 = new float[]{this.cx, this.cy};
                float[] t2 = new float[]{this.cx + this.r, this.cy};
                this.transform.mapPoints(t1);
                this.transform.mapPoints(t2);
                this.cx = t1[0];
                this.cy = t1[1];
                this.r = t2[0] - t1[0];
            }

        }
    }

    public class LinearGradient extends PShapeSVG.Gradient {
        public float x1;
        public float y1;
        public float x2;
        public float y2;

        public LinearGradient(PShapeSVG parent, XML properties) {
            super(parent, properties);
            this.x1 = getFloatWithUnit(properties, "x1", this.svgWidth);
            this.y1 = getFloatWithUnit(properties, "y1", this.svgHeight);
            this.x2 = getFloatWithUnit(properties, "x2", this.svgWidth);
            this.y2 = getFloatWithUnit(properties, "y2", this.svgHeight);
            String transformStr = properties.getString("gradientTransform");
            if (transformStr != null) {
                float[] t = parseTransform(transformStr).get((float[])null);
                this.transform = new Matrix();
                this.transform.setValues(new float[]{t[0], t[1], t[2], t[3], t[4], t[5], 0.0F, 0.0F, 1.0F});
                float[] t1 = new float[]{this.x1, this.y1};
                float[] t2 = new float[]{this.x2, this.y2};
                this.transform.mapPoints(t1);
                this.transform.mapPoints(t2);
                this.x1 = t1[0];
                this.y1 = t1[1];
                this.x2 = t2[0];
                this.y2 = t2[1];
            }

        }
    }

    public static class Gradient extends PShapeSVG {
        Matrix transform;
        public float[] offset;
        public int[] color;
        public int count;

        public Gradient(PShapeSVG parent, XML properties) {
            super(parent, properties, true);
            XML[] elements = properties.getChildren();
            this.offset = new float[elements.length];
            this.color = new int[elements.length];

            for(int i = 0; i < elements.length; ++i) {
                XML elem = elements[i];
                String name = elem.getName();
                if (name.equals("stop")) {
                    String offsetAttr = elem.getString("offset");
                    this.offset[this.count] = parseFloatOrPercent(offsetAttr);
                    String style = elem.getString("style");
                    StringDict styles = parseStyleAttributes(style);
                    String colorStr = styles.get("stop-color");
                    if (colorStr == null) {
                        colorStr = elem.getString("stop-color");
                        if (colorStr == null) {
                            colorStr = "#000000";
                        }
                    }

                    String opacityStr = styles.get("stop-opacity");
                    if (opacityStr == null) {
                        opacityStr = elem.getString("stop-opacity");
                        if (opacityStr == null) {
                            opacityStr = "1";
                        }
                    }

                    int tupacity = PApplet.constrain((int)(PApplet.parseFloat(opacityStr) * 255.0F), 0, 255);
                    this.color[this.count] = tupacity << 24 | parseSimpleColor(colorStr);
                    ++this.count;
                }
            }

            this.offset = PApplet.subset(this.offset, 0, this.count);
            this.color = PApplet.subset(this.color, 0, this.count);
        }
    }
}

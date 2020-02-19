/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-16 The Processing Foundation

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

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PShapeOBJ extends PShape {
    public PShapeOBJ(PApplet parent, String filename) {
        this(parent, parent.createReader(filename));
    }

    public PShapeOBJ(PApplet parent, BufferedReader reader) {
        ArrayList<PShapeOBJ.OBJFace> faces = new ArrayList();
        ArrayList<PShapeOBJ.OBJMaterial> materials = new ArrayList();
        ArrayList<PVector> coords = new ArrayList();
        ArrayList<PVector> normals = new ArrayList();
        ArrayList<PVector> texcoords = new ArrayList();
        parseOBJ(parent, reader, faces, materials, coords, normals, texcoords);
        parent = null;
        this.family = 0;
        this.addChildren(faces, materials, coords, normals, texcoords);
    }

    protected PShapeOBJ(PShapeOBJ.OBJFace face, PShapeOBJ.OBJMaterial mtl, ArrayList<PVector> coords, ArrayList<PVector> normals, ArrayList<PVector> texcoords) {
        this.family = 3;
        if (face.vertIdx.size() == 3) {
            this.kind = 9;
        } else if (face.vertIdx.size() == 4) {
            this.kind = 17;
        } else {
            this.kind = 20;
        }

        this.stroke = false;
        this.fill = true;
        this.fillColor = rgbaValue(mtl.kd);
        this.ambientColor = rgbaValue(mtl.ka);
        this.specularColor = rgbaValue(mtl.ks);
        this.shininess = mtl.ns;
        if (mtl.kdMap != null) {
            this.tintColor = rgbaValue(mtl.kd, mtl.d);
        }

        this.vertexCount = face.vertIdx.size();
        this.vertices = new float[this.vertexCount][12];

        for(int j = 0; j < face.vertIdx.size(); ++j) {
            PVector tex = null;
            PVector norms = null;
            PVector vert = null;
            int vertIdx = (Integer)face.vertIdx.get(j) - 1;
            vert = (PVector)coords.get(vertIdx);
            if (j < face.normIdx.size()) {
                int normIdx = (Integer)face.normIdx.get(j) - 1;
                if (-1 < normIdx) {
                    norms = (PVector)normals.get(normIdx);
                }
            }

            if (j < face.texIdx.size()) {
                int texIdx = (Integer)face.texIdx.get(j) - 1;
                if (-1 < texIdx) {
                    tex = (PVector)texcoords.get(texIdx);
                }
            }

            this.vertices[j][0] = vert.x;
            this.vertices[j][1] = vert.y;
            this.vertices[j][2] = vert.z;
            this.vertices[j][3] = mtl.kd.x;
            this.vertices[j][4] = mtl.kd.y;
            this.vertices[j][5] = mtl.kd.z;
            this.vertices[j][6] = 1.0F;
            if (norms != null) {
                this.vertices[j][9] = norms.x;
                this.vertices[j][10] = norms.y;
                this.vertices[j][11] = norms.z;
            }

            if (tex != null) {
                this.vertices[j][7] = tex.x;
                this.vertices[j][8] = tex.y;
            }

            if (mtl != null && mtl.kdMap != null) {
                this.image = mtl.kdMap;
            }
        }

    }

    protected void addChildren(ArrayList<PShapeOBJ.OBJFace> faces, ArrayList<PShapeOBJ.OBJMaterial> materials, ArrayList<PVector> coords, ArrayList<PVector> normals, ArrayList<PVector> texcoords) {
        int mtlIdxCur = -1;
        PShapeOBJ.OBJMaterial mtl = null;

        for(int i = 0; i < faces.size(); ++i) {
            PShapeOBJ.OBJFace face = (PShapeOBJ.OBJFace)faces.get(i);
            if (mtlIdxCur != face.matIdx || face.matIdx == -1) {
                mtlIdxCur = PApplet.max(0, face.matIdx);
                mtl = (PShapeOBJ.OBJMaterial)materials.get(mtlIdxCur);
            }

            PShape child = new PShapeOBJ(face, mtl, coords, normals, texcoords);
            this.addChild(child);
        }

    }

    protected static void parseOBJ(PApplet parent, BufferedReader reader, ArrayList<PShapeOBJ.OBJFace> faces, ArrayList<PShapeOBJ.OBJMaterial> materials, ArrayList<PVector> coords, ArrayList<PVector> normals, ArrayList<PVector> texcoords) {
        Map<String, Integer> mtlTable = new HashMap();
        int mtlIdxCur = -1;

        try {
            boolean readvt = false;
            boolean readvn = false;
            boolean readv = false;
            String gname = "object";

            while(true) {
                while(true) {
                    String[] parts;
                    do {
                        String line;
                        do {
                            do {
                                if ((line = reader.readLine()) == null) {
                                    if (materials.size() == 0) {
                                        PShapeOBJ.OBJMaterial defMtl = new PShapeOBJ.OBJMaterial();
                                        materials.add(defMtl);
                                    }

                                    return;
                                }

                                line = line.trim();
                            } while(line.equals(""));
                        } while(line.indexOf(35) == 0);

                        while(line.contains("\\")) {
                            line = line.split("\\\\")[0];
                            String s = reader.readLine();
                            if (s != null) {
                                line = line + s;
                            }
                        }

                        parts = line.split("\\s+");
                    } while(parts.length <= 0);

                    PVector tempv;
                    if (parts[0].equals("v")) {
                        tempv = new PVector(Float.valueOf(parts[1]), Float.valueOf(parts[2]), Float.valueOf(parts[3]));
                        coords.add(tempv);
                        readv = true;
                    } else if (parts[0].equals("vn")) {
                        tempv = new PVector(Float.valueOf(parts[1]), Float.valueOf(parts[2]), Float.valueOf(parts[3]));
                        normals.add(tempv);
                        readvn = true;
                    } else if (parts[0].equals("vt")) {
                        tempv = new PVector(Float.valueOf(parts[1]), 1.0F - Float.valueOf(parts[2]));
                        texcoords.add(tempv);
                        readvt = true;
                    } else if (!parts[0].equals("o")) {
                        String mtlname;
                        if (parts[0].equals("mtllib")) {
                            if (parts[1] != null) {
                                mtlname = parts[1];
                                BufferedReader mreader = parent.createReader(mtlname);
                                if (mreader != null) {
                                    parseMTL(parent, mtlname, mreader, materials, mtlTable);
                                    mreader.close();
                                }
                            }
                        } else if (parts[0].equals("g")) {
                            gname = 1 < parts.length ? parts[1] : "";
                        } else if (parts[0].equals("usemtl")) {
                            if (parts[1] != null) {
                                mtlname = parts[1];
                                if (mtlTable.containsKey(mtlname)) {
                                    Integer tempInt = (Integer)mtlTable.get(mtlname);
                                    mtlIdxCur = tempInt;
                                } else {
                                    mtlIdxCur = -1;
                                }
                            }
                        } else if (parts[0].equals("f")) {
                            PShapeOBJ.OBJFace face = new PShapeOBJ.OBJFace();
                            face.matIdx = mtlIdxCur;
                            face.name = gname;

                            for(int i = 1; i < parts.length; ++i) {
                                String seg = parts[i];
                                if (seg.indexOf("/") > 0) {
                                    String[] forder = seg.split("/");
                                    if (forder.length > 2) {
                                        if (forder[0].length() > 0 && readv) {
                                            face.vertIdx.add(Integer.valueOf(forder[0]));
                                        }

                                        if (forder[1].length() > 0 && readvt) {
                                            face.texIdx.add(Integer.valueOf(forder[1]));
                                        }

                                        if (forder[2].length() > 0 && readvn) {
                                            face.normIdx.add(Integer.valueOf(forder[2]));
                                        }
                                    } else if (forder.length > 1) {
                                        if (forder[0].length() > 0 && readv) {
                                            face.vertIdx.add(Integer.valueOf(forder[0]));
                                        }

                                        if (forder[1].length() > 0) {
                                            if (readvt) {
                                                face.texIdx.add(Integer.valueOf(forder[1]));
                                            } else if (readvn) {
                                                face.normIdx.add(Integer.valueOf(forder[1]));
                                            }
                                        }
                                    } else if (forder.length > 0 && forder[0].length() > 0 && readv) {
                                        face.vertIdx.add(Integer.valueOf(forder[0]));
                                    }
                                } else if (seg.length() > 0 && readv) {
                                    face.vertIdx.add(Integer.valueOf(seg));
                                }
                            }

                            faces.add(face);
                        }
                    }
                }
            }
        } catch (Exception var19) {
            var19.printStackTrace();
        }
    }

    protected static void parseMTL(PApplet parent, String mtlfn, BufferedReader reader, ArrayList<PShapeOBJ.OBJMaterial> materials, Map<String, Integer> materialsHash) {
        try {
            PShapeOBJ.OBJMaterial currentMtl = null;

            while(true) {
                while(true) {
                    String[] parts;
                    do {
                        String line;
                        if ((line = reader.readLine()) == null) {
                            return;
                        }

                        line = line.trim();
                        parts = line.split("\\s+");
                    } while(parts.length <= 0);

                    String texname;
                    if (parts[0].equals("newmtl")) {
                        texname = parts[1];
                        currentMtl = addMaterial(texname, materials, materialsHash);
                    } else {
                        if (currentMtl == null) {
                            currentMtl = addMaterial("material" + materials.size(), materials, materialsHash);
                        }

                        if (parts[0].equals("map_Kd") && parts.length > 1) {
                            texname = parts[1];
                            currentMtl.kdMap = parent.loadImage(texname);
                            if (currentMtl.kdMap == null) {
                                System.err.println("The texture map \"" + texname + "\" in the materials definition file \"" + mtlfn + "\" is missing or inaccessible, make sure the URL is valid or that the file has been added to your sketch and is readable.");
                            }
                        } else if (parts[0].equals("Ka") && parts.length > 3) {
                            currentMtl.ka.x = Float.valueOf(parts[1]);
                            currentMtl.ka.y = Float.valueOf(parts[2]);
                            currentMtl.ka.z = Float.valueOf(parts[3]);
                        } else if (parts[0].equals("Kd") && parts.length > 3) {
                            currentMtl.kd.x = Float.valueOf(parts[1]);
                            currentMtl.kd.y = Float.valueOf(parts[2]);
                            currentMtl.kd.z = Float.valueOf(parts[3]);
                        } else if (parts[0].equals("Ks") && parts.length > 3) {
                            currentMtl.ks.x = Float.valueOf(parts[1]);
                            currentMtl.ks.y = Float.valueOf(parts[2]);
                            currentMtl.ks.z = Float.valueOf(parts[3]);
                        } else if ((parts[0].equals("d") || parts[0].equals("Tr")) && parts.length > 1) {
                            currentMtl.d = Float.valueOf(parts[1]);
                        } else if (parts[0].equals("Ns") && parts.length > 1) {
                            currentMtl.ns = Float.valueOf(parts[1]);
                        }
                    }
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }

    protected static PShapeOBJ.OBJMaterial addMaterial(String mtlname, ArrayList<PShapeOBJ.OBJMaterial> materials, Map<String, Integer> materialsHash) {
        PShapeOBJ.OBJMaterial currentMtl = new PShapeOBJ.OBJMaterial(mtlname);
        materialsHash.put(mtlname, materials.size());
        materials.add(currentMtl);
        return currentMtl;
    }

    protected static int rgbaValue(PVector color) {
        return -16777216 | (int)(color.x * 255.0F) << 16 | (int)(color.y * 255.0F) << 8 | (int)(color.z * 255.0F);
    }

    protected static int rgbaValue(PVector color, float alpha) {
        return (int)(alpha * 255.0F) << 24 | (int)(color.x * 255.0F) << 16 | (int)(color.y * 255.0F) << 8 | (int)(color.z * 255.0F);
    }

    protected static class OBJMaterial {
        String name;
        PVector ka;
        PVector kd;
        PVector ks;
        float d;
        float ns;
        PImage kdMap;

        OBJMaterial() {
            this("default");
        }

        OBJMaterial(String name) {
            this.name = name;
            this.ka = new PVector(0.5F, 0.5F, 0.5F);
            this.kd = new PVector(0.5F, 0.5F, 0.5F);
            this.ks = new PVector(0.5F, 0.5F, 0.5F);
            this.d = 1.0F;
            this.ns = 0.0F;
            this.kdMap = null;
        }
    }

    protected static class OBJFace {
        ArrayList<Integer> vertIdx = new ArrayList();
        ArrayList<Integer> texIdx = new ArrayList();
        ArrayList<Integer> normIdx = new ArrayList();
        int matIdx = -1;
        String name = "";

        OBJFace() {
        }
    }
}

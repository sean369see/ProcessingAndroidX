/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2012-16 The Processing Foundation
  Copyright (c) 2004-12 Ben Fry and Casey Reas
  Copyright (c) 2001-04 Massachusetts Institute of Technology

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

import android.view.KeyEvent;
//import java.awt.Cursor;
//import java.awt.event.KeyEvent;


/**
 * Numbers shared throughout processing.core.
 * <P>
 * An attempt is made to keep the constants as short/non-verbose
 * as possible. For instance, the constant is TIFF instead of
 * FILE_TYPE_TIFF. We'll do this as long as we can get away with it.
 */
public interface PConstants {

  // vertex fields
  static public final int X = 0;  // model coords xyz (formerly MX/MY/MZ)
  static public final int Y = 1;
  static public final int Z = 2;

  // built-in rendering options
  static final String JAVA2D = "processing.core.PGraphicsAndroid2D";
  static final String P2D = "processing.opengl.PGraphics2D";
  static final String P2DX = "processing.opengl.PGraphics2DX";
  static final String P3D = "processing.opengl.PGraphics3D";
  static final String OPENGL = P3D;
  static final String STEREO = "processing.vr.VRGraphicsStereo";
  static final String MONO = "processing.vr.VRGraphicsMono";
  static final String VR = STEREO;
  static final String AR = "processing.ar.ARGraphics";
  static final String ARCORE = AR;

  // The PDF and DXF renderers are not available for Android.


  // platform IDs for PApplet.platform

  static final int OTHER   = 0;
  static final int WINDOWS = 1;
  static final int MACOSX  = 2;
  static final int LINUX   = 3;

  static final String[] platformNames = {
    "other", "windows", "macosx", "linux"
  };


  static final float EPSILON = 0.0001f;


  // max/min values for numbers

  /**
   * Same as Float.MAX_VALUE, but included for parity with MIN_VALUE,
   * and to avoid teaching static methods on the first day.
   */
  static final float MAX_FLOAT = Float.MAX_VALUE;
  /**
   * Note that Float.MIN_VALUE is the smallest <EM>positive</EM> value
   * for a floating point number, not actually the minimum (negative) value
   * for a float. This constant equals 0xFF7FFFFF, the smallest (farthest
   * negative) value a float can have before it hits NaN.
   */
  static final float MIN_FLOAT = -Float.MAX_VALUE;
  /** Largest possible (positive) integer value */
  static final int MAX_INT = Integer.MAX_VALUE;
  /** Smallest possible (negative) integer value */
  static final int MIN_INT = Integer.MIN_VALUE;


  // shapes

  static public final int VERTEX = 0;
  static public final int BEZIER_VERTEX = 1;
  static public final int QUADRATIC_VERTEX = 2;
  static public final int CURVE_VERTEX = 3;
  static public final int BREAK = 4;

  @Deprecated
  static public final int QUAD_BEZIER_VERTEX = 2;  // should not have been exposed

  // useful goodness

  static final float PI = (float) Math.PI;
  static final float HALF_PI    = PI / 2.0f;
  static final float THIRD_PI   = PI / 3.0f;
  static final float QUARTER_PI = PI / 4.0f;
  static final float TWO_PI     = PI * 2.0f;
  static final float TAU        = PI * 2.0f;

  static final float DEG_TO_RAD = PI/180.0f;
  static final float RAD_TO_DEG = 180.0f/PI;


  // angle modes

  //static final int RADIANS = 0;
  //static final int DEGREES = 1;


  // used by split, all the standard whitespace chars
  // (also includes unicode nbsp, that little bostage)

  static final String WHITESPACE = " \t\n\r\f\u00A0";


  // for colors and/or images

  static final int RGB    = 1;  // image & color
  static final int ARGB   = 2;  // image
  static final int HSB    = 3;  // color
  static final int ALPHA  = 4;  // image
  static final int CMYK   = 5;  // image & color (someday)
  static final int YUV420 = 6;  // Android video preview.


  // image file types

  static final int TIFF  = 0;
  static final int TARGA = 1;
  static final int JPEG  = 2;
  static final int GIF   = 3;


  // filter/convert types

  static final int BLUR      = 11;
  static final int GRAY      = 12;
  static final int INVERT    = 13;
  static final int OPAQUE    = 14;
  static final int POSTERIZE = 15;
  static final int THRESHOLD = 16;
  static final int ERODE     = 17;
  static final int DILATE    = 18;


  // blend mode keyword definitions
  // @see processing.core.PImage#blendColor(int,int,int)

  public final static int REPLACE    = 0;
  public final static int BLEND      = 1 << 0;
  public final static int ADD        = 1 << 1;
  public final static int SUBTRACT   = 1 << 2;
  public final static int LIGHTEST   = 1 << 3;
  public final static int DARKEST    = 1 << 4;
  public final static int DIFFERENCE = 1 << 5;
  public final static int EXCLUSION  = 1 << 6;
  public final static int MULTIPLY   = 1 << 7;
  public final static int SCREEN     = 1 << 8;
  public final static int OVERLAY    = 1 << 9;
  public final static int HARD_LIGHT = 1 << 10;
  public final static int SOFT_LIGHT = 1 << 11;
  public final static int DODGE      = 1 << 12;
  public final static int BURN       = 1 << 13;


  // for messages

  static final int CHATTER   = 0;
  static final int COMPLAINT = 1;
  static final int PROBLEM   = 2;

  // types of transformation matrices

  static final int PROJECTION = 0;
  static final int MODELVIEW  = 1;

  // types of projection matrices

  static final int CUSTOM       = 0; // user-specified fanciness
  static final int ORTHOGRAPHIC = 2; // 2D isometric projection
  static final int PERSPECTIVE  = 3; // perspective matrix


  // shapes

  // the low four bits set the variety,
  // higher bits set the specific shape type

  static final int GROUP           = 0;   // createShape()

  static final int POINT           = 2;   // primitive
  static final int POINTS          = 3;   // vertices

  static final int LINE            = 4;   // primitive
  static final int LINES           = 5;   // beginShape(), createShape()
  static final int LINE_STRIP      = 50;  // beginShape()
  static final int LINE_LOOP       = 51;

  static final int TRIANGLE        = 8;   // primitive
  static final int TRIANGLES       = 9;   // vertices
  static final int TRIANGLE_STRIP  = 10;  // vertices
  static final int TRIANGLE_FAN    = 11;  // vertices

  static final int QUAD            = 16;  // primitive
  static final int QUADS           = 17;  // vertices
  static final int QUAD_STRIP      = 18;  // vertices

  static final int POLYGON         = 20;  // in the end, probably cannot
  static final int PATH            = 21;  // separate these two

  static final int RECT            = 30;  // primitive
  static final int ELLIPSE         = 31;  // primitive
  static final int ARC             = 32;  // primitive

  static final int SPHERE          = 40;  // primitive
  static final int BOX             = 41;  // primitive

//  static public final int LINE_STRIP    = 50;
//  static public final int LINE_LOOP     = 51;
//  static public final int POINT_SPRITES = 52;


  // shape closing modes

  static final int OPEN = 1;
  static final int CLOSE = 2;


  // shape drawing modes

  /** Draw mode convention to use (x, y) to (width, height) */
  static final int CORNER   = 0;
  /** Draw mode convention to use (x1, y1) to (x2, y2) coordinates */
  static final int CORNERS  = 1;
  /** Draw mode from the center, and using the radius */
  static final int RADIUS   = 2;
  /** @deprecated Use RADIUS instead. */
  static final int CENTER_RADIUS = 2;
  /**
   * Draw from the center, using second pair of values as the diameter.
   * Formerly called CENTER_DIAMETER in alpha releases.
   */
  static final int CENTER   = 3;
  /**
   * Synonym for the CENTER constant. Draw from the center,
   * using second pair of values as the diameter.
   */
  static final int DIAMETER = 3;
  /** @deprecated Use DIAMETER instead. */
  static final int CENTER_DIAMETER = 3;


  // arc drawing modes

  //static final int OPEN = 1;  // shared
  static final int CHORD  = 2;
  static final int PIE    = 3;


  // vertically alignment modes for text

  /** Default vertical alignment for text placement */
  static final int BASELINE = 0;
  /** Align text to the top */
  static final int TOP = 101;
  /** Align text from the bottom, using the baseline. */
  static final int BOTTOM = 102;


  // uv texture orientation modes

  /** texture coordinates in 0..1 range */
  static final int NORMAL = 1;
  /** texture coordinates based on image width/height */
  static final int IMAGE  = 2;


  // texture wrapping modes

  /** textures are clamped to their edges */
  public static final int CLAMP = 0;
  /** textures wrap around when uv values go outside 0..1 range */
  public static final int REPEAT = 1;


  // text placement modes

  /**
   * textMode(MODEL) is the default, meaning that characters
   * will be affected by transformations like any other shapes.
   * <p/>
   * Changed value in 0093 to not interfere with LEFT, CENTER, and RIGHT.
   */
  static final int MODEL = 4;

  /**
   * textMode(SHAPE) draws text using the the glyph outlines of
   * individual characters rather than as textures. If the outlines are
   * not available, then textMode(SHAPE) will be ignored and textMode(MODEL)
   * will be used instead. For this reason, be sure to call textMode()
   * <EM>after</EM> calling textFont().
   * <p/>
   * Currently, textMode(SHAPE) is only supported by OPENGL mode.
   * It also requires Java 1.2 or higher (OPENGL requires 1.4 anyway)
   */
  static final int SHAPE = 5;


  // text alignment modes
  // are inherited from LEFT, CENTER, RIGHT


  // stroke modes

  static final int SQUARE   = 1 << 0;  // called 'butt' in the svg spec
  static final int ROUND    = 1 << 1;
  static final int PROJECT  = 1 << 2;  // called 'square' in the svg spec
  static final int MITER    = 1 << 3;
  static final int BEVEL    = 1 << 5;


  // lighting

  static final int AMBIENT = 0;
  static final int DIRECTIONAL  = 1;
  //static final int POINT  = 2;  // shared with shape feature
  static final int SPOT = 3;


  // key constants

  // only including the most-used of these guys
  // if people need more esoteric keys, they can learn about
  // the esoteric java KeyEvent api and of virtual keys

  // both key and keyCode will equal these values
  // for 0125, these were changed to 'char' values, because they
  // can be upgraded to ints automatically by Java, but having them
  // as ints prevented split(blah, TAB) from working
  static final char BACKSPACE = KeyEvent.KEYCODE_DEL;
  static final char TAB       = KeyEvent.KEYCODE_TAB;
  static final char ENTER     = KeyEvent.KEYCODE_ENTER;
  static final char RETURN    = KeyEvent.KEYCODE_ENTER;
  static final char ESC       = KeyEvent.KEYCODE_ESCAPE;
  static final char DELETE    = KeyEvent.KEYCODE_DEL;

  // i.e. if ((key == CODED) && (keyCode == UP))
  static final int CODED     = 0xffff;

  // key will be CODED and keyCode will be this value
  static final int UP        = KeyEvent.KEYCODE_DPAD_UP;
  static final int DOWN      = KeyEvent.KEYCODE_DPAD_DOWN;
  static final int LEFT      = KeyEvent.KEYCODE_DPAD_LEFT;
  static final int RIGHT     = KeyEvent.KEYCODE_DPAD_RIGHT;

  // These seem essential for most sketches, so they're included.
  // Others can be found in the KeyEvent reference:
  // http://developer.android.com/reference/android/view/KeyEvent.html
  static final int BACK = KeyEvent.KEYCODE_BACK;
  static final int MENU = KeyEvent.KEYCODE_MENU;
  static final int DPAD = KeyEvent.KEYCODE_DPAD_CENTER;


  // key will be CODED and keyCode will be this value
//  static final int ALT       = KeyEvent.VK_ALT;
//  static final int CONTROL   = KeyEvent.VK_CONTROL;
  static final int SHIFT     = KeyEvent.KEYCODE_SHIFT_LEFT;

  // cursor types

//  static final int ARROW = Cursor.DEFAULT_CURSOR;
//  static final int CROSS = Cursor.CROSSHAIR_CURSOR;
//  static final int HAND  = Cursor.HAND_CURSOR;
//  static final int MOVE  = Cursor.MOVE_CURSOR;
//  static final int TEXT  = Cursor.TEXT_CURSOR;
//  static final int WAIT  = Cursor.WAIT_CURSOR;


  /** Screen orientation constant for portrait (the hamburger way). */
  static final int PORTRAIT = 1;
  /** Screen orientation constant for landscape (the hot dog way). */
  static final int LANDSCAPE = 2;


  // hints - hint values are positive for the alternate version,
  // negative of the same value returns to the normal/default state

  @Deprecated
  static final int ENABLE_NATIVE_FONTS        =  1;
  @Deprecated
  static final int DISABLE_NATIVE_FONTS       = -1;

  static final int DISABLE_DEPTH_TEST         =  2;
  static final int ENABLE_DEPTH_TEST          = -2;

  static final int ENABLE_DEPTH_SORT          =  3;
  static final int DISABLE_DEPTH_SORT         = -3;

  static final int DISABLE_OPENGL_ERRORS      =  4;
  static final int ENABLE_OPENGL_ERRORS       = -4;

  static final int DISABLE_DEPTH_MASK         =  5;
  static final int ENABLE_DEPTH_MASK          = -5;

  static final int DISABLE_OPTIMIZED_STROKE   =  6;
  static final int ENABLE_OPTIMIZED_STROKE    = -6;

  static final int ENABLE_STROKE_PERSPECTIVE  =  7;
  static final int DISABLE_STROKE_PERSPECTIVE = -7;

  static final int DISABLE_TEXTURE_MIPMAPS    =  8;
  static final int ENABLE_TEXTURE_MIPMAPS     = -8;

  static final int ENABLE_STROKE_PURE         =  9;
  static final int DISABLE_STROKE_PURE        = -9;

  static final int ENABLE_BUFFER_READING      =  10;
  static final int DISABLE_BUFFER_READING     = -10;

  static final int DISABLE_KEY_REPEAT         =  11;
  static final int ENABLE_KEY_REPEAT          = -11;

  static final int DISABLE_ASYNC_SAVEFRAME    =  12;
  static final int ENABLE_ASYNC_SAVEFRAME     = -12;

  static final int HINT_COUNT                 =  13;


  // error messages

  static final String ERROR_BACKGROUND_IMAGE_SIZE =
    "background image must be the same size as your application";
  static final String ERROR_BACKGROUND_IMAGE_FORMAT =
    "background images should be RGB or ARGB";

  static final String ERROR_TEXTFONT_NULL_PFONT =
    "A null PFont was passed to textFont()";

  static final String ERROR_PUSHMATRIX_OVERFLOW =
    "Too many calls to pushMatrix().";
  static final String ERROR_PUSHMATRIX_UNDERFLOW =
    "Too many calls to popMatrix(), and not enough to pushMatrix().";


  // Some currently missing GLES constants.

//  static final int GL_MIN_EXT = 0x8007;
//  static final int GL_MAX_EXT = 0x8008;
}



// package processing.core;

// public interface PConstants {
//     int X = 0;
//     int Y = 1;
//     int Z = 2;
//     String JAVA2D = "processing.core.PGraphicsAndroid2D";
//     String P2D = "processing.opengl.PGraphics2D";
//     String P2DX = "processing.opengl.PGraphics2DX";
//     String P3D = "processing.opengl.PGraphics3D";
//     String OPENGL = "processing.opengl.PGraphics3D";
//     String STEREO = "processing.vr.VRGraphicsStereo";
//     String MONO = "processing.vr.VRPGraphicsMono";
//     String VR = "processing.vr.VRGraphicsStereo";
//     String AR = "processing.ar.ARGraphics";
//     String ARCORE = "processing.ar.ARGraphics";
//     int OTHER = 0;
//     int WINDOWS = 1;
//     int MACOSX = 2;
//     int LINUX = 3;
//     String[] platformNames = new String[]{"other", "windows", "macosx", "linux"};
//     float EPSILON = 1.0E-4F;
//     float MAX_FLOAT = 3.4028235E38F;
//     float MIN_FLOAT = -3.4028235E38F;
//     int MAX_INT = 2147483647;
//     int MIN_INT = -2147483648;
//     int VERTEX = 0;
//     int BEZIER_VERTEX = 1;
//     int QUADRATIC_VERTEX = 2;
//     int CURVE_VERTEX = 3;
//     int BREAK = 4;
//     /** @deprecated */
//     @Deprecated
//     int QUAD_BEZIER_VERTEX = 2;
//     float PI = 3.1415927F;
//     float HALF_PI = 1.5707964F;
//     float THIRD_PI = 1.0471976F;
//     float QUARTER_PI = 0.7853982F;
//     float TWO_PI = 6.2831855F;
//     float TAU = 6.2831855F;
//     float DEG_TO_RAD = 0.017453292F;
//     float RAD_TO_DEG = 57.295776F;
//     String WHITESPACE = " \t\n\r\f ";
//     int RGB = 1;
//     int ARGB = 2;
//     int HSB = 3;
//     int ALPHA = 4;
//     int CMYK = 5;
//     int YUV420 = 6;
//     int TIFF = 0;
//     int TARGA = 1;
//     int JPEG = 2;
//     int GIF = 3;
//     int BLUR = 11;
//     int GRAY = 12;
//     int INVERT = 13;
//     int OPAQUE = 14;
//     int POSTERIZE = 15;
//     int THRESHOLD = 16;
//     int ERODE = 17;
//     int DILATE = 18;
//     int REPLACE = 0;
//     int BLEND = 1;
//     int ADD = 2;
//     int SUBTRACT = 4;
//     int LIGHTEST = 8;
//     int DARKEST = 16;
//     int DIFFERENCE = 32;
//     int EXCLUSION = 64;
//     int MULTIPLY = 128;
//     int SCREEN = 256;
//     int OVERLAY = 512;
//     int HARD_LIGHT = 1024;
//     int SOFT_LIGHT = 2048;
//     int DODGE = 4096;
//     int BURN = 8192;
//     int CHATTER = 0;
//     int COMPLAINT = 1;
//     int PROBLEM = 2;
//     int PROJECTION = 0;
//     int MODELVIEW = 1;
//     int CUSTOM = 0;
//     int ORTHOGRAPHIC = 2;
//     int PERSPECTIVE = 3;
//     int GROUP = 0;
//     int POINT = 2;
//     int POINTS = 3;
//     int LINE = 4;
//     int LINES = 5;
//     int LINE_STRIP = 50;
//     int LINE_LOOP = 51;
//     int TRIANGLE = 8;
//     int TRIANGLES = 9;
//     int TRIANGLE_STRIP = 10;
//     int TRIANGLE_FAN = 11;
//     int QUAD = 16;
//     int QUADS = 17;
//     int QUAD_STRIP = 18;
//     int POLYGON = 20;
//     int PATH = 21;
//     int RECT = 30;
//     int ELLIPSE = 31;
//     int ARC = 32;
//     int SPHERE = 40;
//     int BOX = 41;
//     int OPEN = 1;
//     int CLOSE = 2;
//     int CORNER = 0;
//     int CORNERS = 1;
//     int RADIUS = 2;
//     /** @deprecated */
//     int CENTER_RADIUS = 2;
//     int CENTER = 3;
//     int DIAMETER = 3;
//     /** @deprecated */
//     int CENTER_DIAMETER = 3;
//     int CHORD = 2;
//     int PIE = 3;
//     int BASELINE = 0;
//     int TOP = 101;
//     int BOTTOM = 102;
//     int NORMAL = 1;
//     int IMAGE = 2;
//     int CLAMP = 0;
//     int REPEAT = 1;
//     int MODEL = 4;
//     int SHAPE = 5;
//     int SQUARE = 1;
//     int ROUND = 2;
//     int PROJECT = 4;
//     int MITER = 8;
//     int BEVEL = 32;
//     int AMBIENT = 0;
//     int DIRECTIONAL = 1;
//     int SPOT = 3;
//     char BACKSPACE = 'C';
//     char TAB = '=';
//     char ENTER = 'B';
//     char RETURN = 'B';
//     char ESC = 'o';
//     char DELETE = 'C';
//     int CODED = 65535;
//     int UP = 19;
//     int DOWN = 20;
//     int LEFT = 21;
//     int RIGHT = 22;
//     int BACK = 4;
//     int MENU = 82;
//     int DPAD = 23;
//     int SHIFT = 59;
//     int PORTRAIT = 1;
//     int LANDSCAPE = 2;
//     /** @deprecated */
//     @Deprecated
//     int ENABLE_NATIVE_FONTS = 1;
//     /** @deprecated */
//     @Deprecated
//     int DISABLE_NATIVE_FONTS = -1;
//     int DISABLE_DEPTH_TEST = 2;
//     int ENABLE_DEPTH_TEST = -2;
//     int ENABLE_DEPTH_SORT = 3;
//     int DISABLE_DEPTH_SORT = -3;
//     int DISABLE_OPENGL_ERRORS = 4;
//     int ENABLE_OPENGL_ERRORS = -4;
//     int DISABLE_DEPTH_MASK = 5;
//     int ENABLE_DEPTH_MASK = -5;
//     int DISABLE_OPTIMIZED_STROKE = 6;
//     int ENABLE_OPTIMIZED_STROKE = -6;
//     int ENABLE_STROKE_PERSPECTIVE = 7;
//     int DISABLE_STROKE_PERSPECTIVE = -7;
//     int DISABLE_TEXTURE_MIPMAPS = 8;
//     int ENABLE_TEXTURE_MIPMAPS = -8;
//     int ENABLE_STROKE_PURE = 9;
//     int DISABLE_STROKE_PURE = -9;
//     int ENABLE_BUFFER_READING = 10;
//     int DISABLE_BUFFER_READING = -10;
//     int DISABLE_KEY_REPEAT = 11;
//     int ENABLE_KEY_REPEAT = -11;
//     int DISABLE_ASYNC_SAVEFRAME = 12;
//     int ENABLE_ASYNC_SAVEFRAME = -12;
//     int HINT_COUNT = 13;
//     String ERROR_BACKGROUND_IMAGE_SIZE = "background image must be the same size as your application";
//     String ERROR_BACKGROUND_IMAGE_FORMAT = "background images should be RGB or ARGB";
//     String ERROR_TEXTFONT_NULL_PFONT = "A null PFont was passed to textFont()";
//     String ERROR_PUSHMATRIX_OVERFLOW = "Too many calls to pushMatrix().";
//     String ERROR_PUSHMATRIX_UNDERFLOW = "Too many calls to popMatrix(), and not enough to pushMatrix().";
// }

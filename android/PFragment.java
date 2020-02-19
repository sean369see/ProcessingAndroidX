/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2016-17 The Processing Foundation

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

package processing.android;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;

import processing.core.PApplet;

public class PFragment extends Fragment implements AppComponent {
    private DisplayMetrics metrics;
    private Point size;
    private PApplet sketch;
    @LayoutRes
    private int layout = -1;

    public PFragment() {
      super();
    }

    public PFragment(PApplet sketch) {
      super();
      this.setSketch(sketch);
    }

    public void initDimensions() {
      this.metrics = new DisplayMetrics();
      this.size = new Point();
      WindowManager wm = this.getActivity().getWindowManager();
      Display display = wm.getDefaultDisplay();
      CompatUtils.getDisplayParams(display, this.metrics, this.size);
    }

    public int getDisplayWidth() {
        return this.size.x;
    }

    public int getDisplayHeight() {
        return this.size.y;
    }

    public float getDisplayDensity() {
        return this.metrics.density;
    }

    public int getKind() {
        //return 0;
        return FRAGMENT; 
    }

    public void setSketch(PApplet sketch) {
      this.sketch = sketch;
      if (this.layout != -1) {
        sketch.parentLayout = this.layout;
      }
    }

    public PApplet getSketch() {
        return this.sketch;
    }

    public void setLayout(@LayoutRes int layout, @IdRes int id, FragmentActivity activity) {
        this.layout = layout;
        if (this.sketch != null) {
            this.sketch.parentLayout = layout;
        }

        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(id, this);
        transaction.commit();
    }

    public void setView(View view, FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(view.getId(), this);
        transaction.commit();
    }

    public boolean isService() {
        return false;
    }

    public ServiceEngine getEngine() {
        return null;
    }

    public void dispose() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.sketch != null) {
            this.sketch.initSurface(inflater, container, savedInstanceState, this, (SurfaceHolder)null);

            // For compatibility with older sketches that run some hardware initialization
            // inside onCreate(), don't call from Fragment.onCreate() because the surface
            // will not be yet ready, and so the reference to the activity and other
            // system variables will be null. In any case, onCreateView() is called
            // immediately after onCreate():
            // https://developer.android.com/reference/android/app/Fragment.html#Lifecycle
            this.sketch.onCreate(savedInstanceState);

            return this.sketch.getSurface().getRootView();
        } else {
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.sketch != null) {
            this.sketch.onStart();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.sketch != null) {
            this.sketch.onResume();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.sketch != null) {
            this.sketch.onPause();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.sketch != null) {
            this.sketch.onStop();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.sketch != null) {
            this.sketch.onDestroy();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.sketch != null) {
            this.sketch.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.sketch != null) {
            this.sketch.onCreateOptionsMenu(menu, inflater);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.sketch != null ? this.sketch.onOptionsItemSelected(item) : super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (this.sketch != null) {
            this.sketch.onCreateContextMenu(menu, v, menuInfo);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return this.sketch != null ? this.sketch.onContextItemSelected(item) : super.onContextItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setOrientation(int which) {
        /*
        if (which == 1) {
            this.getActivity().setRequestedOrientation(1);
        } else if (which == 2) {
            this.getActivity().setRequestedOrientation(0);
        }
        */
        
        if (which == PORTRAIT) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (which == LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }

    public void requestDraw() {
    }

    public boolean canDraw() {
        return this.sketch != null && this.sketch.isLooping();
    }
}

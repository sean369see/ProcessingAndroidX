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

package processing.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.app.FragmentManager;

// Methods that should be implemented in PApplet to maintain backward
// compatibility with (some) functionality available from Activity/Fragment
public interface ActivityAPI {

    // Lifecycle events
    public void onCreate(Bundle var1);
    public void onDestroy();
    public void onStart();
    public void onStop();
    public void onPause();
    public void onResume();

    // Activity and intent events
    public void onActivityResult(int var1, int var2, Intent var3);
    public void onNewIntent(Intent var1);

    // Menu API
    public void onCreateOptionsMenu(Menu var1, MenuInflater var2);
    public boolean onOptionsItemSelected(MenuItem var1);
    public void onCreateContextMenu(ContextMenu var1, View var2, ContextMenuInfo var3);
    public boolean onContextItemSelected(MenuItem var1);
    public void setHasOptionsMenu(boolean var1);

    // IO events
    public void onBackPressed();

    // Activity management
    public FragmentManager getFragmentManager();
    public Window getWindow();

}

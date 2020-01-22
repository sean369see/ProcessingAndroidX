package processing.android;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;

public interface ActivityAPI {
    void onCreate(Bundle var1);

    void onDestroy();

    void onStart();

    void onStop();

    void onPause();

    void onResume();

    void onActivityResult(int var1, int var2, Intent var3);

    void onNewIntent(Intent var1);

    void onCreateOptionsMenu(Menu var1, MenuInflater var2);

    boolean onOptionsItemSelected(MenuItem var1);

    void onCreateContextMenu(ContextMenu var1, View var2, ContextMenuInfo var3);

    boolean onContextItemSelected(MenuItem var1);

    void setHasOptionsMenu(boolean var1);

    void onBackPressed();

    FragmentManager getFragmentManager();

    Window getWindow();
}

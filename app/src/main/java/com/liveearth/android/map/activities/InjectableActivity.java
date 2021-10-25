package com.liveearth.android.map.activities;

import android.app.Activity;

import com.liveearth.android.map.StardroidApplication;
import com.liveearth.android.map.ApplicationComponent;

/**
 * Base class for all activities injected by Dagger.
 *
 * Created by johntaylor on 4/9/16.
 */
public abstract class InjectableActivity extends Activity {
  protected ApplicationComponent getApplicationComponent() {
    return ((StardroidApplication) getApplication()).getApplicationComponent();
  }
}

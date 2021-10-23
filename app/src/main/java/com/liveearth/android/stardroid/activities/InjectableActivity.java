package com.liveearth.android.stardroid.activities;

import android.app.Activity;

import com.liveearth.android.stardroid.ApplicationComponent;
import com.liveearth.android.stardroid.StardroidApplication;

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

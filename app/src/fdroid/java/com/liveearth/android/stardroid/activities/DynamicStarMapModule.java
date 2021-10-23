package com.google.android.stardroid.activities;

import com.liveearth.android.stardroid.activities.AbstractDynamicStarMapModule;
import com.liveearth.android.stardroid.activities.DynamicStarMapActivity;

import dagger.Module;

/**
 * Dagger module
 * Created by johntaylor on 3/29/16.
 */
@Module
public class DynamicStarMapModule extends AbstractDynamicStarMapModule {
  public DynamicStarMapModule(DynamicStarMapActivity activity) {
    super(activity);
  }
}

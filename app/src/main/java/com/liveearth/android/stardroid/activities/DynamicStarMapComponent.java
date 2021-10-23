package com.liveearth.android.stardroid.activities;

import com.liveearth.android.stardroid.ApplicationComponent;
import com.google.android.stardroid.activities.DynamicStarMapModule;
import com.liveearth.android.stardroid.activities.dialogs.EulaDialogFragment;
import com.liveearth.android.stardroid.activities.dialogs.HelpDialogFragment;
import com.liveearth.android.stardroid.activities.dialogs.MultipleSearchResultsDialogFragment;
import com.liveearth.android.stardroid.activities.dialogs.NoSearchResultsDialogFragment;
import com.liveearth.android.stardroid.activities.dialogs.NoSensorsDialogFragment;
import com.liveearth.android.stardroid.activities.dialogs.TimeTravelDialogFragment;
import com.liveearth.android.stardroid.inject.PerActivity;

import dagger.Component;

/**
 * Created by johntaylor on 3/29/16.
 */
@PerActivity
@Component(modules = DynamicStarMapModule.class, dependencies = ApplicationComponent.class)
public interface DynamicStarMapComponent extends EulaDialogFragment.ActivityComponent,
    TimeTravelDialogFragment.ActivityComponent, HelpDialogFragment.ActivityComponent,
    NoSearchResultsDialogFragment.ActivityComponent,
    MultipleSearchResultsDialogFragment.ActivityComponent,
    NoSensorsDialogFragment.ActivityComponent {
  void inject(DynamicStarMapActivity activity);
}


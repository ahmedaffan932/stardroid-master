package com.liveearth.android.map.activities;

import com.liveearth.android.map.inject.PerActivity;
import com.liveearth.android.map.ApplicationComponent;
import com.liveearth.android.map.activities.dialogs.EulaDialogFragment;
import com.liveearth.android.map.activities.dialogs.HelpDialogFragment;
import com.liveearth.android.map.activities.dialogs.MultipleSearchResultsDialogFragment;
import com.liveearth.android.map.activities.dialogs.NoSearchResultsDialogFragment;
import com.liveearth.android.map.activities.dialogs.NoSensorsDialogFragment;
import com.liveearth.android.map.activities.dialogs.TimeTravelDialogFragment;

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


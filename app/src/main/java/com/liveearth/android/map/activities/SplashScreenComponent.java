package com.liveearth.android.map.activities;

import com.liveearth.android.map.inject.PerActivity;
import com.liveearth.android.map.ApplicationComponent;
import com.liveearth.android.map.activities.dialogs.EulaDialogFragment;
import com.liveearth.android.map.activities.dialogs.WhatsNewDialogFragment;

import dagger.Component;

/**
 * Created by johntaylor on 4/2/16.
 */
@PerActivity
@Component(modules = SplashScreenModule.class, dependencies = ApplicationComponent.class)
public interface SplashScreenComponent extends EulaDialogFragment.ActivityComponent,
    WhatsNewDialogFragment.ActivityComponent {
  void inject(SplashScreenActivity activity);
}

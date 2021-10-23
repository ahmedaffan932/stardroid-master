package com.liveearth.android.stardroid.activities;

import com.liveearth.android.stardroid.ApplicationComponent;
import com.liveearth.android.stardroid.activities.dialogs.EulaDialogFragment;
import com.liveearth.android.stardroid.activities.dialogs.WhatsNewDialogFragment;
import com.liveearth.android.stardroid.inject.PerActivity;

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

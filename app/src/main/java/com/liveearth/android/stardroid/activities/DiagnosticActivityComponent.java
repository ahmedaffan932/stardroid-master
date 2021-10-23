package com.liveearth.android.stardroid.activities;

import com.liveearth.android.stardroid.ApplicationComponent;
import com.liveearth.android.stardroid.inject.PerActivity;

import dagger.Component;

/**
 * Created by johntaylor on 4/15/16.
 */
@PerActivity
@Component(modules = DiagnosticActivityModule.class, dependencies = ApplicationComponent.class)
public interface DiagnosticActivityComponent {
  void inject(DiagnosticActivity activity);
}

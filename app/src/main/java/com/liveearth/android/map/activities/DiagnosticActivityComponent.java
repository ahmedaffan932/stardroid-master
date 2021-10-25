package com.liveearth.android.map.activities;

import com.liveearth.android.map.inject.PerActivity;
import com.liveearth.android.map.ApplicationComponent;

import dagger.Component;

/**
 * Created by johntaylor on 4/15/16.
 */
@PerActivity
@Component(modules = DiagnosticActivityModule.class, dependencies = ApplicationComponent.class)
public interface DiagnosticActivityComponent {
  void inject(DiagnosticActivity activity);
}

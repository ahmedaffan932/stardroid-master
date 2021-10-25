package com.liveearth.android.map.activities;

import com.liveearth.android.map.inject.PerActivity;
import com.liveearth.android.map.ApplicationComponent;

import dagger.Component;

/**
 * Created by johntaylor on 4/24/16.
 */
@PerActivity
@Component(modules = CompassCalibrationModule.class, dependencies = ApplicationComponent.class)
public interface CompassCalibrationComponent {
  void inject(CompassCalibrationActivity activity);
}


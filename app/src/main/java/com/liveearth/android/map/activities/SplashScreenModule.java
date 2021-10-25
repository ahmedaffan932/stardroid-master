package com.liveearth.android.map.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.liveearth.android.map.inject.PerActivity;
import com.liveearth.android.map.R;
import com.liveearth.android.map.activities.dialogs.EulaDialogFragment;
import com.liveearth.android.map.activities.dialogs.WhatsNewDialogFragment;

import dagger.Module;
import dagger.Provides;

/**
 * Created by johntaylor on 4/2/16.
 */
@Module
public class SplashScreenModule {
  private SplashScreenActivity activity;

  public SplashScreenModule(SplashScreenActivity activity) {
    this.activity = activity;
  }

  @Provides
  @PerActivity
  EulaDialogFragment provideEulaDialogFragment() {
    EulaDialogFragment fragment = new EulaDialogFragment();
    fragment.setEulaAcceptanceListener(activity);
    return fragment;
  }

  @Provides
  @PerActivity
  WhatsNewDialogFragment provideWhatsNewDialogFragment() {
    WhatsNewDialogFragment whatsNewDialogFragment = new WhatsNewDialogFragment();
    whatsNewDialogFragment.setCloseListener(activity);
    return whatsNewDialogFragment;
  }

  @Provides
  @PerActivity
  Activity provideActivity() {
    return activity;
  }

  @Provides
  @PerActivity
  Animation provideFadeoutAnimation() {
    return AnimationUtils.loadAnimation(activity, R.anim.fadeout);
  }

  @Provides
  @PerActivity
  FragmentManager provideFragmentManager() {
    return activity.getFragmentManager();
  }
}

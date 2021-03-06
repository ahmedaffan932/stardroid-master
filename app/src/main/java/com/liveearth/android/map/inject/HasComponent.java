package com.liveearth.android.map.inject;

/**
 * Implemented by activities to access their dagger component.
 * Created by johntaylor on 4/9/16.
 */
public interface HasComponent<C> {
  C getComponent();
}

// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.liveearth.android.map.layers;

import android.content.res.Resources;
import android.util.Log;

import com.liveearth.android.map.util.MiscUtil;
import com.liveearth.android.map.renderer.RendererController;
import com.liveearth.android.map.renderer.RendererController.AtomicSection;
import com.liveearth.android.map.renderer.RendererControllerBase;
import com.liveearth.android.map.renderer.RendererControllerBase.RenderManager;
import com.liveearth.android.map.renderer.RendererObjectManager.UpdateType;
import com.liveearth.android.map.renderer.util.UpdateClosure;
import com.liveearth.android.map.search.SearchResult;
import com.liveearth.android.map.source.ImageSource;
import com.liveearth.android.map.source.LineSource;
import com.liveearth.android.map.source.PointSource;
import com.liveearth.android.map.source.TextSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Base implementation of the {@link Layer} interface.
 *
 * @author John Taylor
 * @author Brent Bryan
 */
public abstract class AbstractLayer implements Layer {
  private static final String TAG = MiscUtil.getTag(AbstractLayer.class);

  private final ReentrantLock renderMapLock = new ReentrantLock();
  private final HashMap<Class<?>, RenderManager<?>> renderMap = new HashMap<>();
  private final Resources resources;

  private RendererController renderer;

  public AbstractLayer(Resources resources) {
    this.resources = resources;
  }

  protected Resources getResources() {
    return resources;
  }

  @Override
  public void registerWithRenderer(RendererController rendererController) {
    this.renderMap.clear();
    this.renderer = rendererController;
    updateLayerForControllerChange();
  }

  protected abstract void updateLayerForControllerChange();

  @Override
  public void setVisible(boolean visible) {
    renderMapLock.lock();
    try {
      if (renderer == null) {
        Log.w(TAG, "Renderer not set - aborting " + this.getClass().getSimpleName());
        return;
      }

      AtomicSection atomic = renderer.createAtomic();
      for (Entry<Class<?>, RenderManager<?>> entry: renderMap.entrySet()) {
        entry.getValue().queueEnabled(visible, atomic);
      }
      renderer.queueAtomic(atomic);
    } finally {
      renderMapLock.unlock();
    }
  }

  protected void addUpdateClosure(UpdateClosure closure) {
    if (renderer != null) {
      renderer.addUpdateClosure(closure);
    }
  }

  protected void removeUpdateClosure(UpdateClosure closure) {
    if (renderer != null) {
      renderer.removeUpdateCallback(closure);
    }
  }

  /**
   * Forces a redraw of this layer, clearing all of the information about this
   * layer in the renderer and repopulating it.
   */
  protected abstract void redraw();


  protected void redraw(
      final ArrayList<TextSource> textSources,
      final ArrayList<PointSource> pointSources,
      final ArrayList<LineSource> lineSources,
      final ArrayList<ImageSource> imageSources) {
    redraw(textSources, pointSources, lineSources, imageSources, EnumSet.of(UpdateType.Reset));
  }

  /**
   * Updates the renderer (using the given {@link UpdateType}), with then given set of
   * UI elements.  Depending on the value of {@link UpdateType}, current sources will
   * either have their state updated, or will be overwritten by the given set
   * of UI elements.
   */
  protected void redraw(
      final ArrayList<TextSource> textSources,
      final ArrayList<PointSource> pointSources,
      final ArrayList<LineSource> lineSources,
      final ArrayList<ImageSource> imageSources,
      EnumSet<UpdateType> updateTypes) {

    // Log.d(TAG, getLayerName() + " Updating renderer: " + updateTypes);
    if (renderer == null) {
      Log.w(TAG, "Renderer not set - aborting: " + this.getClass().getSimpleName());
      return;
    }

    renderMapLock.lock();
    try {
      // Blog.d(this, "Redraw: " + updateTypes);
      AtomicSection atomic = renderer.createAtomic();
      setSources(textSources, updateTypes, TextSource.class, atomic);
      setSources(pointSources, updateTypes, PointSource.class, atomic);
      setSources(lineSources, updateTypes, LineSource.class, atomic);
      setSources(imageSources, updateTypes, ImageSource.class, atomic);
      renderer.queueAtomic(atomic);
    } finally {
      renderMapLock.unlock();
    }
  }

  /**
   * Sets the objects on the {@link RenderManager} to the given values,
   * creating (or disabling) the {@link RenderManager} if necessary.
   */
  private <E> void setSources(ArrayList<E> sources, EnumSet<UpdateType> updateType,
      Class<E> clazz, AtomicSection atomic) {

    @SuppressWarnings("unchecked")
    RenderManager<E> manager = (RenderManager<E>) renderMap.get(clazz);
    if (sources == null || sources.isEmpty()) {
      if (manager != null) {
        // TODO(brent): we should really just disable this layer, but in a
        // manner that it will automatically be reenabled when appropriate.
        Log.d(TAG, "       " + clazz.getSimpleName());
        manager.queueObjects(Collections.<E>emptyList(), updateType, atomic);
      }
      return;
    }

    if (manager == null) {
      manager = createRenderManager(clazz, atomic);
      renderMap.put(clazz, manager);
    }
    // Blog.d(this, "       " + clazz.getSimpleName() + " " + sources.size());
    manager.queueObjects(sources, updateType, atomic);
  }

  @SuppressWarnings("unchecked")
  <E> RenderManager<E> createRenderManager(Class<E> clazz, RendererControllerBase controller) {
    if (clazz == ImageSource.class) {
      return (RenderManager<E>) controller.createImageManager(getLayerDepthOrder());

    } else if (clazz == TextSource.class) {
      return (RenderManager<E>) controller.createLabelManager(getLayerDepthOrder());

    } else if (clazz == LineSource.class) {
      return (RenderManager<E>) controller.createLineManager(getLayerDepthOrder());

    } else if (clazz == PointSource.class) {
      return (RenderManager<E>) controller.createPointManager(getLayerDepthOrder());
    }
    throw new IllegalStateException("Unknown source type: " + clazz);
  }

  @Override
  public List<SearchResult> searchByObjectName(String name) {
    // By default, layers will return no search results.
    // Override this if the layer should be searchable.
    return Collections.emptyList();
  }

  @Override
  public Set<String> getObjectNamesMatchingPrefix(String prefix) {
    // By default, layers will return no search results.
    // Override this if the layer should be searchable.
    return Collections.emptySet();
  }

  /**
   * Provides a string ID to the internationalized name of this layer.
   */
  // TODO(brent): could this be combined with getLayerDepthOrder?  Not sure - they
  // serve slightly different purposes.
  protected abstract int getLayerNameId();

  @Override
  public String getPreferenceId() {
    return getPreferenceId(getLayerNameId());
  }

  protected String getPreferenceId(int layerNameId) {
    return "source_provider." + layerNameId;
  }

  @Override
  public String getLayerName() {
    return getStringFromId(getLayerNameId());
  }

  /**
   * Return an internationalized string from a string resource id.
   */
  protected String getStringFromId(int resourceId) {
    return resources.getString(resourceId);
  }
}

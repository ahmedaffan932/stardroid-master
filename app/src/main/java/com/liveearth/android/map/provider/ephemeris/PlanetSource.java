// Copyright 2010 Google Inc.
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

package com.liveearth.android.map.provider.ephemeris;

import com.liveearth.android.map.base.Lists;
import com.liveearth.android.map.control.AstronomerModel;
import com.liveearth.android.map.renderer.RendererObjectManager;
import com.liveearth.android.map.source.AbstractAstronomicalSource;
import com.liveearth.android.map.source.AstronomicalSource;
import com.liveearth.android.map.source.ImageSource;
import com.liveearth.android.map.source.PointSource;
import com.liveearth.android.map.source.Sources;
import com.liveearth.android.map.source.TextSource;
import com.liveearth.android.map.source.impl.ImageSourceImpl;
import com.liveearth.android.map.units.GeocentricCoordinates;
import com.liveearth.android.map.units.HeliocentricCoordinates;
import com.liveearth.android.map.units.RaDec;
import com.liveearth.android.map.units.Vector3;
import com.liveearth.android.map.source.impl.PointSourceImpl;
import com.liveearth.android.map.source.impl.TextSourceImpl;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

/**
 * Implementation of the
 * {@link AstronomicalSource} for planets.
 *
 * @author Brent Bryan
 */
public class PlanetSource extends AbstractAstronomicalSource {
  private static final int PLANET_SIZE = 3;
  private static final int PLANET_COLOR = Color.argb(20, 129, 126, 246);
  private static final int PLANET_LABEL_COLOR = 0xf67e81;
  private static final String SHOW_PLANETARY_IMAGES = "show_planetary_images";
  private static final Vector3 UP = new Vector3(0.0f, 1.0f, 0.0f);

  private final ArrayList<PointSource> pointSources = new ArrayList<PointSource>();
  private final ArrayList<ImageSourceImpl> imageSources = new ArrayList<ImageSourceImpl>();
  private final ArrayList<TextSource> labelSources = new ArrayList<TextSource>();
  private final Planet planet;
  private final Resources resources;
  private final AstronomerModel model;
  private final String name;
  private final SharedPreferences preferences;
  private final GeocentricCoordinates currentCoords = new GeocentricCoordinates(0, 0, 0);
  private HeliocentricCoordinates sunCoords;
  private int imageId = -1;

  private long lastUpdateTimeMs  = 0L;

  public PlanetSource(Planet planet, Resources resources,
      AstronomerModel model, SharedPreferences prefs) {

    this.planet = planet;
    this.resources = resources;
    this.model = model;
    this.name = resources.getString(planet.getNameResourceId());
    this.preferences = prefs;
  }

  @Override
  public List<String> getNames() {
    return Lists.asList(name);
  }

  @Override
  public GeocentricCoordinates getSearchLocation() {
    return currentCoords;
  }

  private void updateCoords(Date time) {
    this.lastUpdateTimeMs = time.getTime();
    this.sunCoords = HeliocentricCoordinates.getInstance(Planet.Sun, time);
    this.currentCoords.updateFromRaDec(RaDec.getInstance(planet, time, sunCoords));
    for (ImageSourceImpl imageSource : imageSources) {
      imageSource.setUpVector(sunCoords);  // TODO(johntaylor): figure out why we do this.
    }
  }

  @Override
  public Sources initialize() {
    Date time = model.getTime();
    updateCoords(time);
    this.imageId = planet.getImageResourceId(time);

    if (planet == Planet.Moon) {
      imageSources.add(new ImageSourceImpl(currentCoords, resources, imageId, sunCoords,
          planet.getPlanetaryImageSize()));
    } else {
      boolean usePlanetaryImages = preferences.getBoolean(SHOW_PLANETARY_IMAGES, true);
      if (usePlanetaryImages || planet == Planet.Sun) {
        imageSources.add(new ImageSourceImpl(currentCoords, resources, imageId, UP,
            planet.getPlanetaryImageSize()));
      } else {
        pointSources.add(new PointSourceImpl(currentCoords, PLANET_COLOR, PLANET_SIZE));
      }
    }
    labelSources.add(new TextSourceImpl(currentCoords, name, PLANET_LABEL_COLOR));

    return this;
  }

  @Override
  public EnumSet<RendererObjectManager.UpdateType> update() {
    EnumSet<RendererObjectManager.UpdateType> updates = EnumSet.noneOf(RendererObjectManager.UpdateType.class);

    Date modelTime = model.getTime();
    if (Math.abs(modelTime.getTime() - lastUpdateTimeMs) > planet.getUpdateFrequencyMs()) {
      updates.add(RendererObjectManager.UpdateType.UpdatePositions);
      // update location
      updateCoords(modelTime);

      // For moon only:
      if (planet == Planet.Moon && !imageSources.isEmpty()) {
        // Update up vector.
        imageSources.get(0).setUpVector(sunCoords);

        // update image:
        int newImageId = planet.getImageResourceId(modelTime);
        if (newImageId != imageId) {
          imageId = newImageId;
          imageSources.get(0).setImageId(imageId);
          updates.add(RendererObjectManager.UpdateType.UpdateImages);
        }
      }
    }
    return updates;
  }

  @Override
  public List<? extends ImageSource> getImages() {
    return imageSources;
  }

  @Override
  public List<? extends TextSource> getLabels() {
    return labelSources;
  }

  @Override
  public List<? extends PointSource> getPoints() {
    return pointSources;
  }
}

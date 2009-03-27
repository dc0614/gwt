/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.resources.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Tests ImageResource generation.
 */
public class ImageResourceTest extends GWTTestCase {
  static interface Resources extends ClientBundle {
    @Source("16x16.png")
    ImageResource i16x16();

    @Source("16x16.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource i16x16Horizontal();

    @Source("16x16.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource i16x16Vertical();

    @Source("32x32.png")
    ImageResource i32x32();

    @Source("32x32.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource i32x32Horizontal();

    @Source("32x32.png")
    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource i32x32Vertical();

    @Source("64x64.png")
    ImageResource i64x64();

    @Source("64x64.png")
    ImageResource i64x64Dup();

    @Source("64x64-dup.png")
    ImageResource i64x64Dup2();

    // Test default filename lookup while we're at it
    ImageResource largeLossless();

    // Test default filename lookup while we're at it
    ImageResource largeLossy();
  }

  @Override
  public String getModuleName() {
    return "com.google.gwt.resources.Resources";
  }

  public void testDedup() {
    Resources r = GWT.create(Resources.class);

    ImageResource a = r.i64x64();
    ImageResource b = r.i64x64Dup();
    ImageResource c = r.i64x64Dup2();
    assertEquals(64, a.getHeight());
    assertEquals(64, a.getWidth());

    assertEquals(a.getLeft(), b.getLeft());
    assertEquals(a.getLeft(), c.getLeft());

    assertEquals(a.getLeft(), b.getTop());
    assertEquals(a.getLeft(), c.getTop());

    // See if the size of the image strip is what we expect
    Image i = new Image(a.getURL());
    i.addLoadHandler(new LoadHandler() {
      public void onLoad(LoadEvent event) {
        finishTest();
      }
    });
    i.addErrorHandler(new ErrorHandler() {
      public void onError(ErrorEvent event) {
        fail("ErrorEvent");
      }
    });

    RootPanel.get().add(i);
    delayTestFinish(500);
  }

  public void testPacking() {
    Resources r = GWT.create(Resources.class);

    ImageResource i64 = r.i64x64();
    ImageResource lossy = r.largeLossy();
    ImageResource lossless = r.largeLossless();

    // The large, lossless image should not be bundled
    if (!i64.getURL().startsWith("data:")) {
      assertFalse(i64.getURL().equals(lossless.getURL()));
    }

    // Make sure that the large, lossy image isn't bundled with the rest
    assertTrue(!i64.getURL().equals(lossy.getURL()));

    assertEquals(16, r.i16x16Vertical().getWidth());
    assertEquals(16, r.i16x16Vertical().getHeight());

    assertEquals(16, r.i16x16Horizontal().getWidth());
    assertEquals(16, r.i16x16Horizontal().getHeight());
  }
}

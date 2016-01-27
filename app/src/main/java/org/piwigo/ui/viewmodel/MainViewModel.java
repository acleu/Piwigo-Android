/*
 * Copyright 2016 Phil Bayfield https://philio.me
 * Copyright 2016 Piwigo Team http://piwigo.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.piwigo.ui.viewmodel;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.View;

import org.piwigo.internal.binding.observable.DrawerStateObservable;
import org.piwigo.internal.binding.observable.NavigationItemObservable;
import org.piwigo.ui.model.User;
import org.piwigo.ui.view.MainView;

import javax.inject.Inject;

public class MainViewModel extends BaseViewModel {

    @VisibleForTesting static final String STATE_DRAWER_OPEN = "drawer_open";
    @VisibleForTesting static final String STATE_NAVIGATION_ITEM = "navigation_item";

    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> username = new ObservableField<>();
    public ObservableField<String> url = new ObservableField<>();
    public DrawerStateObservable drawerState = new DrawerStateObservable(false);
    public NavigationItemObservable navigationItem = new NavigationItemObservable();

    private MainView view;
    private NavigationChangedCallback navigationChangedCallback = new NavigationChangedCallback();

    @Inject public MainViewModel() {}

    public void setView(MainView view) {
        this.view = view;
        navigationItem.addOnPropertyChangedCallback(navigationChangedCallback);
    }

    @Override public void onSaveState(Bundle outState) {
        outState.putBoolean(STATE_DRAWER_OPEN, drawerState.get());
        outState.putInt(STATE_NAVIGATION_ITEM, navigationItem.get());
    }

    @Override public void onRestoreState(Bundle savedState) {
        drawerState.set(savedState.getBoolean(STATE_DRAWER_OPEN));
        navigationItem.set(savedState.getInt(STATE_NAVIGATION_ITEM));
    }

    @Override public void onDestroy() {
        navigationItem.removeOnPropertyChangedCallback(navigationChangedCallback);
        view = null;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setUser(User user) {
        username.set(user.username);
        url.set(user.url);
    }

    public void navigationIconClick(View view) {
        drawerState.set(true);
    }

    private class NavigationChangedCallback extends Observable.OnPropertyChangedCallback {

        private boolean firstChange = true;

        @Override public void onPropertyChanged(Observable sender, int propertyId) {
            view.onItemSelected(navigationItem.get());
            if (firstChange) {
                firstChange = false;
            } else {
                drawerState.set(false);
            }
        }

    }

}

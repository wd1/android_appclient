/*
 * 	Copyright (c) 2017. Toshi Inc
 *
 * 	This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.toshi.view.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;

import com.toshi.view.fragment.toplevel.BrowseFragment;
import com.toshi.view.fragment.toplevel.FavoritesFragment;
import com.toshi.view.fragment.toplevel.MeFragment;
import com.toshi.view.fragment.toplevel.RecentFragment;
import com.toshi.view.fragment.toplevel.ScanFragment;

import java.util.ArrayList;

public class NavigationAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();

    public NavigationAdapter(final AppCompatActivity activity, final int menuRes) {
        super(activity.getSupportFragmentManager());

        final PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(android.R.id.content));
        final Menu menu = popupMenu.getMenu();
        activity.getMenuInflater().inflate(menuRes, menu);

        fragments.clear();
        fragments.add(new BrowseFragment());
        fragments.add(new RecentFragment());
        fragments.add(new ScanFragment());
        fragments.add(new FavoritesFragment());
        fragments.add(new MeFragment());
    }

    @Override
    public Fragment getItem(final int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
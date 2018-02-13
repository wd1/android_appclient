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

package com.toshi.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.toshi.R;
import com.toshi.databinding.ActivityLibLicensesBinding;
import com.toshi.presenter.LicenseListPresenter;
import com.toshi.presenter.LoaderIds;
import com.toshi.presenter.factory.LicenseListPresenterFactory;
import com.toshi.presenter.factory.PresenterFactory;

public class LicenseListActivity extends BasePresenterActivity<LicenseListPresenter, LicenseListActivity> {

    private ActivityLibLicensesBinding binding;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lib_licenses);
    }

    public final ActivityLibLicensesBinding getBinding() {
        return this.binding;
    }

    @NonNull
    @Override
    protected PresenterFactory<LicenseListPresenter> getPresenterFactory() {
        return new LicenseListPresenterFactory();
    }

    @Override
    protected void onPresenterPrepared(@NonNull LicenseListPresenter presenter) {}

    @Override
    protected int loaderId() {
        return LoaderIds.get(this.getClass().getCanonicalName());
    }
}

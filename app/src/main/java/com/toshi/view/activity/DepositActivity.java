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
import com.toshi.databinding.ActivityDepositBinding;
import com.toshi.presenter.DepositPresenter;
import com.toshi.presenter.LoaderIds;
import com.toshi.presenter.factory.DepositPresenterFactory;
import com.toshi.presenter.factory.PresenterFactory;

public class DepositActivity extends BasePresenterActivity<DepositPresenter, DepositActivity> {

    private ActivityDepositBinding binding;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deposit);
    }

    public final ActivityDepositBinding getBinding() {
        return this.binding;
    }

    @NonNull
    @Override
    protected PresenterFactory<DepositPresenter> getPresenterFactory() {
        return new DepositPresenterFactory();
    }

    @Override
    protected void onPresenterPrepared(@NonNull DepositPresenter presenter) {}

    @Override
    protected int loaderId() {
        return LoaderIds.get(this.getClass().getCanonicalName());
    }
}

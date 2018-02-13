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
import com.toshi.databinding.ActivityBackupPhraseVerifyBinding;
import com.toshi.presenter.BackupPhraseVerifyPresenter;
import com.toshi.presenter.LoaderIds;
import com.toshi.presenter.factory.BackupPhraseVerifyPresenterFactory;
import com.toshi.presenter.factory.PresenterFactory;

public class BackupPhraseVerifyActivity
        extends BasePresenterActivity<BackupPhraseVerifyPresenter, BackupPhraseVerifyActivity> {

    private ActivityBackupPhraseVerifyBinding binding;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_backup_phrase_verify);
    }

    public ActivityBackupPhraseVerifyBinding getBinding() {
        return this.binding;
    }

    @NonNull
    @Override
    protected PresenterFactory<BackupPhraseVerifyPresenter> getPresenterFactory() {
        return new BackupPhraseVerifyPresenterFactory();
    }

    @Override
    protected void onPresenterPrepared(@NonNull final BackupPhraseVerifyPresenter presenter) {}

    @Override
    protected int loaderId() {
        return LoaderIds.get(this.getClass().getCanonicalName());
    }
}

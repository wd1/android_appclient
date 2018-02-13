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

package com.toshi.presenter;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.toshi.R;
import com.toshi.crypto.HDWallet;
import com.toshi.util.LogUtil;
import com.toshi.view.BaseApplication;
import com.toshi.view.activity.BackupPhraseActivity;
import com.toshi.view.activity.BackupPhraseVerifyActivity;
import com.toshi.view.adapter.BackupPhraseAdapter;
import com.toshi.view.custom.SpaceDecoration;

import java.util.ArrayList;
import java.util.Arrays;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class BackupPhrasePresenter implements Presenter<BackupPhraseActivity> {

    private BackupPhraseActivity activity;
    private CompositeSubscription subscriptions;
    private boolean firstTimeAttaching = true;
    private String backupPhrase;
    private Dialog dialog;

    @Override
    public void onViewAttached(BackupPhraseActivity view) {
        this.activity = view;

        if (this.firstTimeAttaching) {
            this.firstTimeAttaching = false;
            initLongLivingObjects();
        }

        initRecyclerView();
        addBackupPhrase();
        initClickListeners();
        initScreenshotListener();
    }

    private void initLongLivingObjects() {
        this.subscriptions = new CompositeSubscription();
    }

    private void initRecyclerView() {
        final RecyclerView backupPhraseList = this.activity.getBinding().backupPhraseList;

        if (backupPhraseList.getAdapter() != null) {
            return;
        }

        final ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(this.activity)
                .build();
        final int controlSpacing = BaseApplication.get().getResources().getDimensionPixelSize(R.dimen. backup_phrase_spacing);
        backupPhraseList.addItemDecoration(new SpaceDecoration(controlSpacing));
        backupPhraseList.setLayoutManager(chipsLayoutManager);
        final BackupPhraseAdapter adapter = new BackupPhraseAdapter(new ArrayList<>());
        backupPhraseList.setAdapter(adapter);
    }

    private void addBackupPhrase() {
        final Subscription sub =
                BaseApplication.get()
                .getToshiManager()
                .getWallet()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::handleBackupPhraseCallback,
                        this::handleBackupPhraseError
                );

        this.subscriptions.add(sub);
    }

    private void handleBackupPhraseCallback(final HDWallet wallet) {
        this.backupPhrase = wallet.getMasterSeed();
        final String[] backupPhraseList = this.backupPhrase.split(" ");
        final BackupPhraseAdapter adapter = (BackupPhraseAdapter) activity.getBinding().backupPhraseList.getAdapter();
        adapter.setBackupPhraseItems(Arrays.asList(backupPhraseList));
    }

    private void handleBackupPhraseError(final Throwable throwable) {
        LogUtil.exception(getClass(), "Error while getting wallet", throwable);
    }

    private void initClickListeners() {
        this.activity.getBinding().closeButton.setOnClickListener(this::handleCloseButtonClicked);
        this.activity.getBinding().verifyPhraseBtn.setOnClickListener(this::handleVerifyPhraseButtonClicked);
        this.activity.getBinding().copyToClipboard.setOnClickListener(this::handleCopyToClipboardClicked);
    }

    private void handleCloseButtonClicked(final View v) {
        this.activity.finish();
    }

    private void handleVerifyPhraseButtonClicked(final View v) {
        final Intent intent = new Intent(this.activity, BackupPhraseVerifyActivity.class);
        this.activity.startActivity(intent);
    }

    private void handleCopyToClipboardClicked(final View v) {
        final ClipboardManager clipboard = (ClipboardManager) this.activity.getSystemService(Context.CLIPBOARD_SERVICE);
        final ClipData clip = ClipData.newPlainText(this.activity.getString(R.string.backup_phrase), this.backupPhrase);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this.activity, this.activity.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
    }

    private void initScreenshotListener() {
        this.activity.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                this.contentObserver);
    }

    private final ContentObserver contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange) {
            showWarningDialog();
        }
    };

    private void showWarningDialog() {
        if (this.activity == null) return;

        this.dialog = new AlertDialog.Builder(this.activity)
                .setTitle(R.string.screenshot_warning_title)
                .setMessage(R.string.screenshot_warning_message)
                .setPositiveButton(R.string.got_it, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onViewDetached() {
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
        this.subscriptions.clear();
        this.activity.getContentResolver().unregisterContentObserver(this.contentObserver);
        this.activity = null;
    }

    @Override
    public void onDestroyed() {
        this.subscriptions = null;
    }
}

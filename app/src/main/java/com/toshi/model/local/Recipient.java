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

package com.toshi.model.local;

import android.widget.ImageView;

import com.toshi.util.ImageUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Recipient extends RealmObject {

    @PrimaryKey
    private String id;
    private User user;
    private Group group;

    public Recipient() {}

    public Recipient(final User user) {
        this.id = user.getToshiId();
        this.user = user;
    }

    public Recipient(final Group group) {
        this.id = group.getId();
        this.group = group;
    }

    public boolean isGroup() {
        return this.group != null;
    }

    public boolean isUser() {
        return this.group == null;
    }

    public User getUser() {
        return this.user;
    }

    public Group getGroup() {
        return this.group;
    }

    // Helper functions
    public String getThreadId() {
        return this.id;
    }

    public String getDisplayName() {
        return isGroup() ? this.group.getTitle() : this.user.getDisplayName();
    }

    public void loadAvatarInto(final ImageView imageView) {
        if (isGroup()) {
            ImageUtil.load(getGroupAvatar(), imageView);
        } else {
            ImageUtil.load(getUserAvatar(), imageView);
        }
    }

    public boolean hasAvatar() {
        return isGroup() ? this.group.hasAvatar() : this.user.getAvatar() != null;
    }

    public Avatar getGroupAvatar() {
        return this.group.getAvatar();
    }

    public String getUserAvatar() {
        return this.user.getAvatar();
    }

    public boolean isRecipientInvalid() {
        return this.user == null && group == null;
    }

    public void cascadeDelete() {
        // Don't remove this user because it might be used somewhere else
        if (this.group != null) this.group.cascadeDelete();
        deleteFromRealm();
    }
}

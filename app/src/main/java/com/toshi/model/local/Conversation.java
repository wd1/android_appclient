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


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.toshi.model.sofa.SofaMessage;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Conversation extends RealmObject implements ConversationItem {

    @PrimaryKey
    private String threadId;
    private Recipient recipient;
    private SofaMessage latestMessage;
    private long updatedTime;
    private RealmList<SofaMessage> allMessages;
    private int numberOfUnread;
    private ConversationStatus conversationStatus;

    public Conversation() {}

    public Conversation(final Recipient recipient) {
        this.recipient = recipient;
        this.threadId = recipient.getThreadId();
        this.conversationStatus = new ConversationStatus(this.threadId);
    }

    public Conversation updateRecipient(final Recipient recipient) {
        this.recipient = recipient;
        return this;
    }

    public String getThreadId() {
        return threadId;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    @Nullable
    public SofaMessage getLatestMessage() {
        return latestMessage;
    }

    public Conversation setLatestMessageAndUpdateUnreadCounter(final SofaMessage latestMessage) {
        if (isDuplicateMessage(latestMessage)) {
            return this;
        }
        this.numberOfUnread++;
        return addLatestMessage(latestMessage);
    }

    public Conversation setLatestMessage(final SofaMessage latestMessage) {
        if (isDuplicateMessage(latestMessage)) {
            return this;
        }
        return addLatestMessage(latestMessage);
    }

    private Conversation addLatestMessage(final SofaMessage latestMessage) {
        this.latestMessage = latestMessage;
        this.updatedTime = latestMessage.getCreationTime();
        addMessage(latestMessage);
        return this;
    }

    public Conversation updateLatestMessage(final SofaMessage sofaMessage) {
        this.latestMessage = sofaMessage;
        this.updatedTime = latestMessage.getCreationTime();
        return this;
    }

    private boolean isDuplicateMessage(final SofaMessage message) {
        return this.allMessages != null && this.allMessages.contains(message);
    }

    public void addMessage(final SofaMessage latestMessage) {
        if (this.allMessages == null) {
            this.allMessages = new RealmList<>();
        }

        this.allMessages.add(latestMessage);
    }

    public List<SofaMessage> getAllMessages() {
        return allMessages;
    }

    public int getNumberOfUnread() {
        return numberOfUnread;
    }

    public void resetUnreadCounter() {
        this.numberOfUnread = 0;
    }

    // Helper functions
    public final boolean isGroup() {
        return this.recipient.isGroup();
    }

    @NonNull
    public Recipient getRecipient() {
        return this.recipient;
    }

    public boolean isRecipientInvalid() {
        return this.recipient == null || this.recipient.isRecipientInvalid();
    }

    public ConversationStatus getConversationStatus() {
        return conversationStatus;
    }

    @Override
    public int hashCode() {
        return threadId.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Conversation))return false;
        final Conversation otherConversationMessage = (Conversation) other;
        return otherConversationMessage.getThreadId().equals(this.threadId);
    }

    public void cascadeDelete() {
        if (this.recipient != null) this.recipient.cascadeDelete();
        if (this.latestMessage != null) this.latestMessage.cascadeDelete();
        if (this.allMessages != null) this.allMessages.deleteAllFromRealm();
        if (this.conversationStatus != null) this.conversationStatus.deleteFromRealm();
        deleteFromRealm();
    }
}

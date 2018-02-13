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

package com.toshi.view.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.toshi.R;
import com.toshi.model.local.Conversation;
import com.toshi.model.local.Recipient;
import com.toshi.model.sofa.SofaMessage;
import com.toshi.util.LocaleUtil;
import com.toshi.view.adapter.listeners.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ThreadViewHolder extends RecyclerView.ViewHolder {

    private ImageView avatar;
    private TextView name;
    private TextView latestMessage;
    private TextView time;
    private TextView unreadCounter;

    public ThreadViewHolder(final View view) {
        super(view);
        this.name = view.findViewById(R.id.name);
        this.avatar = view.findViewById(R.id.avatar);
        this.latestMessage = view.findViewById(R.id.latest_message);
        this.time = view.findViewById(R.id.time);
        this.unreadCounter = view.findViewById(R.id.unread_counter);
    }

    public void setThread(final Conversation conversation) {
        final Recipient recipient = conversation.getRecipient();
        this.name.setText(recipient.getDisplayName());
        this.unreadCounter.setText(getNumberOfUnread(conversation));
        final String creationTime = getLastMessageCreationTime(conversation);
        this.time.setText(creationTime);

        final int visibility = conversation.getNumberOfUnread() > 0 ? VISIBLE : GONE;
        this.unreadCounter.setVisibility(visibility);

        recipient.loadAvatarInto(this.avatar);
    }

    private String getNumberOfUnread(final Conversation conversation) {
        final int numberOfUnread = conversation.getNumberOfUnread();
        return (numberOfUnread > 99) ? ":)" : String.valueOf(numberOfUnread);
    }

    public void setLatestMessage(final String latestMessage) {
        this.latestMessage.setText(latestMessage);
    }

    private String getLastMessageCreationTime(final Conversation conversation) {
        final SofaMessage latestMessage = conversation.getLatestMessage();
        if (latestMessage == null) return "";
        final long creationTime = latestMessage.getCreationTime();
        final Calendar lastMessageCreationTime = Calendar.getInstance();
        lastMessageCreationTime.setTimeInMillis(creationTime);
        final Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DAY_OF_YEAR) == lastMessageCreationTime.get(Calendar.DAY_OF_YEAR)) {
            return new SimpleDateFormat("h:mm a", LocaleUtil.getLocale()).format(new Date(creationTime));
        } else if (now.get(Calendar.WEEK_OF_YEAR) == lastMessageCreationTime.get(Calendar.WEEK_OF_YEAR)){
            return new SimpleDateFormat("EEE", LocaleUtil.getLocale()).format(new Date(creationTime));
        } else {
            return new SimpleDateFormat("d MMM", LocaleUtil.getLocale()).format(new Date(creationTime));
        }
    }

    public void setOnItemClickListener(final Conversation conversation, final OnItemClickListener<Conversation> listener) {
        this.itemView.setOnClickListener(__ -> listener.onItemClick(conversation));
    }

    public void setOnItemLongClickListener(final Conversation conversation, final OnItemClickListener<Conversation> listener) {
        this.itemView.setOnLongClickListener(__ -> {
            listener.onItemClick(conversation);
            return true;
        });
    }
}
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

package com.toshi.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.toshi.R
import com.toshi.model.local.User
import com.toshi.view.adapter.viewholder.GroupParticipantViewHolder
import java.util.ArrayList

class GroupParticipantAdapter(
        private val attachContextMenu: Boolean = false
) : RecyclerView.Adapter<GroupParticipantViewHolder>() {

    val users: MutableList<User> by lazy { ArrayList<User>() }

    fun addUsers(users: List<User>): GroupParticipantAdapter {
        this.users.addAll(users)
        notifyDataSetChanged()
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupParticipantViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item__group_participant, parent, false)
        return GroupParticipantViewHolder(v, attachContextMenu)
    }

    override fun onBindViewHolder(holder: GroupParticipantViewHolder, position: Int) {
        val user = this.users[position]
        holder.setUser(user)
    }

    override fun getItemCount() = this.users.size
}

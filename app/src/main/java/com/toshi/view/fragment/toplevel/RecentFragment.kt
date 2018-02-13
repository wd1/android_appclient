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

package com.toshi.view.fragment.toplevel

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toshi.R
import com.toshi.extensions.isVisible
import com.toshi.extensions.startActivity
import com.toshi.model.local.Conversation
import com.toshi.model.local.ConversationInfo
import com.toshi.view.activity.ChatActivity
import com.toshi.view.activity.ConversationRequestActivity
import com.toshi.view.activity.ConversationSetupActivity
import com.toshi.view.adapter.RecentAdapter
import com.toshi.view.adapter.listeners.OnItemClickListener
import com.toshi.view.adapter.listeners.OnUpdateListener
import com.toshi.view.adapter.viewholder.ThreadViewHolder
import com.toshi.view.fragment.DialogFragment.ConversationOptionsDialogFragment
import com.toshi.viewModel.RecentViewModel
import kotlinx.android.synthetic.main.fragment_recent.*

class RecentFragment : Fragment(), TopLevelFragment {

    companion object {
        private const val TAG = "RecentFragment"
        private const val NO_MESSAGE_REQUESTS_START_POSITION = 0
        private const val MESSAGE_REQUESTS_START_POSITION = 2
        private const val SCROLL_POSITION = "ScrollPosition"
    }

    override fun getFragmentTag() = TAG

    private lateinit var viewModel: RecentViewModel
    private lateinit var recentAdapter: RecentAdapter
    private var scrollPosition = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_recent, container, false)
    }

    override fun onViewCreated(view: View?, inState: Bundle?) = init(inState)

    private fun init(inState: Bundle?) {
        initViewModel()
        initClickListeners()
        restoreScrollPosition(inState)
        initRecentAdapter()
        initObservers()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(activity).get(RecentViewModel::class.java)
    }

    private fun initClickListeners() {
        startChat.setOnClickListener { startActivity<ConversationSetupActivity>() }
        add.setOnClickListener { startActivity<ConversationSetupActivity>() }
    }

    private fun restoreScrollPosition(inState: Bundle?) {
        inState?.let {
            scrollPosition = it.getInt(SCROLL_POSITION, 0)
        }
    }

    private fun initRecentAdapter() {
        recentAdapter = RecentAdapter()
                .apply {
                    onItemClickListener = OnItemClickListener {
                        startActivity<ChatActivity> { putExtra(ChatActivity.EXTRA__THREAD_ID, it.threadId) }
                    }
                    onItemLongClickListener = OnItemClickListener {
                        viewModel.showConversationOptionsDialog(it.threadId)
                    }
                    onRequestsClickListener = OnUpdateListener {
                        startActivity<ConversationRequestActivity>()
                    }
                }

        recents.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = recentAdapter
        }

        addSwipeToDeleteListener(recents)
        recents.scrollToPosition(scrollPosition)
    }

    private fun initObservers() {
        viewModel.acceptedAndUnacceptedConversations.observe(this, Observer {
            conversations -> conversations?.let { handleConversations(it.first, it.second) }
        })
        viewModel.updatedConversation.observe(this, Observer {
            conversation -> conversation?.let { handleAcceptedConversation(it) }
        })
        viewModel.updatedUnacceptedConversation.observe(this, Observer {
            conversation -> conversation?.let { handleUnacceptedConversation(it) }
        })
        viewModel.conversationInfo.observe(this, Observer {
            conversationInfo -> conversationInfo?.let { showConversationOptionsDialog(it) }
        })
        viewModel.deleteConversation.observe(this, Observer {
            deletedConversation -> deletedConversation?.let { removeItemAtWithUndo(it) }
        })
    }

    private fun handleConversations(acceptedConversations: List<Conversation>, unacceptedConversation: List<Conversation>) {
        recentAdapter.setUnacceptedConversations(unacceptedConversation)
        recentAdapter.setConversations(acceptedConversations)
        updateViewState()
    }

    private fun handleAcceptedConversation(updatedConversation: Conversation) {
        recentAdapter.updateAcceptedConversation(updatedConversation)
        updateViewState()
    }

    private fun handleUnacceptedConversation(updatedConversation: Conversation) {
        recentAdapter.updateUnacceptedConversation(updatedConversation)
        updateViewState()
    }

    private fun showConversationOptionsDialog(conversationInfo: ConversationInfo) {
        ConversationOptionsDialogFragment.newInstance(conversationInfo)
                .setItemClickListener { viewModel.handleSelectedOption(conversationInfo.conversation, it) }
                .show(fragmentManager, ConversationOptionsDialogFragment.TAG)
    }

    private fun removeItemAtWithUndo(conversation: Conversation) {
        recentAdapter.removeItem(conversation, recents)
        updateViewState()
    }

    private fun addSwipeToDeleteListener(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun getSwipeDirs(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
                if (viewHolder !is ThreadViewHolder) return 0
                return super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onMove(recyclerView1: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                recentAdapter.removeItemAtWithUndo(viewHolder.adapterPosition, recyclerView)
                updateEmptyState()
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun updateViewState() {
        updateEmptyState()
        updateDividers()
    }

    private fun updateEmptyState() {
        val isAdapterEmpty = recentAdapter.itemCount == 0
        val isAcceptedConversationsEmpty = recentAdapter.isAcceptedConversationsEmpty

        val params = recents.layoutParams
        params.height = if (isAcceptedConversationsEmpty) ViewGroup.LayoutParams.WRAP_CONTENT else ViewGroup.LayoutParams.MATCH_PARENT
        recents.layoutParams = params

        recents.isVisible(!isAdapterEmpty)
        emptyState.isVisible(isAcceptedConversationsEmpty || isAdapterEmpty)
    }

    private fun updateDividers() {
        val dividerStartPosition =
                if (recentAdapter.isUnacceptedConversationsEmpty) NO_MESSAGE_REQUESTS_START_POSITION
                else MESSAGE_REQUESTS_START_POSITION
        recents.setDividerStartPosition(dividerStartPosition)
    }

    override fun onStart() {
        super.onStart()
        getAcceptedAndUnacceptedConversations()
    }

    private fun getAcceptedAndUnacceptedConversations() = viewModel.getAcceptedAndUnAcceptedConversations()

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(setOutState(outState))
    }

    private fun setOutState(outState: Bundle?): Bundle? {
        val recentsLayoutManager = recents.layoutManager as LinearLayoutManager
        return outState?.apply {
            putInt(SCROLL_POSITION, recentsLayoutManager.findFirstCompletelyVisibleItemPosition())
        }
    }

    override fun onStop() {
        super.onStop()
        recentAdapter.doDelete()
    }
}
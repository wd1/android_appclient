package com.toshi.viewModel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.toshi.extensions.isLocalStatusMessage
import com.toshi.util.LogUtil
import com.toshi.view.BaseApplication
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class MainViewModel : ViewModel() {

    private val subscriptions by lazy { CompositeSubscription() }
    val unreadMessages by lazy { MutableLiveData<Boolean>() }

    init {
        attachUnreadMessagesSubscription()
    }

    private fun attachUnreadMessagesSubscription() {
        val allChangesSubscription = getSofaMessageManager()
                .registerForAllConversationChanges()
                .filter { !it.latestMessage.isLocalStatusMessage() }
                .flatMap { getSofaMessageManager().areUnreadMessages().toObservable() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { unreadMessages.value = it },
                        { LogUtil.exception(javaClass, "Error while fetching unread messages $it") }
                )

        val firstTimeSubscription = getSofaMessageManager()
                .areUnreadMessages()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { unreadMessages.value = it },
                        { LogUtil.exception(javaClass, "Error while fetching unread messages $it") }
                )

        this.subscriptions.addAll(allChangesSubscription, firstTimeSubscription)
    }

    private fun getSofaMessageManager() = BaseApplication.get().sofaMessageManager

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }
}
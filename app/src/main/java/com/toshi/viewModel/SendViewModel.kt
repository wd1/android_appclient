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

package com.toshi.viewModel

import android.arch.lifecycle.ViewModel
import com.toshi.crypto.util.TypeConverter
import com.toshi.crypto.util.hasValidChecksum
import com.toshi.crypto.util.usesChecksum
import com.toshi.manager.model.ExternalPaymentTask
import com.toshi.manager.model.PaymentTask
import com.toshi.manager.model.ToshiPaymentTask
import com.toshi.util.EthUtil
import com.toshi.util.LogUtil
import com.toshi.util.SingleLiveEvent
import com.toshi.view.BaseApplication
import rx.subscriptions.CompositeSubscription

class SendViewModel : ViewModel() {

    private val subscriptions by lazy { CompositeSubscription() }
    private val balanceManager by lazy { BaseApplication.get().balanceManager }
    private val transactionManager by lazy { BaseApplication.get().transactionManager }

    val localAmount by lazy { SingleLiveEvent<String>() }

    fun generateAmount(amountAsEncodedEth: String) {
        val weiAmount = TypeConverter.StringHexToBigInteger(amountAsEncodedEth)
        val ethAmount = EthUtil.weiToEth(weiAmount)

        val sub = balanceManager
                .convertEthToLocalCurrencyString(ethAmount)
                .subscribe(
                        { localAmount.value = it },
                        { LogUtil.exception(javaClass, it) }
                )

        subscriptions.add(sub)
    }

    fun sendPayment(paymentTask: PaymentTask) {
        when (paymentTask) {
            is ExternalPaymentTask -> transactionManager.sendExternalPayment(paymentTask)
            is ToshiPaymentTask -> transactionManager.sendPayment(paymentTask) // The address could be associated with a Toshi user
            else -> LogUtil.e(javaClass, "Invalid payment task in this context")
        }
    }

    fun isPaymentAddressValid(paymentAddress: String?): Boolean {
        val regex = Regex("^0x[a-fA-F0-9]{40}\$")
        return paymentAddress?.let {
            regex.matches(paymentAddress) && !hasInvalidChecksum(paymentAddress)
        } ?: false
    }

    private fun hasInvalidChecksum(paymentAddress: String): Boolean {
        return usesChecksum(paymentAddress) && !hasValidChecksum(paymentAddress)
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }
}
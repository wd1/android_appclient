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

package com.toshi.manager.model

import com.toshi.model.local.EthAndFiat
import com.toshi.model.network.UnsignedTransaction
import com.toshi.model.sofa.Payment

open class PaymentTask(
        open val paymentAmount: EthAndFiat,
        open val gasPrice: EthAndFiat,
        open val totalAmount: EthAndFiat,
        open val payment: Payment,
        open val unsignedTransaction: UnsignedTransaction
)
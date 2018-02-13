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

package com.toshi.model.network;


import android.support.annotation.Nullable;

import com.squareup.moshi.Json;
import com.toshi.crypto.util.TypeConverter;
import com.toshi.R;
import com.toshi.util.EthUtil;
import com.toshi.view.BaseApplication;

import java.math.BigDecimal;
import java.math.BigInteger;

import rx.Single;
import rx.schedulers.Schedulers;

public class Balance {

    @Json(name = "confirmed_balance")
    private String confirmedBalanceAsHex;
    @Json(name = "unconfirmed_balance")
    private String unconfirmedBalanceAsHex;

    private String localBalance;

    public Balance() {}

    public Balance(final String unconfirmedBalanceAsHex) {
        this.unconfirmedBalanceAsHex = unconfirmedBalanceAsHex;
    }

    public BigInteger getConfirmedBalance() {
        return TypeConverter.StringHexToBigInteger(confirmedBalanceAsHex);
    }

    public String getUnconfirmedBalanceAsHex() {
        return unconfirmedBalanceAsHex;
    }

    public BigInteger getUnconfirmedBalance() {
        return TypeConverter.StringHexToBigInteger(unconfirmedBalanceAsHex);
    }

    @Nullable
    public String getLocalBalance() {
        return this.localBalance;
    }

    public Single<String> getFormattedLocalBalance() {
        if (this.localBalance != null) {
            return Single.just(this.localBalance);
        }

        return BaseApplication
                .get()
                .getBalanceManager()
                .convertEthToLocalCurrencyString(EthUtil.weiToEth(getUnconfirmedBalance()))
                .subscribeOn(Schedulers.io());
    }

    public Single<Balance> getBalanceWithLocalBalance() {
        if (this.localBalance != null) {
            return Single.just(this);
        }

        return BaseApplication
                .get()
                .getBalanceManager()
                .convertEthToLocalCurrencyString(EthUtil.weiToEth(getUnconfirmedBalance()))
                .subscribeOn(Schedulers.io())
                .doOnSuccess(localBalance -> this.localBalance = localBalance)
                .map(__ -> this);
    }

    public String getFormattedUnconfirmedBalance() {
        final BigDecimal unconfirmedEthBalance = EthUtil.weiToEth(getUnconfirmedBalance());
        return BaseApplication.get().getString(R.string.eth_balance, EthUtil.ethAmountToUserVisibleString(unconfirmedEthBalance));
    }
}

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

package com.toshi.model.sofa;


import android.support.annotation.IntDef;

import com.squareup.moshi.Json;
import com.toshi.R;
import com.toshi.crypto.util.TypeConverter;
import com.toshi.model.local.SendState;
import com.toshi.util.EthUtil;
import com.toshi.util.LocaleUtil;
import com.toshi.view.BaseApplication;

import java.math.BigDecimal;
import java.math.BigInteger;

import rx.Single;

/**
 * PaymentRequest
 * <p>
 * Request an Ethereum transaction
 *
 */
public class PaymentRequest {

    @IntDef({
            PENDING,
            ACCEPTED,
            REJECTED,
    })
    public @interface State {}
    public static final int PENDING = 0;
    public static final int REJECTED = 1;
    public static final int ACCEPTED = 2;

    private String value;
    private String destinationAddress;
    private String body;

    @Json(name = SofaType.LOCAL_ONLY_PAYLOAD)
    private ClientSideCustomData androidClientSideCustomData;

    public PaymentRequest setValue(final String value) {
        this.value = value;
        return this;
    }

    public Single<PaymentRequest> generateLocalPrice() {
        final BigInteger weiAmount = TypeConverter.StringHexToBigInteger(this.value);
        final BigDecimal ethAmount = EthUtil.weiToEth(weiAmount);
        return BaseApplication
                .get()
                .getBalanceManager()
                .convertEthToLocalCurrencyString(ethAmount)
                .map(this::setLocalPrice);
    }

    public PaymentRequest setDestinationAddress(final String destinationAddress) {
        this.destinationAddress = destinationAddress;
        return this;
    }

    public String getDestinationAddresss() {
        return this.destinationAddress;
    }

    public String getValue() {
        return this.value;
    }

    public String getBody() {
        return this.body;
    }

    private PaymentRequest setLocalPrice(final String localPrice) {
        if (this.androidClientSideCustomData == null) {
            this.androidClientSideCustomData = new ClientSideCustomData();
        }

        this.androidClientSideCustomData.localPrice = localPrice;
        return this;
    }

    public String getLocalPrice() {
        if (this.androidClientSideCustomData == null) return null;
        return this.androidClientSideCustomData.localPrice;
    }

    public PaymentRequest setState(final @State int state) {
        if (this.androidClientSideCustomData == null) {
            this.androidClientSideCustomData = new ClientSideCustomData();
        }

        this.androidClientSideCustomData.state = state;
        return this;
    }

    public @State int getState() {
        if (this.androidClientSideCustomData == null) return PENDING;
        return this.androidClientSideCustomData.state;
    }

    public String toUserVisibleString(final boolean sentByLocal, final @SendState.State int sentStatus) {
        final int messageId = generateMessageId(sentByLocal, sentStatus);
        return String.format(
                LocaleUtil.getLocale(),
                BaseApplication.get().getResources().getString(messageId),
                getLocalPrice());
    }

    private int generateMessageId(final boolean sentByLocal, final @SendState.State int sentStatus) {
        if (sentStatus == SendState.STATE_FAILED) {
            return R.string.latest_message__request_failed;
        } else if (sentByLocal) {
            return R.string.latest_message__payment_request_outgoing;
        } else if (getState() == REJECTED) {
            return R.string.latest_message__payment_request_denied;
        } else if (getState() == ACCEPTED) {
            return R.string.latest_message__payment_request_accepted;
        } else {
            return R.string.latest_message__payment_request;
        }
    }

    private static class ClientSideCustomData {
        private String localPrice;
        private @State int state;
    }
}
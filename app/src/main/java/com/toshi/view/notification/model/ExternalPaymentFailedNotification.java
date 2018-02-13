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

package com.toshi.view.notification.model;

import com.toshi.R;
import com.toshi.view.BaseApplication;

public class ExternalPaymentFailedNotification extends ToshiNotification {
    private final String paymentAddress;

    public ExternalPaymentFailedNotification(final String paymentAddress) {
        super(paymentAddress);
        this.paymentAddress = paymentAddress;
        setDefaultLargeIcon();
    }

    @Override
    public String getTag() {
        return this.paymentAddress;
    }

    @Override
    public String getTitle() {
        return BaseApplication.get().getString(R.string.payment_failed);
    }

    @Override
    String getUnacceptedText() {
        return BaseApplication.get().getString(R.string.external_payment_failure);
    }
}

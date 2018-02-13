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

package com.toshi.util;

import android.support.annotation.IntDef;

public class QrCodeType {
    @IntDef({ADD, PAY, EXTERNAL_PAY, INVALID, PAYMENT_ADDRESS})
    public @interface Type {}
    public static final int ADD = 1;
    public static final int PAY = 2;
    public static final int EXTERNAL_PAY = 3;
    public static final int INVALID = 4;
    public static final int PAYMENT_ADDRESS = 5;
}

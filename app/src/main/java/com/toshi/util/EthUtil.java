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


import com.toshi.crypto.util.TypeConverter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class EthUtil {

    public static final int BIG_DECIMAL_SCALE = 16;
    private static final int NUM_DECIMAL_PLACES = 5;
    private static final String USER_VISIBLE_STRING_FORMATTING = "%.5f";
    private static final BigDecimal weiToEthRatio = new BigDecimal("1000000000000000000");

    public static String hexAmountToUserVisibleString(final String hexEncodedWei) {
        final BigInteger wei = TypeConverter.StringHexToBigInteger(hexEncodedWei);
        return weiAmountToUserVisibleString(wei);
    }

    public static String weiAmountToUserVisibleString(final BigInteger wei) {
        final BigDecimal eth = weiToEth(wei);
        return ethAmountToUserVisibleString(eth);
    }

    public static String ethAmountToUserVisibleString(final BigDecimal eth) {
        return String.format(
                LocaleUtil.getLocale(),
                USER_VISIBLE_STRING_FORMATTING,
                eth.setScale(NUM_DECIMAL_PLACES, BigDecimal.ROUND_DOWN));
    }

    public static BigDecimal weiToEth(final BigInteger wei) {
        if (wei == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(wei)
                .divide(weiToEthRatio)
                .setScale(BIG_DECIMAL_SCALE, BigDecimal.ROUND_DOWN);
    }

    public static BigInteger ethToWei(final BigDecimal amountInEth) {
        return amountInEth.multiply(weiToEthRatio).toBigInteger();
    }

    public static String encodeToHex(final String value) throws NumberFormatException, NullPointerException {
        return String.format("%s%s", "0x", new BigInteger(value).toString(16));
    }

    public static boolean isLargeEnoughForSending(final BigDecimal eth) {
        return eth.setScale(NUM_DECIMAL_PLACES, BigDecimal.ROUND_DOWN).compareTo(BigDecimal.ZERO) == 1;
    }
}

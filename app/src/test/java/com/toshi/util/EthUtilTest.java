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

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EthUtilTest {

    @Test
    public void weiToEthStringCalledWithNullReturnsZero() {
        final String expected = "0.00000";
        assertThat(EthUtil.weiAmountToUserVisibleString(null), is(expected));
    }

    @Test
    public void weiToEthStringCalledWithZeroWeiReturnsCorrectly() {
        final BigInteger zeroWei = BigInteger.ZERO;
        final String expected = "0.00000";
        assertThat(EthUtil.weiAmountToUserVisibleString(zeroWei), is(expected));
    }

    @Test
    public void weiToEthStringCalledWithLittleWeiTruncatesCorrectly() {
        // Output is limit to 10dp
        final BigInteger oneWei = BigInteger.ONE;
        final String expected = "0.00000";
        assertThat(EthUtil.weiAmountToUserVisibleString(oneWei), is(expected));
    }

    @Test
    public void weiToEthStringCalledWithOneEthReturnsCorrectly() {
        final BigInteger oneWei = new BigInteger("1000000000000000000");
        final String expected = "1.00000";
        assertThat(EthUtil.weiAmountToUserVisibleString(oneWei), is(expected));
    }

    @Test
    public void weiToEthStringRoundsDown() {
        final BigInteger oneWei = new BigInteger("1555555555555555555");
        final String expected = "1.55555";
        assertThat(EthUtil.weiAmountToUserVisibleString(oneWei), is(expected));
    }

    @Test(expected = NullPointerException.class)
    public void ethToWeiThrowsExceptionIfCalledWithNull() {
        EthUtil.ethToWei(null);
    }

    @Test
    public void ethToWeiCalledWithZeroEthReturnsCorrectly() {
        final BigDecimal eth = BigDecimal.ZERO;
        final BigInteger expected = BigInteger.ZERO;
        assertThat(EthUtil.ethToWei(eth), is(expected));
    }

    @Test
    public void ethToWeiCalledWithOneEthReturnsCorrectly() {
        final BigDecimal eth = BigDecimal.ONE;
        final BigInteger expected = new BigInteger("1000000000000000000");
        assertThat(EthUtil.ethToWei(eth), is(expected));
    }

    @Test
    public void ethToWeiCalledWithDecimalPointReturnsCorrectly() {
        final BigDecimal eth = new BigDecimal(0.0000000001);
        final BigInteger expected = new BigInteger("100000000");
        assertThat(EthUtil.ethToWei(eth), is(expected));
    }

    @Test
    public void endToEndCurrencyTest() {
        final BigDecimal ethAmount = new BigDecimal("0.1234567890123456");
        final BigInteger weiAmount = EthUtil.ethToWei(ethAmount);
        final String encodedEthAmount = TypeConverter.toJsonHex(weiAmount);
        final BigInteger weiAmountDecoded = TypeConverter.StringHexToBigInteger(encodedEthAmount);
        final BigDecimal ethAmountDecoded = EthUtil.weiToEth(weiAmountDecoded);
        assertThat(ethAmountDecoded, is(ethAmount));
    }

    @Test
    public void isLargeEnoughForSendingFalseIfValueIsZero() {
        final BigDecimal ethAmount = BigDecimal.ZERO;
        assertThat(EthUtil.isLargeEnoughForSending(ethAmount), is(false));
    }

    @Test
    public void isLargeEnoughForSendingFalseIfValueIsSmallerThan5DP() {
        final BigDecimal ethAmount = new BigDecimal("0.000001");
        assertThat(EthUtil.isLargeEnoughForSending(ethAmount), is(false));
    }

    @Test
    public void isLargeEnoughForSendingTrueIfValueIs5DP() {
        final BigDecimal ethAmount = new BigDecimal("0.00001");
        assertThat(EthUtil.isLargeEnoughForSending(ethAmount), is(true));
    }

    @Test
    public void isLargeEnoughForSendingFalseIfValueIsNegative() {
        final BigDecimal ethAmount = new BigDecimal("1").negate();
        assertThat(EthUtil.isLargeEnoughForSending(ethAmount), is(false));
    }
}
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


import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.toshi.view.BaseApplication;

public class KeyboardUtil {

    public static void hideKeyboard(final View view) {
        ((InputMethodManager) BaseApplication
                .get()
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(final View view) {
        ((InputMethodManager) BaseApplication
                .get()
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
}

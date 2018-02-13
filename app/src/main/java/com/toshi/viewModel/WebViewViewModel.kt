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
import com.toshi.R
import com.toshi.util.SingleLiveEvent
import com.toshi.view.BaseApplication
import java.net.URI

class WebViewViewModel(val url: String) : ViewModel() {

    val toolbarUpdate by lazy { SingleLiveEvent<Unit>() }

    fun tryGetAddress(): String {
        return try {
            getAddress()
        } catch (e: IllegalArgumentException) {
            BaseApplication.get().getString(R.string.unknown_address)
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun getAddress(): String {
        val uri = URI.create(url)
        return if (uri.scheme == null) "http://" + uri.toASCIIString()
        else uri.toASCIIString()
    }

    fun updateToolbar() = toolbarUpdate.postValue(Unit)
}
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

package com.toshi.model.local;


import com.toshi.model.network.App;
import com.toshi.view.adapter.ToshiEntityAdapter;

public class DappLink extends App {

    private final String address;

    public DappLink(final String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }

    @Override
    public @ToshiEntityAdapter.ViewType int getViewType() {
        return ToshiEntityAdapter.DAPP_LINK;
    }
}

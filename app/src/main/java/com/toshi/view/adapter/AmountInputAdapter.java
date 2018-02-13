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

package com.toshi.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toshi.R;
import com.toshi.util.LocaleUtil;
import com.toshi.view.adapter.viewholder.AmountInputViewHolder;
import com.toshi.view.adapter.viewholder.AmountInputViewHolderBackspace;

import java.text.DecimalFormatSymbols;

public class AmountInputAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int REGULAR_TYPE = 1;
    private static final int BACKSPACE_TYPE = 2;
    private static final char BACKSPACE = '<';

    private final char ZERO;
    private final char[] valueArray;
    private OnKeyboardItemClicked listener;

    public AmountInputAdapter() {
        final DecimalFormatSymbols dcf = LocaleUtil.getDecimalFormatSymbols();
        final char decimalSeparator = dcf.getMonetaryDecimalSeparator();
        ZERO = dcf.getZeroDigit();
        this.valueArray = new char[] {'1', '2', '3', '4', '5', '6', '7', '8', '9', decimalSeparator, ZERO, BACKSPACE};
    }

    public void setOnKeyboardItemClickListener(final OnKeyboardItemClicked listener) {
        this.listener = listener;
    }

    public interface OnKeyboardItemClicked {
        void onValueClicked(final char value);
        void onBackSpaceClicked();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case BACKSPACE_TYPE: {
                final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_amount_input_backspace, parent, false);
                return new AmountInputViewHolderBackspace(v);
            }
            case REGULAR_TYPE:
            default: {
                final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_amount_input, parent, false);
                return new AmountInputViewHolder(v);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final char value = valueArray[position];

        switch (holder.getItemViewType()) {
            case REGULAR_TYPE : {
                final AmountInputViewHolder vh = (AmountInputViewHolder) holder;
                vh.setText(value);
                vh.bind(listener);

                if (isOnLastRow(position)) {
                    vh.hideDivider();
                }

                break;
            }
            case BACKSPACE_TYPE : {
                final AmountInputViewHolderBackspace vh = (AmountInputViewHolderBackspace) holder;
                vh.bind(listener);
            }
        }
    }

    private boolean isOnLastRow(final int position) {
        return position >= getItemCount() - 3;
    }


    @Override
    public int getItemViewType(int position) {
        return this.valueArray[position] == BACKSPACE
                ? BACKSPACE_TYPE
                : REGULAR_TYPE;
    }

    @Override
    public int getItemCount() {
        return valueArray.length;
    }
}

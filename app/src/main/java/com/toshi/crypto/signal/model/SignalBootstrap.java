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

package com.toshi.crypto.signal.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.signalservice.api.push.SignedPreKeyEntity;
import org.whispersystems.signalservice.internal.push.PreKeyEntity;
import org.whispersystems.signalservice.internal.util.JsonUtil;

import java.util.List;

@JsonPropertyOrder(alphabetic=true)
public class SignalBootstrap {
    @JsonProperty
    @JsonSerialize(using = JsonUtil.IdentityKeySerializer.class)
    @JsonDeserialize(using = JsonUtil.IdentityKeyDeserializer.class)
    private final IdentityKey identityKey;

    @JsonProperty
    private final List<PreKeyEntity> preKeys;

    @JsonProperty
    private final PreKeyEntity lastResortKey;

    @JsonProperty
    private final String password;

    @JsonProperty
    private final int registrationId;

    @JsonProperty
    private final String signalingKey;

    @JsonProperty
    private final SignedPreKeyEntity signedPreKey;


    public SignalBootstrap(
            final List<PreKeyEntity> preKeys,
            final PreKeyEntity lastResortKey,
            final String password,
            final int registrationId,
            final String signalingKey,
            final SignedPreKeyEntity signedPreKey,
            final IdentityKey identityKey) {
        this.preKeys = preKeys;
        this.lastResortKey = lastResortKey;
        this.password = password;
        this.registrationId = registrationId;
        this.signalingKey = signalingKey;
        this.signedPreKey = signedPreKey;
        this.identityKey = identityKey;
    }

}

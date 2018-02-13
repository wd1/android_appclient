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

package com.toshi.crypto.signal.store;


import com.toshi.crypto.signal.SignalPreferences;
import com.toshi.crypto.signal.util.PreKeyUtil;
import com.toshi.crypto.util.HashUtil;
import com.toshi.view.BaseApplication;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.IdentityKeyStore;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.PreKeyStore;
import org.whispersystems.libsignal.state.SessionRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyStore;
import org.whispersystems.libsignal.util.KeyHelper;

import java.io.IOException;
import java.util.List;

public class ProtocolStore implements SignalProtocolStore {

    private static final int PREKEY_START = 1234;
    private static final int PREKEY_COUNT = 100;
    private static final int SIGNED_PREKEY_ID = 1;

    private final PreKeyStore preKeyStore;
    private final SignedPreKeyStore signedPreKeyStore;
    private final IdentityKeyStore identityKeyStore;
    private final SignalSessionStore sessionStore;
    private List<PreKeyRecord> preKeyRecords;

    public ProtocolStore() {
        this.preKeyStore  = new SignalPreKeyStore();
        this.signedPreKeyStore = new SignalPreKeyStore();
        this.identityKeyStore = new SignalIdentityKeyStore();
        this.sessionStore = new SignalSessionStore();
    }

    public ProtocolStore init() {
        return this;
    }

    public SignedPreKeyRecord getSignedPreKey() throws InvalidKeyIdException, InvalidKeyException {
        final int signedPreKeyId = SignalPreferences.getSignedPreKeyId();
        if (signedPreKeyId == -1) {
            return generateSignedPreKey();
        }

        final SignedPreKeyRecord signedPreKey = loadSignedPreKey(signedPreKeyId);
        if (signedPreKey == null) {
            return generateSignedPreKey();
        }
        return signedPreKey;
    }

    private SignedPreKeyRecord generateSignedPreKey() throws InvalidKeyException {
        final SignedPreKeyRecord pk = PreKeyUtil.generateSignedPreKey(BaseApplication.get(), getIdentityKeyPair(), true);
        SignalPreferences.setSignedPreKeyId(pk.getId());
        return pk;
    }

    @Override
    public IdentityKeyPair getIdentityKeyPair() {
        final IdentityKeyPair ikp = identityKeyStore.getIdentityKeyPair();
        if (ikp == null) {
            return generateIdentityKeyPair();
        }
        return ikp;
    }

    private IdentityKeyPair generateIdentityKeyPair() {
        final IdentityKeyPair ikp = KeyHelper.generateIdentityKeyPair();
        SignalPreferences.setSerializedIdentityKeyPair(ikp.serialize());
        return ikp;
    }

    @Override
    public int getLocalRegistrationId() {
        final int rid = identityKeyStore.getLocalRegistrationId();
        if (rid == -1) {
            return generateLocalRegistrationId();
        }
        return rid;
    }

    private int generateLocalRegistrationId() {
        final int rid = KeyHelper.generateRegistrationId(false);
        SignalPreferences.setLocalRegistrationId(rid);
        return rid;
    }

    public String getSignalingKey() {
        final String signallingKey = SignalPreferences.getSignalingKey();
        if (signallingKey == null) {
            return generateSignalingKey();
        }
        return signallingKey;
    }

    private String generateSignalingKey() {
        final String signallingKey = HashUtil.getSecret(52);
        SignalPreferences.setSignalingKey(signallingKey);
        return signallingKey;
    }

    @Override
    public boolean saveIdentity(SignalProtocolAddress address, IdentityKey identityKey) {
        return identityKeyStore.saveIdentity(address, identityKey);
    }

    @Override
    public boolean isTrustedIdentity(final SignalProtocolAddress address, final IdentityKey identityKey, final Direction direction) {
        return identityKeyStore.isTrustedIdentity(address, identityKey, direction);
    }

    public List<PreKeyRecord> getPreKeys() throws InvalidKeyIdException {
        if (!preKeysHaveBeenGenerated()) generatePreKeys();
        return this.preKeyRecords;
    }

    private boolean preKeysHaveBeenGenerated() {
        return this.preKeyStore.containsPreKey(PREKEY_START) && this.preKeyStore.containsPreKey(PREKEY_START + PREKEY_COUNT - 1);
    }

    private void generatePreKeys() {
        this.preKeyRecords = PreKeyUtil.generatePreKeys(BaseApplication.get());
        for (final PreKeyRecord preKeyRecord : preKeyRecords) {
            storePreKey(preKeyRecord.getId(), preKeyRecord);
        }
    }

    public PreKeyRecord getLastResortKey() throws IOException {
        final byte[] serializedLastResortKey = SignalPreferences.getSerializedLastResortKey();
        if (serializedLastResortKey == null) {
            return generateLastResortKey();
        }
        return new PreKeyRecord(serializedLastResortKey);
    }

    private PreKeyRecord generateLastResortKey() {
        final PreKeyRecord lrk = PreKeyUtil.generateLastResortKey(BaseApplication.get());
        SignalPreferences.setSerializedLastResortKey(lrk.serialize());
        return lrk;
    }

    public String getPassword() {
        final String password = SignalPreferences.getPassword();
        if (password == null) {
            return generatePassword();
        }
        return password;
    }

    private String generatePassword() {
        final String password = HashUtil.getSecret(18);
        SignalPreferences.setPassword(password);
        return password;
    }

    @Override
    public PreKeyRecord loadPreKey(int preKeyId) throws InvalidKeyIdException {
        return preKeyStore.loadPreKey(preKeyId);
    }

    @Override
    public void storePreKey(int preKeyId, PreKeyRecord record) {
        preKeyStore.storePreKey(preKeyId, record);
    }

    @Override
    public boolean containsPreKey(int preKeyId) {
        return preKeyStore.containsPreKey(preKeyId);
    }

    @Override
    public void removePreKey(int preKeyId) {
        preKeyStore.removePreKey(preKeyId);
    }

    @Override
    public SessionRecord loadSession(SignalProtocolAddress axolotlAddress) {
        return sessionStore.loadSession(axolotlAddress);
    }

    @Override
    public List<Integer> getSubDeviceSessions(String number) {
        return sessionStore.getSubDeviceSessions(number);
    }

    @Override
    public void storeSession(SignalProtocolAddress axolotlAddress, SessionRecord record) {
        sessionStore.storeSession(axolotlAddress, record);
    }

    @Override
    public boolean containsSession(SignalProtocolAddress axolotlAddress) {
        return sessionStore.containsSession(axolotlAddress);
    }

    @Override
    public void deleteSession(SignalProtocolAddress axolotlAddress) {
        sessionStore.deleteSession(axolotlAddress);
    }

    @Override
    public void deleteAllSessions(String number) {
        sessionStore.deleteAllSessions(number);
    }

    public void deleteAllSessions() {
        sessionStore.deleteAllSessions();
    }

    @Override
    public SignedPreKeyRecord loadSignedPreKey(int signedPreKeyId) throws InvalidKeyIdException {
        return signedPreKeyStore.loadSignedPreKey(signedPreKeyId);
    }

    @Override
    public List<SignedPreKeyRecord> loadSignedPreKeys() {
        return signedPreKeyStore.loadSignedPreKeys();
    }

    @Override
    public void storeSignedPreKey(int signedPreKeyId, SignedPreKeyRecord record) {
        signedPreKeyStore.storeSignedPreKey(signedPreKeyId, record);
    }

    @Override
    public boolean containsSignedPreKey(int signedPreKeyId) {
        return signedPreKeyStore.containsSignedPreKey(signedPreKeyId);
    }

    @Override
    public void removeSignedPreKey(int signedPreKeyId) {
        signedPreKeyStore.removeSignedPreKey(signedPreKeyId);
    }
}

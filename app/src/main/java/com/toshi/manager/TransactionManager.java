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

package com.toshi.manager;

import com.toshi.crypto.HDWallet;
import com.toshi.manager.model.ExternalPaymentTask;
import com.toshi.manager.model.PaymentTask;
import com.toshi.manager.model.ResendToshiPaymentTask;
import com.toshi.manager.model.ToshiPaymentTask;
import com.toshi.manager.model.W3PaymentTask;
import com.toshi.manager.store.PendingTransactionStore;
import com.toshi.manager.transaction.IncomingTransactionManager;
import com.toshi.manager.transaction.OutgoingTransactionManager;
import com.toshi.manager.transaction.TransactionSigner;
import com.toshi.manager.transaction.UpdateTransactionManager;
import com.toshi.model.local.PendingTransaction;
import com.toshi.model.local.UnsignedW3Transaction;
import com.toshi.model.local.User;
import com.toshi.model.network.SentTransaction;
import com.toshi.model.network.SignedTransaction;
import com.toshi.model.sofa.Payment;
import com.toshi.model.sofa.PaymentRequest;
import com.toshi.model.sofa.SofaMessage;
import com.toshi.util.paymentTask.PaymentTaskBuilder;

import rx.Single;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class TransactionManager {

    private HDWallet wallet;
    private PendingTransactionStore pendingTransactionStore;
    private PaymentTaskBuilder paymentTaskBuilder;
    private IncomingTransactionManager incomingTransactionManager;
    private OutgoingTransactionManager outgoingTransactionManager;
    private UpdateTransactionManager updateTransactionManager;
    private TransactionSigner transactionSigner;
    private CompositeSubscription subscriptions;

    /*package */ TransactionManager() {
        initDatabase();
        initSubscriptions();
        initPaymentTaskBuilder();
        initIncomingTransactionManager();
        initTransactionSigner();
        initOutgoingTransactionManager();
        initUpdateTransactionManager();
    }

    private void initDatabase() {
        this.pendingTransactionStore = new PendingTransactionStore();
    }

    private void initSubscriptions() {
        this.subscriptions = new CompositeSubscription();
    }

    private void initPaymentTaskBuilder() {
        this.paymentTaskBuilder = new PaymentTaskBuilder();
    }

    private void initIncomingTransactionManager() {
        this.incomingTransactionManager = new IncomingTransactionManager(this.pendingTransactionStore);
    }

    private void initTransactionSigner() {
        this.transactionSigner = new TransactionSigner();
    }

    private void initOutgoingTransactionManager() {
        this.outgoingTransactionManager = new OutgoingTransactionManager(this.pendingTransactionStore, this.transactionSigner);
    }

    private void initUpdateTransactionManager() {
        this.updateTransactionManager = new UpdateTransactionManager(this.pendingTransactionStore);
    }

    public TransactionManager init(final HDWallet wallet) {
        this.wallet = wallet;
        transactionSigner.setWallet(wallet);
        new Thread(this::initEverything).start();
        return this;
    }

    private void initEverything() {
        updatePendingTransactions();
        attachSubscribers();
    }

    private void updatePendingTransactions() {
        this.updateTransactionManager.updatePendingTransactions();
    }

    public Single<SignedTransaction> signW3Transaction(final W3PaymentTask paymentTask) {
        return this.transactionSigner.signW3Transaction(paymentTask);
    }

    public Single<SentTransaction> sendSignedTransaction(final SignedTransaction signedTransaction) {
        return this.transactionSigner.sendSignedTransaction(signedTransaction);
    }

    private void attachSubscribers() {
        attachNewOutgoingPaymentSubscriber();
        attachNewIncomingPaymentSubscriber();
        attachUpdatePaymentSubscriber();
    }

    private void attachNewIncomingPaymentSubscriber() {
        this.incomingTransactionManager.attachNewIncomingPaymentSubscriber();
    }

    private void attachNewOutgoingPaymentSubscriber() {
        this.outgoingTransactionManager.attachNewOutgoingPaymentSubscriber();
    }

    private void attachUpdatePaymentSubscriber() {
        this.updateTransactionManager.attachUpdatePaymentSubscriber();
    }

    public void sendPayment(final ToshiPaymentTask paymentTask) {
        this.outgoingTransactionManager.addOutgoingToshiPaymentTask(paymentTask);
    }

    public void resendPayment(final ResendToshiPaymentTask paymentTask) {
        this.outgoingTransactionManager.addOutgoingResendPaymentTask(paymentTask);
    }

    public void sendExternalPayment(final ExternalPaymentTask paymentTask) {
        this.outgoingTransactionManager.addOutgoingExternalPaymentTask(paymentTask);
    }

    public final void updatePayment(final Payment payment) {
        this.updateTransactionManager.updatePayment(payment);
    }

    public final void updatePaymentRequestState(final User remoteUser,
                                                final SofaMessage sofaMessage,
                                                final @PaymentRequest.State int newState) {
        this.updateTransactionManager.updatePaymentRequestState(remoteUser, sofaMessage, newState);
    }

    public void addIncomingPayment(final Payment payment) {
        this.incomingTransactionManager.addIncomingPayment(payment);
    }

    public PublishSubject<PendingTransaction> getPendingTransactionObservable() {
        return this.pendingTransactionStore.getPendingTransactionObservable();
    }

    public Single<PaymentTask> buildPaymentTask(final String fromPaymentAddress,
                                                final String toPaymentAddress,
                                                final String ethAmount) {
        return this.paymentTaskBuilder
                .buildToshiPaymentTask(fromPaymentAddress, toPaymentAddress, ethAmount);
    }

    public Single<W3PaymentTask> buildPaymentTask(final String callbackId, final UnsignedW3Transaction unsignedW3Transaction) {
        return this.paymentTaskBuilder
                .buildW3PaymentTask(callbackId, unsignedW3Transaction);
    }

    public void clear() {
        this.subscriptions.clear();
        this.incomingTransactionManager.clearSubscriptions();
        this.outgoingTransactionManager.clearSubscriptions();
        this.updateTransactionManager.clearSubscription();
    }
}
package com.iou.contract;

import com.google.common.util.concurrent.ListenableFuture;
import com.iou.flow.IOUFlow;
import com.iou.state.IOUState;
import net.corda.core.crypto.CryptoUtilities;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IOUFlowTests {
    private MockNetwork net;
    private MockNetwork.MockNode a;
    private MockNetwork.MockNode b;

    @Before
    public void setup() {
        net = new MockNetwork();
        MockNetwork.BasketOfNodes nodes = net.createSomeNodes(2);
        a = nodes.getPartyNodes().get(0);
        b = nodes.getPartyNodes().get(1);
        net.runNetwork();
    }

    @After
    public void tearDown() {
        net.stopNodes();
    }

    @Test
    public void flowReturnsTransactionSignedByTheInitiator() throws Exception {
        IOUState state = new IOUState(
                1,
                a.info.getLegalIdentity(),
                b.info.getLegalIdentity(),
                new IOUContract());
        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
        net.runNetwork();

        SignedTransaction signedTx = future.get();
        signedTx.verifySignatures(CryptoUtilities.getComposite(b.getServices().getLegalIdentityKey().getPublic()));
    }

//    @Test
//    public void flowRejectsInvalidIOUs() throws InterruptedException {
//        IOUState state = new IOUState(
//                -1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork();
//
//        // The IOUContract specifies that IOUs cannot have negative values.
//        try {
//            future.get();
//            fail();
//        } catch (ExecutionException e) {
//            assertTrue(e.getCause() instanceof TransactionVerificationException.ContractRejection);
//        }
//    }

//    @Test
//    public void flowRejectsInvalidIOUStates() throws InterruptedException {
//        IOUState state = new IOUState(
//                -1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork();
//
//        // The IOUContract specifies that an IOU's value cannot be negative.
//        try {
//            future.get();
//            fail();
//        } catch (ExecutionException e) {
//            assertTrue(e.getCause() instanceof TransactionVerificationException.ContractRejection);
//        }
//    }

//    @Test
//    public void signedTransactionReturnedByTheFlowIsSignedByTheAcceptor() throws Exception {
//        IOUState state = new IOUState(
//                1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork();
//
//        SignedTransaction signedTx = future.get();
//        signedTx.verifySignatures(CryptoUtilities.getComposite(a.getServices().getLegalIdentityKey().getPublic()));
//    }

//    @Test
//    public void flowRecordsATransactionInBothPartiesVaults() throws Exception {
//        IOUState state = new IOUState(
//                1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(state, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork();
//        SignedTransaction signedTx = future.get();
//
//        databaseTransaction(a.database, it -> {
//            SignedTransaction recordedTx = a.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//            assertEquals(signedTx.getId(), recordedTx.getId());
//            return null;
//        });
//
//        databaseTransaction(b.database, it -> {
//            SignedTransaction recordedTx = b.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//            assertEquals(signedTx.getId(), recordedTx.getId());
//            return null;
//        });
//    }

//    @Test
//    public void recordedTransactionHasNoInputsAndASingleOutputTheInputIOU() throws Exception {
//        IOUState inputState = new IOUState(
//                1,
//                a.info.getLegalIdentity(),
//                b.info.getLegalIdentity(),
//                new IOUContract());
//        IOUFlow.Initiator flow = new IOUFlow.Initiator(inputState, b.info.getLegalIdentity());
//        ListenableFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();
//        net.runNetwork();
//        SignedTransaction signedTx = future.get();
//
//        databaseTransaction(a.database, it -> {
//            SignedTransaction recordedTx = a.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
//            assert(txOutputs.size() == 1);
//
//            IOUState recordedState = (IOUState) txOutputs.get(0).getData();
//            assertEquals(recordedState.getIOUValue(), inputState.getIOUValue());
//            assertEquals(recordedState.getSender(), inputState.getSender());
//            assertEquals(recordedState.getRecipient(), inputState.getRecipient());
//            assertEquals(recordedState.getLinearId(), inputState.getLinearId());
//            return null;
//        });
//
//        databaseTransaction(b.database, it -> {
//            SignedTransaction recordedTx = b.storage.getValidatedTransactions().getTransaction(signedTx.getId());
//            List<TransactionState<ContractState>> txOutputs = recordedTx.getTx().getOutputs();
//            assert(txOutputs.size() == 1);
//
//            IOUState recordedState = (IOUState) txOutputs.get(0).getData();
//            assertEquals(recordedState.getIOUValue(), inputState.getIOUValue());
//            assertEquals(recordedState.getSender(), inputState.getSender());
//            assertEquals(recordedState.getRecipient(), inputState.getRecipient());
//            assertEquals(recordedState.getLinearId(), inputState.getLinearId());
//            return null;
//        });
//    }
}
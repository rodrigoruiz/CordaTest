
package bootcamp

import co.paralleluniverse.fibers.Suspendable
import com.google.common.collect.ImmutableList
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker


@InitiatingFlow
@StartableByRPC
class TokenIssueFlowInitiator(private val owner: Party, private val amount: Int) : FlowLogic<SignedTransaction>() {
    
    override val progressTracker: ProgressTracker? = ProgressTracker()
    
    @Suspendable
    @Throws(FlowException::class)
    override fun call(): SignedTransaction {
        // We choose our transaction's notary (the notary prevents double-spends).
        val notary = serviceHub.networkMapCache.notaryIdentities[0]
        // We get a reference to our own identity.
        val issuer = ourIdentity
        
        /* ============================================================================
         *         TODO 1 - Create our TokenState to represent on-ledger tokens!
         * ===========================================================================*/
        // We create our new TokenState.
        val tokenState = TokenState(
            issuer = issuer,
            owner = owner,
            amount = amount
        )
        
        /* ============================================================================
         *      TODO 3 - Build our token issuance transaction to update the ledger!
         * ===========================================================================*/
        // We build our transaction.
        val transactionBuilder = TransactionBuilder(notary = notary)
        
        transactionBuilder
            .addOutputState(state = tokenState, contract = TokenContract.ID)
            .addCommand(data = TokenContract.Commands.Issue(), keys = ImmutableList.of(issuer.owningKey))
        
        /* ============================================================================
         *          TODO 2 - Write our TokenContract to control token issuance!
         * ===========================================================================*/
        // We check our transaction is valid based on its contracts.
        transactionBuilder.verify(serviceHub)
        
        val session = initiateFlow(owner)
        
        // We sign the transaction with our private key, making it immutable.
        val signedTransaction = serviceHub.signInitialTransaction(transactionBuilder)
        
        // The counterparty signs the transaction
        val fullySignedTransaction = subFlow(CollectSignaturesFlow(signedTransaction, listOf(session)))
        
        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(FinalityFlow(fullySignedTransaction, listOf(session)))
    }
    
}

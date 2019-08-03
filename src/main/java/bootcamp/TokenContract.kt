
package bootcamp

import examples.ArtContract
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction
import java.lang.IllegalArgumentException


/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
class TokenContract : Contract {
    
    companion object {
        const val ID = "bootcamp.TokenContract"
    }
    
    @Throws(IllegalArgumentException::class)
    override fun verify(tx: LedgerTransaction) {
        if (tx.commands.size != 1) {
            throw IllegalArgumentException("Transaction must have one command.")
        }
        
        val command = tx.getCommand<CommandData>(0)
        val commandType = command.value
        
        when (commandType) {
            is TokenContract.Commands.Issue -> verifyIssue(tx)
            else -> throw IllegalArgumentException("Command type not recognised.")
        }
    }
    
    @Throws(IllegalArgumentException::class)
    private fun verifyIssue(tx: LedgerTransaction) {
        // "Shape" constraints.
        if (tx.inputStates.isNotEmpty()) {
            throw IllegalArgumentException("Issue transaction must have zero inputs.")
        }
        
        if (tx.outputStates.size != 1) {
            throw IllegalArgumentException("Issue transaction must have one output.")
        }
        
        // Content constraints.
        val tokenState = tx.getOutput(0) as? TokenState ?: throw IllegalArgumentException("Ouput must be a TokenState.")
        
        if (tokenState.amount <= 0) {
            throw IllegalArgumentException("Amount must be bigger than 0.")
        }
        
        // Required signer constraints.
        val requiredSigners = tx.getCommand<CommandData>(0).signers
        
        if (!requiredSigners.contains(tokenState.issuer.owningKey)) {
            throw IllegalArgumentException("Issuer must be required signer.")
        }
    }
    
    interface Commands : CommandData {
        class Issue : Commands
    }
    
}

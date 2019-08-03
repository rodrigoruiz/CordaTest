
package bootcamp

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction
import java.lang.IllegalArgumentException


class HouseContract : Contract {
    
    // throws IllegalArgumentException
    override fun verify(tx: LedgerTransaction) {
        if (tx.commands.size != 1) {
            throw IllegalArgumentException("Transaction must have one command.")
        }
        
        val command = tx.getCommand<CommandData>(0)
        val commandType = command.value
        
        when (commandType) {
            is Register -> verifyRegister(tx)
            is Transfer -> verifyTransfer(tx)
            else -> throw IllegalArgumentException("Command type not recognised.")
        }
    }
    
    private fun verifyRegister(tx: LedgerTransaction) {
        // "Shape" constraints.
        if (tx.inputStates.isNotEmpty()) {
            throw IllegalArgumentException("Registration transaction must have no inputs.")
        }
        
        if (tx.outputStates.size != 1) {
            throw IllegalArgumentException("Registration transaction must have one output.")
        }
        
        // Content constraints.
        val houseState = tx.getOutput(0) as? HouseState ?: throw IllegalArgumentException("Ouput must be a HouseState.")
        
        if (houseState.address.length <= 3) {
            throw IllegalArgumentException("Address must be longer than 3 characters.")
        }
        
        if (houseState.owner.name.country.equals("Brazil")) {
            throw IllegalArgumentException("Not allowed to register for Brazilian owners.")
        }
        
        // Required signer constraints.
        val requiredSigners = tx.getCommand<CommandData>(0).signers
        
        val owner = houseState.owner
        val ownersKey = owner.owningKey
        if (!requiredSigners.contains(ownersKey)) {
            throw IllegalArgumentException("Owner of house must sign registration.")
        }
    }
    
    private fun verifyTransfer(tx: LedgerTransaction) {
        // "Shape" constraints.
        if (tx.inputStates.size != 1) {
            throw IllegalArgumentException("Must have one input.")
        }
        
        if (tx.outputStates.size != 1) {
            throw IllegalArgumentException("Must gave one output.")
        }
        
        // Content constraints.
        val inputHouse = tx.getInput(0) as? HouseState ?: throw IllegalArgumentException("Input must be a HouseState")
        val outputHouse = tx.getOutput(0) as? HouseState ?: throw IllegalArgumentException("Output must be a HouseState")
        
        if (!inputHouse.address.equals(outputHouse.address)) {
            throw IllegalArgumentException("In a transfer, the address can't change.")
        }
        
        if (inputHouse.owner.equals(outputHouse.owner)) {
            throw IllegalArgumentException("In a transfer, the owner must change.")
        }
        
        // Required signer constraints.
        val requiredSigners = tx.getCommand<CommandData>(0).signers
        val inputOwner = inputHouse.owner
        val outputOwner = outputHouse.owner
        
        if (requiredSigners.contains(inputOwner.owningKey)) {
            throw IllegalArgumentException("Current owner must sign transfer.")
        }
        
        if (requiredSigners.contains(outputOwner.owningKey)) {
            throw IllegalArgumentException("New owner must sign transfer.")
        }
    }
    
    class Register : CommandData
    class Transfer : CommandData
    
}

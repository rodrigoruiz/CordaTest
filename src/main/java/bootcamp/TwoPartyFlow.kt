
package bootcamp

import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.utilities.unwrap


@InitiatingFlow
@StartableByRPC
class TwoPartyFlow(
    private val counterparty: Party,
    private val number: Int
) : FlowLogic<Int>() {
    
    override fun call(): Int {
        val session = initiateFlow(counterparty)
        session.send(number)
        
        val receivedIncrementedInteger = session.receive(Int::class.java).unwrap { it }
        
        return receivedIncrementedInteger
    }
    
}

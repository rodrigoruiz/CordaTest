
package bootcamp

import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.unwrap
import java.lang.IllegalArgumentException


@InitiatedBy(TwoPartyFlow::class)
class TwoPartyFlowResponder(
    private val counterpartySession: FlowSession
) : FlowLogic<Unit>() {
    
    override fun call() {
        val statesFromVault = serviceHub.vaultService.queryBy(HouseState::class.java).states
        
        val alicesName = CordaX500Name("Alice", "Manchester", "UK")
        val alice = serviceHub.networkMapCache.getNodeByLegalName(alicesName)
        
        val platformVersion = serviceHub.myInfo.platformVersion
        
        
        val receivedInt = counterpartySession.receive(Int::class.java).unwrap(fun(it: Int): Int {
            if (it > 3) {
                throw IllegalArgumentException("Number too high.")
            }
            
            return it
        })
        
        counterpartySession.send(receivedInt + 1)
    }
    
}

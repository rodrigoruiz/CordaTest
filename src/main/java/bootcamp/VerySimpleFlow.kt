
package bootcamp

//import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC


@InitiatingFlow
@StartableByRPC
class VerySimpleFlow : FlowLogic<Int>() {
    
//    @Suspendable
    override fun call(): Int {
        val a = returnOne()
        val b = 2
        
        return a + b
    }
    
    fun returnOne(): Int {
        return 1
    }
    
}

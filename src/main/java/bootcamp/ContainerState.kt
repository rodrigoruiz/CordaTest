
package bootcamp

import com.google.common.collect.ImmutableList
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party


class ContainerState(
    val width: Int,
    val height: Int,
    val depth: Int,
    val contents: String,
    val owner: Party,
    val carrier: Party
) : ContractState {
    
    override val participants: List<AbstractParty>
        get() = ImmutableList.of(owner, carrier)
    
    fun main(args: Array<String>) {
        val jetpackImporters: Party? = null
        val jetpackCarriers: Party? = null
        
        var container = ContainerState(
            2,
            4,
            2,
            "Jetpacks",
            owner = jetpackImporters!!,
            carrier = jetpackCarriers!!
        )
    }
    
}

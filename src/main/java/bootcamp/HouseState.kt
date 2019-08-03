
package bootcamp

import com.google.common.collect.ImmutableList
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party


class HouseState(
    val address: String,
    val owner: Party
) : ContractState {
    
    override val participants: List<AbstractParty>
        get() = ImmutableList.of(owner)
    
    fun main(args: Array<String>) {
        val joel: Party? = null
        val state = HouseState("1 Low Moor Avenue, Berkshire", joel!!)
    }
    
}

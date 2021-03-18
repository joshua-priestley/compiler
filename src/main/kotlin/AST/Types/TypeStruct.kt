package compiler.AST.Types

import AST.Ident
import AST.Types.Type
import antlr.WACCParser.*

open class TypeStruct(private val name: String) : Type() {

    override val typeInt: Int = STRUCT
    override var isParam = false
    override var offsetInTable: Int = 0

    private val memberST = AST.SymbolTable(null, -1)
    private val memberNames = mutableListOf<Ident>()
    private var totalSize = 0

    override fun getTypeSize(): Int {
        return totalSize
    }

    override fun getBaseType(): Type {
        return this
    }

    override fun toString(): String {
        return getName()
        //return VOCABULARY.getSymbolicName(getType())
    }

    fun getName(): String {
        return name
    }

    fun containsMember(name: Ident): Boolean {
        return memberST.containsNodeGlobal(name.toString())
    }

    fun memberType(name: Ident): Type? {
        return memberST.getNodeGlobal(name.toString())
    }

    fun numberOfMembers(): Int {
        return memberNames.size
    }

    fun addMember(name: Ident, type: Type) {
        memberST.addNode(name.toString(), type)
        totalSize += type.getTypeSize()
    }

    override fun hashCode(): Int {
        return getType()
    }

    private fun membersEqual(other: TypeStruct): Boolean {
        if (numberOfMembers() != other.numberOfMembers()) return false
        for (memb in this.memberNames) {
            if (!(other.containsMember(memb) && (other.memberType(memb) == memberType(memb)))) {
                return false
            }
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TypeStruct) return false
        return other.getName() == getName()  && membersEqual(other)
    }

    fun equalsType(other: Any?): Boolean {
        if (other !is TypeStruct) return false
        return other.getName() == getName()
    }
}
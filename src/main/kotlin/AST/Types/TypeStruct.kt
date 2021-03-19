package compiler.AST.Types

import AST.Ident
import AST.SymbolTable
import AST.Types.Type
import antlr.WACCParser.*

open class TypeStruct(private val name: Ident) : Type() {

    override val typeInt: Int = AST.Types.STRUCT
    override var isParam = false
    override var offsetInTable: Int = 0

    private val memberST = SymbolTable(null, -1)
    private val memberNames = mutableListOf<Ident>()
    private var totalSize = 0

    fun getMemberNames() : MutableCollection<Ident>{
        return this.memberNames
    }

    fun getMemberST() : SymbolTable {
        return this.memberST
    }

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
        return name.name
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
        memberNames.add(name)
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
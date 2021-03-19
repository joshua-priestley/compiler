package compiler.AST.Types

import AST.Ident
import AST.SymbolTable
import AST.Types.Type
import antlr.WACCParser.*
import java.util.concurrent.atomic.AtomicInteger

open class TypeClass(private val name: Ident) : Type() {

    override val typeInt: Int = AST.Types.STRUCT
    override var offsetInTable: Int = 0

    private lateinit var classST: SymbolTable

    private val memberNames = mutableListOf<Ident>()
    private val functionNames = mutableListOf<Ident>()
    private var totalSize = 0

    fun setST(st: SymbolTable) {
        classST = st
    }

    fun getST(): SymbolTable {
        return classST
    }

    override fun getTypeSize(): Int {
        return totalSize
    }

    override fun getBaseType(): Type {
        return this
    }

    override fun toString(): String {
        return getName()
    }

    fun getName(): String {
        return name.name
    }

    fun containsMember(name: Ident): Boolean {
        return classST.containsNodeGlobal(name.toString())
    }

    fun containsFunction(name: Ident): Boolean {
        return classST.containsNodeGlobal(name.toString())
    }

    fun memberType(name: Ident): Type? {
        return classST.getNodeGlobal(name.toString())
    }

    fun functionType(name: Ident): Type? {
        return classST.getNodeGlobal(name.toString())
    }

    fun numberOfMembers(): Int {
        return memberNames.size
    }

    fun numberOfFunctions(): Int {
        return functionNames.size
    }

    fun getFunctions(): MutableList<Ident> {
        return functionNames
    }

    fun addMember(name: Ident, type: Type) {
        memberNames.add(name)
        totalSize += type.getTypeSize()
    }

    override fun hashCode(): Int {
        return getType()
    }

    private fun membersEqual(other: TypeClass): Boolean {
        if (numberOfMembers() != other.numberOfMembers()) return false
        for (memb in this.memberNames) {
            if (!(other.containsMember(memb) && (other.memberType(memb) == memberType(memb)))) {
                return false
            }
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TypeClass) return false
        return other.getName() == getName()  && membersEqual(other)
    }

    fun equalsType(other: Any?): Boolean {
        if (other !is TypeClass) return false
        return other.getName() == getName()
    }
}
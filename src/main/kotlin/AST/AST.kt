package AST

import antlr.WACCParser.*
import kotlin.Int

interface Node

/*
 * Programs
 */
data class ProgramNode(val funcs: List<FunctionNode>, val stat: StatementNode) : Node

/*
 * Functions
 */
data class FunctionNode(val type: TypeNode, val ident: Ident, val params: List<Param>, val stat: StatementNode) : Node

/*
 * Statements
 */
interface StatementNode : Node {
    fun valid(): Boolean {
        return when (this) {
            is IfElseNode -> !(!this.then.valid() || !this.else_.valid())
            is SequenceNode -> this.statList[this.statList.size - 1].valid()
            is ExitNode -> true
            is ReturnNode -> true
            else -> false
        }
    }
}

class SkipNode : StatementNode
data class DeclarationNode(val type: TypeNode, val ident: Ident, val value: AssignRHSNode) : StatementNode
data class AssignNode(val lhs: AssignLHSNode, val rhs: AssignRHSNode) : StatementNode
data class ReadNode(val lhs: AssignLHSNode) : StatementNode
data class FreeNode(val expr: ExprNode) : StatementNode
data class ReturnNode(val expr: ExprNode) : StatementNode
data class ExitNode(val expr: ExprNode) : StatementNode
data class PrintNode(val expr: ExprNode) : StatementNode
data class PrintlnNode(val expr: ExprNode) : StatementNode
data class IfElseNode(val expr: ExprNode, val then: StatementNode, val else_: StatementNode) : StatementNode
data class WhileNode(val expr: ExprNode, val do_: StatementNode) : StatementNode
data class BeginEndNode(val stat: StatementNode) : StatementNode
data class SequenceNode(val statList: List<StatementNode>) : StatementNode

/*
 * LHS Assignment
 */
interface AssignLHSNode : Node
data class AssignLHSIdentNode(val ident: Ident) : AssignLHSNode
data class LHSArrayElemNode(val arrayElem: ArrayElem) : AssignLHSNode
data class LHSPairElemNode(val pairElem: PairElemNode) : AssignLHSNode

/*
 * Expressions
 */
interface ExprNode : Node
interface LiterNode : ExprNode
data class IntLiterNode(val value: String) : LiterNode
data class StrLiterNode(val value: String) : LiterNode
data class CharLiterNode(val value: String) : LiterNode
data class BoolLiterNode(val value: String) : LiterNode
class PairLiterNode : ExprNode
data class Ident(val name: String) : LiterNode
data class ArrayElem(val ident: Ident, val expr: List<ExprNode>) : ExprNode
data class UnaryOpNode(val operator: UnOp, val expr: ExprNode) : ExprNode
data class BinaryOpNode(val operator: BinOp, val expr1: ExprNode, val expr2: ExprNode) : ExprNode

// Expression nodes for functions
data class Param(val type: TypeNode, val ident: Ident) : ExprNode


/*
 * Operators
 */
enum class UnOp(val value: Int) {
    NOT(14), MINUS(2), LEN(15), ORD(16), CHR(17), NOT_SUPPORTED(-1)
}

//TODO does making this a node mean its no longer an AST?
//think about how to do binary operator precedence more
enum class BinOp(val value: Int) : Node {
    MUL(3), DIV(4), MOD(5), PLUS(1), MINUS(2), GT(6), GTE(7), LT(8), LTE(9), EQ(10), NEQ(11), AND(12), OR(13), NOT_SUPPORTED(
            -1
    )
}

/*
 * RHS Assignment
 */
interface AssignRHSNode : Node
data class RHSExprNode(val expr: ExprNode) : AssignRHSNode
data class RHSArrayLitNode(val exprs: List<ExprNode>) : AssignRHSNode
data class RHSNewPairNode(val expr1: ExprNode, val expr2: ExprNode) : AssignRHSNode
data class RHSPairElemNode(val pairElem: PairElemNode) : AssignRHSNode
data class RHSCallNode(val ident: Ident, val argList: List<ExprNode>?) : AssignRHSNode

/*
 * Pair Elem
 */
abstract class PairElemNode(open val expr: ExprNode) : Node
data class FstExpr(override val expr: ExprNode) : PairElemNode(expr)
data class SndExpr(override val expr: ExprNode) : PairElemNode(expr)

/*
 * Types
 */
interface TypeNode : Node {
    val type: Type
}

// Base Types
interface BaseType : TypeNode

class Str(override val type: Type = Type(STRING)) : BaseType
class Bool(override val type: Type = Type(BOOL)) : BaseType
class Chr(override val type: Type = Type(CHAR)) : BaseType
class Int(override val type: Type = Type(INT)) : BaseType

// Nested pair type
class Pair(override val type: Type = Type(PAIR_LITER)) : BaseType

// Array Types
interface ArrayType : TypeNode
class ArrayNode(private val typeNode: TypeNode, override val type: Type = Type(typeNode.type)) : ArrayType

//Pair Types
data class PairTypeNode(
        val type1: PairElemTypeNode,
        val type2: PairElemTypeNode,
        override val type: Type = Type(type1.type, type2.type)
) : TypeNode

data class PairElemTypeNode(val typeNode: TypeNode, override val type: Type = typeNode.type) : TypeNode


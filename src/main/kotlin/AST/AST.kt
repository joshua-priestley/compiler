package AST

import AST.Types.Type
import antlr.WACCParser.*
import compiler.AST.Types.*
import kotlin.Int

interface Node

/*
 * Programs
 */
data class ProgramNode(val stucts: List<StructNode>, val classes: List<ClassNode>, val funcs: List<FunctionNode>, val stat: StatementNode) : Node

/*
 * Classes
 */
data class ClassNode(val ident: Ident, val params: List<Param>, val members: List<ClassMember>, val functions: List<FunctionNode>, val type: TypeClass) : Node

interface ClassMember : Node
data class NonInitMember(val memb: MemberNode) : ClassMember
data class InitMember(val memb: DeclarationNode) : ClassMember


/*
 * Structs
 */
data class StructNode(val ident: Ident, val members: List<MemberNode>, val type: TypeStruct) : Node
data class MemberNode(val type: TypeNode, val ident: Ident) : Node

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
            is IfElseNode -> !(!this.then.valid() || !this.else_?.valid()!! || !this.elseIfs.map { it.valid() }.all { it })
            is ElseIfNode -> this.then.valid()
            is SequenceNode -> this.statList[this.statList.size - 1].valid()
            is ExitNode -> true
            is ReturnNode -> true
            else -> false
        }
    }
}

interface SideExprOperator : Node
class AddOneNode : SideExprOperator
class SubOneNode : SideExprOperator
data class AddNNode(val value: ExprNode) : SideExprOperator
data class SubNNode(val value: ExprNode) : SideExprOperator
data class MulNNode(val value: ExprNode) : SideExprOperator
data class DivNNode(val value: ExprNode) : SideExprOperator

class SkipNode : StatementNode
class ContinueNode : StatementNode
class BreakNode : StatementNode
data class DeclarationNode(val type: TypeNode, val ident: Ident, val value: AssignRHSNode) : StatementNode
data class AssignNode(val lhs: AssignLHSNode, val rhs: AssignRHSNode) : StatementNode
data class ReadNode(val lhs: AssignLHSNode) : StatementNode
data class FreeNode(val expr: ExprNode) : StatementNode
data class ReturnNode(val expr: ExprNode) : StatementNode
data class ExitNode(val expr: ExprNode) : StatementNode
data class PrintNode(val expr: ExprNode) : StatementNode
data class PrintlnNode(val expr: ExprNode) : StatementNode
data class IfElseNode(val expr: ExprNode, val then: StatementNode, val elseIfs: List<ElseIfNode>, val else_: StatementNode?) : StatementNode
data class WhileNode(val expr: ExprNode, val do_: StatementNode) : StatementNode
data class DoWhileNode(val do_: StatementNode, val expr: ExprNode) : StatementNode
data class ForNode(val counter: DeclarationNode, val update: StatementNode, val terminator: ExprNode, val do_: StatementNode) : StatementNode
data class BeginEndNode(val stat: StatementNode) : StatementNode
data class SequenceNode(val statList: List<StatementNode>) : StatementNode
data class SideExpressionNode(val ident: AssignLHSIdentNode, val sideExpr: SideExprOperator) : StatementNode
data class CallNode(val ident: Ident, val argList: List<ExprNode>?) : StatementNode

data class ElseIfNode(val expr: ExprNode, val then: StatementNode) : StatementNode

/*
 * LHS Assignment
 */
interface AssignLHSNode : Node
data class AssignLHSIdentNode(val ident: Ident) : AssignLHSNode
data class AssignLHSClassNode(val classMemberNode: ClassMemberNode) : AssignLHSNode
data class AssignLHSStructNode(val structMemberNode: StructMemberNode) : AssignLHSNode
data class LHSArrayElemNode(val arrayElem: ArrayElem) : AssignLHSNode
data class LHSPairElemNode(val pairElem: PairElemNode) : AssignLHSNode

/*
 * Expressions
 */
interface ExprNode : Node
interface LiterNode : ExprNode
data class IntLiterNode(val value: String) : LiterNode
data class HexLiterNode(val value: String) : LiterNode
data class BinLiterNode(val value: String) : LiterNode
data class OctLiterNode(val value: String) : LiterNode
data class StrLiterNode(val value: String) : LiterNode
data class CharLiterNode(val value: String) : LiterNode
data class BoolLiterNode(val value: String) : LiterNode
class PairLiterNode : ExprNode
data class StructMemberNode(val structIdent: Ident, val memberExpr: ExprNode) : ExprNode
data class ClassMemberNode(val structIdent: Ident, val memberIdent: Ident) : ExprNode
data class Ident(var name: String) : LiterNode
data class ArrayElem(val ident: Ident, val expr: List<ExprNode>) : ExprNode
data class UnaryOpNode(val operator: UnOp, val expr: ExprNode) : ExprNode
data class BinaryOpNode(val operator: BinOp, val expr1: ExprNode, val expr2: ExprNode) : ExprNode

// Expression nodes for functions
data class Param(val type: TypeNode, val ident: Ident) : ExprNode


/*
 * Operators
 */
enum class UnOp(val value: kotlin.Int) {
    NOT(14), MINUS(2), LEN(15), ORD(16), CHR(17), BITWISENOT(24), NOT_SUPPORTED(-1)
}

//think about how to do binary operator precedence more
enum class BinOp(val value: Int) : Node {
    MUL(3), DIV(4), MOD(5), PLUS(1), MINUS(2), GT(6), GTE(7), LT(8), LTE(9), EQ(10), NEQ(11), AND(12), OR(13), BITWISEAND(22), BITWISEOR(23), NOT_SUPPORTED(-1)
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
data class RHSClassCallNode(val classIdent: Ident, val callNode: RHSCallNode) : AssignRHSNode
data class RHSFoldNode(val sequenceNode: SequenceNode) : AssignRHSNode
interface RHSNewObject : AssignRHSNode
data class RHSNewStruct(val structName: Ident, val argList: List<ExprNode>) : RHSNewObject
data class RHSNewClass(val className: Ident, val argList: List<ExprNode>?) : RHSNewObject

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

class StructType(override val type: TypeStruct) : TypeNode
class ClassType(override val type: TypeClass) : TypeNode
class VoidType(override val type: Type = TypeBase(VOID)) : TypeNode

// Base Types
interface BaseType : TypeNode

class Str(override val type: Type = TypeBase(STRING)) : BaseType
class Bool(override val type: Type = TypeBase(BOOL)) : BaseType
class Chr(override val type: Type = TypeBase(CHAR)) : BaseType
class Int(override val type: Type = TypeBase(INT)) : BaseType

// Nested pair type
class Pair(override val type: Type = TypePair(null, null)) : BaseType

// Array Types
interface ArrayType : TypeNode
class ArrayNode(private val typeNode: TypeNode, override val type: Type = TypeArray(typeNode.type)) : ArrayType

//Pair Types
data class PairTypeNode(
        val type1: PairElemTypeNode,
        val type2: PairElemTypeNode,
        override val type: Type = TypePair(type1.type, type2.type)
) : TypeNode

data class PairElemTypeNode(val typeNode: TypeNode, override val type: Type = typeNode.type) : TypeNode


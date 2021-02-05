import antlr.WACCParser.*
import javax.swing.plaf.nimbus.State

interface Node

/*
 * Programs
 */
data class ProgramNode(val funcs: List<FunctionNode>, val stat: StatementNode) : Node

/*
 * Functions
 */
data class FunctionNode(val type: Int, val ident: String, val params: List<Param>, val stat: StatementNode) : Node

/*
 * Statements
 */
interface StatementNode : Node

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
data class SequenceNode(val stat1: StatementNode, val stat2: StatementNode) : StatementNode

/*
 * LHS Assignment
 */
interface AssignLHSNode : Node
data class AssignLHSIdentNode(val ident: Ident) : AssignLHSNode
data class LHSArrayElemNode(val arrayElem: ArrayElem) : AssignLHSNode
data class LHSPairElemNode(val pairElem: PairElemNode)

/*
 * Expressions
 */
interface ExprNode : Node
data class IntLiterNode(val value: String) : ExprNode
data class StrLiterNode(val value: String) : ExprNode

/*
 * RHS Assignment
 */
interface AssignRHSNode : Node
data class RHSExprNode(val expr: ExprNode) : AssignRHSNode
data class RHSArrayLitNode(val exprs: List<ExprNode>) : AssignRHSNode
data class RHSNewPairNode(val expr1: ExprNode, val expr2: ExprNode) : AssignRHSNode
data class RHSPairElemNode(val pairElem: PairElemNode) : AssignRHSNode
data class RHSCallNode(val ident: Ident, val argList: List<ExprNode>)

/*
 * Pair Elem
 */
interface PairElemNode : Node
data class FstExpr(val expr: ExprNode) : PairElemNode
data class SndExpr(val expr: ExprNode) : PairElemNode

/*
 * Types
 */
interface TypeNode : Node

// Base Types
interface BaseType : TypeNode
class Str() : BaseType
class Bool() : BaseType
class Chr() : BaseType
class Int() : BaseType

// Array Types
interface ArrayType : TypeNode
data class ArrayNode(val type: TypeNode) : ArrayType


data class Ident(val value: String)
data class Param(val type: String, val ident: Ident)
data class ArrayElem(val ident: Ident, val exprList: List<ExprNode>)
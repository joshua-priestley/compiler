import antlr.WACCParser.*
import javax.swing.plaf.nimbus.State

interface Node

data class ProgramNode(val funcs:List<FunctionNode>, val stat:StatementNode) : Node

data class FunctionNode(val type: Int, val ident: String, val params: List<Param>, val stat: StatementNode) : Node

interface StatementNode : Node

class SkipNode : StatementNode
data class DeclarationNode(val type: TypeNode, val ident: Ident, val value: AssignRHSNode) : StatementNode
data class ExitNode(val expr: ExprNode) : StatementNode
data class PrintlnNode(val expr: ExprNode) : StatementNode
data class PrintNode(val expr: ExprNode) : StatementNode
data class SequenceNode(val stat1: StatementNode, val stat2: StatementNode) : StatementNode

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

/*
 * Types
 */
interface TypeNode : Node

// Base Types
interface BaseType : TypeNode
data class Str(val value: String) : BaseType

data class Ident(val value: String)
data class Param(val type: String, val ident: Ident)

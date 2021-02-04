import antlr.WACCParser.*
import javax.swing.plaf.nimbus.State

interface Node

data class ProgramNode(val funcs:List<FunctionNode>, val stat:StatementNode) : Node

data class FunctionNode(val type: Int, val ident: String, val params: List<Param>, val stat: StatementNode) : Node

interface StatementNode : Node

class SkipNode : StatementNode
data class ExitNode(val expr: ExprNode) : StatementNode
data class PrintlnNode(val expr: ExprNode) : StatementNode
data class PrintNode(val expr: ExprNode) : StatementNode
data class SequenceNode(val stat1: StatementNode, val stat2: StatementNode) : StatementNode

interface ExprNode : Node

data class IntLiterNode(val value: String) : ExprNode
data class StrLiterNode(val value: String) : ExprNode

/*
 * Types
 */
interface TypeNode : Node

// Base Types
interface BaseType : TypeNode
data class Str(val value: String) : BaseType

data class Ident(val value: String)
data class Param(val type: String, val ident: Ident)

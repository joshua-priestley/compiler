import antlr.WACCParser.*

interface Node

data class ProgramNode(val funcs:List<FunctionNode>, val stat:StatementNode) : Node

data class FunctionNode(val dummy:Int) : Node

interface StatementNode : Node

data class ExitNode(val expr: ExprNode) : StatementNode

interface ExprNode : Node

data class IntLiterNode(val value: String) : ExprNode

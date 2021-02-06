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
data class FunctionNode(val type: TypeNode, val ident: Ident, val params: List<Param>, val stat: StatementNode) : Node
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
data class CharLiterNode(val value: String) : ExprNode
data class BoolLiterNode(val value: String) : ExprNode
class PairLiterNode() : ExprNode
data class Ident(val name: String) : ExprNode
data class ArrayElem(val ident: Ident, val expr: List<ExprNode>) : ExprNode
data class UnaryOpNode(val operator: UnOp, val expr: ExprNode) : ExprNode
data class BinaryOpNode(val operator: BinOp, val expr1: ExprNode, val expr2: ExprNode) : ExprNode

// Expression nodes for functions
data class Param(val type: TypeNode, val ident: Ident) : ExprNode
data class ParamList(val params: List<Param>) : ExprNode
data class ArgList(val args: List<ExprNode>) : ExprNode
/*
 * Operators
 */
enum class UnOp {
    NOT, MINUS, LEN, ORD, CHR, NOT_SUPPORTED
}

//TODO does making this a node mean its no longer an AST?
//think about how to do binary operater precedence more
enum class BinOp : Node {
    MUL, DIV, MOD, PLUS, MINUS, GT, GTE, LT, LTE, EQ, NEQ, AND, OR, NOT_SUPPORTED
}

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

//Pair Types
data class PairTypeNode(val type1: PairElemTypeNode, val type2: PairElemTypeNode) : TypeNode
data class PairElemTypeNode(val type: TypeNode) : TypeNode


class SemanticWalker(programNode: ProgramNode) {
    val globalSymbolTable = programNode.globalSymbolTable

    /*
    class PairLiterNode() : ExprNode
    data class Ident(val name: String) : ExprNode
    data class ArrayElem(val ident: Ident, val expr: List<ExprNode>) : ExprNode
    data class UnaryOpNode(val operator: UnOp, val expr: ExprNode) : ExprNode
    data class BinaryOpNode(val operator: BinOp, val expr1: ExprNode, val expr2: ExprNode) : ExprNode
    */

    private fun getTypeOfExpr(exprNode: ExprNode): TypeNode? {
        return when (exprNode) {
            is IntLiterNode -> Int()
            is CharLiterNode -> Chr()
            is BoolLiterNode -> Bool()
            is StrLiterNode -> Str()
            else -> null
        }
    }

    private fun checkExprInSymbolTable(exprNode: ExprNode, symbolTable: SymbolTable): Boolean {
        when(exprNode) {
            is Ident -> {
                if (symbolTable.getNode(exprNode.name) == null) {return false}
            }
            is ArrayElem -> {
                if (symbolTable.getNode(exprNode.ident.name) == null) {return false}
            }
        }
        return true
    }

    fun checkStat(statementNode: StatementNode, symbolTable: SymbolTable, functionNode: FunctionNode?) {
        when (statementNode) {
            is ReturnNode -> {
                val funcReturnNodeType = symbolTable.getNode(functionNode.toString())
                val valid = checkExprInSymbolTable(statementNode.expr, symbolTable)
                if (!valid) {
                    println("Semantic error - Return value not in scope")
                }
                val returnNodeType = getTypeOfExpr(statementNode.expr)
                if (funcReturnNodeType!!::class != returnNodeType!!::class) {
                    println("Semantic error - Mismatched return types")
                    return
                }
            }

        }
    }

}
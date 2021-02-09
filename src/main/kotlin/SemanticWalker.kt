import kotlin.system.exitProcess

class SemanticWalker(programNode: ProgramNode) {
    val globalSymbolTable = programNode.globalSymbolTable

    init {
        for (funcNode in programNode.funcs) {
            checkFunc(funcNode)
        }
        checkStat(programNode.stat, globalSymbolTable, null)
    }

    private fun semanticErrorDetected() {
        exitProcess(SEMANTIC_ERROR_RETURN)
    }

    private fun equalTypes(t1: TypeNode, t2: TypeNode): Boolean {
        return (t1::class == t2::class)
    }

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
                if (symbolTable.getNode(exprNode.name) != null) {return true }
            }
            is ArrayElem -> {
                if (symbolTable.getNode(exprNode.ident.name) != null) {return true}
            }
        }
        return false
    }

    private fun checkStat(statementNode: StatementNode, symbolTable: SymbolTable, functionNode: FunctionNode?) {
        when (statementNode) {
            is SequenceNode -> {
                checkStat(statementNode.stat1, symbolTable, functionNode)
                checkStat(statementNode.stat2, symbolTable, functionNode)
            }
            is IfElseNode -> {
                val condNode = getTypeOfExpr(statementNode.expr)
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- If statement condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            is WhileNode -> {
                val condNode = getTypeOfExpr(statementNode.expr)
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- While loop condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            is ExitNode -> {
                val exitNode = getTypeOfExpr(statementNode.expr)
                if (exitNode == null || !equalTypes(exitNode, Int())) {
                    println("SEMANTIC ERROR --- Cannot exit a non-integer")
                    semanticErrorDetected()
                }
            }
            is BeginEndNode -> {
                if (functionNode != null) {
                    println("SEMANTIC ERROR --- Cannot have a begin or end node in a function")
                    semanticErrorDetected()
                }
            }
            is ReturnNode -> {
                if (functionNode == null) {
                    println("SEMANTIC ERROR --- Return statement outside of function")
                    semanticErrorDetected()
                }
                val funcReturnNodeType = symbolTable.getNode(functionNode.toString())
                val valid = checkExprInSymbolTable(statementNode.expr, symbolTable)
                if (!valid) {
                    println("SEMANTIC ERROR --- Return value not in scope")
                    semanticErrorDetected()
                }
                val returnNodeType = getTypeOfExpr(statementNode.expr)
                if (funcReturnNodeType!!::class != returnNodeType!!::class) {
                    println("SEMANTIC ERROR --- Mismatched return types")
                    semanticErrorDetected()
                }
            }

            is FreeNode -> {
                val valid = checkExprInSymbolTable(statementNode.expr, symbolTable)
                if (!valid) {
                    println("SEMANTIC ERROR --- Variable doesnt exist/Already freed")
                    semanticErrorDetected()
                } else {
                    if (statementNode.expr is Ident) {
                        symbolTable.removeNode(statementNode.expr.name)
                    }
                }
            }
            is ReadNode -> {
                // LHS must be array or variable of type integer or char
            }
            is DeclarationNode -> {}
            is AssignNode -> {}

        }
    }

    private fun checkFunc(functionNode: FunctionNode) {
        checkStat(functionNode.stat, functionNode.functionSymbolTable, functionNode)
    }

}
import kotlin.system.exitProcess

//TODO: When an assign node is reached, change the value in the symbol table
//TODO: Implement the "equalTypes" function everywhere necessary
//TODO: Swap the big if statements for when clauses

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

    private fun getTypeOfBinOp(operator: BinOp): TypeNode? {
        if (1 <= operator.value || operator.value <= 5) {
            return Int()
        } else if (5 <= operator.value || operator.value <= 13) {
            return Bool()
        }
        return null
    }

    private fun getTypeOfUnOp(operator: UnOp): TypeNode? {
        if (operator.value in arrayOf(2, 15, 16)) {
            return Int()
        } else if (operator.value == 14) {
            return Bool()
        } else if (operator.value == 17) {
            return Chr()
        }
        return null
    }

    private fun getTypeOfExpr(exprNode: ExprNode, symbolTable: SymbolTable): TypeNode? {
        //TODO: for array elem, i think this just stores the index of the list so this doesnt need
        //TODO: to be checked in the same way, maybe?
        return when (exprNode) {
            is IntLiterNode -> Int()
            is CharLiterNode -> Chr()
            is BoolLiterNode -> Bool()
            is StrLiterNode -> Str()
            is ArrayElem -> {
                if (getTypeOfExpr(exprNode.expr[0], symbolTable)?.let { equalTypes(it, Int()) } == false) {
                    println("SEMANTIC ERROR --- Array index must be an integer")
                    semanticErrorDetected()
                }
                val arrayNode = symbolTable.getNode(exprNode.ident.name)
                when {
                    arrayNode == null -> {
                        println("SEMANTIC ERROR --- Array does not exist")
                        semanticErrorDetected()
                    }
                    arrayNode::class == DeclarationNode::class -> {
                        return (arrayNode as DeclarationNode).type
                    }
                    arrayNode::class == FunctionNode::class -> {
                        println("SEMANTIC ERROR --- Cannot index a function")
                        semanticErrorDetected()
                    }
                }
                return null
            }
            is Ident -> {
                val variableNode = symbolTable.getNode(exprNode.name)
                when {
                    variableNode == null -> {
                        println("SEMANTIC ERROR --- Variable does not exist")
                        semanticErrorDetected()
                    }
                    variableNode::class == DeclarationNode::class -> {
                        return (variableNode as DeclarationNode).type
                    }
                    variableNode::class == FunctionNode::class -> {
                        println("SEMANTIC ERROR --- Cannot something function??")
                        semanticErrorDetected()
                    }
                }
                return null
            }
            is PairLiterNode -> null
            is UnaryOpNode -> getTypeOfUnOp(exprNode.operator)
            is BinaryOpNode -> getTypeOfBinOp(exprNode.operator)
            else -> null
        }
    }

    /*
    * RHSExprNode(val expr: ExprNode) : AssignRHSNode
    data class RHSArrayLitNode(val exprs: List<ExprNode>) : AssignRHSNode
    data class RHSNewPairNode(val expr1: ExprNode, val expr2: ExprNode) : AssignRHSNode
    data class RHSPairElemNode(val pairElem: PairElemNode) : AssignRHSNode
    data class RHSCallNode
    * */
    private fun checkArrayAllSameType(exprs: List<ExprNode>, symbolTable: SymbolTable): TypeNode? {
        val firstType = getTypeOfExpr(exprs[0], symbolTable)
        for (expr in exprs) {
            if (getTypeOfExpr(expr, symbolTable) == null) {
                println("SEMANTIC ERROR --- Invalid array element")
                semanticErrorDetected()
            } else if (getTypeOfExpr(expr, symbolTable)!!::class != firstType!!::class) {
                println("SEMANTIC ERROR --- Array elements have differing types")
                semanticErrorDetected()
            }
        }
        return firstType
    }

    private fun getRHSType(rhsNode: AssignRHSNode, symbolTable: SymbolTable): TypeNode? {
        return when (rhsNode) {
            is RHSExprNode -> {
                getTypeOfExpr(rhsNode.expr, symbolTable)
            }
            is RHSArrayLitNode -> {
                checkArrayAllSameType(rhsNode.exprs, symbolTable)
            }
            is RHSNewPairNode -> {
                return null
            }
            is RHSPairElemNode -> {
                return null
            }
            is RHSCallNode -> {
                val functionNode = symbolTable.getNode(rhsNode.ident.name)
                when {
                    functionNode == null -> {
                        println("SEMANTIC ERROR --- Function does not exist")
                        semanticErrorDetected()
                    }
                    functionNode::class != FunctionNode::class -> {
                        println("SEMANTIC ERROR --- Call to something other than a function")
                        semanticErrorDetected()
                    }
                    else -> return (functionNode as FunctionNode).type
                }
                return null
            }
            else -> return null
        }
    }

    private fun getLHSType(lhsNode: AssignLHSNode, symbolTable: SymbolTable): TypeNode? {
        return null
    }

    private fun checkExprInSymbolTable(exprNode: ExprNode, symbolTable: SymbolTable): Boolean {
        when (exprNode) {
            is Ident -> {
                if (symbolTable.getNode(exprNode.name) != null) {
                    return true
                }
            }
            is ArrayElem -> {
                if (symbolTable.getNode(exprNode.ident.name) != null) {
                    return true
                }
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
                val condNode = getTypeOfExpr(statementNode.expr, symbolTable)
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- If statement condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            is WhileNode -> {
                val condNode = getTypeOfExpr(statementNode.expr, symbolTable)
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- While loop condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            is ExitNode -> {
                val exitNode = getTypeOfExpr(statementNode.expr, symbolTable)
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
                val returnNodeType = getTypeOfExpr(statementNode.expr, symbolTable)
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
                val lhsTypeNode = getLHSType(statementNode.lhs, symbolTable)
                if (lhsTypeNode == null || (!equalTypes(lhsTypeNode, Int())) || (!equalTypes(lhsTypeNode, Chr()))) {
                    println("SEMANTIC ERROR --- LHS must be of type int or chr variable or array for READ")
                    semanticErrorDetected()
                }
            }
            is DeclarationNode -> {
                val rhsTypeNode = getRHSType(statementNode.value, symbolTable)
                if (rhsTypeNode == null || !equalTypes(statementNode.type, rhsTypeNode)) {
                    println("SEMANTIC ERROR --- LHS type != RHS Type")
                    semanticErrorDetected()
                }
            }
            is AssignNode -> {
                val lhsTypeNode = getLHSType(statementNode.lhs, symbolTable)
                val rhsTypeNode = getRHSType(statementNode.rhs, symbolTable)
                if (lhsTypeNode == null || rhsTypeNode == null || !equalTypes(lhsTypeNode, rhsTypeNode)) {
                    println("SEMANTIC ERROR --- LHS type != RHS Type")
                    semanticErrorDetected()
                }
            }

        }
    }

    private fun checkFunc(functionNode: FunctionNode) {
        checkStat(functionNode.stat, functionNode.functionSymbolTable, functionNode)
    }

}
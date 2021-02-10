import kotlin.system.exitProcess

//TODO: When an assign node is reached, change the value in the symbol table
//TODO: Implement the "equalTypes" function everywhere necessary
//TODO: Swap the big if statements for when clauses

// This class takes in a ProgramNode then walks through the tree doing semantic analysis
class SemanticWalker(programNode: ProgramNode) {
    val globalSymbolTable = programNode.globalSymbolTable

    init {
        // Check each function
        for (funcNode in programNode.funcs) {
            checkFunc(funcNode)
        }
        // Then recursively check the top layer StatementNode
        checkStat(programNode.stat, globalSymbolTable, null)
    }

    // Exit to quit the program if it encounters a semantic error
    private fun semanticErrorDetected() {
        exitProcess(SEMANTIC_ERROR_RETURN)
    }

    // Check two type nodes are the same class
    private fun equalTypes(t1: TypeNode, t2: TypeNode): Boolean {
        return (t1::class == t2::class)
    }

    // Get the type of a binary operator for further checks using the enum values
    private fun getTypeOfBinOp(operator: BinOp): TypeNode? {
        // PLUS, MINUS, MUL, DIV, MOD
        if (1 <= operator.value || operator.value <= 5) {
            return Int()
        // Comparisons, EQ, NEQ, AND, OR
        } else if (5 <= operator.value || operator.value <= 13) {
            return Bool()
        }
        return null
    }

    // Get the type of a unary operator
    private fun getTypeOfUnOp(operator: UnOp): TypeNode? {
        // MINUS, LEN, ORD
        if (operator.value in arrayOf(2, 15, 16)) {
            return Int()
        // NOT
        } else if (operator.value == 14) {
            return Bool()
        // CHR
        } else if (operator.value == 17) {
            return Chr()
        }
        return null
    }

    // Takes and expression node and the symbol table and gets the top layer type of that node
    private fun getTypeOfExpr(exprNode: ExprNode, symbolTable: SymbolTable): TypeNode? {
        //TODO: for array elem, i think this just stores the index of the list so this doesnt need
        //TODO: to be checked in the same way, maybe?
        return when (exprNode) {
            // These are simple conversions of base types
            is IntLiterNode -> Int()
            is CharLiterNode -> Chr()
            is BoolLiterNode -> Bool()
            is StrLiterNode -> Str()
            // Checks the types of the array elem are correct
            is ArrayElem -> {
                // Check the index is correct
                if (getTypeOfExpr(exprNode.expr[0], symbolTable)?.let { equalTypes(it, Int()) } == false) {
                    println("SEMANTIC ERROR --- Array index must be an integer")
                    semanticErrorDetected()
                }
                // Check the node being indexed is an array that exists then return the type
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
            // Get the type of the variable referenced and return it
            is Ident -> {
                val variableNode = symbolTable.getNode(exprNode.name)
                when {
                    // Check it actually exists
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
            //TODO: Pair liter type check
            is PairLiterNode -> null
            //TODO: Check the expression type matches the UnOp type
            is UnaryOpNode -> getTypeOfUnOp(exprNode.operator)
            //TODO: Check each expression type matches the BinOp type
            is BinaryOpNode -> getTypeOfBinOp(exprNode.operator)
            else -> null
        }
    }

    // Check all the elements of an array have the same type
    private fun checkArrayAllSameType(exprs: List<ExprNode>, symbolTable: SymbolTable): TypeNode? {
        val firstType = getTypeOfExpr(exprs[0], symbolTable)
        for (expr in exprs) {
            // If the type cannot be found, something is wrong with the element
            if (getTypeOfExpr(expr, symbolTable) == null) {
                println("SEMANTIC ERROR --- Invalid array element")
                semanticErrorDetected()
            // If the elements type does not match the first then there is an error
            } else if (getTypeOfExpr(expr, symbolTable)!!::class != firstType!!::class) {
                println("SEMANTIC ERROR --- Array elements have differing types")
                semanticErrorDetected()
            }
        }
        // Otherwise all types are the same as the first, so return that
        return firstType
    }

    // Get the type of the RHS of a statement node
    private fun getRHSType(rhsNode: AssignRHSNode, symbolTable: SymbolTable): TypeNode? {
        return when (rhsNode) {
            // If it is expression, simply call the function to get the type
            is RHSExprNode -> {
                getTypeOfExpr(rhsNode.expr, symbolTable)
            }
            // If it is an array then check all types are the same and return that
            is RHSArrayLitNode -> {
                checkArrayAllSameType(rhsNode.exprs, symbolTable)
            }
            is RHSNewPairNode -> {
                //TODO: Implement
                return null
            }
            is RHSPairElemNode -> {
                //TODO: Implement
                return null
            }
            is RHSCallNode -> {
                // Check the reference exists and is actually a function
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
                    // If it is a valid function then return its return type
                    else -> return (functionNode as FunctionNode).type
                }
                return null
            }
            else -> return null
        }
    }

    // Check the LHS of a statement node and get its type
    private fun getLHSType(lhsNode: AssignLHSNode, symbolTable: SymbolTable): TypeNode? {
        return null
    }

    // Check an expression is in the table
    private fun checkExprInSymbolTable(exprNode: ExprNode, symbolTable: SymbolTable): Boolean {
        // An expression will only be in the table if it is an array or a variable
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

    // TODO: Return a boolean to check its passed correctly?
    // TODO: Check the statement of the while loop
    // TODO: Check the expressions of the branches of the if statement?
    // Check all the different types of StatementNodes
    private fun checkStat(statementNode: StatementNode, symbolTable: SymbolTable, functionNode: FunctionNode?) {
        when (statementNode) {
            // If it is a sequence node, check either side
            is SequenceNode -> {
                checkStat(statementNode.stat1, symbolTable, functionNode)
                checkStat(statementNode.stat2, symbolTable, functionNode)
            }
            // Check the condition given is a boolean
            is IfElseNode -> {
                val condNode = getTypeOfExpr(statementNode.expr, symbolTable)
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- If statement condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            // Check the condition given is a boolean
            is WhileNode -> {
                val condNode = getTypeOfExpr(statementNode.expr, symbolTable)
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- While loop condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            // Check the exit value is an integer
            is ExitNode -> {
                val exitNode = getTypeOfExpr(statementNode.expr, symbolTable)
                if (exitNode == null || !equalTypes(exitNode, Int())) {
                    println("SEMANTIC ERROR --- Cannot exit a non-integer")
                    semanticErrorDetected()
                }
            }
            //TODO: I think this is wrong, you can have an end node in a function, not sure about begin?
            //      Might be a syntactic error not semantic
            is BeginEndNode -> {
                if (functionNode != null) {
                    println("SEMANTIC ERROR --- Cannot have a begin or end node in a function")
                    semanticErrorDetected()
                }
            }
            // Checks that the return type is equal to the type of what is actually being returned
            is ReturnNode -> {
                // If we're not in a function then it cant return
                if (functionNode == null) {
                    println("SEMANTIC ERROR --- Return statement outside of function")
                    semanticErrorDetected()
                }
                //TODO: Does this check properly if we're returning just hardcoded values?
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
            // Checks the node being freed hasn't already been freed/never exists
            is FreeNode -> {
                val valid = checkExprInSymbolTable(statementNode.expr, symbolTable)
                if (!valid) {
                    println("SEMANTIC ERROR --- Variable doesnt exist/Already freed")
                    semanticErrorDetected()
                } else {
                    // Removes the freed node from the symbol table so it cant be used anymore
                    if (statementNode.expr is Ident) {
                        symbolTable.removeNode(statementNode.expr.name)
                    }
                }
            }
            // Makes sure the lhs of the argument is an integer or a char type
            is ReadNode -> {
                val lhsTypeNode = getLHSType(statementNode.lhs, symbolTable)
                if (lhsTypeNode == null || (!equalTypes(lhsTypeNode, Int())) || (!equalTypes(lhsTypeNode, Chr()))) {
                    println("SEMANTIC ERROR --- LHS must be of type int or chr variable or array for READ")
                    semanticErrorDetected()
                }
            }
            // Checks the lhs and rhs of the declaration are equal
            is DeclarationNode -> {
                val rhsTypeNode = getRHSType(statementNode.value, symbolTable)
                if (rhsTypeNode == null || !equalTypes(statementNode.type, rhsTypeNode)) {
                    println("SEMANTIC ERROR --- LHS type != RHS Type")
                    semanticErrorDetected()
                }
            }
            // Checks the lhs and rhs of the assignment are equal
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

    // Checks the stat within a function
    private fun checkFunc(functionNode: FunctionNode) {
        checkStat(functionNode.stat, functionNode.functionSymbolTable, functionNode)
    }

}
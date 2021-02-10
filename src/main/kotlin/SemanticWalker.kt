import antlr.WACCParser

//TODO: When an assign node is reached, change the value in the symbol table
//TODO: Implement the "equalTypes" function everywhere necessary
//TODO: Swap the big if statements for when clauses
//TODO: Check lhs stuff because if its an array then the types will differ

// This class takes in a ProgramNode then walks through the tree doing semantic analysis
class SemanticWalker(programNode: ProgramNode, programContext: WACCParser.ProgramContext) {
    /*
    private val globalSymbolTable = programNode.globalSymbolTable
    var errorDetected: Boolean = false

    init {
        // Check each function
        println(programContext)
        for (i in 0..programNode.funcs.size) {
            checkFunc(programNode.funcs[i], programContext.func(i))
        }

        // Then recursively check the top layer StatementNode
        checkStat(programNode.stat, globalSymbolTable, null, programContext.stat())
    }

    // Exit to quit the program if it encounters a semantic error
    private fun semanticErrorDetected() {
        errorDetected = true
        //exitProcess(SEMANTIC_ERROR_RETURN)
    }

    // Check two type nodes are the same class
    private fun equalTypes(t1: TypeNode, t2: TypeNode): Boolean {
        return (t1::class == t2::class)
    }

    // Get the type of a binary operator for further checks using the enum values
    private fun getTypeOfBinOp(operator: BinOp): TypeNode {
        // PLUS, MINUS, MUL, DIV, MOD
        return if (1 <= operator.value || operator.value <= 5) {
            Int()
            // Comparisons, EQ, NEQ, AND, OR
        } else {
            Bool()
        }
    }

    // Get the type of a unary operator
    private fun getTypeOfUnOp(operator: UnOp): TypeNode {
        // MINUS, LEN, ORD
        return if (operator.value in arrayOf(2, 15, 16)) {
            Int()
            // NOT
        } else if (operator.value == 14) {
            Bool()
            // CHR
        } else {
            Chr()
        }
    }

    // Takes and expression node and the symbol table and gets the top layer type of that node
    private fun getTypeOfExpr(exprNode: ExprNode, symbolTable: SymbolTable, exprContext: WACCParser.ExprContext?): TypeNode? {
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
                if (getTypeOfExpr(exprNode.expr[0], symbolTable, (exprContext as WACCParser.ArrayElemContext))?.let { equalTypes(it, Int()) } == false) {
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
            // A Pair Liter is just null
            is PairLiterNode -> Pair()
            is UnaryOpNode -> {
                val opType = getTypeOfUnOp(exprNode.operator)
                val exprType = getTypeOfExpr(exprNode.expr, symbolTable, (exprContext as WACCParser.UnaryOpContext).expr())
                if (exprType == null) {
                    println("SEMANTIC ERROR --- Operator expression issue")
                    semanticErrorDetected()
                }
                if (!equalTypes(opType, exprType!!)) {
                    println("SEMANTIC ERROR --- Wrong type for this operation")
                    semanticErrorDetected()
                }
                return opType
            }
            is BinaryOpNode -> {
                val opType = getTypeOfBinOp(exprNode.operator)
                val expr1Type = getTypeOfExpr(exprNode.expr1, symbolTable, (exprContext as WACCParser.BinaryOpContext).expr(0))
                val expr2Type = getTypeOfExpr(exprNode.expr2, symbolTable, exprContext.expr(1))
                if (expr1Type == null || !equalTypes(expr1Type, opType)) {
                    println("SEMANTIC ERROR --- Expression 1 type error")
                    semanticErrorDetected()
                }
                if (expr2Type == null || !equalTypes(expr2Type, opType)) {
                    println("SEMANTIC ERROR --- Expression 2 type error")
                    semanticErrorDetected()
                }
                return opType
            }
            else -> null
        }
    }

    // Check all the elements of an array have the same type
    private fun checkArrayAllSameType(exprs: List<ExprNode>, symbolTable: SymbolTable, exprCtxs: MutableList<WACCParser.ExprContext>): TypeNode? {
        val firstType = getTypeOfExpr(exprs[0], symbolTable, exprCtxs[0])
        for (i in 0..exprs.size) {
            // If the type cannot be found, something is wrong with the element
            if (getTypeOfExpr(exprs[i], symbolTable, exprCtxs[i]) == null) {
                println("SEMANTIC ERROR --- Invalid array element")
                semanticErrorDetected()
                // If the elements type does not match the first then there is an error
            } else if (getTypeOfExpr(exprs[i], symbolTable, exprCtxs[i])!!::class != firstType!!::class) {
                println("SEMANTIC ERROR --- Array elements have differing types")
                semanticErrorDetected()
            }
        }
        // Otherwise all types are the same as the first, so return that
        return firstType
    }

    // Get the type of the RHS of a statement node
    private fun getRHSType(rhsNode: AssignRHSNode, symbolTable: SymbolTable, rhsExprContext: WACCParser.Assign_rhsContext): TypeNode? {
        return when (rhsNode) {
            // If it is expression, simply call the function to get the type
            is RHSExprNode -> {
                getTypeOfExpr(rhsNode.expr, symbolTable, (rhsExprContext as WACCParser.AssignRhsExprContext).expr())
            }
            // If it is an array then check all types are the same and return that
            is RHSArrayLitNode -> {
                checkArrayAllSameType(rhsNode.exprs, symbolTable, (rhsExprContext as WACCParser.AssignRhsArrayContext).array_liter().expr())
            }
            is RHSNewPairNode -> {
                val type1 = getTypeOfExpr(rhsNode.expr1, symbolTable, (rhsExprContext as WACCParser.AssignRhsNewpairContext).expr(0))
                val type2 = getTypeOfExpr(rhsNode.expr2, symbolTable, rhsExprContext.expr(1))
                if (type1 == null) {
                    println("SEMANTIC ERROR --- First elem of Pair not typeable")
                    semanticErrorDetected()
                } else if (type2 == null) {
                    println("SEMANTIC ERROR --- Second elem of Pair not typeable")
                    semanticErrorDetected()
                }
                PairTypeNode(PairElemTypeNode(type1!!), PairElemTypeNode(type2!!))
            }
            is RHSPairElemNode -> {
                //TODO: the pair elem actually holds the name of the pair that you are trying to get first or second from so this will have to change
                if (rhsNode.pairElem::class == FstExpr::class) {
                    getTypeOfExpr((rhsNode.pairElem as FstExpr).expr, symbolTable, null)
                } else {
                    getTypeOfExpr((rhsNode.pairElem as SndExpr).expr, symbolTable, null)
                }
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
                }
                // If it is a valid function then return its return type
                (functionNode as FunctionNode).type
            }
            else -> null
        }
    }

    // Check the LHS of a statement node and get its type
    private fun getLHSType(lhsNode: AssignLHSNode, symbolTable: SymbolTable, lhsCTX: WACCParser.Assign_lhsContext): TypeNode? {
        return when (lhsNode) {
            is AssignLHSIdentNode -> {
                val node = symbolTable.getNode(lhsNode.ident.name)
                if (node == null) {
                    println("SEMANTIC ERROR --- Variable does not exist")
                    semanticErrorDetected()
                    null
                } else if (node::class != DeclarationNode::class) {
                    println("SEMANTIC ERROR --- LHS not a variable")
                    semanticErrorDetected()
                    null
                } else {
                    (node as DeclarationNode).type
                }
            }
            is LHSArrayElemNode -> {
                val array = symbolTable.getNode(lhsNode.arrayElem.ident.name)
                if (array == null) {
                    println("SEMANTIC ERROR --- Array does not exist")
                    semanticErrorDetected()
                    null
                } else if (array::class != DeclarationNode::class) {
                    println("SEMANTIC ERROR --- LHS not an array")
                    semanticErrorDetected()
                    null
                } else {
                    (array as DeclarationNode).type
                }
            }
            is LHSPairElemNode -> {
                null
            }
            else -> null
        }
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

    private fun isExprHardCoded(exprNode: ExprNode): Boolean {
        return exprNode is StrLiterNode || exprNode is IntLiterNode || exprNode is CharLiterNode || exprNode is BoolLiterNode
    }

    // TODO: Return a boolean to check its passed correctly?
    // TODO: Check the statement of the while loop
    // TODO: Check the expressions of the branches of the if statement?
    // Check all the different types of StatementNodes
    private fun checkStat(statementNode: StatementNode, symbolTable: SymbolTable, functionNode: FunctionNode?, statCTX: WACCParser.StatContext) {
        // If one error is found, dont bother with the rest of the checks
        when (statementNode) {
            // If it is a sequence node, check either side
            is SequenceNode -> {
                checkStat(statementNode.stat1, symbolTable, functionNode, (statCTX as WACCParser.SequenceContext).stat(0))
                checkStat(statementNode.stat2, symbolTable, functionNode, statCTX.stat(1))
            }
            // Check the condition given is a boolean
            is IfElseNode -> {
                val condNode = getTypeOfExpr(statementNode.expr, symbolTable, (statCTX as WACCParser.IfContext).expr())
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- If statement condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            // Check the condition given is a boolean
            is WhileNode -> {
                val condNode = getTypeOfExpr(statementNode.expr, symbolTable, (statCTX as WACCParser.WhileContext).expr())
                if (condNode == null || !equalTypes(condNode, Bool())) {
                    println("SEMANTIC ERROR --- While loop condition must be a boolean")
                    semanticErrorDetected()
                }
            }
            // Check the exit value is an integer
            is ExitNode -> {
                val exitNode = getTypeOfExpr(statementNode.expr, symbolTable, (statCTX as WACCParser.ExitContext).expr())
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
                    return
                }
                val funcReturnNodeType = symbolTable.getNode(functionNode.toString())
                val valid = checkExprInSymbolTable(statementNode.expr, symbolTable)
                if (!valid && !isExprHardCoded(statementNode.expr)) {
                    println("SEMANTIC ERROR --- Return value not in scope")
                    semanticErrorDetected()
                    return
                }
                val returnNodeType = getTypeOfExpr(statementNode.expr, symbolTable, (statCTX as WACCParser.ReturnContext).expr())
                if (returnNodeType == null) {
                    println("SEMANTIC ERROR --- Return type issue")
                    semanticErrorDetected()
                    return
                }
                val func = funcReturnNodeType!!::class
                val ret = returnNodeType::class
                if (func != ret) {
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
                val lhsTypeNode = getLHSType(statementNode.lhs, symbolTable, (statCTX as WACCParser.ReadContext).assign_lhs())
                if (lhsTypeNode == null || (!equalTypes(lhsTypeNode, Int())) || (!equalTypes(lhsTypeNode, Chr()))) {
                    println("SEMANTIC ERROR --- LHS must be of type int or chr variable or array for READ")
                    semanticErrorDetected()
                }
            }
            // Checks the lhs and rhs of the declaration are equal
            is DeclarationNode -> {
                val rhsTypeNode = getRHSType(statementNode.value, symbolTable, (statCTX as WACCParser.VarDeclarationContext).assign_rhs())
                if (rhsTypeNode == null || !equalTypes(statementNode.type, rhsTypeNode)) {
                    println("SEMANTIC ERROR --- LHS type != RHS Type")
                    semanticErrorDetected()
                }
            }
            // Checks the lhs and rhs of the assignment are equal
            is AssignNode -> {
                val lhsTypeNode = getLHSType(statementNode.lhs, symbolTable, (statCTX as WACCParser.VarAssignContext).assign_lhs())
                val rhsTypeNode = getRHSType(statementNode.rhs, symbolTable, statCTX.assign_rhs())
                if (lhsTypeNode == null || rhsTypeNode == null || !equalTypes(lhsTypeNode, rhsTypeNode)) {
                    println("SEMANTIC ERROR --- LHS type != RHS Type")
                    semanticErrorDetected()
                }
            }
        }
    }

    // Checks the stat within a function
    private fun checkFunc(functionNode: FunctionNode, funcCTX: WACCParser.FuncContext) {
        checkStat(functionNode.stat, functionNode.functionSymbolTable, functionNode, funcCTX.stat())
    }
 */
}
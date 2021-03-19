package AST

import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor
import org.antlr.v4.runtime.ParserRuleContext
import AST.Types.*
import AST.Types.Type
import AST.Types.Type.Companion.binaryOpsProduces
import AST.Types.Type.Companion.binaryOpsRequires
import AST.Types.Type.Companion.unaryOpsProduces
import AST.Types.Type.Companion.unaryOpsRequires
import ErrorHandler.SemanticErrorHandler
import ErrorHandler.SyntaxErrorHandler
import compiler.AST.Types.*
import java.util.concurrent.atomic.AtomicInteger


class ASTBuilder(
        private val semanticListener: SemanticErrorHandler,
        private val syntaxHandler: SyntaxErrorHandler,
        private var globalSymbolTable: SymbolTable,
        // A map to store all the functions and their parameters for semantic checking
        private val functionParameters: LinkedHashMap<String, List<Type>> = linkedMapOf(),
        private val classParameters: LinkedHashMap<String, List<Type>> = linkedMapOf()
) : WACCParserBaseVisitor<Node>() {
    private var nextSymbolID = AtomicInteger()
    private var inWhile = false

    // A map to store all the functions and their parameters for semantic checking
    private val structLists: LinkedHashMap<Ident, TypeStruct> = linkedMapOf()
    private val classLists: LinkedHashMap<Ident, TypeClass> = linkedMapOf()
    private val classNodes = mutableListOf<ClassNode>()


    // A flag to know if we want the type a boolean returns or requires
    private var boolTypeResult = false

    /*
    =================================================================
                                PROGRAM
    =================================================================
     */

    // Visits the main program to build the AST
    override fun visitProgram(ctx: ProgramContext): Node {
        val structNodes = visitAllStructs(ctx.struct())
        val classNodes = visitAllClasses(ctx.classs())

        // First add all the functions to the map
        addAllMacros(ctx.macro())
        addAllFunctions(ctx.func())

        // Visit each macro, function and the global stat
        val functionNodes = mutableListOf<FunctionNode>()
        ctx.macro().map { functionNodes.add(visit(it) as FunctionNode) }
        ctx.func().map { functionNodes.add(visit(it) as FunctionNode) }
        val stat = visit(ctx.stat()) as StatementNode

        return ProgramNode(structNodes, classNodes, functionNodes, stat)
    }

    /*
    =================================================================
                                CLASSES
    =================================================================
     */

    private fun visitAllClasses(classes: List<ClasssContext>): List<ClassNode> {
        val classNodes = mutableListOf<ClassNode>()
        classes.map { classNodes.add(visit(it) as ClassNode) }
        return classNodes
    }

    override fun visitClasss(ctx: ClasssContext): Node {
        val prevCounter = nextSymbolID
        val prevST = globalSymbolTable
        val classST = SymbolTable(null, -1)
        nextSymbolID = AtomicInteger()
        globalSymbolTable = classST

        val ident = visit(ctx.ident()) as Ident
        val classType = TypeClass(ident)

        val parameterNodes = mutableListOf<Param>()
        val parameterTypes = mutableListOf<Type>()
        if (ctx.param_list() != null) {
            for (i in 0..ctx.param_list().childCount step 2) {
                val param = visit(ctx.param_list().getChild(i)) as Param
                parameterNodes.add(param)
                globalSymbolTable.addNode(param.ident.toString(), param.type.type.setParameter(true))
                parameterTypes.add(param.type.type)
                classType.addMember(param.ident, param.type.type)
            }
        }

        classParameters[ident.toString()] = parameterTypes

        val membersList = mutableListOf<ClassMember>()
        ctx.class_member().map { membersList.add(visit(it) as ClassMember) }
        for (memb in membersList) {
            if (memb is InitMember) {
                classType.addMember(memb.memb.ident, memb.memb.type.type)
            } else if (memb is NonInitMember) {
                classType.addMember(memb.memb.ident, memb.memb.type.type)
            }
        }

        val functionList = mutableListOf<FunctionNode>()
        ctx.func().map { functionList.add(visit(it) as FunctionNode) }
        ctx.func().forEach { addIndividual(it.ident(), it.type(), it.param_list(), it) }

        classType.setST(globalSymbolTable)
        classLists[ident] = classType

        globalSymbolTable = prevST
        nextSymbolID = prevCounter

        return ClassNode(ident, parameterNodes, membersList, functionList, classType)
    }

    override fun visitClass_member(ctx: Class_memberContext): Node {
        return when {
            ctx.member() != null -> {
                val ident = visit(ctx.member().ident()) as Ident
                val type = visit(ctx.member().type()) as TypeNode
                if (globalSymbolTable.containsNodeLocal(ident.toString())) {
                    semanticListener.redefinedVariable(ident.name, ctx)
                } else {
                    globalSymbolTable.addNode(ident.toString(), type.type)
                }
                NonInitMember(MemberNode(type, ident))
            }
            ctx.declare_var() != null -> {
                val d = visit(ctx.declare_var()) as DeclarationNode
                if (d.value is RHSExprNode && d.value.expr is Ident) {
                    NonInitMember(MemberNode(d.type, d.ident))

                } else {
                    InitMember(d)
                }
            }
            else -> throw Error("Not implemented")
        }
    }

    /*
    =================================================================
                                STRUCTS
    =================================================================
     */

    private fun visitAllStructs(structs: List<StructContext>): List<StructNode> {
        val structNodes = mutableListOf<StructNode>()
        structs.map { structNodes.add(visit(it) as StructNode) }
        return structNodes
    }

    override fun visitStruct(ctx: StructContext): Node {
        val members = mutableListOf<MemberNode>()

        val structIdent = visit(ctx.ident()) as Ident
        val structType = TypeStruct(structIdent)

        ctx.member().map {
            val ident = visit(it.ident()) as Ident
            val type = visit(it.type()) as TypeNode
            structType.addMember(ident, type.type)
            members.add(MemberNode(type, ident))
        }

        val structNode = StructNode(structIdent, members, structType)

        structLists[structIdent] = structType

        return structNode
    }

    /*
    =================================================================
                               FUNCTIONS
    =================================================================
     */
    private fun addIndividual(id: IdentContext, t: TypeContext, p: Param_listContext?, ctx: ParserRuleContext) {
        val ident = visit(id) as Ident // Function name
        val type = visit(t) as TypeNode // Function return type

        // Add each parameter to the function's parameter list in the map
        val parameterTypes = mutableListOf<Type>()
        if (p != null) {
            for (i in 0..p.childCount step 2) {
                val param = visit(p.getChild(i)) as Param
                parameterTypes.add(param.type.type)
            }
        }
        val funcType = TypeFunction(type.type, parameterTypes)

        if (globalSymbolTable.containsNodeLocal(ident.name + funcType.toString())) {
            semanticListener.redefinedVariable(ident.name + "()", ctx)
        } else {
            globalSymbolTable.addNode(ident.name + funcType.toString(), funcType)
        }
    }

    private fun addAllMacros(macroCTXs: MutableList<MacroContext>) {
        macroCTXs.forEach { addIndividual(it.ident(), it.type(), it.param_list(), it) }
    }

    override fun visitMacro(ctx: MacroContext): Node {
        return addSingleFunction(ctx.type(), ctx.ident(), ctx.param_list(), null, ctx.expr(), ctx)
    }


    /*
    =================================================================
                               FUNCTIONS
    =================================================================
     */

    // Visits each function and adds it to the global symbol table
    private fun addAllFunctions(funcCTXs: MutableList<FuncContext>) {
        funcCTXs.forEach { addIndividual(it.ident(), it.type(), it.param_list(), it) }
    }

    // Visit a function for the AST
    override fun visitFunc(ctx: FuncContext): Node {
        return addSingleFunction(ctx.type(), ctx.ident(), ctx.param_list(), ctx.stat(), null, ctx)
    }

    private fun addSingleFunction(t: TypeContext, id: IdentContext, p: Param_listContext?, s: StatContext?, e: ExprContext?, ctx: ParserRuleContext, classFunc: Boolean = false): FunctionNode {
        // Create a new scope for the function
        val functionSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())

        val ident = visit(id) as Ident // Function name
        val type = visit(t) as TypeNode // Return type

        // Add each parameter to the function's symbol table
        val parameterNodes = mutableListOf<Param>()
        val parameterTypes = mutableListOf<Type>()
        if (p != null) {
            for (i in 0..p.childCount step 2) {
                val param = visit(p.getChild(i)) as Param
                parameterNodes.add(param)
                functionSymbolTable.addNode(param.ident.toString(), param.type.type.setParameter(true))
                parameterTypes.add(param.type.type)
            }
        }

        // Add the function's return type to the table too
        functionSymbolTable.addNode("\$RET", type.type.setReturn(true))
        val funcType = TypeFunction(type.type, parameterTypes)
        ident.name = ident.name + funcType.toString()
        functionParameters[ident.name] = parameterTypes

        if (!globalSymbolTable.containsNodeLocal(ident.name)) {
            globalSymbolTable.addChildTable(functionSymbolTable.ID, functionSymbolTable)
        }

        // Assign the current scope to the scope of the function when building its statement node
        val stat: StatementNode
        globalSymbolTable = functionSymbolTable
        if (s != null) {
            stat = visit(s) as StatementNode
            if (!stat.valid() && type !is VoidType) {
                syntaxHandler.addSyntaxError(ctx, "return type of function invalid")
            }
        } else {
            val expr = visit(e) as ExprNode
            val exprType = getExprType(expr, ctx)
            if (exprType != type.type) {
                semanticListener.incompatibleTypeReturn(type.type.toString(), exprType.toString(), ctx)
            }
            stat = ReturnNode(expr)
        }

        // Revert back to the global scope
        globalSymbolTable = globalSymbolTable.parentT!!

        return FunctionNode(type, ident, parameterNodes.toList(), stat)
    }

    /*
    =================================================================
                             PARAMETERS
    =================================================================
     */

    override fun visitParam(ctx: ParamContext): Node {
        val type = visit(ctx.type()) as TypeNode
        val ident = visit(ctx.ident()) as Ident
        return Param(type, ident)
    }

    override fun visitParam_list(ctx: Param_listContext?): Node {
        return visitChildren(ctx)
    }

    private fun checkClassParameters(newClass: RHSNewClass, ctx: ParserRuleContext): Boolean {
        // Checks all the arguments being passed into a function so that all the types match up
        val parameterTypes = classParameters[newClass.className.toString()]
        if (newClass.argList == null && parameterTypes!!.isEmpty()) {
            // Check if there are parameters
            return true
        } else if (newClass.argList!!.size != parameterTypes!!.size) {
            // Check they are the same size
            semanticListener.mismatchedArgs(parameterTypes.size.toString(), newClass.argList.size.toString(), ctx)
            return false
        }
        // Get the type of each argument
        val argTypes = mutableListOf<Type>()
        for (arg in newClass.argList) {
            val type = getExprType(arg, ctx)
            if (type == null) {
                return false
            } else {
                argTypes.add(type)
            }
        }

        // Check each argument matches with the type of the parameter
        for (i in argTypes.indices) {
            if (argTypes[i].getType() != parameterTypes[i].getType()) {
                semanticListener.mismatchedParamTypes(
                        argTypes[i].toString(),
                        parameterTypes[i].toString(),
                        ctx
                )
                return false
            }
        }
        return true
    }

    private fun checkParameters(rhs: RHSCallNode, ctx: ParserRuleContext): Boolean {
        // Checks all the arguments being passed into a function so that all the types match up
        val parameterTypes = functionParameters[rhs.ident.name]
        if (rhs.argList == null && parameterTypes!!.isEmpty()) {
            // Check if there are parameters
            return true
        } else if (rhs.argList!!.size != parameterTypes!!.size) {
            // Check they are the same size
            semanticListener.mismatchedArgs(parameterTypes.size.toString(), rhs.argList.size.toString(), ctx)
            return false
        }
        // Get the type of each argument
        val argTypes = mutableListOf<Type>()
        for (arg in rhs.argList) {
            val type = getExprType(arg, ctx)
            if (type == null) {
                return false
            } else {
                argTypes.add(type)
            }
        }

        // Check each argument matches with the type of the parameter
        for (i in argTypes.indices) {
            if (argTypes[i].getType() != parameterTypes[i].getType()) {
                semanticListener.mismatchedParamTypes(
                        argTypes[i].toString(),
                        parameterTypes[i].toString(),
                        ctx
                )
                return false
            }
        }
        return true
    }

    /*
    ================================================================
                              STATEMENTS
    =================================================================
     */

    override fun visitCall(ctx: CallContext): Node {
        val ident = visit(ctx.ident()) as Ident
        val params = when {
            ctx.arg_list() != null -> ctx.arg_list().expr().map { visit(it) as ExprNode }
            else -> null
        }
        //checkParameters(RHSCallNode(ident, params), ctx)
        val args = params?.map { x -> getExprType(x, ctx) } ?: mutableListOf<Type>()
        val string = "${ident.name}($args)"
        var found = false
        if (!globalSymbolTable.containsNodeGlobal(string)) {
            if (args.contains(TypePair(null, null))) {
                val funcKeys = globalSymbolTable.filterFuncs(ident.toString())
                for (value in funcKeys.values) {
                    if (value is TypeFunction && TypeFunction(value.getReturn(), (args as MutableCollection<Type>)) == value) {
                        found = true
                        break
                    }
                }
            }
            if (!found) {
                semanticListener.funRefBeforeAss(ident.name, ctx)
            }
        }

        return CallNode(ident, params)
    }

    override fun visitSequence(ctx: SequenceContext): Node {
        return SequenceNode(sequenceList(ctx))
    }

    override fun visitMap(ctx: MapContext): Node {
        val functionIdent = visit(ctx.ident(0)) as Ident
        val arrayIdent = visit(ctx.ident(1)) as Ident

        var args = if (ctx.arg_list() != null) ctx.arg_list().expr().map { visit(it) as ExprNode } as MutableList<ExprNode> else null

        // Check the array exists
        if (!globalSymbolTable.containsNodeGlobal(arrayIdent.toString())) {
            semanticListener.undefinedVar(arrayIdent.toString(), ctx)
        } else if (!globalSymbolTable.getNodeGlobal(arrayIdent.toString())!!.getArray()) {
            semanticListener.mapOperatesOnArray(ctx)
        }

        // Create a counter
        val counterVar = Ident("&map_counter")
        globalSymbolTable.addNode(counterVar.toString(), TypeBase(INT))
        val arraySizeVar = Ident("&map_length")
        globalSymbolTable.addNode(arraySizeVar.toString(), TypeBase(INT))

        // Create the symbol table for the while node
        val mapSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        globalSymbolTable = mapSymbolTable

        val size = DeclarationNode(Int(), arraySizeVar, RHSExprNode(UnaryOpNode(UnOp.LEN, arrayIdent)))
        val counter = DeclarationNode(Int(), counterVar, RHSExprNode(IntLiterNode("0")))
        val lhsAssign = LHSArrayElemNode(ArrayElem(arrayIdent, listOf(counterVar)))
        // Make a call node and check the parameters
        if (args == null) {
            args = mutableListOf(ArrayElem(arrayIdent, listOf(counterVar)))
        } else {
            args.add(0, ArrayElem(arrayIdent, listOf(counterVar)))
        }
        val rhsCall = RHSCallNode(functionIdent, args)
        // Check the function exists
        if (!globalSymbolTable.containsNodeGlobal(functionIdent.toString())) {
            semanticListener.funRefBeforeAss(functionIdent.name, ctx)
        } else if (globalSymbolTable.getNodeGlobal(functionIdent.toString()) != getExprType(arrayIdent, ctx)!!.getBaseType()) {
            semanticListener.mapReturnTypeIncorrect(getExprType(arrayIdent, ctx)!!.getBaseType().toString(), globalSymbolTable.getNodeGlobal(functionIdent.toString()).toString(), ctx)
        } else {
            checkParameters(rhsCall, ctx)
        }
        val body1 = AssignNode(lhsAssign, rhsCall)
        val body2 = SideExpressionNode(AssignLHSIdentNode(counterVar), AddOneNode())
        val whileSeq = SequenceNode(mutableListOf(body1, body2))
        val whileLoop = WhileNode(BinaryOpNode(BinOp.LT, counterVar, arraySizeVar), whileSeq)

        globalSymbolTable = globalSymbolTable.parentT!!

        val sequence = mutableListOf(counter, size, whileLoop)

        return SequenceNode(sequence)
    }

    override fun visitBin_op(ctx: Bin_opContext): Node {
        return when {
            ctx.MUL() != null -> BinOp.MUL
            ctx.DIV() != null -> BinOp.DIV
            ctx.PLUS() != null -> BinOp.PLUS
            ctx.MINUS() != null -> BinOp.MINUS
            ctx.AND() != null -> BinOp.AND
            ctx.OR() != null -> BinOp.OR
            ctx.BITWISEAND() != null -> BinOp.BITWISEAND
            ctx.BITWISEOR() != null -> BinOp.BITWISEOR
            else -> BinOp.NOT_SUPPORTED
        }
    }

    private fun typeToNode(type: Type): TypeNode {
        return when (type) {
            TypeBase(INT) -> Int()
            TypeBase(BOOL) -> Bool()
            TypeBase(CHAR) -> Chr()
            else -> Str()
        }
    }

    private fun visitFold(ident: IdentContext, bin_op: Bin_opContext, expr: ExprContext, ctx: ParserRuleContext, left: Boolean = false): Node {
        val arrayIdent = visit(ident) as Ident
        val operator = visit(bin_op) as BinOp
        val startValue = visit(expr) as ExprNode
        val array = globalSymbolTable.getNodeGlobal(arrayIdent.toString())
        if (!globalSymbolTable.containsNodeGlobal(arrayIdent.toString())) {
            semanticListener.undefinedVar(arrayIdent.toString(), ctx)
        } else if (!binaryOpsRequires(operator.value).contains(array!!.getBaseType())) {
            semanticListener.binaryOpType(ctx)
        } else if (!binaryOpsRequires(operator.value).contains(getExprType(startValue, ctx))) {
            semanticListener.binaryOpType(ctx)
        }

        // Create a counter
        val counterVar = Ident("&fold_counter")
        globalSymbolTable.addNode(counterVar.toString(), TypeBase(INT))
        val totalVar = Ident("&fold_total")
        globalSymbolTable.addNode(totalVar.toString(), array!!.getBaseType())
        val arraySizeVar = Ident("&fold_length")
        globalSymbolTable.addNode(arraySizeVar.toString(), TypeBase(INT))

        // Create the symbol table for the while node
        val mapSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        globalSymbolTable = mapSymbolTable

        val size = DeclarationNode(Int(), arraySizeVar, RHSExprNode(UnaryOpNode(UnOp.LEN, arrayIdent)))

        val counter = if (left) {
            DeclarationNode(Int(), counterVar, RHSExprNode(IntLiterNode("0")))
        } else {
            val counterInit = BinaryOpNode(BinOp.MINUS, arraySizeVar, IntLiterNode("1"))
            DeclarationNode(Int(), counterVar, RHSExprNode(counterInit))
        }

        val type = getExprType(startValue, ctx)
        val total = DeclarationNode(typeToNode(type!!), totalVar, RHSExprNode(startValue))

        val lhsAssign = AssignLHSIdentNode(totalVar)
        val rhsOp = if (left) {
            RHSExprNode(BinaryOpNode(operator, totalVar, ArrayElem(arrayIdent, listOf(counterVar))))
        } else {
            RHSExprNode(BinaryOpNode(operator, ArrayElem(arrayIdent, listOf(counterVar)), totalVar))
        }

        val body1 = AssignNode(lhsAssign, rhsOp)
        val body2 = if (left) {
            SideExpressionNode(AssignLHSIdentNode(counterVar), AddOneNode())
        } else {
            SideExpressionNode(AssignLHSIdentNode(counterVar), SubOneNode())
        }
        val whileSeq = SequenceNode(mutableListOf(body1, body2))
        val cond = if (left) {
            BinaryOpNode(BinOp.LT, counterVar, arraySizeVar)
        } else {
            BinaryOpNode(BinOp.GTE, counterVar, IntLiterNode("0"))
        }
        val whileLoop = WhileNode(cond, whileSeq)

        globalSymbolTable = globalSymbolTable.parentT!!

        val sequence = mutableListOf(size, counter, total, whileLoop)

        return RHSFoldNode(SequenceNode(sequence))
    }

    override fun visitAssignRhsFoldl(ctx: AssignRhsFoldlContext): Node {
        return visitFold(ctx.ident(), ctx.bin_op(), ctx.expr(), ctx, true)
    }

    override fun visitAssignRhsFoldr(ctx: AssignRhsFoldrContext): Node {
        return visitFold(ctx.ident(), ctx.bin_op(), ctx.expr(), ctx)
    }

    private fun sequenceList(ctx: SequenceContext): MutableList<StatementNode> {
        //visit head first so any variables will be added to the symbol table
        val head = visit(ctx.stat(0)) as StatementNode
        val tail = when (ctx.stat(1)) {
            is SequenceContext -> sequenceList(ctx.stat(1) as SequenceContext)
            else -> mutableListOf(visit(ctx.stat(1)) as StatementNode)
        }
        tail.add(0, head)
        return tail
    }

    override fun visitVarAssign(ctx: VarAssignContext): Node {
        val lhs = visit(ctx.assign_lhs()) as AssignLHSNode
        val rhs = visit(ctx.assign_rhs()) as AssignRHSNode

        val lhsType = getLHSType(lhs, ctx.assign_lhs())

        boolTypeResult = true
        val rhsType = getRHSType(rhs, ctx)
        boolTypeResult = false
        if (lhsType != null) {
            // Check that the types are equal for all different cases (variable, array or pair)
            if (lhsType.getType() != rhsType!!.getType() && !(lhsType.getArray() && rhsType.getType() == TypeBase(EMPTY_ARR).getType())
                    && !(lhsType.getPair() && rhsType.getType() == TypePair(null, null).getType())
            ) {
                semanticListener.incompatibleTypeAss(lhsType.toString(), rhsType.toString(), ctx)
            }
        }

        return AssignNode(lhs, rhs)
    }

    override fun visitBreak(ctx: BreakContext): Node {
        if (!inWhile) {
            syntaxHandler.addSyntaxError(ctx, "Break outside of If statement or While loop")
        }
        return BreakNode()
    }

    override fun visitContinue(ctx: ContinueContext): Node {
        if (!inWhile) {
            syntaxHandler.addSyntaxError(ctx, "Continue outside of If statement or While loop")
        }
        return ContinueNode()
    }

    override fun visitSideExpression(ctx: SideExpressionContext): Node {
        val ident = visit(ctx.assign_lhs()) as AssignLHSIdentNode
        val lhsType = getLHSType(ident, ctx.assign_lhs())
        if (lhsType != TypeBase(INT)) {
            semanticListener.incompatibleTypeSideExpr(lhsType.toString(), ctx)
        }

        val operator = visit(ctx.sideExpr()) as SideExprOperator
        return SideExpressionNode(ident, operator)
    }

    override fun visitSideOperator(ctx: SideOperatorContext): Node {
        return when {
            ctx.nunOp() != null -> visit(ctx.nunOp())
            else -> visit(ctx.opN())
        }
    }

    override fun visitNunOp(ctx: NunOpContext): Node {
        return when {
            ctx.MINUS() != null -> AddOneNode()
            else -> SubOneNode()
        }
    }

    override fun visitOpN(ctx: OpNContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        val type = getExprType(expr, ctx)
        if (type != TypeBase(INT)) {
            semanticListener.incompatibleTypeSideExpr(type.toString(), ctx)
        }
        return when {
            ctx.ADDN() != null -> AddNNode(expr)
            ctx.SUBN() != null -> SubNNode(expr)
            ctx.MULN() != null -> MulNNode(expr)
            else -> DivNNode(expr)
        }
    }


    override fun visitRead(ctx: ReadContext): Node {
        // Get the type of the variable
        val lhsNode = visit(ctx.assign_lhs()) as AssignLHSNode
        val type = when (lhsNode) {
            is LHSPairElemNode -> getPairElemType(lhsNode.pairElem, ctx)
            is LHSArrayElemNode -> getExprType(lhsNode.arrayElem, ctx)
            else -> {
                if (lhsNode !is AssignLHSIdentNode) {
                    // The read value must go into a variable
                    semanticListener.readNotVariable(ctx)
                }
                if (!globalSymbolTable.containsNodeGlobal((lhsNode as AssignLHSIdentNode).ident.toString())) {
                    semanticListener.undefinedVar(lhsNode.ident.name, ctx)
                }
                globalSymbolTable.getNodeGlobal(lhsNode.ident.toString())
            }
        }

        // Read can only be int or char
        if (!(type == TypeBase(INT) || type == TypeBase(CHAR))) {
            semanticListener.readTypeError(type.toString(), ctx)

        }
        return ReadNode(lhsNode)
    }

    override fun visitExit(ctx: ExitContext): Node {
        // An exit value must be an integer
        val expr = visit(ctx.expr()) as ExprNode
        if (getExprType(expr, ctx.expr()) != TypeBase(INT)) {
            semanticListener.incompatibleExitCode(ctx.expr().text, getExprType(expr, ctx.expr()).toString(), ctx)
        }
        return ExitNode(expr)
    }

    override fun visitFree(ctx: FreeContext): Node {
        // The freed variable must be a pair
        val freedExpr = visit(ctx.expr()) as ExprNode
        val freeType = getExprType(freedExpr, ctx.expr())
        if (freeType == null || !freeType.getPair()) {
            semanticListener.incompatibleTypeFree(freeType.toString(), ctx)
        }
        return FreeNode(freedExpr)
    }

    override fun visitReturn(ctx: ReturnContext): Node {
        // Check the return is inside a function, not global
        if (globalSymbolTable.parentT == null) {
            semanticListener.returnFromGlobal(ctx)
        }

        // Check the expected type is equal to the function's return type
        val exprType = visit(ctx.expr()) as ExprNode
        boolTypeResult = true
        val returnType = getExprType(exprType, ctx.expr())
        boolTypeResult = false
        val expected = globalSymbolTable.getNodeGlobal("\$RET")

        if (expected != returnType || expected == TypeBase(VOID)) {
            semanticListener.incompatibleTypeReturn(expected.toString(), returnType.toString(), ctx)
        }
        return ReturnNode(exprType)
    }


    // Check that if we are printing a variable, it exists
    private fun checkPrint(expr: ExprNode, ctx: ParserRuleContext) {
        if (expr is Ident && !globalSymbolTable.containsNodeGlobal(expr.toString())) {
            semanticListener.undefinedVar(expr.name, ctx)
        }
    }

    override fun visitPrintln(ctx: PrintlnContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        checkPrint(expr, ctx)
        return PrintlnNode(expr)
    }

    override fun visitPrint(ctx: PrintContext): Node {
        val expr = visit(ctx.expr()) as ExprNode
        checkPrint(expr, ctx)
        return PrintNode(expr)
    }

    override fun visitSkip(ctx: SkipContext): Node {
        return SkipNode()
    }

    // Gets the expression node of a conditional context
    private fun getConditionExpression(expr: ExprContext, ctx: ParserRuleContext): ExprNode {
        boolTypeResult = true
        val condExpr = visit(expr) as ExprNode
        // Checks that it is of type bool
        if ((condExpr is Ident && !globalSymbolTable.containsNodeGlobal(condExpr.toString())) || getExprType(condExpr, expr) != TypeBase(BOOL)) {
            semanticListener.conditionalBoolean(getExprType(condExpr, expr).toString(), ctx)
        }

        boolTypeResult = false
        return condExpr
    }

    override fun visitIf(ctx: IfContext): Node {
        val condExpr = getConditionExpression(ctx.expr(), ctx)

        // Create new scope for each branch of the conditional to make sure there are no scoping issues
        // Then traverse down either side
        val ifSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        globalSymbolTable = ifSymbolTable
        val stat1 = visit(ctx.stat(0)) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!

        val elseIfs = mutableListOf<ElseIfNode>()
        if (ctx.else_if().isNotEmpty()) {
            ctx.else_if().map { elseIfs.add(visit(it) as ElseIfNode) }
        }

        var stat2: StatementNode? = null
        if (ctx.stat(1) != null) {
            val elseSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
            globalSymbolTable = elseSymbolTable
            stat2 = visit(ctx.stat(1)) as StatementNode
            globalSymbolTable = globalSymbolTable.parentT!!
        }

        return IfElseNode(condExpr, stat1, elseIfs, stat2)
    }

    override fun visitElse_if(ctx: Else_ifContext): Node {
        val condExpr = getConditionExpression(ctx.expr(), ctx)
        val elseIfST = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        globalSymbolTable = elseIfST
        val stat = visit(ctx.stat()) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!

        return ElseIfNode(condExpr, stat)

    }

    override fun visitWhile(ctx: WhileContext): Node {
        inWhile = true
        val condExpr = getConditionExpression((ctx.expr()), ctx)

        // Create a new scope for the loop
        val loopSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        globalSymbolTable = loopSymbolTable
        val stat = visit(ctx.stat()) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!

        inWhile = false
        return WhileNode(condExpr, stat)
    }

    override fun visitDo_while(ctx: Do_whileContext): Node {
        inWhile = true
        // Create a new scope for the loop
        val loopSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        globalSymbolTable = loopSymbolTable
        val stat = visit(ctx.stat()) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!

        val condExpr = getConditionExpression((ctx.expr()), ctx)

        inWhile = false
        return DoWhileNode(stat, condExpr)
    }

    override fun visitBegin(ctx: BeginContext): Node {
        // Create a new scope for each begin
        val scopeSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        globalSymbolTable = scopeSymbolTable
        val stat = visit(ctx.stat()) as StatementNode
        globalSymbolTable = globalSymbolTable.parentT!!
        return BeginEndNode(stat)
    }

    // Checks that the LHS type = RHS type and creates an AST node
    override fun visitVarDeclaration(ctx: VarDeclarationContext): Node {
        return visit(ctx.declare_var())
    }

    private fun checkForCondSemantics(cond: ExprNode, ctx: ParserRuleContext) {
        if (cond !is BinaryOpNode || binaryOpsProduces(cond.operator.value) != TypeBase(BOOL)) {
            semanticListener.forLoopCond(ctx)
        } else {
            val expr1Type = getExprType(cond.expr1, ctx)
            if (!binaryOpsRequires(cond.operator.value).contains(expr1Type)) {
                semanticListener.binaryOpType(ctx)
            }
            val expr2Type = getExprType(cond.expr2, ctx)
            if (!binaryOpsRequires(cond.operator.value).contains(expr2Type)) {
                semanticListener.binaryOpType(ctx)
            }
        }
    }

    private fun checkUpdateSemantics(update: StatementNode, ctx: ParserRuleContext) {
        if (update !is SideExpressionNode && update !is AssignNode) {
            semanticListener.forLoopUpdate(ctx)
        }
    }

    override fun visitFor_loop(ctx: For_loopContext): Node {
        val loopSymbolTable = SymbolTable(globalSymbolTable, nextSymbolID.incrementAndGet())
        val counter = visit(ctx.for_cond().declare_var()) as DeclarationNode
        globalSymbolTable = loopSymbolTable
        val terminator = visit(ctx.for_cond().expr()) as ExprNode
        val update = visit(ctx.for_cond().stat()) as StatementNode
        val do_ = visit(ctx.stat()) as StatementNode
        checkForCondSemantics(terminator, ctx)
        checkUpdateSemantics(update, ctx)

        globalSymbolTable = globalSymbolTable.parentT!!
        return ForNode(counter, update, terminator, do_)
    }

    override fun visitDeclare_var(ctx: Declare_varContext): Node {
        val type = visit(ctx.type()) as TypeNode
        val ident = Ident(ctx.ident().text)
        val rhs = visit(ctx.assign_rhs()) as AssignRHSNode
        // Check that the variable named exists and is not a function
        if (!boolTypeResult && globalSymbolTable.containsNodeLocal(ident.toString()) && globalSymbolTable.containsNodeLocal(ident.toString())
                && !globalSymbolTable.getNodeLocal(ident.toString())!!.isFunction()
        ) {
            semanticListener.redefinedVariable(ident.name, ctx)
        } else {
            globalSymbolTable.addNode(ident.toString(), type.type)
        }

        val lhsType = type.type
        boolTypeResult = true
        val rhsType = getRHSType(rhs, ctx)
        boolTypeResult = false

        // Check each side's type is equal
        if (rhsType != null) {
            if (lhsType.getType() != rhsType.getType()
                    && !(lhsType.getArray() && rhsType.getType() == TypeBase(EMPTY_ARR).getType())
                    && !(lhsType.getPair() && rhsType.getType() == PAIR_LITER)
            ) {
                semanticListener.incompatibleTypeDecl(ident.name, lhsType.toString(), rhsType.toString(), ctx)
            }
            if (lhsType.getType() == AST.Types.STRUCT || rhsType.getType() == AST.Types.STRUCT) {
                if (lhsType != rhsType) {
                    semanticListener.incompatibleTypeDecl(ident.name, lhsType.toString(), rhsType.toString(), ctx)
                }
            }
        }

        return DeclarationNode(type, ident, rhs)
    }

    /*
    ================================================================
                               TYPES
    ================================================================
     */

    override fun visitType(ctx: TypeContext): Node {
        when {
            ctx.base_type() != null -> visit(ctx.base_type())
            ctx.OPEN_SQUARE() != null -> return ArrayNode(visit(ctx.type()) as TypeNode)
            ctx.pair_type() != null -> return visit(ctx.pair_type())
            ctx.void_type() != null -> return VoidType()
            ctx.struct_type() != null -> return visit(ctx.struct_type())
        }

        return visitChildren(ctx)
    }

    override fun visitStruct_type(ctx: Struct_typeContext): Node {
        val ident = visit(ctx.ident()) as Ident

        val sType = structLists[ident]
        val cType = classLists[ident]
        return if (sType != null && cType != null) {
            semanticListener.objectNotImplemented(ident.name, ctx)
            VoidType()
        } else if (sType != null) {
            StructType(sType)
        } else {
            ClassType(cType!!)
        }

    }

    override fun visitBaseT(ctx: BaseTContext): Node {
        return when {
            ctx.INT() != null -> Int()
            ctx.BOOL() != null -> Bool()
            ctx.CHAR() != null -> Chr()
            else -> Str()
        }
    }

    override fun visitArray_type(ctx: Array_typeContext): Node {
        return ArrayNode(visit(ctx.type()) as TypeNode)
    }

    override fun visitPair_type(ctx: Pair_typeContext): Node {
        return PairTypeNode(
                visit(ctx.pair_elem_type(0)) as PairElemTypeNode,
                visit(ctx.pair_elem_type(1)) as PairElemTypeNode
        )
    }

    override fun visitPair_elem_type(ctx: Pair_elem_typeContext): Node {
        val type: Any = when {
            ctx.PAIR() != null -> Pair()
            ctx.array_type() != null -> visit(ctx.array_type())
            else -> visit(ctx.base_type())
        }
        return PairElemTypeNode(type as TypeNode)
    }

    private fun getPairElemType(pairElem: PairElemNode, ctx: ParserRuleContext): Type? {
        // Checks that the variable used is valid and defined, otherwise a semantic error is thrown
        // if there are no issues, it returns the type of the element
        val expr: ExprNode
        return if (pairElem::class == FstExpr::class) {
            expr = (pairElem as FstExpr).expr
            if (expr::class != Ident::class) {
                semanticListener.fstSndMustBePair(ctx)
                null
            } else if (!globalSymbolTable.containsNodeGlobal((expr as Ident).toString())) {
                semanticListener.undefinedVar(expr.name, ctx)
                null
            } else {
                globalSymbolTable.getNodeGlobal(expr.toString())!!.getPairFst()
            }
        } else {
            expr = (pairElem as SndExpr).expr
            if (expr::class != Ident::class) {
                semanticListener.fstSndMustBePair(ctx)
                null
            } else if (!globalSymbolTable.containsNodeGlobal((expr as Ident).toString())) {
                semanticListener.undefinedVar(expr.name, ctx)
                null
            } else {
                globalSymbolTable.getNodeGlobal(expr.toString())!!.getPairSnd()
            }
        }
    }

    // Case to check each possibility of node
    private fun getLHSType(lhs: AssignLHSNode, ctx: Assign_lhsContext): Type? {
        return when (lhs) {
            is AssignLHSIdentNode -> {
                // Check the type is valid and the variable is not a function name
                if (!globalSymbolTable.containsNodeGlobal(lhs.ident.toString())) {
                    semanticListener.undefinedVar(lhs.ident.name, ctx)
                } else if (globalSymbolTable.getNodeGlobal(lhs.ident.toString())!!.isFunction()) {
                    semanticListener.assigningFunction(lhs.ident.name, ctx)
                }
                globalSymbolTable.getNodeGlobal(lhs.ident.toString())
            }
            is AssignLHSClassNode -> {
                return getClassMembType(lhs.classMemberNode, ctx)
            }
            is AssignLHSStructNode -> {
                if (lhs.structMemberNode.memberExpr is ArrayElem) {
                    return getStructMembType(lhs.structMemberNode, ctx)!!.getBaseType()
                }
                return getStructMembType(lhs.structMemberNode, ctx)
            }
            is LHSArrayElemNode -> {
                // Check the array exists, the type is valid and the index is an integer
                if (!globalSymbolTable.containsNodeGlobal(lhs.arrayElem.ident.toString())) {
                    semanticListener.undefinedVar(lhs.arrayElem.ident.name, ctx)
                } else if (getExprType(lhs.arrayElem.expr[0], ctx) != TypeBase(INT)) {
                    semanticListener.arrayIndex("0", "INT", getExprType(lhs.arrayElem.expr[0], ctx).toString(), ctx)
                } else if (globalSymbolTable.getNodeGlobal(lhs.arrayElem.ident.toString())!!.getType() == STRING) {
                    semanticListener.indexStrings(ctx)
                }
                globalSymbolTable.getNodeGlobal(lhs.arrayElem.ident.toString())!!.getBaseType()
            }
            else -> {
                // Call the helper function to get the type of the pair elem
                getPairElemType((lhs as LHSPairElemNode).pairElem, ctx)
            }
        }
    }

    // Checks every element in the list is the same type
    private fun checkElemsSameType(name: String, exprs: List<ExprNode>, ctx: ParserRuleContext) {
        // No need if empty list
        if (exprs.isEmpty()) {
            return
        }
        // Get the first type as a reference type to compare all the others to
        val firstType = getExprType(exprs[0], ctx)
        for (i in exprs.indices) {
            // If the type cannot be found, something is wrong with the element
            if (getExprType(exprs[i], ctx) == null) {
                semanticListener.arrayIndex(i.toString(), getExprType(exprs[i], ctx).toString(), "NULL", ctx)
                break
                // If the elements type does not match the first then there is an error
            } else if (getExprType(exprs[i], ctx) != firstType) {
                semanticListener.arrayDifferingTypes(name, ctx)
                break
            }
        }
    }

    // Similar idea to getLHSType
    private fun getRHSType(rhs: AssignRHSNode, ctx: ParserRuleContext): Type? {
        return when (rhs) {
            is RHSExprNode -> {
                getExprType(rhs.expr, ctx)
            }
            is RHSCallNode -> {
                // Check the function exists and that the parameters are correct
                val args: List<Type>
                args = if (rhs.argList == null) {
                    mutableListOf(TypeBase(VOID))
                } else {
                    rhs.argList.map { x -> getExprType(x, ctx)!! }
                }
                val string = rhs.ident.name + args.joinToString(separator = "_").replace("[]","ARR")
                if (!globalSymbolTable.containsNodeGlobal(string)) {
                    if (args.contains(TypePair(null, null))) {
                        val funcKeys = globalSymbolTable.filterFuncs(rhs.ident.name)
                        for (value in funcKeys.values) {
                            if (value is TypeFunction && TypeFunction(value.getReturn(), (args as MutableCollection<Type>)) == value) {
                                return value.getReturn()
                            }
                        }
                    }
                    println("2")
                    semanticListener.funRefBeforeAss(rhs.ident.name, ctx)
                    null
                } else {
                    // Return the type of the function's return
                    (globalSymbolTable.getNodeGlobal(string) as TypeFunction).getReturn()
                }
            }
            is RHSClassCallNode -> {
                val prev = globalSymbolTable
                globalSymbolTable = (globalSymbolTable.getNodeGlobal(rhs.classIdent.toString())!! as TypeClass).getST()
                val type = getRHSType(rhs.callNode, ctx)
                globalSymbolTable = prev
                type
            }
            is RHSPairElemNode -> {
                getPairElemType(rhs.pairElem, ctx)
            }
            is RHSArrayLitNode -> {
                checkElemsSameType(rhs.toString(), rhs.exprs, ctx)
                if (rhs.exprs.isEmpty()) {
                    TypeBase(EMPTY_ARR)
                } else {
                    // Check the index is not null
                    val type = getExprType(rhs.exprs[0], ctx)
                    if (type == null) {
                        null
                    } else {
                        TypeArray(type)
                    }
                }
            }
            is RHSFoldNode -> {
                globalSymbolTable.getNodeGlobal(Ident("&fold_total").toString())
            }
            is RHSNewPairNode -> {
                // RHSNewPairElemNode
                var expr1 = getExprType(rhs.expr1, ctx)
                var expr2 = getExprType(rhs.expr2, ctx)

                if (expr1 != null) {
                    if (expr1.getType() == PAIR_LITER) {
                        expr1 = TypePair(null, null)
                    }
                }
                if (expr2 != null) {
                    if (expr2.getType() == PAIR_LITER) {
                        expr2 = TypePair(null, null)
                    }
                }

                when {
                    expr1 == null -> {
                        semanticListener.newPairFalse("1", ctx)
                        null
                    }
                    expr2 == null -> {
                        semanticListener.newPairFalse("2", ctx)
                        null
                    }
                    else -> TypePair(expr1, expr2)
                }
            }
            is RHSNewStruct -> {
                val type = structLists[rhs.structName]
                if (type == null) {
                    semanticListener.structNotImplemented(rhs.structName.name, ctx)
                    null
                } else {
                    type
                }
            }
            is RHSNewClass -> {
                val type = classLists[rhs.className]
                if (type == null) {
                    semanticListener.classNotImplemented(rhs.className.name, ctx)
                    null
                } else {
                    type
                }
            }
            else -> throw Error("RHS not implemented")
        }
    }

    /*
    ================================================================
                            EXPRESSIONS
    ================================================================
    */

    // Check is possible version of an expression and return its type
    private fun getExprType(expr: ExprNode, ctx: ParserRuleContext): Type? {
        return when (expr) {
            is IntLiterNode -> TypeBase(INT)
            is StrLiterNode -> TypeBase(STRING)
            is BoolLiterNode -> TypeBase(BOOL)
            is CharLiterNode -> TypeBase(CHAR)
            is HexLiterNode -> TypeBase(INT)
            is BinLiterNode -> TypeBase(INT)
            is OctLiterNode -> TypeBase(INT)
            is Ident -> {
                // Check the variable exists
                if (!globalSymbolTable.containsNodeGlobal(expr.toString())) {
                    semanticListener.undefinedVar(expr.name, ctx)
                    null
                } else {
                    globalSymbolTable.getNodeGlobal(expr.toString())
                }
            }

            is ArrayElem -> {
                // Check the index is correct and the types match
                if (getExprType(expr.expr[0], ctx) != TypeBase(INT)) {
                    semanticListener.arrayIndex("0", getExprType(expr.expr[0], ctx).toString(), "INT", ctx)
                } else if (!globalSymbolTable.containsNodeGlobal(expr.ident.toString())) {
                    semanticListener.undefinedVar(expr.ident.name, ctx)
                } else if (globalSymbolTable.getNodeGlobal(expr.ident.toString())!!.getType() == STRING) {
                    // Make sure we are not trying to index a string
                    semanticListener.indexStrings(ctx)
                }
                globalSymbolTable.getNodeGlobal(expr.ident.toString())!!.getBaseType()
            }
            // Depending on the flag we either want what the operator requires or produces
            is UnaryOpNode -> {
                if (boolTypeResult) {
                    unaryOpsProduces(expr.operator.value)
                } else {
                    unaryOpsRequires(expr.operator.value)
                }
            }
            is BinaryOpNode -> {
                if (boolTypeResult) {
                    binaryOpsProduces(expr.operator.value)
                } else {
                    val requires = binaryOpsRequires(expr.operator.value)
                    if (requires.contains(getExprType(expr.expr1, ctx)) || requires.contains(TypeBase(ANY))) {
                        binaryOpsProduces(expr.operator.value)
                    } else {
                        semanticListener.binaryOpType(ctx)
                        null
                    }
                }
            }
            is PairLiterNode -> TypePair(null, null)
            is StructMemberNode -> {
                return getStructMembType(expr, ctx)
            }
            is ClassMemberNode -> {
                return getClassMembType(expr, ctx)
            }

            else -> throw Error("Expression not implemented")
        }
    }

    private fun getClassMembType(expr: ClassMemberNode, ctx: ParserRuleContext): Type? {
        val classType = getExprType(expr.structIdent, ctx) as TypeClass?
        return if (classType == null) {
            semanticListener.structNotImplemented(expr.structIdent.name, ctx)
            null
        } else if (!classType.containsMember(expr.memberIdent)) {
            semanticListener.structMemberDoesNotExist(expr.structIdent.name, expr.memberIdent.name, ctx)
            null
        } else {
            classType.memberType(expr.memberIdent)
        }
    }

    private fun getStructMembType(expr: StructMemberNode, ctx: ParserRuleContext): Type? {
        val structType = getExprType(expr.structIdent, ctx) as TypeStruct?
        return if (structType == null) {
            semanticListener.structNotImplemented(expr.structIdent.name, ctx)
            null
        } else if (expr.memberExpr is ArrayElem) {
            if (!structType.containsMember(expr.memberExpr.ident)) {
                semanticListener.structMemberDoesNotExist(expr.structIdent.name, expr.memberExpr.ident.name, ctx)
                null
            } else {
                structType.memberType(expr.memberExpr.ident)
            }
        } else if (!structType.containsMember(expr.memberExpr as Ident)) {
            semanticListener.structMemberDoesNotExist(expr.structIdent.name, expr.memberExpr.name, ctx)
            null
        } else {
            structType.memberType(expr.memberExpr)
        }
    }

    override fun visitLiter(ctx: LiterContext): Node {
        return when {
            ctx.BOOL_LITER() != null -> BoolLiterNode(ctx.text)
            // Remove single quotes from the literal
            ctx.CHAR_LITER() != null -> CharLiterNode(ctx.text.substring(1, ctx.text.length - 1))
            // Remove double quotes from the literal
            ctx.STR_LITER() != null -> StrLiterNode(ctx.text.substring(1, ctx.text.length - 1))
            ctx.HEX_LITER() != null -> {
                if (ctx.text.length > 10) {
                    syntaxHandler.addSyntaxError(
                            ctx,
                            "int value (${ctx.text.toLong()}) must be between -2147483648 and 2147483647"
                    )
                }
                HexLiterNode(ctx.text)
            }
            ctx.OCT_LITER() != null -> {
                if (ctx.text.length > 13) {
                    syntaxHandler.addSyntaxError(
                            ctx,
                            "int value (${ctx.text.toLong()}) must be between -2147483648 and 2147483647"
                    )
                }
                OctLiterNode(ctx.text)
            }
            ctx.BIN_LITER() != null -> {
                if (ctx.text.length > 34) {
                    syntaxHandler.addSyntaxError(
                            ctx,
                            "int value (${ctx.text.toLong()}) must be between -2147483648 and 2147483647"
                    )
                }
                BinLiterNode(ctx.text)
            }
            else -> {
                val value = ctx.text.toLong()
                // Check the integer is within the accepted range
                if (value !in Integer.MIN_VALUE..Integer.MAX_VALUE) {
                    syntaxHandler.addSyntaxError(
                            ctx,
                            "int value (${ctx.text.toLong()}) must be between -2147483648 and 2147483647"
                    )
                }
                IntLiterNode(ctx.text)
            }
        }
    }

    override fun visitPairLiter(ctx: PairLiterContext): Node {
        return PairLiterNode()
    }

    override fun visitId(ctx: IdContext): Node {
        return visit(ctx.ident())
    }

    override fun visitIdent(ctx: IdentContext): Node {
        return Ident(ctx.text)
    }

    override fun visitArrayElem(ctx: ArrayElemContext): Node {
        return visit(ctx.array_elem())
    }

    override fun visitArray_elem(ctx: Array_elemContext): Node {
        return ArrayElem(visit(ctx.ident()) as Ident,
                ctx.expr().map { visit(it) as ExprNode })
    }

    override fun visitUnaryOp(ctx: UnaryOpContext): Node {
        val op = when {
            ctx.NOT() != null -> UnOp.NOT
            ctx.MINUS() != null -> UnOp.MINUS
            ctx.LEN() != null -> UnOp.LEN
            ctx.ORD() != null -> UnOp.ORD
            ctx.CHR() != null -> UnOp.CHR
            ctx.BITWISENOT() != null -> UnOp.BITWISENOT
            else -> UnOp.NOT_SUPPORTED
        }
        return UnaryOpNode(op, visit(ctx.expr()) as ExprNode)
    }

    override fun visitBinaryOp(ctx: BinaryOpContext): Node {
        val op = when {
            ctx.MUL() != null -> BinOp.MUL
            ctx.DIV() != null -> BinOp.DIV
            ctx.MOD() != null -> BinOp.MOD
            ctx.PLUS() != null -> BinOp.PLUS
            ctx.MINUS() != null -> BinOp.MINUS
            ctx.GT() != null -> BinOp.GT
            ctx.GTE() != null -> BinOp.GTE
            ctx.LT() != null -> BinOp.LT
            ctx.LTE() != null -> BinOp.LTE
            ctx.EQ() != null -> BinOp.EQ
            ctx.NEQ() != null -> BinOp.NEQ
            ctx.AND() != null -> BinOp.AND
            ctx.OR() != null -> BinOp.OR
            ctx.BITWISEAND() != null -> BinOp.BITWISEAND
            ctx.BITWISEOR() != null -> BinOp.BITWISEOR
            else -> BinOp.NOT_SUPPORTED
        }

        val expr1 = visit(ctx.expr(0)) as ExprNode
        val expr2 = visit(ctx.expr(1)) as ExprNode

        val exprType = getExprType(expr1, ctx)

        // Check the binary op expression types match up with what is required
        if ((exprType != null && getExprType(expr2, ctx.expr(1)) != null && exprType.getType() != getExprType(
                        expr2,
                        ctx.expr(1)
                )!!.getType())
                || (!binaryOpsRequires(op.value).contains(exprType) && !binaryOpsRequires(op.value).contains(TypeBase(ANY)))
        ) {
            semanticListener.binaryOpType(ctx)
        }

        return BinaryOpNode(op, expr1, expr2)
    }

    override fun visitParentheses(ctx: ParenthesesContext): Node {
        return visit(ctx.expr()) as ExprNode
    }

    override fun visitAssignLhsId(ctx: AssignLhsIdContext): Node {
        return AssignLHSIdentNode(visit(ctx.ident()) as Ident)
    }

    override fun visitAssignLhsStruct(ctx: AssignLhsStructContext): Node {
        val assign = visit(ctx.struct_access())
        return if (assign is StructMemberNode) {
            AssignLHSStructNode(assign)
        } else {
            AssignLHSClassNode(assign as ClassMemberNode)
        }
    }

    override fun visitStruct_access(ctx: Struct_accessContext): Node {
        val ident = visit(ctx.ident(0)) as Ident
        val type = globalSymbolTable.getNodeGlobal(ident.toString())
        val member = visit(ctx.ident(1)) as Ident
        return if (type is TypeStruct) {
            return when {
                ctx.array_elem() != null -> {
                    StructMemberNode(visit(ctx.ident(0)) as Ident, visit(ctx.array_elem()) as ArrayElem)
                }
                else -> {
                    StructMemberNode(visit(ctx.ident(0)) as Ident, visit(ctx.ident(1)) as Ident)
                }
            }
        } else {
            ClassMemberNode(ident, member)
        }
    }

    override fun visitAssignLhsArray(ctx: AssignLhsArrayContext): Node {
        return LHSArrayElemNode(visit(ctx.array_elem()) as ArrayElem)
    }

    override fun visitAssignLhsPair(ctx: AssignLhsPairContext): Node {
        return LHSPairElemNode(visit(ctx.pair_elem()) as PairElemNode)
    }


    override fun visitAssignRhsExpr(ctx: AssignRhsExprContext): Node {
        return RHSExprNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitAssignRhsArray(ctx: AssignRhsArrayContext): Node {
        return RHSArrayLitNode(ctx.array_liter().expr().map { visit(it) as ExprNode })
    }

    override fun visitAssignRhsNewpair(ctx: AssignRhsNewpairContext): Node {
        return RHSNewPairNode(
                visit(ctx.expr(0)) as ExprNode,
                visit(ctx.expr(1)) as ExprNode
        )
    }

    override fun visitAssignRhsPairElem(ctx: AssignRhsPairElemContext): Node {
        return RHSPairElemNode(visit(ctx.pair_elem()) as PairElemNode)
    }

    override fun visitAssignRhsCall(ctx: AssignRhsCallContext): Node {
        return visit(ctx.call_func())
    }

    override fun visitCall_func(ctx: Call_funcContext): Node {
        return if (ctx.ident().size == 1) {
            RHSCallNode(visit(ctx.ident(0)) as Ident,
                    when {
                        ctx.arg_list() != null -> ctx.arg_list().expr().map { visit(it) as ExprNode }
                        else -> null
                    })
        } else {
            val call = RHSCallNode(visit(ctx.ident(1)) as Ident,
                    when {
                        ctx.arg_list() != null -> ctx.arg_list().expr().map { visit(it) as ExprNode }
                        else -> null
                    })
            RHSClassCallNode(visit(ctx.ident(0)) as Ident, call)
        }
    }

    override fun visitAssignRhsNewObject(ctx: AssignRhsNewObjectContext): Node {
        val structIdent = visit(ctx.ident()) as Ident
        return if (structLists[structIdent] != null) {
            val args = ctx.arg_list().expr().map { visit(it) as ExprNode }
            RHSNewStruct(structIdent, args)
        } else {
            val args = if (ctx.arg_list() != null) {
                ctx.arg_list().expr().map { visit(it) as ExprNode }
            } else {
                emptyList()
            }
            val newClass = RHSNewClass(structIdent, args)
            checkClassParameters(newClass, ctx)
            newClass
        }
    }

    override fun visitPairFst(ctx: PairFstContext): Node {
        return FstExpr(visit(ctx.expr()) as ExprNode)
    }

    override fun visitPairSnd(ctx: PairSndContext): Node {
        return SndExpr(visit(ctx.expr()) as ExprNode)
    }
}

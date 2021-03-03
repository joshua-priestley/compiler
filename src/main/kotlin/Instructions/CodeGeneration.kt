package compiler.Instructions

import AST.*
import antlr.WACCParser
import java.util.concurrent.atomic.AtomicInteger

class CodeGeneration(private var globalSymbolTable: SymbolTable) {
    private val currentSymbolID = AtomicInteger()

    private var labelCounter = 0
    private val data: DataSegment = DataSegment()
    private val predefined: PredefinedFuncs = PredefinedFuncs(data)

    fun generateProgram(program: ProgramNode): List<Instruction> {
        val labelInstructions = mutableListOf<Instruction>()
        labelInstructions.add(GlobalLabel("text"))
        labelInstructions.add(GlobalLabel("global main"))

        // Generate the functions
        val funcInstructions = mutableListOf<Instruction>()
        for (func in program.funcs) {
            globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
            funcInstructions.addAll(generateFunction(func))
            globalSymbolTable = globalSymbolTable.parentT!!
        }

        // Generate the main function
        val mainInstructions = mutableListOf<Instruction>()
        // main:
        mainInstructions.add(FunctionDeclaration("main"))

        functionBodyInstructions(mainInstructions, program.stat, true)

        return listOf<Instruction>(data) +
                labelInstructions +
                funcInstructions +
                mainInstructions +
                predefined.toInstructionList()
    }

    private fun generateFunction(function: FunctionNode): List<Instruction> {
        val instructions = mutableListOf<Instruction>()

        // f_NAME:
        instructions.add(FunctionDeclaration("f_${function.ident.name}"))

        // Rest follows same format as the main function
        functionBodyInstructions(instructions, function.stat, false)

        return instructions
    }

    private fun functionBodyInstructions(instructions: MutableList<Instruction>, stat: StatementNode, main: Boolean) {
        // PUSH {lr}
        instructions.add(Push(listOf(Register.lr)))

        val spOffset = globalSymbolTable.localStackSize()

        if (spOffset > 0) {
            instructions.add(Sub(Register.sp, Register.sp, spOffset))
        }

        // Generate all the statements
        instructions.addAll(generateStat(stat))

        if (spOffset > 0) {
            instructions.add(Add(Register.sp, Register.sp, spOffset))
        }

        // LDR r0, =0
        if (main) {
            instructions.add(Load(Register.r0, "0"))
        }

        // POP {pc}
        instructions.add(Pop(listOf(Register.pc)))
        if (!main) {
            instructions.add(Pop(listOf(Register.pc)))
        }

        // .ltorg
        instructions.add(LocalLabel("ltorg"))
    }

    private fun generateStat(stat: StatementNode): List<Instruction> {
        return when (stat) {
            is SkipNode -> generateSkip()
            is DeclarationNode -> generateDeclaration(stat) // TODO
            is AssignNode -> generateAssign(stat) // TODO
            is ReadNode -> generateRead(stat) // TODO
            is FreeNode -> generateFree(stat)
            is ReturnNode -> generateReturn(stat)
            is ExitNode -> generateExit(stat)
            is PrintNode -> generatePrint(stat) // TODO
            is PrintlnNode -> generatePrintln(stat)// TODO
            is IfElseNode -> generateIf(stat)
            is WhileNode -> generateWhile(stat)
            is BeginEndNode -> {
                globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
                val statInstructions = generateStat(stat.stat)
                globalSymbolTable = globalSymbolTable.parentT!!
                statInstructions
            }
            is SequenceNode -> generateSeq(stat)
            else -> throw Error("Should not get here")
        }
    }

    private fun generatePrintln(stat: PrintlnNode): List<Instruction> {
        // TODO find better way of arranging things to make adding the branch cleaner
        val instructions = mutableListOf<Instruction>()
        instructions.addAll(generatePrint(PrintNode(stat.expr)))
        val funcName = predefined.addFunc(PrintLn())
        instructions.add(Branch(funcName, true))
        return instructions
    }

    private fun generatePrint(stat: PrintNode): List<Instruction> {
        val printInstruction = mutableListOf<Instruction>()
        printInstruction.addAll(generateExpr(stat.expr))
        printInstruction.add(Move(Register.r0, Register.r4))

        val funcName: String = when (getType(stat.expr)) {
            Type(WACCParser.INT) -> predefined.addFunc(PrintInt())
            Type(WACCParser.STRING) -> predefined.addFunc(PrintString())
            Type(WACCParser.BOOL) -> predefined.addFunc(PrintBool())
            Type(WACCParser.CHAR) -> "putchar"
            else -> "dummy string"// TODO Handle reference printing
        }

        printInstruction.add(Branch(funcName, true))

        return printInstruction
    }

    private fun generateRead(stat: ReadNode): List<Instruction> {
        return emptyList()
    }

    private fun getExprParameterOffset(expr: ExprNode): Int {
        return when (expr) {
            is PairLiterNode -> 4
            is IntLiterNode -> 4
            is StrLiterNode -> 4
            is CharLiterNode -> 1
            is BoolLiterNode -> 1
            is BinaryOpNode -> Type.binaryOpsProduces(expr.operator.value).getTypeSize()
            is UnaryOpNode -> Type.unaryOpsProduces(expr.operator.value).getTypeSize()
            is ArrayElem -> {
                val type = globalSymbolTable.getNodeGlobal(expr.ident.toString())
                type!!.getBaseType().getTypeSize()
            }
            is Ident -> {
                val type = globalSymbolTable.getNodeGlobal(expr.toString())
                return type!!.getTypeSize()
            }
            else -> {
                0
            }
        }
    }

    private fun generateCallNode(call: RHSCallNode): List<Instruction> {
        val callInstructions = mutableListOf<Instruction>()
        if (call.argList.isNullOrEmpty()) return callInstructions
        val parameters = call.argList.reversed()

        var totalOffset = 0
        for (param in parameters) {
            callInstructions.addAll(generateExpr(param))
            val offset = getExprParameterOffset(param)
            totalOffset += offset
            assert(offset != 0)
            val byte = offset == 1
            callInstructions.add(Store(Register.r4, Register.sp, offset * -1, parameter = true, byte = byte))
        }

        val functionName = "f_${call.ident.name}"
        callInstructions.add(Branch(functionName, true))
        if (totalOffset != 0) callInstructions.add(Add(Register.sp, Register.sp, totalOffset))

        callInstructions.add(Move(Register.r4, Register.r0))

        return callInstructions
    }

    private fun generateRHSNode(rhs: AssignRHSNode): List<Instruction> {
        val rhsInstruction = mutableListOf<Instruction>()
        when (rhs) {
            is RHSCallNode -> rhsInstruction.addAll(generateCallNode(rhs))
            is RHSExprNode -> rhsInstruction.addAll(generateExpr(rhs.expr))
            is RHSArrayLitNode -> rhsInstruction.addAll(generateArrayLitNode(rhs.exprs))
            is RHSNewPairNode -> rhsInstruction.addAll(generateNewPair(rhs))
            is RHSPairElemNode -> rhsInstruction.addAll(generatePairAccess(rhs))
            else -> throw Error("Does not exist")
        }

        return rhsInstruction
    }

    private fun generatePairAccess(rhs: RHSPairElemNode): List<Instruction> {
        val pairAccessInstructions = mutableListOf<Instruction>()
        val first = rhs.pairElem is FstExpr

        val offset = if (first) {
            getStackOffsetValue((rhs.pairElem as FstExpr).expr.toString())
        } else {
            getStackOffsetValue((rhs.pairElem as SndExpr).expr.toString())

        }
        pairAccessInstructions.add(Load(Register.r4, Register.sp, offset))
        pairAccessInstructions.add(Move(Register.r0, Register.r4))
        pairAccessInstructions.add(Branch("p_check_null_pointer", true))

        val type = if (first) {
            globalSymbolTable.getNodeGlobal(((rhs.pairElem) as FstExpr).expr.toString())!!.getPairFst()
        } else {
            globalSymbolTable.getNodeGlobal(((rhs.pairElem) as SndExpr).expr.toString())!!.getPairSnd()
        }

        pairAccessInstructions.add(Load(Register.r4, Register.r4, if (first) 0 else 4))
        pairAccessInstructions.add(Load(Register.r4, Register.r4, sb = type!!.getTypeSize() == 1))
        return pairAccessInstructions
    }

    private fun addPairElem(elem: ExprNode, second: Boolean = false): List<Instruction> {
        val pairElemInstructions = mutableListOf<Instruction>()
        pairElemInstructions.addAll(generateExpr(elem, Register.r5))
        pairElemInstructions.add(Load(Register.r0, getExprParameterOffset(elem)))
        pairElemInstructions.add(Branch("malloc", true))
        pairElemInstructions.add(Store(Register.r5, Register.r0, byte = (getExprParameterOffset(elem) == 1)))
        pairElemInstructions.add(Store(Register.r0, Register.r4, if (!second) 0 else 4))
        return pairElemInstructions
    }

    private fun generateNewPair(pair: RHSNewPairNode): List<Instruction> {
        val newPairInstructions = mutableListOf<Instruction>()

        // Initialisation
        newPairInstructions.add(Load(Register.r0, 8))
        newPairInstructions.add(Branch("malloc", true))
        newPairInstructions.add(Move(Register.r4, Register.r0))

        newPairInstructions.addAll(addPairElem(pair.expr1))
        newPairInstructions.addAll(addPairElem(pair.expr2, true))

        return newPairInstructions
    }

    private fun generateArrayLitNode(elements: List<ExprNode>): List<Instruction> {
        val arrayLitInstructions = mutableListOf<Instruction>()

        var typeSize = 4
        val arraySize = if (elements.isEmpty()) {
            4
        } else {
            typeSize = getExprParameterOffset(elements[0])
            4 + elements.size * typeSize
        }

        // Instructions for allocating space for array
        arrayLitInstructions.add(Load(Register.r0, arraySize))
        arrayLitInstructions.add(Branch("malloc", true))
        arrayLitInstructions.add(Move(Register.r4, Register.r0))

        // Add each element
        var count = 0
        for (expr in elements) {
            arrayLitInstructions.addAll(generateExpr(expr, Register.r5))
            arrayLitInstructions.add(Store(Register.r5, Register.r4, 4 + count * typeSize, byte = typeSize == 1))
            count++
        }

        // Add the size of the array
        arrayLitInstructions.add(Load(Register.r5, count))
        arrayLitInstructions.add(Store(Register.r5, Register.r4))

        return arrayLitInstructions
    }

    private fun loadIdentValue(ident: Ident): List<Instruction> {
        val loadInstructions = mutableListOf<Instruction>()

        val type = globalSymbolTable.getNodeGlobal(ident.toString())!!
        val byte: Boolean = type == Type(WACCParser.CHAR) || type == Type(WACCParser.BOOL)
        val offset = getStackOffsetValue(ident.toString())
        loadInstructions.add(Store(Register.r4, Register.sp, offset, byte = byte))

        return loadInstructions
    }

    private fun generateAssign(stat: AssignNode): List<Instruction> {
        val assignInstructions = mutableListOf<Instruction>()

        assignInstructions.addAll(generateRHSNode(stat.rhs))

        if (stat.lhs is AssignLHSIdentNode) {
            assignInstructions.addAll(loadIdentValue(stat.lhs.ident))
        }

        return assignInstructions
    }

    private fun generateDeclaration(stat: DeclarationNode): List<Instruction> {
        val declareInstructions = mutableListOf<Instruction>()

        declareInstructions.addAll(generateRHSNode(stat.value))
        declareInstructions.addAll(loadIdentValue(stat.ident))

        return declareInstructions
    }

    private fun generateIf(stat: IfElseNode): List<Instruction> {
        val ifInstruction = mutableListOf<Instruction>()

        val elseLabel = nextLabel()
        val endLabel = nextLabel()

        // Load up the conditional
        if (stat.expr is LiterNode) {
            ifInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
        } else {
            ifInstruction.addAll(generateExpr(stat.expr))
        }

        // Compare the conditional
        ifInstruction.add(Compare(Register.r4, 0))
        ifInstruction.add(Branch(elseLabel, false, Conditions.EQ))

        // Then Branch
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        ifInstruction.addAll(generateStat(stat.then))
        ifInstruction.add(Branch(endLabel, false))
        globalSymbolTable = globalSymbolTable.parentT!!

        // Else Branch
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        ifInstruction.add(FunctionDeclaration(elseLabel))
        ifInstruction.addAll(generateStat(stat.else_))
        globalSymbolTable = globalSymbolTable.parentT!!

        ifInstruction.add(FunctionDeclaration(endLabel))

        return ifInstruction
    }

    private fun generateWhile(stat: WhileNode): List<Instruction> {
        val whileInstruction = mutableListOf<Instruction>()

        val conditionLabel = nextLabel()
        val bodyLabel = nextLabel()

        whileInstruction.add(Branch(conditionLabel, false))

        // Loop body
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        whileInstruction.add(FunctionDeclaration(bodyLabel))
        whileInstruction.addAll(generateStat(stat.do_))
        globalSymbolTable = globalSymbolTable.parentT!!

        // Conditional
        whileInstruction.add(FunctionDeclaration(conditionLabel))
        if (stat.expr is LiterNode) {
            whileInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
        } else {
            whileInstruction.addAll(generateExpr(stat.expr))
        }
        whileInstruction.add(Compare(Register.r4, 1))
        whileInstruction.add(Branch(bodyLabel, false, Conditions.EQ))

        return whileInstruction
    }

    private fun generateReturn(stat: ReturnNode): List<Instruction> {
        val returnInstruction = mutableListOf<Instruction>()

        if (stat.expr is LiterNode) {
            returnInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
        } else {
            returnInstruction.addAll(generateExpr(stat.expr))
        }

        returnInstruction.add(Move(Register.r0, Register.r4))
        return returnInstruction
    }

    private fun generateExpr(expr: ExprNode, reg: Register = Register.r4): List<Instruction> {
        return when (expr) {
            is LiterNode -> generateLiterNode(expr, reg)
            is BinaryOpNode -> generateBinOp(expr, reg)
            is UnaryOpNode -> generateUnOp(expr, reg)
            is ArrayElem -> generateArrayElem(expr, reg)
            else -> emptyList()
        }
    }

    private fun generateArrayElem(expr: ArrayElem, reg: Register): List<Instruction> {
        val arrayElemInstructions = mutableListOf<Instruction>()
        val offset = getStackOffsetValue(expr.ident.toString())
        // TODO Registers dont work correctly - need to implement next register section
        arrayElemInstructions.add(Add(Register.r4, Register.sp, offset))
        arrayElemInstructions.addAll(generateExpr(expr.expr[0], Register.r5))
        arrayElemInstructions.add(Load(Register.r4, Register.r4))

        arrayElemInstructions.add(Move(Register.r0, Register.r5))
        arrayElemInstructions.add(Move(Register.r1, Register.r4))
        arrayElemInstructions.add(Branch(predefined.addFunc(CheckArrayBounds()), true))

        val type = globalSymbolTable.getNodeGlobal(expr.ident.toString())!!.getBaseType()
        arrayElemInstructions.add(Add(Register.r4, Register.r4, 4))
        // ADD r4, r4, r5, LSL #2
        arrayElemInstructions.add(Load(Register.r4, Register.r4, sb = type.getTypeSize() == 1))

        return arrayElemInstructions
    }

    private fun generateSeq(stat: SequenceNode): List<Instruction> {
        val sequenceInstruction = mutableListOf<Instruction>()

        stat.statList.map { generateStat(it) }.forEach { sequenceInstruction.addAll(it) }
        return sequenceInstruction
    }

    private fun generateFree(stat: FreeNode): List<Instruction> {
        val freeInstruction = mutableListOf<Instruction>()

        freeInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
        freeInstruction.add(Move(Register.r0, Register.r4))

        val funcName = predefined.addFunc(Freepair())
        freeInstruction.add(Branch(funcName, true))

        return freeInstruction
    }

    private fun generateExit(exitNode: ExitNode): List<Instruction> {
        val exitInstruction = mutableListOf<Instruction>()

        if (exitNode.expr is IntLiterNode) {
            exitInstruction.add(Load(Register.r4, exitNode.expr.value.toInt()))
        } else if (exitNode.expr is Ident) {
            val offset = getStackOffsetValue(exitNode.expr.toString())
            exitInstruction.add(Load(Register.r4, Register.sp, offset))
        }

        exitInstruction.add(Move(Register.r0, Register.r4))
        exitInstruction.add(Branch("exit", true))

        return exitInstruction
    }

    private fun generateSkip(): List<Instruction> {
        return mutableListOf()
    }

    private fun generateLiterNode(exprNode: ExprNode, dstRegister: Register): List<Instruction> {
        val loadInstruction = mutableListOf<Instruction>()
        when (exprNode) {
            is IntLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value.toInt()))
            }
            is StrLiterNode -> {
                data.addMessage(Message(exprNode.value))
                loadInstruction.add(Load(dstRegister, data.getLabel(exprNode.value)))
            }
            is CharLiterNode -> {
                loadInstruction.add(Move(dstRegister, exprNode.value[0]))
            }
            is BoolLiterNode -> {
                loadInstruction.add(Move(dstRegister, if (exprNode.value == "true") {
                    1
                } else {
                    0
                }))
            }
            is Ident -> {
                val type = globalSymbolTable.getNodeGlobal(exprNode.toString())!!
                val offset = getStackOffsetValue(exprNode.toString())
                val sb = type == Type(WACCParser.BOOL) || type == Type(WACCParser.CHAR)
                loadInstruction.add(Load(dstRegister, Register.sp, offset, sb = sb))
            }
        }
        return loadInstruction
    }

    private fun getStackOffsetValue(name: String): Int {
        return if (globalSymbolTable.getNodeGlobal(name)!!.isParameter()) {
            globalSymbolTable.getStackOffset(name) + globalSymbolTable.localStackSize()
        } else {
            globalSymbolTable.localStackSize() - globalSymbolTable.getStackOffset(name)
        }
    }

    private fun nextLabel(): String {
        return "L${labelCounter++}"
    }

    private fun generateUnOp(unOp: UnaryOpNode, reg: Register = Register.r4): List<Instruction> {
        val list = mutableListOf<Instruction>()
        val expr = generateExpr(unOp.expr, reg)
        list.addAll(expr)
        when (unOp.operator) {
            //ORD and CHR are handled by print_int and print_char
            UnOp.NOT -> {
                list.add(Not(reg, reg))
            }
            UnOp.MINUS -> {
                list.add(Minus(reg))
                list.add(Branch(predefined.addFunc(Overflow()), true, Conditions.VS))
            }
            UnOp.LEN -> {
                list.add(Load(Register.r4, reg))
            }
        }
        return list
    }

    private fun generateBinOp(binOp: BinaryOpNode, reg: Register = Register.r4): List<Instruction> {
        val list = mutableListOf<Instruction>()
        var operand1 = reg
        var pop = false
        if (operand1 >= Register.r10){
            pop = true
            operand1 = Register.r10
            list.addAll(generateExpr(binOp.expr1,operand1))
            list.add(Push(mutableListOf(Register.r10)))
        } else {
            list.addAll(generateExpr(binOp.expr1,operand1))
        }
        var operand2 = operand1.nextAvailable()
        if (operand2 >= Register.r10) operand2 = Register.r10
        val expr2 = generateExpr(binOp.expr2, operand2)
        list.addAll(expr2)

        var dstRegister = operand1
        if (pop){
            list.add(Pop(mutableListOf(Register.r11)))
            dstRegister = operand2
            operand1 = Register.r11
        }

        when (binOp.operator) {
            BinOp.PLUS -> {
                list.add(Add(dstRegister, operand1, operand2, true))
                list.add(Branch(predefined.addFunc(Overflow()), true, Conditions.VS))
            }
            BinOp.MINUS -> {
                list.add(Sub(dstRegister, operand1, operand2, true))
                list.add(Branch(predefined.addFunc(Overflow()), true, Conditions.VS))
            }
            BinOp.MUL -> {
                list.add(Multiply(operand1, operand2, operand1, operand2, true))
                list.add(Compare(operand2, operand1, null, ArithmeticShiftRight(31)))
                list.add(Branch(predefined.addFunc(Overflow()), true, Conditions.NE))
            }

            BinOp.AND -> {
                list.add(And(dstRegister, operand1, operand2))
            }
            BinOp.OR -> {
                list.add(Or(dstRegister, operand1, operand2))
            }
            BinOp.EQ -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, 1, Conditions.EQ))
                list.add(Move(dstRegister, 0, Conditions.NE))
            }
            BinOp.NEQ -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, 1, Conditions.NE))
                list.add(Move(dstRegister, 0, Conditions.EQ))
            }
            BinOp.LT -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, 1, Conditions.LT))
                list.add(Move(dstRegister, 0, Conditions.GE))
            }
            BinOp.GT -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, 1, Conditions.GT))
                list.add(Move(dstRegister, 0, Conditions.LE))
            }
            BinOp.GTE -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, 1, Conditions.GE))
                list.add(Move(dstRegister, 0, Conditions.LT))
            }
            BinOp.LTE -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, 1, Conditions.LE))
                list.add(Move(dstRegister, 0, Conditions.GT))
            }

            BinOp.MOD -> {
                list.add(Move(Register.r0, operand1))
                list.add(Move(Register.r1, operand2))
                val funcName = predefined.addFunc(DivideByZero())
                list.add(Branch(funcName, true))
                list.add(Branch("__aeabi_idivmod", true))
                list.add(Move(dstRegister, Register.r1))
            }
            BinOp.DIV -> {
                list.add(Move(Register.r0, operand1))
                list.add(Move(Register.r1, operand2))
                val funcName = predefined.addFunc(DivideByZero())
                list.add(Branch(funcName, true))
                list.add(Branch("__aeabi_idiv", true))
                list.add(Move(dstRegister, Register.r0))
            }
            else -> {
                throw Error("Should not get here")
            }
        }
        return list

    }

    // TODO find better way to get expression types than this - lots of duplication with ASTBuilder
    // maybe add type member to expression nodes superclass and set during ast building?
    private fun getType(expr: ExprNode): Type? {
        return when (expr) {
            is IntLiterNode -> Type(WACCParser.INT)
            is StrLiterNode -> Type(WACCParser.STRING)
            is BoolLiterNode -> Type(WACCParser.BOOL)
            is CharLiterNode -> Type(WACCParser.CHAR)
            is Ident -> globalSymbolTable.getNodeGlobal(expr.toString())
            is ArrayElem -> {
                val type = globalSymbolTable.getNodeGlobal(expr.ident.toString())
                return type?.getBaseType() ?: Type(INVALID)
            }
            is UnaryOpNode -> Type.unaryOpsProduces(expr.operator.value)
            is BinaryOpNode -> Type.binaryOpsProduces(expr.operator.value)
            is PairLiterNode -> Type(PAIR_LITER)
            else -> {
                throw Error("Should not get here")
            }
        }
    }
}
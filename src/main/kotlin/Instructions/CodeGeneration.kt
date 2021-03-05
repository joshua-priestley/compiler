package compiler.Instructions

import AST.*
import antlr.WACCParser
import java.util.concurrent.atomic.AtomicInteger

class CodeGeneration(private var globalSymbolTable: SymbolTable) {
    private val currentSymbolID = AtomicInteger()

    private var inElseStatement = false
    private var stackToAdd = 0
    private var assign = false
    private var printing = false

    private var labelCounter = 0
    private val data: DataSegment = DataSegment()
    private val predefined: PredefinedFuncs = PredefinedFuncs(data)


    companion object {
        private const val MAX_SIZE = 1024
    }

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
            instructions.addAll(growStack(spOffset))
        }

        // Generate all the statements
        instructions.addAll(generateStat(stat))

        if (spOffset > 0) {
            instructions.addAll(shrinkStack(spOffset))
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
            is BeginEndNode -> generateBegin(stat)
            is SequenceNode -> generateSeq(stat)
            else -> throw Error("Should not get here")
        }
    }

    private fun generateBegin(stat: BeginEndNode): List<Instruction> {
        val beginInstructions = mutableListOf<Instruction>()
        stackToAdd += globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        stackToAdd += globalSymbolTable.localStackSize()
        if (globalSymbolTable.localStackSize() > 0) beginInstructions.addAll(growStack(globalSymbolTable.localStackSize()))
        beginInstructions.addAll(generateStat(stat.stat))
        if (globalSymbolTable.localStackSize() > 0) beginInstructions.addAll(shrinkStack(globalSymbolTable.localStackSize()))
        stackToAdd -= globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.parentT!!
        stackToAdd -= globalSymbolTable.localStackSize()
        return beginInstructions
    }

    private fun generatePrintln(stat: PrintlnNode): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        instructions.addAll(generatePrint(PrintNode(stat.expr)))
        instructions.add(Branch(predefined.addFunc(PrintLn()), true))
        return instructions
    }

    private fun generatePrint(stat: PrintNode): List<Instruction> {
        assign = true
        printing = true
        val printInstruction = mutableListOf<Instruction>()
        printInstruction.addAll(generateExpr(stat.expr))
        printInstruction.add(Move(Register.r0, Register.r4))
        assign = false
        printing = false
        val funcName: String = when (getType(stat.expr)) {
            Type(WACCParser.INT) -> predefined.addFunc(PrintInt())
            Type(WACCParser.STRING) -> predefined.addFunc(PrintString())
            Type(WACCParser.BOOL) -> predefined.addFunc(PrintBool())
            Type(WACCParser.CHAR) -> "putchar"
            else -> predefined.addFunc(PrintReference())
        }

        printInstruction.add(Branch(funcName, true))
        return printInstruction
    }

    private fun generateRead(stat: ReadNode): List<Instruction> {
        val readInstructions = mutableListOf<Instruction>()
        assign = true

        val expr: ExprNode = when (stat.lhs) {
            is LHSPairElemNode -> {
                readInstructions.addAll(generatePairAccess(stat.lhs.pairElem, true))
                stat.lhs.pairElem.expr
            }
            is AssignLHSIdentNode -> {
                val offset = getStackOffsetValue(stat.lhs.ident.toString())
                //TODO change value of register
                println(offset)
                readInstructions.add(Add(Register.r4, Register.sp, ImmOp(offset)))
                stat.lhs.ident
            }
            is LHSArrayElemNode -> {
                val offset = getStackOffsetValue(stat.lhs.arrayElem.ident.toString())
                //TODO change value of register
                readInstructions.add(Add(Register.r4, Register.sp, ImmOp(offset)))
                stat.lhs.arrayElem
            }
            else -> throw Error("Does not exist")
        }
        assign = false

        val type = getType(expr)
        readInstructions.add(Move(Register.r0, Register.r4))

        if (type == Type(WACCParser.CHAR)) {
            readInstructions.add(Branch(predefined.addFunc(ReadChar()), true))
        } else {
            readInstructions.add(Branch(predefined.addFunc(ReadInt()), true))
        }
        return readInstructions
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
        var totalOffset = 0
        if (!call.argList.isNullOrEmpty()) {
            val parameters = call.argList.reversed()

            for (param in parameters) {
                callInstructions.addAll(generateExpr(param))
                val offset = getExprParameterOffset(param)
                totalOffset += offset
                assert(offset != 0)
                val byte = offset == 1
                callInstructions.add(Store(Register.r4, Register.sp, offset * -1, parameter = true, byte = byte))
                globalSymbolTable.addToOffset(offset)
            }
        }
        globalSymbolTable.addToOffset(-1 * totalOffset)

        val functionName = "f_${call.ident.name}"
        callInstructions.add(Branch(functionName, true))
        if (totalOffset != 0) callInstructions.add(Add(Register.sp, Register.sp, ImmOp(totalOffset)))

        callInstructions.add(Move(Register.r4, Register.r0))

        return callInstructions
    }

    private fun generateRHSNode(rhs: AssignRHSNode, reg: Register = Register.r5): List<Instruction> {
        val rhsInstruction = mutableListOf<Instruction>()
        when (rhs) {
            is RHSCallNode -> {
                rhsInstruction.addAll(generateCallNode(rhs))
            }
            is RHSExprNode -> {
                rhsInstruction.addAll(generateExpr(rhs.expr))
            }
            is RHSArrayLitNode -> rhsInstruction.addAll(generateArrayLitNode(rhs.exprs, reg))
            is RHSNewPairNode -> rhsInstruction.addAll(generateNewPair(rhs))
            is RHSPairElemNode -> rhsInstruction.addAll(generatePairAccess(rhs.pairElem, false))
            else -> throw Error("Does not exist")
        }

        return rhsInstruction
    }

    private fun generatePairAccess(pairElem: PairElemNode, assign: Boolean, reg: Register = Register.r4): List<Instruction> {
        val pairAccessInstructions = mutableListOf<Instruction>()
        val first = pairElem is FstExpr

        val offset = if (first) {
            getStackOffsetValue((pairElem as FstExpr).expr.toString())
        } else {
            getStackOffsetValue((pairElem as SndExpr).expr.toString())

        }
        pairAccessInstructions.add(Load(reg, Register.sp, offset))
        pairAccessInstructions.add(Move(Register.r0, reg))
        pairAccessInstructions.add(Branch(predefined.addFunc(CheckNullPointer()), true))

        val type = if (first) {
            globalSymbolTable.getNodeGlobal(((pairElem) as FstExpr).expr.toString())!!.getPairFst()
        } else {
            globalSymbolTable.getNodeGlobal(((pairElem) as SndExpr).expr.toString())!!.getPairSnd()
        }

        pairAccessInstructions.add(Load(reg, reg, if (first) 0 else 4))
        if (assign) {
            pairAccessInstructions.add(Store(Register.r4, reg, byte = type!!.getTypeSize() == 1))
        } else {
            pairAccessInstructions.add(Load(Register.r4, Register.r4, sb = type!!.getTypeSize() == 1))
        }
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

    private fun generateArrayLitNode(elements: List<ExprNode>, reg: Register = Register.r5): List<Instruction> {
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
            arrayLitInstructions.addAll(generateExpr(expr, reg))
            arrayLitInstructions.add(Store(reg, Register.r4, 4 + count * typeSize, byte = typeSize == 1))
            count++
        }

        // Add the size of the array
        arrayLitInstructions.add(Load(reg, count))
        arrayLitInstructions.add(Store(reg, Register.r4))

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

    private fun generateLHSAssign(lhs: AssignLHSNode, reg: Register): List<Instruction> {
        val lhsInstructions = mutableListOf<Instruction>()

        when (lhs) {
            is AssignLHSIdentNode -> lhsInstructions.addAll(loadIdentValue(lhs.ident))
            is LHSArrayElemNode -> {
                lhsInstructions.addAll(generateExpr(lhs.arrayElem, reg))
            }
            is LHSPairElemNode -> {
                lhsInstructions.addAll(generatePairAccess(lhs.pairElem, true, reg))
            }
        }

        return lhsInstructions
    }

    private fun generateAssign(stat: AssignNode, reg: Register = Register.r5): List<Instruction> {
        assign = true
        val assignInstructions = mutableListOf<Instruction>()
        if(stat.rhs is RHSExprNode) {
            val a = getType(stat.rhs.expr)
            if(stat.lhs is AssignLHSIdentNode) {
                val b = globalSymbolTable.getNodeGlobal(stat.lhs.ident.toString())!!
                if(a != b) {
                    globalSymbolTable.getNodeGlobal(stat.lhs.ident.toString(), a)!!
                    assignInstructions.addAll(generateRHSNode(stat.rhs, reg.nextAvailable()))
                    assignInstructions.addAll(generateLHSAssign(stat.lhs, reg))
                    globalSymbolTable.getNodeGlobal(stat.lhs.ident.toString(), b)!!
                    assign = false
                    return assignInstructions
                }
            }
        }
        assignInstructions.addAll(generateRHSNode(stat.rhs, reg.nextAvailable()))
        assignInstructions.addAll(generateLHSAssign(stat.lhs, reg))
        assign = false
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
        assign = true
        if (stat.expr is LiterNode) {
            ifInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
        } else {
            ifInstruction.addAll(generateExpr(stat.expr))
        }
        assign = false

        // Compare the conditional
        ifInstruction.add(Compare(Register.r4, ImmOp(0)))
        ifInstruction.add(Branch(elseLabel, false, Conditions.EQ))

        // Then Branch
        stackToAdd += globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        stackToAdd += globalSymbolTable.localStackSize()
        if (globalSymbolTable.localStackSize() > 0) ifInstruction.addAll(growStack(globalSymbolTable.localStackSize()))
        ifInstruction.addAll(generateStat(stat.then))
        if (globalSymbolTable.localStackSize() > 0) ifInstruction.addAll(shrinkStack(globalSymbolTable.localStackSize()))
        ifInstruction.add(Branch(endLabel, false))
        stackToAdd -= globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.parentT!!

        inElseStatement = true
        // Else Branch
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        stackToAdd += globalSymbolTable.localStackSize()
        println("Local stack size: ${globalSymbolTable.localStackSize()}")
        ifInstruction.add(FunctionDeclaration(elseLabel))
        if (globalSymbolTable.localStackSize() > 0) ifInstruction.addAll(growStack(globalSymbolTable.localStackSize()))
        ifInstruction.addAll(generateStat(stat.else_))
        if (globalSymbolTable.localStackSize() > 0) ifInstruction.addAll(shrinkStack(globalSymbolTable.localStackSize()))
        stackToAdd -= globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.parentT!!
        stackToAdd -= globalSymbolTable.localStackSize()

        ifInstruction.add(FunctionDeclaration(endLabel))

        inElseStatement = false
        return ifInstruction
    }

    private fun generateWhile(stat: WhileNode): List<Instruction> {
        val whileInstruction = mutableListOf<Instruction>()

        val conditionLabel = nextLabel()
        val bodyLabel = nextLabel()

        whileInstruction.add(Branch(conditionLabel, false))

        // Loop body
        if (!inElseStatement) stackToAdd += globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        stackToAdd += globalSymbolTable.localStackSize()
        whileInstruction.add(FunctionDeclaration(bodyLabel))
        if (globalSymbolTable.localStackSize() > 0) whileInstruction.addAll(growStack(globalSymbolTable.localStackSize()))
        whileInstruction.addAll(generateStat(stat.do_))
        if (globalSymbolTable.localStackSize() > 0) whileInstruction.addAll(shrinkStack(globalSymbolTable.localStackSize()))
        stackToAdd -= globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.parentT!!
        if (!inElseStatement) stackToAdd -= globalSymbolTable.localStackSize()

        // Conditional
        assign = true
        whileInstruction.add(FunctionDeclaration(conditionLabel))
        if (stat.expr is LiterNode) {
            whileInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
        } else {
            whileInstruction.addAll(generateExpr(stat.expr))
        }
        whileInstruction.add(Compare(Register.r4, ImmOp(1)))
        whileInstruction.add(Branch(bodyLabel, false, Conditions.EQ))
        assign = false

        return whileInstruction
    }

    private fun generateReturn(stat: ReturnNode): List<Instruction> {
        val returnInstruction = mutableListOf<Instruction>()
        assign = true

        if (stat.expr is LiterNode) {
            returnInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
        } else {
            returnInstruction.addAll(generateExpr(stat.expr))
        }

        returnInstruction.add(Move(Register.r0, Register.r4))
        if (stackToAdd > 0) {
            returnInstruction.add(Add(Register.sp, Register.sp, ImmOp(stackToAdd)))
            returnInstruction.add(Pop(listOf(Register.pc)))
        }
        assign = false
        return returnInstruction
    }

    private fun generateExpr(expr: ExprNode, reg: Register = Register.r4): List<Instruction> {
        return when (expr) {
            is LiterNode -> generateLiterNode(expr, reg)
            is BinaryOpNode -> generateBinOp(expr, reg)
            is UnaryOpNode -> generateUnOp(expr, reg)
            is ArrayElem -> generateArrayElem(expr, reg)
            is PairLiterNode -> mutableListOf(Load(reg, 0))
            else -> emptyList()
        }
    }

    private fun generateArrayElem(expr: ArrayElem, reg: Register): List<Instruction> {
        val arrayElemInstructions = mutableListOf<Instruction>()
        val offset = getStackOffsetValue(expr.ident.toString())
        val reg2 = reg.nextAvailable()
        // TODO Registers dont work correctly - need to implement next register section
        var type: Type? = null
        arrayElemInstructions.add(Add(reg, Register.sp, ImmOp(offset)))
        for (element in expr.expr) {
            arrayElemInstructions.addAll(generateExpr(element, reg2))
            arrayElemInstructions.add(Load(reg, reg))

            arrayElemInstructions.add(Move(Register.r0, reg2))
            arrayElemInstructions.add(Move(Register.r1, reg))
            arrayElemInstructions.add(Branch(predefined.addFunc(CheckArrayBounds()), true))

            type = globalSymbolTable.getNodeGlobal(expr.ident.toString())!!.getBaseType()
            arrayElemInstructions.add(Add(reg, reg, ImmOp(4)))
            if (type.getTypeSize() == 1) {
                arrayElemInstructions.add(Add(reg, reg, reg2))
            } else {
                arrayElemInstructions.add(Add(reg, reg, LogicalShiftLeft(reg2, 2)))
            }
        }
        if (type != null) {
            if (!assign || printing) {
                arrayElemInstructions.add(Load(Register.r4, reg, sb = type.getTypeSize() == 1))
            } else {
                arrayElemInstructions.add(Store(Register.r4, reg, byte = type.getTypeSize() == 1))
            }
        }
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

        if (exitNode.expr is Ident) {
            val offset = getStackOffsetValue(exitNode.expr.toString())
            exitInstruction.add(Load(Register.r4, Register.sp, offset))
        } else {
            exitInstruction.addAll(generateExpr(exitNode.expr))
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
                // TODO do we have to deal with other escaped characters?
                if (exprNode.value == "\\0") {
                    loadInstruction.add(Move(dstRegister, ImmOp(0)))
                } else {
                    val char = if (exprNode.value.length == 2) exprNode.value[1] else exprNode.value[0]
                    loadInstruction.add(Move(dstRegister, CharOp(char)))
                }
            }
            is BoolLiterNode -> {
                loadInstruction.add(Move(dstRegister, if (exprNode.value == "true") {
                    ImmOp(1)
                } else {
                    ImmOp(0)
                }))
            }
            is Ident -> {
                val type = globalSymbolTable.getNodeGlobal(exprNode.toString())!!
                val offset = getStackOffsetValue(exprNode.toString())
                loadInstruction.add(Load(dstRegister, Register.sp, offset, sb = type.getTypeSize() == 1))
            }
        }
        return loadInstruction
    }

    private fun getStackOffsetValue(name: String): Int {
        println("N: $name, Param: ${globalSymbolTable.getNodeGlobal(name)!!.isParameter()}, Local: ${globalSymbolTable.containsNodeLocal(name)}, ${globalSymbolTable.parameterStackSize()}, ${globalSymbolTable.getStackOffset(name)}, ${!inElseStatement} && ${globalSymbolTable.localStackSize()}, $assign && ${!globalSymbolTable.containsNodeLocal(name)}, $stackToAdd")
        return if (globalSymbolTable.getNodeGlobal(name)!!.isParameter()) {
            globalSymbolTable.getStackOffset(name) + (if (globalSymbolTable.containsNodeLocal(name)) globalSymbolTable.localStackSize() else 0) + if (!inElseStatement && assign && !globalSymbolTable.containsNodeLocal(name)) stackToAdd else 0
        } else {
            globalSymbolTable.localStackSize() - globalSymbolTable.getStackOffset(name) + (if (assign && !globalSymbolTable.containsNodeLocal(name)) stackToAdd else 0)
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
        if (operand1 >= Register.r10) {
            pop = true
            operand1 = Register.r10
            list.addAll(generateExpr(binOp.expr1, operand1))
            list.add(Push(mutableListOf(Register.r10)))
        } else {
            list.addAll(generateExpr(binOp.expr1, operand1))
        }
        var operand2 = operand1.nextAvailable()
        if (operand2 >= Register.r10) operand2 = Register.r10
        val expr2 = generateExpr(binOp.expr2, operand2)
        list.addAll(expr2)

        var dstRegister = operand1
        if (pop) {
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
                list.add(Compare(operand2, ArithmeticShiftRight(operand1, 31)))
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
                list.add(Move(dstRegister, ImmOp(1), Conditions.EQ))
                list.add(Move(dstRegister, ImmOp(0), Conditions.NE))
            }
            BinOp.NEQ -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, ImmOp(1), Conditions.NE))
                list.add(Move(dstRegister, ImmOp(0), Conditions.EQ))
            }
            BinOp.LT -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, ImmOp(1), Conditions.LT))
                list.add(Move(dstRegister, ImmOp(0), Conditions.GE))
            }
            BinOp.GT -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, ImmOp(1), Conditions.GT))
                list.add(Move(dstRegister, ImmOp(0), Conditions.LE))
            }
            BinOp.GTE -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, ImmOp(1), Conditions.GE))
                list.add(Move(dstRegister, ImmOp(0), Conditions.LT))
            }
            BinOp.LTE -> {
                list.add(Compare(operand1, operand2))
                list.add(Move(dstRegister, ImmOp(1), Conditions.LE))
                list.add(Move(dstRegister, ImmOp(0), Conditions.GT))
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

    // Handles growing of stack beyond MAX_STACK_SIZE
    private fun growStack(offset: Int): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        for (i in 1 .. offset / MAX_SIZE) {
            instructions.add(Sub(Register.sp, Register.sp, ImmOp(MAX_SIZE)))
        }
        instructions.add(Sub(Register.sp, Register.sp, ImmOp(offset % MAX_SIZE)))
        return instructions
    }

    // Handles restoring of stack beyond MAX_STACK_SIZE
    private fun shrinkStack(offset: Int): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        for (i in 1 .. offset / MAX_SIZE) {
            instructions.add(Add(Register.sp, Register.sp, ImmOp(MAX_SIZE)))
        }
        instructions.add(Add(Register.sp, Register.sp, ImmOp(offset % MAX_SIZE)))
        return instructions
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
            is PairElemNode -> getType(expr.expr)
            else -> {
                throw Error("Should not get here")
            }
        }
    }
}

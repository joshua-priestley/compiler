package compiler.CodeGen

import AST.*
import antlr.WACCParser
import compiler.CodeGen.Instructions.ARM.*
import compiler.CodeGen.Instructions.External.*
import compiler.CodeGen.Instructions.Operators.*
import compiler.Instructions.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class CodeGeneration(private var globalSymbolTable: SymbolTable) {
    // Global Variable to know which symbol table to step into
    private val currentSymbolID = AtomicInteger()

    // Booleans to know at which section of code we are in
    private var inElseStatement = false
    private var assign = false
    private var printing = false

    private var endLabel = Stack<String>()
    private var conditionLabel = Stack<String>()

    // Counter for the extra stack value we need to add when in different scopes
    private var stackToAdd = 0

    // Counter for the next available label
    private var labelCounter = 0

    private val data: DataSegment = DataSegment()
    private val predefined: PredefinedFuncs = PredefinedFuncs(data)


    companion object {
        private const val MAX_SIZE = 1024
        private const val TRUE_VAL = 1
        private const val FALSE_VAL = 0
        private const val BOOL_CHAR_SIZE = 1
        private const val INT_STR_SIZE = 4
        private const val REFERENCE_SIZE = 4    // Pairs and Arrays size
        private const val PAIR_SIZE = 8
    }

    //------------------------------------------------
    //            Start the Assembly
    //------------------------------------------------

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

    //------------------------------------------------
    //            Generate Function Nodes
    //------------------------------------------------

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

    //------------------------------------------------
    //            Generate Statement Nodes
    //------------------------------------------------

    private fun generateStat(stat: StatementNode): List<Instruction> {
        return when (stat) {
            is SkipNode -> generateSkip()
            is DeclarationNode -> generateDeclaration(stat)
            is AssignNode -> generateAssign(stat)
            is ReadNode -> generateRead(stat)
            is FreeNode -> generateFree(stat)
            is ReturnNode -> generateReturn(stat)
            is ExitNode -> generateExit(stat)
            is PrintNode -> generatePrint(stat)
            is PrintlnNode -> generatePrintln(stat)
            is IfElseNode -> generateIf(stat)
            is WhileNode -> generateWhile(stat)
            is BeginEndNode -> generateBegin(stat)
            is SequenceNode -> generateSeq(stat)
            is SideExpressionNode -> generateSideExpression(stat)
            is BreakNode -> generateBreak()
            is ContinueNode -> generateContinue()
            else -> throw Error("Should not get here")
        }
    }

    private fun generateBreak(): List<Instruction> {
        return listOf(Branch(endLabel.peek(), false))
    }

    private fun generateContinue(): List<Instruction> {
        return listOf(Branch(conditionLabel.peek(), false))
    }

    private fun generateSideExpression(stat: SideExpressionNode): List<Instruction> {
        val rhsConversion = when (stat.sideExpr) {
            is AddOneNode -> RHSExprNode(BinaryOpNode(BinOp.PLUS, stat.ident.ident, IntLiterNode("1")))
            is SubOneNode -> RHSExprNode(BinaryOpNode(BinOp.MINUS, stat.ident.ident, IntLiterNode("1")))
            is AddNNode -> RHSExprNode(BinaryOpNode(BinOp.PLUS, stat.ident.ident, stat.sideExpr.value))
            is SubNNode -> RHSExprNode(BinaryOpNode(BinOp.MINUS, stat.ident.ident, stat.sideExpr.value))
            is MulNNode -> RHSExprNode(BinaryOpNode(BinOp.MUL, stat.ident.ident, stat.sideExpr.value))
            is DivNNode -> RHSExprNode(BinaryOpNode(BinOp.DIV, stat.ident.ident, stat.sideExpr.value))
            else -> throw Error("Should not get here")
        }
        val assignConversion = AssignNode(stat.ident, rhsConversion)
        return generateAssign(assignConversion)
    }

    private fun generateBegin(stat: BeginEndNode): List<Instruction> {
        val beginInstructions = mutableListOf<Instruction>()
        stackToAdd += globalSymbolTable.localStackSize()
        enterNewScope(beginInstructions, stat.stat)
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
                readInstructions.add(Add(Register.r4, Register.sp, ImmOp(offset)))
                stat.lhs.ident
            }
            is LHSArrayElemNode -> {
                val offset = getStackOffsetValue(stat.lhs.arrayElem.ident.toString())
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

    private fun generateCallNode(call: RHSCallNode): List<Instruction> {
        val callInstructions = mutableListOf<Instruction>()
        var totalOffset = 0
        if (!call.argList.isNullOrEmpty()) {
            val parameters = call.argList.reversed()

            for (param in parameters) {
                callInstructions.addAll(generateExpr(param))
                val offset = getExprOffset(param)
                totalOffset += offset
                assert(offset != 0)
                val byte = offset == BOOL_CHAR_SIZE
                callInstructions.add(Store(Register.r4, Register.sp, offset * -1, parameter = true, byte = byte))
                globalSymbolTable.addToOffset(offset)
            }
        }
        globalSymbolTable.subFromOffset(totalOffset)

        val functionName = "f_${call.ident.name}"
        callInstructions.add(Branch(functionName, true))
        if (totalOffset != 0) callInstructions.add(Add(Register.sp, Register.sp, ImmOp(totalOffset)))

        callInstructions.add(Move(Register.r4, Register.r0))

        return callInstructions
    }

    private fun generateAssign(stat: AssignNode, reg: Register = Register.r5): List<Instruction> {
        assign = true
        val assignInstructions = mutableListOf<Instruction>()
        if (stat.rhs is RHSExprNode) {
            val newType = getType(stat.rhs.expr)
            if (stat.lhs is AssignLHSIdentNode) {
                val oldType = globalSymbolTable.getNodeGlobal(stat.lhs.ident.toString())!!
                if (newType != oldType) {
                    globalSymbolTable.getNodeGlobal(stat.lhs.ident.toString(), newType)!!
                    assignInstructions.addAll(generateRHSNode(stat.rhs, reg.nextAvailable()))
                    assignInstructions.addAll(generateLHSAssign(stat.lhs, reg))
                    globalSymbolTable.getNodeGlobal(stat.lhs.ident.toString(), oldType)!!
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

    private fun generateElseIf(instructions: MutableList<Instruction>, elseIf: ElseIfNode, nextElse: String?, endLabel: String): Boolean {
        if (elseIf.expr is BoolLiterNode && elseIf.expr.value == "false") {
            return false
        }

        val alwaysTrue = elseIf.expr is BoolLiterNode && elseIf.expr.value == "true"

        if (!alwaysTrue) {
            assign = true
            if (elseIf.expr is LiterNode) {
                instructions.addAll(generateLiterNode(elseIf.expr, Register.r4))
            } else {
                instructions.addAll(generateExpr(elseIf.expr))
            }
            assign = false
            // Compare the conditional
            instructions.add(Compare(Register.r4, ImmOp(0)))
            if (nextElse != null) {
                instructions.add(Branch(nextElse, false, Conditions.EQ))
            } else {
                instructions.add(Branch(endLabel, false, Conditions.EQ))
            }
        }

        enterNewScope(instructions, elseIf.then)
        if (!alwaysTrue) {
            instructions.add(Branch(endLabel, false))
        }

        return alwaysTrue
    }

    private fun generateIf(stat: IfElseNode): List<Instruction> {
        val ifInstruction = mutableListOf<Instruction>()

        val doElse = stat.expr is BoolLiterNode && stat.expr.value == "false" || stat.expr is BinaryOpNode && constantEvaluation(stat.expr) == FALSE_VAL
        var doIf = stat.expr is BoolLiterNode && stat.expr.value == "true" || stat.expr is BinaryOpNode && constantEvaluation(stat.expr) == TRUE_VAL

        var elseLabel = ""
        var firstElseIfLabel = ""
        val endLabel = nextLabel()

        if (stat.else_ != null) {
            elseLabel = nextLabel()
        }

        if (stat.elseIfs.isNotEmpty()) {
            firstElseIfLabel = nextLabel()
        }

        if (!doElse && !doIf) {
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
            if (stat.elseIfs.isNotEmpty()) {
                ifInstruction.add(Branch(firstElseIfLabel, false, Conditions.EQ))
            } else if (stat.else_ != null) {
                ifInstruction.add(Branch(elseLabel, false, Conditions.EQ))
            } else {
                ifInstruction.add(Branch(endLabel, false, Conditions.EQ))
            }
        }

        if (!doElse) {
            // Then Branch
            stackToAdd += globalSymbolTable.localStackSize()
            enterNewScope(ifInstruction, stat.then)
            if (stat.else_ != null) {
                ifInstruction.add(Branch(endLabel, false))
            }
        }

        if (!doIf && firstElseIfLabel != "") {
            ifInstruction.add(FunctionDeclaration(firstElseIfLabel))
            for (i in stat.elseIfs.indices) {
                val nextElse: String? = if (i != stat.elseIfs.size - 1) {
                    nextLabel()
                } else if (stat.else_ != null) {
                    elseLabel
                } else {
                    null
                }
                doIf = generateElseIf(ifInstruction, stat.elseIfs[i], nextElse, endLabel)
                if (nextElse != null && nextElse != elseLabel && nextElse != "") {
                    ifInstruction.add(FunctionDeclaration(nextElse))
                }
                if (doIf) {
                    break
                }
            }
        }

        if (stat.else_ != null && !doIf) {
            inElseStatement = true
            // Else Branch
            if (elseLabel != "") {
                ifInstruction.add(FunctionDeclaration(elseLabel))
            }
            enterNewScope(ifInstruction, stat.else_)
            stackToAdd -= globalSymbolTable.localStackSize()
        }

        ifInstruction.add(FunctionDeclaration(endLabel))

        inElseStatement = false
        return ifInstruction
    }

    private fun generateWhile(stat: WhileNode): List<Instruction> {
        val whileInstruction = mutableListOf<Instruction>()

        if (stat.expr is BoolLiterNode && stat.expr.value == "false" || stat.expr is BinaryOpNode && constantEvaluation(stat.expr) == FALSE_VAL) {
            return whileInstruction
        }
        val forever = stat.expr is BoolLiterNode && stat.expr.value == "true" || stat.expr is BinaryOpNode && constantEvaluation(stat.expr) == TRUE_VAL

        conditionLabel.push(nextLabel())
        val bodyLabel = nextLabel()
        endLabel.push(nextLabel())

        whileInstruction.add(Branch(conditionLabel.peek(), false))

        // Loop body
        whileInstruction.add(FunctionDeclaration(bodyLabel))
        if (!inElseStatement) stackToAdd += globalSymbolTable.localStackSize()
        enterNewScope(whileInstruction, stat.do_)
        if (!inElseStatement) stackToAdd -= globalSymbolTable.localStackSize()

        // Conditional
        whileInstruction.add(FunctionDeclaration(conditionLabel.peek()))
        if (forever) {
            whileInstruction.add(Branch(bodyLabel, false))
        } else {
            assign = true
            if (stat.expr is LiterNode) {
                whileInstruction.addAll(generateLiterNode(stat.expr, Register.r4))
            } else {
                whileInstruction.addAll(generateExpr(stat.expr))
            }
            whileInstruction.add(Compare(Register.r4, ImmOp(1)))
            whileInstruction.add(Branch(bodyLabel, false, Conditions.EQ))
            assign = false
        }
        whileInstruction.add(FunctionDeclaration(endLabel.peek()))

        endLabel.pop()
        conditionLabel.pop()

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

    //------------------------------------------------
    //            Generate Expression Nodes
    //------------------------------------------------

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
        pairElemInstructions.add(Load(Register.r0, getExprOffset(elem)))
        pairElemInstructions.add(Branch("malloc", true))
        pairElemInstructions.add(Store(Register.r5, Register.r0, byte = (getExprOffset(elem) == 1)))
        pairElemInstructions.add(Store(Register.r0, Register.r4, if (!second) 0 else 4))
        return pairElemInstructions
    }

    private fun generateNewPair(pair: RHSNewPairNode): List<Instruction> {
        val newPairInstructions = mutableListOf<Instruction>()

        // Initialisation
        newPairInstructions.add(Load(Register.r0, PAIR_SIZE))
        newPairInstructions.add(Branch("malloc", true))
        newPairInstructions.add(Move(Register.r4, Register.r0))

        newPairInstructions.addAll(addPairElem(pair.expr1))
        newPairInstructions.addAll(addPairElem(pair.expr2, true))

        return newPairInstructions
    }

    private fun generateArrayLitNode(elements: List<ExprNode>, reg: Register = Register.r5): List<Instruction> {
        val arrayLitInstructions = mutableListOf<Instruction>()

        var typeSize = INT_STR_SIZE
        val arraySize = if (elements.isEmpty()) {
            REFERENCE_SIZE
        } else {
            typeSize = getExprOffset(elements[0])
            REFERENCE_SIZE + elements.size * typeSize
        }

        // CodeGen.Instructions for allocating space for array
        arrayLitInstructions.add(Load(Register.r0, arraySize))
        arrayLitInstructions.add(Branch("malloc", true))
        arrayLitInstructions.add(Move(Register.r4, Register.r0))

        // Add each element
        var count = 0
        for (expr in elements) {
            arrayLitInstructions.addAll(generateExpr(expr, reg))
            arrayLitInstructions.add(Store(reg, Register.r4, INT_STR_SIZE + count * typeSize, byte = typeSize == 1))
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

    private fun generateArrayElem(expr: ArrayElem, reg: Register): List<Instruction> {
        val arrayElemInstructions = mutableListOf<Instruction>()
        val offset = getStackOffsetValue(expr.ident.toString())
        val reg2 = reg.nextAvailable()
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

    private fun generateLiterNode(exprNode: ExprNode, dstRegister: Register): List<Instruction> {
        val loadInstruction = mutableListOf<Instruction>()
        when (exprNode) {
            is IntLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value.toInt()))
            }
            is HexLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value.replace("0X", "0x")))
            }
            is BinLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value.replace("0b", "2_").replace("0B", "2_")))
            }
            is OctLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value))
            }
            is StrLiterNode -> {
                data.addMessage(Message(exprNode.value))
                loadInstruction.add(Load(dstRegister, data.getLabel(exprNode.value)))
            }
            is CharLiterNode -> {
                if (exprNode.value == "\\0") {
                    loadInstruction.add(Move(dstRegister, ImmOp(0)))
                } else {
                    val char = if (exprNode.value.length == 2) exprNode.value[1] else exprNode.value[0]
                    loadInstruction.add(Move(dstRegister, CharOp(char)))
                }
            }
            is BoolLiterNode -> {
                loadInstruction.add(Move(dstRegister, if (exprNode.value == "true") {
                    ImmOp(TRUE_VAL)
                } else {
                    ImmOp(FALSE_VAL)
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

    private fun generateUnOp(unOp: UnaryOpNode, reg: Register = Register.r4): List<Instruction> {
        val unOpInstructs = mutableListOf<Instruction>()
        val expr = generateExpr(unOp.expr, reg)
        unOpInstructs.addAll(expr)
        when (unOp.operator) {
            //ORD and CHR are handled by print_int and print_char
            UnOp.NOT -> {
                unOpInstructs.add(Not(reg, reg))
            }
            UnOp.MINUS -> {
                unOpInstructs.add(Minus(reg))
                unOpInstructs.add(Branch(predefined.addFunc(Overflow()), true, Conditions.VS))
            }
            UnOp.LEN -> {
                unOpInstructs.add(Load(Register.r4, reg))
            }
            UnOp.BITWISENOT -> {
                unOpInstructs.add(Not(reg, reg))
            }
            else -> emptyList<Instruction>()
        }
        return unOpInstructs
    }

    private fun literToBool(value: String): Boolean {
        return value == "true"
    }

    private fun getConstEvalNested(expr: ExprNode): ExprNode? {
        if (expr is BinaryOpNode) {
            val value = constantEvaluation(expr)
            if (value == null) return value
            return if (expr.operator == BinOp.AND || expr.operator == BinOp.OR) {
                BoolLiterNode(if (value == TRUE_VAL) "true" else "false")
            } else {
                IntLiterNode(value.toString())
            }
        }
        return null
    }

    private fun constantEvaluation(binOp: BinaryOpNode): Int? {
        val lhsExpr = getConstEvalNested(binOp.expr1) ?: binOp.expr1
        val rhsExpr = getConstEvalNested(binOp.expr2) ?: binOp.expr2

        if (lhsExpr is IntLiterNode && rhsExpr is IntLiterNode) {
            return when (binOp.operator.value) {
                BinOp.PLUS.value -> lhsExpr.value.toInt() + rhsExpr.value.toInt()
                BinOp.MINUS.value -> lhsExpr.value.toInt() - rhsExpr.value.toInt()
                BinOp.MUL.value -> lhsExpr.value.toInt() * rhsExpr.value.toInt()
                BinOp.BITWISEAND.value -> lhsExpr.value.toInt() and rhsExpr.value.toInt()
                BinOp.BITWISEOR.value -> lhsExpr.value.toInt() or rhsExpr.value.toInt()
                else -> null
            }
        }
        if (lhsExpr is BoolLiterNode && rhsExpr is BoolLiterNode) {
            return when (binOp.operator.value) {
                BinOp.AND.value -> if (literToBool(lhsExpr.value) && literToBool(rhsExpr.value)) TRUE_VAL else FALSE_VAL
                BinOp.OR.value -> if (literToBool(lhsExpr.value) || literToBool(rhsExpr.value)) TRUE_VAL else FALSE_VAL
                else -> null
            }
        }

        return null
    }

    private fun generateBinOp(binOp: BinaryOpNode, reg: Register = Register.r4): List<Instruction> {
        val binOpInstructs = mutableListOf<Instruction>()
        var operand1 = reg
        var pop = false

        val const = constantEvaluation(binOp)
        if (const != null) {
            return listOf(Load(reg, const))
        }

        //If there are no registers left, use r10 and push onto the stack
        if (operand1 >= Register.r10) {
            pop = true
            operand1 = Register.r10
            binOpInstructs.addAll(generateExpr(binOp.expr1, operand1))
            binOpInstructs.add(Push(mutableListOf(Register.r10)))
        } else {
            binOpInstructs.addAll(generateExpr(binOp.expr1, operand1))
        }
        var operand2 = operand1.nextAvailable()
        if (operand2 >= Register.r10) operand2 = Register.r10
        val expr2 = generateExpr(binOp.expr2, operand2)
        binOpInstructs.addAll(expr2)

        var dstRegister = operand1
        //If pushed onto the stack, pop from the stack into r11
        if (pop) {
            binOpInstructs.add(Pop(mutableListOf(Register.r11)))
            dstRegister = operand2
            operand1 = Register.r11
        }

        when (binOp.operator) {

            //Arithmetic operators
            BinOp.PLUS -> {
                binOpInstructs.add(Add(dstRegister, operand1, operand2, true))
                binOpInstructs.add(Branch(predefined.addFunc(Overflow()), true, Conditions.VS))
            }
            BinOp.MINUS -> {
                binOpInstructs.add(Sub(dstRegister, operand1, operand2, true))
                binOpInstructs.add(Branch(predefined.addFunc(Overflow()), true, Conditions.VS))
            }
            BinOp.MUL -> {
                binOpInstructs.add(Multiply(operand1, operand2, operand1, operand2, true))
                binOpInstructs.add(Compare(operand2, ArithmeticShiftRight(operand1, 31)))
                binOpInstructs.add(Branch(predefined.addFunc(Overflow()), true, Conditions.NE))
            }

            //Boolean operators
            BinOp.AND -> {
                binOpInstructs.add(And(dstRegister, operand1, operand2))
            }
            BinOp.OR -> {
                binOpInstructs.add(Or(dstRegister, operand1, operand2))
            }

            //Comparisons
            BinOp.EQ -> {
                binOpInstructs.add(Compare(operand1, operand2))
                binOpInstructs.add(Move(dstRegister, ImmOp(TRUE_VAL), Conditions.EQ))
                binOpInstructs.add(Move(dstRegister, ImmOp(FALSE_VAL), Conditions.NE))
            }
            BinOp.NEQ -> {
                binOpInstructs.add(Compare(operand1, operand2))
                binOpInstructs.add(Move(dstRegister, ImmOp(TRUE_VAL), Conditions.NE))
                binOpInstructs.add(Move(dstRegister, ImmOp(FALSE_VAL), Conditions.EQ))
            }
            BinOp.LT -> {
                binOpInstructs.add(Compare(operand1, operand2))
                binOpInstructs.add(Move(dstRegister, ImmOp(TRUE_VAL), Conditions.LT))
                binOpInstructs.add(Move(dstRegister, ImmOp(FALSE_VAL), Conditions.GE))
            }
            BinOp.GT -> {
                binOpInstructs.add(Compare(operand1, operand2))
                binOpInstructs.add(Move(dstRegister, ImmOp(TRUE_VAL), Conditions.GT))
                binOpInstructs.add(Move(dstRegister, ImmOp(FALSE_VAL), Conditions.LE))
            }
            BinOp.GTE -> {
                binOpInstructs.add(Compare(operand1, operand2))
                binOpInstructs.add(Move(dstRegister, ImmOp(TRUE_VAL), Conditions.GE))
                binOpInstructs.add(Move(dstRegister, ImmOp(FALSE_VAL), Conditions.LT))
            }
            BinOp.LTE -> {
                binOpInstructs.add(Compare(operand1, operand2))
                binOpInstructs.add(Move(dstRegister, ImmOp(TRUE_VAL), Conditions.LE))
                binOpInstructs.add(Move(dstRegister, ImmOp(FALSE_VAL), Conditions.GT))
            }
            //MOD and DIV are handled by external libraries
            BinOp.MOD -> {
                binOpInstructs.add(Move(Register.r0, operand1))
                binOpInstructs.add(Move(Register.r1, operand2))
                val funcName = predefined.addFunc(DivideByZero())
                binOpInstructs.add(Branch(funcName, true))
                binOpInstructs.add(Branch("__aeabi_idivmod", true))
                binOpInstructs.add(Move(dstRegister, Register.r1))
            }
            BinOp.DIV -> {
                binOpInstructs.add(Move(Register.r0, operand1))
                binOpInstructs.add(Move(Register.r1, operand2))
                val funcName = predefined.addFunc(DivideByZero())
                binOpInstructs.add(Branch(funcName, true))
                binOpInstructs.add(Branch("__aeabi_idiv", true))
                binOpInstructs.add(Move(dstRegister, Register.r0))
            }
            BinOp.BITWISEAND -> {
                binOpInstructs.add(And(dstRegister, operand1, operand2))
            }
            BinOp.BITWISEOR -> {
                binOpInstructs.add(Or(dstRegister, operand1, operand2))
            }
            else -> {
                throw Error("Should not get here")
            }
        }
        return binOpInstructs

    }

    // ---------------------------------------------------------
    //                      Stack Functions
    // ---------------------------------------------------------

    // Handles growing of stack beyond MAX_STACK_SIZE
    private fun growStack(offset: Int): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        for (i in 1..offset / MAX_SIZE) {
            instructions.add(Sub(Register.sp, Register.sp, ImmOp(MAX_SIZE)))
        }
        instructions.add(Sub(Register.sp, Register.sp, ImmOp(offset % MAX_SIZE)))
        return instructions
    }

    // Handles restoring of stack beyond MAX_STACK_SIZE
    private fun shrinkStack(offset: Int): List<Instruction> {
        val instructions = mutableListOf<Instruction>()
        for (i in 1..offset / MAX_SIZE) {
            instructions.add(Add(Register.sp, Register.sp, ImmOp(MAX_SIZE)))
        }
        instructions.add(Add(Register.sp, Register.sp, ImmOp(offset % MAX_SIZE)))
        return instructions
    }

    private fun getStackOffsetValue(name: String): Int {
        var totalOffset = 0
        if (globalSymbolTable.getNodeGlobal(name)!!.isParameter()) {
            totalOffset += globalSymbolTable.getStackOffset(name)
            if (globalSymbolTable.containsNodeLocal(name)) {
                totalOffset += globalSymbolTable.localStackSize()
            }
            if (!inElseStatement && assign && !globalSymbolTable.containsNodeLocal(name)) {
                totalOffset += stackToAdd
            }
        } else {
            totalOffset += globalSymbolTable.localStackSize()
            totalOffset -= globalSymbolTable.getStackOffset(name)
            if (assign && !globalSymbolTable.containsNodeLocal(name)) {
                totalOffset += stackToAdd
            }
        }
        return totalOffset
    }

    private fun getExprOffset(expr: ExprNode): Int {
        return when (expr) {
            is PairLiterNode -> REFERENCE_SIZE
            is IntLiterNode -> INT_STR_SIZE
            is StrLiterNode -> INT_STR_SIZE
            is CharLiterNode -> BOOL_CHAR_SIZE
            is BoolLiterNode -> BOOL_CHAR_SIZE
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

    // ---------------------------------------------------------
    //                     Utility Functions
    // ---------------------------------------------------------

    private fun getType(expr: ExprNode): Type? {
        return when (expr) {
            is IntLiterNode -> Type(WACCParser.INT)
            is StrLiterNode -> Type(WACCParser.STRING)
            is BoolLiterNode -> Type(WACCParser.BOOL)
            is CharLiterNode -> Type(WACCParser.CHAR)
            is Ident -> globalSymbolTable.getNodeGlobal(expr.toString())
            is ArrayElem -> globalSymbolTable.getNodeGlobal(expr.ident.toString())?.getBaseType()
                    ?: Type(INVALID)
            is UnaryOpNode -> Type.unaryOpsProduces(expr.operator.value)
            is BinaryOpNode -> Type.binaryOpsProduces(expr.operator.value)
            is PairLiterNode -> Type(PAIR_LITER)
            is PairElemNode -> getType(expr.expr)
            else -> {
                throw Error("Should not get here")
            }
        }
    }

    private fun enterNewScope(instructionList: MutableList<Instruction>, stat: StatementNode) {
        globalSymbolTable = globalSymbolTable.getChildTable(currentSymbolID.incrementAndGet())!!
        stackToAdd += globalSymbolTable.localStackSize()
        if (globalSymbolTable.localStackSize() > 0) instructionList.addAll(growStack(globalSymbolTable.localStackSize()))
        instructionList.addAll(generateStat(stat))
        if (globalSymbolTable.localStackSize() > 0) instructionList.addAll(shrinkStack(globalSymbolTable.localStackSize()))
        stackToAdd -= globalSymbolTable.localStackSize()
        globalSymbolTable = globalSymbolTable.parentT!!
    }

    private fun nextLabel(): String {
        return "L${labelCounter++}"
    }
}

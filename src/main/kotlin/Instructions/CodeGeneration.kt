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
        globalSymbolTable.printEntries()
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
        instructions.add(Push(listOf(Register.LR)))

        val spOffset = globalSymbolTable.localStackSize()

        if (spOffset > 0 && main) {
            instructions.add(Sub(Register.SP, Register.SP, spOffset))
        }

        // Generate all the statements
        instructions.addAll(generateStat(stat))

        if (spOffset > 0 && main) {
            instructions.add(Add(Register.SP, Register.SP, spOffset))
        }

        // LDR r0, =0
        if (main) {
            instructions.add(Load(Register.R0, "0"))
        }

        // POP {pc}
        instructions.add(Pop(listOf(Register.PC)))
        if (!main) {
            instructions.add(Pop(listOf(Register.PC)))
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
            else -> generateSeq(stat as SequenceNode) // SequenceNode

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
        printInstruction.add(Move(Register.R0, Register.R4))

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
        return when(expr) {
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
            callInstructions.add(Store(Register.R4, Register.SP, offset * -1, parameter = true, byte = byte))
        }

        val functionName = "f_${call.ident.name}"
        callInstructions.add(Branch(functionName, true))
        if (totalOffset != 0) callInstructions.add(Add(Register.SP, Register.SP, totalOffset))

        return callInstructions
    }

    private fun generateAssign(stat: AssignNode): List<Instruction> {
        val assignInstructions = mutableListOf<Instruction>()

        if (stat.rhs is RHSCallNode) {
            assignInstructions.addAll(generateCallNode(stat.rhs))
        }

        return assignInstructions
    }

    private fun generateDeclaration(stat: DeclarationNode): List<Instruction> {
        val declareInstructions = mutableListOf<Instruction>()

        if (stat.value is RHSCallNode) {
            declareInstructions.addAll(generateCallNode(stat.value))
        }

        return declareInstructions    }

    private fun generateIf(stat: IfElseNode): List<Instruction> {
        val ifInstruction = mutableListOf<Instruction>()

        val elseLabel = nextLabel()
        val endLabel = nextLabel()

        // Load up the conditional
        if (stat.expr is LiterNode) {
            ifInstruction.addAll(generateLiterNode(stat.expr, Register.R4))
        } else {
            ifInstruction.addAll(generateExpr(stat.expr))
        }

        // Compare the conditional
        ifInstruction.add(Compare(Register.R4, 0))
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
            whileInstruction.addAll(generateLiterNode(stat.expr, Register.R4))
        } else {
            whileInstruction.addAll(generateExpr(stat.expr))
        }
        whileInstruction.add(Compare(Register.R4, 1))
        whileInstruction.add(Branch(bodyLabel, false, Conditions.EQ))

        return whileInstruction
    }

    private fun generateReturn(stat: ReturnNode): List<Instruction> {
        val returnInstruction = mutableListOf<Instruction>()

        if (stat.expr is LiterNode) {
            returnInstruction.addAll(generateLiterNode(stat.expr, Register.R4))
        } else {
            returnInstruction.addAll(generateExpr(stat.expr))
        }

        returnInstruction.add(Move(Register.R0, Register.R4))
        return returnInstruction
    }

    private fun generateExpr(expr: ExprNode, reg: Register = Register.R4): List<Instruction> {
        return when (expr) {
            is LiterNode -> generateLiterNode(expr, reg)
            is BinaryOpNode -> generateBinOp(expr, reg)
            else -> emptyList()
        }
    }

    private fun generateSeq(stat: SequenceNode): List<Instruction> {
        val sequenceInstruction = mutableListOf<Instruction>()

        stat.statList.map { generateStat(it) }.forEach { sequenceInstruction.addAll(it) }
        return sequenceInstruction
    }

    private fun generateFree(stat: FreeNode): List<Instruction> {
        val freeInstruction = mutableListOf<Instruction>()

        freeInstruction.addAll(generateLiterNode(stat.expr, Register.R4))
        freeInstruction.add(Move(Register.R0, Register.R4))

        val funcName = predefined.addFunc(Freepair())
        freeInstruction.add(Branch(funcName, true))

        return freeInstruction
    }

    private fun generateExit(exitNode: ExitNode): List<Instruction> {
        val exitInstruction = mutableListOf<Instruction>()

        if (exitNode.expr is IntLiterNode) {
            exitInstruction.add(Load(Register.R4, exitNode.expr.value))
        } else if (exitNode.expr is Ident) {
            // TODO
            // Get variable's value from stack
        }

        exitInstruction.add(Move(Register.R0, Register.R4))
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
                loadInstruction.add(Load(dstRegister, exprNode.value))
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
                val offset = globalSymbolTable.localStackSize() - globalSymbolTable.getStackOffset(exprNode.toString())
                loadInstruction.add(Load(dstRegister, Register.SP, offset))
            }
        }
        return loadInstruction
    }

    private fun nextLabel(): String {
        return "L${labelCounter++}"
    }

    private fun generateBinOp(binOp: BinaryOpNode, reg: Register = Register.R4): List<Instruction> {
        val list = mutableListOf<Instruction>()
        val operand2 = reg.nextAvailable()
        val expr1 = generateExpr(binOp.expr1, reg)
        val expr2 = generateExpr(binOp.expr2, operand2)
        list.addAll(expr1)
        list.addAll(expr2)

        when (binOp.operator) {
            BinOp.PLUS -> {

                list.add(AddSub("ADD", reg, reg, operand2, true))
                list.add(Branch(predefined.addFunc(Overflow()), true, Conditions.NE))
            }
            BinOp.MINUS -> {
                list.add(AddSub("SUB", reg, reg, operand2, true))
                list.add(Branch(predefined.addFunc(Overflow()), true, Conditions.NE))
            }
            BinOp.MUL -> {
                list.add(Multiply(reg, operand2, reg, operand2, true))
                list.add(Compare(operand2, reg, null, ArithmeticShiftRight(31)))
                list.add(Branch(predefined.addFunc(Overflow()), true, Conditions.NE))
            }
            else -> {
                // TODO
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
                // TODO fix npe to do with scoping in:
                // wacc_examples/valid/scope/printAllTypes.wacc
                // set global symbol table to child when visiting begin/end node?
                // but which child table do we set it to?
                return type?.getBaseType() ?: Type(INVALID)
            }
            is UnaryOpNode -> Type.unaryOpsProduces(expr.operator.value)
            is BinaryOpNode -> Type.binaryOpsProduces(expr.operator.value)
            is PairLiterNode -> Type(PAIR_LITER)
            else -> {
                println("Shouldn't get here")
                return Type(INVALID)
            }
        }
    }
}
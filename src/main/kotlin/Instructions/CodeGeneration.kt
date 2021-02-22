package compiler.Instructions

import AST.*

class CodeGeneration(private var globalSymbolTable: SymbolTable) {

    fun generateProgram(program: ProgramNode): List<Instruction> {
        // Generate Data Segments
        val dataSegmentInstructions = mutableListOf<Instruction>()

        val labelInstructions = mutableListOf<Instruction>()
        labelInstructions.add(GlobalLabel("text"))
        labelInstructions.add(GlobalLabel("glboal main"))

        // Generate the functions
        val funcInstructions = mutableListOf<Instruction>()
        for (func in program.funcs) {
            funcInstructions.addAll(generateFunction(func))
        }

        // Generate the main function
        val mainInstructions = mutableListOf<Instruction>()
        // main:
        mainInstructions.add(FunctionDeclaration("main"))

        functionBodyInstructions(mainInstructions, program.stat)

        return dataSegmentInstructions +
                labelInstructions +
                funcInstructions +
                mainInstructions
    }

    private fun generateFunction(function: FunctionNode): List<Instruction> {
        val instructions = mutableListOf<Instruction>()

        // f_NAME:
        instructions.add(FunctionDeclaration("f_${function.ident.name}"))

        // Rest follows same format as the main function
        functionBodyInstructions(instructions, function.stat)

        return instructions
    }

    private fun functionBodyInstructions(instructions: MutableList<Instruction>, stat: StatementNode) {
        // PUSH {lr}
        instructions.add(Push(listOf(Register.LR)))

        // Generate all the statements
        instructions.addAll(generateStat(stat))

        // LDR r0, =0
        instructions.add(Load(Register.R0, "0"))

        // POP {pc}
        instructions.add(Pop(listOf(Register.PC)))

        // .ltorg
        instructions.add(LocalLabel("ltorg"))
    }

    private fun generateStat(stat: StatementNode): List<Instruction> {
        return when (stat) {
            is SkipNode -> generateSkip()
            is ExitNode -> generateExit(stat)
            is FreeNode -> generateFree(stat)
            is SequenceNode -> generateSeq(stat)
            else -> mutableListOf()
        }
    }

    private fun generateSeq(stat: SequenceNode): List<Instruction> {
        val sequenceInstruction = mutableListOf<Instruction>()

        sequenceInstruction.addAll(generateStat(stat.stat1))
        sequenceInstruction.addAll(generateStat(stat.stat2))

        return sequenceInstruction
    }

    private fun generateFree(stat: FreeNode): List<Instruction> {
        val freeInstruction = mutableListOf<Instruction>()

        freeInstruction.addAll(generateIterLoad(stat.expr, Register.R4))
        freeInstruction.add(Move(Register.R0, Register.R4))

        // TODO: Library Calls
        freeInstruction.add(Branch("", Conditions.L))

        return freeInstruction
    }

    private fun generateExit(exitNode: ExitNode): List<Instruction> {
        val exitInstruction = mutableListOf<Instruction>()

        if (exitNode.expr is IntLiterNode) {
            exitInstruction.add(Load(Register.R4, exitNode.expr.value))
        } else if (exitNode.expr is Ident) {
            // Get variable's value from stack
        }

        exitInstruction.add(Move(Register.R0, Register.R4))
        exitInstruction.add(Branch("exit", Conditions.L))

        return exitInstruction
    }

    private fun generateSkip(): List<Instruction> {
        return mutableListOf()
    }

    private fun generateIterLoad(exprNode: ExprNode, dstRegister: Register): List<Instruction> {
        val loadInstruction = mutableListOf<Instruction>()
        when (exprNode) {
            is IntLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value))
            }
            is StrLiterNode -> {
                // TODO: Data segment stuff
            }
            is CharLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value))
            }
            is BoolLiterNode -> {
                loadInstruction.add(Load(dstRegister, exprNode.value))
            }
            is Ident -> {
                // TODO: Stack offset stuff
            }
        }
        return loadInstruction
    }

}
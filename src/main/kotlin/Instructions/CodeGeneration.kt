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
        return mutableListOf()
    }

}
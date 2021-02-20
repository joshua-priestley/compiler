package compiler.Instructions

import AST.Node
import AST.SymbolTable

class CodeGeneration(private var globalSymbolTable: SymbolTable) {

    fun generate(node: Node): List<Instruction> {
        return mutableListOf()
    }

}
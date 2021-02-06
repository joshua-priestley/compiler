class SymbolTable(val parentT: SymbolTable?) {
    val functions = HashMap<Ident, FunctionNode>()
    val variables = HashMap<Ident, TypeNode>()
    val table = HashMap<Ident, AssignRHSNode>()

    fun addFunction(function: FunctionNode) {
        if (functions.containsKey(function.ident)) {
            println("Function already exists")
        }
        functions[function.ident] = function
    }

    fun addVariable(variable: DeclarationNode) {
        if (variables.containsKey(variable.ident)) {
            println("Variable already exists")
        }
        variables[variable.ident] = variable.type
        table[variable.ident] = variable.value
    }

    fun getType(ident: Ident): TypeNode? {
        if (variables.containsKey(ident)) {
            return variables[ident]
        }

        return parentT?.getType(ident)
    }

    fun getExpr(ident: Ident): AssignRHSNode? {
        if (table.containsKey(ident)) {
            return table[ident]
        }

        return parentT?.getExpr(ident)
    }

}
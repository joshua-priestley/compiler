class SymbolTable(val parentT: SymbolTable?) {
    val table: LinkedHashMap<String, Node> = linkedMapOf()
    val childrenTables: MutableList<SymbolTable> = mutableListOf()

    init {
        parentT?.addChildTable(this)
    }

    fun addChildTable(child: SymbolTable) {
        childrenTables.add(child)
    }

    fun addNode(name: String, node: Node) {
        table[name] = node
    }

    fun getNode(name: String): Node? {
        var currTable: SymbolTable? = this
        while (currTable != null) {
            val node = table[name]
            if (node != null) {
                return node
            }

            currTable = currTable.parentT
        }

        return null
    }

    fun getGlobalTable(): SymbolTable {
        var currTable = this
        while (currTable.parentT != null) {
            currTable = currTable.parentT!!
        }
        return currTable
    }
}
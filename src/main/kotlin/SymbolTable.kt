class SymbolTable(var parentT: SymbolTable?) {
    private val table: LinkedHashMap<String, Node> = linkedMapOf()
    private val childrenTables: MutableList<SymbolTable> = mutableListOf()

    init {
        parentT?.addChildTable(this)
    }

    fun addChildTable(child: SymbolTable) {
        childrenTables.add(child)
    }

    fun setParentTable(parentT: SymbolTable?) {
        this.parentT = parentT
    }

    fun addNode(name: String, node: Node) {
        table[name] = node
    }

    fun getNode(name: String): Node? {
        var currTable: SymbolTable? = this
        while (currTable != null) {
            val node = currTable.table[name]
            if (node != null) {
                return node
            }

            currTable = currTable.parentT
        }

        return null
    }

    fun removeNode(name: String) {
        var currTable: SymbolTable? = this
        while (currTable!= null) {
            if (currTable.table.containsKey(name)) {
                currTable.table.remove(name)
                return
            }
            currTable = currTable.parentT
        }
    }

    fun getGlobalTable(): SymbolTable {
        var currTable = this
        while (currTable.parentT != null) {
            currTable = currTable.parentT!!
        }
        return currTable
    }
}
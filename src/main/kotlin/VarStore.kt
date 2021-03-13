package compiler

class VarStore {
    val parent: VarStore?
    val varStore: MutableMap<String, Any>

    constructor() {
        parent = null
        varStore = HashMap()
    }

    constructor(parent: VarStore) {
        this.parent = parent
        this.varStore = HashMap()
    }

    fun newScope(): VarStore =
        VarStore(this)

    fun exitScope(): VarStore =
        parent!!


    // Function to check the type to read into
    fun typeIsInt(id: String): Boolean {
        var scope = this
        while (scope.varStore[id] == null) {
            scope = scope.parent!!
        }
        return scope.varStore[id] is Int
    }

    fun assignBaseValue(id: String, value: Any) {
        var scope = this
        while (scope.varStore[id] == null) {
            scope = scope.parent!!
        }
        scope.varStore[id] = value
    }

    fun declareBaseValue(id: String, value: Any) {
        varStore[id] = value
    }

    // Returns object or basic type of identifier
    fun getValue(id: String): Any {
        var scope = this
        while (scope.varStore[id] == null) {
            scope = scope.parent!!
        }
        return varStore[id]!!
    }
}
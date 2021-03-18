package compiler.interpreter

// Stores values of variables between execution of statements in the shell
class VarStore {
    private val parent: VarStore?
    val varStore: MutableMap<String, Any>
    private val funcStore: MutableMap<String, List<String>?>

    constructor() {
        parent = null
        varStore = HashMap()
        funcStore = HashMap()
    }

    constructor(parent: VarStore) {
        this.parent = parent
        this.varStore = HashMap()
        this.funcStore = HashMap()
    }

    fun newScope(): VarStore =
        VarStore(this)

    fun exitScope(): VarStore =
        parent!!

    // Adds the parsed arguments to the variable store of the function
    fun enterFunction(arguments: List<Any>, params: List<String>): VarStore {
        val newStore = VarStore(this)
        for (i in arguments.indices) {
            //println("param: ${params[i]} arg: ${arguments[i]}")
            newStore.declareBaseValue(params[i], arguments[i])
        }
        return newStore
    }

    // Function to check the type to read into
    fun typeIsInt(id: String): Boolean {
        var scope = this
        while (scope.varStore[id] == null) {
            scope = scope.parent!!
        }
        return scope.varStore[id] is Int
    }

    // Assign a value to existing variable in current or parent scope(s)
    fun assignBaseValue(id: String, value: Any) {
        var scope = this
        while (scope.varStore[id] == null) {
            scope = scope.parent!!
        }
        scope.varStore[id] = value
    }

    // Declare a new value in the current scope
    fun declareBaseValue(id: String, value: Any) {
        varStore[id] = value
    }

    // Returns object or basic type of identifier
    fun getValue(id: String): Any {
        var scope = this
        while (scope.varStore[id] == null) {
            scope = scope.parent!!
        }
        return scope.varStore[id]!!
    }
}
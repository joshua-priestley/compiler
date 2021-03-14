package compiler

class VarStore {
    val parent: VarStore?
    val varStore: MutableMap<String, Any>
    val funcStore: MutableMap<String, List<String>?>

    //TODO - do we need the boolean to get scoping right or is it caught by semantic checks?
    /* use to handle scoping correctly for functions ??
     * Allows users to add functions in shell at any point */
    var isFuncOutermost = false

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
    fun enterFunction(arguments: List<Any>, params: List<String>): VarStore{
        val newStore = VarStore(this)
        for (i in arguments.indices) {

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
        return scope.varStore[id]!!
    }
}
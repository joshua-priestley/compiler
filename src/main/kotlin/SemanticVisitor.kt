import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import kotlin.system.exitProcess

class SemanticVisitor(val globalSymbolTable: SymbolTable): WACCParserBaseVisitor<TypeNode?>() {

    override fun visitFunc(ctx: WACCParser.FuncContext?): TypeNode? {
        val returnType = visit(ctx!!.stat())
        if (returnType != null) {
            val funcReturnType = globalSymbolTable.getNode(ctx.toString())
            if (funcReturnType!!::class != returnType::class) {
                println("SEMANTIC ERROR --- Return types do not match")
                semanticErrorDetected()
            }
        }
        return null
    }

/* STATEMENT NODES */

    override fun visitSkip(ctx: WACCParser.SkipContext?): TypeNode? {
        return null
    }

    override fun visitReturn(ctx: WACCParser.ReturnContext?): TypeNode? {
        return visit(ctx!!.expr())
    }

/* EXPRESSION NODES */

    override fun visitType(ctx: WACCParser.TypeContext): TypeNode? {
        when {
            ctx.base_type() != null -> {
                visit(ctx.base_type())
            }
            ctx.OPEN_SQUARE() != null -> {
                return ArrayNode(visit(ctx.type()) as TypeNode)
            }
            ctx.pair_type() != null -> {
                return visit(ctx.pair_type())
            }
        }
        return visitChildren(ctx)
    }

    override fun visitInt(ctx: WACCParser.IntContext): TypeNode {
        return Int()
    }

    override fun visitIntLiter(ctx: WACCParser.IntLiterContext): TypeNode? {
        return Int()
    }

    override fun visitBool(ctx: WACCParser.BoolContext): TypeNode? {
        return Bool()
    }

    override fun visitBoolLiter(ctx: WACCParser.BoolLiterContext): TypeNode? {
        return Bool()
    }

    override fun visitString(ctx: WACCParser.StringContext): TypeNode? {
        return Str()
    }

    override fun visitStrLiter(ctx: WACCParser.StrLiterContext): TypeNode? {
        return Str()
    }

    override fun visitChar(ctx: WACCParser.CharContext): TypeNode? {
        return Chr()
    }

    override fun visitCharLiter(ctx: WACCParser.CharLiterContext): TypeNode? {
        return Chr()
    }

    override fun visitVarAssign(ctx: WACCParser.VarAssignContext): TypeNode? {
        val lhs = visit(ctx.assign_lhs())
        val rhs = visit(ctx.assign_rhs())

        if (lhs == null) {
            println("SEMANTIC ERROR --- LHS has no type")
            semanticErrorDetected()
        }

        if (rhs == null) {
            if (lhs!!::class == Pair()::class) {
                return null
            } else {
                println("SEMANTIC ERROR --- RHS has no type")
                semanticErrorDetected()
            }
        }

        if (lhs!!::class != rhs!!::class) {
            println("SEMANTIC ERROR --- LHS does not equal RHS")
            semanticErrorDetected()
        }
        return rhs
    }

    override fun visitAssignLhsId(ctx: WACCParser.AssignLhsIdContext?): TypeNode? {
        return super.visitAssignLhsId(ctx)
    }

/* SEMANTIC EXIT */

    private fun semanticErrorDetected() {
        exitProcess(SEMANTIC_ERROR_RETURN)
    }
}
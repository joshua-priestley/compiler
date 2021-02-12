import antlr.WACCParser
import antlr.WACCParserBaseVisitor

class SemanticVisitor(private val globalSymbolTable: SymbolTable, val errorHandler: SemanticErrorHandler) : WACCParserBaseVisitor<Node>() {
    /*
    override fun visitProgram(ctx: WACCParser.ProgramContext): Node {
        val fNodes: MutableList<FunctionNode> = mutableListOf()
        for (func in ctx.func()) {
            fNodes.add(visit(func) as FunctionNode)
        }
        return ProgramNode(fNodes, visit(ctx.stat()) as StatementNode)
    }

    override fun visitFunc(func: WACCParser.FuncContext): Node {
        val name: String = func.ident().text
        val returnType = visit(func.type())
        val parameters = mutableListOf<Param>()

        val funcSymbolTable = SymbolTable(globalSymbolTable)

        if (func.param_list() != null) {
            for (param in func.param_list().param()) {
                val p = visit(param) as Param
                funcSymbolTable.addNode(p.ident.name, p)
                parameters.add(p)
            }
        }
        val fNode = FunctionNode(returnType as TypeNode, Ident(name), parameters, visit(func.stat()) as StatementNode)
        if (globalSymbolTable.containsNode(name)) {
            println("SEMANTIC ERROR DETECTED --- FUNCTION ALREADY EXISTS")
        } else {
            globalSymbolTable.addNode(name, fNode)
        }
        return fNode
    }

    /*
================================================================
STATEMENTS
 */

    override fun visitVarAssign(ctx: WACCParser.VarAssignContext): Node {
        return AssignNode(visit(ctx.assign_lhs()) as AssignLHSNode,
                visit(ctx.assign_rhs()) as AssignRHSNode)
    }

    override fun visitRead(ctx: WACCParser.ReadContext): Node {
        return ReadNode(visit(ctx.assign_lhs()) as AssignLHSNode)
    }

    override fun visitExit(ctx: WACCParser.ExitContext): Node {
        return ExitNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitFree(ctx: WACCParser.FreeContext): Node {
        return FreeNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitReturn(ctx: WACCParser.ReturnContext): Node {
        return ReturnNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitPrintln(ctx: WACCParser.PrintlnContext): Node {
        return PrintlnNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitSkip(ctx: WACCParser.SkipContext): Node {
        return SkipNode()
    }

    override fun visitPrint(ctx: WACCParser.PrintContext): Node {
        return PrintNode(visit(ctx.expr()) as ExprNode)
    }

    override fun visitIf(ctx: WACCParser.IfContext): Node {
        return IfElseNode(visit(ctx.expr()) as ExprNode,
                visit(ctx.stat(0)) as StatementNode,
                visit(ctx.stat((1))) as StatementNode)
    }

    override fun visitWhile(ctx: WACCParser.WhileContext): Node {
        return WhileNode(visit(ctx.expr()) as ExprNode,
                visit(ctx.stat()) as StatementNode)
    }

    override fun visitBegin(ctx: WACCParser.BeginContext): Node {
        return BeginEndNode(visit(ctx.stat()) as StatementNode)
    }

    override fun visitSequence(ctx: WACCParser.SequenceContext): Node {
        return SequenceNode(visit(ctx.stat(0)) as StatementNode, visit(ctx.stat(1)) as StatementNode)
    }

    override fun visitVarDeclaration(ctx: WACCParser.VarDeclarationContext): Node {
        return DeclarationNode(visit(ctx.type()) as TypeNode, Ident(ctx.ident().text), visit(ctx.assign_rhs()) as AssignRHSNode)
    }
    */
}
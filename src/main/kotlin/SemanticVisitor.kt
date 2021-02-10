import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import symbolTable.FunctionTables

class SemanticVisitor: WACCParserBaseVisitor<Nothing>() {

    val functionTable: FunctionTables = FunctionTables()

}
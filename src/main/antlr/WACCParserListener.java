// Generated from WACCParser.g4 by ANTLR 4.5
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link WACCParser}.
 */
public interface WACCParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link WACCParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(WACCParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(WACCParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#func}.
	 * @param ctx the parse tree
	 */
	void enterFunc(WACCParser.FuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#func}.
	 * @param ctx the parse tree
	 */
	void exitFunc(WACCParser.FuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#param_list}.
	 * @param ctx the parse tree
	 */
	void enterParam_list(WACCParser.Param_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#param_list}.
	 * @param ctx the parse tree
	 */
	void exitParam_list(WACCParser.Param_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(WACCParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(WACCParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(WACCParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(WACCParser.StatContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#assign_lhs}.
	 * @param ctx the parse tree
	 */
	void enterAssign_lhs(WACCParser.Assign_lhsContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#assign_lhs}.
	 * @param ctx the parse tree
	 */
	void exitAssign_lhs(WACCParser.Assign_lhsContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 */
	void enterAssign_rhs(WACCParser.Assign_rhsContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#assign_rhs}.
	 * @param ctx the parse tree
	 */
	void exitAssign_rhs(WACCParser.Assign_rhsContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void enterArg_list(WACCParser.Arg_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#arg_list}.
	 * @param ctx the parse tree
	 */
	void exitArg_list(WACCParser.Arg_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pair_elem}.
	 * @param ctx the parse tree
	 */
	void enterPair_elem(WACCParser.Pair_elemContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pair_elem}.
	 * @param ctx the parse tree
	 */
	void exitPair_elem(WACCParser.Pair_elemContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(WACCParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(WACCParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#base_type}.
	 * @param ctx the parse tree
	 */
	void enterBase_type(WACCParser.Base_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#base_type}.
	 * @param ctx the parse tree
	 */
	void exitBase_type(WACCParser.Base_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pair_type}.
	 * @param ctx the parse tree
	 */
	void enterPair_type(WACCParser.Pair_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pair_type}.
	 * @param ctx the parse tree
	 */
	void exitPair_type(WACCParser.Pair_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pair_elem_type}.
	 * @param ctx the parse tree
	 */
	void enterPair_elem_type(WACCParser.Pair_elem_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pair_elem_type}.
	 * @param ctx the parse tree
	 */
	void exitPair_elem_type(WACCParser.Pair_elem_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(WACCParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(WACCParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pair_liter}.
	 * @param ctx the parse tree
	 */
	void enterPair_liter(WACCParser.Pair_literContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pair_liter}.
	 * @param ctx the parse tree
	 */
	void exitPair_liter(WACCParser.Pair_literContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#unaryOper}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOper(WACCParser.UnaryOperContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#unaryOper}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOper(WACCParser.UnaryOperContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#binaryOper}.
	 * @param ctx the parse tree
	 */
	void enterBinaryOper(WACCParser.BinaryOperContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#binaryOper}.
	 * @param ctx the parse tree
	 */
	void exitBinaryOper(WACCParser.BinaryOperContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pre1}.
	 * @param ctx the parse tree
	 */
	void enterPre1(WACCParser.Pre1Context ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pre1}.
	 * @param ctx the parse tree
	 */
	void exitPre1(WACCParser.Pre1Context ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pre2}.
	 * @param ctx the parse tree
	 */
	void enterPre2(WACCParser.Pre2Context ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pre2}.
	 * @param ctx the parse tree
	 */
	void exitPre2(WACCParser.Pre2Context ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pre3}.
	 * @param ctx the parse tree
	 */
	void enterPre3(WACCParser.Pre3Context ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pre3}.
	 * @param ctx the parse tree
	 */
	void exitPre3(WACCParser.Pre3Context ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pre4}.
	 * @param ctx the parse tree
	 */
	void enterPre4(WACCParser.Pre4Context ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pre4}.
	 * @param ctx the parse tree
	 */
	void exitPre4(WACCParser.Pre4Context ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pre5}.
	 * @param ctx the parse tree
	 */
	void enterPre5(WACCParser.Pre5Context ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pre5}.
	 * @param ctx the parse tree
	 */
	void exitPre5(WACCParser.Pre5Context ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#pre6}.
	 * @param ctx the parse tree
	 */
	void enterPre6(WACCParser.Pre6Context ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#pre6}.
	 * @param ctx the parse tree
	 */
	void exitPre6(WACCParser.Pre6Context ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#ident}.
	 * @param ctx the parse tree
	 */
	void enterIdent(WACCParser.IdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#ident}.
	 * @param ctx the parse tree
	 */
	void exitIdent(WACCParser.IdentContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#array_elem}.
	 * @param ctx the parse tree
	 */
	void enterArray_elem(WACCParser.Array_elemContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#array_elem}.
	 * @param ctx the parse tree
	 */
	void exitArray_elem(WACCParser.Array_elemContext ctx);
	/**
	 * Enter a parse tree produced by {@link WACCParser#array_liter}.
	 * @param ctx the parse tree
	 */
	void enterArray_liter(WACCParser.Array_literContext ctx);
	/**
	 * Exit a parse tree produced by {@link WACCParser#array_liter}.
	 * @param ctx the parse tree
	 */
	void exitArray_liter(WACCParser.Array_literContext ctx);
}
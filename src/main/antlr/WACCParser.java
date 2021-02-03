// Generated from WACCParser.g4 by ANTLR 4.5
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WACCParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PLUS=1, MINUS=2, MUL=3, DIV=4, MOD=5, GT=6, GTE=7, LT=8, LTE=9, EQ=10, 
		NEQ=11, AND=12, OR=13, NOT=14, LEN=15, ORD=16, CHR=17, BEGIN=18, END=19, 
		IS=20, OPEN_PARENTHESES=21, CLOSE_PARENTHESES=22, OPEN_SQUARE=23, CLOSE_SQUARE=24, 
		INT_LITER=25, SKP=26, BREAK=27, CONTINUE=28, EXIT=29, SEMICOLON=30, COLON=31, 
		COMMA=32, ASSIGN=33, READ=34, FREE=35, RETURN=36, PRINT=37, PRINTLN=38, 
		IF=39, THEN=40, ELSE=41, FI=42, WHILE=43, DO=44, DONE=45, FOR=46, NULL=47, 
		BOOL_LITER=48, NEWPAIR=49, FST=50, SND=51, CALL=52, INT=53, BOOL=54, CHAR=55, 
		STRING=56, PAIR=57, CHAR_LITER=58, STR_LITER=59, WS=60, COMMENT=61, ID=62;
	public static final int
		RULE_program = 0, RULE_func = 1, RULE_param_list = 2, RULE_param = 3, 
		RULE_stat = 4, RULE_assign_lhs = 5, RULE_assign_rhs = 6, RULE_arg_list = 7, 
		RULE_pair_elem = 8, RULE_type = 9, RULE_base_type = 10, RULE_pair_type = 11, 
		RULE_pair_elem_type = 12, RULE_expr = 13, RULE_pair_liter = 14, RULE_unaryOper = 15, 
		RULE_binaryOper = 16, RULE_pre1 = 17, RULE_pre2 = 18, RULE_pre3 = 19, 
		RULE_pre4 = 20, RULE_pre5 = 21, RULE_pre6 = 22, RULE_ident = 23, RULE_array_elem = 24, 
		RULE_array_liter = 25;
	public static final String[] ruleNames = {
		"program", "func", "param_list", "param", "stat", "assign_lhs", "assign_rhs", 
		"arg_list", "pair_elem", "type", "base_type", "pair_type", "pair_elem_type", 
		"expr", "pair_liter", "unaryOper", "binaryOper", "pre1", "pre2", "pre3", 
		"pre4", "pre5", "pre6", "ident", "array_elem", "array_liter"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'+'", "'-'", "'*'", "'/'", "'%'", "'>'", "'>='", "'<'", "'<='", 
		"'=='", "'!='", "'&&'", "'||'", "'!'", "'len'", "'ord'", "'chr'", "'begin'", 
		"'end'", "'is'", "'('", "')'", "'['", "']'", null, "'skip'", "'break'", 
		"'continue'", "'exit'", "';'", "':'", "','", "'='", "'read'", "'free'", 
		"'return'", "'print'", "'println'", "'if'", "'then'", "'else'", "'fi'", 
		"'while'", "'do'", "'done'", "'for'", "'null'", null, "'newpair'", "'fst'", 
		"'snd'", "'call'", "'int'", "'bool'", "'char'", "'string'", "'pair'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "PLUS", "MINUS", "MUL", "DIV", "MOD", "GT", "GTE", "LT", "LTE", 
		"EQ", "NEQ", "AND", "OR", "NOT", "LEN", "ORD", "CHR", "BEGIN", "END", 
		"IS", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", "OPEN_SQUARE", "CLOSE_SQUARE", 
		"INT_LITER", "SKP", "BREAK", "CONTINUE", "EXIT", "SEMICOLON", "COLON", 
		"COMMA", "ASSIGN", "READ", "FREE", "RETURN", "PRINT", "PRINTLN", "IF", 
		"THEN", "ELSE", "FI", "WHILE", "DO", "DONE", "FOR", "NULL", "BOOL_LITER", 
		"NEWPAIR", "FST", "SND", "CALL", "INT", "BOOL", "CHAR", "STRING", "PAIR", 
		"CHAR_LITER", "STR_LITER", "WS", "COMMENT", "ID"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "WACCParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WACCParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode BEGIN() { return getToken(WACCParser.BEGIN, 0); }
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public TerminalNode END() { return getToken(WACCParser.END, 0); }
		public TerminalNode EOF() { return getToken(WACCParser.EOF, 0); }
		public List<FuncContext> func() {
			return getRuleContexts(FuncContext.class);
		}
		public FuncContext func(int i) {
			return getRuleContext(FuncContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(BEGIN);
			setState(56);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(53);
					func();
					}
					} 
				}
				setState(58);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(59);
			stat(0);
			setState(60);
			match(END);
			setState(61);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FuncContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public TerminalNode IS() { return getToken(WACCParser.IS, 0); }
		public StatContext stat() {
			return getRuleContext(StatContext.class,0);
		}
		public TerminalNode END() { return getToken(WACCParser.END, 0); }
		public Param_listContext param_list() {
			return getRuleContext(Param_listContext.class,0);
		}
		public FuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_func; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitFunc(this);
		}
	}

	public final FuncContext func() throws RecognitionException {
		FuncContext _localctx = new FuncContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_func);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			type(0);
			setState(64);
			ident();
			setState(65);
			match(OPEN_PARENTHESES);
			setState(67);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << CHAR) | (1L << STRING) | (1L << PAIR))) != 0)) {
				{
				setState(66);
				param_list();
				}
			}

			setState(69);
			match(CLOSE_PARENTHESES);
			setState(70);
			match(IS);
			setState(71);
			stat(0);
			setState(72);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Param_listContext extends ParserRuleContext {
		public List<ParamContext> param() {
			return getRuleContexts(ParamContext.class);
		}
		public ParamContext param(int i) {
			return getRuleContext(ParamContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WACCParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WACCParser.COMMA, i);
		}
		public Param_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterParam_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitParam_list(this);
		}
	}

	public final Param_listContext param_list() throws RecognitionException {
		Param_listContext _localctx = new Param_listContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_param_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			param();
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(75);
				match(COMMA);
				setState(76);
				param();
				}
				}
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ParamContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public ParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitParam(this);
		}
	}

	public final ParamContext param() throws RecognitionException {
		ParamContext _localctx = new ParamContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			type(0);
			setState(83);
			ident();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatContext extends ParserRuleContext {
		public TerminalNode SKP() { return getToken(WACCParser.SKP, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(WACCParser.ASSIGN, 0); }
		public Assign_rhsContext assign_rhs() {
			return getRuleContext(Assign_rhsContext.class,0);
		}
		public Assign_lhsContext assign_lhs() {
			return getRuleContext(Assign_lhsContext.class,0);
		}
		public TerminalNode READ() { return getToken(WACCParser.READ, 0); }
		public TerminalNode FREE() { return getToken(WACCParser.FREE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RETURN() { return getToken(WACCParser.RETURN, 0); }
		public TerminalNode EXIT() { return getToken(WACCParser.EXIT, 0); }
		public TerminalNode PRINT() { return getToken(WACCParser.PRINT, 0); }
		public TerminalNode PRINTLN() { return getToken(WACCParser.PRINTLN, 0); }
		public TerminalNode IF() { return getToken(WACCParser.IF, 0); }
		public TerminalNode THEN() { return getToken(WACCParser.THEN, 0); }
		public List<StatContext> stat() {
			return getRuleContexts(StatContext.class);
		}
		public StatContext stat(int i) {
			return getRuleContext(StatContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(WACCParser.ELSE, 0); }
		public TerminalNode FI() { return getToken(WACCParser.FI, 0); }
		public TerminalNode WHILE() { return getToken(WACCParser.WHILE, 0); }
		public TerminalNode DO() { return getToken(WACCParser.DO, 0); }
		public TerminalNode DONE() { return getToken(WACCParser.DONE, 0); }
		public TerminalNode BEGIN() { return getToken(WACCParser.BEGIN, 0); }
		public TerminalNode END() { return getToken(WACCParser.END, 0); }
		public TerminalNode SEMICOLON() { return getToken(WACCParser.SEMICOLON, 0); }
		public StatContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stat; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterStat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitStat(this);
		}
	}

	public final StatContext stat() throws RecognitionException {
		return stat(0);
	}

	private StatContext stat(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		StatContext _localctx = new StatContext(_ctx, _parentState);
		StatContext _prevctx = _localctx;
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_stat, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			switch (_input.LA(1)) {
			case SKP:
				{
				setState(86);
				match(SKP);
				}
				break;
			case INT:
			case BOOL:
			case CHAR:
			case STRING:
			case PAIR:
				{
				setState(87);
				type(0);
				setState(88);
				ident();
				setState(89);
				match(ASSIGN);
				setState(90);
				assign_rhs();
				}
				break;
			case FST:
			case SND:
			case ID:
				{
				setState(92);
				assign_lhs();
				setState(93);
				match(ASSIGN);
				setState(94);
				assign_rhs();
				}
				break;
			case READ:
				{
				setState(96);
				match(READ);
				setState(97);
				assign_lhs();
				}
				break;
			case FREE:
				{
				setState(98);
				match(FREE);
				setState(99);
				expr(0);
				}
				break;
			case RETURN:
				{
				setState(100);
				match(RETURN);
				setState(101);
				expr(0);
				}
				break;
			case EXIT:
				{
				setState(102);
				match(EXIT);
				setState(103);
				expr(0);
				}
				break;
			case PRINT:
				{
				setState(104);
				match(PRINT);
				setState(105);
				expr(0);
				}
				break;
			case PRINTLN:
				{
				setState(106);
				match(PRINTLN);
				setState(107);
				expr(0);
				}
				break;
			case IF:
				{
				setState(108);
				match(IF);
				setState(109);
				expr(0);
				setState(110);
				match(THEN);
				setState(111);
				stat(0);
				setState(112);
				match(ELSE);
				setState(113);
				stat(0);
				setState(114);
				match(FI);
				}
				break;
			case WHILE:
				{
				setState(116);
				match(WHILE);
				setState(117);
				expr(0);
				setState(118);
				match(DO);
				setState(119);
				stat(0);
				setState(120);
				match(DONE);
				}
				break;
			case BEGIN:
				{
				setState(122);
				match(BEGIN);
				setState(123);
				stat(0);
				setState(124);
				match(END);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(133);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new StatContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_stat);
					setState(128);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(129);
					match(SEMICOLON);
					setState(130);
					stat(1);
					}
					} 
				}
				setState(135);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Assign_lhsContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Array_elemContext array_elem() {
			return getRuleContext(Array_elemContext.class,0);
		}
		public Pair_elemContext pair_elem() {
			return getRuleContext(Pair_elemContext.class,0);
		}
		public Assign_lhsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_lhs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterAssign_lhs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitAssign_lhs(this);
		}
	}

	public final Assign_lhsContext assign_lhs() throws RecognitionException {
		Assign_lhsContext _localctx = new Assign_lhsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_assign_lhs);
		try {
			setState(139);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(136);
				ident();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(137);
				array_elem();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(138);
				pair_elem();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_rhsContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public Array_literContext array_liter() {
			return getRuleContext(Array_literContext.class,0);
		}
		public TerminalNode NEWPAIR() { return getToken(WACCParser.NEWPAIR, 0); }
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public TerminalNode COMMA() { return getToken(WACCParser.COMMA, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public Pair_elemContext pair_elem() {
			return getRuleContext(Pair_elemContext.class,0);
		}
		public TerminalNode CALL() { return getToken(WACCParser.CALL, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Arg_listContext arg_list() {
			return getRuleContext(Arg_listContext.class,0);
		}
		public Assign_rhsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_rhs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterAssign_rhs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitAssign_rhs(this);
		}
	}

	public final Assign_rhsContext assign_rhs() throws RecognitionException {
		Assign_rhsContext _localctx = new Assign_rhsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_assign_rhs);
		int _la;
		try {
			setState(159);
			switch (_input.LA(1)) {
			case MINUS:
			case NOT:
			case LEN:
			case ORD:
			case CHR:
			case OPEN_PARENTHESES:
			case INT_LITER:
			case NULL:
			case BOOL_LITER:
			case CHAR_LITER:
			case STR_LITER:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(141);
				expr(0);
				}
				break;
			case OPEN_SQUARE:
				enterOuterAlt(_localctx, 2);
				{
				setState(142);
				array_liter();
				}
				break;
			case NEWPAIR:
				enterOuterAlt(_localctx, 3);
				{
				setState(143);
				match(NEWPAIR);
				setState(144);
				match(OPEN_PARENTHESES);
				setState(145);
				expr(0);
				setState(146);
				match(COMMA);
				setState(147);
				expr(0);
				setState(148);
				match(CLOSE_PARENTHESES);
				}
				break;
			case FST:
			case SND:
				enterOuterAlt(_localctx, 4);
				{
				setState(150);
				pair_elem();
				}
				break;
			case CALL:
				enterOuterAlt(_localctx, 5);
				{
				setState(151);
				match(CALL);
				setState(152);
				ident();
				setState(153);
				match(OPEN_PARENTHESES);
				setState(155);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MINUS) | (1L << NOT) | (1L << LEN) | (1L << ORD) | (1L << CHR) | (1L << OPEN_PARENTHESES) | (1L << INT_LITER) | (1L << NULL) | (1L << BOOL_LITER) | (1L << CHAR_LITER) | (1L << STR_LITER) | (1L << ID))) != 0)) {
					{
					setState(154);
					arg_list();
					}
				}

				setState(157);
				match(CLOSE_PARENTHESES);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Arg_listContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WACCParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WACCParser.COMMA, i);
		}
		public Arg_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arg_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterArg_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitArg_list(this);
		}
	}

	public final Arg_listContext arg_list() throws RecognitionException {
		Arg_listContext _localctx = new Arg_listContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_arg_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			expr(0);
			setState(166);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(162);
				match(COMMA);
				setState(163);
				expr(0);
				}
				}
				setState(168);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pair_elemContext extends ParserRuleContext {
		public TerminalNode FST() { return getToken(WACCParser.FST, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SND() { return getToken(WACCParser.SND, 0); }
		public Pair_elemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair_elem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPair_elem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPair_elem(this);
		}
	}

	public final Pair_elemContext pair_elem() throws RecognitionException {
		Pair_elemContext _localctx = new Pair_elemContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_pair_elem);
		try {
			setState(173);
			switch (_input.LA(1)) {
			case FST:
				enterOuterAlt(_localctx, 1);
				{
				setState(169);
				match(FST);
				setState(170);
				expr(0);
				}
				break;
			case SND:
				enterOuterAlt(_localctx, 2);
				{
				setState(171);
				match(SND);
				setState(172);
				expr(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public Base_typeContext base_type() {
			return getRuleContext(Base_typeContext.class,0);
		}
		public Pair_typeContext pair_type() {
			return getRuleContext(Pair_typeContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode OPEN_SQUARE() { return getToken(WACCParser.OPEN_SQUARE, 0); }
		public TerminalNode CLOSE_SQUARE() { return getToken(WACCParser.CLOSE_SQUARE, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 18;
		enterRecursionRule(_localctx, 18, RULE_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			switch (_input.LA(1)) {
			case INT:
			case BOOL:
			case CHAR:
			case STRING:
				{
				setState(176);
				base_type();
				}
				break;
			case PAIR:
				{
				setState(177);
				pair_type();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(185);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TypeContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(180);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(181);
					match(OPEN_SQUARE);
					setState(182);
					match(CLOSE_SQUARE);
					}
					} 
				}
				setState(187);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Base_typeContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(WACCParser.INT, 0); }
		public TerminalNode BOOL() { return getToken(WACCParser.BOOL, 0); }
		public TerminalNode CHAR() { return getToken(WACCParser.CHAR, 0); }
		public TerminalNode STRING() { return getToken(WACCParser.STRING, 0); }
		public Base_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterBase_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitBase_type(this);
		}
	}

	public final Base_typeContext base_type() throws RecognitionException {
		Base_typeContext _localctx = new Base_typeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_base_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << BOOL) | (1L << CHAR) | (1L << STRING))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pair_typeContext extends ParserRuleContext {
		public TerminalNode PAIR() { return getToken(WACCParser.PAIR, 0); }
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public List<Pair_elem_typeContext> pair_elem_type() {
			return getRuleContexts(Pair_elem_typeContext.class);
		}
		public Pair_elem_typeContext pair_elem_type(int i) {
			return getRuleContext(Pair_elem_typeContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(WACCParser.COMMA, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public Pair_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPair_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPair_type(this);
		}
	}

	public final Pair_typeContext pair_type() throws RecognitionException {
		Pair_typeContext _localctx = new Pair_typeContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_pair_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(190);
			match(PAIR);
			setState(191);
			match(OPEN_PARENTHESES);
			setState(192);
			pair_elem_type();
			setState(193);
			match(COMMA);
			setState(194);
			pair_elem_type();
			setState(195);
			match(CLOSE_PARENTHESES);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pair_elem_typeContext extends ParserRuleContext {
		public Base_typeContext base_type() {
			return getRuleContext(Base_typeContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode OPEN_SQUARE() { return getToken(WACCParser.OPEN_SQUARE, 0); }
		public TerminalNode CLOSE_SQUARE() { return getToken(WACCParser.CLOSE_SQUARE, 0); }
		public TerminalNode PAIR() { return getToken(WACCParser.PAIR, 0); }
		public Pair_elem_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair_elem_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPair_elem_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPair_elem_type(this);
		}
	}

	public final Pair_elem_typeContext pair_elem_type() throws RecognitionException {
		Pair_elem_typeContext _localctx = new Pair_elem_typeContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_pair_elem_type);
		try {
			setState(203);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(197);
				base_type();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(198);
				type(0);
				setState(199);
				match(OPEN_SQUARE);
				setState(200);
				match(CLOSE_SQUARE);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(202);
				match(PAIR);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public UnaryOperContext unaryOper() {
			return getRuleContext(UnaryOperContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode INT_LITER() { return getToken(WACCParser.INT_LITER, 0); }
		public TerminalNode BOOL_LITER() { return getToken(WACCParser.BOOL_LITER, 0); }
		public TerminalNode CHAR_LITER() { return getToken(WACCParser.CHAR_LITER, 0); }
		public TerminalNode STR_LITER() { return getToken(WACCParser.STR_LITER, 0); }
		public Pair_literContext pair_liter() {
			return getRuleContext(Pair_literContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Array_elemContext array_elem() {
			return getRuleContext(Array_elemContext.class,0);
		}
		public TerminalNode OPEN_PARENTHESES() { return getToken(WACCParser.OPEN_PARENTHESES, 0); }
		public TerminalNode CLOSE_PARENTHESES() { return getToken(WACCParser.CLOSE_PARENTHESES, 0); }
		public BinaryOperContext binaryOper() {
			return getRuleContext(BinaryOperContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 26;
		enterRecursionRule(_localctx, 26, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(206);
				unaryOper();
				setState(207);
				expr(3);
				}
				break;
			case 2:
				{
				setState(209);
				match(INT_LITER);
				}
				break;
			case 3:
				{
				setState(210);
				match(BOOL_LITER);
				}
				break;
			case 4:
				{
				setState(211);
				match(CHAR_LITER);
				}
				break;
			case 5:
				{
				setState(212);
				match(STR_LITER);
				}
				break;
			case 6:
				{
				setState(213);
				pair_liter();
				}
				break;
			case 7:
				{
				setState(214);
				ident();
				}
				break;
			case 8:
				{
				setState(215);
				array_elem();
				}
				break;
			case 9:
				{
				setState(216);
				match(OPEN_PARENTHESES);
				setState(217);
				expr(0);
				setState(218);
				match(CLOSE_PARENTHESES);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(228);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(222);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(223);
					binaryOper();
					setState(224);
					expr(3);
					}
					} 
				}
				setState(230);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Pair_literContext extends ParserRuleContext {
		public TerminalNode NULL() { return getToken(WACCParser.NULL, 0); }
		public Pair_literContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pair_liter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPair_liter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPair_liter(this);
		}
	}

	public final Pair_literContext pair_liter() throws RecognitionException {
		Pair_literContext _localctx = new Pair_literContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_pair_liter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			match(NULL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryOperContext extends ParserRuleContext {
		public TerminalNode NOT() { return getToken(WACCParser.NOT, 0); }
		public TerminalNode MINUS() { return getToken(WACCParser.MINUS, 0); }
		public TerminalNode LEN() { return getToken(WACCParser.LEN, 0); }
		public TerminalNode ORD() { return getToken(WACCParser.ORD, 0); }
		public TerminalNode CHR() { return getToken(WACCParser.CHR, 0); }
		public UnaryOperContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryOper; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterUnaryOper(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitUnaryOper(this);
		}
	}

	public final UnaryOperContext unaryOper() throws RecognitionException {
		UnaryOperContext _localctx = new UnaryOperContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_unaryOper);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(233);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MINUS) | (1L << NOT) | (1L << LEN) | (1L << ORD) | (1L << CHR))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BinaryOperContext extends ParserRuleContext {
		public Pre1Context pre1() {
			return getRuleContext(Pre1Context.class,0);
		}
		public Pre2Context pre2() {
			return getRuleContext(Pre2Context.class,0);
		}
		public Pre3Context pre3() {
			return getRuleContext(Pre3Context.class,0);
		}
		public Pre4Context pre4() {
			return getRuleContext(Pre4Context.class,0);
		}
		public Pre5Context pre5() {
			return getRuleContext(Pre5Context.class,0);
		}
		public Pre6Context pre6() {
			return getRuleContext(Pre6Context.class,0);
		}
		public BinaryOperContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_binaryOper; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterBinaryOper(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitBinaryOper(this);
		}
	}

	public final BinaryOperContext binaryOper() throws RecognitionException {
		BinaryOperContext _localctx = new BinaryOperContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_binaryOper);
		try {
			setState(241);
			switch (_input.LA(1)) {
			case MUL:
			case DIV:
			case MOD:
				enterOuterAlt(_localctx, 1);
				{
				setState(235);
				pre1();
				}
				break;
			case PLUS:
			case MINUS:
				enterOuterAlt(_localctx, 2);
				{
				setState(236);
				pre2();
				}
				break;
			case GT:
			case GTE:
			case LT:
			case LTE:
				enterOuterAlt(_localctx, 3);
				{
				setState(237);
				pre3();
				}
				break;
			case EQ:
			case NEQ:
				enterOuterAlt(_localctx, 4);
				{
				setState(238);
				pre4();
				}
				break;
			case AND:
				enterOuterAlt(_localctx, 5);
				{
				setState(239);
				pre5();
				}
				break;
			case OR:
				enterOuterAlt(_localctx, 6);
				{
				setState(240);
				pre6();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pre1Context extends ParserRuleContext {
		public TerminalNode MUL() { return getToken(WACCParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(WACCParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(WACCParser.MOD, 0); }
		public Pre1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pre1; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPre1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPre1(this);
		}
	}

	public final Pre1Context pre1() throws RecognitionException {
		Pre1Context _localctx = new Pre1Context(_ctx, getState());
		enterRule(_localctx, 34, RULE_pre1);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MUL) | (1L << DIV) | (1L << MOD))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pre2Context extends ParserRuleContext {
		public TerminalNode PLUS() { return getToken(WACCParser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(WACCParser.MINUS, 0); }
		public Pre2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pre2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPre2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPre2(this);
		}
	}

	public final Pre2Context pre2() throws RecognitionException {
		Pre2Context _localctx = new Pre2Context(_ctx, getState());
		enterRule(_localctx, 36, RULE_pre2);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(245);
			_la = _input.LA(1);
			if ( !(_la==PLUS || _la==MINUS) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pre3Context extends ParserRuleContext {
		public TerminalNode GT() { return getToken(WACCParser.GT, 0); }
		public TerminalNode GTE() { return getToken(WACCParser.GTE, 0); }
		public TerminalNode LT() { return getToken(WACCParser.LT, 0); }
		public TerminalNode LTE() { return getToken(WACCParser.LTE, 0); }
		public Pre3Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pre3; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPre3(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPre3(this);
		}
	}

	public final Pre3Context pre3() throws RecognitionException {
		Pre3Context _localctx = new Pre3Context(_ctx, getState());
		enterRule(_localctx, 38, RULE_pre3);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(247);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GT) | (1L << GTE) | (1L << LT) | (1L << LTE))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pre4Context extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(WACCParser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(WACCParser.NEQ, 0); }
		public Pre4Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pre4; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPre4(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPre4(this);
		}
	}

	public final Pre4Context pre4() throws RecognitionException {
		Pre4Context _localctx = new Pre4Context(_ctx, getState());
		enterRule(_localctx, 40, RULE_pre4);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			_la = _input.LA(1);
			if ( !(_la==EQ || _la==NEQ) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pre5Context extends ParserRuleContext {
		public TerminalNode AND() { return getToken(WACCParser.AND, 0); }
		public Pre5Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pre5; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPre5(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPre5(this);
		}
	}

	public final Pre5Context pre5() throws RecognitionException {
		Pre5Context _localctx = new Pre5Context(_ctx, getState());
		enterRule(_localctx, 42, RULE_pre5);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(AND);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Pre6Context extends ParserRuleContext {
		public TerminalNode OR() { return getToken(WACCParser.OR, 0); }
		public Pre6Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pre6; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterPre6(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitPre6(this);
		}
	}

	public final Pre6Context pre6() throws RecognitionException {
		Pre6Context _localctx = new Pre6Context(_ctx, getState());
		enterRule(_localctx, 44, RULE_pre6);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(253);
			match(OR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(WACCParser.ID, 0); }
		public IdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ident; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitIdent(this);
		}
	}

	public final IdentContext ident() throws RecognitionException {
		IdentContext _localctx = new IdentContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_ident);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Array_elemContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public List<TerminalNode> OPEN_SQUARE() { return getTokens(WACCParser.OPEN_SQUARE); }
		public TerminalNode OPEN_SQUARE(int i) {
			return getToken(WACCParser.OPEN_SQUARE, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> CLOSE_SQUARE() { return getTokens(WACCParser.CLOSE_SQUARE); }
		public TerminalNode CLOSE_SQUARE(int i) {
			return getToken(WACCParser.CLOSE_SQUARE, i);
		}
		public Array_elemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_elem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterArray_elem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitArray_elem(this);
		}
	}

	public final Array_elemContext array_elem() throws RecognitionException {
		Array_elemContext _localctx = new Array_elemContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_array_elem);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			ident();
			setState(262); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(258);
					match(OPEN_SQUARE);
					setState(259);
					expr(0);
					setState(260);
					match(CLOSE_SQUARE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(264); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Array_literContext extends ParserRuleContext {
		public TerminalNode OPEN_SQUARE() { return getToken(WACCParser.OPEN_SQUARE, 0); }
		public TerminalNode CLOSE_SQUARE() { return getToken(WACCParser.CLOSE_SQUARE, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WACCParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WACCParser.COMMA, i);
		}
		public Array_literContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array_liter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).enterArray_liter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WACCParserListener ) ((WACCParserListener)listener).exitArray_liter(this);
		}
	}

	public final Array_literContext array_liter() throws RecognitionException {
		Array_literContext _localctx = new Array_literContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_array_liter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			match(OPEN_SQUARE);
			setState(275);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MINUS) | (1L << NOT) | (1L << LEN) | (1L << ORD) | (1L << CHR) | (1L << OPEN_PARENTHESES) | (1L << INT_LITER) | (1L << NULL) | (1L << BOOL_LITER) | (1L << CHAR_LITER) | (1L << STR_LITER) | (1L << ID))) != 0)) {
				{
				setState(267);
				expr(0);
				setState(272);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(268);
					match(COMMA);
					setState(269);
					expr(0);
					}
					}
					setState(274);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(277);
			match(CLOSE_SQUARE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 4:
			return stat_sempred((StatContext)_localctx, predIndex);
		case 9:
			return type_sempred((TypeContext)_localctx, predIndex);
		case 13:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean stat_sempred(StatContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3@\u011a\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\3\2\7\29\n\2\f\2\16\2<\13\2\3\2\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\5\3F\n\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\7\4P\n\4\f\4\16"+
		"\4S\13\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6\u0081\n\6\3\6\3\6\3\6"+
		"\7\6\u0086\n\6\f\6\16\6\u0089\13\6\3\7\3\7\3\7\5\7\u008e\n\7\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u009e\n\b\3\b\3\b\5"+
		"\b\u00a2\n\b\3\t\3\t\3\t\7\t\u00a7\n\t\f\t\16\t\u00aa\13\t\3\n\3\n\3\n"+
		"\3\n\5\n\u00b0\n\n\3\13\3\13\3\13\5\13\u00b5\n\13\3\13\3\13\3\13\7\13"+
		"\u00ba\n\13\f\13\16\13\u00bd\13\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u00ce\n\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5\17\u00df\n\17\3\17"+
		"\3\17\3\17\3\17\7\17\u00e5\n\17\f\17\16\17\u00e8\13\17\3\20\3\20\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u00f4\n\22\3\23\3\23\3\24\3\24"+
		"\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\32\3\32"+
		"\3\32\6\32\u0109\n\32\r\32\16\32\u010a\3\33\3\33\3\33\3\33\7\33\u0111"+
		"\n\33\f\33\16\33\u0114\13\33\5\33\u0116\n\33\3\33\3\33\3\33\2\5\n\24\34"+
		"\34\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\2\b\3\2\67"+
		":\4\2\4\4\20\23\3\2\5\7\3\2\3\4\3\2\b\13\3\2\f\r\u012c\2\66\3\2\2\2\4"+
		"A\3\2\2\2\6L\3\2\2\2\bT\3\2\2\2\n\u0080\3\2\2\2\f\u008d\3\2\2\2\16\u00a1"+
		"\3\2\2\2\20\u00a3\3\2\2\2\22\u00af\3\2\2\2\24\u00b4\3\2\2\2\26\u00be\3"+
		"\2\2\2\30\u00c0\3\2\2\2\32\u00cd\3\2\2\2\34\u00de\3\2\2\2\36\u00e9\3\2"+
		"\2\2 \u00eb\3\2\2\2\"\u00f3\3\2\2\2$\u00f5\3\2\2\2&\u00f7\3\2\2\2(\u00f9"+
		"\3\2\2\2*\u00fb\3\2\2\2,\u00fd\3\2\2\2.\u00ff\3\2\2\2\60\u0101\3\2\2\2"+
		"\62\u0103\3\2\2\2\64\u010c\3\2\2\2\66:\7\24\2\2\679\5\4\3\28\67\3\2\2"+
		"\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;=\3\2\2\2<:\3\2\2\2=>\5\n\6\2>?\7\25"+
		"\2\2?@\7\2\2\3@\3\3\2\2\2AB\5\24\13\2BC\5\60\31\2CE\7\27\2\2DF\5\6\4\2"+
		"ED\3\2\2\2EF\3\2\2\2FG\3\2\2\2GH\7\30\2\2HI\7\26\2\2IJ\5\n\6\2JK\7\25"+
		"\2\2K\5\3\2\2\2LQ\5\b\5\2MN\7\"\2\2NP\5\b\5\2OM\3\2\2\2PS\3\2\2\2QO\3"+
		"\2\2\2QR\3\2\2\2R\7\3\2\2\2SQ\3\2\2\2TU\5\24\13\2UV\5\60\31\2V\t\3\2\2"+
		"\2WX\b\6\1\2X\u0081\7\34\2\2YZ\5\24\13\2Z[\5\60\31\2[\\\7#\2\2\\]\5\16"+
		"\b\2]\u0081\3\2\2\2^_\5\f\7\2_`\7#\2\2`a\5\16\b\2a\u0081\3\2\2\2bc\7$"+
		"\2\2c\u0081\5\f\7\2de\7%\2\2e\u0081\5\34\17\2fg\7&\2\2g\u0081\5\34\17"+
		"\2hi\7\37\2\2i\u0081\5\34\17\2jk\7\'\2\2k\u0081\5\34\17\2lm\7(\2\2m\u0081"+
		"\5\34\17\2no\7)\2\2op\5\34\17\2pq\7*\2\2qr\5\n\6\2rs\7+\2\2st\5\n\6\2"+
		"tu\7,\2\2u\u0081\3\2\2\2vw\7-\2\2wx\5\34\17\2xy\7.\2\2yz\5\n\6\2z{\7/"+
		"\2\2{\u0081\3\2\2\2|}\7\24\2\2}~\5\n\6\2~\177\7\25\2\2\177\u0081\3\2\2"+
		"\2\u0080W\3\2\2\2\u0080Y\3\2\2\2\u0080^\3\2\2\2\u0080b\3\2\2\2\u0080d"+
		"\3\2\2\2\u0080f\3\2\2\2\u0080h\3\2\2\2\u0080j\3\2\2\2\u0080l\3\2\2\2\u0080"+
		"n\3\2\2\2\u0080v\3\2\2\2\u0080|\3\2\2\2\u0081\u0087\3\2\2\2\u0082\u0083"+
		"\f\3\2\2\u0083\u0084\7 \2\2\u0084\u0086\5\n\6\3\u0085\u0082\3\2\2\2\u0086"+
		"\u0089\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\13\3\2\2"+
		"\2\u0089\u0087\3\2\2\2\u008a\u008e\5\60\31\2\u008b\u008e\5\62\32\2\u008c"+
		"\u008e\5\22\n\2\u008d\u008a\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008c\3"+
		"\2\2\2\u008e\r\3\2\2\2\u008f\u00a2\5\34\17\2\u0090\u00a2\5\64\33\2\u0091"+
		"\u0092\7\63\2\2\u0092\u0093\7\27\2\2\u0093\u0094\5\34\17\2\u0094\u0095"+
		"\7\"\2\2\u0095\u0096\5\34\17\2\u0096\u0097\7\30\2\2\u0097\u00a2\3\2\2"+
		"\2\u0098\u00a2\5\22\n\2\u0099\u009a\7\66\2\2\u009a\u009b\5\60\31\2\u009b"+
		"\u009d\7\27\2\2\u009c\u009e\5\20\t\2\u009d\u009c\3\2\2\2\u009d\u009e\3"+
		"\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\7\30\2\2\u00a0\u00a2\3\2\2\2\u00a1"+
		"\u008f\3\2\2\2\u00a1\u0090\3\2\2\2\u00a1\u0091\3\2\2\2\u00a1\u0098\3\2"+
		"\2\2\u00a1\u0099\3\2\2\2\u00a2\17\3\2\2\2\u00a3\u00a8\5\34\17\2\u00a4"+
		"\u00a5\7\"\2\2\u00a5\u00a7\5\34\17\2\u00a6\u00a4\3\2\2\2\u00a7\u00aa\3"+
		"\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\21\3\2\2\2\u00aa"+
		"\u00a8\3\2\2\2\u00ab\u00ac\7\64\2\2\u00ac\u00b0\5\34\17\2\u00ad\u00ae"+
		"\7\65\2\2\u00ae\u00b0\5\34\17\2\u00af\u00ab\3\2\2\2\u00af\u00ad\3\2\2"+
		"\2\u00b0\23\3\2\2\2\u00b1\u00b2\b\13\1\2\u00b2\u00b5\5\26\f\2\u00b3\u00b5"+
		"\5\30\r\2\u00b4\u00b1\3\2\2\2\u00b4\u00b3\3\2\2\2\u00b5\u00bb\3\2\2\2"+
		"\u00b6\u00b7\f\4\2\2\u00b7\u00b8\7\31\2\2\u00b8\u00ba\7\32\2\2\u00b9\u00b6"+
		"\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb\u00bc\3\2\2\2\u00bc"+
		"\25\3\2\2\2\u00bd\u00bb\3\2\2\2\u00be\u00bf\t\2\2\2\u00bf\27\3\2\2\2\u00c0"+
		"\u00c1\7;\2\2\u00c1\u00c2\7\27\2\2\u00c2\u00c3\5\32\16\2\u00c3\u00c4\7"+
		"\"\2\2\u00c4\u00c5\5\32\16\2\u00c5\u00c6\7\30\2\2\u00c6\31\3\2\2\2\u00c7"+
		"\u00ce\5\26\f\2\u00c8\u00c9\5\24\13\2\u00c9\u00ca\7\31\2\2\u00ca\u00cb"+
		"\7\32\2\2\u00cb\u00ce\3\2\2\2\u00cc\u00ce\7;\2\2\u00cd\u00c7\3\2\2\2\u00cd"+
		"\u00c8\3\2\2\2\u00cd\u00cc\3\2\2\2\u00ce\33\3\2\2\2\u00cf\u00d0\b\17\1"+
		"\2\u00d0\u00d1\5 \21\2\u00d1\u00d2\5\34\17\5\u00d2\u00df\3\2\2\2\u00d3"+
		"\u00df\7\33\2\2\u00d4\u00df\7\62\2\2\u00d5\u00df\7<\2\2\u00d6\u00df\7"+
		"=\2\2\u00d7\u00df\5\36\20\2\u00d8\u00df\5\60\31\2\u00d9\u00df\5\62\32"+
		"\2\u00da\u00db\7\27\2\2\u00db\u00dc\5\34\17\2\u00dc\u00dd\7\30\2\2\u00dd"+
		"\u00df\3\2\2\2\u00de\u00cf\3\2\2\2\u00de\u00d3\3\2\2\2\u00de\u00d4\3\2"+
		"\2\2\u00de\u00d5\3\2\2\2\u00de\u00d6\3\2\2\2\u00de\u00d7\3\2\2\2\u00de"+
		"\u00d8\3\2\2\2\u00de\u00d9\3\2\2\2\u00de\u00da\3\2\2\2\u00df\u00e6\3\2"+
		"\2\2\u00e0\u00e1\f\4\2\2\u00e1\u00e2\5\"\22\2\u00e2\u00e3\5\34\17\5\u00e3"+
		"\u00e5\3\2\2\2\u00e4\u00e0\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2"+
		"\2\2\u00e6\u00e7\3\2\2\2\u00e7\35\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00ea"+
		"\7\61\2\2\u00ea\37\3\2\2\2\u00eb\u00ec\t\3\2\2\u00ec!\3\2\2\2\u00ed\u00f4"+
		"\5$\23\2\u00ee\u00f4\5&\24\2\u00ef\u00f4\5(\25\2\u00f0\u00f4\5*\26\2\u00f1"+
		"\u00f4\5,\27\2\u00f2\u00f4\5.\30\2\u00f3\u00ed\3\2\2\2\u00f3\u00ee\3\2"+
		"\2\2\u00f3\u00ef\3\2\2\2\u00f3\u00f0\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f3"+
		"\u00f2\3\2\2\2\u00f4#\3\2\2\2\u00f5\u00f6\t\4\2\2\u00f6%\3\2\2\2\u00f7"+
		"\u00f8\t\5\2\2\u00f8\'\3\2\2\2\u00f9\u00fa\t\6\2\2\u00fa)\3\2\2\2\u00fb"+
		"\u00fc\t\7\2\2\u00fc+\3\2\2\2\u00fd\u00fe\7\16\2\2\u00fe-\3\2\2\2\u00ff"+
		"\u0100\7\17\2\2\u0100/\3\2\2\2\u0101\u0102\7@\2\2\u0102\61\3\2\2\2\u0103"+
		"\u0108\5\60\31\2\u0104\u0105\7\31\2\2\u0105\u0106\5\34\17\2\u0106\u0107"+
		"\7\32\2\2\u0107\u0109\3\2\2\2\u0108\u0104\3\2\2\2\u0109\u010a\3\2\2\2"+
		"\u010a\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\63\3\2\2\2\u010c\u0115"+
		"\7\31\2\2\u010d\u0112\5\34\17\2\u010e\u010f\7\"\2\2\u010f\u0111\5\34\17"+
		"\2\u0110\u010e\3\2\2\2\u0111\u0114\3\2\2\2\u0112\u0110\3\2\2\2\u0112\u0113"+
		"\3\2\2\2\u0113\u0116\3\2\2\2\u0114\u0112\3\2\2\2\u0115\u010d\3\2\2\2\u0115"+
		"\u0116\3\2\2\2\u0116\u0117\3\2\2\2\u0117\u0118\7\32\2\2\u0118\65\3\2\2"+
		"\2\25:EQ\u0080\u0087\u008d\u009d\u00a1\u00a8\u00af\u00b4\u00bb\u00cd\u00de"+
		"\u00e6\u00f3\u010a\u0112\u0115";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
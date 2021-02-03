// Generated from WACCLexer.g4 by ANTLR 4.5
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WACCLexer extends Lexer {
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
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"PLUS", "MINUS", "MUL", "DIV", "MOD", "GT", "GTE", "LT", "LTE", "EQ", 
		"NEQ", "AND", "OR", "NOT", "LEN", "ORD", "CHR", "BEGIN", "END", "IS", 
		"OPEN_PARENTHESES", "CLOSE_PARENTHESES", "OPEN_SQUARE", "CLOSE_SQUARE", 
		"DIGIT", "SIGN", "INT_LITER", "SKP", "BREAK", "CONTINUE", "EXIT", "SEMICOLON", 
		"COLON", "COMMA", "ASSIGN", "READ", "FREE", "RETURN", "PRINT", "PRINTLN", 
		"IF", "THEN", "ELSE", "FI", "WHILE", "DO", "DONE", "FOR", "NULL", "BOOL_LITER", 
		"ESCAPED_CHAR", "NEWPAIR", "FST", "SND", "CALL", "INT", "BOOL", "CHAR", 
		"STRING", "PAIR", "QUOTE", "D_QUOTE", "CHARACTER", "CHAR_LITER", "STR_LITER", 
		"WS", "COMMENT", "ID"
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


	public WACCLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WACCLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2@\u01b0\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\3\2\3\2\3\3\3\3\3\4\3"+
		"\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3"+
		"\13\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3"+
		"\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3"+
		"\31\3\31\3\32\3\32\3\33\3\33\5\33\u00d3\n\33\3\34\5\34\u00d6\n\34\3\34"+
		"\6\34\u00d9\n\34\r\34\16\34\u00da\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3"+
		"\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3\37\3 \3 "+
		"\3 \3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3\'"+
		"\3\'\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3)\3)\3*"+
		"\3*\3*\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3-\3-\3-\3.\3.\3.\3.\3.\3.\3/\3/"+
		"\3/\3\60\3\60\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3"+
		"\62\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\63\5\63\u014d\n\63\3\64"+
		"\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\67"+
		"\3\67\3\67\3\67\38\38\38\38\38\39\39\39\39\3:\3:\3:\3:\3:\3;\3;\3;\3;"+
		"\3;\3<\3<\3<\3<\3<\3<\3<\3=\3=\3=\3=\3=\3>\3>\3?\3?\3@\3@\3@\5@\u0187"+
		"\n@\3A\3A\3A\3A\3B\3B\7B\u018f\nB\fB\16B\u0192\13B\3B\3B\3C\6C\u0197\n"+
		"C\rC\16C\u0198\3C\3C\3D\3D\7D\u019f\nD\fD\16D\u01a2\13D\3D\3D\3D\3D\3"+
		"E\5E\u01a9\nE\3E\7E\u01ac\nE\fE\16E\u01af\13E\2\2F\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\26+\27-\30/\31\61\32\63\2\65\2\67\339\34;\35=\36?\37A C!E\"G#I$K"+
		"%M&O\'Q(S)U*W+Y,[-]._/a\60c\61e\62g\2i\63k\64m\65o\66q\67s8u9w:y;{\2}"+
		"\2\177\2\u0081<\u0083=\u0085>\u0087?\u0089@\3\2\b\13\2$$))\62\62^^ddh"+
		"hppttvv\5\2$$))^^\5\2\13\f\17\17\"\"\3\2\f\f\5\2C\\aac|\6\2\62;C\\aac"+
		"|\u01b2\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3"+
		"\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2"+
		"\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2"+
		"/\3\2\2\2\2\61\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2"+
		"?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3"+
		"\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2"+
		"\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2"+
		"e\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3"+
		"\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2"+
		"\2\2\u0085\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\3\u008b\3\2\2\2\5\u008d"+
		"\3\2\2\2\7\u008f\3\2\2\2\t\u0091\3\2\2\2\13\u0093\3\2\2\2\r\u0095\3\2"+
		"\2\2\17\u0097\3\2\2\2\21\u009a\3\2\2\2\23\u009c\3\2\2\2\25\u009f\3\2\2"+
		"\2\27\u00a2\3\2\2\2\31\u00a5\3\2\2\2\33\u00a8\3\2\2\2\35\u00ab\3\2\2\2"+
		"\37\u00ad\3\2\2\2!\u00b1\3\2\2\2#\u00b5\3\2\2\2%\u00b9\3\2\2\2\'\u00bf"+
		"\3\2\2\2)\u00c3\3\2\2\2+\u00c6\3\2\2\2-\u00c8\3\2\2\2/\u00ca\3\2\2\2\61"+
		"\u00cc\3\2\2\2\63\u00ce\3\2\2\2\65\u00d2\3\2\2\2\67\u00d5\3\2\2\29\u00dc"+
		"\3\2\2\2;\u00e1\3\2\2\2=\u00e7\3\2\2\2?\u00f0\3\2\2\2A\u00f5\3\2\2\2C"+
		"\u00f7\3\2\2\2E\u00f9\3\2\2\2G\u00fb\3\2\2\2I\u00fd\3\2\2\2K\u0102\3\2"+
		"\2\2M\u0107\3\2\2\2O\u010e\3\2\2\2Q\u0114\3\2\2\2S\u011c\3\2\2\2U\u011f"+
		"\3\2\2\2W\u0124\3\2\2\2Y\u0129\3\2\2\2[\u012c\3\2\2\2]\u0132\3\2\2\2_"+
		"\u0135\3\2\2\2a\u013a\3\2\2\2c\u013e\3\2\2\2e\u014c\3\2\2\2g\u014e\3\2"+
		"\2\2i\u0150\3\2\2\2k\u0158\3\2\2\2m\u015c\3\2\2\2o\u0160\3\2\2\2q\u0165"+
		"\3\2\2\2s\u0169\3\2\2\2u\u016e\3\2\2\2w\u0173\3\2\2\2y\u017a\3\2\2\2{"+
		"\u017f\3\2\2\2}\u0181\3\2\2\2\177\u0186\3\2\2\2\u0081\u0188\3\2\2\2\u0083"+
		"\u018c\3\2\2\2\u0085\u0196\3\2\2\2\u0087\u019c\3\2\2\2\u0089\u01a8\3\2"+
		"\2\2\u008b\u008c\7-\2\2\u008c\4\3\2\2\2\u008d\u008e\7/\2\2\u008e\6\3\2"+
		"\2\2\u008f\u0090\7,\2\2\u0090\b\3\2\2\2\u0091\u0092\7\61\2\2\u0092\n\3"+
		"\2\2\2\u0093\u0094\7\'\2\2\u0094\f\3\2\2\2\u0095\u0096\7@\2\2\u0096\16"+
		"\3\2\2\2\u0097\u0098\7@\2\2\u0098\u0099\7?\2\2\u0099\20\3\2\2\2\u009a"+
		"\u009b\7>\2\2\u009b\22\3\2\2\2\u009c\u009d\7>\2\2\u009d\u009e\7?\2\2\u009e"+
		"\24\3\2\2\2\u009f\u00a0\7?\2\2\u00a0\u00a1\7?\2\2\u00a1\26\3\2\2\2\u00a2"+
		"\u00a3\7#\2\2\u00a3\u00a4\7?\2\2\u00a4\30\3\2\2\2\u00a5\u00a6\7(\2\2\u00a6"+
		"\u00a7\7(\2\2\u00a7\32\3\2\2\2\u00a8\u00a9\7~\2\2\u00a9\u00aa\7~\2\2\u00aa"+
		"\34\3\2\2\2\u00ab\u00ac\7#\2\2\u00ac\36\3\2\2\2\u00ad\u00ae\7n\2\2\u00ae"+
		"\u00af\7g\2\2\u00af\u00b0\7p\2\2\u00b0 \3\2\2\2\u00b1\u00b2\7q\2\2\u00b2"+
		"\u00b3\7t\2\2\u00b3\u00b4\7f\2\2\u00b4\"\3\2\2\2\u00b5\u00b6\7e\2\2\u00b6"+
		"\u00b7\7j\2\2\u00b7\u00b8\7t\2\2\u00b8$\3\2\2\2\u00b9\u00ba\7d\2\2\u00ba"+
		"\u00bb\7g\2\2\u00bb\u00bc\7i\2\2\u00bc\u00bd\7k\2\2\u00bd\u00be\7p\2\2"+
		"\u00be&\3\2\2\2\u00bf\u00c0\7g\2\2\u00c0\u00c1\7p\2\2\u00c1\u00c2\7f\2"+
		"\2\u00c2(\3\2\2\2\u00c3\u00c4\7k\2\2\u00c4\u00c5\7u\2\2\u00c5*\3\2\2\2"+
		"\u00c6\u00c7\7*\2\2\u00c7,\3\2\2\2\u00c8\u00c9\7+\2\2\u00c9.\3\2\2\2\u00ca"+
		"\u00cb\7]\2\2\u00cb\60\3\2\2\2\u00cc\u00cd\7_\2\2\u00cd\62\3\2\2\2\u00ce"+
		"\u00cf\4\62;\2\u00cf\64\3\2\2\2\u00d0\u00d3\5\3\2\2\u00d1\u00d3\5\5\3"+
		"\2\u00d2\u00d0\3\2\2\2\u00d2\u00d1\3\2\2\2\u00d3\66\3\2\2\2\u00d4\u00d6"+
		"\5\65\33\2\u00d5\u00d4\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d8\3\2\2\2"+
		"\u00d7\u00d9\5\63\32\2\u00d8\u00d7\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00d8"+
		"\3\2\2\2\u00da\u00db\3\2\2\2\u00db8\3\2\2\2\u00dc\u00dd\7u\2\2\u00dd\u00de"+
		"\7m\2\2\u00de\u00df\7k\2\2\u00df\u00e0\7r\2\2\u00e0:\3\2\2\2\u00e1\u00e2"+
		"\7d\2\2\u00e2\u00e3\7t\2\2\u00e3\u00e4\7g\2\2\u00e4\u00e5\7c\2\2\u00e5"+
		"\u00e6\7m\2\2\u00e6<\3\2\2\2\u00e7\u00e8\7e\2\2\u00e8\u00e9\7q\2\2\u00e9"+
		"\u00ea\7p\2\2\u00ea\u00eb\7v\2\2\u00eb\u00ec\7k\2\2\u00ec\u00ed\7p\2\2"+
		"\u00ed\u00ee\7w\2\2\u00ee\u00ef\7g\2\2\u00ef>\3\2\2\2\u00f0\u00f1\7g\2"+
		"\2\u00f1\u00f2\7z\2\2\u00f2\u00f3\7k\2\2\u00f3\u00f4\7v\2\2\u00f4@\3\2"+
		"\2\2\u00f5\u00f6\7=\2\2\u00f6B\3\2\2\2\u00f7\u00f8\7<\2\2\u00f8D\3\2\2"+
		"\2\u00f9\u00fa\7.\2\2\u00faF\3\2\2\2\u00fb\u00fc\7?\2\2\u00fcH\3\2\2\2"+
		"\u00fd\u00fe\7t\2\2\u00fe\u00ff\7g\2\2\u00ff\u0100\7c\2\2\u0100\u0101"+
		"\7f\2\2\u0101J\3\2\2\2\u0102\u0103\7h\2\2\u0103\u0104\7t\2\2\u0104\u0105"+
		"\7g\2\2\u0105\u0106\7g\2\2\u0106L\3\2\2\2\u0107\u0108\7t\2\2\u0108\u0109"+
		"\7g\2\2\u0109\u010a\7v\2\2\u010a\u010b\7w\2\2\u010b\u010c\7t\2\2\u010c"+
		"\u010d\7p\2\2\u010dN\3\2\2\2\u010e\u010f\7r\2\2\u010f\u0110\7t\2\2\u0110"+
		"\u0111\7k\2\2\u0111\u0112\7p\2\2\u0112\u0113\7v\2\2\u0113P\3\2\2\2\u0114"+
		"\u0115\7r\2\2\u0115\u0116\7t\2\2\u0116\u0117\7k\2\2\u0117\u0118\7p\2\2"+
		"\u0118\u0119\7v\2\2\u0119\u011a\7n\2\2\u011a\u011b\7p\2\2\u011bR\3\2\2"+
		"\2\u011c\u011d\7k\2\2\u011d\u011e\7h\2\2\u011eT\3\2\2\2\u011f\u0120\7"+
		"v\2\2\u0120\u0121\7j\2\2\u0121\u0122\7g\2\2\u0122\u0123\7p\2\2\u0123V"+
		"\3\2\2\2\u0124\u0125\7g\2\2\u0125\u0126\7n\2\2\u0126\u0127\7u\2\2\u0127"+
		"\u0128\7g\2\2\u0128X\3\2\2\2\u0129\u012a\7h\2\2\u012a\u012b\7k\2\2\u012b"+
		"Z\3\2\2\2\u012c\u012d\7y\2\2\u012d\u012e\7j\2\2\u012e\u012f\7k\2\2\u012f"+
		"\u0130\7n\2\2\u0130\u0131\7g\2\2\u0131\\\3\2\2\2\u0132\u0133\7f\2\2\u0133"+
		"\u0134\7q\2\2\u0134^\3\2\2\2\u0135\u0136\7f\2\2\u0136\u0137\7q\2\2\u0137"+
		"\u0138\7p\2\2\u0138\u0139\7g\2\2\u0139`\3\2\2\2\u013a\u013b\7h\2\2\u013b"+
		"\u013c\7q\2\2\u013c\u013d\7t\2\2\u013db\3\2\2\2\u013e\u013f\7p\2\2\u013f"+
		"\u0140\7w\2\2\u0140\u0141\7n\2\2\u0141\u0142\7n\2\2\u0142d\3\2\2\2\u0143"+
		"\u0144\7v\2\2\u0144\u0145\7t\2\2\u0145\u0146\7w\2\2\u0146\u014d\7g\2\2"+
		"\u0147\u0148\7h\2\2\u0148\u0149\7c\2\2\u0149\u014a\7n\2\2\u014a\u014b"+
		"\7u\2\2\u014b\u014d\7g\2\2\u014c\u0143\3\2\2\2\u014c\u0147\3\2\2\2\u014d"+
		"f\3\2\2\2\u014e\u014f\t\2\2\2\u014fh\3\2\2\2\u0150\u0151\7p\2\2\u0151"+
		"\u0152\7g\2\2\u0152\u0153\7y\2\2\u0153\u0154\7r\2\2\u0154\u0155\7c\2\2"+
		"\u0155\u0156\7k\2\2\u0156\u0157\7t\2\2\u0157j\3\2\2\2\u0158\u0159\7h\2"+
		"\2\u0159\u015a\7u\2\2\u015a\u015b\7v\2\2\u015bl\3\2\2\2\u015c\u015d\7"+
		"u\2\2\u015d\u015e\7p\2\2\u015e\u015f\7f\2\2\u015fn\3\2\2\2\u0160\u0161"+
		"\7e\2\2\u0161\u0162\7c\2\2\u0162\u0163\7n\2\2\u0163\u0164\7n\2\2\u0164"+
		"p\3\2\2\2\u0165\u0166\7k\2\2\u0166\u0167\7p\2\2\u0167\u0168\7v\2\2\u0168"+
		"r\3\2\2\2\u0169\u016a\7d\2\2\u016a\u016b\7q\2\2\u016b\u016c\7q\2\2\u016c"+
		"\u016d\7n\2\2\u016dt\3\2\2\2\u016e\u016f\7e\2\2\u016f\u0170\7j\2\2\u0170"+
		"\u0171\7c\2\2\u0171\u0172\7t\2\2\u0172v\3\2\2\2\u0173\u0174\7u\2\2\u0174"+
		"\u0175\7v\2\2\u0175\u0176\7t\2\2\u0176\u0177\7k\2\2\u0177\u0178\7p\2\2"+
		"\u0178\u0179\7i\2\2\u0179x\3\2\2\2\u017a\u017b\7r\2\2\u017b\u017c\7c\2"+
		"\2\u017c\u017d\7k\2\2\u017d\u017e\7t\2\2\u017ez\3\2\2\2\u017f\u0180\7"+
		")\2\2\u0180|\3\2\2\2\u0181\u0182\7$\2\2\u0182~\3\2\2\2\u0183\u0187\n\3"+
		"\2\2\u0184\u0185\7^\2\2\u0185\u0187\5g\64\2\u0186\u0183\3\2\2\2\u0186"+
		"\u0184\3\2\2\2\u0187\u0080\3\2\2\2\u0188\u0189\5{>\2\u0189\u018a\5\177"+
		"@\2\u018a\u018b\5{>\2\u018b\u0082\3\2\2\2\u018c\u0190\5}?\2\u018d\u018f"+
		"\5\177@\2\u018e\u018d\3\2\2\2\u018f\u0192\3\2\2\2\u0190\u018e\3\2\2\2"+
		"\u0190\u0191\3\2\2\2\u0191\u0193\3\2\2\2\u0192\u0190\3\2\2\2\u0193\u0194"+
		"\5}?\2\u0194\u0084\3\2\2\2\u0195\u0197\t\4\2\2\u0196\u0195\3\2\2\2\u0197"+
		"\u0198\3\2\2\2\u0198\u0196\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019a\3\2"+
		"\2\2\u019a\u019b\bC\2\2\u019b\u0086\3\2\2\2\u019c\u01a0\7%\2\2\u019d\u019f"+
		"\n\5\2\2\u019e\u019d\3\2\2\2\u019f\u01a2\3\2\2\2\u01a0\u019e\3\2\2\2\u01a0"+
		"\u01a1\3\2\2\2\u01a1\u01a3\3\2\2\2\u01a2\u01a0\3\2\2\2\u01a3\u01a4\7\f"+
		"\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a6\bD\2\2\u01a6\u0088\3\2\2\2\u01a7"+
		"\u01a9\t\6\2\2\u01a8\u01a7\3\2\2\2\u01a9\u01ad\3\2\2\2\u01aa\u01ac\t\7"+
		"\2\2\u01ab\u01aa\3\2\2\2\u01ac\u01af\3\2\2\2\u01ad\u01ab\3\2\2\2\u01ad"+
		"\u01ae\3\2\2\2\u01ae\u008a\3\2\2\2\u01af\u01ad\3\2\2\2\16\2\u00d2\u00d5"+
		"\u00da\u014c\u0186\u0190\u0198\u01a0\u01a8\u01ab\u01ad\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
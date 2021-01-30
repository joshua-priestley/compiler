// Generated from C:/Users/Euan Imperial/IdeaProjects/wacc_21/antlr_config\WACCLexer.g4 by ANTLR 4.9.1
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
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PLUS=1, MINUS=2, MUL=3, DIV=4, MOD=5, GT=6, GTE=7, LT=8, LTE=9, EQ=10, 
		NEQ=11, AND=12, OR=13, XOR=14, POW=15, NOT=16, OPEN_PARENTHESES=17, CLOSE_PARENTHESES=18, 
		OPEN_SQUARE=19, CLOSE_SQUARE=20, OPEN_CURLY=21, CLOSE_CURLY=22, INTEGER=23, 
		FLOAT=24, SKP=25, BREAK=26, CONTINUE=27, EXIT=28, SEMICOLON=29, COLON=30, 
		COMMA=31, ASSIGN=32, IF=33, THEN=34, ELSE=35, FI=36, WHILE=37, DO=38, 
		DONE=39, FOR=40, NULL=41, BOOL=42, TRUE=43, FALSE=44, QUOTE=45, D_QUOTE=46, 
		CHARACTER=47, CHAR=48, STRING=49, SPACE=50, WS=51, COMMENT=52;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"PLUS", "MINUS", "MUL", "DIV", "MOD", "GT", "GTE", "LT", "LTE", "EQ", 
			"NEQ", "AND", "OR", "XOR", "POW", "NOT", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", 
			"OPEN_SQUARE", "CLOSE_SQUARE", "OPEN_CURLY", "CLOSE_CURLY", "DIGIT", 
			"SIGN", "INTEGER", "FLOAT", "SKP", "BREAK", "CONTINUE", "EXIT", "SEMICOLON", 
			"COLON", "COMMA", "ASSIGN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", 
			"DONE", "FOR", "NULL", "BOOL", "TRUE", "FALSE", "QUOTE", "D_QUOTE", "CHARACTER", 
			"CHAR", "STRING", "SPACE", "WS", "COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'+'", "'-'", "'*'", "'/'", "'%'", "'>'", "'>='", "'<'", "'<='", 
			"'=='", "'!='", "'&&'", "'||'", "'^'", "'**'", "'!'", "'('", "')'", "'['", 
			"']'", "'{'", "'}'", null, null, "'skip'", "'break'", "'continue'", "'exit'", 
			"';'", "':'", "','", "'='", "'if'", "'then'", "'else'", "'fi'", "'while'", 
			"'do'", "'done'", "'for'", "'null'", null, "'true'", "'false'", "'''", 
			"'\"'", null, null, null, "' '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PLUS", "MINUS", "MUL", "DIV", "MOD", "GT", "GTE", "LT", "LTE", 
			"EQ", "NEQ", "AND", "OR", "XOR", "POW", "NOT", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", 
			"OPEN_SQUARE", "CLOSE_SQUARE", "OPEN_CURLY", "CLOSE_CURLY", "INTEGER", 
			"FLOAT", "SKP", "BREAK", "CONTINUE", "EXIT", "SEMICOLON", "COLON", "COMMA", 
			"ASSIGN", "IF", "THEN", "ELSE", "FI", "WHILE", "DO", "DONE", "FOR", "NULL", 
			"BOOL", "TRUE", "FALSE", "QUOTE", "D_QUOTE", "CHARACTER", "CHAR", "STRING", 
			"SPACE", "WS", "COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\66\u0132\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\5\31\u00a7\n\31\3\32\6\32\u00aa\n\32\r\32\16\32\u00ab\3\33\6\33"+
		"\u00af\n\33\r\33\16\33\u00b0\3\33\3\33\6\33\u00b5\n\33\r\33\16\33\u00b6"+
		"\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36"+
		"\3\36\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3 \3 \3!\3!\3"+
		"\"\3\"\3#\3#\3$\3$\3$\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3(\3("+
		"\3(\3(\3(\3(\3)\3)\3)\3*\3*\3*\3*\3*\3+\3+\3+\3+\3,\3,\3,\3,\3,\3-\3-"+
		"\5-\u0103\n-\3.\3.\3.\3.\3.\3/\3/\3/\3/\3/\3/\3\60\3\60\3\61\3\61\3\62"+
		"\3\62\3\63\3\63\3\63\3\63\3\64\3\64\7\64\u011c\n\64\f\64\16\64\u011f\13"+
		"\64\3\64\3\64\3\65\3\65\3\66\6\66\u0126\n\66\r\66\16\66\u0127\3\67\3\67"+
		"\7\67\u012c\n\67\f\67\16\67\u012f\13\67\3\67\3\67\2\28\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24"+
		"\'\25)\26+\27-\30/\2\61\2\63\31\65\32\67\339\34;\35=\36?\37A C!E\"G#I"+
		"$K%M&O\'Q(S)U*W+Y,[-]._/a\60c\61e\62g\63i\64k\65m\66\3\2\5\4\2C\\c|\4"+
		"\2\13\f\"\"\3\2\f\f\2\u0137\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3"+
		"\2\2\2\2-\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2"+
		";\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3"+
		"\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2"+
		"\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2"+
		"a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3"+
		"\2\2\2\3o\3\2\2\2\5q\3\2\2\2\7s\3\2\2\2\tu\3\2\2\2\13w\3\2\2\2\ry\3\2"+
		"\2\2\17{\3\2\2\2\21~\3\2\2\2\23\u0080\3\2\2\2\25\u0083\3\2\2\2\27\u0086"+
		"\3\2\2\2\31\u0089\3\2\2\2\33\u008c\3\2\2\2\35\u008f\3\2\2\2\37\u0091\3"+
		"\2\2\2!\u0094\3\2\2\2#\u0096\3\2\2\2%\u0098\3\2\2\2\'\u009a\3\2\2\2)\u009c"+
		"\3\2\2\2+\u009e\3\2\2\2-\u00a0\3\2\2\2/\u00a2\3\2\2\2\61\u00a6\3\2\2\2"+
		"\63\u00a9\3\2\2\2\65\u00ae\3\2\2\2\67\u00b8\3\2\2\29\u00bd\3\2\2\2;\u00c3"+
		"\3\2\2\2=\u00cc\3\2\2\2?\u00d1\3\2\2\2A\u00d3\3\2\2\2C\u00d5\3\2\2\2E"+
		"\u00d7\3\2\2\2G\u00d9\3\2\2\2I\u00dc\3\2\2\2K\u00e1\3\2\2\2M\u00e6\3\2"+
		"\2\2O\u00e9\3\2\2\2Q\u00ef\3\2\2\2S\u00f2\3\2\2\2U\u00f7\3\2\2\2W\u00fb"+
		"\3\2\2\2Y\u0102\3\2\2\2[\u0104\3\2\2\2]\u0109\3\2\2\2_\u010f\3\2\2\2a"+
		"\u0111\3\2\2\2c\u0113\3\2\2\2e\u0115\3\2\2\2g\u0119\3\2\2\2i\u0122\3\2"+
		"\2\2k\u0125\3\2\2\2m\u0129\3\2\2\2op\7-\2\2p\4\3\2\2\2qr\7/\2\2r\6\3\2"+
		"\2\2st\7,\2\2t\b\3\2\2\2uv\7\61\2\2v\n\3\2\2\2wx\7\'\2\2x\f\3\2\2\2yz"+
		"\7@\2\2z\16\3\2\2\2{|\7@\2\2|}\7?\2\2}\20\3\2\2\2~\177\7>\2\2\177\22\3"+
		"\2\2\2\u0080\u0081\7>\2\2\u0081\u0082\7?\2\2\u0082\24\3\2\2\2\u0083\u0084"+
		"\7?\2\2\u0084\u0085\7?\2\2\u0085\26\3\2\2\2\u0086\u0087\7#\2\2\u0087\u0088"+
		"\7?\2\2\u0088\30\3\2\2\2\u0089\u008a\7(\2\2\u008a\u008b\7(\2\2\u008b\32"+
		"\3\2\2\2\u008c\u008d\7~\2\2\u008d\u008e\7~\2\2\u008e\34\3\2\2\2\u008f"+
		"\u0090\7`\2\2\u0090\36\3\2\2\2\u0091\u0092\7,\2\2\u0092\u0093\7,\2\2\u0093"+
		" \3\2\2\2\u0094\u0095\7#\2\2\u0095\"\3\2\2\2\u0096\u0097\7*\2\2\u0097"+
		"$\3\2\2\2\u0098\u0099\7+\2\2\u0099&\3\2\2\2\u009a\u009b\7]\2\2\u009b("+
		"\3\2\2\2\u009c\u009d\7_\2\2\u009d*\3\2\2\2\u009e\u009f\7}\2\2\u009f,\3"+
		"\2\2\2\u00a0\u00a1\7\177\2\2\u00a1.\3\2\2\2\u00a2\u00a3\4\62;\2\u00a3"+
		"\60\3\2\2\2\u00a4\u00a7\5\3\2\2\u00a5\u00a7\5\5\3\2\u00a6\u00a4\3\2\2"+
		"\2\u00a6\u00a5\3\2\2\2\u00a7\62\3\2\2\2\u00a8\u00aa\5/\30\2\u00a9\u00a8"+
		"\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac"+
		"\64\3\2\2\2\u00ad\u00af\5/\30\2\u00ae\u00ad\3\2\2\2\u00af\u00b0\3\2\2"+
		"\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b4"+
		"\7\60\2\2\u00b3\u00b5\5/\30\2\u00b4\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2"+
		"\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\66\3\2\2\2\u00b8\u00b9"+
		"\7u\2\2\u00b9\u00ba\7m\2\2\u00ba\u00bb\7k\2\2\u00bb\u00bc\7r\2\2\u00bc"+
		"8\3\2\2\2\u00bd\u00be\7d\2\2\u00be\u00bf\7t\2\2\u00bf\u00c0\7g\2\2\u00c0"+
		"\u00c1\7c\2\2\u00c1\u00c2\7m\2\2\u00c2:\3\2\2\2\u00c3\u00c4\7e\2\2\u00c4"+
		"\u00c5\7q\2\2\u00c5\u00c6\7p\2\2\u00c6\u00c7\7v\2\2\u00c7\u00c8\7k\2\2"+
		"\u00c8\u00c9\7p\2\2\u00c9\u00ca\7w\2\2\u00ca\u00cb\7g\2\2\u00cb<\3\2\2"+
		"\2\u00cc\u00cd\7g\2\2\u00cd\u00ce\7z\2\2\u00ce\u00cf\7k\2\2\u00cf\u00d0"+
		"\7v\2\2\u00d0>\3\2\2\2\u00d1\u00d2\7=\2\2\u00d2@\3\2\2\2\u00d3\u00d4\7"+
		"<\2\2\u00d4B\3\2\2\2\u00d5\u00d6\7.\2\2\u00d6D\3\2\2\2\u00d7\u00d8\7?"+
		"\2\2\u00d8F\3\2\2\2\u00d9\u00da\7k\2\2\u00da\u00db\7h\2\2\u00dbH\3\2\2"+
		"\2\u00dc\u00dd\7v\2\2\u00dd\u00de\7j\2\2\u00de\u00df\7g\2\2\u00df\u00e0"+
		"\7p\2\2\u00e0J\3\2\2\2\u00e1\u00e2\7g\2\2\u00e2\u00e3\7n\2\2\u00e3\u00e4"+
		"\7u\2\2\u00e4\u00e5\7g\2\2\u00e5L\3\2\2\2\u00e6\u00e7\7h\2\2\u00e7\u00e8"+
		"\7k\2\2\u00e8N\3\2\2\2\u00e9\u00ea\7y\2\2\u00ea\u00eb\7j\2\2\u00eb\u00ec"+
		"\7k\2\2\u00ec\u00ed\7n\2\2\u00ed\u00ee\7g\2\2\u00eeP\3\2\2\2\u00ef\u00f0"+
		"\7f\2\2\u00f0\u00f1\7q\2\2\u00f1R\3\2\2\2\u00f2\u00f3\7f\2\2\u00f3\u00f4"+
		"\7q\2\2\u00f4\u00f5\7p\2\2\u00f5\u00f6\7g\2\2\u00f6T\3\2\2\2\u00f7\u00f8"+
		"\7h\2\2\u00f8\u00f9\7q\2\2\u00f9\u00fa\7t\2\2\u00faV\3\2\2\2\u00fb\u00fc"+
		"\7p\2\2\u00fc\u00fd\7w\2\2\u00fd\u00fe\7n\2\2\u00fe\u00ff\7n\2\2\u00ff"+
		"X\3\2\2\2\u0100\u0103\5[.\2\u0101\u0103\5]/\2\u0102\u0100\3\2\2\2\u0102"+
		"\u0101\3\2\2\2\u0103Z\3\2\2\2\u0104\u0105\7v\2\2\u0105\u0106\7t\2\2\u0106"+
		"\u0107\7w\2\2\u0107\u0108\7g\2\2\u0108\\\3\2\2\2\u0109\u010a\7h\2\2\u010a"+
		"\u010b\7c\2\2\u010b\u010c\7n\2\2\u010c\u010d\7u\2\2\u010d\u010e\7g\2\2"+
		"\u010e^\3\2\2\2\u010f\u0110\7)\2\2\u0110`\3\2\2\2\u0111\u0112\7$\2\2\u0112"+
		"b\3\2\2\2\u0113\u0114\t\2\2\2\u0114d\3\2\2\2\u0115\u0116\5_\60\2\u0116"+
		"\u0117\5c\62\2\u0117\u0118\5_\60\2\u0118f\3\2\2\2\u0119\u011d\5a\61\2"+
		"\u011a\u011c\5c\62\2\u011b\u011a\3\2\2\2\u011c\u011f\3\2\2\2\u011d\u011b"+
		"\3\2\2\2\u011d\u011e\3\2\2\2\u011e\u0120\3\2\2\2\u011f\u011d\3\2\2\2\u0120"+
		"\u0121\5a\61\2\u0121h\3\2\2\2\u0122\u0123\7\"\2\2\u0123j\3\2\2\2\u0124"+
		"\u0126\t\3\2\2\u0125\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0125\3\2"+
		"\2\2\u0127\u0128\3\2\2\2\u0128l\3\2\2\2\u0129\u012d\7%\2\2\u012a\u012c"+
		"\n\4\2\2\u012b\u012a\3\2\2\2\u012c\u012f\3\2\2\2\u012d\u012b\3\2\2\2\u012d"+
		"\u012e\3\2\2\2\u012e\u0130\3\2\2\2\u012f\u012d\3\2\2\2\u0130\u0131\b\67"+
		"\2\2\u0131n\3\2\2\2\13\2\u00a6\u00ab\u00b0\u00b6\u0102\u011d\u0127\u012d"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
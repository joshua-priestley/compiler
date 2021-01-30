// Generated from C:/Users/Euan Imperial/IdeaProjects/wacc_21/antlr_config\BasicLexer.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BasicLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		PLUS=1, MINUS=2, MUL=3, DIV=4, MOD=5, GT=6, GTE=7, LT=8, LTE=9, EQ=10, 
		NEQ=11, AND=12, OR=13, XOR=14, POW=15, NOT=16, NEG=17, OPEN_PARENTHESES=18, 
		CLOSE_PARENTHESES=19, INTEGER=20, FLOAT=21, SKP=22, BREAK=23, CONTINUE=24, 
		EXIT=25;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"PLUS", "MINUS", "MUL", "DIV", "MOD", "GT", "GTE", "LT", "LTE", "EQ", 
			"NEQ", "AND", "OR", "XOR", "POW", "NOT", "NEG", "OPEN_PARENTHESES", "CLOSE_PARENTHESES", 
			"DIGIT", "SIGN", "INTEGER", "FLOAT", "SKP", "BREAK", "CONTINUE", "EXIT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'+'", null, "'*'", "'/'", "'%'", "'>'", "'>='", "'<'", "'<='", 
			"'=='", "'!='", "'&&'", "'||'", "'^'", "'**'", "'!'", null, "'('", "')'", 
			null, null, "'skip'", "'break'", "'continue'", "'exit'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "PLUS", "MINUS", "MUL", "DIV", "MOD", "GT", "GTE", "LT", "LTE", 
			"EQ", "NEQ", "AND", "OR", "XOR", "POW", "NOT", "NEG", "OPEN_PARENTHESES", 
			"CLOSE_PARENTHESES", "INTEGER", "FLOAT", "SKP", "BREAK", "CONTINUE", 
			"EXIT"
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


	public BasicLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BasicLexer.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\33\u0095\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\6\3\6\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\5\26k\n\26\3\27\6\27n\n"+
		"\27\r\27\16\27o\3\30\6\30s\n\30\r\30\16\30t\3\30\3\30\6\30y\n\30\r\30"+
		"\16\30z\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3"+
		"\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\2\2\35"+
		"\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20"+
		"\37\21!\22#\23%\24\'\25)\2+\2-\26/\27\61\30\63\31\65\32\67\33\3\2\2\2"+
		"\u0096\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3"+
		"\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2"+
		"\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2"+
		"\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\39\3\2\2\2\5;\3\2\2\2\7=\3\2\2"+
		"\2\t?\3\2\2\2\13A\3\2\2\2\rC\3\2\2\2\17E\3\2\2\2\21H\3\2\2\2\23J\3\2\2"+
		"\2\25M\3\2\2\2\27P\3\2\2\2\31S\3\2\2\2\33V\3\2\2\2\35Y\3\2\2\2\37[\3\2"+
		"\2\2!^\3\2\2\2#`\3\2\2\2%b\3\2\2\2\'d\3\2\2\2)f\3\2\2\2+j\3\2\2\2-m\3"+
		"\2\2\2/r\3\2\2\2\61|\3\2\2\2\63\u0081\3\2\2\2\65\u0087\3\2\2\2\67\u0090"+
		"\3\2\2\29:\7-\2\2:\4\3\2\2\2;<\7/\2\2<\6\3\2\2\2=>\7,\2\2>\b\3\2\2\2?"+
		"@\7\61\2\2@\n\3\2\2\2AB\7\'\2\2B\f\3\2\2\2CD\7@\2\2D\16\3\2\2\2EF\7@\2"+
		"\2FG\7?\2\2G\20\3\2\2\2HI\7>\2\2I\22\3\2\2\2JK\7>\2\2KL\7?\2\2L\24\3\2"+
		"\2\2MN\7?\2\2NO\7?\2\2O\26\3\2\2\2PQ\7#\2\2QR\7?\2\2R\30\3\2\2\2ST\7("+
		"\2\2TU\7(\2\2U\32\3\2\2\2VW\7~\2\2WX\7~\2\2X\34\3\2\2\2YZ\7`\2\2Z\36\3"+
		"\2\2\2[\\\7,\2\2\\]\7,\2\2] \3\2\2\2^_\7#\2\2_\"\3\2\2\2`a\7/\2\2a$\3"+
		"\2\2\2bc\7*\2\2c&\3\2\2\2de\7+\2\2e(\3\2\2\2fg\4\62;\2g*\3\2\2\2hk\5\3"+
		"\2\2ik\5\5\3\2jh\3\2\2\2ji\3\2\2\2k,\3\2\2\2ln\5)\25\2ml\3\2\2\2no\3\2"+
		"\2\2om\3\2\2\2op\3\2\2\2p.\3\2\2\2qs\5)\25\2rq\3\2\2\2st\3\2\2\2tr\3\2"+
		"\2\2tu\3\2\2\2uv\3\2\2\2vx\7\60\2\2wy\5)\25\2xw\3\2\2\2yz\3\2\2\2zx\3"+
		"\2\2\2z{\3\2\2\2{\60\3\2\2\2|}\7u\2\2}~\7m\2\2~\177\7k\2\2\177\u0080\7"+
		"r\2\2\u0080\62\3\2\2\2\u0081\u0082\7d\2\2\u0082\u0083\7t\2\2\u0083\u0084"+
		"\7g\2\2\u0084\u0085\7c\2\2\u0085\u0086\7m\2\2\u0086\64\3\2\2\2\u0087\u0088"+
		"\7e\2\2\u0088\u0089\7q\2\2\u0089\u008a\7p\2\2\u008a\u008b\7v\2\2\u008b"+
		"\u008c\7k\2\2\u008c\u008d\7p\2\2\u008d\u008e\7w\2\2\u008e\u008f\7g\2\2"+
		"\u008f\66\3\2\2\2\u0090\u0091\7g\2\2\u0091\u0092\7z\2\2\u0092\u0093\7"+
		"k\2\2\u0093\u0094\7v\2\2\u00948\3\2\2\2\7\2jotz\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
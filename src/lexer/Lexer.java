package lexer;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class Lexer {

    private int lineNumber = 1;

    private String line = "";

    private boolean isString = false;

    private boolean isSingleQuoteString = false;

    public List<Token> tokenList = new LinkedList<>();

    private StringBuilder stringBuilder;

    private Set<Character> appropriateSymbols = new HashSet<>();

    {
        appropriateSymbols.add('+');
        appropriateSymbols.add('-');
        appropriateSymbols.add('/');
        appropriateSymbols.add('*');
        appropriateSymbols.add('=');
        appropriateSymbols.add('<');
        appropriateSymbols.add('>');
        appropriateSymbols.add(':');
        appropriateSymbols.add(';');
        appropriateSymbols.add(',');
        appropriateSymbols.add('(');
        appropriateSymbols.add(')');
        appropriateSymbols.add('[');
        appropriateSymbols.add(']');
        appropriateSymbols.add('{');
        appropriateSymbols.add('}');
        appropriateSymbols.add('.');
    }

    public boolean isAppropriate(Character c){
        return appropriateSymbols.contains(c);
    }
    private static BufferedReader ReadFile(String path) throws FileNotFoundException {
        FileReader fileReader = new FileReader(path);
            return new BufferedReader(fileReader);
    }

    public void ParseFile(String path) throws Exception {
        BufferedReader br = ReadFile(path);

        do{
            line = br.readLine();
            if(line!=null)
            scan(line);
        }while (line!=null);
    }

    public void scan(String line) throws Exception {
        char peek = ' ';
        boolean dotInString = false;
        boolean isReal = false;
        stringBuilder = null;
        for (int i = 0; i < line.length(); ++i) {
            peek = line.charAt(i);
            if(!isString && !isSingleQuoteString) {
                if (peek == ' ' || peek == '\t' || peek == '\n') {
                    if (stringBuilder != null)
                        scan(stringBuilder.toString());
                }
                if (Character.isDigit(peek)) {
                    if (stringBuilder == null) {
                        stringBuilder = new StringBuilder();
                    }
                    stringBuilder.append(peek);
                    do {
                        if (i + 1 < line.length()) {
                            i = i + 1;
                            peek = line.charAt(i);
                            if (Character.isDigit(peek)) {
                                stringBuilder.append(peek);
                            } else if (peek == '.') {
                                if (!dotInString) {
                                    dotInString = true;
                                    stringBuilder.append(peek);
                                    isReal = true;
                                } else {
                                    throw new ArithmeticException();
                                }
                            } else if (peek == ' ' || peek == '\t' || peek == '\n') {
                                if (isReal) {
                                    tokenList.add(new RealToken(Float.parseFloat(stringBuilder.toString())));
                                    stringBuilder = null;
                                    isReal = false;
                                    dotInString = false;
                                    break;
                                } else {
                                    tokenList.add(new IntToken(Integer.parseInt(stringBuilder.toString())));
                                    stringBuilder = null;
                                    isReal = false;
                                    dotInString = false;
                                    break;
                                }
                            } else if (dotInString) throw new TypeNotPresentException("the one, you wrote is", null);
                        } else {
                            if (isReal) {
                                tokenList.add(new RealToken(Float.parseFloat(stringBuilder.toString())));
                                stringBuilder = null;
                                isReal = false;
                                dotInString = false;
                                break;
                            } else {
                                tokenList.add(new IntToken(Integer.parseInt(stringBuilder.toString())));
                                stringBuilder = null;
                                isReal = false;
                                dotInString = false;
                                break;
                            }
                        }
                    } while (Character.isDigit(peek) || peek == '.');
                }
                if (Character.isLetter(peek) || peek == '_') {
                    if (stringBuilder == null) {
                        stringBuilder = new StringBuilder();
                    }
                    stringBuilder.append(peek);
                    do {
                        if (i + 1 < line.length()) {
                            i = i + 1;
                            peek = line.charAt(i);
                            if (Character.isDigit(peek) || Character.isLetter(peek) || peek == '_') {
                                stringBuilder.append(peek);
                            } else if (peek != ' ' && peek != '\t' && peek != '\n') {
                                if (isAppropriate(peek)) {
                                    Tag tag = returnTag(stringBuilder.toString());
                                    if (tag != null){
                                        tokenList.add(new Token(tag));
                                    }
                                    else
                                        tokenList.add(new IdentifierToken(stringBuilder.toString()));
                                    stringBuilder = null;
                                    break;
                                } else
                                    throw new TypeNotPresentException("the one, you wrote is", null);
                            }
                        } else {
                            Tag tag = returnTag(stringBuilder.toString());
                            if (tag != null){
                                tokenList.add(new Token(tag));
                            }
                            else
                                tokenList.add(new IdentifierToken(stringBuilder.toString()));
                            stringBuilder = null;
                            break;
                        }
                    } while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_');
                    if (stringBuilder != null) {
                        Tag tag = returnTag(stringBuilder.toString());
                        if (tag != null){
                            tokenList.add(new Token(tag));
                        }
                        else
                            tokenList.add(new IdentifierToken(stringBuilder.toString()));
                        stringBuilder = null;
                    }
                }
                if (isAppropriate(peek)) {
                    if (stringBuilder == null) {
                        stringBuilder = new StringBuilder();
                    } else {
                        tokenList.add(new IdentifierToken(stringBuilder.toString()));
                        stringBuilder = new StringBuilder();
                    }
                    stringBuilder.append(peek);
                    switch (peek) {
                        case '+':
                        case '*':
                        case '=':
                        case ';':
                        case '(':
                        case '[':
                        case '{':
                        case ',':
                        case '-': {
                            if (i + 1 < line.length()) {
                                i += 1;
                                peek = line.charAt(i);
                                checkNext(peek);
                            }
                            else{
                                Tag tag;
                                switch (peek){
                                    case '+':{tag = Tag.PLUS;break;}
                                    case '*':{tag = Tag.MULT;break;}
                                    case '=':{tag = Tag.EQ;break;}
                                    case ';':{tag = Tag.SEPARATOR;break;}
                                    case '(':{tag = Tag.OPEN_RND_BR;break;}
                                    case '[':{tag = Tag.OPEN_SQR_BR;break;}
                                    case '{':{tag = Tag.OPEN_CRL_BR;break;}
                                    case ',':{tag = Tag.COMMA;break;}
                                    default:{tag = Tag.MINUS;break;}
                                }
                                tokenList.add(new Token(tag));
                                stringBuilder = null;
                            }
                            break;
                        }
                        case ')':
                        case ']':
                        case '}':{
                            if (i + 1 < line.length()) {
                                i += 1;
                                peek = line.charAt(i);
                                checkNextForClosingBrackets(peek);
                            }
                            else{
                                Tag tag;
                                switch (peek) {
                                    case ')': {
                                        tag = Tag.CLOS_RND_BR;
                                        break;
                                    }
                                    case ']': {
                                        tag = Tag.CLOS_SQR_BR;
                                        break;
                                    }
                                    default: {
                                        tag = Tag.CLOS_CRL_BR;
                                        break;
                                    }
                                }
                                tokenList.add(new Token(tag));
                                stringBuilder = null;
                            }
                            break;
                        }
                        case '>':
                        case '/':
                        case ':':
                        case '<': {
                            if (i + 1 < line.length()) {
                                i += 1;
                                peek = line.charAt(i);
                                if (peek == '=') {
                                    stringBuilder.append(peek);
                                    Tag tag;
                                    switch (stringBuilder.toString()){
                                        case ">=":{tag = Tag.GE; break;}
                                        case "/=":{tag = Tag.NE; break;}
                                        case ":=":{tag = Tag.ASSIGN; break;}
                                        default:{tag = Tag.LE; break;}
                                    }
                                    tokenList.add(new Token(tag));
                                    stringBuilder = null;
                                    if (i+1 < line.length() && isAppropriate(line.charAt(i + 1))) {
                                        throw new TypeNotPresentException("the one, you wrote is", null);
                                    }
                                } else {
                                    if (Character.isLetter(peek) || Character.isDigit(peek) || peek == ' ' || peek == '\t' || peek == '\n') {
                                        if (peek != ' ' && peek != '\t' && peek != '\n') {
                                            Tag tag;
                                            switch (stringBuilder.toString()){
                                                case ">":{tag = Tag.GT;break;}
                                                case "/":{tag = Tag.DIV;break;}
                                                case ":":{tag = Tag.COLON;break;}
                                                default:{tag = Tag.LT;break;}
                                            }
                                            tokenList.add(new Token(tag));
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(peek);
                                        } else {
                                            tokenList.add(new IdentifierToken(stringBuilder.toString()));
                                            stringBuilder = null;
                                        }
                                    } else throw new TypeNotPresentException("the one, you wrote is", null);

                                }
                            }
                            break;
                        }
                        case '.':{
                            if (i + 1 < line.length()) {
                                i += 1;
                                peek = line.charAt(i);
                                if(peek == '.'){
                                    stringBuilder.append(peek);
                                    if  (i + 1 < line.length() && (Character.isDigit(line.charAt(i+1)) || Character.isLetter(line.charAt(i+1))
                                            || line.charAt(i+1)==' ' || line.charAt(i+1) == '\t' || line.charAt(i+1) == '\n')){
                                        tokenList.add(new Token(Tag.DOUBLE_DOT));
                                        stringBuilder = null;
                                    } else if(i + 1 == line.length()){
                                        tokenList.add(new Token(Tag.DOUBLE_DOT));
                                        stringBuilder = null;
                                    } else{
                                        throw new TypeNotPresentException("the one, you wrote is", null);
                                    }
                                } else {
                                    throw new TypeNotPresentException("the one, you wrote is", null);
                                }
                            }
                        }
                    }
                }
            }else{
                stringBuilder.append(peek);
            }
            if(peek == '\"'||peek == '\'') {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(peek);
                if((isString && peek != '\'') || (peek!='\"' && isSingleQuoteString)){
                    stringBuilder.delete(stringBuilder.length()-2,stringBuilder.length()-1);
                    tokenList.add(new StrToken(stringBuilder.toString()));
                    System.out.println(stringBuilder.toString());
                    stringBuilder = null;
                    isString = false;
                    isSingleQuoteString = false;
                } else if(isString == false && isSingleQuoteString == false){
                    if (peek == '\"') {
                        isString = true;
                    }
                    else{
                        isSingleQuoteString = true;
                    }
                } else if(isString == true && peek == '\''){
                    throw new TypeNotPresentException("the one, you wrote is", null);
                } else if(isSingleQuoteString == true && peek == '\"'){
                    throw new TypeNotPresentException("the one, you wrote is", null);
                }
            }
        }
    }

    private void checkNext(Character peek){
        if(Character.isLetter(peek) || Character.isDigit(peek) || peek == ' ' || peek == '\t' || peek == '\n') {
            if(peek != ' ' && peek != '\t') {
                Tag tag;
                switch (stringBuilder.toString()){
                    case "+":{tag = Tag.PLUS;break;}
                    case "*":{tag = Tag.MULT;break;}
                    case "=":{tag = Tag.EQ;break;}
                    case ";":{tag = Tag.SEPARATOR;break;}
                    case "(":{tag = Tag.OPEN_RND_BR;break;}
                    case "[":{tag = Tag.OPEN_SQR_BR;break;}
                    case "{":{tag = Tag.OPEN_CRL_BR;break;}
                    case ",":{tag = Tag.COMMA;break;}
                    default:{tag = Tag.MINUS;break;}
                }
                tokenList.add(new Token(tag));
                stringBuilder = new StringBuilder();
                stringBuilder.append(peek);
            }else{
                Tag tag;
                switch (stringBuilder.toString()){
                    case "+":{tag = Tag.PLUS;break;}
                    case "*":{tag = Tag.MULT;break;}
                    case "=":{tag = Tag.EQ;break;}
                    case ";":{tag = Tag.SEPARATOR;break;}
                    case "(":{tag = Tag.OPEN_RND_BR;break;}
                    case "[":{tag = Tag.OPEN_SQR_BR;break;}
                    case "{":{tag = Tag.OPEN_CRL_BR;break;}
                    case ",":{tag = Tag.COMMA;break;}
                    default:{tag = Tag.MINUS;break;}
                }
                tokenList.add(new Token(tag));
                stringBuilder = null;
            }
        }
        else throw new TypeNotPresentException("the one, you wrote is", null);

    }

    private void checkNextForClosingBrackets(Character peek){
        if(Character.isLetter(peek) || Character.isDigit(peek) || peek == ' ' || peek == '\t' || peek == '\n' || peek==',') {
            if(peek != ' ' && peek != '\t') {
                Tag tag;
                switch (stringBuilder.toString()) {
                    case ")": {
                        tag = Tag.CLOS_RND_BR;
                        break;
                    }
                    case "]": {
                        tag = Tag.CLOS_SQR_BR;
                        break;
                    }
                    default: {
                        tag = Tag.CLOS_CRL_BR;
                        break;
                    }
                }
                tokenList.add(new Token(tag));
                stringBuilder = new StringBuilder();
                stringBuilder.append(peek);
            }else{
                Tag tag;
                switch (stringBuilder.toString()) {
                    case ")": {
                        tag = Tag.CLOS_RND_BR;
                        break;
                    }
                    case "]": {
                        tag = Tag.CLOS_SQR_BR;
                        break;
                    }
                    default: {
                        tag = Tag.CLOS_CRL_BR;
                        break;
                    }
                }
                tokenList.add(new Token(tag));
                stringBuilder = null;
            }
        }
        else throw new TypeNotPresentException("the one, you wrote is", null);

    }
    Tag returnTag(String lexeme){
        Tag tag;
        switch (lexeme.toLowerCase()){
            case "int":{tag = Tag.INT; return tag;}
            case "string":{tag = Tag.STRING; return tag;}
            case "real":{tag = Tag.REAL; return tag;}
            case "bool":{tag = Tag.BOOL; return tag;}
            case "empty":{tag = Tag.EMPTY; return tag;}
            case "func":{tag = Tag.FUNC; return tag;}
            case "true":{tag = Tag.TRUE; return tag;}
            case "false":{tag = Tag.FALSE; return tag;}
            case "and":{tag = Tag.AND; return tag;}
            case "or":{tag = Tag.OR; return tag;}
            case "xor":{tag = Tag.XOR; return tag;}
            case "not":{tag = Tag.NOT; return tag;}
            case "is":{tag = Tag.IS; return tag;}
            case "in":{tag = Tag.IN; return tag;}
            case "readint":{tag = Tag.READ_INT; return tag;}
            case "readreal":{tag = Tag.READ_REAL; return tag;}
            case "readstring":{tag = Tag.READ_STRING; return tag;}
            case "print":{tag = Tag.PRINT; return tag;}
            case "return":{tag = Tag.RETURN; return tag;}
            case "if":{tag = Tag.IF; return tag;}
            case "then":{tag = Tag.THEN; return tag;}
            case "else":{tag = Tag.ELSE; return tag;}
            case "end":{tag = Tag.END; return tag;}
            case "while":{tag = Tag.WHILE; return tag;}
            case "for":{tag = Tag.FOR; return tag;}
            case "loop":{tag = Tag.LOOP; return tag;}
            case "begin":{tag = Tag.BEGIN; return tag;}
            case "var":{tag = Tag.VAR; return tag;}
            case "lambda":{tag = Tag.LAMBDA; return tag;}

        }


        return null;
    }
}

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lexer.*;

class FileParser {
    public List<String> parsed = new LinkedList<String>();

    private int lineNumber = 1;

    private String line = "";

    private boolean isString = false;

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
    }

    public boolean isAppropriate(Character c){
        return appropriateSymbols.contains(c);
    }
    private static BufferedReader ReadFile(String path) throws FileNotFoundException {
        File file = new File(path);
            return new BufferedReader(new FileReader(file));
    }

    public void ParseFile(String path) throws Exception {
        BufferedReader br = ReadFile(path);
        System.out.println(br.read());

        do{
            line = br.readLine();
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
            if(!isString) {
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
                            } else if (peek != ' ' && peek != '\t' && peek != '\n') {//TODO: also check if it's not symbols like /=, =, := and so on
                                if (isAppropriate(peek)) {
                                    tokenList.add(new Word(stringBuilder.toString()));
                                    stringBuilder = null;
                                    break;
                                } else
                                    throw new TypeNotPresentException("the one, you wrote is", null);
                            }
                        } else {
                            tokenList.add(new Word(stringBuilder.toString()));
                            stringBuilder = null;
                            break;
                        }
                    } while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_');
                    if (stringBuilder != null) {
                        tokenList.add(new Word(stringBuilder.toString()));
                        stringBuilder = null;
                    }
                }
                if (isAppropriate(peek)) {
                    if (stringBuilder == null) {
                        stringBuilder = new StringBuilder();
                    } else {
                        tokenList.add(new Word(stringBuilder.toString()));
                        stringBuilder = new StringBuilder();
                    }
                    stringBuilder.append(peek);
                    switch (peek) {
                        case '+':
                        case '*':
                        case '=':
                        case ';':
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                        case '{':
                        case '}':
                        case '-': {
                            if (i + 1 < line.length()) {
                                i += 1;
                                peek = line.charAt(i);
                                checkNext(peek);
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
                                    tokenList.add(new Word(stringBuilder.toString()));
                                    stringBuilder = null;
                                    if (isAppropriate(line.charAt(i + 1))) {
                                        throw new TypeNotPresentException("the one, you wrote is", null);
                                    }
                                } else {
                                    if (Character.isLetter(peek) || Character.isDigit(peek) || peek == ' ' || peek == '\t' || peek == '\n') {
                                        if (peek != ' ' && peek != '\t' && peek != '\n') {
                                            tokenList.add(new Word(stringBuilder.toString()));
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(peek);
                                        } else {
                                            tokenList.add(new Word(stringBuilder.toString()));
                                            stringBuilder = null;
                                        }
                                    } else throw new TypeNotPresentException("the one, you wrote is", null);

                                }
                            }
                            break;
                        }
                    }
                }
            }else{
                stringBuilder.append(peek);
            }
            if(peek == '\"') {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder();
                }
                stringBuilder.append(peek);
                if(isString){
                    stringBuilder.delete(stringBuilder.length()-2,stringBuilder.length()-1);
                    tokenList.add(new StrToken(stringBuilder.toString()));
                    System.out.println(stringBuilder.toString());
                    stringBuilder = null;
                    isString = false;
                }else {
                    isString = true;
                }
            }
        }
    }

    private void checkNext(Character peek){
        if(Character.isLetter(peek) || Character.isDigit(peek) || peek == ' ' || peek == '\t' || peek == '\n') {
            if(peek != ' ' && peek != '\t') {
                tokenList.add(new Word(stringBuilder.toString()));
                stringBuilder = new StringBuilder();
                stringBuilder.append(peek);
            }else{
                tokenList.add(new Word(stringBuilder.toString()));
                stringBuilder = null;
            }
        }
        else throw new TypeNotPresentException("the one, you wrote is", null);

    }
}

package bison.wrappers;

import java.util.ArrayList;

public class Term {
    ArrayList list = new ArrayList();
    public Term(Unary unary){
         list.add(unary);
    }

    public void add(Unary unary, lexer.Token token){
        list.add(unary);
        list.add(token);
    }
}

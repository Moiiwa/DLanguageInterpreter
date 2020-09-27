package bison.wrappers;

import java.util.ArrayList;

public class Term extends Node{
    ArrayList list = new ArrayList();
    public Term(Unary unary){
         list.add(unary);
    }

    public void add(Unary unary, Token token){
        list.add(unary);
        list.add(token);
    }
}

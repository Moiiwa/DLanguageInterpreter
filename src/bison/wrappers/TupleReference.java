package bison.wrappers;

public class TupleReference extends Node{

    Reference reference;
    Token token;

    public TupleReference(Reference reference, Token token){
        this.reference = reference;
        this.token = token;
    }
}

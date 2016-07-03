package org.tamier.symbolic;
import java.util.Objects;

public class Variable {
    enum TYPE {
        VAR, CONSTANT
    }

    // Properties of Variable class
    private TYPE type;
    private int id;
    private Integer constantValue;
    private String variableName;

    public Variable(int id, String variableName) {
        type = TYPE.VAR;
        this.id = id;
        constantValue = null;
        this.variableName = variableName;
    }

    public Variable(int id, int constantValue) {
        type = TYPE.CONSTANT;
        this.id = id;
        this.constantValue = constantValue;
        variableName = null;
    }

    public TYPE getType() {
        return type;
    }

    public Integer getConstantValue() {
        assert type == TYPE.CONSTANT;
        return constantValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public int getId() {
        return id;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Variable)) {
            return false;
        }
        return type == ((Variable) obj).type && Objects.equals(constantValue,
                ((Variable) obj).constantValue) && id == ((Variable) obj).id
                && Objects.equals(variableName, ((Variable) obj).variableName);
    }

    @Override
    public String toString(){
        switch(type){
            case VAR:
                return variableName;
            case CONSTANT:
                return constantValue.toString();
            default:
                return "??";
        }
    }
}

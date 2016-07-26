package org.tamier.symbolic;
import java.util.Objects;

/**
 * Variable is an adjunct class that is used to represent an unknown value. It’s
 * the vocabulary of SymbolicValue. Variable has two types: VARIABLE and
 * CONSTANT. If it’s in CONSTANT state, it represents a concrete integer value.
 * In VARIABLE state, it uses an identifier in upper case, such as "X", "Y" to
 * be a placeholder of the value of parameter x, y.
 *
 * @author tamier
 *
 */
public class Variable {

    // Enum to represent different stae of Variable
    enum TYPE {
        VARIABLE, CONSTANT
    }

    /** Type of this Variable */
    private TYPE type;
    /** Each id is unique to each Variable */
    private int id;
    /**
     * If "this" is in CONSTANT state, constantValue holds the Integer that
     * represents its value. Else, this is in VARIABLE state, and this field is
     * null
     */
    private Integer constantValue;
    /**
     * String representation of this. Needed in solver decoding as a name toeach
     * IntVec. Only Variable in VARIABLE state has non-null variableName.
     */
    private String variableName;

    public Variable(int id, String variableName) {
        type = TYPE.VARIABLE;
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
            case VARIABLE:
                return variableName;
            case CONSTANT:
                return constantValue.toString();
            default:
                return "??";
        }
    }
}

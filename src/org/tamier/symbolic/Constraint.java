package org.tamier.symbolic;
public class Constraint {
    // left is type than right
    public enum TYPE {
        GREATERTHAN, EQUALTO, LESSTHAN, NOTEQUALTO, GREATEROREQUALTO, LESSOREQUALTO;
    }

    TYPE type;
    SymbolicValue left;
    SymbolicValue right;

    public Constraint(SymbolicValue left, TYPE type, SymbolicValue right) {
        this.left = left;
        this.type = type;
        this.right = right;
    }

    @Override
    public int hashCode() {
        return left.hashCode() + right.hashCode() + type.hashCode();
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj == null || !(obj instanceof Constraint)) {
            return false;
        }
        Constraint other = (Constraint) obj;
        // TODO Is this correct?
        return type == other.type && left.equals(other.left) && right.equals(other.right);
    }

    @Override
    public String toString() {
        return left.toString() + " is " + type.toString() + " "
                + right.toString();
    }

    public TYPE getType() {
        return type;
    }

    public SymbolicValue getLeft() {
        return left;
    }

    public SymbolicValue getRight() {
        return right;
    }

    public TYPE getNegateType() {
        TYPE negateType = null;
        switch (type) {
        case GREATERTHAN:
            negateType = TYPE.LESSOREQUALTO;
            break;
        case EQUALTO:
            negateType = TYPE.NOTEQUALTO;
            break;
        case NOTEQUALTO:
            negateType = TYPE.EQUALTO;
            break;
        case LESSTHAN:
            negateType = TYPE.GREATEROREQUALTO;
            break;
        case GREATEROREQUALTO:
            negateType = TYPE.LESSTHAN;
            break;
        case LESSOREQUALTO:
            negateType = TYPE.GREATERTHAN;
            break;
        default:
            System.err.println("Unknown orignal type, abort!");
            System.exit(1);
        }
        return negateType;
    }

    public Constraint negate() {
        return new Constraint(left, getNegateType(), right);
    }

    public String getOperator() {
        String operator = null;
        switch (getType()) {
        case GREATERTHAN:
            operator = ">";
            break;
        case LESSTHAN:
            operator = "<";
            break;
        case EQUALTO:
            operator = "=";
            break;
        case NOTEQUALTO:
            operator = "!=";
            break;
        case GREATEROREQUALTO:
            operator = ">=";
            break;
        case LESSOREQUALTO:
            operator = "<=";
            break;
        default:
            System.err.println("Unknown orignal type, abort!");
            System.exit(1);
        }
        return operator;
    }
}

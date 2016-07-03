package org.tamier.symbolic;
import org.checkerframework.dataflow.analysis.AbstractValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class SymbolicValue implements AbstractValue<SymbolicValue> {

    public enum Type {
        SYMBOLIC, TOP, BOTTOM,
    }

    // Two properties of SymbolicValue
    // enum type
    private Type type;
    // data field
    private Map<Variable, Integer> valueMap;

    public SymbolicValue() {
        valueMap = new HashMap<Variable, Integer>();
        this.type = Type.TOP;
    }

    public SymbolicValue(Type type) {
        valueMap = new HashMap<Variable, Integer>();
        this.type = type;
    }

    public SymbolicValue(Map<Variable, Integer> outSourceMap) {
        valueMap = outSourceMap;
        this.type = Type.SYMBOLIC;
    }

    public boolean isTop() {
        return type.equals(Type.TOP);
    }

    public boolean isSymbolic() {
        return type.equals(Type.SYMBOLIC);
    }

    public boolean isBottom() {
        return type.equals(Type.BOTTOM);
    }

    public Type getType() {
        return type;
    }

    public Map<Variable, Integer> getValueMap() {
        assert isSymbolic();
        return valueMap;
    }

    public void setValueMap(Map<Variable, Integer> mapToSet) {
        assert isSymbolic();
        valueMap = mapToSet;
    }

    public SymbolicValue add(SymbolicValue increment) {
        assert this.isSymbolic() && increment.isSymbolic();
        for (Variable v : increment.getValueMap().keySet()) {
            if (valueMap.containsKey(v)) {
                Integer newFrequence = valueMap.get(v)
                        + increment.valueMap.get(v);
                valueMap.put(v, newFrequence);
            } else {
                valueMap.put(v, increment.valueMap.get(v));
            }
        }
        return this;
    }

    public static SymbolicValue addTwoSymbolicValue(SymbolicValue leftOp, SymbolicValue rightOp){
        assert leftOp.isSymbolic() && rightOp.isSymbolic();
        Map<Variable, Integer> temporaryMap = new HashMap<Variable, Integer>();
        for(Variable vr : rightOp.getValueMap().keySet()){
            if(leftOp.valueMap.containsKey(vr)){
                Integer newFrequence = leftOp.valueMap.get(vr) + rightOp.valueMap.get(vr);
                temporaryMap.put(vr, newFrequence);
            } else{
                temporaryMap.put(vr, rightOp.valueMap.get(vr));
            }
        }
        for(Variable vl : leftOp.getValueMap().keySet()){
            if(rightOp.valueMap.containsKey(vl)){
                
            } else{
                temporaryMap.put(vl, leftOp.valueMap.get(vl));
            }
        }
        return new SymbolicValue(temporaryMap);
    }

    public static SymbolicValue subtractTwoSymbolicValue(SymbolicValue leftOp, SymbolicValue rightOp){
        assert leftOp.isSymbolic() && rightOp.isSymbolic();
        Map<Variable, Integer> temporaryMap = new HashMap<Variable, Integer>();
        for(Variable vr : rightOp.getValueMap().keySet()){
            if(leftOp.valueMap.containsKey(vr)){
                Integer newFrequence = leftOp.valueMap.get(vr) - rightOp.valueMap.get(vr);
                temporaryMap.put(vr, newFrequence);
            } else{
                // left hand side doesn't conttain this variable, so the subtraction result is < 0 
                temporaryMap.put(vr, -rightOp.valueMap.get(vr));
            }
        }
        for(Variable vl : leftOp.getValueMap().keySet()){
            if(rightOp.valueMap.containsKey(vl)){
                
            } else{
                temporaryMap.put(vl, leftOp.valueMap.get(vl));
            }
        }
        return new SymbolicValue(temporaryMap);
    }
    
    @Override
    public SymbolicValue leastUpperBound(SymbolicValue other) {
        if (other.isBottom()) {
            return this.copy();
        }
        if (this.isBottom()) {
            return other.copy();
        }
        if (other.isTop() || this.isTop()) {
            return new SymbolicValue(Type.TOP);
        }
        if (other.getValueMap().equals(getValueMap())) {
            return this.copy();
        }
        return new SymbolicValue(Type.TOP);
    }

    public SymbolicValue copy() {
        if (isSymbolic()) {
            return new SymbolicValue(new HashMap<Variable, Integer>(valueMap));
        }
        // TODO Is there enum object?
        Type type = this.type;
        return new SymbolicValue(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SymbolicValue)) {
            return false;
        }
        SymbolicValue other = (SymbolicValue) obj;
        // TODO Is this correct?
        return type == other.type && Objects.equals(valueMap, other.valueMap);
    }

    @Override
    public int hashCode() {
        int hash;
        return ((hash = (127 * type.hashCode() + valueMap.hashCode())
                % 16908799) >= 0) ? hash : hash + 16908799;
    }

    @Override
    public String toString() {
        switch (type) {
        case TOP:
            return "T";
        case BOTTOM:
            return "B";
        case SYMBOLIC:
            StringBuilder sb = new StringBuilder();
            for (Entry<Variable, Integer> e : valueMap.entrySet()) {
                if (e.getKey().getType() == Variable.TYPE.CONSTANT) {
                    sb.append("(" + String.valueOf(
                            e.getValue() * e.getKey().getConstantValue())
                            + ")+");
                } else {
                    sb.append(
                            "(" + e.getValue() + ")" + "*" + e.getKey() + "+");
                }

            }
            return sb.substring(0, sb.length() - 1).toString();
        }
        assert false;
        return "???";
    }

    public int size() {
        return valueMap.size();
    }
}

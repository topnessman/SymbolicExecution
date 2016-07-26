package org.tamier.symbolic;
import org.checkerframework.dataflow.analysis.AbstractValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * AbstractValue of symbolic execution. It has three types: SYMBOLIC, TOP,
 * BOTTOM. Only SymbolicValue in SYMBOLIC state represents a meaningful symbolic
 * state - an unknown yet trackable value of a parameter. SymbolicValue is a
 * linear combination of Variables. See also <link>Variable</link> for more
 * information.
 *
 * @author tamier
 *
 */
public class SymbolicValue implements AbstractValue<SymbolicValue> {

    // Enum to represent different states of SymbolicValue
    public enum Type {
        SYMBOLIC, TOP, BOTTOM,
    }

    /**
     * Indicates this SymbolicValue's state. Only SYMBOLIC state has non-empty
     * valueMap
     */
    private Type type;
    /** Mapping of <link>Variable</link> to its coefficient */
    private Map<Variable, Integer> valueMap;

    public SymbolicValue() {
        valueMap = new HashMap<Variable, Integer>();
        // Default type if TOP, representing any value
        this.type = Type.TOP;
    }

    public SymbolicValue(Type type) {
        valueMap = new HashMap<Variable, Integer>();
        this.type = type;
    }

    /**
     * Create a SymbolicValue holding an existing valueMap
     *
     * @param outSourceMap
     *            existing valueMap that represents a symbolic value
     */
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

    /**
     * Getter method to get the valueMap of this SymbolicValue. Only when this
     * SymbolicValue is in SYMBOLIC state, it has an effective valueMap that
     * represents a symbolic value.
     *
     * @return valueMap that effectively represents a symbolic value
     */
    public Map<Variable, Integer> getValueMap() {
        assert isSymbolic();
        return valueMap;
    }

    /**
     * Setter method to update valueMap of this
     *
     * @param mapToSet
     *            new valueMap to be set from
     */
    public void setValueMap(Map<Variable, Integer> mapToSet) {
        assert isSymbolic();
        valueMap = mapToSet;
    }

    /**
     * Add another SymbolicValue to this
     *
     * @param increment
     *            SymbolicValue to be added to this
     * @return this object after adding up increment
     */
    public SymbolicValue add(SymbolicValue increment) {
        assert this.isSymbolic() && increment.isSymbolic();
        for (Variable v : increment.getValueMap().keySet()) {
            if (valueMap.containsKey(v)) {
                // Add up the two coefficients and put it back to valueMap of
                // this
                Integer newFrequence = valueMap.get(v)
                        + increment.valueMap.get(v);
                valueMap.put(v, newFrequence);
            } else {
                // this doesn't contain Variable v, create a new Entry in
                // valueMap with the same coefficient as increment
                valueMap.put(v, increment.valueMap.get(v));
            }
        }
        return this;
    }

    /**
     * Utility method to add up two SymbolicValues
     * @param leftOp operand1
     * @param rightOp operand2
     * @return Sum of leftOp and rightOp
     */
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

    /**
     * Utility method to subtract rightOp from leftOp
     * @param leftOp SymbolicValue to be subtracted from
     * @param rightOp SymbolicValue to substract
     * @return Result of leftOp - rightOp
     */
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
    
    /**
     * Takes the least upper bound of two SymbolicValue
     */
    @Override
    public SymbolicValue leastUpperBound(SymbolicValue other) {
        // If either one is BOTTOM, return the other one
        if (other.isBottom()) {
            return this.copy();
        }
        if (this.isBottom()) {
            return other.copy();
        }
        // If either of the two is TOP, after merging, SymbolicValue is any.
        if (other.isTop() || this.isTop()) {
            return new SymbolicValue(Type.TOP);
        }
        // Only if other has the same SymbolicValue as this, taking the least
        // upper bound reserves
        if (other.getValueMap().equals(getValueMap())) {
            return this.copy();
        }
        // In all the other cases, return TOP
        return new SymbolicValue(Type.TOP);
    }

    public SymbolicValue copy() {
        if (isSymbolic()) {
            // If in SYMBOLIC state, create a new SymbolicValue with the exactly
            // same valueMap
            return new SymbolicValue(new HashMap<Variable, Integer>(valueMap));
        }
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
        // Dead code
        return "???";
    }

    /**
     * Returns the size of this valueMap
     *
     * @return size of this valueMap
     */
    public int size() {
        return valueMap.size();
    }
}

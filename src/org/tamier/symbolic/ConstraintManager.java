package org.tamier.symbolic;
import java.util.HashSet;
import java.util.Set;

public class ConstraintManager {

    Set<Constraint> constraintSet;
    
    public ConstraintManager(){
        constraintSet = new HashSet<Constraint>();
    }
    
    public Constraint addConstraint(SymbolicValue left, Constraint.TYPE type, SymbolicValue right){
        Constraint newConstraint = new Constraint(left, type, right);
        constraintSet.add(newConstraint);
        return newConstraint;
    }

    public Set<Constraint> getConstraints() {
        return constraintSet;
    }
    
    public Set<Set<Constraint>> getPermutatedSets(){
        Set<Set<Constraint>> sets = new HashSet<Set<Constraint>>();
        sets.add(constraintSet);
        Set<Constraint> allNegateSet = new HashSet<Constraint>();
        for (Constraint c : constraintSet) {
            allNegateSet.add(c.negate());
        }
        sets.add(allNegateSet);
        for(Constraint c: constraintSet){
            Set<Constraint> copyConstraintSet = new HashSet<Constraint>(constraintSet);
            copyConstraintSet.remove(c);
            copyConstraintSet.add(c.negate());
            sets.add(copyConstraintSet);
        }
        return sets;
    }
}

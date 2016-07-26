package org.tamier.symbolic;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates and manages Constraints.
 *
 * @author tamier
 *
 */
public class ConstraintManager {

    /**
     * Set of Constrains stored.
     */
    Set<Constraint> constraintSet;
    
    public ConstraintManager(){
        constraintSet = new HashSet<Constraint>();
    }

    /**
     * API of adding new Constraint
     *
     * @param left
     *            SymbolicValue of left Node
     * @param type
     *            relationship between SymbolicValues of left and right Node
     * @param right
     *            SymbolicValue of right Node
     * @return Constraint generated
     */
    public Constraint addConstraint(SymbolicValue left, Constraint.TYPE type, SymbolicValue right){
        Constraint newConstraint = new Constraint(left, type, right);
        constraintSet.add(newConstraint);
        return newConstraint;
    }

    /**
     * Get all the Constraints
     *
     * @return set of all the Constraints
     */
    public Set<Constraint> getConstraints() {
        return constraintSet;
    }

    /**
     * Return the permuted result of set of Constraints. Example: <C1,C2>
     * returns <C1,C2>,<!C1,C2>,<C1,!C2>,<!C1,!C2> PS. C1,C2 represent
     * Constraint
     *
     * @return permuted result of Constraint
     */
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

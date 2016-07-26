package org.tamier.symbolic;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.jacop.constraints.LinearInt;
import org.jacop.constraints.PrimitiveConstraint;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.DepthFirstSearch;
import org.jacop.search.IndomainMin;
import org.jacop.search.InputOrderSelect;
import org.jacop.search.Search;
import org.jacop.search.SelectChoicePoint;

import test.TestLauncher;

/**
 * SymbolicSolver is to receive Constraint set, encode it to Jacop solver, solve
 * and get solution, then automatically launch the testcase using the solution
 *
 * @author tamier
 *
 */
public class SymbolicSolver {

    /**
     * Map of string that represents the name of Variable in VARIABLE state to
     * IntVar the represent it in solver
     */
    HashMap<String, IntVar> map = new HashMap<>();

    // Solve set of set of Constraints
    public void solvePermutatedConstraintSet(
            Set<Set<Constraint>> permutatedConstraintSet) {
        for (Set<Constraint> constraintSet : permutatedConstraintSet) {
            solve(constraintSet);
        }
    }

    /**
     * Using a set of Constraints, encode them to solver, gets the solutioin,
     * and lauches test
     *
     * @param constraintSet
     *            set of Constraints to be solved.
     */
    public void solve(Set<Constraint> constraintSet) {
        System.out.println("-----Solving----");
        /*System.out.println("These are the constraints generated:");
        for (Constraint c : constraintSet) {
            System.out.println(c);
        }*/
        // Standard steps of calling Jacop solver
        Store store = new Store();
        for (Constraint c : constraintSet) {
            encodeAndPost(c, store);
        }
        // Map has content and not empty now!
        IntVar[] vars = new IntVar[map.size()];
        int i = 0;
        for (Entry<String, IntVar> e : map.entrySet()) {
            vars[i] = e.getValue();
            i++;
        }

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(store,
                vars, new IndomainMin<IntVar>());
        boolean result = search.labeling(store, select);
        if (result) {
            System.out.println("Solution: ");
            for (int j = 0; j < vars.length; j++) {
                System.out.println(vars[j]);
            }
            if (vars.length == 2) {
                TestLauncher.launchBinary(vars[0].value(), vars[1].value());
            }
            if (vars.length == 3) {
                TestLauncher.launchTernary(vars[0].value(), vars[1].value(),
                        vars[2].value());
            }
        } else {
            System.out.println("These contraints are unsatisfiable!");
        }
        map.clear();
        System.out.println("-----Solver ends----");
    }

    /**
     * Encode the Constraint and post it to Store of Jacop solver
     *
     * @param c
     *            Constraint that needs to be encoded and posted
     * @param store
     *            Jacop Store that stores the constraints among IntVar
     */
    private void encodeAndPost(Constraint c, Store store) {
    
        SymbolicValue refined = SymbolicValue
                .subtractTwoSymbolicValue(c.getLeft(), c.getRight());
    
        IntVar[] vars = new IntVar[refined.size()];
        int[] coeffs = new int[refined.size()];
        int i = 0;
        for (Entry<Variable, Integer> e : refined.getValueMap().entrySet()) {
            Variable key = e.getKey();
            if (key.getType() == Variable.TYPE.VARIABLE) {
                vars[i] = getOrCreateIntVar(key.getVariableName(), store);
            } else {
                vars[i] = new IntVar(store, String.valueOf(key.getConstantValue()), key.getConstantValue(),
                        key.getConstantValue());
            }
            coeffs[i] = e.getValue();
            i++;
        }

        PrimitiveConstraint ctr = new LinearInt(store, vars, coeffs, c.getOperator(), 0);
        store.impose(ctr);
    }

    /**
     * If the Variable in VARIABLE state with value of parameter "name", get its
     * IntVar from cache. Otherwise, create a new IntVar that represents this
     * Variable in VARIABLE state
     */
    IntVar getOrCreateIntVar(String name, Store store) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        // -100, 100 represent the lower and upper bound of solution to this
        // IntVar
        IntVar returnIntVar = new IntVar(store, name, -100, 100);
        map.put(name, returnIntVar);
        return returnIntVar;
    
    }

}

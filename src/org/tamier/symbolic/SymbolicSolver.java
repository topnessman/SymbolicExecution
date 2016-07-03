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
public class SymbolicSolver {

    HashMap<String, IntVar> map = new HashMap<>();

    public void solvePermutatedConstraintSet(
            Set<Set<Constraint>> permutatedConstraintSet) {
        for (Set<Constraint> constraintSet : permutatedConstraintSet) {
            solve(constraintSet);
        }
    }

    public void solve(Set<Constraint> constraintSet) {
        System.out.println("-----Solving----");
        /*System.out.println("These are the constraints generated:");
        for (Constraint c : constraintSet) {
            System.out.println(c);
        }*/
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


    private void encodeAndPost(Constraint c, Store store) {
    
        SymbolicValue refined = SymbolicValue
                .subtractTwoSymbolicValue(c.getLeft(), c.getRight());
    
        IntVar[] vars = new IntVar[refined.size()];
        int[] coeffs = new int[refined.size()];
        int i = 0;
        for (Entry<Variable, Integer> e : refined.getValueMap().entrySet()) {
            Variable key = e.getKey();
            if (key.getType() == Variable.TYPE.VAR) {
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

    IntVar getOrCreateIntVar(String name, Store store) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        IntVar returnIntVar = new IntVar(store, name, -100, 100);
        map.put(name, returnIntVar);
        return returnIntVar;
    
    }

}

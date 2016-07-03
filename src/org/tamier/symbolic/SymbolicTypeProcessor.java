package org.tamier.symbolic;
import org.checkerframework.dataflow.cfg.DataflowTypeProcessor;

import java.util.Set;

public class SymbolicTypeProcessor extends
        DataflowTypeProcessor<SymbolicValue, SymbolicStore, SymbolicTransfer> {

    SymbolicTransfer symbolicTransfer = new SymbolicTransfer();
    @Override
    protected SymbolicTransfer getTransfer() {
        return symbolicTransfer;
    }

    @Override
    public void typeProcessingOver() {
        super.typeProcessingOver();
        Set<Set<Constraint>> permutatedConstraintSet = symbolicTransfer
                .getConstrainManager().getPermutatedSets();
        SymbolicSolver solver = new SymbolicSolver();
        solver.solvePermutatedConstraintSet(permutatedConstraintSet);

    }

}

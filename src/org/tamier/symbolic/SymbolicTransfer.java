package org.tamier.symbolic;
import org.checkerframework.dataflow.analysis.ConditionalTransferResult;
import org.checkerframework.dataflow.analysis.RegularTransferResult;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.UnderlyingAST;
import org.checkerframework.dataflow.cfg.node.AbstractNodeVisitor;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.EqualToNode;
import org.checkerframework.dataflow.cfg.node.GreaterThanNode;
import org.checkerframework.dataflow.cfg.node.GreaterThanOrEqualNode;
import org.checkerframework.dataflow.cfg.node.IntegerLiteralNode;
import org.checkerframework.dataflow.cfg.node.LessThanNode;
import org.checkerframework.dataflow.cfg.node.LessThanOrEqualNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.cfg.node.NotEqualNode;
import org.checkerframework.dataflow.cfg.node.NumericalAdditionNode;
import org.checkerframework.dataflow.cfg.node.NumericalSubtractionNode;

import java.util.List;

/**
 * SymbolicTransfer function implementing transfer function interface performs
 * different operationis on different kinds of Nodes. Two tasks of
 * SymbolicTransfer: 1. Update store with new SymbolicValue at AssignmentNode 2.
 * Generate Constraints at each conditional branch - visitGreaterThan,
 * visitLessThan etc.
 *
 * @author tamier
 *
 */
public class SymbolicTransfer extends
        AbstractNodeVisitor<TransferResult<SymbolicValue, SymbolicStore>, TransferInput<SymbolicValue, SymbolicStore>>
        implements TransferFunction<SymbolicValue, SymbolicStore> {

    /**
     * ConstraintManger to store all of the Constraints generated
     */
    ConstraintManager constraintManager = new ConstraintManager();
    /**
     * VariableManager to instantiate SymbolicStore
     */
    VariableManager variableManager = new VariableManager();

    ConstraintManager getConstrainManager() {
        return constraintManager;
    }

    VariableManager getVariableManager() {
        return variableManager;
    }

    @Override
    public SymbolicStore initialStore(UnderlyingAST underlyingAST,
            List<LocalVariableNode> parameters) {
        SymbolicStore initStore = new SymbolicStore(variableManager);
        return initStore;
    }

    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitNode(Node n,
            TransferInput<SymbolicValue, SymbolicStore> p) {
        // TODO Auto-generated method stub
        return new RegularTransferResult<SymbolicValue, SymbolicStore>(null, p.getRegularStore());
    }
    
    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitLocalVariable(
        LocalVariableNode node, TransferInput<SymbolicValue, SymbolicStore> before) {
        SymbolicStore store = before.getRegularStore();
        // Get or create a SymbolicValue for this node.
        SymbolicValue symValue = store.getOrCreateSymbolicValue(node);
        return new RegularTransferResult<SymbolicValue, SymbolicStore>(symValue, store);
    }
    
    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitIntegerLiteral(
            IntegerLiteralNode n,
            TransferInput<SymbolicValue, SymbolicStore> p) {
        SymbolicStore store = p.getRegularStore();
        // Get or create a SymbolicValue for this node.
        SymbolicValue symValue = store.getOrCreateSymbolicValue(n);
        return new RegularTransferResult<SymbolicValue, SymbolicStore>(symValue, store);
    }

    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitAssignment(
            AssignmentNode n, TransferInput<SymbolicValue, SymbolicStore> p) {
        SymbolicStore store = p.getRegularStore();
        Node target = n.getTarget();
        Node rhs = n.getExpression();
        SymbolicValue result = null;
        // Only update the SymbolicValue of LocalVariableNode
        if (target instanceof LocalVariableNode){
            LocalVariableNode t = (LocalVariableNode) target;
            // Only supports addition, subtraction operation of two Nodes, and
            // direct assignment from a rhs Node to lhs Node
            if(rhs instanceof NumericalAdditionNode){
                NumericalAdditionNode r = (NumericalAdditionNode) rhs;
                Node leftOp = r.getLeftOperand();
                Node rightOp = r.getRightOperand();
                SymbolicValue leftValue = store.getOrCreateSymbolicValue(leftOp);
                SymbolicValue rightValue = store.getOrCreateSymbolicValue(rightOp);
                result = SymbolicValue.addTwoSymbolicValue(leftValue, rightValue);
                store.updateSymbolicValue(t, result);
            } else if (rhs instanceof NumericalSubtractionNode) {
                NumericalSubtractionNode r = (NumericalSubtractionNode) rhs;
                Node leftOp = r.getLeftOperand();
                Node rightOp = r.getRightOperand();
                SymbolicValue leftValue = store.getOrCreateSymbolicValue(leftOp);
                SymbolicValue rightValue = store.getOrCreateSymbolicValue(rightOp);
                result = SymbolicValue.subtractTwoSymbolicValue(leftValue, rightValue);
                store.updateSymbolicValue(t, result);
            } else {
                result = store.getOrCreateSymbolicValue(rhs);
                store.updateSymbolicValue(t, result);
            }
        } 
        return new RegularTransferResult<>(result, store);
    }

    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitGreaterThan(
            GreaterThanNode n, TransferInput<SymbolicValue, SymbolicStore> p) {
        // Don't quite propagate any value, just collect program
        // constraints
        SymbolicStore thenStore = p.getRegularStore();
        SymbolicStore elseStore = p.getRegularStore();
        SymbolicValue left = thenStore.getOrCreateSymbolicValue(n.getLeftOperand());
        SymbolicValue right = thenStore.getOrCreateSymbolicValue(n.getRightOperand());
        // Add GREATERTHAN constraint to ConstraintManager.
        constraintManager.addConstraint(left, Constraint.TYPE.GREATERTHAN, right);
        return new ConditionalTransferResult<>(null, thenStore, elseStore);
    }
    
    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitGreaterThanOrEqual(
            GreaterThanOrEqualNode n,
            TransferInput<SymbolicValue, SymbolicStore> p) {
        SymbolicStore thenStore = p.getRegularStore();
        SymbolicStore elseStore = p.getRegularStore();
        SymbolicValue left = thenStore.getOrCreateSymbolicValue(n.getLeftOperand());
        SymbolicValue right = thenStore.getOrCreateSymbolicValue(n.getRightOperand());
        constraintManager.addConstraint(left, Constraint.TYPE.GREATEROREQUALTO, right);
        return new ConditionalTransferResult<>(null, thenStore, elseStore);
    }
    
    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitLessThan(
            LessThanNode n, TransferInput<SymbolicValue, SymbolicStore> p) {
        SymbolicStore thenStore = p.getRegularStore();
        SymbolicStore elseStore = p.getRegularStore();
        SymbolicValue left = thenStore.getOrCreateSymbolicValue(n.getLeftOperand());
        SymbolicValue right = thenStore.getOrCreateSymbolicValue(n.getRightOperand());
        constraintManager.addConstraint(left, Constraint.TYPE.LESSTHAN, right);
        return new ConditionalTransferResult<>(null, thenStore, elseStore);
    }

    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitLessThanOrEqual(
            LessThanOrEqualNode n,
            TransferInput<SymbolicValue, SymbolicStore> p) {
        SymbolicStore thenStore = p.getRegularStore();
        SymbolicStore elseStore = p.getRegularStore();
        SymbolicValue left = thenStore.getOrCreateSymbolicValue(n.getLeftOperand());
        SymbolicValue right = thenStore.getOrCreateSymbolicValue(n.getRightOperand());
        constraintManager.addConstraint(left, Constraint.TYPE.LESSOREQUALTO, right);
        return new ConditionalTransferResult<>(null, thenStore, elseStore);
    }

    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitEqualTo(
            EqualToNode n, TransferInput<SymbolicValue, SymbolicStore> p) {
        // We don't actually update store. Because we have the contract that
        // Node's SymbolicValues are not updated in any branch. If we update the
        // store with the value of "==" branch, which is different from the
        // original SymbolicValue, then after the successor performs the
        // least upper bound operation, symbolic value becomes top, which is not
        // expected
        SymbolicStore thenStore = p.getRegularStore();
        SymbolicStore elseStore = p.getRegularStore();
        SymbolicValue left = thenStore.getOrCreateSymbolicValue(n.getLeftOperand());
        SymbolicValue right = thenStore.getOrCreateSymbolicValue(n.getRightOperand());
        constraintManager.addConstraint(left, Constraint.TYPE.EQUALTO, right);
        return new ConditionalTransferResult<>(null, thenStore, elseStore);
    }

    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitNotEqual(
            NotEqualNode n, TransferInput<SymbolicValue, SymbolicStore> p) {
        // We don't actually update store.
        SymbolicStore thenStore = p.getRegularStore();
        SymbolicStore elseStore = p.getRegularStore();
        SymbolicValue left = thenStore.getOrCreateSymbolicValue(n.getLeftOperand());
        SymbolicValue right = thenStore.getOrCreateSymbolicValue(n.getRightOperand());
        constraintManager.addConstraint(left, Constraint.TYPE.NOTEQUALTO, right);
        return new ConditionalTransferResult<>(null, thenStore, elseStore);
    }


}

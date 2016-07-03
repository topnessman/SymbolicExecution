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

public class SymbolicTransfer extends
        AbstractNodeVisitor<TransferResult<SymbolicValue, SymbolicStore>, TransferInput<SymbolicValue, SymbolicStore>>
        implements TransferFunction<SymbolicValue, SymbolicStore> {

    ConstraintManager constraintManager = new ConstraintManager();
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
        SymbolicValue symValue = store.getOrCreateSymbolicValue(node);
        return new RegularTransferResult<SymbolicValue, SymbolicStore>(symValue, store);
    }
    
    @Override
    public TransferResult<SymbolicValue, SymbolicStore> visitIntegerLiteral(
            IntegerLiteralNode n,
            TransferInput<SymbolicValue, SymbolicStore> p) {
        SymbolicStore store = p.getRegularStore();
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
        if (target instanceof LocalVariableNode){
            LocalVariableNode t = (LocalVariableNode) target;
            if(rhs instanceof NumericalAdditionNode){
                NumericalAdditionNode r = (NumericalAdditionNode) rhs;
                Node leftOp = r.getLeftOperand();
                Node rightOp = r.getRightOperand();
                // TODO visitLocalVariable should have put the SymbolicValue of this node already? Need to clarify
                SymbolicValue leftValue = store.getOrCreateSymbolicValue(leftOp);
                SymbolicValue rightValue = store.getOrCreateSymbolicValue(rightOp);
                result = SymbolicValue.addTwoSymbolicValue(leftValue, rightValue);
                store.updateSymbolicValue(t, result);
            } else if (rhs instanceof NumericalSubtractionNode) {
                NumericalSubtractionNode r = (NumericalSubtractionNode) rhs;
                Node leftOp = r.getLeftOperand();
                Node rightOp = r.getRightOperand();
                // TODO visitLocalVariable should have put the SymbolicValue of this node already? Need to clarify
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
        // TODO don't quite propagate any value, just collect program
        // constraints
        SymbolicStore thenStore = p.getRegularStore();
        SymbolicStore elseStore = p.getRegularStore();
        SymbolicValue left = thenStore.getOrCreateSymbolicValue(n.getLeftOperand());
        SymbolicValue right = thenStore.getOrCreateSymbolicValue(n.getRightOperand());
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
        // We don't actually update store. Because if the successor performs the
        // least upper bound operation, then it's symbolic value becomes top
        // Second reason is: the original input value doesn't change just
        // because of the if equal comparison. It should keep the original value
        // before if equal, provided we don't assign a new value to it in the
        // successors of conditional block.
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

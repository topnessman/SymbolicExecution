package org.tamier.symbolic;
import org.checkerframework.dataflow.analysis.FlowExpressions.Receiver;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.cfg.CFGVisualizer;
import org.checkerframework.dataflow.cfg.node.IntegerLiteralNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SymbolicStore implements Store<SymbolicStore> {
    VariableManager variableManager;
    Map<Node, SymbolicValue> nodeToSymbolicMapping;
    
    public SymbolicStore(VariableManager variableManager) {
        this.nodeToSymbolicMapping = new HashMap<Node, SymbolicValue>();
        this.variableManager = variableManager;
    }
    
    public SymbolicStore(VariableManager variableManager,
            HashMap<Node, SymbolicValue> outSourceMapping) {
        this.nodeToSymbolicMapping = outSourceMapping;
        this.variableManager = variableManager;
    }
    
    public SymbolicValue getOrCreateSymbolicValue(Node n) {
        if(!nodeToSymbolicMapping.containsKey(n)){
            Map<Variable, Integer> outSourceMap = new HashMap<>();
            if(n instanceof LocalVariableNode){
                outSourceMap.put(variableManager.createVariable(((LocalVariableNode)n).getName().toUpperCase()), 1);
            } else if (n instanceof IntegerLiteralNode){
                outSourceMap.put(variableManager.createConstant(((IntegerLiteralNode)n).getValue()), 1);
            }
            SymbolicValue symValue = new SymbolicValue(outSourceMap);
            nodeToSymbolicMapping.put(n, symValue);
            return symValue;
        }
        return nodeToSymbolicMapping.get(n);
    }

    public void updateSymbolicValue(Node n, SymbolicValue newSymValue) {
        // TODO Is it correct to store LocalVariableNode only?
        assert (n instanceof LocalVariableNode) || (n instanceof IntegerLiteralNode);
        if (nodeToSymbolicMapping.containsKey(n)) {
            nodeToSymbolicMapping.put(n, newSymValue);
        } else{
            nodeToSymbolicMapping.put(n, newSymValue);
        }
        
    }
    @Override
    public SymbolicStore copy() {
        return new SymbolicStore(variableManager.copy(),
                new HashMap<>(nodeToSymbolicMapping));
    }

    @Override
    public SymbolicStore leastUpperBound(SymbolicStore other) {
        Map<Node, SymbolicValue> newNodeToSymbolicMapping = new HashMap<Node, SymbolicValue>();

        // go through all of the information of the other class
        for (Entry<Node, SymbolicValue> e : other.nodeToSymbolicMapping.entrySet()) {
            Node n = e.getKey();
            SymbolicValue otherVal = e.getValue();
            if (nodeToSymbolicMapping.containsKey(n)) {
                // merge if both contain information about a variable
                newNodeToSymbolicMapping.put(n, otherVal.leastUpperBound(nodeToSymbolicMapping.get(n)));
            } else {
                // add new information
                newNodeToSymbolicMapping.put(n, otherVal);
            }
        }

        for (Entry<Node, SymbolicValue> e : nodeToSymbolicMapping.entrySet()) {
            Node n = e.getKey();
            SymbolicValue thisVal = e.getValue();
            if (!other.nodeToSymbolicMapping.containsKey(n)) {
                // add new information
                newNodeToSymbolicMapping.put(n, thisVal);
            }
        }

        return new SymbolicStore(variableManager.copy(),
                (HashMap<Node, SymbolicValue>) newNodeToSymbolicMapping);
    }

    @Override
    public boolean canAlias(Receiver a, Receiver b) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void visualize(CFGVisualizer<?, SymbolicStore, ?> viz) {
        // TODO Auto-generated method stub

    }

}

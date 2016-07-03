package org.tamier.symbolic;
import java.util.HashSet;
import java.util.Set;

public class VariableManager {
    int id;
    Set<Variable> storedVariables;
    Set<Variable> storedConstants;
    public VariableManager() {
        id = 0;
        storedVariables = new HashSet<Variable>();
        storedConstants = new HashSet<Variable>();
    }

    public VariableManager(int id, Set<Variable> storedVariables,
            Set<Variable> storedConstants) {
        this.id = id;
        this.storedVariables = storedVariables;
        this.storedConstants = storedConstants;
    }

    Variable createVariable(String name) {
        Variable var = new Variable(getNextId(), name);
        storedVariables.add(var);
        return var;
    }

    Variable createConstant(int constantValue) {
        Variable cons = new Variable(getNextId(), constantValue);
        storedConstants.add(cons);
        return cons;
    }

    Set<Variable> getVariables() {
        return storedVariables;
    }

    Set<Variable> getConstants() {
        return storedConstants;
    }
    private int getNextId() {
        return id++;
    }

    public VariableManager copy() {
        return new VariableManager(this.id, this.storedVariables,
                this.storedConstants);
    }

}

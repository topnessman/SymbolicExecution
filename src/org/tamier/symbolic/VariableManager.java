package org.tamier.symbolic;
import java.util.HashSet;
import java.util.Set;

/**
 * Manager class to create and manage Variables. It provides APIs like
 * createVariable() to create Variable in VARIABLE state; createConstant() to
 * create Variable in CONSTANT state(with concrete integer value). Each
 * Variable’s id monotonically increases with the generation of Variables.
 * Variables in VARIABLE state and Variables in CONSTANT state are stored in
 * different sets, so that they can be fetched separately.
 *
 * @author tamier
 *
 */
public class VariableManager {
    /**
     * Used to uniquely identify a Variable
     */
    int id;
    /**
     * Set of all Variables in VARIABLE state
     */
    Set<Variable> storedVariables;
    /**
     * Set of all Variables in CONSTANT state
     */
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

    /**
     * Create a Variable in VARIABLE state with its name
     *
     * @param name
     *            Variable's name
     * @return Variable created
     */
    Variable createVariable(String name) {
        Variable var = new Variable(getNextId(), name);
        storedVariables.add(var);
        return var;
    }

    /**
     * Create a Variable in CONSTANT state
     *
     * @param constantValue
     *            integer value that this Vaiable represents
     * @return Variable created(in CONSTANT state)
     */
    Variable createConstant(int constantValue) {
        Variable cons = new Variable(getNextId(), constantValue);
        storedConstants.add(cons);
        return cons;
    }

    /**
     * Get the set of all Variables in VARIABLE state
     *
     * @return set of all Variables in VARIABLE state
     */
    Set<Variable> getVariables() {
        return storedVariables;
    }

    /**
     * Get the set of all Variables in CONSTANT state
     *
     * @return set of all Variables in CONSTANT state
     */
    Set<Variable> getConstants() {
        return storedConstants;
    }

    /**
     * Returns the next available id
     *
     * @return next available id
     */
    private int getNextId() {
        return id++;
    }

    public VariableManager copy() {
        return new VariableManager(this.id, this.storedVariables,
                this.storedConstants);
    }

}

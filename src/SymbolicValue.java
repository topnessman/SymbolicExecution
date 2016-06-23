import org.checkerframework.dataflow.analysis.AbstractValue;

import java.util.Objects;

public class SymbolicValue implements AbstractValue<SymbolicValue> {

    // enum type
    protected Type type;

    // id
    protected Integer id;

    public enum Type {
        SYMBOLIC, TOP, BOTTOM,
    }

    public SymbolicValue(Type type) {
        assert !type.equals(Type.SYMBOLIC);
        this.type = type;
    }

    private SymbolicValue(Integer id) {
        this.type = Type.SYMBOLIC;
        this.id = id;
    }

    public boolean isTop() {
        return type.equals(Type.TOP);
    }

    public boolean isBottom() {
        return type.equals(Type.BOTTOM);
    }

    public boolean isSymbolic() {
        return type.equals(Type.SYMBOLIC);
    }

    public Integer getId() {
        assert isSymbolic();
        return id;
    }

    @Override
    public SymbolicValue leastUpperBound(SymbolicValue other) {
        if (other.isBottom()) {
            return this.copy();
        }
        if (this.isBottom()) {
            return other.copy();
        }
        if (other.isTop() || this.isTop()) {
            return new SymbolicValue(Type.TOP);
        }
        if (other.getId().equals(getId())) {
            return this.copy();
        }
        return new SymbolicValue(Type.TOP);
    }

    public SymbolicValue copy() {
        if (isSymbolic()) {
            return new SymbolicValue(id);
        }
        return new SymbolicValue(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SymbolicValue)) {
            return false;
        }
        SymbolicValue other = (SymbolicValue) obj;
        return type == other.type && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return type.hashCode() + (id != null ? id.hashCode() : 0);
    }

    @Override
    public String toString() {
        switch (type) {
        case TOP:
            return "T";
        case BOTTOM:
            return "B";
        case SYMBOLIC:
            return id.toString();
        }
        assert false;
        return "???";
    }
}

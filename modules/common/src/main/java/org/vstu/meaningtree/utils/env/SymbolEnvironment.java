package org.vstu.meaningtree.utils.env;

import org.jetbrains.annotations.Nullable;
import org.vstu.meaningtree.nodes.Definition;
import org.vstu.meaningtree.nodes.expressions.Identifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.QualifiedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.ScopedIdentifier;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.interfaces.HasSymbolScope;
import org.vstu.meaningtree.utils.env.records.FunctionRecord;
import org.vstu.meaningtree.utils.env.records.UserTypeRecord;
import org.vstu.meaningtree.utils.env.records.VariableRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SymbolEnvironment {
    private HashMap<String, VariableRecord> variables;
    private HashMap<String, FunctionRecord> functions;
    private HashMap<String, UserTypeRecord> userTypes;

    private List<ScopeRecord> anotherScopes;

    private SymbolEnvironment parent;
    private Definition owner;

    public SymbolEnvironment(SymbolEnvironment parent) {
        variables = new HashMap<>();
        functions = new HashMap<>();
        userTypes = new HashMap<>();
        anotherScopes = new ArrayList<>();
        this.parent = parent;
        this.owner = null;
    }

    public void setOwner(Definition def) {
        owner = def;
    }

    public Definition getOwner() {
        return owner;
    }

    @Nullable
    public VariableRecord getVariable(String identifier) {
        return variables.getOrDefault(identifier, null);
    }

    @Nullable
    public FunctionRecord getFunction(String identifier) {
        return functions.getOrDefault(identifier, null);
    }

    @Nullable
    public UserTypeRecord getUserType(String identifier) {
        return userTypes.getOrDefault(identifier, null);
    }

    public List<ScopeRecord> nestedEnvironments() {
        return anotherScopes.stream().toList();
    }

    public void putNestedScope(HasSymbolScope stmt, int position) {
        anotherScopes.add(new ScopeRecord(position, stmt));
    }

    public SymbolEnvironment getParent() {
        return parent;
    }

    public void put(String name, SymbolRecord record) {
        if (record instanceof FunctionRecord fr) {
            functions.put(name, fr);
        } else if (record instanceof VariableRecord vr && !userTypes.containsKey(name)) {
            variables.put(name, vr);
        } else if (record instanceof UserTypeRecord usr && !variables.containsKey(name)) {
            userTypes.put(name, usr);
        } else {
            throw new IllegalArgumentException("Environment is invalid");
        }
    }

    private SymbolRecord[] find(String identifier) {
        List<SymbolRecord> records = new ArrayList<>();
        SymbolRecord rec;
        if ((rec = getUserType(identifier)) != null) {
            records.add(rec);
        }
        if ((rec = getFunction(identifier)) != null) {
            records.add(rec);
        }
        if ((rec = getVariable(identifier)) != null) {
            records.add(rec);
        }
        return records.toArray(new SymbolRecord[0]);
    }

    public SymbolRecord resolve(Identifier identifier, Class<? extends SymbolRecord> preferredRecordType) {
        if (identifier instanceof SimpleIdentifier simpleIdent) {
            return Arrays.stream(find(simpleIdent.getName())).filter(
                    (SymbolRecord rec) -> rec.getClass().equals(preferredRecordType)
            ).toList().getFirst();
        } else if (identifier instanceof ScopedIdentifier scoped) {
            return resolveBySimpleIdentifierList(scoped.getScopeResolution(), preferredRecordType);
        } else if (identifier instanceof QualifiedIdentifier qualified && qualified.getScope() instanceof ScopedIdentifier scoped) {
            UserTypeRecord record = (UserTypeRecord) resolveBySimpleIdentifierList(scoped.getScopeResolution(), UserTypeRecord.class);
            if (record != null) {
                return record.getEnv().resolve(qualified.getMember(), preferredRecordType);
            } else {
                throw new RuntimeException("Not found:".concat(qualified.getMember().toString()));
            }
        }
        throw new RuntimeException("Unknown identifier type");
    }

    private SymbolRecord resolveBySimpleIdentifierList(List<SimpleIdentifier> resolution, Class<? extends SymbolRecord> preferredRecordType) {
        SimpleIdentifier first = resolution.removeFirst();
        SimpleIdentifier last = resolution.removeLast();
        UserTypeRecord type = (UserTypeRecord) resolve(first, UserTypeRecord.class);
        int i = 0;
        for (; i < resolution.size() && type != null; i++) {
            type = (UserTypeRecord) type.getEnv().resolve(resolution.get(i), UserTypeRecord.class);
        }
        if (type == null) {
            throw new RuntimeException("Unexpected identifier end, no such element: ".concat(resolution.get(i).toString()));
        }
        return type.getEnv().resolve(last, preferredRecordType);
    }

}

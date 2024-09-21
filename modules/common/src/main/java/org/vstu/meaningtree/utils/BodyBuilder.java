package org.vstu.meaningtree.utils;

import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.nodes.declarations.*;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;
import org.vstu.meaningtree.nodes.definitions.ClassDefinition;
import org.vstu.meaningtree.nodes.definitions.FunctionDefinition;
import org.vstu.meaningtree.nodes.definitions.MethodDefinition;
import org.vstu.meaningtree.nodes.expressions.identifiers.SimpleIdentifier;
import org.vstu.meaningtree.nodes.expressions.other.AssignmentExpression;
import org.vstu.meaningtree.nodes.interfaces.HasSymbolScope;
import org.vstu.meaningtree.nodes.statements.CompoundStatement;
import org.vstu.meaningtree.utils.env.SymbolEnvironment;
import org.vstu.meaningtree.utils.env.records.*;

import java.util.ArrayList;
import java.util.List;

public class BodyBuilder {
    /** Класс инкапсулирует построение составного оператора с его собственной таблицей символов
     *
     */
    private List<Node> _nodes = new ArrayList<>();
    private SymbolEnvironment _env;

    public BodyBuilder(SymbolEnvironment parent) {
        _env = new SymbolEnvironment(parent);
    }

    public BodyBuilder(BodyBuilder parent) {
        this(parent.getEnv());
    }

    public BodyBuilder(CompoundStatement compound) {
        _nodes = new ArrayList<>(List.of(compound.getNodes()));
        _env = compound.getEnv();
    }

    public BodyBuilder() {
        this(new SymbolEnvironment(null));
    }

    public SymbolEnvironment getEnv() {
        return _env;
    }

    public void put(Node node) {
        int position = _nodes.size();
        if (node instanceof CompoundStatement compound) {
            if (!compound.getEnv().equals(getEnv())) {
                throw new RuntimeException("Compound statement belongs to other symbol env");
            }
            _env.putNestedScope((HasSymbolScope) node, position);
        } else if (node instanceof VariableDeclaration declaration) {
            for (VariableDeclarator v : declaration) {
                _env.put(v.getIdentifier().toString(), new VariableRecord(position, declaration, v.getRValue()));
            }
        } else if (node instanceof FunctionDefinition func) {
            FunctionDeclaration funcDecl = (FunctionDeclaration) func.getDeclaration();
            _env.put(func.getName().toString(), new FunctionRecord(position, funcDecl, func));
        } else if (node instanceof AssignmentExpression assignmentExpr && assignmentExpr.getLValue() instanceof SimpleIdentifier ident) {
            VariableRecord varRecord = _env.getVariable(ident.getName());
            if (varRecord != null) {
                varRecord.putAssignmentPosition(position);
            }
        } else if (node instanceof ClassDefinition class_) {
            ClassDeclaration classDecl = (ClassDeclaration) class_.getDeclaration();
            _env.put(classDecl.getName().toString(), envRecordForClass(position, class_));
        }
        _nodes.add(node);
    }

    private ClassRecord envRecordForClass(int position, ClassDefinition class_) {
        ClassDeclaration classDecl = (ClassDeclaration) class_.getDeclaration();
        SymbolEnvironment nestedEnv = new SymbolEnvironment(getEnv());
        ClassRecord record = new ClassRecord(nestedEnv, position, classDecl, class_);
        int subposition = 0;
        for (Node rawNode : class_.getBody()) {
            if (rawNode instanceof FieldDeclaration declaration) {
                for (VariableDeclarator v : declaration) {
                    nestedEnv.put(v.getIdentifier().toString(), new FieldRecord(subposition, record, declaration, v.getRValue()));
                }
            } else if (rawNode instanceof AssignmentExpression expr && expr.getLValue() instanceof SimpleIdentifier ident) {
                FieldRecord field = (FieldRecord) nestedEnv.getVariable(ident.getName());
                if (field != null) {
                    field.putAssignmentPosition(subposition);
                }
            } if (rawNode instanceof MethodDeclaration decl) {
                nestedEnv.put(decl.getName().toString(), new MethodRecord(subposition, record, decl, null));
            } else if (rawNode instanceof MethodDefinition def) {
                MethodRecord foundSubrecord = (MethodRecord) nestedEnv.getFunction(def.getName().toString());
                if (foundSubrecord == null) {
                    foundSubrecord = new MethodRecord(subposition, record, (MethodDeclaration) def.getDeclaration(), def);
                }
                nestedEnv.put(def.getName().toString(), foundSubrecord);
            } else if (rawNode instanceof ClassDefinition def) {
                ClassDeclaration subclassDecl = (ClassDeclaration) def.getDeclaration();
                nestedEnv.put(subclassDecl.getName().toString(), envRecordForClass(subposition, def));
            }
            subposition++;
        }
        return record;
    }

    public void substitute(int index, Node node) {
        _nodes.set(index, node);
    }

    public Node remove(int index) {
        return _nodes.remove(index);
    }

    public Node removeLast() {
        return _nodes.removeLast();
    }

    public Node removeFirst() {
        return _nodes.removeFirst();
    }

    public void insert(int index, Node node) {
        _nodes.add(index, node);
    }

    public Node getFirst() {return _nodes.getFirst();}

    public Node getLast() {return  _nodes.getLast();}

    public boolean isEmpty() {
        return _nodes.isEmpty();
    }

    public Node getAt(int index) { return _nodes.get(index); }

    public CompoundStatement build() {
        return new CompoundStatement(_env, _nodes);
    }

    public int getCurrentLength() {
        return _nodes.size();
    }

    public Node[] getCurrentNodes() {
        return _nodes.toArray(new Node[0]);
    }
}

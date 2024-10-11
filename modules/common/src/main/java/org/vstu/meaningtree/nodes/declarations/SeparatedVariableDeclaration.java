package org.vstu.meaningtree.nodes.declarations;

import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.nodes.Declaration;
import org.vstu.meaningtree.nodes.Type;
import org.vstu.meaningtree.nodes.declarations.components.VariableDeclarator;

import java.util.ArrayList;
import java.util.List;

public class SeparatedVariableDeclaration extends Declaration {
    /***
     * Подходит для множественных объявлений переменных с разными типами, в поддерживаемых языках (C++)
     * Неподдерживаемые языки должны разбивать это на разные statements
     */

    private List<VariableDeclaration> _decls;

    public SeparatedVariableDeclaration(VariableDeclaration decl1, VariableDeclaration ... decl) {
        _decls = new ArrayList<>();
        _decls.add(decl1);
        _decls.addAll(List.of(decl));
    }

    public SeparatedVariableDeclaration(List<VariableDeclaration> decl) {
        if (decl == null || decl.isEmpty()) {
            throw new MeaningTreeException("Required at least one argument at separate variable declaration");
        }
        _decls = new ArrayList<>(decl);
    }

    public boolean canBeReduced() {
        Type t = _decls.getFirst().getType();
        for (VariableDeclaration decl : _decls) {
            if (!t.equals(decl.getType())) {
                return false;
            }
        }
        return true;
    }

    public VariableDeclaration reduce() {
        if (!canBeReduced()) {
            return null;
        }
        Type t = _decls.getFirst().getType();
        List<VariableDeclarator> decls = new ArrayList<>();
        for (VariableDeclaration decl : _decls) {
            decls.addAll(List.of(decl.getDeclarators()));
        }
        return new VariableDeclaration(t, decls);
    }
}

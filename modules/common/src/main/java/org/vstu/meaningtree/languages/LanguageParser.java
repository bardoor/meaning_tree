package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.treesitter.TSTree;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.utils.TreeSitterUtils;

import java.util.ArrayList;
import java.util.List;

abstract public class LanguageParser {
    protected String _code = "";
    private List<ConfigParameter> _cfg;

    public abstract TSTree getTSTree();

    public TSNode getRootNode() {
        return getTSTree().getRootNode();
    }

    public abstract MeaningTree getMeaningTree(String code);

    void setConfig(List<ConfigParameter> params) {
        _cfg = params;
    }

    protected ConfigParameter getConfigParameter(String paramName) {
        for (ConfigParameter param : _cfg) {
            if (param.getName().equals(paramName)) {
                return param;
            }
        }
        return null;
    }

    protected List<String> lookupErrors(TSNode node) {
        ArrayList<String> result = new ArrayList<>();
        _lookupErrors(node, result);
        return result;
    }

    public String getCodePiece(TSNode node) {
        return TreeSitterUtils.getCodePiece(_code, node);
    }

    private void _lookupErrors(TSNode node, List<String> list) {
        if (node.isNull()) {
            return;
        }
        if (node.isError()) {
            list.add(getCodePiece(node));
            return;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            _lookupErrors(node.getChild(i), list);
        }
    }

    public abstract LanguageTokenizer getTokenizer();
}

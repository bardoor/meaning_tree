package org.vstu.meaningtree.languages;

import org.treesitter.TSNode;
import org.treesitter.TSTree;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.TreeSitterUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class LanguageParser {
    protected String _code = "";
    private List<ConfigParameter> _cfg;
    protected Map<int[], Object> _byteValueTags = new HashMap<>();

    public abstract TSTree getTSTree();

    public TSNode getRootNode() {
        return getTSTree().getRootNode();
    }

    public abstract MeaningTree getMeaningTree(String code);

    protected synchronized MeaningTree getMeaningTree(String code, Map<int[], Object> values) {
        _byteValueTags = values;
        return getMeaningTree(code);
    }

    protected void assignValue(TSNode originNode, Node createdNode) {
        int start = originNode.getStartByte();
        int end = originNode.getEndByte();
        for (int[] indexes : _byteValueTags.keySet()) {
            if (indexes[0] >= start && indexes[1] <= end) {
                createdNode.setAssignedValueTag(_byteValueTags.get(indexes));
                _byteValueTags.remove(indexes);
            }
        }
    }

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

}

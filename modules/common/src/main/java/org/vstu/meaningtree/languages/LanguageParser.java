package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.treesitter.TSNode;
import org.vstu.meaningtree.languages.configs.ConfigParameter;

import java.nio.charset.StandardCharsets;
import java.util.List;

abstract public class LanguageParser {
    protected String _code = "";
    private List<ConfigParameter> _cfg;

    public abstract MeaningTree getMeaningTree(String code);

    protected String getCodePiece(TSNode node) {
        byte[] code = _code.getBytes(StandardCharsets.UTF_8);
        int start = node.getStartByte();
        int end = node.getEndByte();
        return new String(code, start, end - start);
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
}

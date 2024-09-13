package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.nodes.Node;

import java.util.List;

abstract public class LanguageViewer {
    private List<ConfigParameter> _cfg;

    public abstract String toString(Node node);

    public String toString(MeaningTree mt) {
        return toString(mt.getRootNode());
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

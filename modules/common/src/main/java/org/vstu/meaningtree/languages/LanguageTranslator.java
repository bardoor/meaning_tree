package org.vstu.meaningtree.languages;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.treesitter.TSNode;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.exceptions.UnsupportedParsingException;
import org.vstu.meaningtree.exceptions.UnsupportedViewingException;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.Experimental;
import org.vstu.meaningtree.utils.tokens.Token;
import org.vstu.meaningtree.utils.tokens.TokenGroup;
import org.vstu.meaningtree.utils.tokens.TokenList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class LanguageTranslator {
    protected LanguageParser _language;
    protected LanguageViewer _viewer;
    private ArrayList<ConfigParameter> _declaredConfigParams = new ArrayList<>();

    private static ConfigParameter[] getPredefinedCommonConfig() {
        return new ConfigParameter[] {
                // Если translationUnitMode установлен в true, то выводится сразу полный текст программы,
                // а не её часть (например, только выражение)
                new ConfigParameter("translationUnitMode", true, ConfigParameter.Scope.VIEWER),
                // Вывод только одного выражения
                new ConfigParameter("expressionMode", false, ConfigParameter.Scope.TRANSLATOR),
                // Попытаться сгенерировать дерево, несмотря на ошибки
                new ConfigParameter("skipErrors", false, ConfigParameter.Scope.PARSER)
        };
    }

    /**
     * Создает транслятор языка
     * @param language - parser языка
     * @param viewer - viewer языка
     * @param rawConfig - конфигурация в формате "название - значение" в виде строки (тип будет выведен автоматически из строки)
     */
    protected LanguageTranslator(LanguageParser language, LanguageViewer viewer, Map<String, String> rawConfig) {
        _language = language;
        _viewer = viewer;


        _declaredConfigParams.addAll(Arrays.asList(getPredefinedCommonConfig()));
        // Загрузка конфигов, специфических для конкретного языка
        _declaredConfigParams.addAll(Arrays.asList(getDeclaredConfigParameters()));
        for (String paramName : rawConfig.keySet()) {
            ConfigParameter cfg = getConfigParameter(paramName);
            if (cfg != null) {
                cfg.inferValueFrom(rawConfig.get(paramName));
            }
        }

        if (_language != null) {
            _language.setConfig(_declaredConfigParams.stream().filter(
                    (ConfigParameter cfg) -> cfg.getScope() == ConfigParameter.Scope.PARSER || cfg.getScope() == ConfigParameter.Scope.TRANSLATOR).toList());
        }
        if (_viewer != null) {
            _viewer.setConfig(_declaredConfigParams.stream().filter(
                    (ConfigParameter cfg) -> cfg.getScope() == ConfigParameter.Scope.VIEWER || cfg.getScope() == ConfigParameter.Scope.TRANSLATOR).toList());
        }
    }

    public MeaningTree getMeaningTree(String code) {
        return _language.getMeaningTree(prepareCode(code));
    }

    protected void setViewer(LanguageViewer viewer) {
        _viewer = viewer;
        if (_viewer != null) {
            _viewer.setConfig(_declaredConfigParams.stream().filter(
                    (ConfigParameter cfg) -> cfg.getScope() == ConfigParameter.Scope.VIEWER || cfg.getScope() == ConfigParameter.Scope.TRANSLATOR).toList());
        }
    }

    protected void setParser(LanguageParser parser) {
        _language = parser;
        if (_language != null) {
            _language.setConfig(_declaredConfigParams.stream().filter(
                    (ConfigParameter cfg) -> cfg.getScope() == ConfigParameter.Scope.PARSER || cfg.getScope() == ConfigParameter.Scope.TRANSLATOR).toList());
        }
    }

    @Experimental
    public MeaningTree getMeaningTree(TSNode node, String code) {
        return _language.getMeaningTree(node, code);
    }

    @Experimental
    public Pair<Boolean, MeaningTree> tryGetMeaningTree(TSNode node, String code) {
        try {
            return ImmutablePair.of(true, getMeaningTree(node, code));
        } catch (UnsupportedParsingException e) {
            return ImmutablePair.of(false, null);
        }
    }

    public Pair<Boolean, MeaningTree> tryGetMeaningTree(String code) {
        try {
            return ImmutablePair.of(true, getMeaningTree(code));
        } catch (UnsupportedParsingException e) {
            return ImmutablePair.of(false, null);
        }
    }

    protected MeaningTree getMeaningTree(String code, HashMap<int[], Object> values) {
        return _language.getMeaningTree(prepareCode(code), values);
    }

    public MeaningTree getMeaningTree(TokenList tokenList) {
        return getMeaningTree(String.join(" ", tokenList.stream().map((Token t) -> t.value).toList()));
    }

    public Pair<Boolean, MeaningTree> tryGetMeaningTree(TokenList tokens) {
        try {
            return ImmutablePair.of(true, getMeaningTree(tokens));
        } catch (UnsupportedParsingException e) {
            return ImmutablePair.of(false, null);
        }
    }

    public MeaningTree getMeaningTree(TokenList tokenList, Map<TokenGroup, Object> tokenValueTags) {
        HashMap<int[], Object> codeValueTag = new HashMap<>();
        for (TokenGroup grp : tokenValueTags.keySet()) {
            assert grp.source == tokenList;
            int start = 0;
            for (int i = 0; i < grp.start; i++) {
                start += grp.source.get(i).value.getBytes(StandardCharsets.UTF_8).length;
                start += 1;
            }
            int stop = start + 1;
            for (int i = start; i < grp.stop; i++) {
                stop += grp.source.get(i).value.getBytes(StandardCharsets.UTF_8).length;
                if (i != grp.stop - 1) {
                    stop += 1;
                }
            }
            codeValueTag.put(new int[] {start, stop}, tokenValueTags.get(grp));
        }
        return getMeaningTree(String.join(" ", tokenList.stream().map((Token t) -> t.value).toList()), codeValueTag);
    }

    public Pair<Boolean, MeaningTree> tryGetMeaningTree(TokenList tokens, Map<TokenGroup, Object> tokenValueTags) {
        try {
            return ImmutablePair.of(true, getMeaningTree(tokens, tokenValueTags));
        } catch (UnsupportedParsingException e) {
            return ImmutablePair.of(false, null);
        }
    }

    public abstract LanguageTokenizer getTokenizer();

    public String getCode(MeaningTree mt) {
        return _viewer.toString(mt);
    }

    public Pair<Boolean, String> tryGetCode(MeaningTree mt) {
        try {
            String result = getCode(mt);
            return ImmutablePair.of(true, result);
        } catch (UnsupportedViewingException e) {
            return ImmutablePair.of(false, null);
        }
    }

    public Pair<Boolean, TokenList> tryGetCodeAsTokens(MeaningTree mt) {
        try {
            TokenList result = getCodeAsTokens(mt);
            return ImmutablePair.of(true, result);
        } catch (UnsupportedViewingException e) {
            return ImmutablePair.of(false, null);
        }
    }

    public String getCode(Node node) {
        return _viewer.toString(node);
    }

    public TokenList getCodeAsTokens(MeaningTree mt) {
        return getTokenizer().tokenizeExtended(mt);
    }

    public ConfigParameter getConfigParameter(String name) {
        for (ConfigParameter param : _declaredConfigParams) {
            if (param.getName().equals(name)) {
                return param;
            }
        }
        return null;
    }

    public abstract String prepareCode(String code);

    public abstract TokenList prepareCode(TokenList list);

    /**
     * Внимательно следите за тем, чтобы название параметра не пересекалось с уже предопределенными
     * @return специфические параметры конфигурации языка
     */
    protected abstract ConfigParameter[] getDeclaredConfigParameters();
}

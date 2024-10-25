package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.languages.configs.ConfigParameter;
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
                new ConfigParameter("expressionMode", false, ConfigParameter.Scope.PARSER),
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
            } else {
                throw new MeaningTreeException("Missing config parameter: ".concat(paramName));
            }
        }

        _language.setConfig(_declaredConfigParams.stream().filter((ConfigParameter cfg) -> {
            return cfg.getScope() == ConfigParameter.Scope.PARSER || cfg.getScope() == ConfigParameter.Scope.TRANSLATOR;
        }).toList());
        _viewer.setConfig(_declaredConfigParams.stream().filter((ConfigParameter cfg) -> {
            return cfg.getScope() == ConfigParameter.Scope.VIEWER || cfg.getScope() == ConfigParameter.Scope.TRANSLATOR;
        }).toList());
    }

    public MeaningTree getMeaningTree(String code) {
        return _language.getMeaningTree(code);
    }

    protected MeaningTree getMeaningTree(String code, HashMap<int[], Object> values) {
        return _language.getMeaningTree(code, values);
    }

    public MeaningTree getMeaningTree(TokenList tokenList) {
        return getMeaningTree(String.join(" ", tokenList.stream().map((Token t) -> t.value).toList()));
    }

    public MeaningTree getMeaningTree(TokenList tokenList, Map<TokenGroup, Object> tokenValueTags) {
        HashMap<int[], Object> codeValueTag = new HashMap<>();
        for (TokenGroup grp : tokenValueTags.keySet()) {
            assert grp.source == tokenList;
            int start = 0;
            for (int i = 0; i < grp.start; i++) {
                start += grp.source.get(i).value.getBytes(StandardCharsets.UTF_8).length;
                if (i != grp.start - 1) {
                    start += 1;
                }
            }
            int stop = start;
            for (int i = grp.start + 1; i < grp.stop; i++) {
                stop += grp.source.get(i).value.getBytes(StandardCharsets.UTF_8).length;
                if (i != grp.stop - 1) {
                    stop += 1;
                }
            }
            codeValueTag.put(new int[] {start, stop}, tokenValueTags.get(grp));
        }
        return getMeaningTree(String.join(" ", tokenList.stream().map((Token t) -> t.value).toList()), codeValueTag);
    }

    public abstract LanguageTokenizer getTokenizer();

    public String getCode(MeaningTree mt) {
        return _viewer.toString(mt);
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

    /**
     * Внимательно следите за тем, чтобы название параметра не пересекалось с уже предопределенными
     * @return специфические параметры конфигурации языка
     */
    protected abstract ConfigParameter[] getDeclaredConfigParameters();
}

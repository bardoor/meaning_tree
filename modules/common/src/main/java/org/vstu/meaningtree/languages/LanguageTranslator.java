package org.vstu.meaningtree.languages;

import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.exceptions.MeaningTreeException;
import org.vstu.meaningtree.languages.configs.ConfigParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public abstract class LanguageTranslator {
    protected LanguageParser _language;
    protected LanguageViewer _viewer;
    private ArrayList<ConfigParameter> _declaredConfigParams = new ArrayList<>();

    /**
     * Создает транслятор языка
     * @param language - parser языка
     * @param viewer - viewer языка
     * @param rawConfig - конфигурация в формате "название - значение" в виде строки (тип будет выведен автоматически из строки)
     */
    protected LanguageTranslator(LanguageParser language, LanguageViewer viewer, Map<String, String> rawConfig) {
        _language = language;
        _viewer = viewer;

        // Если translationUnitMode установлен в true, то выводится сразу полный текст программы,
        // а не её часть (например, только выражение)
        _declaredConfigParams.add(new ConfigParameter("translationUnitMode", true, ConfigParameter.Scope.VIEWER));
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

    public String getCode(MeaningTree mt) {
        return _viewer.toString(mt);
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

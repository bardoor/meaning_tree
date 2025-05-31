package org.vstu.meaningtree.languages;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.treesitter.TSNode;
import org.vstu.meaningtree.MeaningTree;
import org.vstu.meaningtree.exceptions.UnsupportedParsingException;
import org.vstu.meaningtree.exceptions.UnsupportedViewingException;
import org.vstu.meaningtree.languages.configs.*;
import org.vstu.meaningtree.languages.configs.params.EnforseEntryPoint;
import org.vstu.meaningtree.languages.configs.params.ExpressionMode;
import org.vstu.meaningtree.languages.configs.params.SkipErrors;
import org.vstu.meaningtree.languages.configs.params.TranslationUnitMode;
import org.vstu.meaningtree.nodes.Node;
import org.vstu.meaningtree.utils.Experimental;
import org.vstu.meaningtree.utils.tokens.Token;
import org.vstu.meaningtree.utils.tokens.TokenGroup;
import org.vstu.meaningtree.utils.tokens.TokenList;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class LanguageTranslator {
    protected LanguageParser _language;
    protected LanguageViewer _viewer;
    protected Config _config = new Config();

    public static Config getPredefinedCommonConfig() {
        return new Config(
                new ExpressionMode(false, ConfigScope.TRANSLATOR),
                new TranslationUnitMode(true, ConfigScope.VIEWER),
                new SkipErrors(false, ConfigScope.PARSER),
                new EnforseEntryPoint(true, ConfigScope.ANY)
        );
    }

    protected Config getDeclaredConfig() { return new Config(); }

    /**
     * Создает транслятор языка
     * @param language - parser языка
     * @param viewer - viewer языка
     * @param rawConfig - конфигурация в формате "название - значение" в виде строки (тип будет выведен автоматически из строки)
     */
    protected LanguageTranslator(LanguageParser language, LanguageViewer viewer, Map<String, String> rawConfig) {
        _language = language;
        _viewer = viewer;

        _config.merge(getPredefinedCommonConfig(), getDeclaredConfig());
        var configParser = new ConfigParser();

        // Загрузка конфигов, специфических для конкретного языка
        for (var entry : rawConfig.entrySet()) {
            var param = configParser.parse(entry.getKey(), entry.getValue());
            _config.putNew(param);
        }

        _language.setConfig(
                _config.subset(
                        cfg -> cfg.inAnyScope(ConfigScope.PARSER, ConfigScope.TRANSLATOR)
                )
        );

        _viewer.setConfig(
                _config.subset(
                        cfg -> cfg.inAnyScope(ConfigScope.VIEWER, ConfigScope.TRANSLATOR)
                )
        );
    }

    public MeaningTree getMeaningTree(String code) {
        return _language.getMeaningTree(prepareCode(code));
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

    protected <P, T extends ConfigParameter<P>> Optional<P> getConfigParameter(Class<T> configClass) {
        return Optional.ofNullable(_config).flatMap(config -> config.get(configClass));
    }

    public abstract String prepareCode(String code);

    public abstract TokenList prepareCode(TokenList list);
}

# JavaLanguage

Класс `JavaLanguage` реализует преобразование дерева, разобранного Tree‑sitter, в `MeaningTree`.

## Основные возможности

- **Инициализация**
  - `public JavaLanguage()`

- **Построение дерева**
  - `public synchronized MeaningTree getMeaningTree(String code)`
    - Конвертирует исходнный код на языке `Java` в `MeaningTree`.

- **Использование:**
```java
LanguageParser javaLanguage = new JavaLanguage();
MeaningTree meaningTree = javaLanguage.getMeaningTree("String a = \"Hello, world!\"");
```

## Поддерживаемые классы результирующего дерева

- `Error`
- `ArrayAccess`
- `ArrayCreationExpression`
- `ArrayInitializer`
- `AssignmentExpression`
- `BinaryExpression`
- `Block`
- `BreakStatement`
- `CastExpression`
- `CharacterLiteral`
- `ClassDeclaration`
- `ClassLiteral`
- `Condition`
- `ConstructorDeclaration`
- `ContinueStatement`
- `DecimalFloatingPointLiteral`
- `DecimalIntegerLiteral`
- `DoStatement`
- `EnhancedForStatement`
- `ExpressionStatement`
- `FieldAccess`
- `FieldDeclaration`
- `ForStatement`
- `Identifier`
- `IfStatement`
- `ImportDeclaration`
- `InstanceofExpression`
- `LineComment`
- `LocalVariableDeclaration`
- `MethodDeclaration`
- `MethodInvocation`
- `NullLiteral`
- `ObjectCreationExpression`
- `PackageDeclaration`
- `ParenthesizedExpression`
- `Program`
- `ReturnStatement`
- `ScopedIdentifier`
- `Statement`
- `StringLiteral`
- `SwitchExpression`
- `TernaryExpression`
- `This`
- `True`
- `UnaryExpression`
- `UpdateExpression`
- `VoidType`
- `WhileStatement`


## Текущие ограничения

- **Нет значений по умолчанию** для параметров (всегда `null`).

- **Отсутствие полноценной таблицы символов** и разрешения имён (scope).

- **Пока не поддерживаются аннотации** (annotations всегда пуст).

- **Частичная поддержка пользовательских типов**.

# JavaViewer

Класс `JavaViewer` выполняет преобразование внутреннего дерева (`MeaningTree`) в корректно отформатированный Java‑код с учётом заданных параметров стиля.

## Основные возможности

- **Конфигурируемые параметры форматирования**
  - `indentSpaceCount`, `openBracketOnSameLine`, `bracketsAroundCaseBranches`, `autoVariableDeclaration`

- **Поддержка полного набора узлов `MeaningTree` через метод `toString(Node node)` и `switch`.**

- **Использование:**
```java
LanguageViewer javaViewer = new JavaViewer();
String code = javaViewer.toString(meaningTree);
```

## Конструкторы

```java
// По умолчанию: 4 пробела, открывающая скобка на той же строке, без скобок вокруг case, без авто‑декларации
public JavaViewer()

// Указание всех параметров стиля
public JavaViewer(
    int indentSpaceCount,
    boolean openBracketOnSameLine,
    boolean bracketsAroundCaseBranches,
    boolean autoVariableDeclaration
)

// Инициализация через токенизатор (использует жёстко закодированные значения) из конструктора по умолчанию
public JavaViewer(LanguageTokenizer tokenizer)
```


## Ограничения

- Нет автоматического вывода generics.

- Ограниченная проверка типов (Hindley-Milner отключен).

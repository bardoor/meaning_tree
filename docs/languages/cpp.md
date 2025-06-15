# CppLanguage

Класс `CppLanguage` реализует преобразование дерева, разобранного Tree‑sitter, в `MeaningTree`.

## Основные возможности

- **Инициализация**
  - `public CppLanguage()`
    - Создает пустой парсер, инициализирует структуру для пользовательских типов.
- **Построение дерева**
  - `public synchronized MeaningTree getMeaningTree(String code)`
    - Конвертирует исходнный код на языке `C++` в `MeaningTree`.

- **Использование:**
```java
LanguageParser cppLanguage = new CppLanguage();
MeaningTree meaningTree = cppLanguage.getMeaningTree("int main() { int a = 10; }");
```

## Поддерживаемые классы результирующего дерева

### Корневой объект
- `MeaningTree`

### Точка входа
- `ProgramEntryPoint`

### Функции и параметры
- `FunctionDeclaration`
- `FunctionDefinition`
- `DeclarationArgument`

### Блоки
- `CompoundStatement`

### Литералы и идентификаторы
- `IntegerLiteral`
- `CharacterLiteral`
- `StringLiteral`
- `BoolLiteral`
- `NullLiteral`
- `ArrayLiteral`
- `SimpleIdentifier`
- `QualifiedIdentifier`
- `Identifier`

### Выражения и операции
- `AssignmentExpression`
- `BinaryExpression`
- `UnaryExpression`
- `CallExpression`
- `ConditionalExpression`
- `CommaExpression`
- `SubscriptExpression`
- `UpdateExpression`
- `CastTypeExpression`
- `SizeofExpression`

### Операторы new / delete
- `ObjectNewExpression`
- `PlacementNewExpression`
- `ArrayNewExpression`
- `DeleteExpression`

### Типы
- `IntType`
- `FloatType`
- `CharacterType`
- `StringType`
- `BooleanType`
- `NoReturn`
- `PointerType`
- `ReferenceType`
- `GenericClass`
- `Class`
- `DictionaryType`
- `ListType`
- `SetType`
- `UnknownType`

### Управляющие конструкции
- `IfStatement`
- `WhileLoop`
- `GeneralForLoop`
- `RangeForLoop`
- `InfiniteLoop`
- `SwitchStatement`
- `BasicCaseBlock`
- `FallthroughCaseBlock`
- `DefaultCaseBlock`
- `BreakStatement`
- `ContinueStatement`
- `ReturnStatement`

## Текущие ограничения

- **Нет значений по умолчанию** для параметров функций (всегда `null`).
- **Отсутствие полноценной таблицы символов** и разрешения имён (scope).
- **Пока не поддерживаются аннотации** (annotations всегда пуст).
- **ExpressionMode**: допускается только одно выражение в теле `main`.
- **Частичная поддержка параметров шаблонов** и пользовательских типов (TODO).
- **Расширения C++** (например, `parameter_pack_expansion`) обрабатываются упрощённо через рекурсию.

# CppViewer

Класс `CppViewer` выполняет преобразование внутреннего дерева `MeaningTree` в корректно отформатированный C++‑код с учётом заданных параметров стиля.

---

## Основные возможности

- **Конфигурируемые параметры форматирования**
  - отступы (количество пробелов или символ табуляции);
  - расположение открывающей скобки (`{`) — на той же строке или на следующей;
  - оборачивание веток `case` в дополнительные скобки;
  - автоматическое объявление переменных при инициализации.

- **Поддержка полного набора узлов `MeaningTree`**
  - выражения (`BinaryExpression`, `UnaryExpression`, `TernaryOperator` и др.);
  - операторы управления (`IfStatement`, `WhileLoop`, `SwitchStatement`, `ForLoop` и др.);
  - литералы и идентификаторы (`NumericLiteral`, `StringLiteral`, `Identifier` и др.);
  - функции и их объявления/определения (`FunctionDeclaration`, `FunctionDefinition`);
  - операции `new`/`delete`, работа с коллекциями и многое другое.

- **Автоматическое управление отступами**
  - вложенность блоков увеличивается/уменьшается автоматически;
  - гарантируется, что уровень отступа не станет отрицательным.

- **Использование:**
```java
LanguageViewer cppViewer = new CppViewer();
String code = cppViewer.toString(meaningTree);
```
---

## Конструкторы

```java
// По умолчанию: 4 пробела, открывающая скобка на той же строке, без скобок вокруг case, без авто‑декларации
public CppViewer()

// Указание всех параметров стиля
public CppViewer(
    int indentSpaceCount,
    boolean openBracketOnSameLine,
    boolean bracketsAroundCaseBranches,
    boolean autoVariableDeclaration
)

// Инициализация через токенизатор (использует жёстко закодированные значения) из конструктора по умолчанию
public CppViewer(LanguageTokenizer tokenizer)
```

| Параметр                       | Описание                                                                                         |
|--------------------------------|--------------------------------------------------------------------------------------------------|
| `indentSpaceCount`             | сколько пробелов использовать для одного уровня вложенности (`" ".repeat(indentSpaceCount)`)     |
| `openBracketOnSameLine`        | `true` — `{` сразу после заголовка блока; `false` — на новой строке с отступом                   |
| `bracketsAroundCaseBranches`   | `true` — всегда оборачивать содержимое `case` в `{…}`; `false` — только при объявлении переменных|
| `autoVariableDeclaration`      | `true` — при первом присваивании автоматически генерировать `type name = …;`                      |

---

## Управление отступами

- **`increaseIndentLevel()`**  
  Увеличивает внутренний счётчик вложенности.
- **`decreaseIndentLevel()`**  
  Уменьшает счётчик; при попытке уйти ниже 0 бросает `UnsupportedViewingException`.
- **`indent(String s)`**  
  Добавляет к строке `s` нужное количество повторений строки отступа.

---

## Поддерживаемые конструкции

| Категория                       | Узлы                                      |
|---------------------------------|-------------------------------------------|
| **Вход/вывод**                  | `InputCommand` (std::cin), `PrintCommand` |
| **Управление потоком**          | `IfStatement`, `SwitchStatement`, `ReturnStatement`, `BreakStatement`, `ContinueStatement` |
| **Циклы**                       | `WhileLoop`, `InfiniteLoop`, `RangeForLoop`, `GeneralForLoop` |
| **Операции работы с памятью**   | `MemoryAllocationCall (new)`, `MemoryFreeCall (delete)` |
| **Выражения**                   | `BinaryExpression`, `UnaryExpression`, `TernaryOperator`, `AssignmentExpression`, `CastTypeExpression`, `SizeofExpression` |
| **Коллекции и литералы**        | `PlainCollectionLiteral`, `DictionaryLiteral`, `ArrayInitializer` |

---

## Ограничения

- Не поддерживается локализация (все ключевые слова на английском).
- Не обрабатываются все расширения C++ (ограниченная поддержка шаблонов).
- Нет автоматического разрешения имен (scope analysis отсутствует).
- Авто‑декларация переменных работает только для простых случаев и может потребовать ручной корректировки.


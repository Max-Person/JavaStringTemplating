# JavaStringTemplating

A simple templating library for managing user-made string templates in a flexible but safe way.

#### TL;DR usage example:
```java
Template t = new Template("Hey, $user, you're incorrect. The answer here is ${3+3*2}."); //Define a template
InterpretationData data = new InterpretationData().withVar("user", "Bob"); //Define the data to fill the template
String res = data.interpret(t); // interpret the template using the data
//res is "Hey, Bob, you're incorrect. The answer here is 9."
```
Or, shorter:
```java
String res = new InterpretationData().withVar("user", "Bob")
                  .interpret("Hey, $user, you're incorrect. The answer here is ${3+3*2}.");
```

# Features

## Interpolations

The base functionality of the library is the transformations of template strings (e.g. `"Hello, ${username}"`) into a filled-in string (e.g. `"Hello, Bob"`).

A template is an arbitrary string containing any number of *interpolations* - which are designated by the `${...}` syntax.
*Simple interpolations* omitting the braces are also allowed in case of only using variable name as the interpolated expression - for example, `"Hello, $username"` would be valid.

## Expression Language

The library features a custom expression language that allows evaluating complex expressions to fill template's gaps.
The language currently supports basic arithmetic, comparisons, boolean operations, ternary conditions, field access and method calls. As such, fairly complex expressions can be constructed in the templates.

## Defining variables

The filling-in of the template (its *interpretation*) consists of two parts - the template itself (`Template` class), defining *how* to construct the resulting string, and the interpretation data (`InterpretationData` class), defining *what* to construct the resulting string of. As such, all the variables mentioned in the template are taken from the interpretation data and must be defined by it.

## Providing functionality

The library was made with the task of using user-made templates in mind. As such, a way to provide the user with some predefined functionality was needed that does not hand them the power to freely execute arbitrary code.
The library achieves this by using `@TemplatingSafeMethod` and `@TemplatingSafeField` annotations. Only methods and fields marked by such annotations are visible from the user-defined templates and as such allow the developer to control which functionality they provide to the user.

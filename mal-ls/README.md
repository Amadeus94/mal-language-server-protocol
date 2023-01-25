<!--Project Name-->
# MAL Language Server

<!--A brief description of what the project does-->
Language Server created for the Meta Attack Language (MAL)


# Compiler
The language server utilizes the MAL compiler found [here](github.com) <!--TODO: Add link-->
in order to extract the symbols, and perform the features.  

The compiler involves several stages, however what is important for this project are the Lexical analysis, Parsing, and Semantic Analysis steps.  
 1. **Lexical Analysis**:  Also known as lexing/tokenization is the first stage of the compilation process. The process breaks the source code into a sequence of tokens, which are theb uilding blocks of the language. 
 Tokens Are typically compsed of characters and are defined by regular expressions or other pattern matchign techniques.

 ***Input***: Source Code
 ***Output***:  Stream of tokens

 ***Example***: test.txt file containing:

 ```There is a dog in the garden```

 After the Lexical Analysis stage: 
```
tokens = ["there","is","a","dog","in","the","garden"]
Note: Each Token is also contains some metadata such as location, type, name
```

 2. **Parsing**: Also known as Syntax Analysis, and is the second stage of the compilation process. In this stage the sequence of tokens from the previous stage are analyzed  and construct a Abstract Syntax Tree (AST). The AST represents the the syntactic structure of the program.  In short the AST or "parse tree" is used to ensure that the source code follows the correct grammar and syntax of the language. In other words the otucome is a representation of the source code. 

 ***Input***: Stream of Tokens
 ***Output***:  Abstract Syntax Tree (AST) 

 ***Example***: <!--TODO:--> 


3. **Semantic Analysis** This stage invovles analyzing the AST from the previous stage and extracting the meaning of the source code. Things such as type checking, variable scoping, and control flow analysis.   The objective of this stage is to ensure the source code is semantically correct  and that it adheres to the rules of the language. The output is the Semantic model

 ***Input***: AST
 ***Output***:  Semantic Model 

 ***Example***: <!--TODO:--> 

 The steps are represented inthe compiler code the following classes:
 1. **Lexical Analysis** == lexer.java
 2. **Parsing** == parser.java, which output a AST.java class
 3. **Semantic Analysis** == Analyzer.java
## General Information about LSP4J
- Location class in LSP4j
    - Represent the location of a symbol in source code
        - contains two properties 
            - URI(string file path - ie current file)
            - Range ( position of the Symbol)
                - Position start
                - Position end

Here is how you can create a Location-object
```
Position start = new Position(0,0);
Position end = new Position(0,5);
Range range = new Range(start,end);
String uri = "file:///path/to/file.txt"
Location location = new Location(uri, range);
```
So lets say we have a file  that only contains the following:

```
String getName()
```

The first character position is always 0 so that is the 'S', and the Range is of [)  so the thing at position 5 is not in it.  Which means 0-4 index are the ones


## Symbol
- A Symbol is an identifier 
    - String name
        - name is the Symbol, and has a type (kind) 
    - Contains information such as
        - Location
        - Type
        - Documentation (and/or other metadata related to the identifier)


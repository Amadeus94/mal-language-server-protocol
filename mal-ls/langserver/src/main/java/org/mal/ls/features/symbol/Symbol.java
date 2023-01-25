package org.mal.ls.features.symbol;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SymbolKind;

/**
 * Symbol class
 * 
 * Represents a symbol in the MAL language
 * - name of the symbol
 * - kind of the symbol
 * - location of the symbol (Position in the file). For instance variable is at
 * line 1 column 1
 */
public class Symbol {
    private String name;
    private String kind; // Maybe change this to SymbolKind
    private Location location;

    public Symbol(String name, String kind, Location location) {
        this.name = name;
        this.kind = kind;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public Location getLocation() {
        return location;
    }
}

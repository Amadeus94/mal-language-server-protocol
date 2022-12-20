package org.mal.ls.features.hover;

import org.eclipse.lsp4j.Range;

public class HoverModel {
    public Range range;
    public String id;
    public String type;
    public String typeDescription;
    

    public HoverModel(Range range, String id, String type, String typeDescription) {
        this.range = range;
        this.id = id;
        this.type = type;
        this.typeDescription = typeDescription;
    }
}
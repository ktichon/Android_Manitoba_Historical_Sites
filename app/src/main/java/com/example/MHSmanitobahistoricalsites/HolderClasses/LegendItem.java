package com.example.MHSmanitobahistoricalsites.HolderClasses;

public class LegendItem {
    String typeName;
    Float colour;

    public LegendItem(String typeName, Float colour) {
        this.typeName = typeName;
        this.colour = colour;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Float getColour() {
        return colour;
    }

    public void setColour(Float colour) {
        this.colour = colour;
    }
}

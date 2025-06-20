package stan;

import java.awt.*;

/// Store the fill and border color for the given feature, and an optional buffer if the feature geometrie should be inflated
public record DrawableFeature(DomainFeature feature, Color fillColor, Color borderColor, double buffer){}

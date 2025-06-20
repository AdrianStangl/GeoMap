package stan;

/// Store the full icon path the icon position, the icon height and width, and the label name
public record IconDrawInfo(String iconPath, int x, int y, int width, int height, String label) {}

package stan;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.*;
import java.util.List;

import com.vividsolutions.jts.geom.*;

/**
 * The {@code LabelRenderer} class is responsible for rendering labels and icons on a 2D map using a {@link Graphics2D} context.
 * It includes support for rotated labels (e.g., along roads), icon-label pairs, and collision-aware label placement.
 *
 * It works in conjunction with geometry data provided by JTS and draws elements using screen coordinates calculated
 * from a given bounding envelope.
 */
public class LabelRenderer {
    private final Graphics2D g;
    private final int iconSize;
    private final int globalFontSize;
    private final int width;
    private final int height;
    private final Geometry target;
    private final Envelope env;

    /**
     * Constructs a {@code LabelRenderer} to draw onto a graphics context with given map size and projection bounds.
     *
     * @param g              the {@link Graphics2D} context to draw on
     * @param iconSize       default icon size (used for spacing)
     * @param globalFontSize global font size (used for label text)
     * @param width          width of the drawing area
     * @param height         height of the drawing area
     * @param target         a geometry representing the full area to be rendered; used to compute pixel scaling
     */
    public LabelRenderer(Graphics2D g, int iconSize, int globalFontSize,
                         int width, int height, Geometry target) {
        this.g = g;
        this.iconSize = iconSize;
        this.globalFontSize = globalFontSize;
        this.width = width;
        this.height = height;
        this.target = target;
        this.env = target.getEnvelopeInternal();
    }

    /**
     * Converts a longitude value to a pixel X-coordinate.
     */
    private int toPixelX(double lon) {
        return (int) Math.round((lon - env.getMinX()) * width / (env.getMaxX() - env.getMinX()));
    }

    /**
     * Converts a latitude value to a pixel Y-coordinate.
     */
    private int toPixelY(double lat) {
        return height - (int) Math.round((lat - env.getMinY()) * height / (env.getMaxY() - env.getMinY()));
    }

    /**
     * Attempts to place a label near a specified point (centerX, baselineY),
     * testing multiple offset positions to avoid overlap with icons and other labels.
     *
     * @param g               graphics context
     * @param label           the text to render
     * @param centerX         base X coordinate (center of label)
     * @param baselineY       base Y coordinate (text baseline)
     * @param usedIconAreas   list of icon bounding boxes to avoid
     * @param usedLabelAreas  list of label bounding boxes to avoid
     * @param fm              font metrics for label size
     * @param shadowColor     shadow color (usually dark)
     * @param fillColor       foreground text color
     */
    public void placeAndDrawLabel(Graphics2D g, String label, int centerX, int baselineY,
                                  List<Shape> usedIconAreas, List<Shape> usedLabelAreas, FontMetrics fm, Color shadowColor, Color fillColor) {
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getHeight();

        int[][] offsets = {
                {0, 0},                             // below
                {0, -textHeight - 4},              // above
                {-textWidth / 2 - 5, 0},               // left
                {textWidth/ 2 + 5, 0},                // right
                {-textWidth / 2, textHeight / 2 + 4},  // bottom-left
                {textWidth / 2, textHeight / 2 + 4},   // bottom-right
        };

        for (int[] offset : offsets) {
            int tx = centerX + offset[0] - textWidth / 2;
            int ty = baselineY + offset[1];
            Rectangle labelBox = new Rectangle(tx, ty - textHeight, textWidth, textHeight);

            boolean overlapsIcon = usedIconAreas.stream().anyMatch(r -> r.intersects(labelBox));
            boolean overlapsLabel = usedLabelAreas.stream().anyMatch(r -> r.intersects(labelBox));

            if (!overlapsIcon && !overlapsLabel) {
                drawLabel(g, label, centerX + offset[0], ty, fm, shadowColor, fillColor);
                usedLabelAreas.add(labelBox);
                return;
            }
        }
    }

    /**
     * Draws a label centered horizontally at a given baseline Y-coordinate.
     */
    private void drawLabel(Graphics2D g, String label, int centerX, int baselineY, FontMetrics metrics, Color shadowColor, Color fillColor) {
        int textX = centerX - metrics.stringWidth(label) / 2;
        int textY = baselineY;

        g.setColor(shadowColor);
        g.drawString(label, textX + 1, textY + 1); // Shadow
        g.setColor(fillColor);
        g.drawString(label, textX, textY);         // Foreground
    }

    /**
     * Draws road/street names along their center lines, rotated to match the street direction.
     * Labels are only drawn if not too close to other labels of the same name.
     *
     * @param g2d             graphics context
     * @param fm              font metrics
     * @param streetFeatureList list of street features to label
     * @param usedIconAreas   bounding boxes of existing icons
     * @param usedLabelAreas  bounding boxes of existing labels
     */
    public void drawStreetLabels(Graphics2D g2d, FontMetrics fm, List<DomainFeature> streetFeatureList, List<Shape> usedIconAreas, List<Shape> usedLabelAreas) {
        System.out.println("Drawing Street Labels, this may take a while...");
        double labelMinDistance = 200.0;
        Map<String, List<Point2D>> labelPositions = new HashMap<>();

        for (DomainFeature road : streetFeatureList) {
            Geometry geom = road.geometry();
            if (!(geom instanceof com.vividsolutions.jts.geom.LineString line)) continue;

            Coordinate[] coords = line.getCoordinates();
            if (coords.length < 2) continue;

            Point2D baseLabelPos = computeMidpoint(coords);
            double angle = computeLocalAngle(coords);

            // Check for labels in close proximity
            boolean tooClose = labelPositions
                    .getOrDefault(road.realname(), List.of())
                    .stream().anyMatch(p -> p.distance(baseLabelPos) < labelMinDistance);
            if (tooClose) continue;

            // Text-Masse berechnen
            String label = road.realname();
            int textWidth = fm.stringWidth(label);
            int textHeight = fm.getHeight();
            int ascent = fm.getAscent();

            int[][] offsets = {
                    {0, 0}, {6, 0}, {-6, 0}, {0, 6}, {0, -6},
                    {6, 6}, {-6, -6}, {8, 0}, {0, 8}
            };

            boolean placed = false;
            for (int[] offset : offsets) {
                double offsetX = baseLabelPos.getX() + offset[0];
                double offsetY = baseLabelPos.getY() + offset[1];
                Point2D offsetPos = new Point2D.Double(offsetX, offsetY);


                Shape labelShape = createRotatedLabelShape(label, offsetPos, angle, g2d);

                boolean overlapsLabel = usedLabelAreas.stream().anyMatch(s -> s.intersects(labelShape.getBounds2D()));
                boolean overlapsIcon = usedIconAreas.stream().anyMatch(s -> s.intersects(labelShape.getBounds2D()));

                if (!overlapsLabel && !overlapsIcon) {
                    drawRotatedLabel(g2d, label, offsetPos, angle);
                    usedLabelAreas.add(labelShape);
                    labelPositions.computeIfAbsent(label, k -> new ArrayList<>()).add(offsetPos);
                    placed = true;
                    break;
                }
            }
        }
    }

    /**
     * Computes the midpoint (in pixel space) of a line geometry.
     *
     * @param coords array of line coordinates
     * @return midpoint in screen coordinates
     */
    private Point2D computeMidpoint(Coordinate[] coords) {
        int mid = coords.length / 2;
        return new Point2D.Double(toPixelX(coords[mid].x), toPixelY(coords[mid].y));
    }

    /**
     * Computes the angle in radians representing the orientation of a line segment near the center of a geometry.
     * The angle is adjusted to ensure text is not upside down.
     */
    private double computeLocalAngle(Coordinate[] coords) {
        int mid = coords.length / 2;
        int idx1 = Math.max(0, mid - 1);
        int idx2 = Math.min(coords.length - 1, mid + 1);

        double x1 = toPixelX(coords[idx1].x);
        double y1 = toPixelY(coords[idx1].y);
        double x2 = toPixelX(coords[idx2].x);
        double y2 = toPixelY(coords[idx2].y);

        double dx = x2 - x1;
        double dy = y2 - y1;

        // Check if label would be upsidedown and flip
        double angle = Math.atan2(dy, dx);

        // Nur bei mehr als 90° Neigung kippen (d.h. Text ist "unten")
        if (angle > Math.PI / 2 || angle < -Math.PI / 2) {
            angle += Math.PI; // Rotate
        }

        return angle;
    }

    /**
     * Draws a rotated label at a specified position and angle.
     *
     * @param g2d      graphics context
     * @param text     label text
     * @param position screen coordinate position
     * @param angle    angle in radians to rotate the text
     */
    public void drawRotatedLabel(Graphics2D g2d, String text, Point2D position, double angle) {
        AffineTransform old = g2d.getTransform();

        g2d.translate(position.getX(), position.getY());
        g2d.rotate(angle);

        int textWidth = g2d.getFontMetrics().stringWidth(text);
        int textHeight = g2d.getFontMetrics().getHeight();

        g2d.setColor(Color.BLACK);
        g2d.drawString(text, -textWidth / 2 + 1, textHeight / 3 + 1); // shadow
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, -textWidth / 2, textHeight / 3);         // foreground

        g2d.setTransform(old);
    }

    /**
     * Creates a {@link Shape} representing the area covered by a rotated label.
     * Used for collision detection with other labels and icons.
     *
     * @param text  label text
     * @param pos   label center position
     * @param angle rotation angle in radians
     * @param g2d   graphics context (for font metrics)
     * @return transformed shape representing the rotated text
     */
    public Shape createRotatedLabelShape(String text, Point2D pos, double angle, Graphics2D g2d) {
        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout layout = new TextLayout(text, g2d.getFont(), frc);
        Shape textShape = layout.getOutline(null);

        AffineTransform transform = new AffineTransform();
        transform.translate(pos.getX(), pos.getY());
        transform.rotate(angle);
        transform.translate(-layout.getBounds().getWidth() / 2, layout.getBounds().getHeight() / 3);

        return transform.createTransformedShape(textShape);
    }

    /**
     * Draws labels that are not associated with icons. Useful for standalone features like landmarks or named areas.
     *
     * @param labelOnlyList   list of label-only items
     * @param usedIconAreas   icon bounding boxes to avoid
     * @param usedLabelAreas  label bounding boxes to avoid
     * @param fm              font metrics
     */
    public void drawNormalLabels(List<IconDrawInfo> labelOnlyList, List<Shape> usedIconAreas, List<Shape> usedLabelAreas, FontMetrics fm) {
        // Handle label-only entries
        for (IconDrawInfo labelOnly : labelOnlyList) {
            String label = cleanRealName(labelOnly.label());
            placeAndDrawLabel(g, label, labelOnly.x(), labelOnly.y(), usedIconAreas, usedLabelAreas, fm, Color.BLACK, Color.WHITE);
        }
    }

    /**
     * Draws water-related labels with specific colors. Intended for use with lakes, rivers, etc.
     *
     * @param waterLabelList  list of water-related label items
     * @param usedIconAreas   icon bounding boxes to avoid
     * @param usedLabelAreas  label bounding boxes to avoid
     * @param fm              font metrics
     */
    public void drawWaterLabels(List<IconDrawInfo> waterLabelList, List<Shape> usedIconAreas, List<Shape> usedLabelAreas, FontMetrics fm) {
        Color labelShadow = new Color(90, 140, 200);    // neutral-dunkel
        Color labelFill = new Color(200, 240, 255);    // helles Eisblau
        for (IconDrawInfo labelOnly : waterLabelList) {
            String label = cleanRealName(labelOnly.label());
            placeAndDrawLabel(g, label, labelOnly.x(), labelOnly.y(), usedIconAreas, usedLabelAreas, fm, labelShadow, labelFill);
        }
    }

    /**
     * Draws icons with labels below them, trying multiple small offsets to avoid collisions.
     * If an icon and label placement is found that does not overlap existing content, it is drawn.
     *
     * @param iconDrawList    list of icon draw instructions
     * @param fm              font metrics
     * @param usedIconAreas   list of already drawn icons
     * @param usedLabelAreas  list of already drawn labels
     */
    public void drawIconsAndLabels(List<IconDrawInfo> iconDrawList, FontMetrics fm, List<Shape> usedIconAreas, List<Shape> usedLabelAreas) {
        for (IconDrawInfo info : iconDrawList) {
            String label = cleanRealName(info.label());
            int iconW = info.width();
            int iconH = info.height();

            try {
                BufferedImage img = ImageIO.read(new File(info.iconPath()));

                int[][] offsets = {
                        {0, 0},
                        {4, 0}, {-4, 0}, {0, 4}, {0, -4},
                        {4, 4}, {-4, -4}, {6, 0}, {0, 6}
                };

                boolean placed = false;

                for (int[] offset : offsets) {
                    int x = info.x() + offset[0];
                    int y = info.y() + offset[1];

                    Rectangle iconBox = new Rectangle(x, y, iconW, iconH);

                    int textWidth = fm.stringWidth(label);
                    int textAscent = fm.getAscent();
                    int textHeight = fm.getHeight(); // Used for the full bounding box if needed

                    int labelPadding = (int) (iconSize * 0.8);  // Use icon size divided by factor as padding (found experimentally)
                    int labelX = x + iconW / 2;
                    int labelY = y + iconH + labelPadding;

                    int labelBoxX = labelX - textWidth / 2;
                    int labelBoxY = labelY - textAscent;

                    Rectangle labelBox = new Rectangle(labelBoxX, labelBoxY, textWidth, textHeight);

                    // Overlap check
                    boolean overlapsIcon = usedIconAreas.stream().anyMatch(r -> r.intersects(iconBox));
                    boolean overlapsLabel = usedLabelAreas.stream().anyMatch(r -> r.intersects(labelBox));
                    boolean iconOverLabel = usedLabelAreas.stream().anyMatch(r -> r.intersects(iconBox));
                    boolean labelOverIcon = usedIconAreas.stream().anyMatch(r -> r.intersects(labelBox));

                    if (!overlapsIcon && !overlapsLabel && !iconOverLabel && !labelOverIcon) {
                        g.drawImage(img, x, y, iconW, iconH, null);
                        drawLabel(g, label, labelX, labelY, fm, Color.BLACK, Color.WHITE); // pass in fm for consistency
                        usedIconAreas.add(iconBox);
                        usedLabelAreas.add(labelBox);
                        placed = true;
                        break;
                    }
                }

            } catch (IOException e) {
                System.err.println("Could not load icon: " + info.iconPath());
            }
        }
    }

    /**
     * Removes suffixes (after underscore) from label names, used to clean up feature names before drawing.
     *
     * @param name raw label string
     * @return cleaned label string
     */
    private String cleanRealName(String name) {
        int idx = name.indexOf('_');
        return (idx >= 0) ? name.substring(0, idx) : name;
    }
}
package stan;

/**
 * Maps LSI class codes to icon display information for labeling map features.
 * <p>
 * This class helps determine which icon to display on the map for specific LSI class codes,
 * along with whether the icon should be labeled (have text next to it).
 */
public class LsiIconLabelMap {

    /**
     * Returns the icon display information (icon name and label flag) for a given LSI class.
     *
     * @param lsiClass the LSI classification code
     * @return the {@link IconDisplayInfo} if the class is mapped, or {@code null} otherwise
     */
    public static IconDisplayInfo getIconDisplayInfo(int lsiClass) {
        if (lsiClass == 93120000) {
            return new IconDisplayInfo("fontain", true);
        } else if (lsiClass == 32115000) {
            return new IconDisplayInfo("taxi", false);
        } else if (lsiClass == 21000000) {
            return new IconDisplayInfo("park", false);
        } else if (lsiClass == 32116000 || lsiClass == 32117000) {
            return new IconDisplayInfo("parkinglot_bike", false);
        } else if (lsiClass >= 32110000 && lsiClass <= 32130000) {
            return new IconDisplayInfo("parkinglot", false);
        } else if (lsiClass >= 32140000 && lsiClass <= 32143000) {
            return new IconDisplayInfo("parking_house", false);
        } else if (lsiClass == 92330000 || lsiClass >= 31110000 && lsiClass <= 31113000) {
            return new IconDisplayInfo("park", true);
        } else if (lsiClass == 20211000) {
            return new IconDisplayInfo("university", true);  // Uni
        } else if (lsiClass == 32440000) {
            return new IconDisplayInfo("station", true);
        } else if (lsiClass >= 32410000 && lsiClass <= 32430000) {
            return new IconDisplayInfo("station", false);
        } else if (lsiClass >= 20501240 && lsiClass <= 20501247) {  // Only small restaurant subset, to many otherwise
            return new IconDisplayInfo("restaurant", true);
        } else if (lsiClass >= 20212000 && lsiClass <= 20214100) {  // schools
            return new IconDisplayInfo("school", true);
        } else if (lsiClass == 92100000 ) {  // schools
            return new IconDisplayInfo("construction", false);
        } else if (lsiClass >= 20700000 && lsiClass <= 20757000) {  // medical stuff
            return new IconDisplayInfo("hospital2", true);
        } else if (lsiClass == 31418000 || lsiClass == 31426000 || lsiClass == 31426100) {  // minigolf or other golf
            return new IconDisplayInfo("golf", true);
        } else if (lsiClass >= 20830000 && lsiClass <= 20836000) {  // church stuff
            return new IconDisplayInfo("church", true);
        } else if (lsiClass == 20516110) {
            return new IconDisplayInfo("hotel", true);
        } else if (lsiClass == 20220000) {
            return new IconDisplayInfo("library", true);
        } else if (lsiClass == 20230000) {
            return new IconDisplayInfo("research", true); // Research
        } else if (lsiClass >= 20240000 && lsiClass <= 20242000) {
            return new IconDisplayInfo("planet", true); // Astronomie
        } else if (lsiClass == 31404000) {
            return new IconDisplayInfo("tennis", false); // Tennis
        } else if (lsiClass == 20514200 || lsiClass == 20514300 || lsiClass == 20514100) {
            return new IconDisplayInfo("nightlife", true); // Fun stuff
        } else if (lsiClass >= 20920000 && lsiClass <= 20922000) {
            return new IconDisplayInfo("museum", true); // Museum stuff
        } else if (lsiClass == 93310000) {
            return new IconDisplayInfo("castle", true); // Museum stuff
        }
        return null;
    }
}


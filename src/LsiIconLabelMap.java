public class LsiIconLabelMap {

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
        }
        return null;
    }
}


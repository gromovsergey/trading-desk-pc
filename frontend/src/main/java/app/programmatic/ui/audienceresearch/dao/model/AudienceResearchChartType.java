package app.programmatic.ui.audienceresearch.dao.model;

public enum AudienceResearchChartType {
    BAR_VERTICAL('V'),
    BAR_HORIZONTAL('H'),
    DONUT('D'),
    GOOGLE_GEOCHART('G');

    private final char letter;

    private AudienceResearchChartType(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public static AudienceResearchChartType valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'V':
                return BAR_VERTICAL;
            case 'H':
                return BAR_HORIZONTAL;
            case 'D':
                return DONUT;
            case 'G':
                return GOOGLE_GEOCHART;
            default:
                if (!Character.isLetterOrDigit(letter)) {
                    return null;
                } else {
                    throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
                }
        }
    }
}

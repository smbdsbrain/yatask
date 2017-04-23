package smbdsbrain.yatask;

/**
 * Created by Paul on 4/13/2017.
 */

public class Translation {
    private String originalText;
    private String translatedText;
    private String hintText;
    private String translateLangs;

    public Translation(String originalText, String translatedText, String hintText, String translateLangs) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.hintText = hintText;
        this.translateLangs = translateLangs;
    }

    public Translation(String originalText, String translatedText, String translateLangs) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.translateLangs = translateLangs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Translation that = (Translation) o;

        if (!originalText.equals(that.originalText)) return false;
        if (!translatedText.equals(that.translatedText)) return false;
        return translateLangs.equals(that.translateLangs);

    }

    @Override
    public int hashCode() {
        int result = 1299689 * originalText.hashCode();
        result = 1299689 * result + translatedText.hashCode();
        result = 1299689 * result + translateLangs.hashCode();
        return result;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getHintText() {
        return hintText;
    }

    public void setHintText(String hintText) {
        this.hintText = hintText;
    }

    public String getTranslateLangs() {
        return translateLangs;
    }

    public void setTranslateLangs(String translateLangs) {
        this.translateLangs = translateLangs;
    }
}

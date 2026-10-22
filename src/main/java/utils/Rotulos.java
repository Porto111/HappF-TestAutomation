package utils;

import org.openqa.selenium.By;

/** Builders XPath para elementos Flutter expostos por content-desc/text. */
public final class Rotulos {
    private Rotulos() {}

    public static By contem(String... rotulos) {
        StringBuilder xpath = new StringBuilder("//*[" );
        for (int i = 0; i < rotulos.length; i++) {
            if (i > 0) xpath.append(" or ");
            String literal = xpathLiteral(rotulos[i]);
            xpath.append("contains(@content-desc,").append(literal).append(")")
                    .append(" or contains(@text,").append(literal).append(")");
        }
        return By.xpath(xpath.append("]").toString());
    }

    public static By comecaCom(String rotulo) {
        String literal = xpathLiteral(rotulo);
        return By.xpath("//*[starts-with(@content-desc," + literal + ") or starts-with(@text," + literal + ")]");
    }

    public static By aba(String rotulo) {
        String literal = xpathLiteral(rotulo);
        return By.xpath("//*[(contains(@content-desc," + literal + ") and contains(@content-desc,'Tab'))"
                + " or @content-desc=" + literal + "]");
    }

    public static String xpathLiteral(String value) {
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        String[] parts = value.split("'", -1);
        StringBuilder result = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) result.append(", \"'\", ");
            result.append("'").append(parts[i]).append("'");
        }
        return result.append(")").toString();
    }
}

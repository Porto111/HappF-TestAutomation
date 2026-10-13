package utils;

import org.openqa.selenium.By;

/** Builders de XPath para o Flutter, que expõe rótulos em content-desc (e às vezes em text). */
public final class Rotulos {

    private Rotulos() {
    }

    /** Qualquer elemento cujo content-desc/text CONTENHA um dos rótulos. */
    public static By contem(String... rotulos) {
        StringBuilder sb = new StringBuilder("//*[");
        for (int i = 0; i < rotulos.length; i++) {
            if (i > 0) {
                sb.append(" or ");
            }
            sb.append("contains(@content-desc,'").append(rotulos[i]).append("')")
              .append(" or contains(@text,'").append(rotulos[i]).append("')");
        }
        return By.xpath(sb.append("]").toString());
    }

    /** Elemento cujo content-desc COMEÇA com o rótulo (evita textos longos que só o citam). */
    public static By comecaCom(String rotulo) {
        return By.xpath("//*[starts-with(@content-desc,'" + rotulo + "') or starts-with(@text,'" + rotulo + "')]");
    }

    /** Aba da barra de navegação inferior (Flutter anexa "Tab n of m" ao rótulo). */
    public static By aba(String rotulo) {
        return By.xpath("//*[(contains(@content-desc,'" + rotulo + "') and contains(@content-desc,'Tab'))"
                + " or @content-desc='" + rotulo + "']");
    }
}

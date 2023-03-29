package net.sourceforge.plantuml.servlet.utility;

public final class HtmlUtils {
    private HtmlUtils() {
    }

    public static String htmlEscape(String string) {
        final StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        final int length = string.length();
        for (int offset = 0; offset < length;) {
            final int c = string.codePointAt(offset);
            if (c == ' ') {
                sb.append(' ');
            } else if (c == '"') {
                sb.append("&quot;");
            } else if (c == '&') {
                sb.append("&amp;");
            } else if (c == '<') {
                sb.append("&lt;");
            } else if (c == '>') {
                sb.append("&gt;");
            } else if (c == '\r') {
                sb.append("\r");
            } else if (c == '\n') {
                sb.append("\n");
            } else {
                int ci = 0xffffff & c;
                if (ci < 160) {
                    // nothing special only 7 Bit
                    sb.append((char) c);
                } else {
                    // Not 7 Bit use the unicode system
                    sb.append("&#");
                    sb.append(ci);
                    sb.append(';');
                }
            }
            offset += Character.charCount(c);
        }
        return sb.toString();
    }
}

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.simpleframework.http.Response;

import java.io.PrintStream;

public class HUtils
{
    public static String htmlEncode(final String string)
    {
        if (string == null)
        {
            return "";
        }

        final StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < string.length(); i++)
        {
            final Character character = string.charAt(i);
            if (CharUtils.isAscii(character))
            {
                // Encode common HTML equivalent characters
                stringBuffer.append(StringEscapeUtils.escapeHtml(character.toString()));
            }
            else
            {
                // Why isn't this done in escapeHtml4()?
                stringBuffer.append(
                        String.format("&#x%x;",
                                Character.codePointAt(string, i))
                );
            }
        }
        return stringBuffer.toString();
    }

    public static String htmlEncodeHtml(final String string)
    {
        String s = htmlEncode(string);
        s = s.replaceAll("&lt;", "<");
        s = s.replaceAll("&gt;", ">");
        return s;
    }

    static void generateResponseHeader(Response response)
    {
        // preparing for response
        long time = System.currentTimeMillis();

        // Setting response headers
        response.setValue("Content-Type", "text/html; charset=utf-8");
        response.setValue("Server", "GateServlet/1.0 (Simple 4.0)");
        response.setDate("Date", time);
        response.setDate("Last-Modified", time);
        response.setValue("Access-Control-Allow-Origin", "*");
/*
        response.setValue("Access-Control-Allow-Headers", "X-Requested-With");
        response.setValue("Access-Control-Allow-Headers", "Content-Type");
        response.setValue("Access-Control-Allow-Methods", "PUT, GET, POST, DELETE, OPTIONS");*/
    }

    static void generateHtmlFooter(PrintStream body)
    {
        body.println("</body>");
        body.println("</html>");
    }

    static void generateHtmlHeader(PrintStream body)
    {
        body.println("<!DOCTYPE html>");
        body.println("<html>");
        body.println("<head>" +
                        "<meta charset=\"utf-8\" />" +
                        "<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"NO-CACHE\">" +
                        "<META HTTP-EQUIV=\"EXPIRES\" CONTENT=\"0\"></head>");
        body.println("<style>" +
                "html {\n" +
                "    font-size: 20px;\n" +
                "}\n" +
                ".unbehaved-element {\n" +
                "    font-size: 2em;\n" +
                "}\n" +
                "input {\n" +
                "  font-size: 20px;\n" +
                "}\n" +
                "@media(max-width: 980px) {\n" +
                "  html {\n" +
                "    font-size: 24px;\n" +
                "  }\n" +
                "}\n" +
                "  input {\n" +
                "    font-size: 36px;\n" +
                "  }\n" +
                ".CSSTableGenerator {\n" +
                "\tmargin:0px;padding:0px;\n" +
                "\twidth:100%;\n" +
                "\tbox-shadow: 10px 10px 5px #888888;\n" +
                "\tborder:1px solid #000000;\n" +
                "\t\n" +
                "\t-moz-border-radius-bottomleft:0px;\n" +
                "\t-webkit-border-bottom-left-radius:0px;\n" +
                "\tborder-bottom-left-radius:0px;\n" +
                "\t\n" +
                "\t-moz-border-radius-bottomright:0px;\n" +
                "\t-webkit-border-bottom-right-radius:0px;\n" +
                "\tborder-bottom-right-radius:0px;\n" +
                "\t\n" +
                "\t-moz-border-radius-topright:0px;\n" +
                "\t-webkit-border-top-right-radius:0px;\n" +
                "\tborder-top-right-radius:0px;\n" +
                "\t\n" +
                "\t-moz-border-radius-topleft:0px;\n" +
                "\t-webkit-border-top-left-radius:0px;\n" +
                "\tborder-top-left-radius:0px;\n" +
                "}.CSSTableGenerator table{\n" +
                "    border-collapse: collapse;\n" +
                "        border-spacing: 0;\n" +
                "\twidth:100%;\n" +
                "\theight:100%;\n" +
                "\tmargin:0px;padding:0px;\n" +
                "}.CSSTableGenerator tr:last-child td:last-child {\n" +
                "\t-moz-border-radius-bottomright:0px;\n" +
                "\t-webkit-border-bottom-right-radius:0px;\n" +
                "\tborder-bottom-right-radius:0px;\n" +
                "}\n" +
                ".CSSTableGenerator table tr:first-child td:first-child {\n" +
                "\t-moz-border-radius-topleft:0px;\n" +
                "\t-webkit-border-top-left-radius:0px;\n" +
                "\tborder-top-left-radius:0px;\n" +
                "}\n" +
                ".CSSTableGenerator table tr:first-child td:last-child {\n" +
                "\t-moz-border-radius-topright:0px;\n" +
                "\t-webkit-border-top-right-radius:0px;\n" +
                "\tborder-top-right-radius:0px;\n" +
                "}.CSSTableGenerator tr:last-child td:first-child{\n" +
                "\t-moz-border-radius-bottomleft:0px;\n" +
                "\t-webkit-border-bottom-left-radius:0px;\n" +
                "\tborder-bottom-left-radius:0px;\n" +
                "}.CSSTableGenerator tr:hover td{\n" +
                "\t\n" +
                "}\n" +
                ".CSSTableGenerator tr:nth-child(odd){ background-color:#ffaa56; }\n" +
                ".CSSTableGenerator tr:nth-child(even)    { background-color:#ffffff; }.CSSTableGenerator td{\n" +
                "\tvertical-align:middle;\n" +
                "\t\n" +
                "\t\n" +
                "\tborder:1px solid #000000;\n" +
                "\tborder-width:0px 1px 1px 0px;\n" +
                "\ttext-align:left;\n" +
                "\tpadding:7px;\n" +
                "\tfont-size:24px;\n" +
                "\tfont-family:Arial;\n" +
                "\tfont-weight:bold;\n" +
                "\tcolor:#000000;\n" +
                "}.CSSTableGenerator tr:last-child td{\n" +
                "\tborder-width:0px 1px 0px 0px;\n" +
                "}.CSSTableGenerator tr td:last-child{\n" +
                "\tborder-width:0px 0px 1px 0px;\n" +
                "}.CSSTableGenerator tr:last-child td:last-child{\n" +
                "\tborder-width:0px 0px 0px 0px;\n" +
                "}\n" +
                ".CSSTableGenerator tr:first-child td{\n" +
                "\t\tbackground:-o-linear-gradient(bottom, #ff7f00 5%, #bf5f00 100%);\tbackground:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ff7f00), color-stop(1, #bf5f00) );\n" +
                "\tbackground:-moz-linear-gradient( center top, #ff7f00 5%, #bf5f00 100% );\n" +
                "\tfilter:progid:DXImageTransform.Microsoft.gradient(startColorstr=\"#ff7f00\", endColorstr=\"#bf5f00\");\tbackground: -o-linear-gradient(top,#ff7f00,bf5f00);\n" +
                "\n" +
                "\tbackground-color:#ff7f00;\n" +
                "\tborder:0px solid #000000;\n" +
                "\ttext-align:center;\n" +
                "\tborder-width:0px 0px 1px 1px;\n" +
                "\tfont-size:24px;\n" +
                "\tfont-family:Arial;\n" +
                "\tfont-weight:bold;\n" +
                "\tcolor:#ffffff;\n" +
                "}\n" +
                ".CSSTableGenerator tr:first-child:hover td{\n" +
                "\tbackground:-o-linear-gradient(bottom, #ff7f00 5%, #bf5f00 100%);\tbackground:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ff7f00), color-stop(1, #bf5f00) );\n" +
                "\tbackground:-moz-linear-gradient( center top, #ff7f00 5%, #bf5f00 100% );\n" +
                "\tfilter:progid:DXImageTransform.Microsoft.gradient(startColorstr=\"#ff7f00\", endColorstr=\"#bf5f00\");\tbackground: -o-linear-gradient(top,#ff7f00,bf5f00);\n" +
                "\n" +
                "\tbackground-color:#ff7f00;\n" +
                "}\n" +
                ".CSSTableGenerator tr:first-child td:first-child{\n" +
                "\tborder-width:0px 0px 1px 0px;\n" +
                "}\n" +
                ".CSSTableGenerator tr:first-child td:last-child{\n" +
                "\tborder-width:0px 0px 1px 1px;\n" +
                "}" +
                "}</style>");
        body.println("<body dir=RTL>");
        body.println("<h1>Midburn Gate</h1>");
    }

    static String safeInput(String input)
    {
        if (input == null)
        {
            return "";
        }
        return input.replaceAll("[^\\p{L}\\p{Nd} \\.@\\-_]", "").trim();
    }
}

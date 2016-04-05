/**
 * Created by Roy on 02/05/2014.
 */
public class Ticket
{
    public static boolean validateTicketBarcode(String barcode)
    {
        return !(barcode == null || barcode.length() != 32 || !barcode.matches("[0-9a-f]+"));
    }

    public static String getTicketType(int type)
    {
        switch (type)
        {
            case 1:
            case 6:
            case 9:
                return "מבוגר";
            case 2:
            case 8:
            case 11:
                return "ילד עד 14";
            case 3:
            case 7:
            case 10:
                return "נוער";
        }
        return "לא ידוע";
    }

}

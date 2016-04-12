import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Administration
{
    public static void loadFromFile(String fileName)
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection connection = DriverManager.getConnection(Main.DB_CONNECTION_STRING, "midburn", "midburn");

            int counter = 0;

            File file = new File(fileName);
            CSVParser parser = CSVParser.parse(file, StandardCharsets.UTF_8, CSVFormat.EXCEL.withHeader());
            for (CSVRecord csvRecord : parser)
            {
                if (csvRecord.get("Ticket State").equals("Completed"))
                {
                    int order_number = -1;
                    try {
                        order_number = Integer.parseInt(csvRecord.get("Order"));
                    } catch (NumberFormatException ignored) {}

                    String query = "INSERT INTO tickets " +
                            "(ticket_id, order_number, mail, Name, barcode, ticket_type, buyer_mail, document_id, early_arrival) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, Integer.parseInt(csvRecord.get("Ticket #")));
                    statement.setInt(2, order_number);
                    statement.setString(3, csvRecord.get("E-mail"));
                    statement.setString(4, csvRecord.get("Name"));
                    statement.setString(5, csvRecord.get("ticket barcode"));
                    statement.setString(6, csvRecord.get("Ticket Type").replaceAll("\\(.+\\)", ""));
                    statement.setString(7, csvRecord.get("Buyers E-mail"));
                    statement.setString(8, csvRecord.get("Docment id"));
                    statement.setBoolean(9, (csvRecord.get("Arrive early").equals("1")));

                    statement.execute();
                    statement.close();
                }

                if (counter ++ % 50 == 0)
                {
                    System.out.print(".");
                }
            }

            connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}

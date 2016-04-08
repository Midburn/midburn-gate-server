import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;

public class OfficeServlet implements Container
{
    public void handle(Request request, Response response)
    {
        try
        {
            Query query = request.getQuery();

            String action = "menu";
            if (query.get("action") != null)
            {
                action = query.get("action");
            }

            // Connect to the DB
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection connection = DriverManager.getConnection(Main.DB_CONNECTION_STRING, "midburn", "midburn");

            Statement statement = connection.createStatement();

            PrintStream body = response.getPrintStream();

            if (action.equals("menu"))
            {
                // Generating HTML
                HUtils.generateResponseHeader(response);
                HUtils.generateHtmlHeader(body);

                body.println("<form action = '/' >");
                body.println("<h2>" + HUtils.htmlEncode("חיפוש לפי שם / אימייל") + "</h2>");
                body.println("<input name = 'search_string' />");
                body.println("<input type = 'hidden' name = 'action' value = 'search'/>");
                body.println("<p></p>");

                body.println("<h2>" + HUtils.htmlEncode("חיפוש לפי מספר כרטיס") + "</h2>");
                body.println("<input name = 'ticket' autofocus/>");
                body.println("<p></p>");

                body.println("<input type = 'submit' value = '" + HUtils.htmlEncode("חיפוש כרטיס") + "'/>");
                body.println("</form>");

                body.println("<p>&nbsp;</p><hr>");
                String sql = "select count(*) as cnt from tickets where Entrance_Date is not null and ticket_id > 0";
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next())
                {
                    int count = resultSet.getInt("cnt");
                    body.println("<h2>" + HUtils.htmlEncode("מספר הברנרים שנכנסו לעיר: ") + count + " </h2>");
                    body.println("<a href='/?action=statistics'>" + HUtils.htmlEncode("סטטיסטיקות לפי משמרת") + "</a>");
                    body.println("<a href='/'>Refresh</a>");
                }


                HUtils.generateHtmlFooter(body);

                // Sending response
                body.close();
            }
            else if (action.equals("update_ticket_1"))
            {
                String ticketId = HUtils.safeInput(query.get("ticket"));
                String orderId = HUtils.safeInput(query.get("order"));

                try
                {
                    ticketId = String.valueOf(Integer.parseInt(ticketId));
                    orderId = String.valueOf(Integer.parseInt(orderId));
                }
                catch (Exception e)
                {
                    ticketId = "-1";
                    orderId = "-1";
                }

                // Query the DB
                ResultSet resultSet = statement.executeQuery("select * from tickets where ticket_id = " + ticketId + " and order_number = " + orderId);

                String name = "";
                String ticket_type = "";
                String mail = "";
                String document_id = "";
                Date entrance_date = null;
                String message = null;
                int orderNumber = 0;
                boolean early_arrival = false;

                boolean ticketOK = false;

                // Get data from the query resultset
                if (resultSet.next())
                {
                    name = resultSet.getString("Name");
                    ticket_type = resultSet.getString("ticket_type");
                    mail = resultSet.getString("mail");
                    document_id = resultSet.getString("document_id");
                    entrance_date = resultSet.getDate("Entrance_Date");
                    orderNumber = resultSet.getInt("order_number");
                    early_arrival = resultSet.getBoolean("early_arrival");

                    ticketOK = true;
                }

                if (entrance_date != null)
                {
                    ticketOK = false;
                    message = "לא ניתן לעדכן כרטיס שכבר נכנס לעיר";
                }

                resultSet.close();

                // Generating HTML
                HUtils.generateResponseHeader(response);
                HUtils.generateHtmlHeader(body);

                if (ticketOK)
                {
                    body.println("<form action = '/' >");
                    body.println("<h1>" + HUtils.htmlEncode("מספר כרטיס: ") + ticketId + "</h1>");
                    body.println("<h2>" + HUtils.htmlEncode("מספר הזמנה: ") + orderNumber + "</h2>");
                    body.println("<h2>" + HUtils.htmlEncode("סוג כרטיס: " + ticket_type) + "</h2>");
                    body.println("<input type = 'hidden' name = 'id' value = '" + ticketId + "'/>");
                    body.println("<input type = 'hidden' name = 'action' value = 'update_ticket_2'/>");
                    body.println("<h2>" + HUtils.htmlEncode("שם משתתף: ") + "</h2><input autofocus name = 'name' value = '" + HUtils.htmlEncode(name) + "' /><br/>");
                    body.println("<h2>" + HUtils.htmlEncode("אימייל משתתף: ") + "</h2><input name = 'mail' value = '" + HUtils.htmlEncode(mail) + "' /><br/>");
                    body.println("<h2>" + HUtils.htmlEncode("ת.ז. משתתף: ") + "</h2><input name = 'document_id' value = '" + HUtils.htmlEncode(document_id) + "' /><br/>");
                    body.println("<br/><input type = 'checkbox' name = 'early_arrival' " + (early_arrival ? "checked" : "") + ">" + HUtils.htmlEncode("כניסה מוקדמת? ") + "</input><br/>");
                    body.println("<br/>");
                    body.println("<input type = 'submit' value = '" + HUtils.htmlEncode("עדכון פרטי כרטיס") + "'/>");
                    body.println("</form>");
                    body.println("<h2><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h2>");
                }
                else
                {
                    if (message != null)
                    {
                        body.println("<h2>" + HUtils.htmlEncode(message) + "<h2>");
                    }
                    else
                    {
                        body.println("<h2>" + HUtils.htmlEncode("מספר כרטיס שגוי!") + "<h2>");
                    }
                    body.println("<h2><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h2>");
                }

                HUtils.generateHtmlFooter(body);

                // Sending response
                body.close();

            }
            else if (action.equals("update_ticket_2"))
            {
                String ticketId = HUtils.safeInput(query.get("id"));
                String name = HUtils.safeInput(query.get("name"));
                String mail = HUtils.safeInput(query.get("mail"));
                String document_id = HUtils.safeInput(query.get("document_id"));
                String early_arrival = HUtils.safeInput(query.get("early_arrival"));

                statement.executeUpdate("insert into tickets_log select now(), tickets.* from tickets where ticket_id = " + ticketId);

                statement.executeUpdate("update tickets set Name = '" + name +
                        "', mail = '" + mail + "', " +
                        (early_arrival.equals("on") ? "early_arrival = 1" : "early_arrival = null") + ", " +
                        "document_id = '" + document_id + "'" +
                        " where ticket_id = " + ticketId);
                HUtils.generateResponseHeader(response);
                HUtils.generateHtmlHeader(body);
                body.println("<h2>" + HUtils.htmlEncode("הכרטיס עודכן") + "</h2>");
                body.println("<h2><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h2>");
                body.println("<script>setTimeout(function() {window.location.assign('/');}, 1500);</script>");
                HUtils.generateHtmlFooter(body);
                body.close();
            }
            else if (action.equals("pre_mark_entered"))
            {
                String ticketId = HUtils.safeInput(query.get("ticket_id"));

                String sql = "select * from tickets where ticket_id='" + ticketId + "'";
                ResultSet resultSet = statement.executeQuery(sql);
                String name = "";
                String document_id = "";
                if (resultSet.next()){
                    name = resultSet.getString("Name");
                    document_id = resultSet.getString("document_id");
                }


                HUtils.generateResponseHeader(response);
                HUtils.generateHtmlHeader(body);
                body.println("<h2>" + HUtils.htmlEncode(name) + "</h2>");
                body.println("<h2>" + HUtils.htmlEncode("ת.ז: " + document_id) + "</h2>");
                body.println("<p>&nbsp;</p>");
                body.println("<h3>" + HUtils.htmlEncode("חובה לבדוק תעודת זהות!") + "</h3>");

                //mark entered button here
                body.println("<form action = '/' >");
                body.println("<input type = 'hidden' name = 'ticket_id' value = '" + ticketId + "'/>");
                body.println("<input type = 'hidden' name = 'action' value = 'mark_entered'/>");
                body.println("<br/>");
                body.println("<input type = 'submit' value = '" + HUtils.htmlEncode("אשר כניסה") + "'/>");
                body.println("<a href='/'><input type = 'button' value = '" + HUtils.htmlEncode("ביטול") + "'/></a>");
                body.println("</form>");

                HUtils.generateHtmlFooter(body);
                body.close();
            }
            else if (action.equals("mark_entered"))
            {
                String ticketId = HUtils.safeInput(query.get("ticket_id"));

                statement.executeUpdate("insert into tickets_log select now(), tickets.* from tickets where ticket_id = " + ticketId);
                statement.executeUpdate(
                        "update tickets set Entrance_Date = Now() " +
                                "where ticket_id = " + ticketId);

                HUtils.generateResponseHeader(response);
                HUtils.generateHtmlHeader(body);
                body.println("<h2>" + HUtils.htmlEncode("אושר!") + "</h2>");
                body.println("<h2><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h2>");
                body.println("<script>setTimeout(function() {window.location.assign('/');}, 5000);</script>");

                HUtils.generateHtmlFooter(body);
                body.close();
            }
            else if (action.equals("search"))
            {
                HUtils.generateResponseHeader(response);
                HUtils.generateHtmlHeader(body);
                body.println("<h3><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h3>");
                body.println(HUtils.htmlEncodeHtml("<h2>יש לוודא התאמה מול תעודה מזהה!</h2>"));
                body.println(HUtils.htmlEncodeHtml("<div class='CSSTableGenerator'><table dir='rtl' border=1><tr><td>עריכה</td><td>מספר הזמנה</td><td>מספר כרטיס</td><td>סוג כרטיס</td><td>שם</td><td>אימייל</td><td>ת.ז.</td><td>תאריך כניסה</td><td>הגעה מוקדמת</td><td>אשר כניסה</td></tr>"));
                String searchString = HUtils.safeInput(query.get("search_string"));
                String ticket = HUtils.safeInput(query.get("ticket"));
                String sql;
                if (ticket != null && !ticket.isEmpty()) {
                    ticket = ticket.replaceAll("[^0-9]", "");
                    sql = "select * from tickets where ticket_id='" + ticket + "'";
                }
                else if (searchString != null && !searchString.isEmpty())
                {
                    sql = "select * from tickets where Name like '?' or mail like '?' order by name";
                    sql = sql.replaceAll("\\?", "%" + searchString + "%");
                }
                else
                {
                    sql = "select * from tickets where false";
                }
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next())
                {
                    String entrance_date = "";
                    Time entrance_time = resultSet.getTime("Entrance_Date");
                    Date entrance_date_obj = resultSet.getDate("Entrance_Date");
                    if (entrance_date_obj != null && entrance_time != null)
                    {
                        entrance_date = entrance_time.toString() + " " + entrance_date_obj.toString();
                    }


                    body.println("<tr><td>" +
                                    (entrance_date.isEmpty()
                                            ?
                                            "<a href=/?action=update_ticket_1&ticket=" +
                                                    resultSet.getInt("ticket_id") + "&order=" +
                                                    resultSet.getInt("order_number") + ">" + HUtils.htmlEncode("עריכה") + "</a>"
                                            : "") +
                                            "</td><td>" +
                            resultSet.getInt("ticket_id") + "</td><td>" +
                            resultSet.getInt("order_number") + "</td><td>" +
                            HUtils.htmlEncode(resultSet.getString("ticket_type")) + "</td><td>" +
                            HUtils.htmlEncode(resultSet.getString("Name")) + "</td><td>" +
                            "<a href='/?action=search&search_string=" + resultSet.getString("mail") + "'>" + resultSet.getString("mail") + "</a></td><td>" +
                            resultSet.getString("document_id") + "</td><td>" +
                            entrance_date + "</td><td>" +
                            HUtils.htmlEncode(resultSet.getBoolean("early_arrival") ? "יש אישור" : "") + "</td><td>" +
                            (entrance_date.isEmpty()
                                    ?
                                    "<a href=/?action=pre_mark_entered&ticket_id=" +
                                            resultSet.getInt("ticket_id") + ">" + HUtils.htmlEncode("אשר") + "</a>"
                                    : "") +
                            "</td></tr>"
                    );
                }
                body.println("</table></div>");
                body.println("<h3><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h3>");
                HUtils.generateHtmlFooter(body);
                body.close();
            }
            else if (action.equals("statistics")) {
                String sql = "select s.start_date, s.end_date, s.gater, s.ip, count(*) as cnt " +
                             "from tickets t join shifts s on s.shift_id = t.shift_id " +
                             "group by s.start_date, s.end_date, s.gater, s.ip " +
                             "order by s.shift_id desc";
                ResultSet resultSet = statement.executeQuery(sql);

                HUtils.generateResponseHeader(response);
                HUtils.generateHtmlHeader(body);

                body.println("<h3><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h3>");
                body.println(HUtils.htmlEncodeHtml("<div class='CSSTableGenerator'><table dir='rtl' border=1><tr><td>תאריך התחלה</td><td>תאריך סיום</td><td>מפעיל עמדה</td><td>IP</td><td>מספר כרטיסים</td></tr>"));
                while (resultSet.next())
                {
                    body.println("<tr>" +
                            "<td>" + resultSet.getString("start_date") + "</td>" +
                            "<td>" + (resultSet.getString("end_date") != null ? resultSet.getString("end_date"): HUtils.htmlEncode("משמרת פתוחה")) + "</td>" +
                            "<td>" + HUtils.htmlEncode(resultSet.getString("gater")) + "</td>" +
                            "<td>" + resultSet.getString("ip") + "</td>" +
                            "<td>" + resultSet.getString("cnt") + "</td>" +
                            "</tr>");
                }
                body.println("</table></div>");
                body.println("<h3><a href='/'>" + HUtils.htmlEncode("חזרה לתפריט") + "</a></h3>");
                HUtils.generateHtmlFooter(body);
                resultSet.close();
                body.close();
            }


            statement.close();
            connection.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            try
            {
                response.getPrintStream().println(e);
                response.getPrintStream().close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
    }

}

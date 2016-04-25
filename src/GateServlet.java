import org.json.simple.JSONObject;
import org.simpleframework.http.Query;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.*;

@SuppressWarnings("unchecked")
public class GateServlet implements Container
{
    public void handle(Request request, Response response)
    {
        try
        {
            boolean ticketOK = false;
            String message;

            // Get ticket ID from URL
            Query query = request.getQuery();

            String action = "entrance";
            String act = HUtils.safeInput(query.get("action"));
            if (!act.isEmpty())
            {
                action = act;
            }

            // Connect to the DB
            Class.forName("com.mysql.jdbc.Driver");
            java.sql.Connection connection = DriverManager.getConnection(Main.DB_CONNECTION_STRING, "midburn", "midburn");

            Statement statement = connection.createStatement();
            PrintStream body = response.getPrintStream();

            String clientAddress = request.getClientAddress().getHostString();
            if (action.equals("login"))
            {
                LoginHandler(response, query, statement, body, clientAddress);
            }
            else if (action.equals("logout"))
            {
                LogoutHandler(response, statement, body, clientAddress);
            }

            int shiftId = 0;
            ResultSet set = statement.executeQuery("SELECT * from shifts WHERE ip = '" + clientAddress + "' and end_date is NULL");
            if (set.next())
            {
                shiftId = set.getInt("shift_id");
            }

            //TODO cancelled shift for decompression party.
            if (true /*shiftId != 0*/) {
                if (action.contains("entrance")) {
                    String ticketBarcode = "";
                    // Query the DB
                    ResultSet resultSet = null;
                    if (action.equals("entrance")) {
                        ticketBarcode = HUtils.safeInput(query.get("id"));
                        if (ticketBarcode.length() == 0) {
                            ticketBarcode = "0";
                        }
                        resultSet = statement.executeQuery("select * from tickets where barcode = '" + ticketBarcode + "'");
                    } else if (action.equals("manual_entrance")) {
                        String orderNumber = HUtils.safeInput(query.get("order"));
                        String ticketNumber = HUtils.safeInput(query.get("ticket"));
                        String sql = "select * from tickets where order_number = '" + orderNumber +
                                     "' and ticket_id = '" + ticketNumber + "'";
                        resultSet = statement.executeQuery(sql);
                    }

                    String ticket_number = "";
                    String name = "";
                    int ticket_type=0;
                    String entrance_date = "";
                    String order_number = "";
                    boolean early_arrival;
                    boolean cancelledTicket;
                    boolean disabled_parking = false;

                    if (action.equals("entrance") && !Ticket.validateTicketBarcode(ticketBarcode)) {
                        message = "ברקוד לא תקין. חשד לכרטיס מזויף!";
                    } else {
                        // Get data from the query resultset
                        assert resultSet != null;
                        if (resultSet.next()) {
                            cancelledTicket = resultSet.getBoolean("Cancelled");
                            ticketBarcode = resultSet.getString("barcode");
                            ticket_number = resultSet.getString("ticket_id");
                            name = resultSet.getString("Name");
                            order_number = resultSet.getString("order_number");
                            ticket_type = resultSet.getInt("ticket_type");
                            early_arrival = resultSet.getBoolean("early_arrival");
                            disabled_parking = resultSet.getBoolean("disabled_parking");
                            Time entrance_time = resultSet.getTime("Entrance_Date");
                            Date entrance_date_obj = resultSet.getDate("Entrance_Date");
                            if (entrance_date_obj != null && entrance_time != null) {
                                entrance_date = entrance_time.toString() + " " + entrance_date_obj.toString();
                            }

                            if (entrance_time != null && !entrance_date.isEmpty()) {
                                message = "הכרטיס כבר נקרא בעבר!";
                            } else if (cancelledTicket) {
                                message = "הכרטיס בוטל!";
                            } else if (Main.earlyArrivalMode && !early_arrival)
                            {
                                message = "אין אישור להגעה מוקדמת";
                            }
                            else {
                                ticketOK = true;
                                message = "כרטיס תקין";
                            }
                        } else {
                            message = "מספר הכרטיס אינו ידוע!";
                        }

                        resultSet.close();
                    }

                    // Color dispatching for message
                    String color = "red";
                    if (ticketOK) {
                        color = "green";
                    }

                    HUtils.generateResponseHeader(response);

                    JSONObject object = new JSONObject();
                    object.put("barcode", ticketBarcode);
                    object.put("order_number", order_number);
                    object.put("ticket_number", ticket_number);
                    object.put("name", HUtils.htmlEncode(name));
                    object.put("ticket_type", HUtils.htmlEncode(Ticket.getTicketType(ticket_type)));
                    object.put("disabled_parking", disabled_parking?HUtils.htmlEncode("יש"):HUtils.htmlEncode("אין"));
                    object.put("entrance_date", entrance_date);
                    object.put("color", color);
                    object.put("message", HUtils.htmlEncode(message));
                    body.println(object);

                    // Sending response
                    body.close();
                } else if (action.equals("save")) {
                    String ticketBarcode = HUtils.safeInput(query.get("id"));
                    if (ticketBarcode.length() == 0) {
                        ticketBarcode = "0";
                    }
                    int count;
                    if (ticketBarcode.equals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa") ||
                        ticketBarcode.equals("cccccccccccccccccccccccccccccccc")) {
                        count = 1;
                    } else {
                        String sql = "update tickets set Entrance_Date = Now(), shift_id = " + shiftId +
                                     " where barcode = '" + ticketBarcode + "'";
                        count = statement.executeUpdate(sql);
                    }

                    HUtils.generateResponseHeader(response);
                    JSONObject object = new JSONObject();
                    if (count == 1) {
                        object.put("color", "green");
                    } else {
                        object.put("color", "red");
                        object.put("message", HUtils.htmlEncode("תקלה בשמירת הכרטיס!"));
                    }
                    body.println(object);

                    // Sending response
                    body.close();
                }
                else if (action.equals("counter")) {
                    CounterHandler(response, statement, body, clientAddress, shiftId);
                }
            }
            else // No login
            {
                NoLoginHandler(response, body);
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

	private void NoLoginHandler(Response response, PrintStream body) {
		HUtils.generateResponseHeader(response);
		JSONObject object = new JSONObject();
		object.put("color", "red");
		object.put("message", HUtils.htmlEncode("העמדה נעולה, אנא פנה לאחראי המשמרת!"));
		body.println(object);
		body.close();
	}

	private void CounterHandler(Response response, Statement statement, PrintStream body, String clientAddress,
			int shiftId) throws SQLException {
		int entranceCounter = 0;
		ResultSet resultSet = statement.executeQuery("select count(*) as cnt from tickets where shift_id = " + shiftId);
		if (resultSet.next()) {
		    entranceCounter = resultSet.getInt("cnt");
		    resultSet.close();
		}

		HUtils.generateResponseHeader(response);
		JSONObject object = new JSONObject();
		object.put("entrance_counter", entranceCounter);
		object.put("ip", clientAddress);
		body.println(object);

		// Sending response
		body.close();
	}

	private void LogoutHandler(Response response, Statement statement, PrintStream body, String clientAddress)
			throws SQLException {
		String sql = "UPDATE shifts set end_date = now() WHERE end_date is null and ip = '" + clientAddress + "'";
		statement.execute(sql);
		HUtils.generateResponseHeader(response);
		JSONObject object = new JSONObject();
		object.put("message", HUtils.htmlEncode("העמדה נסגרה"));
		object.put("color", "green");
		body.println(object);
		body.close();
	}

	private void LoginHandler(Response response, Query query, Statement statement, PrintStream body,
			String clientAddress) throws SQLException {
		String message;
		ResultSet set = statement.executeQuery("SELECT * from shifts WHERE ip = '" + clientAddress + "' and end_date is NULL");
		String color;
		if (set.next())
		{
		    message = "העמדה פתוחה מהמשמרת הקודמת. יש לסגור לפני פתיחה מחדש.";
		    color = "red";
		}
		else {
		    String gater = HUtils.safeInput(query.get("gater"));
		    String sql = "INSERT into shifts(start_date, end_date, ip, gater) " +
		            "VALUES (now(), null, '" + clientAddress + "', '" + gater + "')";
		    statement.execute(sql);
		    message = "העמדה נפתחה";
		    color = "green";
		}

		HUtils.generateResponseHeader(response);
		JSONObject object = new JSONObject();
		object.put("message", HUtils.htmlEncode(message));
		object.put("color", color);
		body.println(object);
		body.close();
	}

}
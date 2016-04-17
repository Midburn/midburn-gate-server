import org.json.simple.parser.ParseException;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.UUID;

public class Main
{
	
    public static String LOGIN_TOKEN;
    public static String DB_CONNECTION_STRING;
    public static boolean EARLY_ARRIVAL_MODE;
    public static int SERVER_PORT;
    public static int CLIENT_PORT;

    public static String CONFIG_FILE = "config.txt";

    public static boolean earlyArrivalMode = false;

    public static void main(String[] args) throws Exception
    {    	
    	try
    	{
            loadParams();

            if (args.length > 1) {
                if (args[0].equals("load")) {
                    Administration.loadFromFile(args[1]);
                    System.exit(0);
                }
            }
            else if (args.length == 1) {
                if (args[0].equals("early")) {
                    earlyArrivalMode = true;
                }
            }
    	}
    	catch (Exception e)
    	{
    		usageMSG();
    		System.exit(0);
    	}
        
        
    	LOGIN_TOKEN = UUID.randomUUID().toString();
        Container container = new GateServlet();
                
        Server server = new ContainerServer(container);
        Connection connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(CLIENT_PORT);

        connection.connect(address);

        container = new OfficeServlet();
        server = new ContainerServer(container);
        connection = new SocketConnection(server);
        address = new InetSocketAddress(SERVER_PORT);

        connection.connect(address);

    }
    
    public static void usageMSG()
    {
    	System.out.println("usage\toption 1:java -jar MidburnGate.jar load [csv file path]");
    }
    
    public static void loadParams() throws IOException, ParseException
    {
    	Configuration config = new Configuration(CONFIG_FILE);
    	
        EARLY_ARRIVAL_MODE = Boolean.parseBoolean(config.getAttribute("early_arrival_mode"));
        DB_CONNECTION_STRING = config.getAttribute("db_connection_string");
        SERVER_PORT = Integer.parseInt(config.getAttribute("server_port"));
        CLIENT_PORT = Integer.parseInt(config.getAttribute("client_port"));   
        OfficeServlet.USERNAME = config.getAttribute("admin_username");
        OfficeServlet.PASSWORD = config.getAttribute("admin_password");
        		
    }
}

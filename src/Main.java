import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Main
{
    public static String DB_CONNECTION_STRING = "jdbc:mysql://localhost/midburn?useUnicode=true&characterEncoding=utf8";
    public static boolean earlyArrivalMode = false;

    public static void main(String[] args) throws Exception
    {
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

        Container container = new GateServlet();
        Server server = new ContainerServer(container);
        Connection connection = new SocketConnection(server);
        SocketAddress address = new InetSocketAddress(8080);

        connection.connect(address);

        container = new OfficeServlet();
        server = new ContainerServer(container);
        connection = new SocketConnection(server);
        address = new InetSocketAddress(9090);

        connection.connect(address);

    }
}

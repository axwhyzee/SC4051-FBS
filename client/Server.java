import middleware.network.RUDP;
import middleware.protos.*;


public class Server {
    public static void main(String args[]) {
        // configure RUDP client
        RUDP rudp = new RUDP();
        int server_port = 5432;
        TestService service = new ConcreteTestService();
        TestServiceServicer servicer = new TestServiceServicer(service);
        rudp.listen(server_port, servicer);
    }
}

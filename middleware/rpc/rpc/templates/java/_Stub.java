import java.net.InetAddress;
import middleware.network.RUDP;
import middleware.network.Bytes;


public class {__SERVICE_NAME__}Stub {
	private final InetAddress server_addr;
	private final int server_port;
	private final RUDP proto;
	
	public {__SERVICE_NAME__}Stub(InetAddress server_addr, int server_port, RUDP proto) {
		this.server_addr = server_addr;
		this.server_port = server_port;
		this.proto = proto;
	}

	public Bytes _send(Bytes data) throws Exception {
		return proto.send(server_addr, server_port, data);
	}

{__STUB_METHODS__}}

import java.net.InetAddress;
import middleware.network.Protocol;
import middleware.network.Bytes;


public class {__SERVICE_NAME__}Stub {
	private final InetAddress server_addr;
	private final int server_port;
	private final Protocol proto;
	
	public {__SERVICE_NAME__}Stub(InetAddress server_addr, int server_port, Protocol proto) {
		this.server_addr = server_addr;
		this.server_port = server_port;
		this.proto = proto;
	}

	public Bytes _send(Bytes data) {
		return proto.send(server_addr, server_port, data);
	}

{__STUB_METHODS__}}

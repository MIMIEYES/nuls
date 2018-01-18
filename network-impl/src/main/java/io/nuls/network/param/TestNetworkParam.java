package io.nuls.network.param;

import io.nuls.network.NetworkContext;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.param.AbstractNetworkParam;
import io.nuls.network.filter.impl.DefaultMessageFilter;
import io.nuls.network.message.DefaultNetWorkEventHandlerFactory;

import java.net.InetSocketAddress;

/**
 * @author vivi
 * @date 2017/11/21
 */
public class TestNetworkParam extends AbstractNetworkParam {

    private static TestNetworkParam instance;

    private TestNetworkParam() {
        this.maxInCount = NetworkContext.getNetworkConfig().getPropValue(NetworkConstant.NETWORK_NODE_MAX_IN, 20);
        this.maxOutCount =  NetworkContext.getNetworkConfig().getPropValue(NetworkConstant.NETWORK_NODE_MAX_OUT, 10);
        this.port = 8002;
        this.packetMagic = 987654322;

        InetSocketAddress address1 = new InetSocketAddress("192.168.1.156", port);
        InetSocketAddress address2 = new InetSocketAddress("192.168.1.157", port);
        InetSocketAddress address3 = new InetSocketAddress("192.168.1.158", port);
        seedNodes.add(address1);
        seedNodes.add(address2);
        seedNodes.add(address3);

        this.messageFilter = DefaultMessageFilter.getInstance();
        this.messageHandlerFactory = DefaultNetWorkEventHandlerFactory.getInstance();
    }

    public static synchronized TestNetworkParam get() {
        if (instance == null) {
            instance = new TestNetworkParam();
        }
        return instance;
    }

}

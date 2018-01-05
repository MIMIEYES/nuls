package io.nuls.network.param;

import io.nuls.core.utils.cfg.ConfigLoader;
import io.nuls.network.constant.NetworkConstant;
import io.nuls.network.entity.param.AbstractNetworkParam;
import io.nuls.network.filter.impl.DefaultMessageFilter;
import io.nuls.network.message.DefaultNetWorkEventHandlerFactory;

import java.net.InetSocketAddress;

/**
 * @author vivi
 * @date 2017/11/21
 */
public class MainNetworkParam extends AbstractNetworkParam {

    private static MainNetworkParam instance;

    private MainNetworkParam() {
        super();
        this.port = ConfigLoader.getCfgValue(NetworkConstant.NETWORK_SECTION, NetworkConstant.NETWORK_SERVER_PORT, 8632);
        this.packetMagic = ConfigLoader.getCfgValue(NetworkConstant.NETWORK_SECTION, NetworkConstant.NETWORK_MAGIC, 936152748);

        InetSocketAddress address1 = new InetSocketAddress("192.168.1.156", port);
        InetSocketAddress address2 = new InetSocketAddress("192.168.1.157", port);
        InetSocketAddress address3 = new InetSocketAddress("192.168.1.158", port);
        seedPeers.add(address1);
        seedPeers.add(address2);
        seedPeers.add(address3);

        this.messageFilter = DefaultMessageFilter.getInstance();
        this.messageHandlerFactory = DefaultNetWorkEventHandlerFactory.getInstance();
    }

    public static synchronized MainNetworkParam get() {
        if (instance == null) {
            instance = new MainNetworkParam();
        }
        return instance;
    }

}

package io.nuls.rpc.service.impl;

import org.glassfish.grizzly.http.server.HttpServer;
import io.nuls.core.utils.log.Log;
import io.nuls.rpc.constant.RpcConstant;
import io.nuls.rpc.resources.impl.NulsResourceConfig;
import io.nuls.rpc.service.intf.RpcServerService;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Niels
 * @date 2017/9/25
 *
 */
public class RpcServerServiceImpl implements RpcServerService {

    private static final RpcServerServiceImpl INSTANCE = new RpcServerServiceImpl();

    public static RpcServerService getInstance() {
        return INSTANCE;
    }

    private
     HttpServer httpServer;

    @Override
    public void startServer(String ip, int port, String moduleUrl) {
        URI serverURI = UriBuilder.fromUri("http://" + ip + "/" + moduleUrl).port(port).build();
        final Map<String, Object> initParams = new HashMap<>();
        initParams.put("jersey.config.server.provider.packages", RpcConstant.PACKAGES);
        initParams.put("load-on-startup", "1");
        NulsResourceConfig rc = new NulsResourceConfig();
        rc.addProperties(initParams);
        httpServer = GrizzlyHttpServerFactory.createHttpServer(serverURI, rc);

        Log.info("http restFul server is started!url is " + serverURI.toString());
    }

    @Override
    public void shutdown() {
        httpServer.shutdown();
    }

    @Override
    public boolean isStarted() {
        if(null==this.httpServer){
            return false;
        }
        return this.httpServer.isStarted();
    }

}

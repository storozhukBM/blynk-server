package cc.blynk.server.core.hardware;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.TransportTypeHolder;
import cc.blynk.server.core.BaseServer;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.BaseSimpleChannelInboundHandler;
import cc.blynk.server.workers.notifications.NotificationsProcessor;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class HardwareServer extends BaseServer {

    private final HardwareHandlersHolder handlersHolder;
    private final ChannelInitializer<SocketChannel> channelInitializer;

    public HardwareServer(ServerProperties props, UserRegistry userRegistry, SessionsHolder sessionsHolder,
                          GlobalStats stats, NotificationsProcessor notificationsProcessor, TransportTypeHolder transportType) {
        super(props.getIntProperty("server.default.port"), transportType);

        this.handlersHolder = new HardwareHandlersHolder(props, userRegistry, sessionsHolder, notificationsProcessor);
        int hardTimeoutSecs = props.getIntProperty("hard.socket.idle.timeout", 15);
        log.debug("hard.socket.idle.timeout = {}", hardTimeoutSecs);
        this.channelInitializer = new HardwareChannelInitializer(sessionsHolder, stats, handlersHolder, hardTimeoutSecs);

        log.info("Hardware server port {}.", port);
    }

    @Override
    public BaseSimpleChannelInboundHandler[] getBaseHandlers() {
        return handlersHolder.getBaseHandlers();
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return channelInitializer;
    }

    @Override
    public void stop() {
        log.info("Shutting down default server...");
        super.stop();
    }

}

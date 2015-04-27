package cc.blynk.integration.model;

import cc.blynk.client.core.AppClient;
import cc.blynk.common.handlers.common.decoders.MessageDecoder;
import cc.blynk.common.handlers.common.encoders.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.util.Random;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public class TestAppClient extends AppClient {

    public final SimpleClientHandler responseMock = Mockito.mock(SimpleClientHandler.class);
    private int msgId = 0;

    private ChannelPipeline pipeline;
    private boolean disableAppSsl;

    public TestAppClient(String host, int port) {
        super(host, port, Mockito.mock(Random.class), false);
        Mockito.when(random.nextInt(Short.MAX_VALUE)).thenReturn(1);
        this.disableAppSsl = false;
    }

    public TestAppClient(String host, int port, boolean disableAppSsl) {
        super(host, port, Mockito.mock(Random.class), disableAppSsl);
        Mockito.when(random.nextInt(Short.MAX_VALUE)).thenReturn(1);
        this.disableAppSsl = disableAppSsl;
    }

    @Override
    public void start(BufferedReader commandInputStream) {
        if (commandInputStream == null) {
            nioEventLoopGroup = new NioEventLoopGroup();

            Bootstrap b = new Bootstrap();
            b.group(nioEventLoopGroup).channel(NioSocketChannel.class).handler(getChannelInitializer());

            try {
                // Start the connection attempt.
                this.channel = b.connect(host, port).sync().channel();
            } catch (InterruptedException e) {
                log.error(e);
            }
        } else {
            super.start(commandInputStream);
        }
    }

    @Override
    public ChannelInitializer<SocketChannel> getChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                TestAppClient.this.pipeline = pipeline;

                if (!disableAppSsl) {
                    pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                }

                pipeline.addLast(new MessageDecoder());
                pipeline.addLast(new MessageEncoder());
                pipeline.addLast(responseMock);
            }
        };
    }

    public TestAppClient send(String line) {
        send(produceMessageBaseOnUserInput(line, ++msgId));
        return this;
    }

    public TestAppClient send(String line, int id) {
        send(produceMessageBaseOnUserInput(line, id));
        return this;
    }

    public void reset() {
        Mockito.reset(responseMock);
        msgId = 0;
    }

    public void replace(SimpleClientHandler simpleClientHandler) {
        pipeline.removeLast();
        pipeline.addLast(simpleClientHandler);
    }

}

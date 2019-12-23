package com.pr.sepp.utils.jenkins.pool;

import com.pr.sepp.utils.jenkins.JenkinsClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;


@Slf4j
public class JenkinsFactory implements PooledObjectFactory<JenkinsClient> {

    private String username;
    private String password;
    private String url;
    private int requestConnTimeout;
    private int socketTimeout;
    private int connTimeout;
    private int maxConnPerRoute = 10;
    private int maxConnTotal = 20;

    JenkinsFactory(String url, String username, String password, int requestConnTimeout, int socketTimeout,
                   int connTimeout, int maxConnPerRoute, int maxConnTotal) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.requestConnTimeout = requestConnTimeout;
        this.socketTimeout = socketTimeout;
        this.maxConnPerRoute = maxConnPerRoute;
        this.connTimeout = connTimeout;
        this.maxConnTotal = maxConnTotal;
    }

    JenkinsFactory(String url, String username, String password) {
        this(url, username, password, 5_000, 10_000,
                5_000, 10, 20);
    }

    public HttpClientBuilder clientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectionRequestTimeout(requestConnTimeout);
        builder.setSocketTimeout(socketTimeout);
        builder.setConnectTimeout(connTimeout);
        httpClientBuilder.setDefaultRequestConfig(builder.build());
        httpClientBuilder.setMaxConnPerRoute(maxConnPerRoute);
        httpClientBuilder.setMaxConnTotal(maxConnTotal);
        return httpClientBuilder;
    }

    @Override
    public PooledObject<JenkinsClient> makeObject() throws Exception {
        final JenkinsClient jenkinsClient = new JenkinsClient(url, username, password, clientBuilder());
        try {
            jenkinsClient.isRunning();
        } catch (Exception e) {
            jenkinsClient.close();
            throw e;
        }
        return new DefaultPooledObject<>(jenkinsClient);
    }

    @Override
    public void destroyObject(PooledObject<JenkinsClient> p) throws Exception {
        JenkinsClient jenkinsClient = p.getObject();
        if (jenkinsClient.isRunning()) {
            try {
                jenkinsClient.close();
            } catch (Exception e) {
                log.error("", e);
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<JenkinsClient> p) {
        JenkinsClient jenkinsClient = p.getObject();
        try {
            return jenkinsClient.isRunning();
        } catch (final Exception e) {
            log.error("jenkins client health check failed", e);
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<JenkinsClient> p) throws Exception {
        log.debug("activateObject........................");
    }

    @Override
    public void passivateObject(PooledObject<JenkinsClient> p) throws Exception {
        log.debug("passivateObject.......................");
    }
}

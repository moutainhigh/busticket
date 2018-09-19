package com.vpclub.bait.busticket.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(value = "redis")
public class RedisConifg {
    private String ip;
    private String password;
    private String port;
    private Pool pool = new Pool();

    public class Pool{
        private Integer maxIdle;//空闲连接实例的最大数目，为负值时没有限制
        private Integer maxTotal;
        private Boolean testOnBorrow;
        private Boolean testOnReturn;
        private Integer timeout;

        public Integer getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(Integer maxIdle) {
            this.maxIdle = maxIdle;
        }

        public Integer getMaxTotal() {
            return maxTotal;
        }

        public void setMaxTotal(Integer maxTotal) {
            this.maxTotal = maxTotal;
        }

        public Boolean getTestOnBorrow() {
            return testOnBorrow;
        }

        public void setTestOnBorrow(Boolean testOnBorrow) {
            this.testOnBorrow = testOnBorrow;
        }

        public Boolean getTestOnReturn() {
            return testOnReturn;
        }

        public void setTestOnReturn(Boolean testOnReturn) {
            this.testOnReturn = testOnReturn;
        }

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}

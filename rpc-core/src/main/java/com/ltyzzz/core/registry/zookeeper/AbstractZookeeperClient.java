package com.ltyzzz.core.registry.zookeeper;

import org.apache.zookeeper.Watcher;

import java.util.List;

public abstract class AbstractZookeeperClient {

    private String zkAddress;

    private int baseSleepTimes;

    private int maxRetryTimes;

    public AbstractZookeeperClient(String zkAddress) {
        this.zkAddress = zkAddress;
        this.baseSleepTimes = 1000;
        this.maxRetryTimes = 3;
    }

    public AbstractZookeeperClient(String zkAddress, Integer baseSleepTimes, Integer maxRetryTimes) {
        this.zkAddress = zkAddress;
        if (baseSleepTimes == null) {
            this.baseSleepTimes = 1000;
        } else {
            this.baseSleepTimes = baseSleepTimes;
        }
        if (maxRetryTimes == null) {
            this.maxRetryTimes = 3;
        } else {
            this.maxRetryTimes = maxRetryTimes;
        }
    }

    public abstract void updateNodeData(String path, String data);

    public abstract Object getClient();

    /**
     * 拉取节点的数据
     *
     * @param path
     * @return
     */
    public abstract String getNodeData(String path);

    /**
     * 获取指定目录下的字节点数据
     *
     * @param path
     * @return
     */
    public abstract List<String> getChildrenData(String path);

    /**
     * 创建持久化类型节点数据信息
     *
     * @param path
     * @param data
     */
    public abstract void createPersistentData(String path, String data);

    public abstract void createPersistentWithSeqData(String path, String data);

    /**
     * 创建临时节点数据类型信息
     *
     * @param path
     * @param data
     */
    public abstract void createTemporaryData(String path, String data);
    /**
     * 创建有序且临时类型节点数据信息
     *
     * @param path
     * @param data
     */

    public abstract void createTemporarySeqData(String path, String data);

    /**
     * 设置某个节点的数值
     *
     * @param path
     * @param data
     */
    public abstract void setTemporaryData(String path, String data);

    /**
     * 断开zk的客户端链接
     */
    public abstract void destroy();

    /**
     * 展示节点下边的数据
     *
     * @param path
     */
    public abstract List<String> listNode(String path);

    /**
     * 删除节点下边的数据
     *
     * @param path
     * @return
     */
    public abstract boolean deleteNode(String path);

    public abstract boolean existNode(String path);

    /**
     * 监听 path 路径下某个节点的数据变化
     *
     * @param path
     */
    public abstract void watchNodeData(String path, Watcher watcher);

    /**
     * 监听子节点下的数据变化
     *
     * @param path
     * @param watcher
     */

    public abstract void watchChildNodeData(String path, Watcher watcher);

    public String getZkAddress() {
        return zkAddress;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public int getBaseSleepTimes() {
        return baseSleepTimes;
    }

    public void setBaseSleepTimes(int baseSleepTimes) {
        this.baseSleepTimes = baseSleepTimes;
    }

    public int getMaxRetryTimes() {
        return maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }
}

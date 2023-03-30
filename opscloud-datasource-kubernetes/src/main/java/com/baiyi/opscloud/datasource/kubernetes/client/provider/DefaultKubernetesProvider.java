package com.baiyi.opscloud.datasource.kubernetes.client.provider;

import com.baiyi.opscloud.common.datasource.KubernetesConfig;
import com.baiyi.opscloud.core.util.SystemEnvUtil;
import com.baiyi.opscloud.datasource.kubernetes.client.MyKubernetesClientBuilder;
import com.google.common.base.Joiner;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

/**
 * @Author baiyi
 * @Date 2022/9/14 09:40
 * @Version 1.0
 */
public class DefaultKubernetesProvider {

    /**
     * 5.0
     * return new DefaultKubernetesClient(config);
     *
     * @param kubernetes
     * @return
     */
    public static KubernetesClient buildClient(KubernetesConfig.Kubernetes kubernetes) {
        preSet(kubernetes);
        io.fabric8.kubernetes.client.Config config = new ConfigBuilder()
                .withTrustCerts(true)
                // .withWebsocketTimeout(KubeClient.Config.WEBSOCKET_TIMEOUT)
                // .withConnectionTimeout(KubeClient.Config.CONNECTION_TIMEOUT)
                // .withRequestTimeout(KubeClient.Config.REQUEST_TIMEOUT)
                .build();
        return new KubernetesClientBuilder().withConfig(config).build();
    }

    private static void preSet(KubernetesConfig.Kubernetes kubernetes) {
        System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE, toKubeconfigPath(kubernetes));
        System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_REQUEST_TIMEOUT_SYSTEM_PROPERTY,
                String.valueOf(MyKubernetesClientBuilder.Values.REQUEST_TIMEOUT));
        System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_WEBSOCKET_TIMEOUT_SYSTEM_PROPERTY,
                String.valueOf(MyKubernetesClientBuilder.Values.WEBSOCKET_TIMEOUT));
        System.setProperty(io.fabric8.kubernetes.client.Config.KUBERNETES_CONNECTION_TIMEOUT_SYSTEM_PROPERTY,
                String.valueOf(MyKubernetesClientBuilder.Values.CONNECTION_TIMEOUT));
    }

    private static String toKubeconfigPath(KubernetesConfig.Kubernetes kubernetes) {
        String path = Joiner.on("/").join(kubernetes.getKubeconfig().getPath(),
                io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE);
        return SystemEnvUtil.renderEnvHome(path);
    }

}

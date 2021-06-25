package com.baiyi.caesar.datasource.kubernetes.convert;

import com.baiyi.caesar.common.type.DsAssetTypeEnum;
import com.baiyi.caesar.datasource.builder.AssetContainer;
import com.baiyi.caesar.datasource.builder.AssetContainerBuilder;
import com.baiyi.caesar.datasource.util.TimeUtil;
import com.baiyi.caesar.domain.generator.caesar.DatasourceInstance;
import com.baiyi.caesar.domain.generator.caesar.DatasourceInstanceAsset;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.Date;

/**
 * @Author baiyi
 * @Date 2021/6/25 4:22 下午
 * @Version 1.0
 */
public class DeploymentAssetConvert {

    public static Date toGmtDate(String time) {
        return TimeUtil.toGmtDate(time, TimeUtil.Format.UTC);
    }

    public static AssetContainer toAssetContainer(DatasourceInstance dsInstance, Deployment entry) {
        DatasourceInstanceAsset asset = DatasourceInstanceAsset.builder()
                .instanceUuid(dsInstance.getUuid())
                .assetId(entry.getMetadata().getUid()) // 资产id
                .name(entry.getMetadata().getName())
                .assetKey(entry.getMetadata().getName())
                // entry.getSpec().getTemplate().getSpec().getContainers().get(0).getImage() 容器模版镜像
                .assetKey2(entry.getMetadata().getNamespace()) // namespace
                .kind(entry.getKind())
                .assetType(DsAssetTypeEnum.KUBERNETES_DEPLOYMENT.name())
                .createdTime(toGmtDate(entry.getMetadata().getCreationTimestamp()))
                .build();

        return AssetContainerBuilder.newBuilder()
                .paramAsset(asset)
                .paramProperty("replicas", entry.getSpec().getReplicas())
                .build();
    }
}

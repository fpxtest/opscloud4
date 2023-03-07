package com.baiyi.opscloud.leo.task;

import com.baiyi.opscloud.common.instance.OcInstance;
import com.baiyi.opscloud.domain.generator.opscloud.LeoDeploy;
import com.baiyi.opscloud.leo.action.deploy.BaseDeployHandler;
import com.baiyi.opscloud.leo.helper.DeployingLogHelper;
import com.baiyi.opscloud.leo.helper.LeoHeartbeatHelper;
import com.baiyi.opscloud.service.leo.LeoDeployService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @Author baiyi
 * @Date 2022/12/26 13:48
 * @Version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LeoDeployCompensationTask {

    private final LeoDeployService deployService;

    private final LeoHeartbeatHelper heartbeatHelper;

    private final DeployingLogHelper logHelper;

    //private final DsConfigHelper dsConfigHelper;

    //private final LeoPostDeployHandler postDeployHandler;

    //private final ThreadPoolTaskExecutor leoExecutor;

    public void handleTask() {
        List<LeoDeploy> leoDeploys = deployService.queryNotFinishDeployWithOcInstance(OcInstance.ocInstance);
        if (CollectionUtils.isEmpty(leoDeploys)) {
            return;
        }
        leoDeploys.forEach(leoDeploy -> {
            if (!heartbeatHelper.isLive(LeoHeartbeatHelper.HeartbeatTypes.DEPLOY, leoDeploy.getId())) {
//                LeoDeployModel.DeployConfig deployConfig = LeoDeployModel.load(leoDeploy);
//                LeoBaseModel.DsInstance dsInstance = deployConfig.getDeploy().getKubernetes().getInstance();
//                // TODO 空指针异常
//                final String instanceUuid = dsInstance.getUuid();
//                KubernetesConfig kubernetesConfig = getKubernetesConfigWithUuid(instanceUuid);
//                DeployingSupervisor deployingSupervisor = new DeployingSupervisor(
//                        this.heartbeatHelper,
//                        leoDeploy,
//                        deployService,
//                        logHelper,
//                        deployConfig,
//                        kubernetesConfig.getKubernetes(),
//                        postDeployHandler
//                );
//                log.info("执行补偿任务: deployId={}", leoDeploy.getId());
//                leoExecutor.execute(new Thread(deployingSupervisor));
                LeoDeploy saveLeoDeploy = LeoDeploy.builder()
                        .id(leoDeploy.getId())
                        .deployResult(BaseDeployHandler.RESULT_ERROR)
                        .endTime(new Date())
                        .isFinish(true)
                        .isActive(false)
                        .deployStatus("任务异常终止,心跳丢失！")
                        .build();
                deployService.updateByPrimaryKeySelective(saveLeoDeploy);
                logHelper.error(leoDeploy,"任务异常终止,心跳丢失！");
            }
        });
    }

//    private KubernetesConfig getKubernetesConfigWithUuid(String uuid) {
//        DatasourceConfig dsConfig = dsConfigHelper.getConfigByInstanceUuid(uuid);
//        return dsConfigHelper.build(dsConfig, KubernetesConfig.class);
//    }

}

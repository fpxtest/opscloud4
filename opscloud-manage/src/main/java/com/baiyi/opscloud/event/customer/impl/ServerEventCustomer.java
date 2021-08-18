package com.baiyi.opscloud.event.customer.impl;

import com.baiyi.opscloud.ansible.ServerGroupingAlgorithm;
import com.baiyi.opscloud.datasource.manager.ServerManager;
import com.baiyi.opscloud.domain.generator.opscloud.Server;
import com.baiyi.opscloud.domain.types.BusinessTypeEnum;
import com.baiyi.opscloud.event.NoticeEvent;
import com.baiyi.opscloud.util.ServerTreeUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author baiyi
 * @Date 2021/8/17 7:03 下午
 * @Version 1.0
 */
@Component
public class ServerEventCustomer extends AbstractEventConsumer<Server> {

    @Resource private ServerTreeUtil serverTreeUtil;

    @Resource
    private ServerGroupingAlgorithm serverGroupingAlgorithm;

    @Resource
    private ServerManager serverManager;

    @Override
    public String getEventType() {
        return BusinessTypeEnum.SERVER.name();
    }

    @Override
    protected void preEventProcessing(NoticeEvent noticeEvent) {
        Server eventData = toEventData(noticeEvent.getMessage());
        serverGroupingAlgorithm.evictGrouping(eventData.getServerGroupId());
        serverTreeUtil.evictWrap(eventData.getServerGroupId());
    }

    @Override
    protected void onCreateMessage(NoticeEvent noticeEvent) {
        Server eventData = toEventData(noticeEvent.getMessage());
        serverManager.create(eventData);
    }

    @Override
    protected void onUpdateMessage(NoticeEvent noticeEvent) {
        Server eventData = toEventData(noticeEvent.getMessage());
        serverManager.update(eventData);
    }

    @Override
    protected void onDeleteMessage(NoticeEvent noticeEvent) {
        Server eventData = toEventData(noticeEvent.getMessage());
        serverManager.delete(eventData.getId());
    }

}
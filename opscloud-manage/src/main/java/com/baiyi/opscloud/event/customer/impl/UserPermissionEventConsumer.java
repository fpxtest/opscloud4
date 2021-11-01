package com.baiyi.opscloud.event.customer.impl;

import com.baiyi.opscloud.datasource.manager.DsAccountGroupManager;
import com.baiyi.opscloud.datasource.manager.DsServerGroupManager;
import com.baiyi.opscloud.domain.generator.opscloud.UserPermission;
import com.baiyi.opscloud.domain.types.BusinessTypeEnum;
import com.baiyi.opscloud.event.NoticeEvent;
import com.baiyi.opscloud.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Author baiyi
 * @Date 2021/8/20 9:39 上午
 * @Version 1.0
 */
@Component
@RequiredArgsConstructor
public class UserPermissionEventConsumer extends AbstractEventConsumer<UserPermission> {

    private final DsServerGroupManager dsServerGroupManager;

    private final DsAccountGroupManager dsAccountGroupManager;

    private final UserService userService;

    @Override
    public String getEventType() {
        return BusinessTypeEnum.USER_PERMISSION.name();
    }

    @Override
    protected void onCreateMessage(NoticeEvent noticeEvent) {
        UserPermission eventData = toEventData(noticeEvent.getMessage());
        if (eventData.getBusinessType() == BusinessTypeEnum.USERGROUP.getType()) {
            dsAccountGroupManager.grant(userService.getById(eventData.getUserId()), eventData);
            return;
        }
        if (eventData.getBusinessType() == BusinessTypeEnum.SERVERGROUP.getType()) {
            dsServerGroupManager.grant(userService.getById(eventData.getUserId()), eventData);
        }
    }

    @Override
    protected void onDeleteMessage(NoticeEvent noticeEvent) {
        UserPermission eventData = toEventData(noticeEvent.getMessage());
        if (eventData.getBusinessType() == BusinessTypeEnum.USERGROUP.getType()) {
            dsAccountGroupManager.revoke(userService.getById(eventData.getUserId()), eventData);
        }
        if (eventData.getBusinessType() == BusinessTypeEnum.SERVERGROUP.getType()) {
            dsServerGroupManager.revoke(userService.getById(eventData.getUserId()), eventData);
        }
    }

}


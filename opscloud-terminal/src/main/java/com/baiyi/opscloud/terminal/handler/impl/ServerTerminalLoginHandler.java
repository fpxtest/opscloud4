package com.baiyi.opscloud.terminal.handler.impl;

import com.baiyi.opscloud.domain.generator.opscloud.Server;
import com.baiyi.opscloud.domain.generator.opscloud.TerminalSession;
import com.baiyi.opscloud.interceptor.SuperAdminInterceptor;
import com.baiyi.opscloud.service.server.ServerService;
import com.baiyi.opscloud.sshcore.builder.TerminalSessionInstanceBuilder;
import com.baiyi.opscloud.sshcore.enums.InstanceSessionTypeEnum;
import com.baiyi.opscloud.sshcore.enums.MessageState;
import com.baiyi.opscloud.sshcore.handler.RemoteInvokeHandler;
import com.baiyi.opscloud.sshcore.message.ServerMessage;
import com.baiyi.opscloud.sshcore.model.HostSystem;
import com.baiyi.opscloud.sshcore.model.ServerNode;
import com.baiyi.opscloud.terminal.handler.AbstractServerTerminalHandler;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;

/**
 * 登录
 *
 * @Author baiyi
 * @Date 2020/5/11 9:36 上午
 * @Version 1.0
 */
@Slf4j
@Component
public class ServerTerminalLoginHandler extends AbstractServerTerminalHandler<ServerMessage.Login> {

    @Resource
    private SuperAdminInterceptor sAInterceptor;

    @Resource
    private ServerService serverService;

    @Autowired
    private ThreadPoolTaskExecutor coreExecutor;

    @Override
    public String getState() {
        return MessageState.LOGIN.getState();
    }

    @Override
    public void handle(String message, Session session, TerminalSession terminalSession) {
        try {
            ServerMessage.Login loginMessage = toMessage(message);
            heartbeat(terminalSession.getSessionId());
            for (ServerNode serverNode : loginMessage.getServerNodes()) {
                coreExecutor.submit(() -> {
                    log.info("登录服务器节点: instanceId={}", serverNode.getInstanceId());
                    sAInterceptor.interceptLoginServer(serverNode.getId());
                    HostSystem hostSystem = hostSystemHandler.buildHostSystem(serverNode, loginMessage);
                    Server server = serverService.getById(serverNode.getId());
                    RemoteInvokeHandler.openWithWebTerminal(terminalSession.getSessionId(), serverNode.getInstanceId(), hostSystem);
                    terminalSessionInstanceService.add(TerminalSessionInstanceBuilder.build(terminalSession, hostSystem, InstanceSessionTypeEnum.SERVER));
                });
            }
        } catch (Exception e) {
            log.error("批量登录服务器错误: {}", e.getMessage());
        }
    }

    @Override
    protected ServerMessage.Login toMessage(String message) {
        return new GsonBuilder().create().fromJson(message, ServerMessage.Login.class);
    }

}

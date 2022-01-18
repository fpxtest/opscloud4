package com.baiyi.opscloud.packer.workorder;

import com.baiyi.opscloud.common.util.BeanCopierUtil;
import com.baiyi.opscloud.domain.generator.opscloud.User;
import com.baiyi.opscloud.domain.generator.opscloud.WorkOrderTicket;
import com.baiyi.opscloud.domain.vo.user.UserVO;
import com.baiyi.opscloud.domain.vo.workorder.WorkOrderTicketVO;
import com.baiyi.opscloud.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Author baiyi
 * @Date 2022/1/11 3:14 PM
 * @Version 1.0
 */
@Component
@RequiredArgsConstructor
public class WorkOrderTicketPacker {

    private final UserService userService;

    private final WorkOrderTicketEntryPacker ticketEntryPacker;

    private final WorkOrderPacker workOrderPacker;

    private final WorkOrderWorkflowPacker workOrderWorkflowPacker;

    private final WorkOrderTicketNodePacker nodePacker;


    /**
     * 转换工单至视图
     *
     * @param workOrderTicket
     * @return
     */
    public WorkOrderTicketVO.TicketView toTicketView(WorkOrderTicket workOrderTicket) {
        WorkOrderTicketVO.Ticket ticket = WorkOrderTicketVO.Ticket.builder()
                .id(workOrderTicket.getId())
                .userId(workOrderTicket.getUserId())
                .username(workOrderTicket.getUsername())
                .workOrderId(workOrderTicket.getWorkOrderId())
                .nodeId(workOrderTicket.getNodeId())
                .ticketPhase(workOrderTicket.getTicketPhase())
                .ticketStatus(workOrderTicket.getTicketStatus())
                .startTime(workOrderTicket.getStartTime())
                .endTime(workOrderTicket.getEndTime())
                .comment(workOrderTicket.getComment())
                .build();
        User user = userService.getByUsername(workOrderTicket.getUsername());
        WorkOrderTicketVO.TicketView ticketView = WorkOrderTicketVO.TicketView.builder()
                .createUser(toCreateUser(workOrderTicket))
                .workOrderTicket(ticket)
                .build();
        workOrderPacker.wrap(ticketView);
        ticketEntryPacker.wrap(ticketView);
        workOrderWorkflowPacker.wrap(ticketView); // 工作流节点
        nodePacker.wrap(ticketView);
        return ticketView;
    }

    private UserVO.User toCreateUser(WorkOrderTicket workOrderTicket) {
        User user = userService.getByUsername(workOrderTicket.getUsername());
        return BeanCopierUtil.copyProperties(user, UserVO.User.class);
    }

}
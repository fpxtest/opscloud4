package com.baiyi.opscloud.controller.http;

import com.baiyi.opscloud.common.HttpResult;
import com.baiyi.opscloud.domain.DataTable;
import com.baiyi.opscloud.domain.param.ser.SerDeployParam;
import com.baiyi.opscloud.domain.vo.ser.SerDeployVO;
import com.baiyi.opscloud.facade.ser.SerDeployFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author 修远
 * @Date 2023/6/7 11:20 AM
 * @Since 1.0
 */

@RestController
@RequestMapping("/api/ser/")
@Tag(name = "ser包发布管理")
@RequiredArgsConstructor
@Slf4j
public class SerDeployController {

    private final SerDeployFacade serDeployFacade;

    @Operation(summary = "ser包上传")
    @PostMapping(value = "/upload")
    public HttpResult<Boolean> uploadFile(@RequestBody MultipartFile file,
                                          @RequestHeader("x-task-uuid") @NotNull(message = "缺少任务id") String taskUuid) {
        serDeployFacade.uploadFile(file, taskUuid);
        return HttpResult.SUCCESS;
    }

    @Operation(summary = "分页查询 Ser 包发布任务")
    @PostMapping(value = "/task/page/query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<DataTable<SerDeployVO.Task>> queryProjectPage(@RequestBody @Valid SerDeployParam.TaskPageQuery pageQuery) {
        return new HttpResult<>(serDeployFacade.querySerDeployTaskPage(pageQuery));
    }

    @Operation(summary = "UUID 查询 Ser 包发布任务详情")
    @PostMapping(value = "/task/uuid/query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<SerDeployVO.Task> getSerDeployTaskByUuid(@RequestBody @Valid SerDeployParam.QueryByUuid param) {
        return new HttpResult<>(serDeployFacade.getSerDeployTaskByUuid(param));
    }

    @Operation(summary = "新增 Ser 包发布任务")
    @PostMapping(value = "/task/add", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> addSerDeployTask(@RequestBody @Valid SerDeployParam.AddTask addTask) {
        serDeployFacade.addSerDeployTask(addTask);
        return HttpResult.SUCCESS;
    }

    @Operation(summary = "更新 Ser 包发布任务")
    @PutMapping(value = "/task/update", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> updateSerDeployTask(@RequestBody @Valid SerDeployParam.UpdateTask updateTask) {
        serDeployFacade.updateSerDeployTask(updateTask);
        return HttpResult.SUCCESS;
    }

    @Operation(summary = "删除 Ser 包发布任务 Item")
    @DeleteMapping(value = "/task/item/del", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> deleteSerDeployTaskItem(@RequestParam Integer id) {
        serDeployFacade.deleteSerDeployTaskItem(id);
        return HttpResult.SUCCESS;
    }


}

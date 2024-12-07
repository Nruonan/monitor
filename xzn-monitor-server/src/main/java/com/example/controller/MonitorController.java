package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.RenameClientReqDTO;
import com.example.entity.vo.response.ClientPreviewRespDTO;
import com.example.service.ClientService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nruonan
 * @description
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {
    @Resource
    ClientService service;

    @GetMapping("/list")
    public RestBean<List<ClientPreviewRespDTO>> listAllClient(){
        return RestBean.success(service.listAllClient());
    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestBody RenameClientReqDTO requestParam){
        service.renameClient(requestParam);
        return RestBean.success();
    }
}

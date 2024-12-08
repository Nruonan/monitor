package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.RenameClientReqDTO;
import com.example.entity.vo.request.RenameNodeReqDTO;
import com.example.entity.vo.response.ClientDetailsRespDTO;
import com.example.entity.vo.response.ClientPreviewRespDTO;
import com.example.entity.vo.response.ClientSimpleRespDTO;
import com.example.entity.vo.response.RuntimeDetailRespDTO;
import com.example.entity.vo.response.RuntimeHistoryRespDTO;
import com.example.service.AccountService;
import com.example.service.ClientService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Resource
    AccountService accountService;

    @GetMapping("/list")
    public RestBean<List<ClientPreviewRespDTO>> listAllClient(@RequestAttribute(Const.ATTR_USER_ID) int userId,
        @RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        List<ClientPreviewRespDTO> clientPreviewRespDTOS = service.listAllClient();
        if (this.isAdminAccount(userRole)){
            return RestBean.success(clientPreviewRespDTOS);
        } else {
            List<Integer> ids = this.accountAccessClients(userId);
            return RestBean.success(clientPreviewRespDTOS.stream()
                .filter(dto -> ids.contains(dto.getId())).toList());
        }
    }

    @GetMapping("/simple-list")
    public RestBean<List<ClientSimpleRespDTO>> simpleClientList(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole)){
            return RestBean.success(service.simpleClientList());
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }

    }

    @PostMapping("/rename")
    public RestBean<Void> renameClient(@RequestAttribute(Const.ATTR_USER_ID) int userId,
        @RequestAttribute(Const.ATTR_USER_ROLE) String userRole,
        @RequestBody RenameClientReqDTO requestParam) {
        if (this.permissionCheck(userId,userRole,requestParam.getId())){
            service.renameClient(requestParam);
            return RestBean.success();
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }
    }

    @PostMapping("/rename-node")
    public RestBean<Void> renameClient(@RequestAttribute(Const.ATTR_USER_ID) int userId,
        @RequestAttribute(Const.ATTR_USER_ROLE) String userRole,
        @RequestBody RenameNodeReqDTO requestParam) {
        if (this.permissionCheck(userId,userRole,requestParam.getId())){
            service.renameNode(requestParam);
            return RestBean.success();
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }
    }

    @GetMapping("/details")
    public RestBean<ClientDetailsRespDTO> details(@RequestAttribute(Const.ATTR_USER_ID) int userId,
        @RequestAttribute(Const.ATTR_USER_ROLE) String userRole,
        @RequestParam("clientId") int id) {
        if (this.permissionCheck(userId,userRole,id)){
            return RestBean.success(service.clientDetails(id));
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }
    }

    @GetMapping("/runtime-history")
    public RestBean<RuntimeHistoryRespDTO> runtimeDetailsHistory(@RequestAttribute(Const.ATTR_USER_ID) int userId,
        @RequestAttribute(Const.ATTR_USER_ROLE) String userRole,
        @RequestParam("clientId") int clientId) {
        if (this.permissionCheck(userId,userRole,clientId)){
            return RestBean.success(service.clientRuntimeDetailsHistory(clientId));
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }
    }

    @GetMapping("/runtime-now")
    public RestBean<RuntimeDetailRespDTO> runtimeDetailsNow(@RequestAttribute(Const.ATTR_USER_ID) int userId,
        @RequestAttribute(Const.ATTR_USER_ROLE) String userRole,
        @RequestParam("clientId") int clientId) {
        if (this.permissionCheck(userId,userRole,clientId)){
            return RestBean.success(service.clientRuntimeDetailsNow(clientId));
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }
    }

    @GetMapping("/register")
    public RestBean<String> registerToken(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole) {
        if (this.isAdminAccount(userRole)){
            return RestBean.success(service.registerToken());
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }
    }

    @GetMapping("/delete")
    public RestBean<Void> deleteClient(@RequestAttribute(Const.ATTR_USER_ROLE) String userRole,
        @RequestParam("clientId") int clientId) {
        if (this.isAdminAccount(userRole)){
            service.deleteClient(clientId);
            return RestBean.success();
        }else{
            return RestBean.failure(401,"权限不足无法访问");
        }
    }

    /**
     * 根据用户ID获取该用户可以访问的客户端列表
     * 
     * @param uid 用户ID，用于标识和获取用户信息
     * @return 返回一个包含用户可访问客户端ID的列表
     */
    private List<Integer> accountAccessClients(int uid) {
        // 通过账户服务根据用户ID获取账户信息，并进一步获取客户端列表
        return accountService.getById(uid).getClientList();
    }

    /**
     * 检查用户角色是否为管理员账户
     * @param role 用户的角色字符串，该字符串包含了用户的角色信息
     * @return 如果用户角色被认定为管理员，则返回true；否则返回false
     */
    private boolean isAdminAccount(String role) {
        return role.substring(5).equals(Const.ROLE_ADMIN);
    }

    /**
     * 权限检查方法
     * 该方法用于判断用户是否有权限访问指定客户端
     * 
     * @param uid 用户ID，用于识别用户
     * @param role 用户角色，用于判断用户是否为管理员
     * @param clientId 客户端ID，用户试图访问的客户端
     * @return 如果用户有权限访问指定客户端，则返回true；否则返回false
     */
    private boolean permissionCheck(int uid,String role, int clientId){
        // 检查用户是否为管理员角色，如果是，则直接返回true
        if (this.isAdminAccount(role))return true;
        // 检查用户是否有访问指定客户端的权限
        return this.accountAccessClients(uid).contains(clientId);
    }
}
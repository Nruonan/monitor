package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.ClientDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Nruonan
 * @description
 */
@Mapper
public interface ClientMapper extends BaseMapper<ClientDO> {
    boolean verifyAndRegister(String token);
}

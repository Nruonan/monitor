package com.example.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Nruonan
 * @description
 */
@Data
public class RenameClientReqDTO {
    @NotNull
    private Integer id;
    @Min(1)
    @Max(10)
    private String name;

}

package com.example.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author Nruonan
 * @description
 */
@Data
public class RenameNodeReqDTO {
    @NotNull
    private Integer id;
    @Pattern(regexp = "(cn|hk|jp|us|sg|kr|de)")
    private String location;
    @Length(min =1, max=10)
    private String node;
}

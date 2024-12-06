package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Nruonan
 * @description
 */
@Data
@AllArgsConstructor
public class ConnectionConfig {
    String address;
    String token;
}

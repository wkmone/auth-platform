package com.company.auth.user.dto;
import lombok.Data;

@Data
public class UserPageQuery {
    private String username;
    private String status;
    private long page = 1;
    private long size = 10;
}

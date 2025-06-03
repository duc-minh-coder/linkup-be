package com.example.linkup.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdatePasswordRequest {
    @NotNull
    @Size(min = 8, message = "USERNAME_INVALID")
    private String username;

    @NotNull
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String oldPassword;

    @NotNull
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String newPassword;

    @Builder.Default
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updated_time = new Date();
}

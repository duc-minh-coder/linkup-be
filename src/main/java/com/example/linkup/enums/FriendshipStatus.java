package com.example.linkup.enums;

import lombok.Getter;

@Getter
public enum FriendshipStatus {
    REQUEST_RECEIVED, // user là ng nhận
    REQUEST_SENT, // user là ng gửi
    FRIEND,
    NOT_FRIEND,
    OWNER
}

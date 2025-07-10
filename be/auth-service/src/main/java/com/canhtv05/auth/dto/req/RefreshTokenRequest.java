package com.canhtv05.auth.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequest {

    String refreshToken;
    String email;
}

package com.canhtv05.auth.dto.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest implements Serializable {

    String email;
    String password;
}

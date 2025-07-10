package com.canhtv05.user.dto.res;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AbstractResponse implements Serializable {

    String id;
    Instant createdAt;
    Instant updatedAt;
}

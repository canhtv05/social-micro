package com.canhtv05.user.enity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractEntity {

    @Column(name = "description")
    String description;
}

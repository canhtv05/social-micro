package com.canhtv05.profile.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(generatorClass = GeneratedValue.UUIDGenerator.class)
    UUID id;

    @Property("created_at")
    @CreatedDate
    @DateTimeFormat(pattern = "HH:mm:ss dd-MM-yyyy")
    Instant createdAt;

    @Property("updated_at")
    @LastModifiedDate
    @DateTimeFormat(pattern = "HH:mm:ss dd-MM-yyyy")
    Instant updatedAt;
}
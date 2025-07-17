package com.canhtv05.post.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse {

    int currentPage;
    int totalPages;
    int pageSize;
    long totalElements;
}

package com.gialong.facebook.base;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;      // danh sách item trong trang
    private int page;             // số trang hiện tại (0-based)
    private int size;             // số lượng item / trang
    private long totalElements;   // tổng số item
    private int totalPages;       // tổng số trang
    private boolean last;         // có phải trang cuối không
}
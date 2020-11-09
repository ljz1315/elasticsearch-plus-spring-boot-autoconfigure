package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lijz
 * @Description 分页参数
 * @Date: 2020/8/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageInfo {
    private int pageNum;
    private int size;
    private boolean isPage;
}

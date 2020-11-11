package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.handle;

import java.util.List;

public interface Handler<T> {
    void handle(List<T> dataList);
}

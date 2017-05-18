package com.atex.blogping.dao.impl;

import com.atex.blogping.dao.BlogpingDAO;
import com.atex.blogping.dto.WeblogDTO;
import com.google.inject.Singleton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Data storage implementation using a ConcurrentLinkedQueue.
 */
@Singleton
public class QueueBackedBlogpingDAO implements BlogpingDAO {

    private ConcurrentLinkedQueue<WeblogDTO> weblogs = new ConcurrentLinkedQueue<>();

    @Override
    public void saveWeblog(WeblogDTO weblog) {

        if (weblog == null) {
            throw new IllegalArgumentException("weblog is null");
        }

        weblogs.add(weblog);
    }

    @Override
    public List<WeblogDTO> getWeblogs() {
        List<WeblogDTO> weblogList = Arrays.asList(weblogs.toArray(new WeblogDTO[0]));
        Collections.reverse(weblogList);
        return weblogList;
    }

    @Override
    public void deleteWeblog(final WeblogDTO weblogDTO) {
        if (weblogDTO == null) {
            throw new IllegalArgumentException("weblog is null");
        }

        weblogs.removeIf(item ->
                item.getName().equals(weblogDTO.getName()) &&
                        item.getUrl().equals(weblogDTO.getUrl()));
    }
}

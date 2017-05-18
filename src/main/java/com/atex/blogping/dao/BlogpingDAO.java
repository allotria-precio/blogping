package com.atex.blogping.dao;

import com.atex.blogping.dto.WeblogDTO;

import java.util.List;

public interface BlogpingDAO {

    /**
     * Saves a {@link WeblogDTO} to the data store.
     * @param weblog The weblog to store.
     */
    void saveWeblog(WeblogDTO weblog);

    /**
     * Retrieves all stored {@link WeblogDTO}s in reverse order.
     * @return List of weblogs.
     */
    List<WeblogDTO> getWeblogs();

    /**
     * Delete a {@link WeblogDTO} from the data store.
     * @param weblogDTO The weblog to delete.
     */
    void deleteWeblog(WeblogDTO weblogDTO);
}

package com.rs.service;

import com.rs.model.PageResult;
import com.rs.model.ticket.Train;

public interface TrainService {

    /**
     * Admin: Page query
     */
    PageResult<Train> adminPage(String name, String type, Integer status, Integer pageNum, Integer pageSize);

    /**
     * Admin: Add train
     */
    void add(Train train);

    /**
     * Admin: Update train
     */
    void update(Train train);

    /**
     * Admin: Delete train
     */
    void delete(Long id);
}

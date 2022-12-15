package com.zhexu.cs677_lab3.business.raft;

import com.fasterxml.jackson.core.JsonProcessingException;

@FunctionalInterface
public
interface RollbackMethod {
    void rollBack() throws JsonProcessingException;
}

package com.example.eatathome.Server.Models;

import java.util.List;

public class MyResponseRes {

    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<ResultRes> results;
}

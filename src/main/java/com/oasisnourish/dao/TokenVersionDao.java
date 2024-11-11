package com.oasisnourish.dao;

public interface TokenVersionDao {

    long find(int userId, String tokenCategory, String tokenType);

    long increment(int userId, String tokenCategory, String tokenType);
}

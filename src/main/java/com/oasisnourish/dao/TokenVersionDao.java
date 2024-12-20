package com.oasisnourish.dao;

import com.oasisnourish.enums.Tokens;

public interface TokenVersionDao {

    long find(int userId, Tokens.Category tokenCategory, Tokens.Type tokenType);

    long increment(int userId, Tokens.Category tokenCategory, Tokens.Type tokenType);
}

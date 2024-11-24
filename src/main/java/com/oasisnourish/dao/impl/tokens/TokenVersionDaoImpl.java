package com.oasisnourish.dao.impl.tokens;

import com.oasisnourish.dao.tokens.TokenVersionDao;
import com.oasisnourish.db.RedisConnection;
import com.oasisnourish.enums.Tokens;

public class TokenVersionDaoImpl implements TokenVersionDao {

    private final RedisConnection redisConnection;

    public TokenVersionDaoImpl(RedisConnection redisConnection) {
        this.redisConnection = redisConnection;
    }

    @Override
    public long find(int userId, Tokens.Category tokenCategory, Tokens.Type tokenType) {
        String key = getKey(userId, tokenCategory, tokenType);
        var jedis = redisConnection.getJedis();

        if (!jedis.exists(key)) {
            jedis.set(key, "1");
        }
        return Long.parseLong(jedis.get(key));
    }

    @Override
    public long increment(int userId, Tokens.Category tokenCategory, Tokens.Type tokenType) {
        String key = getKey(userId, tokenCategory, tokenType);
        var jedis = redisConnection.getJedis();

        if (!jedis.exists(key)) {
            jedis.set(key, "0");
        }
        return jedis.incr(key);
    }

    private String getKey(int userId, Tokens.Category tokenCategory, Tokens.Type tokenType) {
        return "user:" + userId + ":token-category:" + tokenCategory.getCategory() + ":token-type:" + tokenType.getType();
    }
}

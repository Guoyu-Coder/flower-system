package com.ai.learning.common.constant;

public class RedisConstant {

    public static final String USER_TOKEN_PREFIX = "user:token:";
    public static final String USER_INFO_PREFIX = "user:info:";
    public static final String AI_SESSION_PREFIX = "ai:session:";
    public static final String AI_CONTEXT_PREFIX = "ai:context:";
    public static final String QUESTION_CACHE_PREFIX = "question:cache:";
    public static final String HOT_QUESTION_PREFIX = "question:hot:";
    
    public static final long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60L;
    public static final long SESSION_EXPIRE_TIME = 24 * 60 * 60L;
    public static final long QUESTION_CACHE_TIME = 30 * 60L;
}

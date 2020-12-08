package com.bjtu.redis.jedis;

import org.junit.Test;

import com.bjtu.redis.JedisInstance;

import redis.clients.jedis.Jedis;

import java.util.Scanner;

public class JedisInstanceTest {

    /**
     * 基本使用
     */
    @Test
    public void test() {
        Scanner scanner = new Scanner(System.in);
        Jedis jedis = JedisInstance.getInstance().getResource();
        jedis.hset("key","x","0");
        System.out.println("Start!");

        System.out.println(jedis.hget("key","x"));
        jedis.hincrBy("key","x",1);
        System.out.println(jedis.hget("key","x"));
        jedis.hincrBy("key","x",1);
        System.out.println(jedis.hget("key","x"));
        jedis.hincrBy("key","x",1);
    }

}

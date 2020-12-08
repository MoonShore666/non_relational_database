package com.bjtu.redis;

import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.util.*;
import java.text.SimpleDateFormat;

public class RedisCounter {
    private Jedis jedis;
    private int index;
    public String key;
    public  String field;
    private long incrNum;
    private long timeInerval;
    private SimpleDateFormat dateFormat;
    private long startTime;

    private int counter_type;
    public static final int normalType = 0;
    public static final int hashType = 1;

    private int freq_select_type;
    public static final int greedy = 0;
    public static final int lazzy = 1;

    public RedisCounter(){
        jedis = JedisInstance.getInstance().getResource();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        startTime = getTime();
        key = "defaultKey";
        field = "defaultField";
        counter_type = 0;
        freq_select_type = 0;
        timeInerval = 3600;
        if( !jedis.exists(key)) {
            jedis.setex(key, 3600,"0");
        }
    }

    public void incr(){
        int timeIntervalNum = (int) Math.floor( (float)(getTime()-startTime)/(timeInerval*1000));
        String key_ = key;
        key_ += getFormatTime(startTime + timeInerval*timeIntervalNum);

        if( counter_type == normalType){
            jedis.incrBy(key_,incrNum);
            jedis.expire(key_,(int) (timeInerval*10));
        }else{
            jedis.hincrBy(key_,field,incrNum);
        }
    }

    public int getIncr(){
        return freq(dateFormat.format(new Date(startTime)),dateFormat.format((new Date())));
    }

    public int freq(String from, String to){
        int freq = 0;
        int fromIndex = 0;
        int toIndex = 0;
        try {
            if(freq_select_type == greedy){
                fromIndex = (int) Math.floor( (float)(dateFormat.parse(from).getTime()-startTime)/(timeInerval*1000));
                toIndex = (int) Math.ceil( (float)(dateFormat.parse(to).getTime()-startTime)/(timeInerval*1000));
            }else if(freq_select_type == lazzy){
                fromIndex = (int) Math.ceil((float)(dateFormat.parse(from).getTime()-startTime)/(timeInerval*1000));
                toIndex = (int) Math.floor((float)(dateFormat.parse(to).getTime()-startTime)/(timeInerval*1000));
            }
            if(fromIndex<0){fromIndex = 0;}
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(counter_type == normalType){
            for(int index = fromIndex; index < toIndex; index++){
                String keyName = key+getFormatTime(startTime+timeInerval*index);
                String i = jedis.get( keyName );
                freq += Integer.parseInt( i );
            }
        }else if(counter_type == hashType){
            for(int index = fromIndex; index < toIndex; index++){
                freq += Integer.parseInt( jedis.hget( key+getFormatTime(startTime+timeInerval*index),field ) );
            }
        }
        return freq;
    }

    public void InputString(String value){
        if(counter_type == normalType)
            jedis.set(key+"_str",value);
        else if(counter_type == hashType)
            jedis.hset(key+"_str",field,value);
        else
            return;
    }

    public String getString() {
        if (counter_type == normalType)
            return jedis.get(key + "_str");
        else if (counter_type == hashType)
            return jedis.hget(key + "_str", field);
        else
            return "";
    }

    public void list( List list){
        RedisTemplate<String,List> redisTemplate = new RedisTemplate<String, List>();
        String name = key;
        if(counter_type == normalType)
            name += "_list";
        else if(counter_type == hashType)
            name += "_"+field+"_list";
        else
            return;
        redisTemplate.opsForList().leftPush(name,list);
    }

    public List getList(){
        String name = key;
        if(counter_type == normalType)
            name += "_list";
        else if(counter_type == hashType)
            name += "_"+field+"_list";
        return jedis.lrange(name,0,jedis.llen(name));
    }

    public void set(Set set){
        RedisTemplate<String,Set> redisTemplate = new RedisTemplate<String, Set>();
        String name = key;
        if(counter_type == normalType)
            name += "_set";
        else if(counter_type == hashType)
            name += "_"+field+"_set";
        else
            return;
        redisTemplate.opsForSet().add(name,set);
    }

    public Set getSet(){
        String name = key;
        if(counter_type == normalType)
            name += "_set";
        else if(counter_type == hashType)
            name += "_"+field+"_set";
        return jedis.smembers(name);
    }

    public void zset(Set set){
        RedisTemplate<String,Set> redisTemplate = new RedisTemplate<String, Set>();
        String name = key;
        if(counter_type == normalType)
            name += "_zset";
        else if(counter_type == hashType)
            name += "_"+field+"_zset";
        else
            return;
        redisTemplate.opsForZSet().add(name,set,1);
    }

    public Set getZset(){
        String name = key;
        if(counter_type == normalType)
            name += "_zset";
        else if(counter_type == hashType)
            name += "_"+field+"_zset";
        return jedis.zrange(name,0,jedis.zcard(name));
    }

    public void proto(){

    }

    private String getFormatTime(long time){
        return dateFormat.format(new Date(time));
    }

    private long getTime(){
        return (new Date()).getTime();
    }

    public void setIndex(int index){this.index = index;}

    public void setKey(String key){ this.key = key; }

    public void setField(String field){this.field = field;}

    public void setIncrNum(long incrNum){ this.incrNum = incrNum; }

    public void setTimeInerval(long timeInerval){ this.timeInerval = timeInerval; }

    public void setDateFormat(String dateFormat){ this.dateFormat = new SimpleDateFormat(dateFormat); }

    public void setStartTime(long startTime){this.startTime = startTime;}

    public void setFormatedStartTime(String formatedStartTime){
        try {
            this.startTime = dateFormat.parse(formatedStartTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setCounterType(int type){ this.counter_type = type; }

    public void setFreq_select_type(int type){this.freq_select_type = type;}

    public int getIndex(){return this.index;}

    public String getKey(){return this.key;}

    public String getField(){return this.field;}

    public int getCounterType(){return counter_type;}

    public long getStartTime(){return startTime;}

    public String getFormatedStartTime(){return dateFormat.format(new Date(startTime));}

    public String getDateFormat(){ return dateFormat.toString(); }
}

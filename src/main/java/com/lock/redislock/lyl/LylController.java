package com.lock.redislock.lyl;

import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequestMapping("lyl")
@RestController
public class LylController {
    private static Logger logger = LoggerFactory.getLogger(LylController.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private Redisson redisson;
    //@Autowired
    //private RedissonClient redissonClient;
    @GetMapping("sjf")
    public String sjf(HttpServletRequest request,@RequestParam("type")String type){
        String type1 = request.getParameter("type");
        return "sjf " + type;
    }

    @RequestMapping("deductStock")
    public String deductStock(){
        String redisLock = "sjf1";
        String clientId = UUID.randomUUID().toString();
        try {
            Boolean a = redisTemplate.opsForValue().setIfAbsent(redisLock, clientId,10,TimeUnit.SECONDS);
            if (a == false){
                return "没有抢到锁";
            }
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if (stock > 0){
                int realStock = stock - 1;
                redisTemplate.opsForValue().set("stock",realStock + "");
                System.out.println("扣除库存成功,剩余库存"+realStock);
                return "扣除库存成功,剩余库存"+realStock + " a= " +  a;
            }else {
                System.out.println("扣减失败,库存不足");
                return "扣减失败,库存不足" + " a= " +  a;
            }
        }finally {
            if (clientId.equals(redisTemplate.opsForValue().get(redisLock))){
                redisTemplate.delete(redisLock);
            }
        }
    }

    @RequestMapping("deductRedisStock")
    public String deductRedisStock(){
        String redisLock = "sjf";
        RLock lock = redisson.getLock(redisLock);
        try {
            lock.lock();
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if (stock > 0){
                int realStock = stock - 1;
                redisTemplate.opsForValue().set("stock",realStock + "");
                System.out.println("扣除库存成功,剩余库存"+realStock);
                return "扣除库存成功,剩余库存"+realStock;
            }else {
                System.out.println("扣减失败,库存不足");
                return "扣减失败,库存不足";
            }
        }finally {
            lock.unlock();
        }
    }


    @ResponseBody
    @GetMapping("/loveSjf")
    public String loveSjf(@RequestParam("sid") String serverId) {
        Long counter = redisTemplate.opsForValue().increment("COUNTER", 1);
        RLock lock = redisson.getLock("TEST");
        try {
            lock.lock();
            logger.info("Request Thread - " + counter + "[" + serverId +"] locked and begun...");
            Thread.sleep(5000); // 5 sec
            logger.info("Request Thread - " + counter + "[" + serverId +"] ended successfully...");
        } catch (Exception ex) {
            logger.error("Error occurred");
        } finally {
            lock.unlock();
            logger.info("Request Thread - " + counter + "[" + serverId +"] unlocked...");
        }

        return "lock-" + counter + "[" + serverId +"]";
    }
}

package com.rs.aspect;

import com.rs.annotation.Lock;
import com.rs.enums.RespCode;
import com.rs.exception.CommonException;
import com.rs.util.AspectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class LockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(lock)")
    public Object handleLock(ProceedingJoinPoint pjp, Lock lock) throws Throwable {
        log.info("进入锁切面");
        //锁key的格式化字符，此字符串中有spEL表达式
        String formatter = lock.formatter();
        Method method = AspectUtils.getMethod(pjp);
        Object[] args = pjp.getArgs();
        //得到锁的key
        String redisKey = AspectUtils.parse(formatter, method, args);
        //获取锁阻塞等待的时间,如果是0表示去尝试获取锁，如果获取不到则结束
        long waitTime = 0;
        //阻塞等待获取锁
        if (lock.block()) {
            //根据时间单位转换成ms
            waitTime = lock.waitTime();
        }
        //加锁时长
        long time = lock.time();
        //启动看门狗自动续期
        if(lock.startDog()){
            time = -1;
            //如果设置自动续期必须在方法执行后释放锁
            if(!lock.unlock()){
                throw new BadRequestException("参数不合法");
            }
        }
        RLock rLock = redissonClient.getLock(redisKey);
        //尝试加锁
        boolean success = rLock.tryLock(waitTime,time, lock.unit());
        if (!success && !lock.block()) {
            //未阻塞要求的情况下未得到锁
            throw new CommonException(RespCode.ERROR, "未得到锁");
        }
        if (!success) {
            //阻塞情况下未得到锁，请求超时
            throw new CommonException(RespCode.ERROR, "获取锁失败");
        }

        try {
            return pjp.proceed();
        } finally {
            if (lock.unlock()) {
                log.info("释放锁");
                rLock.unlock();
            }
        }
    }
}

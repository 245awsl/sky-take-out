package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面类, 实现公共字段字段填充逻辑处理
 */
// Aspect让它成为切面类
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点 对哪些类哪些方法进行拦截
     */
    // 切点表达式，对哪些方法进行拦截
    // * com.sky.mapper.*.*(..)拦截这个包下所有的类还有所以的方法和参数类型
    // @annotation加入要拦截的方法，方法定义在AutoFill这个枚举里面了
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 前置通知：在通知中进行公告字段的赋值
     */
    @Before("autoFillPointCut()")
    // 插入连接点就可以知道拦截了什么方法，什么参数
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充....");

        // 获取当前被拦截的方法上的数据库操作类型
        // @AutoFill(value = OperationType.INSERT) 获取这个是什么类型
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获得方法上得注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        // 获得数据库操作类型
        OperationType operationType = autoFill.value();

        // 获取到当前被拦截的方法的参数 -- 实体对象 比如说void update(Employee employee)的实体
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            return;
        }

        // 获取第一个实体参数
        Object entity = args[0];

        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT){
            // 为4个公共字段都需要赋值
            // 获取set方法
            try {                                                            // 这里定义了常量
                Method setCreateTimes = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射为对象属性赋值
                setCreateTimes.invoke(entity,now);
                setCreateUser.invoke(entity, currentId);
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (operationType == OperationType.UPDATE){
            // 为2个公共字段赋值
            // 获取set方法
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 通过反射为对象属性赋值
                setUpdateTime.invoke(entity, now);
                setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

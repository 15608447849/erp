package framework.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: leeping
 * @Date: 2019/6/26 18:58
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {
    String detail() default "";
    Class<?> imp() default void.class;//指向实现类
    boolean inPrint() default true; // 是否打印传参信息
    boolean outPrint() default true;// 是否打印调用结果
    boolean timePrint() default true;//是否打印调用时间
}
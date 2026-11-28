package org.example.springboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 授权注解：标在方法（或整个 Controller）上，表示"至少需要这个角色才能访问"。
 *
 * <p>用法：{@code @RequireRole(2)} 表示 role >= 2（管理员）才放行。
 *
 * <p>它本身不做事，只是一个"标签"。真正干活的是 JwtInterceptor：
 * 它通过 HandlerMethod 把这个标签读出来再判断。
 * 这也正是我们选 HandlerInterceptor 而不选 Filter 的原因——Filter 只有路径，看不到方法上的注解。
 */
// TODO(源) 把下面这两行注解各写一句注释：@Target 在限制什么？@Retention(RUNTIME) 为什么必须写（不写会怎样）？
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /** 需要的角色等级：1 = 普通用户，2 = 管理员。按"至少"理解，不是"等于"。 */
    int value();
}

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
// @Target：限制本注解能贴在哪些位置——METHOD（方法）、TYPE（类/接口/注解类型）。不写就处处可贴，编译器不帮你拦错地方。
// @Retention(RUNTIME)：注解信息保留到运行期，反射才读得到。默认是 CLASS——只编译进 .class，JVM 不加载，getMethodAnnotation 返回 null。
// （这两个叫"元注解"，它们自己也遵守同样的规矩：JDK 的 Target 就是被 @Target(ANNOTATION_TYPE) 修饰的。）
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /** 需要的角色等级：1 = 普通用户，2 = 管理员。按"至少"理解，不是"等于"。 */
    int value();
}

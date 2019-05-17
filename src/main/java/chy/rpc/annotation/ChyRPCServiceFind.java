package chy.rpc.annotation;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ChyRPCServiceFind {

    String serviceName() default "";
    String beanName() default "";

}

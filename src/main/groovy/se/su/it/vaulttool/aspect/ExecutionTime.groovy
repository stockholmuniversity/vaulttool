package se.su.it.vaulttool.aspect

import groovy.time.TimeDuration
import groovy.time.TimeCategory
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut

@Aspect
class ExecutionTime {
    Date before
    Date after
    @Before("execution(* se.su.it.vaulttool.VaultRestService.*(..)) && !execution(* se.su.it.vaulttool.VaultRestService.*MetaClass(..))")
    void timeBefore(JoinPoint joinPoint){
        before = new Date()
    }

    @After("execution(* se.su.it.vaulttool.VaultRestService.*(..)) && !execution(* se.su.it.vaulttool.VaultRestService.*MetaClass(..))")
    void timeAfter(JoinPoint joinPoint){
        after = new Date()
        TimeDuration duration = TimeCategory.minus(after, before)
        println("Measuring execution time for ${joinPoint.signature.name}, took ${duration} to run.")
    }
}

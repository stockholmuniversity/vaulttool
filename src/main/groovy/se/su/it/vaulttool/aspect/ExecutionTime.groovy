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
    @Before("within(se.su.it.vaulttool.VaultRestService)")
    void timeBefore(JoinPoint joinPoint){
        before = new Date()
    }

    @After("within(se.su.it.vaulttool.VaultRestService)")
    void timeAfter(JoinPoint joinPoint){
        after = new Date()
        TimeDuration duration = TimeCategory.minus(after, before)
        println("${joinPoint.signature.name} took ${duration} to run")
    }
}

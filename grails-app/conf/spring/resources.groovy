// Place your Spring DSL code here
beans = {
    tokenRenewer(se.su.it.vaulttool.schedule.TokenRenewer) { bean ->
        bean.autowire = true
    }
    xmlns aop:"http://www.springframework.org/schema/aop"
    aspectBean(se.su.it.vaulttool.aspect.ExecutionTime)
    aop.config("proxy-target-class":true) {
    }
}

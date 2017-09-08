// Place your Spring DSL code here
beans = {
    tokenRenewer(se.su.it.vaulttool.schedule.TokenRenewer) { bean ->
        bean.autowire = true
    }
}

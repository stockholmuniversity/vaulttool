package se.su.it.vaulttool

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "dashboard", action: "index")
        "/dashboard/secret/$key**" (controller:"dashboard", action:"secret")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

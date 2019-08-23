package com.brainco.cloud

import com.brainco.cloud.blacklist.util.JwtBlacklistHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class Controller {

    @GetMapping("/request")
    fun requestWithFilter(): String{
        return "request"
    }

    @GetMapping("/logout")
    fun logout(){

    }
}
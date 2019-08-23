package com.brainco.cloud

import com.brainco.cloud.blacklist.JwtBlacklistConfigurer
import com.brainco.cloud.blacklist.util.JwtBlacklistHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@ComponentScan
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var jwtBlacklistHandler: JwtBlacklistHandler

    override fun configure(http: HttpSecurity) {
        http
                .apply(JwtBlacklistConfigurer(jwtBlacklistHandler, "/logout")).and()
    }

}

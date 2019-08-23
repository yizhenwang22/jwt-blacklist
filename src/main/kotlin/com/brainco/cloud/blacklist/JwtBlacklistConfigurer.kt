package com.brainco.cloud.blacklist

import com.brainco.cloud.blacklist.util.JwtBlacklistHandler
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter

/**
 * brainco_cloud
 *
 * @author Yizhen Wang
 */
class JwtBlacklistConfigurer(private val jwtBlacklistHandler: JwtBlacklistHandler, private val logoutPath: String):
        SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    override fun configure(httpSecurity: HttpSecurity){
        httpSecurity.exceptionHandling()
                .and()
                .addFilterBefore(JwtBlacklistCheckFilter(jwtBlacklistHandler), UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(JwtTokenBlacklistFilter(jwtBlacklistHandler, logoutPath), LogoutFilter::class.java)
    }
}
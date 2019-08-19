package com.brainco.cloud.blacklist

import com.brainco.cloud.blacklist.util.JwtBlacklistHandler
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest


/**
 * brainco-cloud
 *
 * @authro Yizhen Wang
 *
 */

class JwtTokenBlacklistFilter(private val jwtBlacklistHandler: JwtBlacklistHandler,
                              private val logoutPath: String): GenericFilterBean() {

    override fun doFilter(req: ServletRequest?, res: ServletResponse?, filterChain: FilterChain?){
        val token = jwtBlacklistHandler.resolveToken(req as HttpServletRequest)
        if(token != null && req.requestURI.toString() == logoutPath){
            jwtBlacklistHandler.revokeToken(token)
            return
        }
        filterChain?.doFilter(req, res)
    }
}
package com.brainco.cloud.blacklist

import com.brainco.cloud.core.ErrorCodes
import com.brainco.cloud.blacklist.exception.ForbiddenException
import com.brainco.cloud.blacklist.util.JwtBlacklistHandler
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * brainco_cloud
 *
 * @author Yizhen Wang
 */
class JwtTokenBlacklistCheckFilter(private val jwtBlacklistHandler: JwtBlacklistHandler): GenericFilterBean() {
    override fun doFilter(req: ServletRequest?, res: ServletResponse?, filterChain: FilterChain?) {
        val token = jwtBlacklistHandler.resolveToken(req as HttpServletRequest)
        if(token != null){
            if(jwtBlacklistHandler.checkExist(token)){
                throw ForbiddenException(errorCode = ErrorCodes.BAD_USER_CREDENTIALS,
                        message = "Authorization Expired")
            }
        }
        filterChain?.doFilter(req, res)
    }
}
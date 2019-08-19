package com.brainco.cloud.blacklist

import com.brainco.cloud.core.ErrorCodes
import com.brainco.cloud.core.exception.RestBaseException
import com.brainco.cloud.blacklist.exception.ForbiddenException
import com.brainco.cloud.blacklist.util.JwtBlacklistHandler
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * brainco_cloud
 *
 * @author Yizhen Wang
 */
class ForbiddenTokenExceptionHandler(private val jwtBlacklistHandler: JwtBlacklistHandler): OncePerRequestFilter() {
    override fun doFilterInternal(req: HttpServletRequest, res: HttpServletResponse, filterChain: FilterChain) {
        try {
            val token = jwtBlacklistHandler.resolveToken(req)
            if(token != null && jwtBlacklistHandler.checkExist(token)){
                throw ForbiddenException(errorCode = ErrorCodes.BAD_USER_CREDENTIALS,
                        message = "Authorization Expired")
            }
            else{
                filterChain.doFilter(req, res)
            }
        } catch (e: Exception) {
            println()
            println()
            println(e.toString())
            println()
            println()
            forbiddenTokenExceptionInternal(req, res, e)
        }
    }

    private fun forbiddenTokenExceptionInternal(req: HttpServletRequest, res: HttpServletResponse, e: Exception){
        when(e) {
            // Explicitly mapping custom exceptions thrown in filter
            is RestBaseException -> {
                res.status = e.status.value()
                res.contentType = "application/json"
                res.writer.write(
                        jacksonObjectMapper()
                                .writeValueAsString(
                                        ExceptionResponseEntity(
                                                e.errorCode.toString(),
                                                e.message!!
                                        )
                                )
                )
            }
            // Retain any framework exception to be handled in original way
            else -> throw e
        }
    }

    private class ExceptionResponseEntity(
            val errorCode: String,
            val message: String
    )
}
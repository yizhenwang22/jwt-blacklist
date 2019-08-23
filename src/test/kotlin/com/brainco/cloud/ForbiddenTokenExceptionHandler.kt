package com.brainco.cloud

import com.brainco.cloud.core.exception.RestBaseException
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
            filterChain.doFilter(req, res)
        } catch (e: Exception) {
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
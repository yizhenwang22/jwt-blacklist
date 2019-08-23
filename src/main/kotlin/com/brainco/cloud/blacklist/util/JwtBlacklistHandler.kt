package com.brainco.cloud.blacklist.util

import com.brainco.cloud.blacklist.nosql.JwtBlacklist
import com.brainco.cloud.blacklist.persistence.JwtBlacklistRepository
import java.util.*
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import jdk.nashorn.internal.runtime.JSType.toLong
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject


/**
 * brainco-cloud
 *
 * @author Yizhen Wang
 */
@Component
class JwtBlacklistHandler {

    @Autowired
    private lateinit var jwtBlacklistRepository: JwtBlacklistRepository

    fun resolveToken(req: HttpServletRequest): String?{
        val bearerToken = req.getHeader("Authorization")
        return if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            bearerToken.substring(7, bearerToken.length)
        } else null
    }

    fun revokeToken(token: String): Boolean {
        val split_string = token.split(".")
        val base64EncodedBody = split_string[1]
        val base64Url = Base64(true)

        val body = String(base64Url.decode(base64EncodedBody))
        val tokenBody = JSONObject(body)

        val sub = UUID.fromString(tokenBody.get("sub") as String)
        val iat = toLong(tokenBody.get("iat"))
        val exp = toLong(tokenBody.get("exp"))

        val timestamp = Timestamp(System.currentTimeMillis())
        val ttl = exp * 1000 - timestamp.time

        if(ttl > 0) {
            val jwtBlacklist = JwtBlacklist(sub.toString() + iat.toString(), ttl)

            if (!jwtBlacklistRepository.existsById(sub.toString() + iat.toString())) {
                jwtBlacklistRepository.save(jwtBlacklist)
                println(sub.toString() + iat.toString())
                return true
            }
        }
        return false
    }

    fun invokeToken(token: String): Boolean{
        val split_string = token.split(".")
        val base64EncodedBody = split_string[1]
        val base64Url = Base64(true)

        val body = String(base64Url.decode(base64EncodedBody))
        val tokenBody = JSONObject(body)

        val sub = UUID.fromString(tokenBody.get("sub") as String)
        val iat = toLong(tokenBody.get("iat"))
        val exp = toLong(tokenBody.get("exp"))

        if(jwtBlacklistRepository.existsById(sub.toString() + iat.toString())){
            jwtBlacklistRepository.deleteById(sub.toString() + iat.toString())
            return true
        }

        return false
    }

    fun checkExist(token: String): Boolean{
        val split_string = token.split(".")
        val base64EncodedBody = split_string[1]
        val base64Url = Base64(true)

        val body = String(base64Url.decode(base64EncodedBody))
        val tokenBody = JSONObject(body)

        val sub = UUID.fromString(tokenBody.get("sub") as String)
        val iat = toLong(tokenBody.get("iat"))
        val exp = toLong(tokenBody.get("exp"))

        println(sub.toString() + iat.toString())

        return jwtBlacklistRepository.existsById(sub.toString() + iat.toString())
    }

    fun checkBlacklistRemainingTime(token: String): String{
        val split_string = token.split(".")
        val base64EncodedBody = split_string[1]
        val base64Url = Base64(true)

        val body = String(base64Url.decode(base64EncodedBody))
        val tokenBody = JSONObject(body)

        val sub = UUID.fromString(tokenBody.get("sub") as String)
        val iat = toLong(tokenBody.get("iat"))
        val exp = toLong(tokenBody.get("exp"))

        if(jwtBlacklistRepository.existsById(sub.toString() + iat.toString())) {
            val jwtBlacklist = jwtBlacklistRepository.findById(sub.toString() + iat.toString())

            return checkExist(token).toString() + jwtBlacklist.get().ttl.toString()
        }
        return "not found"
    }
}
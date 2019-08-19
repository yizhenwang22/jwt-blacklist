package com.brainco.cloud.blacklist.util

import com.brainco.cloud.blacklist.nosql.JwtBlacklist
import com.brainco.cloud.blacklist.persistence.JwtBlacklistRepository
import java.util.*
import javax.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp


/**
 * brainco-cloud
 *
 * @author Yizhen Wang
 */
@Component
class JwtBlacklistHandler(private var signingKey: String = "brainco-cloud-test-secret") {

    @Autowired
    private lateinit var jwtBlacklistRepository: JwtBlacklistRepository

    fun resolveToken(req: HttpServletRequest): String?{
        val bearerToken = req.getHeader("Authorization")
        return if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            bearerToken.substring(7, bearerToken.length)
        } else null
    }

    fun revokeToken(token: String): Boolean {
        val claims = Jwts.parser()
                .setSigningKey(signingKey.toByteArray())
                .parseClaimsJws(token).body

        val sub: UUID
        try {
            sub = UUID.fromString(claims.subject)
        }
        catch (e: IllegalArgumentException ){
            return false
        }

        val iat = claims.issuedAt
        val exp = claims.expiration
        val timestamp = Timestamp(System.currentTimeMillis())
        val ttl = exp.time - timestamp.time

        if(ttl > 0) {
            val jwtBlacklist = JwtBlacklist(sub.toString() + iat.toString(), ttl)

            if (!jwtBlacklistRepository.existsById(sub.toString() + iat.toString())) {
                jwtBlacklistRepository.save(jwtBlacklist)
                return true
            }
        }
        return false
    }

    fun invokeToken(token: String): Boolean{
        val claims = Jwts.parser()
                .setSigningKey(signingKey.toByteArray())
                .parseClaimsJws(token).body

        val sub: UUID
        try {
            sub = UUID.fromString(claims.subject)
        }
        catch (e: IllegalArgumentException ){
            return false
        }
        val iat = claims.issuedAt

        if(jwtBlacklistRepository.existsById(sub.toString() + iat.toString())){
            jwtBlacklistRepository.deleteById(sub.toString() + iat.toString())
            return true
        }

        return false
    }

    fun checkExist(token: String): Boolean{
        val claims = Jwts.parser()
                .setSigningKey(signingKey.toByteArray())
                .parseClaimsJws(token).body

        val sub: UUID
        try {
            sub = UUID.fromString(claims.subject)
        }
        catch (e: IllegalArgumentException ){
            return false
        }
        val iat = claims.issuedAt

        return jwtBlacklistRepository.existsById(sub.toString() + iat.toString())
    }

    fun checkBlacklistRemainingTime(token: String): String{
        val claims = Jwts.parser()
                .setSigningKey(signingKey.toByteArray())
                .parseClaimsJws(token).body

        val sub: UUID
        try {
            sub = UUID.fromString(claims.subject)
        }
        catch (e: IllegalArgumentException ){
            return false.toString()
        }

        val iat = claims.issuedAt

        if(jwtBlacklistRepository.existsById(sub.toString() + iat.toString())) {
            val jwtBlacklist = jwtBlacklistRepository.findById(sub.toString() + iat.toString())

            return checkExist(token).toString() + jwtBlacklist.get().ttl.toString()
        }
        return "not found"
    }
}
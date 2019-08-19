package com.brainco.cloud.blacklist.nosql

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive

/**
 * brainco-cloud
 *
 * @author Yizhen Wang
 *
 */
@RedisHash
class JwtBlacklist(@Id val subject: String, val milliTTL: Long?) {

    @TimeToLive
    var ttl = when {
        milliTTL == null -> throw Exception()
        milliTTL < 0 -> throw Exception()
        else -> milliTTL / 1000
    }
}
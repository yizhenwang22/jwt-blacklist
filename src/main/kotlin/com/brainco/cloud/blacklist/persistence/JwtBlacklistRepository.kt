package com.brainco.cloud.blacklist.persistence

import com.brainco.cloud.blacklist.nosql.JwtBlacklist
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


/**
 * Repository to save blacklisted token that has been used but not disabled
 *
 * @auther Yizhen Wang
 */

@Repository
interface JwtBlacklistRepository: CrudRepository<JwtBlacklist, String> {
}
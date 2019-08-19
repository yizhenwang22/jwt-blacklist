package com.brainco.cloud.blacklist.exception

import com.brainco.cloud.core.ErrorCodes
import com.brainco.cloud.core.exception.RestBaseException
import org.springframework.http.HttpStatus

/**
 * brainco_cloud
 *
 * @author Yizhen Wang
 */
class ForbiddenException(errorCode: ErrorCodes, message: String, cause: Throwable? = null)
    : RestBaseException(errorCode = errorCode, status = HttpStatus.FORBIDDEN, message = message, cause = cause) {

}
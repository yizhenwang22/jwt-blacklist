package com.brainco.cloud

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import org.springframework.http.*
import org.springframework.http.HttpEntity
import org.springframework.web.client.HttpStatusCodeException


@RunWith(SpringRunner::class)
@SpringBootTest/*(classes = [JwtBlacklistApplication::class])*/

class JwtBlacklistApplicationTests {

	val restTemplate = RestTemplate()
	val authUri = "http://localhost:9098/auth/login"
	val uri1 = "http://localhost:8991/request"
	val uri2 = "http://localhost:8991/logout"
	val httpHeaders = HttpHeaders()
	val authHeaders = HttpHeaders()

	@Test
	fun contextLoads() {
	}

	@Test
	fun requestTest() {
		httpHeaders.add("Content-Type", "application/json")
		val requestEntity = HttpEntity("", httpHeaders)
		val responseEntity = restTemplate.exchange(uri1, HttpMethod.GET, requestEntity, String::class.java)
		assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.OK)
	}

	@Test
	fun blacklistTest() {
		authHeaders.add("Content-Type", "application/json")
		val obj = JSONObject()
		obj.put("email", "yizhen.wang@brainco.tech")
		obj.put("password", "123456")
		val authRequestEntity = HttpEntity(obj.toString(), authHeaders)
		val authResponseEntity = restTemplate.exchange(authUri, HttpMethod.POST, authRequestEntity, String::class.java)
		val authResponseBody = authResponseEntity.body
		val authResponseBodyJson = JSONObject(authResponseBody)
		val token = "Bearer " + authResponseBodyJson.get("token")

		httpHeaders.add("Content-Type", "application/json")
		httpHeaders.set("Authorization", token)
		val requestEntity = HttpEntity(null, httpHeaders)

		val responseEntityRequestNonBlacklisted = restTemplate.exchange(uri1, HttpMethod.GET, requestEntity, String::class.java)
		assertThat(responseEntityRequestNonBlacklisted.statusCode).isEqualTo(HttpStatus.OK)

		val responseEntityLogout = restTemplate.exchange(uri2, HttpMethod.GET, requestEntity, String::class.java)
		assertThat(responseEntityLogout.statusCode).isEqualTo(HttpStatus.OK)

		var statusCode = HttpStatus.OK
		try {
			restTemplate.exchange(uri1, HttpMethod.GET, requestEntity, String::class.java)
		} catch (e: HttpStatusCodeException){
			statusCode = e.statusCode
		}
		assertThat(statusCode).isEqualTo(HttpStatus.FORBIDDEN)
	}
}

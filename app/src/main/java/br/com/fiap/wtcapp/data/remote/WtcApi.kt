package br.com.fiap.wtcapp.data.remote

import br.com.fiap.wtcapp.data.remote.dto.LoginRequestDto
import br.com.fiap.wtcapp.data.remote.dto.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/** Typed Retrofit description of the WTC REST API. */
interface WtcApi {
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): LoginResponseDto
}

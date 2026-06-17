package br.com.fiap.wtcapp.data.remote

import br.com.fiap.wtcapp.data.remote.dto.AttachmentUploadResponseDto
import br.com.fiap.wtcapp.data.remote.dto.AuditResponseDto
import br.com.fiap.wtcapp.data.remote.dto.AuditSummaryDto
import br.com.fiap.wtcapp.data.remote.dto.CampaignRequestDto
import br.com.fiap.wtcapp.data.remote.dto.CampaignResponseDto
import br.com.fiap.wtcapp.data.remote.dto.ChatMessageRequestDto
import br.com.fiap.wtcapp.data.remote.dto.ConversationRequestDto
import br.com.fiap.wtcapp.data.remote.dto.ConversationResponseDto
import br.com.fiap.wtcapp.data.remote.dto.CustomerRequestDto
import br.com.fiap.wtcapp.data.remote.dto.CustomerResponseDto
import br.com.fiap.wtcapp.data.remote.dto.GoogleLoginRequestDto
import br.com.fiap.wtcapp.data.remote.dto.LoginRequestDto
import br.com.fiap.wtcapp.data.remote.dto.LoginResponseDto
import br.com.fiap.wtcapp.data.remote.dto.MessageResponseDto
import br.com.fiap.wtcapp.data.remote.dto.PageDto
import br.com.fiap.wtcapp.data.remote.dto.RegisterRequestDto
import br.com.fiap.wtcapp.data.remote.dto.RegisterResponseDto
import br.com.fiap.wtcapp.data.remote.dto.SegmentRequestDto
import br.com.fiap.wtcapp.data.remote.dto.SegmentResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Typed Retrofit description of the WTC REST API. Each endpoint carries its full path
 * because the backend mixes `/customers` and `/api/v1/...` prefixes. Authenticated
 * endpoints rely on [AuthInterceptor] to attach the bearer token.
 */
@Suppress("TooManyFunctions") // A typed REST surface legitimately has one function per endpoint.
interface WtcApi {
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): LoginResponseDto

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto,
    ): RegisterResponseDto

    @POST("api/v1/auth/google")
    suspend fun loginWithGoogle(
        @Body request: GoogleLoginRequestDto,
    ): LoginResponseDto

    @GET("customers")
    suspend fun listCustomers(
        @Query("vip") vip: Boolean?,
        @Query("fidelidade") loyalty: Boolean?,
        @Query("ativo") active: Boolean?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): PageDto<CustomerResponseDto>

    @POST("customers")
    suspend fun createCustomer(
        @Body request: CustomerRequestDto,
    ): CustomerResponseDto

    @GET("api/v1/segments")
    suspend fun listSegments(): List<SegmentResponseDto>

    @POST("api/v1/segments")
    suspend fun createSegment(
        @Body request: SegmentRequestDto,
    ): SegmentResponseDto

    @GET("api/v1/campaigns")
    suspend fun listCampaigns(): List<CampaignResponseDto>

    @POST("api/v1/campaigns")
    suspend fun createCampaign(
        @Body request: CampaignRequestDto,
    ): CampaignResponseDto

    @GET("api/v1/conversations/customer/{customerId}")
    suspend fun listConversations(
        @Path("customerId") customerId: String,
    ): List<ConversationResponseDto>

    @POST("api/v1/conversations")
    suspend fun createConversation(
        @Body request: ConversationRequestDto,
    ): ConversationResponseDto

    @GET("api/v1/messages/conversation/{conversationId}")
    suspend fun listMessages(
        @Path("conversationId") conversationId: String,
    ): List<MessageResponseDto>

    @POST("api/v1/conversations/{conversationId}/messages")
    suspend fun sendReply(
        @Path("conversationId") conversationId: String,
        @Body request: ChatMessageRequestDto,
    ): MessageResponseDto

    @GET("api/v1/audit")
    suspend fun listAuditEvents(): List<AuditResponseDto>

    @GET("api/v1/audit/summary")
    suspend fun getAuditSummary(): AuditSummaryDto

    /**
     * Uploads a photo as multipart to the backend, which stores it and returns the URL
     * to display it. Replaces the former pre-signed S3/MinIO flow.
     */
    @Multipart
    @POST("api/v1/attachments")
    suspend fun uploadAttachment(
        @Part file: MultipartBody.Part,
    ): AttachmentUploadResponseDto
}

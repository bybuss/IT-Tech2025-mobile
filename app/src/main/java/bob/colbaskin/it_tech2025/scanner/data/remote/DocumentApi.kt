package bob.colbaskin.it_tech2025.scanner.data.remote

import bob.colbaskin.it_tech2025.scanner.data.models.DocumentCheckResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface DocumentApi {

    @GET("/api/v1/document/check/{base64_doc_data}")
    suspend fun checkDocument(
        @Path("base64_doc_data") hash: String
    ): Response<DocumentCheckResponse>
}

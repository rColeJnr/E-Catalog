import retrofit2.http.GET
import retrofit2.http.Query

interface GutenbergApi {
    @GET("/books")
    suspend fun fetchBooks(

    )

//    @GET("/books")
//    susp

}
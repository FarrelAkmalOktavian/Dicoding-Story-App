package com.example.dicodingstoryappselangkahmenujukebebasan.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.ListStoryItem
import com.example.dicodingstoryappselangkahmenujukebebasan.data.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class StoryPagingSource(
    private val apiService: ApiService,
    private val token: String
) : PagingSource<Int, ListStoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val page = params.key ?: 1
        return try {
            val authorization = "Bearer $token"
            val response = apiService.getStories(authorization, page, params.loadSize)
            val stories = response.listStory

            LoadResult.Page(
                data = stories,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (stories.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition
    }
}

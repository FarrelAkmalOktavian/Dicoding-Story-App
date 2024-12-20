package com.example.dicodingstoryappselangkahmenujukebebasan

import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.ListStoryItem
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResult
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.StoryResponse

object DataDummy {

    fun generateDummyStoryResponse(): StoryResponse {
        val storiesList = mutableListOf<ListStoryItem>()
        for (index in 1..15) {
            val story = ListStoryItem(
                createdAt = "2024-12-20T14:45:00Z",
                description = "Description for story number $index",
                id = "sample_story_$index",
                lat = index.toDouble() * 3,
                lon = index.toDouble() * 4,
                name = "Author $index",
                photoUrl = "https://example.org/images/sample_story_$index.jpg"
            )
            storiesList.add(story)
        }

        return StoryResponse(
            error = false,
            message = "Sample stories fetched successfully",
            listStory = storiesList
        )
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "User successfully logged in",
            loginResult = LoginResult(
                userId = "user-def789_ghi012",
                name = "Jane Smith",
                token = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWRlZjc4OV9naGkwMTIiLCJpYXQiOjE2ODAwNzYwMDB9.zfQRS7QzHvndOf8Tpg2FHRt2Nds5EBKR9kgVsJLkGdA"
            )
        )
    }
}
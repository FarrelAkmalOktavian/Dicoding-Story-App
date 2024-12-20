package com.example.dicodingstoryappselangkahmenujukebebasan.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.dicodingstoryappselangkahmenujukebebasan.data.pref.UserModel
import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
import com.example.dicodingstoryappselangkahmenujukebebasan.data.response.LoginResponse
import com.example.dicodingstoryappselangkahmenujukebebasan.data.result.Result
import com.example.dicodingstoryappselangkahmenujukebebasan.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class LoginViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(userRepository)

        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when login is loading should return Loading result`() = runTest {
        val loadingResult = Result.Loading
        val flowResult = flow {
            emit(loadingResult)
        }

        Mockito.`when`(userRepository.login("test@example.com", "password123"))
            .thenReturn(flowResult)

        loginViewModel.login("test@example.com", "password123")

        val result = loginViewModel.loginResult.getOrAwaitValue()
        Assert.assertTrue(result is Result.Loading)
    }

    @Test
    fun `when login is successful should return Success result`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val loginResponse = LoginResponse(error = false, loginResult = null, message = "Login successful")
        val result = Result.Success(loginResponse)

        val flowResult = flow {
            emit(result)
        }

        Mockito.`when`(userRepository.login(email, password)).thenReturn(flowResult)

        loginViewModel.login(email, password)

        val loginResult = loginViewModel.loginResult.getOrAwaitValue()

        Assert.assertTrue(loginResult is Result.Success)
        val successData = loginResult as Result.Success
        Assert.assertEquals(loginResponse, successData.data)
    }

    @Test
    fun `when login fails should return Error result`() = runTest {
        val email = "wrong@example.com"
        val password = "wrongpassword"
        val errorMessage = "Invalid login credentials"
        val errorResult = Result.Error(errorMessage)

        val flowResult = flow {
            emit(errorResult)
        }

        Mockito.`when`(userRepository.login(email, password)).thenReturn(flowResult)

        loginViewModel.login(email, password)

        val loginResult = loginViewModel.loginResult.getOrAwaitValue()

        Assert.assertTrue(loginResult is Result.Error)
        val errorData = loginResult as Result.Error
        Assert.assertEquals(errorMessage, errorData.message)
    }

    @Test
    fun `saveSession should call saveSession method in UserRepository`() = runTest {
        val fakeUser = UserModel(
            email = "test@example.com",
            token = "token123",
            isLogin = true
        )

        loginViewModel.saveSession(fakeUser)

        Mockito.verify(userRepository).storeSession(fakeUser)
    }
}
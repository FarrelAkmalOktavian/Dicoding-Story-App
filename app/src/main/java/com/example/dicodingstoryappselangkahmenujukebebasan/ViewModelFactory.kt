    package com.example.dicodingstoryappselangkahmenujukebebasan

    import android.content.Context
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import com.example.dicodingstoryappselangkahmenujukebebasan.data.repository.UserRepository
    import com.example.dicodingstoryappselangkahmenujukebebasan.di.Injection
    import com.example.dicodingstoryappselangkahmenujukebebasan.ui.addstory.AddStoryViewModel
    import com.example.dicodingstoryappselangkahmenujukebebasan.ui.login.LoginViewModel
    import com.example.dicodingstoryappselangkahmenujukebebasan.ui.main.MainViewModel
    import com.example.dicodingstoryappselangkahmenujukebebasan.ui.map.MapsViewModel
    import com.example.dicodingstoryappselangkahmenujukebebasan.ui.register.RegisterViewModel

    class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(repository) as T
                }
                modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                    MainViewModel(repository) as T
                }
                modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                    RegisterViewModel(repository) as T
                }
                modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                    AddStoryViewModel(repository) as T
                }
                modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                    MapsViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }

        companion object {
            @Volatile
            private var INSTANCE: ViewModelFactory? = null

            @JvmStatic
            suspend fun getInstance(context: Context): ViewModelFactory {
                val repository = Injection.provideRepository(context)
                return INSTANCE ?: synchronized(this) {
                    INSTANCE ?: ViewModelFactory(repository).also {
                        INSTANCE = it
                    }
                }
            }
        }
    }
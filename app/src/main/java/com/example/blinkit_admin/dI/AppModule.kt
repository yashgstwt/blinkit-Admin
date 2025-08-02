package com.example.blinkit_admin.dI

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSupabaseInstance(): SupabaseClient {
        val supabase = createSupabaseClient(
            supabaseUrl = "https://xpcvzteqrrgkblzrumaj.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhwY3Z6dGVxcnJna2JsenJ1bWFqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDUzODY1MjUsImV4cCI6MjA2MDk2MjUyNX0.-dcNwIU1kY7GjJ1huYQ9mLN7KMuEeg59EAu92-KloTA"
        ) {
            install(Postgrest)
        }
        return supabase

    }

}
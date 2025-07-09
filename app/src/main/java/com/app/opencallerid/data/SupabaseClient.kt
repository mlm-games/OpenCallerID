package com.app.opencallerid.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    private const val SUPABASE_URL = "https://my-project-id-to-add-later.supabase.co"
    private const val SUPABASE_KEY = "api-key-to-add"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }
}
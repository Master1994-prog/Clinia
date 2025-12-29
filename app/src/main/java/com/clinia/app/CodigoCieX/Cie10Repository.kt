package com.clinia.app.CodigoCieX

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class Cie10Repository(
    private val context: Context
) {
    @Volatile private var cache: List<Cie10Item> = emptyList()

    suspend fun ensureLoaded() {
        if (cache.isNotEmpty()) return

        cache = withContext(Dispatchers.IO) {
            context.assets.open("cie10_minsa.csv").use { input ->
                BufferedReader(InputStreamReader(input, Charsets.UTF_8)).use { br ->
                    val lines = br.readLines()
                    if (lines.isEmpty()) return@withContext emptyList()

                    lines.drop(1).mapNotNull { line ->
                        val parts = line.split(",")
                        if (parts.size < 2) null
                        else Cie10Item(
                            codigo = parts[0].trim(),
                            descripcion = parts.drop(1).joinToString(",").trim()
                        )
                    }
                }
            }
        }
    }

    fun search(query: String, limit: Int = 20): List<Cie10Item> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()

        return cache.asSequence()
            .filter {
                it.codigo.lowercase().contains(q) ||
                        it.descripcion.lowercase().contains(q)
            }
            .take(limit)
            .toList()
    }
}

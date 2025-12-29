package com.clinia.app.PetitorioPNUME

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class PnumRepository(private val context: Context) {

    @Volatile private var cache: List<PnumItem> = emptyList()

    suspend fun ensureLoaded(assetName: String = "pnume.csv") {
        if (cache.isNotEmpty()) return

        cache = withContext(Dispatchers.IO) {
            val text = context.assets.open(assetName)
                .bufferedReader(Charsets.UTF_8)
                .use { it.readText() }

            val lines = text.split("\n").map { it.trimEnd('\r') }.filter { it.isNotBlank() }
            if (lines.isEmpty()) return@withContext emptyList()

            val header = parseCsvLine(lines.first()).map { it.trim().lowercase() }

            fun idx(col: String): Int = header.indexOf(col)

            val iDci = idx("denominacion_comun_internacional")
            val iConc = idx("concentracion")
            val iForma = idx("forma_farmaceutica")
            val iPres = idx("presentacion")
            val iCons = idx("consideraciones_especiales_de_uso")
            val iGrupo = idx("grupo")
            val iAuto = idx("autorizacion_de_uso")

            // Si falta alguna columna clave, mejor fallar “suave” devolviendo vacío
            if (iDci < 0) return@withContext emptyList()

            lines.drop(1).mapNotNull { line ->
                val parts = parseCsvLine(line)
                val dci = parts.getOrNull(iDci)?.trim().orEmpty()
                if (dci.isBlank()) return@mapNotNull null

                PnumItem(
                    dci = dci,
                    concentracion = parts.getOrNull(iConc)?.trim().orEmpty(),
                    formaFarmaceutica = parts.getOrNull(iForma)?.trim().orEmpty(),
                    presentacion = parts.getOrNull(iPres)?.trim().orEmpty(),
                    consideraciones = parts.getOrNull(iCons)?.trim().orEmpty(),
                    grupo = parts.getOrNull(iGrupo)?.trim().orEmpty(),
                    autorizacion = parts.getOrNull(iAuto)?.trim().orEmpty()
                )
            }
        }
    }

    /**
     * ✅ Allowlist por DCI:
     * - incluye la DCI completa
     * - y una variante SIN paréntesis (ej: "Ketamina (como clorhidrato)" -> "Ketamina")
     */
    fun allowedDciSet(): Set<String> {
        val out = HashSet<String>()
        cache.forEach { item ->
            val full = normalize(item.dci)
            if (full.isNotBlank()) out.add(full)

            val noParen = normalize(removeParenthesis(item.dci))
            if (noParen.isNotBlank()) out.add(noParen)
        }
        return out
    }

    fun findByDci(dci: String): List<PnumItem> {
        val key = normalize(dci)
        return cache.filter { normalize(it.dci) == key || normalize(removeParenthesis(it.dci)) == key }
    }

    private fun removeParenthesis(s: String): String =
        s.replace(Regex("\\s*\\(.*?\\)\\s*"), " ").replace(Regex("\\s+"), " ").trim()

    private fun normalize(s: String): String =
        s.trim().lowercase()
            .replace("á","a").replace("é","e").replace("í","i")
            .replace("ó","o").replace("ú","u").replace("ñ","n")

    /**
     * Parser CSV simple con comillas:
     * maneja casos tipo: "Ketamina (como clorhidrato)",50mg/mL,INY,10mL,...
     */
    private fun parseCsvLine(line: String): List<String> {
        val result = ArrayList<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' -> {
                    inQuotes = !inQuotes
                }
                c == ',' && !inQuotes -> {
                    result.add(sb.toString())
                    sb.clear()
                }
                else -> sb.append(c)
            }
            i++
        }
        result.add(sb.toString())
        return result
    }
}

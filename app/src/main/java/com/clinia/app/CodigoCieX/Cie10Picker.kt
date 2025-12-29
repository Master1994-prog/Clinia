package com.clinia.app.CodigoCieX

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Cie10Picker(
    label: String = "Diagnóstico (CIE-10)",
    query: String,
    onQueryChange: (String) -> Unit,
    results: List<Cie10Item>,
    onPick: (Cie10Item) -> Unit
) {
    Column {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )

        if (results.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .background(Color.White, RoundedCornerShape(14.dp))
            ) {
                items(results) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPick(item) }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = item.codigo,
                            modifier = Modifier.width(90.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(item.descripcion)
                    }
                }
            }
        }
    }
}

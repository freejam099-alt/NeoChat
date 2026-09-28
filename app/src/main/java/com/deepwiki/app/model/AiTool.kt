package com.deepwiki.app.model

import kotlinx.serialization.Serializable

@Serializable
data class ToolParameter(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean = true
)

@Serializable
data class ToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<ToolParameter>
)

object BuiltinAiTools {
    val WEB_SEARCH = ToolDefinition(
        name = "web_search",
        description = "Mencari informasi terkini di web, berita, riset, atau dokumentasi teknis secara real-time.",
        parameters = listOf(
            ToolParameter("query", "string", "Kata kunci pencarian yang spesifik dan efektif"),
            ToolParameter("max_results", "number", "Jumlah hasil pencarian yang diinginkan (default 5)", false)
        )
    )

    val MEMORY_TOOLS = ToolDefinition(
        name = "memory_tools",
        description = "Menyimpan atau mengambil ingatan/konteks jangka panjang pengguna di database DeepWiki.",
        parameters = listOf(
            ToolParameter("action", "string", "Pilihan aksi: 'store', 'retrieve', 'delete', atau 'list'"),
            ToolParameter("key", "string", "Kunci pengenal memori"),
            ToolParameter("content", "string", "Konten memori yang akan disimpan (diperlukan jika action='store')", false)
        )
    )

    val MCP_CHECK = ToolDefinition(
        name = "mcp_check",
        description = "Mengecek konektivitas, kesehatan, dan status endpoint server MCP (Model Context Protocol).",
        parameters = listOf(
            ToolParameter("server_id", "string", "ID server MCP yang ingin diperiksa statusnya")
        )
    )

    val MCP_LIST = ToolDefinition(
        name = "mcp_list",
        description = "Melihat daftar seluruh server MCP terdaftar beserta tools yang diekspos oleh tiap server.",
        parameters = emptyList()
    )

    val MCP_USE = ToolDefinition(
        name = "mcp_use",
        description = "Mengaktifkan atau menonaktifkan server MCP tertentu untuk sesi percakapan ini.",
        parameters = listOf(
            ToolParameter("server_id", "string", "ID server MCP"),
            ToolParameter("enable", "boolean", "true untuk mengaktifkan, false untuk mematikan")
        )
    )

    val MCP_TOOLS_USE = ToolDefinition(
        name = "mcp_tools_use",
        description = "Mengeksekusi tool spesifik yang disediakan oleh salah satu server MCP yang sedang aktif.",
        parameters = listOf(
            ToolParameter("server_id", "string", "ID server MCP penyedia tool"),
            ToolParameter("tool_name", "string", "Nama tool MCP yang akan dipanggil"),
            ToolParameter("arguments", "object", "Argumen parameter JSON untuk tool MCP tersebut")
        )
    )

    val ALL_TOOLS = listOf(
        WEB_SEARCH,
        MEMORY_TOOLS,
        MCP_CHECK,
        MCP_LIST,
        MCP_USE,
        MCP_TOOLS_USE
    )
}

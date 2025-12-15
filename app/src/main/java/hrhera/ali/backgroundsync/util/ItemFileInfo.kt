
package hrhera.ali.backgroundsync.util

data class ItemFileInfo(
        val orignalFilePath: String,
        val chankSizeInMb: Int,
        val itemName: String,
        val folderName: String,
        val parts: Map<Int, String>,
        val createdAt: Long=System.currentTimeMillis(),
    )
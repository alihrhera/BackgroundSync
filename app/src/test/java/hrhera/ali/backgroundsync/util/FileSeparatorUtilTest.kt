package hrhera.ali.backgroundsync.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.FileOutputStream


@RunWith(RobolectricTestRunner::class)
class FileSeparatorUtilTest {

    private lateinit var context: Context
    private lateinit var inputFile: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        inputFile = File(context.cacheDir, "input.txt")
        val data = ByteArray(3 * 1024 * 1024) { 1 }
        FileOutputStream(inputFile).use { it.write(data) }
    }

    @After
    fun tearDown() {
        context.cacheDir.deleteRecursively()
    }

    @Test
    fun `GIVEN new file WHEN split THEN create parts and info json`() {
        // GIVEN
        val itemId = "item1"
        val chunkSizeMb = 1

        // WHEN
        val result = FileSeparatorUtil.splitFileToChach(
            context = context,
            inputFile = inputFile,
            itemId = itemId,
            chunkSize = chunkSizeMb
        )

        // THEN
        val itemDir = File(context.cacheDir, itemId)
        val jsonFile = File(itemDir, "info.json")

        assertTrue(itemDir.exists())
        assertTrue(jsonFile.exists())
        assertEquals(3, result.parts.size)
    }

     @Test
    fun `GIVEN valid cached data WHEN split called again THEN return old info`() {
        // GIVEN
        val itemId = "item2"
        val chunkSizeMb = 1

        FileSeparatorUtil.splitFileToChach(
            context,
            inputFile,
            itemId,
            chunkSizeMb
        )

        // WHEN
        val result = FileSeparatorUtil.splitFileToChach(
            context,
            inputFile,
            itemId,
            chunkSizeMb
        )

        // THEN
        assertEquals(3, result.parts.size)
        assertEquals(inputFile.absolutePath, result.orignalFilePath)
    }


        @Test
        fun `GIVEN existing cache WHEN chunk size changes THEN recreate cache`() {
            // GIVEN
            val itemId = "item3"

            FileSeparatorUtil.splitFileToChach(
                context,
                inputFile,
                itemId,
                1
            )

            // WHEN
            val result = FileSeparatorUtil.splitFileToChach(
                context,
                inputFile,
                itemId,
                2
            )

            // THEN
            assertEquals(2, result.parts.size)
        }

        @Test
        fun `GIVEN missing part file WHEN split THEN recreate cache`() {
            // GIVEN
            val itemId = "item4"
            val chunkSizeMb = 1

            val oldResult = FileSeparatorUtil.splitFileToChach(
                context,
                inputFile,
                itemId,
                chunkSizeMb
            )

            File(oldResult.parts[0]!!).delete()

            // WHEN
            val newResult = FileSeparatorUtil.splitFileToChach(
                context,
                inputFile,
                itemId,
                chunkSizeMb
            )

            // THEN
            assertEquals(3, newResult.parts.size)
        }

}

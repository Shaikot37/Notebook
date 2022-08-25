package com.shaikot.notebook.google

import android.util.Base64
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import com.shaikot.notebook.util.ByteSegments
import com.shaikot.notebook.util.FileInputSource
import java.io.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class GoogleDriveApiDataRepository(private val mDriveService: Drive) {
    private val FILE_MIME_TYPE = "text/plain"
    private val APP_DATA_FOLDER_SPACE = "appDataFolder"
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()
    fun uploadFile(file: File, fileName: String): Task<Void?> {
        return createFile(fileName)
            .continueWithTask(
                mExecutor
            ) { task: Task<String?> ->
                val fileId =
                    task.result ?: throw IOException("Null file id when requesting file upload.")
                writeFile(file, fileId, fileName)
            }
    }

    fun downloadFile(file: File, fileName: String): Task<Void?> {
        return queryFiles()
            .continueWithTask(
                mExecutor
            ) { task: Task<FileList?> ->
                val fileList = task.result
                    ?: throw IOException("Null file list when requesting file download.")
                var currentFile: com.google.api.services.drive.model.File? = null
                for (f in fileList.files) {
                    if (f.name == fileName) {
                        currentFile = f
                        break
                    }
                }
                if (currentFile == null) {
                    throw IOException("File not found when requesting file download.")
                }
                val fileId = currentFile.id
                readFile(file, fileId)
            }
    }

    private fun readFile(
        file: File,
        fileId: String
    ): Task<Void?> {
        return Tasks.call(mExecutor) {
            var encoded: String
            mDriveService.files()[fileId].executeMediaAsInputStream().use { `is` ->
                BufferedReader(InputStreamReader(`is`)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    encoded = stringBuilder.toString()
                }
            }
            val decoded =
                Base64.decode(encoded, Base64.DEFAULT)
            FileOutputStream(file).use { stream -> stream.write(decoded) }
            null
        }
    }

    fun queryFiles(): Task<FileList?> {
        return Tasks.call(
            mExecutor
        ) {
            mDriveService.files().list().setSpaces(APP_DATA_FOLDER_SPACE).execute()
        }
    }

    private fun writeFile(
        file: File,
        fileId: String,
        fileName: String
    ): Task<Void?> {
        return Tasks.call(mExecutor) {
            val metadata = getMetaData(fileName)
            val bytes = ByteSegments.toByteArray(FileInputSource(file))
            val encoded =
                Base64.encodeToString(bytes, Base64.DEFAULT)
            val contentStream =
                ByteArrayContent.fromString(FILE_MIME_TYPE, encoded)

            // Update the metadata and contents.
            mDriveService.files().update(fileId, metadata, contentStream).execute()
            null
        }
    }

    private fun createFile(fileName: String): Task<String?> {
        return Tasks.call(mExecutor) {
            val metadata = getMetaData(fileName)
            metadata.parents = listOf(APP_DATA_FOLDER_SPACE)
            val googleFile =
                mDriveService.files().create(metadata).execute()
                    ?: throw IOException("Null result when requesting file creation.")
            googleFile.id
        }
    }

    private fun getMetaData(fileName: String): com.google.api.services.drive.model.File {
        return com.google.api.services.drive.model.File()
            .setMimeType(FILE_MIME_TYPE)
            .setName(fileName)
    }
}

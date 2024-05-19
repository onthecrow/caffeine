package com.onthecrow.caffeine.data

import androidx.datastore.core.Serializer
import com.onthecrow.caffeine.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class SettingsSerializer @Inject constructor(
    @DispatcherIO private val dispatcher: CoroutineDispatcher
): Serializer<CaffeineSettings> {

    override val defaultValue: CaffeineSettings
        get() = CaffeineSettings.DEFAULT

    override suspend fun readFrom(input: InputStream): CaffeineSettings {
        return ProtoBuf.decodeFromByteArray(CaffeineSettings.serializer(), input.readBytes())
    }

    override suspend fun writeTo(t: CaffeineSettings, output: OutputStream) {
        val byteArray = ProtoBuf.encodeToByteArray(CaffeineSettings.serializer(), t)
        withContext(dispatcher) {
            output.write(byteArray)
        }
    }
}
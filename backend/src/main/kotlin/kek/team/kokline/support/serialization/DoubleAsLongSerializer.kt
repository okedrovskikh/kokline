package kek.team.kokline.support.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class DoubleAsLongSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(requireNotNull(this::class.simpleName), PrimitiveKind.DOUBLE)

    override fun deserialize(decoder: Decoder): Long = decoder.decodeDouble().toLong()

    override fun serialize(encoder: Encoder, value: Long) = encoder.encodeLong(value)
}

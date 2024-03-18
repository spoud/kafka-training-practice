package io.spoud.training;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;

import java.io.File;
import java.nio.file.Files;

public class AvroRead
{
    public static void main( String[] args )
    {
        String result = "";
        // read avro message from kafka with a local schema file
        try {
            File schemaFile = new File("kafka-demos/java-read-avro-message-from-file/src/main/resources/schema.avsc");
            schemaFile = new File(schemaFile.getAbsolutePath());
            Schema schema = new Schema.Parser().parse(schemaFile);
            // read file as byte array extracted with e.g. kcat
            byte[] payload = removeFirst5BytesForSchemaId(Files.readAllBytes(new File("kafka-demos/java-read-avro-message-from-file/src/main/resources/premium_kcat.avro").toPath()));
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(payload, null);
            GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
            GenericRecord record = reader.read(null, decoder);
            result += "message:" + record.toString() + "\n";
        } catch(Throwable e) {
            result += "error: " + e.getMessage();
        }
        System.out.println(result);
    }

    private static byte[] removeFirst5BytesForSchemaId(byte[] payload) {
        byte[] result = new byte[payload.length - 5];
        byte[] schemaId = new byte[4];
        System.arraycopy(payload, 5, result, 0, result.length);
        System.arraycopy(payload, 1, schemaId, 0, 4);
        System.out.println("schemaId: " + convertBytesToLong(schemaId));
        return result;
    }

    public static long convertBytesToLong(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            throw new IllegalArgumentException("Byte array must contain at least 4 bytes");
        }
        return ((long) (bytes[0] & 0xff) << 24) |
                ((long) (bytes[1] & 0xff) << 16) |
                ((long) (bytes[2] & 0xff) << 8)  |
                ((long) (bytes[3] & 0xff));
    }
}

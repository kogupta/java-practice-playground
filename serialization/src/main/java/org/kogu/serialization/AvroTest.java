package org.kogu.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.*;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

/**
 * Avro serialization test:
 *
 * Test data:
 * <ul>
 *   <li>Number of objects: 8375</li>
 *   <li>Size of input json file: 14335.04 kb</li>
 *   <li>Time taken for Json deserialization: 712 millis</li>
 * </ul>
 *
 * <p>
 *
 * <pre>
 * ---------  data file writer  ---------
 * with compression --
 *   Size of file [data file writer]: 1019.05 kb
 *   Time taken to serialize: 813 millis
 *   Time taken to de-serialize: 330 millis
 * no compression --
 *   Size of file [data file writer]: 8322.16 kb
 *   Time taken to serialize: 87 millis
 *   Time taken to de-serialize: 122 millis
 *
 * ---------  binary encoder  ---------
 * Size of file[binary encoder]: 3690.86 kb
 * Time taken to serialize: 97 millis
 * Time taken to de-serialize: 135 millis
 * </pre>
 *
 */
public class AvroTest {
  public static void main(String[] args) throws IOException {
//    createSchemaFile(args[0], Pojo.class);

    Pojo[] pojos = parseJsonFile(Paths.get(args[1]));

    Path avroFile = Paths.get("/dev/shm/pojo.avro");
    clear(avroFile);

    System.out.println("---------  data file writer  ---------");
    System.out.println("☑ compression --");
    new FileBasedSerDe(true).validate(pojos, avroFile);
    clear(avroFile);

    System.out.println("no compression --");
    new FileBasedSerDe(false).validate(pojos, avroFile);
    clear(avroFile);

    System.out.println();

    System.out.println("---------  binary encoder  ---------");
    new BinarySerDe().validate(pojos, avroFile);

    avroFile.toFile().deleteOnExit();
  }

  public static void clear(Path p) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(p.toFile(), "rw");
    raf.setLength(0);
  }

  private static Pojo[] parseJsonFile(Path jsonFile) throws IOException {
    File file = jsonFile.toFile();

    ObjectMapper mapper = new ObjectMapper();
    long t0 = System.currentTimeMillis();
    Pojo[] pojos = mapper.readValue(file, Pojo[].class);
    System.out.printf("Time taken for Json deserialization: %d millis%n", System.currentTimeMillis() - t0);

    System.out.println("Number of pojos obtained: " + pojos.length);

    double v = (double) file.length() / 1024;
    System.out.printf("Size of input json file: %.2f kb%n", v);

    return pojos;
  }

  interface SerDe {
    <T> void serializeToFile(T[] pojos, Path avroFile) throws IOException;
    <T> List<T> deserializeFromFile(Path avroFile, Class<T> clazz) throws IOException;

    default <T> void validate(T[] pojos, Path avroFile) {
      if (pojos == null || pojos.length == 0) {
        System.out.println("Nothing to do here ....");
        return;
      }

      try {
        long t0 = System.currentTimeMillis();
        this.serializeToFile(pojos, avroFile);
        System.out.printf("Time taken to serialize: %d millis%n", System.currentTimeMillis() - t0);

        Class<T> aClass = (Class<T>) pojos[0].getClass();
        t0 = System.currentTimeMillis();
        List<T> obtained = this.deserializeFromFile(avroFile, aClass);
        System.out.printf("Time taken to de-serialize: %d millis%n", System.currentTimeMillis() - t0);

        Object[] objects = obtained.toArray();
        assert pojos.length == objects.length;
        assert Arrays.equals(pojos, objects);
      } catch (IOException t) {
        throw new UncheckedIOException(t);
      }
    }
  }

  static class FileBasedSerDe implements SerDe {
    private final boolean isCompressed;

    FileBasedSerDe(boolean isCompressed) {
      this.isCompressed = isCompressed;
    }

    @Override
    public <T> void serializeToFile(T[] pojos, Path avroFile) throws IOException {
      Schema schema = ReflectData.get().getSchema(pojos[0].getClass());
      DatumWriter<T> datumWriter = new ReflectDatumWriter<>(schema);

      File f = avroFile.toFile();

      try(DataFileWriter<T> dataFileWriter = new DataFileWriter<>(datumWriter)) {
        if (isCompressed) {
          dataFileWriter.setCodec(CodecFactory.snappyCodec());
        }

        dataFileWriter.create(schema, f);
        for (T pojo : pojos) {
          dataFileWriter.append(pojo);
        }
      }

      double v = (double) f.length() / 1024;
      System.out.printf("size of file [data file writer]: %.2f kb%n", v);
    }

    @Override
    public <T> List<T> deserializeFromFile(Path avroFile, Class<T> clazz) throws IOException {
      DatumReader<T> reader = new ReflectDatumReader<>(clazz);
      List<T> obtained = new ArrayList<>();

      try (DataFileReader<T> dataFileReader = new DataFileReader<>(avroFile.toFile(), reader)) {
        while (dataFileReader.hasNext()) {
          T next = dataFileReader.next();
          obtained.add(next);
        }
      }

      return obtained;
    }
  }

  static class BinarySerDe implements SerDe {
    @Override
    public <T> void serializeToFile(T[] pojos, Path avroFile) throws IOException {
      Schema schema = ReflectData.get().getSchema(pojos[0].getClass());
      DatumWriter<T> writer = new ReflectDatumWriter<>(schema);

      try (OutputStream os = Files.newOutputStream(avroFile, CREATE, WRITE, TRUNCATE_EXISTING);
           SnappyOutputStream out = new SnappyOutputStream(os)) {
        BinaryEncoder binEncoder = EncoderFactory.get().binaryEncoder(out, null);
        for (T t: pojos) {
          writer.write(t, binEncoder);
        }

        binEncoder.flush();
      }

      double v = (double) (avroFile.toFile().length()) / 1024;
      System.out.printf("size of file[binary encoder]: %.2f kb%n", v);
    }

    @Override
    public <T> List<T> deserializeFromFile(Path avroFile, Class<T> clazz) throws IOException {
      Schema schema = ReflectData.get().getSchema(clazz);
      DatumReader<T> reader = new ReflectDatumReader<>(schema);
      List<T> obtained = new ArrayList<>();
      try (InputStream in = Files.newInputStream(avroFile, READ);
           SnappyInputStream is = new SnappyInputStream(in)) {
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(is, null);
        while (!decoder.isEnd()) {
          T pojo = reader.read(null, decoder);

          obtained.add(pojo);
        }
      }

      return obtained;
    }
  }

  static void createSchemaFile(String dir, Class<Pojo> type) throws IOException {
    Path path = Paths.get(dir, type.getSimpleName() + ".avsc");
    Schema schema = ReflectData.get().getSchema(type);
//    System.out.println(schema.toString(true));
//    System.out.println(schema.toString(false));

    String s = schema.toString(true);
    Files.write(path, s.getBytes(), CREATE, WRITE, TRUNCATE_EXISTING);
  }

}

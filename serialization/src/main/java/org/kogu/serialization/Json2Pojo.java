package org.kogu.serialization;

import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Json2Pojo {
  public static void main(String[] args) throws IOException {
    Path src = Paths.get(args[0]);
    File target = Paths.get(args[1]).toFile();
    Files.createDirectories(target.toPath());

    JCodeModel codeModel = new JCodeModel();
    GenerationConfig config = new DefaultGenerationConfig() {
      @Override
      public boolean isUsePrimitives() {
        return true;
      }

      @Override
      public boolean isIncludeHashcodeAndEquals() {
        return false;
      }

      @Override
      public boolean isIncludeToString() {
        return false;
      }

      @Override
      public boolean isGenerateBuilders() { // set config option by overriding method
        return true;
      }

      @Override
      public SourceType getSourceType() {
        return SourceType.JSON;
      }
    };

    RuleFactory ruleFactory = new RuleFactory(config, new NoopAnnotator(), new SchemaStore());
    SchemaMapper mapper = new SchemaMapper(ruleFactory, new SchemaGenerator());
    URL source = src.toUri().toURL();
    mapper.generate(codeModel, "Pojo", "com.example", source);

    codeModel.build(target);
  }
}

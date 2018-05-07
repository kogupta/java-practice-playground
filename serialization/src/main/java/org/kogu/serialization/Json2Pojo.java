package org.kogu.serialization;

import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.*;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Json2Pojo {
  public static void main(String[] args) throws IOException {
    Path src = Paths.get(args[0]);
    File target = Paths.get(args[1]).toFile();

    JCodeModel codeModel = new JCodeModel();
    GenerationConfig config = new DefaultGenerationConfig() {
      @Override
      public boolean isGenerateBuilders() { // set config option by overriding method
        return true;
      }

      @Override
      public SourceType getSourceType() {
        return SourceType.JSON;
      }
    };

    SchemaMapper mapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
    URL source = src.toUri().toURL();
    mapper.generate(codeModel, "Pojo", "com.example", source);

    codeModel.build(target);
  }
}

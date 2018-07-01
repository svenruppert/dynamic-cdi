/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.reflections.serializers;

import com.google.gson.*;
import org.reflections.Reflections;
import org.reflections.util.Utils;
import repacked.com.google.common.collect.Multimap;
import repacked.com.google.common.collect.Multimaps;
import repacked.com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class JsonSerializer implements Serializer {
  private Gson gson;

  public Reflections read(InputStream inputStream) {
    return getGson().fromJson(new InputStreamReader(inputStream) , Reflections.class);
  }

  public File save(Reflections reflections , String filename) {
    try {
      File file = Utils.prepareFile(filename);
      Files.write(toString(reflections) , file , Charset.defaultCharset());
      return file;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String toString(Reflections reflections) {
    return getGson().toJson(reflections);
  }

  private Gson getGson() {
    if (gson == null) {
      gson = new GsonBuilder()
          .registerTypeAdapter(
              Multimap.class ,
              (com.google.gson.JsonSerializer<Multimap>) (multimap , type , jsonSerializationContext) -> jsonSerializationContext.serialize(multimap.asMap()))
          .registerTypeAdapter(
              Multimap.class ,
              (JsonDeserializer<Multimap>) (jsonElement , type , jsonDeserializationContext) -> {
                final Multimap<String, String> map = Multimaps.newSetMultimap(new HashMap<String, Collection<String>>() , HashSet::new);
                for (Map.Entry<String, JsonElement> entry : ((JsonObject) jsonElement).entrySet()) {
                  for (JsonElement element : (JsonArray) entry.getValue()) {
                    map.put(entry.getKey() , element.getAsString());
                  }
                }
                return map;
              })
          .setPrettyPrinting()
          .create();

    }
    return gson;
  }
}

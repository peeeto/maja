package net.io_0.maja.models;

import lombok.*;
import net.io_0.maja.WithUnconventionalName;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
@ToString
@Builder @AllArgsConstructor
public class Deep {
  @WithUnconventionalName("obj") private Nested objectToPojo;
  private Map<String, Object> objectToMap;
  private List<Nested> objectArrayToObjectList;
  private Set<Nested> objectArrayToObjectSet;
}

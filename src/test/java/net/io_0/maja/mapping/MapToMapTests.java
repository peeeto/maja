package net.io_0.maja.mapping;

import lombok.extern.slf4j.Slf4j;
import net.io_0.maja.models.DeepNamed;
import net.io_0.maja.models.Flat;
import net.io_0.maja.models.Named;
import net.io_0.maja.models.SpecialNamed;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.function.Supplier;

import static net.io_0.maja.TestUtils.assertCollectionEquals;
import static net.io_0.maja.TestUtils.toStringMap;
import static net.io_0.maja.mapping.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Narrative:
 *   As an POJO to Map mapper API consumer
 *
 *   I want to convert a POJO to a Map
 *   so that I can interact with APIs that require a Map
 *
 *   and I want to be able to deal with names
 *   so that I don't get problems with Java naming conventions or enums
 */
@Slf4j
public class MapToMapTests {
  /**
   * Scenario: A flat Map object should be mapped to a POJO and back
   */
  @Test
  public void mapToFlatMap() {
    // Given a flat Map
    Map<String, Object> referenceMap = simplifiedFlatMap;

    // When it is mapped
    Flat pojo = Mapper.fromMap(referenceMap, Flat.class);

    // Then the data should be present in the POJO
    assertFlatDataPresent(pojo);

    // And mapping back should not loose information
    Map<String, Object> map = Mapper.toMap(pojo);

    assertCollectionEquals(
      toStringMap(referenceMap, "stringToByteArray").entrySet(),
      toStringMap(map, "stringToByteArray").entrySet()
    );
  }

  /**
   * Scenario: It should be possible to map a Map with absent properties and null to POJO and back
   */
  @Test
  public void mapToMapWithNullAndAbsentProperties() {
    // Given a map with null and absent properties
    Map<String, Object> referenceMap = new HashMap<>(){};
    referenceMap.put("obj", null);
    referenceMap.put("stringArrayToEnumList", List.of("str 1", "str-2", "STR3"));

    // When it is mapped
    DeepNamed pojo = Mapper.fromMap(referenceMap, DeepNamed.class);

    // Then mapping back should not loose information
    Map<String, Object> map = Mapper.toMap(pojo);

    assertCollectionEquals(
      toStringMap(referenceMap, "obj").entrySet(),
      toStringMap(map, "obj").entrySet()
    );
    assertNull(map.get("obj"));
  }

  /**
   * Scenario: It should be possible to have different names in Map and POJOs (and Enums)
   */
  @Test @SuppressWarnings({"unchecked", "rawtypes"})
  public void mapToDeepNamedMap() {
    // Given a deep Map with java special names
    Map<String, Object> referenceMapA = simplifiedDeepNamedMap;
    Map<String, Object> referenceMapB = Map.of("aSpecialName", 4, "BSpecialName", 5, "MaJa", 6, "caJa", 7);
    Map<String, Object> referenceMapC = Map.of("x-obj", 3, "y-obj", 4);

    // When it is mapped
    DeepNamed pojoA = Mapper.fromMap(referenceMapA, DeepNamed.class);
    Named pojoB = Mapper.fromMap(referenceMapB, Named.class);
    SpecialNamed pojoC = Mapper.fromMap(referenceMapC, SpecialNamed.class);

    // Then the data should be present in the POJO
    assertDeepNamedDataPresent(pojoA);
    assertEquals(4, pojoB.getASpecialName());
    assertEquals(5, pojoB.getBSpecialName());
    assertEquals(6, pojoB.getMaJa());
    assertEquals(7, pojoB.getCaJa());
    assertEquals(3, pojoC.getXObj());
    assertEquals(4, pojoC.getYObj());

    // And mapping back should not loose information
    Map<String, Object> mapA = Mapper.toMap(pojoA);
    Map<String, Object> mapB = Mapper.toMap(pojoB);
    Map<String, Object> mapC = Mapper.toMap(pojoC);

    assertCollectionEquals(
      toStringMap(referenceMapA, "x-obj", "objectArrayToObjectSet").entrySet(),
      toStringMap(mapA, "x-obj", "objectArrayToObjectSet").entrySet()
    );
    assertCollectionEquals(
      toStringMap((Map) referenceMapA.get("x-obj")).entrySet(),
      toStringMap((Map) mapA.get("x-obj")).entrySet()
    );
    assertCollectionEquals(
      toStringMap((Map) ((Set) referenceMapA.get("objectArrayToObjectSet")).toArray()[0]).entrySet(),
      toStringMap((Map) ((List) mapA.get("objectArrayToObjectSet")).get(0)).entrySet()
    );
    assertCollectionEquals(referenceMapB.entrySet(), mapB.entrySet());
    assertCollectionEquals(referenceMapC.entrySet(), mapC.entrySet());
  }

  private static Map<String, Object> simplifiedFlatMap = ((Supplier<Map<String, Object>>)() -> {
    Map<String, Object> map = new HashMap<>();
    map.putAll(Map.of(
      "stringToString", "str",
      "stringToEnum", "STR2",
      "stringToBigDecimal", "42",
      "stringToFloat", "20.1",
      "stringToDouble", "220.1",
      "stringToInteger", "5",
      "stringToLong", "2147483648",
      "stringToLocalDate", "2019-07-23",
      "stringToOffsetDateTime", "2010-01-01T10:00:10+01:00",
      "stringToUUID", "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    ));
    map.putAll(Map.of(
      "stringToURI", "http://www.example.com/eula",
      "stringToByteArray", "dGVzdA==",
      "stringToFile", "/path/filename.ext",
      "numberToBigDecimal", 43,
      "numberToFloat", 21.1,
      "numberToDouble", 221.1,
      "numberToInteger", 6,
      "numberToLong", 2147483649L,
      "numberToString", 9001,
      "stringArrayToStringList", List.of("a", "b", "b", "a")
    ));
    map.putAll(Map.of(
      "stringArrayToOffsetDateTimeSet", Set.of("2010-01-01T10:00:10+02:00", "2010-01-01T10:00:10+03:00"),
      "numberArrayToFloatList", List.of(1.0, 2.3, 4.0, 3.2, 1.0),
      "numberArrayToIntegerSet", List.of(0, 1, 2, 3),
      "booleanToBoolean", true,
      "booleanToString", false
    ));
    return map;
  }).get();

  private static Map<String, Object> simplifiedDeepNamedMap = Map.of(
    "x-obj", Map.of(
      "stringToUUID", "3fa85f64-5717-4562-b3fc-2c963f66afa1",
      "numberToBigDecimal", 1,
      "stringArrayToStringList", List.of("a", "a", "a", "a"),
      "numberArrayToIntegerSet", List.of(0, 1, 2, 3),
      "bool", true
    ),
    "objectArrayToObjectSet", Set.of(
      Map.of(
        "stringToUUID", "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "numberToBigDecimal", 43,
        "stringArrayToStringList", List.of("a", "b", "b", "a"),
        "numberArrayToIntegerSet", List.of(0, 1, 2, 3),
        "bool", true
      )
    ),
    "stringArrayToEnumList", List.of("str 1", "str-2", "STR3")
  );
}

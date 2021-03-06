package net.io_0.maja.mapping.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import lombok.extern.slf4j.Slf4j;
import net.io_0.maja.PropertyIssue;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
 * Instead of stopping deserialization on the first problem this handler collects failure messages as issues.
 * Fields with deserialization problems / exceptions will be set to null.
 */
@Slf4j
public class PropertyIssueCollectingDeserializationProblemHandler extends DeserializationProblemHandler {
  private final Consumer<PropertyIssue> propertyIssueConsumer;

  public PropertyIssueCollectingDeserializationProblemHandler(Consumer<PropertyIssue> propertyIssueConsumer) {
    this.propertyIssueConsumer = propertyIssueConsumer;
  }

  @Override
  public Object handleWeirdStringValue(DeserializationContext ctx, Class<?> targetType, String valueToConvert, String failureMsg) {
    return addErrorAndReturnNull(ctx, "Weird String Value", format("%s, %s", valueToConvert, failureMsg));
  }

  @Override
  public Object handleInstantiationProblem(DeserializationContext ctx, Class<?> instClass, Object argument, Throwable t) {
    return addErrorAndReturnNull(ctx, "Instantiation Problem", format("%s, %s", argument, t.getMessage()));
  }

  @Override
  public Object handleWeirdKey(DeserializationContext ctx, Class<?> rawKeyType, String keyValue, String failureMsg) {
    return addErrorAndReturnNull(ctx, "Weird Key", format("%s, %s", keyValue, failureMsg));
  }

  @Override
  public Object handleWeirdNumberValue(DeserializationContext ctx, Class<?> targetType, Number valueToConvert, String failureMsg) {
    return addErrorAndReturnNull(ctx, "Weird Number Value", format("%s, %s", valueToConvert, failureMsg));
  }

  @Override
  public Object handleUnexpectedToken(DeserializationContext ctx, JavaType targetType, JsonToken t, JsonParser p, String failureMsg) {
    return addErrorAndReturnNull(ctx, "Unexpected Token", format("%s, %s", t, failureMsg));
  }

  @Override
  public Object handleMissingInstantiator(DeserializationContext ctx, Class<?> instClass, ValueInstantiator instantiator, JsonParser p, String msg) {
    return addErrorAndReturnNull(ctx, "Missing Instantiator", format("%s, %s", instantiator, msg));
  }

  /**
   * Add error to map in context and return null as value substitute
   *
   * @param ctx context to get error map from and field name
   * @param code code to add as error code
   * @param message message to add as error message
   * @return null, as value substitute
   */
  private Object addErrorAndReturnNull(DeserializationContext ctx, String code, String message) {
    propertyIssueConsumer.accept(PropertyIssue.of(extractAttributeName(ctx.getParser()), code, removeLineBreaks(message)));

    return null;
  }

  /**
   * Json path e.g. "/zoo/1/colorEnum" to simple attribute name e.g. "zoo.1.colorEnum"
   *
   * @param parser parser to extract json path from
   * @return simplified path
   */
  private String extractAttributeName(JsonParser parser) {
    return parser.getParsingContext().pathAsPointer().toString().substring(1).replace("/", ".");
  }

  private String removeLineBreaks(String string) {
    return string.replaceAll("\\R+", "");
  }
}

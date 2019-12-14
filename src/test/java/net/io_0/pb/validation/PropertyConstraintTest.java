package net.io_0.pb.validation;

import net.io_0.pb.PropertyIssue;
import net.io_0.pb.PropertyIssues;
import net.io_0.pb.models.Pet;
import org.junit.jupiter.api.Test;

import static net.io_0.pb.validation.PropertyConstraint.*;
import static net.io_0.pb.validation.Validation.invalid;
import static org.junit.jupiter.api.Assertions.*;

class PropertyConstraintTest {
  @Test
  void onAndCheckTests() {
    PropertyIssues propertyIssues;
    PropertyValidator<Object> validator1 = p -> invalid(PropertyIssue.of(p.getName(), "Failed"));
    PropertyValidator<Object> validator2 = p -> invalid(PropertyIssue.of(p.getName(), "Failed too"));

    propertyIssues = on(Pet.NAME, validator1).apply(new Pet()).check().getPropertyIssues();
    assertEquals("[PropertyIssue(propertyName=name, issue=Failed)]", propertyIssues.toString());

    propertyIssues = on(Pet.NAME, "label", validator1).apply(new Pet()).check().getPropertyIssues();
    assertEquals("[PropertyIssue(propertyName=label, issue=Failed)]", propertyIssues.toString());

    propertyIssues = on(Pet.NAME, validator1, validator2).apply(new Pet()).check().getPropertyIssues();
    assertEquals("[PropertyIssue(propertyName=name, issue=Failed), PropertyIssue(propertyName=name, issue=Failed too)]", propertyIssues.toString());

    propertyIssues = on(Pet.NAME, "label", validator1, validator2).apply(new Pet()).check().getPropertyIssues();
    assertEquals("[PropertyIssue(propertyName=label, issue=Failed), PropertyIssue(propertyName=label, issue=Failed too)]", propertyIssues.toString());
  }
}
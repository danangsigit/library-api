package test.technical.librarian.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;

public class AssociationUtils {

  static String getIdField(Object model) throws NoSuchFieldException {
    Field[] fields = FieldUtils.getFieldsWithAnnotation(model.getClass(), Id.class);
    if (fields.length == 0) {
      throw new NoSuchFieldException("No @Id field defined in class: " + model.getClass());
    }
    if (fields.length > 1) {
      throw new UnsupportedOperationException("Multiple @Id fields are currently unsupported");
    }

    return fields[0].getName();
  }

  static String getDifferentiatorField(Object model) throws NoSuchFieldException {
    Field[] fields = FieldUtils.getFieldsWithAnnotation(model.getClass(), Differentiator.class);
    if (fields.length == 0) {
      throw new NoSuchFieldException(
          "No @Differentiator field defined in class: " + model.getClass());
    }
    if (fields.length > 1) {
      throw new UnsupportedOperationException(
          "Multiple @Differentiator fields are currently unsupported");
    }

    return fields[0].getName();
  }
}

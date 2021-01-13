package test.technical.librarian.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import javax.persistence.OneToOne;
import java.lang.reflect.Field;

public class OneToOneUtils {

  /**
   * Helper function to save <code>@OneToOne</code> relationship.
   *
   * @param form form object
   * @param model model object
   * @param field field in both form object and model object
   * @param targetClass type of associated child object
   * @param listener a listener that can be implemented to add additional logic for associated child
   */
  public static void save(Object form, Object model, String field, Class targetClass,
                          AssociationListener listener) {
    save(form, model, field, field, targetClass, listener);
  }

  /**
   * Helper function to save <code>@OneToOne</code> relationship.
   *
   * @param form form object
   * @param model model object
   * @param formField source field in form object
   * @param modelField destination field in model object
   * @param targetClass type of associated child object
   * @param listener a listener that can be implemented to add additional logic for associated child
   * child
   */
  public static void save(Object form, Object model, String formField, String modelField,
                          Class targetClass, AssociationListener listener) {
    try {
      Object assoc = PropertyUtils.getProperty(model, modelField);
      Object value = PropertyUtils.getProperty(form, formField);

      //determine parent field of associated child
      Field otmField = model.getClass().getDeclaredField(modelField);
      OneToOne otmAnnotation = otmField.getAnnotation(OneToOne.class);
      String parentField = otmAnnotation.mappedBy();

      if (value == null) {
        PropertyUtils.setProperty(model, modelField, null);
      } else {
        if (assoc == null) {
          assoc = targetClass.newInstance();
          PropertyUtils.setProperty(assoc, parentField, model);
          PropertyUtils.setProperty(model, modelField, assoc);
        }

        BeanUtils.copyProperties(value, assoc);

        if (listener != null) {
          listener.associate(assoc, value);
        }
      }
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Helper function to populate <code>@OneToOne</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param field field in both model object and form object
   * @param targetClass type of associated form object
   * @param listener a listener that can be implemented to add additional logic for associated child
   */
  public static void populate(Object model, Object form, String field, Class targetClass, AssociationListener listener) {
    populate(model, form, field, field, targetClass, listener);
  }

  /**
   * Helper function to populate <code>@OneToOne</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param modelField destination field in model object
   * @param formField source field in form object
   * @param targetClass type of associated form object
   * @param listener a listener that can be implemented to add additional logic for associated child
   */
  public static void populate(Object model, Object form, String modelField, String formField,
                              Class targetClass, AssociationListener listener) {
    try {
      Object assoc = PropertyUtils.getProperty(model, modelField);
      Object value = PropertyUtils.getProperty(form, formField);

      if (assoc != null) {
        if (value == null) {
          value = targetClass.newInstance();
          PropertyUtils.setProperty(form, formField, value);
        }

        BeanUtils.copyProperties(assoc, value);

        if (listener != null) {
          listener.associate(assoc, value);
        }
      }
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }
}

package test.technical.librarian.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;

public class ManyToOneUtils {

  /**
   * Helper function to save <code>@ManyToOne</code> relationship.
   *
   * @param form form object
   * @param model model object
   * @param field field in both form object and model object
   * @param repo repository object to find selected value in database
   */
  public static void save(Object form, Object model, String field, CrudRepository repo) {
    save(form, model, field, field, repo);
  }

  /**
   * Helper function to save <code>@ManyToOne</code> relationship.
   *
   * @param form form object
   * @param model model object
   * @param formField source field in form object
   * @param modelField destination field in model object
   * @param repo repository object to find selected value in database
   */
  @SuppressWarnings("unchecked")
  public static void save(Object form, Object model, String formField, String modelField,
                          CrudRepository repo) {
    try {
      Object value = PropertyUtils.getProperty(form, formField);
      if (value == null) {
        PropertyUtils.setProperty(model, modelField, null);
      } else {
        if (!(value instanceof Serializable)) {
          throw new UnsupportedOperationException(
              "Unsupported id type: " + value.getClass().getName());
        }

        Optional assoc = repo.findById(value);
        if (assoc.isPresent()) {
          PropertyUtils.setProperty(model, modelField, assoc.get());
        }
      }
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Helper function to populate <code>@ManyToOne</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param field field in both model object and form object
   */
  public static void populate(Object model, Object form, String field) {
    populate(model, form, field, field);
  }

  /**
   * Helper function to populate <code>@ManyToOne</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param modelField source field in model object
   * @param formField destination field in form object
   */
  public static void populate(Object model, Object form, String modelField, String formField) {
    try {
      Object assoc = PropertyUtils.getProperty(model, modelField);
      if (assoc == null) {
        return;
      }

      String idField = AssociationUtils.getIdField(assoc);
      Object value = PropertyUtils.getProperty(assoc, idField);
      PropertyUtils.setProperty(form, formField, value);
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }
}

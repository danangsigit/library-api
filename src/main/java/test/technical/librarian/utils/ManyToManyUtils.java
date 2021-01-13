package test.technical.librarian.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.*;

public class ManyToManyUtils {

  /**
   * Helper function to save <code>@ManyToMany</code> relationship.
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
   * Helper function to save <code>@ManyToMany</code> relationship.
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
      Collection assocs = (Collection) PropertyUtils.getProperty(model, modelField);
      Collection values = (Collection) PropertyUtils.getProperty(form, formField);

      if (assocs == null) {
        assocs = new LinkedHashSet();
      }

      if (values == null) {
        assocs.clear();
      } else {
        //remove missing items
        for (Iterator i = assocs.iterator(); i.hasNext(); ) {
          Object assoc = i.next();
          String idField = AssociationUtils.getIdField(assoc);
          Object assocId = PropertyUtils.getProperty(assoc, idField);
          if (!values.contains(assocId)) {
            i.remove();
          }
        }

        //add new items
        for (Object value : values) {
          if (value == null) {
            continue;
          }

          if (!(value instanceof Serializable)) {
            throw new UnsupportedOperationException(
                "Unsupported id type: " + value.getClass().getName());
          }

          Optional assoc = repo.findById(value);
          if (assoc.isPresent()) {
            assocs.add(assoc.get());
          }
        }

        PropertyUtils.setProperty(model, modelField, assocs);
      }
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Helper function to populate <code>@ManyToMany</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param field field in both model object and form object
   */
  public static void populate(Object model, Object form, String field) {
    populate(model, form, field, field);
  }

  /**
   * Helper function to populate <code>@ManyToMany</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param modelField source field in model object
   * @param formField destination field in form object
   */
  @SuppressWarnings("unchecked")
  public static void populate(Object model, Object form, String modelField, String formField) {
    try {
      Collection assocs = (Collection) PropertyUtils.getProperty(model, modelField);
      Collection values = new ArrayList();

      if (assocs != null) {
        for (Object assoc : assocs) {
          String idField = AssociationUtils.getIdField(assoc);
          Object assocId = PropertyUtils.getProperty(assoc, idField);
          values.add(assocId);
        }
      }

      PropertyUtils.setProperty(form, formField, values);
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }
}

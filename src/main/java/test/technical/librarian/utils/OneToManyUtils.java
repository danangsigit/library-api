package test.technical.librarian.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.util.*;

public class OneToManyUtils {

  /**
   * Helper function to save <code>@OneToMany</code> relationship.
   *
   * @param form form object
   * @param model model object
   * @param field field in both form object and model object
   * @param targetClass type of each model child object
   * @param listener a listener that can be implemented to add additional logic for each saved
   * child
   */
  public static void save(Object form, Object model, String field, Class targetClass,
                          AssociationListener listener) {
    save(form, model, field, field, targetClass, listener);
  }

  /**
   * Helper function to save <code>@OneToMany</code> relationship.
   *
   * @param form form object
   * @param model model object
   * @param formField source field in form object
   * @param modelField destination field in model object
   * @param targetClass type of each model child object
   * @param listener a listener that can be implemented to add additional logic for each associated
   * child
   */
  @SuppressWarnings("unchecked")
  public static void save(Object form, Object model, String formField, String modelField,
                          Class targetClass, AssociationListener listener) {
    try {
      Collection assocs = (Collection) PropertyUtils.getProperty(model, modelField);
      Collection values = (Collection) PropertyUtils.getProperty(form, formField);

      //determine parent field of each model child
      Field otmField = model.getClass().getDeclaredField(modelField);
      OneToMany otmAnnotation = otmField.getAnnotation(OneToMany.class);
      String parentField = otmAnnotation.mappedBy();

      if (assocs == null) {
        assocs = new LinkedHashSet();
      }

      if (values == null) {
        assocs.clear();
      } else {
        //remove missing items
        for (Iterator i = assocs.iterator(); i.hasNext(); ) {
          Object assoc = i.next();
          String assocIdField = AssociationUtils.getIdField(assoc);
          Object assocId = PropertyUtils.getProperty(assoc, assocIdField);

          boolean found = false;
          for (Object value : values) {
            String valueIdField = AssociationUtils.getIdField(value);
            Object valueId = PropertyUtils.getProperty(value, valueIdField);

            if (assocId.equals(valueId)) {
              found = true;
              break;
            }
          }

          if (!found) {
            i.remove();
          }
        }

        //add new items or update existing items
        for (Object value : values) {
          String valueIdField = AssociationUtils.getIdField(value);
          Object valueId = PropertyUtils.getProperty(value, valueIdField);

          Object target = null;
          if (valueId != null) {
            for (Object assoc : assocs) {
              String assocIdField = AssociationUtils.getIdField(assoc);
              Object assocId = PropertyUtils.getProperty(assoc, assocIdField);

              if (assocId.equals(valueId)) {
                target = assoc;
                break;
              }
            }
          }

          if (target == null) {
            target = targetClass.newInstance();
            //set differentiator field to an uuid
            String differentiatorField = AssociationUtils.getDifferentiatorField(target);
            PropertyUtils.setProperty(target, differentiatorField, UUID.randomUUID().toString());
            //set parent field of this child to given model object
            PropertyUtils.setProperty(target, parentField, model);
            assocs.add(target);
          }

          BeanUtils.copyProperties(value, target, valueIdField);

          if (listener != null) {
            listener.associate(target, value);
          }
        }

        PropertyUtils.setProperty(model, modelField, assocs);
      }
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Helper function to populate <code>@OneToMany</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param field field in both model object and form object
   * @param targetClass type of each form child object
   * @param listener a listener that can be implemented to add additional logic for each associated
   * child
   */
  public static void populate(Object model, Object form, String field, Class targetClass,
                              AssociationListener listener) {
    populate(model, form, field, field, targetClass, listener);
  }

  /**
   * Helper function to populate <code>@OneToMany</code> relationship.
   *
   * @param model model object
   * @param form form object
   * @param modelField destination field in model object
   * @param formField source field in form object
   * @param targetClass type of each form child object
   * @param listener a listener that can be implemented to add additional logic for each associated
   * child
   */
  @SuppressWarnings("unchecked")
  public static void populate(Object model, Object form, String modelField, String formField,
                              Class targetClass, AssociationListener listener) {
    try {
      Collection assocs = (Collection) PropertyUtils.getProperty(model, modelField);
      Collection values = new ArrayList();

      if (assocs != null) {
        for (Object assoc : assocs) {
          Object value = targetClass.newInstance();
          BeanUtils.copyProperties(assoc, value);

          if (listener != null) {
            listener.associate(assoc, value);
          }

          values.add(value);
        }
      }

      PropertyUtils.setProperty(form, formField, values);
    } catch (ReflectiveOperationException ex) {
      throw new RuntimeException(ex);
    }
  }
}

package net.silthus.rccities.util;

import org.apache.commons.lang.ClassUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class ReflectionUtil {

    /**
     * When {@code Type} initialized with a value of an object, its fully qualified class displayName
     * will be prefixed with this.
     *
     * @see {@link ReflectionUtil#getClassName(Type)}
     */
    private static final String TYPE_CLASS_NAME_PREFIX = "class ";
    private static final String TYPE_INTERFACE_NAME_PREFIX = "interface ";

    // Deduce the net.minecraft.server.v* package
    private static String OBC_PREFIX = Bukkit.getServer().getClass().getPackage().getName();
    private static String NMS_PREFIX = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");
    private static String VERSION = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");
    // Variable replacement
    private static Pattern MATCH_VARIABLE = Pattern.compile("\\{([^\\}]+)\\}");

    /*
     *  Utility class with static access methods, no need for constructor.
     */
    private ReflectionUtil() {

    }

    /**
     * Expand variables such as "{nms}" and "{obc}" to their corresponding packages.
     *
     * @param name the full name of the class
     * @return the expanded string
     */
    private static String expandVariables(String name) {
        StringBuffer output = new StringBuffer();
        Matcher matcher = MATCH_VARIABLE.matcher(name);

        while (matcher.find()) {
            String variable = matcher.group(1);
            String replacement;

            // Expand all detected variables
            if ("nms".equalsIgnoreCase(variable))
                replacement = NMS_PREFIX;
            else if ("obc".equalsIgnoreCase(variable))
                replacement = OBC_PREFIX;
            else if ("version".equalsIgnoreCase(variable))
                replacement = VERSION;
            else
                throw new IllegalArgumentException("Unknown variable: " + variable);

            // Assume the expanded variables are all packages, and append a dot
            if (replacement.length() > 0 && matcher.end() < name.length() && name.charAt(matcher.end()) != '.')
                replacement += ".";
            matcher.appendReplacement(output, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(output);
        return output.toString();
    }

    /**
     * Retrieve a class by its canonical name.
     *
     * @param canonicalName the canonical name
     * @return the class
     */
    private static Class<?> getCanonicalClass(String canonicalName) {
        try {
            return Class.forName(canonicalName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find " + canonicalName, e);
        }
    }

    /**
     * Retrieve a class from its full name.
     * <p/>
     * Strings enclosed with curly brackets such as {TEXT} will be replaced according
     * to the following table:
     * <p/>
     * <table border="1">
     * <tr>
     * <th>Variable</th>
     * <th>Content</th>
     * </tr>
     * <tr>
     * <td>{nms}</td>
     * <td>Actual package name of net.minecraft.server.VERSION</td>
     * </tr>
     * <tr>
     * <td>{obc}</td>
     * <td>Actual pacakge name of org.bukkit.craftbukkit.VERSION</td>
     * </tr>
     * <tr>
     * <td>{version}</td>
     * <td>The current Minecraft package VERSION, if any.</td>
     * </tr>
     * </table>
     *
     * @param lookupName the class name with variables
     * @return the looked up class
     * @throws IllegalArgumentException If a variable or class could not be found
     */
    public static Class<?> getClass(String lookupName) {
        return getCanonicalClass(expandVariables(lookupName));
    }

    /**
     * {@link Type#toString()} value is the fully qualified class displayName prefixed
     * with {@link ReflectionUtil#TYPE_CLASS_NAME_PREFIX}. This method will substring it, for it to be eligible
     * for {@link Class#forName(String)}.
     *
     * @param type the {@code Type} value whose class displayName is needed.
     *
     * @return {@code String} class displayName of the invoked {@code type}.
     *
     * @see {@link ReflectionUtil#getClass()}
     */
    public static String getClassName(Type type) {

        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_CLASS_NAME_PREFIX)) {
            className = className.substring(TYPE_CLASS_NAME_PREFIX.length());
        } else if (className.startsWith(TYPE_INTERFACE_NAME_PREFIX)) {
            className = className.substring(TYPE_INTERFACE_NAME_PREFIX.length());
        }
        return className;
    }

    /**
     * Returns the {@code Class} object associated with the given {@link Type}
     * depending on its fully qualified displayName.
     *
     * @param type the {@code Type} whose {@code Class} is needed.
     *
     * @return the {@code Class} object for the class with the specified displayName.
     *
     * @throws ClassNotFoundException if the class cannot be located.
     * @see {@link ReflectionUtil#getClassName(Type)}
     */
    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {

        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    /**
     * Creates a new instance of the class represented by this {@code Type} object.
     *
     * @param type the {@code Type} object whose its representing {@code Class} object
     *             will be instantiated.
     *
     * @return a newly allocated instance of the class represented by
     * the invoked {@code Type} object.
     *
     * @throws ClassNotFoundException if the class represented by this {@code Type} object
     *                                cannot be located.
     * @throws InstantiationException if this {@code Type} represents an abstract class,
     *                                an interface, an array class, a primitive type, or void;
     *                                or if the class has no nullary constructor;
     *                                or if the instantiation fails for some other reason.
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     * @see {@link Class#newInstance()}
     */
    public static Object newInstance(Type type)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class<?> clazz = getClass(type);
        if (clazz == null) {
            return null;
        }
        return clazz.newInstance();
    }

    /**
     * Returns an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * If the returned value is null, then this object represents a non-parameterized
     * object.
     *
     * @param object the {@code object} whose type arguments are needed.
     *
     * @return an array of {@code Type} objects representing the actual type
     * arguments to this object.
     *
     * @see {@link Class#getGenericSuperclass()}
     * @see {@link ParameterizedType#getActualTypeArguments()}
     */
    public static Type[] getParameterizedTypes(Object object) {

        Type superclassType = object.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }

        return ((ParameterizedType) superclassType).getActualTypeArguments();
    }

    /**
     * Checks whether a {@code Constructor} object with no parameter types is specified
     * by the invoked {@code Class} object or not.
     *
     * @param clazz the {@code Class} object whose constructors are checked.
     *
     * @return {@code true} if a {@code Constructor} object with no parameter types is specified.
     *
     * @throws SecurityException If a security manager, <i>s</i> is present and any of the
     *                           following conditions is met:
     *                           <ul>
     *                           <li> invocation of
     *                           {@link SecurityManager#checkMemberAccess
     *                           s.checkMemberAccess(this, Member.PUBLIC)} denies
     *                           access to the constructor
     *                           <p>
     *                           <li> the caller's class loader is not the same as or an
     *                           ancestor of the class loader for the current class and
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           s.checkPackageAccess()} denies access to the package
     *                           of this class
     *                           </ul>
     * @see {@link Class#getConstructor(Class...)}
     */
    public static boolean hasDefaultConstructor(Class<?> clazz) throws SecurityException {

        Class<?>[] empty = {};
        try {
            clazz.getConstructor(empty);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class for the field represented by the given {@code String displayName} parameter inside
     * the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared fields to be
     *              checked for a certain field.
     * @param name  the field displayName as {@code String} to be
     *              compared with {@link Field#getName()}
     *
     * @return the {@code Class} object representing the type of given field displayName.
     *
     * @see {@link Class#getDeclaredFields()}
     * @see {@link Field#getType()}
     */
    public static Class<?> getFieldClass(Class<?> clazz, String name) {

        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }

        Class<?> propertyClass = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(name)) {
                propertyClass = field.getType();
                break;
            }
        }

        return propertyClass;
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target    the target type
     * @param name      the name of the field, or NULL to ignore
     * @param fieldType a compatible field type
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
        return getField(target, name, fieldType, 0);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className lookup name of the class, see {@link #getClass(String)}
     * @param name      the name of the field, or NULL to ignore
     * @param fieldType a compatible field type
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(String className, String name, Class<T> fieldType) {
        return getField(getClass(className), name, fieldType, 0);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param target    the target type
     * @param fieldType a compatible field type
     * @param index     the number of compatible fields to skip
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
        return getField(target, null, fieldType, index);
    }

    /**
     * Retrieve a field accessor for a specific field type and name.
     *
     * @param className lookup name of the class, see {@link #getClass(String)}
     * @param fieldType a compatible field type
     * @param index     the number of compatible fields to skip
     * @return the field accessor
     */
    public static <T> FieldAccessor<T> getField(String className, Class<T> fieldType, int index) {
        return getField(getClass(className), fieldType, index);
    }

    // Common method
    private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);

                // A function for retrieving a specific field value
                return new FieldAccessor<T>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public T get(Object target) {
                        try {
                            return (T) field.get(target);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public void set(Object target, Object value) {
                        try {
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Cannot access reflection.", e);
                        }
                    }

                    @Override
                    public boolean hasField(Object target) {
                        // target instanceof DeclaringClass
                        return field.getDeclaringClass().isAssignableFrom(target.getClass());
                    }
                };
            }
        }

        // Search in parent classes
        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class as a return type for the method represented by the given
     * {@code String displayName} parameter inside the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared methods to be
     *              checked for the wanted method displayName.
     * @param name  the method displayName as {@code String} to be
     *              compared with {@link Method#getName()}
     *
     * @return the {@code Class} object representing the return type of the given method displayName.
     *
     * @see {@link Class#getDeclaredMethods()}
     * @see {@link Method#getReturnType()}
     */
    public static Class<?> getMethodReturnType(Class<?> clazz, String name) {

        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }

        name = name.toLowerCase();
        Class<?> returnType = null;

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                returnType = method.getReturnType();
                break;
            }
        }

        return returnType;
    }

    public static Method getMethod(Object object, String methodName, Object... args) throws NoSuchMethodException {

        Class[] argsClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClasses[i] = args[i].getClass();
        }
        Method finalMethod = null;
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                boolean match = true;
                for (int i = 0; i < argsClasses.length; i++) {
                    if (method.getParameterTypes()[i].isPrimitive()) {
                        Class<?> aClass = ClassUtils.primitiveToWrapper(method.getParameterTypes()[i]);
                        if (aClass.isAssignableFrom(argsClasses[i])) {
                            continue;
                        } else {
                            match = false;
                            break;
                        }
                    }
                    if (!method.getParameterTypes()[i].isAssignableFrom(argsClasses[i])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    finalMethod = method;
                    break;
                }
            }
        }
        if (finalMethod == null) {
            throw new NoSuchMethodException(
                    "No method signature found for " + methodName + " in " + object.getClass().getCanonicalName());
        }
        return finalMethod;
    }

    /**
     * Extracts the enum constant of the specified enum class with the
     * specified displayName. The displayName must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param clazz the {@code Class} object of the enum type from which
     *              to return a constant.
     * @param name  the displayName of the constant to return.
     *
     * @return the enum constant of the specified enum type with the
     * specified displayName.
     *
     * @throws IllegalArgumentException if the specified enum type has
     *                                  no constant with the specified displayName, or the specified
     *                                  class object does not represent an enum type.
     * @see {@link Enum#valueOf(Class, String)}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getEnumConstant(Class<?> clazz, String name) {

        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }
        return Enum.valueOf((Class<Enum>) clazz, name);
    }

    private static void extractTypeArguments(Map<Type, Type> typeMap, Class<?> clazz) {

        Type genericSuperclass = clazz.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return;
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeParameter = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
        Type[] actualTypeArgument = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < typeParameter.length; i++) {
            if (typeMap.containsKey(actualTypeArgument[i])) {
                actualTypeArgument[i] = typeMap.get(actualTypeArgument[i]);
            }
            typeMap.put(typeParameter[i], actualTypeArgument[i]);
        }
    }

    public static Class<?> findSubClassParameterType(Object instance, Class<?> classOfInterest, int parameterIndex) {

        Map<Type, Type> typeMap = new HashMap<Type, Type>();
        Class<?> instanceClass = instance.getClass();
        while (classOfInterest != instanceClass.getSuperclass()) {
            extractTypeArguments(typeMap, instanceClass);
            instanceClass = instanceClass.getSuperclass();
            if (instanceClass == null) throw new IllegalArgumentException();
        }

        ParameterizedType parameterizedType = (ParameterizedType) instanceClass.getGenericSuperclass();
        Type actualType = parameterizedType.getActualTypeArguments()[parameterIndex];
        if (typeMap.containsKey(actualType)) {
            actualType = typeMap.get(actualType);
        }

        if (actualType instanceof Class) {
            return (Class<?>) actualType;
        } else if (actualType instanceof TypeVariable) {
            return browseNestedTypes(instance, (TypeVariable<?>) actualType);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static Class<?> browseNestedTypes(Object instance, TypeVariable<?> actualType) {

        Class<?> instanceClass = instance.getClass();
        List<Class<?>> nestedOuterTypes = new LinkedList<Class<?>>();
        for (
                Class<?> enclosingClass = instanceClass.getEnclosingClass();
                enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            try {
                Field this$0 = instanceClass.getDeclaredField("this$0");
                Object outerInstance = this$0.get(instance);
                Class<?> outerClass = outerInstance.getClass();
                nestedOuterTypes.add(outerClass);
                Map<Type, Type> outerTypeMap = new HashMap<Type, Type>();
                extractTypeArguments(outerTypeMap, outerClass);
                for (Map.Entry<Type, Type> entry : outerTypeMap.entrySet()) {
                    if (!(entry.getKey() instanceof TypeVariable)) {
                        continue;
                    }
                    TypeVariable<?> foundType = (TypeVariable<?>) entry.getKey();
                    if (foundType.getName().equals(actualType.getName())
                            && isInnerClass(foundType.getGenericDeclaration(), actualType.getGenericDeclaration())) {
                        if (entry.getValue() instanceof Class) {
                            return (Class<?>) entry.getValue();
                        }
                        actualType = (TypeVariable<?>) entry.getValue();
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) { /* this should never happen */ }

        }
        throw new IllegalArgumentException();
    }

    private static boolean isInnerClass(GenericDeclaration outerDeclaration, GenericDeclaration innerDeclaration) {

        if (!(outerDeclaration instanceof Class) || !(innerDeclaration instanceof Class)) {
            throw new IllegalArgumentException();
        }
        Class<?> outerClass = (Class<?>) outerDeclaration;
        Class<?> innerClass = (Class<?>) innerDeclaration;
        while ((innerClass = innerClass.getEnclosingClass()) != null) {
            if (innerClass == outerClass) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> getNmsClass(String basePackage, String clazzName) {

        return getNmsClass(basePackage, "", clazzName);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getNmsClass(String basePackage, String clazzName, Class<T> type) {
        return (Class<T>) getNmsClass(basePackage, clazzName);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getNmsClassInstance(String basePackage, String clazzName) {
        try {
            return Optional.ofNullable((T) getNmsClass(basePackage, clazzName).newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Class<?> getNmsClass(String basePackage, String detailPackage, String clazzName) {

        try {
            String mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Class<?> clazz = Class.forName(basePackage + "." + mcVersion + "." + detailPackage
                    + (detailPackage == null || detailPackage.equals("") ? "" : ".") + clazzName);
            return clazz;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {

        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }

    public static <T> T get(Class<?> sourceClass, Class<T> fieldClass, String fieldName) {

        return get(sourceClass, fieldClass, null, fieldName);
    }

    public static <T> T get(Object instance, Class<T> fieldClass, String fieldName) {

        return get(instance.getClass(), fieldClass, instance, fieldName);
    }

    public static <T> T get(Class<?> sourceClass, Class<T> fieldClass, Object instance, String fieldName) {

        try {
            Field field = sourceClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");

            boolean accessible = modifiersField.isAccessible();
            if (!accessible) {
                modifiersField.setAccessible(true);
            }

            try {
                return fieldClass.cast(field.get(instance));
            } finally {
                if (!accessible) {
                    modifiersField.setAccessible(false);
                }
            }
        } catch (IllegalArgumentException | ClassCastException | SecurityException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean set(Object instance, String fieldName, Object value) {

        return set(instance.getClass(), instance, fieldName, value);
    }

    public static boolean set(Class<?> sourceClass, String fieldName, Object value) {

        return set(sourceClass, null, fieldName, value);
    }

    public static boolean set(Class<?> sourceClass, Object instance, String fieldName, Object value) {

        try {
            Field field = sourceClass.getDeclaredField(fieldName);
            boolean accessible = field.isAccessible();

            //field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");

            int modifiers = modifiersField.getModifiers();
            boolean isFinal = (modifiers & Modifier.FINAL) == Modifier.FINAL;

            if (!accessible) {
                field.setAccessible(true);
            }
            if (isFinal) {
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
            }
            try {
                field.set(instance, value);
            } finally {
                if (isFinal) {
                    modifiersField.setInt(field, modifiers | Modifier.FINAL);
                }
                if (!accessible) {
                    field.setAccessible(false);
                }
            }

            return true;
        } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public static boolean set(Object instance, Field field, Object value) {

        try {
            boolean accessible = field.isAccessible();

            //field.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");

            int modifiers = modifiersField.getModifiers();
            boolean isFinal = (modifiers & Modifier.FINAL) == Modifier.FINAL;

            if (!accessible) {
                field.setAccessible(true);
            }
            if (isFinal) {
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
            }
            try {
                field.set(instance, value);
            } finally {
                if (isFinal) {
                    modifiersField.setInt(field, modifiers | Modifier.FINAL);
                }
                if (!accessible) {
                    field.setAccessible(false);
                }
            }

            return true;
        } catch (IllegalArgumentException | NoSuchFieldException | IllegalAccessException | SecurityException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * An interface for retrieving the field content.
     *
     * @param <T> field type
     */
    public interface FieldAccessor<T> {
        /**
         * Retrieve the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @return the value of the field
         */
        public T get(Object target);

        /**
         * Set the content of a field.
         *
         * @param target the target object, or NULL for a static field
         * @param value  the new value of the field
         */
        public void set(Object target, Object value);

        /**
         * Determine if the given object has this field.
         *
         * @param target the object to test
         * @return TRUE if it does, FALSE otherwise
         */
        public boolean hasField(Object target);
    }
}
package com.foros.model.security;

public interface NotManagedEntity {

    public final static class Util {

        private Util() {
        }

        public static boolean isManaged(OwnedEntity object) {
            return !(object instanceof NotManagedEntity);
        }

        public static boolean isManaged(Class... classes) {
            if (classes == null || classes.length == 0) {
                throw new IllegalArgumentException("null or empty array");
            }

            boolean result = isManagedClass(classes[0]);
            for (int i = 1; i < classes.length; i++) {
                if (isManagedClass(classes[i]) != result) {
                    throw new IllegalArgumentException("classes");
                }
            }
            return result;
        }

        private static boolean isManagedClass(Class clazz) {
            if (!OwnedEntity.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("class must implement OwnedEntity");
            }

            return !NotManagedEntity.class.isAssignableFrom(clazz);
        }
    }
}

package com.foros.model.security;

/**
 *
 * @author vladimir
 * @version $Revision: 1.2 $
 */
public enum PaymentType {
    PAID_IN(0, "Paid In"),
    PAID_OUT(1, "Paid Out"),
    ADJUSTMENT(2, "Adjustment");
    
    private final String name;
    private final int id;

    PaymentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static PaymentType valueOf(int id) throws IllegalArgumentException {
        switch (id) {
            case 0:
                return PAID_IN;
            case 1:
                return PAID_OUT;
            case 2:
                return ADJUSTMENT;
            default:
                throw new IllegalArgumentException("Illegal id given: '" + id + "'");
        }
    }
}

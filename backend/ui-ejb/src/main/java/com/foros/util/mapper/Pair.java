package com.foros.util.mapper;

public class Pair<T1, T2> {

    private T1 leftValue;
    private T2 rightValue;

    public Pair(T1 leftValue, T2 rightValue) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
    }

    public T1 getLeftValue() {
        return leftValue;
    }

    public T2 getRightValue() {
        return rightValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (leftValue != null ? !leftValue.equals(pair.leftValue) : pair.leftValue != null) return false;
        if (rightValue != null ? !rightValue.equals(pair.rightValue) : pair.rightValue != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftValue != null ? leftValue.hashCode() : 0;
        result = 31 * result + (rightValue != null ? rightValue.hashCode() : 0);
        return result;
    }
}

package com.summit.chat.GlobalHandle.Validator;

public interface GlobalValidator<T> {
    public boolean validate(T dto);
}

package com.bank.transaction.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeTransaction
{
    PERSONAL(2002),
    COMPANY(2003);

    private final int value;

    public static TypeTransaction fromInteger(int val) {
        switch(val) {
            case 0:
                return PERSONAL;
            case 1:
                return COMPANY;

        }
        return null;
    }
}
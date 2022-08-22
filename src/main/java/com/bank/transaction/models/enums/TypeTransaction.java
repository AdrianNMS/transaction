package com.bank.transaction.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeTransaction
{
    PERSONAL_CREDIT(2000),
    COMPANY_CREDIT(2001),
    PERSONAL_CREDIT_CARD(2002),
    COMPANY_CREDIT_CARD(2003);

    private final int value;

    public static TypeTransaction fromInteger(int val) {
        switch(val) {
            case 2000:
                return PERSONAL_CREDIT;
            case 2001:
                return COMPANY_CREDIT;
            case 2002:
                return PERSONAL_CREDIT_CARD;
            case 2003:
                return COMPANY_CREDIT_CARD;
        }
        return null;
    }
}
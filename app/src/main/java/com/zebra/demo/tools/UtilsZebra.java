package com.zebra.demo.tools;

import com.zebra.demo.R;
import com.zebra.rfid.api3.MEMORY_BANK;
import com.zebra.rfid.api3.STATE_AWARE_ACTION;
import com.zebra.rfid.api3.TARGET;

public class UtilsZebra {

    public static  MEMORY_BANK getMemoryBankEnum(String index)
    {
        MEMORY_BANK memBank = MEMORY_BANK.MEMORY_BANK_EPC;
        switch(index)
        {
            case "0":
                memBank = MEMORY_BANK.MEMORY_BANK_RESERVED;
                break;
            case "1":
                memBank = MEMORY_BANK.MEMORY_BANK_EPC;
                break;
            case "2":
                memBank = MEMORY_BANK.MEMORY_BANK_TID;
                break;
            case "3":
                memBank = MEMORY_BANK.MEMORY_BANK_USER;
                break;
        }
        return memBank;
    }

    public static STATE_AWARE_ACTION getStateAwareAction(String actionIndex)
    {
        STATE_AWARE_ACTION stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_ASRT_SL_NOT_DSRT_SL;
        switch (actionIndex)
        {
            case "0":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A_NOT_INV_B;
                break;
            case "1":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_ASRT_SL_NOT_DSRT_SL;
                break;
            case "2":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A;
                break;
            case "3":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_ASRT_SL;
                break;
            case "4":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_B;
                break;
            case "5":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_DSRT_SL;
                break;
            case "6":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_A2BB2A;
                break;
            case "7":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_NEG_SL;
                break;
            case "8":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_B_NOT_INV_A;
                break;
            case "9":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_DSRT_SL_NOT_ASRT_SL;
                break;
            case "10":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_INV_B;
                break;
            case "11":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_DSRT_SL;
                break;
            case "12":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_A;
                break;
            case "13":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_ASRT_SL;
                break;
            case "14":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_INV_A2BB2A;
                break;
            case "15":
                stateAwareAction = STATE_AWARE_ACTION.STATE_AWARE_ACTION_NOT_NEG_SL;
                break;
        }
        return stateAwareAction;
    }

    public static TARGET getStateAwareTarget(String index)
    {
        TARGET target = TARGET.TARGET_SL;
        switch (index)
        {
            case "0":
                target = TARGET.TARGET_SL;
                break;
            case "1":
                target = TARGET.TARGET_INVENTORIED_STATE_S0;
                break;
            case "2":
                target = TARGET.TARGET_INVENTORIED_STATE_S1;;
                break;
            case "3":
                target = TARGET.TARGET_INVENTORIED_STATE_S2;
                break;
            case "4":
                target = TARGET.TARGET_INVENTORIED_STATE_S3;
                break;

        }
        return target;
    }
}

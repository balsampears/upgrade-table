package com.balsam.upgradetable.config;


import java.util.UUID;

public interface Constants {

    String MOD_ID = "upgradetable";

    int MAX_LEVEL_TOTAL = 16;
    int MAX_LEVEL_PER_ABILITY = 6;


    interface Uuid{
        UUID MAX_DURATION = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACB0");
        UUID ATTACK_DAMAGE = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACB1");
        UUID ATTACK_SPEED = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACB2");
    }
}

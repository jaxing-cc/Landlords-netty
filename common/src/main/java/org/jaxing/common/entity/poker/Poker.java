package org.jaxing.common.entity.poker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Poker {
    private byte id;
    private byte value;
    private PokerType type;

    @Override
    public String toString() {
        return id +":"+type + "@"+value;
    }
}

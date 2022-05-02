package com.rulerofnightmares.game.Components;

import com.almasb.fxgl.entity.component.Component;

public class DamageDealerComponent extends Component{
    
    int dmg;

    public DamageDealerComponent(int dmg){
        this.dmg = dmg;
    }

    public int dealDmg(){
        return dmg;
    }
}

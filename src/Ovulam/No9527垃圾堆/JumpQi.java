package Ovulam.No9527垃圾堆;

import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Icon;
import mindustry.net.Packets;
import mindustry.world.Block;

public class JumpQi extends Block {
    public TextureRegionDrawable icon;
    public JumpQi(String name) {
        super(name);
        update = true;
        sync = true;
        configurable = true;
    }

    @Override
    public void init() {
        super.init();
        //必须在这里初始化
        icon = Icon.right;
    }

    public class JumpQiBuild extends Building{

        @Override
        public void buildConfiguration(Table table){
            table.defaults().width(216f);
            table.button("跳至下一波", icon, () -> Call.adminRequest(Vars.player, Packets.AdminAction.wave, null));
        }
    }
}

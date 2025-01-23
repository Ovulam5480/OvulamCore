package Ovulam.world.type;

public class ItemAttributes{
    public float flammability, explosiveness, radioactivity, charge;

    public ItemAttributes(float flammability, float explosiveness, float radioactivity, float charge) {
        this.flammability = flammability;
        this.explosiveness = explosiveness;
        this.radioactivity = radioactivity;
        this.charge = charge;
    }

    public void setAttribute(float flammability, float explosiveness,
                             float radioactivity, float charge){
        this.flammability = flammability;
        this.explosiveness = explosiveness;
        this.radioactivity = radioactivity;
        this.charge = charge;
    }
}